package chessClasses.pieces;

import java.util.ArrayList;

import chessClasses.ChessBoard;
import chessClasses.Position;

public class Tower extends ChessPiece{
	
	private boolean everMoved;
	
	public Tower(ChessBoard board, Position position, String color) {
		super(board, position, color);
		this.everMoved = false;
	}
	
	/** This constructor is only to create a copy of a tower that has moved already
	 * 
	 * @param board
	 * @param position
	 * @param color
	 * @param everMoved
	 */
	public Tower(ChessBoard board, Position position, String color, boolean everMoved) {
		this(board, position, color);
		this.everMoved = everMoved;
	}

	@Override
	protected ArrayList<Position> possibleMoves() {
		ArrayList<Position> possiblePositions = new ArrayList<Position>();
		
		possiblePositions.addAll(this.position.positionsLine(Position.UP, 7, this.color, this.board, false, true));
		possiblePositions.addAll(this.position.positionsLine(Position.DOWN, 7, this.color, this.board, false, true));
		possiblePositions.addAll(this.position.positionsLine(Position.RIGHT, 7, this.color, this.board, false, true));
		possiblePositions.addAll(this.position.positionsLine(Position.LEFT, 7, this.color, this.board, false, true));
		
		return possiblePositions;
	}
	
	public boolean getEverMoved() {
		return everMoved;
	}
	
	public void setEverMoved(boolean everMoved) {
		this.everMoved = everMoved;
	}
	
	@Override
	protected void pieceSpecificMovementBehavior(Position targetPosition) {
		this.everMoved = true; //When the tower moves this boolean is set to true to prevent castling in the future
	}
}
