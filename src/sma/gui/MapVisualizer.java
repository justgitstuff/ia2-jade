package sma.gui;

import javax.swing.*;
import java.awt.geom.Ellipse2D;
import java.awt.Composite;
import java.awt.geom.Rectangle2D;
import java.awt.AlphaComposite;
import java.awt.Toolkit;
import java.awt.Point;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage; 
import javax.imageio.*;
import java.io.*;

import sma.ontology.Cell;
import sma.ontology.InfoAgent;
/**
 * <p><B>Title:</b> IA2-SMA</p>
 * <p><b>Description:</b> Practical exercise 2010-11. Recycle swarm.</p>
 * Visualization of the map. There are several elements to depict, as
 * buildings, streets, recycling centers, the agents, etc.<br>
 * This class *should be* modified and improved in order to show as good as
 * possible all the changes in the simulation. We provide several high-level
 * methods which can be rewritten as needed.<br>
 * <p><b>Copyright:</b> Copyright (c) 2011</p>
 * <p><b>Company:</b> Universitat Rovira i Virgili (<a
 * href="http://www.urv.cat">URV</a>)</p>
 * @author David Isern & Joan Albert L�pez
 */
public class MapVisualizer extends JPanel {

  private int inset = 50;
   int nrows, ncols;
   private Cell[][] t;
   java.awt.Point start, end;
   int dx, dy, gap;
   private Rectangle2D.Double cellBorder;
   private Rectangle2D.Double building;
   
   private Ellipse2D.Double agentFigure;



   public MapVisualizer(Cell[][] t) {
     this.t = t;
     nrows = t.length;
     ncols = t[0].length;

     Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
     start = new Point(inset, inset);
     end = new Point(screenSize.width - inset * 2, screenSize.height - inset * 2);
     dx = (end.x-start.x)/ncols;
     dy = ((end.y-start.y)/nrows)-4;

     gap = 5;
     cellBorder = new Rectangle2D.Double(gap+10, gap+10, dx, dy);

     agentFigure = new Ellipse2D.Double(gap+10+(dx/4),gap+10+(dy/4),(dx/2),(dy/2));

   }

    private void drawBuilding(Graphics2D g2d, int x, int y, Cell c) {
//       g2d.translate(dx * x, dy * y);
      try {
    	if (c.isDiscovered()) g2d.setPaint(Color.CYAN.darker());
    	else g2d.setPaint(Color.BLUE.darker());
        g2d.fill(cellBorder);
        g2d.setPaint(Color.DARK_GRAY);
        g2d.draw(cellBorder);
        String msg = c.getGarbageString();
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                             java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        java.awt.Font font = new java.awt.Font("Serif", java.awt.Font.PLAIN, 11);
        g2d.setFont(font);
        g2d.setPaint(Color.WHITE);
        g2d.drawString(msg,dx-40,dy);
      } catch(Exception e) {
        e.printStackTrace();
      }
     
    }

    private void drawRecyclingCenter(Graphics2D g2d, int x, int y, Cell c) {
//      System.out.println("HOSPITAL (x,y)=("+x+","+y+")");
//       g2d.translate(dx * x, dy * y);
      try {
    	if (c.isDiscovered()) g2d.setPaint(Color.WHITE);
      	else g2d.setPaint(Color.GRAY);
        g2d.fill(cellBorder);
        g2d.setPaint(Color.DARK_GRAY);
        g2d.draw(cellBorder);
        String msg = c.getGarbagePointsString();
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                             java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        java.awt.Font font = new java.awt.Font("Serif", java.awt.Font.PLAIN, 11);
        g2d.setFont(font);
        g2d.setPaint(Color.BLACK);
        g2d.drawString(msg,dx-40,dy);
      } catch(Exception e) {
        e.printStackTrace();
      }
    }
    
    private void moveXY(Graphics2D g2d, int x, int y) {
      g2d.translate(dx * x, dy * y);
    }

