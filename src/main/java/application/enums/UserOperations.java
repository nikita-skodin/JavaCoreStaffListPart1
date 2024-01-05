package application.enums;

import lombok.Getter;

@Getter
public enum UserOperations {

    LIST_OF_ALL_EMPLOYEES("1"),
    ADD_NEW_EMPLOYEE("2"),
    CHANGE_EMPLOYEE_TYPE("3"),
    ASSIGN_EMPLOYEE_TO_MANAGER("4"),
    SORT_LIST_BY_FULL_NAMES("5"),
    SORT_LIST_BY_HIRING_DATES("6"),
    CLOSE_THE_APPLICATION("7");

    private final String number;

    UserOperations(String text) {
        this.number = text;
    }

    @Override
    public String toString() {
        return this.number;
    }

    public boolean equals(String number){
        return this.number.equals(number);
    }
}
