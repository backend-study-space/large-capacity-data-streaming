package example.largecapacitydatastreaming;


import java.util.Date;

public record Employee(
        String firstName,
        String lastName,
        String email,
        String department,
        double salary,
        Date hireDate
) {
}
