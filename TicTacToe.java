import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TicTacToe {
	
	private String[][] gameBoard = {{" ", "   |", " ", "   |", " "}, 
			{"---", "+", "---", "+", "---"}, 
			{" ", "   |", " ", "   |", " "},
			{"---", "+", "---", "+", "---"}, 
			{" ", "   |", " ", "   |", " "}};
	
	private ArrayList<Integer> playerPositions = new ArrayList<Integer>();
	private ArrayList<Integer> cpuPositions = new ArrayList<Integer>();
	
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
	
	public void placeX(int index, String user)
	{
		
		String sign = "";
		
		if (user.equals("player"))
		{
			sign = "X";
			playerPositions.add(index);
		}
		
		else sign = "O";
		
		if (user.equals("cpu"))
		{
			Random rand = new Random();
			index = rand.nextInt(9) + 1;
			while (cpuPositions.contains(index) || playerPositions.contains(index))
			{
				index = rand.nextInt(9) + 1;
			}
//			index = rand.nextInt(9) + 1;
			cpuPositions.add(index);
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
			if (playerPositions.containsAll(l))
			{
				return "player";
			}
			
			else if (cpuPositions.containsAll(l))
			{
				return "cpu";
			}
			
			else if (playerPositions.size() + cpuPositions.size() == 9)
			{
				return "tie";
			}
		}
		
		return "";
	}
	
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
		
		playerPositions.clear();
		cpuPositions.clear();
	}
	
	
	
//	public static void main(String[] args)
//	{
//		TicTacToe game = new TicTacToe();
//		System.out.println(game.getBoardSection(0));
//		System.out.println(game.getBoardSection(1));
//		System.out.println(game.getBoardSection(2));
//		System.out.println(game.getBoardSection(3));
//		System.out.println(game.getBoardSection(4));
//		
//		
//		game.placeX(4, "player");
//		game.placeX(1, "cpu");
//			
//		System.out.println(" ");
//	
//		System.out.println(game.getBoardSection(0));
//		System.out.println(game.getBoardSection(1));
//		System.out.println(game.getBoardSection(2));
//		System.out.println(game.getBoardSection(3));
//		System.out.println(game.getBoardSection(4));
//		
//		game.resetBoard();
//		
//		System.out.println(game.getBoardSection(0));
//		System.out.println(game.getBoardSection(1));
//		System.out.println(game.getBoardSection(2));
//		System.out.println(game.getBoardSection(3));
//		System.out.println(game.getBoardSection(4));
//		
//	}

}
