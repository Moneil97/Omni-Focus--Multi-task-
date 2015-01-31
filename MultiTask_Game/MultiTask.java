package MultiTask_Game;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import Enemies.Ball;
import Enemies.EnemyPaddle;
import Enemies.FallingLevel;
import Enemies.FallingRectangle;
import Player.Player1;
import Player.Player2;
import Player.Player3;
import Player.Player4;

/**
 * If it ain't broke, don't fix it
 * 
 * @Jack
 */

public class MultiTask extends JFrame implements Runnable, KeyListener, MouseMotionListener, MouseListener {

	private static final long serialVersionUID = 1L;
	private Image Background = new ImageIcon(this.getClass().getResource("/Media/TitleScreen.png")).getImage();
	private final int gameWidthOriginal = 550;
	private final int gameHeightOriginal = 360;
	private int gameWidth = gameWidthOriginal;
	private int gameHeight = gameHeightOriginal;
	private Graphics dbg;
	private Image dbi;
	private int frames = 0;
	private long prevTime;
	private int fps = 0;
	private int ups = 40;
	private boolean menu;
	private boolean lvl1, lvl2, lvl3, lvl4;
	private boolean fail;
	private int score = 0;
	private boolean pause = false;
	private String pauseText = "Press any key to begin."; // 40, 300, 100, 70
	private SimpleButton play = new SimpleButton("Play", new Font("tahoma", Font.PLAIN, 18), 20, 300 -30, 100, 70, new Color(70, 170, 70), new Color(120, 255, 110));
	private SimpleButton scores = new SimpleButton("Scores", new Font("tahoma", Font.PLAIN, 18), 140, 300 -30, 100, 70, new Color(70, 170, 70), new Color(120, 255, 110));
	private SimpleButton controls = new SimpleButton("Configure\nControls", new Font("tahoma", Font.PLAIN, 18), 260, 300 -30, 100, 70, new Color(70, 170, 70), new Color(120, 255, 110));
	private SimpleButton reset = new SimpleButton("Restart", new Font("tahoma", Font.PLAIN, 18), 0, 0, 200, 20, new Color(70, 170, 70), new Color(120, 255, 110));
	private SimpleButton usernameEnter = new SimpleButton("Enter Username", new Font("tahoma", Font.PLAIN, 18), 0, 0, 170, 20, new Color(70, 170, 70), new Color(120, 255, 110));
	private SimpleButton submitButton = new SimpleButton("Submit", new Font("tahoma", Font.PLAIN, 18), 0, 0, 70, 20, new Color(70, 170, 70), new Color(120, 255, 110));
	private boolean f3 = true;
	private int rektUpdates = 0;
	private List<FallingRectangle> rekts = new ArrayList<FallingRectangle>();
	private List<FallingLevel> levels = new ArrayList<FallingLevel>();
	private int pauseUpdates = 0;
	private int floorzUpdates = 0;
	private String serverStatus = "could not connect with server";
	private boolean cheatsEnabled = false;
	private boolean serverCreated = false;
	static String status = "dandy";
	static String incoming = "";			
	static String IP = "127.0.0.1";		
	static ServerSocket serverSocket;	
	public static int inwards = 1234; 
	public static int outwards = 4321;

