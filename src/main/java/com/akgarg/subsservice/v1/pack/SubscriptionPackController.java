package com.akgarg.subsservice.v1.pack;

import com.akgarg.subsservice.request.CreatePackRequest;
import com.akgarg.subsservice.request.UpdatePackRequest;
import com.akgarg.subsservice.response.CreatePackResponse;
import com.akgarg.subsservice.response.DeletePackResponse;
import com.akgarg.subsservice.response.GetPacksResponse;
import com.akgarg.subsservice.response.UpdatePackResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static com.akgarg.subsservice.utils.SubsUtils.checkValidationResultAndThrowExceptionOnFailure;

@RestController
@RequestMapping("/api/v1/subscriptions/packs")
@RequiredArgsConstructor
public class SubscriptionPackController {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    private final SubscriptionPackService subscriptionPackService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetPacksResponse> getSubscriptionPacks(
            @RequestHeader(value = REQUEST_ID_HEADER) final String requestId,
            @RequestParam(value = "page", defaultValue = "0") final int page,
            @RequestParam(name = "limit", defaultValue = "3") final int limit,
            @RequestParam(value = "getComparison", defaultValue = "false") final boolean getComparison
    ) {
        final var response = subscriptionPackService.getPacks(requestId, page, limit, getComparison);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatePackResponse> createSubscriptionPack(
            @RequestHeader(value = REQUEST_ID_HEADER) final String requestId,
            @RequestBody @Valid final CreatePackRequest request,
            final BindingResult validationResult
    ) {
        checkValidationResultAndThrowExceptionOnFailure(validationResult);
        final var response = subscriptionPackService.createPack(requestId, request);
        return ResponseEntity.status(response.statusCode()).body(response);
    }

    @PatchMapping(value = "/{packId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UpdatePackResponse> updateSubscriptionPlan(
            @RequestHeader(value = REQUEST_ID_HEADER) final String requestId,
            @PathVariable(name = "packId") final String packId,
            @RequestBody final UpdatePackRequest request
    ) {
        final var response = subscriptionPackService.updatePack(requestId, packId, request);
        return ResponseEntity.status(response.statusCode()).body(response);
    }

    @DeleteMapping(value = "/{packId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<DeletePackResponse> deleteSubscriptionPack(
            @RequestHeader(value = REQUEST_ID_HEADER) final String requestId,
            @PathVariable(name = "packId") final String packId) {
        final var response = subscriptionPackService.deletePack(requestId, packId);
        return ResponseEntity.status(response.statusCode()).body(response);
    }

}
