package entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

// TODO think about @Data

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Employee {
    UUID id;
    String fullName;
    LocalDate birthdayDate;
    LocalDate hiringDate;

    @Override
    public String toString() {
        return String.format("""
                %s
                id : %s
                full name : %s
                birthday date : %s
                hiring date : %s""",
                this.getClass().getSimpleName(), id.toString(), fullName, birthdayDate, hiringDate);
    }
}
