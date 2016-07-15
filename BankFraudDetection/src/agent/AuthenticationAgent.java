package agent;

import helper.DBConnection;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.LoginModel;
import model.TransactionModel;

public class AuthenticationAgent extends Agent {

    private Hashtable catalogue;

    public static ArrayList<String> ListOfLoggedInUser = new ArrayList<String>();

    public static ArrayList<String> ListOfFailedLoggedInUser = new ArrayList<String>();

    private LoginModel _user = new LoginModel();

    private DBConnection _db = new DBConnection();

    private String _agent_name = "Authentication-Agent";

    private String _agent_service_type = "authentication-service";
    private String _agent_service_name = "Bank-authentication-service";

     protected void setup() {
         catalogue = new Hashtable();

       DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType(_agent_service_type);
        sd.setName(_agent_service_name);
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        addBehaviour(new CheckValidUser());

        }

     protected void takeDown() {
        // Deregister from the yellow pages
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        System.out.println(_agent_name + getAID().getName() + " terminating.");
    }

    private class CheckValidUser extends CyclicBehaviour {

        public void action() {

            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);
            System.out.println("started authentication-agent");
            if (msg == null) {
                System.out.println("null message in auth-agent");
            }
            if (msg != null) {
                // REQUEST Message received. Process it
                _user = new LoginModel();
                try {
                    _user = (LoginModel) msg.getContentObject();
                    System.out.println("auth-agent-checking: " + _user._user_id + " " + _user._password);
                } catch (UnreadableException ex) {
                    System.out.println("auth-exception:" + ex.getMessage());
                    Logger.getLogger(AuthenticationAgent.class.getName()).log(Level.SEVERE, null, ex);
                }
                ACLMessage reply = msg.createReply();

                // Write code for checking user id and password from DB
                boolean isValid = _db.checkUserInformationFromDB(_user._user_id, _user._password);

                if (isValid) {

                    reply.setPerformative(ACLMessage.CONFIRM);
                    reply.setContent("true");

                    System.out.println("auth-agent:valid user" + _user._user_id + " " + _user._password);

                    //ListOfLoggedInUser.add(_user._user_id);

                } else {
                    //ListOfFailedLoggedInUser.add(_user._user_id);
                    System.out.println("auth-agent:INVALID user" + _user._user_id + " " + _user._password);
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("false");
                }
                myAgent.send(reply);
            } else {
                block();
            }
        }
        
         
    }

}
