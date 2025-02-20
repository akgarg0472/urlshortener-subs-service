package com.akgarg.subsservice.v1.pack;

import com.akgarg.subsservice.request.CreatePackRequest;
import com.akgarg.subsservice.request.UpdatePackRequest;
import com.akgarg.subsservice.response.CreatePackResponse;
import com.akgarg.subsservice.response.DeletePackResponse;
import com.akgarg.subsservice.response.GetPacksResponse;
import com.akgarg.subsservice.response.UpdatePackResponse;
import com.akgarg.subsservice.v1.pack.cache.SubscriptionPackCache;
import com.akgarg.subsservice.v1.pack.db.SubscriptionPackDatabaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;

@Slf4j
@Service
@AllArgsConstructor
public class SubscriptionPackService {

    private static final String PACK_NOT_FOUND_BY_ID_LOG_MSG = "Pack with id {} not found";

    private final SubscriptionPackDatabaseService subscriptionPackDatabaseService;
    private final SubscriptionPackCache subscriptionPackCache;

    public CreatePackResponse createPack(final CreatePackRequest request) {
        log.info("Create pack request received: {}", request);

        final var existingPack = getSubscriptionPackByPackId(request.id());

        if (existingPack.isPresent()) {
            log.warn("Subscription pack already exists for id: {}", request.id());
            return new CreatePackResponse(HttpStatus.CONFLICT.value(),
                    "Pack already exists with id: " + request.id(),
                    SubscriptionPackDTO.fromSubscriptionPack(existingPack.get())
            );
        }

        final var pack = getSubscriptionPack(request);

        final var savedPack = subscriptionPackDatabaseService.saveOrUpdatePack(pack);

        log.info("Subscription pack created successfully: {}", savedPack);

        subscriptionPackCache.addOrUpdatePack(savedPack);

        log.info("Subscription pack created successfully: {}", savedPack);

        final var subscriptionPackDTO = SubscriptionPackDTO.fromSubscriptionPack(savedPack);

        return new CreatePackResponse(HttpStatus.CREATED.value(),
                "Pack created successfully",
                subscriptionPackDTO
        );
    }

    public GetPacksResponse getPacks(final int page, final int limit, final boolean getComparison) {
        log.debug("Get packs request received, page={}, limit={}", page, limit);

        final var subscriptionPacks = subscriptionPackCache.getAllPacks(
                page * limit,
                limit,
                true,
                false
        );

        if (subscriptionPacks.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("Fetching subscription packs from database");
            }

            final var pageRequest = PageRequest.of(
                    page,
                    limit,
                    Sort.by("createdAt").descending()
            );

            final var packs = subscriptionPackDatabaseService.findAllByVisibleAndDeleted(
                    true,
                    false,
                    pageRequest
            );

            subscriptionPacks.addAll(packs);
            packs.forEach(subscriptionPackCache::addOrUpdatePack);
        }

        final var subscriptionPackDTOS = subscriptionPacks.stream()
                .map(SubscriptionPackDTO::fromSubscriptionPack)
                .sorted(Comparator.comparing(SubscriptionPackDTO::order))
                .toList();

