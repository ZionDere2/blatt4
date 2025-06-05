import java.util.InputMismatchException;
import java.util.ArrayList;
import java.util.List;

// Irgendwer: Klasse fürs Tic-Tac-Toe Brett n x n, leere Felder sind 0, x=+1, o=-1.
public class Board {
    private int n;
    private int[][] grid;        // grid[x][y]
    private int freeCount;       // wie viele Felder noch frei sind
    private Position lastMove;   // letzte gesetzte Position
    private int lastPlayer;      // wer zuletzt dran war (+1 oder -1)

    // Konstruktor, wirft Exception, falls n<1 oder n>10
    public Board(int n) {
        if (n < 1 || n > 10) {
            throw new InputMismatchException("n muss zwischen 1 und 10 sein.");
        }
        this.n = n;
        grid = new int[n][n];
        freeCount = n * n;
        lastMove = null;
        lastPlayer = 0;
        // grid wird automatisch mit 0 initialisiert
    }

    // Gib die Größe zurück
    public int getN() {
        return n;
    }

    // Anzahl freier Felder
    public int nFreeFields() {
        return freeCount;
    }

    // Wert an Position pos: 0=leer, +1=x, -1=o
    public int getField(Position pos) {
        int x = pos.x;
        int y = pos.y;
        if (x < 0 || x >= n || y < 0 || y >= n) {
            throw new InputMismatchException("Position ungültig: " + pos);
        }
        return grid[x][y];
    }

    // Setzt token an pos, token muss -1,0 oder +1 sein. Passt freeCount an, falls 0↔±1
    public void setField(Position pos, int token) {
        int x = pos.x;
        int y = pos.y;
        if (x < 0 || x >= n || y < 0 || y >= n) {
            throw new InputMismatchException("Position ungültig: " + pos);
        }
        if (token != -1 && token != 0 && token != 1) {
            throw new InputMismatchException("Token muss -1,0 oder +1 sein: " + token);
        }
        int old = grid[x][y];
        if (old == 0 && token != 0) {
            freeCount--;
        } else if (old != 0 && token == 0) {
            freeCount++;
        }
        grid[x][y] = token;
    }

    // Führt einen Zug von player (±1) an Position pos aus; wirft IllegalArgumentException, wenn nicht frei
    public void doMove(Position pos, int player) {
        if (player != 1 && player != -1) {
            throw new IllegalArgumentException("Spieler muss ±1 sein.");
        }
        int x = pos.x;
        int y = pos.y;
        if (x < 0 || x >= n || y < 0 || y >= n) {
            throw new IllegalArgumentException("Position außerhalb: " + pos);
        }
        if (grid[x][y] != 0) {
            throw new IllegalArgumentException("Feld belegt: " + pos);
        }
        setField(pos, player);
        lastMove = new Position(x, y);
        lastPlayer = player;
    }

    // Macht den Zug an pos rückgängig (setzt wieder 0). Falls eh schon 0, dann nichts tun.
    public void undoMove(Position pos) {
        int x = pos.x;
        int y = pos.y;
        if (x < 0 || x >= n || y < 0 || y >= n) {
            throw new IllegalArgumentException("Position ungültig: " + pos);
        }
        if (grid[x][y] == 0) {
            return; // war eh frei
        }
        setField(pos, 0);
        lastMove = null;
        lastPlayer = 0;
    }

    // Checke, ob der letzte Zug gewonnen hat (nur Zeile/Spalte/Diagonalen um lastMove)
    public boolean isGameWon() {
        if (lastMove == null) {
            return false;
        }
        int xm = lastMove.x;
        int ym = lastMove.y;
        int p = lastPlayer; // +1 oder -1

        // Zeile prüfen
        boolean gewonnen = true;
        for (int xi = 0; xi < n; xi++) {
            if (grid[xi][ym] != p) {
                gewonnen = false;
                break;
            }
        }
        if (gewonnen) return true;

        // Spalte prüfen
        gewonnen = true;
        for (int yi = 0; yi < n; yi++) {
            if (grid[xm][yi] != p) {
                gewonnen = false;
                break;
            }
        }
        if (gewonnen) return true;

        // Hauptdiagonale nur, wenn xm==ym
        if (xm == ym) {
            gewonnen = true;
            for (int i = 0; i < n; i++) {
                if (grid[i][i] != p) {
                    gewonnen = false;
                    break;
                }
            }
            if (gewonnen) return true;
        }

        // Nebendiagonale nur, wenn xm+ym == n-1
        if (xm + ym == n - 1) {
            gewonnen = true;
            for (int i = 0; i < n; i++) {
                if (grid[i][n - 1 - i] != p) {
                    gewonnen = false;
                    break;
                }
            }
            if (gewonnen) return true;
        }

        return false;
    }

    // Liefere Liste aller freien Felder als Positionen
    public Iterable<Position> validMoves() {
        List<Position> moves = new ArrayList<>();
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                if (grid[x][y] == 0) {
                    moves.add(new Position(x, y));
                }
            }
        }
        return moves;
    }

    // Für Debug: Brett ausdrucken, '.' für leer, 'x' für +1, 'o' für -1
    public void print() {
        System.out.println("----- Board(" + n + "x" + n + ") -----");
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                char c;
                if (grid[x][y] == 1) {
                    c = 'x';
                } else if (grid[x][y] == -1) {
                    c = 'o';
                } else {
                    c = '.';
                }
                System.out.print(c + " ");
            }
            System.out.println();
        }
        System.out.println("-----------------------");
    }
}

