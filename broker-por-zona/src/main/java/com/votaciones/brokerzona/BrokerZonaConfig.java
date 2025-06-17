package com.votaciones.brokerzona;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class BrokerZonaConfig {
    private Properties properties = new Properties();

    public BrokerZonaConfig(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            properties.load(fis);
        }
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
} 