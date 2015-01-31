import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

public class MultiTask extends JFrame implements Runnable, KeyListener, MouseMotionListener, MouseListener {

	private static final long serialVersionUID = 1L;
	private int gameWidthOriginal = 550;
	private int gameHeightOriginal = 400;
	private int gameWidth = 550;
	private int gameHeight = 400;
	private Graphics dbg;
	private Image dbi;
	private int frames = 0;
	private long prevTime;
	private int fps = 0;
	private int ups = 0;
	private boolean menu;
	private boolean lvl1, lvl2, lvl3, lvl4;
	private boolean fail;
	private int score = 0;
	private boolean pause = false;
	private String pauseText = "Press any key to begin.";								//40, 300, 100, 70
	private SimpleButton play = new SimpleButton("Play", new Font("tahoma", Font.PLAIN, 18), 40, 300, 100, 70, new Color(70, 170, 70), new Color(120, 255, 110));
	private SimpleButton scores = new SimpleButton("Scores", new Font("tahoma", Font.PLAIN, 18), 180, 300, 100, 70, new Color(70, 170, 70), new Color(120, 255, 110));
	private SimpleButton reset = new SimpleButton("Restart", new Font("tahoma", Font.PLAIN, 18), 0, 0, 200, 20, new Color(70, 170, 70), new Color(120, 255, 110));
	private boolean f3 = true;
	private int rektUpdates = 0;
	private List<FallingRectangle> rekts = new ArrayList<FallingRectangle>();
	private int pauseUpdates = 0;
	private String serverStatus = "trying to connect with server";
	private boolean cheatsEnabled = true;

	public static void main(String[] args) {
		MultiTask sam = new MultiTask();
		sam.setVisible(true);
		sam.run();
	}

