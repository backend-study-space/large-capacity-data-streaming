package example.largecapacitydatastreaming.v4.service;

import example.largecapacitydatastreaming.Employee;
import example.largecapacitydatastreaming.EmployeeDto;
import example.largecapacitydatastreaming.support.FileWriteService;
import example.largecapacitydatastreaming.support.aop.pointcut.TimeTracer;
import example.largecapacitydatastreaming.v4.repository.EmployeeRepositoryV4;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@TimeTracer
public class EmployeeServiceV4 {

    private final EmployeeRepositoryV4 employeeRepositoryV4;

    private final FileWriteService<EmployeeDto> fileWriteService;

    public EmployeeServiceV4(EmployeeRepositoryV4 employeeRepositoryV4, FileWriteService<EmployeeDto> fileWriteService) {
        this.employeeRepositoryV4 = employeeRepositoryV4;
        this.fileWriteService = fileWriteService;
    }

    public void findAllEmployees(String filePath) {
        fileWriteService.writeHeader(EmployeeDto.class, filePath);

        List<EmployeeDto> list = new ArrayList<>();

        long rowCount = employeeRepositoryV4.resultSetStream(
                        100000,
                        (rs, rowNum) -> new Employee(
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("email"),
                                rs.getString("department"),
                                rs.getDouble("salary"),
                                rs.getDate("hire_date")
                        ), () -> {
                            fileWriteService.writeBody(EmployeeDto.class, list, filePath);
                            list.clear();
                        }
                )
                .map(EmployeeDto::create)
                .peek(list::add).count();
    }
}
