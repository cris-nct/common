package properties;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import oshi.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

@Slf4j
public class UpdateProperties {

    public static void updateApplicationProperties(String propertiesFile) {
        try {
            File applicationPropFile = new File(propertiesFile);
            if (!applicationPropFile.exists()) {
                log.warn(propertiesFile + " is missing");
                return;
            }

            log.info("Updating " + propertiesFile);
            final PropertyFile currentFileWorkDir = new PropertyFile();
            currentFileWorkDir.load(new FileInputStream(applicationPropFile));

            final PropertyFile sourceCodeProperties = new PropertyFile();
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            sourceCodeProperties.load(contextClassLoader.getResourceAsStream(propertiesFile));

            //Add missing properties
            for (String propName : currentFileWorkDir.getKeys()) {
                String propValue = currentFileWorkDir.getProperty(propName);
                if (StringUtils.isBlank(sourceCodeProperties.getProperty(propName))) {
                    sourceCodeProperties.addProperty(propName, propValue);
                }
            }

            //Add to sourceCodeProperties the values which are different in currentFileWorkDir
            for (String propName : sourceCodeProperties.getKeys()) {
                String sourceCodeValue = sourceCodeProperties.getProperty(propName);
                String propFileValue = currentFileWorkDir.getProperty(propName);
                if (!StringUtils.equals(sourceCodeValue, propFileValue) && StringUtils.isNotBlank(propFileValue)) {
                    sourceCodeProperties.setProperty(propName, propFileValue);
                }
            }

            sourceCodeProperties.save(new FileWriter(propertiesFile));
            log.info("Updated successfully");
        } catch (Exception e) {
            log.error("Can not update " + propertiesFile + " file", e);
        } finally {
            Util.sleep(2000);
        }
    }

}
