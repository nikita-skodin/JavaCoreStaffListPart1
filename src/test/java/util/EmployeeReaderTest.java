package util;

import entities.Employee;
import entities.Manager;
import entities.OtherEmployee;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeeReaderTest {

    private static final String VALID_FILE_PATH = "src/test/resources/xml/valid.xml";
    private static final String INVALID_FILE_PATH = "src/test/resources/xml/invalid.xml";

    private final EmployeeReader employeeReader = new EmployeeReader();

    @Test
    void readXML() {

        UUID id = UUID.fromString("c3be8edf-3ca8-44f5-a25c-57389066c455");

        Employee employee = new Employee(id, "Skodin Nikita Dm",
                LocalDate.now(), LocalDate.now());

        OtherEmployee otherEmployee = new OtherEmployee(id, "Skodin Nikita Dm",
                LocalDate.now(), LocalDate.now(), "de");

        Manager manager1 = new Manager(id, "Skodin Nikita Dm",
                LocalDate.now(), LocalDate.now(), new ArrayList<>(List.of(employee, otherEmployee)));

        Manager manager2 = new Manager(id, "Skodin Nikita Dm",
                LocalDate.now(), LocalDate.now(), new ArrayList<>(List.of(employee, otherEmployee, manager1)));

        List<Employee> expectedList = List.of(employee, otherEmployee, manager1, manager2);

        List<Employee> resultList = employeeReader.readXML(VALID_FILE_PATH);

        for (Employee e : expectedList){
            System.out.println(e);
        }

        System.out.println();

        for (Employee r : resultList){
            System.out.println(r);
        }

        assertEquals(expectedList, resultList);
    }
}