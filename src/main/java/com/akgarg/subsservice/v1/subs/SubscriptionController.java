package com.akgarg.subsservice.v1.subs;

import com.akgarg.subsservice.request.MakeSubscriptionRequest;
import com.akgarg.subsservice.response.GetAllSubscriptionResponse;
import com.akgarg.subsservice.response.GetSubscriptionResponse;
import com.akgarg.subsservice.response.MakeSubscriptionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.akgarg.subsservice.utils.SubsUtils.checkValidationResultAndThrowExceptionOnFailure;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private static final String USER_ID_HEADER_NAME = "X-USER-ID";
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    private final SubscriptionService subscriptionService;

    // TODO: restrict for admin only access
    @PostMapping
    public ResponseEntity<MakeSubscriptionResponse> makeSubscription(
            @RequestHeader(value = REQUEST_ID_HEADER) final String requestId,
            @RequestBody @Valid final MakeSubscriptionRequest request,
            final BindingResult validationResult
    ) {
        checkValidationResultAndThrowExceptionOnFailure(validationResult);
        final var response = subscriptionService.subscribe(requestId, request);
        return ResponseEntity.status(response.statusCode()).body(response);
    }

    @GetMapping("/active")
    public ResponseEntity<GetSubscriptionResponse> getActiveSubscription(
            @RequestHeader(value = REQUEST_ID_HEADER) final String requestId,
            @RequestHeader(value = USER_ID_HEADER_NAME) final String requestIdHeader,
            @RequestParam(value = "userId") final String userId
    ) {
        if (requestIdHeader == null || userId == null) {
            final var response = GetSubscriptionResponse.builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Invalid request ID")
                    .build();
            return ResponseEntity.badRequest().body(response);
        }

        if (!requestIdHeader.equals(userId)) {
            final var response = GetSubscriptionResponse.builder()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .message("You are not authorized to access this resource")
                    .build();
            return ResponseEntity.badRequest().body(response);
        }

        final var response = subscriptionService.getActiveUserSubscription(requestId, userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping
    public ResponseEntity<GetAllSubscriptionResponse> getAllSubscription(
            @RequestHeader(value = REQUEST_ID_HEADER) final String requestId,
            @RequestHeader(value = USER_ID_HEADER_NAME) final String requestIdHeader,
            @RequestParam(value = "userId") final String userId) {
        if (requestIdHeader == null || userId == null) {
            final var response = GetAllSubscriptionResponse.builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Invalid request ID")
                    .build();
            return ResponseEntity.badRequest().body(response);
        }

        if (!requestIdHeader.equals(userId)) {
            final var response = GetAllSubscriptionResponse.builder()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .message("You are not authorized to access this resource")
                    .build();
            return ResponseEntity.badRequest().body(response);
        }

        final var response = subscriptionService.getAllSubscriptions(requestId, userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    // used by other services (Payment service) to keep track of all active subscriptions
    @GetMapping("/active-all")
    public ResponseEntity<List<ActiveSubscription>> getActiveSubscriptions() {
        final var response = subscriptionService.getAllActiveSubscriptions();
        return ResponseEntity.ok(response);
    }

}
