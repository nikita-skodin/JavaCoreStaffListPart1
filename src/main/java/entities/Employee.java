package entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;

// TODO think about @Data

@ToString
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
}
