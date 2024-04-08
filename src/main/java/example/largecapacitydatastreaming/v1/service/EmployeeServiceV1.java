package example.largecapacitydatastreaming.v1.service;

import example.largecapacitydatastreaming.Employee;
import example.largecapacitydatastreaming.support.FileWriteService;
import example.largecapacitydatastreaming.support.aop.pointcut.TimeTracer;
import example.largecapacitydatastreaming.v1.repository.EmployeeRepositoryV1;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@TimeTracer
public class EmployeeServiceV1 {

    private final EmployeeRepositoryV1 employeeRepositoryV1;

    private final FileWriteService<Employee> fileWriteService;

    public EmployeeServiceV1(EmployeeRepositoryV1 employeeRepositoryV1, FileWriteService<Employee> fileWriteService) {
        this.employeeRepositoryV1 = employeeRepositoryV1;
        this.fileWriteService = fileWriteService;
    }

    public List<Employee> findAllEmployees() {
        return employeeRepositoryV1.findAll((rs, rowNum) -> new Employee(
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("department"),
                rs.getDouble("salary"),
                rs.getDate("hire_date")
        ));
    }

    public void writeFile(String filePath) {
        List<Employee> allEmployees = findAllEmployees();

        fileWriteService.writeHeader(Employee.class, filePath);
        fileWriteService.writeBody(Employee.class, allEmployees, filePath);
    }
}
