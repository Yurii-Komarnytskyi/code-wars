package kyu5;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;




class SimpleEncryptionQwerty {
	// https://www.codewars.com/SimpleEncryptionQwerty/57f14afa5f2f226d7d0000f4/train/java

	private static Map<Integer, String> qwertyRows = Stream
			.of(new AbstractMap.SimpleEntry<>(0, "qwertyuiop"), new AbstractMap.SimpleEntry<>(1, "asdfghjkl"),
					new AbstractMap.SimpleEntry<>(2, "zxcvbnm,."))
			.collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

	private static Map<Integer, String> qwertyRowsShiftOn = Stream
			.of(new AbstractMap.SimpleEntry<>(0, "QWERTYUIOP"), new AbstractMap.SimpleEntry<>(1, "ASDFGHJKL"),
					new AbstractMap.SimpleEntry<>(2, "ZXCVBNM<>"))
			.collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

	public static String encrypt(String text, int key) {
		if (key == 0) {
			return text;
		}

		StringBuilder result = new StringBuilder();
		List<Integer> encryptionKeys = decryptRowKeys(key);
		IntStream.rangeClosed(0, text.length() - 1).forEach(i -> {
			String currentLetter = String.valueOf(text.charAt(i));
			if (currentLetter.isBlank()) {
				result.append(currentLetter);
			} else {
				Entry<Integer, String> rowEntry = findApropriateEntry(currentLetter);
				String row = rowEntry.getValue();
				char encryptedLetter = row.charAt(pickNextEncryptedIndex(row.indexOf(currentLetter),
						encryptionKeys.get(rowEntry.getKey()), row.length() - 1));
				result.append(encryptedLetter);
			}
		});
		return result.toString();
	}

	public static String decrypt(String encryptedText, int key) {
		if (key == 0) {
			return encryptedText;
		}

		StringBuilder result = new StringBuilder();
		List<Integer> encryptionKeys = decryptRowKeys(key);
		IntStream.rangeClosed(0, encryptedText.length() - 1).forEach(i -> {
			String currentLetter = String.valueOf(encryptedText.charAt(i));
			if (currentLetter.isBlank()) {
				result.append(currentLetter);
			} else {
				Entry<Integer, String> rowEntry = findApropriateEntry(currentLetter);
				String row = rowEntry.getValue();
				char encryptedLetter = row.charAt(pickNextDecryptedIndex(row.indexOf(currentLetter),
						encryptionKeys.get(rowEntry.getKey()), row.length() - 1));
				result.append(encryptedLetter);
			}
		});
		return result.toString();
	}

	private static int pickNextEncryptedIndex(int qwertyIndex, int shiftToRight, int rowLength) {
		boolean overlap = (qwertyIndex + shiftToRight) > rowLength;
		if (overlap) {
			return ((qwertyIndex + shiftToRight) - rowLength) - 1;
		}
		return qwertyIndex + shiftToRight;
	}

	private static int pickNextDecryptedIndex(int qwertyIndex, int shiftToLeft, int rowLength) {
		boolean overlap = (qwertyIndex - shiftToLeft) < 0;
		if (overlap) {
			return rowLength - shiftToLeft + qwertyIndex + 1;
		}
		return Math.abs(qwertyIndex - shiftToLeft);
	}

	private static Entry<Integer, String> findApropriateEntry(String letter) {
		if (qwertyRows.values().stream().anyMatch(values -> values.contains(letter))) {
			return qwertyRows.entrySet().stream().filter(entry -> entry.getValue().contains(letter)).findFirst().get();
		} else {
			return qwertyRowsShiftOn.entrySet().stream().filter(entry -> entry.getValue().contains(letter)).findFirst()
					.orElse(new AbstractMap.SimpleEntry<>(0, String.join("", Collections.nCopies(10, letter))));
		}
	}

	private static List<Integer> decryptRowKeys(int key) {
		return Arrays.stream(String.valueOf(key).split("")).mapToInt(Integer::valueOf).boxed()
				.collect(Collectors.toList());
	}

}

class SumOfK { // NEEDS SOME POLISH

	// https://www.codewars.com/kata/55e7280b40e1c4a06d0000aa/train/java

	private static List<int[]> allPossibleTownsCombinations = new ArrayList<>();

	public static Integer chooseBestSum(int maxDistanceCovered, int townsToVisit, List<Integer> distancesBetweenTowns) {
		if (distancesBetweenTowns.size() < townsToVisit) {
			return null;
		}
		combinations(distancesBetweenTowns, townsToVisit, 0, new int[townsToVisit]);
		OptionalInt result = allPossibleTownsCombinations.stream()
				.mapToInt(townCombinations -> Arrays.stream(townCombinations).sum())
				.filter(distanceToCover -> distanceToCover <= maxDistanceCovered).max();
		return (result.isPresent()) ? result.getAsInt() : null;
	}

