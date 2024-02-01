/*
 * Author: Shelby
 * Date: 05/03/2013
 * Rev: 
 * Notes: Represents a moving, appearing/disappearing image. 
 */
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class MovingImage {
	//Fields
	private int x, y;
	private int width, height;
	private Image image;
	private boolean isVisible;
	
	//Constructors
	public MovingImage(String filename, int x, int y, int w, int h) {
		this((new ImageIcon(filename)).getImage(),x,y,w,h);
	}
	
	public MovingImage(Image img, int x, int y, int w, int h) {
		image = img;
		this.x = x;
		this.y = y;
		width = w;
		height = h;
		isVisible = true;
	}
	
	//Methods
	public void toggleVisibility() {
		isVisible = !isVisible;
	}
	
	public void moveToLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void moveByAmount(int x, int y) {
		this.x += x;
		this.y += y;
	}
	
	public void applyWindowLimits(int windowWidth, int windowHeight) {
		x = Math.min(x,windowWidth-this.width);
		y = Math.min(y,windowHeight-this.height);
		x = Math.max(0,x);
		y = Math.max(0,y);
	}
	
	public boolean isPointInImage(int mouseX, int mouseY) {
		if (mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height)
			return true;
		return false;
	}
	
	public void resize(int w, int h) {
		width = w;
		height = h;
	}
	
	public void draw(Graphics g, ImageObserver io) {
		g.setColor(Color.WHITE);
		if (isVisible)
			g.drawImage(image,x,y,width,height,io);
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public boolean isVisible() {
		return isVisible;
	}
	
	
	
}