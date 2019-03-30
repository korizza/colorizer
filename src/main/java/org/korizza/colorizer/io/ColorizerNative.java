package org.korizza.colorizer.io;


import org.apache.log4j.Logger;

import java.util.function.UnaryOperator;

public class ColorizerNative {
    private static final Logger log = Logger.getLogger(ColorizerTask.class);

    static {
        log.debug("java.library.path: " + System.getProperty("java.library.path"));
        try {
            System.loadLibrary("colorizer");
        } catch (Error | Exception e) {
            log.error("Failed to load JNI colorizer library: ", e);
        }
    }

    private final UnaryOperator<Integer> funcIsCanfeled;

    public ColorizerNative() {
        this.funcIsCanfeled = null;
    }

    public ColorizerNative(UnaryOperator<Integer> funcIsCanfeled) {
        this.funcIsCanfeled = funcIsCanfeled;
    }

    public boolean isCanceled(int idx) {
        return funcIsCanfeled.apply(idx) == 1;
    }

    public native int[] colorize(String text);

    public native boolean test();
}