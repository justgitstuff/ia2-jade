package sma.harvester_manager;
import sma.ontology.Cell;
import jade.proto.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.core.Agent;

public class ProtocolContractNetResponder{

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

		//El constructor rep la instància de l’agent que l’ha creat
		//i la plantilla amb els filtres per als missatges entrants
		public ProtocolContractNetRes (Agent myAgent, MessageTemplate mt)
		{
			super(myAgent, mt);
		}
		
		//s’executa	quan es rep un missatge CFP i caldrà que retorni la resposta corresponent
		//(not-understood, refuse o propose)
		protected ACLMessage prepareResponse (ACLMessage msg)
		{	
			Cell content=null;
			try {
				content = (Cell) msg.getContentObject();
			} catch (UnreadableException e) {
				e.printStackTrace();
			}
			System.out.println("Harvester: Rebut de "+msg.getSender()+". Material:"+content.getGarbageType()+", x: "+content.getColumn()+", y: "+content.getRow());
			ACLMessage reply = msg.createReply();
			reply.setPerformative(ACLMessage.PROPOSE);
			//Or refuse or not-understood.
			////En el content un int la distància al punt.
			reply.setContent("Responc del harvester "+this.myAgent.getName()+".");
			return reply;
		}
		
		//s’executa quan es rep un missatge Accept-proposal. També rep per paràmetres el
		//CFP inicial i la resposta (Propose). Cal que executi l’acció corresponent i
		//retorni el resultat (Inform o Failure).
		protected ACLMessage prepareResultNotification (ACLMessage cfp, ACLMessage propose, ACLMessage accept)
		{
			ACLMessage inform = accept.createReply();
			System.out.println("Soc el harvester "+this.myAgent.getName()+", rebut de "+accept.getSender()+"acceptada la meva proposta: "+propose.getContent()+".");
			inform.setPerformative(ACLMessage.INFORM);
			//Or failure.
			return inform;
		}
		
		//manega la recepció de missatges Reject-proposal.
		protected void handleRejectProposal (ACLMessage cfp, ACLMessage propose, ACLMessage reject)
		{
			System.out.println("Sóc el Harvester "+this.myAgent.getName()+". Han refusat la meva proposta "+propose.getContent()+".");
		}
	}
	}