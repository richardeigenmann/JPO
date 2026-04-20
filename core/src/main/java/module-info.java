module org.jpo.datamodel {
    exports org.jpo.datamodel;

    opens org.jpo.datamodel to com.google.common;

    requires activation;
    requires com.google.common;
    requires commons.jcs3.core;
    requires java.desktop;
    requires java.logging;
    requires java.prefs;
    requires jide.oss;
    requires jsch;
    requires jsr305;
    requires mail;
    //requires metadata.extractor;
    requires org.apache.commons.text;
    requires org.apache.commons.compress;
    requires org.apache.commons.io;
    requires org.apache.commons.lang3;
    requires org.apache.commons.net;
    requires org.jetbrains.annotations;
    requires org.json;
    //requires io.github.classgraph;
    requires jdk.xml.dom;
    requires org.apache.commons.logging;
    requires org.jspecify;
    requires io.github.classgraph;
    requires java.datatransfer;
    requires com.drew.metadata;
    requires org.apache.tika.core;

    //requires org.mockito.junit.jupiter;  // should be test only but feels left out
}

