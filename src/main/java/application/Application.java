package application;

import application.enums.UserOperations;
import lombok.extern.log4j.Log4j2;
import services.ApplicationService;

import java.util.Scanner;

@Log4j2
public class Application {

    private final Scanner scanner = new Scanner(System.in);
    private final ApplicationService applicationService = new ApplicationService();

    public void start() {
        String input;
        boolean isWorking = true;

        while (isWorking) {
            write(getMainMenuText());

            input = scan();

            isWorking = doSomething(input);
        }
    }

    private String getMainMenuText() {
        return """

                Main Menu

                1 - List of all employees
                2 - Add new employee
                3 - Change the type of employee
                4 - Assign an employee to a manager
                5 - Sort the list by full name
                6 - Sort the list by employment dates
                7 - Delete an employee by id
                8 - Close the application
                """;
    }

    private boolean doSomething(String input) {

        UserOperations operation;
        try {
            operation = UserOperations.getValueFromString(input);
        } catch (IllegalArgumentException e) {
            write("No such operation");
            return true;
        }

        switch (operation) {
            case LIST_OF_ALL_EMPLOYEES -> applicationService.showListOfAllEmployees();
            case ADD_NEW_EMPLOYEE -> applicationService.processAddNewEmployee();
            case CHANGE_EMPLOYEE_TYPE -> applicationService.processChangeEmployeeType();
            case ASSIGN_EMPLOYEE_TO_MANAGER -> applicationService.processAssignEmployeeToManager();
            case SORT_LIST_BY_FULL_NAMES -> applicationService.processSortListByFullName();
            case SORT_LIST_BY_HIRING_DATES -> applicationService.processSortListByHiringDate();
            case REMOVE_EMPLOYEE_BY_ID -> applicationService.processRemoveEmployeeById();
            case CLOSE_THE_APPLICATION -> {
                write("Exit");
                return false;
            }
        }

        return true;
    }

    private void write(Object o) {
        System.out.println(o);
    }

    private String scan() {
        return scanner.nextLine();
    }
}