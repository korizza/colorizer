package org.korizza.colorizer.io;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ColorizerNativeTest {
    private static final Logger log = Logger.getLogger(ColorizerNativeTest.class);

    @Test
    public void colorize() {
        ColorizerNative colorizerNative = new ColorizerNative((x) -> {
            return 0;
        });

        int[] numberSet = {-16776961, -16776961, -16776961};
        assertArrayEquals(numberSet, colorizerNative.colorize("123"));

        int[] textrSet = {-16777216, -16777216, -16777216};
        assertArrayEquals(textrSet, colorizerNative.colorize("Abc"));

        int[] spaceSet = {-1, -1, -1};
        assertArrayEquals(spaceSet, colorizerNative.colorize("   "));
    }

    @Test
    public void test1() {
        ColorizerNative colorizerNative = new ColorizerNative((x) -> {
            return 0;
        });
        assertEquals(true, colorizerNative.test());
    }
}
