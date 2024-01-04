package tests;

import entities.Employee;
import entities.Manager;
import entities.OtherEmployee;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class MainXMLTest {
    protected static final String ROOT = "src/test/resources/xml";
    protected static final Path TEMP_FILE_PATH = Path.of(ROOT + "/temp.xml");
    protected static final Path EMPLOYEE_PATH = Path.of(ROOT + "/employee.xml");
    protected static final Path EMPLOYEES_PATH = Path.of(ROOT + "/employees.xml");
    protected static final Path BIG_LIST_OF_EMPLOYEES_PATH = Path.of(ROOT + "/bigListOfEmployees.xml");

    protected static final UUID id = UUID.fromString("c3be8edf-3ca8-44f5-a25c-57389066c455");
    protected static final LocalDate date = LocalDate.parse("2024-01-02");
    protected static final Employee employee = new Employee(id, "Skodin Nikita Dm", date, date);

    @BeforeEach
    void setUp() throws IOException {
        Files.createFile(TEMP_FILE_PATH);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(TEMP_FILE_PATH);
    }

    /**
     * @returns a list of objects represented in a file named bigListOfEmployees.xml
     */
    protected static ArrayList<Employee> getEmployees() {
        Employee employee = new Employee(id, "Skodin Nikita Dm",
                date, date);

        OtherEmployee otherEmployee = new OtherEmployee(id, "Skodin Nikita Dm",
                date, date, "de");

        Manager manager1 = new Manager(id, "Skodin Nikita Dm",
                date, date, new ArrayList<>(List.of(employee, otherEmployee)));

        Manager manager2 = new Manager(id, "Skodin Nikita Dm",
                date, date, new ArrayList<>(List.of(employee, otherEmployee, manager1)));

        Manager manager3 = new Manager(id, "Skodin Nikita Dm",
                date, date, new ArrayList<>(List.of(employee, otherEmployee, manager2)));

        return new ArrayList<>(List.of(employee, otherEmployee, manager1, manager2, manager3));
    }

}
