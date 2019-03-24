package org.korizza.colorizer.demo;

import org.korizza.colorizer.io.StateProcessor;

import javax.swing.*;

public class App {
    private final UI ui;

    public App() {
        ui = new UI();
        ui.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            App app = new App();
        });
    }
}
