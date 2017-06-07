// Program: Final Project in Robotics and AI
// Author: Hy Truong Son
// Major: PhD Student in Machine Learning
// Institution: Departmnet of Computer Science, The University of Chicago
// Email: hytruongson@uchicago.edu

package GUI;

import Algorithms.Normalization;
import Algorithms.BallDetection;
import Algorithms.RobotDetection;
import Algorithms.KalmanFilter;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

public class Agent {

	// Field image
	private ObjectImage fieldImage;

	// Robot image
	private ObjectImage robotImage;

	// Ball image
	private ObjectImage ballImage; 

	// Step size in pixels
	private int step;

	// Probability of changing direction
	private int changeDirPercent;

	// Delay time in miliseconds
	private int delayTime;

	// Input from Camera
	private ObjectImage camera;

	// Background subtraction and color detections from input camera
	private ObjectImage filter;

	// Output of camera image with corrected positions of Robots and Balls by Kalman Filter
	private ObjectImage processed_camera;

	// Output of filter image with objects detection
	private ObjectImage processed_filter;

	// Number of robots
	private int nRobots;

	// Number of balls
	private int nBalls;

	// Robot ground-truth positions
	private ArrayList < Coordinate > robots;

	// Ball ground-truth positions
	private ArrayList < Coordinate > balls;

	// Moving directions of robots
	private ArrayList < Integer > dir_robots;

	// Moving directions of balls
	private ArrayList < Integer > dir_balls;

	// Algorithm to detect robots and return the rectangles (white-color detection and BFS algorithm)
	private RobotDetection robotDetection;

	// Algorithm to detect balls and return the rectangles (red-color detection and BFS algorithm)
	private BallDetection ballDetection;

	// Kalman Filter 1 for Robots
	private KalmanFilter robot_kalmanFilter;

	// Kalman Filter 2 for Balls
	private KalmanFilter ball_kalmanFilter;

	// Boolean variable to determine to use Kalman Filter or not
	private boolean useKF;

	// Motion model noise
	private double motionNoise;

	// Measurement noise
	private double measurementNoise;

	// Directions
	private final int nDir = 8;
	private final int DX[] = {-1, 1, 0, 0, -1, -1, 1, 1};
	private final int DY[] = {0, 0, -1, 1, -1, 1, -1, 1};

	// Frame to show Camera
	private ShowFrame cameraFrame;

	// Frame to show filtered image from the Camera
	private ShowFrame filterFrame;

	// Randomization
	private Random rand = new Random();

	// +-------------+
	// | Constructor |
	// +-------------+

	public Agent(int nRobots, int robotSize, int nBalls, int ballSize, String fieldImageName, String robotImageName, String ballImageName, int step, int changeDirPercent, int delayTime, boolean useKF, double motionNoise, double measurementNoise) {
		System.out.println("Number of robots: " + Integer.toString(nRobots));
		System.out.println("Robot size in pixels: " + Integer.toString(robotSize));
		System.out.println("Number of balls: " + Integer.toString(nBalls));
		System.out.println("Ball size in pixels: " + Integer.toString(ballSize));	
		System.out.println("Field image: " + fieldImageName);
		System.out.println("Robot image: " + robotImageName);
		System.out.println("Ball image: " + ballImageName);
		System.out.println("Step size in pixels: " + Integer.toString(step));
		System.out.println("Percent of changing directions: " + Integer.toString(changeDirPercent));
		System.out.println("Delay time: " + Integer.toString(delayTime));
		System.out.println("Motion model noise: " + Double.toString(motionNoise));
		System.out.println("Measurement noise: " + Double.toString(measurementNoise));
		System.out.println("Use Kalman Filter: " + Boolean.toString(useKF));

		fieldImage = getImage(fieldImageName);
		robotImage = getImage(robotImageName, robotSize);
		ballImage = getImage(ballImageName, ballSize);

		this.nRobots = nRobots;
		this.nBalls = nBalls;

		cameraFrame = new ShowFrame("Camera frame", fieldImage.width, fieldImage.height);
		filterFrame = new ShowFrame("Filter frame", fieldImage.width, fieldImage.height);

		camera = new ObjectImage(fieldImage.width, fieldImage.height);
		processed_camera = new ObjectImage(fieldImage.width, fieldImage.height);

		filter = new ObjectImage(fieldImage.width, fieldImage.height);
		processed_filter = new ObjectImage(fieldImage.width, fieldImage.height);

		robotDetection = new RobotDetection(fieldImage.width, fieldImage.height);
		ballDetection = new BallDetection(fieldImage.width, fieldImage.height);

		this.step = step;
		this.changeDirPercent = changeDirPercent;
		this.delayTime = delayTime;
		this.motionNoise = motionNoise;
		this.measurementNoise = measurementNoise;
		this.useKF = useKF;

		process();
	}

