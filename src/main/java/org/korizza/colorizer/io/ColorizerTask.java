package org.korizza.colorizer.io;

import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.function.BinaryOperator;

public class ColorizerTask {
    private static final Logger log = Logger.getLogger(ColorizerTask.class);

    private final String text;
    private final ColorizerNative colorizerNative;
    private final StyledDocument document;
    private final int offset;
    private final BinaryOperator<Integer> getCanceled;
    private Integer id;


    public ColorizerTask(Integer taskId, StyledDocument document, String text, int offset, BinaryOperator<Integer> getCanceled) {
        this.text = text;
        this.document = document;
        this.offset = offset;
        this.getCanceled = getCanceled;
        this.id = taskId;

        this.colorizerNative = new ColorizerNative((x) -> {
            return getCanceled.apply(offset + x, id);
        });
    }


    public Integer getId() {
        return id;
    }

    public void run() {
        try {
            log.debug("Start calculating colors");
            final int[] colors = colorizerNative.colorize(text);
            log.debug("End calculating colors");
            SwingUtilities.invokeLater(() -> {
                log.debug("Started colorizing");
                int curOffset = offset;
                MutableAttributeSet attributes = new SimpleAttributeSet();
                for (int c : colors) {
                    Color newColor = new Color(c);
                    StyleConstants.FontConstants.setForeground(attributes, Color.blue);
                    document.setCharacterAttributes(curOffset++, 1, attributes, true);
                    log.debug(String.format("Colorized at %d to color %d", curOffset-1));
                }
            });

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }
}
