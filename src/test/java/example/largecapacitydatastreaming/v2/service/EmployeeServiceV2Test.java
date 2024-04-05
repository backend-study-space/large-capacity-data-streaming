package example.largecapacitydatastreaming.v2.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class EmployeeServiceV2Test {

    @Autowired
    private EmployeeServiceV2 employeeServiceV2;

    @Test
    void fileWriteTest() {
        employeeServiceV2.findAllEmployees("test2.csv");
    }
}