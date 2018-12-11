/*****************************************
 * Class MyCopression
 * Input parameters: FileName, N, Mode
 * 8800444224 Joe Chang Oct. 14, 2018
 *****************************************/

import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import java.util.*;

public class MyCompression {
	static int IMAGE_WIDTH = 352;		// Image width
    static int IMAGE_HEIGHT = 288;		// Image height
    static int N, counter, extra;		// N clusters, counter, extra offset for rgb or grayscale
    static BufferedImage imgOriginal = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
    static BufferedImage img = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
    static String fileName;				// File name
    static int mode;					// Mode
    static byte[] bytes;				// Bytes of input image
    static Pairs[] clustersP;			// Clusters of mode 1
    static Quartets[] clustersQ;		// Clusters of mode 2
    static Hexadecet[] clustersH;		// Clusters of mode 3
    static boolean fail = false;		// Fail to read image
    
	public static void main(String[] args){
	    	
			fileName = args[0];					// Obtain file name
			N = Integer.parseInt(args[1]);		// Obtain N
			mode = Integer.parseInt(args[2]);	// Obtain mode
			
			if(mode > 3 || mode < 1) {
				System.err.println("Mode Error");
				return ;
			}
			
			// Read image
			readImg();		
			if(fail) {
				return ;
			}
			// Calculate vector spaces
		    int[] vectorSpace = calculateVectorSpace(imgOriginal);
	    	System.out.println("Printing Image...");
		    
		    // Quantization
		    if(mode == 1) {
			    for (int y = 0; y < IMAGE_HEIGHT ; y++) {
			        for (int x = 0; x < IMAGE_WIDTH ; x += 2) {
			            int clusterId = vectorSpace[IMAGE_WIDTH * y + x];
			            img.setRGB(x, y, clustersP[clusterId].getPixel());
			            img.setRGB(x + 1, y, clustersP[clusterId].getPixel2());
			        }
			    }
		    }
		    else if(mode == 2) {
			    for (int y = 0; y < IMAGE_HEIGHT ; y += 2) {
			        for (int x = 0; x < IMAGE_WIDTH ; x += 2) {
			            int clusterId = vectorSpace[IMAGE_WIDTH * y + x];
			            img.setRGB(x, y, clustersQ[clusterId].getPixel(0));
			            img.setRGB(x + 1, y, clustersQ[clusterId].getPixel(1));
			            img.setRGB(x, y + 1, clustersQ[clusterId].getPixel(2));
			            img.setRGB(x + 1, y + 1, clustersQ[clusterId].getPixel(3));
			        }
			    }
		    }
		    else if(mode == 3) {
			    for (int y = 0; y < IMAGE_HEIGHT ; y += 4) {
			        for (int x = 0; x < IMAGE_WIDTH ; x += 4) {
			            int clusterId = vectorSpace[IMAGE_WIDTH * y + x];
			            img.setRGB(x, y, clustersH[clusterId].getPixel(0));
			            img.setRGB(x + 1, y, clustersH[clusterId].getPixel(1));
			            img.setRGB(x + 2, y, clustersH[clusterId].getPixel(2));
			            img.setRGB(x + 3, y, clustersH[clusterId].getPixel(3));
			            img.setRGB(x, y + 1, clustersH[clusterId].getPixel(4));
			            img.setRGB(x + 1, y + 1, clustersH[clusterId].getPixel(5));
			            img.setRGB(x + 2, y + 1, clustersH[clusterId].getPixel(6));
			            img.setRGB(x + 3, y + 1, clustersH[clusterId].getPixel(7));
			            img.setRGB(x, y + 2, clustersH[clusterId].getPixel(8));
			            img.setRGB(x + 1, y + 2, clustersH[clusterId].getPixel(9));
			            img.setRGB(x + 2, y + 2, clustersH[clusterId].getPixel(10));
			            img.setRGB(x + 3, y + 2, clustersH[clusterId].getPixel(11));
			            img.setRGB(x, y + 3, clustersH[clusterId].getPixel(12));
			            img.setRGB(x + 1, y + 3, clustersH[clusterId].getPixel(13));
			            img.setRGB(x + 2, y + 3, clustersH[clusterId].getPixel(14));
			            img.setRGB(x + 3, y + 3, clustersH[clusterId].getPixel(15));
			        }
			    }
		    }

			// Print images
		    JFrame frame = new JFrame();
			JPanel  panel = new JPanel();
			panel.add (new JLabel (new ImageIcon (imgOriginal)));
			panel.add (new JLabel (new ImageIcon (img)));
			      
			frame.getContentPane().add (panel);
			frame.pack();
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		   
	}
	
