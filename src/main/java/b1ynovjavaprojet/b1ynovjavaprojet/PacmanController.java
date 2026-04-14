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
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PacmanController {

    @FXML
    private Canvas canvas;
    private GraphicsContext dessin;
    @FXML
    private javafx.scene.control.Label scoreLabel;
    @FXML
    private javafx.scene.layout.Pane gameOverOverlay;
    @FXML
    private javafx.scene.control.Label finalScoreLabel;
    @FXML
    private Button btnPouvoir;
    @FXML
    private javafx.scene.control.Label timerLabel;
    @FXML
    private javafx.scene.control.Label LabelGameOver;
    @FXML
    private javafx.scene.shape.Rectangle health1;
    @FXML
    private javafx.scene.shape.Rectangle health2;
    @FXML
    private javafx.scene.shape.Rectangle health3;

    // Tiles
    private final int tileSize = 32;

    // Variables de jeu
    private int score = 0;
    private boolean isDead = false;
    private int curDX = 0, curDY = 0;
    private int playerX, playerY;
    private int[][] enemies;
    private int timerPouvoir = 0;
    private boolean isPowerActive = false;
    private int cooldownPouvoir = 0;
    private int[] enemyUnderTile;
    private int secondesRestantesReelles = 0;
    private int timerSpawnEnnemies = 0;
    private int cyclesPourScore = 0;
    private int casesRestantesGlissage = 0;
    private int health = 3;
    private int maxHealth = 3;
    private boolean hasCollidedThisFrame = false;

    // 0 = Sol, 1 = Mur, 2 -> Ennemis, 3 -> Joueur, 4 = Points
    private int[][] map = {
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
            { 1, 4, 4, 4, 4, 4, 4, 4, 4, 1, 1, 1, 4, 4, 4, 4, 4, 4, 4, 4, 1 },
            { 1, 4, 1, 1, 4, 1, 1, 1, 4, 1, 1, 1, 4, 1, 1, 1, 4, 1, 1, 4, 1 },
            { 1, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 1 },
            { 1, 4, 1, 1, 4, 1, 4, 1, 1, 1, 1, 1, 1, 1, 4, 1, 4, 1, 1, 4, 1 },
            { 1, 4, 4, 4, 4, 1, 4, 4, 4, 1, 1, 1, 4, 4, 4, 1, 4, 4, 4, 4, 1 },
            { 1, 1, 1, 1, 4, 1, 1, 1, 4, 1, 1, 1, 4, 1, 1, 1, 4, 1, 1, 1, 1 },
            { 1, 1, 1, 1, 4, 1, 4, 4, 4, 4, 4, 4, 4, 4, 4, 1, 4, 1, 1, 1, 1 },
            { 1, 4, 4, 4, 4, 4, 4, 1, 1, 2, 2, 2, 1, 1, 4, 4, 4, 4, 4, 4, 1 },
            { 1, 4, 1, 1, 4, 1, 4, 1, 1, 1, 1, 1, 1, 1, 4, 1, 4, 1, 1, 4, 1 },
            { 1, 4, 4, 1, 4, 1, 4, 4, 4, 4, 4, 4, 4, 4, 4, 1, 4, 1, 4, 4, 1 },
            { 1, 1, 4, 4, 4, 4, 4, 1, 1, 1, 1, 1, 1, 1, 4, 4, 4, 4, 4, 1, 1 },
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

        // Chercher les ennemis
        searchEnnemies();

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

            // Toutes les 0.25 secondes
            if (now - lastTick > 300_000_000) {
                lastTick = now;

                // Reset flag de collision
                hasCollidedThisFrame = false;

                // Mouvements ennemis
                iaEnnemies();

                update();

                // Points par seconde

                cyclesPourScore++;

                if (cyclesPourScore >= 3) {
                    score++;
                    scoreLabel.setText("Score: " + score);
                    cyclesPourScore = 0;
                }

                // Pouvoir
                if (isPowerActive) {
                    timerPouvoir++;

                    double secRelatived = (timerPouvoir * 0.3);
                    timerLabel.setText("Pouvoir : " + (int) (20.0 - secRelatived * 0.3) + "s");

                    if (timerPouvoir >= 66) {
                        isPowerActive = false;
                        timerPouvoir = 0;
                        cooldownPouvoir = 30;
                        timerLabel.setText("");
                    }
                }

                if (cooldownPouvoir > 0) {
                    cooldownPouvoir--;
                    timerLabel.setText("Cooldown : " + (int) (cooldownPouvoir * 0.3) + "s");

                    if (cooldownPouvoir == 0) {
                        btnPouvoir.setDisable(false);
                        timerLabel.setText("");
                    }
                }

                // S'il y a moins de 3 ennemis, en faire réapparaitre
                if (enemies.length < 3) {
                    timerSpawnEnnemies++;

                    // Si 5 sec éccoulés
                    if (timerSpawnEnnemies == 20) {
                        spawnEnnemies();
                    }
                }
            }
        }
    };

    private void update() {
        if (isDead) {
            draw();
            return;
        }

        // Fais bouger pacman
        if (casesRestantesGlissage > 0) {
            playerMovement(curDX, curDY);
            casesRestantesGlissage--;

            if (casesRestantesGlissage <= 0) {
                casesRestantesGlissage = 0;
                curDX = 0;
                curDY = 0;
            }
        }

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
                } else if (cell == 4) {
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

    // Logique de déplacement du joueur
    private void playerMovement(int dx, int dy) {
        if (isDead) {
            return;
        }

        int newX = playerX + dx;
        int newY = playerY + dy;

        // Vérifier les limites ou les murs
        if (newY < 0 || newY >= map.length || newX < 0 || newX >= map[0].length || map[newY][newX] == 1) {
            casesRestantesGlissage = 0;
            curDX = 0;
            curDY = 0;
            return;
        }

        // Récupération points
        if (map[newY][newX] == 4) {
            score += 2;
            if (scoreLabel != null) {
                scoreLabel.setText("Score: " + score);
            }
        }

        // Vérifier si c'est un ennemi
        if (map[newY][newX] == 2 && !isPowerActive) {
            if (health > 0) {
                loseHealth();
            } else {
                isDead = true;
                gameOver();
            }
            return;
        } else {
            // Si pouvoir actif, tuer l'ennemi
            if (map[newY][newX] == 2 && isPowerActive) {
                score += 20;
                scoreLabel.setText("Score: " + score);
                enemies = java.util.Arrays.stream(enemies).filter(e -> !(e[0] == newY && e[1] == newX))
                        .toArray(int[][]::new);
            }
        }

        // MAJ positions
        map[playerY][playerX] = 0;
        playerX = newX;
        playerY = newY;
        map[playerY][playerX] = 3;

        // Vérifier victoire
        if (verifierVictoire()) {
            win();
            return;
        }

        // Reset directions
        // curDX = 0;
        // curDY = 0;

        draw();
    }

    // Touches du clavier
    public void setupControls(Scene scene) {
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();

            if (code == KeyCode.W || code == KeyCode.Z) {
                if (casesRestantesGlissage <= 0) {
                    curDX = 0;
                    curDY = -1;
                    casesRestantesGlissage = 2;
                }
            } else if (code == KeyCode.S) {
                if (casesRestantesGlissage <= 0) {
                    curDX = 0;
                    curDY = 1;
                    casesRestantesGlissage = 2;
                }
            } else if (code == KeyCode.A || code == KeyCode.Q) {
                if (casesRestantesGlissage <= 0) {
                    curDX = -1;
                    curDY = 0;
                    casesRestantesGlissage = 2;
                }
            } else if (code == KeyCode.D) {
                if (casesRestantesGlissage <= 0) {
                    curDX = 1;
                    curDY = 0;
                    casesRestantesGlissage = 2;
                }
            }
        });
    }

    // Chercher les ennemis
    private void searchEnnemies() {
        List<int[]> listEnemies = new ArrayList<>();

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x] == 2) {
                    listEnemies.add(new int[] { y, x });
                }
            }
        }

        enemies = listEnemies.toArray(new int[0][0]);
        enemyUnderTile = new int[enemies.length];
    }

    // IA des ennemis
    private void iaEnnemies() {
        java.util.Random rand = new java.util.Random();

        int[][] directions = {
                { 0, 1 },
                { 0, -1 },
                { 1, 0 },
                { -1, 0 }
        };

        for (int i = 0; i < enemies.length; i++) {
            int ennemiY = enemies[i][0];
            int ennemiX = enemies[i][1];

            for (int essai = 0; essai < 10; essai++) {
                int[] dir = directions[rand.nextInt(4)];
                int newY = ennemiY + dir[0];
                int newX = ennemiX + dir[1];

                // Vérifier limites
                if (newY < 0 || newY >= map.length || newX < 0 || newX >= map[0].length) {
                    continue;
                }

                int destination = map[newY][newX];

                // Si joueur touché -> Game Over
                if (destination == 3 && !isPowerActive && !hasCollidedThisFrame) {
                    if (health > 0) {
                        loseHealth();
                        hasCollidedThisFrame = true;
                    } else {
                        isDead = true;
                        gameOver();
                        return;
                    }
                }

                // Si la case est libre
                if (destination == 0 || destination == 4) {
                    // Remettre la tuile
                    map[ennemiY][ennemiX] = enemyUnderTile[i];

                    // Sauvegarde tuile
                    enemyUnderTile[i] = destination;

                    // MAJ coordonnées
                    enemies[i][0] = newY;
                    enemies[i][1] = newX;

                    // Nouvelle position
                    map[newY][newX] = 2;

                    break;
                }
            }
        }
    }

    // Game Over
    private void gameOver() {
        sauvegarderScore(score);

        // Afficher l'overlay
        finalScoreLabel.setText("Score: " + score);
        gameOverOverlay.setVisible(true);

        gameLoop.stop();
    }

    // Menu
    @FXML
    private void switchToMenu() throws Exception {
        HelloApplication app = new HelloApplication();
        app.switchScene("menu.fxml");
    }

    // Sauvegarder
    private void sauvegarderScore(int score) {
        String sql = "INSERT INTO scores(jeu, score) VALUES(?, ?)";
        try (Connection conn = ConexionBdd.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "PacMan");
            stmt.setInt(2, this.score);

            stmt.executeUpdate();
            System.out.println("Score Snake sauvegardé : " + this.score);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Activer pouvoir
    @FXML
    private void activerPouvoir() {
        isPowerActive = true;
        timerPouvoir = 0;
        btnPouvoir.setDisable(true);
        secondesRestantesReelles = 0;
    }

    // Apparition ennemis toutes les 5 secondes
    private void spawnEnnemies() {
        map[8][11] = 2;
        timerSpawnEnnemies = 0;

        // Ajouter ennemi
        List<int[]> list = new ArrayList<>(java.util.Arrays.asList(enemies));
        list.add(new int[] { 8, 11 });
        enemies = list.toArray(new int[0][0]);

        int[] newUnder = new int[enemies.length];
        System.arraycopy(enemyUnderTile, 0, newUnder, 0, enemyUnderTile.length);
        enemyUnderTile = newUnder;
    }

    // Victoire
    private boolean verifierVictoire() {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x] == 4) {
                    return false;
                }
            }
        }
        return true;
    }

    private void win() {
        LabelGameOver.setText("Victoire");
        sauvegarderScore(score);

        // Afficher l'overlay
        finalScoreLabel.setText("Score: " + score);
        gameOverOverlay.setVisible(true);

        gameLoop.stop();
    }

    // Gestion vie
    private void loseHealth() {
        health--;
        updateHealthBar();

        if (health <= 0) {
            isDead = true;
            gameOver();
        }
    }

    // Coeurs UI
    private void updateHealthBar() {
        health1.setFill(health >= 1 ? Color.RED : Color.GRAY);
        health2.setFill(health >= 2 ? Color.RED : Color.GRAY);
        health3.setFill(health >= 3 ? Color.RED : Color.GRAY);
    }
}
