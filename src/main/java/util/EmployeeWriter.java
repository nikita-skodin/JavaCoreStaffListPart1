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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Properties;

public class EmployeeWriter {

    private final DocumentBuilder builder;

    {
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public void writeXML(Path path, List<Employee> employees) {

        if (!Files.exists(path)){
            throw new NoSuchFileException(path.toString());
        }

        if (employees == null){
            throw new IllegalArgumentException("List cannot be null or empty");
        }

        Document document = builder.newDocument();

        Element root = document.createElement("employees");
        document.appendChild(root);

        write(document, employees, root);

        save(document, path);

    }

    private void write(Document document, List<Employee> employees, Element rootElement) {
        for (Employee employee : employees) {
            if (employee instanceof Manager) {
                writeManager((Manager) employee, document, rootElement);
            } else if (employee instanceof OtherEmployee) {
                writeOtherEmployee((OtherEmployee) employee, document, rootElement);
            } else if (employee != null) {
                writeEmployee(employee, document, rootElement);
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

    private static void save(Document document, Path path) throws IOException, TransformerException {
        document.getDocumentElement().normalize();
        DOMSource dom = new DOMSource(document);
        Transformer transformer = TransformerFactory.newInstance()
                .newTransformer();

        Properties outFormat = new Properties();
        outFormat.setProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperties(outFormat);

        StreamResult result = new StreamResult(Files.newOutputStream(
                path,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE));
        transformer.transform(dom, result);
    }

}
