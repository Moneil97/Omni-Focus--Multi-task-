import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D.Double;

public class Player {

	private int x, y, width, height, leftBound, rightBound, topBound, bottomBound, lvl;
	private Color color;

	public Player(int i) {

		System.out.println("Player " + i + " Created");
	}

	public Player(int lvl, int x, int y, int width, int height, int left, int right, int top, int bottom, Color color) {

		this.lvl = lvl;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.leftBound = left;
		this.rightBound = right;
		this.topBound = top;
		this.bottomBound = bottom;
		this.color = color;
	}
	
	public void draw(Graphics2D g2, String str)
	{
		if (lvl == 1)
		{
			try {
				//Draw lvl1 Player
				g2.setColor(color);
				g2.fillOval(x, y, width, height);
				//Player Outline
				g2.setColor(Color.black);
				g2.drawOval(x, y, width, height);
			}catch (Exception e) {
			}
		}
		if (lvl == 2)
		{
			
		}
		if (lvl == 3)
		{
			
		}
		if (lvl == 4)
		{
			
		}
			
	}

	public Double getBounds() {
		return (new Double(x, y, width, height));
	}	

	public Rectangle2D.Double getRektBounds() {
		return new Rectangle2D.Double(x, y, width, height);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
		if (x + width > rightBound){
			this.x = rightBound - width;
		}
		else if (x < leftBound)
		{
			this.x = leftBound;
		}
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public int getLeftBound() {
		return leftBound;
	}

	public int getRightBound() {
		return rightBound;
	}

	public int getUpperBound() {
		return topBound;
	}

	public int getLowerBound() {
		return bottomBound;
	}
	
	public void setRightBound(int rb)
	{
		this.rightBound = rb;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	@SuppressWarnings("unused")
	private static void say(String s) {
		System.out.println(s);
	}
}
