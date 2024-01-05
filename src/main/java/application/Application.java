package application;

import lombok.extern.log4j.Log4j2;

import java.util.Scanner;

@Log4j2
public class Application {

    private final Scanner scanner = new Scanner(System.in);

    public void start(){

        String input = "";

        while (!input.equals("7")){
            write(getMainMenuText());

            input = scan();

            doSomething(input);
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

    private void write(Object o){
        System.out.println(o);
    }

    private String scan(){
        return scanner.nextLine();
    }

    private void doSomething(String input) {
        write("resp - " + input);
    }



}
