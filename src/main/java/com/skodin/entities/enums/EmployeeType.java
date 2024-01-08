package com.skodin.entities.enums;

public enum EmployeeType {

    MANAGER,
    EMPLOYEE,
    OTHER_EMPLOYEE;

    public static String getPretty(){

        StringBuilder stringBuilder = new StringBuilder();

        for (EmployeeType type : values()){
            stringBuilder.append("\t").append(type).append("\n");
        }

        return stringBuilder.toString();
    }

}
