package util;

import entities.Employee;
import org.junit.jupiter.api.Test;

import javax.xml.parsers.ParserConfigurationException;
import java.util.List;

class EmployeeReaderTest {
    private final EmployeeReader employeeReader = new EmployeeReader();

    EmployeeReaderTest() throws ParserConfigurationException {
    }

    @Test
    void readXML() {
        List<Employee> list = employeeReader.readXML();
        System.out.println(list);
    }
}