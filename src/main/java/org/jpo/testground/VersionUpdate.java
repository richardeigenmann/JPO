package org.jpo.testground;

import org.jpo.datamodel.Settings;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @see <a href="https://stackoverflow.com/questions/24177348/font-awesome-with-swing">https://stackoverflow.com/questions/24177348/font-awesome-with-swing</a>
 */
public class VersionUpdate {

    public VersionUpdate() {
        EventQueue.invokeLater(() -> {

            JButton retrieveJson = new JButton("Retrieve Json");
            retrieveJson.addActionListener(e -> {
                System.out.println("Button clicked");
                final String versionJson;
                try {
                    versionJson = readStringFromURL(Settings.JPO_VERSION_URL);
                    System.out.println(versionJson);

                    final JSONObject obj = new JSONObject(versionJson);
                    final String versionString = obj.getString("currentVersion");
                    final Float currentVersion = Float.valueOf(versionString);
                    System.out.println(currentVersion);
                    if (currentVersion > Float.valueOf(Settings.JPO_VERSION)) {
                        System.out.println("JPO is out of date");

                        JEditorPane ep = new JEditorPane("text/html", "<html><body>"
                                + "You are running version " + Settings.JPO_VERSION
                                + " of JPO.<br>The current version is " + versionString
                                + "<br>Would you like to visit the website<br><a href=\"" + Settings.JPO_DOWNLOAD_URL
                                + "\">" + Settings.JPO_DOWNLOAD_URL + "</a><br>so you can upgrade?"
                                + "</body></html>");
                        // handle link events
                        ep.addHyperlinkListener(new HyperlinkListener() {
                            @Override
                            public void hyperlinkUpdate(HyperlinkEvent e) {
                                if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED))
                                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                                        try {
                                            Desktop.getDesktop().browse(new URI(Settings.JPO_DOWNLOAD_URL));
                                            JOptionPane.getRootFrame().dispose();
                                        } catch (IOException ioException) {
                                            ioException.printStackTrace();
                                        } catch (URISyntaxException uriSyntaxException) {
                                            uriSyntaxException.printStackTrace();
                                        }
                                    }
                            }
                        });
                        ep.setEditable(false);

                        JCheckBox neverShowAgain = new JCheckBox("Don't ask again (Edit > Settings)");
                        neverShowAgain.setHorizontalAlignment(JCheckBox.LEFT);

                        JPanel content = new JPanel();
                        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
                        content.add(ep);
                        content.add(neverShowAgain);


                        int n = JOptionPane.showConfirmDialog(Settings.getAnchorFrame(), content, "Title",
                                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        System.out.println("Selected: " + n);

                        if (n == 0) {
                            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                                Desktop.getDesktop().browse(new URI(Settings.JPO_DOWNLOAD_URL));
                            }
                        }

                    }

                } catch (IOException | URISyntaxException ioException) {
                    ioException.printStackTrace();
                }
            });

            JFrame frame = new JFrame("Version Update");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.add(retrieveJson);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }


    public static String readStringFromURL(final String requestURL) throws IOException {
        try (Scanner scanner = new Scanner(new URL(requestURL).openStream(),
                StandardCharsets.UTF_8.toString())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    public static void main(String[] args) {
        new VersionUpdate();
    }

}
