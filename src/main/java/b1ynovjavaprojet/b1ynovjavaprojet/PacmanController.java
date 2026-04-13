package b1ynovjavaprojet.b1ynovjavaprojet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

public class PacmanController {

    @FXML
    private Canvas canvas;
    private GraphicsContext dessin;
    @FXML
    private javafx.scene.control.Label scoreLabel;

    // Tiles
    private final int tileSize = 32;

    // Variables de jeu
    private int score = 0;
    private boolean isDead = false;
    private int curDX = 0, curDY = 0;
    private int playerX, playerY;

    // 0 = Sol, 1 = Mur, 2 -> Ennemis, 3 -> Joueur
    private int[][] map = {
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 0, 1 },
            { 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1 },
            { 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1 },
            { 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 2, 1, 1, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 0, 1 },
            { 1, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 1 },
            { 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }
    };

    @FXML
    public void initialize() {

        // Trouver la position initiale du joueur dans la map
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x] == 3) {
                    playerX = x;
                    playerY = y;
                }
            }
        }

        dessin = canvas.getGraphicsContext2D();

        // Lancement du jeu
        draw();

        // Activer lecture clavier
        canvas.setFocusTraversable(true);

        // Boucle infinie
        gameLoop.start();
    }

    // Boucle de jeu infinie
    private long lastTick = 0;
    private final AnimationTimer gameLoop = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if (isDead) {
                return;
            }

            if (lastTick == 0) {
                lastTick = now;
                return;
            }

            // Toutes les 0.15 secondes
            if (now - lastTick > 150_000_000) {
                lastTick = now;

                update();
            }
        }
    };

    private void update() {
        if (isDead || (curDX == 0 && curDY == 0)) {
            draw();
            return;
        }

        // Fais bouger pacman
        playerMovement(curDX, curDY);
        draw();
    }

    // Redessiner le jeu
    private void draw() {
        // Effacer le canvas
        dessin.setFill(Color.BLACK);
        dessin.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                int cell = map[y][x];
                int positionX = x * tileSize;
                int positionY = y * tileSize;

                if (cell == 1) {
                    // Murs
                    dessin.setFill(Color.web("#1919A6"));
                    dessin.fillRoundRect(positionX + 2, positionY + 2, tileSize - 4, tileSize - 4, 8, 8);
                } else if (cell == 0) {
                    // Points
                    dessin.setFill(Color.web("#FFB8AE"));
                    dessin.fillOval(positionX + 14, positionY + 14, 4, 4);
                } else if (cell == 2) {
                    // Ennemis
                    dessin.setFill(Color.RED);
                    dessin.fillRoundRect(positionX + 4, positionY + 4, tileSize - 8, tileSize - 8, 15, 15);
                } else if (cell == 3) {
                    // Pacman
                    dessin.setFill(Color.YELLOW);
                    dessin.fillOval(positionX + 2, positionY + 2, tileSize - 4, tileSize - 4);
                }
            }
        }
    }

    // Logique de déplacement du serpent
    private void playerMovement(int dx, int dy) {
        if (isDead)
            return;

        int newX = playerX + dx;
        int newY = playerY + dy;

        // Vérifier les limites
        if (newY < 0 || newY >= map.length || newX < 0 || newX >= map[0].length)
            return;

        // Vérifier si c'est un mur
        if (map[newY][newX] == 1) {
            return;
        }

        // Vérifier si c'est un ennemi
        if (map[newY][newX] == 2) {
            isDead = true;
            System.out.println("Game Over!");
            return;
        }

        // Score
        if (map[newY][newX] == 0) {
            score += 10;
            if (scoreLabel != null) {
                scoreLabel.setText("Score: " + score);
            }
        }

        // MAJ positions
        map[playerY][playerX] = -1;
        playerX = newX;
        playerY = newY;
        map[playerY][playerX] = 3;

        // Reset directions
        curDX = 0;
        curDY = 0;

        draw();
    }

    // Touches du clavier
    public void setupControls(Scene scene) {
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();

            if (code == KeyCode.W || code == KeyCode.Z) {
                curDX = 0;
                curDY = -1;
            } else if (code == KeyCode.S) {
                curDX = 0;
                curDY = 1;
            } else if (code == KeyCode.A || code == KeyCode.Q) {
                curDX = -1;
                curDY = 0;
            } else if (code == KeyCode.D) {
                curDX = 1;
                curDY = 0;
            }
        });
    }
}
