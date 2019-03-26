package org.korizza.colorizer.demo;

import org.korizza.colorizer.ui.ColorizedTextPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UI extends JFrame implements ActionListener {

    // text pane
    private final ColorizedTextPane textPane;

    // scroll pane
    private final JScrollPane scrollPane;


    // frame title
    private static final String FRAME_TITLE = "Colorizer";

    // ctor
    UI() {

        // create text pane
        textPane = new ColorizedTextPane();
        addWindowListener(textPane.getWindowAdapter());
        scrollPane = new JScrollPane(textPane);

        // add components into frame
        this.getContentPane().add(scrollPane);
        setSize(500, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle(FRAME_TITLE);

        Font font = new Font("Serif", Font.PLAIN, 17);
        setFont(font);
    }

    // action handler
    public void actionPerformed(ActionEvent e) {

    }
}
