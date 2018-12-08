<?php include("page-start.php"); ?>
<div class="container">
    <h3>System Requirements</h3>

    <p>JPO is a pure Java application so it runs on any computer that runs Java.</p>

    <p>You must have Java 1.8.0 or later. Use the command <code>java -version</code> to find out 
    what version you are running if you aren't sure.</p>

    <p>You can download the Java Runtime Environment (JRE) or the Java Software Development Kit (SDK)
        here: <a href="http://www.java.com/en/download/index.jsp">http://www.java.com/en/download/index.jsp</a>
        Get the JRE is you just want to run java programs. Get the SDK if you want to develop code.</p>


    <hr>
    <h3>Installing on Windows</h3>

    <a href="https://sourceforge.net/projects/j-po/files/JPO-Installer-0.13.exe/download" rel="nofollow">
        <img alt="Download JPO Java Picture Organizer for Windows" src="https://a.fsdn.com/con/app/sf-download-button">
    </a>

    <a href="http://sourceforge.net/projects/j-po/files">SourceForge download area</a>

    <hr>
    <h3>Java Web Start</h3>

    <p><a href="jpo-0.13.jnlp">
        <button type="button" class="btn btn-success">Current version (0.13)</button>
    </a></p>
    
    <p><a href="jpo-devel.jnlp">
        <button type="button" class="btn btn-success">Development version (0.13)</button>
    </a></p>

    <p><a href="jpo-0.12.jnlp">
        <button type="button" class="btn btn-success">Old version (0.12)</button>
    </a></p>

    <p><a href="jpo-stable.jnlp">
        <button type="button" class="btn btn-success">Old "stable" version</button>
    </a></p>


    <hr>
    <h3>Scary Warnings?</h3>
    <img src="jpo_scr_5.png" width=658 height=591 alt="Java Scary Error"><br>
    <p>The latest versions of Java default to ultra paranoid security 
        settings. Given the state of the world this is a good thing but it can 
        stop you from running JPO. JPO does need to read the pictures on your filesystem and does need to 
        write to your disk as that pretty much is the point of a software to organise your
        pictures. I have not bought a certificate from an issuer which makes JPO 'untrustworthy'. 
        I don't know why you should trust software simply because someone paid money to someone else 
        for a digital certificate unless that somehow involves a code review and 
        ongoing good practices.</p>
    <p>I can assure you that JPO doesn't spy on you, doesn't send spam and doesn't try to sell you 
        anything. Better than that, it's open source software so you or someone you trust can go and
        analyse the lines of source code and compile your own version! Check out the linked code analysis tool reports to see
        how JPO is doing in terms of code quality, test coverage and other developer metrics.</p>

    <hr>
    <h3>Setting up Java Web Start to run JPO</h3>

    <p>As of 2018 you need to whitelist the JPO site URL in the Java Console. Find the Java Console program
    (called jconsole) and run it. Go to the Web Settings > Exception Site List and add the JPO URL there:</p>

    <p><img src="Java_Control_Panel.png" width="902" height="575" border="0"></p>
    <p><img src="Java_Control_Panel_add_jpo.png" width="894" height="532" border="0"></p>
    <p><img src="Java_Control_Panel_jpo_added.png" width="892" height="527" border="0"></p>


    <p><strong>Potential problem:</strong> Your browser needs to know that it should
        start "Java Web Start" when it downloads a <code>.jnlp</code> extension file (Mimetype 
        <code>application/x-java-jnlp-file</code>). If your browser doesn't have the right
        association you can run it from the command line or by creating a desktop icon. The command 
        looks like this:</p>
    <p><font color="darkRed"><code>javaws http://j-po.sourceforge.net/jpo-stable.jnlp</code></font></p>

    <p>Often you will need to know where the javaws program was installed. On
        Windows do a search for javaws.exe. On Linux you can do <br>
        <code><font color="darkRed">
            find / -name javaws -print</font></code></p>


    <hr>
    <h3>Local Installation</h3>
    <p>First download the jar file and save it in your program directory:<br>
        <a href="http://j-po.sourceforge.net/jpo-0.12.jar">jpo-0.12.jar</a> or <a href="http://j-po.sourceforge.net/jpo-0.12.jar">jpo-0.13.jar</a><br>
    </p>
    <p><strong>Note:</strong> Make sure they retain the <code>.jar</code> file extension.

    <p> Then you need to create a script or batch file to run everything. On Linux the script would look like this:</p>
    <p><code>/PATH/TO/YOUR/JAVA/bin/java -XX:+AggressiveHeap -classpath /PATH/TO/YOUR/JPO/JAR/jpo-0.13.jar Main</code></p>
    <!-- -Xms80M -Xmx2000M -->

    <p><strong>Note:</strong> Put everything on one long line.</p>

    <p>On a particular Windows machine I installed JPO into c:\Program Files\Jpo. The resulting 
        <code>JPO.bat</code> file looks like this: (you can download it here: <a href="Jpo.bat">Jpo.bat</a></p>

    <p><code>c:\windows\system32\java -XX:+AggressiveHeap -jar "c:\Program Files\Jpo\jpo-0.13.jar"</code></p>
    <!-- -Xms80M -Xmx2000M -->


    <hr>
    <p>Last update to this page: 7 Dec 2018<br>
        Copyright 2003-2016 by Richard Eigenmann, Z&uuml;rich, Switzerland</p>
</div>
<?php include("page-end.php"); ?>
