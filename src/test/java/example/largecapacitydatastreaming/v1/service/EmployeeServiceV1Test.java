package example.largecapacitydatastreaming.v1.service;

import example.largecapacitydatastreaming.Employee;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class EmployeeServiceV1Test {

    @Autowired
    private EmployeeServiceV1 employeeServiceV1;

    @Test
    void selectAllTest() {
        List<Employee> allEmployees = employeeServiceV1.findAllEmployees();

        Assertions.assertThat(allEmployees.size()).isGreaterThan(0);
    }

    @Test
    void fileWriteTest() {
        employeeServiceV1.writeFile("test.csv");
    }
}