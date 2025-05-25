package server.Database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface DatabaseQueryOperation<T> {
    T execute(PreparedStatement statement) throws SQLException;
}
