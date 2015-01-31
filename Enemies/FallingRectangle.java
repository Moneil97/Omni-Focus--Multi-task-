package Enemies;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

public class FallingRectangle {

	private int x, y, width, height, speed;
	private Color color;

	static Random g = new Random();

	public FallingRectangle(int xMax, int y, int width) {
		this.x = rand(0,xMax);
		this.y = y;
		this.width = width;
		this.height = 20 + rand(0,60);
		this.speed = 2 + rand(0,3);

		color = new Color(rand(0,255), rand(0,255), rand(0,255));
	}

	public Rectangle2D.Double getBounds() {
		return new Rectangle2D.Double(x, y, width, height);
	}

	public void draw(Graphics2D g2) {
		g2.setColor(color);
		g2.fillRect(x, y, width, height);
		g2.setColor(Color.black);
		g2.drawRect(x, y, width, height);
	}

	public void update() {
		y += speed;
	}

	private static int rand(int min, int max) {
		// [min, max]
		return min + (int) (Math.random() * ((max - min) + 1));
	}

	public int getY() {
		return y;
	}
	
	@SuppressWarnings("unused")
	private static void say(String s) {
		System.out.println(s);
	}

}
