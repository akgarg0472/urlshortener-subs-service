package com.akgarg.subsservice.v1.subs;

import com.akgarg.subsservice.exception.BadRequestException;
import com.akgarg.subsservice.exception.SubscriptionException;
import com.akgarg.subsservice.notification.NotificationService;
import com.akgarg.subsservice.request.MakeSubscriptionRequest;
import com.akgarg.subsservice.response.GetSubscriptionResponse;
import com.akgarg.subsservice.response.MakeSubscriptionResponse;
import com.akgarg.subsservice.v1.pack.SubscriptionPackService;
import com.akgarg.subsservice.v1.subs.cache.SubscriptionCache;
import com.akgarg.subsservice.v1.subs.db.SubscriptionDatabaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
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

        if (activeSubscription.isPresent() && activeSubscription.get().getExpiresAt() > timestamp) {
            final var subscriptionId = activeSubscription.get().getId();
            log.warn("{} subscription already exists for userId={}, subscriptionId={}", requestId, request.userId(), subscriptionId);
            return new MakeSubscriptionResponse(
                    HttpStatus.CONFLICT.value(),
                    "Subscription already exists with subscription id %s".formatted(subscriptionId),
                    SubscriptionDTO.fromSubscription(activeSubscription.get())
            );
        }

        final var subscription = new Subscription();
        subscription.setId(UUID.randomUUID().toString().replace("-", ""));
        subscription.setAmount(request.amount());
        subscription.setCurrency(request.currency());
        subscription.setExpiresAt(timestamp + subscriptionPack.getValidityDuration());
        subscription.setSubscribedAt(timestamp);
        subscription.setUserId(request.userId());
        subscription.setDescription(request.description());
        subscription.setPackId(subscriptionPack.getId());

        final var createdSubscription = subscriptionDatabaseService.addSubscription(requestId, subscription);

        log.info("{} subscription successful for userId={} for packId={}", requestId, request.userId(), request.packId());

        final var subscriptionDTO = SubscriptionDTO.fromSubscription(createdSubscription);

        subscriptionCache.addSubscription(requestId, subscriptionDTO);

        notificationService.sendSubscriptionSuccess(requestId, subscriptionDTO);

        return new MakeSubscriptionResponse(
                HttpStatus.CREATED.value(),
                "Subscription successful",
                subscriptionDTO
        );
    }

    public GetSubscriptionResponse getUserSubscription(final String requestId, final String userId) {
        log.info("{} received get subscription request for userId {}", requestId, userId);

        final var subscriptionDTOOptional = getSubscriptionForUserId(requestId, userId);

        if (subscriptionDTOOptional.isEmpty()) {
            log.info("{} subscription not found for userId={}", requestId, userId);

            return new GetSubscriptionResponse(
                    HttpStatus.OK.value(),
                    "No active subscription found",
                    null
            );
        }

        final var subscriptionDTO = subscriptionDTOOptional.get();
        log.debug("{} subscription fetched for userId={}: {}", requestId, userId, subscriptionDTO);

        return new GetSubscriptionResponse(
                HttpStatus.OK.value(),
                "Subscription retrieved successfully",
                subscriptionDTO
        );
    }

    public List<ActiveSubscription> getAllActiveSubscriptions() {
        try {
            final var subscriptions = subscriptionDatabaseService.findAllActiveSubscriptions();
            return subscriptions.stream()
                    .map(subscription -> new ActiveSubscription(
                                    subscription.getUserId(),
                                    subscription.getPackId(),
                                    subscription.getExpiresAt()
                            )
                    ).toList();
        } catch (Exception e) {
            throw new SubscriptionException(new String[]{"Failed to fetch active subscription details"},
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Failed to get active subscriptions");
        }
    }

    private Optional<SubscriptionDTO> getSubscriptionForUserId(final String requestId, final String userId) {
        final var cachedSubscriptionDtoOptional = subscriptionCache.getSubscriptionByUserId(requestId, userId);

        if (cachedSubscriptionDtoOptional.isPresent()) {
            log.info("{} subscription found for userId={} in cache", requestId, userId);
            return cachedSubscriptionDtoOptional;
        }

        final var subscriptionOptional = subscriptionDatabaseService.findActiveSubscription(requestId, userId);

        subscriptionOptional.ifPresent(
                subscription -> subscriptionCache.addSubscription(requestId, SubscriptionDTO.fromSubscription(subscription)
                )
        );

        return subscriptionOptional.map(SubscriptionDTO::fromSubscription);
    }

}
