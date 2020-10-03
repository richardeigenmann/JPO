package org.jpo.gui.swing;

import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.TestOnly;
import org.jpo.cache.QUEUE_PRIORITY;
import org.jpo.datamodel.*;
import org.jpo.eventbus.JpoEventBus;
import org.jpo.eventbus.SetPictureRotationRequest;
import org.jpo.gui.CategoryEditorJFrame;
import org.jpo.gui.ImageFilter;
import org.jpo.gui.ThumbnailController;
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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2002-2020  Richard Eigenmann.
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

    /**
     * The Thumbnail Controller for the thumbnail being shown
     */
    private final ThumbnailController thumbnailController = new ThumbnailController(new Thumbnail(), Settings.getThumbnailSize());

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
     * Label to display the checksum of the image file. Gets updated so it's
     * here.
     */
    private final JLabel checksumJLabel = new JLabel();

    /**
     * Label to display the SHA-256 fileHash of the image file
     */
    private final JLabel fileHashJLabel = new JLabel();

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
    private static final Category setupCategories = new Category(Integer.MIN_VALUE, Settings.getJpoResources().getString("setupCategories"));
    private static final Category noCategories = new Category(Integer.MIN_VALUE, Settings.getJpoResources().getString("noCategories"));
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
    private static final Font ERROR_LABEL_FONT = Font.decode(Settings.getJpoResources().getString("ThumbnailDescriptionJPanelLargeFont"));

    /**
     * The icon to rotate the picture to the left
     */
    private static final ImageIcon ROTATE_LEFT_ICON = new ImageIcon(Objects.requireNonNull(PictureInfoEditor.class.getClassLoader().getResource("icon_RotCCDown.gif")));

    /**
     * The icon to rotate the picture to the right
     */
    private static final ImageIcon ROTATE_RIGHT_ICON = new ImageIcon(Objects.requireNonNull(PictureInfoEditor.class.getClassLoader().getResource("icon_RotCWDown.gif")));

    /**
     * Constructs a Picture Info Editor
     *
     * @param setOfNodes Set of nodes
     * @param index      index
     */
    public PictureInfoEditor(NodeNavigatorInterface setOfNodes, int index) {
        this(setOfNodes.getNode(index));
    }

    /**
     * Constructor a Picture Info Editor
     *
     * @param editNode The node being edited.
     */
    public PictureInfoEditor(final SortableDefaultMutableTreeNode editNode) {
        super(Settings.getJpoResources().getString("PictureInfoEditorHeading"));

        pictureInfo = (PictureInfo) Objects.requireNonNull(editNode.getUserObject());
        this.myNode = editNode;

        // set this up so that we can close the GUI if the picture node is removed while we
        // are displaying it.
        editNode.getPictureCollection().getTreeModel().addTreeModelListener(myTreeModelListener);
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
        final JPanel mainPanel = new JPanel(new MigLayout());

        mainPanel.add(thumbnailController.getThumbnail());

        final JTabbedPane tabs = new JTabbedPane();
        mainPanel.add(tabs, "wrap");

        final JPanel rotationPanel = new JPanel();
        final JLabel rotationJLabel = new JLabel(Settings.getJpoResources().getString("rotationLabel"));
        rotationPanel.add(rotationJLabel);

        final JSpinner spinner = new JSpinner(angleModel);

        //Make the angle formatted without a thousands separator.
        spinner.setEditor(new JSpinner.NumberEditor(spinner, "###.##"));
        rotationPanel.add(spinner);

        final JButton rotateLeftJButton = new JButton(ROTATE_LEFT_ICON);
        rotateLeftJButton.setMnemonic(KeyEvent.VK_L);
        rotateLeftJButton.addActionListener((ActionEvent e) -> {
            angleModel.setValue(((Double) angleModel.getValue() + 270) % 360);
            saveRotation();
        });
        rotateLeftJButton.setToolTipText(Settings.getJpoResources().getString("rotateLeftJButton.ToolTipText"));
        rotationPanel.add(rotateLeftJButton);

        final JButton rotateRightJButton = new JButton(ROTATE_RIGHT_ICON);
        rotateRightJButton.setMnemonic(KeyEvent.VK_R);
        rotateRightJButton.addActionListener((ActionEvent e) -> {
            angleModel.setValue(((Double) angleModel.getValue() + 90) % 360);
            saveRotation();
        });
        rotateRightJButton.setToolTipText(Settings.getJpoResources().getString("rotateRightJButton.ToolTipText"));
        rotationPanel.add(rotateRightJButton);

        mainPanel.add(rotationPanel);

        final JPanel buttonJPanel = new JPanel();
        buttonJPanel.setLayout(new FlowLayout());

        final JButton okJButton = new JButton(Settings.getJpoResources().getString("genericOKText"));
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

        final JButton cancelJButton = new JButton(Settings.getJpoResources().getString("genericCancelText"));
        cancelJButton.setPreferredSize(Settings.getDefaultButtonDimension());
        cancelJButton.setMinimumSize(Settings.getDefaultButtonDimension());
        cancelJButton.setMaximumSize(Settings.getDefaultButtonDimension());
        cancelJButton.setBorder(BorderFactory.createRaisedBevelBorder());
        cancelJButton.addActionListener((ActionEvent e) -> getRid());
        buttonJPanel.add(cancelJButton);

        final JButton resetJButton = new JButton(Settings.getJpoResources().getString("resetLabel"));
        resetJButton.setPreferredSize(Settings.getDefaultButtonDimension());
        resetJButton.setMinimumSize(Settings.getDefaultButtonDimension());
        resetJButton.setMaximumSize(Settings.getDefaultButtonDimension());
        resetJButton.setBorder(BorderFactory.createRaisedBevelBorder());
        resetJButton.addActionListener((ActionEvent e) -> loadData());
        buttonJPanel.add(resetJButton);
        mainPanel.add(buttonJPanel);

        final JPanel infoTab = new JPanel();
        infoTab.setLayout(new MigLayout());

        JLabel descriptionJLabel = new JLabel(Settings.getJpoResources().getString("pictureDescriptionLabel"));
        infoTab.add(descriptionJLabel, "span 2, wrap");

        descriptionJTextArea.setPreferredSize(new Dimension(400, 150));
        descriptionJTextArea.setWrapStyleWord(true);
        descriptionJTextArea.setLineWrap(true);
        descriptionJTextArea.setEditable(true);
        infoTab.add(descriptionJTextArea, "span 2, wrap");

        final JLabel sizeLabelJLabel = new JLabel("Size:");
        infoTab.add(sizeLabelJLabel, "aligny top");

        infoTab.add(sizeJLabel, "aligny top, wrap");

        final JLabel creationTimeJLabel = new JLabel(Settings.getJpoResources().getString("creationTimeLabel"));
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

        final JButton reparseButton = new JButton("reparse");
        infoTab.add(reparseButton, "wrap");
        reparseButton.addActionListener((ActionEvent e) -> doReparseDate());

        final JLabel filmReferenceJLabel = new JLabel(Settings.getJpoResources().getString("filmReferenceLabel"));
        infoTab.add(filmReferenceJLabel, "span 2, wrap");

        filmReferenceJTextField.setPreferredSize(TEXT_FIELD_DIMENSION);
        infoTab.add(filmReferenceJTextField, "span 2, wrap");

        final JLabel commentJLabel = new JLabel(Settings.getJpoResources().getString("commentLabel"));
        infoTab.add(commentJLabel, "span 2, wrap");

        commentJTextField.setPreferredSize(TEXT_FIELD_DIMENSION);
        infoTab.add(commentJTextField, "span 2, wrap");

        final JLabel photographerJLabel = new JLabel(Settings.getJpoResources().getString("photographerLabel"));
        infoTab.add(photographerJLabel, "span 2, wrap");

        photographerJTextField.setPreferredSize(TEXT_FIELD_DIMENSION);
        infoTab.add(photographerJTextField, "span 2, wrap");

        final JLabel copyrightHolderJLabel = new JLabel(Settings.getJpoResources().getString("copyrightHolderLabel"));
        infoTab.add(copyrightHolderJLabel, "span 2, wrap");

        copyrightHolderJTextField.setPreferredSize(TEXT_FIELD_DIMENSION);
        infoTab.add(copyrightHolderJTextField, "span 2, wrap");

        final JLabel latitudeJLabel = new JLabel(Settings.getJpoResources().getString("latitudeLabel"));
        infoTab.add(latitudeJLabel, "aligny top");

        NumberFormat nfl = new DecimalFormat("###.#####################");
        latitudeJTextField = new JFormattedTextField(nfl);
        latitudeJTextField.setPreferredSize(SHORT_FIELD_DIMENSION);
        infoTab.add(latitudeJTextField, "wrap");

        final JLabel longitudeJLabel = new JLabel(Settings.getJpoResources().getString("longitudeLabel"));
        infoTab.add(longitudeJLabel, "aligny top");

        longitudeJTextField = new JFormattedTextField(nfl);
        longitudeJTextField.setPreferredSize(SHORT_FIELD_DIMENSION);
        infoTab.add(longitudeJTextField, "wrap");

        final JScrollPane jScrollPane = new JScrollPane(infoTab);
        jScrollPane.setWheelScrollingEnabled(true);
        tabs.add("Info", jScrollPane);

        final JPanel fileTab = new JPanel(new MigLayout());
        JLabel highresLocationJLabel = new JLabel(Settings.getJpoResources().getString("highresLocationLabel"));
        fileTab.add(highresLocationJLabel, "span 2, wrap");
        highresErrorJLabel.setFont(ERROR_LABEL_FONT);
        highresLocationJTextField.setPreferredSize(TEXT_FIELD_DIMENSION);
        fileTab.add(highresLocationJTextField);

        final JButton highresLocationJButton = new ThreeDotButton();
        highresLocationJButton.addActionListener((ActionEvent e) -> chooseFile());
        fileTab.add(highresLocationJButton, "wrap");
        fileTab.add(highresErrorJLabel, "span 2, wrap");

        final JButton refreshChecksumJButton = new JButton(Settings.getJpoResources().getString("checksumJButton"));
        refreshChecksumJButton.setPreferredSize(new Dimension(80, 25));
        refreshChecksumJButton.setMinimumSize(new Dimension(80, 25));
        refreshChecksumJButton.setMaximumSize(new Dimension(80, 25));
        refreshChecksumJButton.addActionListener((ActionEvent e) -> new Thread(
                () -> {
                    pictureInfo.setSha256();
                    pictureInfo.calculateChecksum();
                }
        ).start());

        fileTab.add(checksumJLabel, "wrap");
        fileTab.add(fileHashJLabel, "wrap");
        fileTab.add(refreshChecksumJButton, "wrap");

        tabs.add("File", fileTab);

        final JPanel categoriesTab = new JPanel(new MigLayout());
        final JLabel categoriesJLabel = new JLabel(Settings.getJpoResources().getString("categoriesJLabel-2"));
        categoriesTab.add(categoriesJLabel, "wrap");

        categoriesTab.add(categoryAssignmentsJLabel, "wrap");

        categoriesJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        categoriesJList.addListSelectionListener((ListSelectionEvent e) -> {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    if (categoriesJList.isSelectedIndex(((DefaultListModel) categoriesJList.getModel()).indexOf(setupCategories))) {
                        new CategoryEditorJFrame();
                    } else if (categoriesJList.isSelectedIndex(((DefaultListModel) categoriesJList.getModel()).indexOf(noCategories))) {
                        categoriesJList.clearSelection();
                    }
                    categoryAssignmentsJLabel.setText(selectedJListCategoriesToString(categoriesJList));
                }
        );

        categoriesTab.add(listJScrollPane, "push, grow, wrap");
        tabs.add("Categories", categoriesTab);

        // Add Exif panel
        exifTagsJTextArea.setWrapStyleWord(true);
        exifTagsJTextArea.setLineWrap(false);
        exifTagsJTextArea.setEditable(true);
        exifTagsJTextArea.setRows(17);
        exifTagsJTextArea.setColumns(35);

        // stop undesired scrolling in the window when doing append
        final NonFocussedCaret dumbCaret = new NonFocussedCaret();
        exifTagsJTextArea.setCaret(dumbCaret);

        final JScrollPane exifJScrollPane = new JScrollPane();
        exifJScrollPane.setViewportView(exifTagsJTextArea);
        exifJScrollPane.setWheelScrollingEnabled(true);

        tabs.add("Exif", exifJScrollPane);

        tabs.add("Map", mapViewer.getJXMapViewer());
        mapViewer.getJXMapViewer().addMouseListener(new MyMapClickListener(mapViewer.getJXMapViewer()));

        setLayout(new MigLayout());
        getContentPane().add(mainPanel);
    }

    private final MapViewer mapViewer = new MapViewer();

    /**
     * populates the text fields with the values from the PictureInfo object
     */
    private void loadData() {
        creationTimeJTextField.setText(pictureInfo.getCreationTime());
        parsedCreationTimeJLabel.setText(pictureInfo.getFormattedCreationTime());
        descriptionJTextArea.setText(pictureInfo.getDescription());
        highresLocationJTextField.setText(pictureInfo.getImageFile().toString());
        checksumJLabel.setText(Settings.getJpoResources().getString("checksumJLabel") + pictureInfo.getChecksumAsString());
        fileHashJLabel.setText(Settings.getJpoResources().getString("fileHashJLabel") + pictureInfo.getFileHashAsString());
        filmReferenceJTextField.setText(pictureInfo.getFilmReference());
        LOGGER.info(String.format("Retrieving angle: %f", pictureInfo.getRotation()));
        angleModel.setValue(pictureInfo.getRotation());
        latitudeJTextField.setText(Double.toString(pictureInfo.getLatLng().x));
        longitudeJTextField.setText(Double.toString(pictureInfo.getLatLng().y));
        commentJTextField.setText(pictureInfo.getComment());
        photographerJTextField.setText(pictureInfo.getPhotographer());
        copyrightHolderJTextField.setText(pictureInfo.getCopyrightHolder());

        listModel.removeAllElements();
        categoriesJList.clearSelection();
        listModel.addElement(setupCategories);
        listModel.addElement(noCategories);

        List<Integer> selections = new ArrayList<>();
        myNode.getPictureCollection().getCategoryKeySet().forEach(key -> {
            final String category = myNode.getPictureCollection().getCategory(key);
            final Category categoryObject = new Category(key, category);
            listModel.addElement(categoryObject);
            if ((pictureInfo.getCategoryAssignments() != null) && (pictureInfo.getCategoryAssignments().contains(key))) {
                selections.add(listModel.indexOf(categoryObject));
            }
        });

        int[] selectionsArray = new int[selections.size()];
        int j = 0;
        for (Integer key : selections) {
            selectionsArray[j] = (key);
            j++;
        }
        categoriesJList.setSelectedIndices(selectionsArray);
        categoryAssignmentsJLabel.setText(selectedJListCategoriesToString(categoriesJList));

        ExifInfo exifInfo = new ExifInfo(pictureInfo.getImageFile());
        exifInfo.decodeExifTags();

        sizeJLabel.setText(String.format("%s x %s", exifInfo.getExifWidth(), exifInfo.getExifHeight()));

        exifTagsJTextArea.append(Settings.getJpoResources().getString("ExifTitle"));
        exifTagsJTextArea.append(exifInfo.getComprehensivePhotographicSummary());
        exifTagsJTextArea.append("-------------------------\nAll Tags:\n");
        exifTagsJTextArea.append(exifInfo.getAllTags());

        setColorIfError();
    }

    /**
     * Set up a PictureInfoChangeListener to get updated on change events in the
     * Picture Metadata
     */
    private final PictureInfoChangeListener myPictureInfoChangeListener = new PictureInfoChangeListener() {
        /**
         * here we get notified by the PictureInfo object that something has
         * changed.
         */
        @Override
        public void pictureInfoChangeEvent(final PictureInfoChangeEvent e) {
            if (e.getDescriptionChanged()) {
                descriptionJTextArea.setText(pictureInfo.getDescription());
            }
            if (e.getHighresLocationChanged()) {
                highresLocationJTextField.setText(pictureInfo.getImageLocation());
            }
            if (e.getChecksumChanged()) {
                checksumJLabel.setText(Settings.getJpoResources().getString("checksumJLabel") + pictureInfo.getChecksumAsString());
            }
            if (e.getFileHashChanged()) {
                fileHashJLabel.setText(Settings.getJpoResources().getString("fileHashJLabel") + pictureInfo.getFileHashAsString());
            }
            if (e.getCreationTimeChanged()) {
                creationTimeJTextField.setText(pictureInfo.getCreationTime());
                parsedCreationTimeJLabel.setText(pictureInfo.getFormattedCreationTime());
            }
            if (e.getFilmReferenceChanged()) {
                filmReferenceJTextField.setText(pictureInfo.getFilmReference());
            }
            if (e.getRotationChanged()) {
                LOGGER.info(String.format("Processing a Rotation Changed notification: angle is: %f", pictureInfo.getRotation()));
                angleModel.setValue(pictureInfo.getRotation());
            }
            if (e.getLatLngChanged()) {
                latitudeJTextField.setText(Double.toString(pictureInfo.getLatLng().x));
                longitudeJTextField.setText(Double.toString(pictureInfo.getLatLng().y));
                positionMap();
            }
            if (e.getCommentChanged()) {
                commentJTextField.setText(pictureInfo.getComment());
            }
            if (e.getPhotographerChanged()) {
                photographerJTextField.setText(pictureInfo.getPhotographer());
            }
            if (e.getCopyrightHolderChanged()) {
                copyrightHolderJTextField.setText(pictureInfo.getCopyrightHolder());
            }

        }
    };

    /**
     * This utility method builds a string from the selected categories in a
     * supplied JList
     *
     * @param theList the List
     * @return a string for the selected categories
     */
    private static String selectedJListCategoriesToString(final JList<Category> theList) {
        final StringBuilder resultString = new StringBuilder();
        if (!theList.isSelectionEmpty()) {
            final List<Category> selectedCategories = theList.getSelectedValuesList();
            String comma = "";
            for (final Category c : selectedCategories) {
                if (!((c.equals(setupCategories)) || (c.equals(noCategories)))) {
                    resultString.append(comma).append(c.toString());
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
        final File f = new File(highresLocationJTextField.getText());
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
    private void getRid() {
        if (myNode.getPictureCollection().getTreeModel() != null) {
            myNode.getPictureCollection().getTreeModel().removeTreeModelListener(myTreeModelListener);
        }
        pictureInfo.removePictureInfoChangeListener(myPictureInfoChangeListener);
        setVisible(false);
        dispose();
    }

    /**
     * Reparse the date from the EXIF information
     */
    private void doReparseDate() {
        try {
            final URL url = new URL(highresLocationJTextField.getText());
            final File file = new File(url.toURI());
            final ExifInfo exifInfo = new ExifInfo(file);
            final String timestamp = exifInfo.getCreateDateTime();
            creationTimeJTextField.setText(timestamp);
            parseTimestamp(timestamp);

        } catch (final MalformedURLException | URISyntaxException ex) {
            Logger.getLogger(PictureInfoEditor.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
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
                        myNode,
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
        final JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileFilter(new ImageFilter());

        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jFileChooser.setApproveButtonText(Settings.getJpoResources().getString("genericSelectText"));
        jFileChooser.setDialogTitle(Settings.getJpoResources().getString("highresChooserTitle"));
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
         * @param e The event
         */
        @Override
        public void treeNodesRemoved(final TreeModelEvent e) {
            if (SortableDefaultMutableTreeNode.wasNodeDeleted(myNode, e)) {
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
            Object object = super.getNextValue();
            if (object == null) {
                object = 0.0;
            }
            return object;
        }

        @Override
        public Object getPreviousValue() {
            Object object = super.getPreviousValue();
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
