package be.somedi.eeg;

import be.somedi.eeg.domain.XMLCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.xml.parsers.ParserConfigurationException;

@SpringBootApplication
public class EegApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(EegApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(EegApplication.class, args);

        LOGGER.info("App started!");
        XMLCreator xmlCreator = applicationContext.getBean(XMLCreator.class);
        try {
            xmlCreator.createNewXMLFiles();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        applicationContext.close();
    }

}
