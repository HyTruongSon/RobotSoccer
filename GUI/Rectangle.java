// Program: Final Project in Robotics and AI
// Author: Hy Truong Son
// Major: PhD Student in Machine Learning
// Institution: Departmnet of Computer Science, The University of Chicago
// Email: hytruongson@uchicago.edu

package GUI;

public class Rectangle {
	public Coordinate top;
	public Coordinate bottom;
	public Coordinate center;

	public Rectangle(int x1, int y1, int x2, int y2) {
		top = new Coordinate(x1, y1);
		bottom = new Coordinate(x2, y2);
		center = new Coordinate((top.x + bottom.x) / 2, (top.y + bottom.y) / 2);
	}

	public Rectangle(Coordinate top, Coordinate bottom) {
		this.top = new Coordinate(top);
		this.bottom = new Coordinate(bottom);
		center = new Coordinate((this.top.x + this.bottom.x) / 2, (this.top.y + this.bottom.y) / 2);
	}
};