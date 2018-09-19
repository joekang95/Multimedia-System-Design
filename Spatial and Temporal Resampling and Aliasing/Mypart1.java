/********************
 * Name: Joe Chang
 * USCID: 8800444224
 *******************/

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class Mypart1 {
	JFrame frame;
	JLabel lbIm1;
	JLabel lbIm2;
	BufferedImage img;
	BufferedImage img2;
	static int width = 512;
	static int height = 512;
	static int xc = width/2;
	static int yc = height/2;

	// Draws a black line on the given buffered image from the pixel defined by (x1, y1) to (x2, y2)
	public void drawLine(BufferedImage image, int x1, int y1, int x2, int y2) {
		Graphics2D g = image.createGraphics();
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(1));
		g.drawLine(x1, y1, x2, y2);
		g.drawImage(image, 0, 0, null);
	}

	public void showIms(String[] args){
        // args[0] -> Number of lines
        // args[1] -> Scaling factor
        // args[2] -> Boolean value for aliasing
		
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

		// Initialize a plain white image
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				
				byte r = (byte) 255;
				byte g = (byte) 255;
				byte b = (byte) 255;
				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
				img.setRGB(x,y,pix);
			}
		}

		// Draw outer square frame
		drawLine(img, 0, 0, width-1, 0);				// top edge
		drawLine(img, 0, 0, 0, height-1);				// left edge
		drawLine(img, 0, height-1, width-1, height-1);	// bottom edge
		drawLine(img, width-1, height-1, width-1, 0); 	// right edge
		
		/*************************
		 * Part 1 
		 * parameter: n
		 *************************/

		int n = Integer.parseInt(param0);				// First parameter n : draw lines separated by 360/n degrees 
		float theta = 0;								// Theta = degree
		int x2 = 0, y2 = 0;								// Initialize x2 and y2
		
		for(int i = 0 ; i < n ; i++){					// Draw lines 
			if(theta == 0) {
				drawLine(img, xc, yc, width - 1, yc);
			}
			else if(theta <= 45 || theta > 315) {
				y2 = ((int)(xc * Math.tan(theta * Math.PI / 180)));		// y amount = xlength * radian = xlength * tan(degree * PI/180)
				drawLine(img, xc, yc, width - 1, yc + y2);
			}
			else if(theta == 90) {
				drawLine(img, xc, yc, xc, height - 1);
			}
			else if(theta > 45 && theta <= 135) {
				x2 = ((int)(yc / Math.tan(theta * Math.PI / 180)));
				drawLine(img, xc, yc, xc + x2, height - 1);
			}
			else if(theta == 180) {
				drawLine(img, xc, yc, 0, yc);
			}
			else if(theta > 135 && theta <= 225) {
				y2 = ((int)(xc * Math.tan(theta * Math.PI / 180)));
				drawLine(img, xc, yc, 1, yc - y2);
			}
			else if(theta == 270) {
				drawLine(img, xc, yc, xc, 0);
			}
			else if(theta > 225 && theta <= 315) {
				x2 = ((int)(yc / Math.tan(theta * Math.PI / 180)));
				drawLine(img, xc, yc, xc - x2, 1);
			}
			theta += 360.0f/(float)n;					// Next degree = current degree + 360/n
		}

		/*************************
		 * Part 2 
		 * parameter: s, alias
		 *************************/
		
		float s = Float.parseFloat(param1);				// Second parameter s : scale image down by s 
		int alias = Integer.parseInt(param2);			// Third parameter alias : decide whether or not to anti-alaising
	
		if(alias != 1 && alias != 0) {
			System.err.println("Boolean Value Error");
			return;
		}
		
		if(s >= 1.0){
        	img2 = resize(img, s, alias);
        }
		else if(s == 0.0){
			img2 = img;
		}
		else {
			System.err.println("Cannot Resize");
			return;
		}
		
		/*************************
		 * Part 3 
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
		
	}
	
	/*****************************
	 * Function reszie
	 * Input: Image, Scale, Alias
	 * Output: Resized Image
	 ****************************/
	public static BufferedImage resize(BufferedImage img, float s, int alias) {
		
		int newWidth = (int) (width / s);				// New width = original width / scale
		int newHeight = (int) (height / s);				// New height = original height / scale
		
		BufferedImage newImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
		
		if(alias == 1){
			img = antiAlias(img, width, height);		// Deal with aliasing first by going through low-pass filter
		}
		
		for(int y = 0 ; y < newHeight ; y++){			// Whiten the background of new image
			for (int x = 0 ; x < newWidth ; x++){
				byte r = (byte) 255;
				byte g = (byte) 255;
				byte b = (byte) 255;
				int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
				newImg.setRGB(x, y, pix);
			}
		}
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
		
		for(int y = 0 ; y < height ; y++){				// For all pixels
			for(int x = 0 ; x < width ; x++){
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
						g += (weight[counter] *((colorTmp >> 8) & 0xff));
						b += (weight[counter] *((colorTmp) & 0xff));
						counter++;						// Next pixel's coordinate
					}
				}
				int color = 0xff000000 | (((r / ave) & 0xff) << 16) | (((g / ave) & 0xff) << 8) | ((b / ave) & 0xff);
				newImg.setRGB(x, y, color);
			}
		}
		return newImg;
	}
	
	public static void main(String[] args) {
		Mypart1 ren = new Mypart1();
		ren.showIms(args);
	}
}
