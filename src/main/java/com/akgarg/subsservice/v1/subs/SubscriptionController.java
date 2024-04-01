package com.akgarg.subsservice.v1.subs;

import com.akgarg.subsservice.request.MakeSubscriptionRequest;
import com.akgarg.subsservice.request.VerifySubscriptionRequest;
import com.akgarg.subsservice.response.GetSubscriptionResponse;
import com.akgarg.subsservice.response.MakeSubscriptionResponse;
import com.akgarg.subsservice.response.VerifySubscriptionResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static com.akgarg.subsservice.utils.SubsUtils.checkValidationResultAndThrowExceptionOnFailure;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(final SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    public ResponseEntity<MakeSubscriptionResponse> makeSubscription(
            @RequestBody @Valid final MakeSubscriptionRequest request,
            final BindingResult validationResult
    ) {
        checkValidationResultAndThrowExceptionOnFailure(validationResult);
        final MakeSubscriptionResponse response = subscriptionService.subscribe(request);
        return ResponseEntity
                .status(response.statusCode())
                .body(response);
    }

    @GetMapping
    public ResponseEntity<GetSubscriptionResponse> getSubscription(
            @RequestParam("userId") final String userId
    ) {
        final GetSubscriptionResponse response = subscriptionService.getSubscriptionByUserId(userId);
        return ResponseEntity.status(response.statusCode()).body(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifySubscriptionResponse> verifySubscription(
            @RequestBody @Valid final VerifySubscriptionRequest request,
            final BindingResult validationResult
    ) {
        checkValidationResultAndThrowExceptionOnFailure(validationResult);
        final VerifySubscriptionResponse response = subscriptionService.verifySubscription(request);
        return ResponseEntity.status(response.statusCode()).body(response);
    }

}
