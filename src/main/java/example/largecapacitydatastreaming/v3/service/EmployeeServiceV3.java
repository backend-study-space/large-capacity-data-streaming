package example.largecapacitydatastreaming.v3.service;

import example.largecapacitydatastreaming.Employee;
import example.largecapacitydatastreaming.EmployeeDto;
import example.largecapacitydatastreaming.support.FileWriteService;
import example.largecapacitydatastreaming.support.aop.pointcut.TimeTracer;
import example.largecapacitydatastreaming.v3.repository.EmployeeRepositoryV3;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

@Service
@TimeTracer
public class EmployeeServiceV3 {

    private final EmployeeRepositoryV3 employeeRepositoryV3;

    private final FileWriteService<EmployeeDto> fileWriteService;

    public EmployeeServiceV3(EmployeeRepositoryV3 employeeRepositoryV3, FileWriteService<EmployeeDto> fileWriteService) {
        this.employeeRepositoryV3 = employeeRepositoryV3;
        this.fileWriteService = fileWriteService;
    }

    public void findAllEmployees(String filePath) {
        fileWriteService.writeHeader(EmployeeDto.class, filePath);

        BufferedWriter bufferedWriter = FileWriteService.createWriter(filePath);

        employeeRepositoryV3.resultSetStream((rs, rowNum) -> new Employee(
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("department"),
                        rs.getDouble("salary"),
                        rs.getDate("hire_date")))
                .map(EmployeeDto::create)
                .forEach(employeeDto -> fileWriteService.writeBody(employeeDto, bufferedWriter));
    }
}
