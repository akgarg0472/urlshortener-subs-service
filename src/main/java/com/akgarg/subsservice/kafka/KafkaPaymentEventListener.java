package com.akgarg.subsservice.kafka;

import com.akgarg.subsservice.exception.BadRequestException;
import com.akgarg.subsservice.request.MakeSubscriptionRequest;
import com.akgarg.subsservice.v1.subs.SubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Profile("prod")
@Component
@RequiredArgsConstructor
public class KafkaPaymentEventListener {

    private final SubscriptionService subscriptionService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.payment.success.topic.name}", containerFactory = "manualAckConcurrentKafkaListenerContainerFactory")
    public void onMessage(final ConsumerRecord<String, String> consumerRecord, final Acknowledgment acknowledgment) {
        log.info("received kafka payment event: {}", consumerRecord.value());

        boolean isProcessingSuccessful = false;

        try {
            final var paymentCompleteEvent = objectMapper.readValue(consumerRecord.value(), PaymentCompleteEvent.class);

            final var subscriptionRequest = new MakeSubscriptionRequest(
                    Objects.requireNonNull(paymentCompleteEvent.userId(), "userId is null"),
                    Objects.requireNonNull(paymentCompleteEvent.packId(), "packId is null"),
                    Objects.requireNonNull(paymentCompleteEvent.paymentId(), "paymentId is null"),
                    Objects.requireNonNull(paymentCompleteEvent.amount(), "amount is null"),
                    Objects.requireNonNull(paymentCompleteEvent.currency(), "currency is null"),
                    paymentCompleteEvent.paymentGateway()
            );

            final var requestId = consumerRecord.key();
            final var subscriptionResponse = subscriptionService.subscribe(requestId, subscriptionRequest);

            if (subscriptionResponse.statusCode() == HttpStatus.CREATED.value() ||
                    subscriptionResponse.statusCode() == HttpStatus.CONFLICT.value()) {
                isProcessingSuccessful = true;
            }
        } catch (Exception e) {
            log.error("error processing kafka event", e);

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
