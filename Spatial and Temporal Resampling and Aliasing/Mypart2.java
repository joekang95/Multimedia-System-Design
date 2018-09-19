/********************
 * Name: Joe Chang
 * USCID: 8800444224
 *******************/

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.*;

public class Mypart2 implements ActionListener{

	static JFrame frame;
	static JLabel lbIm1;
	static JLabel lbIm2;
	static BufferedImage img;
	static BufferedImage img2;
	static BufferedImage tmp;
	static int width = 512;
	static int height = 512;
	static int xc = width/2;
	static int yc = height/2;
    private Timer left, right;
    static int n;
    static float s;
    static float fps;
    static float orifps;
    private float counter = 0;
    private float counter2 = 0;

	// Draws a black line on the given buffered image from the pixel defined by (x1, y1) to (x2, y2)
	public static void drawLine(BufferedImage image, int x1, int y1, int x2, int y2, boolean f) {
		Graphics2D g = image.createGraphics();
		if(f){
			g.setColor(Color.RED);
			g.setStroke(new BasicStroke(2));
		}
		else{
			g.setColor(Color.BLACK);
			g.setStroke(new BasicStroke(1));
		}
		/*g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(1));*/
		g.drawLine(x1, y1, x2, y2);
		g.drawImage(image, 0, 0, null);
	}
	
	// Draws a circle with center (x, y) and radius r
	/*public static void drawCircle(BufferedImage image, int x, int y, int r) {
		Graphics2D g = image.createGraphics();g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(1));
		x = x - (r / 2);
		y = y - (r / 2);
		g.drawOval(x, y, r, r);
	}*/
	
	public void showImg(String[] args){
		//args[0] -> Number of lines
        //args[1] -> Speed of rotations
        //args[2] -> Fps
		
		// Read a parameter from command line

		String param0, param1, param2;
		try{
			param0 = args[0];
			param1 = args[1];
			param2 = args[2];
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("Input Format Error");
			return ;
		}

		// Initialize images
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		img2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		/*************************
		 * Part 1 
		 * parameter: 
		 * n, s (Speed of Rotation), 
		 * fps(Right fps)
		 *************************/

		n = Integer.parseInt(param0);				// First parameter n : draw lines separated by 360/n degrees 
		s = Float.parseFloat(param1);				// Second parameter s : speed of rotation = rotation / second 
		fps = Float.parseFloat(param2);				// Third parameter fps : sample frame / second
		orifps = 36 * s;							// Original video's fps
		
		/*************************
		 * Part 2 
		 * Print Images
		 *************************/
		
		// Use labels to display the images
		frame = new JFrame();
		GridBagLayout gLayout = new GridBagLayout();
		frame.getContentPane().setLayout(gLayout);

		lbIm1 = new JLabel(new ImageIcon(img));
		lbIm2 = new JLabel(new ImageIcon(img2));

		frame.getContentPane().add(lbIm1);
		frame.getContentPane().add(lbIm2);

		frame.pack();
		frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		left = new Timer((int)(1000/(orifps)), this);		// For every 1 second / frames, perform action
		left.start();										// Begin left video
		right = new Timer((int)(1000/(fps)), this);			// For every 1 second / frames, perform action
		right.start();										// Begin right video
		
	}
	
	/*****************************
	 * Function drawImg
	 * Input: n, theta, image
	 * Output: none
	 ****************************/
	
	public static void drawImg(int n, float theta, BufferedImage tmp){
		
		//drawCircle(tmp,xc, yc, height);				// Draws wheel's outer frame
		int x2 = 0, y2 = 0;		
		boolean first = true;						// Determine if colored red
		theta = theta % 360;						// Convert degree to within 360
		
		for(int i = 0 ; i < n ; i++){				// Draw lines 
			if(theta == 0) {
				drawLine(tmp, xc, yc, width, yc, first);
			}
			else if(theta <= 45 || theta > 315) {
				y2 = ((int)(xc * Math.tan(theta * Math.PI / 180)));
				drawLine(tmp, xc, yc, width, yc + y2, first);
				/*x2 = ((int)(xc * Math.cos(theta * Math.PI / 180)));		// x amount = radius * radian = radius * cos(degree * PI/180)
				y2 = ((int)(yc * Math.sin(theta * Math.PI / 180)));		// y amount = radius * radian = radius * sin(degree * PI/180)
				drawLine(tmp, xc, yc, xc + x2, yc + y2, first);*/
			}
			else if(theta == 90) {
				drawLine(tmp, xc, yc, xc, height, first);
			}
			else if(theta > 45 && theta <= 135) {
				x2 = ((int)(yc / Math.tan(theta * Math.PI / 180)));
				drawLine(tmp, xc, yc, xc + x2, height, first);
				/*x2 = ((int)(xc * Math.cos(theta * Math.PI / 180)));
				y2 = ((int)(yc * Math.sin(theta * Math.PI / 180)));
				drawLine(tmp, xc, yc, xc + x2, yc + y2, first);*/
			}
			else if(theta == 180) {
				drawLine(tmp, xc, yc, 0, yc, first);
			}
			else if(theta > 135 && theta <= 225) {
				y2 = ((int)(xc * Math.tan(theta * Math.PI / 180)));
				drawLine(tmp, xc, yc, 0, yc - y2, first);
				/*x2 = ((int)(xc * Math.cos(theta * Math.PI / 180)));
				y2 = ((int)(yc * Math.sin(theta * Math.PI / 180)));
				drawLine(tmp, xc, yc, xc + x2, yc + y2, first);*/
			}
			else if(theta == 270) {
				drawLine(tmp, xc, yc, xc, 0, first);
			}
			else if(theta > 225 && theta <= 315) {
				x2 = ((int)(yc / Math.tan(theta * Math.PI / 180)));
				drawLine(tmp, xc, yc, xc - x2, 0, first);
				/*x2 = ((int)(xc * Math.cos(theta * Math.PI / 180)));
				y2 = ((int)(yc * Math.sin(theta * Math.PI / 180)));
				drawLine(tmp, xc, yc, xc + x2, yc + y2, first);*/
			}
			theta += 360.0f/(float)n;				// Next degree = current degree + 360/n
			theta = theta % 360;					// Convert degree to within 360
			if(first){								// If a red line is drawn, then rest of the lines are black
				first = false;
			}
		}
	}
	
	/*****************************
	 * Function white
	 * Input: image
	 * Output: none
	 ****************************/
	
	public static void white(BufferedImage i){
		for(int y = 0 ; y < i.getHeight() ; y++){	// Set all pixels to white
			for(int x = 0 ; x < i.getWidth() ; x++){
				byte r = (byte) 255;
				byte g = (byte) 255;
				byte b = (byte) 255;
				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
				i.setRGB(x,y,pix);
			}
		}
	}
	
	public static void main(String[] args) {
		Mypart2 ren = new Mypart2();
		ren.showImg(args);
	}
	
	@Override
    public void actionPerformed(ActionEvent e){
		if(e.getSource() == left){
	        counter = (counter + (360 * s / orifps)) % 360;	// Caculate next degree 
	        white(img);										// White the image 
			drawImg(n, counter, img);						// Draw lines
			frame.repaint();								// Repaint frame
		}
		if(e.getSource() == right){
	        counter2 = (counter2 + (360 * s / fps)) % 360;	// Caculate next degree 
	        white(img2);									// White the image 
			drawImg(n, counter2, img2);						// Draw lines
			frame.repaint();								// Repaint frame
		}
    }

}
