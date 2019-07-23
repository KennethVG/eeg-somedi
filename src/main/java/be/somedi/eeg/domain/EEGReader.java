package be.somedi.eeg.domain;

import be.somedi.eeg.entity.Person;
import be.somedi.eeg.service.PersonService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EEGReader {

    @Value("${path-read}")
    private Path pathToRead;
    @Value("${path-error}")
    private Path pathToError;
    @Value("${startname-file}")
    private String startNameOfFile;

    private static final int SIZE_INSS = 11;

    private static final Logger LOGGER = LoggerFactory.getLogger(EEGReader.class);

    private final PersonService personService;

    @Autowired
    public EEGReader(PersonService personService) {
        this.personService = personService;
    }

    public List<Person> getPatients(){
        List<Person> patients = null;
        try {
            LOGGER.info("Path to read: " + pathToRead);
            patients = Files.list(pathToRead)
                    .filter(Files::isRegularFile)
                    .filter(path -> StringUtils.endsWith(path.toString(), ".pdf"))
                    .map(path -> {
                        Person patient = null;
                        try {
                            patient = personService.findByInss(StringUtils.left(path.getFileName().toString(), SIZE_INSS));
                            if(patient == null){
                                Files.copy(path, Paths.get(pathToError +"\\" + path.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                            } else {
                                Path toPath = Paths.get(pathToRead + backUpPdf(path));
                                if(Files.notExists(toPath)){
                                    Files.createDirectories(toPath);
                                }
                                Files.copy(path, toPath , StandardCopyOption.REPLACE_EXISTING);
                                String urlToPDF = startNameOfFile + backUpPdf(path);
                                patient.setUrlToPDF(urlToPDF);
                            }
                            Files.delete(path);

                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        return patient;
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return patients;
    }

    private String backUpPdf(Path path){
        LocalDate now = LocalDate.now();
        return "/" + now.getYear()+ "/" + now.getYear() + "-"
                + now.getMonthValue() + "-" + now.getDayOfMonth() + "/" + path.getFileName().toString().replaceAll(" ", "_");
    }
}


