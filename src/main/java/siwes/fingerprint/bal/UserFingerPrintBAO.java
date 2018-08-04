package siwes.fingerprint.bal;

import java.util.List;
import siwes.fingerprint.dal.UserFingerPrintDAO;
import siwes.fingerprint.model.UserFingerPrint;

public class UserFingerPrintBAO {
	
	
    public UserFingerPrintBAO(){

    }
	
    public List<UserFingerPrint> getAllUserFingerPrints(){
        UserFingerPrintDAO userFingerPrintDAO = new UserFingerPrintDAO();
        return userFingerPrintDAO.getAllUserFingerPrints();
    }
    public int saveFingerPrints(UserFingerPrint userFingerPrint){
        UserFingerPrintDAO userFingerPrintDAO = new UserFingerPrintDAO();
        return userFingerPrintDAO.saveFingerPrints(userFingerPrint);
    }

}
