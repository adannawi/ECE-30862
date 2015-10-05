public class Worker implements Runnable {
	private int row;
	private int col;
	private int rowStart;
	private int colStart;
	private int size;
	private int [][] A;
	private int [][] B;
	private int [][] C;
	private int ID;
	
	public Worker (int ID, int numThrd, int N, int[][] A, int[][] B, int[][] C) {
		
		this.row = N/2; //was N/2
		this.col = N;
		this.rowStart = 0;
		this.colStart = 0;
		this.size = N;
		this.A = A;
		this.B = B;
		this.C = C;
		this.ID = ID;
		if (numThrd == 1) { this.row = N; }

		if (numThrd == 2) {
			if (ID == 1) {
				this.rowStart = N/2;
				this.row = N;
			}
		}
		
		if (numThrd == 4) {
			if (ID == 0) {
				//this.row = N/2;
				//this.rowStart = 0;
				//this.col = N/2;
			//	this.colStart = 0;
				this.row = N/2;
				this.rowStart = 0;
				this.col = N/2;
				this.colStart = 0;
			}
			if (ID == 1) {
			//	this.colStart = 0;
			//	this.col = N;
			//	this.row = N;
			//	this.rowStart = N/2;
				this.row = N/2;
				this.rowStart = 0;
				this.col = N;
				this.colStart = N/2;
			}
			if (ID == 2) {
			//	this.rowStart = 0;
			//	this.row = N;
			//	this.col = N/2;
			//	this.colStart = 0;
				this.row = N;
				this.rowStart = N/2;
				this.col = N/2;
				this.colStart = 0;
			}
			if (ID == 3) {
			//	this.colStart = N/2;
			//	this.col = N;
			//	this.rowStart = 0;
			//	this.row = N;
				this.row = N;
				this.rowStart = N/2;
				this.col = N;
				this.colStart = N/2;
			}
		}

		
	}
	
	public void run() {
		for (int i = rowStart; i < row; i++)
			for (int j = colStart; j < col; j++)
				for (int k = 0; k < size; k++)
					C[i][j] += A[i][k] * B[k][j];
		
	}
}
