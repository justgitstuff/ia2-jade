package sma.harvester_manager;
import jade.core.*;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.AchieveREInitiator;


public class SendFinishDownload{
	
	public void addBehaivour(Agent agent){		
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setProtocol(sma.UtilsAgents.PROTOCOL_DOWNLOAD);
		msg.setContent(sma.UtilsAgents.OK);
		msg.setSender(agent.getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(sma.UtilsAgents.HARVESTER_MANAGER_AGENT);
		msg.addReceiver(sma.UtilsAgents.searchAgent(agent, sd));
		agent.addBehaviour(new SendFinishWork(agent, msg));
	}

	public class SendFinishWork extends AchieveREInitiator{	
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
	
		public SendFinishWork(Agent arg0, ACLMessage arg1)
		{
			super(arg0, arg1);
		}
		
		@Override
		protected void handleInform(ACLMessage msg) {
			//
			if((msg.getProtocol().equals(sma.UtilsAgents.PROTOCOL_DOWNLOAD))&&(msg.getContent().equals(sma.UtilsAgents.BE_SCOUT)))
				System.out.println("Harvester Manager send to harvester be_scout.");
		}
	
	
		/*public SendFinishDownload(Agent myAgent, String content)
		{
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setProtocol(sma.UtilsAgents.PROTOCOL_DOWNLOAD);
			msg.setContent(content);
			msg.setSender(myAgent.getAID());
			msg.addReceiver(getIdReceiver());
			myAgent.send(msg);
			
			//Now wait for response of manager harvester.
			MessageTemplate mt1 = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			MessageTemplate mt2 = MessageTemplate.MatchProtocol(sma.UtilsAgents.PROTOCOL_DOWNLOAD);
			ACLMessage msg2 = myAgent.blockingReceive(MessageTemplate.and(mt1, mt2));
	
			//If all is correct the content is marked with "be_scout" and return this value. Else return "error".
			if (msg2.getContent().equals(sma.UtilsAgents.BE_SCOUT)) ;////////return msg2.getContent();
			else ;///////return "error";
			//end of this communication.
		}*/
	}
}
