package com.akgarg.subsservice.v1.pack;

import com.akgarg.subsservice.exception.SubscriptionException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum SubscriptionPackValidity {

    MONTHLY("month", 30L * 24 * 60 * 60 * 1000),
    ANNUAL("annual", 365L * 24 * 60 * 60 * 1000);

    private final String label;
    private final long durationInMillis;

    public static SubscriptionPackValidity fromLabel(final String label) {
        for (final var validity : values()) {
            if (validity.getLabel().equalsIgnoreCase(label)) {
                return validity;
            }
        }
        throw new SubscriptionException(
                new String[]{},
                HttpStatus.BAD_REQUEST.value(),
                "Invalid subscription pack validity: " + label + ". Expected values are " + SubscriptionPackValidity.labels()
        );
    }

    private static List<String> labels() {
        return Arrays.stream(values()).map(SubscriptionPackValidity::getLabel).toList();
    }

}
