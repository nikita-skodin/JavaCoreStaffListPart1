package com.skodin.util;

import com.skodin.entities.Employee;
import com.skodin.exceptions.FileNotFoundException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import com.skodin.MainXMLTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmployeeWriterTest extends MainXMLTest {
    private final EmployeeWriter employeeWriter = new EmployeeWriter();
    @Test
    void writeXML_nonExistentTargetFile_throwsFileNotFoundException() {
        Path nonExistentPath = Path.of("non/existent/path");

        assertThrows(FileNotFoundException.class,
                () -> employeeWriter.writeXML(nonExistentPath, List.of(EMPLOYEE)));
    }

    @Test
    @SneakyThrows
    void writeXML_validTargetFile_writeWML() {
        List<Employee> employees = getEmployees();

        employeeWriter.writeXML(TEMP_FILE_PATH, employees);

        String expected = Files.readString(BIG_LIST_OF_EMPLOYEES_PATH);
        String actual = Files.readString(TEMP_FILE_PATH);

        assertEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void writeXML_validTargetFileAndNullEmployees_write_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> employeeWriter.writeXML(TEMP_FILE_PATH, null));
    }

    @Test
    @SneakyThrows
    void writeXML_validTargetFileAndEmptyListOfEmployees_writeXML() {
        List<Employee> employees = new ArrayList<>();

        employeeWriter.writeXML(TEMP_FILE_PATH, employees);

        String actual = Files.readString(TEMP_FILE_PATH);

        assertEquals(EMPTY_XML_FILE, actual);
    }

    @Test
    @SneakyThrows
    void writeXML_NotEmptyList_rewritesFile(){

        Files.writeString(TEMP_FILE_PATH, "DAMAGE IT");

        employeeWriter.writeXML(TEMP_FILE_PATH, getEmployees());

        String expected = Files.readString(BIG_LIST_OF_EMPLOYEES_PATH);
        String actual = Files.readString(TEMP_FILE_PATH);

        assertEquals(expected, actual);
    }
}