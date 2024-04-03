package example.largecapacitydatastreaming.v1.repository;

import example.largecapacitydatastreaming.Employee;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EmployeeRepository {

    private final DataSource dataSource;

    public EmployeeRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> List<T> findAll(String sql, RowMapper<T> rowMapper) {
        int rowNum = 0;

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
