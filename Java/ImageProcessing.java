import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ImageProcessing {
	
	private int imageRows;
	private int imageCols;
	private int imageMin;
	private int imageMax;
	private int newMin;
	private int newMax;
	
	public int[][] zeroFramedArray;
	public int[][] skeletonArray;
	public int[] neighborsArray;
	
	public ImageProcessing(Scanner input) {
		
		if(input.hasNext()) {
			imageRows = input.nextInt();
		}
		if(input.hasNext()) {
			imageCols = input.nextInt();
		}
		if(input.hasNext()) {
			imageMax = input.nextInt();
		}
		if(input.hasNext()) {
			imageMin = input.nextInt();
		}
		
		initializeArrays();
		
	}
	
	public void initializeArrays() {
		zeroFramedArray = new int[imageRows + 2][imageCols + 2];
		skeletonArray = new int[imageRows + 2][imageCols + 2];
		neighborsArray = new int[9];

		for (int i = 0; i < (imageRows + 2); i++) {
			for (int j = 0; j < (imageCols + 2); j++) {
				zeroFramedArray[i][j] = 0;
				skeletonArray[i][j] = 0;
			}
		}
		
		for(int i = 0; i < 9; i++) {
			neighborsArray[i] = 0;
		}
		
	}

	public void resetArray(int[][] array) {

		for (int i = 0; i < (imageRows + 2); i++) {
			for (int j = 0; j < (imageCols + 2); j++) {
				array[i][j] = 0;
			}
		}
	}
	
	public void loadImage(Scanner input) {
		while(input.hasNext()) {
			for (int i = 1; i < (imageRows + 1); i++) {
				for (int j = 1; j < (imageCols + 1); j++) {
					zeroFramedArray[i][j] = input.nextInt();
				}
			}
		}
	}
	
	public void compute8Distances(int[][] array, FileWriter output) {
		try {
			firstPass8Distance(array);
			output.write("First Pass Distance Transform:\n");
			prettyPrint(array, output, 1);
			secondPass8Distance(array);
			output.write("Second Pass Distance Transform:\n");
			prettyPrint(array, output, 1);			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void firstPass8Distance(int[][] zeroFramedAry) {
		int min = neighborsArray[0];
		for (int i = 1; i < imageRows + 1; i++) {
			for (int j = 1; j < imageCols + 1; j++) {
				if (zeroFramedAry[i][j] > 0) {
					loadNeighbors(i, j, 1);
					min = findMinNeighbor(neighborsArray, 1);
					zeroFramedAry[i][j] = min + 1;
				}
			}
		}
	}
	
	public void loadNeighbors(int i, int j, int passId) {
		
		if (passId == 1) {
			neighborsArray[0] = zeroFramedArray[i - 1][j - 1];
			neighborsArray[1] = zeroFramedArray[i - 1][j];
			neighborsArray[2] = zeroFramedArray[i - 1][j + 1];
			neighborsArray[3] = zeroFramedArray[i][j - 1];
		}
		if (passId == 2) {
			neighborsArray[0] = zeroFramedArray[i][j + 1];
			neighborsArray[1] = zeroFramedArray[i + 1][j - 1];
			neighborsArray[2] = zeroFramedArray[i + 1][j];
			neighborsArray[3] = zeroFramedArray[i + 1][j + 1];
			neighborsArray[4] = zeroFramedArray[i][j];
		}
		if (passId == 3 || passId == 4) {
			int m = 0;
			for (int k = i - 1; k < i + 2; k++) {
				for (int l = j - 1; l < j + 2; l++) {
					neighborsArray[m] = zeroFramedArray[k][l];
					m++;
				}
			}
		}
		
	}
	
	public int findMinNeighbor(int[] neighbors, int passId) {
		int min = neighbors[0];
		
		if(passId == 1) {
			for (int k = 0; k < 4; k++) {
				if (min > neighbors[k]) {
					min = neighbors[k];
				}
			}
		}
		if (passId == 2) {
			for (int k = 0; k < 4; k++) {				
				if (min > neighbors[k]) {
					min = neighbors[k];
				}
			}
			min++;
			if (neighbors[4] < min) {
				min = neighbors[4];
			}
		}
		return min;
	}
	
	public void secondPass8Distance(int[][] array) {
		int min = neighborsArray[0];
		newMin = array[1][1];
		newMax = array[1][1];
		for (int i = imageRows; i > 0; i--) {
			for (int j = imageCols; j > 0; j--) {
				if(array[i][j] > 0) {
					loadNeighbors(i, j, 2);
					min = findMinNeighbor(neighborsArray, 2);
					array[i][j] = min;
				}
				if (array[i][j] < newMin) {
					newMin = array[i][j];
				}
				if (array[i][j] > newMax) {
					newMax = array[i][j];
				}
			}
		}
	}

	public void skeletonExtraction(int[][] zeroFramedArray, int[][] skeletonArray, FileWriter skeleton, FileWriter output) {

		try {
			computeLocalMaxima(zeroFramedArray, skeletonArray);
			output.write("\nLocal Maxima Content of Skeleton Array:\n");
			prettyPrint(skeletonArray, output, 1);
			extractLocalMaxima(skeletonArray, skeleton);
			skeleton.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void computeLocalMaxima(int[][] zeroFramedArray, int[][] skeletonArray) {
		for (int i = 1; i < imageRows + 1; i++) {
			for (int j = 1; j < imageCols + 1; j++) {
				if (zeroFramedArray[i][j] > 0 && isLocalMaxima(zeroFramedArray, i, j)) {
					skeletonArray[i][j] = zeroFramedArray[i][j];
				} else {
					skeletonArray[i][j] = 0;
				}
			}
		}
	}

	public boolean isLocalMaxima(int[][] zeroFramedAry, int i, int j) {
		boolean flag = true;
		loadNeighbors(i, j, 3);
		for (int k = 0; k < 9; k++) {
			if (zeroFramedAry[i][j] < neighborsArray[k]) {
				flag = false;
			}
		}
		return flag;
	}

	public void skeletonExpansion(int[][] zeroFramedAry, Scanner skeleton, FileWriter output) {

		try {
			resetArray(zeroFramedAry);
			loadSkeleton(skeleton, zeroFramedAry);
			firstPassExpansion(zeroFramedAry);
			output.write("First Pass Expansion:\n");
			prettyPrint(zeroFramedAry, output, 2);
			secondPassExpansion(zeroFramedAry);
			output.write("\nSecond Pass Expansion:\n");
			prettyPrint(zeroFramedAry, output, 2);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void loadSkeleton(Scanner input, int[][] zeroFramedArray) {
		int hRow = -1, hCol = -1, hMin = -1, hMax = -1;
		if (input.hasNext()) {
			hRow = input.nextInt();
		}

		if (input.hasNext()) {
			hCol = input.nextInt();
		}

		if (input.hasNext()) {
			hMin = input.nextInt();
		}

		if (input.hasNext()) {
			hMax = input.nextInt();
		}

		int row = -1, col = -1, val = -1;

		while (input.hasNext()) {
			row = input.nextInt();
			col = input.nextInt();
			val = input.nextInt();
			zeroFramedArray[row][col] = val;
		}
	}

	void firstPassExpansion(int[][] zeroFramedArray) {
		int max = neighborsArray[0];
		for (int i = 1; i < imageRows + 1; i++) {
			for (int j = 1; j < imageCols + 1; j++) {
				if (zeroFramedArray[i][j] == 0) {
					loadNeighbors(i, j, 4);
					max = findMaxNeighbor(neighborsArray, 1);
					zeroFramedArray[i][j] = Math.max(0, max);

				}
			}
		}
	}

	public int findMaxNeighbor(int[] neighborsArray, int passId) {
		int max = neighborsArray[0];
		if (passId == 1 || passId == 2) {
			for (int k = 0; k < 9; k++) {
				if (max < neighborsArray[k]) {
					max = neighborsArray[k];
				}
			}
			max--;
		}
		return max;
	}

	public void secondPassExpansion(int[][] zeroFramedArray) {
		int max = neighborsArray[0];
		for (int i = imageRows; i > 0; i--) {
			for (int j = imageCols; j > 0; j--) {
				loadNeighbors(i, j, 4);
				max = findMaxNeighbor(neighborsArray, 2);
				zeroFramedArray[i][j] = Math.max(zeroFramedArray[i][j], max);
			}
		}
	}

	public void extractLocalMaxima(int[][] skeletonAry, FileWriter output) {
		try {
			output.write(imageRows + " " + imageCols + " " + newMin + " " + newMax + "\n");
			for (int i = 1; i < imageRows + 1; i++) {
				for (int j = 1; j < imageCols + 1; j++) {
					if (skeletonAry[i][j] > 0) {
						output.write(i + " " + j + " " + skeletonAry[i][j] + "\n");
					}
				}
			}
			output.write("\n");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void prettyPrint(int[][] array, FileWriter output, int printId) {

		if(printId == 1) {
			try {
				for (int i = 1; i < imageRows + 1; i++) {
					for (int j = 1; j < imageCols + 1; j++) {
						if (array[i][j] != 0) {
							if (array[i][j] < 10) {
								output.write(array[i][j] + "  ");
							} else {
								output.write(array[i][j] + " ");
							}
						} else {
							output.write("   ");
						}
					}
					output.write("\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(printId == 2) {
			try {
				for (int i = 1; i < imageRows + 1; i++) {
					for (int j = 1; j < imageCols + 1; j++) {
						if (array[i][j] != 0) {
							if (array[i][j] < 10) {
								output.write(array[i][j] + " ");
							} else {
								output.write(array[i][j] + "");
							}
						} else {
							output.write("  ");
						}
					}
					output.write("\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void arrayToFile(int[][] zeroFramedArray, FileWriter output) {
		try {
			output.write(imageRows + " " + imageCols + " 0 1 \n");
			for (int i = 1; i < imageRows + 1; i++) {
				for (int j = 1; j < imageCols + 1; j++) {
					if (zeroFramedArray[i][j] > 0) {
						output.write("1 ");
					} else {
						output.write("0 ");
					}
				}
				output.write("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public int getImageRows() {
		return imageRows;
	}
	public void setImageRows(int imageRows) {
		this.imageRows = imageRows;
	}
	public int getImageCols() {
		return imageCols;
	}
	public void setImageCols(int imageCols) {
		this.imageCols = imageCols;
	}
	public int getImageMin() {
		return imageMin;
	}
	public void setImageMin(int imageMin) {
		this.imageMin = imageMin;
	}
	public int getImageMax() {
		return imageMax;
	}
	public void setImageMax(int imageMax) {
		this.imageMax = imageMax;
	}
	public int getNewMin() {
		return newMin;
	}
	public void setNewMin(int newMin) {
		this.newMin = newMin;
	}
	public int getNewMax() {
		return newMax;
	}
	public void setNewMax(int newMax) {
		this.newMax = newMax;
	}
	
	

}
