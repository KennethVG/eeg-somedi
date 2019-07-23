package be.somedi.eeg.repository;

import be.somedi.eeg.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Person findByInss(String inss);

}
