package chessClasses;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import chessClasses.pieces.ChessPiece;


public class Position{
	public static final char[] LETTERS = new char[] {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
	public static final char[] NUMBERS = new char[] {'1', '2', '3', '4', '5', '6', '7', '8'};
	
	public static final String UP = "up";
	public static final String DOWN = "down";
	public static final String LEFT = "left";
	public static final String RIGHT = "right";
	public static final String UPRIGHT = "upright";
	public static final String UPLEFT = "upleft";
	public static final String DOWNRIGHT = "downright";
	public static final String DOWNLEFT = "downleft";
	
	
	/** Method that advances the positionCharacter by the specified jump amount, for example if passed the character 'a' and a jump amount of 1, it returns 'b'
	 * 
	 * @param postion The starting point
	 * @param jumpAmount The amount to jump, can also be negative
	 * @param mode 'a' for letter mode and '1' for number mode
	 * @return The result position or null if it does not exist
	 * @throws IndexOutOfBoundsException If the position you are trying to jump to does not exist
	 */
	public Position jump(int jumpAmount, char mode) throws IndexOutOfBoundsException{
		Character resultChar = null;
		
		for(int i = 0; i<8; i++) {
			if (mode == 'a') {
				if(LETTERS[i] == this.letter) {
					resultChar = LETTERS[i + jumpAmount];
					return new Position(resultChar, this.number);
				}
			}else if(mode == '1') {
				if (NUMBERS[i] == this.number) {
					resultChar = NUMBERS[i + jumpAmount];
					return new Position(this.letter, resultChar);
				}
			}else {
				throw new InvalidParameterException("Mode values can only be 'a' for letter mode or '1' for number mode");
			}
		}
		return null;
	}
	
	/** Method that gets how much gap there is between two letters or numbers
	 * 
	 * @param char1
	 * @param char2
	 * @return An int with the gap amount
	 */
	public static int gapAmount(char char1, char char2) {
		boolean validParameter1 = false;
		boolean validParameter2 = false;
		String char1Type = null;
		String char2Type = null;
		int char1Index = -1;
		int char2Index = -1;
		
		for (int i = 0; i < 8; i++) {
			if (LETTERS[i]==char1) {
				char1Index = i;
				char1Type = "letter";
				validParameter1=true;
			}else if (NUMBERS[i] == char1) {
				char1Index = i;
				char1Type = "number";
				validParameter1=true;
			}
			
			if (LETTERS[i]==char2) {
				char2Index = i;
				char2Type = "letter";
				validParameter2=true;
			}else if (NUMBERS[i] == char2) {
				char2Index = i;
				char2Type = "number";
				validParameter2=true;
			}
		}
		
		if (validParameter1 && validParameter2 && char1Type.equals(char2Type)) {
			return char2Index - char1Index;
		}else {
			throw new InvalidParameterException("Letters only range between a-h and numbers between 1-8 and both characters must be either numbers or letters");
		}
	}
	
	/** Method that gets the positions between two positions of the same row or column
	 * 
	 * @param position2
	 * @return An ArrayList with the positions between them
	 */
	public ArrayList<Position> positionsBetween(Position position2){
		ArrayList <Position> positionsBetween = new ArrayList<Position>();
		if (this.letter != position2.letter && this.number != position2.number) {
			throw new InvalidParameterException("The positions must be in the same row or column");
		}else if (this.number == position2.number) {
			int letterGap = gapAmount(this.getLetter(), position2.getLetter());
			positionsBetween = positionsLine(RIGHT, letterGap, null, null, true, false);
		}else {
			int numberGap = gapAmount(this.getNumber(), position2.getNumber());
			positionsBetween = positionsLine(UP, numberGap, null, null, true, false);
		}
		if (positionsBetween.size()>0) {positionsBetween.removeLast();}
		return positionsBetween;
	}
	
	/** Method that gets the positions of a line from the current position
	 * 
	 * @param direction The direction of the line
	 * @param amount The amount cells the line will advance
	 * @param color The color of the piece
	 * @param board The board
	 * @param ignorePieces Boolean that is true if you dont want to take into account other pieces impeding the line
	 * @param eatEnemy If it finds an enemy piece during the line if it should be eaten or not
	 * @return An ArrayList with all the positions of the line, not including the starting one
	 */
	public ArrayList<Position> positionsLine(String direction, int amount, String color, ChessBoard board, boolean ignorePieces, boolean eatEnemy){
		int verticalModifier = 0;
		int horizontalModifier = 0;
		switch (direction) {
		case UP:
			verticalModifier = 1;
			break;
		case DOWN:
			verticalModifier = -1;
			break;
		case RIGHT:
			horizontalModifier = 1;
			break;
		case LEFT:
			horizontalModifier = -1;
			break;
		case UPRIGHT:
			verticalModifier = 1;
			horizontalModifier = 1;
			break;
		case UPLEFT:
			verticalModifier = 1;
			horizontalModifier = -1;
			break;
		case DOWNRIGHT:
			verticalModifier = -1;
			horizontalModifier = 1;
			break;
		case DOWNLEFT:
			verticalModifier = -1;
			horizontalModifier = -1;
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + direction);
		}
		verticalModifier = (amount>0) ? verticalModifier : verticalModifier * -1;
		horizontalModifier = (amount>0) ? horizontalModifier : horizontalModifier * -1;
				
		ArrayList<Position> linePositions = new ArrayList<Position>();
		linePositions.add(this);
		for(int i = 0; i<Math.abs(amount); i++) {
			Position currentPosition = linePositions.getLast();
			Position nextPosition;
			try {
				nextPosition = currentPosition.jump(verticalModifier, '1').jump(horizontalModifier, 'a');
			} catch (IndexOutOfBoundsException e) {
				break;
			}
			ChessPiece positionPiece;
			if (!ignorePieces && (positionPiece = board.findPiece(nextPosition))!=null) { 
				if (color.equals(positionPiece.getColor())) {
					break;
				}else {
					if (eatEnemy) linePositions.add(nextPosition);
					break;
				}
			}else {
				linePositions.add(nextPosition);
			}
			
		}
		//Remove the first position because it is the position we are in, it was only meant to be added to calculate the others in the loop
		linePositions.remove(0); 
		return linePositions;
	}
	
