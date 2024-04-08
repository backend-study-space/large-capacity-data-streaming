package example.largecapacitydatastreaming.v1.repository;

import example.largecapacitydatastreaming.support.aop.pointcut.TimeTracer;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@TimeTracer
public class EmployeeRepositoryV1 {

    private final DataSource dataSource;

    public EmployeeRepositoryV1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> List<T> findAll(RowMapper<T> rowMapper) {
        int rowNum = 0;
        String sql = "SELECT * FROM employee";

        try (Connection connection = DataSourceUtils.getConnection(dataSource);
             PreparedStatement pStmt = connection.prepareStatement(sql);
             ResultSet rs = pStmt.executeQuery()
        ) {
            List<T> tList = new ArrayList<>();

            while (rs.next()) {
                tList.add(rowMapper.mapRow(rs, rowNum++));
            }

            return tList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
