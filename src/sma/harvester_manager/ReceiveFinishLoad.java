package sma.harvester_manager;
import java.io.IOException;

import javax.swing.CellRendererPane;

import sma.ontology.Cell;
import sma.ontology.InfoAgent;
import sma.ontology.InfoGame;
import jade.core.*;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.*;
import jade.proto.AchieveREResponder;

public class ReceiveFinishLoad{
	private InfoGame game;
	
	/**
	 * Receive from harvester that all garbage is load, and content have all the distance of all recycling center (in one list).
	 */
	public void addBehaviour(Agent agent){
		MessageTemplate mt1 = MessageTemplate.MatchProtocol(sma.UtilsAgents.PROTOCOL_QUERY);
		MessageTemplate mt2 = MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF);
		agent.addBehaviour(new RecieveFinishL(agent,MessageTemplate.and(mt1, mt2)));
	}
	
	public class RecieveFinishL extends AchieveREResponder{		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public RecieveFinishL(Agent arg0, MessageTemplate arg1){
			super(arg0, arg1);
		}
		
		/**
		 * Execute when receive message. Receive all the distance from recycling centers in DistanceList and return cell of recycling center.
		 */
		@Override
		protected ACLMessage prepareResponse(ACLMessage arg0) throws NotUnderstoodException, RefuseException {
			DistanceList dist=null;			
			ACLMessage r= arg0.createReply();
			Cell cellRecyclingCenter=null;
			try {
				dist = (DistanceList) arg0.getContentObject();
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			//In dist have all the distance to recycling center.
			//Choose the cell that i decided and return that cell.
			//For each recycling center see the distance.
			for (int x=0;x<game.getMap().length-1;x++)
			{					
				for (int y=0; y<game.getMap()[x].length-1;y++)
				{
					Cell c=game.getCell(x,y);
					//if getGarbageunits is 0 -> no garbage.
					if(c.getCellType() == Cell.RECYCLING_CENTER)
					{	
						//In harvester have the harvester who send that FinishLoad.
						InfoAgent harvester = sma.UtilsAgents.findAgent(arg0.getSender(), game).getAgent();
						//index have the type of garbage from harvester.
						int index = harvester.getCurrentType();
												
						try {
							//In c.getGarbagePoints()[index] have points to drop material "index" in that recycling center.
							//If is zero means that recycling center no accept that type of garbage.
							if (c.getGarbagePoints()[index]>0){
								
								//TODO Choose the best recycling center.
								cellRecyclingCenter = c;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}					
				}
			}
			
			r.setPerformative(ACLMessage.AGREE);
			//Cell cel = getGame().getCell(3, 4);
			try {
				r.setContentObject(cellRecyclingCenter);
			} catch (IOException e) {
				e.printStackTrace();
			}	
			System.out.println("Receive from harvester that distancelist, dist 1: "+dist.getDistances().get(0)+", dist 2: "+dist.getDistances().get(1)+"...");
			return r;
		}		
	}	
	
	public InfoGame getGame() {
		return game;
	}

	public void setGame(InfoGame game) {
		this.game = game;
	}
}

