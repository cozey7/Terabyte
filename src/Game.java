/*
 * Author: Michael Chen
 * Date: 05/25/2020
 * Rev:
 * Notes: Class to play sounds
 */
import javax.swing.*;

public class Game extends JFrame {

	public Game() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 200, 555, 798);
		setTitle("Pacman");
		Board board = new Board();
		add(board);
	}
	public static void main(String[] args) {
	    Game game = new Game();
	    game.setVisible(true);
	}
}
