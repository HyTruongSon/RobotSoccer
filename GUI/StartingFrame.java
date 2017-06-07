// Program: Final Project in Robotics and AI
// Author: Hy Truong Son
// Major: PhD Student in Machine Learning
// Institution: Departmnet of Computer Science, The University of Chicago
// Email: hytruongson@uchicago.edu

package GUI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class StartingFrame extends JFrame {

	private final String titleFrame = "Final Project in Robotics and AI";
	private final int widthFrame = 600;
	private final int heightFrame = 700;
	private final int marginFrame = 20;
	private final int widthLabel = 400;
	private final int widthBox = widthFrame - widthLabel - 3 * marginFrame;
	private final int heightComponent = 30;

	private final String[] robots = {"1", "2", "3", "4", "5", "6"};
	private final String[] balls = {"1", "2", "3", "4", "5", "6"};
	private final String[] robotSizes = {"100", "200", "300", "400"};
	private final String[] ballSizes = {"50", "100"};
	private final String[] steps = {"10", "20", "30", "40", "50", "60", "70", "80", "90", "100", "110", "120", "130", "140", "150", "160", "170", "180", "190", "200"};
	private final String[] probs = {"10", "20", "30", "40", "50", "60", "70", "80", "90", "100"};
	private final String[] times = {"0", "10", "50", "100"};
	private final String[] options = {"Yes", "No"};

	private final String fieldImageName = "Images/soccer-field.png";
	private final String robotImageName = "Images/NAO.jpg";
	private final String ballImageName = "Images/red-ball.png";
 
	private JLabel robotLabel;
	private JComboBox robotBox;

	private JLabel robotSizeLabel;
	private JComboBox robotSizeBox;

	private JLabel ballLabel;
	private JComboBox ballBox;

	private JLabel ballSizeLabel;
	private JComboBox ballSizeBox;

	private JLabel stepLabel;
	private JComboBox stepBox;

	private JLabel probLabel;
	private JComboBox probBox;

	private JLabel timeLabel;
	private JComboBox timeBox;

	private JLabel optionLabel;
	private JComboBox optionBox;

	private JLabel motionNoiseLabel;
	private JTextField motionNoiseText;

	private JLabel measurementNoiseLabel;
	private JTextField measurementNoiseText;

	private JButton aboutButton;
	private JButton startButton;

	public StartingFrame() {
		setTitle(titleFrame);
		setSize(widthFrame, heightFrame);
		setResizable(false);
		setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		// Number of robots
		int x = marginFrame;
		int y = marginFrame;

		robotLabel = new JLabel("Number of robots:");
		robotLabel.setBounds(x, y, widthLabel, heightComponent);
		robotLabel.setForeground(Color.BLUE);
		add(robotLabel);

		x += widthLabel + marginFrame;

		robotBox = new JComboBox(robots);
		robotBox.setBounds(x, y, widthBox, heightComponent);
		add(robotBox);

		// Robot size
		x = marginFrame;
		y += heightComponent + marginFrame;

		robotSizeLabel = new JLabel("Robot size in pixels:");
		robotSizeLabel.setBounds(x, y, widthLabel, heightComponent);
		robotSizeLabel.setForeground(Color.BLUE);
		add(robotSizeLabel);

		x += widthLabel + marginFrame;

		robotSizeBox = new JComboBox(robotSizes);
		robotSizeBox.setBounds(x, y, widthBox, heightComponent);
		add(robotSizeBox);

		// Number of balls
		x = marginFrame;
		y += heightComponent + marginFrame;

		ballLabel = new JLabel("Number of balls:");
		ballLabel.setBounds(x, y, widthLabel, heightComponent);
		ballLabel.setForeground(Color.BLUE);
		add(ballLabel);

		x += widthLabel + marginFrame;

		ballBox = new JComboBox(robots);
		ballBox.setBounds(x, y, widthBox, heightComponent);
		add(ballBox);

		// Ball size
		x = marginFrame;
		y += heightComponent + marginFrame;

		ballSizeLabel = new JLabel("Ball size in pixels:");
		ballSizeLabel.setBounds(x, y, widthLabel, heightComponent);
		ballSizeLabel.setForeground(Color.BLUE);
		add(ballSizeLabel);

		x += widthLabel + marginFrame;

		ballSizeBox = new JComboBox(ballSizes);
		ballSizeBox.setBounds(x, y, widthBox, heightComponent);
		add(ballSizeBox);

		// Step size
		x = marginFrame;
		y += heightComponent + marginFrame;

		stepLabel = new JLabel("Step size in pixels:");
		stepLabel.setBounds(x, y, widthLabel, heightComponent);
		stepLabel.setForeground(Color.BLUE);
		add(stepLabel);

		x += widthLabel + marginFrame;

		stepBox = new JComboBox(steps);
		stepBox.setBounds(x, y, widthBox, heightComponent);
		add(stepBox);

		// Probability
		x = marginFrame;
		y += heightComponent + marginFrame;

		probLabel = new JLabel("Probability (Percent) of changing directions:");
		probLabel.setBounds(x, y, widthLabel, heightComponent);
		probLabel.setForeground(Color.BLUE);
		add(probLabel);

		x += widthLabel + marginFrame;

		probBox = new JComboBox(probs);
		probBox.setBounds(x, y, widthBox, heightComponent);
		add(probBox);

		// Delay time
		x = marginFrame;
		y += heightComponent + marginFrame;

		timeLabel = new JLabel("Delay time (miliseconds):");
		timeLabel.setBounds(x, y, widthLabel, heightComponent);
		timeLabel.setForeground(Color.BLUE);
		add(timeLabel);

		x += widthLabel + marginFrame;

		timeBox = new JComboBox(times);
		timeBox.setBounds(x, y, widthBox, heightComponent);
		add(timeBox);

		// Motion model noise
		x = marginFrame;
		y += heightComponent + marginFrame;

		motionNoiseLabel = new JLabel("Motion model noise:");
		motionNoiseLabel.setBounds(x, y, widthLabel, heightComponent);
		motionNoiseLabel.setForeground(Color.BLUE);
		add(motionNoiseLabel);

		x += widthLabel + marginFrame;

		motionNoiseText = new JTextField("1.0");
		motionNoiseText.setBounds(x, y, widthBox, heightComponent);
		add(motionNoiseText);

		// Measurement noise
		x = marginFrame;
		y += heightComponent + marginFrame;

		measurementNoiseLabel = new JLabel("Measurement noise:");
		measurementNoiseLabel.setBounds(x, y, widthLabel, heightComponent);
		measurementNoiseLabel.setForeground(Color.BLUE);
		add(measurementNoiseLabel);

		x += widthLabel + marginFrame;

		measurementNoiseText = new JTextField("1.0");
		measurementNoiseText.setBounds(x, y, widthBox, heightComponent);
		add(measurementNoiseText);

		// Kalman Filter options
		x = marginFrame;
		y += heightComponent + marginFrame;

		optionLabel = new JLabel("Use Kalman Filter or not:");
		optionLabel.setBounds(x, y, widthLabel, heightComponent);
		optionLabel.setForeground(Color.BLUE);
		add(optionLabel);

		x += widthLabel + marginFrame;

		optionBox = new JComboBox(options);
		optionBox.setBounds(x, y, widthBox, heightComponent);
		add(optionBox);

		// About button
		x = marginFrame;
		y += heightComponent + marginFrame;

		aboutButton = new JButton("About Me");
		aboutButton.setBounds(x, y, widthFrame - 2 * marginFrame, heightComponent);
		add(aboutButton);

		aboutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					@Override
					public void run() {
						AboutFrame aboutFrame = new AboutFrame();
					}
				}.start();
			}
		});

		// Start button
		x = marginFrame;
		y += heightComponent + marginFrame;

		startButton = new JButton("Start");
		startButton.setBounds(x, y, widthFrame - 2 * marginFrame, heightComponent);
		add(startButton);

		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final int nRobots = Integer.parseInt(robots[robotBox.getSelectedIndex()]);
				final int robotSize = Integer.parseInt(robotSizes[robotSizeBox.getSelectedIndex()]);
				
				final int nBalls = Integer.parseInt(balls[ballBox.getSelectedIndex()]);
				final int ballSize = Integer.parseInt(ballSizes[ballSizeBox.getSelectedIndex()]);

				final int step = Integer.parseInt(steps[stepBox.getSelectedIndex()]);
				final int prob = Integer.parseInt(probs[probBox.getSelectedIndex()]);
				final int time = Integer.parseInt(times[timeBox.getSelectedIndex()]);

				final double motionNoise = Double.parseDouble(motionNoiseText.getText());
				final double measurementNoise = Double.parseDouble(measurementNoiseText.getText());

				if (motionNoise <= 0.0) {
					JOptionPane.showMessageDialog(null, "The motion noise must be greater than zero!");
					return;
				}

				if (measurementNoise <= 0.0) {
					JOptionPane.showMessageDialog(null, "The measurement noise must be greater than zero!");
					return;
				}

				boolean opt = true;
				if (optionBox.getSelectedIndex() == 1) {
					opt = false;
				}
				final boolean useKF = opt;

				new Thread() {
					@Override
					public void run() {
						Agent agent = new Agent(nRobots, robotSize, nBalls, ballSize, fieldImageName, robotImageName, ballImageName, step, prob, time, useKF, motionNoise, measurementNoise);	
					}
				}.start();
			}
		});

		setVisible(true);
	}

} 