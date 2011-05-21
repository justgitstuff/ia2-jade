package sma.harvester_manager;
import java.io.IOException;
import sma.ontology.Cell;
import sma.ontology.InfoAgent;
import sma.ontology.InfoGame;
import jade.core.*;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.*;
import jade.proto.AchieveREResponder;

public class ReceiveFinishLoad{
	private InfoGame game;
	
	/**
	 * Receive from harvester that all garbage is load, and content have all the distance of all recycling center (in one list).
	 */
	public void addBehaviour(Agent agent){
		MessageTemplate mt1 = MessageTemplate.MatchProtocol(sma.UtilsAgents.PROTOCOL_QUERY);
		MessageTemplate mt2 = MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF);
		agent.addBehaviour(new RecieveFinishL(agent,MessageTemplate.and(mt1, mt2)));
	}
	
	public class RecieveFinishL extends AchieveREResponder{		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public RecieveFinishL(Agent arg0, MessageTemplate arg1){
			super(arg0, arg1);
		}
		
		/**
		 * Execute when receive message. Receive all the distance from recycling centers in DistanceList and return cell of recycling center.
		 */
		@Override
		protected ACLMessage prepareResponse(ACLMessage arg0) throws NotUnderstoodException, RefuseException {
			DistanceList dist=null;			
			ACLMessage r= arg0.createReply();
			Cell cellRecyclingCenter=null;
			Cell temp = null;
			int pos = 0;
			int distancia;
			int pointsChoose=0;
			int distanciaMinima=0;
			try {
				dist = (DistanceList) arg0.getContentObject();
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			
			//In harvester have the harvester who send that FinishLoad.
			InfoAgent harvester = sma.UtilsAgents.findAgent(arg0.getSender(), game).getAgent();
			//index have the type of garbage from harvester.
			int index = harvester.getCurrentType();
			
			//In dist have all the distance to recycling center.
			//Choose the cell that i decided and return that cell.
			//For each recycling center see the distance.
			for (int x=0;x<game.getMap().length;x++)
			{					
				for (int y=0; y<game.getMap()[x].length;y++)
				{
					Cell c=game.getCell(x,y);
					if (c!=null)
					{
						//if getGarbageunits is 0 -> no garbage.
						if(c.getCellType() == Cell.RECYCLING_CENTER)
						{							
							if (dist.getDistances().isEmpty()) System.out.println("Harvester Manager: Caution, list of distance recycling center is empty.");
							//In distancia have integer with distance between harvester-recyclingCenter.
							distancia = dist.getDistances().get(pos);
							pos++;						
							try {
								//In c.getGarbagePoints()[index] have points to drop material "index" in that recycling center.
								//If is zero means that recycling center no accept that type of garbage.
								int points=c.getGarbagePoints()[index];
								if (points>0){
									temp = c;
									//In first time actualizing values.
									if (cellRecyclingCenter==null){
										cellRecyclingCenter = temp;
										distanciaMinima = distancia;
										pointsChoose=points;
									}																		
									//temp have the recycling center of that cell. cellRecyclingCenter is the best option.
									//If the current distance is less than distance choose before...
									if(distancia < distanciaMinima){
										//If recycling center previously chosen give less points that 2*current points, or
										//the current distance is less than distance previously chosen /2. 
										if ((pointsChoose<2*points)||(distancia<distanciaMinima/2)){
											cellRecyclingCenter = temp;
											distanciaMinima=distancia;
											pointsChoose=points;
										}
									}else{										
										//If current distance is longer than previously chosen,
										//and have more than 2*points previously chosen, but current distance not is longer than double distance previously chosen...
										if((points>2*pointsChoose)&&(distanciaMinima*2>distancia))
										{
											cellRecyclingCenter = temp;
											distanciaMinima=distancia;
											pointsChoose=points;
										}
									}									
								}
							} catch (Exception e) {
								e.printStackTrace();
							}						
						}
					}
				}
			}
			
			if (cellRecyclingCenter == null) System.out.println("Harvester Manager: Not found any recycling center for "+index+".");			
			r.setPerformative(ACLMessage.AGREE);
			try {
				r.setContentObject(cellRecyclingCenter);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return r;
		}		
	}	
	
	public InfoGame getGame() {
		return game;
	}

	public void setGame(InfoGame game) {
		this.game = game;
	}
}

