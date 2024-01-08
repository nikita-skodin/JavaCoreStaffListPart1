package com.skodin.entities;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OtherEmployee extends Employee{
    String description;

    //dependency on the parent object
    public OtherEmployee (Employee employee, String description){
        super(employee.getId(), employee.getFullName(), employee.getBirthdayDate(), employee.getHiringDate());
        this.description = description;
    }

    public OtherEmployee(UUID id, String fullName, LocalDate birthdayDate, LocalDate hiringDate, String description) {
        super(id, fullName, birthdayDate, hiringDate);
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("""
                %s
                description : %s""",
                super.toString(), getDescription());
    }
}
