/*
 * Author: Clarence Choy and Michael Chen
 * Date: 05/24/2020
 * Rev:
 * Notes: Represents a ghost in pacman
 */
import java.util.ArrayList;

public class Ghost extends Entity{
	//Fields
	private int speed = 2;
	private boolean getUnstuck = false, equalX = false, equalY = false, goDown = false, goRight = false, isVulnerable;

	//Constructor
	public Ghost(int x, int y, String fileNameR, String fileNameL, String fileNameU, String fileNameD) {
		super(x, y, 28, 30, fileNameR, fileNameL, fileNameU, fileNameD);
		isVulnerable = false;
		setMoveState(UP);
	}

	/*
	 * Author: Clarence Choy
	 * Notes: Chases pacman
	 */
	public void chase(int x, int y, Board board) {
		int pacmanY = board.getPacman().getY();
		int pacmanX = board.getPacman().getX();
		if(pacmanY > this.getY() && !this.isColliding(board.getWalls(), Entity.DOWN) && !getUnstuck) {
			this.moveForward(speed);
		} 
		if(pacmanY < this.getY() && !this.isColliding(board.getWalls(), Entity.UP) && !getUnstuck) {
			this.moveForward(-speed);
		}
		if(pacmanX > this.getX() && !this.isColliding(board.getWalls(), Entity.RIGHT) && !getUnstuck) {
			this.moveSideways(speed);
		}
		if(pacmanX < this.getX() && !this.isColliding(board.getWalls(), Entity.LEFT) && !getUnstuck) {
			this.moveSideways(-speed);
		} 
		if(x == this.getX() && y == this.getY() || getUnstuck == true) {
			getUnstuck(board);
			return;
		}
	}

