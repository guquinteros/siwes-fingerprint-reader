/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package siwes.fingerprint.db;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Wilfred
 */
public class DBConnection {
    
    // db connection props
    public String port = "3306";
    public String host = "localhost";
    private String db;
    private String user;
    private String pass;
    
    public Connection conn = null;
    
    // fingerprint props
    private static final long serialVersionUID = 1589083633711580633L;
    private Integer id;
    private Integer userId;
    private byte[] image;
    
    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] fingerprint) {
        this.image = fingerprint;
    }
    
    public void setDBUser(String user){
        this.user = user;
    }
    public void setDB(String db){
        this.db = db;
    }
    public void setPass(String pass){
        this.pass = pass;
    }
    public Connection connect(){
        try{
            String driver = "jdbc:mysql://";
            String server = host+":"+port+"/";
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(driver+server+db,user,pass);
            if(conn != null){
                System.out.println("Database connection established");
            }
            else if (conn == null)
            {
                throw new SQLException();
            }
        }catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }catch(ClassNotFoundException e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }catch(NullPointerException e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }finally{
            return conn;
        }
    }
    
    public void Close(){
        conn = null;
    }
    
    public String SaveFingerPrint(ByteArrayInputStream imageStream, Integer imageSize){
        try{
            PreparedStatement saveFingerPrint = conn.prepareStatement(
                    "INSERT INTO user_finger_print (user_id, fingerprint) values(?,?)");
            saveFingerPrint.setInt(1, userId);
            saveFingerPrint.setBinaryStream(2, imageStream, imageSize);
            saveFingerPrint.execute();
            saveFingerPrint.close();
            return "Fingerprint Image Saved Successfully!";
            
        }catch(SQLException ex){
           return ex.getMessage(); 
        }
       
    }
}
