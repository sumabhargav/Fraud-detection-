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
import userinterface.OnlineTransactionGui;
public class UserAgentOnlineTrans extends Agent {

    private final TransactionModel targetUser;
  
    private AID[] onlineTransAgents;
    private OnlineTransactionGui myGui;

    private final String _agent_service_type = "transaction-service";

    private String _Logged_in_User = "";

    private String _MainATMAgentName = "";

    Object[] args;

    public UserAgentOnlineTrans() {
        this.targetUser = new TransactionModel();
    }

    protected void setup() {

        args = getArguments();
        if (args != null && args.length > 0) {
            this._Logged_in_User = (String) args[0];

            myGui = new OnlineTransactionGui(this, _Logged_in_User);
            myGui.setTitle(myGui.getTitle()+"-"+_Logged_in_User);
            myGui.showGui();
        }
        System.out.println("Hello! Online Transaction-User-agent " + getAID().getName() + " is ready.");

    }

    public void requestToOnlineTransAgent(String from_account_no, String to_account_no, String amount) {
        addBehaviour(new OneShotBehaviour() {
            public void action() {

                // Update the list of seller agents
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();

                targetUser._user_id = _Logged_in_User;
                targetUser._amount = amount;
                targetUser._from_account_no = from_account_no;
                targetUser._to_account_no = to_account_no;

                sd.setType(_agent_service_type);
                template.addServices(sd);
                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    System.out.println("User: " + _Logged_in_User + " has found the following online Transaction agents:");
                    onlineTransAgents = new AID[result.length];
                    for (int i = 0; i < result.length; ++i) {
                        onlineTransAgents[i] = result[i].getName();
                        System.out.println(onlineTransAgents[i].getName());

                        _MainATMAgentName = onlineTransAgents[i].getName();
                    }
                } catch (FIPAException fe) {
                    fe.printStackTrace();
                }

                myAgent.addBehaviour(new RequestPerformer());

            }
        });
        }

    public void ForwardToNextLevel(final String User_id, final UserAgentOnlineTrans myagent) {

        addBehaviour(new OneShotBehaviour() {
            public void action() {

                System.out.println(UserAgent.class.getName());
                try {
                    AgentContainer container = myagent.getContainerController();
                    container.createNewAgent("userAgentATMTrans_" + User_id, UserAgentATMTrans.class.getName(), args).start();
                    myagent.doDelete();
                    myGui.dispose();
                } catch (StaleProxyException ex) {
                    Logger.getLogger(UserAgentOnlineTrans.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

    }

    protected void takeDown() {
        myGui.dispose();
        System.out.println("Online Transaction User-agent " + getAID().getName() + " for " + _Logged_in_User + " terminating.");
    }

    /**
     * Inner class RequestPerformer. This is the behaviour used by UserAgent
     * agents to request seller agents the target book.
     */
    private class RequestPerformer extends Behaviour {

        //private AID bestAuthenticAgent;  
        private String isValidTrans;  

        private String FailedReason; 
        private int repliesCnt = 0; 
        private MessageTemplate mt; 
        private int step = 0;
        private String conversation_id = "online-trans-svc";

        public void action() {
            switch (step) {
                case 0:
                    ACLMessage req = new ACLMessage(ACLMessage.REQUEST);
                    for (int i = 0; i < onlineTransAgents.length; ++i) {
                        req.addReceiver(onlineTransAgents[i]);
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
                        if (repliesCnt >= onlineTransAgents.length) {
                      
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
                System.out.println(targetUser._user_id + " has been failed to transfer amount from " + targetUser._from_account_no + " to " + targetUser._to_account_no);
                UserName = targetUser._user_id;
                Status = FailedReason;
                Remarks = targetUser._user_id + " has been failed to transfer amount from " + targetUser._from_account_no + " to " + targetUser._to_account_no;
                myGui.setNotification(agentName, UserName, Status, Remarks);
            }
            if (step == 2 && isValidTrans != null) {

                System.out.println(targetUser._user_id + " has been successful to transfer amount from " + targetUser._from_account_no + " to " + targetUser._to_account_no);

                myGui.setAvailableBalance();
                myGui.showErrorMessage(targetUser._user_id + " has been successful to transfer amount from " + targetUser._from_account_no + " to " + targetUser._to_account_no);
            }

            return ((step == 2 && isValidTrans == null) || step == 2);

        }

    } 
}
