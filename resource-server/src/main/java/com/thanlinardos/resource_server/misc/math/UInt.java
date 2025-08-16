package com.thanlinardos.resource_server.misc.math;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class UInt extends Number {
    public static final long MIN_VALUE = 0L;
    public static final long MAX_VALUE = 4294967295L;

    private final long value;

    public UInt(long value) {
        if (!isValueValid(value)) {
            throw new IllegalArgumentException("Value is not a valid unsigned integer: " + value);
        }
        this.value = value;
    }

    private boolean isValueValid(long value) {
        return value >= MIN_VALUE && value <= MAX_VALUE;
    }

    @Override
    public int intValue() {
        return (int) value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    public String toString() {
        return this.value + "U";
    }
}
