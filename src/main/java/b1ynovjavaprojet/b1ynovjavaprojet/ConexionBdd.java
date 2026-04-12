package b1ynovjavaprojet.b1ynovjavaprojet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionBdd {

    /**
     * Fichier SQLite binaire sous {@code data/}. Ne pas confondre avec un fichier
     * texte
     * contenant du SQL nommé {@code bdd.db} à la racine : SQLite exige un format
     * binaire.
     */
    private static final String URL = buildJdbcUrl();

    private static String buildJdbcUrl() {
        Path dbPath = Path.of("data", "bdd.db");
        Path parent = dbPath.getParent();
        if (parent != null) {
            try {
                Files.createDirectories(parent);
            } catch (IOException e) {
                throw new IllegalStateException("Impossible de créer le dossier data/ pour la base SQLite", e);
            }
        }
        String path = dbPath.toAbsolutePath().normalize().toString().replace('\\', '/');
        return "jdbc:sqlite:" + path;
    }

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        // Une instruction par appel : avec SQLite JDBC, un seul execute() sur une
        // chaîne multi-instructions n'exécute souvent que la première.
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS scores ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "jeu TEXT NOT NULL, "
                            + "score INTEGER NOT NULL)");
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS scores_true_or_false_questions ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "question TEXT NOT NULL, "
                            + "reponse BOOLEAN NOT NULL)");
            st.executeUpdate(
                    "INSERT OR IGNORE INTO scores_true_or_false_questions (id, question, reponse) VALUES "
                            + "(1, 'La switch est sortie en 2018 ?', 0), "
                            + "(2, 'Napoléon a été Empereur ?', 1), "
                            + "(3, 'La racine carrée de 16 est 4 ?', 1), "
                            + "(4, 'Y a t-il une faute dans L''ami à moi ?', 1)");
            st.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS scores_true_or_false_scores ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "reponsesCorrectes INTEGER NOT NULL, "
                            + "score INTEGER NOT NULL)");
        }
        return conn;
    }
}