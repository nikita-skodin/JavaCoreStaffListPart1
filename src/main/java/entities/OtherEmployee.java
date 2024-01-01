package entities;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OtherEmployee extends Employee{
    String description;

    public OtherEmployee(UUID id, String fullName, LocalDate birthdayDate, LocalDate hiringDate, String description) {
        super(id, fullName, birthdayDate, hiringDate);
        this.description = description;
    }
}
