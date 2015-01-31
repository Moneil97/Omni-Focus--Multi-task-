package MultiTask_Game;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public class SimpleButton {

	private int x, y, width, height;
	private Color color, hover;
	private String text;
	private Font font;

	private boolean hovering, pressed;

	private Rectangle rekt;

	public SimpleButton(String text, Font font, int x, int y, int width, int height, Color color, Color hover) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.color = color;
		this.hover = hover;
		this.text = text;
		this.font = font;

		rekt = new Rectangle(x, y, width, height);
	}

	public void setText(String s) {
		this.text = s;
	}

	public void draw(Graphics2D g2) {
		g2.setFont(font);
		if (hovering) {
			g2.setColor(hover);
		} else {
			g2.setColor(color);
		}
		g2.fillRect(x, y, width, height);
		g2.setColor(Color.black);
		String[] brokenLines = text.split("\n");
		for (int i = 0; i < brokenLines.length; i++) {
			g2.drawString(brokenLines[i], x + width / 2 - g2.getFontMetrics().stringWidth(brokenLines[i]) / 2, y + height / 2 + g2.getFontMetrics().getHeight() / 4 + g2.getFontMetrics().getHeight()
					* i - g2.getFontMetrics().getHeight() * (brokenLines.length - 1) / 2);
		}
	}

	public void isHovering(MouseEvent e) {
		if (rekt.contains(e.getX(), e.getY())) {
			if (!hovering) {
				hovering = true;
			}
		} else {
			hovering = false;
		}
	}

	public void isPressed(MouseEvent e) {
		if (hovering) {
			pressed = true;
		}
	}

	public boolean getPressed() {
		return pressed;
	}

	public void setX(int x) {
		this.x = x;
		rekt = new Rectangle(x, y, width, height);
	}

	public void setY(int y) {
		this.y = y;
		rekt = new Rectangle(x, y, width, height);
	}

	public void setWidth(int w) {
		this.width = w;
		rekt = new Rectangle(x, y, width, height);
	}

	public void setHeight(int h) {
		this.height = h;
		rekt = new Rectangle(x, y, width, height);
	}

	public void reset() {
		pressed = false;
		hovering = false;
	}

	public void setPressed(boolean b) {
		pressed = b;
		System.out.println("reset");
	}
}
