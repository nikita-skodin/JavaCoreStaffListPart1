package com.skodin.util;

import com.skodin.entities.Employee;
import com.skodin.exceptions.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import com.skodin.MainXMLTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeReaderTest extends MainXMLTest {

    private final EmployeeReader employeeReader = new EmployeeReader();

    @Test
    void readXML_validSourceFile_returnsListOfEmployees() {
        List<Employee> list = employeeReader.readXML(BIG_LIST_OF_EMPLOYEES_PATH);

        assertNotNull(list);
        assertEquals(getEmployees().size(), list.size());
        assertEquals(getEmployees(), list);
    }

    @Test
    void readXML_nonExistentFile_throwsFileNotFoundException() {
        Path nonExistentPath = Path.of("non/existent/path");

        assertThrows(FileNotFoundException.class,
                () -> employeeReader.readXML(nonExistentPath));
    }

    @Test
    void readXML_EmptyFile_throwsFileIsEmptyException() {
        assertThrows(FileIsEmptyException.class,
                () -> employeeReader.readXML(TEMP_FILE_PATH));
    }

    @Test
    @SneakyThrows
    void readXML_damagedFile_throwsDamagedFileException() {
        Files.deleteIfExists(TEMP_FILE_PATH);
        Files.copy(EMPLOYEES_PATH, TEMP_FILE_PATH);

        Files.writeString(TEMP_FILE_PATH, "invalid text", StandardOpenOption.APPEND);

        assertThrows(DamagedFileException.class,
                () -> employeeReader.readXML(TEMP_FILE_PATH));
    }

    @Test
    @SneakyThrows
    void readXML_incorrectContentInFile_throwsIncorrectContentException() {
        String text = """
                <?xml version="1.0" encoding="UTF-8" standalone="no"?>
                <employees>
                    <employee id="c3be8edf-3ca8-44f5-a25c-57389066c455">
                        <name>Skodin Nikita Dm</name>
                        <birthdayDate>2024-01-02</birthdayDate>
                        <hiringDate>2024-01-02</hiringDate>
                    </employee>
                </employees>
                """;
        Files.writeString(TEMP_FILE_PATH, text);

        assertThrows(IncorrectContentException.class,
                () -> employeeReader.readXML(TEMP_FILE_PATH));
    }

    @Test
    @SneakyThrows
    void readXML_nullFile_throwsPathIsNullException() {
        assertThrows(PathIsNullException.class,
                () -> employeeReader.readXML(null));
    }

    @Test
    void pathValidate_NonExistentFile_throwsFileNotFoundException() {
        Path nonExistentFile = Path.of("non/existent/path");
        assertThrows(FileNotFoundException.class,
                () -> employeeReader.pathValidate(nonExistentFile));
    }

    @Test
    void pathValidate_EmptyFile_throwsFileIsEmptyException() {
        assertThrows(FileIsEmptyException.class,
                () -> employeeReader.pathValidate(TEMP_FILE_PATH));
    }

    @Test
    @SneakyThrows
    void pathValidate_DamagedFile_throwsDamagedFileException() {
        Files.copy(EMPLOYEES_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);

        Files.writeString(TEMP_FILE_PATH, "invalid text", StandardOpenOption.APPEND);

        assertThrows(DamagedFileException.class,
                () -> employeeReader.pathValidate(TEMP_FILE_PATH));
    }

    @Test
    @SneakyThrows
    void pathValidate_nullPath_throwsPathIsNullException() {
        assertThrows(PathIsNullException.class,
                () -> employeeReader.pathValidate(null));
    }
}