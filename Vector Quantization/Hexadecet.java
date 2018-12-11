/*****************************************
 * Class Hexadecet
 * Used for mode 3: 4 by 4 pixels
 * 8800444224 Joe Chang Oct. 10, 2018
 *****************************************/

public class Hexadecet {
	int id;							// Cluster ID	
    int[][] pix = new int[16][3];	// Centroids of cluster and their rgbs
    int[][] total = new int[16][3];	// Total rgb of centroids
    int amount;						// Total number of pixels in cluster
    
    public Hexadecet(int id, int pixel, int pixel2, int pixel3, int pixel4
    		, int pixel5, int pixel6, int pixel7, int pixel8
    		, int pixel9, int pixel10, int pixel11, int pixel12
    		, int pixel13, int pixel14, int pixel15, int pixel16) {
    	this.id = id;
    	int[] p = {pixel, pixel2, pixel3, pixel4,
    			pixel5, pixel6, pixel3, pixel8,
    			pixel9, pixel10, pixel11, pixel2,
    			pixel13, pixel14, pixel15, pixel6};		// Initialization pixels 
    	int[] r = {16, 8, 0};							// Shift values
    	
    	// Store rgb of pixels
    	for(int i = 0 ; i < 3 ; i++) {
    		for(int j = 0 ; j < 16 ; j++) {
        		pix[j][i] = p[j] >> r[i] & 0x000000ff;
      		}
    	}
    	
    	// Add pixels to cluster
        addPixel(p);
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
    
    void addPixel(int[] pixel) {
    	int[] r = {16, 8, 0};		// Shift values

    	// Add pixel's rgb to rgb of total
    	for(int i = 0 ; i < 3 ; i++) {
    		for(int j = 0 ; j < 16 ; j++) {
    			total[j][i] += pixel[0] >> r[i] & 0x000000ff;
    		}
    	}

    	// Increase number of pixels in cluster
        amount++;
        
        // Update centroid pixels
        for(int i = 0 ; i < 3 ; i++) {
    		for(int j = 0 ; j < 16 ; j++) {
    			pix[j][i] = total[j][i] / amount;
    		}
        }
    }
    
    void removePixel(int[] pixel) {
    	int[] r = {16, 8, 0};		// Shift values
    	
    	// Subtract pixel's rgb from rgb of total
    	for(int i = 0 ; i < 3 ; i++) {
    		for(int j = 0 ; j < 16 ; j++) {
    			total[j][i] -= pixel[0] >> r[i] & 0x000000ff;
    		}
    	}
    	
    	// Decrease number of pixels in cluster
        amount--;
        
        // Update centroid pixels
        for(int i = 0 ; i < 3 ; i++) {
    		for(int j = 0 ; j < 16 ; j++) {
    			pix[j][i] = total[j][i] / amount;
    		}
        }
    }
    
    int distance(int[] pixel) {
    	int[] sum = new int[16];	// Distance of pixels
    	int[][] p = new int[16][3];	// Pixels to be calculated
    	int[] r = {16, 8, 0};		// Shift values
    	
    	// Store pixels to be calculated
    	for(int i = 0 ; i < 3 ; i++) {
    		for(int j = 0 ; j < 16 ; j++) {
    			p[j][i] = pixel[j] >> r[i] & 0x000000ff;
    		}
    	}
    	
    	// Calculate euclidean distance for rgb
    	for(int i = 0 ; i < 3 ; i++) {
    		for(int j = 0 ; j < 16 ; j++) {
        		sum[j] += Math.pow(Math.abs(pix[j][i] - p[j][i]), 2);
     		}
    	}
    	double result = 0;
    	for(double s : sum) {
    		result += Math.sqrt(s);
    	}

    	// Return distance
    	return (int)(result);
    }
}
