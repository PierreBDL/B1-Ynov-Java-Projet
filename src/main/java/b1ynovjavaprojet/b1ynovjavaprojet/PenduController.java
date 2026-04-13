package b1ynovjavaprojet.b1ynovjavaprojet;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PenduController {

    @FXML
    private Canvas canvas;
    @FXML
    private Label wordDisplayLabel;
    @FXML
    private Label usedLettersLabel;
    @FXML
    private FlowPane keyboardPane;

    private String secretWord;
    private char[] guessedWord;
    private int mistakes = 0;
    private final int MAX_MISTAKES = 7;
    private List<String> usedLetters = new ArrayList<>();

    @FXML
    public void initialize() {
        startNewGame();
    }

    private void startNewGame() {
        mistakes = 0;
        usedLetters.clear();
        keyboardPane.setDisable(false);
        drawHangman(0);

        secretWord = getRandomWordFromDb().toUpperCase();
        guessedWord = new char[secretWord.length()];
        for (int i = 0; i < secretWord.length(); i++) {
            guessedWord[i] = '_';
        }

        updateUI();
        createKeyboard();
    }

    private String getRandomWordFromDb() {
        String sql = "SELECT mot FROM pendu_mots ORDER BY RANDOM() LIMIT 1";
        try (Connection conn = ConexionBdd.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet result = stmt.executeQuery(sql)) {
            if (result.next()) {
                return result.getString("mot");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "JAVA";
    }

    private void createKeyboard() {
        keyboardPane.getChildren().clear();
        for (char caractere = 'A'; caractere <= 'Z'; caractere++) {
            Button btn = new Button(String.valueOf(caractere));
            btn.setPrefSize(40, 40);
            btn.setOnAction(e -> handleGuess(btn));
            keyboardPane.getChildren().add(btn);
        }
    }

    private void handleGuess(Button btn) {
        String letter = btn.getText();
        btn.setDisable(true);
        usedLetters.add(letter);

        if (secretWord.contains(letter)) {
            for (int i = 0; i < secretWord.length(); i++) {
                if (secretWord.charAt(i) == letter.charAt(0))
                    guessedWord[i] = letter.charAt(0);
            }
        } else {
            mistakes++;
            drawHangman(mistakes);
        }

        updateUI();
        checkGameOver();
    }

    private void drawHangman(int step) {
        GraphicsContext dessin = canvas.getGraphicsContext2D();
        dessin.setStroke(Color.WHITE);
        dessin.setLineWidth(3);

        switch (step) {
            case 0:
                dessin.clearRect(0, 0, 400, 400);
                break;
            case 1:
                dessin.strokeLine(50, 350, 250, 350);
                break;
            case 2:
                dessin.strokeLine(100, 350, 100, 50);
                dessin.strokeLine(100, 50, 250, 50);
                break;
            case 3:
                dessin.strokeLine(250, 50, 250, 100);
                break;
            case 4:
                dessin.strokeOval(225, 100, 50, 50);
                break;
            case 5:
                dessin.strokeLine(250, 150, 250, 250);
                break;
            case 6:
                dessin.strokeLine(250, 170, 220, 210);
                dessin.strokeLine(250, 170, 280, 210);
                break;
            case 7:
                dessin.strokeLine(250, 250, 220, 300);
                dessin.strokeLine(250, 250, 280, 300);
                break;
        }
    }

    private void updateUI() {
        StringBuilder display = new StringBuilder();
        for (char caractere : guessedWord)
            display.append(caractere).append(" ");
        wordDisplayLabel.setText(display.toString());
        usedLettersLabel.setText("Lettres : " + String.join(", ", usedLetters));
    }

    private void checkGameOver() {
        if (new String(guessedWord).equals(secretWord)) {
            wordDisplayLabel.setText("GAGNÉ ! (" + secretWord + ")");
            sauvegarderScore(MAX_MISTAKES - mistakes);
            keyboardPane.setDisable(true);
        } else if (mistakes >= MAX_MISTAKES) {
            wordDisplayLabel.setText("PERDU ! Le mot était : " + secretWord);
            keyboardPane.setDisable(true);
        }
    }

    private void sauvegarderScore(int score) {
        String sql = "INSERT INTO scores(jeu, score) VALUES(?, ?)";
        try (Connection conn = ConexionBdd.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "Pendu");
            stmt.setInt(2, score);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void switchToMenu() throws Exception {
        new HelloApplication().switchScene("menu.fxml");
    }
}