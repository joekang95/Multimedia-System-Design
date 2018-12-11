/*****************************************
 * Class Quartets
 * Used for mode 2: 2 by 2 pixels
 * 8800444224 Joe Chang Oct. 10, 2018
 *****************************************/

public class Quartets {
	int id;							// Cluster ID	
    int[][] pix = new int[4][3];	// Centroids of cluster and their rgbs
    int[][] total = new int[4][3];	// Total rgb of centroids
    int amount;						// Total number of pixels in cluster
    
    public Quartets(int id, int pixel, int pixel2, int pixel3, int pixel4) {
    	this.id = id;
    	int[] p = {pixel, pixel2, pixel3, pixel4};	// Initialization pixels 
    	int[] r = {16, 8, 0};						// Shift values
    	
    	// Store rgb of pixels
    	for(int i = 0 ; i < 3 ; i++) {
    		pix[0][i] = p[0] >> r[i] & 0x000000ff;
			pix[1][i] = p[1] >> r[i] & 0x000000ff;
			pix[2][i] = p[2] >> r[i] & 0x000000ff;
			pix[3][i] = p[3] >> r[i] & 0x000000ff;
    	}
    	
    	// Add pixels to cluster
        addPixel(pixel, pixel2, pixel3, pixel4);
    }

    int getId() {
    	// Return cluster id
        return id;
    }

    int getPixel(int i) {
    	// Obtain i-th centroid pixel
        int r = pix[i][0];
        int g = pix[i][1];
        int b = pix[i][2];
        return 0xff000000 | r << 16 | g << 8 | b;
    }
    
    void addPixel(int pixel, int pixel2, int pixel3, int pixel4) {
    	int[] p = {pixel, pixel2, pixel3, pixel4};		// Pixels
    	int[] r = {16, 8, 0};							// Shift values

    	// Add pixel's rgb to rgb of total
    	for(int i = 0 ; i < 3 ; i++) {
    		total[0][i] += p[0] >> r[i] & 0x000000ff;
    		total[1][i] += p[1] >> r[i] & 0x000000ff;
        	total[2][i] += p[2] >> r[i] & 0x000000ff;
        	total[3][i] += p[3] >> r[i] & 0x000000ff;
    	}
    	
    	// Increase number of pixels in cluster
        amount++;
        
        // Update centroid pixels
        for(int i = 0 ; i < 3 ; i++) {
        	pix[0][i] = total[0][i] / amount;
        	pix[1][i] = total[1][i] / amount;
        	pix[2][i] = total[2][i] / amount;
        	pix[3][i] = total[3][i] / amount;
        }
    }
    
    void removePixel(int pixel, int pixel2, int pixel3, int pixel4) {
    	int[] p = {pixel, pixel2, pixel3, pixel4};		// Pixels
    	int[] r = {16, 8, 0};							// Shift values
    	
    	// Subtract pixel's rgb from rgb of total
    	for(int i = 0 ; i < 3 ; i++) {
    		total[0][i] -= p[0] >> r[i] & 0x000000ff;
    		total[1][i] -= p[1] >> r[i] & 0x000000ff;
        	total[2][i] -= p[2] >> r[i] & 0x000000ff;
        	total[3][i] -= p[3] >> r[i] & 0x000000ff;
    	}
    	
    	// Decrease number of pixels in cluster
        amount--;
        
        // Update centroid pixels
        for(int i = 0 ; i < 3 ; i++) {
        	pix[0][i] = total[0][i] / amount;
        	pix[1][i] = total[1][i] / amount;
        	pix[2][i] = total[2][i] / amount;
        	pix[3][i] = total[3][i] / amount;
        }
    }
    
    int distance(int pixel, int pixel2, int pixel3, int pixel4) {
    	int[] sum = new int[4];		// Distance of pixels
    	int[] p = {pixel >> 16 & 0x000000ff, pixel >> 8 & 0x000000ff, pixel & 0x000000ff};
    	int[] p2 = {pixel2 >> 16 & 0x000000ff, pixel2 >> 8 & 0x000000ff, pixel2 & 0x000000ff};
    	int[] p3 = {pixel3 >> 16 & 0x000000ff, pixel3 >> 8 & 0x000000ff, pixel3 & 0x000000ff};
    	int[] p4 = {pixel4 >> 16 & 0x000000ff, pixel4 >> 8 & 0x000000ff, pixel4 & 0x000000ff};
    	
    	// Calculate euclidean distance for rgb
    	for(int i = 0 ; i < 3 ; i++) {
    		sum[0] += Math.pow(Math.abs(pix[0][i] - p[i]), 2);
    		sum[1] += Math.pow(Math.abs(pix[1][i] - p2[i]), 2);
    		sum[2] += Math.pow(Math.abs(pix[2][i] - p3[i]), 2);
    		sum[3] += Math.pow(Math.abs(pix[3][i] - p4[i]), 2);
    	}
    	
    	// Return distance
    	return (int)(Math.sqrt(sum[0]) + Math.sqrt(sum[1]) + Math.sqrt(sum[2]) + Math.sqrt(sum[3]));
    }
}
