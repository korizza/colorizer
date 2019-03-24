package org.korizza.colorizer.io;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.function.UnaryOperator;

public class ColorizerNative {
    static {
        URL url = ColorizerNative.class.getResource("/native/libcolorizer.so");
        try {
            File tmpDir = Files.createTempDirectory("colorizer").toFile();
            tmpDir.deleteOnExit();

            File nativeLibTmpFile = new File(tmpDir, "libcolorizer.so");
            nativeLibTmpFile.deleteOnExit();

            try (InputStream in = url.openStream()) {
                Files.copy(in, nativeLibTmpFile.toPath());
                System.load(nativeLibTmpFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        return funcIsCanfeled.apply(idx) == 0;
    }

    public native int[] colorize(String text);

    public native boolean test();
}