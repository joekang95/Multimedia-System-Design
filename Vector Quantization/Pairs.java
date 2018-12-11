/*****************************************
 * Class Pairs
 * Used for mode 1: side by side pixels
 * 8800444224 Joe Chang Oct. 10, 2018
 *****************************************/


public class Pairs {
	int id;						// Cluster ID	
    int[] pix = new int[3];		// Left centroid of cluster and its rgb
    int[] pix2 = new int[3];	// Right centroid of cluster and its rgb
    int[] totalX = new int[3];	// Total rgb of first pixel
    int[] totalY = new int[3];	// Total rgb of second pixel
    int amount;					// Total number of pixels in cluster
    
    public Pairs(int id, int pixel, int pixel2) {
    	this.id = id;
    	
    	// Store rgb of first pixel
    	pix[0] = pixel >> 16 & 0x000000ff;
    	pix[1] = pixel >> 8 & 0x000000ff;
    	pix[2] = pixel & 0x000000ff;
    	
    	// Store rgb of second pixel
    	pix2[0] = pixel2 >> 16 & 0x000000ff;
    	pix2[1] = pixel2 >> 8 & 0x000000ff;
    	pix2[2] = pixel2 & 0x000000ff;
    	
    	// Add pixel to cluster
        addPixel(pixel, pixel2);
    }

    int getId() {
    	// Return cluster id
        return id;
    }

    int getPixel() {
    	// Obtain left centroid pixel
        int r = pix[0];
        int g = pix[1];
        int b = pix[2];
        return 0xff000000 | r << 16 | g << 8 | b;
    }
    
    int getPixel2() {
       	// Obtain right centroid pixel
        int r = pix2[0];
        int g = pix2[1];
        int b = pix2[2];
        return 0xff000000 | r << 16 | g << 8 | b;
    }
    
    void addPixel(int pixel, int pixel2) {
    	// Add first pixel's rgb to rgb of first total
    	totalX[0] += pixel >> 16 & 0x000000ff;
    	totalX[1] += pixel >> 8 & 0x000000ff;
    	totalX[2] += pixel & 0x000000ff;
    	
    	// Add scecond pixel's rgb to rgb of scecond total
    	totalY[0] += pixel2 >> 16 & 0x000000ff;
    	totalY[1] += pixel2 >> 8 & 0x000000ff;
    	totalY[2] += pixel2 & 0x000000ff;
    	
    	// Increase number of pixels in cluster
        amount++;
        
        // Update centroid pixels
        for(int i = 0 ; i < 3 ; i++) {
        	pix[i] = totalX[i] / amount;
        	pix2[i] = totalY[i] / amount;
        }
        
    }
    
    void removePixel(int pixel, int pixel2) {
    	// Subtract first pixel's rgb to rgb of first total
    	totalX[0] -= pixel >> 16 & 0x000000ff;
    	totalX[1] -= pixel >> 8 & 0x000000ff;
    	totalX[2] -= pixel & 0x000000ff;
    	
    	// Subtract second pixel's rgb from rgb of second total
    	totalY[0] -= pixel2 >> 16 & 0x000000ff;
    	totalY[1] -= pixel2 >> 8 & 0x000000ff;
    	totalY[2] -= pixel2 & 0x000000ff;
    	
    	// Decrease number of pixels in cluster
        amount--;
        
        // Update centroid pixels
        for(int i = 0 ; i < 3 ; i++) {
        	pix[i] = totalX[i] / amount;
        	pix2[i] = totalY[i] / amount;
        }
    }
    
    int distance(int pixel, int pixel2) {
    	int[] sum = new int[2];		// Distance of both pixels
    	int[] p = {pixel >> 16 & 0x000000ff, pixel >> 8 & 0x000000ff, pixel & 0x000000ff};
    	int[] p2 = {pixel2 >> 16 & 0x000000ff, pixel2 >> 8 & 0x000000ff, pixel2 & 0x000000ff};
    	
    	// Calculate euclidean distance for rgb
    	for(int i = 0 ; i < 3 ; i++) {
    		sum[0] += Math.pow(Math.abs(pix[i] - p[i]), 2);
    		sum[1] += Math.pow(Math.abs(pix2[i] - p2[i]), 2);
    	}
    	
    	// Return distance
    	return (int)(Math.sqrt(sum[0]) + Math.sqrt(sum[1]));
    }
}
