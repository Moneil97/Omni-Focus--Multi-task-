package Player;
import java.awt.Color;

public class Player {

	protected int x;
	protected int y;
	protected int width;
	protected int height;
	private int leftBound;
	private int rightBound;
	private int upperBound;
	private int lowerBound;
	protected Color color;

	public Player(int i) {

		System.out.println("Player " + i + " Created");
	}

	public Player(int x, int y, int width, int height, int left, int right, int top, int bottom, Color color) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.leftBound = left;
		this.rightBound = right;
		this.upperBound = top;
		this.lowerBound = bottom;
		this.color = color;
	}
	
	public void moveHorizontally(boolean leftHeld, boolean rightHeld, int speed)
	{
		if (leftHeld)
			if (x > leftBound)
				x = x - speed;
			else
				x = leftBound;
				
		if (rightHeld)
			if (x + width < rightBound)
				x = x + speed;
			else
				x = rightBound - width;
	}
	
	public void moveVertically(boolean upHeld, boolean downHeld, int speed)
	{
		if (upHeld)
			if (y > upperBound)
				y = y - speed;
		if (downHeld)
			if (y + height < lowerBound)
				y = y + speed;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int x) {
		this.x = x;
		if (x + width > rightBound) {
			this.x = rightBound - width;
		} else if (x < leftBound) {
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
		return upperBound;
	}

	public int getLowerBound() {
		return lowerBound;
	}

	public void setRightBound(int rb) {
		this.rightBound = rb;
	}

	public Color getColor() {
		return color;
	}

	@SuppressWarnings("unused")
	private static void say(String s) {
		System.out.println(s);
	}
}
