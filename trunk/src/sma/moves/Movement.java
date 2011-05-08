package sma.moves;

import java.io.Serializable;

import jade.core.AID;
import jade.core.Agent;

public class Movement implements Serializable {

	private static final long serialVersionUID = -2002385773495345696L;

	public enum Direction{
		UP,DOWN,LEFT,RIGHT;
	}
	 
	public enum Action{
		GO, PUT, GET;
	}
	
	public enum Type{
		METAL, PAPER, GLASS, PLASTIC;
		public boolean equals(char c)
		{
			switch (c)
			{
			case 'M': return this.equals(METAL);
			case 'P': return this.equals(PLASTIC);
			case 'G': return this.equals(GLASS);
			case 'A': return this.equals(PAPER);
			}
			return false;
		}
		
	}
	
	private AID agent;
	private Direction direction;
	private Action action;
	private Type type;
	
	public void setAgent(AID agent) {
		this.agent = agent;
	}
	public AID getAgent() {
		return agent;
	}
	/**
	 * Empty constructor, remember to initialize agent and order with its setters
	 */
	public Movement()
	{
		this.agent=null;
		this.action=null;
		this.type=null;
		this.direction=null;
	}
/**
 * Constructor for a movement order needs an agent and a Action type, example: Action.GO
 * Also the direction, example: Direction.UP
 * @param agent
 * @param action
 * @param direction
 */
	public Movement(AID agent, Action action, Direction direction)
	{
		this.agent=agent;
		this.action=action;
		this.type=null;
		this.direction=direction;
	}
	/**
	 * Constructor for a movement order of GET/PUT type 
	 * needs an agent and a Movement Action example: Action.GET
	 * you must specify a material type, example: Type.METAL
	 * Also the direction, example: Direction.UP
	 * @param agent
	 * @param action
	 * @param direction
	 * @param type
	 */
	public Movement(AID agent, Action action, Direction direction, Type type)
	{
		this.agent=agent;
		this.action=action;
		this.type=type;
		this.direction=direction;
	}
	
	
	public void setType(Type type) {
		this.type = type;
	}
	public Type getType() {
		return type;
	}
	public void setAction(Action action) {
		this.action = action;
	}
	public Action getAction() {
		return action;
	}
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	public Direction getDirection() {
		return direction;
	}
	
}
