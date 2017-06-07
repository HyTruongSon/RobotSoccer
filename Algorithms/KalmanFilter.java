// Program: Final Project in Robotics and AI
// Author: Hy Truong Son
// Major: PhD Student in Machine Learning
// Institution: Departmnet of Computer Science, The University of Chicago
// Email: hytruongson@uchicago.edu

package Algorithms;

import java.util.ArrayList;

import GUI.Coordinate;
import GUI.Rectangle;

public class KalmanFilter {

	private final int nObjects;
	private ArrayList<Coordinate> position;

	// Identity matrix
	private Matrix I;

	// Motion model
	private Matrix A;

	// Motion noise
	private Matrix R;

	// Measurement model
	private Matrix C;

	// Measurement noise
	private Matrix Q;

	// Prediction
	private Matrix mu_bar;
	private Matrix sigma_bar;

	// Update
	private Matrix K;
	private Matrix mu;
	private Matrix sigma;

	// Sampling period
	private double delta;

	// Motion model noise
	private double motionNoise;

	// Measurement noise
	private double measurementNoise;

	// +-------------+
	// | Constructor |
	// +-------------+

	public KalmanFilter(int nObjects, ArrayList<Coordinate> initial, double delta, double motionNoise, double measurementNoise) {
		this.nObjects = nObjects;
		this.delta = delta;
		this.motionNoise = motionNoise;
		this.measurementNoise = measurementNoise;

		position = new ArrayList<>();
		for (int i = 0; i < initial.size(); ++i) {
			position.add(initial.get(i));
		}

		// Identity matrix
		I = new Matrix(4 * this.nObjects, 4 * this.nObjects);
		for (int i = 0; i < I.nRows; ++i) {
			I.value[i][i] = 1.0;
		}

		// Initialization for Motion model (velocity model)
		A = new Matrix(4 * this.nObjects, 4 * this.nObjects);
		for (int i = 0; i < A.nRows; ++i) {
			A.value[i][i] = 1.0;
		}
		for (int i = 0; i < 2 * this.nObjects; ++i) {
			A.value[i][2 * this.nObjects + i] = this.delta;
		}

		// Motion noise
		R = new Matrix(4 * this.nObjects, 4 * this.nObjects);
		for (int i = 0; i < R.nRows; ++i) {
			R.value[i][i] = this.motionNoise;
		}

		// Initialization for Measurement model
		C = new Matrix(2 * this.nObjects, 4 * this.nObjects);
		for (int i = 0; i < C.nRows; ++i) {
			C.value[i][i] = 1.0;
		}

		// Measurement noise
		Q = new Matrix(2 * this.nObjects, 2 * this.nObjects);
		for (int i = 0; i < 2 * this.nObjects; ++i) {
			Q.value[i][i] = this.measurementNoise;
		}

		// Initial mu
		mu = new Matrix(4 * this.nObjects, 1);
		int count = 0;
		for (int i = 0; i < position.size(); ++i) {
			double x = position.get(i).x;
			double y = position.get(i).y;
			mu.value[count][0] = x;
			++count;
			mu.value[count][0] = y;
			++count;
		}
		for (int i = 2 * this.nObjects; i < 4 * this.nObjects; ++i) {
			mu.value[i][0] = this.delta;
		}

		// Initial sigma
		sigma = new Matrix(4 * this.nObjects, 4 * this.nObjects);
		for (int i = 0; i < sigma.nRows; ++i) {
			sigma.value[i][i] = 1.0;
		}
	}

	// +--------------------+
	// | Euclidean Distance |
	// +--------------------+

	private double Euclidean(Coordinate A, Coordinate B) {
		return Math.sqrt((A.x - B.x) * (A.x - B.x) + (A.y - B.y) * (A.y - B.y));
	}

	private double Euclidean(Coordinate A, Rectangle B) {
		return Euclidean(A, B.center);
	}

