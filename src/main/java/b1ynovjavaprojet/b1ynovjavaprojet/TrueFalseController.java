package b1ynovjavaprojet.b1ynovjavaprojet;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TrueFalseController {

    @FXML
    private Label questionLabel;
    @FXML
    private Label feedbackLabel;

    private final List<question> questions = new ArrayList<>();
    private int index = 0;

    @FXML
    public void initialize() {
        chargerQuestionsDepuisBdd();
        afficherQuestionCourante();
    }

    private void chargerQuestionsDepuisBdd() {
        questions.clear();
        String sql = "SELECT id, question, reponse FROM scores_true_or_false_questions";
        try (Connection conn = ConexionBdd.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet result = stmt.executeQuery()) {
            while (result.next()) {
                int id = result.getInt("id");
                String texte = result.getString("question");
                boolean reponse = result.getBoolean("reponse");
                questions.add(new question(id, texte, reponse));
            }
        } catch (SQLException e) {
            questionLabel.setText("Erreur lors du chargement des questions.");
        }
    }

    private void afficherQuestionCourante() {
        if (questions.isEmpty()) {
            questionLabel.setText("Aucune question.");
            return;
        }
        if (index >= questions.size()) {
            questionLabel.setText("Quiz terminé !");
            feedbackLabel.setText("");
            return;
        }
        questionLabel.setText(questions.get(index).texte());
        feedbackLabel.setText("");
    }

    private record question(int id, String texte, boolean bonneReponse) {
    }

    @FXML
    void handleTrueAnswer() {
        verifierReponse(true);
    }

    @FXML
    void handleFalseAnswer() {
        verifierReponse(false);
    }

    private void verifierReponse(boolean choixUtilisateur) {
        if (index >= questions.size()) {
            return;
        }
        question questionReponse = questions.get(index);
        boolean correct = choixUtilisateur == questionReponse.bonneReponse();
        feedbackLabel.setText(correct ? "Correct !" : "Faux !");
        index++;
        if (index < questions.size()) {
            questionLabel.setText(questions.get(index).texte());
            feedbackLabel.setText("");
        } else {
            questionLabel.setText("Quiz terminé !");
            feedbackLabel.setText("");
        }
    }

    @FXML
    void switchToMenu() throws Exception {
        HelloApplication app = new HelloApplication();
        app.switchScene("menu.fxml");
    }
}
