package com.sistemaelectoral.reliablemsg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private final String component;
    private final String logFile;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Logger(String component) {
        this.component = component;
        this.logFile = "logs/" + component + ".log";
        createLogDirectory();
    }

    private void createLogDirectory() {
        File directory = new File("logs");
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public synchronized void log(String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
            String timestamp = LocalDateTime.now().format(formatter);
            String logMessage = String.format("[%s] [%s] %s", timestamp, component, message);
            writer.println(logMessage);
            System.out.println(logMessage); // También mostrar en consola
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }
} 