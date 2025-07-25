package graphicInterface;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class JPossibleMoveCircle extends JButton implements ActionListener{

	private static final long serialVersionUID = 1L;
	
	private int width;
	private int height;
	
	/** The action listener of this button only calls moveAction.run()
	 * 
	 */
	private Runnable moveAction = null;
	
	public JPossibleMoveCircle(JChessCell chessCell) {
		super();
		
		this.width = chessCell.getWidth();
		this.height = chessCell.getHeight();
		
		this.setOpaque(false);
		this.setBorderPainted(false);
		this.setContentAreaFilled(false);
		this.setBackground(Color.WHITE);
		
		this.setIcon(InterfaceIcon.MOVECIRCLE.getImageIcon(width, height, null));
		
		this.addActionListener(this);
		this.setVisible(false);
	}
	
	public Runnable getMoveAction() {
		return moveAction;
	}

	public void setMoveAction(Runnable moveAction) {
		this.moveAction = moveAction;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (moveAction!=null) {
			moveAction.run();
		}
	}
}