        return GetPacksResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .packs(subscriptionPackDTOS)
                .comparisons(getComparison ? getComparisonTableForPacks(subscriptionPackDTOS) : null)
                .build();
    }

    public Optional<SubscriptionPack> getDefaultSubscriptionPack() {
        log.info("Retrieving default subscription pack");

        var subscriptionPack = subscriptionPackCache.getDefaultSubscriptionPack();

        if (subscriptionPack.isPresent()) {
            log.debug("Retrieved default subscription pack from cache");
            return subscriptionPack;
        }

        subscriptionPack = subscriptionPackDatabaseService.findDefaultSubscriptionPack();
        subscriptionPack.ifPresent(subscriptionPackCache::addOrUpdatePack);
        return subscriptionPack;
    }

    public UpdatePackResponse updatePack(final String packId, final UpdatePackRequest request) {
        log.info("Request received to update pack with id: {} -> {}", packId, request);

        final var packOptional = getSubscriptionPackByPackId(packId);

        if (packOptional.isEmpty()) {
            log.warn(PACK_NOT_FOUND_BY_ID_LOG_MSG, packId);

            return new UpdatePackResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "No pack found with id: " + packId,
                    null
            );
        }

        final var pack = packOptional.get();

        if (log.isDebugEnabled()) {
            log.debug("Existing pack: {}", pack);
        }

        final boolean updated = updatePackFields(pack, request);

        if (!updated) {
            log.info("Pack not updated because no new field was provided");
            return new UpdatePackResponse(HttpStatus.OK.value(),
                    "No fields provided for update. The pack remains unchanged",
                    null
            );
        }

        pack.setUpdatedAt(System.currentTimeMillis());

        final var updatedPack = subscriptionPackDatabaseService.saveOrUpdatePack(pack);
        final var updatedPackDTO = SubscriptionPackDTO.fromSubscriptionPack(updatedPack);
        subscriptionPackCache.addOrUpdatePack(updatedPack);

        log.info("Subscription pack updated successfully: {}", updatedPackDTO);

        return new UpdatePackResponse(HttpStatus.OK.value(),
                "Pack updated successfully",
                updatedPackDTO
        );
    }

    public DeletePackResponse deletePack(final String packId) {
        log.info("Received request to delete pack with id: {}", packId);

        final var packOptional = getSubscriptionPackByPackId(packId);

        if (packOptional.isEmpty()) {
            log.error(PACK_NOT_FOUND_BY_ID_LOG_MSG, packId);
            return new DeletePackResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    "No pack found to delete",
                    null
            );
        }

        final var pack = packOptional.get();

        if (pack.isDeleted()) {
            return new DeletePackResponse(
                    HttpStatus.OK.value(),
                    "Pack is already deleted",
                    null
            );
        }

        pack.setDeleted(true);
        pack.setVisible(false);
        pack.setSelected(false);

        final var deletedPack = subscriptionPackDatabaseService.saveOrUpdatePack(pack);

        log.info("Pack with id {} deleted successfully", packId);

        subscriptionPackCache.deletePack(packId);

        final var subscriptionPackDTO = SubscriptionPackDTO.fromSubscriptionPack(deletedPack);

        return new DeletePackResponse(
                HttpStatus.OK.value(),
                "Pack deleted successfully with id=%s".formatted(packId),
                subscriptionPackDTO
        );
    }

    private boolean updatePackFields(final SubscriptionPack pack, final UpdatePackRequest request) {
        var updated = updateFieldIfDifferent(pack::setName, request.name(), pack.getName());

        if (updateFieldIfDifferent(pack::setDescription, request.description(), pack.getDescription())) {
            updated = true;
        }

        if (updateFieldIfDifferent(pack::setPrice, request.price(), pack.getPrice())) {
            updated = true;
        }

        if (updateFieldIfDifferent(pack::setFeatures, request.features(), pack.getFeatures())) {
            updated = true;
        }

        if (updateFieldIfDifferent(pack::setPrivileges, request.privileges(), pack.getPrivileges())) {
            updated = true;
        }

        if (request.validity() != null) {
            final var packValidity = SubscriptionPackValidity.fromLabel(request.validity());

            if (updateFieldIfDifferent(pack::setValidityLabel, packValidity.getLabel(), pack.getValidityLabel())) {
                updated = true;
            }

            if (updateFieldIfDifferent(pack::setValidityDuration, packValidity.getDurationInMillis(), pack.getValidityDuration())) {
                updated = true;
            }
        }

        if (request.visible() != null && updateFieldIfDifferent(pack::setVisible, request.visible(), pack.isVisible())) {
            updated = true;
        }

        if (request.selected() != null && updateFieldIfDifferent(pack::setSelected, request.selected(), pack.getSelected())) {
            updated = true;
        }

        if (request.defaultPack() != null && updateFieldIfDifferent(pack::setDefaultPack, request.defaultPack(), pack.getDefaultPack())) {
            updated = true;
        }

        return updated;
    }

    private <T> boolean updateFieldIfDifferent(final Consumer<T> setter, final T newValue, final T oldValue) {
        if (newValue != null && !Objects.equals(newValue, oldValue)) {
            setter.accept(newValue);
            return true;
        }
        return false;
    }

    public Optional<SubscriptionPack> getSubscriptionPackByPackId(final String packId) {
        return subscriptionPackCache.getPackById(packId)
                .or(() -> subscriptionPackDatabaseService.findByPackId(packId));
    }

    private SubscriptionPack getSubscriptionPack(final CreatePackRequest request) {
        final var packValidity = SubscriptionPackValidity.fromLabel(request.validity());
        final var pack = new SubscriptionPack();
        pack.setId(request.id());
        pack.setName(request.name());
        pack.setDescription(request.description());
        pack.setPrice(request.price());
        pack.setCurrency(request.currency());
        pack.setValidityLabel(packValidity.getLabel());
        pack.setValidityDuration(packValidity.getDurationInMillis());
        pack.setVisible(request.visible());
        pack.setFeatures(request.features());
        pack.setPrivileges(request.privileges());
        pack.setSelected(request.selected());
        pack.setDeleted(false);
        pack.setDefaultPack(request.defaultPack());
        return pack;
    }

    private PackComparison getComparisonTableForPacks(final List<SubscriptionPackDTO> packs) {
        final var headers = new ArrayList<String>();
        headers.add("Feature");
        packs.forEach(pack -> headers.add(pack.name()));

        final var rows = new ArrayList<List<Object>>();

        for (final var packPrivilege : PackPrivilege.values()) {
            if (packPrivilege == PackPrivilege.ANALYTIC) {
                continue;
            }

            final var row = new ArrayList<>();
            final var feature = packPrivilege.value();
            row.add(packPrivilege.key());

            for (final var pack : packs) {
                var featureFound = false;

                for (final var privilege : pack.privileges()) {
                    if (privilege.startsWith(feature)) {
                        final var value = getPrivilegeValue(privilege);
                        row.add(value);
                        featureFound = true;
                        break;
                    }
                }

                if (!featureFound) {
                    row.add(null);
                }
            }

            rows.add(row);
        }

        return new PackComparison(headers, rows);
    }

    private Object getPrivilegeValue(final String privilege) {
        final var value = privilege.split(":", 2)[1].trim();

        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(value);
        } else {
            return value;
        }
    }

}
