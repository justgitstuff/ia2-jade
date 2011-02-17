import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.*;
import jade.proto.*;
import jade.lang.acl.*;
import java.util.*;

public class MyFirstAgent extends Agent {

  void RegisterDF() {
    DFAgentDescription dfd = new DFAgentDescription();
    ServiceDescription sd = new ServiceDescription();
    sd.setName("MyAgent1");
    sd.setType("Useless agent");
    Property p = new Property();
    p.setName("City");
    p.setValue(new String("Reus"));
    sd.addProperties(p);
    dfd.addServices(sd);
    dfd.setName(getAID());

    try {
      DFService.register(this, dfd);
	  System.out.println("["+getLocalName()+"]:"+" has been registered in DF");
    } catch (jade.domain.FIPAException e) { 
      System.out.println("["+getLocalName()+"]:"+" hasn't been registered in DF");
      doDelete();
    } 
  }

  void DeRegisterDF() {
    try {
      DFService.deregister(this);
      System.out.println("["+getLocalName()+"]:"+" has been removed from DF");
    } catch (Exception e) {
      System.out.println("["+getLocalName()+"]:"+" hasn't been removed from DF");
    }
  }

  public void takeDown(){
    DeRegisterDF();
  }

  public void setup() {
	RegisterDF();
    addBehaviour(new MyBehaviour());
  }

  class MyBehaviour extends OneShotBehaviour {
	  
	public MyBehaviour(){
		super ();
	}

	public void action() {
		System.out.println("Hello World!");
	}
	    
  }

}
