package chessClasses.pieces;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import chessClasses.ChessBoard;
import chessClasses.Position;

public abstract class ChessPiece{
	
	protected ChessBoard board;
	protected Position position;
	protected ArrayList<Position> previousPositions;
	protected String color;
	
	protected ArrayList<Position> currentPossibleMoves = new ArrayList<Position>();
	
	public ChessPiece(ChessBoard board, Position position, String color)  {
		color = color.strip().toLowerCase();
		
		// Color is a String instead of something like a boolean in case i want to make a game mode with more colors in the future
		if(!color.equals("white") && !color.equals("black")) throw new InvalidParameterException("The color can only be \"white\" or \"black\"");
		
		this.board = board;
		this.position = position;
		this.color = color;
		previousPositions = new ArrayList<Position>();
		
		// A piece makes no sense without a board, so it forces its addition to one from creation
		if (color.equals("white")) {
			board.addWhitePieces(this);
		}else {
			board.addBlackPieces(this);
		}
	}
	
	/** Method that returns the possible moves a piece can perform, its implementation depends on the piece where you call it from
	 * 
	 * @return An ArrayList with the moves it can perform
	 */
	protected abstract ArrayList<Position> possibleMoves();
	
	/** Method that updates the piece, resetting its moves and adding what it can do in its current position
	 * 
	 * @param KingAccounted A boolean, set it to false if you do not want to take the king into account for possible moves calculations 
	 */
	public void updatePossibleMoves(boolean KingAccounted) {
		this.currentPossibleMoves = possibleMoves();
		
		//If it is not an hypothetical board then it also removes the moves that put the king at direct risk by cloning the board and trying said move
		if (KingAccounted && !board.isHypotheticalBoard()) {
			positionsLoop:
				for (int i = currentPossibleMoves.size()-1; i>= 0; i--) {
					Position position = currentPossibleMoves.get(i);
					ChessBoard clonedBoard = board.createHypotheticalBoard();
					clonedBoard.findPiece(this.position).move(position);
					
					Position allyKingPosition = clonedBoard.findKing(color).getPosition();
					for (Position enemyPossibleMove : clonedBoard.allPossiblePositions(this.getOppositeColor())) {
						if (enemyPossibleMove.equals(allyKingPosition)) {
							currentPossibleMoves.remove(i);
							continue positionsLoop;
						}
					}
				}
		}
	}
	
	/** Method that moves the piece to the specified position, updating the board afterwards calling board.updateBoard(false) and changing the color turn
	 * 
	 */
	public void move(Position targetPosition) {
		boolean validMove = false;
		for (Position possiblePosition : currentPossibleMoves) if (targetPosition.equals(possiblePosition)) validMove = true;
		if (!validMove) throw new InvalidParameterException("This piece cannot move to this position");
		
		//If there is a piece in the position we are trying to move it must mean it is an enemy and it has to be killed
		ChessPiece targetPositionPiece = board.findPiece(targetPosition);
		if (targetPositionPiece != null) board.killPiece(targetPositionPiece); 
		
		pieceSpecificMovementBehavior(targetPosition);
		
		this.previousPositions.add(this.position);
		this.position = targetPosition;
		board.setLastPieceToMove(this);
		
		board.updateBoard(true);
		board.setTurnOf(this.isWhite() ? "black" : "white"); //If the turn was white changes to black and vice versa
		
	}
	
	/** Method that includes specific behavior when moving of a piece. It should be implemented empty if the piece has no special behavior
	 * 
	 */
	protected abstract void pieceSpecificMovementBehavior(Position targetPosition);
	
	public Position getPosition() {
		return position;
	}

	public ArrayList<Position> getPreviousPositions() {
		return previousPositions;
	}

	public void setPreviousPositions(ArrayList<Position> previousPositions) {
		this.previousPositions = previousPositions;
	}

	public ArrayList<Position> clonePreviousPositions() {
		ArrayList<Position> clonedPreviousPositions = new ArrayList<Position>();
		
		for (Position position : previousPositions) {
			clonedPreviousPositions.add(position);
		}
		
		return clonedPreviousPositions;
	}
	
	public String getColor() {
		return color;
	}
	
	/** Method that returns true if the piece is white and false if not
	 * 
	 */
	public boolean isWhite() {
		return this.color.equals("white");
	}
	
	/** Method that returns true if the piece is black and false if not
	 * 
	 */
	public boolean isBlack() {
		return this.color.equals("white");
	}
	
	/** Method that returns true if the piece is in the first row of its color,  being 1 if white or 8 if black
	 * 
	 */
	public boolean isFirstRow() {
		return position.getNumber() == (isWhite() ? '1' : '8');
	}
	
	/** Method that returns true if the piece is in the last row of its color, being 8 if white or 1 if black
	 * 
	 */
	public boolean isLastRow() {
		return position.getNumber() == (isWhite() ? 8 : 1);
	}
	
	/** Method that returns the opposite color of the current piece, if the piece is black it will return white and vice versa
	 * 
	 * @return A String with "white" or "black"
	 */
	public String getOppositeColor() {
		return this.color.equals("white") ? "black" : "white";
	}
	
	public ArrayList<Position> getCurrentPossibleMoves() {
		return currentPossibleMoves;
	}

	@Override
	public String toString() {
		return "ChessPiece [board=" + board + ", position=" + position + ", previousPositions=" + previousPositions
				+ ", color=" + color + ", currentPossibleMoves=" + currentPossibleMoves + "]";
	}
}
