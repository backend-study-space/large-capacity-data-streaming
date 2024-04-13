package example.largecapacitydatastreaming.v4.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class EmployeeServiceV4Test {

    @Autowired
    private EmployeeServiceV4 employeeServiceV4;

    @Test
    void fileWriteTest2() {
        employeeServiceV4.writeFileByInterface("test5.csv");
    }
}