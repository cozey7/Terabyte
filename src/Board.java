/*
 * Author: Michael Chen, Andy Ding, Clarence Choy
 * Date: 05/24/2020
 * Rev:
 * Notes: Sets up the board
 */
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

import sun.audio.AudioData;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.audio.ContinuousAudioDataStream;

public class Board extends JPanel implements ActionListener, KeyListener {
	//Fields
	private JButton start;
	ArrayList<Consumable> consumables;
	private ArrayList<Wall> walls;
	public int runtime = 0; //The amount of frames the game has been running for.
	private BufferedImage background;
	private Clip clip; // For background music
	private Pacman pacman;
	private Ghost blinky, pinky, inky, clyde;
	public int gameState; // -1 = not started, 0 = running, 1 = victory/death, 2 - death animation
	private int ghostCombo;
	private Timer timer; 
	private int keyPressed;
	private int keyRequest;
	private int score, highScore, lives, extraLife = 10000;
	public static final int DRAWING_WIDTH = 540;
	public static final int DRAWING_HEIGHT = 759;

	/*
	 * Author: Michael Chen
	 * Notes: Constructor
	 */
	public Board() {
		score = 0;
		highScore = 0;
		start = new JButton("Play!");
		start.addActionListener(this);
		start.setSize(100, 50);
		start.setFont(new Font("TimesRoman", Font.BOLD, 20));
		start.setOpaque(false);
		start.setBackground(Color.YELLOW);
		start.setForeground(Color.YELLOW);
		start.setFocusPainted(false);
		this.addKeyListener(this);
		this.setFocusable(true);
		this.requestFocus();
		startGame();
	}

