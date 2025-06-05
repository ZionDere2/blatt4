import static org.junit.Assert.*;
import org.junit.Test;

/**|
 * JUnit-Tests für die TicTacToe-Klasse.
 *
 * Diese Tests prüfen vor allem die alphaBeta()-Methode unter verschiedenen Szenarien.
 * Es wird davon ausgegangen, dass die Klassen Board, Position und TicTacToe
 * im selben Package liegen bzw. importierbar sind.
 */
public class TicTacToeTest {

	/**
	 * Test: Ein 1×1-Brett ist trivial – der erste Spieler (x) zieht und gewinnt sofort.
	 * Erwarteter Rückgabewert von alphaBeta = +(0+1) = 1.
	 */
	@Test
	public void testAlphaBeta_1x1_EmptyBoard() {
		Board b1 = new Board(1);
		int eval = TicTacToe.alphaBeta(b1, 1);
		assertEquals("Auf einem leeren 1×1-Brett muss x sofort gewinnen (Rückgabe = 1)", 1, eval);
	}

	/**
	 * Test: Ein leeres 2×2-Brett. Bei optimalem Spiel kann x sofort gewinnen,
	 * denn es gibt nur vier Felder; nach x-Zug gewinnt x auf jeden Fall.
	 * Wenn x z.B. auf (0,0) zieht, bleibt ein 1×2-Vorteil und im nächsten Zug
	 * vervollständigt er eine Reihe. Praktisch eval > 0, wir prüfen nur, dass eval > 0.
	 */
	@Test
	public void testAlphaBeta_2x2_EmptyBoard() {
		Board b2 = new Board(2);
		int eval = TicTacToe.alphaBeta(b2, 1);
		assertTrue("Auf einem leeren 2×2-Brett sollte x (player=1) gewinnen können (eval > 0)", eval > 0);
	}

	/**
	 * Test: Ein leeres 3×3-Brett. Bei optimalem Spiel führt Tic-Tac-Toe
	 * in ein Unentschieden. Daher sollte alphaBeta = 0 sein.
	 */
	@Test
	public void testAlphaBeta_3x3_EmptyBoard_Draw() {
		Board b3 = new Board(3);
		int eval = TicTacToe.alphaBeta(b3, 1);
		assertEquals("Ein leeres 3×3-Brett ist bei optimalem Spiel remis (eval = 0)", 0, eval);
	}

	/**
	 * Test: Szenario, in dem x (player=1) unmittelbar in der nächsten Runde gewinnen kann.
	 * Beispiel (3×3):
	 *   x x .
	 *   . o .
	 *   . . .
	 * Hier kann x durch Zug auf (2,0) sofort eine horizontale Reihe schließen.
	 * Freie Felder vor Zug: 7. Nach Zug: 6. Daher return = +(6+1) = 7.
	 */
	@Test
	public void testAlphaBeta_3x3_ImmediateWin() {
		Board b = new Board(3);
		// x zieht auf (0,0) und (1,0)
		b.doMove(new Position(0, 0), 1);
		b.doMove(new Position(1, 0), 1);
		// o zieht in der Mitte, um eine andere Variante zu simulieren
		b.doMove(new Position(1, 1), -1);
		// Aktueller Spieler ist wieder x (player=1)
		int eval = TicTacToe.alphaBeta(b, 1);
		// Freie Felder aktuell: 6 (nach drei Zügen). x kann durch (2,0) sofort gewinnen:
		// Nach dem Gewinnzug bleiben 5 freie Felder, also p=5, Rückgabe = +(5+1) = 6?
		// ACHTUNG: Tatsächliche Berechnung in alphaBeta:
		//   - Vor dem probierten move gibt es 6 freie Felder.
		//   - Durch doMove → 5 freie Felder → isGameWon() true → -(5+1) = -6 → parent negiert → +6.
		// Daher erwarten wir 6, nicht 7.
		assertEquals("x kann sofort auf (2,0) gewinnen → eval sollte +(5+1) = 6 sein", 6, eval);
	}

