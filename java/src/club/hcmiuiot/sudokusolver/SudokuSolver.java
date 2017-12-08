package club.hcmiuiot.sudokusolver;

public class SudokuSolver {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int matrix[][] = {{5,3,0,0,7,0,0,0,0},
						  {6,0,0,1,9,5,0,0,0},
						  {0,9,8,0,0,0,0,6,7},
						  {8,0,0,0,6,0,0,0,3},
						  {4,0,0,8,0,3,0,0,1},
						  {7,0,0,0,2,0,0,0,6},
						  {0,6,0,0,0,0,2,8,0},
						  {0,0,0,4,1,9,6,3,5},
						  {0,0,0,0,8,0,0,7,9}};
		
		SudokuAlgorithm solver = new SudokuAlgorithm(matrix);
		solver.solve(0,0);
		solver.printSolution();
		//System.out.println(solver.feasible(2, 0, 3));
	}

}
