<div align="center">
  <h1 align="center">🎮 PacMan Maze</h1>
  <p align="center">
    Mini-jeux fait en Java avec JavaFX pour le projet de B1 à YNOV. <br />
    <br />
    <br />
    <a href="https://github.com/issues">🐛 Signaler un bug</a>
    ·
    <a href="https://github.com/issues">💡 Demander une fonctionnalité</a>
  </p>
  <p align="center">
    <img src="https://img.shields.io/badge/language-Java%20%7C%20JavaFX-FF6B00?style=for-the-badge&labelColor=000000" />
    <img src="https://img.shields.io/badge/build-Maven-C71A36?style=for-the-badge&labelColor=000000" />
    <img src="https://img.shields.io/badge/status-Jouable-28A745?style=for-the-badge&labelColor=000000" />
  </p>
</div>

---

### 🔍 Vue d'ensemble

Ce projet consite à créer plusieurs mini-jeux en Java, dont un jeu de PacMan, un snake ou encore un pendu. Les jeux sont développés avec JavaFX pour l'interface graphique et utilisent une base de données SQLite pour la sauvegarde des scores.

Les jeux sont entièrement intégrés avec une base de données SQLite pour la sauvegarde des meilleurs scores. Votre progression et vos points sont automatiquement sauvegardés après chaque partie.

> **Jouez localement** depuis votre ordinateur. Aucune dépendance externe requise (Maven gère tout).


### Jeux inclus :
- **PacMan Maze** : Collectez les points tout en évitant les ennemis.
- **Snake Classic** : Faites grandir votre serpent en mangeant des pommes.
- **Pendu** : Devinez le mot avant de perdre toutes vos vies.
- **Plus ou moins** : Devinez un nombre entre 1 et 1000.
- **True or False** : Testez vos connaissances avec des questions à choix binaire.

### 🎮 Jouer

**Compilez et lancez le projet** :
   ```bash
   ./mvnw javafx:run
   ```

---

### 🧰 Prérequis

- Java JDK 21 ou supérieur
- Maven 3.6+
- Un système d'exploitation Windows, macOS ou Linux

---

### 🚀 Installation et utilisation

#### Option 1 — Lancer directement

```bash
./mvnw javafx:run
```

#### Option 2 — Builder puis exécuter

```bash
# Compiler le projet
./mvnw clean package

# Exécuter l'application
./mvnw javafx:run
```

#### Option 3 — Sur Windows

```cmd
mvnw.cmd javafx:run
```

---

### 📖 Comment jouer

1. **Démarrez une partie**
   - Cliquez sur le jeu de votre choix depuis le menu

2. **Jouez**
   - Utilisez **la souris** ou **ZQSD** ou **WASD** pour vous déplacer
   - Réalisez l'objectif du jeu

---

### 📁 Structure du projet

```
.
├── pom.xml                                          # Configuration Maven
├── README.md                                        # Ce fichier
├── mvnw / mvnw.cmd                                  # Maven wrapper (Windows/Unix)
├── data/                                            # BDD
│   └── bdd.db
├── src/
│   └── main/
│       ├── java/
│       │   ├── module-info.java                   # Configuration modules
│       │   └── b1ynovjavaprojet/b1ynovjavaprojet/
│       │       ├── HelloApplication.java          # Point d'entrée et gestion des scènes
│       │       ├── Launcher.java                  # Lanceur JavaFX
│       │       ├── ConexionBdd.java               # Gestion de la base de données SQLite
│       │       ├── MenuController.java            # Contrôleur du menu principal
│       │       ├── PacmanController.java          # Logique principale de Pacman
│       │       ├── SnakeController.java           # Logique principale de Snake
│       │       ├── PenduController.java           # Logique principale du pendu
│       │       ├── PlusOuMoinsController.java     # Logique principale du jeu "Plus ou moins"
│       │       └── TrueOrFalseController.java     # Logique principale du jeu "True or False"
│       └── resources/
│           └── b1ynovjavaprojet/b1ynovjavaprojet/
│               ├── menu.fxml                      # Interface du menu principal
│               ├── pacman.fxml                    # Interface du jeu Pacman
│               ├── snake.fxml                     # Interface du jeu Snake
│               ├── pendu.fxml                     # Interface du jeu Pendu
│               ├── plusoumoins.fxml               # Interface du jeu "Plus ou moins"
│               ├── trueorfalse.fxml               # Interface du jeu "True or False"
│               ├── style.css                      # Feuille de styles
│               └── images/                        # Ressources graphiques
└── target/
```

---

### 🛠️ Stack technique

| Couche | Technologie |
|---|---|
| **Langage** | Java 21 |
| **Framework UI** | JavaFX 21 |
| **Build** | Apache Maven |
| **Base de données** | SQLite |
| **Rendu** | Canvas JavaFX (AnimationTimer) |

---

### 💾 Système de sauvegarde

Les scores sont automatiquement sauvegardés dans une base de données SQLite (`data/`) :
- Score de la partie terminée
- Nom du jeu (PacMan)

**Base de données utilisée :**
- Fichier SQLite local
- Schéma : `scores(id, jeu, score, date)`

Pour réinitialiser les scores, supprimez le fichier de base de données.

---

### 🔧 Configuration

#### Résolution de l'écran (Snake et PacMan)

- Largeur du canvas : 672px (21 tiles × 32px)
- Hauteur du canvas : 416px (13 tiles × 32px)
- Taille des tiles : 32px × 32px

---

<div align="center">

**Développé pour B1 - Informatique** — Projet scolaire YNOV

**Pierre BDL**

</div>
