package be.somedi.eeg;

import be.somedi.eeg.domain.XMLCreator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.xml.parsers.ParserConfigurationException;

@SpringBootApplication
public class EegApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(EegApplication.class, args);

        System.out.println("App started!");
        XMLCreator xmlCreator = applicationContext.getBean(XMLCreator.class);
        try {
            xmlCreator.createNewXMLFiles();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        applicationContext.close();
    }

}
