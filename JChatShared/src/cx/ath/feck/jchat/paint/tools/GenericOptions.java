/*
 * GenericOptions.java
 *
 * Created on May 23, 2007, 9:13 AM
 */

package cx.ath.feck.jchat.paint.tools;

/**
 *
 * @author  john
 */
public class GenericOptions extends javax.swing.JPanel {
	
	/** Creates new form GenericOptions */
	public GenericOptions() {
		initComponents();
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jSlider1 = new javax.swing.JSlider();
        jSlider2 = new javax.swing.JSlider();
        jSlider3 = new javax.swing.JSlider();
        jSlider4 = new javax.swing.JSlider();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jSlider1.setForeground(java.awt.Color.red);
        jSlider1.setMaximum(255);
        jSlider1.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider1.setPreferredSize(new java.awt.Dimension(28, 100));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        jPanel1.add(jSlider1, gridBagConstraints);

        jSlider2.setForeground(java.awt.Color.green);
        jSlider2.setMaximum(255);
        jSlider2.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider2.setPaintLabels(true);
        jSlider2.setPreferredSize(new java.awt.Dimension(28, 100));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        jPanel1.add(jSlider2, gridBagConstraints);

        jSlider3.setForeground(java.awt.Color.blue);
        jSlider3.setMaximum(255);
        jSlider3.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider3.setPaintLabels(true);
        jSlider3.setPreferredSize(new java.awt.Dimension(28, 100));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        jPanel1.add(jSlider3, gridBagConstraints);

        jSlider4.setMaximum(255);
        jSlider4.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider4.setPaintLabels(true);
        jSlider4.setPreferredSize(new java.awt.Dimension(28, 100));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        jPanel1.add(jSlider4, gridBagConstraints);

        jTextField1.setText("jTextField1");
        jTextField1.setPreferredSize(new java.awt.Dimension(30, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        jPanel1.add(jTextField1, gridBagConstraints);

        jTextField2.setText("jTextField2");
        jTextField2.setPreferredSize(new java.awt.Dimension(30, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        jPanel1.add(jTextField2, gridBagConstraints);

        jTextField3.setText("jTextField3");
        jTextField3.setPreferredSize(new java.awt.Dimension(30, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        jPanel1.add(jTextField3, gridBagConstraints);

        jTextField4.setText("jTextField3");
        jTextField4.setPreferredSize(new java.awt.Dimension(30, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        jPanel1.add(jTextField4, gridBagConstraints);

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.setPreferredSize(new java.awt.Dimension(30, 30));
        jPanel2.setLayout(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel1.add(jPanel2, gridBagConstraints);

        add(jPanel1, new java.awt.GridBagConstraints());
    }// </editor-fold>//GEN-END:initComponents
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JSlider jSlider2;
    private javax.swing.JSlider jSlider3;
    private javax.swing.JSlider jSlider4;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    // End of variables declaration//GEN-END:variables
	
}
