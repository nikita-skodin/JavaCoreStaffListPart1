package entities;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

// TODO think about @Data
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Employee {
    UUID id;
    String fullName;
    LocalDate birthdayDate;
    LocalDate hiringDate;
}
