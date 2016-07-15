package agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentContainer;
import jade.wrapper.StaleProxyException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.TransactionModel;
import userinterface.ATMTransactionGui;
public class UserAgentATMTrans extends Agent {

    private final TransactionModel targetUser;

    private String _MainATMAgentName = "";
    private AID[] atmAgents;
    private ATMTransactionGui myGui;
    private final String _agent_service_type = "atm-transaction-service";
    private String _Logged_in_User = "";
    Object[] args;

    public UserAgentATMTrans() {
        this.targetUser = new TransactionModel();
    }

    protected void setup() {

        args = getArguments();
        if (args != null && args.length > 0) {
            this._Logged_in_User = (String) args[0];

            // Create and show the GUI 
            myGui = new ATMTransactionGui(this, _Logged_in_User);
            myGui.setTitle(myGui.getTitle() + "-" + _Logged_in_User);
            myGui.showGui();
        }
        System.out.println("Hello! ATM-User-agent " + getAID().getName() + " is ready.");

    }

    public void requestToATMAgent(final String amount, final String CardNo, final String CardPin) {
        addBehaviour(new OneShotBehaviour() {
            public void action() {

                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();

                targetUser._user_id = _Logged_in_User;
                targetUser._amount = amount;

                targetUser._card_no = CardNo;
                targetUser._card_pin = CardPin;

                sd.setType(_agent_service_type);
                template.addServices(sd);
                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    System.out.println("User: " + _Logged_in_User + " has found the following ATM agents:");
                    atmAgents = new AID[result.length];
                    for (int i = 0; i < result.length; ++i) {
                        atmAgents[i] = result[i].getName();
                        System.out.println(atmAgents[i].getName());

                        _MainATMAgentName = atmAgents[i].getName();

                    }
                } catch (FIPAException fe) {
                    fe.printStackTrace();
                }

                myAgent.addBehaviour(new RequestPerformer());

            }
        });
    }

    public void ForwardToNextLevel(final String User_id, final UserAgentATMTrans agent) {

        addBehaviour(new OneShotBehaviour() {
            public void action() {
                System.out.println(UserAgent.class.getName());
                try {
                    AgentContainer container = agent.getContainerController();
                    container.createNewAgent("userAgentOnlineTrans_" + User_id, UserAgentOnlineTrans.class.getName(), args).start();
                    agent.doDelete();
                    myGui.dispose();
                } catch (StaleProxyException ex) {
                    Logger.getLogger(UserAgentATMTrans.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

    }

      protected void takeDown() {
        myGui.dispose();
         System.out.println("ATM User-agent " + getAID().getName() + " for " + _Logged_in_User + " terminating.");
    }

    private class RequestPerformer extends Behaviour {

        private String FailedReason; 
        private String isValidTrans;  
        private int repliesCnt = 0; 
        private MessageTemplate mt; 
        private int step = 0;
        private String conversation_id = "atm-svc";

        private String recieverAgentName = "";

        public void action() {
            switch (step) {
                case 0:
                    
                    ACLMessage req = new ACLMessage(ACLMessage.REQUEST);
                    for (int i = 0; i < atmAgents.length; ++i) {
                        req.addReceiver(atmAgents[i]);
                    }
                    try {
                        req.setContentObject(targetUser);

                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                        Logger.getLogger(UserAgentATMTrans.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    System.out.println("Step 0");
                    req.setConversationId(conversation_id);
                    req.setReplyWith("req" + System.currentTimeMillis()); // Unique value
                    myAgent.send(req);
                    // Prepare the template to get proposals
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId(conversation_id),
                            MessageTemplate.MatchInReplyTo(req.getReplyWith()));
                    step = 1;
                    break;
                case 1:
                    
                    ACLMessage reply = myAgent.receive(mt);
                    if (reply != null) {
                        
                        if (reply.getPerformative() == ACLMessage.CONFIRM) {
                             isValidTrans = reply.getContent();

                            System.out.println("GotResult:---" + isValidTrans);
                        } else if (reply.getPerformative() == ACLMessage.REFUSE) {
                            FailedReason = reply.getContent();

                        }

                        repliesCnt++;
                        if (repliesCnt >= atmAgents.length) {
                             step = 2;
                        }
                    } else {
                        block();
                    }
                    break;
                case 2:
                    step = 3;
                    break;

            }
        }

        public boolean done() {

            System.out.println("Entering Done " + step);

            System.out.println("validationStatus: " + isValidTrans);

            String agentName = _MainATMAgentName;
            String UserName = "";
            String Status = "";
            String Remarks = "";

            if (step == 2 && isValidTrans == null) {
                System.out.println(targetUser._user_id + " has failed to withdraw " + targetUser._amount + " with Card no:" + targetUser._card_no);
                UserName = targetUser._user_id;
                Status = FailedReason;
                Remarks = targetUser._user_id + " has failed to withdraw " + targetUser._amount + " with Card no:" + targetUser._card_no;
                myGui.setNotification(agentName, UserName, Status, Remarks);
            }
            if (step == 2 && isValidTrans != null) {

                System.out.println(targetUser._user_id + " has withdrawn " + targetUser._amount + " with Card no:" + targetUser._card_no);
                myGui.showErrorMessage(targetUser._user_id + " has withdrawn " + targetUser._amount + " with Card no:" + targetUser._card_no);
            }

            return ((step == 2 && isValidTrans == null) || step == 2);

        }

    }  
}
