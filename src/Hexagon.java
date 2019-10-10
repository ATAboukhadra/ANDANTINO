import java.awt.*;

public class Hexagon extends Polygon{
	private static final long serialVersionUID = 1L;
	private static final int SIDES = 6;
	private Point[] points = new Point[SIDES];
	private Point center = new Point(0,0);
	private int radius;
	private int rotation = 90;
	
	public Hexagon(int x, int y, int radius) {
		npoints = SIDES;
		xpoints = new int[SIDES];
		ypoints = new int[SIDES];
		
		this.center = new Point(x, y);
		this.radius = radius;
		
		updatePoints();
	}
	
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
		updatePoints();
	}

	public int getRotation() {
		return rotation;
	}
	public void setRotation(int rotation) {
		this.rotation = rotation;
	}
	public Point getCenter() {
		return center;
	}
	public void setCenter(int x, int y) {
		this.center = new Point(x, y);
		
		updatePoints();
	}
	private void updatePoints() {
		for (int i = 0; i < SIDES; i++) {
			double angle = findAngle((double) i / SIDES);
			Point point = findPoint(angle);
			xpoints[i] = point.x;
			ypoints[i] = point.y;
			points[i] = point;
		}
	}

	private Point findPoint(double angle) {
		int x = (int) (center.x + Math.cos(angle) * radius);
		int y = (int) (center.y + Math.sin(angle) * radius);
		return new Point(x, y);
	}

	private double findAngle(double d) {
		return d*Math.PI*2 + Math.toRadians((rotation + 180) % 360);
	}
	
	public void draw(Graphics2D g, int x, int y, int lineThickness, int colorValue, boolean filled) {
		Stroke s = g.getStroke();
		Color c = g.getColor();
		g.setColor(new Color(colorValue));
		g.setStroke(new BasicStroke(lineThickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
		
		if(filled)
			g.fillPolygon(xpoints, ypoints, npoints);
		else
			g.drawPolygon(xpoints, ypoints, npoints);
		g.setColor(c);
		g.setStroke(s);
		
	}
	
}
