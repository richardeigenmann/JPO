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

            <p>Start Netbeans and click the menu item <pre>Team > Other CVS > Checkout</pre>. Into the CVS Root field 
            paste the following:<br>
            <pre>:pserver:anonymous@j-po.cvs.sourceforge.net:/cvsroot/j-po</pre>
            Click Next Click Finish. 
            On the dialog that appears click Open Project. To compile and run the application press the F6 key.</p>


            <h2 id="source">Installing the source:</h2>

            <p>Do you have the prerequisites?</p>
            <ul>
                <li>Java Software Development Kit (SDK) 1.7 or later</li>
                <li>Apache Ant</li>
                <li>CVS</li>
                <li>Junit</li>
            </ul>


            <p>I've had my fair share of issues with the prerequisites. Here is how to test your set-up:</p>

            <code># Depending on your environment you might need to<br>
                export JVM=/usr/java/jdk1.7.0_55<br>
                # or<br>
                export JAVA_HOME=/usr/java/jdk1.7.0_55<br></code>
            <p>Test it with this command:</p>
            <code>$JVM/bin/java -version</code>
            <p>If you get something like the following it's ok, otherwise sort out your Java SDK installation first!</p>
            <code>java version "1.7.0_55"
                Java(TM) SE Runtime Environment (build 1.7.0_55-b13)<br>
                Java HotSpot(TM) 64-Bit Server VM (build 24.55-b03, mixed mode)</code>

            <p>Check you have a working Java compiler with:</p>
            <code>javac -version<br><br>
                javac 1.7.0_55</code>

            <p>Now check that your ant works properly (and you might have to set the ANT_HOME environment variable):</p>
            <code>ant -version<br><br>
                Apache Ant(TM) version 1.9.2 compiled on October 2 2013</code>

            <p>Use the following command to checkout the latest cvs source from sourceforge:</p>
            <code>cvs -z3 -d:pserver:anonymous@j-po.cvs.sourceforge.net:/cvsroot/j-po checkout -P Jpo</code>

            <p>You would then cd to the Jpo directory:</p>
            <code>cd /wherever/you/put/the/code/.../Jpo</code>

            <p>In order to run the unit tests (without which you can't build) you need
                a working JUnit installation. Check this out with the following command:</p>
            <code>ant JUNIT
                Buildfile: /home/richi/Downloads/Jpo/build.xml<br>
                [echo] Apache Ant(TM) version 1.9.2 compiled on October 2 2013 is using the <br>
                [echo] build file /wherever/you/put/the/code/Jpo/build.xml to build the project<br>
                [echo] "Java Picture Organizer" Use the "-debug" switch to see loads of<br>
                [echo] debug information, "ant --help" reminds you of what other options<br>
                [echo] there are and "ant -p" tells you what targets this build file supports.<br>
                <br>
                JUNIT:<br>
                [echo] Testing if JUnit is present: true<br>
                <br>
                BUILD SUCCESSFUL<br>
                Total time: 0 seconds<br>
            </code>

            <p>The line with "Testing if JUnit is present: true" is the key here.</p>
            <code>JUNIT:<br><br>BUILD SUCCESSFUL<br>Total time: 0 seconds</code>


            <p>The command <code>ant -diagnostics</code> might
                be helpful. I wish someone had told be about this years ago. Also <code>ant -debug target</code>.
                Well actually, read the <a href = "http://www.oreilly.com/catalog/anttdg/">O'Reilly Ant book</a>.</p>

            <p>Now compile the code:</p>
            <code>ant compile</code>
            <p>And then run it with</p>
            <code>ant go</code>

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
            <p>Last update to this page: 4 July 2014<br>
                Copyright 2003-2014 by Richard Eigenmann, Z&uuml;rich, Switzerland</p>
        </td>
    </tr>

</table>


</body>
</html>
