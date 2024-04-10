package example.largecapacitydatastreaming.v3.repository;

import example.largecapacitydatastreaming.support.aop.pointcut.TimeTracer;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Repository
@TimeTracer
public class EmployeeRepositoryV3 {

    private final DataSource dataSource;

    public EmployeeRepositoryV3(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> Stream<T> resultSetStream(RowMapper<T> rowMapper) {
        String sql = "SELECT * FROM employee";

        try {
            return CustomSpliterator.queryForStream(dataSource, sql, rowMapper);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static class CustomSpliterator<T> implements Spliterator<T> {

        private int rowNum = 0;

        private final ResultSet rs;

        private final RowMapper<T> rowMapper;


        public CustomSpliterator(ResultSet rs, RowMapper<T> rowMapper) {
            this.rs = rs;
            this.rowMapper = rowMapper;
        }

        public static <T> Stream<T> queryForStream(DataSource dataSource, String sql, RowMapper<T> rowMapper) throws SQLException {
            Connection connection = DataSourceUtils.getConnection(dataSource);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            return StreamSupport.stream(new CustomSpliterator<>(rs, rowMapper), false).onClose(() -> {
                JdbcUtils.closeResultSet(rs);
                JdbcUtils.closeStatement(stmt);
                DataSourceUtils.releaseConnection(connection, dataSource);
            });
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> consumer) {
            try {
                if (rs.next()) {
                    consumer.accept(rowMapper.mapRow(rs, rowNum++));

                    return true;
                } else {
                    return false;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Spliterator<T> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return 0;
        }

        @Override
        public int characteristics() {
            return ORDERED;
        }
    }
}
