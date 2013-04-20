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

            <h2 id="windows">Build on Windows</h2>
            <p>Have you installed the Java Software Developer Kit (SDK)?
                <a href="http://www.oracle.com/technetwork/java/javase/downloads/index.html">Link</a><br>
                Have you installed Netbeans? <a href="http://netbeans.org/downloads/index.html">Link</a><br>

            <p>Start Netbeans and click the menu item Team > CVS > Checkout. Into the CVS Root field 
                paste :pserver:anonymous@j-po.cvs.sourceforge.net:/cvsroot/j-po into the field Click Next Click Finish. 
                On the dialog that appears click Open Project. To compile and run the application press the F6 key.</p>


            <h2 id="source">Installing the source:</h2>
            <p>These instructions are now a little dated as they stem from the 
                pre-Netbeans development phase. RE 8 Oct 2011</p>

            <p>Naturally there are some prerequisites:
                a Java Software Development Kit (SDK), Apache Ant, a CVS client and JUnit.</p>
            <p>Pitfalls to be aware of: Ant can be very fussy and annoying with cryptic,
                incomprehensible error messages. On my SuSE 11.2 machine things have gotten
                much better. SuSE have done all sorts of magic with the Java installation and 
                the 6 release seems to be found by the command line without too much hassle.
                Ant also does some magic so that as long as the ant command itself is found 
                ant is perfectly happy to build. But, to be honest, I have also spent many
                frustrating hours fiddling with things I didn't understand. I can say that 
                there are two very important environment things: the location of the JVM and
                the location of ANT. The location of the JVM can be specified on a Linux (bash) 
                system by:</p>
            <code>export JVM=/usr/java/jdk1.5.0_07</code>
            <p>Test it with this command:</p>
            <code>$JVM/bin/java -version</code>
            <p>If you get something like the following it's ok, otherwise sort out your Java SDK installation first!</p>
            <code>Java(TM) 2 Runtime Environment, Standard Edition (build 1.5.0_07-b03)<br>
                Java HotSpot(TM) Client VM (build 1.5.0_07-b03, mixed mode, sharing)</code>

            <p>Not sure, but JAVA_HOME can be used for the same thing and on my system is set to the
                same directory. I have both variables set to the same thing and it works. Good enough for me.</p>

            <p>Check you have a working java compiler with:</p>
            <code>javac -version<br><br>
                javac 1.5.0_10<br>
                javac: no source files<br>
                Usage: ....</code>

            <p>Now check that your ant works properly (and this is where the
                ANT_HOME environment variable can be important):</p>
            <code>ant -version<br><br>
                Apache Ant version 1.6.5 compiled on May 3 2006</code>

            <p>Use the following command to checkout the latest cvs source from sourceforge:</p>
            <code>cvs -z3 -d:pserver:anonymous@j-po.cvs.sourceforge.net:/cvsroot/j-po checkout -P Jpo</code>

            <p>You would then cd to the Jpo directory:</p>
            <code>cd /wherever/you/put/the/code/.../Jpo</code>

            <p>In order to run the unit tests (without which you can't build) you need
                a working JUnit installation. Check this out with the following command:</p>
            <code>ant JUNIT</code>

            <p>A lot of stuff will scroll over the screen but if the last lines say something
                like the following you are good:</p>
            <code>JUNIT:<br><br>BUILD SUCCESSFUL<br>Total time: 0 seconds</code>


            <p>If the above doesn't work fix it! The command <code>ant -diagnostics</code> might
                be helpful. I wish someone had told be about this years ago. Also <code>ant -debug target</code>.
                Well actually, read the <a href = "http://www.oreilly.com/catalog/anttdg/">O'Reilly Ant book</a>.<br>
                Next you compile the code with the command</p>
            <code>ant compile</code>
            <p>And then run it with</p>
            <code>ant go</code>

            <p>In order to build and deply the jar files you need to generate a key with
                which you can sign the jar files</p>
            <code>keytool -genkey -keystore myKeystore -alias myself</code>
            <p>See the Java Web Start developers guide for details:
                <a href="http://java.sun.com/products/javawebstart/docs/developersguide.html">http://java.sun.com/products/javawebstart/docs/developersguide.html</a></p>


            <p>For convenience I have set up a shell script that sets my environment. I run it with the
                <code>source jpoenv.sh</code> command. It does the following:</p>
            <pre>
#!/bin/sh  
JAVA_HOME=/opt/IBMJava2-14
JAVA_DEV_ROOT=/path_to_my_sources/Jpo 
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
            <p>Set up Eclipse and install the CVS plug-in</p>
            <p>File > New > Other... > expand CVS > Projects from CVS</p>
            <p>Host: j-po.cvs.sourceforge.net</p>
            <p>Repository path: /cvsroot/j-po</p>
            <p>User: anyonymous</p>
            <p>Password: leave empty</p>
            <p>Connection type: leave on pserver</p>
            <p>leave radio button on "Use default port"</p>
            <p>Next Panel</p>
            <p>Use specified module name: Jpo</p>
            <p>Next Panel</p>
            <p>Put radiobutton into "Check out as a project in the workspace"</p>
            <p>Project Name: Jpo</p>
            <p>Next right-click on the build.xml file > Run/Debug Settings > Make sure you have a JRE selected.</p>
            <p>To run right-click on the build.xml file and click run-as Ant Build file.

            <hr>
            <p>Last update to this page: 6 April 2013<br>
                Copyright 2003-2013 by Richard Eigenmann, Z&uuml;rich, Switzerland</p>
        </td>
    </tr>

</table>


</body>
</html>
