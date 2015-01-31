package Enemies;
import java.awt.Color;
import java.awt.Graphics2D;

public class EnemyPaddle {

	private int x,y,width,height;
	private Color color;
	private int TOP_BAR_SIZE = 30;
	private int BOTTOM_BAR_SIZE = 2;
	private int lowerBound;
		
	public EnemyPaddle(int x, int y, int width, int height, int lowerBound,Color color) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.color = color;
		this.lowerBound = lowerBound;
	}
	
	
	public void update(Ball b) {
		y = (b.getY() + b.getSize()/2 - height / 2);
		
		if (y < TOP_BAR_SIZE)
		{
			y = TOP_BAR_SIZE;
		}
		else if (y + height > lowerBound- BOTTOM_BAR_SIZE)
		{
			y = lowerBound -height - BOTTOM_BAR_SIZE;
		}
	}
	
	public void draw(Graphics2D g2) {
		
		g2.setColor(color);
		g2.fillRect(x, y, width, height);
		
		g2.setColor(Color.black);
		g2.drawRect(x, y, width, height);
	}
	
	public int getX()
	{
		return x;
	}
	public int getY()
	{
		return y;
	}
	
	public void setX(int x)
	{
		this.x = x;
	}
	public void setY(int y )
	{
		this.y = y;
	}
	public int getWidth()
	{
		return width;
	}
	public int getHeight()
	{
		return height;
	}
	
	public Color getColor()
	{
		return color;
	}


}
