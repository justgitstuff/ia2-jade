package sma.ontology;

import java.io.*;
import java.util.StringTokenizer;

import sma.gui.UtilsGUI;

/**
 * <p><B>Title:</b> IA2-SMA</p>
 * <p><b>Description:</b> Practical exercise 2010-11. Recycle swarm.</p>
 * Information about the current game. This object is initialized from a file.
 * <p><b>Copyright:</b> Copyright (c) 2011</p>
 * <p><b>Company:</b> Universitat Rovira i Virgili (<a
 * href="http://www.urv.cat">URV</a>)</p>
 * @author David Isern & Joan Albert López
 * @see sma.CoordinatorAgent
 * @see sma.CentralAgent
 */
public class InfoGame implements java.io.Serializable {

  private AuxInfo info;
  private Cell[][] map;
 
  static private boolean DEBUG = false;

  public InfoGame() {
	  info=new AuxInfo();
  }

  public AuxInfo getInfo() {
	return info;
  }

  public void setInfo(AuxInfo info) {
	this.info = info;
  }

  public Cell[][] getMap() { return this.map; }

  public Cell getCell(int x, int y) { return this.map[x][y]; }
  public void setCell(int x, int y, Cell c) { this.map[x][y] = c; }
  
  private void showMessage(String s) {
    if(this.DEBUG)
      System.out.println(s);
  }

//  /**
//   * We write the string specified into a file.
//   * @param content String to write
//   * @param file Pathname of the file
//   * @return Nothing
//   */
//  private void writeFile(String content, File file) throws IOException {
//    StringBuffer sb = new StringBuffer(content);
//    PrintStream outFile = new PrintStream(new FileOutputStream(file));
//    for (int i = 0; i < content.length(); i++) {
//      outFile.print(sb.charAt(i));
//    }
//    //    System.out.println(content.length()+" characters write");
//  }
  public void writeGameResult(String fileOutput, Cell[][] t) throws IOException, Exception {
    File file= new File(fileOutput);
    String content = "" + this.info.getGameDuration()+"\n"+this.info.getTimeout()+"\n";
    for(int r=0; r<t.length; r++) {
      for(int c=0; c<t[0].length; c++) {
        Cell ca = t[r][c];
        content = content + Cell.getCellType(ca.getCellType());
        if(ca.getCellType()==Cell.BUILDING)
          content = content + ca.getGarbageUnits();
        content+="\t";
      }
      content+="\n";
    }
    UtilsGUI.writeFile(content,file);
    showMessage("File written");
  }


  public void readGameFile (String file) throws IOException,Exception {
	FileReader fis = new FileReader(file);
    BufferedReader dis = new BufferedReader(fis);
    int NROWS = 0, NCOLS = 0;
    
	String dades = dis.readLine(); StringTokenizer st = new StringTokenizer(dades, " ");
	this.info.setGameDuration(Integer.parseInt(st.nextToken()));
	dades = dis.readLine(); st = new StringTokenizer(dades, " ");
	this.info.setTimeout(Long.parseLong(st.nextToken()));
	dades = dis.readLine(); st = new StringTokenizer(dades, " ");
	NROWS = Integer.parseInt(st.nextToken());
	dades = dis.readLine(); st = new StringTokenizer(dades, " ");
	NCOLS = Integer.parseInt(st.nextToken());
	this.map = new Cell[NROWS][NCOLS];
	dades = dis.readLine(); st = new StringTokenizer(dades, " ");
	this.info.setNumScouts(Integer.parseInt(st.nextToken()));
	dades = dis.readLine(); st = new StringTokenizer(dades, " ");
	this.info.setNumHarvesters(Integer.parseInt(st.nextToken()));
	dades = dis.readLine(); st = new StringTokenizer(dades, " ");
	dades = st.nextToken(); st = new StringTokenizer(dades, ",");
	this.info.setTypeHarvesters(new String[this.info.getNumHarvesters()]);
	this.info.setCapacityHarvesters(new int[this.info.getNumHarvesters()]);
	for (int i=0; i<this.info.getNumHarvesters(); i++) {
		String str = st.nextToken();
		StringTokenizer st2 = new StringTokenizer(str, "-");
		this.info.getTypeHarvesters()[i] = st2.nextToken();
		this.info.getCapacityHarvesters()[i] = Integer.parseInt(st2.nextToken());
	}
	int col=0, row=0;
	//Llegim mapa
	while ((dades = dis.readLine()) != null) {
		col=0;
		st = new StringTokenizer(dades, " ");
		while (st.hasMoreTokens()){
			String str = st.nextToken();
			if(str.equals("s")) this.map[row][col]= new Cell(Cell.STREET);
			else{
				if(str.charAt(0)=='b') {
					this.map[row][col]= new Cell(Cell.BUILDING);
					if (str.length()>1){
						String type = str.substring(str.length()-2, str.length()-1);
						int units = Integer.parseInt(str.substring(2, str.length()-2));
						this.map[row][col].setGarbageUnits(units);
						this.map[row][col].setGarbageType(type.charAt(0));
					}					
				}else{
					this.map[row][col]= new Cell(Cell.RECYCLING_CENTER);
					if (str.length()>1){
						str = str.substring(2, str.length()-1);
						StringTokenizer st2 = new StringTokenizer(str, ",");
						int[] points = new int[4];
						points[0] = Integer.parseInt(st2.nextToken());
						points[1] = Integer.parseInt(st2.nextToken());
						points[2] = Integer.parseInt(st2.nextToken());
						points[3] = Integer.parseInt(st2.nextToken());
						this.map[row][col].setGarbagePoints(points);
						this.map[row][col].setDiscovered(true);
						this.info.addRecyclingCenter(this.map[row][col]);
					}					
				}
			}
			this.map[row][col].setRow(row);
			this.map[row][col].setColumn(col);
			showMessage(((Cell)map[row][col]).toString() );
			col++;
		}
		row++;		
	}
  }

} //endof class InfoPartida
