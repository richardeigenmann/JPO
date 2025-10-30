module org.jpo {
    exports org.jpo;
    exports org.jpo.gui;
    exports org.jpo.eventbus;
    exports org.jpo.gui.swing;
    exports org.jpo.testground;
    exports org.jpo.datamodel;

    opens org.jpo to com.google.common;
    opens org.jpo.gui to com.google.common;
    opens org.jpo.eventbus to com.google.common;
    opens org.jpo.gui.swing to com.google.common;
    opens org.jpo.datamodel to com.google.common;

    requires activation;
    requires com.google.common;
    requires com.miglayout.swing;
    requires commons.jcs3.core;
    requires docking.frames.common;
    requires java.desktop;
    requires java.logging;
    requires java.prefs;
    requires jide.oss;
    requires jsch;
    requires jsr305;
    requires jwizz;
    requires mail;
    requires metadata.extractor;
    requires org.apache.commons.text;
    requires org.apache.commons.compress;
    requires org.apache.commons.io;
    requires org.apache.commons.lang3;
    requires org.apache.commons.net;
    requires org.jetbrains.annotations;
    requires org.json;
    requires org.tagcloud;
    //requires io.github.classgraph;
    requires jdk.xml.dom;
    requires org.jxmapviewer.jxmapviewer2;  // required for svg
    requires org.apache.commons.logging;
    requires org.jspecify;
    requires io.github.classgraph;
    //requires org.jpo;

    //requires org.mockito.junit.jupiter;  // should be test only but feels left out
}

