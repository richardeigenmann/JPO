module org.jpo {
    exports org.jpo;
    exports org.jpo.gui;
    exports org.jpo.eventbus;
    exports org.jpo.gui.swing;

    opens org.jpo to com.google.common;
    opens org.jpo.gui to com.google.common;
    opens org.jpo.eventbus to com.google.common;
    opens org.jpo.gui.swing to com.google.common;

    requires java.desktop;
    requires java.logging;
    requires java.prefs;
    requires com.miglayout.swing;
    requires org.tagcloud;
    requires org.apache.commons.compress;
    requires commons.jcs3.core;
    requires commons.jcs3.jcache;
    requires org.apache.commons.io;
    requires org.jetbrains.annotations;
    requires com.google.common;
    requires org.apache.commons.text;
    requires jsch;
    requires org.apache.commons.net;
    requires org.checkerframework.checker.qual;
    requires jxmapviewer2;
    requires docking.frames.common;
    requires jide.oss;
    //requires javax.cache;
    //requires org.apache.batik.bridge.RhinoInterpreterFactory;
}

