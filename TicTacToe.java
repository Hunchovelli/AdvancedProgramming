// This class has been adapted from the following YouTube video: https://youtu.be/gQb3dE-y1S4

// This class is used to implement the TicTacToe game that the user can play with the server

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class TicTacToe {
	
	private String[][] gameBoard = {{" ", "   |", " ", "   |", " "}, 
			{"---", "+", "---", "+", "---"}, 
			{" ", "   |", " ", "   |", " "},
			{"---", "+", "---", "+", "---"}, 
			{" ", "   |", " ", "   |", " "}};
	
	
	// Used to check the positions played by the two players
	private ArrayList<Integer> player1Positions = new ArrayList<Integer>();
	private ArrayList<Integer> player2Positions = new ArrayList<Integer>();
	
	String player1;
	String player2;
	
	public TicTacToe(String player1, String player2)
	{
		this.player1 = player1;
		this.player2 = player2;
	}
	
	// Get matrix representation of the game board
	public String[][] getMatrixBoard()
	{
		return gameBoard;
	}
	
	// Get string representation of the game board
	public String getStringBoard()
	{
		String board = "";
		
		for (String[] row : gameBoard)
		{
			for (String symbol : row)
			{
				board += symbol;
			}
			
			board += "\n";
		}
		
		return board;
	}
	
	// Get a section of the matrix
	// Used by the server to get each section individually
	public String getBoardSection(int index)
	{
		String section = "";
		if (index > 4)
		{
			return null;
		}
		
		else
		{
			String[] row = gameBoard[index];
			
			for (String symbol : row)
			{
				section += symbol;
			}
		}
		
		return section;
	}
	
	// Used to place the relevant symbol based on the player and position
	public void placeSign(int index, String user)
	{
		
		String sign = "";
		
		if (user.equals(player1))
		{
			sign = "X";
			player1Positions.add(index);
		}
		
		else if (user.equals(player2)) 
		{
			sign = "O";
			player2Positions.add(index);
		}	
		
		switch(index)
		{
		case 1: 
			gameBoard[0][0] = sign;
			break;
		case 2: 
			gameBoard[0][2] = sign;
			break;
		case 3: 
			gameBoard[0][4] = sign;
			break;
		case 4: 
			gameBoard[2][0] = sign;
			break;
		case 5: 
			gameBoard[2][2] = sign;
			break;
		case 6: 
			gameBoard[2][4] = sign;
			break;
		case 7: 
			gameBoard[4][0] = sign;
			break;
		case 8: 
			gameBoard[4][2] = sign;
			break;
		case 9: 
			gameBoard[4][4] = sign;
			break;
		default:
			break;
		}
	}
	
	// Check if a particular position has already been played
	public boolean checkifPlayed(int index)
	{
		if (player2Positions.contains(index) || player1Positions.contains(index))
		{
			return true;
		}
		
		return false;
	}
	
	// Determine the result of the game
	public String checkWinner()
	{
		List topRow = Arrays.asList(1, 2, 3);
		List midRow = Arrays.asList(4, 5, 6);
		List botRow = Arrays.asList(7, 8, 9);
		List leftCol = Arrays.asList(1, 4, 7);
		List midCol = Arrays.asList(2, 5, 8);
		List rightCol = Arrays.asList(3, 6, 9);
		List leftDiagonal = Arrays.asList(1, 5, 9);
		List rightDiagonal = Arrays.asList(3, 5, 7);
		
		List<List> winning = new ArrayList<>();
		
		winning.add(topRow);
		winning.add(midRow);
		winning.add(botRow);
		winning.add(leftCol);
		winning.add(rightCol);
		winning.add(midCol);
		winning.add(leftDiagonal);
		winning.add(rightDiagonal);
		
		for (List l : winning) 
		{
			if (player1Positions.containsAll(l))
			{
				return player1;
			}
			
			else if (player2Positions.containsAll(l))
			{
				return player2;
			}
			
			else if (player1Positions.size() + player2Positions.size() == 9)
			{
				return "tie";
			}
		}
		
		return "";
	}
	
	// Reset the board
	public void resetBoard()
	{
		for (int i = 0; i < gameBoard.length; i++)
		{
			for (int j = 0; j < gameBoard[i].length; j++)
			{
				if (gameBoard[i][j].equals("+") || gameBoard[i][j].equals("   |") || gameBoard[i][j].equals("---"))
				{
					continue;
				}
				
				else gameBoard[i][j] = "";
			}
		}
		
		player1Positions.clear();
		player2Positions.clear();
	}
	
	
//###########################################################################################################################################################################
	
	// THIS MAIN METHOD IS USED TO PLAY A GAME OF TIC TAC TOE BETWEEN 2 PLAYERS IN THE CONSOLE. YOU CAN UNCOMMENT IT TO TEST IT OUT.
	
//###########################################################################################################################################################################	
	
//	public static void main(String[] args)
//	{
//		Scanner players = new Scanner(System.in);
//		System.out.println("Enter the player names:");
//		String player1 = players.next();
//		String player2 = players.next();
//		
//		Scanner position = new Scanner(System.in);
//		
//		TicTacToe game = new TicTacToe(player1, player2);
//		
//		
//// Code stub used to fix unit test case		
////		
////		TicTacToe game = new TicTacToe("player1", "player2");
////		
//		String board = game.getStringBoard();
//////		
//		System.out.println(board);
////		
////		String expected = "    |    | \n" 
////		                + "---+---+---\n" 
////				        + "    |    | \n" 
////		                + "---+---+---\n" 
////				        + "    |    | \n";
//////		
////		System.out.println(expected);
////		System.out.println(expected.equals(board));
//		
//		
//		
//		
//		
//		
//		String result = "";
//		
//		while(true)
//		{
//			System.out.println("Enter a position on the board to place your piece (1-9): ");
//			int index1 = position.nextInt();
//			
//			while(game.checkifPlayed(index1))
//			{
//				System.out.println("Position already played. Choose another position:");
//				index1 = position.nextInt();
//			}
//			
//			game.placeSign(index1, player1);
//			
//			System.out.println(game.getStringBoard());
//			
//			result = game.checkWinner();
//			
//			if (!result.equals(""))
//			{
//				if (result.equals("tie"))
//				{
//					System.out.println("the game has resulted in a tie");
//					game.resetBoard();
//					break;
//				}
//				
//				else 
//				{
//					System.out.println("The winner is: " + result);
//					game.resetBoard();
//					break;
//				}
//		
//			}
//			
//			
//			System.out.println("Enter a position on the board to place your piece (1-9): ");
//			int index2 = position.nextInt();
//			
//			while(game.checkifPlayed(index2))
//			{
//				System.out.println("Position already played. Choose another position:");
//				index2 = position.nextInt();
//			}
//			
//			game.placeSign(index2, player2);
//			
//			System.out.println(game.getStringBoard());
//			
//			result = game.checkWinner();
//			
//			if (!result.equals(""))
//			{
//				System.out.println("The winner is: " + result);
//				game.resetBoard();
//				break;
//			}
//			
//			
//			
//		}
//		
//		players.close();
//		position.close();
//		
//		
//	}

}