	// +---------------+
	// | Infinite loop |
	// +---------------+

	private void process() {
		// Randomly initialize the Robots positions
		initPositions();

		// Initialize the Kalman Filter for Robots
		robot_kalmanFilter = new KalmanFilter(this.nRobots, robots, (double)(step), motionNoise, measurementNoise);

		// Initialize the Kalman Filter for Balls
		ball_kalmanFilter = new KalmanFilter(this.nBalls, balls, (double)(step), motionNoise, measurementNoise);

		// Infinite loop
		while (true) {
			// Move the Robots and the Balls
			genPositions();

			// Generate a synthetic Camera image
			genCamera();

			// Generate a filtered image from the Camera
			genFilter();

			// Draw the Camera image
			cameraFrame.drawPicture(camera);

			// Robots detection
			ArrayList<Rectangle> rect_robots = robotDetection.detect(filter);

			// Balls detection
			ArrayList<Rectangle> rect_balls = ballDetection.detect(filter);

			// Add rectangles to the filter image
			addRectsFilter(rect_robots, rect_balls);

			// Update the new Robots positions by Kalman Filter
			ArrayList<Coordinate> estimate_robots = null;
			if (useKF) {
				estimate_robots = robot_kalmanFilter.filter_on(rect_robots);
			} else {
				estimate_robots = robot_kalmanFilter.filter_off(rect_robots);
			}

			// Update the new Balls positions by Kalman Filter
			ArrayList<Coordinate> estimate_balls = null;
			if (useKF) {
				estimate_balls = ball_kalmanFilter.filter_on(rect_balls);
			} else {
				estimate_balls = ball_kalmanFilter.filter_off(rect_balls);
			}

			// Draw the output picture
			filterFrame.drawPicture(processed_filter, estimate_robots, estimate_balls);

			// Delay sometime for the users to see the results
			if (delayTime > 0) {
				try {
					Thread.sleep(delayTime);
				} catch (Exception exc) {
					System.err.println(exc.toString());
				}
			}
		}
	}

	// +-----------------------------------+
	// | Generate a synthetic Camera image |
	// +-----------------------------------+

	private void genCamera() {
		for (int x = 0; x < fieldImage.width; ++x) {
			for (int y = 0; y < fieldImage.height; ++y) {
				camera.red[x][y] = fieldImage.red[x][y];
				camera.green[x][y] = fieldImage.green[x][y];
				camera.blue[x][y] = fieldImage.blue[x][y];
			}
		}

		for (int i = 0; i < nBalls; ++i) {
			int u = balls.get(i).x - ballImage.width / 2;
			int v = balls.get(i).y - ballImage.height / 2;

			for (int x = 0; x < ballImage.width; ++x) {
				for (int y = 0; y < ballImage.height; ++y) {
					if (isNotWhite(ballImage.red[x][y], ballImage.green[x][y], ballImage.blue[x][y])) {
						camera.red[u + x][v + y] = ballImage.red[x][y];
						camera.green[u + x][v + y] = ballImage.green[x][y];
						camera.blue[u + x][v + y] = ballImage.blue[x][y];
					}
				}
			}
		}

		for (int i = 0; i < nRobots; ++i) {
			int u = robots.get(i).x - robotImage.width / 2;
			int v = robots.get(i).y - robotImage.height / 2;

			for (int x = 0; x < robotImage.width; ++x) {
				for (int y = 0; y < robotImage.height; ++y) {
					if (isNotWhite(robotImage.red[x][y], robotImage.green[x][y], robotImage.blue[x][y])) {
						camera.red[u + x][v + y] = robotImage.red[x][y];
						camera.green[u + x][v + y] = robotImage.green[x][y];
						camera.blue[u + x][v + y] = robotImage.blue[x][y];
					}
				}
			}
		}
	}

	// +----------------------------------------------------------------+
	// | From the Camera image, filter background, red and white colors |
	// +----------------------------------------------------------------+

