
/*
 * 
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class a2048 {
	private Tile[] myTiles;
	boolean myWin = false;
	boolean myLose = false;
	int myScore = 0;
	int myTarget = 0;
	

	public a2048() {
		resetGame();
	}

	public boolean isPowerOfTwo(int input) {
		int sum = 0;
		while(input != 0) {
			if((input & 1) == 1) {
				sum++;
			}
			input >>= 1;
		}
		return sum==1;
	}
	
	public void resetGame() {
		myScore = 0;
		@SuppressWarnings("resource")
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Please set your target number:");
		String inStr = keyboard.nextLine();
		while(true) {
		while(!inStr.matches("[1234567890]+")) {
			System.out.println("Invalid Input (Must be a number). Please reset your target number:");
			inStr = keyboard.nextLine();
		}
		int input=Integer.parseInt(inStr);
		while (myTarget == 0) {
			if (input > 4 && isPowerOfTwo(input)) {
				myTarget = input;
				break;
			} else {
				System.out.println("Invalid Input (Must include a power of 2 & bigger than 4. Please reset your target number:");
				input = keyboard.nextInt();
			}
		}break;
		}
		myWin = false;
		myLose = false;
		myTiles = new Tile[4 * 4];
		for (int i = 0; i < myTiles.length; i++) {
			myTiles[i] = new Tile();
		}

		addTile();
		addTile();
	}

	public void left() {
		boolean needAddTile = false;
		for (int i = 0; i < 4; i++) {
			Tile[] line = getLine(i);
			Tile[] merged = mergeLine(moveLine(line));
			setLine(i, merged);
			if (!needAddTile && !compare(line, merged)) {
				needAddTile = true;
			}
		}

		if (needAddTile) {
			addTile();
		}
	}

	public void right() {
		myTiles = rotate(180);
		left();
		myTiles = rotate(180);
	}

	public void up() {
		myTiles = rotate(270);
		left();
		myTiles = rotate(90);
	}

	public void down() {
		myTiles = rotate(90);
		left();
		myTiles = rotate(270);
	}

	private Tile tileAt(int x, int y) {
		return myTiles[x + y * 4];
	}

	private void addTile() {
		List<Tile> list = availableSpace();
		if (!availableSpace().isEmpty()) {
			int index = (int) (Math.random() * list.size()) % list.size();
			Tile emptyTime = list.get(index);
			emptyTime.value = Math.random() < 0.9 ? 2 : 4;
		}
	}

	private List<Tile> availableSpace() {
		final List<Tile> list = new ArrayList<Tile>(16);
		for (Tile t : myTiles) {
			if (t.isEmpty()) {
				list.add(t);
			}
		}
		return list;
	}

	private boolean isFull() {
		return availableSpace().size() == 0;
	}

	boolean canMove() {
		if (!isFull()) {
			return true;
		}
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				Tile t = tileAt(x, y);
				if ((x < 3 && t.value == tileAt(x + 1, y).value) || ((y < 3) && t.value == tileAt(x, y + 1).value)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean compare(Tile[] line1, Tile[] line2) {
		if (line1 == line2) {
			return true;
		} else if (line1.length != line2.length) {
			return false;
		}

		for (int i = 0; i < line1.length; i++) {
			if (line1[i].value != line2[i].value) {
				return false;
			}
		}
		return true;
	}

	private Tile[] rotate(int angle) {
		Tile[] newTiles = new Tile[4 * 4];
		int offsetX = 3, offsetY = 3;
		if (angle == 90) {
			offsetY = 0;
		} else if (angle == 270) {
			offsetX = 0;
		}

		double rad = Math.toRadians(angle);
		int cos = (int) Math.cos(rad);
		int sin = (int) Math.sin(rad);
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 4; y++) {
				int newX = (x * cos) - (y * sin) + offsetX;
				int newY = (x * sin) + (y * cos) + offsetY;
				newTiles[(newX) + (newY) * 4] = tileAt(x, y);
			}
		}
		return newTiles;
	}

	private Tile[] moveLine(Tile[] oldLine) {
		LinkedList<Tile> l = new LinkedList<Tile>();
		for (int i = 0; i < 4; i++) {
			if (!oldLine[i].isEmpty())
				l.addLast(oldLine[i]);
		}
		if (l.size() == 0) {
			return oldLine;
		} else {
			Tile[] newLine = new Tile[4];
			ensureSize(l, 4);
			for (int i = 0; i < 4; i++) {
				newLine[i] = l.removeFirst();
			}
			return newLine;
		}
	}

	private Tile[] mergeLine(Tile[] oldLine) {
		LinkedList<Tile> list = new LinkedList<Tile>();
		for (int i = 0; i < 4 && !oldLine[i].isEmpty(); i++) {
			int num = oldLine[i].value;
			if (i < 3 && oldLine[i].value == oldLine[i + 1].value) {
				num *= 2;
				myScore += num;
				if (num == myTarget) {
					myWin = true;
				}
				i++;
			}
			list.add(new Tile(num));
		}
		if (list.size() == 0) {
			return oldLine;
		} else {
			ensureSize(list, 4);
			return list.toArray(new Tile[4]);
		}
	}

	private static void ensureSize(java.util.List<Tile> l, int s) {
		while (l.size() != s) {
			l.add(new Tile());
		}
	}

	private Tile[] getLine(int index) {
		Tile[] result = new Tile[4];
		for (int i = 0; i < 4; i++) {
			result[i] = tileAt(i, index);
		}
		return result;
	}

	private void setLine(int index, Tile[] re) {
		System.arraycopy(re, 0, myTiles, index * 4, 4);
	}

	static class Tile {
		int value;

		public Tile() {
			this(0);
		}

		public Tile(int num) {
			value = num;
		}

		public boolean isEmpty() {
			return value == 0;
		}
	}

	public void printGameBoard() {
		for (int i = 0; i < myTiles.length; i++) {
			System.out.print(myTiles[i].value + "\t");
			if ((i + 1) % 4 == 0) {
				System.out.println();
			}
		}
	}

	public static Tile[] copy(final Tile[] oldTile) {
		Tile[] newTile = new Tile[16];
		for (int i = 0; i < newTile.length; i++) {
			newTile[i] = new Tile(oldTile[i].value);
		}
		return newTile;

	}

	public static void main(String[] args) {
		a2048 game = new a2048();
		Tile[] lastTiles = copy(game.myTiles);
		game.printGameBoard();
		@SuppressWarnings("resource")
		Scanner keyboard1 = new Scanner(System.in);
		while (!game.myWin && !game.myLose) {
			char input = keyboard1.nextLine().charAt(0);
			if (!game.canMove()) {
				game.myLose = true;
			}
			switch (input) {
			case 'a':
				lastTiles = copy(game.myTiles);
				game.left();
				break;
			case 'd':
				lastTiles = copy(game.myTiles);
				game.right();

				break;
			case 's':
				lastTiles = copy(game.myTiles);
				game.down();

				break;
			case 'w':
				lastTiles = copy(game.myTiles);
				game.up();

				break;
			case 'r':
				lastTiles = copy(game.myTiles);
				game.resetGame();

				break;
			case 'c':
				game.myTiles = copy(lastTiles);
				break;

			}

			if (!game.myWin && !game.canMove()) {
				game.myLose = true;
			}
			game.printGameBoard();
		}
		if (game.myWin) {
			System.out.println("You win! Your score is " + game.myScore + ".");
		}
		if (game.myLose) {
			System.out.println("You lose! Your score is " + game.myScore + ".");
		}

	}
}