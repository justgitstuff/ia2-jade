package sma.ontology;

/**
 * <p><B>Title:</b> IA2-SMA</p>
 * <p><b>Description:</b> Practical exercise 2010-11. Recycle swarm. Robocup Rescue.
 * Object transmitted during the initialization of agents.</p>
 * <p><b>Copyright:</b> Copyright (c) 2009</p>
 * <p><b>Company:</b> Universitat Rovira i Virgili (<a
 * href="http://www.urv.cat">URV</a>)</p>
 * @author David Isern & Joan Albert López
 * @version 2.0
 */
public class CellList implements java.io.Serializable {

  private Cell[] cellList;

  public void setCellList(java.util.List<Cell> l) {
    cellList = new Cell[l.size()];
    for(int kk=0; kk<l.size(); kk++) {
    	cellList[kk] = (Cell)l.get(kk);
    }
  }

  public Cell[] getCellList() {
    return this.cellList;
  }
  public void clearCellList() {
    this.cellList = new Cell[0];
  }

  public CellList() {
  }


} //endof CellList
