package sma.harvester_manager;
import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.FailureException;

public class ContractNetResponderAgent_sencer extends Agent{

	protected void setup(){
		//Que el que rebi sigui fipa_contract_net i un cfp
		MessageTemplate template = MessageTemplate.and(MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET), MessageTemplate.MatchPerformative(ACLMessage.CFP));
		
		//Constructor of contractNetResponder, where yours parameters are (agent, messagetemplate).
		addBehaviour(new ContractNetResponder(this, template){
			protected ACLMessage prepareResponse(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
				System.out.println("Agent "+getLocalName()+": CFP received from "+cfp.getSender().getName()+". Action is "+cfp.getContent());
				int proposal = evaluateAction();
				if(proposal>2){
					//we give proposal.
					System.out.println("Agent "+getLocalName()+": Proposing "+proposal);
					ACLMessage propose = cfp.createReply();
					propose.setPerformative(ACLMessage.PROPOSE);
					propose.setContent(String.valueOf(proposal));
					return propose;
				}else{
					//we refuse
					System.out.println("Agent "+getLocalName()+": Refuse");
					throw new RefuseException("proposal-failed");
				}
			}
			
			protected ACLMessage prepareResultNotification(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException{
				System.out.println("Agent "+getLocalName()+": Proposal accepted.");
				if(performAction()){
					System.out.println("Agent "+getLocalName()+": Action successfully performed.");
					ACLMessage inform = accept.createReply();
					inform.setPerformative(ACLMessage.INFORM);
					return inform;
				}else {
					System.out.println("Agent "+getLocalName()+": Action execution failed.");
					throw new FailureException("unexpected_error");
				}
			}
			
			protected void handleRejectProposal(ACLMessage reject){
				System.out.println("Agent "+getLocalName()+": Proposal rejected");
			}
		});
	}
	
	private int evaluateAction(){
		//Generating a random number.
		return (int)(Math.random()*10);
	}
	
	private boolean performAction(){
		return (Math.random()>0.2);
	}
}
