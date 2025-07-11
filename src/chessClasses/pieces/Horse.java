package chessClasses.pieces;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import chessClasses.ChessBoard;
import chessClasses.Position;

public class Horse extends ChessPiece{

	public Horse(ChessBoard board, Position position, String color) {
		super(board, position, color);
	}

	@Override
	protected ArrayList<Position> possibleMoves() {
		ArrayList<Position> possiblePositions = new ArrayList<Position>();
		
		for(int i = 0; i<8; i++) {
			Position lMove = calcLMove(i);
			if(lMove != null) possiblePositions.add(lMove);
		}
		
		
		
		for (int i = possiblePositions.size() - 1; i>=0 ; i--) {
			Position targetPosition = possiblePositions.get(i);
			ChessPiece pieceInPosition =  board.findPiece(targetPosition);
			//The horse can't move to this position because it is already occupied by another piece of the same color
			if (pieceInPosition != null && pieceInPosition.getColor().equals(this.getColor())) possiblePositions.remove(targetPosition); 
		}
		
		return possiblePositions;
	}
	
	/** Method that gets a possible l move the horse can do or null if he cannot do this particular move
	 * 
	 * @param moveNumber A horse can in an empty board do 8 moves, this number ranging from (0 to 7 both included) will one of the eight
	 * @return
	 */
	private Position calcLMove(int moveNumber) {
		if(!(moveNumber>=0&&moveNumber<8)) throw new InvalidParameterException("The parameter moveNumber can only be ranging 0-7 both included");
		int letterJumpAmount = 0;
		int numberJumpAmount = 0;
		
		switch (moveNumber) {
		case 0: 
			letterJumpAmount = 1;
			numberJumpAmount = 2;
			break;
		case 1: 
			letterJumpAmount = 1;
			numberJumpAmount = -2;
			break;
		case 2: 
			letterJumpAmount = -1;
			numberJumpAmount = 2;
			break;
		case 3: 
			letterJumpAmount = -1;
			numberJumpAmount = -2;
			break;
		case 4: 
			letterJumpAmount = 2;
			numberJumpAmount = 1;
			break;
		case 5: 
			letterJumpAmount = 2;
			numberJumpAmount = -1;
			break;
		case 6: 
			letterJumpAmount = -2;
			numberJumpAmount = 1;
			break;
		case 7: 
			letterJumpAmount = -2;
			numberJumpAmount = -1;
			break;
		}
		
		try {
			return this.position.jump(letterJumpAmount, 'a').jump(numberJumpAmount, '1');
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

}