	// +---------------+
	// | Kalman Filter |
	// +---------------+

	public ArrayList<Coordinate> filter_on(ArrayList<Rectangle> measure) {
		// Initialize the cost matrix for Hungarian Matching Algorithm
		int nx = position.size();
		int ny = measure.size();
		int N = Math.max(nx, ny);

		double cost[][] = new double [N][N];
		double INF = 1e6;
		for (int x = 0; x < N; ++x) {
			for (int y = 0; y < N; ++y) {
				cost[x][y] = INF;
			}
		}

		for (int x = 0; x < nx; ++x) {
			for (int y = 0; y < ny; ++y) {
				cost[x][y] = Euclidean(position.get(x), measure.get(y));
			}
		}

		// +----------------------------------------------------------+
		// | Hungarian Matching Algorithm - Maximum Flow Minimum Cost |
		// +----------------------------------------------------------+

		Hungarian_Matching hm = new Hungarian_Matching(N, cost);
		ArrayList<Integer> match = hm.findMatch();

		// Process the case when fewer measurements than the number of objects
		ArrayList<Coordinate> new_position = new ArrayList<>();
		for (int x = 0; x < nx; ++x) {
			int y = match.get(x);
			if (y < ny) {
				new_position.add(measure.get(y).center);
			} else {
				double MIN = 1e9;
				int index = -1;
				for (int j = 0; j < ny; ++j) {
					double distance = cost[x][j];
					if (distance < MIN) {
						MIN = distance;
						index = j;
					}
				}
				if (index == -1) {
					new_position.add(position.get(x));
				} else {
					new_position.add(measure.get(index).center);
				}
			}
		}

		// +----------------------------+
		// | Kalman Filter - Prediction |
		// +----------------------------+

		mu_bar = A.multiply(mu);
		sigma_bar = (A.multiply(sigma.multiply(A.transpose()))).add(R);

		// +------------------------+
		// | Kalman Filter - Update |
		// +------------------------+

		// Kalman gain
		Matrix inv = ((C.multiply(sigma_bar).multiply(C.transpose())).add(Q)).inverse();
		K = sigma_bar.multiply(C.transpose()).multiply(inv);

		// New measurement 
		Matrix z = new Matrix(2 * nObjects, 1);
		int count = 0;
		for (int i = 0; i < new_position.size(); ++i) {
			double x = new_position.get(i).x;
			double y = new_position.get(i).y;
			z.value[count][0] = x;
			++count;
			z.value[count][0] = y;
			++count;
		}

		// Update the mean
		mu = mu_bar.add(K.multiply(z.subtract(C.multiply(mu_bar))));

		// Update the variance
		sigma = (I.subtract(K.multiply(C))).multiply(sigma_bar);

		// Update the position based on the new mean
		for (int i = 0; i < position.size(); ++i) {
			int x = (int)(mu.value[2 * i][0]);
			int y = (int)(mu.value[2 * i + 1][0]);
			position.set(i, new Coordinate(x, y));
		}

		return position;
	}

	// +-----------------------------------------------------------------+
	// | There is no Kalman Filter, just matching to the nearest measure |
	// +-----------------------------------------------------------------+

	public ArrayList<Coordinate> filter_off(ArrayList<Rectangle> measure) {
		ArrayList<Coordinate> new_position = new ArrayList<>();
		for (int i = 0; i < position.size(); ++i) {
			double MIN = 1e9;
			int index = -1;
			for (int j = 0; j < measure.size(); ++j) {
				double distance = Euclidean(position.get(i), measure.get(j));
				if (distance < MIN) {
					MIN = distance;
					index = j;
				}
			}
			if (index == -1) {
				new_position.add(position.get(i));
			} else {
				new_position.add(measure.get(index).center);
			}
		}

		for (int i = 0; i < position.size(); ++i) {
			position.set(i, new_position.get(i));
		}

		return position;
	}

}