package org.jpo.datamodel;

import org.jpo.eventbus.JpoEventBus;
import org.jpo.eventbus.RemoveOldLowresThumbnailsRequest;
import org.jpo.gui.swing.LabelFrame;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 XmlReader.java:  class that reads the xml file

 Copyright (C) 2002 - 2022  Richard Eigenmann.
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
 * class that reads an XML collection and creates a tree of
 * SortableDefaultMutableTreeNodes
 */
public class XmlReader {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger(XmlReader.class.getName());

    private XmlReader() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Constructor an XML parser that can read our picture list XML files.
     *
     * @param inputStream The stream which is to be parsed
     * @param startNode   The node that becomes the root node for the nodes being. It is modified!
     *                    read.
     */
    public static void read(final InputStream inputStream, final SortableDefaultMutableTreeNode startNode) {
        final var bufferedInputStream = new BufferedInputStream(inputStream);

        final var lowresUrls = new StringBuilder();


        final var factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        SAXParser saxParser;
        try {
            saxParser = factory.newSAXParser();
            saxParser.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            saxParser.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        } catch (final ParserConfigurationException | SAXException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return;
        }

        final var loadProgressGui = new LabelFrame(Settings.getJpoResources().getString("org.jpo.dataModel.XmlReader.loadProgressGuiTitle"));
        try {
            saxParser.parse(bufferedInputStream, new SaxEventHandler(startNode, loadProgressGui, lowresUrls));
        } catch (final SAXParseException spe) {
            // Error generated by the parser
            LOGGER.log(Level.INFO, "\n** Parsing error" + ", line {0}, uri {1}", new Object[]{spe.getLineNumber(), spe.getSystemId()});
            LOGGER.log(Level.INFO, "   {0}", spe.getMessage());
        } catch (final SAXException sxe) {
            // Error generated by this application
            // (or a parser-initialization error)
            LOGGER.severe("SAXException: " + sxe.getMessage());
            if (sxe.getException() != null) {
                LOGGER.severe("Embedded Exception: " + sxe.getException().getMessage());
            }
        } catch (final IOException ex) {
            LOGGER.severe("IOException: " + ex.getMessage() );
        }

        loadProgressGui.getRid();

        if ( lowresUrls.length() > 1 ) {
            LOGGER.log(Level.FINE, "lowresUrls length is {0}", lowresUrls.length());
            JpoEventBus.getInstance().post(new RemoveOldLowresThumbnailsRequest(lowresUrls));

        }
    }

}
