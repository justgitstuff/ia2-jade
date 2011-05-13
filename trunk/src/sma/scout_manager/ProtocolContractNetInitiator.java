package sma.scout_manager;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import sma.gui.Quadrant;
import sma.ontology.Cell;

public class ProtocolContractNetInitiator {
	
	/**
	 * Enter a cell where content the material and the position where I want to go the harvester.
	 * @param Agent
	 * @param Cell
	 */
	public void addBehaviour(Agent agent, Cell content) throws IOException
	{
		ACLMessage ms = new ACLMessage(ACLMessage.CFP);
		ms.setProtocol(sma.UtilsAgents.CONTRACT_NET);
		ms.setContentObject(content);
		ms.setSender(agent.getAID());		
		ms = FindReceivers(agent,ms);
		ProtocolContractNetInit protocol = new ProtocolContractNetInit(agent, ms);
		protocol.setTargetCell(content);
		agent.addBehaviour(protocol);
	}

	private ACLMessage FindReceivers(Agent agent, ACLMessage msg) {		
		ServiceDescription sd = new ServiceDescription();
		sd.setType(sma.UtilsAgents.SCOUT_AGENT);		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.addServices(sd);
		try{
			while(true) {
				SearchConstraints c = new SearchConstraints();
				c.setMaxResults(new Long(-1));
				DFAgentDescription[] result = DFService.search(agent, dfd, c);
				System.out.println("ContractNetInitiator: in search responders.");
				if(result.length > 0){
					int i = 0;
					int j = result.length;
					while (i<j){
						dfd = result[i];
						System.out.println("ContractNetInitiator: add receiver "+dfd.getName());
						msg.addReceiver(dfd.getName());
						i=i+1;					
					}
					break;
				}
				Thread.sleep(2000); /*Each 5 seconds we try to search*/
			}
		} catch(Exception fe) {
			fe.printStackTrace();
		     System.out.println( agent.getLocalName() + " search with DF is not succeeded because of " + fe.getMessage() );
		     agent.doDelete();
		}		
		return msg;
	}

	public class ProtocolContractNetInit extends ContractNetInitiator{

		private static final long serialVersionUID = 1L;
		private Cell targetCell = null;
		
		public ProtocolContractNetInit (Agent myAgent, ACLMessage msg)
		{
			super(myAgent, msg); 
		}
		
		public void setTargetCell(Cell targetCell) {
			this.targetCell = targetCell;
		}

		/**
		 * Executed when all responses have been collected or when the timeout is expired.
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		protected void handleAllResponses(Vector responses, Vector acceptances) {
			
			// Evaluate proposals.
			int bestProposal = -1;			
			ACLMessage accept = null;
			Enumeration e = responses.elements();
			while (e.hasMoreElements()) {
				ACLMessage msg = (ACLMessage) e.nextElement();
				if (msg.getPerformative() == ACLMessage.PROPOSE) {
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
					acceptances.addElement(reply);
					int proposal = Integer.parseInt(msg.getContent());
					
					// Check if the scout is in the cell of the target point
					AID sender = msg.getSender();
					Quadrant quadrant = ((ScoutManagerAgent) this.myAgent).getScoutsQuadrants().get(sender);
					if (targetCell.getRow() >= quadrant.x1 && targetCell.getRow() <= quadrant.x2
							&& targetCell.getColumn() > quadrant.y1 && targetCell.getColumn() < quadrant.y2) {
						if (proposal > bestProposal) {
							bestProposal = proposal;						
							accept = reply;
						}
					}
					
				}
			}
			// Accept the proposal of the best proposer
			if (accept != null) {
				System.out.println("Accepting proposal "+bestProposal);
				accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
			}				
		}
	
		protected void handleNotUnderstood (ACLMessage msg) {
			System.out.println("HandleNoutUnderstood");
		}
		protected void handleRefuse (ACLMessage msg) {
			System.out.println("handlerefuse");
		}
		protected void handleInform (ACLMessage msg) {
			System.out.println("Handleinform");
		}
		protected void handleFailure (ACLMessage msg){
			System.out.println("Handlefailure");
		}
	}
}