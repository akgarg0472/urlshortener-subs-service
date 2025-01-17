package com.akgarg.subsservice.v1.pack;

import java.util.List;

public record PackComparison(List<String> headers,
                             List<List<Object>> rows) {
}