	private void genFilter() {
		for (int x = 0; x < filter.width; ++x) {
			for (int y = 0; y < filter.height; ++y) {
				if ((camera.red[x][y] == fieldImage.red[x][y]) && (camera.green[x][y] == fieldImage.green[x][y]) && (camera.blue[x][y] == fieldImage.blue[x][y])) {
					filter.red[x][y] = 0;
					filter.green[x][y] = 0;
					filter.blue[x][y] = 0;
					continue;
				}

				if ((camera.red[x][y] >= 150) && (camera.green[x][y] <= 50) && (camera.blue[x][y] <= 50)) {
					filter.red[x][y] = 255;
					filter.green[x][y] = 0;
					filter.blue[x][y] = 0;
					continue;
				}

				filter.red[x][y] = 255;
				filter.green[x][y] = 255;
				filter.blue[x][y] = 255;
			}
		}
	}

	// +----------------------------------------------------------------------+
	// | Add rectangles of the objects detected by Object Detection algorithm |
	// +----------------------------------------------------------------------+

	private void addRectsFilter(ArrayList<Rectangle> rect_robots, ArrayList<Rectangle> rect_balls) {
		for (int x = 0; x < filter.width; ++x) {
			for (int y = 0; y < filter.height; ++y) {
				processed_filter.red[x][y] = filter.red[x][y];
				processed_filter.green[x][y] = filter.green[x][y];
				processed_filter.blue[x][y] = filter.blue[x][y];
			}
		}

		for (int i = 0; i < rect_robots.size(); ++i) {
			int x1 = rect_robots.get(i).top.x;
			int y1 = rect_robots.get(i).top.y;

			int x2 = rect_robots.get(i).bottom.x;
			int y2 = rect_robots.get(i).bottom.y;

			for (int x = x1; x <= x2; ++x) {
				processed_filter.red[x][y1] = 0;
				processed_filter.green[x][y1] = 255;
				processed_filter.blue[x][y1] = 0;

				processed_filter.red[x][y2] = 0;
				processed_filter.green[x][y2] = 255;
				processed_filter.blue[x][y2] = 0;
			}

			for (int y = y1; y <= y2; ++y) {
				processed_filter.red[x1][y] = 0;
				processed_filter.green[x1][y] = 255;
				processed_filter.blue[x1][y] = 0;

				processed_filter.red[x2][y] = 0;
				processed_filter.green[x2][y] = 255;
				processed_filter.blue[x2][y] = 0;
			}
		}

		for (int i = 0; i < rect_balls.size(); ++i) {
			int x1 = rect_balls.get(i).top.x;
			int y1 = rect_balls.get(i).top.y;

			int x2 = rect_balls.get(i).bottom.x;
			int y2 = rect_balls.get(i).bottom.y;

			for (int x = x1; x <= x2; ++x) {
				if (Math.min(x - x1, x2 - x) <= 5) {
					processed_filter.red[x][y1] = 0;
					processed_filter.green[x][y1] = 255;
					processed_filter.blue[x][y1] = 0;

					processed_filter.red[x][y2] = 0;
					processed_filter.green[x][y2] = 255;
					processed_filter.blue[x][y2] = 0;
				}
			}

			for (int y = y1; y <= y2; ++y) {
				if (Math.min(y - y1, y2 - y) <= 5) {
					processed_filter.red[x1][y] = 0;
					processed_filter.green[x1][y] = 255;
					processed_filter.blue[x1][y] = 0;

					processed_filter.red[x2][y] = 0;
					processed_filter.green[x2][y] = 255;
					processed_filter.blue[x2][y] = 0;
				}
			}
		}
	}

	// +--------------------------------------------------------------------+
	// | Randomize the initial positions and directions of Robots and Balls |
	// +--------------------------------------------------------------------+

