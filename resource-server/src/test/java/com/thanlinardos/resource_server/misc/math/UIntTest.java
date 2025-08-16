package com.thanlinardos.resource_server.misc.math;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UIntTest {

    public static Stream<Arguments> rangeUIntFactory() {
        return LongStream.range(2, 100)
                .mapToObj(Arguments::of);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, 1, -1, -100, -4294967295L, UInt.MIN_VALUE, UInt.MAX_VALUE, 131435L})
    @MethodSource("rangeUIntFactory")
    void testUInt_zero(Long value) {
        if (value == null) {
            assertThrows(NullPointerException.class, () -> new UInt(value));
        } else if (value < UInt.MIN_VALUE || value > UInt.MAX_VALUE) {
            assertThrows(IllegalArgumentException.class, () -> new UInt(value));
        } else {
            UInt uInt = new UInt(value);
            assertEquals(value, uInt.longValue());
        }
    }
}