	private Character letter;
	private Character number;
	
	public Position(Character letter, Character number) {
		
		boolean validLetter = false;
		boolean validNumber = false;
		
		for(int i = 0; i < 8; i++) {
			if (LETTERS[i] == letter) validLetter = true;
			if (NUMBERS[i] == number) validNumber = true;
		}
		
		if (!validLetter || !validNumber) {
			throw new InvalidParameterException("Letters only range between a-h and numbers between 1-8");
		}
		
		this.letter = letter;
		this.number = number;
	}
	
	public Position(String position) {
		this(position.charAt(0), position.charAt(1));
	}
	
	public Character getLetter() {
		return letter;
	}

	public void setLetter(Character letter) {
		this.letter = letter;
	}

	public Character getNumber() {
		return number;
	}

	public void setNumber(Character number) {
		this.number = number;
	}
	
	@Override
	public String toString() {
		return ("" + this.letter + this.number);
	}
	
	public Position clone() {
		return new Position(this.letter, this.number); 
	}

	@Override
	public boolean equals(Object obj) {
		
		if (obj instanceof Position 
				&& ((Position) obj).letter == this.letter 
				&& ((Position) obj).number == this.number) {
			return true;
			
		}else if(obj instanceof String){
			String position = (String) obj;
			position = position.strip().toLowerCase();
			
			if(position.length() != 2){
				return false;
			}else if(position.charAt(0) == this.letter && position.charAt(1) == this.number) {
				return true;
			}else {
				return false;
			}
			
		}else {
			return false;
		}
	}
	
	
	
}
