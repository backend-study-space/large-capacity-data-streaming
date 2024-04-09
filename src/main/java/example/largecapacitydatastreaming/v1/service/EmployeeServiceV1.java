package example.largecapacitydatastreaming.v1.service;

import example.largecapacitydatastreaming.Employee;
import example.largecapacitydatastreaming.EmployeeDto;
import example.largecapacitydatastreaming.support.FileWriteService;
import example.largecapacitydatastreaming.support.aop.pointcut.TimeTracer;
import example.largecapacitydatastreaming.v1.repository.EmployeeRepositoryV1;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@TimeTracer
public class EmployeeServiceV1 {

    private final EmployeeRepositoryV1 employeeRepositoryV1;

    private final FileWriteService<EmployeeDto> fileWriteService;

    public EmployeeServiceV1(EmployeeRepositoryV1 employeeRepositoryV1, FileWriteService<EmployeeDto> fileWriteService) {
        this.employeeRepositoryV1 = employeeRepositoryV1;
        this.fileWriteService = fileWriteService;
    }

    public List<EmployeeDto> findAllEmployees() {
        List<Employee> employees = employeeRepositoryV1.findAll((rs, rowNum) -> new Employee(
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("department"),
                rs.getDouble("salary"),
                rs.getDate("hire_date")
        ));

        List<EmployeeDto> employeesDto = new ArrayList<>();

        for (Employee employee : employees) {
            employeesDto.add(EmployeeDto.create(employee));
        }

        return employeesDto;
    }

    public void writeFile(String filePath) {
        List<EmployeeDto> allEmployees = findAllEmployees();

        fileWriteService.writeHeader(EmployeeDto.class, filePath);
        fileWriteService.writeBody(EmployeeDto.class, allEmployees, filePath);
    }
}
