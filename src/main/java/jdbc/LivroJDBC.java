package jdbc;

import config.DatabaseConfig;
import model.Livro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivroJDBC {

    private static final String SQL_LISTAR =
        "SELECT id, titulo, autor, ano_publicacao, categoria FROM livro ORDER BY titulo";

    private static final String SQL_POR_ID =
        "SELECT id, titulo, autor, ano_publicacao, categoria FROM livro WHERE id = ?";

    private static final String SQL_INSERIR =
        "INSERT INTO livro (titulo, autor, ano_publicacao, categoria) VALUES (?, ?, ?, ?)";

    private static final String SQL_DELETAR =
        "DELETE FROM livro WHERE id = ?";

    private static final String SQL_CRIAR_TABELA = """
        CREATE TABLE IF NOT EXISTS livro (
            id              SERIAL PRIMARY KEY,
            titulo          VARCHAR(255) NOT NULL,
            autor           VARCHAR(150) NOT NULL,
            ano_publicacao  INTEGER,
            categoria       VARCHAR(100)
        )
        """;

    public List<Livro> listarLivros() {
        List<Livro> livros = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_LISTAR);
             ResultSet rs   = stmt.executeQuery()) {

            while (rs.next()) {
                livros.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("[LivroJDBC] Erro ao listar livros: " + e.getMessage());
        }

        return livros;
    }

    public Livro buscarPorId(Long id) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_POR_ID)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("[LivroJDBC] Erro ao buscar por id: " + e.getMessage());
        }
        return null;
    }

    public Livro inserir(Livro livro) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERIR, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getAutor());
            stmt.setObject(3, livro.getAnoPublicacao(), Types.INTEGER);
            stmt.setString(4, livro.getCategoria());

            int linhas = stmt.executeUpdate();

            if (linhas > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        livro.setId(keys.getLong(1));
                    }
                }
                return livro;
            }

        } catch (SQLException e) {
            System.err.println("[LivroJDBC] Erro ao inserir livro: " + e.getMessage());
        }
        return null;
    }

    public boolean deletar(Long id) {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETAR)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[LivroJDBC] Erro ao deletar livro: " + e.getMessage());
            return false;
        }
    }

    public void criarTabelaSeNaoExistir() {
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(SQL_CRIAR_TABELA);
            System.out.println("[LivroJDBC] Tabela 'livro' verificada/criada com sucesso.");

        } catch (SQLException e) {
            System.err.println("[LivroJDBC] Erro ao criar tabela: " + e.getMessage());
        }
    }

    private Livro mapearResultSet(ResultSet rs) throws SQLException {
        Livro l = new Livro();
        l.setId(rs.getLong("id"));
        l.setTitulo(rs.getString("titulo"));
        l.setAutor(rs.getString("autor"));
        l.setAnoPublicacao(rs.getObject("ano_publicacao", Integer.class));
        l.setCategoria(rs.getString("categoria"));
        return l;
    }
}
