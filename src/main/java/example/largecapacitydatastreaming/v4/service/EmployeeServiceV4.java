package example.largecapacitydatastreaming.v4.service;

import example.largecapacitydatastreaming.Employee;
import example.largecapacitydatastreaming.EmployeeDto;
import example.largecapacitydatastreaming.support.FileWriteService;
import example.largecapacitydatastreaming.support.aop.pointcut.TimeTracer;
import example.largecapacitydatastreaming.v4.repository.EmployeeRepositoryV4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;

@Service
@TimeTracer
public class EmployeeServiceV4 {

    private final EmployeeRepositoryV4 employeeRepositoryV4;

    private final FileWriteService<EmployeeDto> fileWriteService;

    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceV4.class);

    public EmployeeServiceV4(EmployeeRepositoryV4 employeeRepositoryV4, FileWriteService<EmployeeDto> fileWriteService) {
        this.employeeRepositoryV4 = employeeRepositoryV4;
        this.fileWriteService = fileWriteService;
    }

    public void writeFileByPinPoint(String filePath) {
        fileWriteService.writeHeader(EmployeeDto.class, filePath);

        BufferedWriter bufferedWriter = fileWriteService.getBufferedWriter(filePath);

        employeeRepositoryV4.resultSetStream((rs, rowNum) -> new Employee(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("department"),
                        rs.getDouble("salary"),
                        rs.getDate("hire_date")
                ), rowNum -> log.info("현재 {}개 처리되었습니다.", rowNum))
                .map(EmployeeDto::create)
                .forEach(employeeDto -> fileWriteService.writeBody(employeeDto, bufferedWriter));
    }
}
