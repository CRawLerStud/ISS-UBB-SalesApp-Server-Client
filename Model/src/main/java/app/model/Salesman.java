package app.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("Salesman")
public class Salesman extends Employee{

    public Salesman() {}

    public Salesman(Long id, String name, String surname, LocalDate birthdate, String username, String password) {
        super(id, name, surname, birthdate, username, password);
    }
}
