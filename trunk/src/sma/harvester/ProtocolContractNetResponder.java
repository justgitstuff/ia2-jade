package sma.harvester;


import java.io.IOException;

import sma.harvester_manager.DistanceList;
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
	private boolean freeAgent=true;// true = free false= transport
	private InfoAgent infoAgent;
	private Cell goDescarga;
	Cell content=null;
	SendFinishLoad protocolSendFinishLoad;
	SendFinishDownload protocolSendFinishDownload;
	private boolean accepted;
	private boolean existGarbatge=false;
	private boolean existAgentGarbatge=false;
	private Agent myAgent;
		
	/**
	 * Control for each turn
	 * @param infoGame the infoGame to set
	 */
	public void setInfoGame(InfoGame infoGame) {
		this.infoGame = infoGame;
		accepted=false;
		existGarbatge=false;
		existAgentGarbatge=false;
		
		infoAgent=sma.UtilsAgents.findAgent(myAgent.getAID(), infoGame).getAgent();
		
		
				
		//Let's see if the harvester is full and then send a list of distances to points of recycling
		if (freeAgent)
			if( (infoAgent.getMaxUnits()== infoAgent.getUnits())){
				notifyFinishedLoad(infoGame);
			}

		
		
		Cell varAgent=sma.UtilsAgents.findAgent(this.myAgent.getAID(), infoGame);
		//Control that the harvester has garbage but hasn't garbage arround
		if(freeAgent)
			if((infoAgent.getUnits()>0))
					if(!sma.UtilsAgents.isGarbageArround(infoGame, infoAgent.getCurrentType(), varAgent))
					{
						notifyFinishedLoad(infoGame);
					}
		// look if the harvester have some unit of garbatge
			if (varAgent.isThereAnAgent()){
				if(varAgent.getAgent().getUnits()!=0)
					existAgentGarbatge=true;
			}
					
		
		//look that the game does not have more garbage of the same type that harvester can to catch
		for(int x=0;x<infoGame.getMap().length ;x++){
			for(int y=0;y<infoGame.getMap()[x].length ;y++)
			{
				Cell c=infoGame.getCell(x, y);
				try {
					if(c.getCellType()==Cell.BUILDING)
						if (c.getGarbageUnits()!=0){ 
						
							boolean tipusCarga=false;
												
								if(c.getGarbageType()=='G'){
									tipusCarga= infoAgent.getGarbageType()[0];					
								}
								else if(c.getGarbageType()=='P'){
									tipusCarga= infoAgent.getGarbageType()[1];					
								}
								else if(c.getGarbageType()=='M'){
									tipusCarga= infoAgent.getGarbageType()[2];					
								}else if(c.getGarbageType()=='A'){
									tipusCarga= infoAgent.getGarbageType()[3];					
								}
								
							if(tipusCarga)
								existGarbatge=true;
					
						
						}
				} catch (Exception e) {
					// Rarely will go here
				}
			}
		}
		// control if harvester is transport the garbage
		if(!freeAgent){
			
			Cell begin = new Cell(Cell.BUILDING);
			int distance = evaluateAction(content);
			begin.setColumn(my_x);
			begin.setRow(my_y);


			// look at the perimeter
			if(sma.UtilsAgents.cellDistance(begin, goDescarga)==1){
				
					try {
						if(infoAgent.getUnits()!=0){
							ms.put(getNextStepDesti(goDescarga),sma.moves.Movement.typeFromInt(infoAgent.getCurrentType()));
						}else{
							protocolSendFinishDownload.addBehaviour(myAgent);
							
							freeAgent= true;
							accepted=false;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}				
				
			}else{
				
				evaluateAction(goDescarga);
				ms.go(getNextStep());			
				
			} 	
						
			// Communicate that harvester have garbage if harvester isn't full but in the game haven't more garbage	
		}else if(existAgentGarbatge && !existGarbatge && freeAgent){		
			notifyFinishedLoad(infoGame);			
		}		
	}


	// Search the list of distances to points of recycling and send that I am full to know the site of recycling
	private void notifyFinishedLoad(InfoGame infoGame) {
		DistanceList list = new DistanceList();
		 
		for (int x=0;x<infoGame.getMap().length;x++){					
			for (int y=0; y<infoGame.getMap()[x].length;y++)
			{
				Cell c=infoGame.getCell(x,y);
				//if getGarbageunits is 0 -> no garbage.
				if (c!=null)
				{
					if(c.getCellType() == Cell.RECYCLING_CENTER)
					{
						list.addDistance(evaluateAction(c));
					}
				}
			}
		}
		
		
			try {
				try {
					//point of recycling
					goDescarga = protocolSendFinishLoad.blockingMessage(myAgent,list );								
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (UnreadableException e) {
				e.printStackTrace();
			}					
		
		freeAgent=false;
	}
	
	
	

	private InfoGame infoGame;
	/**
	 * Receive a cell where content the material and the position where manager harvester want to go the harvester.
	 * @param Agent
	 * @param Cell
	 */	
	public void addBehaviour (Agent agent)
	{	myAgent=agent;
		MessageTemplate mt1 = MessageTemplate.MatchProtocol(sma.UtilsAgents.CONTRACT_NET);
		MessageTemplate mt2 = MessageTemplate.MatchPerformative(ACLMessage.CFP);
		agent.addBehaviour(new ProtocolContractNetRes(agent,MessageTemplate.and(mt1, mt2)));
	}

	public class ProtocolContractNetRes extends ContractNetResponder{
		/**
		 * ProtocolContractNet
		 */
		private static final long serialVersionUID = 1L;
	
		public ProtocolContractNetRes (Agent myAgent, MessageTemplate mt)
		{	
			super(myAgent, mt);
			System.out.println("Harvester: into cnet constructor");
			ms = new MovementSender(myAgent, myAgent.getAID(),sma.UtilsAgents.HARVESTER_MANAGER_AGENT);
			protocolSendFinishLoad = new SendFinishLoad();
			protocolSendFinishDownload = new SendFinishDownload();
		}
		
		/**Execute when receive a CFP message and need return integer with distance and
		*(not-understood, refuse o propose).
		*/
		protected ACLMessage prepareResponse (ACLMessage msg)
		{	
			int distance;
			try {
				content = (Cell) msg.getContentObject();
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			ACLMessage reply = msg.createReply();
			//Or refuse or not-understood.
			
			
			//if already accepted a contract, refuse the new one
			if(accepted)
			{
				reply.setPerformative(ACLMessage.REFUSE);
				return reply;
			}
			
			infoAgent=sma.UtilsAgents.findAgent(myAgent.getAID(), infoGame).getAgent();

			//if i cannot carry that, refuse
			boolean canCarry=infoAgent.getGarbageType()[Cell.getGarbagePointsIndex(content.getGarbageType())];
			if(!canCarry)
			{
				reply.setPerformative(ACLMessage.REFUSE);
				return reply;
			}
			
			
			boolean canAccept=false;
			
			//check if this agent is also carrying garbage of different type
			if(infoAgent.getUnits()>0)
			{
				if(infoAgent.getCurrentType()!=Cell.getGarbagePointsIndex(content.getGarbageType()))
					{
						reply.setPerformative(ACLMessage.REFUSE);
						return reply;
					}else{
						canAccept=false;
					}
			}	
				
			if(freeAgent||canAccept)
			{
				if(infoAgent.getUnits()==infoAgent.getMaxUnits())
				{
					reply.setPerformative(ACLMessage.REFUSE);
					return reply;
				}else{
					distance=evaluateAction(content);
					
					if(distance==10000){
						reply.setPerformative(ACLMessage.REFUSE);
						return reply;
					}else{
						reply.setPerformative(ACLMessage.PROPOSE);
						reply.setContent(Integer.toString(distance));
						
						return reply;
					}
				}
			}
			
			reply.setPerformative(ACLMessage.REFUSE);
			return reply;			
		
		}
		
		
		
		/**Execute when receive Accept-proposal. The Parameters are CFP initial and the response (Propose)
		*Return Inform or Failure.
		*/
		protected ACLMessage prepareResultNotification (ACLMessage cfp, ACLMessage propose, ACLMessage accept)
		{
			accepted=true;freeAgent=true;
			ACLMessage inform = accept.createReply();
			//Your code.
			inform.setPerformative(ACLMessage.CONFIRM);
		
			Cell begin = new Cell(Cell.STREET);
			
			//Search my position
			int my_x=sma.UtilsAgents.findAgent(this.myAgent.getAID(), infoGame).getColumn();
			int my_y=sma.UtilsAgents.findAgent(this.myAgent.getAID(), infoGame).getRow();
			
			begin.setColumn(my_x);
			begin.setRow(my_y);
			
						
			// look at the perimeter
			if(sma.UtilsAgents.cellDistance(begin, content)==1){
				
				try {
					if(content.getGarbageUnits()>0){						
						ms.get(getNextStepDesti(content),sma.moves.Movement.typeFromInt(infoAgent.getCurrentType()));											
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else{
					ms.go(getNextStep());				
			} 
			
			
		return inform;
		}
		
		
		
		
		/**
		 * Execute when the message is Reject-proposal.
		 */
		protected void handleRejectProposal (ACLMessage cfp, ACLMessage propose, ACLMessage reject)
		{
			//PROPOSAL REJECTED
		}
	}
	
	

	/**
	 * 
	 * @return the next Direction where harvester have go, this Direction is calculated in short Path
	 */
	private Direction getNextStep(){
		int destination_x = short_path.getX(1);
		int destination_y = short_path.getY(1);
		
		
		if(my_x<destination_x && my_y==destination_y){ 
			return Direction.RIGHT;	
		
		}if(my_x>destination_x && my_y==destination_y){ 
			return Direction.LEFT;
		}			
		if(my_y<destination_y && my_x==destination_x){ 
			 return Direction.DOWN;
		}			
		if(my_y>destination_y && my_x==destination_x){ 
			return Direction.UP;
		}
		if(my_y>destination_y && my_x>destination_x){ 
			return Direction.UPLEFT;
		}
		if(my_y<destination_y && my_x<destination_x){ 
			return Direction.DOWNRIGHT;
		}
					
		if(my_x<destination_x && my_y>destination_y){ 
			return Direction.UPRIGHT;
		}
		if(my_x>destination_x && my_y<destination_y){ 
			return Direction.DOWNLEFT;
		}
		
		return Direction.UP;
		
	}
	
	
	
	
	/**
	 * 
	 * @param dest 
	 * @return the next Direction where harvester have go 
	 */
	private Direction getNextStepDesti(Cell dest){
		int destination_x = dest.getColumn();
		int destination_y = dest.getRow();
		
		
		if(my_x<destination_x && my_y==destination_y){ 
			return Direction.RIGHT;	
		
		}if(my_x>destination_x && my_y==destination_y){ 
			return Direction.LEFT;
		}			
		if(my_y<destination_y && my_x==destination_x){ 
			 return Direction.DOWN;
		}			
		if(my_y>destination_y && my_x==destination_x){ 
			return Direction.UP;
		}
		if(my_y>destination_y && my_x>destination_x){ 
			return Direction.UPLEFT;
		}
		if(my_y<destination_y && my_x<destination_x){ 
			return Direction.DOWNRIGHT;
		}
					
		if(my_x<destination_x && my_y>destination_y){ 
			return Direction.UPRIGHT;
		}
		if(my_x>destination_x && my_y<destination_y){ 
			return Direction.DOWNLEFT;
		}
		
		return Direction.UP;
		
	}
	
	/**
	 * 
	 * @param cell
	 * @return the best distance for harvester to go a one cell
	 */
	private int evaluateAction(Cell cell){
		
		int xfinal = cell.getColumn();
		int yfinal = cell.getRow();
		
		//return the short path
		PathTest test = new PathTest(infoGame);
		
		//Search my position
		my_x=sma.UtilsAgents.findAgent(this.myAgent.getAID(), infoGame).getColumn();
		my_y=sma.UtilsAgents.findAgent(this.myAgent.getAID(), infoGame).getRow();
		
	
		// option 1			
		test.PosicioInicial(my_x,my_y,1); 
		Path stepsPathFinal1= test.PosicioFinal(xfinal,yfinal,1);

		
					
		// option 2
		test.PosicioInicial(my_x,my_y,2);
		Path stepsPathFinal2= test.PosicioFinal(xfinal,yfinal,2);
		int distFinal=10000;
		//Control if possible the second option
		if(stepsPathFinal2!=null){
			distFinal = test.distanciaPesos(stepsPathFinal2);
			short_path = stepsPathFinal2;
		
			
			//Control if possible the first option
			if(stepsPathFinal1!=null){
				
				int distPesosOp1= test.distanciaPesos(stepsPathFinal1);
				if(distFinal>distPesosOp1){
					distFinal=distPesosOp1;
					short_path = stepsPathFinal1;
				}
			}		
		}
		
		return distFinal;
	}
	
	
	
	}