import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ThreadedSum {
	
	public static final int N = 70000000;
	public static final int MAXTHREADS = 25;
	public static final String filename = "SumOutputVals" + MAXTHREADS + ".csv";
	private static int[] array;
	
	public static void generateRandomArray() {
		array = new int[N];
		for(int i = 0; i < N; i++) {
			array[i] = (int)(Math.random()*1000000);
		}
	}

	public static void main(String[] args) {
		System.out.println("Generating random array of size " + N);
		generateRandomArray();
		System.out.println("Finished array generation.  Now running serial sum. (Output to file named " + filename);
		long curSum = 0;
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(filename));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		long serialBefore = System.currentTimeMillis();
		for(int i = 0; i < N; i++) {
			curSum += array[i];
		}
		long serialAfter = System.currentTimeMillis();
		
		long timeTaken = serialAfter-serialBefore;
		System.out.println("The serial runtime to sum " + N + 
				" elements is: " + timeTaken + 
				"ms with a sum of: " + curSum);
		try {
			writer.write("1,"+ timeTaken + "\n");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//time for threads
		long[] returnedSums;
		long shortestTime = Long.MAX_VALUE;
		ArrayList<Integer> fastestNumThreads = new ArrayList<>();
		System.out.println("Starting threads...\n*****************************************\n");
		Thread[] threads;
		for(int numThreads = 2; numThreads <= MAXTHREADS; numThreads++) {
			threads = new Thread[numThreads];
			returnedSums = new long[numThreads];

			long Setup = System.currentTimeMillis();
			for(int i = 0; i < numThreads; i ++){
				SumRunner s = new SumRunner(numThreads, i, array, returnedSums);
				threads[i] = new Thread(s);
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
			long sum = 0;
			for(long l: returnedSums) {
				sum += l;
			}
			long After = System.currentTimeMillis();
			long total = (After - Before);
			fastestNumThreads.add((int) total);
			shortestTime = Math.min(total, shortestTime);
			long setup = (After - Setup);
			System.out.println("Time to sum " + N + " elements with " + numThreads + " threads is: " + total + "ms (The total time including setup is: " + setup + "ms.)");
			try {
				writer.write(numThreads + ","+ total + "\n");
			} catch (IOException e1) {
				e1.printStackTrace();
			}


			/*TODO 2: write the code to generate the number of threads given
			 * by numThreads.  Each thread should take its portion of
			 * the array (for example, if the array is of length 100 and 
			 * there are 4 threads, then the first thread should take numbers 
			 * 0-23, the second should take numbers 24-49, the third 50-74 and 
			 * the fourth 75-99) and sum the values.  You should use the 
			 * array named returnedSums by passing it in to the constructor 
			 * of your Runnable object.  E.g., Thread 0 should place its 
			 * partial sum in returnedSums[0], thread 1 in returnedSums[1], 
			 * etc.  When finished you will need to sum up all the values in
			 * the returnedSums array at the end of this loop, which should be
			 * included in the calculation for time elapsed.
			 * 
			 * HINT: You will create the threads, start them, and then to make sure
			 * they have all finished, you should call join() on each thread before
			 * summing up the results in returnedSums. join() assures the code in the 
			 * current thread does not go any further until that thread has stopped 
			 * running.
			 * 
			 * You should time each run in the same manner as the serial version 
			 * was timed above. And report the times to the console as well as 
			 * to the csv file as done above using the variable named "writer". 
			 * FYI: csv files are simply data separated by commas and new lines.  
			 * Each line should be one entry. It should look like:
			 * 1,<time_for_1_thread>
			 * 2,<time_for_2_threads>
			 * 3,<time_for_3_threads>
			 * 4,<time_for_4_threads>
			 * ...
			 * 
			 * You should time 2 separate things, reporting them both:
			 * 		1) The time it takes to create the threads up to the time it 
			 *         is all finished.
			 * 		2) The time it takes just to do the calculations AFTER the 
			 *         threads have been created.
			 * 
			 * For tracking the shortestTime across all threads, use the time found 
			 * from (2) above.
			 * 
			 */
			
			
			
			/* IMPORTANT: You should make sure that the results you got 
			 * from the threaded version match the results from the serial 
			 * version already completed above and stored in curSum.  Once 
			 * you are sure it works properly, you do not need to run this 
			 * code subsequently.
			 * 
			 */
		}
		
		System.out.print("********************************************\n" + 
				"\nThe fastest runtime is " + shortestTime + "ms from using the following number of threads: ");
				
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

	static class SumRunner implements Runnable{
		int start, end, idx;
		int[] arr;
		long[] res;

		SumRunner(int numThreads, int idx, int[] array,  long[] returnSum){
			this.arr = array;
			this.res = returnSum;
			this.idx = idx;
			this.start = idx * (array.length / numThreads);
			this.end = idx == numThreads-1 ? array.length-1 : this.start + array.length / numThreads;
		}
		@Override
		public void run() {
			long sum = 0;
			for(int i = this.start; i <= this.end; i ++){
				sum += this.arr[i];
			}
			this.res[idx] = sum;
		}
	}
}

/* TODO 1: Create a Runnable class to sum up a portion of the array based on the size "N"
 * of the array and how many threads there are.  Each thread should take an equal portion
 * of the numbers in the array above.  Hint: You will want to pass in the array to the
 * constructor for this class and give the thread an id so it knows which elements it
 * is to sum and which index to use for storing the result.
 * 
 */






