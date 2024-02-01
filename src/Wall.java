/*
 * Author: Michael Chen
 * Date: 05/25/2020
 * Rev:
 * Notes: Represents the walls in Pacman
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class Wall {
	//Fields
	private int x, y, width, height;
	
	//Constructor
	public Wall(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	//Draws the wall
	public void draw(Graphics2D g) {
		g.setColor(new Color(255, 255, 255, 200));
		g.setStroke(new BasicStroke(1f));
		g.drawRect(x, y, width , height );
	}
	
	//Returns the x coordinate
	public int getX() {
		return x;
	}

	//Returns the y coordinate
	public int getY() {
		return y;
	}

	//Returns the width
	public int getWidth() {
		return width;
	}

	//Returns the height
	public int getHeight() {
		return height;
	}
}
