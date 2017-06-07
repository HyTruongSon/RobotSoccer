// Program: Final Project in Robotics and AI
// Author: Hy Truong Son
// Major: PhD Student in Machine Learning
// Institution: Departmnet of Computer Science, The University of Chicago
// Email: hytruongson@uchicago.edu

package Algorithms;

public class Matrix {

	public int nRows;
	public int nColumns;
	public double value[][];

	// +-------------------------------------+
	// | Constructor for the Identity matrix |
	// +-------------------------------------+

	public Matrix(int nRows) {
		this.nRows = nRows;
		this.nColumns = 1;
		value = new double [this.nRows][this.nColumns];
		for (int i = 0; i < this.nRows; ++i) {
			for (int j = 0; j < this.nColumns; ++j) {
				value[i][j] = 0.0;
			}
		}
	}

	// +-----------------------------+
	// | Constructor the Zero matrix |
	// +-----------------------------+

	public Matrix(int nRows, int nColumns) {
		this.nRows = nRows;
		this.nColumns = nColumns;
		value = new double [this.nRows][this.nColumns];
		for (int i = 0; i < this.nRows; ++i) {
			for (int j = 0; j < this.nColumns; ++j) {
				value[i][j] = 0.0;
			}
		}
	}

	// +----------------------------+
	// | Constructor given an array |
	// +----------------------------+

	public Matrix(int nRows, int nColumns, double input[][]) {
		this.nRows = nRows;
		this.nColumns = nColumns;
		value = new double [this.nRows][this.nColumns];
		for (int i = 0; i < this.nRows; ++i) {
			for (int j = 0; j < this.nColumns; ++j) {
				value[i][j] = input[i][j];
			}
		}
	}

	// +-----------+
	// | Transpose |
	// +-----------+

	public Matrix transpose() {
		Matrix ret = new Matrix(nColumns, nRows);
		for (int i = 0; i < nColumns; ++i) {
			for (int j = 0; j < nRows; ++j) {
				ret.value[i][j] = value[j][i];
			}
		}
		return ret;
	}

	// +-----+
	// | Add |
	// +-----+

	public Matrix add(Matrix another) {
		Matrix ret = new Matrix(nRows, nColumns);
		for (int i = 0; i < nRows; ++i) {
			for (int j = 0; j < nColumns; ++j) {
				ret.value[i][j] = value[i][j] + another.value[i][j];
			}
		}
		return ret;
	}

	// +----------+
	// | Subtract |
	// +----------+

	public Matrix subtract(Matrix another) {
		Matrix ret = new Matrix(nRows, nColumns);
		for (int i = 0; i < nRows; ++i) {
			for (int j = 0; j < nColumns; ++j) {
				ret.value[i][j] = value[i][j] - another.value[i][j];
			}
		}
		return ret;
	}

	// +-----------------------+
	// | Matrix multiplication |
	// +-----------------------+

	public Matrix multiply(Matrix another) {
		if (nColumns != another.nRows) {
			return null;
		}
		Matrix ret = new Matrix(nRows, another.nColumns);
		for (int i = 0; i < nRows; ++i) {
			for (int j = 0; j < another.nColumns; ++j) {
				ret.value[i][j] = 0.0;
				for (int v = 0; v < nColumns; ++v) {
					ret.value[i][j] += value[i][v] * another.value[v][j];
				}
			}
		}
		return ret;
	}

	// +------------------------------------+
	// | Gauss-Jordan elimination algorithm |
	// +------------------------------------+

	public Matrix Gauss_Jordan() {
		Matrix ret = new Matrix(nRows, nColumns);
		for (int i = 0; i < nRows; ++i) {
			for (int j = 0; j < nColumns; ++j) {
				ret.value[i][j] = value[i][j];
			}
		}

		for (int k = 0; k < Math.min(nRows, nColumns); ++k) {
			// Find the k-th pivot
			int i_max = k;
			for (int i = k + 1; i < nRows; ++i) {
				if (Math.abs(ret.value[i][k]) > Math.abs(ret.value[i_max][k])) {
					i_max = i;
				}
			}
			
			// Check if the matrix is singular
			if (Math.abs(ret.value[i_max][k]) < 1e-8) {
				System.err.println("Singular matrix");
				return null;
			}

			// Swap the k-th row and the i_max row
			for (int j = k; j < nColumns; ++j) {
				double temp = ret.value[i_max][j];
				ret.value[i_max][j] = ret.value[k][j];
				ret.value[k][j] = temp;
			}

			// Do for all rows below the pivot
			for (int i = k + 1; i < nRows; ++i) {
				double f = ret.value[i][k] / ret.value[k][k];
				for (int j = k + 1; j < nColumns; ++j) {
					ret.value[i][j] -= ret.value[k][j] * f;
				}
				ret.value[i][k] = 0.0;
			}
		}

		// Reduced row-echelon form
		for (int k = nRows - 1; k >= 0; --k) {
			double value = ret.value[k][k];
			for (int j = k; j < nColumns; ++j) {
				ret.value[k][j] /= value;
			}

			for (int i = k - 1; i >= 0; --i) {
				value = ret.value[i][k];
				for (int j = k; j < nColumns; ++j) {
					ret.value[i][j] -= value * ret.value[k][j];
				}
			}
		}

		return ret;
	}

	// +---------------------------------------------------+
	// | Computing the inverse by Gauss-Jordan elimination |
	// +---------------------------------------------------+

	public Matrix inverse() {
		if (nRows != nColumns) {
			return null;
		}

		Matrix A = new Matrix(nRows, 2 * nRows);
		for (int i = 0; i < nRows; ++i) {
			for (int j = 0; j < nRows; ++j) {
				A.value[i][j] = value[i][j];
				if (i == j) {
					A.value[i][j + nRows] = 1.0;
				} else {
					A.value[i][j + nRows] = 0.0;
				}
			}
		}

		Matrix B = A.Gauss_Jordan();

		Matrix C = new Matrix(nRows, nRows);
		for (int i = 0; i < nRows; ++i) {
			for (int j = 0; j < nRows; ++j) {
				C.value[i][j] = B.value[i][j + nRows];
			}
		}
		return C;
	}

	public void print() {
		System.out.println(toString());
	}

	public String toString() {
		String str = new String();
		for (int i = 0; i < nRows; ++i) {
			for (int j = 0; j < nColumns; ++j) {
				str += Double.toString(value[i][j]) + " ";
			}
			str += "\n";
		}
		return str;
	}

}