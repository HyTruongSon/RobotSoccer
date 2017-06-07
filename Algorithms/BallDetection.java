// Program: Final Project in Robotics and AI
// Author: Hy Truong Son
// Major: PhD Student in Machine Learning
// Institution: Departmnet of Computer Science, The University of Chicago
// Email: hytruongson@uchicago.edu

package Algorithms;

import java.util.ArrayList;

import GUI.Coordinate;
import GUI.Rectangle;
import GUI.ObjectImage;

public class BallDetection {

	private int widthFrame;
	private int heightFrame;

	// Breadth-First Search
	private int rear, front;
	private Coordinate queue[];
	private int nComponents;
	private int component[][];

	private final int nDir = 8;
	private final int DX[] = {-1, 1, 0, 0, -1, -1, 1, 1};
	private final int DY[] = {0, 0, -1, 1, -1, 1, -1, 1};

	public BallDetection(int widthFrame, int heightFrame) {
		this.widthFrame = widthFrame;
		this.heightFrame = heightFrame;

		queue = new Coordinate [this.widthFrame * this.heightFrame];
		for (int i = 0; i < this.widthFrame * this.heightFrame; ++i) {
			queue[i] = new Coordinate(0, 0);
		}

		component = new int [this.widthFrame][this.heightFrame];
	}

	private void push_queue(int x, int y) {
		queue[rear].x = x;
		queue[rear].y = y;
		++rear;
		component[x][y] = nComponents;
	}

	public ArrayList<Rectangle> detect(ObjectImage filter) {
		for (int x = 0; x < widthFrame; ++x) {
			for (int y = 0; y < heightFrame; ++y) {
				component[x][y] = -1;
			}
		}

		ArrayList<Rectangle> ret = new ArrayList<>();

		nComponents = 0;
		for (int x = 0; x < widthFrame; ++x) {
			for (int y = 0; y < heightFrame; ++y) {
				if ((filter.red[x][y] == 255) && (filter.green[x][y] == 0) && (filter.blue[x][y] == 0) && (component[x][y] == -1)) {
					++nComponents;
					rear = 0;
					front = 0;
					push_queue(x, y);

					int MIN_X = x;
					int MIN_Y = y;
					int MAX_X = x;
					int MAX_Y = y;
					
					while (rear != front) {
						int i = queue[front].x;
						int j = queue[front].y;
						++front;

						for (int d = 0; d < nDir; ++d) {
							int u = i + DX[d];
							int v = j + DY[d];

							if ((u >= 0) && (u < widthFrame) && (v >= 0) && (v < heightFrame)) {
								if ((filter.red[u][v] == 255) && (filter.green[u][v] == 0) && (filter.blue[u][v] == 0) && (component[u][v] == -1)) {
									push_queue(u, v);
									MIN_X = Math.min(MIN_X, u);
									MIN_Y = Math.min(MIN_Y, v);
									MAX_X = Math.max(MAX_X, u);
									MAX_Y = Math.max(MAX_Y, v);
								}
							}
						}
					}

					ret.add(new Rectangle(MIN_X, MIN_Y, MAX_X, MAX_Y));
				}
			}
		}

		return ret;
	}

}