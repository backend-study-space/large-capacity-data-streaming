package example.largecapacitydatastreaming;

import example.largecapacitydatastreaming.support.Column;
import example.largecapacitydatastreaming.support.SerializableCustom;

import java.util.Date;

public record EmployeeDto(
        @Column(Name = "fullName")
        String fullName,
        @Column(Name = "autoCompleteName")
        String autoCompleteName,
        @Column(Name = "email")
        String email,
        @Column(Name = "department")
        String department,
        @Column(Name = "salary")
        double salary,
        @Column(Name = "hireDate")
        Date hireDate
) implements SerializableCustom {
        public static EmployeeDto create(Employee employee) {
                String str = employee.firstName() + " " + employee.lastName();

                return new EmployeeDto(
                        str,
                        analysisName(str),
                        employee.email(),
                        employee.department(),
                        employee.salary(),
                        employee.hireDate()
                );
        }

        @Override
        public String serialize() {
                return fullName + "," + autoCompleteName + "," + email + "," + department + "," + salary + "," + hireDate;
        }

        private static String analysisName(String fullName) {
                StringBuilder str = new StringBuilder();
                StringBuilder temp = new StringBuilder();

                for (char c : fullName.toCharArray()) {
                        temp.append(c);
                        str.append(temp).append(" ");
                }

                return str.toString();
        }
}