	public MultiTask() {
		pack();
		setTitle("MultiTask");
		setSize(gameWidth, gameHeight);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setBackground(Color.gray);
		prevTime = System.nanoTime();
		menu = true;
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	@Override
	public void run() {

		Thread PaintIt = new Thread(new Runnable() {
			public void run() {
				while (true)
					repaint();
			}
		});

		Thread GameLoop = new Thread(new Runnable() {
			public void run() {
				while (true) {
					while (!fail) {
						GameUpdate();
					}
					while (true) {
						System.out.print(""); //Need this to work for some dumb reason
						if (reset.getPressed()) {
							play.reset();
							scores.reset();
							reset.reset();
							gameWidth = gameWidthOriginal;
							gameHeight = gameHeightOriginal;
							setSize(gameWidthOriginal, gameHeightOriginal);
							setLocationRelativeTo(null);
							pause = false;
							lvl1 = false;
							lvl2 = false;
							lvl3 = false;
							lvl4 = false;
							fail = false;
							menu = true;
							onetimer = true;
							pauseText = "Press any key to begin.";
							break;
						}
					}
				}
			}
		});

		PaintIt.start();
		GameLoop.start();
	}

	private Thread screenAdjust1;
	private boolean lvl2Adjusted = false;
	private Thread screenAdjust2;
	private boolean lvl3Adjusted = false;
	private boolean[] lvlSetup = new boolean[4];
	private Player[] players = new Player[4];
	private int gameUpdates = 0;
	private EnemyPaddle ai;
	private Ball ball;

	private void GameUpdate() {
		long startTime = System.nanoTime();
		updateEverySecond();
		updateKeys();

		if (menu) {
			score = 0;
			// They click play
			if (play.getPressed()) {
				lvl1 = true;
				pause = true;
				pauseText = "use the left and right\narrow keys to move the ball\nand avoid the falling objects\n\npress any key";
				score = 0;
				menu = false;				
			}
			if (scores.getPressed()) {
				
			}
		}
		if (lvl1) {
			// Level Setup
			if (!lvlSetup[0]) {
				players[0] = new Player(1, 250, 356, 40, 40, 3, 546, 0, 0, Color.red);
				lvlSetup[0] = true;
			}
			if (pause) {
				rekts.clear();
			}
			if (rektUpdates % 40 == 0 && !pause) {
				rektUpdates = 0;
				rekts.add(new FallingRectangle(470, -100, 80));
			}
			for (int i = 0; i < rekts.size(); i++) {
				rekts.get(i).update();
				checkForCollisions(rekts.get(i));
				// If rekt is passed the bottom of it's game border, delete it
				if (rekts.get(i).getY() > 400)
					rekts.remove(i);
			}
			// Level End
			if (!lvl2Adjusted && score >= 20) {
				lvl2Adjusted = true;
				screenAdjust1 = new Thread(new Runnable() {
					public void run() {
						pause = true;
						pauseText = "use the up and down\narrow keys to move your paddle\nand volley the ball\n\npress any key";
						while (gameWidth < 1100) {
							gameWidth += 10;
							setSize(gameWidth, gameHeight);
							setLocationRelativeTo(null);
							sleep(5);
						}
						lvl2 = true;
						screenAdjust1 = null;
					}
				});
				screenAdjust1.start();
			}
		}
		if (lvl2) {
			// Level Setup
			if (!lvlSetup[1]) {
				players[0].setRightBound(549);
				players[1] = new Player(2, 570, 150, 20, 100, gameWidthOriginal, gameWidth, 0, gameHeightOriginal, Color.blue);
				ai = new EnemyPaddle(1050, 150, 20, 100, Color.blue);
				ball = new Ball(700, 100, 20, 2,  gameWidthOriginal, gameWidth, 0, gameHeightOriginal, Color.pink);
				lvlSetup[1] = true;
			}
			
//			updateBall();
//			updateEnemy();
			ball.update();
			ai.update(ball);
			
			// Level End
			if (!lvl3Adjusted && score >= 40) {
				lvl3Adjusted = true;
				screenAdjust2 = new Thread(new Runnable() {
					public void run() {
						pause = true;
						pauseText = "Level three is beginning,\nhold on to your testicles";
						while (gameHeight < 800) {
							gameHeight += 10;
							setSize(gameWidth, gameHeight);
							setLocationRelativeTo(null);
							sleep(5);
						}
						lvl3 = true;
						screenAdjust2 = null;
					}
				});
				screenAdjust2.start();
			}
		}
		if (lvl3) {
			// Level Setup
			if (!lvlSetup[2]) {
				players[0].setY(358);
				players[2] = new Player(3);
				lvlSetup[2] = true;
			}

			// Level End
			if (score > 60 && !lvl4) {
				lvl4 = true;
			}
		}
		if (lvl4) {
			// Level Setup
			if (!lvlSetup[3]) {
				players[3] = new Player(4);
				lvlSetup[3] = true;
			}

			// Level End
		}

		if (fail) {
			if (cheatsEnabled) {
				serverStatus = "cheat mode enabled";
			}
		}
		gameUpdates++;
		if (!pause)
			rektUpdates++;
		sleep(25 - (int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)); // ms

		if (pause) {
			pauseUpdates++;
		} else {
			pauseUpdates = 0;
		}
	}


	public void checkForCollisions(FallingRectangle rekt) {
		if (players[0].getBounds().intersects(rekt.getBounds()) && !godMode) {
			fail = true;
		}
		if (lvl2)
		{
			if (ball.getBounds().intersects(players[1].getRektBounds())) {
				
			}
		}
		
	}

	private void updateKeys() {
		if (leftHeld)
			if (players[0] != null)
				if (checkBounds(players[0], "left"))
					players[0].setX(players[0].getX() - 15);
		if (rightHeld)
			if (players[0] != null)
				if (checkBounds(players[0], "right"))
					players[0].setX(players[0].getX() + 15);
		if (upHeld)
			if (players[1] != null)
				if (checkBounds(players[1], "upper"))
					players[1].setY(players[1].getY() - 10);
		if (downHeld)
			if (players[1] != null)
				if (checkBounds(players[1], "lower"))
					players[1].setY(players[1].getY() + 10);
	}

	private boolean checkBounds(Player player, String dir) {
		if (dir.equals("left")) {
			if (player.getX() > player.getLeftBound())
				return true;
		} else if (dir.equals("right")) {
			if (player.getX() + player.getWidth() < player.getRightBound())
				return true;
		} else if (dir.equals("upper")) {
			if (player.getY() > player.getUpperBound())
				return true;
		} else if (dir.equals("lower")) {
			if (player.getY() + player.getHeight() < player.getLowerBound())
				return true;
		}
		return false;
	}

	private void updateEverySecond() {
		long passed = (System.nanoTime() - prevTime);
		if (passed > 1000000000) {
			if (!pause) {
				score++;
			}
			fps = (int) (frames / (passed / 1000000000));
			frames = 0;
			ups = (int) (gameUpdates / (passed / 1000000000));
			gameUpdates = 0;
			prevTime = System.nanoTime();
		}
	}

	public void paint(Graphics g) {
		dbi = createImage(getWidth(), getHeight());
		dbg = dbi.getGraphics();
		paintComponent(dbg);
		g.drawImage(dbi, 0, 0, this);
	}

	private Font defaultFont = new Font("tahoma", Font.PLAIN, 12);
	private Font pausedFont = new Font("tahoma", Font.PLAIN, 18);

	private Thread serverConnect;
	private boolean onetimer = true;

	private void paintComponent(Graphics g) {
		// AA Text smoothing
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(defaultFont);

		if (menu) {
			g2.setColor(Color.white);
			g2.setFont(new Font("tahoma", Font.BOLD, 16));
			g2.drawString("cameron o'neil", 380, 300 + g2.getFontMetrics().getHeight() / 2);
			g2.drawString("jack dahms", 380, 320 + g2.getFontMetrics().getHeight() / 2);
			play.draw(g2);
			scores.draw(g2);
		}
		if (lvl1) {
			
				players[0].draw(g2, "lvl1");
			

			for (int i = 0; i < rekts.size(); i++) {
				rekts.get(i).draw(g2);
			}
		}
		if (lvl2) {
			// Draw level Background
			g2.setColor(Color.gray);
			g2.fillRect(gameWidthOriginal, 0, gameWidth, gameHeightOriginal);
			// Draw Vertical Black Border
			g2.setColor(Color.BLACK);
			g2.drawLine(gameWidth / 2, 0, gameWidth / 2, gameHeight);
			
			try {
				//Draw lvl2 Player
				g2.setColor(players[1].getColor());
				g2.fillRect(players[1].getX(), players[1].getY(), players[1].getWidth(), players[1].getHeight());
				//Player Outline
				g2.setColor(Color.black);
				g2.drawRect(players[1].getX(), players[1].getY(), players[1].getWidth(), players[1].getHeight());
				
				//Draw AI
				ai.draw(g2);
				
				//Draw Ball
				ball.draw(g2);
				
			}catch(Exception e){}
		}
		if (lvl3) {
			// Draw level Background
			g2.setColor(Color.gray);
			g2.fillRect(0, gameHeight / 2, gameWidth / 2, gameHeight);
			// Draw Horizontal Black Border
			g2.setColor(Color.BLACK);
			g2.drawLine(0, gameHeight / 2, gameWidth, gameHeight / 2);
		}
		if (lvl4) {

		}
		if (pause) {

			g2.setColor(Color.darkGray);
			g2.fillRect(gameWidth / 2 - 150, gameHeight / 2 - 75, 300, 150);
			g2.setColor(Color.white);
			g2.setFont(pausedFont);

			String[] brokenLines = pauseText.split("\n");
			for (int i = 0; i < brokenLines.length; i++) {
				g2.drawString(brokenLines[i], gameWidth / 2 - g2.getFontMetrics().stringWidth(brokenLines[i]) / 2, gameHeight / 2 + g2.getFontMetrics().getHeight() / 4 + g2.getFontMetrics().getHeight() * i - g2.getFontMetrics().getHeight() * (brokenLines.length - 1) / 2);
			}
		}

		if (fail) {
			pause = true;
			pauseText = "";

			serverConnect = new Thread(new Runnable() {
				public void run() {

					serverConnect = null;
				}
			});
			serverConnect.start();

			g2.setColor(Color.darkGray);
			g2.fillRect(gameWidth / 2 - 150, gameHeight / 2 - 75, 300, 150);
			g2.setColor(new Color(230, 30, 30));
			g2.setFont(new Font("tahoma", Font.BOLD, 60));
			g2.drawString("FAIL", gameWidth / 2 - g2.getFontMetrics().stringWidth("FAIL") / 2, gameHeight / 2 - g2.getFontMetrics().getHeight() / 4);

			g2.setColor(Color.white);
			g2.setFont(pausedFont);
			g2.drawString("final score: " + score, gameWidth / 2 - g2.getFontMetrics().stringWidth("final score: " + score) / 2, gameHeight / 2 + 10);
			g2.drawString(serverStatus, gameWidth / 2 - g2.getFontMetrics().stringWidth(serverStatus) / 2, gameHeight / 2 + 10 + g2.getFontMetrics().getHeight());

			if (onetimer) {
				reset.setX(gameWidth / 2 - 100);
				reset.setY(gameHeight / 2 + 45);
				onetimer = false;
			}
			reset.draw(g2);

		}

		g2.setColor(Color.WHITE);
		g2.setFont(defaultFont);
		g2.drawString("Score: " + score, 25, 50);
		if (f3) {
			g2.drawString("FPS: " + fps, 25, 70);
			g2.drawString("UPS: " + ups + "/40", 25, 90);
			g2.drawString("Cheats: " + cheatsEnabled, 25, 110);
			if(cheatsEnabled) {
				g2.drawString("Godmode: " + godMode, 25, 130);
			}
		}
		frames++;
	}

	private void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (Exception e) {
			say("Sleep Error: " + i);
		}
	}

