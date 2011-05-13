package sma.harvester;


import sma.moves.Movement.Direction;
import sma.moves.MovementSender;
import sma.ontology.Cell;
import sma.ontology.InfoAgent;
import sma.ontology.InfoGame;
import sma.pathFinding.Path;
import sma.pathFinding.PathTest;
import jade.proto.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.core.Agent;

public class ProtocolContractNetResponder{
	private Path short_path;
	private int my_x, my_y;
	private MovementSender ms;
	private boolean myState=true;// true = lliure false= transportar
	private InfoAgent infoAgent;
	private Cell endDescarga;
	
	
	
	
	/**
	 * @param infoGame the infoGame to set
	 */
	public void setInfoGame(InfoGame infoGame) {
		this.infoGame = infoGame;
	}

	private InfoGame infoGame;
	/**
	 * Receive a cell where content the material and the position where manager harvester want to go the harvester.
	 * @param Agent
	 * @param Cell
	 */	
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
			System.out.println("Harvester: into cnet constructor");
			ms = new MovementSender(myAgent, myAgent.getAID(),sma.UtilsAgents.HARVESTER_MANAGER_AGENT);
			
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
			System.out.println("Harvester: Receive from "+msg.getSender()+". Material:"+content.getGarbageType()+", x: "+content.getColumn()+", y: "+content.getRow());
			ACLMessage reply = msg.createReply();
			//Or refuse or not-understood.
			
			infoAgent=sma.UtilsAgents.findAgent(myAgent.getAID(), infoGame).getAgent();
			
			if(myState){				
				//Content have a int with a distance.
				reply.setPerformative(ACLMessage.PROPOSE);
				distance=evaluateAction(content);
				System.out.println("He calculat distancia "+ distance);
				reply.setContent(Integer.toString(distance));
				//TODO mirar si puk karregar akest tipus de brosa
				
			}else{
				reply.setPerformative(ACLMessage.REFUSE);
				// S'acaba comunicacio
				//controalr si moviment reciclatege o descarrega
				//distance= evaluateAction(content);// Content cambiar per una cell pos basura
				
				Cell begin = new Cell(Cell.BUILDING);
				//Em busco a mi mateix
				my_x=sma.UtilsAgents.findAgent(this.myAgent.getAID(), infoGame).getRow();
				my_y=sma.UtilsAgents.findAgent(this.myAgent.getAID(), infoGame).getColumn();
				
				begin.setColumn(my_y);
				begin.setRow(my_x);
				
				// retorna 1 si sta al perimetre, llavors descarga
				if(sma.UtilsAgents.cellDistance(begin, endDescarga)==1){
					
					ms.put(getNextStep(), TypeFromInt(infoAgent.getCurrentType()));

					
				}else{// decisio mourem
					
					evaluateAction(endDescarga);
					ms.go(getNextStep());
					
					
				}
				
				// o notificar lliure si es el cas cambia estat
				
				
			}
		//	}
			
			
			
			
			return reply;
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
			
			System.out.println("Harvester a "+ my_x+" "+my_y);
			
			
			
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
			
			if(my_x<destination_x && my_y==destination_y){ 
				return Direction.RIGHT;	
			
			}else if(my_x>destination_x && my_y==destination_y){ 
				return Direction.LEFT;
			}			
			else if(my_y<destination_y && my_x==destination_x){ 
				 return Direction.DOWN;
			}			
			else if(my_y>destination_y && my_x==destination_x){ 
				return Direction.UP;
			}
			else if(my_y>destination_y && my_x>destination_x){ 
				return Direction.UPLEFT;
			}
			else if(my_y<destination_y && my_x<destination_x){ 
				return Direction.DOWNRIGHT;
			}
						
			else if(my_x<destination_x && my_y>destination_y){ 
				return Direction.UPRIGHT;
			}
			else if(my_x>destination_x && my_y<destination_y){ 
				return Direction.DOWNLEFT;
			}
			
			return Direction.UP;
			
		}
		
		
		
		
		
		
		/**Execute when receive Accept-proposal. The Parameters are CFP initial and the response (Propose)
		*Return Inform or Failure.
		*/
		protected ACLMessage prepareResultNotification (ACLMessage cfp, ACLMessage propose, ACLMessage accept)
		{
			ACLMessage inform = accept.createReply();
			//Your code.
			System.out.println("I am the harvester "+this.myAgent.getName()+", received from "+accept.getSender()+" accepted my propouse: "+propose.getContent()+".");
			inform.setPerformative(ACLMessage.CONFIRM);
			
			// aixo suicceeix kuan macepta la distancia i miro si simplement em desplazo, 
			// o stik al voltant i llavors carrego si es el cas i no faig ms
			// i si akao de rekollo enviao sendFInishLoad( dic posteriroment les dist amb tots els reciclatges)
			
			// controlat
			
			//this.myAgent.
			//if(stikcargant || stikbuidant){
			//	reply.setPerformative(ACLMessage.REFUSE);
			//}else{
			
			// 
			ms.go(getNextStep());
			
			//Stik carregant
			//ms.get(d, t);
			
			//Or failure.
			return inform;
		}
		
		/**
		 * Execute when the message is Reject-proposal.
		 */
		protected void handleRejectProposal (ACLMessage cfp, ACLMessage propose, ACLMessage reject)
		{
			System.out.println("I am the harvester "+this.myAgent.getName()+". Refuse my propouse "+propose.getContent()+".");
		}
	}
	}