package sma.harvester;

import sma.UtilsAgents;
import sma.ontology.InfoGame;
import sma.pathFinding.Path;
import sma.pathFinding.PathTest;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class HarvesterAgent extends Agent{

	private static final long serialVersionUID = 2649857519665884242L;
	
	private sma.ontology.InfoGame game;
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
	    sd1.setType(UtilsAgents.HARVESTER_AGENT);
	    sd1.setName(getLocalName());
	    sd1.setOwnership(UtilsAgents.OWNER);
	    DFAgentDescription dfd = new DFAgentDescription();
	    dfd.addServices(sd1);
	    dfd.setName(getAID());
	    try {
	      DFService.register(this, dfd);
	      showMessage("Registered to the DF");
	    }
	    catch (FIPAException e) {
	      System.err.println(getLocalName() + " registration with DF " + "unsucceeded. Reason: " + e.getMessage());
	      doDelete();
	    }
	    //Get the game info
	    Object[] args = getArguments();
	    
	    if(args.length>0)
	    {
			this.game=(InfoGame)getArguments()[0];
			if(this.game!=null)
			{
				
				// CONTROL MAPA, el carragem i funcions d'us
				PathTest test = new PathTest(this.game);	
					
				//test.PosicioInicial(15,15,1);
				//Path stepsPathFinal= test.PosicioFinal(25,8,1);
				
				//distancia(stepsPathFinal);
				//stepsFinals(stepsPathFinal);
				//int distPesosOp1= test.distanciaPesos(stepsPathFinal);
				
						
				// OPCIOOOOO 2
				//test.PosicioInicial(15,15,2);
				//Path stepsPathFinal2= test.PosicioFinal(25,8,2);
				
				//int distPesosOp2 = test.distanciaPesos(stepsPathFinal2);
				
				// Distancia pitjor dels casos 
				//int distFinal= distPesosOp1;
				
				//if(distFinal>distPesosOp2)distFinal=distPesosOp2;
				
				//System.out.println("Distancia FINAL" + distFinal);
				
				
				
				showMessage("Tinc un joc amb "+game.getInfo().getNumScouts()+" scouts");
			}
		}
		super.setup();
	}

	@Override
	protected void takeDown() {
		// TODO Auto-generated method stub
		super.takeDown();
	}



}
