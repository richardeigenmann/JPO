/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * YearsBrowser.java
 *
 * Created on 02-Apr-2009, 20:21:00
 */

package jpo.gui;

import javax.swing.JPanel;

/**
 *
 * @author Richard Eigenmann
 */
public class YearsBrowser extends javax.swing.JFrame {

    /** Creates new form YearsBrowser */
    public YearsBrowser() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        yearspanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        yearspanel.setMaximumSize(new java.awt.Dimension(500, 500));
        yearspanel.setPreferredSize(new java.awt.Dimension(500, 500));

        javax.swing.GroupLayout yearspanelLayout = new javax.swing.GroupLayout(yearspanel);
        yearspanel.setLayout(yearspanelLayout);
        yearspanelLayout.setHorizontalGroup(
            yearspanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 679, Short.MAX_VALUE)
        );
        yearspanelLayout.setVerticalGroup(
            yearspanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 548, Short.MAX_VALUE)
        );

        jScrollPane1.setViewportView(yearspanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new YearsBrowser().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel yearspanel;
    // End of variables declaration//GEN-END:variables


    /**
     * Returns the display panel
     * @return the display panel
     */
    public JPanel getDisplayPanel() {
        return yearspanel;
    }
}
