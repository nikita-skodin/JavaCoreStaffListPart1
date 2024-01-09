# StaffList
The console application, written according to the Technical Specification (below),
is designed to account for employees using an XML file as storage.

## Overview
- [Stack](#Stack)
- [Technical Specification](#Technical-Specification)
- [Requirements](#Requirements)
- [Getting Started](#Getting-Started)
- [Testing](#Testing)

## Stack
- Java core
- XML (DOM)
- Maven
- JUnit5

## Technical Specification
The Technical Specification were taken from open sources, link below
https://docs.google.com/document/d/16h9Qw2vkAlhw-T1qPQ6c2quRWpbbf-LIKlvOxzHZ6Wo/pub

## Requirements
Java 17+ is required to run the application

## Getting Started
The fat-jar file with the build is located right in the root of the project,
run it from there with the following command:
```sh
java -jar StaffList-1.jar
```

## Testing
Service class test coverage is about 100 percent\n
file in src/test/resources/xml/ read only\n
JUnit5 was used for testing\n