	public static int[] calculateVectorSpace(BufferedImage image) {
    	System.out.println("Calculating VectorSpace...");
		
		int[] vectorSpace = new int[IMAGE_WIDTH * IMAGE_HEIGHT];
		int[] pre = new int[IMAGE_WIDTH * IMAGE_HEIGHT];
		Arrays.fill(vectorSpace, -1);	// Initialize vector spaces
		Arrays.fill(pre, -1);	// Initialize vector spaces
		boolean loop = true;			// Switch for looping

		if(mode == 1) {
			clustersP = createClustersP(image);		// Create mode 1 clusters
			int counter = 0;						// Counter for counting loop
	    	System.out.println("Obtaining Minimum Distance Cluster...");
			while(loop) {
				loop = false;
		       	for(int y = 0 ; y < IMAGE_HEIGHT ; y++) {
					for (int x = 0 ; x < IMAGE_WIDTH ; x += 2) {
						int pixel = image.getRGB(x, y);			// Obtain left pixel
						int pixel2 = image.getRGB(x + 1, y);	// Obtain right pixel
						
						// Find the closest cluster for pair(pixel, pixel2)
						Pairs cluster = getMinClusterP(pixel, pixel2);	

	                	pre[IMAGE_WIDTH * y + x] = vectorSpace[IMAGE_WIDTH * y + x];
		                   
						// If pair is not in closet cluster
		                if (vectorSpace[IMAGE_WIDTH * y + x] != cluster.getId()) {
		                	// If is stored in vector space, remove pair
		                	if (vectorSpace[IMAGE_WIDTH * y + x] != -1) {
		                		clustersP[vectorSpace[IMAGE_WIDTH  *y + x]].removePixel(pixel, pixel2);
		                	}
		                	// Add pair to closet cluster
		                	cluster.addPixel(pixel, pixel2);
		                	// Since moved, can still update codebook
		                	loop = true;
		                	// Save cluster ID
		                	vectorSpace[IMAGE_WIDTH * y + x] = cluster.getId();
		                }
					}
				}
				counter++;	// Increase counter
				int diff = 0;
				for(int i = 0 ; i < pre.length ; i++) {
					if(pre[i] != vectorSpace[i]) {
						diff++;
					}
				}
				if(diff < 2 || counter >= 215) {
					loop = false;
				}
		    	//System.out.println("Loop" + ": " + counter);
			}
		}
		else if(mode == 2) {
			clustersQ = createClustersQ(image);		// Create mode 2 clusters
			int counter = 0;						// Counter for counting loop
	    	System.out.println("Obtaining Minimum Distance Cluster...");			
			while(loop) {
				loop = false;
				for(int y = 0 ; y < IMAGE_HEIGHT ; y += 2) {
					for (int x = 0 ; x < IMAGE_WIDTH ; x += 2) {
						int pixel = image.getRGB(x, y);			// Obtain first pixel
						int pixel2 = image.getRGB(x + 1, y);	// Obtain second pixel
						int pixel3 = image.getRGB(x, y + 1);	// Obtain third pixel
						int pixel4 = image.getRGB(x + 1, y + 1);// Obtain fourth pixel
						
						// Find the closest cluster for quartet(pixel, pixel2, pixel3, pixel4)
						Quartets cluster = getMinClusterQ(pixel, pixel2, pixel3, pixel4);

	                	pre[IMAGE_WIDTH * y + x] = vectorSpace[IMAGE_WIDTH * y + x];
						// If quartet is not in closet cluster		                   
		                if (vectorSpace[IMAGE_WIDTH * y + x] != cluster.getId()) {
		                	// If is stored in vector space, remove quartet
		                	if (vectorSpace[IMAGE_WIDTH * y + x] != -1) {
		                		clustersQ[vectorSpace[IMAGE_WIDTH  *y + x]].removePixel(pixel, pixel2, pixel3, pixel4);
		                	}
		                	// Add quartet to closet cluster
		                	cluster.addPixel(pixel, pixel2, pixel3, pixel4);
		                	// Since moved, can still update codebook
		                	loop = true;
		                	// Save cluster ID
		                	vectorSpace[IMAGE_WIDTH * y + x] = cluster.getId();
		                }
					}
				}
				counter++;	// Increase counter
				int diff = 0;
				for(int i = 0 ; i < pre.length ; i++) {
					if(pre[i] != vectorSpace[i]) {
						diff++;
					}
				}
				if(diff < 2 || counter >= 215) {
					loop = false;
				}
		    	//System.out.println("Loop" + ": " + counter);
			}
			
		}
		else if(mode == 3) {
			clustersH = createClustersH(image);		// Create mode 3 clusters
			int counter = 0;						// Counter for counting loop
	    	System.out.println("Obtaining Minimum Distance Cluster...");
			while(loop) {
				loop = false;
				for(int y = 0 ; y < IMAGE_HEIGHT ; y += 4) {
					for (int x = 0 ; x < IMAGE_WIDTH ; x += 4) {
						// Obtain 16 pixels
						int[] pixel = {image.getRGB(x, y), image.getRGB(x + 1, y), image.getRGB(x + 2, y), image.getRGB(x + 3, y)
								, image.getRGB(x, y + 1), image.getRGB(x + 1, y + 1), image.getRGB(x + 2, y + 1), image.getRGB(x + 3, y + 1)
								, image.getRGB(x, y + 2), image.getRGB(x + 1, y + 2), image.getRGB(x + 2, y + 2), image.getRGB(x + 3, y + 2)
								, image.getRGB(x, y + 3), image.getRGB(x + 1, y + 3), image.getRGB(x + 2, y + 3), image.getRGB(x + 3, y + 3)};
						
						// Find the closest cluster for hexadecet(pixel ... pixel15)
						Hexadecet cluster = getMinClusterH(pixel);

	                	pre[IMAGE_WIDTH * y + x] = vectorSpace[IMAGE_WIDTH * y + x];
						// If hexadecet is not in closet cluster		                   		                   
		                if (vectorSpace[IMAGE_WIDTH * y + x] != cluster.getId()) {
		                	// If is stored in vector space, remove hexadecet
		                	if (vectorSpace[IMAGE_WIDTH * y + x] != -1) {
		                		clustersH[vectorSpace[IMAGE_WIDTH  *y + x]].removePixel(pixel);
		                	}
		                	// Add hexadecet to closet cluster
		                	cluster.addPixel(pixel);
		                	// Since moved, can still update codebook
		                	loop = true;
		                	// Save cluster ID
		                	vectorSpace[IMAGE_WIDTH * y + x] = cluster.getId();
		                }
					}
				}
				counter++;	// Increase counter
				int diff = 0;
				for(int i = 0 ; i < pre.length ; i++) {
					if(pre[i] != vectorSpace[i]) {
						diff++;
					}
				}
				if(diff < 2 || counter >= 215) {
					loop = false;
				}
		    	//System.out.println("Loop" + ": " + diff);
			}
			
		}
		return vectorSpace;
	}
	
