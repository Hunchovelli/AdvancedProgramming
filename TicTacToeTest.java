import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TicTacToeTest {
	
	TicTacToe game;
	
	@BeforeEach
	void setUp() {
		game = new TicTacToe("player1", "player2");
	}
	
	@Test
	void testGetStringBoard() {		
		String expected = "    |    | \n" 
                + "---+---+---\n" 
		        + "    |    | \n" 
                + "---+---+---\n" 
		        + "    |    | \n";
		
		assertEquals(expected, game.getStringBoard());
	}
	
	@Test
	void testValidBoardSection() {
		
		String expected = "    |    | ";
		
		assertEquals(expected, game.getBoardSection(2));
	}
	
	@Test
	void testInValidBoardSection() {
		
		assertNull(game.getBoardSection(7));
	}

	@Test
	void testPlaceSignPlayer1() {
		game.placeSign(1, "player1");
		assertEquals("X", game.getMatrixBoard()[0][0]);
	}
	
	@Test
	void testPlaceSignPlayer2() {
		game.placeSign(1, "player2");
		assertEquals("O", game.getMatrixBoard()[0][0]);
	}
	
	@Test
	void testPlaceSignInvalidIndex() {
		game.placeSign(10, "player1");
		assertEquals(" ", game.getMatrixBoard()[0][0]);
	}
	
	@Test
	void testPlaceSignInvalidPlayer() {
		game.placeSign(10, "player3");
		assertEquals(" ", game.getMatrixBoard()[0][0]);
	}

	@Test
	void testCheckifPlayedFalse() {
		int index = 1;
		assertFalse(game.checkifPlayed(index));
	}
	
	@Test
	void testCheckifPlayedPlayer1() {
		int index = 1;
		game.placeSign(index, "player1");
		assertTrue(game.checkifPlayed(index));
	}
	
	@Test
	void testCheckifPlayedPlayer2() {
		int index = 1;
		game.placeSign(index, "player2");
		assertTrue(game.checkifPlayed(index));
	}

	@Test
	void testCheckWinnerPlayer1() {
		game.placeSign(1, "player1");
		game.placeSign(5, "player1");
		game.placeSign(9, "player1");
		
		assertEquals("player1", game.checkWinner());
	}
	
	@Test
	void testCheckWinnerPlayer2() {
		game.placeSign(3, "player2");
		game.placeSign(5, "player2");
		game.placeSign(7, "player2");
		
		assertEquals("player2", game.checkWinner());
	}
	
	@Test
	void testCheckTie() {
		game.placeSign(1, "player1");
		game.placeSign(5, "player1");
		game.placeSign(9, "player1");
		game.placeSign(3, "player2");
		game.placeSign(2, "player2");
		game.placeSign(7, "player2");
		game.placeSign(4, "player1");
		game.placeSign(6, "player2");
		game.placeSign(8, "player1");
		
		assertEquals("tie", game.checkWinner());
	}
	
	@Test
	void testNoResult() {		
		assertEquals("", game.checkWinner());
	}

	@Test
	void testResetBoard() {
		game.placeSign(3, "player2");
		game.placeSign(5, "player2");
		game.placeSign(7, "player2");
		game.resetBoard();
		String board = game.getStringBoard();
		
		String expected = "   |   |\n" 
                        + "---+---+---\n" 
		                + "   |   |\n" 
                        + "---+---+---\n" 
		                + "   |   |\n";
		
		
		assertEquals(expected, board);
		
	}

}
