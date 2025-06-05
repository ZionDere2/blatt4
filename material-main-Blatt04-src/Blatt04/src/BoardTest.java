// src/BoardTest.java

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.InputMismatchException;
import java.util.List;

/**
 * JUnit‐Tests für die Klasse Board.
 *
 * Diese Tests prüfen grundlegende Funktionalitäten:
 * - Konstruktor‐Grenzen
 * - getN(), nFreeFields()
 * - getField(), setField()
 * - doMove(), undoMove()
 * - isGameWon() für verschiedene Gewinn‐Szenarien
 * - validMoves()
 */
public class BoardTest {

	@Test
	public void testConstructorValidSize() {
		Board b3 = new Board(3);
		assertEquals("getN() sollte 3 zurückliefern", 3, b3.getN());
		assertEquals("nFreeFields() auf neuem 3×3‐Board sollte 9 sein", 9, b3.nFreeFields());

		Board b10 = new Board(10);
		assertEquals(10, b10.getN());
		assertEquals(100, b10.nFreeFields());
	}

	@Test
	public void testConstructorInvalidSize() {
		try {
			new Board(0);
			fail("Bei n=0 hätte eine InputMismatchException geworfen werden müssen");
		} catch (InputMismatchException e) {
			// richtig so
		}

		try {
			new Board(11);
			fail("Bei n=11 hätte eine InputMismatchException geworfen werden müssen");
		} catch (InputMismatchException e) {
			// richtig so
		}
	}

	@Test
	public void testGetAndSetField() {
		Board b = new Board(4);
		Position p = new Position(2, 1);

		// Standardmäßig ist jedes Feld 0
		assertEquals(0, b.getField(p));

		// Ungültige Positionen
		try {
			b.getField(new Position(-1, 0));
			fail("getField mit ungültiger Position sollte werfen");
		} catch (InputMismatchException e) { }

		try {
			b.setField(new Position(4, 4), 1);
			fail("setField mit Position außerhalb sollte werfen");
		} catch (InputMismatchException e) { }

		// Setze erlaubte Werte
		b.setField(p, 1);
		assertEquals(1, b.getField(p));
		assertEquals(15, b.nFreeFields()); // 16 minus eins

		b.setField(p, 0);
		assertEquals(0, b.getField(p));
		assertEquals(16, b.nFreeFields());

		b.setField(p, -1);
		assertEquals(-1, b.getField(p));
		assertEquals(15, b.nFreeFields());

		// Ungültiger Token
		try {
			b.setField(p, 2);
			fail("setField mit ungültigem Token sollte werfen");
		} catch (InputMismatchException e) { }
	}

	@Test
	public void testDoMoveAndUndoMove() {
		Board b = new Board(3);
		Position p = new Position(1, 1);

		// doMove auf freies Feld
		b.doMove(p, 1);
		assertEquals(1, b.getField(p));
		assertEquals(8, b.nFreeFields());

		// doMove auf besetztem Feld → IllegalArgumentException
		try {
			b.doMove(p, -1);
			fail("doMove auf besetztem Feld sollte IllegalArgumentException werfen");
		} catch (IllegalArgumentException e) { }

		// Undo und prüfen, dass Feld wieder frei ist
		b.undoMove(p);
		assertEquals(0, b.getField(p));
		assertEquals(9, b.nFreeFields());

		// undoMove auf freiem Feld tut nichts, wirft nicht
		b.undoMove(p);
		assertEquals(0, b.getField(p));
		assertEquals(9, b.nFreeFields());
	}

	@Test
	public void testIsGameWonRowsAndCols() {
		// Test Horizontale Gewinnreihe
		Board bRow = new Board(3);
		bRow.doMove(new Position(0, 0), 1);
		bRow.doMove(new Position(1, 0), 1);
		bRow.doMove(new Position(2, 0), 1);
		assertTrue("Drei x horizontal → isGameWon() sollte true sein", bRow.isGameWon());

		// Test Vertikale Gewinnreihe
		Board bCol = new Board(3);
		bCol.doMove(new Position(1, 0), -1);
		bCol.doMove(new Position(1, 1), -1);
		bCol.doMove(new Position(1, 2), -1);
		assertTrue("Drei o vertikal → isGameWon() sollte true sein", bCol.isGameWon());
	}

	@Test
	public void testIsGameWonDiagonals() {
		// Test Hauptdiagonale
		Board bDiag1 = new Board(3);
		bDiag1.doMove(new Position(0, 0), 1);
		bDiag1.doMove(new Position(1, 1), 1);
		bDiag1.doMove(new Position(2, 2), 1);
		assertTrue("Drei x auf Hauptdiagonale → isGameWon() true", bDiag1.isGameWon());

		// Test Nebendiagonale
		Board bDiag2 = new Board(3);
		bDiag2.doMove(new Position(2, 0), -1);
		bDiag2.doMove(new Position(1, 1), -1);
		bDiag2.doMove(new Position(0, 2), -1);
		assertTrue("Drei o auf Nebendiagonale → isGameWon() true", bDiag2.isGameWon());
	}

	@Test
	public void testIsGameWonFalseCases() {
		// Kein Gewinn bei weniger Steinen
		Board b = new Board(3);
		b.doMove(new Position(0, 0), 1);
		b.doMove(new Position(1, 0), 1);
		assertFalse("Nur zwei x horizontal → isGameWon() false", b.isGameWon());

		// Kein Gewinn nach Rückgängig
		b.undoMove(new Position(1, 0));
		assertFalse("UndoMove → keine Gewinnreihe → false", b.isGameWon());
	}

	@Test
	public void testValidMovesList() {
		Board b = new Board(2);
		// Zunächst sind 4 freie Felder
		List<Position> moves = (List<Position>) b.validMoves();
		assertEquals(4, moves.size());

		// Setze ein Feld, dann sollten nur noch 3 moves übrig sein
		b.doMove(new Position(0, 1), 1);
		moves = (List<Position>) b.validMoves();
		assertEquals(3, moves.size());
		assertFalse("Position (0,1) sollte nicht mehr in validMoves()", moves.contains(new Position(0, 1)));

		// Setze noch ein Feld, validMoves shrinks accordingly
		b.doMove(new Position(1, 0), -1);
		moves = (List<Position>) b.validMoves();
		assertEquals(2, moves.size());
	}

	@Test
	public void testFullBoardNoWin() {
		// Schachbrett‐Konfiguration 3×3 ohne Gewinner
		Board b = new Board(3);
		// x o x
		// o x o
		// o x x
		b.setField(new Position(0, 0), 1);
		b.setField(new Position(1, 0), -1);
		b.setField(new Position(2, 0), 1);
		b.setField(new Position(0, 1), -1);
		b.setField(new Position(1, 1), 1);
		b.setField(new Position(2, 1), -1);
		b.setField(new Position(0, 2), -1);
		b.setField(new Position(1, 2), 1);
		b.setField(new Position(2, 2), 1);

		// Alle Felder belegt, kein Gewinner
		assertEquals(0, b.nFreeFields());
		assertFalse("Kein Dreier in einer Reihe → isGameWon() false", b.isGameWon());
	}
}


