package util;

import entities.Employee;
import entities.Manager;
import entities.OtherEmployee;
import org.junit.jupiter.api.Test;

import javax.xml.parsers.ParserConfigurationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class EmployeeWriterTest {

    private final EmployeeWriter employeeWriter = new EmployeeWriter();

    EmployeeWriterTest() throws ParserConfigurationException {
    }

    @Test
    void write() {

        UUID id = UUID.randomUUID();

        Employee employee = new Employee(id, "Skodin Nikita Dm",
                LocalDate.now(), LocalDate.now());

        OtherEmployee otherEmployee = new OtherEmployee(id, "Skodin Nikita Dm",
                LocalDate.now(), LocalDate.now(), "de");

        Manager manager = new Manager(id, "Skodin Nikita Dm",
                LocalDate.now(), LocalDate.now(), new ArrayList<>(List.of(employee, otherEmployee)));


        employeeWriter.writeXML(employee, otherEmployee, manager);

    }
}