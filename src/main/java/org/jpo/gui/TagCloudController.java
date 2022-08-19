package org.jpo.gui;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.datamodel.TextQuery;
import org.jpo.eventbus.JpoEventBus;
import org.jpo.eventbus.ShowGroupRequest;
import org.jpo.eventbus.ShowQueryRequest;
import org.tagcloud.TagClickListener;
import org.tagcloud.TagCloud;
import org.tagcloud.WeightedWord;
import org.tagcloud.WeightedWordInterface;

import javax.swing.*;
import java.util.*;

/*
 Copyright (C) 2009-2022  Richard Eigenmann.
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
 * Manages the Tag Cloud
 */
public class TagCloudController implements TagClickListener {

    /**
     * Reference to the TagCloud widget
     */
    private final TagCloud tagCloud = new TagCloud();

    /**
     * Constructs the Controller
     */
    public TagCloudController() {
        JpoEventBus.getInstance().register( TagCloudController.this );
        tagCloud.addTagClickListener(TagCloudController.this);
        tagCloud.setMaxWordsToShow(Settings.getTagCloudWords());
    }


    /**
     * Returns the tag cloud component
     *
     * @return the tag cloud component
     */
    public JComponent getTagCloud() {
        return tagCloud;
    }

    private NodeWordMapper nodeWordMapper;

    /**
     * Handles the ShowGroupRequest by updating the display...
     *
     * @param event Event
     */
    @Subscribe
    public void handleGroupSelectionEvent( final ShowGroupRequest event ) {
        SwingUtilities.invokeLater( () -> {
            nodeWordMapper = new NodeWordMapper(event.node());
            tagCloud.setWordsList(nodeWordMapper.getWeightedWords());
        } );
    }

    @Override
    public void tagClicked(final WeightedWordInterface weightedWord) {
        if (nodeWordMapper == null) {
            return;
        }

        final var textQuery = new TextQuery(weightedWord.getWord());
        textQuery.setStartNode(nodeWordMapper.getRootNode());
        JpoEventBus.getInstance().post(new ShowQueryRequest(textQuery));
    }

    private static final Set<String> strikeWordsSet = new HashSet<>(Arrays.asList(
            "als",
            "Am",
            "am",
            "an",
            "An",
            "and",
            "at",
            "auf",
            "Auf",
            "Aufstieg",
            "aus",
            "bei",
            "Bei",
            "Beim",
            "beim",
            "Blick",
            "by",
            "das",
            "Das",
            "de",
            "del",
            "dem",
            "den",
            "der",
            "Der",
            "des",
            "Die",
            "die",
            "DSC",
            "dsc",
            "DSCN",
            "durch",
            "Ein",
            "ein",
            "eine",
            "einem",
            "einen",
            "einer",
            "eines",
            "El",
            "en",
            "fuer",
            "für",
            "gesehen",
            "hat",
            "hinter",
            "ich",
            "II",
            "Il",
            "im",
            "Im",
            "Image",
            "img",
            "IMG",
            "in",
            "In",
            "in",
            "ist",
            "je",
            "La",
            "man",
            "meine",
            "MG",
            "mir",
            "Mit",
            "mit",
            "nach",
            "neben",
            "Neuer",
            "nicht",
            "noch",
            "Nähe",
            "nähert",
            "ob",
            "of",
            "of",
            "on",
            "Richtung",
            "san",
            "SDC",
            "sein",
            "sich",
            "The",
            "the",
            "to",
            "ueber",
            "um",
            "und",
            "uns",
            "unter",
            "Unterwegs",
            "unterwegs",
            "van",
            "vom",
            "von",
            "vor",
            "warum",
            "was",
            "wer",
            "wie",
            "wie",
            "wir",
            "wird",
            "with",
            "without",
            "wurde",
            "während",
            "zu",
            "zum",
            "zur",
            "zwischen",
            "über"
    ) );

    private static final String[] multiWordTerms = {
        "Aprés Ski",
        "Cape Town",
        "Crans Montana",
        "Den Haag",
        "Empire State",
        "Goldman Sachs",
        "Groot Marico",
        "Halfmoon Bay",
        "Hoch Ybrig",
        "Lions Head",
        "Marigot Bay",
        "Nags Head",
        "New York",
        "New Zealand",
        "Persischer Golf",
        "Petit Bateau",
        "Quadra Island",
        "Red Sea",
        "Rotes Meer",
        "Saudi Arabien",
        "Seleger Moor",
        "South Africa",
        "South Georgia",
        "St Gallen",
        "St. Petersinsel",
        "Tel Aviv",
        "Toten Meer",
        "Totes Meer",
        "Vic Falls",
        "Victoria Falls",
        "Washington State"
    };


    private static class NodeWordMapper {

        private final List<WeightedWordInterface> weightedWordList = new ArrayList<>();

        private final SortableDefaultMutableTreeNode rootNode;

        NodeWordMapper(final SortableDefaultMutableTreeNode node) {
            this.rootNode = node;
            buildList();
        }

        public List<WeightedWordInterface> getWeightedWords() {
            return weightedWordList;
        }

        public SortableDefaultMutableTreeNode getRootNode() {
            return rootNode;
        }

        /**
         * Zips through the nodes and builds the word to node set map.
         */
        private void buildList() {
            final var nodes = rootNode.breadthFirstEnumeration();
            while ( nodes.hasMoreElements() ) {
                if (((SortableDefaultMutableTreeNode) nodes.nextElement()).getUserObject() instanceof PictureInfo pi) {
                    splitAndAdd(wordCountMap, pi.getDescription());
                }
            }
            wordCountMap.keySet().forEach(key -> weightedWordList.add(new WeightedWord(key, wordCountMap.get(key))));
        }

        /**
         * split the string and add the node to the map
         *
         * @param description The description of split
         */
        private static void splitAndAdd(final Map<String, Integer> wordCountMap, final String description) {
            final var fixAprostropheS = description.replace("\'s", "");
            final var noPunctuation = fixAprostropheS.replaceAll("[\\.:!,\\'\\\";\\?\\(\\)\\[\\]#\\$\\*\\+<>\\/&=]", "");
            var noNumbers = noPunctuation.replaceAll("\\d", "");

            for (final var multiWordTerm : multiWordTerms) {
                if (noNumbers.contains(multiWordTerm)) {
                    noNumbers = noNumbers.replace(multiWordTerm, "");
                    addWord(wordCountMap, multiWordTerm);
                }
            }

            for (final var s : noNumbers.split("[\\s_\\-]+")) {
                if (!strikeWordsSet.contains(s)) {
                    addWord(wordCountMap, s);
                }
            }
        }

        private final Map<String, Integer> wordCountMap = new HashMap<>();

        private static void addWord(final Map<String, Integer> wordCountMap, final String word) {
            if (wordCountMap.containsKey(word)) {
                wordCountMap.put(word, wordCountMap.get(word) + 1);
            } else {
                wordCountMap.put(word, 1);
            }
        }

    }
}
