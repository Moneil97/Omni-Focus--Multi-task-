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

public class MultiTask_old extends JFrame implements Runnable, KeyListener,
		MouseMotionListener, MouseListener {

	private static final long serialVersionUID = 1L;
	private Image Background = new ImageIcon(this.getClass().getResource(
			"/Media/TitleScreen.png")).getImage();
	private Image upDown = new ImageIcon(this.getClass().getResource(
			"/Media/UpDownArrows.png")).getImage();
	private Image leftRight = new ImageIcon(this.getClass().getResource(
			"/Media/LeftRightArrows.png")).getImage();
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
	private SimpleButton play = new SimpleButton("Play", new Font("tahoma",
			Font.PLAIN, 18), 10, 210, 200, 40, new Color(70, 170, 70),
			new Color(120, 255, 110));
	private SimpleButton scores = new SimpleButton("Scores", new Font("tahoma",
			Font.PLAIN, 18), 10, 260, 200, 40, new Color(70, 170, 70),
			new Color(120, 255, 110));
	private SimpleButton controls = new SimpleButton("Configure Controls",
			new Font("tahoma", Font.PLAIN, 18), 10, 310, 200, 40, new Color(70,
					170, 70), new Color(120, 255, 110));
	private SimpleButton reset = new SimpleButton("Restart", new Font("tahoma",
			Font.PLAIN, 18), 1000, 1000, 200, 20, new Color(70, 170, 70),
			new Color(120, 255, 110));
	private SimpleButton enterUsername = new SimpleButton("Enter Username",
			new Font("tahoma", Font.PLAIN, 18), 1000, 1000, 200, 20, new Color(
					70, 170, 70), new Color(120, 255, 110));
	private SimpleButton submitUsername = new SimpleButton("Submit", new Font(
			"tahoma", Font.PLAIN, 18), 1000, 1000, 200, 20, new Color(70, 170,
			70), new Color(230, 230, 80));
	private boolean f3 = false;
	private int rektUpdates = 0;
	private List<FallingRectangle> rekts = new ArrayList<FallingRectangle>();
	private List<FallingLevel> levels = new ArrayList<FallingLevel>();
	private int pauseUpdates = 0;
	private int floorzUpdates = 0;
	private boolean cheatsEnabled = false;
	static String status = "dandy";
	static String incoming = "";
	static String IP = "dahms.noip.me";
	static ServerSocket serverSocket;
	public static int inwards = 80;
	public static int outwards = 80;
	private static boolean usernameInput = false;

	public static void main(String[] args) {
		MultiTask_old sam = new MultiTask_old();
		sam.setVisible(true);
		sam.run();
	}

	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	public MultiTask_old() {
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
				while (true) {
					repaint();
					if (ups <= 0)
						ups = 40;
					sleep((20 * (40 / ups)));
				}
			}
		});

		Thread submitScore = new Thread(new Runnable() {
			public void run() {
				try {
					serverSocket = new ServerSocket(inwards);
					sendMessage("client - server socket created");
					say(10);
				} catch (Exception e) {
					e.printStackTrace();
				}
				while (true) {
					try {
						say(11);
						Socket SS_accept = serverSocket.accept();
						say(12); //cameron's does not reach here
						
						BufferedReader SS_BF = new BufferedReader(
								new InputStreamReader(
										SS_accept.getInputStream()));

						String clientMessage = SS_BF.readLine();

						if (clientMessage != null) {
							PrintStream SSPS = new PrintStream(
									SS_accept.getOutputStream());
							SSPS.println("Received");
							incoming = clientMessage;
							say(incoming);
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
						if (cheatsEnabled) {
							status = "cheaters don't count";
						}
						System.out.print(""); // Need this to work for some dumb
												// reason
						if (enterUsername.getPressed()) {
							usernameInput = true;
						}
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
							godMode = false;
							cheatsEnabled = false;
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
		submitScore.start();
	}

	private Thread screenAdjust1;
	private boolean lvl2Adjusted = false;
	private Thread screenAdjust2;
	private boolean lvl3Adjusted = false;
	private boolean[] lvlSetup = new boolean[4];
	// private Player[] players = new Player[4];
	private int gameUpdates = 0;
	private EnemyPaddle ai;
	private Ball ball;
	@SuppressWarnings("unused")
	private boolean canMove = true;
	private boolean controlsMenu;

	Player1 player1;
	Player2 player2;
	Player3 player3;
	Player4 player4;
	private boolean lvl3RightAllowed;

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
			} else if (scores.getPressed()) {
				menu = false;
				scoresMenu = true;
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
				prevLvl4LeftKey = lvl4LeftKey;
				prevLvl4RightKey = lvl4RightKey;

				UpdateTextOnControlsConfigButton();

				controls.reset();
			}
		} else if (controlsMenu) {
			f3 = false;
			// Cancel is Clicked
			if (cancel.getPressed()) {
				lvl1LeftKey = prevLvl1LeftKey;
				lvl1RightKey = prevLvl1RightKey;
				lvl2UpKey = prevLvl2UpKey;
				lvl2DownKey = prevLvl2DownKey;
				lvl3LeftKey = prevLvl3LeftKey;
				lvl3RightKey = prevLvl3RightKey;
				lvl4LeftKey = prevLvl4LeftKey;
				lvl4RightKey = prevLvl4RightKey;
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
			} else if (defaults.getPressed()) {
				lvl1LeftKey = lvl1LeftKeyDefault;
				lvl1RightKey = lvl1RightKeyDefault;
				lvl2UpKey = lvl2UpKeyDefault;
				lvl2DownKey = lvl2DownKeyDefault;
				lvl3LeftKey = lvl3LeftKeyDefault;
				lvl3RightKey = lvl3RightKeyDefault;
				lvl4LeftKey = lvl4LeftKeyDefault;
				lvl4RightKey = lvl4RightKeyDefault;
				UpdateTextOnControlsConfigButton();
				defaults.reset();
			}
		} else if (scoresMenu) {
			if (scoresBack.getPressed()) {
				scoresMenu = false;
				menu = true;
				scoresBack.reset();
			}
		}
		if (lvl1) {
			// Level Setup
			if (!lvlSetup[0]) {
				player1 = new Player1(gameWidthOriginal / 2 - 20,
						gameHeightOriginal - 49/* 354 */, 40, 40, 7,
						gameWidthOriginal - 8, 0, 0, Color.red);
				lvlSetup[0] = true;
			}

			if (!pause) {
				if (rektUpdates % 40 == 0) {
					rektUpdates = 0;
					rekts.add(new FallingRectangle(470, -100, 80));
				}

				for (int i = 0; i < rekts.size(); i++) {
					rekts.get(i).update();
					if (player1.getBounds()
							.intersects(rekts.get(i).getBounds()) && !godMode)
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
				player1.setRightBound(gameWidthOriginal - 1);// 549);
				player2 = new Player2(570, 150, 20, 100, gameWidthOriginal,
						gameWidth, 20, gameHeightOriginal, Color.blue);
				ai = new EnemyPaddle(1050, 150, 20, 100, gameHeightOriginal,
						Color.blue);
				ball = new Ball(1100 / 4 * 3, 200, 20, 2, gameWidthOriginal,
						1052, 30, gameHeightOriginal - 5, Color.pink);
				lvlSetup[1] = true;
			}
			if (!pause) {
				ball.update();
				if (ball.getX() < gameWidthOriginal + ball.getSize()
						+ ball.getSpeed()
						&& !godMode)
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
				player1.setY(gameHeightOriginal - 42);// (358);
				ball.setLowerBound(gameHeightOriginal);
				player3 = new Player3(275, 450, 30, 30, 7, gameWidthOriginal,
						gameHeightOriginal, gameHeight, Color.green);
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

				lvl3RightAllowed = true;
				for (int i = 0; i < levels.size(); i++) {
					if (levels.get(i).getRightRectLeftSide()
							.intersects(player3.getRightSide())) {
						say("right on left of right");
						lvl3RightAllowed = false;
					}

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
					if (levels.get(i).getY() + levels.get(i).getHeight() < player3
							.getY())
						levels.remove(i);
					else if (godMode)
						if (levels.get(i).getY() < gameHeightOriginal
								+ player3.getHeight() + 5)
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
				// player4 = new Player4();
				lvlSetup[3] = true;
			}

			if (!pause) {
				// Game Updates
				// Check for fail
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

		sleep(25 - (int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime()
				- startTime)); // ms
	}

	public static void sendMessage(final String text) {
		Thread t = new Thread() {
			public void run() {
				Socket client = null;
				try {
					//System.out.println(0);
					client = new Socket(IP, outwards);
				} catch (IOException e) {
					e.printStackTrace();
				}
				//System.out.println(1);
				PrintStream myPS = null;
				try {
					//System.out.println(2);
					myPS = new PrintStream(client.getOutputStream());
					myPS.println(text);
					status = "dandy";
				} catch (IOException | NullPointerException e) {
					e.printStackTrace();
					status = "could not connect";
				}
				//System.out.println(3);
	
				BufferedReader myBR = null;
				try {
					myBR = new BufferedReader(new InputStreamReader(
							client.getInputStream()));
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
		};
		t.start();		
	}

	private void UpdateTextOnControlsConfigButton() {
		// Reset Text On Buttons
		lvl1LeftControl.setText(KeyEvent.getKeyText(lvl1LeftKey));
		lvl1RightControl.setText(KeyEvent.getKeyText(lvl1RightKey));
		lvl2UpControl.setText(KeyEvent.getKeyText(lvl2UpKey));
		lvl2DownControl.setText(KeyEvent.getKeyText(lvl2DownKey));
		lvl3LeftControl.setText(KeyEvent.getKeyText(lvl3LeftKey));
		lvl3RightControl.setText(KeyEvent.getKeyText(lvl3RightKey));
		lvl4LeftControl.setText(KeyEvent.getKeyText(lvl4LeftKey));
		lvl4RightControl.setText(KeyEvent.getKeyText(lvl4RightKey));
	}

	String received;
	private int speed = 10;

	private void updateKeys() {
		if (player1 != null)
			player1.moveHorizontally(lvl1LeftHeld, lvl1RightHeld, speed);
		if (player2 != null)
			player2.moveVertically(lvl2UpHeld, lvl2DownHeld, speed);
		if (player3 != null)
			if (lvl3RightAllowed)
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
	private Font creditsFont = new Font("arial", Font.BOLD, 16);
	private Font pausedFont = new Font("tahoma", Font.PLAIN, 18); // duplicates,
																	// but helps
																	// with
																	// clarity
	private Font levelsFont = new Font("tahoma", Font.PLAIN, 18);

	private SimpleButton scoresBack = new SimpleButton("Back", pausedFont,
			gameWidth / 2 - 40, 315, 80, 40, new Color(170, 70, 70), new Color(
					230, 230, 80));

	private boolean onetimer = true;

	private int lvl1LeftKeyDefault = KeyEvent.VK_A;
	private int lvl1RightKeyDefault = KeyEvent.VK_D;
	private int lvl2UpKeyDefault = KeyEvent.VK_UP;
	private int lvl2DownKeyDefault = KeyEvent.VK_DOWN;
	private int lvl3LeftKeyDefault = KeyEvent.VK_LEFT;
	private int lvl3RightKeyDefault = KeyEvent.VK_RIGHT;
	private int lvl4LeftKeyDefault = 65;
	private int lvl4RightKeyDefault = 65;
	private int lvl1LeftKey = lvl1LeftKeyDefault;
	private int lvl1RightKey = lvl1RightKeyDefault;
	private int lvl2UpKey = lvl2UpKeyDefault;
	private int lvl2DownKey = lvl2DownKeyDefault;
	private int lvl3LeftKey = lvl3LeftKeyDefault;
	private int lvl3RightKey = lvl3RightKeyDefault;
	private int lvl4LeftKey = lvl4LeftKeyDefault;
	private int lvl4RightKey = lvl4RightKeyDefault;
	private int prevLvl1LeftKey;
	private int prevLvl1RightKey;
	private int prevLvl2UpKey;
	private int prevLvl2DownKey;
	private int prevLvl3LeftKey;
	private int prevLvl3RightKey;
	private int prevLvl4LeftKey;
	private int prevLvl4RightKey;
	private SimpleButton lvl1LeftControl = new SimpleButton(
			KeyEvent.getKeyText(lvl1LeftKey),
			new Font("tahoma", Font.PLAIN, 30), 110, 40, 175, 50, new Color(70,
					170, 70), new Color(120, 255, 110));
	private SimpleButton lvl1RightControl = new SimpleButton(
			KeyEvent.getKeyText(lvl1RightKey), new Font("tahoma", Font.PLAIN,
					30), 330, 40, 175, 50, new Color(70, 170, 70), new Color(
					120, 255, 110));
	private SimpleButton lvl2UpControl = new SimpleButton(
			KeyEvent.getKeyText(lvl2UpKey), new Font("tahoma", Font.PLAIN, 30),
			110, 105, 175, 50, new Color(70, 170, 70), new Color(120, 255, 110));
	private SimpleButton lvl2DownControl = new SimpleButton(
			KeyEvent.getKeyText(lvl2DownKey),
			new Font("tahoma", Font.PLAIN, 30), 330, 105, 175, 50, new Color(
					70, 170, 70), new Color(120, 255, 110));
	private SimpleButton lvl3LeftControl = new SimpleButton(
			KeyEvent.getKeyText(lvl3LeftKey),
			new Font("tahoma", Font.PLAIN, 30), 110, 170, 175, 50, new Color(
					70, 170, 70), new Color(120, 255, 110));
	private SimpleButton lvl3RightControl = new SimpleButton(
			KeyEvent.getKeyText(lvl3RightKey), new Font("tahoma", Font.PLAIN,
					30), 330, 170, 175, 50, new Color(70, 170, 70), new Color(
					120, 255, 110));
	private SimpleButton lvl4LeftControl = new SimpleButton(
			KeyEvent.getKeyText(lvl4LeftKey),
			new Font("tahoma", Font.PLAIN, 30), 110, 235, 175, 50, new Color(
					70, 170, 70), new Color(120, 255, 110));
	private SimpleButton lvl4RightControl = new SimpleButton(
			KeyEvent.getKeyText(lvl4RightKey), new Font("tahoma", Font.PLAIN,
					30), 330, 235, 175, 50, new Color(70, 170, 70), new Color(
					120, 255, 110));
	private SimpleButton apply = new SimpleButton("Apply", new Font("tahoma",
			Font.PLAIN, 18), gameWidth / 4 - 40, 315, 80, 40, new Color(70,
			170, 70), new Color(230, 230, 80));
	private SimpleButton cancel = new SimpleButton("Cancel", new Font("tahoma",
			Font.PLAIN, 18), gameWidth / 2 - 40, 315, 80, 40, new Color(170,
			70, 70), new Color(230, 230, 80));
	private SimpleButton defaults = new SimpleButton("Defaults", new Font(
			"tahoma", Font.PLAIN, 18), gameWidth / 4 * 3 - 40, 315, 80, 40,
			new Color(70, 70, 170), new Color(230, 230, 80));

	private void paintComponent(Graphics g) {
		// AA Text smoothing
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(defaultFont);

		if (menu) {
			g2.drawImage(Background, 0, 0, gameWidth, gameHeight, null);

			play.draw(g2);
			scores.draw(g2);
			controls.draw(g2);

			g2.setColor(Color.white);
			g2.setFont(creditsFont);
			g2.drawString("Cameron O'Neil", 220, 316 + g2.getFontMetrics()
					.getHeight() / 2);
			g2.drawString("Jack Dahms", 220, 336 + g2.getFontMetrics()
					.getHeight() / 2);
		} else if (controlsMenu) {

			g2.setColor(Color.white);
			g2.setFont(levelsFont);
			g2.drawString("Level 1", 25, 70);
			g2.drawString("Level 2", 25, 135);
			g2.drawString("Level 3", 25, 200);
			g2.drawString("Level 4", 25, 265);

			lvl1LeftControl.draw(g2);
			lvl1RightControl.draw(g2);
			lvl2UpControl.draw(g2);
			lvl2DownControl.draw(g2);
			lvl3LeftControl.draw(g2);
			lvl3RightControl.draw(g2);
			lvl4LeftControl.draw(g2);
			lvl4RightControl.draw(g2);
			apply.draw(g2);
			cancel.draw(g2);
			defaults.draw(g2);

			g2.setColor(Color.white);
			
			
			g2.drawImage(leftRight, 292, 50, 30, 30, null);
			//g2.drawString("< >", 292, 70);
			g2.drawImage(upDown, 292, 115, 30, 30, null);
			//g2.drawString("^ v", 292, 135);
			g2.drawImage(leftRight, 292, 180, 30, 30, null);
			//g2.drawString("< >", 292, 200);
			g2.drawImage(upDown, 292, 245, 30, 30, null);
			//g2.drawString("^ v", 292, 265);

			if (lvl1LeftControl.getPressed() || lvl1RightControl.getPressed()
					|| lvl2UpControl.getPressed()
					|| lvl2DownControl.getPressed()
					|| lvl3LeftControl.getPressed()
					|| lvl3RightControl.getPressed()) {
				pauseText = "Please enter a key.";
				pause = true;
			} else {
				pause = false;
			}

		} else if (scoresMenu) {
			scoresBack.draw(g2);
			g2.setColor(Color.darkGray);
			g2.fillRect(gameWidth / 2 - 150, gameHeight / 2 - 145, 300, 270);
			g2.setFont(pausedFont);

			// String[] brokenLines = null;
			// for (int i = 0; i < brokenLines.length; i++) {
			// g2.drawString(brokenLines[i], gameWidth / 2 -
			// g2.getFontMetrics().stringWidth(brokenLines[i]) / 2, gameHeight /
			// 2 + g2.getFontMetrics().getHeight() / 4
			// + g2.getFontMetrics().getHeight() * i -
			// g2.getFontMetrics().getHeight() * (brokenLines.length - 1) / 2);
			// }
		}

		if (lvl1) {
			g2.setColor(Color.white);
			g2.drawString("Score: " + score, 25, 50);
			try {
				player1.draw(g2);
			} catch (Exception e) {
			}

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
				player2.draw(g2);
				ai.draw(g2);
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

				for (int i = 0; i < levels.size(); i++) {
					levels.get(i).draw(g2);
				}
			} catch (Exception e) {
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
				g2.drawString(brokenLines[i], gameWidth / 2
						- g2.getFontMetrics().stringWidth(brokenLines[i]) / 2,
						gameHeight / 2 + g2.getFontMetrics().getHeight() / 4
								+ g2.getFontMetrics().getHeight() * i
								- g2.getFontMetrics().getHeight()
								* (brokenLines.length - 1) / 2);
			}
		}

		if (fail) {
			g2.setColor(Color.darkGray);
			g2.fillRect(gameWidth / 2 - 150, gameHeight / 2 - 75, 300, 150);
			if (usernameInput) {
				g2.setColor(Color.white);
				g2.setFont(new Font("impact", Font.BOLD, 25));
				g2.drawString("Enter Username", gameWidth / 2 - g2.getFontMetrics().stringWidth("Enter Username") / 2, gameHeight / 2 - 45);
				
				g2.setFont(new Font("tahoma", Font.PLAIN, 16));
				g2.drawString("letters, numbers, and spaces", gameWidth / 2 - g2.getFontMetrics().stringWidth("letters, numbers, and spaces") / 2, 
						gameHeight / 2 - 27);
				g2.drawString("cannot begin or end with a space", gameWidth / 2 - g2.getFontMetrics().stringWidth("cannot begin or end with a space") / 2, 
						gameHeight / 2 - 8);
				
				g2.setColor(Color.lightGray);
				g2.fillRect(gameWidth / 2 - 100, gameHeight / 2, 200, 26);
				g2.setColor(Color.black);
				g2.drawRect(gameWidth / 2 - 100, gameHeight / 2, 200, 26);
				
				g2.setFont(new Font("courier", Font.PLAIN, 16));							//16 * 16 looks arbitrary, but isn't. 
				g2.drawString(username, gameWidth / 2 - 100, gameHeight / 2 + g2.getFontMetrics().getHeight() / 16 * 16);
				
				submitUsername.draw(g2);
				if (submitUsername.getPressed()) {
					if (username.substring(0, 1).equals(" ") || username.substring(username.length() - 1, username.length()).equals(" ")) {
						submitUsername.reset();
					} else {
						sendMessage("cameron");
						submitUsername.reset();						
					}
				}
			} else {
				g2.setColor(new Color(230, 30, 30));
				g2.setFont(new Font("tahoma", Font.BOLD, 60));
				g2.drawString("FAIL", gameWidth / 2
						- g2.getFontMetrics().stringWidth("FAIL") / 2,
						gameHeight / 2 - g2.getFontMetrics().getHeight() / 4);

				g2.setColor(Color.white);
				g2.setFont(pausedFont);
				g2.drawString("final score: " + score, gameWidth / 2 - g2.getFontMetrics().stringWidth("final score: " + score) / 2,
						gameHeight / 2 + 7);

				if (onetimer) {
					submitUsername.setX(gameWidth / 2 - 100);
					submitUsername.setY(gameHeight / 2 + 46);
					reset.setX(gameWidth / 2 - 100);
					reset.setY(gameHeight / 2 + 46);
					enterUsername.setX(gameWidth / 2 - 100);
					enterUsername.setY(gameHeight / 2 + 18);
					onetimer = false;
				}
				if (status.equals("dandy")) { // status.equals("dandy")
					enterUsername.draw(g2);
				} else {
					g2.drawString(status, gameWidth / 2
							- g2.getFontMetrics().stringWidth(status) / 2,
							gameHeight / 2 + 11
									+ g2.getFontMetrics().getHeight());
				}
				reset.draw(g2);
			}

		}

		g2.setColor(Color.WHITE);
		g2.setFont(defaultFont);
		if (f3) {
			g2.drawString("FPS: " + fps, 25, 70);
			g2.drawString("UPS: " + ups + "/40", 25, 90);
			g2.drawString(mouseX + ", " + mouseY, 25, 110);
			g2.drawString("Cheats: " + cheatsEnabled, 25, 110);
			if (cheatsEnabled) {
				g2.drawString("Godmode: " + godMode, 25, 150);
			}
		}

		g2.drawString(mouseX + ", " + mouseY, gameWidth - 60, gameHeight - 10);

		frames++;
	}

	private void sleep(int i) {
		try {
			Thread.sleep(i);
		} catch (Exception e) {
			// say("Sleep Error: " + i);
		}
	}

	private boolean lvl1LeftHeld;
	private boolean lvl1RightHeld;
	private boolean lvl2UpHeld;
	private boolean lvl2DownHeld;
	private boolean lvl3LeftHeld;
	private boolean lvl3RightHeld;
	private boolean godMode;

	String username = "";

	public void keyPressed(KeyEvent e) {
		if (usernameInput) {
			/**
			 * Username can only consist of spaces, numbers, and letters
			 * Username must be 19 or fewer characters
			 */
			if (e.getKeyCode() > 64 && e.getKeyCode() < 91) { // a - z
				if (e.isShiftDown())
					username += KeyEvent.getKeyText(e.getKeyCode());
				else
					username += KeyEvent.getKeyText(e.getKeyCode())
							.toLowerCase();
			} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				if (username.length() > 0)
					username = username.substring(0, username.length() - 1);
			} else if (e.getKeyCode() > 47 && e.getKeyCode() < 58) {
				username += KeyEvent.getKeyText(e.getKeyCode());

			} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				username += " ";
			}
			if (username.length() > 20) {
				username = username.substring(0, 20);
			}
		} else if (controlsMenu) {
			if (lvl1LeftControl.getPressed()) {
				lvl1LeftKey = e.getKeyCode();
				lvl1LeftControl.setText(KeyEvent.getKeyText(e.getKeyCode()));
				say("Left Key set to: " + KeyEvent.getKeyText(e.getKeyCode())
						+ " (" + e.getKeyCode() + ")");
				lvl1LeftControl.reset();
			} else if (lvl1RightControl.getPressed()) {
				lvl1RightKey = e.getKeyCode();
				lvl1RightControl.setText(KeyEvent.getKeyText(e.getKeyCode()));
				say("Right Key set to: " + KeyEvent.getKeyText(e.getKeyCode())
						+ " (" + e.getKeyCode() + ")");
				lvl1RightControl.reset();
			} else if (lvl2UpControl.getPressed()) {
				lvl2UpKey = e.getKeyCode();
				lvl2UpControl.setText(KeyEvent.getKeyText(e.getKeyCode()));
				say("Up Key set to: " + KeyEvent.getKeyText(e.getKeyCode())
						+ " (" + e.getKeyCode() + ")");
				lvl2UpControl.reset();
			} else if (lvl2DownControl.getPressed()) {
				lvl2DownKey = e.getKeyCode();
				lvl2DownControl.setText(KeyEvent.getKeyText(e.getKeyCode()));
				say("Down Key set to: " + KeyEvent.getKeyText(e.getKeyCode())
						+ " (" + e.getKeyCode() + ")");
				lvl2DownControl.reset();
			} else if (lvl3LeftControl.getPressed()) {
				lvl3LeftKey = e.getKeyCode();
				lvl3LeftControl.setText(KeyEvent.getKeyText(e.getKeyCode()));
				say("Left Key set to: " + KeyEvent.getKeyText(e.getKeyCode())
						+ " (" + e.getKeyCode() + ")");
				lvl3LeftControl.reset();
			} else if (lvl3RightControl.getPressed()) {
				lvl3RightKey = e.getKeyCode();
				lvl3RightControl.setText(KeyEvent.getKeyText(e.getKeyCode()));
				say("Right Key set to: " + KeyEvent.getKeyText(e.getKeyCode())
						+ " (" + e.getKeyCode() + ")");
				lvl3RightControl.reset();
			}
		} else if (scoresMenu) {

		} else {
			if (pause) {
				if (pauseUpdates > 60)
					pause = false;
			}

			int keyCode = e.getKeyCode();

			switch (keyCode) {
			case (KeyEvent.VK_F):
				if (fail)
					reset.setPressed(true);
				break;
			case (KeyEvent.VK_F3):
				f3 = !f3; // shows stats
				break;
			}

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

			// if (e.isControlDown() && e.isAltDown() && e.isShiftDown() &&
			if (keyCode == KeyEvent.VK_C)
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

	int mouseX;
	int mouseY;

	public void mouseMoved(MouseEvent e) {

		mouseX = e.getX();
		mouseY = e.getY();

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
			lvl4LeftControl.isHovering(e);
			lvl4RightControl.isHovering(e);
			apply.isHovering(e);
			cancel.isHovering(e);
			defaults.isHovering(e);
		} else if (scoresMenu) {
			scoresBack.isHovering(e);
		} else if (usernameInput) {
			submitUsername.isHovering(e);
		} else if (fail) {
			reset.isHovering(e);
			enterUsername.isHovering(e);

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
			lvl4LeftControl.isPressed(e);
			lvl4RightControl.isPressed(e);
			apply.isPressed(e);
			cancel.isPressed(e);
			defaults.isPressed(e);
		} else if (scoresMenu) {
			scoresBack.isPressed(e);
		} else if (usernameInput) {
			submitUsername.isPressed(e);
		} else if (fail) {
			reset.isPressed(e);
			enterUsername.isPressed(e);
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

	private static void say(Object s) {
		System.out.println(s);
	}
}
