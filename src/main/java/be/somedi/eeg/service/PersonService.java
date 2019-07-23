package be.somedi.eeg.service;

import be.somedi.eeg.entity.Person;

public interface PersonService {

    Person findByInss(String inss);
}
