import java.io.IOException;
import java.util.Scanner;

public class matrix {
	
	//Function to fill in an array with a given input size
	//Input size has to be divisible by two, if not, alert the user and prompt for input
	public static int[][] fill(int N){ //throws IOException{
		if (N %2 != 0) {} //throw new IOException(N+" is not divisible by 2!");}
		int [][] mat = new int[N][N];
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				mat[i][j] = 0 + (int)(Math.random() * ((20 - 0) + 1));
		return mat;
	}
	
	
	//Perform matrix multiplication
	public static int[][] multiply (int[][] A, int[][] B) {
		int mA = A.length;
		int nA = A[0].length;
		int mB = B.length;
		int nB = B[0].length;
		if (nA != mB) throw new RuntimeException("Illegal matrix dimensions.");
		int[][] C = new int[mA][nB];
		for (int i = 0; i < mA; i++)
			for (int j = 0; j < nB; j++)
				for (int k = 0; k < nA; k++)
					C[i][j] += A[i][k] * B[k][j];
		return C;
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
	
	//Main function
	public static void main(String[] args) throws InterruptedException {
		int N = 3000;
		int row = 0;
		int col = 0;
		
		final int NUM_THREAD = 2;
		/*
		switch (NUM_THREAD) {
		case 1: row = N;
		        col = N;
		        break;
		case 2: row = N/2;
				col = N;
				break;
		case 4: row = N/2;
				col = N/2;
				break;
		default: row = N;
				 col = N;
				 break;
		}
		
		*/
		int[][] A = matrix.fill(N);
		int[][] B = matrix.fill(N);
		int[][] C = new int [N][N];
		int threadct = 0;
		//Do multithreading
		double timeBeforeMult = System.currentTimeMillis();
		Thread t = new Thread(new Worker(0, NUM_THREAD, N, A, B, C));
		Thread t2 = new Thread(new Worker(1, NUM_THREAD, N, A, B, C));
	//	Thread t3 = new Thread(new Worker(2, NUM_THREAD, N, A, B, C));
	//	Thread t4 = new Thread(new Worker(3, NUM_THREAD, N, A, B, C));
		t.start();
		t2.start();
	//	t3.start();
	//	t4.start();
		t.join();
		t2.join();

	//	int[][] C = matrix.multiply(A, B);
		double timeAfterMult = System.currentTimeMillis();
		double runTime = timeAfterMult - timeBeforeMult;
		System.out.println("Matrix A:");
		//matrix.print(A, N);
		System.out.println("Matrix B:");
		//matrix.print(B, N);
		System.out.println("Run time (ms):" +runTime);
		System.out.println("Matrix C (multiplied):");
		//matrix.print(C, N);

	}
	
}