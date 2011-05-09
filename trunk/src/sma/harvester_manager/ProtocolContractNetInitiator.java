package sma.harvester_manager;
import jade.proto.*;
import jade.lang.acl.ACLMessage;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.io.IOException;
import java.util.Vector;
import sma.ontology.Cell;

public class ProtocolContractNetInitiator{
	//Entra una cell on hi ha el material i la posició on vull que vagi.
	public void addBehaviour(Agent agent, Cell content) throws IOException
	{
		ACLMessage ms = new ACLMessage(ACLMessage.CFP);
		ms.setProtocol(sma.UtilsAgents.CONTRACT_NET);
		ms.setContentObject(content);
		ms.setSender(agent.getAID());
		ms = FindReceivers(agent,ms);
		agent.addBehaviour(new ProtocolContractNetInit(agent, ms));
	}

	private ACLMessage FindReceivers(Agent agent, ACLMessage msg) {		
		ServiceDescription sd = new ServiceDescription();
		sd.setType(sma.UtilsAgents.HARVESTER_AGENT);
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.addServices(sd);
		try{
			while(true) {
				SearchConstraints c = new SearchConstraints();
				c.setMaxResults(new Long(-1));
				DFAgentDescription[] result = DFService.search(agent, dfd, c);				
				if(result.length > 0){
					int i = 0;
					int j = result.length;
					while (i<j){
						dfd = result[i];
						msg.addReceiver(dfd.getName());
						i=i+1;					
					}
					break;
				}
				Thread.sleep(2000); /*Each 5 seconds we try to search*/
			}
		} catch(Exception fe) {
			fe.printStackTrace();
		     System.out.println( agent.getLocalName() +
		                         " search with DF is not succeeded because of " +
		                         fe.getMessage() );
		     agent.doDelete();
		}		
		return msg;
	}

	public class ProtocolContractNetInit extends ContractNetInitiator{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		////El constructor rep una referència a l’agent que l’ha creat
		//i el missatge (CFP) a enviar
		public ProtocolContractNetInit (Agent myAgent, ACLMessage msg)
		{
			//ficar tots els receivers. el msg ha de portar-ho tot. aquí o a fora quan ho instancies.
			//msg.addReceiver();
			
			super(myAgent, msg); 
		}
		
		//s’executa cada cop que es rep un missatge de Propose
		@SuppressWarnings("unchecked")
		protected void handlePropose (ACLMessage propose, Vector acceptances)
		{
			ACLMessage reply = propose.createReply();
			//Evaluate the proposal
			System.out.println("L'agent: "+propose.getSender()+", ha enviat "+propose.getContent()+".");
			//Accept o reject proposal.
			reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
			acceptances.add(reply);
		}
		
		protected void handleNotUnderstood (ACLMessage msg) {
			System.out.println("HandleNoutUnderstood");
		}
		protected void handleRefuse (ACLMessage msg) {
			System.out.println("handlerefuse");
		}
		protected void handleInform (ACLMessage msg) {
			System.out.println("Handleinform");
		}
		protected void handleFailure (ACLMessage msg){
			System.out.println("Handlefailure");
		}
	}
}