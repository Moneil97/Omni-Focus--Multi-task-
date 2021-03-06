package Player;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Ellipse2D.Double;


public class Player1 extends Player{

	public Player1(int x, int y, int width, int height, int left, int right, int top, int bottom, Color color) {
		super(x, y, width, height, left, right, top, bottom, color);
	}
	
	public Double getBounds() {
		return (new Double(x, y, width, height));
	}
	
	public void draw(Graphics g2)
	{
		//Draw lvl1 Player
		g2.setColor(color);
		g2.fillOval(x, y, width, height);
		// Player Outline
		g2.setColor(Color.black);
		g2.drawOval(x, y, width, height);
	}

}
