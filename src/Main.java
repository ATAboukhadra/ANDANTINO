import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Main extends JPanel {

	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 1200;
	public static final int HEIGHT = 800;
	private static final int OFFSET = 7;
	private static final int R = 20;
	private Game game;
	private static boolean start = true;
	public static JFrame f;
	public static boolean blackStarts;
	public static boolean gameCreated;
	public static boolean gameover;

	public Main() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		game = new Game();
		gameCreated = false;
		gameover = false;
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!start && !gameover) {
					if (!gameCreated)
						create();
					else {
						int x = e.getX();
						int y = e.getY();
						int idx = game.findNearestCenter(x, y);
						if (game.isValid(idx)) {
							draw(game.turn, idx);
							game.handlePlay(idx, blackStarts ? 2 : 1);
							// System.out.println("enclosment = " + game.enclosementValue(idx));
							if (!gameover) {
								Play p = game.search(idx, blackStarts ? 1 : 2);
								draw(game.turn, p.pos);
								game.handlePlay(p.pos, blackStarts ? 1 : 2);
							}
						}
					}
				}
			}

		});
	}

	private void create() {
		game.createNeighbors();
		// handle
		if (!blackStarts) {
			State s = new State(game.grid, Game.CELLS / 2, game.turn, game.noFilled);
			Play p = Game.chooseRandom(s);
			game.grid[p.pos] = 2; // white
			game.noFilled++;
			game.turn = true; // fee la5bata hena
			draw(!game.turn, p.pos);
		}
		gameCreated = true;
	}

	public void draw(boolean turn, int idx) {
		Graphics2D g = (Graphics2D) getGraphics();

		if (!turn)
			g.setColor(new Color(0xFFFFFFFF));
		else
			g.setColor(new Color(0x00000000));

		g.fillOval(StateDecider.xs[idx] - R + OFFSET, StateDecider.ys[idx] - R + OFFSET, (R - OFFSET) * 2,
				(R - OFFSET) * 2);
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Point origin = new Point(WIDTH / 2, HEIGHT / 2);
		g2d.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));

		drawHexGrid(g2d, origin, 19, 4);
		drawLetters(g2d, origin, 19, 4);
	}

	private void drawLetters(Graphics2D g2d, Point origin, int size, int pad) {
		int half = size / 2;
		int cols = size - half;
		double ang30 = Math.toRadians(30);
		double xOff = Math.cos(ang30) * (R + pad);
		double yOff = Math.sin(ang30) * (R + pad);

		int firstx = (int) (origin.x + xOff * (1 - cols));
		int firsty = (int) (origin.y + yOff * half * -3);

		// Top Side (A-J)
		int lastx = 0;
		int lasty = 0;
		for (int j = 0; j < cols; j++) {
			int x = (int) (origin.x + xOff * (j * 2 + 1 - cols)) + 5;
			int y = (int) (origin.y + yOff * half * -3) - 45;
			lastx = x;
			lasty = y;
			JLabel letter = new JLabel();
			letter.setText((char) ('A' + j) + "");
			letter.setBounds(x, y, 20, 20);
			add(letter);
		}

		// Right Side (J-S)
		for (int j = 0; j < 10; j++) {
			int x = lastx + (j + 1) * 21 - 7;
			int y = lasty + (j + 1) * 35 - 25;

			JLabel letter = new JLabel();
			letter.setText((char) ('J' + j) + "");
			letter.setBounds(x, y, 20, 20);
			add(letter);
		}

		// left side (1-10)
		for (int j = 0; j < 10; j++) {
			int x = firstx - (j + 1) * 21 - 20;
			int y = firsty + (j + 1) * 36 - 50;
			lastx = x;
			lasty = y;
			JLabel letter = new JLabel();
			letter.setText(j + 1 + "");
			letter.setBounds(x, y, 20, 20);
			add(letter);
		}

		// Right Side (10-19)
		for (int j = 0; j < 9; j++) {
			int x = lastx + (j + 1) * 21;
			int y = lasty + (j + 1) * 36 + 7;

			JLabel letter = new JLabel();
			letter.setText(j + 11 + "");
			letter.setBounds(x, y, 20, 20);
			add(letter);
		}
	}

	private void drawHexGrid(Graphics2D g2d, Point origin, int size, int pad) {
		double ang30 = Math.toRadians(30);
		double xOff = Math.cos(ang30) * (R + pad);
		double yOff = Math.sin(ang30) * (R + pad);

		int half = size / 2;
		int idx = 0;
		for (int i = 0; i < size; i++) {
			int cols = size - Math.abs(i - half);
			for (int j = 0; j < cols; j++) {
				int x = (int) (origin.x + xOff * (j * 2 + 1 - cols));
				int y = (int) (origin.y + yOff * (i - half) * 3);
				StateDecider.xs[idx] = x;
				StateDecider.ys[idx] = y;
				Game.stringRep[idx] = (char) ('A' + (i < 10 ? j : j + i - 9)) + "" + (i + 1);
				idx++;
				drawHex(g2d, x, y, R);
				if (i == 9 && j == 9)
					g2d.fillOval(x - R + OFFSET, y - R + OFFSET, (R - OFFSET) * 2, (R - OFFSET) * 2);
			}
		}
	}

	private void drawHex(Graphics2D g2d, int x, int y, int r) {
		Hexagon h = new Hexagon(x, y, r);

		h.draw(g2d, x, y, 0, 0xB2BCFF, true);
		h.draw(g2d, x, y, 4, 0x6B99A3, false);

	}

	private static void switchPanels() {
		Main p = new Main();
		start = false;
		f.setContentPane(p);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setLocationRelativeTo(null);
	}

	public static void main(String[] args) {
		f = new JFrame();

		// color are the other way around because it is the same logic but after
		// switching colors
		JButton blackButton = new JButton("White");
		blackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				blackStarts = true;
				switchPanels();
			}
		});

		JButton whiteButton = new JButton("Black");
		whiteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				blackStarts = false;
				switchPanels();
			}

		});

		JLabel text = new JLabel();
		text.setText("Choose your color");
		text.setBounds(WIDTH / 2 - 50, HEIGHT / 2 - 100, 400, 50);
		blackButton.setBounds(WIDTH / 2 + 50, HEIGHT / 2 - 20, 100, 30);
		whiteButton.setBounds(WIDTH / 2 - 100, HEIGHT / 2 - 20, 100, 30);

		f.add(text);
		f.add(blackButton);
		f.add(whiteButton);
		f.setSize(WIDTH, HEIGHT);
		f.setLayout(null);
		f.setVisible(true);

	}

	public static void switchToWinning(String win) {
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(null, win, "", JOptionPane.WARNING_MESSAGE);
	}
}
