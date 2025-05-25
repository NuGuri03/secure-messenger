package server.database;

import java.sql.*;
import java.util.HashMap;

import networked.CryptoUtil;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    private HashMap<String, PreparedStatement> preparedStatements = new HashMap<>();

    private DatabaseManager(String sqliteFileName) throws SQLException {
        if (instance != null) {
            throw new IllegalStateException("DatabaseManager instance already exists.");
        }

        if (sqliteFileName == null || sqliteFileName.isEmpty()) {
            // Use default SQLite file name
            sqliteFileName = "server.db";
        } else {
            System.out.println("[DatabaseManager] Using non-default SQLite filename: '" + sqliteFileName + "'");
        }

        instance = this;
        connection = DriverManager.getConnection("jdbc:sqlite:" + sqliteFileName);
        initializeDatabase();
    }

    public static DatabaseManager getInstance() {
        return instance;
    }

    public static void init(String sqliteFileName) throws SQLException {
        if (instance == null) {
            new DatabaseManager(sqliteFileName);
        }
    }

    public static void init() throws SQLException {
        // Use default SQLite file name
        init(null);
    }

    public <T> T query(String sql, DatabaseQueryOperation<T> callback) {
        try (var statement = prepareSql(sql)) {
            return callback.execute(statement);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public long generateId() {
        return CryptoUtil.generateRandomId();
    }

    private PreparedStatement prepareSql(String sql) throws SQLException {
        if (preparedStatements.containsKey(sql)) {
            var stmt = preparedStatements.get(sql);
            if (!stmt.isClosed()) {
                stmt.clearParameters();
                return stmt;
            } else {
                preparedStatements.remove(sql);
            }
        }

        PreparedStatement statement = connection.prepareStatement(sql);
        preparedStatements.put(sql, statement);
        return statement;
    }

    private void initializeDatabase() throws SQLException {
        try (var statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY,
                    handle TEXT NOT NULL UNIQUE COLLATE NOCASE,
                    public_key BLOB NOT NULL,
                    nickname TEXT NOT NULL,
                    bio TEXT DEFAULT "" NOT NULL,
                    encrypted_private_key BLOB NOT NULL,
                    encrypted_private_key_iv BLOB NOT NULL,
                    hashed_authentication_key BLOB NOT NULL,
                    hashed_authentication_key_salt BLOB NOT NULL,
                    created_at INTEGER DEFAULT (unixepoch('subsec') * 1000) NOT NULL,
                    updated_at INTEGER DEFAULT (unixepoch('subsec') * 1000) NOT NULL
                );
            """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS rooms (
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL
                );
            """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS room_users (
                    room_id INTEGER NOT NULL,
                    user_id INTEGER NOT NULL,
                    encrypted_key BLOB NOT NULL,

                    PRIMARY KEY (room_id, user_id),
                    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
                );
            """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS messages (
                    id INTEGER PRIMARY KEY,
                    room_id INTEGER NOT NULL,
                    author_id INTEGER,
                    created_at INTEGER DEFAULT (unixepoch()) NOT NULL,
                    encrypted_content BLOB NOT NULL,

                    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
                    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL
                );
            """);

            statement.execute("""
                CREATE INDEX IF NOT EXISTS index_messages ON messages(room_id, created_at);
            """);
        }
    }
}
