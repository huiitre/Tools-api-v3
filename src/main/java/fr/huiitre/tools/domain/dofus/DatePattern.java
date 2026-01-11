package fr.huiitre.tools.domain.dofus;

import java.util.Objects;

public class DatePattern {
    
    private final String raw;

    public DatePattern(String raw) {
        if (raw == null || raw.isBlank()) throw new IllegalArgumentException("ALMANAX_PATTERN_REQUIRED");
        // validation “simple” (à adapter) : 3 segments séparés par /
        String[] parts = raw.split("/");
        if (parts.length != 3) throw new IllegalArgumentException("ALMANAX_PATTERN_INVALID");
        this.raw = raw;
    }

    public String raw() { return raw; }

    @Override public boolean equals(Object o) {
        return (o instanceof DatePattern p) && Objects.equals(raw, p.raw);
    }
    @Override public int hashCode() { return Objects.hash(raw); }
    @Override public String toString() { return raw; }
}
