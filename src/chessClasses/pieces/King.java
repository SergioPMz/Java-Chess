package chessClasses.pieces;

import java.util.ArrayList;

import chessClasses.ChessBoard;
import chessClasses.Position;

public class King extends ChessPiece{
	
	/** A boolean to know if the piece has ever moved for castling purposes
	 * 
	 */
	private boolean everMoved;

	public King(ChessBoard board, Position position, String color) {
		super(board, position, color);
		this.everMoved = false;
	}
	
	public King(ChessBoard board, Position position, String color, boolean everMoved) {
		this(board, position, color);
		this.everMoved = everMoved;
	}

	@Override
	protected ArrayList<Position> possibleMoves() {
		ArrayList<Position> possiblePositions = new ArrayList<Position>();
		
		possiblePositions.addAll(this.position.positionsLine(Position.UP, 1, this.color, board, false, true));
		possiblePositions.addAll(this.position.positionsLine(Position.DOWN, 1, this.color, board, false, true));
		possiblePositions.addAll(this.position.positionsLine(Position.RIGHT, 1, this.color, board, false, true));
		possiblePositions.addAll(this.position.positionsLine(Position.LEFT, 1, this.color, board, false, true));
		possiblePositions.addAll(this.position.positionsLine(Position.UPRIGHT, 1, this.color, board, false, true));
		possiblePositions.addAll(this.position.positionsLine(Position.UPLEFT, 1, this.color, board, false, true));
		possiblePositions.addAll(this.position.positionsLine(Position.DOWNRIGHT, 1, this.color, board, false, true));
		possiblePositions.addAll(this.position.positionsLine(Position.DOWNLEFT, 1, this.color, board, false, true));
		
		if (!everMoved) { //This code is to detect when castling is possible and to add it to the possible moves
			ArrayList<Tower> towers = board.findTowers(color);
			towerLoop:
			for (Tower tower : towers) {
				if (!tower.getEverMoved()) {
					ArrayList<Position> positionsBetween = this.position.positionsBetween(tower.getPosition());
					for (Position position : positionsBetween) {
						if (board.findPiece(position) == null) {
							if (!(positionsBetween.size() == 3 && positionsBetween.getLast().equals(position)) //The king can castle if an enemy is attacking the last position of a gap of three 
									&& (board.allPossiblePositions(this.getOppositeColor()).contains(position) //The king cannot castle if they are attacking the other positions of the castling
									|| board.allPossiblePositions(this.getOppositeColor()).contains(this.position))) { //The king cannot castle if it is being attacked directly
								continue towerLoop;
							}else {
								continue;
							}
						}else {
							continue towerLoop;
						}
					}
					possiblePositions.add(positionsBetween.get(1));
				}
			}
		}
		
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
		
		// This code is to make sure castling works as intended, as it has to move the tower as well
		if(!everMoved && ( targetPosition.getLetter() == 'g'||targetPosition.getLetter() == 'c' )) {
			Tower towerToCastleWith = null;
			char towerLetter = targetPosition.getLetter() == 'c' ? 'a' : 'h';
			
			for (Tower tower : board.findTowers(this.color)) {
				if (!tower.getEverMoved() && tower.position.getLetter() == towerLetter) {
					towerToCastleWith = tower;
					break;
				}
			}

			towerToCastleWith.previousPositions.add(towerToCastleWith.position);
			towerToCastleWith.position = this.position.positionsBetween(targetPosition).get(0);
			towerToCastleWith.setEverMoved(true);
		}
		this.everMoved = true; //When the king moves this boolean is set to true to prevent castling in the future
	}
}
