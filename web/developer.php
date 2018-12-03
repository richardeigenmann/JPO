<!DOCTYPE HTML>
<html>
    <head>
        <title>JPO Homepage</title>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    </head>
    <body>
        <table CELLSPACING=0 CELLPADDING=10>
            <tr>
                <th colspan="2" height="60" bgcolor="#97a4da"><h1>JPO Java Picture Organizer</h1></th>
    </tr>
    <tr>
        <td width="150" bgcolor="#97a4da" valign="top">
            <?php include("nav.html"); ?>
        </td>
        <td>

            <h2 id="windows">Build your own with Netbeans</h2>
            <p>Have you installed the Java Software Developer Kit (SDK)?
                <a href="http://www.oracle.com/technetwork/java/javase/downloads/index.html">Link</a><br>
                Have you installed Netbeans? <a href="http://netbeans.org/downloads/index.html">Link</a><br>
                And does your Netbeans have the CVS plugin?<br>

            <p>Start Netbeans and click the menu item <pre>Team > Git > Clone</pre>. Into the CVS Root field
            paste the following:<br>
            <pre>github.com:richardeigenmann/JPO.git</pre>
            Click Next Click Finish. 
            On the dialog that appears click Open Project. To compile and run the application press the F6 key.</p>


            <h2 id="source">Installing the source:</h2>

            <p>Do you have the prerequisites?</p>
            <ul>
                <li>Java Software Development Kit (SDK) 1.8 or later</li>
                <li>Gradle</li>
                <li>Git</li>
            </ul>


            <p>I've had my fair share of issues with the prerequisites. Here is how to test your set-up:</p>

            <code># Depending on your environment you might need to<br>
                export JVM=/usr/java/jdk1.8.0_65<br>
                # or<br>
                export JAVA_HOME=/usr/java/jdk1.8.0_65<br></code>
            <p>Test it with this command:</p>
            <code>$JVM/bin/java -version</code>
            <p>If you get something like the following it's ok, otherwise sort out your Java SDK installation first!</p>
            <code>java version "1.8.0_65"
                Java(TM) SE Runtime Environment (build 1.8.0_65-b17)<br>
                Java HotSpot(TM) 64-Bit Server VM (build 25.65-b01, mixed mode)</code>

            <p>Check you have a working Java compiler with:</p>
            <code>javac -version<br><br>
                javac 1.8.0_65</code>

            <p>Now check that your Gradle works properly:</p>
            <code>gradle -version<br><br>
<br>
------------------------------------------------------------<br>
Gradle 5.0<br>
------------------------------------------------------------<br>
<br>
Build time:   2018-11-26 11:48:43 UTC<br>
Revision:     7fc6e5abf2fc5fe0824aec8a0f5462664dbcd987<br>
<br>
Kotlin DSL:   1.0.4<br>
Kotlin:       1.3.10<br>
Groovy:       2.5.4<br>
Ant:          Apache Ant(TM) version 1.9.13 compiled on July 10 2018<br>
JVM:          1.8.0_181 (Oracle Corporation 25.181-b13)<br>
OS:           Linux 4.12.14-lp150.12.25-default amd64</code>

            <p>Use the following command to checkout the latest cvs source from Github:</p>
            <code>git clone https://github.com/richardeigenmann/JPO.git</code>

            <p>You would then cd to the Jpo directory:</p>
            <code>cd /wherever/you/put/the/code/.../Jpo</code>

            <code>gradle run</code>

            <p>In order to build and deply the jar files you need to generate a key with
                which you can sign the jar files</p>
            <code>keytool -genkey -keystore myKeystore -alias myself</code>
            <p>See the Java Web Start developers guide for details:
                <a href="http://java.sun.com/products/javawebstart/docs/developersguide.html">http://java.sun.com/products/javawebstart/docs/developersguide.html</a></p>


            <p>For convenience used to use a shell script that sets up my environment. I used to run it with the
                <code>source jpoenv.sh</code> command. It did the following:</p>
            <pre>
#!/bin/sh  
JAVA_HOME=JAVA_HOME=/usr/lib64/jvm/java-1.7.0
IS_UNIX=true 
JAVA_KEY_STORE=/path_to_my_keystore/javaKeyStore
JAVA_KEY_STORE_KEY=my_secret_key

export JAVA_HOME 
export JAVA_DEV_ROOT 
export IS_UNIX
export JAVA_KEY_STORE
export JAVA_KEY_STORE_KEY

cd /path_to_my_sources/Jpo
            </pre>



            <p>The following targets are supported by the ant buildfile:</p>
            <code>ant compile     - compiles everything requiring a compilation. The classes are put in<br>
                build/classes   by their package (which is jpo)<br>
                ant run		- runs the application from build/classes<br>
                ant clean	- deletes the compiled classes<br>
                ant rebuild	- run clean and compile<br>
                ant javadoc	- creates the javadoc in the directory<br>
                build/docs/index.html<br>
                ant go          - compiles and runs the application<br>
                ant buildjar    - builds the jar files<br>
                ant signjar	- signs the jar files (if yout have the keys...)</code>



            <p>Sourcefore also have some notes about downloading with cvs: <a href="http://sourceforge.net/cvs/?group_id=71359">http://sourceforge.net/cvs/?group_id=71359</a></p>

            <p>Sourceforge has a neat tool to browse the source code which also highlights
                differences between version very nicely: <a href="http://j-po.cvs.sourceforge.net/j-po/">http://j-po.cvs.sourceforge.net/j-po/</a></p>



            <h2 id="eclipse">Using Eclipse</h2>
            <p>Set up Eclipse and install the Git plug-in</p>
            <p>File > Import > Git > Projects from Git > Next > Clone URI > Next</p>
            <p>URI: https://github.com/richardeigenmann/JPO.git</p>
            <p>You have to have the Gradle plug in enabled and it should find all the build targets</p>

            <hr>
            <p>Last update to this page: 3 Dec 2018<br>
                Copyright 2003-2018 by Richard Eigenmann, Z&uuml;rich, Switzerland</p>
        </td>
    </tr>

</table>


</body>
</html>
