package util;

import entities.Employee;
import entities.Manager;
import entities.OtherEmployee;
import exceptions.DamagedFileException;
import exceptions.IncorrectContentException;
import lombok.SneakyThrows;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EmployeeReader {

    private final DocumentBuilder builder;

    {
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public List<Employee> readXML(Path path) {

        if (path == null){
            throw new IllegalArgumentException("File cannot be null");        }

        if (!Files.exists(path)){
            throw new NoSuchFileException(path.toString());
        }

        if (Files.size(path) == 0L){
            throw new IllegalArgumentException("File cannot be empty");
        }

        List<Employee> list = new ArrayList<>();

        Document document = checkIsFileDamaged(path);

        document.getDocumentElement().normalize();

        Element rootElement = document.getDocumentElement();

        try {
            read(rootElement, list);
        } catch (NullPointerException e) {
            throw new IncorrectContentException(e.getMessage());
        }

        return list;
    }

    private void read(Element rootElement, List<Employee> list) {
        NodeList nodeList = rootElement.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node item = nodeList.item(i);
            if (item instanceof Element childElement) {
                switch (childElement.getTagName()) {
                    case "employee" -> list.add(readEmployee(childElement));
                    case "otheremployee" -> list.add(readOtheremployee(childElement));
                    case "manager" -> list.add(readManager(childElement));
                }
            }
        }
    }

    private Employee readEmployee(Element element) {
        return new Employee(
                UUID.fromString(element.getAttribute("id")),
                element.getElementsByTagName("fullName").item(0).getTextContent(),
                LocalDate.parse(element.getElementsByTagName("birthdayDate").item(0).getTextContent()),
                LocalDate.parse(element.getElementsByTagName("hiringDate").item(0).getTextContent()));
    }

    private OtherEmployee readOtheremployee(Element element) {
        return new OtherEmployee(readEmployee(element),
                element.getElementsByTagName("description").item(0).getTextContent());
    }

    private Manager readManager(Element element) {

        ArrayList<Employee> subordinatesList = new ArrayList<>();
        NodeList subordinatesNodeList = element.getChildNodes();

        for (int i = 0; i < subordinatesNodeList.getLength(); i++) {
            Node subordinatesItem = subordinatesNodeList.item(i);
            if (subordinatesItem instanceof Element subordinatesElement
                    && subordinatesElement.getTagName().equals("subordinates")) {
                read(subordinatesElement, subordinatesList);
            }
        }

        return new Manager(readEmployee(element), subordinatesList);
    }

    public Document checkIsFileDamaged(Path path) {
        Document document;

        try {
            document = builder.parse(path.toFile());
        } catch (SAXException | IOException e) {
            throw new DamagedFileException(e.getMessage());
        }
        return document;
    }
}