	/* 
	 * Author: Clarence Choy
	 * Notes: Helps the ghost get out of spots where it gets stuck
	 */
	public void getUnstuck(Board board) {
		boolean exception = false;
		int pacmanY = board.getPacman().getY();
		int pacmanX = board.getPacman().getX();
		if(board.getPacman().getX() == 458 && board.getPacman().getY() == 438){
			exception = true;
		}
		//Bottom Right
		if(pacmanX > this.getX() && pacmanY > this.getY() && !equalX && !equalY) {
			getUnstuck = true;
			if(this.isColliding(board.getWalls(), Entity.DOWN) && !this.isColliding(board.getWalls(), Entity.LEFT)) {
				this.moveSideways(-speed);
			}else if (!this.isColliding(board.getWalls(), Entity.DOWN)){
				this.moveForward(3);
				getUnstuck = false;
				return;
			}
			//Top Right
		} else if(pacmanX > this.getX() && pacmanY < this.getY() && !this.isColliding(board.getWalls(), Entity.LEFT) && !equalX && !equalY) {
			getUnstuck = true;
			if(this.isColliding(board.getWalls(), Entity.UP)) {
				this.moveSideways(-speed);
			}else if (!this.isColliding(board.getWalls(), Entity.UP)) {
				this.moveForward(-3);
				getUnstuck = false;
				return;
			}
			//Top Left
		} else if(pacmanX < this.getX() && pacmanY < this.getY() && !this.isColliding(board.getWalls(), Entity.RIGHT) && !equalX && !equalY) {
			getUnstuck = true;
			if(this.isColliding(board.getWalls(), Entity.UP)) {
				this.moveSideways(speed);
			}else if (!this.isColliding(board.getWalls(), Entity.UP)) {
				this.moveForward(-3);
				getUnstuck = false;
				return;
			}
			//Bottom Left
		} else if(pacmanX < this.getX() && pacmanY > this.getY() && !this.isColliding(board.getWalls(), Entity.RIGHT) && !equalX && !equalY) {
			getUnstuck = true;
			if(this.isColliding(board.getWalls(), Entity.DOWN)) {
				this.moveSideways(speed);
			}else if (!this.isColliding(board.getWalls(), Entity.DOWN)) {
				this.moveForward(3);
				getUnstuck = false;
				return;
			}
		}

		//Equal Y's
		if (pacmanY == this.getY() && pacmanX != this.getX() || equalY) {
			getUnstuck = true;
			equalY = true;
			//PacmanX is greater than ghostX
			if(pacmanX > this.getX()) {
				//Ghost goes up
				if(this.isColliding(board.getWalls(), Entity.RIGHT) && !this.isColliding(board.getWalls(), Entity.UP) && goDown == false && exception == false) {
					this.moveForward(-speed);
				} else if(!this.isColliding(board.getWalls(), Entity.RIGHT) && goDown == false) {
					this.moveSideways(3);
					getUnstuck = false;
					equalY = false;
					return;
				} else if(this.isColliding(board.getWalls(), Entity.RIGHT) && this.isColliding(board.getWalls(), Entity.UP) && goDown == false || exception == true) {
					goDown = true;
				}
				//Ghost goes down
				if(this.isColliding(board.getWalls(), Entity.RIGHT) && !this.isColliding(board.getWalls(), Entity.DOWN) && goDown) {
					this.moveForward(speed);
				} else if(!this.isColliding(board.getWalls(), Entity.RIGHT) && goDown) {
					this.moveSideways(4);
					getUnstuck = false;
					equalY = false;
					goDown = false;
					return;
				}
				//PacmanX is less than ghostX
			} else if(pacmanX < this.getX()) {
				//Ghost goes up
				if(this.isColliding(board.getWalls(), Entity.LEFT) && !this.isColliding(board.getWalls(), Entity.UP) && goDown == false) {
					this.moveForward(-speed);
				} else if(!this.isColliding(board.getWalls(), Entity.LEFT) && goDown == false) {
					this.moveSideways(-3);
					getUnstuck = false;
					equalY = false;
					return;
				} else if(this.isColliding(board.getWalls(), Entity.LEFT) && this.isColliding(board.getWalls(), Entity.UP) && goDown == false) {
					goDown = true;
				}
				//Ghost goes down
				if(this.isColliding(board.getWalls(), Entity.LEFT) && !this.isColliding(board.getWalls(), Entity.DOWN) && goDown) {
					this.moveForward(speed);
				} else if(!this.isColliding(board.getWalls(), Entity.LEFT) && goDown) {
					this.moveSideways(-3);
					getUnstuck = false;
					equalY = false;
					goDown = false;
					return;
				}
			}
		}
		//Equal X's
		if(pacmanX == this.getX() && pacmanY != this.getY() || equalX) {
			getUnstuck = true;
			equalX = true;
			//PacmanY is greater than ghostY
			if(pacmanY > this.getY()) {
				//Ghost goes left
				if(this.isColliding(board.getWalls(), Entity.DOWN) && !this.isColliding(board.getWalls(), Entity.LEFT) && goRight == false && exception == false) {
					this.moveSideways(-speed);
				} else if(!this.isColliding(board.getWalls(), Entity.DOWN) && goRight == false) {
					this.moveForward(3);
					getUnstuck = false;
					equalX = false;
					return;
				} else if(this.isColliding(board.getWalls(), Entity.DOWN) && this.isColliding(board.getWalls(), Entity.LEFT) && goRight == false || exception == true) {
					goRight = true;
				}
				//Ghost goes right
				if(this.isColliding(board.getWalls(), Entity.DOWN) && !this.isColliding(board.getWalls(), Entity.RIGHT) && goRight) {
					this.moveSideways(speed);
				} else if(!this.isColliding(board.getWalls(), Entity.DOWN) && goRight) {
					this.moveForward(3);
					getUnstuck = false;
					equalX = false;
					goRight = false;
					return;
				}
				//PacmanY is less than ghostY
			} else if(pacmanY < this.getY()) {
				//Ghost goes left
				if(this.isColliding(board.getWalls(), Entity.UP) && !this.isColliding(board.getWalls(), Entity.LEFT) && goRight == false) {
					this.moveSideways(-speed);
				} else if(!this.isColliding(board.getWalls(), Entity.UP) && goRight == false) {
					this.moveForward(-3);
					getUnstuck = false;
					equalX = false;
					return;
				} else if(this.isColliding(board.getWalls(), Entity.UP) && this.isColliding(board.getWalls(), Entity.LEFT) && goRight == false) {
					goRight = true;
				}
				//Ghost goes right
				if(this.isColliding(board.getWalls(), Entity.UP) && !this.isColliding(board.getWalls(), Entity.RIGHT) && goRight) {
					this.moveSideways(speed);
				} else if(!this.isColliding(board.getWalls(), Entity.UP) && goRight) {
					this.moveForward(-3);
					getUnstuck = false;
					equalX = false;
					goRight = false;
					return;
				}
			}
		} 
	}

