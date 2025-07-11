package graphicInterface;

import java.awt.Color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JRootPane;

import chessClasses.ChessBoard;
import chessClasses.Position;
import chessClasses.pieces.Bishop;
import chessClasses.pieces.ChessPiece;
import chessClasses.pieces.Horse;
import chessClasses.pieces.King;
import chessClasses.pieces.Pawn;
import chessClasses.pieces.Queen;
import chessClasses.pieces.Tower;

public class JChessCell extends JButton implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Position position;
	private ChessPiece currentPiece;
	private MainWindow mainWindow;
	private ChessBoard board;
	
	private JPossibleMoveCircle possibleMoveCircle;
	private int width;
	private int height;
	
	public JChessCell(Position position, int x, int y, int width, int height, MainWindow mainWindow) {
		super();
		this.position = position;
		this.board = mainWindow.getBoard();
		this.mainWindow = mainWindow;
		
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		this.setBounds(x, y, width, height);
		this.setBackground(Color.WHITE);
		
		this.width = width;
		this.height = height;
		
		this.possibleMoveCircle = new JPossibleMoveCircle(this);
		this.add(possibleMoveCircle);
		this.addActionListener(this);
	}

	/** Method that updates the current cell to reflect the state of its position in the board
	 * 
	 */
	public void updateCell(ChessBoard board) {

		this.currentPiece = board.findPiece(this.position);
		
		this.setEnabled(true);
		if (this.currentPiece instanceof Pawn) {
			this.setIcon(InterfaceIcon.PAWN.getImageIcon(width, height, this.currentPiece.getColor()));
		}else if (this.currentPiece instanceof Tower) {
			this.setIcon(InterfaceIcon.TOWER.getImageIcon(width, height, this.currentPiece.getColor()));
		}else if (this.currentPiece instanceof Horse) {
			this.setIcon(InterfaceIcon.HORSE.getImageIcon(width, height, this.currentPiece.getColor()));
		}else if (this.currentPiece instanceof Bishop) {
			this.setIcon(InterfaceIcon.BISHOP.getImageIcon(width, height, this.currentPiece.getColor()));
		}else if (this.currentPiece instanceof Queen) {
			this.setIcon(InterfaceIcon.QUEEN.getImageIcon(width, height, this.currentPiece.getColor()));
		}else if (this.currentPiece instanceof King) {
			this.setIcon(InterfaceIcon.KING.getImageIcon(width, height, this.currentPiece.getColor()));
		}else {
			this.setEnabled(false);
			this.setIcon(null);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mainWindow.hideAllPossibleCircles();
		if (this.currentPiece != null && this.currentPiece.getColor().equals(board.getTurnOf())) {
			for (Position possibleMove : this.currentPiece.getCurrentPossibleMoves()) {
					
				JChessCell targetChessCell = this.findJChessCell(possibleMove);
				JPossibleMoveCircle movementCircle = targetChessCell.getPossiblePositionCircle();
				movementCircle.setMoveAction(()->{
					this.currentPiece.move(possibleMove);
					if (this.currentPiece instanceof King) {
						mainWindow.updateAllCells();
					}else if (this.currentPiece instanceof Pawn) {
						mainWindow.updateAllCells();
						if (targetChessCell.currentPiece.getPosition().getNumber() == '1'||targetChessCell.currentPiece.getPosition().getNumber() == '8') {
							targetChessCell.crownPawn();
						}
					}else {
						mainWindow.updateCells(this, findJChessCell(possibleMove));
					}
					
					mainWindow.checkVictory();
				});
				movementCircle.setVisible(true);
			}
		}
	}
	/** Method that crowns a pawn, showing a menu to change it to the piece that is desired, which can be a horse, bishop, tower or queen
	 * 
	 */
	public void crownPawn() {
		JDialog crownPawnWindow = new JDialog(mainWindow, true);
		crownPawnWindow.setLayout(null);
		crownPawnWindow.setSize(360, 150);
		crownPawnWindow.setLocationRelativeTo(mainWindow);
		crownPawnWindow.getContentPane().setBackground(new Color(232, 195, 158));
		crownPawnWindow.setUndecorated(true);
		crownPawnWindow.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		
		int size = 70;
		ImageIcon[] piecePictures = new ImageIcon[] {
				InterfaceIcon.HORSE.getImageIcon(70, this.currentPiece.getColor()),
				InterfaceIcon.BISHOP.getImageIcon(70, this.currentPiece.getColor()),
				InterfaceIcon.TOWER.getImageIcon(70, this.currentPiece.getColor()),
				InterfaceIcon.QUEEN.getImageIcon(70, this.currentPiece.getColor())};
		
		for(int i = 0; i < 4 ; i++) {
			JButton option = new JButton();
			option.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
			option.setBounds(25 + 80*i, 40, size, size);
			option.setBackground(new Color(211, 141, 71));
			option.setIcon(piecePictures[i]);
			option.setName(i + ""); // + "" to quickly convert the int to String without having to call the String class
			option.addActionListener((e)->{
				String name = ((JButton)e.getSource()).getName();
				switch (name) {
				case "0": 
					new Horse(board, this.currentPiece.getPosition().clone(), this.currentPiece.getColor());
					break;
				case "1": 
					new Bishop(board, this.currentPiece.getPosition().clone(), this.currentPiece.getColor());
					break;
				case "2": 
					new Tower(board, this.currentPiece.getPosition().clone(), this.currentPiece.getColor(), true);
					break;
				case "3": 
					new Queen(board, this.currentPiece.getPosition().clone(), this.currentPiece.getColor());
					break;
				default:
					throw new IllegalArgumentException("Unexpected value: " + name);
				}
				board.killPiece(this.currentPiece); //The pawn has to be killed
				board.updateBoard(false); //An update is necessary for the other pieces to reflect the sudden change of a piece type
				this.updateCell(board); //It is only necessary to update the current cell because only the cell where the pawn crowned is expected o change
				crownPawnWindow.dispose(); //Closes the secondary window
			});
			crownPawnWindow.add(option);
		}
		
		crownPawnWindow.setVisible(true);
	}
	
	/** Method that searches for a JChessCell cell Object with the specified position
	 * 
	 * @param position
	 * @return
	 */
	public JChessCell findJChessCell(Position position) {
		for (JChessCell jChessCell : mainWindow.getChessCells()) {
			if (jChessCell.getPosition().equals(position)) {
				return jChessCell;
			}
		}
		return null;
	}
		
	public Position getPosition() {
		return position;
	}
	
	public JPossibleMoveCircle getPossiblePositionCircle() {
		return possibleMoveCircle;
	}
}
