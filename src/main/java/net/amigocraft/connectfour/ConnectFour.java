package net.amigocraft.connectfour;

import org.fusesource.jansi.AnsiConsole;

import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * A simple command line Connect Four app.
 * @author Maxim Roncace
 * @version 1.0.2
 */
public class ConnectFour {

	public static final String ANSI_RED = "\u001B[31;1m";
	public static final String ANSI_YELLOW = "\u001B[33;1m";
	public static final String ANSI_CYAN = "\u001B[36;1m";
	public static final String ANSI_WHITE = "\u001B[37;1m";

	private static int[][] pieces = new int[7][6];
	private static boolean turn = true;
	private static Scanner sc = new Scanner(System.in);

	/**
	 * Initializes and runs the main game loop
	 */
	public static void initLoop(){ // initialize the game loop
		for (int[] row : pieces)
			Arrays.fill(row, 0); // fill the piece array
		printBoard(); // render the board
		while (calcWinner() == 0){ // while no one has one yet
			AnsiConsole.out.println((turn ? ANSI_RED + "Player 1," : ANSI_CYAN + "Player 2,") + ANSI_WHITE +
					" it is your turn.");
			// notify whichever player that it's their turn
			int col; // init so we can catch any user-triggered exceptions
			try {
				col = sc.nextInt() + 1;
			}
			catch (InputMismatchException ex){ // they entered a non-integer
				AnsiConsole.out.println("Invalid input!");
				continue;
			}
			catch (NoSuchElementException ex){ // the program is being terminated
				break; // clean termination
			}
			if (col < 1 || col >= pieces.length){ // piece is out of range
				AnsiConsole.out.println("Invalid column!");
				continue; // player retains turn
			}
			boolean placed = false; // whether the piece has been placed yet
			for (int i = 0; i < pieces[col].length; i++){ // if this loop finishes the column is full
				if (pieces[col][i] == 0){ // if there is no piece present yet
					pieces[col][i] = turn ? 1 : 2; // place it for whichever player
					placed = true; // piece has been placed
					break; // no need to continue with the loop, that would screw things up a lot
				}
			}
			if (!placed){ // could not place piece
				AnsiConsole.out.println("Column is full!"); // notify
				continue; // player retains turn
			}
			printBoard(); // render the board now that a piece has been placed
			int winner = calcWinner(); // call the method to calculate a potential winner
			if (winner > 0){ // someone has won
				AnsiConsole.out.println((winner == 1 ? ANSI_RED : ANSI_CYAN) + "Player " + winner + ANSI_WHITE +
						" wins!"); // notify
				try {
					Object obj = new Object(); // exclusively for hanging the thread
					synchronized (obj){ // required to obtain object lock
						obj.wait(3000L); // wait 3 seconds before exiting
					}
				}
				catch (InterruptedException ex){
				} // oh, well
				return; // no need to continue
			}
			turn = !turn; // pass the turn to the other player
		}
	}

	/**
	 * Prints the board in its current state to the console.
	 */
	public static void printBoard(){
		// print the column numbers
		AnsiConsole.out.print(ANSI_WHITE + "( ");
		for (int i = 0; i < pieces.length; i++)
			AnsiConsole.out.print((i + 1) + " ");
		AnsiConsole.out.print(")");
		AnsiConsole.out.println(); // spacer
		for (int j = pieces[0].length - 1; j >= 0; j--){ // iterate rows
			AnsiConsole.out.print(ANSI_YELLOW + "| "); // edge of board
			for (int i = 0; i < pieces.length; i++){ // iterate colums
				int res = pieces[i][j]; // get piece at index
				AnsiConsole.out.print((res == 0 ?
						ANSI_YELLOW + "O" :
						(res == 1 ? ANSI_RED : ANSI_CYAN)
								+ res + ANSI_WHITE) + " ");
			}
			AnsiConsole.out.print(ANSI_YELLOW + "|"); // edge of board
			AnsiConsole.out.println(); // spacer
		}
	}

