package sma.map;


public class Map {
	Integer[][] map;
	
	public Map(int rows,int cols){
		map = new Integer[rows][cols];
		for(int i=0; i<rows; i++)
			for(int j=0; j<cols; j++)
				map[i][j]=-1;
	}

}
