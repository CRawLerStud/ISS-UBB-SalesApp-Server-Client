package app.model;

import java.time.LocalDate;

public class Admin extends Employee{

    public Admin(Long id, String name, String surname, LocalDate birthdate, String username, String password) {
        super(id, name, surname, birthdate, username, password);
    }
}
