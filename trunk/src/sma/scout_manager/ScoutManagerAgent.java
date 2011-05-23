package sma.scout_manager;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.AchieveREResponder;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sma.UtilsAgents;
import sma.gui.Quadrant;
import sma.ontology.Cell;
import sma.ontology.InfoAgent;
import sma.ontology.InfoGame;


/**
 * Manage the scouts in order to discover the map, doing it in an efficient and fast and manner.
 * @author Ferran
 */
public class ScoutManagerAgent extends Agent{

	private static final long serialVersionUID = -3186674807204123899L;

	private InfoGame game;
	private ProtocolContractNetInitiator contract;
	
	private boolean justOneTime = true;
	
	private Map<AID, Quadrant> scoutsQuadrants = new HashMap<AID, Quadrant>();
	List<Quadrant> quadrants = null;
	private Point lastPoint = null;
	private int numScouts = 0;
	private boolean quadrantsJoined = false;
	
	/**
	 * A message is shown in the log area of the GUI
	 * @param str String to show
	 */
	private void showMessage(String str) {
		System.out.println(getLocalName() + ": " + str);
	}
	
	@Override
	protected void setup() {
		// Register the agent to the DF
		ServiceDescription sd1 = new ServiceDescription();
		sd1.setType(UtilsAgents.SCOUT_MANAGER_AGENT);
		sd1.setName(getLocalName());
		sd1.setOwnership(UtilsAgents.OWNER);
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.addServices(sd1);
		dfd.setName(getAID());
		
		try {
			DFService.register(this, dfd);
			showMessage("Registered to the DF");
		} catch (FIPAException e) {
			System.err.println(getLocalName() + " registration with DF " + "unsucceeded. Reason: " + e.getMessage());
			doDelete();
		}
		
		MessageTemplate mt = MessageTemplate.MatchProtocol(UtilsAgents.PROTOCOL_TURN);
		this.addBehaviour(new QueriesReceiver(this, mt));
	    
		contract = new ProtocolContractNetInitiator();
		
		MovementRely movement = new MovementRely();
		movement.addBehavior(this, UtilsAgents.COORDINATOR_AGENT);
		
		super.setup();
	}
	
