package properties;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.FileWriter;

@Slf4j
public class UpdateProperties {

    public static void updateApplicationProperties(String filename) {
        try {
            log.info("Updating " + filename);
            final PropertyFile sourcecodeFile = new PropertyFile();
            sourcecodeFile.load(new ClassPathResource(filename).getInputStream());

            final PropertyFile propertiesFile = new PropertyFile();
            propertiesFile.load(new FileInputStream(filename));

            boolean change = false;
            //Add missing properties
            for (String propName : sourcecodeFile.getKeys()) {
                String propValue = sourcecodeFile.getProperty(propName);
                if (StringUtils.isBlank(propertiesFile.getProperty(propName))) {
                    log.debug("Adding missing property: " + propName);
                    propertiesFile.addProperty(propName, propValue);
                    change = true;
                }
            }
            if (change) {
                propertiesFile.save(new FileWriter(filename));
                log.info("Updated successfully");
            } else {
                log.info("no changes to " + filename);
            }
        } catch (Exception e) {
            log.error("Can not update " + filename + " file", e);
        }
    }

}
