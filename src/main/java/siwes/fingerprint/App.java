package siwes.fingerprint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import siwes.fingerprint.model.UserFingerPrint;
import siwes.fingerprint.utilities.HibernateUtil;


/**
 * Hello world!
 *
 */
public class App 
{
    @SuppressWarnings("unused")
	public static void main( String[] args )
    {
        System.out.println( "Hello World fingerprinters!" );
        String fingerPrintTemplate =  null;
        App app = new App();
        int fingerprint1 =app.storeFingerPrints(1001,fingerPrintTemplate);
        
        List<UserFingerPrint> userFingerPrints = new ArrayList<UserFingerPrint>();
        userFingerPrints = app.getAllUserFingerPrints();
        
        for(UserFingerPrint userFingerPrint : userFingerPrints){
          System.out.println("FingerPrint size is: "+userFingerPrint.getLeftIndex().length);
        }
    }
    @SuppressWarnings("unchecked")
	public List<UserFingerPrint> getAllUserFingerPrints(){
    	Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		
		List<UserFingerPrint> userFingerPrints = (List<UserFingerPrint>)session.createQuery(
				"FROM UserFingerPrint").list();
		
		session.getTransaction().commit();
		session.close();
		
		return userFingerPrints;
    }
    public int storeFingerPrints(int userID, String pathToFingerPrint){
    	UserFingerPrint userFingerPrint = new UserFingerPrint();
    	userFingerPrint.setUserId(userID);
    	
    	String absoluteFilePath = "";
    	absoluteFilePath = pathToFingerPrint;
    	File file = new File(absoluteFilePath);
    	byte[] leftIndexFinger = new byte[(int)file.length()];
    	
    	FileInputStream fileInputStream = null;
    	try {
			 fileInputStream = new FileInputStream(file);
			 fileInputStream.read(leftIndexFinger); 
			 fileInputStream.close();
			 
			 //add byteArray
			 userFingerPrint.setLeftIndex(leftIndexFinger);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(IOException io){
			
		}
    	
    	Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		int id = (Integer) session.save(userFingerPrint);
		session.getTransaction().commit();
		session.close();
		
		System.out.println("Fingerprint successfully captured...");
		return id;
    }
}
