## Pacman Game - Java Swing Edition

A fully playable **Pacman** game built in Java using **Swing** for the GUI. It features user authentication, a persistent leaderboard, periodic puzzle challenges 
via a REST API, and game-over jokes. The project demonstrates **low coupling**, **high cohesion**, **event-driven programming**, and **secure password storage**.

## Game Features

- Classic Pacman gameplay – collect dots, avoid ghosts.
- Smooth keyboard controls (arrow keys).
- Background music (plays `pacmanMusic.wav`).
- **Timer-based puzzle trigger**: Every 30 seconds, the game fetches a heart-counting puzzle from an external API.
- **Joke on game over**: When you lose all lives, a random joke is pulled from JokeAPI.
- **User system**:
  - Register / Login with password strength validation.
  - "Remember Me" for auto-login.
  - Leaderboard showing top 3 high scores.
- **Inactivity auto-logout** – after 30 minutes of no input, returns to login screen.
- **Pause** (`P` key) and **restart** (`S` from intro screen).

## Architecture & Design Highlights

The project follows **Model-View-Controller (MVC)** and **Data Access Object (DAO)** patterns to achieve low coupling and high cohesion.

Package       -> Responsibility
`pacman.game` ->  Game loop, state, rendering, input handling, ghost AI, timers |
`pacman.api`  ->  REST API clients (Heart Game puzzle, JokeAPI) |
`pacman.auth` ->  User authentication, password hashing (SHA-256 + salt), session management |
`pacman.db`   ->  SQLite database connection and initialization |
`pacman.ui`   ->  All Swing dialogs (login, register, leaderboard, puzzle, joke) |

**Key design decisions:**
- `GameState` stores only data (game board, positions, score).  
- `GameController` runs the logic (movement, collisions, timers) and communicates with the UI via callbacks (e.g., `repaintCallback`).  
- `Renderer` draws everything but knows nothing about input or timers.  
- `UserRepository` encapsulates database operations – switching from SQLite to another DB would only require changing `DatabaseManager`.  

## Interoperability – External APIs

Two REST APIs are integrated using `java.net.http.HttpClient` (no extra dependencies):

1. **Heart Game API** (`http://marcconrad.com/uob/heart/api.php`)  
   - Fetches a puzzle (image + solution) every 30 seconds.  
   - Correct answer gives +50 bonus points.

2. **JokeAPI** (`https://v2.jokeapi.dev/joke/Any`)  
   - Fetches a clean (non-offensive) joke when the player loses all lives.  
   - Supports both single‑part and two‑part jokes.

##  Security – Virtual Identity

- Passwords are **never stored in plain text**.  
- `SecureRandom` generates a 16‑byte salt per user.  
- `SHA-256` hashes the password + salt.  
- The `rememberToken` is a random UUID, stored in the database and saved to a local file (`.pacman_token`) when the user checks "Remember Me".  
- On logout, the token file is deleted; the database token remains for future re‑login.

##  How to Run

### Prerequisites
- Java 17 or higher (JDK)
- SQLite driver (included with Java 8+ via `java.sql`, no extra JAR needed)

### Steps
1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/Pacman-Game.git
   cd Pacman-Game
