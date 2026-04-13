package b1ynovjavaprojet.b1ynovjavaprojet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Duration;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

public class SnakeController {

    @FXML
    private Canvas canvas;
    private GraphicsContext dessin;

    // Tiles
    private final int tileSize = 32;

    // État du jeu
    private final List<int[]> snakeBody = new ArrayList<>();
    private int foodX, foodY;
    private int snakeX, snakeY;
    private int curDX = 0, curDY = 0;
    private boolean isDead = false;

    // Score
    private int score = 0;

    // Images
    private javafx.scene.image.Image pommeImg;
    private javafx.scene.image.Image SerpentTeteImg;
    private javafx.scene.image.Image SerpentCorpsImg;

    // 0 = Sol, 1 = Mur
    private int[][] map = {
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
            { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
    };

    @FXML
    public void initialize() {
        dessin = canvas.getGraphicsContext2D();
        
        // Charger images
        try {
            String pommePath = "/b1ynovjavaprojet/b1ynovjavaprojet/images/snake/pomme.png";
            String serpentPath = "/b1ynovjavaprojet/b1ynovjavaprojet/images/snake/serpent.png";
            String serpentCorpsPath = "/b1ynovjavaprojet/b1ynovjavaprojet/images/snake/serpent-corps.png";

            var isPomme = getClass().getResourceAsStream(pommePath);
            var isSerpent = getClass().getResourceAsStream(serpentPath);
            var isSerpentBody = getClass().getResourceAsStream(serpentCorpsPath);

            // On vérifie si les fichiers existent sur le disque
            if (isPomme != null) {
                pommeImg = new javafx.scene.image.Image(isPomme);
            }

            if (isSerpent != null) {
                SerpentTeteImg = new javafx.scene.image.Image(isSerpent);
            }

            if (isSerpentBody != null) {
                SerpentCorpsImg = new javafx.scene.image.Image(isSerpentBody);
            }
        } catch (Exception e) {}

        // Lancement du jeu
        loadMap();
        spawnFood();
        spawnSnake();

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
            return;
        }
        // Fais bouger le sepent dans la même direction tant qu'il n'y a pas d'autre
        // direction
        moveSnake(curDX, curDY);
        draw();
    }

