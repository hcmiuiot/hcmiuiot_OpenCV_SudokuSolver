package club.hcmiuiot.sudokusolver;

public class SudokuSolver {

	static int matrix[][] = {{5,3,0,0,7,0,0,0,0},
			  	 	  		 {6,0,0,1,9,5,0,0,0},
			  	 	  		 {0,9,8,0,0,0,0,6,7},
			  	 	  		 {8,0,0,0,6,0,0,0,3},
			  	 	  		 {4,0,0,8,0,3,0,0,1},
			  	 	  		 {7,0,0,0,2,0,0,0,6},
			  	 	  		 {0,6,0,0,0,0,2,8,0},
			  	 	  		 {0,0,0,4,1,9,6,3,5},
			  	 	  		 {0,0,0,0,8,0,0,7,9}};
	
	static int matrix2[][] = {{5,3,0,7,0,0,0,0,0},
					  		  {0,0,9,0,0,0,0,0,0},
					  		  {0,0,0,0,4,0,0,0,0},
					  		  {0,0,0,0,0,0,0,0,0},
					  		  {0,0,0,0,0,0,8,0,0},
					  		  {0,0,0,1,0,0,0,0,0},
					  		  {0,0,0,0,0,0,0,0,0},
					  		  {0,0,0,0,0,0,0,0,0},
					  		  {0,2,0,0,0,0,0,0,6}};
	
	public static void main(String[] args) {
		
//		SudokuAlgorithm solver = new SudokuAlgorithm(matrix2);
//		solver.solve();
//		solver.printFirstSolution();
		
		ImageProcessing imgProc = new ImageProcessing();
		imgProc.process();
	}
	

}