    private Color getColor(InfoAgent agent) {
      if(agent.getAgentType()==InfoAgent.HARVESTER) return new Color(255,255,255);
      if(agent.getAgentType()==InfoAgent.SCOUT) return new Color(255,0,0);
      return null;
    }

    private void drawStreetAgents(Graphics2D g2d, int x, int y, Cell c) {
      if(c.isThereAnAgent()) {
    	c.setDiscovered(true);
    	if (x>0){
    		t[x-1][y].setDiscovered(true);
    		if (y>0) t[x-1][y-1].setDiscovered(true);
    		if (y<t[x].length-1) t[x-1][y+1].setDiscovered(true);
    	}
    	if (x<t.length-1){
    		t[x+1][y].setDiscovered(true);
    		if (y>0) t[x+1][y-1].setDiscovered(true);
    		if (y<t[x].length-1) t[x+1][y+1].setDiscovered(true);
    	}
    	if (y>0) t[x][y-1].setDiscovered(true);
		if (y<t[x].length-1) t[x][y+1].setDiscovered(true);
    		
    	
        InfoAgent agent = c.getAgent();
        g2d.setPaint(getColor(agent));
        g2d.translate((dx/6),(dy/6));
        g2d.fill(agentFigure);
        g2d.translate(-(dx/6),-(dy/6));
        if (agent.getAgentType()==InfoAgent.HARVESTER){
        	char currentType;
        	if (agent.getCurrentType()==InfoAgent.GLASS) currentType='G';
        	else if (agent.getCurrentType()==InfoAgent.METAL) currentType='M';
        	else if (agent.getCurrentType()==InfoAgent.PLASTIC) currentType='P';
        	else if (agent.getCurrentType()==InfoAgent.PAPER) currentType='A';
        	else currentType='-';
        	String msg = currentType + " " + agent.getUnits();
            g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                                 java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            java.awt.Font font = new java.awt.Font("Serif", java.awt.Font.PLAIN, 11);
            g2d.setFont(font);
            g2d.setPaint(Color.BLACK);
            g2d.drawString(msg,dx-40,dy-10);
        }
      }else{
    	  if (!c.isDiscovered()){
        	  try {
        	    	g2d.setPaint(Color.DARK_GRAY);
        	        g2d.fill(cellBorder);
        	        g2d.setPaint(Color.DARK_GRAY);
        	        g2d.draw(cellBorder);
        	      } catch(Exception e) {
        	        e.printStackTrace();
        	      }
          }
      }
      
    }

    private void drawStreet(Graphics2D g2d, int x, int y, Cell c) {
      //       g2d.translate(dx * x, dy * y);
      try {
        g2d.setPaint(Color.LIGHT_GRAY);
        g2d.fill(cellBorder);
        g2d.setPaint(Color.DARK_GRAY);
        g2d.draw(cellBorder);
        drawStreetAgents(g2d,x,y,c);
      } catch(Exception e) {
        e.printStackTrace();
      }

    }


    public void paintComponent(Graphics g) {
      clear(g);
      Graphics2D g2d = (Graphics2D)g;
      for(int i=0; i<t.length; i++) {
        for(int j=0; j<t[0].length; j++) {
          g2d.draw(cellBorder);
          if(t[i][j].getCellType()==Cell.STREET) drawStreet(g2d, i, j, t[i][j]);
          if(t[i][j].getCellType()==Cell.RECYCLING_CENTER) drawRecyclingCenter(g2d, i, j, t[i][j]);
          if(t[i][j].getCellType()==Cell.BUILDING) drawBuilding(g2d, i, j, t[i][j]);
          g2d.translate(dx,0);
        }
        g2d.translate(-(dx*t[0].length),dy);
      }

      this.repaint();
   }

   protected void clear(Graphics g) {
     super.paintComponent(g);
   }

   protected Ellipse2D.Double getCircle() {
     return(agentFigure);
   }

 }
