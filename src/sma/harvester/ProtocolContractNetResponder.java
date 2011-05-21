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
	private boolean myState=true;// true = lliure false= transportar
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
	 * @param infoGame the infoGame to set
	 */
	public void setInfoGame(InfoGame infoGame) {
		this.infoGame = infoGame;
		accepted=false;
		

	
		accepted=false;
		existGarbatge=false;
		existAgentGarbatge=false;
		
		infoAgent=sma.UtilsAgents.findAgent(myAgent.getAID(), infoGame).getAgent();
		
		/**
		 * FALTA CONTROLAR LA RECOLECTA DELS DIFERENTS TIPUS DE BROSAAAAA
		 * EN LA ITERACIO FINAL MIRAR NOMES AMB BROSA QUE PUC RECOLLIR
		 * EN EL CONTRACT NET REBUTJAR SINO PUC ANAR A BUSCAR
		 * 
		 */
		
		
				
		// Mirar si estic ple envio finish load per anar a descarregar això de forma dinamica durant l'execuccio
		
		if( infoAgent.getMaxUnits()== infoAgent.getUnits()){
			
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
						goDescarga = protocolSendFinishLoad.blockingMessage(myAgent,list );								
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (UnreadableException e) {
					e.printStackTrace();
				}					
			
			myState=false;
			
		}
		
				
				
		
		//LA ULTIMA ITERACIO en el cas que no hi ha mes brosa a recollir
		for(int x=0;x<infoGame.getMap().length ;x++){
			for(int y=0;y<infoGame.getMap()[x].length ;y++)
			{
				Cell c=infoGame.getCell(x, y);
				try {
					if(c.getCellType()==Cell.BUILDING)
						if (c.getGarbageUnits()!=0) existGarbatge=true;
					
					if (c.isThereAnAgent())
						if(c.getAgent().getUnits()!=0)
							existAgentGarbatge=true;
					
					
					
				} catch (Exception e) {
					// Rarely will go here
				}
			}
		}
		// No existeix més brosa al mapa y ya s'ha enviat previament l'ordre de finishload per descarregar
		if(existAgentGarbatge && !existGarbatge && !myState){
			
			Cell begin = new Cell(Cell.BUILDING);
			int distance = evaluateAction(content);
			begin.setColumn(my_x);
			begin.setRow(my_y);
			


			// retorna 1 si sta al perimetre, llavors descarga
			if(sma.UtilsAgents.cellDistance(begin, goDescarga)==1){
				
					try {
						if(infoAgent.getUnits()!=0){
							ms.put(getNextStepDesti(goDescarga),sma.moves.Movement.typeFromInt(infoAgent.getCurrentType()));
						}else{
							// ENVIAR K STIK DESCARREGAT
							// sendFInishDOwnlLOAD
							protocolSendFinishDownload.addBehaviour(myAgent);
							
							myState= true;
							accepted=false;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				
			}else{// decisio mourem
				
				evaluateAction(goDescarga);
				ms.go(getNextStep());
				
				
			} 	
			
			
		// NOTIFICAR SEND FINISH LOAD quan e recollit la brosa final del mapa encara k no estigui ple	
		}else if(existAgentGarbatge && !existGarbatge && myState){
			

			
		
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
						goDescarga = protocolSendFinishLoad.blockingMessage(myAgent,list );								
						
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (UnreadableException e) {
					e.printStackTrace();
				}					
			
			myState=false;
			
		
			
			
		}
		
		
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
		 * 
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
			//Cell content=null;
			try {
				content = (Cell) msg.getContentObject();
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			System.out.println("Harvester: Receive from "+msg.getSender()+". Material:"+content.getGarbageType()+", x: "+content.getColumn()+", y: "+content.getRow());
			ACLMessage reply = msg.createReply();
			//Or refuse or not-understood.
			
			infoAgent=sma.UtilsAgents.findAgent(myAgent.getAID(), infoGame).getAgent();
			//	if((myState)&&(!accepted)){	
			System.out.println("MySTATE  "+ myState + "Accepted" + accepted+ "OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
			
			//for(int x=0;x<infoAgent.getGarbageType().length;x++){
		
			boolean tipusCarga=false;
				if(content.getGarbageType()=='G'){
					tipusCarga= infoAgent.getGarbageType()[0];					
				}
				else if(content.getGarbageType()=='P'){
					tipusCarga= infoAgent.getGarbageType()[1];					
				}
				else if(content.getGarbageType()=='M'){
					tipusCarga= infoAgent.getGarbageType()[2];					
				}else if(content.getGarbageType()=='A'){
					tipusCarga= infoAgent.getGarbageType()[3];					
				}
				
			//}
			
			if((myState)&&(!accepted)){				
				//Content have a int with a distance.
				
				distance=evaluateAction(content);
				
				if(distance==10000){
					reply.setPerformative(ACLMessage.REFUSE);
				}else{
					reply.setPerformative(ACLMessage.PROPOSE);
					reply.setContent(Integer.toString(distance));					
				}
				//TODO mirar si puk karregar akest tipus de brosa(harvest pot rekullir akest tipus)
			
			}else if((myState)&&(accepted)){
				
				reply.setPerformative(ACLMessage.REFUSE);
					
				
				
			}else{
				
				reply.setPerformative(ACLMessage.REFUSE);
				Cell begin = new Cell(Cell.BUILDING);
				distance=evaluateAction(content);
				begin.setColumn(my_x);
				begin.setRow(my_y);
				


				// retorna 1 si sta al perimetre, llavors descarga
				if(sma.UtilsAgents.cellDistance(begin, goDescarga)==1){
					
						try {
							if(infoAgent.getUnits()!=0){
								ms.put(getNextStepDesti(goDescarga),sma.moves.Movement.typeFromInt(infoAgent.getCurrentType()));
							}else{
								// ENVIAR K STIK DESCARREGAT
								// sendFInishDOwnlLOAD
								protocolSendFinishDownload.addBehaviour(myAgent);
								
								myState= true;
								accepted=false;
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
					
				}else{// decisio mourem
					
					evaluateAction(goDescarga);
					ms.go(getNextStep());
					
					
				} 					
			}			
			return reply;
		}
		
		
		
		
		
		
		
		
		/**Execute when receive Accept-proposal. The Parameters are CFP initial and the response (Propose)
		*Return Inform or Failure.
		*/
		protected ACLMessage prepareResultNotification (ACLMessage cfp, ACLMessage propose, ACLMessage accept)
		{
			accepted=true;
			ACLMessage inform = accept.createReply();
			//Your code.
			System.out.println("I am the harvester "+this.myAgent.getName()+", received from "+accept.getSender()+" accepted my propouse: "+propose.getContent()+".");
			inform.setPerformative(ACLMessage.CONFIRM);
		
			Cell begin = new Cell(Cell.STREET);
			
			//Em busco a mi mateix
			int my_x=sma.UtilsAgents.findAgent(this.myAgent.getAID(), infoGame).getColumn();
			int my_y=sma.UtilsAgents.findAgent(this.myAgent.getAID(), infoGame).getRow();
			
			begin.setColumn(my_x);
			begin.setRow(my_y);
			
						
			// retorna 1 si sta al perimetre, llavors descarga
			if(sma.UtilsAgents.cellDistance(begin, content)==1){
				
				try {
					//Haurà de ser >0 units
					if(content.getGarbageUnits()>0){
						
						ms.get(getNextStepDesti(content),sma.moves.Movement.typeFromInt(infoAgent.getCurrentType()));
						// part extreta a control a cada iteració setinfo
						
						
						}
					/*
					else{ // ultim garbatge
							// TREURE DEL CONTRACT NETTTT
							ms.get(getNextStepDesti(content),sma.moves.Movement.typeFromInt(infoAgent.getCurrentType()));
							
							// NOTIFICAR SEND FINISH LOAD
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
										goDescarga = protocolSendFinishLoad.blockingMessage(myAgent,list );								
										
									} catch (IOException e) {
										e.printStackTrace();
									}
								} catch (UnreadableException e) {
									e.printStackTrace();
								}					
							
							myState=false;
							
						}*/
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else{// decisio mourem
					ms.go(getNextStep());				
			} 
			
			
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
	
	

	
	private Direction getNextStep(){
		int destination_x = short_path.getX(1);
		int destination_y = short_path.getY(1);
		
		System.out.println("From "+my_x+" "+my_y+" to "+ destination_x + " "+ destination_y );
		
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
	
	
	
	
	
	private Direction getNextStepDesti(Cell dest){
		int destination_x = dest.getColumn();
		int destination_y = dest.getRow();
		
		System.out.println("From "+my_x+" "+my_y+" to "+ destination_x + " "+ destination_y );
		
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
	
	
	private int evaluateAction(Cell cell){
		
		int xfinal = cell.getColumn();
		int yfinal = cell.getRow();
		
		//System.out.println("Destination Cell "+cell);
		
		//retornem el cami mes curt
		PathTest test = new PathTest(infoGame);
		
		//Em busco a mi mateix
		my_x=sma.UtilsAgents.findAgent(this.myAgent.getAID(), infoGame).getColumn();
		my_y=sma.UtilsAgents.findAgent(this.myAgent.getAID(), infoGame).getRow();
		
		//System.out.println("Finding path from "+ my_x+" "+my_y+" to "+xfinal+" "+yfinal);
		
		
		
		// op1
		
		test.PosicioInicial(my_x,my_y,1); 
		Path stepsPathFinal1= test.PosicioFinal(xfinal,yfinal,1);

		
					
		// OPCIOOOOO 2
		test.PosicioInicial(my_x,my_y,2);
		Path stepsPathFinal2= test.PosicioFinal(xfinal,yfinal,2);
		int distFinal=10000;
		if(stepsPathFinal2!=null){
			distFinal = test.distanciaPesos(stepsPathFinal2);
			short_path = stepsPathFinal2;
		
			//Mirem que hi ha un cami descobert possible de comunicaciÃ³
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