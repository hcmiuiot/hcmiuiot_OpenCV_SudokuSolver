package club.hcmiuiot.sudokusolver;

public class SudokuAlgorithm {
	private int _size = 9;
	private int matrix[][] = new int[_size][_size];
	
	public SudokuAlgorithm(int[][] matrix) {
		this.matrix = matrix;
		//int temp[][] = new int[9][9];
//		for (int i=0;i<9;i++)
//			for (int j=0;j<9;j++)
//				this.matrix[i][j] = matrix[j][i];
	}

	public int getSize() {return _size;}
	public void setSize(int size) {_size = size;}

	public boolean feasible(int x, int y, int k) {
		int i = 0, j = 0;
	    for(i = 0; i < getSize() ; i++) {
	        if(matrix[x][i] == k) 	 return false;
	    }
	    
	    for(i = 0; i < getSize() ; i++) {
	        if(matrix[i][y] == k) 	 return false;
	    }
	    
	    int a = x/3, b = y/3;
	    for(i = 3*a; i < 3*a+3; i++) {
	        for(j = 3*b; j < 3*b+3; j++) {
	            if(matrix[i][j] == k) return false;
	        }
	    }
	    return true;	
	}
	
	public void solve(int x, int y){
	    if(y == getSize()) {
	        if(x == getSize()-1) {
	            //printSolution(S);
	            //exit(0);
	        	return;
	        } else {
	        	solve(x+1, 0);
	        }
	    } 
	    else 
	    	if(matrix[x][y] == 0){
	    		int k = 0;
	    		for (k = 1; k <= getSize(); k++){
	    			if( feasible(x,y,k) ) {
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
	
	public void solve(){
	    solve(0,0);
	}
	
	public void printSolution() {
		System.out.println();
	    for (int i = 0; i<9; i++) {
	        for (int j = 0; j<9; j++)
	        	System.out.print("" + matrix[i][j] + " ");
	        System.out.println();
	    }
	}
}
