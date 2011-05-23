package sma.harvester_manager;
import jade.proto.*;
import jade.lang.acl.ACLMessage;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import sma.ontology.Cell;

public class ProtocolContractNetInitiator{	
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
		agent.addBehaviour(new ProtocolContractNetInit(agent, ms));
	}

	/**
	 * For find all harvesters agent. 
	 */
	private ACLMessage FindReceivers(Agent agent, ACLMessage msg) {		
		ServiceDescription sd = new ServiceDescription();
		sd.setType(sma.UtilsAgents.HARVESTER_AGENT);		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.addServices(sd);
		try{
			while(true) {
				SearchConstraints c = new SearchConstraints();
				c.setMaxResults(new Long(-1));
				DFAgentDescription[] result = DFService.search(agent, dfd, c);				
				if(result.length > 0){
					int i = 0;
					int j = result.length;
					while (i<j){
						dfd = result[i];
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

		public ProtocolContractNetInit (Agent myAgent, ACLMessage msg)
		{
			super(myAgent, msg); 
		}
		
		/**
		 * Executed when all responses have been collected or when the timeout is expired.
		 */
		@SuppressWarnings("unchecked")
		protected void handleAllResponses(Vector responses, Vector acceptances)
		{		
			// Evaluate proposals.
			int bestProposal = -1;
			int firstTime=0;
			ACLMessage accept = null;
			Enumeration e = responses.elements();
			while (e.hasMoreElements()) {
				ACLMessage msg = (ACLMessage) e.nextElement();
				if (msg.getPerformative() == ACLMessage.PROPOSE) {
					ACLMessage reply = msg.createReply();
					if (firstTime == 0){
						bestProposal = Integer.parseInt(msg.getContent());
						firstTime++;
						accept=reply;
					}
					reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
					acceptances.addElement(reply);
					int proposal = Integer.parseInt(msg.getContent());					
					if (proposal<bestProposal){
						bestProposal = proposal;
						accept=reply;
					}
				}
			}
			// Accept the proposal of the best proposer
			if (accept != null) {				
				accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
			}				
		}
	
		protected void handleNotUnderstood (ACLMessage msg) {
			//System.out.println("HandleNoutUnderstood");
		}
		protected void handleRefuse (ACLMessage msg) {
			//System.out.println("handlerefuse");
		}
		protected void handleInform (ACLMessage msg) {
			//System.out.println("Handleinform");
		}
		protected void handleFailure (ACLMessage msg){
			//System.out.println("Handlefailure");
		}
	}
}