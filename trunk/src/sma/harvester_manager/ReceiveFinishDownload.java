package sma.harvester_manager;
import jade.core.*;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.*;
import jade.proto.AchieveREResponder;

public class ReceiveFinishDownload{

	/**
	 * Receive from harvester that all garbage is download in recycling center, and content have "OK".
	 */
	public void addBehaviour(Agent agent){		
		MessageTemplate mt1 = MessageTemplate.MatchProtocol(sma.UtilsAgents.PROTOCOL_DOWNLOAD);
		MessageTemplate mt2 = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		agent.addBehaviour(new RecieveFinishWork(agent,MessageTemplate.and(mt1, mt2)));
	}	
	
	public class RecieveFinishWork extends AchieveREResponder{	
		private static final long serialVersionUID = -9124135937363184173L;
	
		public RecieveFinishWork(Agent arg0, MessageTemplate arg1) {
			super(arg0, arg1);	
		}	
		
		/**
		 * Execute when receive message. Receive ok from the harvester and response "Be_scout".
		 */
		@Override
		protected ACLMessage prepareResponse(ACLMessage arg0) throws NotUnderstoodException, RefuseException {
			ACLMessage r= arg0.createReply();
			
			if(arg0.getContent().equals(sma.UtilsAgents.OK))
			{
				r.setContent(sma.UtilsAgents.BE_SCOUT);				
			}
			if(arg0.getContent().equals(sma.UtilsAgents.FAILURE))
			{
				System.out.println("Harvester: "+arg0.getSender()+", send failure download garbage.");
			}
			return r;
		}		
	}
}

