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
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.LoginModel;
import userinterface.LoginFormGui;

public class UserAgent extends Agent {

    private LoginModel targetUser = new LoginModel();
              
    private AID[] authenticationAgents;
    private LoginFormGui myGui;

    private String _agent_service_type = "authentication-service";

    private String _Logged_in_User = "";

    private boolean _is_Login_Success = false;

    private String _MainATMAgentName = "";

    
    protected void setup() {
        myGui = new LoginFormGui(this);
        myGui.showGui();

        _is_Login_Success = false;
        System.out.println("Hello! User-agent " + getAID().getName() + " is ready.");
       
    }

    public void requestToAuthenticationAgent(final String user_id, final String password) {
        addBehaviour(new OneShotBehaviour() {
            public void action() {

                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                _is_Login_Success = false;
                _Logged_in_User = user_id;

                targetUser._user_id = user_id;
                targetUser._password = password;

                sd.setType(_agent_service_type);
                template.addServices(sd);
                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    System.out.println("User: " + _Logged_in_User + " has found the following authentication agents:");
                    authenticationAgents = new AID[result.length];
                    for (int i = 0; i < result.length; ++i) {
                        authenticationAgents[i] = result[i].getName();
                        System.out.println(authenticationAgents[i].getName());
                        _MainATMAgentName = authenticationAgents[i].getName();
                    }
                } catch (FIPAException fe) {
                    fe.printStackTrace();
                }

                myAgent.addBehaviour(new RequestPerformer());

            }
        });
          }

    protected void ForwardToNextLevel(String User_id) {

        if (_Logged_in_User != null && !_Logged_in_User.isEmpty()) {

            AgentContainer container = this.getContainerController();
            Object[] args = new Object[]{_Logged_in_User};
           
            System.out.println(UserAgent.class.getName());
            try {
                container.createNewAgent("userAgentOnlineTrans_" + User_id, UserAgentOnlineTrans.class.getName(), args).start();
                this.doDelete();
                myGui.dispose();
            } catch (StaleProxyException ex) {
                Logger.getLogger(UserAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    protected void takeDown() {
        
        myGui.dispose();
        System.out.println("User-agent " + getAID().getName() + " for " + _Logged_in_User + " terminating.");
    }
 private class RequestPerformer extends Behaviour {

        private AID bestAuthenticAgent; 
        private String isValidUser;  
        private int repliesCnt = 0; 
        private MessageTemplate mt; 
        private int step = 0;
        private String conversation_id = "login-svc";

        private int countLoop = 0;

        public void action() {
            switch (step) {
                case 0:
                     ACLMessage req = new ACLMessage(ACLMessage.REQUEST);
                    for (int i = 0; i < authenticationAgents.length; ++i) {
                        req.addReceiver(authenticationAgents[i]);
                    }
                    try {
                        req.setContentObject(targetUser);
                        System.out.println(targetUser._password);
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                        Logger.getLogger(UserAgent.class.getName()).log(Level.SEVERE, null, ex);
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
                            isValidUser = reply.getContent();

                            System.out.println("GotResult:---" + isValidUser);
                        }
                        repliesCnt++;
                        if (repliesCnt >= authenticationAgents.length) {
                            // We received all replies

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

            System.out.println("validationStatus: " + isValidUser);

            String agentName = _MainATMAgentName;
            String UserName = "";
            String Status = "";
            String Remarks = "";

            if (step == 2 && isValidUser == null) {

                System.out.println("Attempt failed: Invalid Login Attempt for " + targetUser._user_id);
                _is_Login_Success = false;
                System.out.println("Current Logged inarray list is:" + AuthenticationAgent.ListOfLoggedInUser);
                System.out.println("Current Failed array list is:" + AuthenticationAgent.ListOfFailedLoggedInUser);

                if (checkFailedAttempt(targetUser._user_id) >= 3) {

                    UserName = targetUser._user_id;
                     Remarks = "Login Attempt failed  3 times for " + targetUser._user_id;
                    myGui.setNotification(agentName, UserName, Status, Remarks);

                } else {
                    
                    myGui.showErrorMessage("Invalid Login Attempt");
                    AuthenticationAgent.ListOfFailedLoggedInUser.add(targetUser._user_id);
                }

            }
            if (step == 2 && isValidUser != null) {
                _is_Login_Success = true;
                System.out.println("Valid Login Attempt for " + targetUser._user_id);

                System.out.println("Current Logged inarray list is:" + AuthenticationAgent.ListOfLoggedInUser);
                System.out.println("Current Failed array list is:" + AuthenticationAgent.ListOfFailedLoggedInUser);

                if (checkForDuplicateLogin(targetUser._user_id) >= 1) {

                    UserName = targetUser._user_id;
                    Remarks = "Duplicate Login Attempt for " + targetUser._user_id;
                    myGui.setNotification(agentName, UserName, Status, Remarks);

                } else {
                    AuthenticationAgent.ListOfLoggedInUser.add(targetUser._user_id);
                    AuthenticationAgent.ListOfFailedLoggedInUser.removeAll(Collections.singleton(targetUser._user_id));
                    ForwardToNextLevel(targetUser._user_id);
                }

            }

            return ((step == 2 && isValidUser == null) || step == 2);

        }

        public int checkForDuplicateLogin(String UserId) {
            int count = 0;
            for (int i = 0; i < AuthenticationAgent.ListOfLoggedInUser.size(); i++) {
                String user = AuthenticationAgent.ListOfLoggedInUser.get(i);

                if (user.equals(UserId)) {
                    count++;
                    break;
                }
            }
            return count;
        }

        public int checkFailedAttempt(String UserId) {
            int countv = 0;
            for (int i = 0; i < AuthenticationAgent.ListOfFailedLoggedInUser.size(); i++) {
                String Str = AuthenticationAgent.ListOfFailedLoggedInUser.get(i);
                if (Str.equals(UserId)) {
                    countv++;
                }

            }
            return countv;
        }

    }  
}
