package com.akgarg.subsservice.v1.pack;

import com.akgarg.subsservice.request.CreatePackRequest;
import com.akgarg.subsservice.request.UpdatePackRequest;
import com.akgarg.subsservice.response.*;
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

    private final SubscriptionPackService subscriptionPackService;

    @GetMapping(value = "/{packId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetSubscriptionPackResponse> getPacks(
            @PathVariable(value = "packId") final String packId
    ) {
        return subscriptionPackService
                .getSubscriptionPackByPackId(packId)
                .map(subscriptionPack -> ResponseEntity.ok().body(GetSubscriptionPackResponse.builder()
                        .statusCode(200)
                        .message("Pack found with id " + packId)
                        .pack(SubscriptionPackDTO.fromSubscriptionPack(subscriptionPack))
                        .build()))
                .orElseGet(() -> ResponseEntity.ok().body(GetSubscriptionPackResponse.builder()
                        .statusCode(404)
                        .message("Pack not found with id " + packId)
                        .build())
                );
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetPacksResponse> getAllSubscriptionPacks(
            @RequestParam(value = "page", defaultValue = "0") final int page,
            @RequestParam(name = "limit", defaultValue = "3") final int limit,
            @RequestParam(value = "getComparison", defaultValue = "false") final boolean getComparison
    ) {
        final var response = subscriptionPackService.getPacks(page, limit, getComparison);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreatePackResponse> createSubscriptionPack(
            @RequestBody @Valid final CreatePackRequest request,
            final BindingResult validationResult
    ) {
        checkValidationResultAndThrowExceptionOnFailure(validationResult);
        final var response = subscriptionPackService.createPack(request);
        return ResponseEntity.status(response.statusCode()).body(response);
    }

    @PatchMapping(value = "/{packId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UpdatePackResponse> updateSubscriptionPlan(
            @PathVariable(name = "packId") final String packId,
            @RequestBody final UpdatePackRequest request
    ) {
        final var response = subscriptionPackService.updatePack(packId, request);
        return ResponseEntity.status(response.statusCode()).body(response);
    }

    @DeleteMapping(value = "/{packId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<DeletePackResponse> deleteSubscriptionPack(
            @PathVariable(name = "packId") final String packId) {
        final var response = subscriptionPackService.deletePack(packId);
        return ResponseEntity.status(response.statusCode()).body(response);
    }

}
