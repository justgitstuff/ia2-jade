package sma.harvester_manager;
import sma.ontology.Cell;
import jade.proto.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.core.Agent;

public class ProtocolContractNetResponder{
	/**
	 * Receive a cell where content the material and the position where manager harvester want to go the harvester.
	 * @param Agent
	 * @param Cell
	 */
	public void addBehaviour (Agent agent)
	{
		MessageTemplate mt1 = MessageTemplate.MatchProtocol(sma.UtilsAgents.CONTRACT_NET);
		MessageTemplate mt2 = MessageTemplate.MatchPerformative(ACLMessage.CFP);
		agent.addBehaviour(new ProtocolContractNetRes(agent,MessageTemplate.and(mt1, mt2)));
	}

	public class ProtocolContractNetRes extends ContractNetResponder{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
	
		public ProtocolContractNetRes (Agent myAgent, MessageTemplate mt)
		{
			super(myAgent, mt);
		}
		
		/**Execute when receive a CFP message and need return integer with distance and
		*(not-understood, refuse o propose).
		*/
		protected ACLMessage prepareResponse (ACLMessage msg)
		{	
			int distance;
			Cell content=null;
			try {
				content = (Cell) msg.getContentObject();
			} catch (UnreadableException e) {
				e.printStackTrace();
			}			
			ACLMessage reply = msg.createReply();
			reply.setPerformative(ACLMessage.PROPOSE);			
			//Content have a int with a distance.			
			distance=1;
			reply.setContent(Integer.toString(distance));
			return reply;
		}
		
		/**Execute when receive Accept-proposal. The Parameters are CFP initial and the response (Propose)
		*Return Inform or Failure.
		*/
		protected ACLMessage prepareResultNotification (ACLMessage cfp, ACLMessage propose, ACLMessage accept)
		{
			ACLMessage inform = accept.createReply();			
			System.out.println("I am the harvester "+this.myAgent.getName()+", received from "+accept.getSender()+" accepted my propouse: "+propose.getContent()+".");
			inform.setPerformative(ACLMessage.CONFIRM);
			return inform;
		}
		
		/**
		 * Execute when the message is Reject-proposal.
		 */
		protected void handleRejectProposal (ACLMessage cfp, ACLMessage propose, ACLMessage reject)
		{
			System.out.println("I am the harvester "+this.myAgent.getName()+". Refuse my propouse "+propose.getContent()+".");
		}
	}
	}