	/*
	 * Author: Michael Chen
	 * Notes: Ghosts wander in random directions
	 */
	public void wander(Board board, int speed) {
		ArrayList<Integer> choices = new ArrayList<Integer>();
		if (!this.isColliding(board.getWalls(), LEFT)) choices.add(LEFT);
		if (!this.isColliding(board.getWalls(), RIGHT)) choices.add(RIGHT);
		if (!this.isColliding(board.getWalls(), DOWN)) choices.add(DOWN);
		if (!this.isColliding(board.getWalls(), UP)) choices.add(UP);

		int choice = (int) Math.floor(Math.random() * choices.size());
		if (!isCaged(board)) {
			if (choices.size() > 1 && Entity.getOppositeMovement(choices.get(choice)) != this.getMoveState()) 
				this.setMoveState(choices.get(choice));
		}
		else {
			if (this.isColliding(board.getWalls(), this.getMoveState())) {
				this.setMoveState(Entity.getOppositeMovement(this.getMoveState()));
				double direction = Math.random();
				if (direction < 0.05 && this.isColliding(board.getWalls(), UP)) this.setMoveState(RIGHT);
			}
			else if (!this.isColliding(board.getWalls(), UP) && (getMoveState() == LEFT || getMoveState() == RIGHT)){
				this.setMoveState(UP);
			}
		}
		move(board, speed);
	}

	/*
	 * Author: Michael Chen
	 * Notes: Moves the ghost after deciding which way to go
	 */
	public void move(Board board, int speed) {

		if (this.getMoveState() == Entity.LEFT && !this.isColliding(board.getWalls(), LEFT)) this.moveSideways(-speed);
		else if (this.getMoveState() == Entity.RIGHT && !this.isColliding(board.getWalls(), RIGHT)) this.moveSideways(speed);
		else if (this.getMoveState() == Entity.UP && !this.isColliding(board.getWalls(), UP)) this.moveForward(-speed);
		else if (this.getMoveState() == Entity.DOWN && !this.isColliding(board.getWalls(), DOWN)) this.moveForward(speed);
	}
	
	/*
	 * Author: Michael Chen
	 * Notes: Returns whether the ghost is inside the box or not.
	 */
	public boolean isCaged(Board board) {
		
		for (int i = board.getWalls().size() - 2; i <= board.getWalls().size() - 1; i++) {
			Wall wall = board.getWalls().get(i);
			for (int x = this.getX(); x <= this.getX() + this.getWidth(); x++) {
				for (int y = this.getY(); y <= this.getY() + this.getHeight() + 2; y++) {
					if (wall.getX() < x && x < wall.getX() + wall.getWidth()) {
						if (wall.getY() < y && y < wall.getY() + wall.getHeight()) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	/*
	 * Author: Michael Chen
	 * Notes: Sets the vulnerability state of the ghosts
	 */
	public void setVulnerability(boolean vulnerable) {
		isVulnerable = vulnerable;
	}
	
	/*
	 * Author: Michael Chen
	 * Notes: Returns the vulnerability state of the ghosts
	 */
	public boolean getVulnerabilty() {
		return isVulnerable;
	}
}