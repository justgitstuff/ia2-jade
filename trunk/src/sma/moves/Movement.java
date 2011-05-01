package sma.moves;

import jade.core.Agent;

public class Movement {
	
	public enum Order{
		UP,DOWN,LEFT,RIGHT;
	}
	
	private Agent agent;
	private Order order;
	
	public Order getOrder() {
		return order;
	}
	public void setOrder(Order order) {
		this.order = order;
	}
	public void setAgent(Agent agent) {
		this.agent = agent;
	}
	public Agent getAgent() {
		return agent;
	}
	/**
	 * Empty constructor, remember to initialize agent and order with its setters
	 */
	public Movement()
	{
		this.agent=null;
		this.order=null;
	}
	/**
	 * Constructor for a movement order needs an agent and a Movement Order example: Order.UP
	 * @param agent
	 * @param order
	 */
	public Movement(Agent agent, Order order)
	{
		this.agent=agent;
		this.order=order;
	}
	
}
