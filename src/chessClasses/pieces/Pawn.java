package chessClasses.pieces;

import java.util.ArrayList;

import chessClasses.ChessBoard;
import chessClasses.Position;

public class Pawn extends ChessPiece{

	public Pawn(ChessBoard board, Position position, String color) {
		super(board, position, color);
	}

	@Override
	protected ArrayList<Position> possibleMoves() {
		ArrayList<Position> possiblePositions = new ArrayList<Position>();
		
		//This boolean is true if the pawn is in the starting position
		boolean isStartingPosition = this.position.getNumber() == (this.color.equals("white") ? '2': '7');
		int jumpAmount;
		if (isStartingPosition) {
			jumpAmount = 2;
			
		}else {
			jumpAmount = 1;
		}
		//This adds the normal forward movements the pawn can do
		possiblePositions.addAll(this.position.positionsLine((this.color.equals("white")) ? Position.UP : Position.DOWN, jumpAmount, this.color, this.board, false, false));
		
		ArrayList<ChessPiece> enemyPieces = (this.color.equals("white")) ? board.getBlackPieces() : board.getWhitePieces();
		
		//These are the diagonal positions in the direction of the pawn
		ArrayList<Position> possibleDiagonalEats = new ArrayList<Position>();
		possibleDiagonalEats.addAll(this.position.positionsLine(Position.UPRIGHT, (this.color.equals("white") ? 1 : -1), color, board, true, true));
		possibleDiagonalEats.addAll(this.position.positionsLine(Position.UPLEFT, (this.color.equals("white") ? 1 : -1), color, board, true, true));
		
		
		for (Position position : possibleDiagonalEats) {
			ChessPiece positionPiece = board.findPiece(position);
			if (positionPiece != null && !positionPiece.color.equals(this.color)) { 
				possiblePositions.add(position); //If there is an enemy in the diagonal then the pawn can eat it and it is a valid move
			}else if (this.position.getNumber() == (this.color.equals("white") ? '5' : '4')) { // Checks the pawn can eat en passant and if so adds it to the possible moves
				for (ChessPiece piece : enemyPieces) {
					if (piece instanceof Pawn && piece == board.getLastPieceToMove()) {
						if (piece.getPreviousPositions().getLast().getNumber() == (piece.color.equals("white") ? '2' : '7')
								&& piece.position.getNumber() == (piece.color.equals("white") ? '4' : '5')
								&& piece.position.jump((piece.color.equals("white") ? -1 : 1), '1').equals(position)) {
							possiblePositions.add(position);
						}
					}
				}
			}
		}
		
		return possiblePositions;
	}


}