    // Redessiner le jeu
    private void draw() {
        // Sol et les murs
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                dessin.setFill(map[y][x] == 1 ? Color.BLACK : Color.GRAY);
                dessin.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
            }
        }

        // Nourriture
        if (pommeImg != null) {
            dessin.drawImage(pommeImg, foodX * tileSize, foodY * tileSize, tileSize, tileSize);
        } else {
            dessin.setFill(Color.RED);
            dessin.fillRect(foodX * tileSize, foodY * tileSize, tileSize, tileSize);
        }

        // Corps du serpent
        dessin.setFill(Color.web("#2ecc71"));
        for (int[] part : snakeBody) {
            if (SerpentTeteImg != null) {
                dessin.drawImage(SerpentCorpsImg, part[0] * tileSize, part[1] * tileSize, tileSize, tileSize);
            } else {
                dessin.fillRect(part[0] * tileSize, part[1] * tileSize, tileSize, tileSize);
            }
        }

        // Tête
        if (SerpentTeteImg != null) {
            dessin.drawImage(SerpentTeteImg, snakeX * tileSize, snakeY * tileSize, tileSize, tileSize);
        } else {
            dessin.setFill(Color.GREEN);
            dessin.fillRect(snakeX * tileSize, snakeY * tileSize, tileSize, tileSize);
        }
    }

    // Charger la carte
    private void loadMap() {
        dessin = canvas.getGraphicsContext2D();

        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {

                switch (map[y][x]) {
                    case 1:
                        dessin.setFill(Color.BLACK);
                        break;
                    case 0:
                        dessin.setFill(Color.GRAY);
                        break;
                    case 2:
                        if (pommeImg != null) {
                            dessin.drawImage(pommeImg, foodX * tileSize, foodY * tileSize, tileSize, tileSize);
                        } else {
                            dessin.setFill(Color.RED);
                            dessin.fillRect(foodX * tileSize, foodY * tileSize, tileSize, tileSize);
                        }
                        break;
                    default:
                        dessin.setFill(Color.TRANSPARENT);
                        break;
                }

                dessin.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
            }
        }
    }

    // Générer de la nourriture
    private void spawnFood() {
        foodX = (int) (Math.random() * map[0].length);
        foodY = (int) (Math.random() * map.length);

        // Mur -> Générer une nouvelle position
        while (map[foodY][foodX] != 0) {
            foodX = (int) (Math.random() * map[0].length);
            foodY = (int) (Math.random() * map.length);
        }

        // Dessiner la nourriture
        if (pommeImg != null) {
            dessin.drawImage(pommeImg, foodX * tileSize, foodY * tileSize, tileSize, tileSize);
        } else {
            dessin.setFill(Color.RED);
            dessin.fillRect(foodX * tileSize, foodY * tileSize, tileSize, tileSize);
        }
    }

    // Apparition du serpent
    private void spawnSnake() {
        snakeX = (int) (Math.random() * map[0].length);
        snakeY = (int) (Math.random() * map.length);

        // Mur ou nourriture -> Générer une nouvelle position
        while (map[snakeY][snakeX] != 0 && (snakeX != foodX || snakeY != foodY)) {
            snakeX = (int) (Math.random() * map[0].length);
            snakeY = (int) (Math.random() * map.length);
        }

        // Dessiner le serpent
        if (SerpentTeteImg != null) {
            dessin.drawImage(SerpentTeteImg, snakeX * tileSize, snakeY * tileSize, tileSize, tileSize);
        } else {
            dessin.setFill(Color.GREEN);
            dessin.fillRect(snakeX * tileSize, snakeY * tileSize, tileSize, tileSize);
        }
    }

    // Logique de déplacement du serpent
    private void moveSnake(int dx, int dy) {
        // Regarder si on est mort
        if (isDead) {
            return;
        }

        int newX = snakeX + dx;
        int newY = snakeY + dy;

        // Vérifier les collisions avec les murs
        if (map[newY][newX] == 1) {
            isDead = true;
            gameOver();
            return;
        }

        // Vérifier les collisions avec la nourriture
        if (newX == foodX && newY == foodY) {
            snakeBody.add(new int[] { snakeX, snakeY });
            spawnFood();
        } else if (!snakeBody.isEmpty()) {
            snakeBody.remove(0);
            snakeBody.add(new int[] { snakeX, snakeY });
        }

        // Mettre à jour la position du serpent
        snakeX = newX;
        snakeY = newY;

        // Vérifier les collisions avec la queue
        if (checkQueueCollisions()) {
            isDead = true;
            gameOver();
            return;
        }

        // Redessiner le serpent
        loadMap();
    }

    // Touches du clavier
    public void setupControls(Scene scene) {
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();

            if (code == KeyCode.W || code == KeyCode.Z) {
                if (curDY != 1) {
                    curDX = 0;
                    curDY = -1;
                }
            } else if (code == KeyCode.S) {
                if (curDY != -1) {
                    curDX = 0;
                    curDY = 1;
                }
            } else if (code == KeyCode.A || code == KeyCode.Q) {
                if (curDX != 1) {
                    curDX = -1;
                    curDY = 0;
                }
            } else if (code == KeyCode.D) {
                if (curDX != -1) {
                    curDX = 1;
                    curDY = 0;
                }
            }
        });
    }

    // Game Over
    private void gameOver() {
        sauvegarderScore(score);

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

    // Menu
    @FXML
    void switchToMenu() throws Exception {
        HelloApplication app = new HelloApplication();
        app.switchScene("menu.fxml");
    }

    // Sauvegarder
    void sauvegarderScore(int score) {
        String sql = "INSERT INTO scores(jeu, score) VALUES('Snake', " + score + ")";
        try (Connection conn = ConexionBdd.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Collisions avec la queue
    private boolean checkQueueCollisions() {
        for (int[] part : snakeBody) {
            if (part[0] == snakeX && part[1] == snakeY) {
                return true;
            }
        }
        return false;
    }

}
