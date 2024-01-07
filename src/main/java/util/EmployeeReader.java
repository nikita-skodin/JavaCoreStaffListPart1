package util;

import entities.Employee;
import entities.Manager;
import entities.OtherEmployee;
import exceptions.*;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
public class EmployeeReader {

    private final DocumentBuilder builder;

    {
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @throws PathIsNullException       if path is null
     * @throws FileNotFoundException     if file not found
     * @throws FileIsEmptyException      if file is empty
     * @throws DamagedFileException      if file is not .xml or damaged
     * @throws IncorrectContentException if file has incorrect tag
     */
    @SneakyThrows
    public List<Employee> readXML(Path path) {

        pathValidate(path);

        Document document = checkIsFileDamaged(path);

        List<Employee> list = new ArrayList<>();

        document.getDocumentElement().normalize();

        Element rootElement = document.getDocumentElement();

        try {
            read(rootElement, list);
        } catch (NullPointerException e) {
            throw new IncorrectContentException("File %s contains incorrect data".formatted(path));
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
        builder.setErrorHandler(new CustomErrorHandler());

        try {
            document = builder.parse(path.toFile());
        } catch (SAXException | IOException e) {
            throw new DamagedFileException("File %s is damaged".formatted(path));
        }
        return document;
    }

    // public for tests

    /**
     * @throws PathIsNullException   if path is null
     * @throws FileNotFoundException if file not found
     * @throws FileIsEmptyException  if file is empty
     * @throws DamagedFileException  if file is not .xml or damaged
     */
    @SneakyThrows
    public void pathValidate(Path path) {

        if (path == null) {
            throw new PathIsNullException("Path cannot be null");
        }

        if (!Files.exists(path)) {
            throw new FileNotFoundException("File %s does not exist".formatted(path));
        }

        if (Files.size(path) == 0L) {
            throw new FileIsEmptyException("File %s is empty".formatted(path));
        }

        checkIsFileDamaged(path);

    }

    private static class CustomErrorHandler implements ErrorHandler {
        @Override
        public void warning(SAXParseException e) {
            log.debug(e);
        }

        @Override
        public void error(SAXParseException e) {
            log.debug(e);
        }

        @Override
        public void fatalError(SAXParseException e) {
            log.debug(e);
        }
    }
}
