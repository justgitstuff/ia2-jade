package sma.harvester;
import java.io.IOException;

import sma.harvester_manager.DistanceList;
import sma.ontology.Cell;
import jade.core.*;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.AchieveREInitiator;

public class SendFinishLoad{
	
	/**
	 * Send to manager harvester that all garbage is load, and content have all the distance of all recycling center (in one list).
	 */
	public void addBehaviour(Agent agent, DistanceList content) throws IOException{
		
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_REF);
		msg.setProtocol(sma.UtilsAgents.PROTOCOL_QUERY);
		msg.setContentObject(content);
		msg.setSender(agent.getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(sma.UtilsAgents.HARVESTER_MANAGER_AGENT);
		msg.addReceiver(sma.UtilsAgents.searchAgent(agent, sd));
		agent.addBehaviour(new SendFinishL(agent, msg));
	}
	
	public Cell blockingMessage(Agent agent, DistanceList content) throws IOException, UnreadableException
	{
		ACLMessage msg = new ACLMessage(ACLMessage.QUERY_REF);
		msg.setProtocol(sma.UtilsAgents.PROTOCOL_QUERY);
		msg.setContentObject(content);
		msg.setSender(agent.getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(sma.UtilsAgents.HARVESTER_MANAGER_AGENT);
		msg.addReceiver(sma.UtilsAgents.searchAgent(agent, sd));
		agent.send(msg);
		
		MessageTemplate mt1 = MessageTemplate.MatchPerformative(ACLMessage.AGREE);
		MessageTemplate mt2 = MessageTemplate.MatchProtocol(sma.UtilsAgents.PROTOCOL_QUERY);
		ACLMessage msg2 = agent.blockingReceive(MessageTemplate.and(mt1, mt2));
		Cell cell = (Cell) msg2.getContentObject();
		return cell;
		
		
	}
	
	public class SendFinishL extends AchieveREInitiator{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SendFinishL(Agent arg0, ACLMessage arg1){
			super(arg0, arg1);
		}
		
		/**
		 * Receive agree from the manager harvester, and the content is the cell of the recycling center
		 */
		@Override
		protected void handleAgree(ACLMessage msg) {
			sma.ontology.Cell cell = null;
			try {
				cell = (Cell) msg.getContentObject();
				// CODI PER DESCARREGAR
				// Guardar CELL DESTI
				// FICAR ESTAT DESCARGAR
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			System.out.println("Receive from HarvesterManager, cell x: "+cell.getColumn()+", cell y: "+cell.getRow());
		}

		/**
		 * Receive refuse from the manager harvester. The content is null.
		 */
		@Override
		protected void handleRefuse(ACLMessage arg0) {			
			System.out.println("From Harvester Manager: refuse in finish load.");
		}		
	}	
}

