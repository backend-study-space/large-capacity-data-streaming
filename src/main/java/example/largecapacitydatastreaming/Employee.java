package example.largecapacitydatastreaming;


import example.largecapacitydatastreaming.support.Column;

import java.util.Date;

public record Employee (
        @Column(Name = "firstName")
        String firstName,
        @Column(Name = "lastName")
        String lastName,
        @Column(Name = "email")
        String email,
        @Column(Name = "department")
        String department,
        @Column(Name = "salary")
        double salary,
        @Column(Name = "hireDate")
        Date hireDate
) {
}
