import java.awt.Color;
import java.awt.Graphics2D;

public class EnemyPaddle {

	private int x,y,width,height;
	private Color color;
	
	public EnemyPaddle(int x, int y, int width, int height, Color color) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.color = color;
	}
	
	public void update(Ball b) {
		setY(b.getY() + b.getSize()/2 - height / 2);
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
