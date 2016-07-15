package userinterface;

import agent.AuthenticationAgent;
import agent.UserAgentOnlineTrans;
import helper.DBConnection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
public class OnlineTransactionGui extends javax.swing.JFrame {

    private UserAgentOnlineTrans myAgent;
    private String LoggedInUser = "";
    DBConnection db;

    /**
     * Creates new form HomePage
     */
    public OnlineTransactionGui(UserAgentOnlineTrans a, String LoggedUser) {
        super(a.getLocalName());
        myAgent = a;
        LoggedInUser = LoggedUser;
        db = new DBConnection();
        initComponents();

        setAvailableBalance();

        txtAvailableBalance.setEditable(false);
    }

    public void setAvailableBalance() {
        String Account = cmbAccountNo.getSelectedItem().toString();
        String Available_balance = db.getAccountBalance(Account.substring(0, 4), Account.substring(5));
        txtAvailableBalance.setText(Available_balance);
    }

    public void showGui() {

        super.setVisible(true);
    }

     private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cmbAccountNo = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        txtAvailableBalance = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnOK = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        txttransferAmount = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cmbToAccountNo = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnATMTransaction = new javax.swing.JButton();

        //setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Online Transaction");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 0)));

        jLabel1.setText("Account Number");

        try {
            //cmbAccountNo.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
            db.loadAccountNumberCombo(LoggedInUser.substring(4), cmbAccountNo);
            if (cmbAccountNo.getItemCount() > 0) {
                cmbAccountNo.setSelectedIndex(0);
            }

        } catch (SQLException ex) {
            System.out.println(LoggedInUser.substring(4) + ex.getMessage());
            Logger.getLogger(OnlineTransactionGui.class.getName()).log(Level.SEVERE, null, ex);
        }

        cmbAccountNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                setAvailableBalance();
            }
        });

        jLabel2.setText("Available Balance");

        jLabel3.setText("Transfer Amount");

        btnOK.setText("OK");
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        jLabel7.setText("To Account Number");

        try {
            //cmbToAccountNo.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));

            db.loadAccountNumberCombo(LoggedInUser.substring(4), cmbToAccountNo);

            if (cmbToAccountNo.getItemCount() > 0) {
                cmbToAccountNo.setSelectedIndex(0);
            }

        } catch (SQLException ex) {
            System.out.println(LoggedInUser.substring(4) + ex.getMessage());
            Logger.getLogger(OnlineTransactionGui.class.getName()).log(Level.SEVERE, null, ex);
        }

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(btnOK, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(cmbToAccountNo, 0, 125, Short.MAX_VALUE)
                                        .addComponent(jLabel3)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel1)
                                        .addComponent(cmbAccountNo, 0, 125, Short.MAX_VALUE)
                                        .addComponent(txtAvailableBalance)
                                        .addComponent(txttransferAmount)
                                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addContainerGap(110, Short.MAX_VALUE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(38, 38, 38)
                                .addComponent(jLabel6)
                                .addContainerGap(247, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jLabel1)
                        .addGap(4, 4, 4)
                        .addComponent(cmbAccountNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAvailableBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txttransferAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7)
                        .addGap(4, 4, 4)
                        .addComponent(cmbToAccountNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnOK)
                                .addComponent(btnCancel))
                        .addContainerGap(30, Short.MAX_VALUE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(35, 35, 35)
                                .addComponent(jLabel6)
                                .addContainerGap(240, Short.MAX_VALUE)))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 0)));

        //Code to Wrap the Text according to the width
        jTextArea1.setLineWrap(true);
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setColumns(0);

        jTextArea1.setFont(new java.awt.Font("Monospaced", 1, 13)); // NOI18N
        jTextArea1.setForeground(new java.awt.Color(255, 0, 51));
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1)
        );

        jLabel4.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel4.setText("Online Transaction");

        jLabel5.setFont(new java.awt.Font("Tahoma", 3, 11)); // NOI18N
        jLabel5.setText("Notification");

        btnATMTransaction.setText("ATM Transaction");
        btnATMTransaction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnATMTransactionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnATMTransaction, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(25, 25, 25))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel4)
                                .addComponent(jLabel5)
                                .addComponent(btnATMTransaction))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(29, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        myAgent.requestToOnlineTransAgent(cmbAccountNo.getSelectedItem().toString(), cmbToAccountNo.getSelectedItem().toString(), txttransferAmount.getText());

    }

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }

    private void btnATMTransactionActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        myAgent.ForwardToNextLevel(LoggedInUser, myAgent);
    }

    public void setNotification(String agentName, String UserName, String Status, String Remarks) {
        
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.append("\r\n-------" + "Fraud Detected" + "------------" + "\r\n");
        jTextArea1.append("Agent Name:" + agentName + "\r\n");
        jTextArea1.append("Operation Status:" + Status + "\r\n");
        jTextArea1.append("User Name:" + UserName + "\r\n");
        jTextArea1.append("Remarks:" + Remarks + "\r\n");
    }

    public void showErrorMessage(String errorMessage) {
        JOptionPane.showMessageDialog(null, errorMessage, "Error Massage", JOptionPane.INFORMATION_MESSAGE);

    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        // TODO add your handling code here:
        ClearUserStatus();
        myAgent.doDelete();
        System.out.println(myAgent.getName() + " deleting");
    }

    public void ClearUserStatus() {
        AuthenticationAgent.ListOfFailedLoggedInUser.removeAll(Collections.singleton(LoggedInUser));
        AuthenticationAgent.ListOfLoggedInUser.removeAll(Collections.singleton(LoggedInUser));
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton btnATMTransaction;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JComboBox cmbAccountNo;
    private javax.swing.JComboBox cmbToAccountNo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField txtAvailableBalance;
    private javax.swing.JTextField txttransferAmount;
    // End of variables declaration                   
}
