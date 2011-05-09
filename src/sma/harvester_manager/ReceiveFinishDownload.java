package sma.harvester_manager;
import jade.core.*;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.*;
import jade.proto.AchieveREResponder;

public class ReceiveFinishDownload{

	public void addBehaivour(Agent agent){		
		MessageTemplate mt1 = MessageTemplate.MatchProtocol(sma.UtilsAgents.PROTOCOL_DOWNLOAD);
		MessageTemplate mt2 = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		agent.addBehaviour(new RecieveFinishWork(agent,MessageTemplate.and(mt1, mt2)));
	}	
	
	public class RecieveFinishWork extends AchieveREResponder{	
		private static final long serialVersionUID = -9124135937363184173L;
	
		public RecieveFinishWork(Agent arg0, MessageTemplate arg1) {
			super(arg0, arg1);	
		}	
		
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
		/*public ReciveFinishDownload (Agent myAgent)
		{
			//Now wait from the harvester when he finish the download garbage in the recycling center.		
			MessageTemplate mt1 = MessageTemplate.MatchProtocol(sma.UtilsAgents.PROTOCOL_DOWNLOAD);
			MessageTemplate mt2 = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage msg=myAgent.blockingReceive(MessageTemplate.and(mt1, mt2));
			setContent(msg.getContent());						
			System.out.println("El harvester: "+msg.getSender()+", ha enviat el content: "+getContent()+".");		
			//If download it's ok.
			if (getContent().equals(sma.UtilsAgents.OK))
			{
				System.out.println("Harvester: "+msg.getSender()+", downloaded has successfully garbage.");
				//Send be_scout to the harvester.
				ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);
				msg2.setProtocol(sma.UtilsAgents.PROTOCOL_DOWNLOAD);
				msg2.setContent(sma.UtilsAgents.BE_SCOUT);
				msg2.setSender(myAgent.getAID());
				msg2.addReceiver(msg.getSender());
				myAgent.send(msg2);			
				
			}else if(getContent().equals(sma.UtilsAgents.FAILURE))
			{//else download failure.
				System.out.println("Harvester: "+msg.getSender()+", send failure download garbage.");
			}else System.out.println("Message from Harvester "+msg.getSender()+", failure in comunication protocol.");		
			
		}
	
		public String getContent() {
			return content;
		}
	
		public void setContent(String content) {
			this.content = content;
		}	*/
		
	}
}

