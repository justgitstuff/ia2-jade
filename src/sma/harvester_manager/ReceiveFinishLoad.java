package sma.harvester_manager;
import java.io.IOException;

import sma.ontology.Cell;
import sma.ontology.InfoGame;
import jade.core.*;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.*;
import jade.proto.AchieveREResponder;

public class ReceiveFinishLoad{
	InfoGame game;
	
	/**
	 * Receive from harvester that all garbage is load, and content have all the distance of all recycling center (in one list).
	 */
	public void addBehaviour(Agent agent, InfoGame games){
		this.game=games;
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
			try {
				dist = (DistanceList) arg0.getContentObject();
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			//In dist have all the distance to recycling center.
			//Choose the cell that i decided and return that cell.
			r.setPerformative(ACLMessage.AGREE);
			Cell cel = game.getCell(3, 4);
			try {
				r.setContentObject(cel);
			} catch (IOException e) {
				e.printStackTrace();
			}	
			System.out.println("Receive from harvester that distancelist, dist 1: "+dist.getDistances().get(0)+", dist 2: "+dist.getDistances().get(1)+"...");
			return r;
		}		
	}	
}

