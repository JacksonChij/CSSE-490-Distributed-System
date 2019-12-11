import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class ThreadedMatrixMult {
	
	public static int ROWSIZE = 100;
	public static int COLSIZE = 100;
	public static int NUMMATRICES = 1000;
	public static final int MAXTHREADS = 500;
	public static final String filename = "MatrixOutputVals"+MAXTHREADS +".csv";
	private static double[][][] matrices;
	private static double[][][] serialResults;
	private static double[][][] threadedResults;
	
	public static void generateRandomMatrices() {
		matrices = new double[NUMMATRICES][ROWSIZE][COLSIZE];
		for(int matrixNum = 0; matrixNum < NUMMATRICES; matrixNum++) {
			for(int i = 0; i < ROWSIZE; i++) {
				for(int j = 0; j < COLSIZE; j++) {
					matrices[matrixNum][i][j] = (Math.random()*NUMMATRICES);
				}
			}
		}
	}
	
	public static double[][] multiplyMatrices(double[][] firstMatrix, double[][] secondMatrix) {
	    double[][] result = new double[firstMatrix.length][secondMatrix[0].length];
	 
	    for (int row = 0; row < result.length; row++) {
	        for (int col = 0; col < result[row].length; col++) {
	            result[row][col] = multiplyMatricesCell(firstMatrix, secondMatrix, row, col);
	        }
	    }
	 
	    return result;
	}
	
	public static double multiplyMatricesCell(double[][] firstMatrix, double[][] secondMatrix, int row, int col) {
	    double cell = 0;
	    for (int i = 0; i < secondMatrix.length; i++) {
	        cell += firstMatrix[row][i] * secondMatrix[i][col];
	    }
	    return cell;
	}
	
	public static boolean MatrixEqualsMatrixEquals(double[][][] first, double[][][] second) {
		for(int i = 0; i < first.length; i++) {
			for(int row = 0; row < first[0].length; row++) {
				for(int col = 0; col < first[0][0].length; col++) {
					if(!(first[i][row][col] < (second[i][row][col])+0.0001 &&
							first[i][row][col] > (second[i][row][col])-0.0001)) {
						System.out.println("Found inequality at " + i + " " +
							row + " " + col + " with first being " + first[i][row][col]
							+ " and second being " + second[i][row][col]);
						return false;
					}
				}
			}
		}
		
		return true;
	}

	public static void main(String[] args) {
		System.out.println("Generating " + NUMMATRICES + " random matrices...");
		generateRandomMatrices();
		System.out.println("Finished generating matrices, now multiplying each pair." +
		" Output to file named " + filename);
		
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(filename));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		serialResults = new double[NUMMATRICES][ROWSIZE][COLSIZE];
		long serialBefore = System.currentTimeMillis();
		for(int i = 0; i < NUMMATRICES; i++) {
			serialResults[i] = multiplyMatrices(matrices[i], matrices[(i+1)%NUMMATRICES]);
		}
		long serialAfter = System.currentTimeMillis();
		
		long timeTaken = serialAfter-serialBefore;
		System.out.println("The serial runtime to multiply " + NUMMATRICES + 
				" matrices is: " + timeTaken + 
				"ms");
		try {
			writer.write("1,"+ timeTaken + "\n");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		//Time for threads!
		threadedResults = new double[NUMMATRICES][ROWSIZE][COLSIZE];
		long shortestTime = Long.MAX_VALUE;
		ArrayList<Integer> fastestNumThreads = new ArrayList<>();
		System.out.println("Starting threads...\n*****************************************\n");
		Thread[] threads;
		int increment = 1;
		for(int numThreads = 2; numThreads <= MAXTHREADS; numThreads+=increment) {
			threads = new Thread[numThreads];
			long Setup = System.currentTimeMillis();
			for(int i = 0; i < numThreads; i ++){
				Runnable r = new MuxRunner(matrices,numThreads, i, threadedResults);
				threads[i] = new Thread(r);
			}
			long Before = System.currentTimeMillis();
			for(Thread t: threads){
				t.start();
			}
			for(Thread t: threads){
				try {
					t.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			long After = System.currentTimeMillis();
			long total = (After - Before);
			long setup = After - Setup;
			System.out.println("Time to multiply " + NUMMATRICES + " elements with " + numThreads + " threads is: " + total + "ms (The total time including setup is: " + setup + "ms.)");
			fastestNumThreads.add((int) total);
			shortestTime = Math.min(shortestTime,total);
			try {
				writer.write(numThreads + ","+ total + "\n");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			/*TODO 2: write the code to generate the number of threads given
			 * by numThreads.  Each thread should take its portion of
			 * the matrices (for example, if there are 100 matrices and 4
			 * threads, then the first thread should take matrices 0-23, 
			 * the second should take matrices 24-49, the third 50-74 and 
			 * the fourth 75-99).
			 * 
			 * HINT: You will create the threads, start them, and then to make sure
			 * they have all finished, you should call join() on each thread before
			 * checking the results and reporting the time taken. join() assures the 
			 * code in the current thread does not go any further until that thread 
			 * has stopped running.
			 * 
			 * You should time each run in the same manner as the serial version 
			 * was timed above. And report the times to the console as well as 
			 * to the csv file as done above using the variable named "writer". 
			 * Hint: csv files are simply data separated by commas and new lines.  
			 * Each line should be one entry. It should look like:
			 * 1,<time_for_1_thread>
			 * 2,<time_for_2_threads>
			 * 3,<time_for_3_threads>
			 * 4,<time_for_4_threads>
			 * ...
			 * 
			 * You should time 2 separate things, reporting them both:
			 * 		1) The time it takes to create the threads to the time it is 
			 *         all finished.
			 * 		2) The time it takes just to do the calculations AFTER the 
			 *         threads have been created.
			 * 
			 * For tracking the shortestTime across all threads, use the time found 
			 * from (2) above.
			 * 
			 */
			
			
			
			/* IMPORTANT: You should make sure that the results you got 
			 * from the threaded version match the results from the serial 
			 * version already completed above.  Uncomment the if statement 
			 * below to do so. Once you are sure it works properly,
			 * you do not need to run this code subsequently, it takes up
			 * a considerable amount of time...
			 */
			
			if(!MatrixEqualsMatrixEquals(threadedResults, serialResults)) {
				System.out.println("PROBLEM!  NOT EQUAL!");
			}
		}
		
		System.out.print("********************************************\n" + 
				"\nThe fastest runtime is " + shortestTime + 
				"ms from using the following number of threads: ");
				
		
		//NOTE: Until you complete the loop above, this will cause a NullPointerException
		for(int i = 0; i < fastestNumThreads.size(); i++) {
			if(fastestNumThreads.get(i) == shortestTime){
				System.out.print(i + " ");
			}
		}
		
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static class MuxRunner implements Runnable{
		double[][][] res;
		double[][][] matrices;
		int start, end, idx;

		MuxRunner(double[][][] matrices, int numThreads, int idx, double[][][] threadedResults){
			this.matrices = matrices;
			this.idx = idx;
			this.res = threadedResults;
			this.start = idx * (matrices.length / numThreads);
			this.end = idx == numThreads-1 ? matrices.length-1 : this.start + matrices.length / numThreads;

		}
		@Override
		public void run() {
			for(int i = this.start; i <= this.end; i ++){
				this.res[i] = multiplyMatrices(this.matrices[i], this.matrices[(i+1) % this.matrices.length]);

			}
		}
	}
}

/* TODO 1: Write a Runnable class to do multi-threaded matrix multiplication.
 * Each thread should take its portion of the matrices and store the results
 * in the threadedResults variable above.  (Hint, you'll need to pass 
 * threadedResults in to the constructor for the class and save a reference 
 * to it.  When you pass an array to a function or constructor, the parameter 
 * is only a reference to the original array... thus, it won't bog down the 
 * speed of your program.)
 * 
 */