	private void initPositions() {
		robots = new ArrayList<>();
		dir_robots = new ArrayList<>();

		balls = new ArrayList<>();
		dir_balls = new ArrayList<>();

		for (int i = 0; i < nRobots; ++i) {
			while (true) {
				int x = Math.abs(rand.nextInt()) % fieldImage.width;
				int y = Math.abs(rand.nextInt()) % fieldImage.height;

				if (x - robotImage.width / 2 < 0) {
					continue;
				} 
				if (x + robotImage.width / 2 >= fieldImage.width) {
					continue;
				}
				if (y - robotImage.height / 2 < 0) {
					continue;
				} 
				if (y + robotImage.height / 2 >= fieldImage.height) {
					continue;
				}

				robots.add(new Coordinate(x, y));
				break;
			}

			dir_robots.add(Math.abs(rand.nextInt()) % nDir);
		}

		for (int i = 0; i < nBalls; ++i) {
			while (true) {
				int x = Math.abs(rand.nextInt()) % fieldImage.width;
				int y = Math.abs(rand.nextInt()) % fieldImage.height;

				if (x - ballImage.width / 2 < 0) {
					continue;
				} 
				if (x + ballImage.width / 2 >= fieldImage.width) {
					continue;
				}
				if (y - ballImage.height / 2 < 0) {
					continue;
				} 
				if (y + ballImage.height / 2 >= fieldImage.height) {
					continue;
				}

				balls.add(new Coordinate(x, y));
				break;
			}

			dir_balls.add(Math.abs(rand.nextInt()) % nDir);
		}
	}

	// +--------------------------------------------------------------------------+
	// | Randomize the orientations, then moving the robots and balls accordingly |
	// +--------------------------------------------------------------------------+

	private void genPositions() {
		for (int i = 0; i < nRobots; ++i) {
			while (true) {
				int d = dir_robots.get(i);
				if (Math.abs(rand.nextInt()) % 100 <= changeDirPercent) {
					d = Math.abs(rand.nextInt()) % nDir;
				}

				int x = robots.get(i).x + DX[d] * step;
				int y = robots.get(i).y + DY[d] * step;

				if (x - robotImage.width / 2 < 0) {
					continue;
				} 
				if (x + robotImage.width / 2 >= fieldImage.width) {
					continue;
				}
				if (y - robotImage.height / 2 < 0) {
					continue;
				} 
				if (y + robotImage.height / 2 >= fieldImage.height) {
					continue;
				}

				dir_robots.set(i, d);
				robots.set(i, new Coordinate(x, y));
				break;
			}
		}

		for (int i = 0; i < nBalls; ++i) {
			while (true) {
				int d = dir_balls.get(i);
				if (Math.abs(rand.nextInt()) % 100 <= changeDirPercent) {
					d = Math.abs(rand.nextInt()) % nDir;
				}

				int x = balls.get(i).x + DX[d] * step;
				int y = balls.get(i).y + DY[d] * step;

				if (x - ballImage.width / 2 < 0) {
					continue;
				} 
				if (x + ballImage.width / 2 >= fieldImage.width) {
					continue;
				}
				if (y - ballImage.height / 2 < 0) {
					continue;
				} 
				if (y + ballImage.height / 2 >= fieldImage.height) {
					continue;
				}

				dir_balls.set(i, d);
				balls.set(i, new Coordinate(x, y));
				break;
			}
		}
	}

	// +------------------------+
	// | Read images from files |
	// +------------------------+

	private ObjectImage getImage(String imageName) {
		BufferedImage inputImage = null;
        try {
            inputImage = ImageIO.read(new File(imageName));
        } catch (IOException exc) {
            System.err.println(exc.toString());
            JOptionPane.showMessageDialog(null, "Cannot find the image " + imageName);
        }

        ObjectImage img = new ObjectImage();

        img.width = inputImage.getWidth(null);
        img.height = inputImage.getHeight(null);

        img.red = new int [img.width][img.height];
        img.green = new int [img.width][img.height];
        img.blue = new int [img.width][img.height];

        for (int i = 0; i < img.width; ++i) {
            for (int j = 0; j < img.height; ++j) {
                int RGB = inputImage.getRGB(i, j);
                img.red[i][j] = (RGB & 0x00ff0000) >> 16;
                img.green[i][j] = (RGB & 0x0000ff00) >> 8;
                img.blue[i][j] = RGB & 0x000000ff;
            }
        }

		return img;
	}

	// +------------------------------------------------------+
	// | Extract the white-color background from input images |
	// +------------------------------------------------------+

	private boolean isNotWhite(int red, int green, int blue) {
		if ((red != 255) || (green != 255) || (blue != 255)) {
			return true;
		}
		return false;
	}

	// +--------------------------------------------------------------------------+
	// | Read image from file, then remove the white-color background, and resize |
	// +--------------------------------------------------------------------------+

