package sma.harvester_manager;

import sma.UtilsAgents;
import sma.moves.Movement;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;

public class MovementRely{

	AID destinationAgent;
	/**
	 * Adds a behavior to  relay movement orders
	 * @param agent - the agent to add the behavior to
	 * @param destination - the agent to rely the orders to. Example: sma.UtilsAgents.COORDINATOR_AGENT
	 */
	public void addBehavior(Agent agent, String destination)
	{
		
	    // we search the Coordinator Agent
	    ServiceDescription searchCriterion = new ServiceDescription();
	    searchCriterion.setType(destination);
	    this.destinationAgent = UtilsAgents.searchAgent(agent, searchCriterion);
		
	    // Add a behavior to receive movement orders from my agents
	    MessageTemplate mt= MessageTemplate.MatchProtocol(sma.UtilsAgents.PROTOCOL_MOVEMENT);
		agent.addBehaviour(new MessageReceiver(agent,mt));

	}
	

	private class MessageReceiver extends AchieveREResponder
	{
		@Override
		protected ACLMessage prepareResultNotification(ACLMessage arg0,
				ACLMessage arg1) throws FailureException {
			// TODO Auto-generated method stub
			return null;
		}

		private static final long serialVersionUID = -3474714465765086672L;

		public MessageReceiver(Agent arg0, MessageTemplate arg1) {
			super(arg0, arg1);
		}

		@Override
		protected ACLMessage prepareResponse(ACLMessage arg0)
				throws NotUnderstoodException, RefuseException {
			ACLMessage response = arg0.createReply();
			try {
				if(arg0.getContentObject() instanceof Movement)
				{
					response.setPerformative(ACLMessage.AGREE);
					// Send the message to the coordinator
					ACLMessage requestInicial = new ACLMessage(ACLMessage.REQUEST);
				    requestInicial.clearAllReceiver();
				    requestInicial.addReceiver(destinationAgent);
				    requestInicial.setProtocol(sma.UtilsAgents.PROTOCOL_MOVEMENT);
				    try {
				      requestInicial.setContentObject(arg0.getContentObject());
				    } catch (Exception e) {
				      e.printStackTrace();
				    }
				    //we add a behavior that sends the message and waits for an answer
				    this.myAgent.addBehaviour(new MessageSender(this.myAgent, requestInicial));
				}else{
					response.setPerformative(ACLMessage.NOT_UNDERSTOOD);
				}
			} catch (UnreadableException e) {
				response.setPerformative(ACLMessage.FAILURE);
				e.printStackTrace();
			}
			return response;
		}
		
	}
	
	private class MessageSender extends AchieveREInitiator
	{
		private static final long serialVersionUID = -702843887974362912L;

		public MessageSender(Agent arg0, ACLMessage arg1) {
			super(arg0, arg1);
		}
		
	}

}
