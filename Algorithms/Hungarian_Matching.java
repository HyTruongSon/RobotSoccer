// Program: Final Project in Robotics and AI
// Author: Hy Truong Son
// Major: PhD Student in Machine Learning
// Institution: Departmnet of Computer Science, The University of Chicago
// Email: hytruongson@uchicago.edu

package Algorithms;

import java.util.ArrayList;

public class Hungarian_Matching {

	private double INF = 1e9;

	private int N;
	private int nVertices;
	private int source;
	private int sink;

	// Maximum-Flow Minimum-Cost
	private double cost[][];
	private int capacity[][];
	private int flow[][];

	// Bellman-Ford's algorithm with rounded queue data structure
	private int rear;
	private int front;
	private int queue[];
	private int trace[];
	private boolean inqueue[];
	private double d[];

	public Hungarian_Matching(int N, double input_cost[][]) {
		this.N = N;

		// Construct the flow network
		nVertices = 2 * N + 2;
		source = 2 * N;
		sink = 2 * N + 1;

		cost = new double [nVertices][nVertices];
		capacity = new int [nVertices][nVertices];
		flow = new int [nVertices][nVertices];

		for (int i = 0; i < nVertices; ++i) {
			for (int j = 0; j < nVertices; ++j) {
				capacity[i][j] = 0;
			}
		}

		for (int i = 0; i < N; ++i) {
			cost[source][i] = 0.0;
			capacity[source][i] = 1;

			cost[N + i][sink] = 0.0;
			capacity[N + i][sink] = 1;
		}

		for (int i = 0; i < N; ++i) {
			for (int j = 0; j < N; ++j) {
				cost[i][N + j] = input_cost[i][j];
				capacity[i][N + j] = 1;
			}
		}

		queue = new int [nVertices];
		trace = new int [nVertices];
		inqueue = new boolean [nVertices];
		d = new double [nVertices];
	}

	private void push_queue(int v) {
		if (!inqueue[v]) {
			queue[rear] = v;
			rear = (rear + 1) % nVertices;
			inqueue[v] = true;
		}
	}

	private int pop_queue() {
		int v = queue[front];
		front = (front + 1) % nVertices;
		inqueue[v] = false;
		return v;
	}

	private boolean Bellman_Ford() {
		for (int v = 0; v < nVertices; ++v) {
			inqueue[v] = false;
			d[v] = INF;
		}
		d[source] = 0.0;
		rear = 0;
		front = 0;
		push_queue(source);

		while (rear != front) {
			int u = pop_queue();
			for (int v = 0; v < nVertices; ++v) {
				if (capacity[u][v] > flow[u][v]) {
					int sign = 1;
					if (flow[u][v] < 0) {
						sign = -1;
					}
					if (d[v] > d[u] + sign * cost[u][v]) {
						d[v] = d[u] + sign * cost[u][v];
						trace[v] = u;
						push_queue(v);
					}
				}
			}
		}

		if (d[sink] == INF) {
			return false;
		}
		return true;
	}

	private void increase_flow() {
		int delta = (int)(INF);
		int v = sink;
		while (v != source) {
			int u = trace[v];
			delta = Math.min(delta, capacity[u][v] - flow[u][v]);
			v = u;
		}
		v = sink;
		while (v != source) {
			int u = trace[v];
			flow[u][v] += delta;
			flow[v][u] -= delta;
			v = u;
		}
	}

	public ArrayList<Integer> findMatch() {
		for (int i = 0; i < nVertices; ++i) {
			for (int j = 0; j < nVertices; ++j) {
				flow[i][j] = 0;
			}
		}

		while (Bellman_Ford()) {
			increase_flow();
		}

		ArrayList<Integer> match = new ArrayList<>();
		for (int i = 0; i < N; ++i) {
			for (int j = 0; j < N; ++j) {
				if (flow[i][N + j] > 0) {
					match.add(j);
					break;
				}
			}
		}

		return match;
	}

}