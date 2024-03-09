package com.akgarg.subsservice.subs.v1;

import com.akgarg.subsservice.request.MakeSubscriptionRequest;
import com.akgarg.subsservice.response.GetSubscriptionResponse;
import com.akgarg.subsservice.response.MakeSubscriptionResponse;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(final SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public MakeSubscriptionResponse subscribe(final MakeSubscriptionRequest request) {
        return null;
    }

    public GetSubscriptionResponse getSubscriptionByUserId(final String userId) {
        return null;
    }

}
