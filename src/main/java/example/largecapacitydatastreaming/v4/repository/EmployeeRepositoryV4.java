package example.largecapacitydatastreaming.v4.repository;

import example.largecapacitydatastreaming.support.aop.pointcut.TimeTracer;
import org.apache.logging.log4j.util.Strings;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Repository
@TimeTracer
public class EmployeeRepositoryV4 {

    private final DataSource dataSource;

    public EmployeeRepositoryV4(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public <T> Stream<T> resultSetStream(int bulkPoint,
                                         RowMapper<T> rowMapper,
                                         Runnable listener) {
        String sql = "SELECT * FROM employee";

        try {
            return CustomSpliterator.queryForStream(dataSource, sql, rowMapper, bulkPoint, listener);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> Stream<T> resultSetStream(RowMapper<T> rowMapper,
                                         Runnable listener) {
        String sql = "SELECT * FROM employee";

        try {
            return CustomSpliterator.queryForStream(dataSource, sql, rowMapper, listener);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static class CustomSpliterator<T> implements Spliterator<T> {

        private static final int DEFAULT_BULK_POINT = 10_000;

        private int bulkPoint = DEFAULT_BULK_POINT;

        private int rowNum = 0;

        private Runnable listener;

        private final ResultSet rs;

        private final RowMapper<T> rowMapper;


        public CustomSpliterator(ResultSet rs, RowMapper<T> rowMapper, int bulkPoint) {
            this.rs = rs;
            this.rowMapper = rowMapper;
            this.bulkPoint = bulkPoint;
        }

        public CustomSpliterator(ResultSet rs, RowMapper<T> rowMapper) {
            this.rs = rs;
            this.rowMapper = rowMapper;
        }

        public EmployeeRepositoryV4.CustomSpliterator<T> pinned(Runnable listener) {
            this.listener = listener;

            return this;
        }

        public static <T> Stream<T> queryForStream(DataSource dataSource,
                                                   String sql,
                                                   RowMapper<T> rowMapper,
                                                   Runnable listener) throws SQLException {
            ComfortableConnection connection = new ComfortableConnection(dataSource, sql);

            return StreamSupport.stream(new CustomSpliterator<>(connection.resultSet, rowMapper)
                    .pinned(listener), false)
                    .onClose(connection::close);
        }

        public static <T> Stream<T> queryForStream(DataSource dataSource,
                                                   String sql,
                                                   RowMapper<T> rowMapper,
                                                   int pinPoint,
                                                   Runnable listener) throws SQLException {
            ComfortableConnection connection = ComfortableConnection.open(dataSource, sql);

            return StreamSupport.stream(new CustomSpliterator<>(connection.resultSet, rowMapper, pinPoint)
                    .pinned(listener), false)
                    .onClose(connection::close);
        }

        private static class ComfortableConnection {
            private final DataSource dataSource;

            private final Connection connection;

            private final Statement statement;

            private final ResultSet resultSet;

            private ComfortableConnection(DataSource dataSource, String sql) throws SQLException {
                this.dataSource = dataSource;
                this.connection = DataSourceUtils.getConnection(dataSource);
                this.statement = connection.createStatement();
                this.resultSet = statement.executeQuery(sql);
            }

            public static ComfortableConnection open(DataSource dataSource, String sql) throws SQLException {
                Objects.requireNonNull(dataSource);
                Strings.isNotBlank(sql);

                return new ComfortableConnection(dataSource, sql);
            }

            public void close() {
                JdbcUtils.closeResultSet(resultSet);
                JdbcUtils.closeStatement(statement);
                DataSourceUtils.releaseConnection(connection, dataSource);
            }
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> consumer) {
            try {
                if (rs.next()) {
                    T t = rowMapper.mapRow(rs, rowNum++);
                    consumer.accept(t);

                    if (rowNum > 0 && rowNum % bulkPoint == 0) {
                        listener.run();
                    }

                    return true;
                } else {
                    listener.run();
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
