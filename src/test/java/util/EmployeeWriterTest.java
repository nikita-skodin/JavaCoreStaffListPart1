package util;

import entities.Employee;
import entities.Manager;
import entities.OtherEmployee;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeeWriterTest {

    private static final String TEMP_FILE_PATH = "src/test/resources/xml/temp.xml";
    private static final String VALID_FILE_PATH = "src/test/resources/xml/validXML.xml";
    private final EmployeeWriter employeeWriter = new EmployeeWriter();


    @BeforeEach
    void setUp() throws IOException {
        Files.createFile(Paths.get(TEMP_FILE_PATH));
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(TEMP_FILE_PATH));
    }

    @Test
    @SneakyThrows
    void writeXML_writeValidFile() {
        UUID id = UUID.fromString("c3be8edf-3ca8-44f5-a25c-57389066c455");

        Employee employee = new Employee(id, "Skodin Nikita Dm",
                LocalDate.now(), LocalDate.now());

        OtherEmployee otherEmployee = new OtherEmployee(id, "Skodin Nikita Dm",
                LocalDate.now(), LocalDate.now(), "de");

        Manager manager1 = new Manager(id, "Skodin Nikita Dm",
                LocalDate.now(), LocalDate.now(), new ArrayList<>(List.of(employee, otherEmployee)));

        Manager manager2 = new Manager(id, "Skodin Nikita Dm",
                LocalDate.now(), LocalDate.now(), new ArrayList<>(List.of(employee, otherEmployee, manager1)));

        Manager manager3 = new Manager(id, "Skodin Nikita Dm",
                LocalDate.now(), LocalDate.now(), new ArrayList<>(List.of(employee, otherEmployee, manager2)));


        employeeWriter.writeXML(TEMP_FILE_PATH, employee, otherEmployee, manager1, manager2, manager3);

        String expected = Files.readString(Path.of(VALID_FILE_PATH));
        String result = Files.readString(Path.of(TEMP_FILE_PATH));

        assertEquals(expected, result);
    }
}