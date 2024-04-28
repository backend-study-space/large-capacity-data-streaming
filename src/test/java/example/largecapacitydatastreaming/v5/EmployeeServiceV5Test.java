package example.largecapacitydatastreaming.v5;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EmployeeServiceV5Test {

    @Autowired
    private EmployeeServiceV5 employeeServiceV5;

    @Test
    void fileWriteTestByVirtualThreads() {
        employeeServiceV5.writeFileByVirtualThreads("test6.csv");
    }
}