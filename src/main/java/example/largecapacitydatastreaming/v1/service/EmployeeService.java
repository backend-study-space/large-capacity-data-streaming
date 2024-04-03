package example.largecapacitydatastreaming.v1.service;

import example.largecapacitydatastreaming.Employee;
import example.largecapacitydatastreaming.v1.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> findAllEmployees(String sql) {
        return employeeRepository.findAll(sql, (rs, rowNum) -> new Employee(rs.getString("name")));
    }
}
