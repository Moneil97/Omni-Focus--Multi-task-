package Player;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;


public class Player2 extends Player{

	public Player2(int x, int y, int width, int height, int left,
			int right, int top, int bottom, Color color) {
		super(x, y, width, height, left, right, top, bottom, color);
	}
	
	public Rectangle2D.Double getRektBounds() {
		return new Rectangle2D.Double(x, y, width, height);
	}
	
	public void draw(Graphics g2)
	{
		// Draw lvl2 Player
		g2.setColor(color);
		g2.fillRect(x, y, width, height);
		// Player Outline
		g2.setColor(Color.black);
		g2.drawRect(x, y, width, height);
	}


}