	/**
	 * Determines whether a player has won the game.
	 * @return the number of the player who has won, or 0 if a winner cannot be determined.
	 */
	public static int calcWinner(){
		//AnsiConsole.out.println("All: " + checkCols() + ", " + checkRows() + ", " + checkDiags()); // debug
		return Math.max(checkCols(), Math.max(checkRows(), checkDiags())); // check all possible arrangements
	}

	/**
	 * Determines whether a player has won vertically.
	 * @return the number of the player who has won, or 0 if a winner cannot be determined.
	 */
	private static int checkCols(){ // checks for vertical win
		for (int i = 0; i < pieces.length; i++){ // iterate columns
			int streak = 0; // current consecutive piece streak
			int last = 0; // last piece color
			for (int j = 0; j < pieces[i].length; j++){ // iterate vertically through rows
				if (last != 0){ // there was a piece
					if (pieces[i][j] == last){ // streak continues
						if (streak == 3){ // this is the fourth in a row
							//System.out.println("Col: " + i + ", " + j);
							return pieces[i][j]; // return the winner
						}
						else
							streak += 1; // streak continues
					}
					else {
						streak = 1; // new streak
						last = pieces[i][j];
					}
				}
				else {
					last = pieces[i][j];
					streak = last == 0 ? 0 : 1; // reset streak
				}
			}
		}
		return 0; // no winner
	}

	/**
	 * Determines whether a player has won horizontally.
	 * @return the number of the player who has won, or 0 if a winner cannot be determined.
	 */
	private static int checkRows(){ // checks rows for horizontal victory
		for (int j = 0; j < pieces[0].length; j++){ // iterate rows
			int last = 0;
			int streak = 0;
			for (int i = 0; i < pieces.length; i++){ // iterate columns
				if (last != 0){
					if (pieces[i][j] == last){
						if (streak == 3){ // this is the fourth in a row
							//System.out.println("Row: " + i + ", " + j);
							return pieces[i][j]; // return the winner
						}
						else
							streak += 1; // streak continues
					}
					else {
						streak = 1; // new streak
						last = pieces[i][j];
					}
				}
				else {
					last = pieces[i][j];
					streak = last == 0 ? 0 : 1; // reset streak
				}
			}
		}
		return 0;
	}

	/**
	 * Determines whether a player has won diagonally.
	 * @return the number of the player who has won, or 0 if a winner cannot be determined.
	 */
	private static int checkDiags(){
		return Math.max(checkUpDiags(), checkDownDiags());
	}

	/**
	 * Determines whether a player has won diagonally, going up and right.
	 * @return the number of the player who has won, or 0 if a winner cannot be determined.
	 */
	private static int checkUpDiags(){ // checks for diagonal victory going right and up
		for (int i = 0; i < pieces.length - 3; i++){ // iterate rows
			genLoop:
			for (int j = 0; j < pieces[i].length - 3; j++){ // iterate columns
				int prec = pieces[i][j];
				if (prec == 0)
					continue;
				for (int off = 1; off <= 3; off++){ // check for diagonal 4
					if (pieces[i + off][j + off] != prec)
						continue genLoop; // no victory
				}
				//AnsiConsole.out.println("Up diag: " + i + ", " + j);
				return prec; // return winner
			}
		}
		return 0; // no winner
	}

	/**
	 * Determines whether a player has won diagonally, going down and right.
	 * @return the number of the player who has won, or 0 if a winner cannot be determined.
	 */
	private static int checkDownDiags(){ // checks for diagonal victory going right and down
		for (int i = 0; i < pieces.length - 3; i++){ // iterate columns
			genLoop:
			for (int j = pieces[i].length - 1; j >= 3; j--){ // iterate rows
				int prec = pieces[i][j];
				if (prec == 0)
					continue;
				for (int off = 1; off <= 3; off++){ // check for diagonal 4
					if (pieces[i + off][j - off] != prec)
						continue genLoop; // no victory
				}
				//AnsiConsole.out.println("Down diag: " + i + ", " + j);
				return prec; // return winner
			}
		}
		return 0; // no winner
	}
}
