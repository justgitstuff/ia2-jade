package sma.harvester;
import jade.core.*;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.proto.AchieveREInitiator;


public class SendFinishDownload{
	
	/**
	 * Send to manager harvester that all garbage is download in recycling center.
	 */
	public void addBehaviour(Agent agent){		
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
		
		/**
		 * Receive inform from the manager harvester, and the content is "Be_scout".
		 */
		@Override
		protected void handleInform(ACLMessage msg) {
			//
			if((msg.getProtocol().equals(sma.UtilsAgents.PROTOCOL_DOWNLOAD))&&(msg.getContent().equals(sma.UtilsAgents.BE_SCOUT))){
				System.out.println("Receive from Harvester Manager be_scout.");
			}else System.out.println("Error in protocol in download garbage, repeat it.");
			//Cal tornar a cridar el protocol load.				
		}
	}
}
