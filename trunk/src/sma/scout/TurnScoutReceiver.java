package sma.scout;

import sma.ontology.InfoGame;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREResponder;

public class TurnScoutReceiver {
	/**
	 * Adds a behavior to receive new Turn notifications
	 * @param agent - the agent to add the behavior to
	 */
	public void addBehavior(Agent agent)
	{
	    MessageTemplate mt= MessageTemplate.MatchProtocol(sma.UtilsAgents.PROTOCOL_TURN);
		agent.addBehaviour(new MessageReceiver(agent,mt));
	}
	
	class MessageReceiver extends AchieveREResponder
	{
		@Override
		protected ACLMessage prepareResponse(ACLMessage arg0)
				throws NotUnderstoodException, RefuseException {
			ACLMessage response=arg0.createReply();
			
			try {
				if(arg0.getContentObject() instanceof InfoGame)
				{
					response.setPerformative(ACLMessage.AGREE);
					//TODO you have the new game info on arg0.getContentObject()
				}else{
					throw new NotUnderstoodException("Not the expected object type");
				}
			} catch (UnreadableException e) {
				response.setPerformative(ACLMessage.FAILURE);
			}
			
			return response;
		}

		private static final long serialVersionUID = -2066908850596603472L;

		public MessageReceiver(Agent arg0, MessageTemplate arg1) {
			super(arg0, arg1);
		}
		
	}
}
