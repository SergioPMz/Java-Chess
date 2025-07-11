package chessClasses;

import java.util.ArrayList;

import chessClasses.pieces.Bishop;
import chessClasses.pieces.ChessPiece;
import chessClasses.pieces.Horse;
import chessClasses.pieces.King;
import chessClasses.pieces.Pawn;
import chessClasses.pieces.Queen;
import chessClasses.pieces.Tower;

public class ChessBoard{
	private ChessPiece lastPieceToMove;
	
	private ArrayList<ChessPiece> whitePieces = new ArrayList<ChessPiece>();
	private ArrayList<ChessPiece> blackPieces = new ArrayList<ChessPiece>();
	private ArrayList<ChessPiece> cemeteryPieces = new ArrayList<ChessPiece>();
	private boolean hypotheticalBoard = false;
	
	private String turnOf = "white";
	
	/** Method that generates the default pieces for the board
	 * 
	 */
	public void generateDefaultPieces() {
		for (char letter : Position.LETTERS) {
			new Pawn(this, new Position(letter, '2'), "white");
			new Pawn(this, new Position(letter, '7'), "black");
		}
		
		new Tower(this, new Position('a', '1'), "white");
		new Tower(this, new Position('h', '1'), "white");
		new Tower(this, new Position('a', '8'), "black");
		new Tower(this, new Position('h', '8'), "black");
		
		new Horse(this, new Position('b', '1'), "white");
		new Horse(this, new Position('g', '1'), "white");
		new Horse(this, new Position('b', '8'), "black");
		new Horse(this, new Position('g', '8'), "black");
		
		new Bishop(this, new Position('c', '1'), "white");
		new Bishop(this, new Position('f', '1'), "white");
		new Bishop(this, new Position('c', '8'), "black");
		new Bishop(this, new Position('f', '8'), "black");
		
		new Queen(this, new Position('d', '1'), "white");
		new Queen(this, new Position('d', '8'), "black");
		
		new King(this, new Position('e', '1'), "white");
		new King(this, new Position('e', '8'), "black");
	}
	
	public ChessBoard(boolean defaultBoard) {
		if (defaultBoard) {
			
			generateDefaultPieces();
			
			updateBoard(false);
		}
	}
	
	public ChessBoard(boolean defaultBoard, boolean hypotheticalBoard) {
		this(defaultBoard);
		this.hypotheticalBoard = hypotheticalBoard;
	}

	public ArrayList<ChessPiece> getWhitePieces() {
		return whitePieces;
	}

	public void addWhitePieces(ChessPiece piece) {
		this.whitePieces.add(piece);
	}
	
	public ArrayList<ChessPiece> getBlackPieces() {
		return blackPieces;
	}

	public void addBlackPieces(ChessPiece piece) {
		this.blackPieces.add(piece);
	}
	
	public ArrayList<ChessPiece> getCemeteryPieces() {
		return cemeteryPieces;
	}
	
	/** Methos that kills a piece, removing it from the living pieces and sending it to the cemetery
	 * 
	 * @param piece The piece to be killed
	 */
	public void killPiece(ChessPiece piece) {
		this.cemeteryPieces.add(piece);
		
		if (piece.getColor().equals("white")) {
			this.whitePieces.remove(piece);
		}else {
			this.blackPieces.remove(piece);
		}
	}
	
	/** Method that returns both the black pieces and the white pieces that are alive in the same ArrayList
	 * 
	 * @return An ArrayList with the pieces
	 */
	public ArrayList<ChessPiece> getFieldPieces() {
		ArrayList<ChessPiece> fieldPieces = new ArrayList<ChessPiece>();
		fieldPieces.addAll(whitePieces);
		fieldPieces.addAll(blackPieces);
		return fieldPieces;
	}
	
	/** Method that gets the enemy pieces
	 * 
	 * @param currentPieceColor The color of the piece to get the enemies of
	 * @return An ArrayList with the enemy pieces
	 */
	public ArrayList<ChessPiece> getEnemyPieces(String currentPieceColor) {
		return currentPieceColor.equals("white") ? blackPieces : whitePieces;
	}
	
	public ChessPiece getLastPieceToMove() {
		return lastPieceToMove;
	}

	public void setLastPieceToMove(ChessPiece lastPieceToMove) {
		this.lastPieceToMove = lastPieceToMove;
	}
	
	public boolean isHypotheticalBoard() {
		return hypotheticalBoard;
	}

