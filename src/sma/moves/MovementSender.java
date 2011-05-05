package sma.moves;

import sma.UtilsAgents;
import sma.moves.Movement.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPANames.InteractionProtocol;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

public class MovementSender{

	Agent my;
	AID destination;
	
	/**
	 * Implements sending turn decisions to a higher Agent
	 * @param agent - the agent that wants to move
	 * @param destination - the type of agent to send it to. Example: UtilsAgents.COORDINATOR_AGENT
	 */
	public MovementSender(Agent agent, String destination)
	{
		this.my=agent;
		
		   // Search for destination Agent
		   ServiceDescription searchCriterion = new ServiceDescription();
		   searchCriterion.setType(destination);
		   this.destination = UtilsAgents.searchAgent(agent, searchCriterion);
		   // searchAgent is a blocking method, so we will obtain always a correct AID
	}
	/**
	 * Sends a movement order
	 * @param d - direction to move, example: Direction.UP
	 */
	public void go(Movement.Direction d)
	{
	    Movement m=new Movement(my,Action.GO,d);
		send(m);
	}
	/**
	 * Sends a get trash order
	 * @param d - direction of the trash, example: Direction.UP
	 * @param t - type of trash to get, example: Type.METAL
	 */
	public void get(Movement.Direction d, Movement.Type t)
	{
		Movement m=new Movement(my,Action.GET,d,t);
		send(m);
	}
	/**
	 * Sends a put trash order
	 * @param d - direction of the trash, example: Direction.UP
	 * @param t - type of trash to put, example: Type.METAL
	 */
	public void put(Movement.Direction d, Movement.Type t)
	{
		Movement m=new Movement(my,Action.PUT,d,t);
		send(m);
	}

	private void send(Movement m) {
		//Send the message
	    ACLMessage requestInicial = new ACLMessage(ACLMessage.REQUEST);
	    requestInicial.clearAllReceiver();
	    requestInicial.addReceiver(destination);
	    requestInicial.setProtocol(InteractionProtocol.FIPA_QUERY);
	    

	    
	    try {
	      requestInicial.setContentObject(m);
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    //we add a behavior that sends the message and waits for an answer
		my.addBehaviour(new MessageSender(my,requestInicial));
	}

	class MessageSender extends AchieveREInitiator
	{
		private static final long serialVersionUID = 7709846829251324278L;

		public MessageSender(Agent arg0, ACLMessage arg1) {
			super(arg0, arg1);
		}
	}
	
}
