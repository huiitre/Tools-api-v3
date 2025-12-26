package fr.huiitre.tools.infrastructure.persistence.common;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.jdbc.datasource.DataSourceUtils;

public abstract class AbstractPostgresRepository {

    protected final DataSource dataSource;

    protected AbstractPostgresRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected Connection openConnection() throws SQLException {
        return DataSourceUtils.getConnection(dataSource);
    }

    protected RuntimeException sqlError(String message, SQLException e) {
        return new RuntimeException(message, e);
    }
}
