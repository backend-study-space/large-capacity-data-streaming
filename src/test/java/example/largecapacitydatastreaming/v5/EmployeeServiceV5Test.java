package example.largecapacitydatastreaming.v5;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmployeeServiceV5Test {

    @Autowired
    private EmployeeServiceV5 employeeServiceV5;

    @Test
    void fileWriteTestByNonVirtualThreads() {
        employeeServiceV5.writeFileByNonVirtualThreads("test6.csv");
    }

    @Test
    void fileWriteTest() {
        employeeServiceV5.writeFileByVirtualThreads("test7.csv");
    }
}