package org.jpo.gui.swing;

import com.google.common.eventbus.Subscribe;
import net.miginfocom.swing.MigLayout;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.Settings;
import org.jpo.eventbus.CategoriesWereModified;
import org.jpo.eventbus.CategoryAssignmentWindowRequest;
import org.jpo.eventbus.JpoEventBus;
import org.jpo.eventbus.OpenCategoryEditorRequest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2021 Richard Eigenmann.
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
 * Brings up a window showing all the available Categories and allows the user to assign or remove categories
 * for the nodes supplied in the CategoryAssignmnetWindowRequest.
 * <p>
 * Since there can be many categories they are shown as checkboxes in multiple columns. (Layout works left to
 * right but the categories should be shown alphabetically top to bottom in the columns which gave rise to the code
 * bloat in the populateCheckBoxes function.)
 * <p>
 * The checkboxes show whether none, all or some of the supplied nodes have the category.
 * The TristateCheckBox class has logic to allow selection or deselection on categories that have none or all.
 * Where some nodes have a category, the TristateCheckBox cycles through select all, deselect all and leave unchanged.
 * Elections where the node categoiry assignments will be changed are shown in a different color.
 *
 * @author Richard Eigenmann
 */
public class CategroyAssignmentWindow {

    public static final String CATEGORY = "Category";
    private static final Logger LOGGER = Logger.getLogger(CategroyAssignmentWindow.class.getName());
    private static final int COLUMNS = 8;
    private final JFrame frame = new JFrame();
    private final JPanel categoriesPanel = new JPanel();
    private final CategoryAssignmentWindowRequest request;
    private final KeyListener escapeKeyListener = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {
            // don't need
        }

