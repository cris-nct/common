package properties;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;

@Slf4j
public class UpdateProperties {

    public static void updateApplicationProperties(InputStream source, String filename) {
        try {
            log.info("Updating " + filename);
            final PropertyFile currentFileWorkDir = new PropertyFile();
            currentFileWorkDir.load(source);

            final PropertyFile sourceCodeProperties = new PropertyFile();
            sourceCodeProperties.load(new FileInputStream(filename));

            boolean change = false;
            //Add missing properties
            for (String propName : currentFileWorkDir.getKeys()) {
                String propValue = currentFileWorkDir.getProperty(propName);
                if (StringUtils.isBlank(sourceCodeProperties.getProperty(propName))) {
                    log.debug("Adding missing property: " + propName);
                    sourceCodeProperties.addProperty(propName, propValue);
                    change = true;
                }
            }

            //Add to sourceCodeProperties the values which are different in currentFileWorkDir
            if (change) {
                for (String propName : sourceCodeProperties.getKeys()) {
                    String sourceCodeValue = sourceCodeProperties.getProperty(propName);
                    String propFileValue = currentFileWorkDir.getProperty(propName);
                    if (!StringUtils.equals(sourceCodeValue, propFileValue)
                            && StringUtils.isNotBlank(propFileValue)) {
                        sourceCodeProperties.setProperty(propName, propFileValue);
                    }
                }
                sourceCodeProperties.save(new FileWriter(filename));
                log.info("Updated successfully");
            } else {
                log.info("no changes to " + filename);
            }
        } catch (Exception e) {
            log.error("Can not update " + filename + " file", e);
        }
    }

}
