package jpo;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;



/**
 *  Constructor for a progress GUI for the XML reader
 *
 */
public class LoadProgressGui extends JFrame {
    
    private boolean interrupt = false;
    
    JLabel progLabel = new JLabel();
    
    /**
     *  Constructor for the LoadProgressGui
     */
    public LoadProgressGui() {
        setTitle( "Loading file" );
        setLocationRelativeTo( Settings.anchorFrame );
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                interrupt = true;
            }
        });
        
        
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(4, 4, 4, 4);
        
        JPanel contentJPanel = new JPanel();
        contentJPanel.setLayout(new GridBagLayout());
        contentJPanel.setPreferredSize( new Dimension(260, 30) );
        getContentPane().add( contentJPanel );
        
        progLabel.setPreferredSize( new Dimension( 250,20 ) );
        progLabel.setBorder( BorderFactory.createEmptyBorder( 5,5,5,5 ) );
        progLabel.setHorizontalAlignment( javax.swing.SwingConstants.CENTER );
        //progLabel.setVisible( false );
        constraints.gridy++;
        contentJPanel.add( progLabel, constraints );
        
        pack();
        setVisible(true);
    }
    
    public void update( String message ) {
        progLabel.setText( message );
    }
    
    /**
     *  method that closes te frame and gets rid of it
     */
    public void getRid() {
        setVisible( false );
        dispose();
    }
    
}



