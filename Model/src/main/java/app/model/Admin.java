package app.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("Admin")
public class Admin extends Employee{

    public Admin() {}

    public Admin(Long id, String name, String surname, LocalDate birthdate, String username, String password) {
        super(id, name, surname, birthdate, username, password);
    }
}
