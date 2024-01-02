package util;

import entities.Employee;
import entities.Manager;
import entities.OtherEmployee;
import exceptions.DamagedFileException;
import exceptions.InvalidTagNameException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmployeeReaderTest {

    private static final String VALID_FILE_PATH = "src/test/resources/xml/validXML.xml";
    private static final String INVALID_FILE_PATH = "src/test/resources/xml/invalidXML.xml";
    private static final String INVALID_TAG_FILE_PATH = "src/test/resources/xml/invalidTagName.xml";

    private final EmployeeReader employeeReader = new EmployeeReader();

    @Test
    void readXML_readXMLFromValidFile_ReturnsListOfEmployees() {

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

        List<Employee> expectedList = List.of(employee, otherEmployee, manager1, manager2, manager3);

        List<Employee> resultList = employeeReader.readXML(VALID_FILE_PATH);

        assertEquals(expectedList.size(), resultList.size());
        for (Employee employee1 : resultList) {
            System.out.println(employee1);
        }
        assertEquals(expectedList, resultList);
    }

    @Test
    void readXML_readXMLFromInvalidFile_ThrowsDamagedFileException() {
        assertThrows(DamagedFileException.class, () -> employeeReader.readXML(INVALID_FILE_PATH));
    }

    @Test
    void readXML_readXMLFromInvalidTagFile_ThrowsInvalidTagNameException() {
        assertThrows(InvalidTagNameException.class, () -> employeeReader.readXML(INVALID_TAG_FILE_PATH));
    }

}