	private ObjectImage getImage(String imageName, int size) {
		BufferedImage inputImage = null;
        try {
            inputImage = ImageIO.read(new File(imageName));
        } catch (IOException exc) {
            System.err.println(exc.toString());
            JOptionPane.showMessageDialog(null, "Cannot find the image " + imageName);
        }

        int inputWidth = inputImage.getWidth(null);
        int inputHeight = inputImage.getHeight(null);

        int inputRed[][] = new int [inputWidth][inputHeight];
        int inputGreen[][] = new int [inputWidth][inputHeight];
        int inputBlue[][] = new int [inputWidth][inputHeight];

        for (int i = 0; i < inputWidth; ++i) {
            for (int j = 0; j < inputHeight; ++j) {
                int RGB = inputImage.getRGB(i, j);
                inputRed[i][j] = (RGB & 0x00ff0000) >> 16;
                inputGreen[i][j] = (RGB & 0x0000ff00) >> 8;
                inputBlue[i][j] = RGB & 0x000000ff;
            }
        }

        int leftX = 0;
        for (int x = 0; x < inputWidth; ++x) {
        	boolean found = false;
        	for (int y = 0; y < inputHeight; ++y) {
        		if (isNotWhite(inputRed[x][y], inputGreen[x][y], inputBlue[x][y])) {
        			found = true;
        			break;
        		}
        	}
        	if (found) {
        		leftX = x;
        		break;
        	}
        }

        int rightX = inputWidth - 1;
        for (int x = inputWidth - 1; x >= 0; --x) {
        	boolean found = false;
        	for (int y = 0; y < inputHeight; ++y) {
        		if (isNotWhite(inputRed[x][y], inputGreen[x][y], inputBlue[x][y])) {
        			found = true;
        			break;
        		}
        	}
        	if (found) {
        		rightX = x;
        		break;
        	}
        }

        int lowY = 0;
        for (int y = 0; y < inputHeight; ++y) {
        	boolean found = false;
        	for (int x = 0; x < inputWidth; ++x) {
        		if (isNotWhite(inputRed[x][y], inputGreen[x][y], inputBlue[x][y])) {
        			found = true;
        			break;
        		}
        	}
        	if (found) {
        		lowY = y;
        		break;
        	}
        }

        int highY = inputHeight - 1;
        for (int y = inputHeight - 1; y >= 0; --y) {
        	boolean found = false;
        	for (int x = 0; x < inputWidth; ++x) {
        		if (isNotWhite(inputRed[x][y], inputGreen[x][y], inputBlue[x][y])) {
        			found = true;
        			break;
        		}
        	}
        	if (found) {
        		highY = y;
        		break;
        	}
        }

        int cropWidth = rightX - leftX + 1;
        int cropHeight = highY - lowY + 1;

        int cropRed[][] = new int [cropWidth][cropHeight];
        int cropGreen[][] = new int [cropWidth][cropHeight];
        int cropBlue[][] = new int [cropWidth][cropHeight];

        for (int x = 0; x < cropWidth; ++x) {
        	for (int y = 0; y < cropHeight; ++y) {
        		cropRed[x][y] = inputRed[x + leftX][y + lowY];
        		cropGreen[x][y] = inputGreen[x + leftX][y + lowY];
        		cropBlue[x][y] = inputBlue[x + leftX][y + lowY];
        	}
        }

		ObjectImage img = new ObjectImage();
		
		img.width = size;
		img.height = size;

		img.red = new int [img.width][img.height];
		img.green = new int [img.width][img.height];
		img.blue = new int [img.width][img.height];

		Normalization.Standardize(cropRed, img.red, cropWidth, cropHeight, img.width, img.height);
		Normalization.Standardize(cropGreen, img.green, cropWidth, cropHeight, img.width, img.height);
		Normalization.Standardize(cropBlue, img.blue, cropWidth, cropHeight, img.width, img.height);

		return img;
	}

	// +--------------------------------------------+
	// | Convert red, green and blue into RGB value |
	// +--------------------------------------------+

	private int RGB(int red, int green, int blue){
        return (0xff000000) | (red << 16) | (green << 8) | blue;
    }

    // +-----------------------+
    // | Write image into file |
    // +-----------------------+

	private void writeImage(ObjectImage img, String imageName) {
		BufferedImage outputImage = new BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < img.width; ++i) {
            for (int j = 0; j < img.height; ++j) {
                outputImage.setRGB(i, j, RGB(img.red[i][j], img.green[i][j], img.blue[i][j]));
            }
        }
        
        try {
            ImageIO.write(outputImage, "jpg", new File(imageName));
        } catch (IOException exc) {
            System.err.println(exc.toString());
        }
	}

}