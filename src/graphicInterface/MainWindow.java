package graphicInterface;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import chessClasses.ChessBoard;
import chessClasses.Position;
import chessClasses.pieces.King;

@SuppressWarnings("serial")
public class MainWindow extends JFrame{
	private JPanel panel;
	private ChessBoard board;
	private static final int CELLSIZE = 80;
	
	private  ArrayList<JChessCell> chessCells = new ArrayList<JChessCell>();;
	
	public MainWindow() {
		super("Chess");
		
		this.board = new ChessBoard(true);
		
	    this.setSize(800, 800);
	    this.setResizable(false);
	    this.setLocationRelativeTo(null);
	    this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //This kills the terminal when the window is closed
	    this.setIconImage(InterfaceIcon.KING.getImageIcon(50, "black").getImage());
	    
	    loadPanel();
	    loadCells();
	    updateAllCells();
	    loadDecoration();
	    loadRetryButton();
	    
	}
	
	/** Method that loads the JPanel and adds it to the JFrame
	 * 
	 */
	private void loadPanel() {
		this.panel = new JPanel();
		this.panel.setLayout(null);
		this.panel.setBackground(new Color(232, 195, 158));
        this.setContentPane(panel);
	}
	
	/** Method that loads the cells to the mainWindow
	 * 
	 */
	private void loadCells() {
		int x;
	    int y = 625;
	    
	    boolean coloredCell = true;;
	    for (char number : Position.NUMBERS) {
	    	x=70;
	    	for (char letter : Position.LETTERS) {
				JChessCell chessCell = new JChessCell(new Position(letter, number), x, y, CELLSIZE, CELLSIZE, this);
				x += CELLSIZE;
				
				if (coloredCell) {
					chessCell.setBackground(new Color(211, 141, 71));
					coloredCell = false;
				}else {
					chessCell.setBackground(new Color(251, 206, 156));
					coloredCell = true;
				}
				
				this.panel.add(chessCell);
				chessCells.add(chessCell);
			}
	    	
	    	coloredCell = !coloredCell;
	    	y -= CELLSIZE;
		}
	}
	
	/** Method that loads the labels of the letters and numbers on the sides of the board
	 * 
	 */
	private void loadDecoration() {
	    int x = 70;
	    
	    for (char letter : Position.LETTERS) {
	    	JLabel letterLabelUp = new JLabel((letter + "").toUpperCase());
	    	letterLabelUp.setHorizontalAlignment(SwingConstants.CENTER);
	    	letterLabelUp.setVerticalAlignment(SwingConstants.CENTER);
	    	letterLabelUp.setFont(new Font("Arial", Font.BOLD, 20));
	    	
	    	JLabel letterLabelDown = new JLabel((letter + "").toUpperCase());
	    	letterLabelDown.setHorizontalAlignment(SwingConstants.CENTER);
	    	letterLabelDown.setVerticalAlignment(SwingConstants.CENTER);
	    	letterLabelDown.setFont(new Font("Arial", Font.BOLD, 20));
	    	
	    	letterLabelUp.setBounds(x,10,CELLSIZE,60);
	    	letterLabelDown.setBounds(x,700,CELLSIZE,60);
	    	
	    	this.panel.add(letterLabelUp);
	    	this.panel.add(letterLabelDown);
	    	
	    	x+=CELLSIZE;
	    }
	    
	    int y = 635;
	    for (char number : Position.NUMBERS) {
	    	JLabel numberLabelRight = new JLabel((number + "").toUpperCase());
	    	numberLabelRight.setHorizontalAlignment(SwingConstants.CENTER);
	    	numberLabelRight.setVerticalAlignment(SwingConstants.CENTER);
	    	numberLabelRight.setFont(new Font("Arial", Font.BOLD, 20));
	    	
	    	JLabel numberLabelLeft = new JLabel((number + "").toUpperCase());
	    	numberLabelLeft.setHorizontalAlignment(SwingConstants.CENTER);
	    	numberLabelLeft.setVerticalAlignment(SwingConstants.CENTER);
	    	numberLabelLeft.setFont(new Font("Arial", Font.BOLD, 20));
	    	
	    	numberLabelRight.setBounds(-5,y,CELLSIZE,60);
	    	numberLabelLeft.setBounds(705,y,CELLSIZE,60);
	    	
	    	this.panel.add(numberLabelRight);
	    	this.panel.add(numberLabelLeft);
	    	
	    	y-=CELLSIZE;
	    }
	}
	
	/** Method that loads the retry button
	 * 
	 */
	private void loadRetryButton() {
	    JButton retryButton = new JButton();
	    retryButton.setBounds(735, 15, 30, 30);
	    retryButton.setOpaque(false);
	    retryButton.setBorderPainted(false);
	    retryButton.setContentAreaFilled(false);

	    retryButton.setIcon(InterfaceIcon.RETRYBUTTON.getImageIcon(30, null));
		
	    retryButton.addActionListener((e)->{
	    	int input = JOptionPane.showConfirmDialog(null, "Restart?", "Restart game" ,JOptionPane.WARNING_MESSAGE);
	    	if (input == 0) {
				board.restart();
				this.updateAllCells();
			}
	    });
		
	    this.panel.add(retryButton);
	    this.setVisible(true);
	}
	
	/** Method that hides all possibleMovesCircles
	 * 
	 */
	public void hideAllPossibleCircles() {
		for (JChessCell jChessCell : chessCells) {
			jChessCell.getPossiblePositionCircle().setVisible(false);
		}
	}
	
	/** Method that updates all cells with the current board state
	 * 
	 */
	public void updateAllCells() {
		hideAllPossibleCircles();
		for (JChessCell jChessCell : chessCells) {
			jChessCell.updateCell(this.board);
		}
	}
	
	/** Method that updates the two specified cells in the order given
	 * 
	 */
	public void updateCells(JChessCell jChessCell1, JChessCell JChessCell2) {
		hideAllPossibleCircles();
		jChessCell1.updateCell(this.board);
		JChessCell2.updateCell(this.board);
	}

	public ChessBoard getBoard() {
		return board;
	}

	public void setBoard(ChessBoard board) {
		this.board = board;
	}

	public ArrayList<JChessCell> getChessCells() {
		return chessCells;
	}

	public void setChessCells(ArrayList<JChessCell> chessCells) {
		this.chessCells = chessCells;
	}
	
	/** Method that checks if there is a checkmate or a stalemate
	 * 
	 */
	public void checkVictory() {
		ArrayList<Position> whitePossibleMoves = board.allPossiblePositions("white");
		ArrayList<Position> blackPossibleMoves = board.allPossiblePositions("black");
		String winner = null;
		boolean stalemate = true;
		boolean gameFinished = false;
		if (whitePossibleMoves.size() == 0) {
			gameFinished = true;
			King whiteKing = (King) board.findKing("white");
			for (Position position : blackPossibleMoves) {
				if (whiteKing.getPosition().equals(position)) {
					stalemate = false;
					winner = "Black";
					break;
				}
			}
			
		}else if (blackPossibleMoves.size() == 0) {
			gameFinished = true;
			King blackKing = (King) board.findKing("black");
			for (Position position : whitePossibleMoves) {
				if (blackKing.getPosition().equals(position)) {
					stalemate = false;
					winner = "White";
					break;
				}
			}
		}if (gameFinished) {
			if (!stalemate) {
				JOptionPane.showMessageDialog(null, winner + " wins");
			}else{
				JOptionPane.showMessageDialog(null, "Stalemate");
			} 
		}
		
	}
}
