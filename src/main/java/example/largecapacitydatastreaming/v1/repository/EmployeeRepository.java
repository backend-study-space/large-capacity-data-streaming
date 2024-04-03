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
import java.util.Map;

@Repository
public class EmployeeRepository {

    private final DataSource dataSource;

    public EmployeeRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> Pair<Integer, List<T>> findAll(String sql, RowMapper<T> rowMapper) {
        Integer rowNum = 0;
        Connection connection = DataSourceUtils.getConnection(dataSource);

        try (ResultSet rs = connection.prepareStatement(sql).executeQuery(sql)) {
            List<T> tList = new ArrayList<>();

            while (rs.next()) {
                T t = rowMapper.mapRow(rs, rowNum++);

                tList.add(t);
            }

            return Pair.of(rowNum, tList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
