public class Worker implements Runnable {
	private int row;
	private int col;
	private int rowStart;
	private int colStart;
	private int [][] A;
	private int [][] B;
	private int [][] C;
	
	public Worker (int ID, int numThrd, int N, int[][] A, int[][] B, int[][] C) {
		
		this.row = N/2;
		this.col = N;
		this.rowStart = 0;
		this.colStart = 0;
		this.A = A;
		this.B = B;
		this.C = C;

		if (numThrd == 2) {
			if (ID == 1) {
				this.rowStart = N/2;
				this.row = N;
			}
		}
		
		if (numThrd == 4) {
			if (ID == 0) {
				this.row = N/2;
				this.rowStart = 0;
				this.col = N/2;
				this.colStart = 0;
			}
			if (ID == 1) {
				this.colStart = 0;
				this.col = N;
				this.row = N;
				this.rowStart = N/2;
			}
			if (ID == 2) {
				this.rowStart = 0;
				this.row = N;
				this.col = N/2;
				this.colStart = 0;
			}
			if (ID == 3) {
				this.colStart = N/2;
				this.col = N;
				this.rowStart = 0;
				this.row = N;
			}
		}

		
	}
	
	public void run() {
		for (int i = rowStart; i < row; i++)
			for (int j = colStart; j < col; j++)
				for (int k = 0; k < col; k++)
					C[i][j] += A[i][k] * B[k][j];
		
	}
}

/*
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
*/