	/*
	 * Author: Michael Chen
	 * Notes: Method that starts the game and sets up the board
	 */
	public void startGame() {
		if (lives == 0) lives = 3;
		ghostCombo = 0;
		runtime = 0;
		keyPressed = KeyEvent.VK_RIGHT;
		keyRequest = KeyEvent.VK_RIGHT;
		gameState = -1;

		try {
			background = ImageIO.read(new File("board.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		walls = new ArrayList<Wall>();
		consumables = new ArrayList<Consumable>();


		createWalls();
		createConsumables();
		spawnEntities();

		timer = new Timer(16, this);
		timer.setInitialDelay(4217);
		pacman.setMoveState(Entity.RIGHT);
		start.setVisible(true);

		add(start);
	}

	/*
	 * Author: Michael Chen
	 * Method that resets board when pacman dies
	 */
	private void deathSequence() {
		lives--;
		runtime = 0;
		spawnEntities();
		if (lives == 0) {
			if (score > highScore) highScore = score;
			score = 0;
			Timer t = new Timer(3000, e -> {
				startGame();
				repaint();
			});
			t.setRepeats(false);
			t.start();
		}
		else {
			timer = new Timer(16, this);
			timer.setInitialDelay(1000);
			timer.start();
		}
	}

	/*
	 * Author: Michael Chen
	 * Notes: Spawns the entities on the map
	 */
	private void spawnEntities() {
		pacman = new Pacman(256, 324, "pacmanR.gif", "pacmanL.gif", "pacmanU.gif", "pacmanD.gif");
		blinky = new Ghost(256, 207, "blinky.png", "blinky.png", "blinky.png", "blinky.png");
		pinky = new Ghost(220, 270, "pinky.png", "pinky.png", "pinky.png", "pinky.png");
		inky = new Ghost(256, 270, "inky.png", "inky.png", "inky.png", "inky.png");
		clyde = new Ghost(292, 270, "clyde.png", "clyde.png", "clyde.png", "clyde.png");
	}

	/*
	 * Author: Clarence Choy & Michael Chen
	 * Notes: User controls direction of pacman, determines what happens when pacman consumes a powerpellet
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		//Pacman controls
		if (gameState == 0) {
			if (runtime == 1) {
				File sirenPath = new File("pacman_siren.wav");
				if (sirenPath.exists()) {

					try {
						AudioInputStream input = AudioSystem.getAudioInputStream(sirenPath);
						clip = AudioSystem.getClip();
						clip.open(input);
						clip.start();
						clip.loop(Clip.LOOP_CONTINUOUSLY);
					} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if (keyPressed == KeyEvent.VK_LEFT && !pacman.isColliding(walls, Entity.LEFT)) {
				pacman.moveSideways(-2);
				pacman.setMoveState(Entity.LEFT);
			}

			if (keyPressed == KeyEvent.VK_RIGHT && !pacman.isColliding(walls, Entity.RIGHT)) {
				pacman.moveSideways(2);
				pacman.setMoveState(Entity.RIGHT);
			}

			if (keyPressed == KeyEvent.VK_UP && !pacman.isColliding(walls, Entity.UP)) {
				pacman.moveForward(-2);
				pacman.setMoveState(Entity.UP);
			}

			if (keyPressed == KeyEvent.VK_DOWN && !pacman.isColliding(walls, Entity.DOWN)) {
				pacman.moveForward(2);
				pacman.setMoveState(Entity.DOWN);
			}

			//Pacman eats a powerpellet
			if(pacman.getPowerPelletEaten()) {
				pacman.setEnergizedTime(pacman.getEnergizedTime() + 1);
				if(pacman.getEnergizedTime() == 1) {
					ghostCombo = 0;
					clip.stop();
					File sirenPath = new File("pacman_large_pellet.wav");
					if (sirenPath.exists()) {

						try {
							AudioInputStream input = AudioSystem.getAudioInputStream(sirenPath);
							clip = AudioSystem.getClip();
							clip.open(input);
							clip.start();
							clip.loop(Clip.LOOP_CONTINUOUSLY);
						} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					blinky = new Ghost(blinky.getX(), blinky.getY(), "frightenedGhost.png", "frightenedGhost.png", "frightenedGhost.png", "frightenedGhost.png");
					pinky = new Ghost(pinky.getX(), pinky.getY(), "frightenedGhost.png", "frightenedGhost.png", "frightenedGhost.png", "frightenedGhost.png");
					inky = new Ghost(inky.getX(), inky.getY(), "frightenedGhost.png", "frightenedGhost.png", "frightenedGhost.png", "frightenedGhost.png");
					clyde = new Ghost(clyde.getX(), clyde.getY(), "frightenedGhost.png", "frightenedGhost.png", "frightenedGhost.png", "frightenedGhost.png");
					blinky.setVulnerability(true);
					pinky.setVulnerability(true);
					inky.setVulnerability(true);
					clyde.setVulnerability(true);
				}

				if (pacman.getEnergizedTime() >= 470) {
					if ((550 - pacman.getEnergizedTime()) % 20 <= 10) {
						if (blinky.getVulnerabilty()) {
							blinky = new Ghost(blinky.getX(), blinky.getY(), "frightenedGhostFade.png", "frightenedGhostFade.png", "frightenedGhostFade.png", "frightenedGhostFade.png");
							blinky.setVulnerability(true);
						}
						if(pinky.getVulnerabilty()) {
							pinky = new Ghost(pinky.getX(), pinky.getY(), "frightenedGhostFade.png", "frightenedGhostFade.png", "frightenedGhostFade.png", "frightenedGhostFade.png");
							pinky.setVulnerability(true);
						}
						if (inky.getVulnerabilty()) {
							inky = new Ghost(inky.getX(), inky.getY(), "frightenedGhostFade.png", "frightenedGhostFade.png", "frightenedGhostFade.png", "frightenedGhostFade.png");
							inky.setVulnerability(true);
						}
						if (clyde.getVulnerabilty()) {
							clyde = new Ghost(clyde.getX(), clyde.getY(), "frightenedGhostFade.png", "frightenedGhostFade.png", "frightenedGhostFade.png", "frightenedGhostFade.png");
							clyde.setVulnerability(true);
						}
					}
					else {
						if (blinky.getVulnerabilty()) {
							blinky = new Ghost(blinky.getX(), blinky.getY(), "frightenedGhost.png", "frightenedGhost.png", "frightenedGhost.png", "frightenedGhost.png");
							blinky.setVulnerability(true);
						}
						if(pinky.getVulnerabilty()) {
							pinky = new Ghost(pinky.getX(), pinky.getY(), "frightenedGhost.png", "frightenedGhost.png", "frightenedGhost.png", "frightenedGhost.png");
							pinky.setVulnerability(true);
						}
						if (inky.getVulnerabilty()) {
							inky = new Ghost(inky.getX(), inky.getY(), "frightenedGhost.png", "frightenedGhost.png", "frightenedGhost.png", "frightenedGhost.png");
							inky.setVulnerability(true);
						}
						if (clyde.getVulnerabilty()) {
							clyde = new Ghost(clyde.getX(), clyde.getY(), "frightenedGhost.png", "frightenedGhost.png", "frightenedGhost.png", "frightenedGhost.png");
							clyde.setVulnerability(true);
						}
					}
				}

				if(pacman.getEnergizedTime() == 550) {
					clip.stop();
					File sirenPath = new File("pacman_siren.wav");
					if (sirenPath.exists()) {

						try {
							AudioInputStream input = AudioSystem.getAudioInputStream(sirenPath);
							clip = AudioSystem.getClip();
							clip.open(input);
							clip.start();
							clip.loop(Clip.LOOP_CONTINUOUSLY);
						} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					blinky = new Ghost(blinky.getX(), blinky.getY(), "blinky.png", "blinky.png", "blinky.png", "blinky.png");
					pinky = new Ghost(pinky.getX(), pinky.getY(), "pinky.png", "pinky.png", "pinky.png", "pinky.png");
					inky = new Ghost(inky.getX(), inky.getY(), "inky.png", "inky.png", "inky.png", "inky.png");
					clyde = new Ghost(clyde.getX(), clyde.getY(), "clyde.png", "clyde.png", "clyde.png", "clyde.png");
					pacman.setPowerPelletEaten(false);
				}
			}

			if(pacman.getEnergizedTime() < 550) {
				if(blinky.getVulnerabilty() == true) 
					blinky.wander(this, 1);
				else
					if (runtime % (60 * 8) >= (60 * 3)) blinky.chase(blinky.getX(), blinky.getY(), this);
				if(pinky.getVulnerabilty() == true) 
					pinky.wander(this, 1);
				else
					pinky.wander(this, 2);
				if(inky.getVulnerabilty() == true) 
					inky.wander(this, 1);
				else
					inky.wander(this, 2);
				if(clyde.getVulnerabilty() == true) 
					clyde.wander(this, 1);
				else
					clyde.wander(this, 2);
			}else {

				if (runtime % (60 * 8) >= (60 * 3)) blinky.chase(blinky.getX(), blinky.getY(), this);
				else blinky.wander(this, 2);
				pinky.wander(this, 2);
				inky.wander(this, 2);
				clyde.wander(this, 2);

			}
		} else if (gameState == 1){
			clip.stop();
		}

		checkEntities();
		checkKey();

		//Start of game
		if (event.getSource() == start) {
			gameState = 0;
			start.setVisible(false);
			remove(start);
			revalidate();

			EasySound intro = new EasySound("pacman_beginning.wav");
			intro.play();

			timer.start();
		}

		//Repaints
		repaint();
		runtime++;
	}

	/*
	 * Author: Michael Chen
	 * Notes: Scoreboard and sets up the game
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Color.BLACK);

		int width = getWidth();
		int height = getHeight();

		double xRatio = (double)width/DRAWING_WIDTH;
		double yRatio = (double)height/DRAWING_HEIGHT;
		double ratio = Math.min(xRatio, yRatio);

		int x = (int)(width - background.getWidth(null) * ratio) / 2;
		int y = (int)(height - background.getHeight(null) * ratio) / 2;

		Graphics2D g2 = (Graphics2D)g;
		AffineTransform at = g2.getTransform();


		g2.drawImage(background, x, y, (int)(background.getWidth(null) * ratio), (int)(background.getHeight(null) * ratio), null);
		g2.translate(x, y);
		g2.scale(ratio,ratio);
		for(Consumable c: consumables) {
			c.draw(g2, this);
		}

		if (isEmpty()) {
			gameState = 1;
			if (runtime > 360)
				runtime = 0;
			if (runtime % 60 >= 30) {
				for (Wall wall : walls) {
					wall.draw(g2);
				}
			}

			if (runtime >= 180) {
				timer.stop();
				startGame();
				repaint();
			}
		}
		start.setBounds(x + (int)((background.getWidth(null) - 100) * ratio)/2, y + (int)((background.getHeight(null) + 100) * ratio)/2, (int) (100 * ratio), (int) (50 * ratio));
		try {
			Font f = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(new File("Retro Gaming.ttf"))).deriveFont(Font.CENTER_BASELINE, (int)(20 * ratio));
			g.setFont(f);
			start.setFont(f);
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (lives == 0) {
			g.setColor(Color.RED);
			int gmWidth = g.getFontMetrics().stringWidth("GAME OVER");
			g.drawString("GAME OVER", (DRAWING_WIDTH - gmWidth) / 2, 344);
		}
		else {
			if (pacman != null) pacman.draw(g2, this, this);
			if (gameState != 2) {
				blinky.draw(g2, this);
				inky.draw(g2, this);
				pinky.draw(g2, this);
				clyde.draw(g2, this);
			}
		}

		//Scoreboard
		g.setColor(Color.WHITE);
		int hsWidth = g.getFontMetrics().stringWidth("HIGH SCORE");
		g.drawString("SCORE", 0, -50);
		g.drawString(score + "", 0, -25);
		g.drawString("HIGH SCORE", (DRAWING_WIDTH - hsWidth) / 2, -50);
		g.drawString(highScore + "", (DRAWING_WIDTH - hsWidth) / 2, -25);
		for (int i = 0; i < lives - 1; i++) {
			g.drawImage(new ImageIcon("pacmanR.png").getImage(), 10 + (38 * i), 610, 28, 30, this);
		}

		if ((pacman.isColliding(blinky) && (pacman.getEnergizedTime() >=  550 || !blinky.getVulnerabilty())) ||
				(pacman.isColliding(pinky) && (pacman.getEnergizedTime() >=  550 || !pinky.getVulnerabilty())) ||
				(pacman.isColliding(inky) && (pacman.getEnergizedTime() >=  550 || !inky.getVulnerabilty())) ||
				(pacman.isColliding(clyde) && (pacman.getEnergizedTime() >=  550 || !clyde.getVulnerabilty()))) {
			if (gameState == 0) {
				runtime = 0;
				gameState = 1;
			}

			if  (runtime == 60) {
				gameState = 2;
				pacman = new Pacman(pacman.getX(), pacman.getY(), "pacmanDeath.gif");
				EasySound death = new EasySound("pacman_death.wav");
				death.play();
			} else if (runtime >= 153) {
				repaint();
				gameState = 0;
				timer.stop();
				deathSequence();
				repaint();
			}
		} else if(pacman.isColliding(blinky) && (pacman.getEnergizedTime() < 550 && blinky.getVulnerabilty())) {
			blinky.setIsDead(true);
			EasySound eat = new EasySound("pacman_ghost_eat.wav");
			eat.play();
			score = score + 200 * (int)Math.pow(2, ghostCombo);
			timer.stop();
			timer = new Timer(16, this);
			timer.setInitialDelay(1000);
			timer.start();
			g2.setColor(new Color(33, 255, 255));
			g2.setFont(new Font(g2.getFont().getName(), g2.getFont().getStyle(), 17));
			g2.drawString(200 * (int)Math.pow(2, ghostCombo) + "", blinky.getX(), blinky.getY());
			blinky = new Ghost(256, 207, "blinky.png", "blinky.png", "blinky.png", "blinky.png");
			ghostCombo++;
		}
		else if(pacman.isColliding(pinky) && (pacman.getEnergizedTime() < 550 && pinky.getVulnerabilty())) {
			pinky.setIsDead(true);
			EasySound eat = new EasySound("pacman_ghost_eat.wav");
			eat.play();
			score = score + 200 * (int)Math.pow(2, ghostCombo);
			timer.stop();
			timer = new Timer(16, this);
			timer.setInitialDelay(1000);
			timer.start();
			g2.setColor(new Color(33, 255, 255));
			g2.setFont(new Font(g2.getFont().getName(), g2.getFont().getStyle(), 17));
			g2.drawString(200 * (int)Math.pow(2, ghostCombo) + "", pinky.getX(), pinky.getY());
			pinky = new Ghost(220, 270, "pinky.png", "pinky.png", "pinky.png", "pinky.png");
			ghostCombo++;
		}
		else if(pacman.isColliding(inky) && (pacman.getEnergizedTime() < 550 && inky.getVulnerabilty())) {
			inky.setIsDead(true);
			EasySound eat = new EasySound("pacman_ghost_eat.wav");
			eat.play();
			score = score + 200 * (int)Math.pow(2, ghostCombo);
			timer.stop();
			timer = new Timer(16, this);
			timer.setInitialDelay(1000);
			timer.start();
			g2.setColor(new Color(33, 255, 255));
			g2.setFont(new Font(g2.getFont().getName(), g2.getFont().getStyle(), 17));
			g2.drawString(200 * (int)Math.pow(2, ghostCombo) + "", inky.getX(), inky.getY());
			inky = new Ghost(256, 270, "inky.png", "inky.png", "inky.png", "inky.png");
			ghostCombo++;
		}
		else if(pacman.isColliding(clyde) && (pacman.getEnergizedTime() < 550 && clyde.getVulnerabilty())) {
			clyde.setIsDead(true);
			EasySound eat = new EasySound("pacman_ghost_eat.wav");
			eat.play();
			score = score + 200 * (int)Math.pow(2, ghostCombo);
			timer.stop();
			timer = new Timer(16, this);
			timer.setInitialDelay(1000);
			timer.start();
			g2.setColor(new Color(33, 255, 255));
			g2.setFont(new Font(g2.getFont().getName(), g2.getFont().getStyle(), 17));
			g2.drawString(200 * (int)Math.pow(2, ghostCombo) + "", clyde.getX(), clyde.getY());
			clyde = new Ghost(292, 270, "clyde.png", "clyde.png", "clyde.png", "clyde.png");
			ghostCombo++;
		}

		if(score >= extraLife) {
			lives++;
			extraLife += 10000;
		}

		g2.setTransform(at);
	}

	/*
	 * Author: Clarence Choy & Michael Chen
	 * Notes: Checks the positions of pacman and the ghosts and determines death and when the entities teleport across the screen
	 */
	public void checkEntities() {

		int x = pacman.getX() + pacman.getWidth()/2;
		if(x < 0) {
			pacman.moveSideways(DRAWING_WIDTH);
		} else if(x > DRAWING_WIDTH) {
			pacman.moveSideways(-DRAWING_WIDTH);
		}
		if(blinky.getX() + blinky.getWidth()/2 < 0) {
			blinky.moveSideways(DRAWING_WIDTH);
		} else if(blinky.getX() + blinky.getWidth()/2 > DRAWING_WIDTH) {
			blinky.moveSideways(-DRAWING_WIDTH);
		}
		if(pinky.getX() + pinky.getWidth()/2 < 0) {
			pinky.moveSideways(DRAWING_WIDTH);
		} else if(pinky.getX() + pinky.getWidth()/2 > DRAWING_WIDTH) {
			pinky.moveSideways(-DRAWING_WIDTH);
		}
		if(inky.getX() + inky.getWidth()/2 < 0) {
			inky.moveSideways(DRAWING_WIDTH);
		} else if(inky.getX() + inky.getWidth()/2 > DRAWING_WIDTH) {
			inky.moveSideways(-DRAWING_WIDTH);
		}
		if(clyde.getX() + clyde.getWidth()/2 < 0) {
			clyde.moveSideways(DRAWING_WIDTH);
		} else if(clyde.getX() + clyde.getWidth()/2 > DRAWING_WIDTH) {
			clyde.moveSideways(-DRAWING_WIDTH);
		}

		pacman.consume(consumables, this);
	}

	/*
	 * Author: Clarence Choy
	 * Notes: Checks the key that was pressed
	 */
	public void checkKey() {
		if (keyRequest == KeyEvent.VK_LEFT && !pacman.isColliding(walls, Entity.LEFT)) {
			if (keyPressed != KeyEvent.VK_LEFT) {
				keyPressed = KeyEvent.VK_LEFT;
			}

		}
		else if (keyRequest == KeyEvent.VK_RIGHT && !pacman.isColliding(walls, Entity.RIGHT)) {
			if (keyPressed != KeyEvent.VK_RIGHT) {
				keyPressed = KeyEvent.VK_RIGHT;
			}

		}
		else if (keyRequest == KeyEvent.VK_UP && !pacman.isColliding(walls, Entity.UP)) {
			if (keyPressed != KeyEvent.VK_UP) {
				keyPressed = KeyEvent.VK_UP;
			}

		}
		else if (keyRequest == KeyEvent.VK_DOWN && !pacman.isColliding(walls, Entity.DOWN)) {
			if (keyPressed != KeyEvent.VK_DOWN) {
				keyPressed = KeyEvent.VK_DOWN;
			}

		}
	}

	//Determines what key has been pressed
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			keyRequest = KeyEvent.VK_LEFT;
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			keyRequest = KeyEvent.VK_RIGHT;
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			keyRequest = KeyEvent.VK_UP;
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			keyRequest = KeyEvent.VK_DOWN;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}

	/*
	 * Author: Michael Chen
	 * Notes: Creates all the walls on the screen
	 */
	public void createWalls() {

		//Border
		Wall wall1 = new Wall(0, 0, 535, 9);
		walls.add(wall1);
		Wall wall2 = new Wall(0, 9, 10, 174);
		walls.add(wall2);
		Wall wall3 = new Wall(0, 183, 106, 77);
		walls.add(wall3);
		Wall wall4 = new Wall(0, 300, 106, 76);
		walls.add(wall4);
		Wall wall5 = new Wall(0, 376, 10, 212);
		walls.add(wall5);
		Wall wall6 = new Wall(0, 588, 535, 9);
		walls.add(wall6);

		//Walls
		Wall wall7 = new Wall(48, 48, 58, 39);
		walls.add(wall7);
		Wall wall8 = new Wall(144, 48, 78, 39);
		walls.add(wall8);
		Wall wall9 = new Wall(261, 9, 19, 78);
		walls.add(wall9);
		Wall wall10 = new Wall(48, 126, 58, 18);
		walls.add(wall10);
		Wall wall11 = new Wall(144, 126, 20, 134);
		walls.add(wall11);
		Wall wall12 = new Wall(164, 183, 58, 19);
		walls.add(wall12);
		Wall wall13 = new Wall(202, 126, 134, 18);
		walls.add(wall13);
		Wall wall14 = new Wall(261, 145, 20, 57);
		walls.add(wall14);

		Wall wall18 = new Wall(144, 300, 20, 76);
		walls.add(wall18);
		Wall wall19 = new Wall(202, 358, 136, 18);
		walls.add(wall19);
		Wall wall20 = new Wall(261, 376, 19, 58);
		walls.add(wall20);
		Wall wall21 = new Wall(48, 415, 58, 19);
		walls.add(wall21);
		Wall wall22 = new Wall(86, 434, 20, 58);
		walls.add(wall22);
		Wall wall23 = new Wall(144, 415, 78, 19);
		walls.add(wall23);
		Wall wall24 = new Wall(10, 473, 39, 19);
		walls.add(wall24);
		Wall wall25 = new Wall(203, 473, 134, 19);
		walls.add(wall25);
		Wall wall26 = new Wall(261, 491, 19, 58);
		walls.add(wall26);
		Wall wall27 = new Wall(48, 531, 174, 18);
		walls.add(wall27);
		Wall wall28 = new Wall(144, 473, 22, 58);
		walls.add(wall28);


		//Ghost Box
		Wall wall15 = new Wall(202, 241, 9, 78);
		walls.add(wall15);
		Wall wall16 = new Wall(211, 241, 39, 11);
		walls.add(wall16);
		Wall wall17 = new Wall(211, 310, 118, 9);
		walls.add(wall17);

		//Reflection across the center
		ArrayList<Wall> tempWalls = new ArrayList<>(walls);
		for (Wall w: tempWalls) {
			int reflectionX = background.getWidth(null) - w.getX() - w.getWidth();
			walls.add(new Wall(reflectionX, w.getY(), w.getWidth(), w.getHeight()));
		}

		//Inside of the box (must be added last)
		GhostWall wall29 = new GhostWall(250, 241, 40, 9);
		walls.add(wall29);
		GhostWall wall30 = new GhostWall(211, 251, 118, 57);
		walls.add(wall30);
	}

	/*
	 * Author: Andy Ding
	 * Notes: Creates the consumables on the board
	 */
	public void createConsumables() {

		//Dots
		int[] yValues = {30, 70, 105, 135, 165, 195, 225, 253, 280, 310, 340, 367, 395, 426, 450, 480, 510, 540, 570};
		int count, y;

		for(int x = 0; x < background.getWidth(null); x += 30) 
			for(int i = 0; i < yValues.length; i++) {

				count = 0;
				y = yValues[i];
				for(Wall w: walls)
					if (w.getX() <= x && w.getX() + w.getWidth() >= x) 
						if (y <= w.getY() + w.getHeight() && y >= w.getY()) 
							count++;
				if(count == 0 && (x != 270 || y != 340) && (x != 30 || y != 70) 
						&& (x != 510 || y != 70) && (x != 510 || y != 450) && (x != 30 || y != 450)) {
					Dot dot = new Dot("dot.png", x, y, 3, 3);
					consumables.add(dot);
				}
			}

		//Power pellets
		Powerpellet powerpellet1 = new Powerpellet("powerpellet.png", 22, 62, 15, 15);
		consumables.add(powerpellet1);
		Powerpellet powerpellet2 = new Powerpellet("powerpellet.png", 503, 62, 15, 15);
		consumables.add(powerpellet2);
		Powerpellet powerpellet3 = new Powerpellet("powerpellet.png", 502, 442, 15, 15);
		consumables.add(powerpellet3);
		Powerpellet powerpellet4 = new Powerpellet("powerpellet.png", 22, 442, 15, 15);
		consumables.add(powerpellet4);

		Cherry cherry = new Cherry("cherry.png", 253, 323, 35, 35);
		consumables.add(cherry);
		cherry.toggleVisibility();
	}

	//Returns the walls
	public ArrayList<Wall> getWalls() {
		return walls;
	}

	//Returns pacman
	public Pacman getPacman() {
		return pacman;
	}

	//Sets the score
	public void setScore(int score) {
		this.score = score;
	}

	//Gets the score
	public int getScore() {
		return score;
	}

	//Returns runtime
	public int getRuntime() {
		return runtime;
	}

	//Returns game state
	public int getGameState() {
		return gameState;
	}

	//Checks if there are any consumables left on the board
	public boolean isEmpty() {
		for (Consumable c : consumables) {
			if (c.isVisible()) return false;
		}
		return true;
	}

}