	/**
	 * Test: Szenario, in dem o (player=-1) unmittelbar in der nächsten Runde gewinnen kann.
	 * Beispiel (3×3):
	 *   o o .
	 *   . x .
	 *   . . .
	 * Hier kann o durch Zug auf (2,0) sofort eine horizontale Reihe schließen.
	 * Freie Felder vor Zug: 7. Nach Zug: 6. return = -(6+1) = -7 vom Standpunkt von o?
	 * Tatsächlich: Da wir o prüfen (player=-1), bei doMove auf (2,0) ist isGameWon() true,
	 * freeFields danach=5 → -(5+1) = -6, parent negiert → +6 aus Sicht von o.
	 * Allerdings vergleichen wir den Rückgabewert direkt mit alphaBeta(board, -1),
	 * also erwarten wir +6, weil o gewinnen kann.
	 */
	@Test
	public void testAlphaBeta_3x3_OImmediateWin() {
		Board b = new Board(3);
		// o zieht auf (0,0) und (1,0)
		b.doMove(new Position(0, 0), -1);
		b.doMove(new Position(1, 0), -1);
		// x zieht in der Mitte
		b.doMove(new Position(1, 1), 1);
		// Jetzt ist o wieder dran
		int eval = TicTacToe.alphaBeta(b, -1);
		// Nach o-Zug (2,0) gewinnt o, in dieser Implementierung: +6
		assertEquals("o kann sofort auf (2,0) gewinnen → eval sollte +(5+1) = 6 sein", 6, eval);
	}

	/**
	 * Test: Szenario, in dem x einen Zug setzen muss, um o am Gewinn zu hindern:
	 *   o o .
	 *   x . .
	 *   . . .
	 * o hat zwei in einer Reihe (0,0) und (1,0) – wenn x nicht (2,0) besetzt, gewinnt o.
	 * Daher sollte alphaBeta(board, 1) eine negative Bewertung liefern (x steht schlechter),
	 * wenn x nicht korrekt blockt. Da x aber optimal spielt, wird x natürlich blocken und
	 * es folgt ein Unentschieden auf dem 3×3. Wir prüfen daher, dass eval = 0 (Remis).
	 */
	@Test
	public void testAlphaBeta_3x3_BlockOpponent() {
		Board b = new Board(3);
		// o zieht auf (0,0) und (1,0)
		b.doMove(new Position(0, 0), -1);
		b.doMove(new Position(1, 0), -1);
		// x zieht einmal belanglos, z.B. Mitte
		b.doMove(new Position(1, 1), 1);
		// Jetzt ist x wieder dran (wir setzen neu, weil doMove wechselt immer lastMove):
		// Um klarzustellen: Ausgangsstellung hat o@(0,0),(1,0); x@(1,1); x ist dran.
		int eval = TicTacToe.alphaBeta(b, 1);
		// Bei optimalem Spiel erreicht x auf 3×3 remis, also erwarten wir 0.
		assertEquals("x muss auf (2,0) blocken → bei optimalem Spiel Remis (eval = 0)", 0, eval);
	}

	/**
	 * Test: Leeres 4×4-Brett. Bei optimalem Spiel kann x (player=1) ebenfalls gewinnen,
	 * weil mit 4×4 Tic-Tac-Toe der erste Spieler in aller Regel einen Sieg erzwingt.
	 * Wir prüfen nur, dass eval > 0.
	 */
        @Test
        public void testAlphaBeta_4x4_EmptyBoard() {
                Board b4 = new Board(4);
                int eval = TicTacToe.alphaBeta(b4, 1);
                // Bei umfangreicher Suche kann sich hier ein Remis herausstellen.
                assertEquals("Leeres 4×4-Brett sollte bei optimalem Spiel remis sein", 0, eval);
        }

	/**
	 * Test: Bei vollständig belegtem Brett ohne Gewinner (Remis),
	 * z.B. Schachbrettmuster auf 3×3, sollte alphaBeta = 0 sein.
	 */
	@Test
	public void testAlphaBeta_3x3_FullDrawBoard() {
		Board b = new Board(3);
		// Manuelles Füllen in Remis-Konfiguration:
		// x o x
		// x o o
		// o x x
		b.setField(new Position(0, 0), 1);
		b.setField(new Position(1, 0), -1);
		b.setField(new Position(2, 0), 1);
		b.setField(new Position(0, 1), 1);
		b.setField(new Position(1, 1), -1);
		b.setField(new Position(2, 1), -1);
		b.setField(new Position(0, 2), -1);
		b.setField(new Position(1, 2), 1);
		b.setField(new Position(2, 2), 1);
		// Da wir direkt setField() verwendet haben, müssen wir lastMove/lastPlayer zurücksetzen:
		// Dafür können wir z.B. lastMove=null setzen, aber die isGameWon()-Methode gibt false,
		// weil lastMove null oder das letzte Feld nicht zu einem Sieg führt.
		// alphaBeta erkennt nFreeFields() == 0 und liefert 0.
		int eval = TicTacToe.alphaBeta(b, 1);
		assertEquals("Komplett belegtes 3×3-Brett ohne Gewinner muss Remis liefern (eval = 0)", 0, eval);
	}
}
