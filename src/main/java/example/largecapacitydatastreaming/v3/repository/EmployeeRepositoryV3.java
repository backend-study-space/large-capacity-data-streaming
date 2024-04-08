package example.largecapacitydatastreaming.v3.repository;

import example.largecapacitydatastreaming.support.aop.pointcut.TimeTracer;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
@TimeTracer
public class EmployeeRepositoryV3 {

    private final DataSource dataSource;

    public EmployeeRepositoryV3(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> void findAll(RowMapper<T> rowMapper) {

    }
}
