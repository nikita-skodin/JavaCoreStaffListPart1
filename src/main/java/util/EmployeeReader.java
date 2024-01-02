package util;

import entities.Employee;
import entities.Manager;
import entities.OtherEmployee;
import exceptions.DamagedFileException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
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

    public List<Employee> readXML(String path) {
        List<Employee> list = new ArrayList<>();

        Document document;

        try {
            document = builder.parse(path);
        } catch (SAXException | IOException e) {
            throw new DamagedFileException(e.getMessage());
        }

        document.getDocumentElement().normalize();

        Element rootElement = document.getDocumentElement();

        read(rootElement, list);

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

        // one tag here
        NodeList subordinatesNodeList = element.getElementsByTagName("subordinates");

        for (int i = 0; i < subordinatesNodeList.getLength(); i++) {
            Node subordinatesItem = subordinatesNodeList.item(i);
            if (subordinatesItem instanceof Element subordinatesElement) {
                read(subordinatesElement, subordinatesList);
            }
        }

        return new Manager(readEmployee(element), subordinatesList);
    }
}
