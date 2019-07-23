package be.somedi.eeg.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "dbo.PersonalInfo_Person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String inss;
    private LocalDate birthDate;

    @Transient
    private String urlToPDF;

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getInss() {
        return inss;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getUrlToPDF() {
        return urlToPDF;
    }

    public void setUrlToPDF(String urlToPDF) {
        this.urlToPDF = urlToPDF;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return Objects.equals(inss, person.inss);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inss);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", inss='" + inss + '\'' +
                ", birthDate=" + birthDate +
                ", urlToPDF='" + urlToPDF + '\'' +
                '}';
    }
}
