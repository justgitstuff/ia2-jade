package sma.scout;

import sma.moves.MovementSender;
import sma.moves.Movement.Direction;
import sma.ontology.Cell;
import sma.ontology.InfoGame;
import sma.pathFinding.Path;
import sma.pathFinding.PathTest;
import jade.proto.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.core.Agent;

public class ProtocolContractNetResponder{
	private InfoGame infoGame;
	private Path short_path;
	private int my_x, my_y;
	private MovementSender ms;
	/**
	 * Receive a cell where content the material and the position where manager harvester want to go the harvester.
	 * @param Agent
	 * @param Cell
	 */
	
	public void setInfoGame(InfoGame infoGame){
		this.infoGame = infoGame;
	}
	
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
			ms = new MovementSender(myAgent,myAgent.getAID(),sma.UtilsAgents.SCOUT_MANAGER_AGENT);
			
		
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
			System.out.println("Scout: Receive from "+msg.getSender()+". Material:"+content.getGarbageType()+", x: "+content.getColumn()+", y: "+content.getRow());
			ACLMessage reply = msg.createReply();
			reply.setPerformative(ACLMessage.PROPOSE);
			//Or refuse or not-understood.
			//Content have a int with a distance.			
			distance = evaluateAction(content);
			reply.setContent(Integer.toString(distance));
			return reply;
		}
		
		/**Execute when receive Accept-proposal. The Parameters are CFP initial and the response (Propose)
		*Return Inform or Failure.
		*/
		protected ACLMessage prepareResultNotification (ACLMessage cfp, ACLMessage propose, ACLMessage accept)
		{
			ACLMessage inform = accept.createReply();
			//Your code.
			System.out.println("I am the scout "+this.myAgent.getName()+", received from "+accept.getSender()+" accepted my propouse: "+propose.getContent()+".");
			inform.setPerformative(ACLMessage.CONFIRM);
			ms.go(getNextStep());

			//Or failure.
			return inform;
		}
		
		/**
		 * Execute when the message is Reject-proposal.
		 */
		protected void handleRejectProposal (ACLMessage cfp, ACLMessage propose, ACLMessage reject)
		{
			System.out.println("I am the scout "+this.myAgent.getName()+". Refuse my propouse "+propose.getContent()+".");
		}  
		
		
		
		private int evaluateAction(Cell cell){
			
			int xfinal = cell.getColumn();
			int yfinal = cell.getRow();
			
			System.out.println("Destination Cell "+cell);
			
			//retornem el cami mes curt
			PathTest test = new PathTest(infoGame);
			
			//Em busco a mi mateix
			my_x=sma.UtilsAgents.findAgent(this.myAgent.getAID(), infoGame).getRow();
			my_y=sma.UtilsAgents.findAgent(this.myAgent.getAID(), infoGame).getColumn();
			
			System.out.println("Scout a "+ my_x+" "+my_y);
			
			
			
			// op1
			
			test.PosicioInicial(my_x,my_y,1); 
			Path stepsPathFinal1= test.PosicioFinal(xfinal,yfinal,1);
			

			
						
			// OPCIOOOOO 2
			test.PosicioInicial(my_x,my_y,2);
			Path stepsPathFinal2= test.PosicioFinal(xfinal,yfinal,2);
			
			int distFinal = test.distanciaPesos(stepsPathFinal2);
			short_path = stepsPathFinal2;
			
			//Mirem que hi ha un cami descobert possible de comunicació
			if(stepsPathFinal1!=null){
				
				int distPesosOp1= test.distanciaPesos(stepsPathFinal1);
						
				
				if(distFinal>distPesosOp1){
					distFinal=distPesosOp1;
					short_path = stepsPathFinal1;
				}
			}
			
			
			return distFinal;
		}
		
		
		
		
		private Direction getNextStep(){// com estan distribuits els index de la matriu del mapa??
			//Movement m = new Movement();
			int destination_x = short_path.getX(1);
			int destination_y = short_path.getY(1);
			
			if(my_x<destination_x){ 
				return Direction.RIGHT;	//m.setDirection(sma.moves.Movement.Direction.DOWN);}
			
			}else if(my_x>destination_x){ 
				return Direction.LEFT;//m.setDirection(sma.moves.Movement.Direction.UP);
			}
			
			else if(my_y<destination_y){ 
				 return Direction.DOWN;//m.setDirection(sma.moves.Movement.Direction.LEFT);
			}
			
			else if(my_y>destination_y){ 
				return Direction.UP;//m.setDirection(sma.moves.Movement.Direction.RIGHT);
			}
			
			return Direction.UP;
			
		}
	
	}
}