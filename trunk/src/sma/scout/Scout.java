package sma.scout;

import sma.UtilsAgents;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class Scout extends Agent {

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
	    sd1.setType(UtilsAgents.SCOUT_AGENT);
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

		super.setup();
	}
	@Override
	protected void takeDown() {
		// TODO Auto-generated method stub
		super.takeDown();
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -1956175742934189946L;
//   �� 
	public Scout()
	{
		super();
	}
}
