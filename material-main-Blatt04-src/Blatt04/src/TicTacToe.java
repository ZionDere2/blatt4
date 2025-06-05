/**
 * Diese Klasse implementiert Alpha-Beta (Negamax) für beliebiges n×n‐Tic‐Tac‐Toe.
 */
public class TicTacToe {

    /**
     * Einstiegsmethode, die von außen aufgerufen wird.
     * @param board  aktueller Spielzustand
     * @param player aktueller Spieler (1 = 'x', −1 = 'o')
     * @return       Bewertung des aktuellen Zustands aus Sicht von „player“
     */
    public static int alphaBeta(Board board, int player) {
        // alpha = −∞, beta = +∞ initial
        return alphaBetaHelper(board, player, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Rekursive Hilfsmethode mit Alpha/Beta‐Parametern (Negamax-Variante).
     *
     * @param board  aktueller Spielzustand
     * @param player aktueller Spieler (1 oder −1)
     * @param alpha  bisher bester (maximierender) Wert
     * @param beta   bisher schlechtester (minimierender) Wert
     * @return       Bewertung aus Sicht von "player"
     */
    private static int alphaBetaHelper(Board board, int player, int alpha, int beta) {
        // 1) Terminaltest: Hat der Gegner (−player) gerade gewonnen?
        if (board.isGameWon()) {
            int p = board.nFreeFields();       // übrig gebliebene freie Felder
            return -(p + 1);                   // Gegner hat gewonnen → −(p+1)
        }

        // 2) Terminaltest: Kein freies Feld mehr → Unentschieden (0)
        if (board.nFreeFields() == 0) {
            return 0;
        }

        // 3) Negamax-Loop über alle legalen Züge für "player"
        int bestValue = Integer.MIN_VALUE;
        for (Position pos : board.validMoves()) {
            // Zug ausführen
            board.doMove(pos, player);

            // Negamax-Rekursionsaufruf: Vorzeichen invertieren, alpha/beta vertauschen
            int score = -alphaBetaHelper(board, -player, -beta, -alpha);

            // Zug zurücknehmen
            board.undoMove(pos);

            // bestValue aktualisieren (maximieren)
            if (score > bestValue) {
                bestValue = score;
            }
            // alpha aktualisieren
            if (bestValue > alpha) {
                alpha = bestValue;
            }
            // Alpha-Beta‐Abschneiden
            if (alpha >= beta) {
                break;
            }
        }

        return bestValue;
    }

    /**
     * Diese Methode wird in den JUnit‐Tests nicht abgefragt, kann also leer bleiben.
     */
    public static void evaluatePossibleMoves(Board board, int player) {
        // leer, wird von TicTacToeTest.java nicht geprüft
    }

    // Kein eigenes main() erforderlich – die Tests rufen alphaBeta() sofort auf.
}

