## Installation instructions
    - Clone repository
    - mvn install
    - Go to phpMyAdmin run java_winter.sql
    - Run the program
  
## Project dependencies:
    - JCalendar library version 1.4, from the group ID "com.toedter" and artifact ID "jcalendar".
    - 
    - JDatePicker library version 1.3.4, from the group ID "org.jdatepicker" and artifact ID "jdatepicker".
    - 
    - MySQL Connector/J library version 8.0.32, from the group ID "mysql" and artifact ID "mysql-connector-java".

## Build configuration:
    The Maven Compiler Plugin version 3.10.0 is used to compile the Java source code.
    The Exec Maven Plugin version 3.0.0 is used to execute a Java class as part of the build process.
    The main class to be executed is specified as "cz.cuni.mff.java.zapocet.Main".