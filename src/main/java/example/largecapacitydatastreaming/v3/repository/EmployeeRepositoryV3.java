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

    public <T> Stream<T> resultSetStream(int pinPoint,
                                         RowMapper<T> rowMapper,
                                         Consumer<T> consumer) {
        String sql = "SELECT * FROM employee";

        try {
            return CustomSpliterator.queryForStream(dataSource, sql, rowMapper, pinPoint, consumer);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static class CustomSpliterator<T> implements Spliterator<T> {

        private int pinPoint;

        private Consumer<T> listener;

        private final ResultSet rs;

        private final RowMapper<T> rowMapper;

        private int rowNum = 0;

        public CustomSpliterator(ResultSet rs, RowMapper<T> rowMapper) {
            this.rs = rs;
            this.rowMapper = rowMapper;
        }

        public CustomSpliterator(ResultSet rs, RowMapper<T> rowMapper, int pinPoint) {
            this.rs = rs;
            this.rowMapper = rowMapper;
            this.pinPoint = pinPoint;
        }

        public CustomSpliterator<T> pinned(Consumer<T> listener) {
            this.listener = listener;

            return this;
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

        public static <T> Stream<T> queryForStream(DataSource dataSource,
                                                   String sql,
                                                   RowMapper<T> rowMapper,
                                                   int pinPoint,
                                                   Consumer<T> consumer) throws SQLException {
            Connection connection = DataSourceUtils.getConnection(dataSource);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            return StreamSupport.stream(new CustomSpliterator<>(rs, rowMapper, pinPoint).pinned(consumer), false).onClose(() -> {
                JdbcUtils.closeResultSet(rs);
                JdbcUtils.closeStatement(stmt);
                DataSourceUtils.releaseConnection(connection, dataSource);
            });
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> consumer) {
            try {
                if (rs.next()) {
                    T t = rowMapper.mapRow(rs, rowNum++);
                    consumer.accept(t);

                    if (rowNum > 0 && rowNum % pinPoint == 0) {
                        listener.accept(t);
                    }

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
