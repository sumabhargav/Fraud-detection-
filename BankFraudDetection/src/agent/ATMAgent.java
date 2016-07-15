
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
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Rules;
import model.TransactionModel;

public class ATMAgent extends Agent {

    private TransactionModel _transaction_model = new TransactionModel();

    private DBConnection _db = new DBConnection();

    private String _agent_name = "ATM-Transaction-Agent";

    private String _agent_service_type = "atm-transaction-service";
    private String _agent_service_name = "Bank-atm-transaction-service";
    protected void setup() {

        // Register the transaction-agent service in the yellow pages
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

        addBehaviour(new PerformTransaction());

    }

    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        System.out.println(_agent_name + getAID().getName() + " terminating.");
    }

    private class PerformTransaction extends CyclicBehaviour {

        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);
            System.out.println("started atm transaction-agent");
            if (msg == null) {
                System.out.println("null message in atm trnsaction-agent");
            }
            if (msg != null) {

                // REQUEST Message received. Process it
                _transaction_model = new TransactionModel();
                try {
                    _transaction_model = (TransactionModel) msg.getContentObject();
                    System.out.println("atm transaction-agent-initializing:" + _transaction_model._amount + " withdrawing from " + _transaction_model._from_account_no);
                } catch (UnreadableException ex) {
                    System.out.println("atm transaction-agent-exception:" + ex.getMessage());

                    Logger.getLogger(ATMAgent.class.getName()).log(Level.SEVERE, null, ex);
                }
                ACLMessage reply = msg.createReply();

                //Checking Rules
                Rules LEVEL = Rules.NONE;
                String return_values = "";
                double available_balance = 0;

                return_values = _db.getAccountNoFromPIN(_transaction_model._card_no, _transaction_model._card_pin);

                if (!return_values.isEmpty()) {

                    _transaction_model._from_account_no = return_values;

                    // Checking Account Balance
                    return_values = _db.getAccountBalance(_transaction_model._from_account_no.substring(0, 4), _transaction_model._from_account_no.substring(5));

                    if (!return_values.isEmpty()) {
                        available_balance = Double.parseDouble(return_values);
                        if (available_balance <= 0) {
                            LEVEL = Rules.INSUFFICIENT_BALANCE;
                        }

                    } else {
                        LEVEL = Rules.INVALID_ACCOUNT_NUMBER;
                    }
                } else {
                    LEVEL = Rules.INVALID_CARD_NO_OR_PIN_NO;
                }

                //Checking Transaction Amount
                if (LEVEL == Rules.NONE) {
                    if (_transaction_model._amount.isEmpty()) {
                        LEVEL = Rules.INVALID_TRANSFER_AMOUNT;
                    } else {

                        double trans_amount = Double.parseDouble(_transaction_model._amount);

                        if (trans_amount <= 0) {
                            LEVEL = Rules.INVALID_TRANSFER_AMOUNT;
                        }
                        if (trans_amount > 100000) {
                            LEVEL = Rules.TRANSACTION_MAX_LIMIT;
                        }
                        if (trans_amount > available_balance) {
                            LEVEL = Rules.TRANS_AMOUNT_GREATER_THAN_BALANCE;
                        }
                    }
                }

                if (LEVEL == Rules.NONE) {
                    String isSuccess = _db.performATMTransaction(_transaction_model._user_id, _transaction_model._from_account_no, _transaction_model._amount, _transaction_model._card_no, _transaction_model._card_pin);
                    if (isSuccess.equals("1")) {
                        reply.setPerformative(ACLMessage.CONFIRM);
                        //reply.setContent("true");  
                        System.out.println("atm transaction-agent Successfully performed :Amount " + _transaction_model._amount + " Transfering " + "From " + _transaction_model._from_account_no + " to " + _transaction_model._to_account_no);
                    } else {

                        System.out.println("atm transaction-agent Failed to perform :Amount " + _transaction_model._amount + " Transfering " + "From " + _transaction_model._from_account_no + " to " + _transaction_model._to_account_no);
                        reply.setPerformative(ACLMessage.REFUSE);
                        //reply.setContent("false"); 
                        LEVEL = Rules.UNKNOWN;
                    }
                } else {
                    System.out.println("atm transaction-agent Failed to withdraw Amount " + _transaction_model._amount + " Transfering " + "From " + _transaction_model._from_account_no);
                    System.out.println("Failed Reason: " + LEVEL.name());
                    reply.setPerformative(ACLMessage.REFUSE);
                }
                reply.setContent(LEVEL.name());
                myAgent.send(reply);
            } else {
                block();
            }
        }
    }

}
