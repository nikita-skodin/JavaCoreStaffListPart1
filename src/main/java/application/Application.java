package application;

import lombok.extern.log4j.Log4j2;
import services.ApplicationService;

import java.util.Scanner;

import static application.enums.UserOperations.*;

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
                                
                Главное меню
                                
                1 - Список всех сотрудников
                2 - Добавить сотрудника
                3 - Изменить тип сотрудника
                4 - Привязать сотрудника к менеджеру
                5 - Сортировать список по фамилиям
                6 - Сортировать список по датам принятия на работу
                7 - Закрыть приложение
                """;
    }

    private boolean doSomething(String input) {

        if (LIST_OF_ALL_EMPLOYEES.equals(input)) {

            applicationService.showListOfAllEmployees();

        } else if (ADD_NEW_EMPLOYEE.equals(input)) {

            applicationService.processAddNewEmployee();

        } else if (CHANGE_EMPLOYEE_TYPE.equals(input)) {

            applicationService.processChangeEmployeeType();

        } else if (ASSIGN_EMPLOYEE_TO_MANAGER.equals(input)) {

            applicationService.processAssignEmployeeToManager();

        } else if (SORT_LIST_BY_FULL_NAMES.equals(input)) {

            applicationService.processSortListByFullName();

        } else if (SORT_LIST_BY_HIRING_DATES.equals(input)) {

            applicationService.processSortListByHiringDate();

        } else if (CLOSE_THE_APPLICATION.equals(input)) {
            return false;
        } else {
            write("Нет такой операции");
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