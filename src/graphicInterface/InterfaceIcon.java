package graphicInterface;

import java.awt.Image;
import java.security.InvalidParameterException;

import javax.swing.ImageIcon;

public enum InterfaceIcon {
	PAWN("src/icons/?_pawn.png"), 
	HORSE("src/icons/?_horse.png"), 
	BISHOP("src/icons/?_bishop.png"), 
	TOWER("src/icons/?_tower.png"), 
	QUEEN("src/icons/?_queen.png"), 
	KING("src/icons/?_king.png"), 
	RETRYBUTTON("src/icons/retry.png"), 
	MOVECIRCLE("src/icons/possibleMoveCircle.png");
	
	private final String path;
	
	private InterfaceIcon(String path) {
		this.path = path;
	}
	
	/** Method that formats an ImageIcon of the specified size
	 * 
	 * @param width The width of the image resolution
	 * @param height The height of the image resolution
	 * @param color If you are trying to get a piece icon you must pass "white" or "black" depending on the color you want, otherwise this can be left null
	 * @return
	 */
	public ImageIcon getImageIcon(int width, int height, String color) {
		String definitiveImagePath = this.path;
		if (color!=null) {
			definitiveImagePath = definitiveImagePath.replace("?" , color);
		}
		
		if (definitiveImagePath.contains("?")) {
			throw new InvalidParameterException("You must pass \"white\" or \"black\" in the parameter color to get this icon");
		}
		
		ImageIcon icon  = new ImageIcon(definitiveImagePath);
		return new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
	}
	
	/** Method that formats an ImageIcon of the specified size, assumed to be a square
	 * 
	 * @param size The size of the image resolution
	 * @param color If you are trying to get a piece icon you must pass "white" or "black" depending on the color you want, otherwise this can be left null
	 * @return
	 */
	public ImageIcon getImageIcon(int size, String color) {
		return getImageIcon(size, size, color);
	}
}
