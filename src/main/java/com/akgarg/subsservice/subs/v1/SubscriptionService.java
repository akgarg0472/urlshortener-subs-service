package com.akgarg.subsservice.subs.v1;

import com.akgarg.subsservice.exception.BadRequestException;
import com.akgarg.subsservice.plans.v1.plan.Plan;
import com.akgarg.subsservice.plans.v1.plan.PlanRepository;
import com.akgarg.subsservice.request.MakeSubscriptionRequest;
import com.akgarg.subsservice.request.VerifySubscriptionRequest;
import com.akgarg.subsservice.response.GetSubscriptionResponse;
import com.akgarg.subsservice.response.MakeSubscriptionResponse;
import com.akgarg.subsservice.response.VerifySubscriptionResponse;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.akgarg.subsservice.utils.SubsUtils.generateLogId;

@Service
@AllArgsConstructor
public class SubscriptionService {

    private static final Logger LOGGER = LogManager.getLogger(SubscriptionService.class);

    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;

    public MakeSubscriptionResponse subscribe(final MakeSubscriptionRequest request) {
        final String logId = generateLogId();
        LOGGER.info("{} received create subscription request: {}", logId, request);

        final Plan plan = planRepository.findById(request.planId())
                .orElseThrow(() -> {
                    LOGGER.error("{} plan not found with id {}", logId, request.planId());
                    return new BadRequestException(
                            new String[]{"No plan found with id %s".formatted(request.planId())},
                            "Plan not found"
                    );
                });

        final long timestamp = System.currentTimeMillis();
        final Optional<Subscription> lastSubscription = subscriptionRepository.findFirstByUserIdOrderByExpiresAtDesc(request.userId());

        if (lastSubscription.isPresent() && lastSubscription.get().getExpiresAt() > timestamp) {
            final String subscriptionId = lastSubscription.get().getId();
            LOGGER.error("{} subscription already exists for userId={}, subscriptionId={}", logId, request.userId(), subscriptionId);
            return new MakeSubscriptionResponse(
                    HttpStatus.CONFLICT.value(),
                    "Subscription already exists with subscription id %s".formatted(subscriptionId)
            );
        }

        final Subscription subscription = new Subscription();
        subscription.setId(UUID.randomUUID().toString());
        subscription.setAmount(request.amount());
        subscription.setCurrency(request.currency());
        subscription.setExpiresAt(timestamp + plan.getValidity());
        subscription.setSubscribedAt(timestamp);
        subscription.setUserId(request.userId());
        subscription.setDescription(request.description());
        subscription.setPlan(plan);

        subscriptionRepository.save(subscription);

        LOGGER.info("{} subscription successful for userId={} for planId={}", logId, request.userId(), request.planId());

        return new MakeSubscriptionResponse(
                HttpStatus.CREATED.value(),
                "Subscription successful with id=%s".formatted(subscription.getId())
        );
    }

    public GetSubscriptionResponse getSubscriptionByUserId(final String userId) {
        final String logId = generateLogId();
        LOGGER.info("{} received get subscription request for userId {}", logId, userId);

        final Optional<Subscription> subscriptionOptional = subscriptionRepository.findFirstByUserIdOrderByExpiresAtDesc(userId);

        if (subscriptionOptional.isEmpty()) {
            LOGGER.error("{} no subscription found for userId={}", logId, userId);
            return new GetSubscriptionResponse(
                    HttpStatus.NOT_FOUND.value(),
                    "No subscription found for userId %s".formatted(userId),
                    null
            );
        }

        final Subscription subscription = subscriptionOptional.get();
        final long timestamp = System.currentTimeMillis();

        if (subscription.getExpiresAt() <= timestamp) {
            LOGGER.error("{} no active subscription found for userId={}", logId, userId);
            return new GetSubscriptionResponse(
                    HttpStatus.NOT_FOUND.value(),
                    "No active subscription found for userId %s".formatted(userId),
                    null
            );
        }

        final SubscriptionDTO subscriptionDTO = SubscriptionMapper.toDto(subscription);

        return new GetSubscriptionResponse(
                HttpStatus.OK.value(),
                "Subscription found",
                subscriptionDTO
        );
    }

    public VerifySubscriptionResponse verifySubscription(final VerifySubscriptionRequest request) {
        final String logId = generateLogId();
        LOGGER.info(
                "{} received verify subscription request for userId={}, planId={} and subsId={}",
                logId,
                request.userId(),
                request.planId(),
                request.subsId()
        );

        final Optional<Subscription> subscriptionOptional = subscriptionRepository.findFirstByUserIdOrderByExpiresAtDesc(request.userId());

        if (subscriptionOptional.isEmpty()) {
            LOGGER.error("{} subscription verification failed. No subscription found", logId);
            return new VerifySubscriptionResponse(
                    HttpStatus.OK.value(),
                    "No subscription found",
                    false
            );
        }

        final long timestamp = System.currentTimeMillis();
        final Subscription subscription = subscriptionOptional.get();

        if (subscription.getExpiresAt() < timestamp) {
            LOGGER.error("{} subscription verification failed because no active subscription found", logId);
            return new VerifySubscriptionResponse(
                    HttpStatus.OK.value(),
                    "No active subscription found",
                    false
            );
        }

        if (!subscription.getId().equals(request.subsId()) || !subscription.getPlan().getId().equals(request.planId())) {
            LOGGER.error("{} subscription verification failed due to invalid params", logId);
            return new VerifySubscriptionResponse(
                    HttpStatus.OK.value(),
                    "Subscription verification failed",
                    false
            );
        }

        LOGGER.info("{} subscription verification successful", logId);

        return new VerifySubscriptionResponse(
                HttpStatus.OK.value(),
                "Subscription verified successfully",
                true
        );
    }

}