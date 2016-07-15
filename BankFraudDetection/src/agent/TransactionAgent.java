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
public class TransactionAgent extends Agent {

    private TransactionModel _transaction_model = new TransactionModel();

    private DBConnection _db = new DBConnection();

    private String _agent_name = "Transaction-Agent";

    private String _agent_service_type = "transaction-service";
    private String _agent_service_name = "Bank-transaction-service";

    protected void setup() {

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
            System.out.println("started transaction-agent");
            if (msg == null) {
                System.out.println("null message in trnsaction-agent");
            }
            if (msg != null) {
                 _transaction_model = new TransactionModel();
                try {
                    _transaction_model = (TransactionModel) msg.getContentObject();
                    System.out.println("transaction-agent-initializing:" + _transaction_model._amount + " transferring from " + _transaction_model._from_account_no + " to " + _transaction_model._to_account_no);
                } catch (UnreadableException ex) {
                    System.out.println("transaction-agent-exception:" + ex.getMessage());

                    Logger.getLogger(TransactionAgent.class.getName()).log(Level.SEVERE, null, ex);
                }
                ACLMessage reply = msg.createReply();

                //Checking Rules
                Rules LEVEL = Rules.NONE;
                String return_values = "";
                double available_balance = 0;
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
                    boolean isValidAccount = _db.isValidAccount(_transaction_model._from_account_no.substring(0, 4), _transaction_model._from_account_no.substring(5));

                    if (!isValidAccount) {

                        LEVEL = Rules.INVALID_ACCOUNT_NUMBER;
                    }
                }

                if (LEVEL == Rules.NONE) {
                    String isSuccess = _db.performTransaction(_transaction_model._user_id, _transaction_model._from_account_no, _transaction_model._to_account_no, _transaction_model._amount);
                    if (isSuccess.equals("1")) {
                        reply.setPerformative(ACLMessage.CONFIRM);
                        //reply.setContent("true");  
                        System.out.println("transaction-agent Successfully performed :Amount " + _transaction_model._amount + " Transfering " + "From " + _transaction_model._from_account_no + " to " + _transaction_model._to_account_no);
                    } else {

                        System.out.println("transaction-agent Failed to perform :Amount " + _transaction_model._amount + " Transfering " + "From " + _transaction_model._from_account_no + " to " + _transaction_model._to_account_no);
                        reply.setPerformative(ACLMessage.REFUSE);
                             LEVEL = Rules.UNKNOWN;
                    }
                } else {
                    System.out.println("transaction-agent Failed to perform :Amount " + _transaction_model._amount + " Transfering " + "From " + _transaction_model._from_account_no + " to " + _transaction_model._to_account_no);

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
