package club.hcmiuiot.sudokusolver;

import java.util.ArrayList;
import java.util.List;

public class SudokuAlgorithm {
	private int _size = 9;
	private boolean solveAll = false;
	private boolean solved = false;
	private int matrix[][] = new int[_size][_size];
	private List<int[][]> solvedMatrix = new ArrayList<>();
	
	public SudokuAlgorithm(int[][] matrix) {
		this.matrix = matrix.clone();
	}

	public int  getSize() 		  {return _size;}
	public void setSize(int size) {_size = size;}

	public boolean feasible(int x, int y, int k) {
		int i = 0, j = 0;
		
	    for(i = 0; i < getSize(); i++) {
	        if (matrix[x][i] == k || matrix[i][y] == k) 	 
	        	return false;
	    }
	        
	    int a = x/3, b = y/3;
	    
	    for(i = 3*a; i < 3*a+3; i++) {
	        for(j = 3*b; j < 3*b+3; j++) {
	            if(matrix[i][j] == k) return false;
	        }
	    }
	    
	    return true;	
	}
	
	public void solve(int x, int y) {
		if (solved && solveAll == false)
			return;
		
	    if (y == getSize()) {
	        if (x == getSize()-1) {
	        	int temp[][] = new int[getSize()][getSize()];
	        	for (int i=0; i<getSize(); i++) {
	        		for (int j=0; j<getSize(); j++) {
	        			temp[i][j] = matrix[i][j];
	        		}
	        	}
	        	solvedMatrix.add(temp);
	        	solved = true;
	        	return;
	        } 
	        else {
	        	solve(x+1, 0);
	        }
	    } 
	    else 
	    	if (matrix[x][y] == 0) {
	    		int k = 0;
	    		for (k = 1; k <= getSize(); k++) {
	    			if ( feasible(x,y,k) ) {
	    				matrix[x][y] = k;
	    				solve(x, y+1);
	    				matrix[x][y] = 0;
	    			}
	    		}
	    	} 
	    	else {
	    		solve(x,y+1);
	    	}
	}
	
	public void solve() {
		solveAll = false;
	    solve(0,0);
	}
	
	public void solveAll() {
		solveAll = true;
		solve(0,0);
	}
	
	public void printSolution(int[][] pmatrix) {
		System.out.println();
	    for (int i = 0; i < getSize(); i++) {
	        for (int j = 0; j < getSize(); j++)
	        	System.out.print("" + pmatrix[i][j] + " ");
	        System.out.println();
	    }
	}
	
	public boolean printFirstSolution() {
		if (solvedMatrix.size() == 0) {
			System.out.println("Didn't find any solution!");
			return false;
		}
		printSolution(solvedMatrix.get(0));
		return true;	
	}
	
	public List<int[][]> getSolutions() {
		return solvedMatrix;
	}
	
	public int[][] getFirstSolution() {
		if (solvedMatrix.size() > 0)
			return solvedMatrix.get(0);
		else
			return null;
	}
}
