// Program: Final Project in Robotics and AI
// Author: Hy Truong Son
// Major: PhD Student in Machine Learning
// Institution: Departmnet of Computer Science, The University of Chicago
// Email: hytruongson@uchicago.edu

package GUI;

public class ObjectImage {
	public int width, height;
	public int red[][];
	public int green[][];
	public int blue[][];

	public ObjectImage() {
	}

	public ObjectImage(int width, int height) {
		this.width = width;
		this.height = height;
		red = new int [this.width][this.height];
		green = new int [this.width][this.height];
		blue = new int [this.width][this.height];
	}
};