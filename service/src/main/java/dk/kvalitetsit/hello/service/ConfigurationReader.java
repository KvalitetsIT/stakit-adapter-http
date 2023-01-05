package dk.kvalitetsit.hello.service;

import dk.kvalitetsit.hello.service.model.ConfigurationModel;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ConfigurationReader {

    public ConfigurationModel readConfiguration(String filePath) throws FileNotFoundException {
        Yaml yaml = new Yaml( new Constructor(ConfigurationModel.class));

        InputStream inputStream = new FileInputStream(filePath);
        ConfigurationModel configurationModel = yaml.load(inputStream);
        return configurationModel;
    }
}
