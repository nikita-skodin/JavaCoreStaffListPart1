package util;

import entities.Employee;
import entities.Manager;
import entities.OtherEmployee;
import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class EmployeeWriter {

    private final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

    public EmployeeWriter() throws ParserConfigurationException {
    }

    @SneakyThrows
    public void writeXML(Employee... employees) {

        Document document = builder.newDocument();
        Element root = document.createElement("employees");
        document.appendChild(root);

        write(document, List.of(employees), root);

        // save
        DOMSource dom = new DOMSource(document);
        Transformer transformer = TransformerFactory.newInstance()
                .newTransformer();
        StreamResult result = new StreamResult(Files.newOutputStream(
                Path.of("src/test/resources/test.xml"),
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE));
        transformer.transform(dom, result);

    }

    private void write(Document document, List<Employee> employees, Element subordinates) {
        for (Employee employee : employees) {
            if (employee instanceof Manager) {
                writeManager((Manager) employee, document, subordinates);
            } else if (employee instanceof OtherEmployee) {
                writeOtherEmployee((OtherEmployee) employee, document, subordinates);
            } else if (employee != null) {
                writeEmployee(employee, document, subordinates);
            }
        }
    }

    private Element writeEmployee(Employee employee, Document document, Element root) {
        Element element = document.createElement(employee.getClass().getSimpleName().toLowerCase());
        element.setAttribute("id", employee.getId().toString());
        root.appendChild(element);

        Element fullName = document.createElement("fullName");
        fullName.appendChild(document.createTextNode(employee.getFullName()));
        element.appendChild(fullName);

        Element birthdayDate = document.createElement("birthdayDate");
        birthdayDate.appendChild(document.createTextNode(employee.getBirthdayDate().toString()));
        element.appendChild(birthdayDate);

        Element hiringDate = document.createElement("hiringDate");
        hiringDate.appendChild(document.createTextNode(employee.getHiringDate().toString()));
        element.appendChild(hiringDate);

        return element;
    }

    private void writeManager(Manager manager, Document document, Element root) {
        Element element = writeEmployee(manager, document, root);

        List<Employee> employees = manager.getSubordinates();
        Element subordinates = document.createElement("subordinates");

        write(document, employees, subordinates);

        element.appendChild(subordinates);
    }

    private void writeOtherEmployee(OtherEmployee otherEmployee, Document document, Element root) {
        Element element = writeEmployee(otherEmployee, document, root);

        Element description = document.createElement("description");
        description.appendChild(document.createTextNode(otherEmployee.getDescription()));
        element.appendChild(description);
    }

}