	public static void main(String[] args) {
		MultiTask sam = new MultiTask();
		sam.setVisible(true);
		sam.run();
	}
	
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	public MultiTask() {
		pack();
		setTitle("Omni Focus");
		setMaximumSize(Toolkit.getDefaultToolkit().getScreenSize());
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
				{
					repaint();
					if (ups <= 0)
						ups = 40;
					sleep((20 * (40/ups)));
				}
			}
		});
		
		Thread submitScore = new Thread(new Runnable() {
			public void run() {				
				try {
		    		serverSocket = new ServerSocket(inwards);
		    	} catch(Exception e) {
		    		e.printStackTrace();
		    	}				
				while (true) {
					try {
						Socket SS_accept = serverSocket.accept();
						
						BufferedReader SS_BF= new BufferedReader(new InputStreamReader(SS_accept.getInputStream()));
					
						String clientMessage = SS_BF.readLine();
					
						if (clientMessage!=null) {
							PrintStream SSPS = new PrintStream(SS_accept.getOutputStream());
							incoming = clientMessage;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}); 
		
		Thread GameLoop = new Thread(new Runnable() {
			public void run() {
				while (true) {
					while (!fail) 
						GameUpdate();
					while (true) {
						System.out.print(""); // Need this to work for some dumb
												// reason
						if (reset.getPressed()) {
							play.reset();
							scores.reset();
							controls.reset();
							reset.reset();
							rekts.clear();
							lvl2Adjusted = false;
							lvl3Adjusted = false;
							lvlSetup = new boolean[4];
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
							setResizable(false);
							onetimer = true;
							pauseText = "Press any key to begin.";
							serverCreated = false;
							break;
						}
					}
				}
			}
		});
		PaintIt.start();
		GameLoop.start();
		submitScore.start();
	}

	private Thread screenAdjust1;
	private boolean lvl2Adjusted = false;
	private Thread screenAdjust2;
	private boolean lvl3Adjusted = false;
	private boolean[] lvlSetup = new boolean[4];
	//private Player[] players = new Player[4];
	private int gameUpdates = 0;
	private EnemyPaddle ai;
	private Ball ball;
	@SuppressWarnings("unused")
	private boolean canMove = true;
	private boolean controlsMenu;

	private int prevLvl1LeftKey;
	private int prevLvl1RightKey;
	private int prevLvl2UpKey;
	private int prevLvl2DownKey;
	private int prevLvl3LeftKey;
	private int prevLvl3RightKey;
	
	Player1 player1;
	Player2 player2;
	Player3 player3;
	Player4 player4;

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
				setResizable(true);
			} else if (scores.getPressed()) {
				scores.reset();
			} else if (controls.getPressed()) {
				menu = false;
				controlsMenu = true;
				// Save Old Keys (not Default) in case they cancel
				say("Saving old Controls");
				prevLvl1LeftKey = lvl1LeftKey;
				prevLvl1RightKey = lvl1RightKey;
				prevLvl2UpKey = lvl2UpKey;
				prevLvl2DownKey = lvl2DownKey;
				prevLvl3LeftKey = lvl3LeftKey;
				prevLvl3RightKey = lvl3RightKey;
				
				UpdateTextOnControlsConfigButton();
				
				controls.reset();
			}
		} else if (controlsMenu) {
			// Cancel is Clicked
			if (cancel.getPressed()) {
				lvl1LeftKey = prevLvl1LeftKey;
				lvl1RightKey = prevLvl1RightKey;
				lvl2UpKey = prevLvl2UpKey;
				lvl2DownKey = prevLvl2DownKey;
				lvl3LeftKey = prevLvl3LeftKey;
				lvl3RightKey = prevLvl3RightKey;
				say("Reverting old Controls");
				controlsMenu = false;
				menu = true;
				cancel.reset();
			}
			// Apply is Clicked
			else if (apply.getPressed()) {
				controlsMenu = false;
				menu = true;
				apply.reset();
			}
			else if (defaults.getPressed())
			{
				lvl1LeftKey = 65; // "A"
				lvl1RightKey = 68; // "D"
				lvl2UpKey = 38; // Up Arrow
				lvl2DownKey = 40; // Down Arrow
				lvl3LeftKey = 37; // Left Arrow
				lvl3RightKey = 39; // Right Arrow
				UpdateTextOnControlsConfigButton();
				defaults.reset();
			}
		}
		if (lvl1) {
			// Level Setup
			if (!lvlSetup[0]) {
				player1 = new Player1(gameWidthOriginal/2 -20 , gameHeightOriginal - 49/*354*/, 40, 40, 7, gameWidthOriginal - 8, 0, 0, Color.red);
				lvlSetup[0] = true;
			}
			
			if (!pause) {
				if (rektUpdates % 40 == 0) {
					rektUpdates = 0;
					rekts.add(new FallingRectangle(470, -100, 80));
				}
				
				for (int i = 0; i < rekts.size(); i++) {
					rekts.get(i).update();
					if (player1.getBounds().intersects(rekts.get(i).getBounds()) && !godMode)
						fail = true;
					// If rekt is passed the bottom of it's game border, delete
					// it
					if (rekts.get(i).getY() > 400)
						rekts.remove(i);
				}
			}
			// Level End
			if (!lvl2Adjusted && score >= 20) {
				lvl2Adjusted = true;
				screenAdjust1 = new Thread(new Runnable() {
					public void run() {
						pause = true;
						pauseText = "use the up and down\narrow keys to move your paddle\nand volley the ball\n\npress any key";
						while (gameWidth < gameWidthOriginal * 2) {
							gameWidth += 10;
							setSize(gameWidth, gameHeight);
							setLocationRelativeTo(null);
							sleep(10);
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
				player1.setRightBound(gameWidthOriginal - 1);//549);
				player2 = new Player2(570, 150, 20, 100, gameWidthOriginal, gameWidth, 20, gameHeightOriginal, Color.blue);
				ai = new EnemyPaddle(1050, 150, 20, 100, gameHeightOriginal,Color.blue);
				ball = new Ball(1100 / 4 * 3, 200, 20, 2, gameWidthOriginal, 1052, 30, gameHeightOriginal -5, Color.pink);
				lvlSetup[1] = true;
			}
			if (!pause) {
				ball.update();
				if (ball.getX() < gameWidthOriginal + ball.getSize() + ball.getSpeed() && !godMode)
					fail = true;
				ai.update(ball);
				if (ball.getBounds().intersects(player2.getRektBounds())) {
					ball.setXDir(ball.getXDir() * -1);
					ball.setX(player2.getX() + player2.getWidth() + 1);
				}
			}
			// Level End
			if (!lvl3Adjusted && score >= 40) {
				lvl3Adjusted = true;
				screenAdjust2 = new Thread(new Runnable() {
					public void run() {
						pause = true;
						pauseText = "Level three is beginning,\nhold on";
						while (gameHeight < gameHeightOriginal * 2) {
							gameHeight += 10;
							setSize(gameWidth, gameHeight);
							setLocationRelativeTo(null);
							sleep(10);
						}
						lvl3 = true;
						screenAdjust2 = null;
					}
				});
				screenAdjust2.start();
			}
		}
		int floorSpeed = 3;

		if (lvl3) {
			// Level Setup
			if (!lvlSetup[2]) {
				player1.setY(gameHeightOriginal - 42);//(358);
				ball.setLowerBound(gameHeightOriginal);
				player3 = new Player3(275, 450, 30, 30, 7, gameWidthOriginal, gameHeightOriginal, gameHeight, Color.green);
				lvlSetup[2] = true;
			}

			if (!pause) {
				// Game Updates
				if (floorzUpdates % 50 == 0) {
					floorzUpdates = 0;
					levels.add(new FallingLevel(gameHeight, 20, floorSpeed, 100));
				}

				boolean freeballin = true;
				boolean hortfree = true;

				for (int i = 0; i < levels.size(); i++) {
					if (levels.get(i).checkVertCollisions(player3)) { // returns
																			// true
																			// if
																			// touching
						freeballin = false;
					}
					if (levels.get(i).checkHortCollisions(player3)) { // returns
																			// true
																			// if
																			// touching
						hortfree = false;
					}
					levels.get(i).update();
					if (levels.get(i).getY() + levels.get(i).getHeight() < player3.getY())
						levels.remove(i);
					else if (godMode)
						if (levels.get(i).getY() < gameHeightOriginal + player3.getHeight() + 5)
							levels.remove(i);
				}
				// Check for fail
				if (player3.getY() < gameHeight / 2) {
					fail = true;
				}

				if (freeballin) {
					player3.setY(player3.getY() + floorSpeed);

					if (player3.getY() + player3.getHeight() > gameHeight) {
						player3.setY(gameHeight - player3.getHeight());
					}
				} else {
					player3.setY(player3.getY() - floorSpeed);
				}

				if (hortfree) {

				} else {

				}
			}

			// Level End
			if (!lvl4 && score > 60) {
				lvl4 = true;
			}
		}
		if (lvl4) {
			// Level Setup
			if (!lvlSetup[3]) {
				//player4 = new Player4();
				lvlSetup[3] = true;
			}

			if (!pause) {

				// Game Updates

				// Check for fail
			}

		}
		if (fail) {
			if(!serverCreated) {
				serverCreated = true;
				
			}
		}
		gameUpdates++;
		if (!pause) {
			rektUpdates++;
			floorzUpdates++;
		}

		if (pause) {
			pauseUpdates++;
		} else {
			pauseUpdates = 0;
		}
		
		sleep(25 - (int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)); // ms
	}

	public static void sendMessage(String text)
    {      
		Socket client = null;
		try {
			client = new Socket(IP, outwards);
		} catch ( IOException e) {}
		
		PrintStream myPS = null;
		try {
			myPS = new PrintStream(client.getOutputStream());
			myPS.println(text);
			status = "dandy";
		} catch (IOException | NullPointerException e) {
			e.printStackTrace();
			status = "could not connect";
		}
		
		
		BufferedReader myBR = null;
		try {
			myBR = new BufferedReader(new InputStreamReader(client.getInputStream()));
		} catch (IOException | NullPointerException e) {
			e.printStackTrace();
		}
		
		String temp = null;
		try {
			temp = myBR.readLine();
		} catch (IOException | NullPointerException e) {
			e.printStackTrace();
		}

    }

	private void UpdateTextOnControlsConfigButton() 
	{
		// Reset Text On Buttons
		lvl1LeftControl.setText(KeyEvent.getKeyText(lvl1LeftKey));
		lvl1RightControl.setText(KeyEvent.getKeyText(lvl1RightKey));
		lvl2UpControl.setText(KeyEvent.getKeyText(lvl2UpKey));
		lvl2DownControl.setText(KeyEvent.getKeyText(lvl2DownKey));
		lvl3LeftControl.setText(KeyEvent.getKeyText(lvl3LeftKey));
		lvl3RightControl.setText(KeyEvent.getKeyText(lvl3RightKey));
	}

	String received;
	private int speed=10;

	private void updateKeys() {
		if (player1 != null)
			player1.moveHorizontally(lvl1LeftHeld, lvl1RightHeld, speed);
		if (player2 != null)
			player2.moveVertically(lvl2UpHeld, lvl2DownHeld, speed);
		if (player3 != null)
			player3.moveHorizontally(lvl3LeftHeld, lvl3RightHeld, speed);
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

	@SuppressWarnings("unused")
	private Thread submitScore;
	private boolean onetimer = true;
	@SuppressWarnings("unused")
	private boolean onetimerUse = true;
	private boolean onetimerSub = true;

	private int lvl1LeftKey = 65; // "A"
	private int lvl1RightKey = 68; // "D"
	private int lvl2UpKey = 38; // Up Arrow
	private int lvl2DownKey = 40; // Down Arrow
	private int lvl3LeftKey = 37; // Left Arrow
	private int lvl3RightKey = 39; // Right Arrow
	private SimpleButton lvl1LeftControl = new SimpleButton(KeyEvent.getKeyText(lvl1LeftKey),   new Font("tahoma", Font.PLAIN, 36), 30, 95, 220, 50, new Color(70, 170, 70), new Color(120, 255, 110));
	private SimpleButton lvl1RightControl = new SimpleButton(KeyEvent.getKeyText(lvl1RightKey), new Font("tahoma", Font.PLAIN, 36), 260, 95, 220, 50, new Color(70, 170, 70), new Color(120, 255, 110));
	private SimpleButton lvl2UpControl = new SimpleButton(KeyEvent.getKeyText(lvl2UpKey),       new Font("tahoma", Font.PLAIN, 36), 30, 180, 220, 50, new Color(70, 170, 70), new Color(120, 255, 110));
	private SimpleButton lvl2DownControl = new SimpleButton(KeyEvent.getKeyText(lvl2DownKey),   new Font("tahoma", Font.PLAIN, 36), 260, 180, 220, 50, new Color(70, 170, 70), new Color(120, 255, 110));
	private SimpleButton lvl3LeftControl = new SimpleButton(KeyEvent.getKeyText(lvl3LeftKey),   new Font("tahoma", Font.PLAIN, 36), 30, 260, 220, 50, new Color(70, 170, 70), new Color(120, 255, 110));
	private SimpleButton lvl3RightControl = new SimpleButton(KeyEvent.getKeyText(lvl3RightKey), new Font("tahoma", Font.PLAIN, 36), 260, 260, 220, 50, new Color(70, 170, 70), new Color(120, 255, 110));
	private SimpleButton apply = new SimpleButton("Apply", new Font("tahoma", Font.PLAIN, 18), 200, 340-25, 80, 40, Color.green, Color.yellow);
	private SimpleButton cancel = new SimpleButton("Cancel", new Font("tahoma", Font.PLAIN, 18), 280, 340-25, 80, 40, Color.red, Color.yellow);
	private SimpleButton defaults = new SimpleButton("Defaults", new Font("tahoma", Font.PLAIN, 18), 360, 340 -25, 80, 40, Color.cyan, Color.yellow);

	private void paintComponent(Graphics g) {
		// AA Text smoothing
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(defaultFont);

		if (menu) {

			g2.drawImage(Background, 0, 0, gameWidth, gameHeight, null);

			play.draw(g2);
			scores.draw(g2);
			controls.draw(g2);

			g2.setColor(Color.white);
			g2.setFont(new Font("arial", Font.BOLD, 16));
			g2.drawString("Cameron O'Neil", 380, 300 + g2.getFontMetrics().getHeight() / 2);
			g2.drawString("Jack Dahms", 380, 320 + g2.getFontMetrics().getHeight() / 2);
		} else if (controlsMenu) {
			g2.setColor(Color.darkGray);
			g2.fillRect(10, 30, gameWidthOriginal - 20, gameHeightOriginal - 40);

			g2.setColor(Color.green);
			g2.fillRect(20, 70, gameWidthOriginal - 40, 90);

			g2.setColor(Color.white);
			g2.drawString("Level 1 Controls:", 30, 55);

			g2.drawString("Left:", 128, 88);
			g2.drawString("Right:", 345, 88);

			lvl1LeftControl.draw(g2);
			lvl1RightControl.draw(g2);
			lvl2UpControl.draw(g2);
			lvl2DownControl.draw(g2);
			lvl3LeftControl.draw(g2);
			lvl3RightControl.draw(g2);
			apply.draw(g2);
			cancel.draw(g2);
			defaults.draw(g2);

			if (lvl1LeftControl.getPressed() || lvl1RightControl.getPressed() || lvl2UpControl.getPressed() || lvl2DownControl.getPressed() || lvl3LeftControl.getPressed() || lvl3RightControl.getPressed())
				drawEnterKeyText(g2);

		}else if (scoresMenu) {

		}

		if (lvl1) {
			try {
				player1.draw(g2);
			} catch (Exception e) {}

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

				try {
					player2.draw(g2);
				} catch (Exception e) {
				}

				// Draw AI
				ai.draw(g2);

				// Draw Ball
				ball.draw(g2);

			} catch (Exception e) {
			}
		}
		if (lvl3) {
			// Draw level Background
			g2.setColor(Color.gray);
			g2.fillRect(0, gameHeight / 2, gameWidth / 2, gameHeight);
			// Draw Horizontal Black Border
			g2.setColor(Color.BLACK);
			g2.drawLine(0, gameHeight / 2, gameWidth, gameHeight / 2);

			try {
				player3.draw(g2);
			} catch (Exception e) {
			}

			for (int i = 0; i < levels.size(); i++) {
				levels.get(i).draw(g2);
			}
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
				g2.drawString(brokenLines[i], gameWidth / 2 - g2.getFontMetrics().stringWidth(brokenLines[i]) / 2, gameHeight / 2 + g2.getFontMetrics().getHeight() / 4
						+ g2.getFontMetrics().getHeight() * i - g2.getFontMetrics().getHeight() * (brokenLines.length - 1) / 2);
			}
		}

		if (fail) {

			g2.setColor(Color.darkGray);
			g2.fillRect(gameWidth / 2 - 150, gameHeight / 2 - 75, 300, 150);
			g2.setColor(new Color(230, 30, 30));
			g2.setFont(new Font("tahoma", Font.BOLD, 60));
			g2.drawString("FAIL", gameWidth / 2 - g2.getFontMetrics().stringWidth("FAIL") / 2, gameHeight / 2 - g2.getFontMetrics().getHeight() / 4);

			g2.setColor(Color.white);
			g2.setFont(pausedFont);
			g2.drawString("final score: " + score, gameWidth / 2 - g2.getFontMetrics().stringWidth("final score: " + score) / 2, gameHeight / 2 + 10);

			if (onetimer) {
				reset.setX(gameWidth / 2 - 100);
				reset.setY(gameHeight / 2 + 46);
				onetimer = false;
			}
			if (onetimerSub) {
				submitButton.setX(gameWidth / 2 + 55);
				submitButton.setY(gameHeight / 2 + 18);
			}
			if (serverStatus.equals("dandy")) {
				submitButton.draw(g2);
			} else {
				g2.drawString(serverStatus, gameWidth / 2 - g2.getFontMetrics().stringWidth(serverStatus) / 2, gameHeight / 2 + 10 + g2.getFontMetrics().getHeight());
			}
			reset.draw(g2);
		}

		if (!controlsMenu) {
			g2.setColor(Color.WHITE);
			g2.setFont(defaultFont);
			g2.drawString("Score: " + score, 25, 50);
			if (f3) {
				g2.drawString("FPS: " + fps, 25, 70);
				g2.drawString("UPS: " + ups + "/40", 25, 90);
				g2.drawString("Cheats: " + cheatsEnabled, 25, 110);
				if (cheatsEnabled) {
					g2.drawString("Godmode: " + godMode, 25, 130);
				}
			}
		}
		frames++;
	}

	private void drawEnterKeyText(Graphics g2) {
		g2.setColor(Color.red);
		g2.setFont(new Font("tahoma", Font.PLAIN, 50));
		g2.drawString("Please Enter A Key", 50, 250);
		g2.setFont(defaultFont);
	}

	private void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (Exception e) {
			//say("Sleep Error: " + i);
		}
	}

	private boolean lvl1LeftHeld;
	private boolean lvl1RightHeld;
	private boolean lvl2UpHeld;
	private boolean lvl2DownHeld;
	private boolean lvl3LeftHeld;
	private boolean lvl3RightHeld;
	private boolean godMode;

	public void keyPressed(KeyEvent e) {

		if (controlsMenu)
		{
			if (lvl1LeftControl.getPressed()) {
				lvl1LeftKey = e.getKeyCode();
				lvl1LeftControl.setText(KeyEvent.getKeyText(e.getKeyCode()));
				say("Left Key set to: " + KeyEvent.getKeyText(e.getKeyCode()) + " (" + e.getKeyCode() + ")");
				lvl1LeftControl.reset();
			} else if (lvl1RightControl.getPressed()) {
				lvl1RightKey = e.getKeyCode();
				lvl1RightControl.setText(KeyEvent.getKeyText(e.getKeyCode()));
				say("Right Key set to: " + KeyEvent.getKeyText(e.getKeyCode()) + " (" + e.getKeyCode() + ")");
				lvl1RightControl.reset();
			}
			else if (lvl2UpControl.getPressed()) {
				lvl2UpKey = e.getKeyCode();
				lvl2UpControl.setText(KeyEvent.getKeyText(e.getKeyCode()));
				say("Up Key set to: " + KeyEvent.getKeyText(e.getKeyCode()) + " (" + e.getKeyCode() + ")");
				lvl2UpControl.reset();
			}
			else if (lvl2DownControl.getPressed()) {
				lvl2DownKey = e.getKeyCode();
				lvl2DownControl.setText(KeyEvent.getKeyText(e.getKeyCode()));
				say("Down Key set to: " + KeyEvent.getKeyText(e.getKeyCode()) + " (" + e.getKeyCode() + ")");
				lvl2DownControl.reset();
			}
			else if (lvl3LeftControl.getPressed()) {
				lvl3LeftKey = e.getKeyCode();
				lvl3LeftControl.setText(KeyEvent.getKeyText(e.getKeyCode()));
				say("Left Key set to: " + KeyEvent.getKeyText(e.getKeyCode()) + " (" + e.getKeyCode() + ")");
				lvl3LeftControl.reset();
			}
			else if (lvl3RightControl.getPressed()) {
				lvl3RightKey = e.getKeyCode();
				lvl3RightControl.setText(KeyEvent.getKeyText(e.getKeyCode()));
				say("Right Key set to: " + KeyEvent.getKeyText(e.getKeyCode()) + " (" + e.getKeyCode() + ")");
				lvl3RightControl.reset();
			}
		}
		else if (scoresMenu)
		{
			if (usernameEnter.getPressed()) {

			}
		}
		else {
			if (pause) {
				if (pauseUpdates > 60)
					pause = false;
			}

			int keyCode = e.getKeyCode();

			if (keyCode == lvl1LeftKey)
				lvl1LeftHeld = true;
			else if (keyCode == lvl1RightKey)
				lvl1RightHeld = true;
			else if (keyCode == lvl2UpKey)
				lvl2UpHeld = true;
			else if (keyCode == lvl2DownKey)
				lvl2DownHeld = true;
			else if (keyCode == lvl3LeftKey)
				lvl3LeftHeld = true;
			else if (keyCode == lvl3RightKey)
				lvl3RightHeld = true;
			else if (keyCode == KeyEvent.VK_F)
				if (fail)
					reset.setPressed(true);
				else if (keyCode == KeyEvent.VK_F3)
					f3 = !f3; // shows stats

			// if (e.isControlDown() && e.isAltDown() && e.isShiftDown() &&
			if (e.getKeyCode() == KeyEvent.VK_C)
				cheatsEnabled = true;

			if (cheatsEnabled) {
				switch (keyCode) {
				case KeyEvent.VK_NUMPAD8: // up, increase score
					score++;
					break;
				case KeyEvent.VK_G: // godmode, cannot fail
					godMode = !godMode;
					break;
				case KeyEvent.VK_L: // lose, brings to fail
					fail = true;
					break;
				case KeyEvent.VK_K: // kill, brings to menu
					reset.setPressed(true);
					fail = true;
					break;
				}
			}
		}
	}

	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();

		if (keyCode == lvl1LeftKey) {
			lvl1LeftHeld = false;
		} else if (keyCode == lvl1RightKey) {
			lvl1RightHeld = false;
		} else if (keyCode == lvl2UpKey) {
			lvl2UpHeld = false;
		} else if (keyCode == lvl2DownKey) {
			lvl2DownHeld = false;
		} else if (keyCode == lvl3LeftKey) {
			lvl3LeftHeld = false;
		} else if (keyCode == lvl3RightKey) {
			lvl3RightHeld = false;
		}
	}

	public void mouseMoved(MouseEvent e) {
		if (menu) {
			play.isHovering(e);
			scores.isHovering(e);
			controls.isHovering(e);
		} else if (controlsMenu) {
			lvl1LeftControl.isHovering(e);
			lvl1RightControl.isHovering(e);
			lvl2UpControl.isHovering(e);
			lvl2DownControl.isHovering(e);
			lvl3LeftControl.isHovering(e);
			lvl3RightControl.isHovering(e);
			apply.isHovering(e);
			cancel.isHovering(e);
			defaults.isHovering(e);
		} else if (scoresMenu) {
			usernameEnter.isHovering(e);
		} else if (fail) {
			reset.isHovering(e);
			submitButton.isHovering(e);
		}
	}

	private boolean scoresMenu;

	public void mousePressed(MouseEvent e) {
		if (menu) {
			play.isPressed(e);
			scores.isPressed(e);
			controls.isPressed(e);
		} else if (controlsMenu) {
			lvl1LeftControl.isPressed(e);
			lvl1RightControl.isPressed(e);
			lvl2UpControl.isPressed(e);
			lvl2DownControl.isPressed(e);
			lvl3LeftControl.isPressed(e);
			lvl3RightControl.isPressed(e);
			apply.isPressed(e);
			cancel.isPressed(e);
			defaults.isPressed(e);
		} else if (scoresMenu) {
			usernameEnter.isPressed(e);
		} else if (fail) {
			reset.isPressed(e);
			submitButton.isPressed(e);
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
