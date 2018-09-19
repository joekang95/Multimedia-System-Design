/********************
 * Name: Joe Chang
 * USCID: 8800444224
 *******************/

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.*;

public class MyExtraCredit implements ActionListener{

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
    static float speed;
    static float fps;
    static float orifps;
    static float newfps;
    static float scale;
    static int alias;
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
        //args[3] -> Scale
        //args[4] -> Anti-aliasing
		
		// Read a parameter from command line

		String param0, param1, param2, param3, param4;
		try{
			param0 = args[0];
			param1 = args[1];
			param2 = args[2];
			param3 = args[3];
			param4 = args[4];
		}catch(Exception e){
			e.printStackTrace();
			System.err.println("Input Format Error");
			return ;
		}

		// Initialize a plain white image
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		img2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		/********************************
		 * Part 1 
		 * parameter: 
		 * n, speed (Speed of Rotation), 
		 * fps(Right fps), scale, alias
		 *******************************/

		n = Integer.parseInt(param0);				// First parameter n : draw lines separated by 360/n degrees 
		speed = Float.parseFloat(param1);			// Second parameter speed : speed of rotation = rotation / second 
		fps = Float.parseFloat(param2);				// Third parameter fps : sample frame / second
		scale = Float.parseFloat(param3);			// Fourth parameter scale : scale image down by s
		alias = Integer.parseInt(param4);			// Fifth parameter alias : decide whether or not to anti-alaising
		orifps = speed * 36;						// Original video's fps
		
		if(scale < 1) {
			System.err.println("Scale Value Error");
			return ;
		}
		if(alias != 1 && alias != 0) {
			System.err.println("Alias Value Error");
			return ;
		}
		
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
	
	public static BufferedImage resize(BufferedImage img, int width, int height, float s, int alias){
		
		int newWidth = (int) (width / s);				// New width = original width / scale
		int newHeight = (int) (height / s);				// New height = original height / scale
		
		BufferedImage newImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
		
		if(alias == 1){
			img = antiAlias(img, width, height);		// Deal with aliasing first by going through low-pass filter
		}

		white(newImg);									// Whiten the background of new image
		
		for(int y = 0 ; y < newHeight ; y++){			// Sample pixels from original image
			for(int x = 0 ; x < newWidth ; x++){
				int color = img.getRGB((int)(x * s), (int)(y * s));
				newImg.setRGB(x, y, color);
			}
		}
		return newImg;
    }

	/*****************************
	 * Function antiAlias
	 * Input: Image, Height, Width
	 * Output: New Image
	 ****************************/
	
	public static BufferedImage antiAlias(BufferedImage img, int width, int height) {
		
		BufferedImage newImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		/********************************************
		 * Graph of coordinates of surrounding pixels
		 * (x-1, y-1)|(x, y-1)|(x+1, y-1)
		 * -------------------------------  
		 *  (x-1, y) | (x, y) | (x+1, y)
		 * -------------------------------
		 * (x-1, y+1)|(x, y+1)|(x+1, y+1)
		 *******************************************/
		
		int[] horizontal = {-1, 0, 1};    				// X coordinates
		int[] vertical = {1, 0, 1};						// Y coordinates
		int[] weight = {1, 2, 1, 2, 4, 2, 1, 2, 1}; 	// Filter values (3*3 weighted low-pass filter)
		int counter = 0;								// Counter used to traverse through X and Y coordinates
		int ave = 16;									// Total of filter values
		white(newImg);
		
		for (int y = 0; y < height ; y++){				// For all pixels
			for (int x = 0 ; x < width; x++){
				int r = 0;
				int g = 0;
				int b = 0;
				counter = 0;
				for(int v : vertical){
					for (int h : horizontal){
						int colorTmp;
						if(h + x < 0 || v + y < 0 || h + x >= width || v + y >= height) {
							continue;					// Ignore out-of-range surrounding pixels
						}
						colorTmp = img.getRGB(h + x, v + y);
						r += (weight[counter] * ((colorTmp >> 16) & 0xff));
						g += (weight[counter] * ((colorTmp >> 8) & 0xff));
						b += (weight[counter] * ((colorTmp) & 0xff));
						counter++;
					}
				}
				int color = 0xff000000 | (((r / ave) & 0xff) << 16) | (((g / ave) & 0xff) << 8) | ((b / ave) & 0xff);
				newImg.setRGB(x, y, color);
			}
		}
		return newImg;
	}
	
	public static void main(String[] args) {
		MyExtraCredit ren = new MyExtraCredit();
		ren.showImg(args);
	}
	
	@Override
    public void actionPerformed(ActionEvent e){
		if(e.getSource() == left){
	        counter = (counter + (360 * speed / orifps)) % 360;	// Caculate next degree 
	        white(img);											// White the image 
			drawImg(n, counter, img);							// Draw lines
			//drawCircle(img, xc, yc, height);					// Draw wheel's outer frame
			frame.repaint();									// Repaint frame
		}
		if(e.getSource() == right){
			float rps = speed / fps;							// Rotations per second
			/*if(scale == 1 && alias == 0) {						// If scale = 1, must preform anti-aliasing
				alias = 1;
			}*/
			if(alias == 1 && (fps <= 2.0 * speed)){
	            rps = 1 - rps;									// Anti-temporal aliasing
	        }
	        counter2 = (counter2 + (360 * rps)) % 360;			// Caculate next degree 
	        white(img2);										// White the image 
			drawImg(n, counter2, img2);							// Draw lines
	        if(scale >= 1.0){
				tmp = new BufferedImage(width/(int)scale, height/(int)scale, BufferedImage.TYPE_INT_RGB);	// Temporary image to store resized image2
				tmp = resize(img2, width, height, scale, alias);					// Reize image2
				//drawCircle(tmp, xc/(int)scale, yc/(int)scale, height/(int)scale);	// Draw wheel's outer frame
	        }
	        lbIm2.setIcon(new ImageIcon(tmp));					// Set icon as tmp
			frame.repaint();									// Repaint frame
		}
    }

}
