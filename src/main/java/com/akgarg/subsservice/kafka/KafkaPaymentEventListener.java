package com.akgarg.subsservice.kafka;

import com.akgarg.subsservice.exception.BadRequestException;
import com.akgarg.subsservice.request.MakeSubscriptionRequest;
import com.akgarg.subsservice.response.MakeSubscriptionResponse;
import com.akgarg.subsservice.v1.subs.SubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Profile("prod")
@Component
@RequiredArgsConstructor
public class KafkaPaymentEventListener {

    private static final Logger LOGGER = LogManager.getLogger(KafkaPaymentEventListener.class);

    private final SubscriptionService subscriptionService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.payment.topic.name}", containerFactory = "manualAckConcurrentKafkaListenerContainerFactory")
    public void onMessage(final ConsumerRecord<String, String> consumerRecord, final Acknowledgment acknowledgment) {
        LOGGER.info("received kafka payment event: {}", consumerRecord.value());

        boolean isProcessingSuccessful = false;

        try {
            final PaymentEvent paymentEvent = objectMapper.readValue(consumerRecord.value(), PaymentEvent.class);
            final var subscriptionRequest = new MakeSubscriptionRequest(
                    Objects.requireNonNull(paymentEvent.userId(), "userId is null"),
                    Objects.requireNonNull(paymentEvent.planId(), "planId is null"),
                    Objects.requireNonNull(paymentEvent.amount(), "amount is null"),
                    Objects.requireNonNull(paymentEvent.currency(), "currency is null"),
                    paymentEvent.paymentGateway()
            );
            final MakeSubscriptionResponse subscriptionResponse = subscriptionService.subscribe(subscriptionRequest);

            if (subscriptionResponse.statusCode() == HttpStatus.CREATED.value() ||
                    subscriptionResponse.statusCode() == HttpStatus.CONFLICT.value()) {
                isProcessingSuccessful = true;
            }
        } catch (Exception e) {
            LOGGER.error("error processing kafka event", e);

            if (e instanceof BadRequestException) {
                isProcessingSuccessful = true;
            }
        } finally {
            if (isProcessingSuccessful) {
                acknowledgment.acknowledge();
            }
        }
    }

}
