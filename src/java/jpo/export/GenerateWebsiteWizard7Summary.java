package jpo.export;

import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import jpo.Settings;
import net.javaprog.ui.wizard.AbstractStep;

/*
GenerateWebsiteWizard7Summary.java:  Summarise before you go and do it

Copyright (C) 2008  Richard Eigenmann.
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
 * This Wizard step summarises the settings and then goes off and does them
 * @author Richard Eigenmann
 */
public class GenerateWebsiteWizard7Summary extends AbstractStep {

    /**
     * The link to the values that this panel should change
     */
    private final HtmlDistillerOptions options;

    /**
     * This Wizard step summarises the settings and then goes off and does them
     * @param options
     */
    public GenerateWebsiteWizard7Summary( HtmlDistillerOptions options ) {
        super( Settings.jpoResources.getString( "summary" ), Settings.jpoResources.getString( "summary" ) );
        this.options = options;
    }
    /**
     * Shows the summary
     */
    protected JTextArea summary = new JTextArea();

    /**
     * Returns the widgets to show the summary
     * @return the component that shows the summary
     */
    @Override
    protected JComponent createComponent() {
        JPanel wizardStep = new JPanel();

        JScrollPane sp = new JScrollPane( summary );
        sp.setPreferredSize( new Dimension( 300, 200 ) );
        sp.setMaximumSize( new Dimension( 300, 200 ) );
        sp.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS );
        sp.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );
        wizardStep.add( sp );
        return wizardStep;
    }

    /**
     * Refreshes the contents of the widgets
     */
    public void prepareRendering() {
        summary.setText( options.toString() );
        setCanFinish( true );
    }
}