	/** Method that gets all the possible positions of all the pieces of a color
	 * 
	 * @param color The color of the pieces to get the moves of
	 * @return An ArrayList with all the positions they can move to
	 */
	public ArrayList<Position> allPossiblePositions(String color){
		ArrayList<Position> allPossiblePositions = new ArrayList<Position>();
		ArrayList<ChessPiece> pieces = (color.equals("white")) ? this.getWhitePieces() : this.getBlackPieces();
		
		for (ChessPiece chessPiece : pieces) {
			allPossiblePositions.addAll(chessPiece.getCurrentPossibleMoves());
		}
		return allPossiblePositions;
	}

	/** Method that updates the board state
	 * 
	 * @param KingAccounted If the king should be accounted in the update, if set to false it will first update it without taking into 
	 * account the king and then call the same method with this parameter set to true to update it a second time, otherwise the board might update with unexpected behavior
	 */
	public void updateBoard(boolean KingAccounted) {
		for (ChessPiece chessPiece : getFieldPieces()) {
			chessPiece.updatePossibleMoves(KingAccounted);
		}
		if (!KingAccounted) {
			updateBoard(true);
		}
	}
	
	/** Method that clones a board to a board to a exact copy with the hypothetical board boolean set on.
	 * The hypothetical board will be used to calculate movements without taking into account risking the king, in order to see which movements
	 * put it at risk in order to remove them. This is done by the method updatePossibleMoves in the class ChessPiece
	 * 
	 */
	public ChessBoard clone(){
		ChessBoard clone = new ChessBoard(false, true);
		
		for (ChessPiece chessPiece : getFieldPieces()) {
			ChessPiece newPiece = null;
			if (chessPiece instanceof Pawn) {
				newPiece = new Pawn(clone, chessPiece.getPosition().clone(), chessPiece.getColor());
			}else if (chessPiece instanceof Tower) {
				newPiece = new Tower(clone, chessPiece.getPosition().clone(), chessPiece.getColor(), ((Tower) chessPiece).getEverMoved());
			}else if (chessPiece instanceof Bishop) {
				newPiece = new Bishop(clone, chessPiece.getPosition().clone(), chessPiece.getColor());
			}else if (chessPiece instanceof Horse) {
				newPiece = new Horse(clone, chessPiece.getPosition().clone(), chessPiece.getColor());
			}else if (chessPiece instanceof Queen) {
				newPiece = new Queen(clone, chessPiece.getPosition().clone(), chessPiece.getColor());
			}else if (chessPiece instanceof King) {
				newPiece = new King(clone, chessPiece.getPosition().clone(), chessPiece.getColor(), ((King) chessPiece).getEverMoved());
			}
			newPiece.clonePreviousPositions(chessPiece.getPreviousPositions());
		}
		if (this.lastPieceToMove != null) {
			clone.setLastPieceToMove(clone.findPiece(this.lastPieceToMove.getPosition()));
		}
		
		clone.updateBoard(false);
		return clone;
	}
	
	/** Method that finds a piece in the board by position
	 * 
	 * @param position The position to check
	 * @return The piece in that position or null if there are none
	 */
	public ChessPiece findPiece(Position position) {
		for (ChessPiece chessPiece : getFieldPieces()) {
			if (chessPiece.getPosition().equals(position)) {
				return chessPiece;
			}
		}
		return null;
	}
	
	/** Method that finds a King of the specified color
	 * 
	 * @param color The color of the king
	 * @return The King or null if it was not found
	 */
	public ChessPiece findKing(String color) {
		for (ChessPiece chessPiece : (color.equals("white") ? this.getWhitePieces() : this.getBlackPieces())) {
			if (chessPiece instanceof King) {
				return chessPiece;
			}
		}
		return null;
	}
	
	/** Method that finds the towers of a specified color
	 * 
	 * @param color The color of the towers to look for
	 * @return An ArrayList with the towers
	 */
	public ArrayList<Tower> findTowers(String color) {
		ArrayList<Tower> towers = new ArrayList<Tower>();
		for (ChessPiece chessPiece : (color.equals("white") ? this.getWhitePieces() : this.getBlackPieces())) {
			if (chessPiece instanceof Tower) {
				towers.add((Tower) chessPiece);
			}
		}
		return towers;
	}
	
	/** Method that restarts the board to the default state
	 * 
	 */
	public void restart() {
		this.blackPieces = new ArrayList<ChessPiece>();
		this.whitePieces = new ArrayList<ChessPiece>();
		this.cemeteryPieces = new ArrayList<ChessPiece>();
		
		generateDefaultPieces();
		
		this.lastPieceToMove = null;
		this.turnOf = "white";
		
		updateBoard(false);
	}
	
	/** Method that returns the color of the side that is to play the next move
	 * 
	 * @return A String which can be "white" or "black" depending on who is to move
	 */
	public String getTurnOf() {
		return turnOf;
	}

	public void setTurnOf(String turnOf) {
		this.turnOf = turnOf;
	}

}
