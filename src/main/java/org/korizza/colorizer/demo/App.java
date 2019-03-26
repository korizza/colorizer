package org.korizza.colorizer.demo;

import javax.swing.*;

public class App {
    private final UI ui;

    public App() {
        ui = new UI();
        ui.setVisible(true);
    }

    public static void main(String[] args) throws InterruptedException {
        final long start = System.nanoTime();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                System.out.format("HOOKED");
            }
        }));

        SwingUtilities.invokeLater(() -> {
            App app = new App();
        });
    }
}
