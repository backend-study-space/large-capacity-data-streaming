package example.largecapacitydatastreaming;

import example.largecapacitydatastreaming.support.Column;

import java.util.Date;

public record EmployeeDto(
        @Column(Name = "fullName")
        String fullName,
        @Column(Name = "email")
        String email,
        @Column(Name = "department")
        String department,
        @Column(Name = "salary")
        double salary,
        @Column(Name = "hireDate")
        Date hireDate
) {
        public static EmployeeDto create(Employee employee) {
                return new EmployeeDto(
                        employee.firstName() + " " + employee.lastName(),
                        employee.email(),
                        employee.department(),
                        employee.salary(),
                        employee.hireDate()
                );
        }
}
