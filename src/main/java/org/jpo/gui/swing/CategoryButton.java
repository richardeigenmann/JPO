package org.jpo.gui.swing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/*
 Copyright (C) 2021  Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed
 in the hope that it will be useful, but WITHOUT ANY WARRANTY.
 Without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */

/**
 * A widget to show the category
 */

public class CategoryButton extends JPanel {
    private final JButton removeButton = new JButton("\uf057");

    final JButton categoryLabel;

    /**
     * Constructs the category button.
     *
     * @param categoryDescription The description of the category
     */
    public CategoryButton(final String categoryDescription) {

        categoryLabel = new JButton(categoryDescription) {
            @Override
            protected void paintComponent(final Graphics g) {
                final var arcs = new Dimension(15, 15);
                final var width = getWidth();
                final var height = getHeight();
                final var graphics = (Graphics2D) g;
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


                //Draws the rounded opaque panel with borders.
                graphics.setColor(getBackground());
                graphics.fillRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);//paint background
                graphics.setColor(getForeground());
                graphics.drawRoundRect(0, 0, width - 1, height - 1, arcs.width, arcs.height);//paint border
                super.paintComponent(g);
            }
        };

        categoryLabel.setBorder(new EmptyBorder(3, 4, 3, 4));
        categoryLabel.setContentAreaFilled(false);
        this.add(categoryLabel);

        removeButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        removeButton.setContentAreaFilled(false);
        removeButton.setFocusPainted(false);
        removeButton.setOpaque(false);
        removeButton.setFont(FontAwesomeFont.getFontAwesomeRegular18());
        this.add(removeButton);
        this.revalidate(); // makes the JScrollPanel reevaluate it's size so it grows
    }

    /**
     * Connects a RemovalListener to the button so that it can find out that the user clicked the remove icon
     *
     * @param actionListener the Listener for the event
     */
    public void addRemovalListener(final ActionListener actionListener) {
        removeButton.addActionListener(actionListener);
    }

    /**
     * Connects a Listener to handle the click events ont he button
     *
     * @param actionListener The Listener to handle the click events on the button.
     */
    public void addClickListener(final ActionListener actionListener) {
        categoryLabel.addActionListener(actionListener);
    }

}
