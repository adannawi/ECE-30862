import java.io.IOException;
import java.util.Scanner;

public class matrix {

	//Function to fill in an array with a given input size
	//Input size has to be divisible by two, if not, alert the user and prompt for input
	public static int[][] fill(int N) {
		int [][] mat = new int[N][N];
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				mat[i][j] = 0 + (int)(Math.random() * ((20 - 0) + 1));
		return mat;
	}

	
	//Diagnostic matrix print
	public static void print(int[][] A, int N) {
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				System.out.print(" "+A[i][j]);
			}
				System.out.println("");
		}
	
	}
	
	public static void divideByTwo(int[][] C, int N) {
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				C[i][j] /= 2;
			}
		}
	}
	
	public static int getN() {
		int N = 0;
		boolean flg = false;
		Scanner reader = new Scanner(System.in);
		while (!flg) {
		System.out.print("Enter a number: ");
		N = reader.nextInt();
		if (N % 2 != 0) {
			System.out.println("ERROR! N is not divisible by 2!"); 
			flg =  false; 
			} else { 
				flg = true; }
		}
		return N;
	}
	
	
	//Main function
	public static void main(String[] args) throws InterruptedException {
		int N = getN();
		int row = 0;
		int col = 0;
		
		final int NUM_THREAD = 1; //Define number of threads to be used
		int[][] A = matrix.fill(N);
		int[][] B = matrix.fill(N);
		int[][] C = new int [N][N];
		int threadct = 0;
		//Do multithreading
		//1 thread
		Thread t = new Thread(new Worker(0, NUM_THREAD, N, A, B, C));
		
		
		//2 threads
		Thread t2_0 = new Thread(new Worker(0, NUM_THREAD*2, N, A, B, C));
		Thread t2_1 = new Thread(new Worker(1, NUM_THREAD*2, N, A, B, C));
		
		
		//4 threads
		Thread t3_0 = new Thread(new Worker(0, NUM_THREAD*4, N, A, B, C));
		Thread t3_1 = new Thread(new Worker(1, NUM_THREAD*4, N, A, B, C));
		Thread t3_2 = new Thread(new Worker(2, NUM_THREAD*4, N, A, B, C));
		Thread t3_3 = new Thread(new Worker(3, NUM_THREAD*4, N, A, B, C));
		
		
		//////////////////CASE 1/////////////////////////////////////
		System.out.println("Case 1: (1 thread, N = "+N+" ("+N+"x"+N+" matrices)");
		double timeBeforeMult = System.currentTimeMillis();
		t.start();
		t.join();
		double timeAfterMult = System.currentTimeMillis();
		double runTime = timeAfterMult - timeBeforeMult;
		System.out.println("Run time (ms):" +runTime);
		
		//////////////////CASE 2/////////////////////////////////////
		System.out.println("Case 2: (2 threads, N = "+N+" ("+N+"x"+N+" matrices)");
		double timeBeforeMult2 = System.currentTimeMillis();
		t2_0.start();
		t2_1.start();
		t2_0.join();
		t2_1.join();
		double timeAfterMult2 = System.currentTimeMillis();
		double runTime2 = timeAfterMult2 - timeBeforeMult2;
		System.out.println("Run time (ms):" +runTime2);
		//////////////////CASE 3/////////////////////////////////////
		System.out.println("Case 3: (4 threads, N = "+N+" ("+N+"x"+N+" matrices)");
		double timeBeforeMult3 = System.currentTimeMillis();
		t3_0.start();
		t3_1.start();
		t3_2.start();
		t3_3.start();
		t3_0.join();
		t3_1.join();
		t3_2.join();
		t3_3.join();
		double timeAfterMult3 = System.currentTimeMillis();
		double runTime3 = timeAfterMult3 - timeBeforeMult3;
		System.out.println("Run time (ms):" +runTime3);
		
		
	//	System.out.println("Matrix A:");
	//	matrix.print(A, N);
	//	System.out.println("Matrix B:");
	//	matrix.print(B, N);
	//	System.out.println("Matrix C (multiplied):");
	//	matrix.print(C, N);

	}
	
}