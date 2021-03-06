package Enemies;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D.Double;

/**
 * If it ain't broke, don't fix it
 * @Jack
 */

public class Ball {
	
	private int x, y;
	private int size;
	private int speed;
	private Color color;
	private int top, right, bottom, left;
	
	int xdir, ydir;
	
	public Ball(int x, int y, int size, int speed, int left, int right, int top, int bottom, Color color) {
		
		this.x = x;
		this.y = y;
		this.size = size;
		this.speed = speed;
		this.color = color;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
		
		int temp = rand(0, 3);
		if(temp == 0) {
			xdir = 1;
			ydir = -1;
		}
		if(temp == 1) {
			xdir = 1;
			ydir = 1;
		}
		if(temp == 2) {
			xdir = -1;
			ydir = -1;
		}
		if(temp == 3) {
			xdir = -1;
			ydir = 1;
		}
		
	}
	
	public void draw(Graphics2D g2) {
		g2.setColor(color);
		g2.fillOval(x, y, size, size);
		g2.setColor(Color.black);
		g2.drawOval(x, y, size, size);
	}
	
	public void update() {		
		if (xdir == 1) 
			if (x + size + speed < right){
				x += speed * xdir;
			} else {
				xdir *= -1;
			}
		if (ydir == 1)
			if (y + size + speed < bottom){
				y += speed * ydir;
			} else {
				ydir *= -1;
			}
		if (xdir == -1)
			if (x - speed > left){
				x += speed * xdir;
			} else {
				xdir *= -1;
			}
		
		if (ydir == -1)
			if (y + speed > top){
				y += speed * ydir;
			} else {
				ydir *= -1;
			}
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
	public int getSize()
	{
		return size;
	}
	public int getSpeed() {
		return speed;
	}
	public Color getColor()
	{
		return color;
	}
	public void setXDir(int i) {
		xdir = i;
	}
	public int getXDir() {
		return xdir;
	}
	
	public void setLowerBound(int l)
	{
		bottom = l;
	}
	
	public Double getBounds() {
		return (new Double(x, y, size, size));
	}
	
	@SuppressWarnings("unused")
	private static void say(String s) {
		System.out.println(s);
	}
	
	private static int rand(int min, int max) {
		//[min, max)
		return min + (int)(Math.random() * ((max - min) + 1));
	}

}
