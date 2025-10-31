package org.jpo.gui.swing;

import net.miginfocom.swing.MigLayout;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.jpo.cache.QUEUE_PRIORITY;
import org.jpo.datamodel.*;
import org.jpo.eventbus.JpoEventBus;
import org.jpo.eventbus.SetPictureRotationRequest;
import org.jpo.gui.*;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.input.MapClickListener;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2002-2025 Richard Eigenmann.
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
 * An editor window that allows the attributes of a picture to be modified
 *
 * @author Richard Eigenmann
 */
public class PictureInfoEditor extends JFrame {

    /**
     * Dimension for the edit fields
     */
    private static final Dimension TEXT_FIELD_DIMENSION = new Dimension(400, 20);

    /**
     * Dimension for the time, latitude and longitude
     */
    private static final Dimension SHORT_FIELD_DIMENSION = new Dimension(180, 20);

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(PictureInfoEditor.class.getName());

    private static final String ALIGNY_TOP = "aligny top";
    private static final String SPAN_2_WRAP = "span 2, wrap";

    /**
     * The Thumbnail Controller for the thumbnail being shown
     */
    private final transient ThumbnailController thumbnailController = new ThumbnailController(Settings.getThumbnailSize());

    /**
     * The description of the picture
     */
    private final JTextArea descriptionJTextArea = new JTextArea();

    /**
     * The location of the image file
     */
    private final JTextField creationTimeJTextField = new JTextField();

    /**
     * This label will hold the parsed date of what was in the creation time.
     */
    private final JLabel parsedCreationTimeJLabel = new JLabel();

    /**
     * The location of the image file
     */
    private final JTextField highresLocationJTextField = new JTextField();

    /**
     * An informative message about what sort of error we have if any on the
     * highres image
     */
    private final JLabel highresErrorJLabel = new JLabel("");

    /**
     * Label to display the SHA-256 fileHash of the image file
     */
    private final JTextField sha256JTextField = new JTextField();

    /**
     * Label to display the file Size of the image file
     */
    private final JLabel fileSizeJLabel = new JLabel();

    /**
     * The location of the image file
     */
    private final JTextField filmReferenceJTextField = new JTextField();
    /**
     * The latitude of the image
     */
    private JFormattedTextField latitudeJTextField;
    /**
     * The longitude of the image
     */
    private JFormattedTextField longitudeJTextField;
    /**
     * The comment field
     */
    private final JTextField commentJTextField = new JTextField();
    /**
     * The photographer field
     */
    private final JTextField photographerJTextField = new JTextField();
    /**
     * The copyright field
     */
    private final JTextField copyrightHolderJTextField = new JTextField();
    /**
     * Shows the Exif size
     */
    private final JLabel sizeJLabel = new JLabel("");
    /**
     * The category assignments
     */
    private final JLabel categoryAssignmentsJLabel = new JLabel();
    /**
     * The list model supporting the category assignments
     */
    private final DefaultListModel<Category> listModel = new DefaultListModel<>();
    /**
     * JList to hold the categories
     */
    private final JList<Category> categoriesJList = new JList<>(listModel);
    private final JScrollPane listJScrollPane = new JScrollPane(categoriesJList);
    private static final Category setupCategories = new Category(Integer.MIN_VALUE, JpoResources.getResource("setupCategories"));
    private static final Category noCategories = new Category(Integer.MIN_VALUE, JpoResources.getResource("noCategories"));
    /**
     * The text area to use for showing the Exif data
     */
    private final JTextArea exifTagsJTextArea = new JTextArea();
    private final SpinnerModel angleModel = new MySpinnerNumberModel();
    /**
     * the node being edited
     */
    private final SortableDefaultMutableTreeNode myNode;
    /**
     * the PictureInfo object being displayed
     */
    private final PictureInfo pictureInfo;
    /**
     * Font used to show the error label
     */
    private static final Font ERROR_LABEL_FONT = Font.decode(JpoResources.getResource("ThumbnailDescriptionJPanelLargeFont"));

    /**
     * The icon to rotate the picture to the left
     */
    private static final ImageIcon ROTATE_LEFT_ICON = new ImageIcon(Objects.requireNonNull(PictureInfoEditor.class.getClassLoader().getResource("icon_RotCCDown.gif")));

