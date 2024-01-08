package com.skodin.entities;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Manager extends Employee {
    List<Employee> subordinates;

    //dependency on the parent object
    public Manager (Employee employee, List<Employee> subordinates){
        super(employee.getId(), employee.getFullName(), employee.getBirthdayDate(), employee.getHiringDate());
        this.subordinates = subordinates;
    }

    public Manager(UUID id, String fullName, LocalDate birthdayDate, LocalDate hiringDate, List<Employee> subordinates) {
        super(id, fullName, birthdayDate, hiringDate);
        this.subordinates = subordinates;
    }

    public List<Employee> getSubordinates() {
        return subordinates == null ? new ArrayList<>() : subordinates;
    }

    @Override
    public String toString() {
        return String.format("""
                %s
                subordinates :%s""",
                super.toString(), getPrettySubordinates());
    }

    private String getPrettySubordinates(){

        int counter = 1;

        StringBuilder result = new StringBuilder();

        for(Employee employee : getSubordinates()){
            result.append("\n\n").append(counter).append(": ").append(employee.toString());
            counter++;
        }

        return result.toString().replaceAll("\n", "\n\t");
    }
}
