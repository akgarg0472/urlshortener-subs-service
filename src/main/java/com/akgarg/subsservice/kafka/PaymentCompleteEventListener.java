package com.akgarg.subsservice.kafka;

import com.akgarg.subsservice.request.MakeSubscriptionRequest;
import com.akgarg.subsservice.v1.subs.SubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Profile("prod")
@Component
@RequiredArgsConstructor
public class PaymentCompleteEventListener extends AbstractKafkaEventListener {

    private final SubscriptionService subscriptionService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.payment.success.topic.name:urlshortener.payment.events}",
            containerFactory = "paymentEventManualAckConcurrentKafkaListenerContainerFactory")
    public void onMessage(final ConsumerRecord<String, String> consumerRecord, final Acknowledgment acknowledgment) {
        final var requestId = generateRequestId(consumerRecord);
        log.info("[{}] received kafka payment event: {}", requestId, consumerRecord.value());

        try {
            final var paymentCompleteEvent = objectMapper.readValue(consumerRecord.value(), PaymentCompleteEvent.class);

            final var subscriptionRequest = new MakeSubscriptionRequest(
                    Objects.requireNonNull(paymentCompleteEvent.userId(), "userId is null"),
                    Objects.requireNonNull(paymentCompleteEvent.packId(), "packId is null"),
                    Objects.requireNonNull(paymentCompleteEvent.paymentId(), "paymentId is null"),
                    Objects.requireNonNull(paymentCompleteEvent.amount(), "amount is null"),
                    Objects.requireNonNull(paymentCompleteEvent.currency(), "currency is null"),
                    paymentCompleteEvent.paymentGateway(),
                    paymentCompleteEvent.email(),
                    paymentCompleteEvent.name()
            );

            subscriptionService.subscribe(requestId, subscriptionRequest);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("[{}] error processing kafka event", requestId, e);
        }
    }

}
