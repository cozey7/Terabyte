/*
 * Author: Andy Ding
 * Date: 05/25/2020
 * Rev: 01
 * Notes: Represents any consumable in Pacman
 */
public class Consumable extends MovingImage{
	//Constructor
	public Consumable(String fileName, int x, int y, int width, int height) {
		super(fileName, x, y, width, height);
		new MovingImage(fileName, x, y, width, height);
	}
}