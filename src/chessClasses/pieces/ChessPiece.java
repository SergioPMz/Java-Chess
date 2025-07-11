package chessClasses.pieces;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import chessClasses.ChessBoard;
import chessClasses.Position;

public abstract class ChessPiece {
	
	protected ChessBoard board;
	protected Position position;
	protected ArrayList<Position> previousPositions;
	protected String color;
	
	protected ArrayList<Position> currentPossibleMoves = new ArrayList<Position>();
	
	public ChessPiece(ChessBoard board, Position position, String color)  {
		color = color.strip().toLowerCase();
		
		if(!color.equals("white") && !color.equals("black")) {
			throw new IllegalArgumentException("The color can only be \"white\" or \"black\"");
		}
		
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
	 * @param KingAccounted A boolean, set it to true if you do not want to take the king into account for possible moves calculations 
	 */
	public void updatePossibleMoves(boolean KingAccounted) {
		this.currentPossibleMoves = possibleMoves();
		
		//If it is not an hypothetical board then it also removes the moves that put the king at direct risk by cloning the board and trying said move
		if (KingAccounted && !board.isHypotheticalBoard()) {
			positionsLoop:
				for (int i = currentPossibleMoves.size()-1; i>= 0; i--) {
					Position position = currentPossibleMoves.get(i);
					ChessBoard clonedBoard = board.clone();
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
	public void move(Position position) {
		boolean validMove = false;
		for (Position possiblePosition : currentPossibleMoves) {
			if (position.equals(possiblePosition)) validMove = true;
		}
		if (!validMove) {
			throw new InvalidParameterException("This piece cannot move to this position"); //If the move is not among the valid moves it throws this exception
		}
		
		ChessPiece targetPositionPiece = board.findPiece(position);
		if (targetPositionPiece != null) {
			board.killPiece(targetPositionPiece); //If there is a piece in the position we are trying to move it must mean it is an enemy and it has to be killed
		}
		
		if (this instanceof Pawn) { // This code is to kill the enemy piece correctly if the current piece is trying to eat en passant
			for (int i = 0; i < board.getEnemyPieces(this.color).size(); i++) {
				ChessPiece targetPiece = board.getEnemyPieces(this.color).get(i);
				if (targetPiece instanceof Pawn && targetPiece == board.getLastPieceToMove()) { //Checks the enemy piece is a pawn and was the last one to move
					if (targetPiece.getPreviousPositions().getLast().getNumber() == (targetPiece.color.equals("white") ? '2' : '7') //Checks the enemy pawn was in the starting position
							&& targetPiece.position.getNumber() == (targetPiece.color.equals("white") ? '4' : '5') //Checks the enemy pawn has moved 2 cells in the last move of the board
							&& targetPiece.position.jump((targetPiece.color.equals("white") ? -1 : 1), '1').equals(position)) { //Checks the position in between is the position we are trying to move to, which means we are going to eat it
						board.killPiece(targetPiece);
						break;
					}
				}
			}
		}
		
		// This code is to make sure castling works as intended, as it has to move the tower as well
		if(this instanceof King && !((King) this).getEverMoved() && this.position.positionsBetween(position).size() > 0) {
				Tower towerToCastleWith = null;
				char towerLetter = 'z'; //value that has no meaning, it is always going to be changed to a letter ranging a-h
				if (position.getLetter() == 'c') {
					towerLetter = 'a';
				}else if (position.getLetter() == 'g') {
					towerLetter = 'h';
				}
				for (Tower tower : board.findTowers(this.color)) {
					if (!tower.getEverMoved() && tower.position.getLetter() == towerLetter) {
						towerToCastleWith = tower;
					}
				}

				towerToCastleWith.previousPositions.add(towerToCastleWith.position);
				towerToCastleWith.position = this.position.positionsBetween(position).get(0);
				towerToCastleWith.setEverMoved(true);
			}
		
		// If it is a king or a tower which are moving it sets the boolean everMoved to true to prevent castling with it
		if (this instanceof King) {
			((King) this).setEverMoved(true);
		}else if (this instanceof Tower){
			((Tower) this).setEverMoved(true);
		}
		
		this.previousPositions.add(this.position);
		this.position = position;
		
		board.setLastPieceToMove(this);
		board.updateBoard(false);
		board.setTurnOf(this.color.equals("white") ? "black" : "white"); //If the turn was white changes to black and vice versa
		
	}

	public Position getPosition() {
		return position;
	}

	public ArrayList<Position> getPreviousPositions() {
		return previousPositions;
	}

	public void setPreviousPositions(ArrayList<Position> previousPositions) {
		this.previousPositions = previousPositions;
	}

	public void clonePreviousPositions(ArrayList<Position> previousPositions) {
		ArrayList<Position> clonedPreviousPositions = new ArrayList<Position>();
		
		for (Position position : previousPositions) {
			clonedPreviousPositions.add(position.clone());
		}
		
		this.previousPositions = clonedPreviousPositions;
	}
	
	public String getColor() {
		return color;
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
