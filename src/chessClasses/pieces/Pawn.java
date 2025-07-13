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
		boolean isStartingPosition = this.position.getNumber() == (this.isWhite() ? '2': '7');
		int jumpAmount;
		if (isStartingPosition) {
			jumpAmount = 2;
			
		}else {
			jumpAmount = 1;
		}
		//This adds the normal forward movements the pawn can do
		possiblePositions.addAll(this.position.positionsLine((this.isWhite()) ? Position.UP : Position.DOWN, jumpAmount, this.color, this.board, false, false));
		
		ArrayList<ChessPiece> enemyPieces = (this.isWhite()) ? board.getBlackPieces() : board.getWhitePieces();
		
		//These are the diagonal positions in the direction of the pawn
		ArrayList<Position> possibleDiagonalEats = new ArrayList<Position>();
		possibleDiagonalEats.addAll(this.position.positionsLine(Position.UPRIGHT, (this.isWhite() ? 1 : -1), color, board, true, true));
		possibleDiagonalEats.addAll(this.position.positionsLine(Position.UPLEFT, (this.isWhite() ? 1 : -1), color, board, true, true));
		
		
		for (Position position : possibleDiagonalEats) {
			ChessPiece positionPiece = board.findPiece(position);
			if (positionPiece != null && !positionPiece.color.equals(this.color)) { 
				possiblePositions.add(position); //If there is an enemy in the diagonal then the pawn can eat it and it is a valid move
			}else if (this.position.getNumber() == (this.isWhite() ? '5' : '4')) { // Checks the pawn can eat en passant and if so adds it to the possible moves
				for (ChessPiece piece : enemyPieces) {
					if (piece instanceof Pawn && piece == board.getLastPieceToMove()) {
						if (piece.getPreviousPositions().getLast().getNumber() == (piece.isWhite() ? '2' : '7')
								&& piece.position.getNumber() == (piece.isWhite() ? '4' : '5')
								&& piece.position.jump((piece.isWhite() ? -1 : 1), '1').equals(position)) {
							possiblePositions.add(position);
						}
					}
				}
			}
		}
		
		return possiblePositions;
	}

	@Override
	protected void pieceSpecificMovementBehavior(Position targetPosition) {
		// This code is to kill the enemy piece correctly if the pawn is trying to eat en passant
		for (int i = 0; i < board.getEnemyPieces(this.color).size(); i++) {
			ChessPiece targetPiece = board.getEnemyPieces(this.color).get(i);
			if (targetPiece instanceof Pawn && targetPiece == board.getLastPieceToMove()) { //Checks the enemy piece is a pawn and was the last one to move
				if (targetPiece.getPreviousPositions().getLast().getNumber() == (targetPiece.isWhite() ? '2' : '7') //Checks the enemy pawn was in the starting position
						&& targetPiece.position.getNumber() == (targetPiece.isWhite() ? '4' : '5') //Checks the enemy pawn has moved 2 cells in the last move of the board
						&& targetPiece.position.jump((targetPiece.isWhite() ? -1 : 1), '1').equals(targetPosition)) { //Checks the position in between is the position we are trying to move to, which means we are going to eat it
					board.killPiece(targetPiece);
					break;
				}
			}
		}
	}
}
