package b1ynovjavaprojet.b1ynovjavaprojet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionBdd {
    private static final String URL = buildJdbcUrl();

    // Conenxion au fichier de la bdd ou création du fichier
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


    // Création des tables
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);

        try (Statement st = conn.createStatement()) {
            st.executeUpdate( // Scores
                    "CREATE TABLE IF NOT EXISTS scores ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "jeu TEXT NOT NULL, "
                            + "score INTEGER NOT NULL)");
            st.executeUpdate( // Questions True or False
                    "CREATE TABLE IF NOT EXISTS true_or_false_questions ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + "question TEXT NOT NULL, "
                            + "reponse BOOLEAN NOT NULL)");
            st.executeUpdate( // Question de départ
                    "INSERT OR IGNORE INTO true_or_false_questions (id, question, reponse) VALUES "
                            + "(1, 'La switch est sortie en 2018 ?', 0), "
                            + "(2, 'Napoléon a été Empereur ?', 1), "
                            + "(3, 'La racine carrée de 16 est 4 ?', 1), "
                            + "(4, 'Y a t-il une faute dans L''ami à moi ?', 1)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS pendu_mots (" // Mots du pendu
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "mot TEXT NOT NULL UNIQUE)");
            st.executeUpdate("INSERT OR IGNORE INTO pendu_mots (mot) VALUES " // Mots de départ
                    + "('ordinateur'), "
                    + "('javafx'), "
                    + "('sqlite'),"
                    + "('programmation'), "
                    + "('developpement'),"
                    + "('application'), "
                    + "('interface'),"
                    + "('utilisateur'), "
                    + "('projet')");
        }
        return conn;
    }
}