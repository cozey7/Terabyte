/*
 * Author: Michael Chen, Clarence Choy, and Andy Ding
 * Date: 05/24/2020
 * Rev:
 * Notes: Represents Pacman
 */
import java.util.ArrayList;

public class Pacman extends Entity {
	//Fields
	private int dotsConsumed = 0;
	private int cherryCount = 0;
	
	private int energizedTime = 1000;
	private boolean powerPelletEaten = false;
	
	//Constructors
	public Pacman(int x, int y, String fileName) {
		super(x, y, 28, 30, fileName);
	}
	
	public Pacman(int x, int y, String fileNameR, String fileNameL, String fileNameU, String fileNameD) {
		super(x, y, 28, 30, fileNameR, fileNameL, fileNameU, fileNameD);
	}
	
	/*
	 * Author: Andy Ding
	 * Notes: Method that allows pacman to consume the dots and pellets
	 */
	public void consume(ArrayList<Consumable> consumables, Board board) {
		for(Consumable c: consumables) {
			
			if(c instanceof Cherry && (dotsConsumed == 70 || dotsConsumed == 170) && cherryCount == 0) {
				c.toggleVisibility();
				cherryCount++;
			}
			
			if(c.getX() + c.getWidth()/2 >= this.getX() && c.getX() - c.getWidth()/2 <= this.getX() + this.getWidth()) {
				if (c.getY() + c.getHeight()/2 >= this.getY() && c.getY() - c.getHeight()/2 <= this.getY() + this.getHeight()) {
					if(c.isVisible() == true) {
						if (c instanceof Dot) {
							board.setScore(board.getScore() + 10);
							dotsConsumed++;
							if(cherryCount != 0) cherryCount = 0;
							EasySound wakka = new EasySound("pacman_wakka_wakka.wav");
							wakka.play();
						}
						else if (c instanceof Powerpellet) {
							board.setScore(board.getScore() + 50);
							powerPelletEaten = true;
							energizedTime = 0;
						}
						else if (c instanceof Cherry)  {
							EasySound cherry = new EasySound("pacman_fruit.wav");
							cherry.play();
							board.setScore(board.getScore() + 100);
						}
						c.toggleVisibility();
					}
				}
			}
			
		}

	}
	
	/*
	 * Author: Michael Chen
	 * Notes: Determines if pacman is colliding with a ghost
	 */
	public boolean isColliding(Ghost ghost) {
		for (int x = this.getX() + getWidth() / 2 - 10; x < this.getX() + getWidth() / 2 + 10; x++) {
			for (int y = this.getY() + getHeight() / 2 - 10; y < this.getY() + getHeight() / 2 + 10; y++) {
				if (ghost.getX() + getWidth() / 2 == x && ghost.getY() + getHeight() / 2 == y) {
					return true;
				}
			}
		}
		if(this.getX() == ghost.getX() && this.getY() == ghost.getY()) {
			return true;
		}
		return false;
	}
	
	/*
	 * Author: Clarence Choy
	 */
	//Returns if pacman has eaten a powerpellet
	public boolean getPowerPelletEaten() {
		return powerPelletEaten;
	}
	
	//Sets powerPelletEaten
	public void setPowerPelletEaten(boolean eaten) {
		powerPelletEaten = eaten;
	}
	
	//Gets how long pacman has been energized
	public int getEnergizedTime() {
		return energizedTime;
	}
	
	//Sets energizedTime
	public void setEnergizedTime(int time) {
		energizedTime = time;
	}
}