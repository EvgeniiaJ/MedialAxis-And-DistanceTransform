import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class MedialAxisThinningDistanceTransform {
	
	public static void main(String[] args) {

		String imageFileName;
		String distanceTransformFileName;
		String expansionFileName;
		String skelet;
		String decompress; 
		
		if(args.length != 3) {
			System.out.println("Invalid Number of Arguments.");
			System.out.println("Enter names of image file, file for outputing distance transform, and file for expansion output.");
			System.exit(0);
		}

		try {
			
			imageFileName = args[0];
			distanceTransformFileName = args[1];
			expansionFileName = args[2];
			skelet = distanceTransformFileName.substring(0, distanceTransformFileName.indexOf(".")) + "_skeleton.txt";
			decompress = distanceTransformFileName.substring(0, distanceTransformFileName.indexOf(".")) + "_decompressed.txt"; 
			
			Scanner image = new Scanner(new File(imageFileName));
			FileWriter distanceTransform = new FileWriter(new File(distanceTransformFileName));
			FileWriter expansion = new FileWriter(new File(expansionFileName));
			FileWriter skeleton = new FileWriter(new File(skelet));
			FileWriter decompressed = new FileWriter(new File(decompress));
			
			ImageProcessing imageProcessing = new ImageProcessing(image);
			imageProcessing.resetArray(imageProcessing.zeroFramedArray);
			imageProcessing.resetArray(imageProcessing.skeletonArray);
			imageProcessing.loadImage(image);
			imageProcessing.compute8Distances(imageProcessing.zeroFramedArray, distanceTransform);
			imageProcessing.skeletonExtraction(imageProcessing.zeroFramedArray, imageProcessing.skeletonArray, skeleton, distanceTransform);
			Scanner inputSkeleton = new Scanner(new File(skelet));
			imageProcessing.skeletonExpansion(imageProcessing.zeroFramedArray, inputSkeleton, expansion);
			imageProcessing.arrayToFile(imageProcessing.zeroFramedArray, decompressed);

			image.close();
			distanceTransform.close();
			expansion.close();
			decompressed.close();
			inputSkeleton.close();
			
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}
