package org.korizza.colorizer.ui;

import org.apache.log4j.Logger;
import org.korizza.colorizer.io.StateProcessor;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;

public class ColorizedTextPane extends JTextPane {

    private static final Logger log = Logger.getLogger(ColorizedTextPane.class);

    private final StateProcessor stateProcessor;

    public WindowAdapter getWindowAdapter() {
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stateProcessor.close();
            }
        };
    }

    public ColorizedTextPane() {
        stateProcessor = new StateProcessor(getDocument(), getStyledDocument());
        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                stateProcessor.onInsertUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                stateProcessor.onRemoveUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent e) {
            }
        });

        Font font = new Font("Serif", Font.PLAIN, 17);
        setFont(font);
        setForeground(Color.black);
    }


    @Override
    public synchronized void removeComponentListener(ComponentListener l) {
        super.removeComponentListener(l);

    }
}
