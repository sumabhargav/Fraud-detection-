package helper;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {

    public static Connection dbConnecter() throws Exception {

        String driver = "com.mysql.jdbc.Driver";
        String connection = "jdbc:mysql://localhost:3306/frauddetectiondb";
        String user = "root";
        String password = "";
        Class.forName(driver);
        Connection con = DriverManager.getConnection(connection, user, password);
        return con;
    }

    public String SaveQueryBySQL(String Query) {

        String errorMessage = "";
        try {
            Statement Stat = dbConnecter().createStatement();
            Stat.executeUpdate(Query);
            errorMessage = "";

        } catch (Exception ex) {
            errorMessage = ex.getMessage();
        }

        return errorMessage;

    }

    public void GetResultSetbyQuery(String Sql) throws SQLException {
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {

            pst = dbConnecter().prepareStatement(Sql);
            rs = pst.executeQuery();
            int i = 0;
            if (rs.next()) {
                //number_of_votes = rs.getString("number_of_votes");
                //candidate_name = rs.getString("candidate_name");

                i++;
            }

            if (i == 1) {
                System.out.println(i + " Record Found");
            } else {
                System.out.println(i + " Records Found");
            }

            pst.close();
            rs.close();
        } catch (Exception ex) {
            if (pst != null) {
                pst.close();
            }
            if (rs != null) {
                rs.close();
            }

        }

    }

    public void loadAccountNumberCombo(String Customer_id, javax.swing.JComboBox combo) throws SQLException {

        String Sql = "SELECT * FROM  account t   where t.customer_id=" + Customer_id;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {

            pst = dbConnecter().prepareStatement(Sql);
            rs = pst.executeQuery();
            int i = 0;
            while (rs.next()) {
                combo.addItem(rs.getString("branch_id") + "-" + rs.getString("account_no"));
                i++;
            }

            if (i == 1) {
                System.out.println(i + " Record Found");
            } else {
                System.out.println(i + " Records Found");
            }

            pst.close();
            rs.close();
        } catch (Exception ex) {
            if (pst != null) {
                pst.close();
            }
            if (rs != null) {
                rs.close();
            }
            System.out.println(Customer_id + ex.getMessage());
        }

    }
    
    public String getCustomerId(String account) {

        boolean isSuccess = false;
        String user_id_db = "";

        String Sql = "SELECT t.customer_id FROM  account t where  concat(t.branch_id,'-',t.account_no)= '"+account+"'";
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {

            pst = dbConnecter().prepareStatement(Sql);
            rs = pst.executeQuery();
            int i = 0;
            if (rs.next()) {
                user_id_db = rs.getString("customer_id");
                i++;
            }
            if (user_id_db != null) {
                if (!user_id_db.isEmpty()) {
                    System.out.println(i + " Record Found");
                    isSuccess = true;
                } else {
                    isSuccess = false;
                    System.out.println(i + " Record not Found");
                }

            }
            pst.close();
            rs.close();
        } catch (Exception ex) {
            isSuccess = false;
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException ex1) {
                    Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex1) {
                    Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            System.out.println(ex.getMessage());
        }
        
        if(isSuccess)
            return user_id_db;
        return "";
    }
    
     public boolean getAvailableUser(String user_id) {

        boolean isSuccess = false;
        String user_id_db = "";

        String Sql = "SELECT * FROM  user t where t.user_id='" + user_id + "'";
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {

            pst = dbConnecter().prepareStatement(Sql);
            rs = pst.executeQuery();
            int i = 0;
            if (rs.next()) {
                user_id_db = rs.getString("user_id");
                i++;
            }
            if (user_id_db != null) {
                if (!user_id_db.isEmpty()) {
                    System.out.println(i + " Record Found");
                    isSuccess = true;
                } else {
                    isSuccess = false;
                    System.out.println(i + " Record not Found");
                }

            }
            pst.close();
            rs.close();
        } catch (Exception ex) {
            isSuccess = false;
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException ex1) {
                    Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex1) {
                    Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            System.out.println(ex.getMessage());
        }
        return isSuccess;
    }

    public boolean checkUserInformationFromDB(String user_id, String password) {

        boolean isSuccess = false;
        String user_id_db = "";

        String Sql = "SELECT * FROM  user t where t.user_id='" + user_id + "' and t.password='" + password + "'";
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {

            pst = dbConnecter().prepareStatement(Sql);
            rs = pst.executeQuery();
            int i = 0;
            if (rs.next()) {
                user_id_db = rs.getString("user_id");
                i++;
            }
            if (user_id_db != null) {
                if (!user_id_db.isEmpty()) {
                    System.out.println(i + " Record Found");
                    isSuccess = true;
                } else {
                    isSuccess = false;
                    System.out.println(i + " Record not Found");
                }

            }
            pst.close();
            rs.close();
        } catch (Exception ex) {
            isSuccess = false;
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException ex1) {
                    Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex1) {
                    Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            System.out.println(ex.getMessage());
        }
        return isSuccess;
    }

    public String performTransaction(String user_id, String from_account_no, String to_account_no, String amount) {

        String isSuccess = "0";
        System.out.println("Update action in DB: Amount " + amount + " Transfering " + "From " + from_account_no + " to " + to_account_no);
        isSuccess = performOnlineTrans(user_id, from_account_no, to_account_no, amount);
        return isSuccess;

    }

    public String performATMTransaction(String user_id, String from_account_no, String amount, String CardNo, String CardPIN) {

        String isSuccess = "0";
        System.out.println("Update action in DB: Amount " + amount + " Withdrawing " + "From " + from_account_no + " with Card No-:" + CardNo);
        isSuccess = performAtmTrans(user_id, from_account_no, amount, CardNo, CardPIN);
        return isSuccess;

    }

    public String performOnlineTrans(String user_id, String from_account_no, String to_account_no, String amount) {
        String errorMessage = "0";
        String Call = "{ call do_online_transaction(?,?,?,?,?) }";
        CallableStatement cs = null;
        try {

            cs = dbConnecter().prepareCall(Call);
            // Step-3: set input parameters ...
            cs.setString(1, from_account_no);
            cs.setString(2, to_account_no);
            cs.setString(3, amount);
            //cs.setString(4, user_id);

            // Step-4: register output parameters ...
            cs.registerOutParameter(4, java.sql.Types.DECIMAL);
            cs.registerOutParameter(5, java.sql.Types.VARCHAR);
            // Step-5: execute the stored procedures: proc3
            boolean hadResults = cs.execute();
            // Step-6: extract the output parameters

            Double current_balamce = 0.0;
            String isSuccess = "0";
            //while (hadResults) {
            //ResultSet rs = cs.getResultSet();
            //   hadResults = cs.getMoreResults();

            current_balamce = cs.getDouble("current_available_balance");
            isSuccess = cs.getString("isSuccess");
            // }

            System.out.println("Status" + isSuccess + "Current Balance after update =" + current_balamce);

            // Step-7: get ParameterMetaData
            /*ParameterMetaData pmeta = cs.getParameterMetaData();
             if (pmeta == null) {
             System.out.println("Vendor does not support ParameterMetaData");
             } else {
             System.out.println(pmeta.getParameterType(1));
             }
             */
            errorMessage = isSuccess;

        } catch (Exception ex) {
            errorMessage = ex.getMessage();
            System.out.println("SQL Exception in procedure: " + ex.getMessage());
            errorMessage = "0";
        } finally {
            //errorMessage = "0";
            try {
                cs.close();
            } catch (SQLException ex) {
                Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return errorMessage;

    }

    public String performAtmTrans(String user_id, String from_account_no, String amount, String CardNo, String CardPIN) {
        String errorMessage = "0";
        String Call = "{ call do_atm_transaction(?,?,?,?,?) }";
        CallableStatement cs = null;
        try {

            cs = dbConnecter().prepareCall(Call);
            // Step-3: set input parameters ...
            cs.setString(1, amount);
            cs.setString(2, CardNo);
            cs.setString(3, CardPIN);

            // Step-4: register output parameters ...
            cs.registerOutParameter(4, java.sql.Types.DECIMAL);
            cs.registerOutParameter(5, java.sql.Types.VARCHAR);
            // Step-5: execute the stored procedures: proc3
            boolean hadResults = cs.execute();
            // Step-6: extract the output parameters

            Double current_balamce = 0.0;
            String isSuccess = "0";
            //while (hadResults) {
            //ResultSet rs = cs.getResultSet();
            //   hadResults = cs.getMoreResults();

            current_balamce = cs.getDouble("current_available_balance");
            isSuccess = cs.getString("isSuccess");
            // }

            System.out.println("Status" + isSuccess + "Current Balance after update =" + current_balamce);

            // Step-7: get ParameterMetaData
            /*ParameterMetaData pmeta = cs.getParameterMetaData();
             if (pmeta == null) {
             System.out.println("Vendor does not support ParameterMetaData");
             } else {
             System.out.println(pmeta.getParameterType(1));
             }
             */
            errorMessage = isSuccess;

        } catch (Exception ex) {
            errorMessage = ex.getMessage();
            System.out.println("SQL Exception in procedure: " + ex.getMessage());
            errorMessage = "0";
        } finally {
            //errorMessage = "0";
            try {
                cs.close();
            } catch (SQLException ex) {
                Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return errorMessage;

    }
    
    public String getAccountNoFromPIN(String card,String pin) {

        boolean isSuccess = false;
        String account_no = "";

        String Sql = "SELECT  concat(t.branch_id,'-' , t.account_no) AS account_no FROM atm_card_info t WHERE t.card_number = '"+card+"'"  ;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {

            pst = dbConnecter().prepareStatement(Sql);
            rs = pst.executeQuery();
            int i = 0;
            if (rs.next()) {
                account_no = rs.getString("account_no");
                i++;
            }
            if (account_no != null) {
                if (!account_no.isEmpty()) {
                    //System.out.println(i + " Record Found");
                    isSuccess = true;
                } else {
                    isSuccess = false;
                    //System.out.println(i + " Record not Found");
                }

            }
            pst.close();
            rs.close();
        } catch (Exception ex) {
            isSuccess = false;
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException ex1) {
                    Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex1) {
                    Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            //System.out.println(ex.getMessage());
        }
        return account_no;
    }
    
     public String getAccountBalance(String branch_id, String account_no) {

        boolean isSuccess = false;
        String balance = "";

        String Sql = "SELECT available_balance FROM  account t where t.branch_id='" + branch_id + "' and t.account_no='" + account_no + "'";
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {

            pst = dbConnecter().prepareStatement(Sql);
            rs = pst.executeQuery();
            int i = 0;
            if (rs.next()) {
                balance = rs.getString("available_balance");
                i++;
            }
            if (balance != null) {
                if (!balance.isEmpty()) {
                    //System.out.println(i + " Record Found");
                    isSuccess = true;
                } else {
                    isSuccess = false;
                    //System.out.println(i + " Record not Found");
                }

            }
            pst.close();
            rs.close();
        } catch (Exception ex) {
            isSuccess = false;
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException ex1) {
                    Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex1) {
                    Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            //System.out.println(ex.getMessage());
        }
        return balance;
    }
     
     public boolean isValidAccount(String branch_id, String account_no) {

        boolean isSuccess = false;
        String balance = "";

        String Sql = "SELECT available_balance FROM  account t where t.branch_id='" + branch_id + "' and t.account_no='" + account_no + "'";
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {

            pst = dbConnecter().prepareStatement(Sql);
            rs = pst.executeQuery();
            int i = 0;
            if (rs.next()) {
                balance = rs.getString("available_balance");
                i++;
            }
            if (balance != null) {
                if (!balance.isEmpty()) {
                    //System.out.println(i + " Record Found");
                    isSuccess = true;
                } else {
                    isSuccess = false;
                    //System.out.println(i + " Record not Found");
                }

            }
            pst.close();
            rs.close();
        } catch (Exception ex) {
            isSuccess = false;
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException ex1) {
                    Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex1) {
                    Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
            //System.out.println(ex.getMessage());
        }
        return isSuccess;
    }

}