	public static Pairs[] createClustersP(BufferedImage image) {
    	System.out.println("Creating Clusters...");
		Pairs[] result = new Pairs[N];	// Create mode 1 clusters
		int x = 0; 
		int y = 0;
		int dx = IMAGE_WIDTH / N;
		int dy = IMAGE_HEIGHT / N;
	     
		for(int i=0 ; i < N ; i++) {
			result[i] = new Pairs(i, image.getRGB(x, y), image.getRGB(x + 1, y));
	    	//System.out.println((int)(image.getRGB(x, y) >> 16 & 0x000000ff) + " " + (int)(image.getRGB(x, y) >> 8 & 0x000000ff)+ " " + (int)(image.getRGB(x, y) & 0x000000ff));
	    	//System.out.println((int)(result[i].getPixel() >> 16 & 0x000000ff) + " " + (int)(result[i].getPixel() >> 8 & 0x000000ff)+ " " + (int)(result[i].getPixel() & 0x000000ff));
			x += dx; 
			y += dy;
		}
		return result;
	}
	
	public static Quartets[] createClustersQ(BufferedImage image) {
    	System.out.println("Creating Clusters...");
		Quartets[] result = new Quartets[N];	// Create mode 2 clusters
		int x = 0; 
		int y = 0;
		int dx = IMAGE_WIDTH / N;
		int dy = IMAGE_HEIGHT / N;
	     
		for(int i=0 ; i < N ; i++) {
			result[i] = new Quartets(i, image.getRGB(x, y), image.getRGB(x + 1, y)
					, image.getRGB(x, y + 1), image.getRGB(x + 1, y + 1));
	    	//System.out.println((int)(image.getRGB(x, y) >> 16 & 0x000000ff) + " " + (int)(image.getRGB(x, y) >> 8 & 0x000000ff)+ " " + (int)(image.getRGB(x, y) & 0x000000ff));
	    	//System.out.println((int)(result[i].getPixel(0) >> 16 & 0x000000ff) + " " + (int)(result[i].getPixel(0) >> 8 & 0x000000ff)+ " " + (int)(result[i].getPixel(0) & 0x000000ff));
			x += dx; 
			y += dy;
		}
		return result;
	}
	
