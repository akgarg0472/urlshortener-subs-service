package com.akgarg.subsservice.v1.subs;

import com.akgarg.subsservice.exception.BadRequestException;
import com.akgarg.subsservice.exception.SubscriptionException;
import com.akgarg.subsservice.notification.NotificationService;
import com.akgarg.subsservice.request.MakeSubscriptionRequest;
import com.akgarg.subsservice.response.*;
import com.akgarg.subsservice.v1.pack.SubscriptionPack;
import com.akgarg.subsservice.v1.pack.SubscriptionPackDTO;
import com.akgarg.subsservice.v1.pack.SubscriptionPackService;
import com.akgarg.subsservice.v1.subs.cache.SubscriptionCache;
import com.akgarg.subsservice.v1.subs.db.SubscriptionDatabaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class SubscriptionService {

    private final SubscriptionDatabaseService subscriptionDatabaseService;
    private final SubscriptionPackService subscriptionPackService;
    private final NotificationService notificationService;
    private final SubscriptionCache subscriptionCache;

    public void subscribeDefaultPack(final String requestId, final String userId) {
        log.info("[{}] Subscribing default pack", requestId);

        final var activeSubscription = getActiveSubscriptionForUserId(requestId, userId);

        if (activeSubscription.isPresent()) {
            log.warn("[{}] Active subscription found", requestId);
            throw new SubscriptionException(
                    new String[]{"Active subscription found"},
                    HttpStatus.CONFLICT.value(),
                    "Failed to subscribe default pack"
            );
        }

        final var defaultSubscriptionPack = subscriptionPackService.getDefaultSubscriptionPack(requestId);

        if (defaultSubscriptionPack.isEmpty()) {
            log.warn("[{}] Default subscription pack not found", requestId);
            throw new SubscriptionException(
                    new String[]{"No default subscription pack found"},
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Failed to subscribe default pack"
            );
        }

        final var subscriptionPack = defaultSubscriptionPack.get();
        final var timestamp = System.currentTimeMillis();

        final var subscription = new Subscription();
        subscription.setId(generateSubscriptionId(userId, subscriptionPack.getId()));
        subscription.setPackId(subscriptionPack.getId());
        subscription.setUserId(userId);
        subscription.setCurrency(subscriptionPack.getCurrency());
        subscription.setDescription("Default Free Subscription");
        subscription.setAmount(subscriptionPack.getPrice());
        subscription.setSubscribedAt(timestamp);
        subscription.setDefaultSubscription(Boolean.TRUE);
        subscription.setStatus(SubscriptionStatus.ACTIVE.name());
        subscription.setExpiresAt(timestamp + subscriptionPack.getValidityDuration());

        final var createdSubscription = subscriptionDatabaseService.addSubscription(requestId, subscription);
        final var subscriptionDTO = SubscriptionDTO.fromSubscription(createdSubscription);

        subscriptionCache.addSubscription(requestId, subscriptionDTO);

        log.info("{} default subscription successful for userId={}", requestId, userId);
    }

    public MakeSubscriptionResponse subscribe(final String requestId, final MakeSubscriptionRequest request) {
        log.info("{} received create subscription request: {}", requestId, request);

        final var subscriptionPack = subscriptionPackService.getSubscriptionPackByPackId(requestId, request.packId())
                .orElseThrow(() -> {
                    log.error("{} subscription pack not found for id {}", requestId, request.packId());

                    return new BadRequestException(
                            new String[]{"No pack found with id %s".formatted(request.packId())},
                            "Pack not found"
                    );
                });

        final var timestamp = System.currentTimeMillis();
        final var activeSubscription = subscriptionDatabaseService.findActiveSubscription(requestId, request.userId());

        if (activeSubscription.isPresent() &&
                activeSubscription.get().getExpiresAt() > timestamp &&
                Boolean.FALSE.equals(activeSubscription.get().getDefaultSubscription())) {
            final var subscriptionId = activeSubscription.get().getId();
            log.warn("{} active subscription found for userId={}, subscriptionId={}", requestId, request.userId(), subscriptionId);
            return new MakeSubscriptionResponse(
                    HttpStatus.CONFLICT.value(),
                    "Subscription already exists with subscription id %s".formatted(subscriptionId),
                    SubscriptionDTO.fromSubscription(activeSubscription.get())
            );
        }

        if (activeSubscription.isPresent()) {
            activeSubscription.get().setExpiresAt(System.currentTimeMillis());
            activeSubscription.get().setStatus(SubscriptionStatus.EXPIRED.name());
            final var updatedSubscription = subscriptionDatabaseService.updateSubscription(requestId, activeSubscription.get());
            subscriptionCache.addSubscription(requestId, SubscriptionDTO.fromSubscription(updatedSubscription));
            log.info("{} current subscription marked as {}", requestId, SubscriptionStatus.EXPIRED.name());
        }

        final var subscription = new Subscription();
        subscription.setId(generateSubscriptionId(request.userId(), request.packId()));
        subscription.setAmount(request.amount());
        subscription.setCurrency(request.currency());
        subscription.setExpiresAt(timestamp + subscriptionPack.getValidityDuration());
        subscription.setSubscribedAt(timestamp);
        subscription.setUserId(request.userId());
        subscription.setDescription(request.description());
        subscription.setPackId(subscriptionPack.getId());
        subscription.setDefaultSubscription(Boolean.FALSE);
        subscription.setStatus(SubscriptionStatus.ACTIVE.name());

        final var createdSubscription = subscriptionDatabaseService.addSubscription(requestId, subscription);

        log.info("{} subscription successful for userId={} for packId={}", requestId, request.userId(), request.packId());

        final var subscriptionDTO = SubscriptionDTO.fromSubscription(createdSubscription);

        subscriptionCache.addSubscription(requestId, subscriptionDTO);

        subscriptionDTO.setEmail(request.email());
        subscriptionDTO.setName(request.name());

        notificationService.sendSubscriptionSuccessEmail(requestId,
                subscriptionDTO,
                SubscriptionPackDTO.fromSubscriptionPack(subscriptionPack)
        );

        return new MakeSubscriptionResponse(
                HttpStatus.CREATED.value(),
                "Subscription successful",
                subscriptionDTO
        );
    }

    public GetSubscriptionResponse getActiveUserSubscription(final String requestId, final String userId) {
        log.info("[{}] received get active subscription request for userId {}", requestId, userId);

        final var subscriptionDTOOptional = getActiveSubscriptionForUserId(requestId, userId);

        if (subscriptionDTOOptional.isEmpty()) {
            log.info("[{}] subscription not found for userId={}", requestId, userId);

            return GetSubscriptionResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("No active subscription found")
                    .build();
        }

        final var subscriptionPackOptional = subscriptionPackService.getSubscriptionPackByPackId(
                requestId,
                subscriptionDTOOptional.get().getPackId()
        );

        if (subscriptionPackOptional.isEmpty()) {
            log.warn("[{}] subscription pack not found for userId={}", requestId, userId);
            return GetSubscriptionResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Failed to fetch subscription pack details")
                    .build();
        }

        final var subscriptionDTO = subscriptionDTOOptional.get();
        final var subscriptionPack = subscriptionPackOptional.get();

        log.debug("{} subscription fetched for userId {}: {}", requestId, userId, subscriptionDTO);
        log.debug("{} subscription pack fetched for userId {}: {}", requestId, userId, subscriptionPack);

        final var subscription = createResponseSubscriptionFromSubscription(subscriptionDTO);
        final var pack = createResponsePackFromSubscriptionPack(subscriptionPack);

        return GetSubscriptionResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Subscription details fetched successfully")
                .subscription(subscription)
                .pack(pack)
                .build();
    }

    public GetAllSubscriptionResponse getAllSubscriptions(final String requestId, final String userId) {
        log.info("[{}] received get all subscription request for userId {}", requestId, userId);

        final var subscriptions = subscriptionCache.getAllSubscriptionsByUserId(requestId, userId);

        if (subscriptions.isPresent()) {
            log.info("{} subscriptions found for userId={} in cache", requestId, userId);
            return GetAllSubscriptionResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("All subscriptions fetched successfully")
                    .subscriptions(subscriptions.get().stream().map(this::createResponseSubscriptionFromSubscription).toList())
                    .build();
        }

        final var subscriptionsFromDb = subscriptionDatabaseService.findAllSubscriptionsForUserId(requestId, userId);

        if (subscriptionsFromDb.isEmpty()) {
            log.warn("[{}] subscription not found for userId={}", requestId, userId);
            return GetAllSubscriptionResponse.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("No subscriptions found")
                    .build();
        }

        final var subscriptionDTOS = subscriptionsFromDb.stream().map(SubscriptionDTO::fromSubscription).toList();
        subscriptionCache.addUserSubscriptions(
                requestId,
                userId,
                subscriptionDTOS
        );

        return GetAllSubscriptionResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message("All subscriptions fetched successfully")
                .subscriptions(subscriptionDTOS.stream().map(this::createResponseSubscriptionFromSubscription).toList())
                .build();
    }

    private Optional<SubscriptionDTO> getActiveSubscriptionForUserId(final String requestId, final String userId) {
        final var cachedSubscriptionDtoOptional = subscriptionCache.getActiveSubscriptionByUserId(requestId, userId);

        if (cachedSubscriptionDtoOptional.isPresent()) {
            log.info("[{}] subscription found for userId={} in cache", requestId, userId);
            return cachedSubscriptionDtoOptional;
        }

        final var subscriptionOptional = subscriptionDatabaseService.findActiveSubscription(requestId, userId);

        subscriptionOptional.ifPresent(
                subscription -> subscriptionCache.addSubscription(requestId, SubscriptionDTO.fromSubscription(subscription)
                )
        );

        return subscriptionOptional.map(SubscriptionDTO::fromSubscription);
    }

    private GetSubscriptionResponsePack createResponsePackFromSubscriptionPack(final SubscriptionPack subscriptionPack) {
        final var pack = new GetSubscriptionResponsePack();
        pack.setId(subscriptionPack.getId());
        pack.setName(subscriptionPack.getName());
        pack.setPrivileges(subscriptionPack.getPrivileges());
        pack.setFeatures(subscriptionPack.getFeatures());
        pack.setDefaultPack(subscriptionPack.getDefaultPack());
        return pack;
    }

    private GetSubscriptionResponseSubscription createResponseSubscriptionFromSubscription(final SubscriptionDTO subscriptionDTO) {
        final var subscription = new GetSubscriptionResponseSubscription();
        subscription.setSubscriptionId(subscriptionDTO.getId());
        subscription.setUserId(subscriptionDTO.getUserId());
        subscription.setPackId(subscriptionDTO.getPackId());
        subscription.setActivatedAt(subscriptionDTO.getActivatedAt());
        subscription.setExpiresAt(subscriptionDTO.getExpiresAt());
        subscription.setStatus(subscriptionDTO.getStatus().name());
        return subscription;
    }

    private String generateSubscriptionId(final String userId, final String packId) {
        final var timestamp = System.currentTimeMillis() / 1000;
        final var userIdHash = userId.hashCode();
        final var result = String.format("%04x", (timestamp - userIdHash) & 0xFFFF);
        final var uuidSegment = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return String.format("sub-%s%s%s", String.format("%04x", packId.hashCode() & 0xFFFF), result, uuidSegment);
    }

}
