package b1ynovjavaprojet.b1ynovjavaprojet;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TrueFalseController {

    // Questions BDD
    @FXML
    private Label questionLabel;
    @FXML
    private Label feedbackLabel;

    // Questions joueur
    @FXML
    private TextField inputQuestion;
    @FXML
    private TextField inputAnswer;
    @FXML

    private final List<question> questions = new ArrayList<>();
    private int index = 0;
    private int score = 0;

    @FXML
    public void initialize() {
        if (!chargerQuestionsDepuisBdd()) {
            questionLabel.setText("Erreur lors du chargement des questions.");
            return;
        }
        afficherQuestionCourante();
    }

    // Tentative de chargement des questions
    private boolean chargerQuestionsDepuisBdd() {
        questions.clear(); // Nettoyage des précédentes questions

        // Requête BDD
        String sql = "SELECT id, question, reponse FROM true_or_false_questions";
        try (Connection conn = ConexionBdd.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet result = stmt.executeQuery()) {
            while (result.next()) {
                int id = result.getInt("id");
                String texte = result.getString("question");
                boolean reponse = result.getBoolean("reponse");
                questions.add(new question(id, texte, reponse));
            }

            // Mélange des questions
            java.util.Collections.shuffle(questions);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Affichage de la question en cours
    private void afficherQuestionCourante() {
        // Si pas de questions
        if (questions.isEmpty()) {
            questionLabel.setText("Aucune question.");
            return;
        }

        // S'il n'y a plus de questions -> fin
        if (index >= questions.size()) {
            questionLabel.setText("Quiz terminé !");
            feedbackLabel.setText("");
            return;
        }
        questionLabel.setText(questions.get(index).texte());
        feedbackLabel.setText("");
    }

    // Struct / Constructor pour les questions
    private record question(int id, String texte, boolean bonneReponse) {
    }

    // Envoyer true si on clique sur vrai
    @FXML
    void handleTrueAnswer() {
        verifierReponse(true);
    }

    // Sinon false
    @FXML
    void handleFalseAnswer() {
        verifierReponse(false);
    }

    // Vérificaton si on a bien répondu
    private void verifierReponse(boolean choixUtilisateur) {
        // Vérif si on est à la fin
        if (index >= questions.size())
            return;

        question questionActuelle = questions.get(index); // Récup de la question

        // Vérif si c'est la bonne réponse
        if (choixUtilisateur == questionActuelle.bonneReponse()) {
            feedbackLabel.setText("Correct !");
            score++;
        } else {
            feedbackLabel.setText("Incorrect !");
        }

        // Attendre avant de mettre la question suivante
        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(event -> {
            index++; // Passer à la question suivante

            if (index < questions.size()) {
                afficherQuestionCourante(); // Afficher la question suivante
            } else {
                // Si on a fini
                questionLabel.setText("Quiz terminé ! \nVotre score : " + score + "/" + questions.size());
                feedbackLabel.setText("");

                // Sauvegarde
                sauvegarderScore(score);

                // Retour au menu principal
                PauseTransition pauseReturn = new PauseTransition(Duration.seconds(3));
                pauseReturn.setOnFinished(e -> {
                    try {
                        switchToMenu();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                pauseReturn.play();
            }
        });

        pause.play();
    }

    // Retour au menu
    @FXML
    void switchToMenu() throws Exception {
        HelloApplication app = new HelloApplication();
        app.switchScene("menu.fxml");
    }

    // Sauvegarde des scores dans la bdd (réponses justes)
    void sauvegarderScore(int score) {
        String sql = "INSERT INTO scores(jeu, score) VALUES(?, ?)";
        try (Connection conn = ConexionBdd.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "TrueFalse");
            stmt.setInt(2, this.score);

            stmt.executeUpdate();
            System.out.println("Score Snake sauvegardé : " + this.score);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Ajout question
    @FXML
    protected void onValidateClick() {
        // Récup des entrées utilisateur et suppression des espaces
        String questionText = inputQuestion.getText().trim();
        String answerText = inputAnswer.getText().trim().toLowerCase();

        // Vérif si l'utilisateur a bien rempli
        if (questionText.isEmpty() || answerText.isEmpty()) {
            feedbackLabel.setText("Veuillez remplir les deux champs.");
            return;
        }

        // Vérif de la réponse
        int answer;
        if (answerText.equals("vrai") || answerText.equals("true")) {
            answer = 1;
        } else if (answerText.equals("faux") || answerText.equals("false")) {
            answer = 0;
        } else {
            feedbackLabel.setText("Réponse invalide. Utilisez 'Vrai' ou 'Faux' ou 'True' ou 'False'.");
            return;
        }

        // Ajout à la BDD
        String sql = "INSERT INTO true_or_false_questions(question, reponse) VALUES('" + questionText + "', " + answer
                + ")";
        try (Connection conn = ConexionBdd.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
            feedbackLabel.setText("Question ajoutée !");
            inputQuestion.clear();
            inputAnswer.clear();
            chargerQuestionsDepuisBdd(); // Recharger les questions depuis le début
            score = 0; // Réinitialiser le score
            index = 0; // Revenir à la première question
        } catch (SQLException e) {
            e.printStackTrace();
            feedbackLabel.setText("Erreur lors de l'ajout de la question.");
        }
    }
}