	private boolean leftHeld;
	private boolean rightHeld;
	private boolean upHeld;
	private boolean downHeld;
	private boolean godMode;

	public void keyPressed(KeyEvent e) {
		if (pause) {
			if (pauseUpdates > 60)
				pause = false;
		}
		int keyCode = e.getKeyCode();
		switch (keyCode) {
			case KeyEvent.VK_LEFT: //left, moves left
				leftHeld = true;
				break;
			case KeyEvent.VK_RIGHT: //right, moves right
				rightHeld = true;
				break;
			case KeyEvent.VK_UP:
				upHeld = true;
				break;
			case KeyEvent.VK_DOWN:
				downHeld = true;
				break;
			case KeyEvent.VK_F://fail, shortcut for reset button
				if (fail) {
					reset.setPressed(true);
				}
				break;
			case KeyEvent.VK_F3: //f3, shows stats
				f3 = !f3;
				break;
		}
		if (e.isControlDown() && e.isAltDown() && e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_C) {
			cheatsEnabled = true;
		}
		if (cheatsEnabled) {
			switch (keyCode) {
				case KeyEvent.VK_NUMPAD8: //up, increase score
					score++;
					break;
				case KeyEvent.VK_G: //godmode, cannot fail
					godMode = !godMode;
					break;
				case KeyEvent.VK_L: //lose, brings to fail
					fail = true;
					break;
				case KeyEvent.VK_K: //kill, brings to menu
					reset.setPressed(true);
					fail = true;
					break;
				}
		}
	}

	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		case KeyEvent.VK_LEFT:
			leftHeld = false;
			break;
		case KeyEvent.VK_RIGHT:
			rightHeld = false;
			break;
		case KeyEvent.VK_UP:
			upHeld = false;
			break;
		case KeyEvent.VK_DOWN:
			downHeld = false;
			break;
		}
	}

	public void mouseMoved(MouseEvent e) {
		if (menu) {
			play.isHovering(e);
			scores.isHovering(e);
		}
		if (fail) {
			reset.isHovering(e);
		}
	}

	public void mousePressed(MouseEvent e) {
		if (menu) {
			play.isPressed(e);
			scores.isPressed(e);
		}
		if (fail) {
			reset.isPressed(e);
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	private static void say(String s) {
		System.out.println(s);
	}

	@SuppressWarnings("unused")
	private void say(long l) {
		System.out.println(l);
	}
}
