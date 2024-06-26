package example.largecapacitydatastreaming.v2.repository;

import example.largecapacitydatastreaming.support.aop.pointcut.TimeTracer;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@TimeTracer
public class EmployeeRepositoryV2 {

    private final DataSource dataSource;

    public EmployeeRepositoryV2(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public int getCount() {
        String sql = "SELECT COUNT(*) FROM employee";
        int count = 0;
        try (Connection connection = DataSourceUtils.getConnection(dataSource);
             PreparedStatement pStmt = connection.prepareStatement(sql);
        ) {
            try (ResultSet rs = pStmt.executeQuery()) {

                if (rs.next()) {
                    count = rs.getInt(1);
                }

                return count;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> findAll(RowMapper<T> rowMapper, int start, int end) {
        int rowNum = 0;
        String sql = "SELECT * FROM employee LIMIT ?, ?";

        try (Connection connection = DataSourceUtils.getConnection(dataSource);
             PreparedStatement pStmt = connection.prepareStatement(sql);
        ) {
            pStmt.setInt(1, start);
            pStmt.setInt(2, end - start);

            try (ResultSet rs = pStmt.executeQuery()) {
                List<T> tList = new ArrayList<>();

                while (rs.next()) {
                    tList.add(rowMapper.mapRow(rs, rowNum++));
                }

                return tList;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
