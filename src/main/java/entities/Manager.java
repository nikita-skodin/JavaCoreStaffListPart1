package entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor()
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Manager extends Employee {
    List<Employee> subordinates;

    // TODO get rid of the dependency on the parent object
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
}
