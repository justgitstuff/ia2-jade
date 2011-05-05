import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.*;

public class MySecondAgent extends Agent {

  /**
	 * 
	 */
	private static final long serialVersionUID = -7270886249073598835L;

void RegisterDF() {
    DFAgentDescription dfd = new DFAgentDescription();
    ServiceDescription sd = new ServiceDescription();
    sd.setName("MyAgent2");
    sd.setType("Almost as useless as the first one");
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
	SequentialBehaviour sb = new SequentialBehaviour();
	sb.addSubBehaviour(new HelloBehaviour());
	sb.addSubBehaviour(new GoodbyeBehaviour());
	sb.addSubBehaviour(new OnceAgain(this));
	addBehaviour(sb);
  }

  class HelloBehaviour extends OneShotBehaviour {
	  
	/**
	 * 
	 */
	private static final long serialVersionUID = -272314131295129668L;

	public HelloBehaviour(){
		super ();
	}

	public void action() {
		System.out.println("Hello World!");
		try {Thread.sleep(2000);} catch ( InterruptedException e ) {}
	}
	    
  }
  
  class GoodbyeBehaviour extends OneShotBehaviour {
	  
		/**
	 * 
	 */
	private static final long serialVersionUID = 1699355093844157979L;

		public GoodbyeBehaviour(){
			super ();
		}

		public void action() {
			System.out.println("Goodbye World!");
			try {Thread.sleep(2000);} catch ( InterruptedException e ) {}
		}
		    
	  }
  
  class OnceAgain extends OneShotBehaviour {
	  
		/**
	 * 
	 */
	private static final long serialVersionUID = 1586150530550406229L;

		public OnceAgain(Agent myagent){
			super ();
			this.myAgent=myagent;
		}

		public void action() {
			SequentialBehaviour sb = new SequentialBehaviour();
			sb.addSubBehaviour(new HelloBehaviour());
			sb.addSubBehaviour(new GoodbyeBehaviour());
			sb.addSubBehaviour(new OnceAgain(myAgent));
			this.myAgent.addBehaviour(sb);
		}
		    
  }

}
