package Player;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;


public class Player3 extends Player{

	public Player3(int x, int y, int width, int height, int left,
			int right, int top, int bottom, Color color) {
		super(x, y, width, height, left, right, top, bottom, color);
		
	}
	
	public Rectangle getRectBounds() {
		return (new Rectangle(x, y, width, height));
	}
	
	public void draw(Graphics g2)
	{
		// Draw lvl3 Player
		g2.setColor(color);
		g2.fillRect(x, y, width, height);
		// Player Outline
		g2.setColor(Color.black);
		g2.drawRect(x, y, width, height);
	}


}
