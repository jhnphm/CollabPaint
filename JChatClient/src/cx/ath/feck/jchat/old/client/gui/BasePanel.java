/*
 * BasePanel.java
 *
 * Created on April 19, 2007, 6:05 PM
 */

package cx.ath.feck.jchat.old.client.gui;

import cx.ath.feck.jchat.old.client.Singletons;
import java.awt.CardLayout;
import java.awt.Component;
import javax.swing.SwingUtilities;

/**
 * Root panel under applet, keeps all subpanels inside a cardlayout.
 * Depends on: MainPanel, LoginConfirmPanel, LoginPanel
 * @author John Pham
 */
public class BasePanel extends javax.swing.JPanel {
	
	/**
	 * Creates new form BasePanel. Set all the panels, then call setPanels() before use.
	 */
	public BasePanel() {
		initComponents();
		this.setPanels();
		this.setState(BasePanel.LOGIN);
		SwingUtilities.invokeLater(new Runnable(){

            public void run() {
                Singletons.getClientCore().connect();
            }
		});
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">                          
    	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">                          
    	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">                          
    	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">                          
    	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">                          
    	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">                          
    	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">                          
    	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">                          
    	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.CardLayout());

        setPreferredSize(new java.awt.Dimension(640, 480));
    }// </editor-fold>//GEN-END:initComponents
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
	
	/**
	 * Constant parameter for setState()- used to display login window.
	 */
	public static final int   LOGIN             = 0;
	/**
	 * Constant parameter for setState()- used to display status window.
	 */
	public static final int   DISPLAYSTATUS      = 1;
	/**
	 * Constant parameter for setState()- used when logged in.
	 */
	public static final int   LOGGEDIN          = 2;
	/**
	 * Constant parameter for setState()- used when login w/ admin.
	 */
	public static final int   LOGGEDINASADMIN   = 3;
	
	
	
	private CardLayout cardLayout;
	
	/**
	 * Sets state, use the LOGIN,DISPLAYSTATUS,LOGGEDIN, or LOGGEDINASADMIN constants as the parameter.
	 * @param state Sets state of window
	 */
	public void setState(final int state) {
		switch (state) {
		case LOGIN :
			cardLayout.show(BasePanel.this,"loginPanel");
			break;
			
		case DISPLAYSTATUS :
			cardLayout.show(BasePanel.this,"loginConfirmPanel");
			break;
			
		case LOGGEDIN :
			cardLayout.show(BasePanel.this,"mainPanel");
			break;
			
		case LOGGEDINASADMIN :
			Singletons.getMainPanel().setIsAdmin(true);
			cardLayout.show(BasePanel.this,"mainPanel");
			break;
		default:
		}
		
	}
	
	/**
	 * Actually adds panels to layout, run after {@link #setLoginPanel setLoginPanel()}, Is thread safe.
	 * {@link #setLoginConfirmPanel setLoginConfirmPanel()}, and
	 * {@link #setMainPanel setMainPanel()} are all run.
	 */
	public void setPanels(){
		BasePanel.this.setLayout(cardLayout = new CardLayout());
		for(Component c: BasePanel.this.getComponents()){
			BasePanel.this.remove(c);
		}
		BasePanel.this.add(Singletons.getLoginPanel(),"loginPanel");
		BasePanel.this.add(Singletons.getLoginConfirmPanel(),"loginConfirmPanel");
		BasePanel.this.add(Singletons.getMainPanel(),"mainPanel");
	}
}