        @Override
        public void keyPressed(KeyEvent e) {
            // don't need
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                frame.dispose();
            }
        }
    };

    public CategroyAssignmentWindow(final CategoryAssignmentWindowRequest request) {
        this.request = request;
        EventQueue.invokeLater(() -> {
            frame.setTitle(String.format("Category assignment for %d pictures", request.nodes().size()));

            final var outerPanel = new JPanel();
            outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));
            final var titlePanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
            final var title = new JLabel(String.format("%d Nodes selected", request.nodes().size()));
            title.setFont(RobotoFont.getFontRobotoThin24());
            titlePanel.add(title);
            outerPanel.add(titlePanel);

            categoriesPanel.setLayout(new MigLayout("wrap " + COLUMNS));

            final var jscrollPanel = new JScrollPane(categoriesPanel);
            outerPanel.add(jscrollPanel);

            final var buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
            final var editCategoriesJButton = new JButton("Edit");
            editCategoriesJButton.setPreferredSize(Settings.getDefaultButtonDimension());
            editCategoriesJButton.addActionListener(e -> JpoEventBus.getInstance().post(new OpenCategoryEditorRequest()));
            final var cancelJButton = new JButton("Cancel");
            cancelJButton.setPreferredSize(Settings.getDefaultButtonDimension());
            cancelJButton.addActionListener(e -> frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING)));
            final var okJButton = new JButton("Save");
            okJButton.setPreferredSize(Settings.getDefaultButtonDimension());
            okJButton.addActionListener(e -> {
                saveChanges(categoriesPanel, request);
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
            });
            buttonPanel.add(editCategoriesJButton);
            buttonPanel.add(cancelJButton);
            buttonPanel.add(okJButton);
            outerPanel.add(buttonPanel);

            populate();

            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.add(outerPanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            JpoEventBus.getInstance().register(this);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(final WindowEvent e) {
                    super.windowClosed(e);
                    JpoEventBus.getInstance().unregister(CategroyAssignmentWindow.this);
                }
            });
        });
    }

    private static void saveChanges(final JPanel categoriesPanel, final CategoryAssignmentWindowRequest request) {
        for (final var component : categoriesPanel.getComponents()) {
            if (component instanceof TristateCheckBox tristateCheckBox) {
                final var category = (Map.Entry<Integer, String>) tristateCheckBox.getClientProperty(CATEGORY);
                for (final var node : request.nodes()) {
                    final var pictureInfo = (PictureInfo) node.getUserObject();
                    if (tristateCheckBox.getSelection() == TristateCheckBox.TCheckBoxChosenState.UNSELECT) {
                        pictureInfo.removeCategory(category.getKey());
                    } else if (tristateCheckBox.getSelection() == TristateCheckBox.TCheckBoxChosenState.SELECT) {
                        pictureInfo.addCategoryAssignment(category.getKey());
                    }
                }
            }
        }
    }

    @Subscribe
    public void handleEvent(final CategoriesWereModified request) {
        repopulate();
    }

    private void repopulate() {
        categoriesPanel.removeAll();
        populate();
        categoriesPanel.revalidate();
    }

    private void populate() {
        final Map<Integer, Integer> categoryUsageCount = analyseNodes(request);
        populateCheckBoxes(categoryUsageCount, request.nodes().size(), categoriesPanel);
    }

    private Map<Integer, Integer> analyseNodes(final CategoryAssignmentWindowRequest request) {
        final HashMap<Integer, Integer> categoryUsageCount = new HashMap<>();
        for (final var node : request.nodes()) {
            if (node.getUserObject() instanceof PictureInfo pi) {
                LOGGER.log(Level.FINE, "Analysing Node {0}", pi);
                for (var categoryCode : pi.getCategoryAssignments()) {
                    var count = categoryUsageCount.get(categoryCode);
                    if (count == null) {
                        count = Integer.valueOf(1);
                    } else {
                        count++;
                    }
                    categoryUsageCount.put(categoryCode, count);
                }
            }
        }
        return categoryUsageCount;
    }

    private void populateCheckBoxes(final Map<Integer, Integer> categopryUsageCount, int nodes, final JPanel categoriesPanel) {
        final var pictureCollection = Settings.getPictureCollection();
        final List<TristateCheckBox> sortedCheckBoxList = new ArrayList<>();
        pictureCollection.getSortedCategoryStream().forEach(category -> {
            if (categopryUsageCount.get(category.getKey()) == null) {
                final var tristateCheckBox = new TristateCheckBox(category.getValue(), TristateCheckBox.TCheckBoxInitialState.UNSELECTED);
                tristateCheckBox.addKeyListener(escapeKeyListener);
                tristateCheckBox.putClientProperty(CATEGORY, category);
                sortedCheckBoxList.add(tristateCheckBox);
            } else if (categopryUsageCount.get(category.getKey()) == nodes) {
                final var tristateCheckBox = new TristateCheckBox(category.getValue(), TristateCheckBox.TCheckBoxInitialState.SELECTED);
                tristateCheckBox.addKeyListener(escapeKeyListener);
                tristateCheckBox.putClientProperty(CATEGORY, category);
                sortedCheckBoxList.add(tristateCheckBox);
            } else {
                final var tristateCheckBox = new TristateCheckBox(category.getValue(), TristateCheckBox.TCheckBoxInitialState.MIXED);
                tristateCheckBox.addKeyListener(escapeKeyListener);
                tristateCheckBox.putClientProperty(CATEGORY, category);
                sortedCheckBoxList.add(tristateCheckBox);
            }
        });

        final var transposedList = new TristateCheckBox[sortedCheckBoxList.size() + COLUMNS];
        final var maxrows = (int) Math.ceil(sortedCheckBoxList.size() / ((double) COLUMNS));
        for (var i = 0; i < sortedCheckBoxList.size(); i++) {
            int col = i / maxrows;
            int row = i % maxrows;
            int pos = row * COLUMNS + col;
            LOGGER.log(Level.FINE, "maxrows: {0} i: {1} r: {2} c: {3} pos: {4} Category: {5}", new Object[]{maxrows, i, row, col, pos, sortedCheckBoxList.get(i).getText()});
            transposedList[pos] = sortedCheckBoxList.get(i);
        }

        for (var i = 0; i < transposedList.length; i++) {
            if (transposedList[i] != null) {
                categoriesPanel.add(transposedList[i]);
            } else {
                // need to add a non-showing component to keep the alignment
                categoriesPanel.add(new JPanel());
            }
        }
    }

}
