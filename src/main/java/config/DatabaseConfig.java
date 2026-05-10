package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    public static final String URL      = "jdbc:postgresql://localhost:5432/catalogo_livros";
    public static final String USUARIO  = "postgres";
    public static final String SENHA    = "postgres";
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    private DatabaseConfig() { }
}
