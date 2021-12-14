package ru.job4j.grabber.storage.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configurator {

    private final Properties properties = new Properties();
    private final String propertiesName;

    public Configurator(String propertiesName) {
        this.propertiesName = propertiesName;
        create();
    }

    private void create() {
        try (InputStream in = Configurator.class.getClassLoader().getResourceAsStream(propertiesName)) {
            if (in != null) {
                properties.load(in);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Properties getProperties() {
        return properties;
    }
}
