package entities;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor()
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Manager extends Employee {
    List<Employee> subordinates;

    public Manager(UUID id, String fullName, LocalDate birthdayDate, LocalDate hiringDate, List<Employee> subordinates) {
        super(id, fullName, birthdayDate, hiringDate);
        this.subordinates = subordinates;
    }

    public List<Employee> getSubordinates() {
        return subordinates == null ? new ArrayList<>() : subordinates;
    }
}