    /**
     * The icon to rotate the picture to the right
     */
    private static final ImageIcon ROTATE_RIGHT_ICON = new ImageIcon(Objects.requireNonNull(PictureInfoEditor.class.getClassLoader().getResource("icon_RotCWDown.gif")));

    private static final NumberFormat NUMBER_FORMAT_FOR_LATLNG = new DecimalFormat("###.#####################");

    /**
     * Constructor a Picture Info Editor
     *
     * @param editNode The node being edited.
     */
    public PictureInfoEditor(final SortableDefaultMutableTreeNode editNode) {
        super(JpoResources.getResource("PictureInfoEditorHeading"));

        this.myNode = editNode;
        this.pictureInfo = (PictureInfo) editNode.getUserObject();
        Objects.requireNonNull(pictureInfo);

        // set this up so that we can close the GUI if the picture node is removed while we
        // are displaying it.
        editNode.getPictureCollection().addTreeModelListener(myTreeModelListener);
        pictureInfo.addPictureInfoChangeListener(myPictureInfoChangeListener);

        thumbnailController.setNode(new SingleNodeNavigator(editNode), 0);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                getRid();
            }
        });

        initComponents();

        loadData();
        positionMap();

        pack();
        setLocationRelativeTo(Settings.getAnchorFrame());
        setVisible(true);
    }

    /**
     * initialise the Swing components and place them.
     */
    private void initComponents() {
        final var mainPanel = new JPanel(new MigLayout());

        mainPanel.add(thumbnailController.getThumbnail());

        final var tabs = new JTabbedPane();
        mainPanel.add(tabs, "wrap");

        final var rotationPanel = getRotationPanel();
        mainPanel.add(rotationPanel);

        final var buttonJPanel = getButtonPanel();
        mainPanel.add(buttonJPanel);

        final var infoTab = getInfoTab();
        final var infoTabJScrollPane = new JScrollPane(infoTab);
        infoTabJScrollPane.setWheelScrollingEnabled(true);
        tabs.add("Info", infoTabJScrollPane);

        final var fileTab = getFileTab();
        tabs.add("File", fileTab);

        final var categoriesTab = getCategoriesTab();
        tabs.add("Categories", categoriesTab);

        final var exifJScrollPane = getExifJScrollPane();
        tabs.add("Exif", exifJScrollPane);

        final JPanel mapTab = getMapTab();

        tabs.add("Map", mapTab);

        mapViewer.getJXMapViewer().addMouseListener(new MyMapClickListener(mapViewer.getJXMapViewer()));

        setLayout(new MigLayout());
        getContentPane().add(mainPanel);
    }

    @NotNull
    private JPanel getMapTab() {
        final var mapTab = new JPanel();
        mapTab.setLayout(new MigLayout());
        mapTab.add(mapViewer.getJXMapViewer(), "push, grow, span 4, wrap");
        final var latitudeJLabel = new JLabel(JpoResources.getResource("latitudeLabel"));
        mapTab.add(latitudeJLabel, ALIGNY_TOP);

        latitudeJTextField = new JFormattedTextField(NUMBER_FORMAT_FOR_LATLNG);
        latitudeJTextField.setPreferredSize(SHORT_FIELD_DIMENSION);
        mapTab.add(latitudeJTextField);

        final var longitudeJLabel = new JLabel(JpoResources.getResource("longitudeLabel"));
        mapTab.add(longitudeJLabel, ALIGNY_TOP);

        longitudeJTextField = new JFormattedTextField(NUMBER_FORMAT_FOR_LATLNG);
        longitudeJTextField.setPreferredSize(SHORT_FIELD_DIMENSION);
        mapTab.add(longitudeJTextField, "wrap");
        return mapTab;
    }

    @NotNull
    private JPanel getRotationPanel() {
        final var rotationPanel = new JPanel();
        final var rotationJLabel = new JLabel(JpoResources.getResource("rotationLabel"));
        rotationPanel.add(rotationJLabel);

        final var spinner = new JSpinner(angleModel);

        //Make the angle formatted without a thousand separator.
        spinner.setEditor(new JSpinner.NumberEditor(spinner, "###.##"));
        rotationPanel.add(spinner);

        final var rotateLeftJButton = new JButton(ROTATE_LEFT_ICON);
        rotateLeftJButton.setMnemonic(KeyEvent.VK_L);
        rotateLeftJButton.addActionListener((ActionEvent e) -> {
            angleModel.setValue(((Double) angleModel.getValue() + 270) % 360);
            saveRotation();
        });
        rotateLeftJButton.setToolTipText(JpoResources.getResource("rotateLeftJButton.ToolTipText"));
        rotationPanel.add(rotateLeftJButton);

        final var rotateRightJButton = new JButton(ROTATE_RIGHT_ICON);
        rotateRightJButton.setMnemonic(KeyEvent.VK_R);
        rotateRightJButton.addActionListener((ActionEvent e) -> {
            angleModel.setValue(((Double) angleModel.getValue() + 90) % 360);
            saveRotation();
        });
        rotateRightJButton.setToolTipText(JpoResources.getResource("rotateRightJButton.ToolTipText"));
        rotationPanel.add(rotateRightJButton);
        return rotationPanel;
    }

    @NotNull
    private JPanel getButtonPanel() {
        final var buttonJPanel = new JPanel();
        buttonJPanel.setLayout(new FlowLayout());

        final var okJButton = new JButton(JpoResources.getResource("genericOKText"));
        okJButton.setPreferredSize(Settings.getDefaultButtonDimension());
        okJButton.setMinimumSize(Settings.getDefaultButtonDimension());
        okJButton.setMaximumSize(Settings.getDefaultButtonDimension());
        okJButton.setBorder(BorderFactory.createRaisedBevelBorder());
        okJButton.addActionListener((ActionEvent e) -> {
            saveFieldData();
            getRid();
        });
        okJButton.setDefaultCapable(true);
        getRootPane().setDefaultButton(okJButton);
        buttonJPanel.add(okJButton);

        final var cancelJButton = new JButton(JpoResources.getResource("genericCancelText"));
        cancelJButton.setPreferredSize(Settings.getDefaultButtonDimension());
        cancelJButton.setMinimumSize(Settings.getDefaultButtonDimension());
        cancelJButton.setMaximumSize(Settings.getDefaultButtonDimension());
        cancelJButton.setBorder(BorderFactory.createRaisedBevelBorder());
        cancelJButton.addActionListener((ActionEvent e) -> getRid());
        buttonJPanel.add(cancelJButton);

        final var resetJButton = new JButton(JpoResources.getResource("resetLabel"));
        resetJButton.setPreferredSize(Settings.getDefaultButtonDimension());
        resetJButton.setMinimumSize(Settings.getDefaultButtonDimension());
        resetJButton.setMaximumSize(Settings.getDefaultButtonDimension());
        resetJButton.setBorder(BorderFactory.createRaisedBevelBorder());
        resetJButton.addActionListener((ActionEvent e) -> loadData());
        buttonJPanel.add(resetJButton);
        return buttonJPanel;
    }

    @NotNull
    private JScrollPane getExifJScrollPane() {
        // Add Exif panel
        exifTagsJTextArea.setWrapStyleWord(true);
        exifTagsJTextArea.setLineWrap(false);
        exifTagsJTextArea.setEditable(true);
        exifTagsJTextArea.setRows(17);
        exifTagsJTextArea.setColumns(35);

        // stop undesired scrolling in the window when doing append
        final var dumbCaret = new NonFocussedCaret();
        exifTagsJTextArea.setCaret(dumbCaret);

        final var exifJScrollPane = new JScrollPane();
        exifJScrollPane.setViewportView(exifTagsJTextArea);
        exifJScrollPane.setWheelScrollingEnabled(true);
        return exifJScrollPane;
    }

    @NotNull
    private JPanel getCategoriesTab() {
        final var categoriesTab = new JPanel(new MigLayout());
        final var categoriesJLabel = new JLabel(JpoResources.getResource("categoriesJLabel-2"));
        categoriesTab.add(categoriesJLabel, "wrap");

        categoriesTab.add(categoryAssignmentsJLabel, "wrap");

        categoriesJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        categoriesJList.addListSelectionListener((ListSelectionEvent e) -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            if (categoriesJList.isSelectedIndex(((DefaultListModel<?>) categoriesJList.getModel()).indexOf(setupCategories))) {
                        new CategoryEditorJFrame(myNode.getPictureCollection());
                    } else if (categoriesJList.isSelectedIndex(((DefaultListModel<?>) categoriesJList.getModel()).indexOf(noCategories))) {
                        categoriesJList.clearSelection();
                    }
                    categoryAssignmentsJLabel.setText(selectedJListCategoriesToString(categoriesJList));
                }
        );

        categoriesTab.add(listJScrollPane, "push, grow, wrap");
        return categoriesTab;
    }

    @NotNull
    private JPanel getFileTab() {
        final var fileTab = new JPanel(new MigLayout());
        final var highresLocationJLabel = new JLabel(JpoResources.getResource("highresLocationLabel"));
        fileTab.add(highresLocationJLabel, SPAN_2_WRAP);
        highresErrorJLabel.setFont(ERROR_LABEL_FONT);
        highresLocationJTextField.setPreferredSize(TEXT_FIELD_DIMENSION);
        fileTab.add(highresLocationJTextField);

        final var highresLocationJButton = new ThreeDotButton();
        highresLocationJButton.addActionListener((ActionEvent e) -> chooseFile());
        fileTab.add(highresLocationJButton, "wrap");
        fileTab.add(highresErrorJLabel, SPAN_2_WRAP);

        fileTab.add(new JLabel(JpoResources.getResource("fileHashJLabel")), "wrap");
        sha256JTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // no action
            }

            @Override
            public void focusLost(FocusEvent e) {
                saveSha256();
            }
        });
        fileTab.add(sha256JTextField, "spanx, growx, wrap");

        final var refreshChecksumJButton = new JButton(JpoResources.getResource("checksumJButton"));
        refreshChecksumJButton.setPreferredSize(new Dimension(80, 25));
        refreshChecksumJButton.setMinimumSize(new Dimension(80, 25));
        refreshChecksumJButton.setMaximumSize(new Dimension(80, 25));
        refreshChecksumJButton.addActionListener((ActionEvent e) -> new Thread(
                () -> {
                    LOGGER.log(Level.INFO, "Refreshing sha256 for image {0}", pictureInfo);
                    pictureInfo.setSha256();
                }
        ).start());

        fileTab.add(refreshChecksumJButton, "wrap");

        fileTab.add(fileSizeJLabel);

        return fileTab;
    }

    @NotNull
    private JPanel getInfoTab() {
        final var infoTab = new JPanel();
        infoTab.setLayout(new MigLayout());

        final var descriptionJLabel = new JLabel(JpoResources.getResource("pictureDescriptionLabel"));
        infoTab.add(descriptionJLabel, SPAN_2_WRAP);

        descriptionJTextArea.setPreferredSize(new Dimension(400, 150));
        descriptionJTextArea.setWrapStyleWord(true);
        descriptionJTextArea.setLineWrap(true);
        descriptionJTextArea.setEditable(true);
        infoTab.add(descriptionJTextArea, SPAN_2_WRAP);

        final var sizeLabelJLabel = new JLabel("Size:");
        infoTab.add(sizeLabelJLabel, ALIGNY_TOP);

        infoTab.add(sizeJLabel, "aligny top, wrap");

        final var creationTimeJLabel = new JLabel(JpoResources.getResource("creationTimeLabel"));
        infoTab.add(creationTimeJLabel, "spany 3, aligny top");

        creationTimeJTextField.setPreferredSize(SHORT_FIELD_DIMENSION);
        creationTimeJTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(final FocusEvent e) {
                parseTimestamp(creationTimeJTextField.getText());
            }

            @Override
            public void focusLost(final FocusEvent e) {
                parseTimestamp(creationTimeJTextField.getText());
            }
        });
        infoTab.add(creationTimeJTextField, "wrap");

        parsedCreationTimeJLabel.setFont(ERROR_LABEL_FONT);
        infoTab.add(parsedCreationTimeJLabel, "wrap");

        final var reparseButton = new JButton("reparse");
        infoTab.add(reparseButton, "wrap");
        reparseButton.addActionListener((ActionEvent e) -> doReparseDate());

        final var filmReferenceJLabel = new JLabel(JpoResources.getResource("filmReferenceLabel"));
        infoTab.add(filmReferenceJLabel, SPAN_2_WRAP);

        filmReferenceJTextField.setPreferredSize(TEXT_FIELD_DIMENSION);
        infoTab.add(filmReferenceJTextField, SPAN_2_WRAP);

        final var commentJLabel = new JLabel(JpoResources.getResource("commentLabel"));
        infoTab.add(commentJLabel, SPAN_2_WRAP);

        commentJTextField.setPreferredSize(TEXT_FIELD_DIMENSION);
        infoTab.add(commentJTextField, SPAN_2_WRAP);

        final var photographerJLabel = new JLabel(JpoResources.getResource("photographerLabel"));
        infoTab.add(photographerJLabel, SPAN_2_WRAP);

        photographerJTextField.setPreferredSize(TEXT_FIELD_DIMENSION);
        infoTab.add(photographerJTextField, SPAN_2_WRAP);

        final var copyrightHolderJLabel = new JLabel(JpoResources.getResource("copyrightHolderLabel"));
        infoTab.add(copyrightHolderJLabel, SPAN_2_WRAP);

        copyrightHolderJTextField.setPreferredSize(TEXT_FIELD_DIMENSION);
        infoTab.add(copyrightHolderJTextField, SPAN_2_WRAP);

        return infoTab;
    }

    private final transient MapViewer mapViewer = new MapViewer();

    /**
     * populates the text fields with the values from the PictureInfo object
     */
    private void loadData() {
        setDescription();
        setHighresLocation();
        setSha256();
        setCreationTime();
        setFilmReference();
        setRotation();
        setLatLng();
        setComment();
        setPhotographer();
        setCopyrightHolder();

        listModel.removeAllElements();
        categoriesJList.clearSelection();
        listModel.addElement(setupCategories);
        listModel.addElement(noCategories);

        final var selections = new ArrayList<Integer>();
        myNode.getPictureCollection().getSortedCategoryStream().forEach(entry -> {
            final var category = new Category(entry.getKey(), entry.getValue());
            listModel.addElement(category);
            if ((pictureInfo.getCategoryAssignments() != null) && (pictureInfo.getCategoryAssignments().contains(entry.getKey()))) {
                selections.add(listModel.indexOf(category));
            }
        });

        final var selectionsArray = new int[selections.size()];
        var j = 0;
        for (final var key : selections) {
            selectionsArray[j] = (key);
            j++;
        }
        categoriesJList.setSelectedIndices(selectionsArray);
        categoryAssignmentsJLabel.setText(selectedJListCategoriesToString(categoriesJList));

        final var exifInfo = new ExifInfo(pictureInfo.getImageFile());
        exifInfo.decodeExifTags();

        sizeJLabel.setText(String.format("%s x %s", exifInfo.getExifWidth(), exifInfo.getExifHeight()));

        exifTagsJTextArea.append(JpoResources.getResource("ExifTitle"));
        exifTagsJTextArea.append(exifInfo.getComprehensivePhotographicSummary());
        exifTagsJTextArea.append("-------------------------\nAll Tags:\n");
        exifTagsJTextArea.append(exifInfo.getAllTags());

        setColorIfError();
    }

    /**
     * Set up a PictureInfoChangeListener to get updated on change events in the
     * Picture Metadata
     */
    private final transient PictureInfoChangeListener myPictureInfoChangeListener = e -> {
        LOGGER.log(Level.INFO, "Got a PictureInfoChangeEvent");
        if (e.getDescriptionChanged()) {
            setDescription();
        }
        if (e.getHighresLocationChanged()) {
            setHighresLocation();
        }
        if (e.getSha256Changed()) {
            setSha256();
        }
        if (e.getCreationTimeChanged()) {
            setCreationTime();
        }
        if (e.getFilmReferenceChanged()) {
            setFilmReference();
        }
        if (e.getRotationChanged()) {
            setRotation();
        }
        if (e.getLatLngChanged()) {
            setLatLng();
            positionMap();
        }
        if (e.getCommentChanged()) {
            setComment();
        }
        if (e.getPhotographerChanged()) {
            setPhotographer();
        }
        if (e.getCopyrightHolderChanged()) {
            setCopyrightHolder();
        }
    };

    private void setCopyrightHolder() {
        copyrightHolderJTextField.setText(pictureInfo.getCopyrightHolder());
    }

    private void setPhotographer() {
        photographerJTextField.setText(pictureInfo.getPhotographer());
    }

    private void setComment() {
        commentJTextField.setText(pictureInfo.getComment());
    }

    private void setLatLng() {
        latitudeJTextField.setText(Double.toString(pictureInfo.getLatLng().x));
        longitudeJTextField.setText(Double.toString(pictureInfo.getLatLng().y));
    }

    private void setRotation() {
        angleModel.setValue(pictureInfo.getRotation());
    }

    private void setFilmReference() {
        filmReferenceJTextField.setText(pictureInfo.getFilmReference());
    }

    private void setCreationTime() {
        creationTimeJTextField.setText(pictureInfo.getCreationTime());
        parsedCreationTimeJLabel.setText(pictureInfo.getFormattedCreationTime());
    }

    private void setSha256() {
        sha256JTextField.setText(pictureInfo.getSha256());
    }

    private void saveSha256() {
        pictureInfo.setSha256(sha256JTextField.getText());
    }

    private void setHighresLocation() {
        highresLocationJTextField.setText(pictureInfo.getImageFile().toString());
        fileSizeJLabel.setText(pictureInfo.getImageFile() == null ? "no file" : FileUtils.byteCountToDisplaySize(pictureInfo.getImageFile().length()));
    }

    private void setDescription() {
        descriptionJTextArea.setText(pictureInfo.getDescription());
    }

    /**
     * This utility method builds a string from the selected categories in a
     * supplied JList
     *
     * @param theList the List
     * @return a string for the selected categories
     */
    private static String selectedJListCategoriesToString(final JList<Category> theList) {
        final var resultString = new StringBuilder();
        if (!theList.isSelectionEmpty()) {
            final List<Category> selectedCategories = theList.getSelectedValuesList();
            var comma = "";
            for (final Category c : selectedCategories) {
                if (!((c.equals(setupCategories)) || (c.equals(noCategories)))) {
                    resultString.append(comma).append(c);
                    comma = ", ";
                }
            }
        }
        return resultString.toString();
    }

    /**
     * method that sets the URL fields to red if the file is not found and red
     * if the URL is not a valid URL
     */
    private void setColorIfError() {
        final var f = new File(highresLocationJTextField.getText());
        if (Files.isReadable(f.toPath())) {
            highresLocationJTextField.setForeground(Color.black);
            highresErrorJLabel.setText("");
        } else {
            highresLocationJTextField.setForeground(Color.red);
            highresErrorJLabel.setText("File is not readable");
        }
    }


    /**
     * Close the editor window and release all listeners.
     */
    public void getRid() {
        if (myNode.getPictureCollection().getTreeModel() != null) {
            myNode.getPictureCollection().removeTreeModelListener(myTreeModelListener);
        }
        pictureInfo.removePictureInfoChangeListener(myPictureInfoChangeListener);
        setVisible(false);
        dispose();
    }

    /**
     * Reparse the date from the EXIF information
     */
    private void doReparseDate() {
        final var file = new File(highresLocationJTextField.getText());
        final var exifInfo = new ExifInfo(file);
        final var timestamp = exifInfo.getCreateDateTime();
        creationTimeJTextField.setText(timestamp);
        parseTimestamp(timestamp);
    }

    /**
     * Parses the supplied timestamp and shows the result in the
     * parsedCreationTimeJLabel
     *
     * @param timestamp Timestamp
     */
    private void parseTimestamp(final String timestamp) {
        parsedCreationTimeJLabel.setText(String.format("%tc", Tools.parseDate(timestamp)));
    }

    /**
     * saves the data in the fields back to the PictureInfo object
     */
    private void saveFieldData() {
        pictureInfo.setDescription(descriptionJTextArea.getText());
        pictureInfo.setCreationTime(creationTimeJTextField.getText());
        pictureInfo.setImageLocation(new File(highresLocationJTextField.getText()));
        pictureInfo.setSha256(sha256JTextField.getText());
        pictureInfo.setComment(commentJTextField.getText());
        pictureInfo.setPhotographer(photographerJTextField.getText());
        pictureInfo.setFilmReference(filmReferenceJTextField.getText());
        pictureInfo.setCopyrightHolder(copyrightHolderJTextField.getText());

        saveRotation();

        pictureInfo.setLatLng(getLatLng());
        int[] indexes = categoriesJList.getSelectedIndices();
        Category category;
        pictureInfo.clearCategoryAssignments();

        for (int index : indexes) {
            category = listModel.getElementAt(index);
            pictureInfo.addCategoryAssignment(category.getKey());
        }
    }

    /**
     * Don't use: accessor to the private saveFieldData function for unit tests.
     */
    @TestOnly
    public void callSaveFieldData() {
        saveFieldData();
    }


    /**
     * Returns a point with the latitude and longitude of the values in the
     * textfields. If the text can't be parsed properly the previous value is
     * returned.
     *
     * @return the point on the globe
     */
    private Point2D.Double getLatLng() {
        double latitude;
        try {
            latitude = Double.parseDouble(latitudeJTextField.getText());

        } catch (NumberFormatException ex) {
            latitude = pictureInfo.getLatLng().x;
            LOGGER.info(String.format("Latitude String %s could not be parsed: %s --> leaving at old value: %f", latitudeJTextField.getText(), ex.getMessage(), latitude));
        }
        double longitude;
        try {
            longitude = Double.parseDouble(longitudeJTextField.getText());
        } catch (NumberFormatException ex) {
            longitude = pictureInfo.getLatLng().y;
            LOGGER.info(String.format("Longitude String %s could not be parsed: %s --> leaving at old value: %f", longitudeJTextField.getText(), ex.getMessage(), longitude));
        }
        return new Point2D.Double(latitude, longitude);
    }

    /**
     * This method saves the rotation value
     */
    private void saveRotation() {
        JpoEventBus.getInstance().post(
                new SetPictureRotationRequest(
                        List.of(myNode),
                        (double) angleModel.getValue(),
                        QUEUE_PRIORITY.HIGH_PRIORITY
                )
        );
    }

    /**
     * method that brings up a JFileChooser and places the path of the file
     * selected into the JTextField of the highres locations
     */
    private void chooseFile() {
        final var jFileChooser = new JFileChooser();
        jFileChooser.setFileFilter(new ImageFilter());

        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jFileChooser.setApproveButtonText(JpoResources.getResource("genericSelectText"));
        jFileChooser.setDialogTitle(JpoResources.getResource("highresChooserTitle"));
        jFileChooser.setCurrentDirectory(new File(highresLocationJTextField.getText()));

        final int returnVal = jFileChooser.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                highresLocationJTextField.setText(jFileChooser.getSelectedFile().toURI().toURL().toString());

            } catch (MalformedURLException x) {
                // don't update the text field then
            }
        }
        setColorIfError();
    }

    /**
     * Set up a TreeModelListener to learn of updates to the tree and be able to
     * close the window if the node we are editing has been removed or to update
     * the fields if it was changed.
     * Here we are not that interested in TreeModel change events other than to find out if our
     * current node was removed in which case we close the Window.
     */
    private final transient TreeModelListener myTreeModelListener = new TreeModelListener() {

        /**
         * implemented here to satisfy the TreeModelListener interface; not
         * used.
         *
         * @param e The event
         */
        @Override
        public void treeNodesChanged(final TreeModelEvent e) {
            // noop
        }

        /**
         * implemented here to satisfy the TreeModelListener interface; not
         * used.
         *
         * @param e The event
         */
        @Override
        public void treeNodesInserted(final TreeModelEvent e) {
            // noop
        }

        /**
         * The TreeModelListener interface tells us of tree node removal events.
         * We use this here to determine if the node being displayed is the one
         * removed or whether it is a child of the removed nodes. If so we close
         * the window.
         *
         * @param treeModelEvent The event
         */
        @Override
        public void treeNodesRemoved(final TreeModelEvent treeModelEvent) {
            if (SortableDefaultMutableTreeNode.wasNodeDeleted(myNode, treeModelEvent)) {
                getRid();
            }
        }

        /**
         * implemented here to satisfy the TreeModelListener interface; not
         * used.
         *
         * @param e The event
         */
        @Override
        public void treeStructureChanged(final TreeModelEvent e) {
            // noop
        }
    };

    private static class MySpinnerNumberModel
            extends SpinnerNumberModel {

        MySpinnerNumberModel() {
            super(0.0, //initial value
                    0.0, //min
                    359.9, //max
                    .1f);                //step
        }

        @Override
        public Object getNextValue() {
            var object = super.getNextValue();
            if (object == null) {
                object = 0.0;
            }
            return object;
        }

        @Override
        public Object getPreviousValue() {
            var object = super.getPreviousValue();
            if (object == null) {
                object = 359.9;
            }
            return object;
        }
    }

    private void positionMap() {
        mapViewer.setMarker(getLatLng());

    }

    private class MyMapClickListener extends MapClickListener {

        MyMapClickListener(final JXMapViewer viewer) {
            super(viewer);
        }

        @Override
        public void mapClicked(final GeoPosition location) {
            latitudeJTextField.setText(Double.toString(location.getLatitude()));
            longitudeJTextField.setText(Double.toString(location.getLongitude()));
            positionMap();
        }

    }

}
