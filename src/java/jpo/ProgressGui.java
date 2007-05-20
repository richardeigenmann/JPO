package jpo;

import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.*;
import javax.imageio.stream.*;
import javax.swing.Timer;


/*
ProgressGui.java:  a class that shows the progress in adding pictures

Copyright (C) 2002-2007  Richard Eigenmann.
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or any later version. This program is distributed 
in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS 
FOR A PARTICULAR PURPOSE.  See the GNU General Public License for 
more details. You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
The license is in gpl.txt.
See http://www.gnu.org/copyleft/gpl.html for the details.
*/


/**
 *   a private class that allows the PictureAdder to show what it is doing.
 */
public class ProgressGui extends JFrame implements ActionListener {
	/**
	 *  button to start the export
	 **/
	private JButton okJButton = new JButton ( Settings.jpoResources.getString("genericOKText") );

	/**
	 *  button to cancel the dialog
	 **/
	private JButton cancelJButton = new JButton ( Settings.jpoResources.getString("genericCancelText") );


	/**
	 *  Progress Indicator
	 */
	private JProgressBar progBar;
	
	/**
	 *  Label
	 */
	private JLabel progLabel;

	/**
	 *  variable that is checked periodically that stops the addDirectory loop in a controlled way
 	*/
	public boolean interrupt = false;


	/**
	 *  The string that should be shown after completion. Something like "12 pictures added".
	 */
	private String doneString;

	/**
	 * how long the gui should show after it has finished.
	 */
	private int timeout = 60000;
	

	/**
	 *  Constructor for a progress GUI
	 *
	 *  @param  max   The total to count to
	 *  @param  title  The Title to be shown in the dialog
	 *  @param  doneString  The text to show when done.
	 *
	 */
	public ProgressGui( int max, String title, String doneString ) {
		this.doneString = doneString;
		setTitle ( title );
		setLocationRelativeTo ( Settings.anchorFrame );
		setDefaultCloseOperation( DISPOSE_ON_CLOSE );
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if ( okJButton.isVisible() )
					getRid();
				else
					interrupt = true;
			}
	        });  

		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(4, 4, 4, 4);

		JPanel contentJPanel = new JPanel();
		contentJPanel.setLayout(new GridBagLayout());
		contentJPanel.setPreferredSize( new Dimension(250, 100) );
		getContentPane().add( contentJPanel );

		progBar = new JProgressBar( 0, max );
		progBar.setBorder( BorderFactory.createLineBorder( Color.gray, 1 ) );
		progBar.setStringPainted( true );
		progBar.setPreferredSize( new Dimension(140, 20) );
		progBar.setMaximumSize( new Dimension(240, 20) );
		progBar.setMinimumSize( new Dimension(140, 20) );
		progBar.setValue(0);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.NONE;
		contentJPanel.add( progBar, constraints );	

		progLabel = new JLabel();
		progLabel.setPreferredSize( new Dimension( 160,20 ) );
		progLabel.setBorder( BorderFactory.createEmptyBorder( 5,5,5,5 ) );
		progLabel.setHorizontalAlignment( javax.swing.SwingConstants.CENTER );
		//progLabel.setVisible( false );
		constraints.gridy++;
		contentJPanel.add( progLabel, constraints );


		okJButton.setPreferredSize( Settings.defaultButtonDimension );
	        okJButton.setMinimumSize( Settings.defaultButtonDimension );
        	okJButton.setMaximumSize( Settings.defaultButtonDimension );
		okJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
		okJButton.setDefaultCapable( true );
	        okJButton.addActionListener( this );
		constraints.gridy++;
		contentJPanel.add( okJButton, constraints );	

		cancelJButton.setPreferredSize( Settings.defaultButtonDimension );
        	cancelJButton.setMinimumSize( Settings.defaultButtonDimension );
	        cancelJButton.setMaximumSize( Settings.defaultButtonDimension );
		cancelJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
		cancelJButton.setDefaultCapable( true );
        	cancelJButton.addActionListener( this );
		constraints.gridy++;
		contentJPanel.add( cancelJButton, constraints );	

		okJButton.setVisible( false );
		this.getRootPane().setDefaultButton ( cancelJButton );
		
		pack();
		setVisible(true);
	}


	/**
	 *  method that closes te frame and gets rid of it
	 */
	private void getRid() {
		setVisible ( false );
		dispose ();
	}


	/** 
	 *  method that analyses the user initiated action and performs what the user requested
	 **/
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancelJButton) 
			interrupt = true;
		else 	if (e.getSource() == okJButton) {
			getRid();
		} 
	}


	
	/**
	 *  Adds 1 to the progress counter
	 */
	public void progressIncrement() {
		progBar.setValue( progBar.getValue() + 1 );
		progLabel.setText( Integer.toString( progBar.getValue() ) 
			+ " / " 
			+ Integer.toString( progBar.getMaximum() ));
	}

	/**
	 *  decreses the total by 1
	 */
	public void decrementTotal() {
		progBar.setMaximum( progBar.getMaximum() - 1 );
		progLabel.setText( Integer.toString( progBar.getValue() ) 
			+ " / " 
			+ Integer.toString( progBar.getMaximum() ));
	}



	/** 
	 *  removes the Cancel Button and adds an ok button
	 */
	public void switchToDoneMode() {
		okJButton.setVisible( true );
		cancelJButton.setVisible( false );
		//progBar.setVisible( false );
		//progLabel.setVisible( true );
		progLabel.setText( Integer.toString( progBar.getValue() ) + doneString );
		
		
		Timer timer = new Timer( timeout, new ActionListener() {
			public void actionPerformed (ActionEvent evt ) {
				getRid();
			}    
		});
		timer.setRepeats( false );
		timer.start();
	}
	

}

