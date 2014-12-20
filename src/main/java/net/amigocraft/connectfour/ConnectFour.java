package net.amigocraft.connectfour;

import org.fusesource.jansi.AnsiConsole;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConnectFour {

	public static final String ANSI_RED = "\u001B[31;1m";
	public static final String ANSI_YELLOW = "\u001B[33;1m";
	public static final String ANSI_CYAN = "\u001B[36;1m";
	public static final String ANSI_WHITE = "\u001B[37;1m";

	private static int[][] pieces = new int[7][6];
	private static boolean turn = true;
	private static Scanner sc = new Scanner(System.in);

	public static void main(String[] args){

		for (int[] row : pieces)
			Arrays.fill(row, 0);

		initLoop();

	}

	public static void initLoop(){
		printBoard();
		while (calcWinner() == 0){
			AnsiConsole.out.println((turn ? ANSI_RED + "Player 1," : ANSI_CYAN + "Player 2,") + ANSI_WHITE + " it is your turn.");
			int col;
			try {
				col = sc.nextInt() - 1;
			}
			catch (InputMismatchException ex){
				AnsiConsole.out.println("Invalid input!");
				continue;
			}
			catch (NoSuchElementException ex){
				break; // clean termination
			}
			if (col >= pieces.length){
				AnsiConsole.out.println("Invalid column!");
				continue;
			}
			boolean placed = false;
			for (int i = 0; i < pieces[col].length; i++){
				if (pieces[col][i] == 0){
					pieces[col][i] = turn ? 1 : 2;
					placed = true;
					break;
				}
			}
			if (!placed){
				AnsiConsole.out.println("Column is full!");
				continue;
			}
			printBoard();
			int winner = calcWinner();
			if (winner > 0){
				AnsiConsole.out.println((winner == 1 ? ANSI_RED : ANSI_CYAN) + "Player " + winner + ANSI_WHITE + " wins!");
				try {
					Object obj = new Object();
					synchronized(obj){
						obj.wait(3000L);
					}
				}
				catch (InterruptedException ex){} // oh, well
				return;
			}
			turn = !turn;
		}
	}

	public static void printBoard(){
		AnsiConsole.out.print(ANSI_WHITE + "( ");
		for (int i = 0; i < pieces.length; i++)
			AnsiConsole.out.print((i + 1) + " ");
		AnsiConsole.out.print(")");
		AnsiConsole.out.println();
		for (int j = pieces[0].length - 1; j >= 0; j--){
			AnsiConsole.out.print(ANSI_YELLOW + "| ");
			for (int i = 0; i < pieces.length; i++){
				int res = pieces[i][j];
				AnsiConsole.out.print((res == 0 ? ANSI_YELLOW + "O" : (res == 1 ? ANSI_RED : ANSI_CYAN) + res + ANSI_WHITE) + " ");
			}
			AnsiConsole.out.print(ANSI_YELLOW + "|");
			AnsiConsole.out.println();
		}
	}

	public static int calcWinner(){
		//AnsiConsole.out.println("All: " + checkCols() + ", " + checkRows() + ", " + checkDiags());
		return Math.max(checkCols(), Math.max(checkRows(), checkDiags()));
	}

	private static int checkCols(){
		for (int i = 0; i < pieces.length; i++){
			int streak = 0;
			int last = 0;
			for (int j = 0; j < pieces[i].length; j++){
				if (last != 0){
					if (pieces[i][j] == last){
						if (streak == 3){
							//System.out.println("Col: " + i + ", " + j);
							return pieces[i][j];
						}
						else
							streak += 1;
					}
					else {
						streak = 1;
						last = pieces[i][j];
					}
				}
				else {
					last = pieces[i][j];
					streak = last == 0 ? 0 : 1;
				}
			}
		}
		return 0;
	}

	private static int checkRows(){
		for (int j = 0; j < pieces[0].length; j++){
			int last = 0;
			int streak = 0;
			for (int i = 0; i < pieces.length; i++){
				if (last != 0){
					if (pieces[i][j] == last){
						if (streak == 3){
							//System.out.println("Row: " + i + ", " + j);
							return pieces[i][j];
						}
						else
							streak += 1;
					}
					else {
						streak = 1;
						last = pieces[i][j];
					}
				}
				else {
					last = pieces[i][j];
					streak = last == 0 ? 0 : 1;
				}
			}
		}
		return 0;
	}

	private static int checkDiags(){
		return Math.max(checkUpDiags(), checkDownDiags());
	}

	private static int checkUpDiags(){
		for (int i = 0; i < pieces.length - 3; i++){
			genLoop:
			for (int j = 0; j < pieces[i].length - 3; j++){
				int prec = pieces[i][j];
				if (prec == 0)
					continue genLoop;
				for (int off = 1; off <= 3; off++){
					if (pieces[i + off][j + off] != prec)
						continue genLoop;
				}
				//AnsiConsole.out.println("Up diag: " + i + ", " + j);
				return prec;
			}
		}
		return 0;
	}

	private static int checkDownDiags(){
		for (int i = 0; i < pieces.length - 3; i++){
			genLoop:
			for (int j = pieces[i].length - 1; j >= 3; j--){
				int prec = pieces[i][j];
				if (prec == 0)
					continue genLoop;
				for (int off = 1; off <= 3; off++){
					if (pieces[i + off][j - off] != prec)
						continue genLoop;
				}
				//AnsiConsole.out.println("Down diag: " + i + ", " + j);
				return prec;
			}
		}
		return 0;
	}

}