	static void combinations(List<Integer> distancesBetweenTowns, int townsToVisit, int startPosition, int[] result) {
		if (townsToVisit == 0) {
			String resultToString = Arrays.toString(result);
			allPossibleTownsCombinations
					.add(Arrays.stream(resultToString.substring(1, resultToString.length() - 1).split(", "))
							.mapToInt(Integer::valueOf).toArray());
			return;
		}
		for (int i = startPosition; i <= distancesBetweenTowns.size() - townsToVisit; i++) {
			result[result.length - townsToVisit] = distancesBetweenTowns.get(i);
			combinations(distancesBetweenTowns, townsToVisit - 1, i + 1, result);
		}
	}
}

class SnakesLadders {
	// https://www.codewars.com/kata/587136ba2eefcb92a9000027/train/java

	private List<Player> players = new ArrayList<>();
	private List<Lift> lifts = new ArrayList<>();
	private int indexOfActivePlayer = 0;
	private final int FINISH_SQUARE = 100;
	private boolean gameOver = false;

	public SnakesLadders() {
		players.add(new Player(1));
		players.add(new Player(2));

		lifts.addAll(List.of(new Lift(2, 38), new Lift(7, 14), new Lift(8, 31), new Lift(15, 26), new Lift(16, 6),
				new Lift(21, 42), new Lift(28, 84), new Lift(36, 44), new Lift(46, 25), new Lift(49, 11),
				new Lift(51, 67), new Lift(62, 19), new Lift(64, 60), new Lift(71, 91), new Lift(74, 53),
				new Lift(78, 98), new Lift(89, 68), new Lift(87, 94), new Lift(92, 88), new Lift(95, 75),
				new Lift(99, 80)));
	}

	public String play(int diceOne, int diceTwo) {
		if (gameOver) {
			return "Game over!";
		} else {
			Player activePlayer = players.get(indexOfActivePlayer);

			activePlayer.updatePositionOnBoard(diceOne + diceTwo);
			updIndexOfActivePlayer(diceOne == diceTwo);

			if (activePlayer.positionOnBoard == FINISH_SQUARE) {
				gameOver = true;
			}

			return activePlayer.toString();
		}
	}

	void updIndexOfActivePlayer(boolean diceAreDouble) {
		if (!diceAreDouble) {
			indexOfActivePlayer = (indexOfActivePlayer < players.size() - 1) ? ++indexOfActivePlayer : 0;
		}
	}

	class Player {

		private int playerId;
		private int positionOnBoard = 0;

		public Player(int playerId) {
			this.playerId = playerId;
		}

		void updatePositionOnBoard(int diceSum) {
			positionOnBoard += diceSum;
			if (positionOnBoard > FINISH_SQUARE) {
				bounceOff();
			}
			if (lifts.stream().anyMatch(lift -> lift.getOrigin() == positionOnBoard)) {
				positionOnBoard = lifts.stream().filter(l -> l.getOrigin() == positionOnBoard).findFirst().get()
						.getDestination();
			}
		}

		void bounceOff() {
			positionOnBoard = FINISH_SQUARE - (positionOnBoard - FINISH_SQUARE);
		}

		@Override
		public String toString() {
			if (this.positionOnBoard == FINISH_SQUARE) {
				return String.format("Player %d Wins!", playerId);
			}
			return String.format("Player %d is on square %d", playerId, positionOnBoard);
		}
	}

	class Lift {
		private int positionA;
		private int positionB;

		Lift(int positionA, int positionB) {
			this.positionA = positionA;
			this.positionB = positionB;
		}

		int getOrigin() {
			return positionA;
		}

		int getDestination() {
			return positionB;
		}

	}

}

class Connect4 {
	// https://www.codewars.com/kata/586c0909c1923fdb89002031/train/java

	private final List<List<String>> theBoard = new ArrayList<>();
	private final List<Player> players = new ArrayList<>();
	List<String> currentColumn;
	private Player activePlayer;
	private boolean gameOver = false;
	private final String YELLOW = "y";
	private final String GREEN = "g";
	private final String BLANK = " ";
	private final String COLUMN_FULL = "Column full!";
	private final int WINNING_STREAK = 4;
	private final int AMOUNT_OF_COLUMNS = 7;
	private final int COLUMN_SIZE = 6;

	private class Player {

		private int playerId;
		private String discColor;
		private boolean isActive = false;

