/*
 * Author: Michael Chen and Clarence Choy
 * Date: 05/24/2020
 * Rev:
 * Notes: Represents any entity in pacman, superclass of ghost and pacman
 */
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class Entity extends MovingImage{
	//Fields
	private ArrayList<BufferedImage> framesR, framesL, framesU, framesD;
	private int speed;
	private int stopDistance;
	private String image;
	private double velX, velY, x, y;
	private int width, height;
	private int moveState = Entity.LEFT;
	private boolean isDead = false;
	public static final int RIGHT = 0;
	public static final int UP = 1;
	public static final int LEFT = 2;
	public static final int DOWN = 3;

	//Constructors
	public Entity(int x, int y, int width, int height, String fileName) {
		super(fileName, x, y, width, height);
		image = fileName;
		speed = 6;
		stopDistance = 2;
		velX = 0;
		velY = 0;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		framesR = createFrames(fileName);
		framesL = createFrames(fileName);
		framesU = createFrames(fileName);
		framesD = createFrames(fileName);
	}
	
	public Entity(int x, int y,int width, int height, String fileNameR, String fileNameL, String fileNameU, String fileNameD) {
		super(fileNameR, x, y, width, height);
		image = fileNameR;
		speed = 6;
		stopDistance = 2;
		velX = 0;
		velY = 0;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		framesR = createFrames(fileNameR);
		framesL = createFrames(fileNameL);
		framesU = createFrames(fileNameU);
		framesD = createFrames(fileNameD);
	}

	/*
	 * Author: Clarence Choy
	 * Notes: Moves the entity forward by a specified distance
	 */
	public void moveForward(int distance) {
		velY += distance;
		y += distance;
		moveByAmount(0, (int)velY);
		velY = 0;
	}

	/*
	 * Author: Clarence Choy
	 * Notes: Moves the entity sideways by a specified distance
	 */
	public void moveSideways(int distance) {
		velX += distance;
		x += distance;
		moveByAmount((int)velX, 0);
		velX = 0;
	}

	//Sets the move state
	public void setMoveState(int moveState) {
		this.moveState = moveState;
	}

	/*
	 * Author: Michael Chen
	 * Notes: Splits a gif file into frames
	 */
	public ArrayList<BufferedImage> createFrames(String path) {
		ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>();
		if (this instanceof Pacman) {

			ImageReader reader = ImageIO.getImageReadersBySuffix("GIF").next();
			try {
				ImageInputStream in = ImageIO.createImageInputStream(new File(path));
				reader.setInput(in);

				for (int i = 0, count = reader.getNumImages(true); i < count; i++)
				{
					BufferedImage image = reader.read(i);
					frames.add(image);
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		//Pacman's death animation
		if (path.toLowerCase().contains("death")) {
			ArrayList<BufferedImage> deathFrames = new ArrayList<BufferedImage>();
			for (BufferedImage image : frames) {
				for (int i = 0; i < 3; i++) {
					deathFrames.add(image);
				}
			}
			return deathFrames;
		}
		return frames;
	}
	
	/*
	 * Author: Michael Chen
	 * Notes: Loops and draws the splitted frames of the gifs
	 */
	public void draw(Graphics g, ImageObserver io, Board board) {
		ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>();
		if (getMoveState() == LEFT) frames = framesL;
		else if (getMoveState() == RIGHT) frames = framesR;
		else if (getMoveState() == UP) frames = framesU;
		else if (getMoveState() == DOWN) frames = framesD;

		int runTime = board.getRuntime();
		if (this instanceof Pacman) {
			if (!frames.isEmpty()) {
				BufferedImage image = frames.get(0);
				g.drawImage(image, (int)x, (int)y, 28, 30, io);
				if (runTime % 3 == 0 && (board.getGameState() == 0|| board.getGameState() == 2)) {
					if (!this.isColliding(board.getWalls(), getMoveState())) {
						frames.remove(0);
						if (!this.image.toLowerCase().contains("death")) frames.add(image);
					}
				}
			}
		}
	}
	
	//Returns moveState
	public int getMoveState() {
		return moveState;
	}

	/*
	 * Author: Michael Chen
	 * Notes: Determines if an entity is colliding into a wall
	 */
	public boolean isColliding(ArrayList<Wall> walls, int moveState) {
		for (int i = 0; i < walls.size() - 1; i++) {
			Wall wall = walls.get(i);
			if (moveState == RIGHT) {
				if (wall.getX() <= x + width + speed && wall.getX() + wall.getWidth() >= x + width + speed) {
					for (int y = (int)this.y; y <= this.y + height; y++) {
						if (y <= wall.getY() + wall.getHeight() + stopDistance && y >= wall.getY() - stopDistance)
							return true;
					}
				}
			}
			else if (moveState == UP) {
				if (wall.getY() <= y - speed && wall.getY() + wall.getHeight() >= y - speed) {
					for (int x= (int)this.x; x <= this.x + width; x++) {
						if (x <= wall.getX() + wall.getWidth() + stopDistance && x >= wall.getX() - stopDistance) {
							if (wall.getClass().equals(GhostWall.class) && this.getClass().equals(Ghost.class)) return false;
							return true;
						}
					}
				}
			}
			else if (moveState == LEFT) {
				if (wall.getX() <= x - speed && wall.getX() + wall.getWidth() >= x - speed) {
					for (int y = (int)this.y; y <= this.y + height; y++) {
						if (y <= wall.getY() + wall.getHeight() + stopDistance && y >= wall.getY() - stopDistance)
							return true;
					}
				}
			}
			else if (moveState == DOWN) {
				if (wall.getY() <= y + height + speed && wall.getY() + wall.getHeight() >= y + height + speed) {
					for (int x= (int)this.x; x <= this.x + width; x++) {
						if (x <= wall.getX() + wall.getWidth() + stopDistance && x >= wall.getX() - stopDistance)
							return true;
					}
				}
			}
		}
		return false;
	}

	/*
	 * Author: Michael Chen
	 * Notes: Gets the opposite movement
	 */
	public static int getOppositeMovement(int moveState) {
        if (moveState == LEFT) return RIGHT;
        else if (moveState == RIGHT) return LEFT;
        else if (moveState == UP) return DOWN;
        else if (moveState == DOWN) return UP;
        return -1;
    }
	
	//Returns if the entity is dead
	public boolean getIsDead() {
		return isDead;
	}
	
	//Sets isDead
	public void setIsDead(boolean isDead) {
		this.isDead = isDead;
	}

}