	public static Hexadecet[] createClustersH(BufferedImage image) {
    	System.out.println("Creating Clusters...");
		Hexadecet[] result = new Hexadecet[N];	// Create mode 3 clusters
		int x = 0; 
		int y = 0;
		int dx = IMAGE_WIDTH / N;
		int dy = IMAGE_HEIGHT / N;
		for(int i=0 ; i < N ; i++) {
			result[i] = new Hexadecet(i, image.getRGB(x, y), image.getRGB(x + 1, y), image.getRGB(x + 2, y), image.getRGB(x + 3, y)
					, image.getRGB(x, y + 1), image.getRGB(x + 1, y + 1), image.getRGB(x + 2, y + 1), image.getRGB(x + 3, y + 1)
					, image.getRGB(x, y + 2), image.getRGB(x + 1, y + 2), image.getRGB(x + 2, y + 2), image.getRGB(x + 3, y + 2)
					, image.getRGB(x, y + 3), image.getRGB(x + 1, y + 3), image.getRGB(x + 2, y + 3), image.getRGB(x + 3, y + 3));
			x += dx; 
			y += dy;
		}
		return result;
	}

	public static Pairs getMinClusterP(int pixel, int pixel2) {
		Pairs cluster = null;
	    int min = Integer.MAX_VALUE;
	    for(int i = 0 ; i < clustersP.length ; i++) {
	    	int distance = clustersP[i].distance(pixel, pixel2);
	    	if (distance < min) {
	    		min = distance;
	    		cluster = clustersP[i];
	    	}
	    }
	    return cluster;
	}

	public static Quartets getMinClusterQ(int pixel, int pixel2, int pixel3, int pixel4) {
		Quartets cluster = null;
	    int min = Integer.MAX_VALUE;
	    for(int i = 0 ; i < clustersQ.length ; i++) {
	    	int distance = clustersQ[i].distance(pixel, pixel2, pixel3, pixel4);
	    	if (distance < min) {
	    		min = distance;
	    		cluster = clustersQ[i];
	    	}
	    }
	    return cluster;
	}

	public static Hexadecet getMinClusterH(int[] pixel) {
		Hexadecet cluster = null;
	    int min = Integer.MAX_VALUE;
	    for(int i = 0 ; i < clustersH.length ; i++) {
	    	int distance = clustersH[i].distance(pixel);
	    	if (distance < min) {
	    		min = distance;
	    		cluster = clustersH[i];
	    	}
	    }
	    return cluster;
	}
	
	public static void readImg() {
		System.out.println("Reading Image...");
		try{
			File file = new File(fileName);
			InputStream is = new FileInputStream(file);
			String[] parts = fileName.split("\\.");
		
			long len = file.length();
			bytes = new byte[(int)len];
			    
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0){
				offset += numRead;
			}
		        
			counter = 0;
			extra = 0;
			if(parts[parts.length - 1].equals("rgb")){
				extra = IMAGE_HEIGHT * IMAGE_WIDTH;
			}
			else if(parts[parts.length - 1].equals("raw")){
				extra = 0;
			}
			else {
				System.err.println("Input File Format Error");
			}
		    	
			for(int y = 0 ; y < IMAGE_HEIGHT ; y++){
				for(int x = 0 ; x < IMAGE_WIDTH ; x++){
						
					byte r = bytes[counter];
					byte g = bytes[counter + extra];
					byte b = bytes[counter + extra * 2];

					int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
					imgOriginal.setRGB(x, y, pix);
					counter++;
				}
			}
			is.close();
				
		}catch(FileNotFoundException e) {
			System.err.print(e);
			fail = true;
		}catch(IOException e) {
			System.err.print(e);
			fail = true;
		}
	}
}

