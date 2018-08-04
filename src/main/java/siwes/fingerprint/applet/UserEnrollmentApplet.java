package siwes.fingerprint.applet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import siwes.fingerprint.bal.UserFingerPrintBAO;
import siwes.fingerprint.model.UserFingerPrint;
import siwes.fingerprint.db.DBConnection;

import com.digitalpersona.onetouch.DPFPCaptureFeedback;
import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPImageQualityAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPImageQualityEvent;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import com.digitalpersona.onetouch.capture.event.DPFPSensorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPSensorEvent;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;

public class UserEnrollmentApplet extends JApplet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4988022778791170606L;
        private String msg;
        
        private DBConnection dbConn = new DBConnection();

	private DPFPCapture capturer = DPFPGlobal.getCaptureFactory().createCapture();
	private DPFPEnrollment enroller = DPFPGlobal.getEnrollmentFactory().createEnrollment();
	private DPFPTemplate template;
	private int userID;
	private JButton startEnrollment = new JButton("Scan Finger");
	
	private JButton saveEnrollment = new JButton("Save FingerPrint");
	
	private JLabel picture = new JLabel();
	private JTextField prompt = new JTextField();
	private JTextArea log = new JTextArea();
	private JTextField status = new JTextField("[status line]");
	
 
	
	public void init(){
		userID = 0;
		try{
			userID = Integer.parseInt(getParameter("userId"));
		}catch(NumberFormatException e){
			
		}
		setUserID(userID);

		this.generateUI();
		
		updateStatus();
		
		capturer.addDataListener(new DPFPDataAdapter() {
			@Override public void dataAcquired(final DPFPDataEvent e) {
				SwingUtilities.invokeLater(new Runnable() {	public void run() {
					makeReport("The fingerprint sample was captured.");
					setPrompt("Scan the same fingerprint again.");
					process(e.getSample());
				}});
			}
		});
		
		//Check that the scanner is connected
		capturer.addReaderStatusListener(new DPFPReaderStatusAdapter() {
			@Override public void readerConnected(final DPFPReaderStatusEvent e) {
				SwingUtilities.invokeLater(new Runnable() {	public void run() {
		 			makeReport("The fingerprint reader was connected.");
				}});
			}
			@Override public void readerDisconnected(final DPFPReaderStatusEvent e) {
				SwingUtilities.invokeLater(new Runnable() {	public void run() {
					makeReport("The fingerprint reader was disconnected.");
				}});
			}
		});
		
		
		capturer.addSensorListener(new DPFPSensorAdapter() {
                    @Override public void fingerTouched(final DPFPSensorEvent e) {
                        SwingUtilities.invokeLater(new Runnable() {	public void run() {
                                makeReport("The fingerprint reader was touched.");
                        }});
                    }
                    @Override public void fingerGone(final DPFPSensorEvent e) {
                        SwingUtilities.invokeLater(new Runnable() {	public void run() {
                                makeReport("The finger was removed from the fingerprint reader.");
                        }});
                    }
		});
		
		capturer.addImageQualityListener(new DPFPImageQualityAdapter() {
                    @Override public void onImageQuality(final DPFPImageQualityEvent e) {
                        SwingUtilities.invokeLater(new Runnable() {	public void run() {
                            if (e.getFeedback().equals(DPFPCaptureFeedback.CAPTURE_FEEDBACK_GOOD))
                                    makeReport("The quality of the fingerprint sample is good.");
                            else
                                    makeReport("The quality of the fingerprint sample is poor.");
                        }});
                    }
		});
	}
	
	public void generateUI(){
            setLayout(new BorderLayout());
            rootPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            this.startEnrollment.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent actionEventInstance) {
                    scanUserFingerPrint();
                    startEnrollment.setEnabled(false);
                }

            });

            this.saveEnrollment.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {

                    saveUserFingerPrintTemplate(getUserID());
                }

            });


            picture.setPreferredSize(new Dimension(240, 280));
            picture.setBorder(BorderFactory.createLoweredBevelBorder());
            prompt.setFont(UIManager.getFont("Panel.font"));
            prompt.setEditable(false);
            prompt.setColumns(40);
            prompt.setMaximumSize(prompt.getPreferredSize());
            prompt.setBorder(
                            BorderFactory.createCompoundBorder(
                                    BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), "Prompt:"),
                                    BorderFactory.createLoweredBevelBorder()
                            ));
            log.setColumns(40);
            log.setEditable(false);
            log.setFont(UIManager.getFont("Panel.font"));
            JScrollPane logpane = new JScrollPane(log);
            logpane.setBorder(
                            BorderFactory.createCompoundBorder(
                                    BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), "Status:"),
                                    BorderFactory.createLoweredBevelBorder()
                            ));

            status.setEditable(false);
            status.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            status.setFont(UIManager.getFont("Panel.font"));

            JButton quit = new JButton("Close");
            quit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { 
                    setVisible(false);
                    stopScanning();
                    }});
		
		JPanel top = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		top.setBackground(Color.getColor("control"));
		top.add(startEnrollment);
		top.add(this.saveEnrollment);
		this.saveEnrollment.setEnabled(false);
		
		
		JPanel right = new JPanel(new BorderLayout());
		right.setBackground(Color.getColor("control"));
		right.add(prompt, BorderLayout.PAGE_START);
		right.add(logpane, BorderLayout.CENTER);
		
		JPanel center = new JPanel(new BorderLayout());
		center.setBackground(Color.getColor("control"));
		center.add(right, BorderLayout.CENTER);
		center.add(picture, BorderLayout.LINE_START);
		center.add(status, BorderLayout.PAGE_END);
			
		JPanel bottom = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		bottom.setBackground(Color.getColor("control"));
		bottom.add(quit);

		setLayout(new BorderLayout());
		add(top,BorderLayout.PAGE_START);
		add(center, BorderLayout.CENTER);
		add(bottom, BorderLayout.PAGE_END);
		
		this.setSize(650, 300);
		
	}
	
	protected void process(DPFPSample sample)
	{
            // Draw fingerprint sample image.
            drawPicture(convertSampleToBitmap(sample));

            // Process the sample and create a feature set for the enrollment purpose.
            DPFPFeatureSet features = extractFeatures(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);

            // Check quality of the sample and add to enroller if it's good
            if (features != null) try
            {
                    makeReport("The fingerprint feature set was created.");
                    enroller.addFeatures(features);		// Add feature set to template.
            }
            catch (DPFPImageQualityException ex) { }
            finally {
                    updateStatus();

                    // Check if template has been created.
                    switch(enroller.getTemplateStatus())
                    {
                            case TEMPLATE_STATUS_READY:	// report success and stop capturing
                                    stop();
                                    //((MainForm)getOwner()).setTemplate(enroller.getTemplate());
                                    setTemplate(enroller.getTemplate());
                                    setPrompt("NEXT, save the fingerprint .");
                                    this.saveEnrollment.setEnabled(true);
                                    break;

                            case TEMPLATE_STATUS_FAILED:	// report failure and restart capturing
                                    enroller.clear();
                                    stop();
                                    updateStatus();
                                    //((MainForm)getOwner()).setTemplate(null);
                                    //setDummyTemplate(null);
                                    JOptionPane.showMessageDialog(this, "The fingerprint template is not valid. Repeat fingerprint enrollment.", "Fingerprint Enrollment", JOptionPane.ERROR_MESSAGE);
                                    start();
                                    break;
                            default:
                                    break;
                    }
            }
	}
	
 
	
	protected void scanUserFingerPrint(){
            //cancel thread if it running, and start capture again

            capturer.startCapture();
            setPrompt("Using the fingerprint reader, scan your fingerprint.");
	}
	protected void stopScanning(){
		
            capturer.stopCapture();
		
	}
	
	protected void restartScanning(){
            start();
            capturer.stopCapture();
            capturer.startCapture();
	}
	public void generateUserFingerPrintTemplate(){
		
	}
	
	public void saveUserFingerPrintTemplate(int userID){
            try{
                String dbUser = getParameter("dbUser");
                String dbPass = getParameter("dbPassword");
                String db = getParameter("dbName");
                dbConn.setDBUser(dbUser);
                dbConn.setPass(dbPass);
                dbConn.setDB(db);
                dbConn.setUserId(userID);

                // obtain fingerprint image
                ByteArrayInputStream imageStream = new ByteArrayInputStream(getTemplate().serialize());
                Integer imageSize = getTemplate().serialize().length;

//                if(dbConn.SaveFingerPrint(imageStream, imageSize))
//                {
//                    setPrompt("user fingerprint successfully enrolled!! ");
//                    JOptionPane.showConfirmDialog(this, "user fingerprint successfully enrolled!! ");
//                    this.startEnrollment.setEnabled(true);
//                }else{
//                    throw new Exception();
//                }

                msg = dbConn.SaveFingerPrint(imageStream, imageSize);

                }catch(Exception e){
                        //setPrompt("-----Ooops, "+e.getMessage());
                        JOptionPane.showConfirmDialog(this, msg);
                }finally{
                        this.saveEnrollment.setEnabled(false);
                        JOptionPane.showConfirmDialog(this, msg);
                }
      
	}
	
	public void setStatus(String string) {
            status.setText(string);
	}
	private void updateStatus()
	{
            // Show number of samples needed.
            setStatus(String.format("Fingerprint samples needed: %1$s", enroller.getFeaturesNeeded()));
		
	}
	public void setPrompt(String string) {
            prompt.setText(string);
	}
	
	public void makeReport(String string) {
            log.append(string + "\n");
	}
	
	public void drawPicture(Image image) {
            picture.setIcon(new ImageIcon(
                    image.getScaledInstance(picture.getWidth(), picture.getHeight(), Image.SCALE_DEFAULT)));
	}
	
	protected Image convertSampleToBitmap(DPFPSample sample) {
            return DPFPGlobal.getSampleConversionFactory().createImage(sample);
	}

	
	
	protected DPFPFeatureSet extractFeatures(DPFPSample sample, DPFPDataPurpose purpose)
	{
            DPFPFeatureExtraction extractor = DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
            try {
                    return extractor.createFeatureSet(sample, purpose);
            } catch (DPFPImageQualityException e) {
                    return null;
            }
	}

	public DPFPTemplate getTemplate() {
            return template;
	}

	public void setTemplate(DPFPTemplate template) {
            this.template = template;
	}

	public int getUserID() {
            return userID;
	}

	public void setUserID(int userID) {
            this.userID = userID;
	}
	
	
}
