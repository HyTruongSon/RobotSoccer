// Program: Final Project in Robotics and AI
// Author: Hy Truong Son
// Major: PhD Student in Machine Learning
// Institution: Departmnet of Computer Science, The University of Chicago
// Email: hytruongson@uchicago.edu

import GUI.*;
import Algorithms.*;

public class MainProgram {

	public static void main(String args[]) {
		new Thread() {
			@Override
			public void run() {
				new StartingFrame();
			}
		}.start();
	}

}