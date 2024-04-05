package example.largecapacitydatastreaming.v1.service;

import example.largecapacitydatastreaming.Employee;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @Test
    void selectAllTest() {
        String SQL = "SELECT * FROM employee";

        List<Employee> allEmployees = employeeService.findAllEmployees(SQL);

        System.out.println(allEmployees.size());

        Assertions.assertThat(allEmployees.size()).isGreaterThan(0);
    }
}