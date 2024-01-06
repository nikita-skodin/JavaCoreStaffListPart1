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

        UserOperations operation;
        try {
            operation = UserOperations.getValueFromString(input);
        } catch (IllegalArgumentException e) {
            write("данная операция отсутствует");
            return true;
        }

        switch (operation) {
            case LIST_OF_ALL_EMPLOYEES -> applicationService.showListOfAllEmployees();
            case ADD_NEW_EMPLOYEE -> applicationService.processAddNewEmployee();
            case CHANGE_EMPLOYEE_TYPE -> applicationService.processChangeEmployeeType();
            case ASSIGN_EMPLOYEE_TO_MANAGER -> applicationService.processAssignEmployeeToManager();
            case SORT_LIST_BY_FULL_NAMES -> applicationService.processSortListByFullName();
            case SORT_LIST_BY_HIRING_DATES -> applicationService.processSortListByHiringDate();
            case CLOSE_THE_APPLICATION -> {
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