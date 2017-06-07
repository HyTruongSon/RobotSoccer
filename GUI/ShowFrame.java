// Program: Final Project in Robotics and AI
// Author: Hy Truong Son
// Major: PhD Student in Machine Learning
// Institution: Departmnet of Computer Science, The University of Chicago
// Email: hytruongson@uchicago.edu

package GUI;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.ImageIcon;

import java.util.ArrayList;
import java.util.Random;

public class ShowFrame extends JFrame {

	private int widthFrame;
	private int heightFrame;
	private JButton pictureButton;

    // Randomization
    private Random rand = new Random();

	public ShowFrame(String titleFrame, int widthFrame, int heightFrame) {
		this.widthFrame = widthFrame;
		this.heightFrame = heightFrame;

		setTitle(titleFrame);
        setSize(widthFrame, heightFrame);
        setResizable(false);
        setLayout(null);
        setLocation(rand.nextInt(500), rand.nextInt(500));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        pictureButton = new JButton();
        add(pictureButton);

        setVisible(true);
	}

	private int RGB(int red, int green, int blue){
        return (0xff000000) | (red << 16) | (green << 8) | blue;
    }

	public void drawPicture(ObjectImage img) {
		BufferedImage outputImage = new BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < img.width; ++i) {
            for (int j = 0; j < img.height; ++j) {
                outputImage.setRGB(i, j, RGB(img.red[i][j], img.green[i][j], img.blue[i][j]));
            }
        }

		pictureButton.setBounds(0, 0, widthFrame, heightFrame);
        pictureButton.setIcon(new ImageIcon(outputImage.getScaledInstance(widthFrame, heightFrame, Image.SCALE_DEFAULT)));
	}

    public void drawPicture(ObjectImage img, ArrayList<Coordinate> estimate_robots, ArrayList<Coordinate> estimate_balls) {
        BufferedImage outputImage = new BufferedImage(img.width, img.height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < img.width; ++i) {
            for (int j = 0; j < img.height; ++j) {
                outputImage.setRGB(i, j, RGB(img.red[i][j], img.green[i][j], img.blue[i][j]));
            }
        }

        Graphics2D g2d = outputImage.createGraphics();
        g2d.setFont(new Font("Serif", Font.BOLD, 10));

        for (int i = 0; i < estimate_robots.size(); ++i) {
            int x = estimate_robots.get(i).x;
            int y = estimate_robots.get(i).y;
            String str = "Robot " + Integer.toString(i + 1);
            FontMetrics fm = g2d.getFontMetrics();
            g2d.setPaint(Color.ORANGE);
            g2d.drawString(str, x, y);
        }

        for (int i = 0; i < estimate_balls.size(); ++i) {
            int x = estimate_balls.get(i).x;
            int y = estimate_balls.get(i).y;
            String str = "Ball " + Integer.toString(i + 1);
            FontMetrics fm = g2d.getFontMetrics();
            g2d.setPaint(Color.ORANGE);
            g2d.drawString(str, x, y);
        }

        g2d.dispose();

        pictureButton.setBounds(0, 0, widthFrame, heightFrame);
        pictureButton.setIcon(new ImageIcon(outputImage.getScaledInstance(widthFrame, heightFrame, Image.SCALE_DEFAULT)));
    }

}