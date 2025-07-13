package chessClasses.pieces;

import java.util.ArrayList;

import chessClasses.ChessBoard;
import chessClasses.Position;

public class Bishop extends ChessPiece{

	public Bishop(ChessBoard board, Position position, String color) {
		super(board, position, color);
	}

	@Override
	protected ArrayList<Position> possibleMoves() {
		ArrayList<Position> possiblePositions = new ArrayList<Position>();
		
		possiblePositions.addAll(this.position.positionsLine(Position.UPRIGHT, 7, this.color, this.board, false, true));
		possiblePositions.addAll(this.position.positionsLine(Position.UPLEFT, 7, this.color, this.board, false, true));
		possiblePositions.addAll(this.position.positionsLine(Position.DOWNRIGHT, 7, this.color, this.board, false, true));
		possiblePositions.addAll(this.position.positionsLine(Position.DOWNLEFT, 7, this.color, this.board, false, true));
		
		return possiblePositions;
	}
	
	@Override
	protected void pieceSpecificMovementBehavior(Position targetPosition) {
		//This piece has no specific behavior when moving
	}
}