	/**
	 * It will send contract nets to the scouts expecting these to go to a specified uncharted cell of the map.
	 * @param myAgent
	 */
	private void manageScouts(Agent myAgent) {
		ServiceDescription sd1 = new ServiceDescription();
	    sd1.setType(UtilsAgents.SCOUT_AGENT);
	    
	    DFAgentDescription descripcion = new DFAgentDescription();
        descripcion.addServices(sd1);
        
        Cell[][] map = this.game.getMap();
        
		try {
			if (justOneTime) {
				// Get the scouts
				DFAgentDescription[] scouts = DFService.search(this, descripcion);
				numScouts = scouts.length;
				
				// Divide the map into quadrants and assign the scouts to them
				quadrants = ScoutManagerUtils.divideCity(map.length, map[0].length, numScouts);
				
				// Assign each quadrant to each scout
				for (int i = 0; i < numScouts; i++) {
					scoutsQuadrants.put(scouts[i].getName(), quadrants.get(i));
				}
				
				justOneTime = false;
			}
			
			if (!quadrantsJoined) {
				quadrantsJoined = ScoutManagerUtils.joinQuadrants(ScoutManagerUtils.divideCity(map.length, map[0].length, numScouts*2), 
						this.game.getMap(), scoutsQuadrants);
			}
			
			
			boolean canTheymove = false;
			if (ScoutManagerUtils.mapNotEntirelyDiscoveredYet(this.game.getMap())) {
				// Get the target cell for each scout
				// Each Scout has its quadrant
				for (Quadrant quadrant:ScoutManagerUtils.divideCity(map.length, map[0].length, numScouts*2)) {
					
					Point targetPoint = ScoutManagerUtils
							.chooseUnchartedPointInAQuadrant(quadrant, this.game.getMap(), lastPoint);
					if (targetPoint != null) {
						Cell targetCell = new Cell(Cell.STREET);
						targetCell.setRow(targetPoint.x);
						targetCell.setColumn(targetPoint.y);
						contract.addBehaviour(myAgent, targetCell);
						lastPoint = targetPoint;
						canTheymove = true;
					}
				}
			} else {
				for (int i = 0; i < numScouts; i++) {
					// Send the scouts to a corner where they can't interfere with the harvesters
					Point targetPoint = ScoutManagerUtils.chooseCornerPoint(this.game.getMap(), this.game.getInfo().getTurn());
					Cell targetCell = new Cell(Cell.STREET);
					targetCell.setRow(targetPoint.x);
					targetCell.setColumn(targetPoint.y);
					contract.addBehaviour(myAgent, targetCell);
					lastPoint = targetPoint;
				}
			}
			
			if (!canTheymove) {
				// Send the scouts to a corner where they can't interfere with the harvesters
				Point targetPoint = ScoutManagerUtils.chooseCornerPoint(this.game.getMap(), this.game.getInfo().getTurn());
				Cell targetCell = new Cell(Cell.STREET);
				targetCell.setRow(targetPoint.x);
				targetCell.setColumn(targetPoint.y);
				contract.addBehaviour(myAgent, targetCell);
				lastPoint = targetPoint;
			}
			
		} catch (FIPAException e) {
			System.err.println("No scouts found by the ScoutManager");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Manages all incoming FIPA REQUESTS
	 * You can discrimine the type of message with the "instanceof"
	 * @author Roger
	 */
	class QueriesReceiver extends AchieveREResponder {

		private static final long serialVersionUID = -6176920249600913698L;
		
		/* (non-Javadoc)
		 * @see jade.proto.AchieveREResponder#prepareResponse(jade.lang.acl.ACLMessage)
		 */
		@Override
		protected ACLMessage prepareResponse(ACLMessage arg0) throws NotUnderstoodException, RefuseException {
			ACLMessage response = arg0.createReply();

			try {
				if (arg0.getContentObject() instanceof InfoGame) {
					response.setPerformative(ACLMessage.AGREE);
					game = (InfoGame) arg0.getContentObject();
					showMessage("New turn " + game.getInfo().getTurn());
					
					manageScouts(this.myAgent);
				} else if (arg0.getPerformative() == ACLMessage.AGREE) {
					response = null;
				} else {
					throw new NotUnderstoodException("Not the expected object type");
				}
			} catch (UnreadableException e) {
				response.setPerformative(ACLMessage.FAILURE);
			}
			
			return response;
		}

		/* (non-Javadoc)
		 * @see jade.proto.AchieveREResponder#prepareResultNotification(jade.lang.acl.ACLMessage, jade.lang.acl.ACLMessage)
		 */
		@Override
		protected ACLMessage prepareResultNotification(ACLMessage arg0, ACLMessage arg1) throws FailureException {
			return null;
		}

		/* (non-Javadoc)
		 * @see jade.proto.AchieveREResponder#handleRequest(jade.lang.acl.ACLMessage)
		 */
		@Override
		protected ACLMessage handleRequest(ACLMessage arg0) throws NotUnderstoodException, RefuseException {
			try {
				Object objectReceived = arg0.getContentObject();
				if (objectReceived instanceof InfoGame) {
					// Is the coordinator informing of a new turn
					game = (InfoGame) objectReceived;
					showMessage("New turn received from coordinator: " + game.getInfo().getTurn());

					// Find all my agents and send them the new turn
					ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
					for (int x = 0; x < game.getMap().length; x++) {
						for (int y = 0; y < game.getMap()[x].length; y++) {
							Cell c = game.getCell(x, y);
							if (c!=null)
								if (c.isThereAnAgent()) {
									InfoAgent a = c.getAgent();
									if (a.getAgent().equals("S")) {
										message.addReceiver(a.getAID());
									}
								}
						}
					}
					message.setProtocol(UtilsAgents.PROTOCOL_TURN);
					message.setSender(this.myAgent.getAID());
					message.setContentObject(game);
					this.myAgent.send(message);
				}
				
			} catch (UnreadableException e) {
				showMessage("Received an Object that cannot be understood");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return super.handleRequest(arg0);
		}

		public QueriesReceiver(Agent arg0, MessageTemplate arg1) {
			super(arg0, arg1);
		}
	}
	
	public Map<AID, Quadrant> getScoutsQuadrants() {
		return scoutsQuadrants;
	}

}
