package be.somedi.eeg.service;

import be.somedi.eeg.entity.Person;
import be.somedi.eeg.repository.PersonRepository;
import org.springframework.stereotype.Service;

@Service
public class PersonServiceImpl implements PersonService {

    private PersonRepository personRepository;

    @Override
    public Person findByInss(String inss) {
        return personRepository.findByInss(inss);
    }
}
