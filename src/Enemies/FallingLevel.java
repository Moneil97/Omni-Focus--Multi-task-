package Enemies;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import Player.Player3;

public class FallingLevel {

	private int y;
	private int height;
	private int speed;
	private int gapSize;
	private Color color;

	private Rectangle leftangle;
	private Rectangle rightangle;
	//private Rectangle gap;

	int temp;

	public FallingLevel(int y, int height, int speed, int gapSize) {
		this.y = y;
		this.height = height;
		this.speed = speed;
		this.gapSize = gapSize;
		color = new Color(rand(0, 255), rand(0, 255), rand(0, 255));

		temp = rand(0, 450) - gapSize;
		leftangle = new Rectangle(0, y, 50 + temp, height);
		rightangle = new Rectangle(50 + temp + gapSize, y, 500 - temp - gapSize, height);
		//gap = new Rectangle(50 + temp, y, 50 + temp + gapSize, height);
	}

	public void update() {
		y -= speed;
	}

	public void draw(Graphics2D g2) {
		g2.setColor(color);
		g2.fillRect(leftangle.x, y, leftangle.width, height);
		g2.fillRect(rightangle.x, y, rightangle.width, height);

		g2.setColor(Color.black);
		g2.drawRect(leftangle.x, y, leftangle.width, height);
		g2.drawRect(rightangle.x, y, rightangle.width, height);
	}

	private static int rand(int min, int max) {
		// [min, max]
		return min + (int) (Math.random() * ((max - min) + 1));
	}

	public int getY() {
		return y;
	}

	public int getHeight() {
		return height;
	}

	public boolean checkVertCollisions(Player3 p) {
		if (p.getRectBounds().intersects(new Rectangle(0, y, 50 + temp, height)) || p.getRectBounds().intersects(new Rectangle(50 + temp + gapSize, y, 500 - temp - gapSize, height))) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean checkHortCollisions(Player3 p) {
		if (p.getRectBounds().intersects(new Rectangle(50 + temp, y, 50 + temp + gapSize, height))) {
			return true;
		} else {
			return false;
		}
	}
	
	public Rectangle getRect2()
	{
		return new Rectangle (50 + temp + gapSize, y, 500 - temp - gapSize, height);
	}
	
	public Rectangle getRightRectLeftSide()
	{
		return new Rectangle(50 + temp + gapSize, y, 5 , height);
	}

}
