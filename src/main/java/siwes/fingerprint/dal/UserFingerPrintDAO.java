package siwes.fingerprint.dal;


import java.util.List;

import org.hibernate.Session;
import siwes.fingerprint.model.UserFingerPrint;
import siwes.fingerprint.utilities.HibernateUtil;

public class UserFingerPrintDAO {
	
	public UserFingerPrintDAO(){
		
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
	 
	    public int saveFingerPrints(UserFingerPrint userFingerPrint){
	    	Session session = HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();

                int id = (Integer) session.save(userFingerPrint);
                session.getTransaction().commit();
                session.close();

                return id;
	    }
}
