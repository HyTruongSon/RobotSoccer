// Program: Final Project in Robotics and AI
// Author: Hy Truong Son
// Major: PhD Student in Machine Learning
// Institution: Departmnet of Computer Science, The University of Chicago
// Email: hytruongson@uchicago.edu

package GUI;

public class Coordinate {
	public int x, y;

	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Coordinate(Coordinate another) {
		x = another.x;
		y = another.y;
	}
};