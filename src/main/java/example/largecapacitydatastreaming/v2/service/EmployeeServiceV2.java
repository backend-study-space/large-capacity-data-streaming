package example.largecapacitydatastreaming.v2.service;

import example.largecapacitydatastreaming.Employee;
import example.largecapacitydatastreaming.EmployeeDto;
import example.largecapacitydatastreaming.support.FileWriteService;
import example.largecapacitydatastreaming.support.aop.pointcut.TimeTracer;
import example.largecapacitydatastreaming.v2.repository.EmployeeRepositoryV2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@TimeTracer
public class EmployeeServiceV2 {

    private final EmployeeRepositoryV2 employeeRepositoryV2;

    private final FileWriteService<EmployeeDto> fileWriteService;

    public EmployeeServiceV2(EmployeeRepositoryV2 employeeRepositoryV2, FileWriteService<EmployeeDto> fileWriteService) {
        this.employeeRepositoryV2 = employeeRepositoryV2;
        this.fileWriteService = fileWriteService;
    }

    public void findAllEmployees(String filePath) {
        fileWriteService.writeHeader(EmployeeDto.class, filePath);

        int pageSize = 100_000;
        int totalRecords = employeeRepositoryV2.getCount();

        for (int start = 0; start < totalRecords; start += pageSize) {
            int end = Math.min(start + pageSize, totalRecords);

            List<Employee> employees = employeeRepositoryV2.findAll((rs, rowNum) -> new Employee(
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("department"),
                    rs.getDouble("salary"),
                    rs.getDate("hire_date")
            ), start, end);

            List<EmployeeDto> employeesDto = new ArrayList<>();

            for (Employee employee : employees) {
                employeesDto.add(EmployeeDto.create(employee));
            }

            fileWriteService.writeBody(EmployeeDto.class, employeesDto, filePath);
        }
    }
}