		public Player(int playerId, String discColor) {
			this.playerId = playerId;
			this.discColor = discColor;
		}

		private String dropADisc() {
			int blankIndex = currentColumn.indexOf(BLANK);
			if (blankIndex == -1) {
				return COLUMN_FULL;
			}
			currentColumn.set(blankIndex, activePlayer.discColor);
			return String.format("Player %d has a turn", playerId);
		}

		private void reverseIsActiveField() {
			isActive = !isActive;
		}

	}

	public Connect4() {
		players.add(new Player(1, YELLOW));
		players.add(new Player(2, GREEN));
		activePlayer = players.get(0);
		activePlayer.reverseIsActiveField();

		IntStream.range(0, AMOUNT_OF_COLUMNS).forEach(column -> {
			theBoard.add(column, new ArrayList<>());
			IntStream.range(0, COLUMN_SIZE).forEach(row -> {
				theBoard.get(column).add(BLANK);
			});
		});

	}

	public String play(int column) {
		if (gameOver) {
			return "Game has finished!";
		}
		currentColumn = theBoard.get(column);
		String resultOfATurn = activePlayer.dropADisc();

		if (checkIfActivePlayerHasWon()) {
			gameOver = true;
			return String.format("Player %d wins!", activePlayer.playerId);
		} else {
			activePlayer = updActivePlayer(resultOfATurn);
			return resultOfATurn;
		}

	}

	Player updActivePlayer(String playersResult) {
		if (!playersResult.equals(COLUMN_FULL)) {
			players.stream().forEach(Player::reverseIsActiveField); 
			return players.stream().filter(player -> player.isActive == true).findFirst().get();
		} else {
			return activePlayer;
		}
	}

	private boolean checkIfActivePlayerHasWon() {
		return checkTheBoardVertically() || checkTheBoardHorizontally() || checkTheBoardDiagonally();
	}

	private boolean checkTheBoardVertically() {
		return checkForFourConsecutiveDiscs(currentColumn);
	}

	private boolean checkTheBoardHorizontally() {
		int horizontalIndex = currentColumn.lastIndexOf(activePlayer.discColor);
		return checkForFourConsecutiveDiscs(theBoard.stream().map(column -> column.get(horizontalIndex)).toList());
	}

	private boolean checkTheBoardDiagonally() {
		List<String> diagonalToTheLeft = new ArrayList<>();
		List<String> bottomToTop = new ArrayList<>();
		int horizontalIndex = currentColumn.lastIndexOf(activePlayer.discColor);
		int currentColumnIndex = theBoard.indexOf(currentColumn);
		
		{	// BOTTOM TO TOP
			int topRight = (currentColumnIndex + ((COLUMN_SIZE-1) - horizontalIndex) > AMOUNT_OF_COLUMNS-1)
					? AMOUNT_OF_COLUMNS-1
					: currentColumnIndex + ((COLUMN_SIZE-1) - horizontalIndex);
			int bottomLeft = (currentColumnIndex - horizontalIndex < 0) ? 0
					: currentColumnIndex - horizontalIndex;
			int discIndex = 0;
			for(int col = bottomLeft; col <= topRight; col++) {
				bottomToTop.add(theBoard.get(col).get(discIndex));
				discIndex++;
			}
		}
		
		{	// TOP TO BOTTOM
			int bottomRight = (currentColumnIndex + horizontalIndex > AMOUNT_OF_COLUMNS-1) 
					? AMOUNT_OF_COLUMNS - 1
					: currentColumnIndex + horizontalIndex;
			int topLeft = (currentColumnIndex - ((COLUMN_SIZE-1) - horizontalIndex) < 0) ? 0
					: currentColumnIndex - ((COLUMN_SIZE-1) - horizontalIndex);
			int discIndex = 0;
			for(int col = bottomRight; col >= topLeft; col--) {
				diagonalToTheLeft.add(theBoard.get(col).get(discIndex));
				discIndex++;
			}
		}
		return checkForFourConsecutiveDiscs(bottomToTop) || checkForFourConsecutiveDiscs(diagonalToTheLeft);
	}

	private boolean checkForFourConsecutiveDiscs(List<String> list) {
		int numberOfConsecutiveDiscs = 0;
		for (String disc : list) {
			if (disc.equals(activePlayer.discColor)) {
				++numberOfConsecutiveDiscs;
				if (numberOfConsecutiveDiscs == WINNING_STREAK) {
					break;
				}
			} else {
				numberOfConsecutiveDiscs = 0;
			}
		}
		return numberOfConsecutiveDiscs == WINNING_STREAK;
	}
}


public class KyuFive {

	public static void main(String[] args) {
		Connect4 t = new Connect4();
	}

}
