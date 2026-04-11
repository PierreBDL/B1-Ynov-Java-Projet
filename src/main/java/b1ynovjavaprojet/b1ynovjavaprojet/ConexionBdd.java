package b1ynovjavaprojet.b1ynovjavaprojet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
        String sql =
                // Scores Plus ou Moins
                "CREATE TABLE IF NOT EXISTS scores (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "jeu TEXT NOT NULL, " +
                        "tentatives INTEGER NOT NULL, " +
                        "score INTEGER NOT NULL);" +

                // Table des questions et scores True or False
                "CREATE TABLE IF NOT EXISTS scores_true_or_false_questions (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "question TEXT NOT NULL, " +
                        "reponse BOOLEAN NOT NULL);" +

                // Questions par défaut (id fixes : pas de doublon si getConnection() est rappelé)
                "INSERT OR IGNORE INTO scores_true_or_false_questions (id, question, reponse) VALUES " +
                        "(1, 'La switch est sortie en 2018 ?', 0), " +
                        "(2, 'Napoléon a été Empereur ?', 1), " +
                        "(3, 'La racine carrée de 16 est 4 ?', 1), " +
                        "(4, 'Y a t-il une faute dans L''ami à moi ?', 1);" +

                // Table des scores True or False
                "CREATE TABLE IF NOT EXISTS scores_true_or_false_scores (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "reponsesCorrectes INTEGER NOT NULL, " +
                        "score INTEGER NOT NULL);";
        conn.createStatement().execute(sql);
        return conn;
    }
}