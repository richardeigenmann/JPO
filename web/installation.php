<?php include("page-start.php"); ?>

<div class="container">
    <h2>Installing on Windows</h2>

    <a href="https://sourceforge.net/projects/j-po/files/latest/download" rel="nofollow">
        <img alt="Download JPO Java Picture Organizer" src="https://a.fsdn.com/con/app/sf-download-button">
    </a>

    <img src="https://travis-ci.org/richardeigenmann/JPO.svg?branch=master" alt="Travis Build Status">

    <a href="http://sourceforge.net/projects/j-po/files"> 
        <h2><span class="label label-success">
                <span class="glyphicon glyphicon-save" aria-hidden="true"></span>
                SourceForge download area
            </span>
        </h2></a>

    <hr>
    <span class="para-heading" id="javawebstart">Java Web Start</span>

    <a href="jpo-devel.jnlp">
        <h2><span class="label label-success">
                <span class="glyphicon glyphicon-play" aria-hidden="true"></span>
                Development Version (0.13)
            </span>
        </h2>
    </a><br>
    <a href="jpo-0.13.jnlp">
        <h2><span class="label label-success">
                <span class="glyphicon glyphicon-play" aria-hidden="true"></span>
                Current Version (0.13)
            </span>
        </h2>
    </a><br>
    <a href="jpo-0.12.jnlp">
        <h2><span class="label label-success">
                <span class="glyphicon glyphicon-play" aria-hidden="true"></span>
                Old Version (0.12)
            </span>
        </h2>
    </a><br>
    <a href="jpo-stable.jnlp">
        <h2><span class="label label-default">
                <span class="glyphicon glyphicon-play" aria-hidden="true"></span>
                Old Version (0.9)
            </span>
        </h2>
    </a>


    <h2>Scary Warnings</h2>
    <p>I have not figured out how do get a certificate with which to sign
        this Free Open Source Software. Consequently Java, your browser and everyone 
        else feel they are at liberty to pop up increasingly alarmist messages 
        warning against the use of this software. At this point all I can recommend
        is that you click away the warnings and use JPO if you find it useful.</p>

    <p>If you have any suggestions on how to improve on this state of affairs,
        please get in touch with me.</p>

    <p><strong>Note:</strong> You will see the following warning screen:</p>

    <p><img src="jpo_scr_4.jpg" width="493" height="240" border="0"></p>


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


    <span class="para-heading" id="requirements">System Requirements</span>

    <p>JPO is a pure Java application so it runs on any computer that runs Java.</p>

    <p>You need <strong>a recent Java runtime installation</strong>. You must have Java 
        1.8.0 or later. Use the command <code>java -version</code> to find out 
        what version you are running if you aren't sure.</p>


    <p>You can download the Java Runtime Environment (JRE) or the Java Software Development Kit (SDK)
        here: <a href="http://www.java.com/en/download/index.jsp">http://www.java.com/en/download/index.jsp</a>
        Get the JRE is you just want to run java programs. Get the SDK if you want to develop code.</p>

    <p>Dealing with bitmaps inside the JVM consumes huge amounts of memory. On low specified
        machines this can become frustrating when the operating system decides to spend minutes 
        swapping memory around. You should be OK if you have more than 512MB and are 
        dealing with 6 Megapixel images. You can tweak things by changing the amount of 
        pictures JPO is allowed to cache in the settings. You can also tweak the amount of
        memory allocated to the JVM but you will need to make a local installation described
        below.</p>



    <hr>
    <span class="para-heading" id="local">Local Installation</span>
    <p>First download the jar file and save it in your program directory:<br>
        <a href="http://j-po.sourceforge.net/jpo-0.12.jar">jpo-0.13.jar</a> or <a href="http://j-po.sourceforge.net/jpo-0.12.jar">jpo-0.13.jar</a><br>
    </p>

    <p><strong>Note:</strong> Make sure they retain the <code>.jar</code> file extension. 
        One Microsoft XP computer I saw saved the file with a <code>.zip</code> extension.
        Java then was not able to read the code.


    <p> Then you need to create a script or batch file to run everything. On Linux the script would look like this:</p>
    <p><font color="darkRed"><code>/PATH/TO/YOUR/JAVA/bin/java -XX:+AggressiveHeap -classpath /PATH/TO/YOUR/JPO/JAR/jpo-0.13.jar Main</code></font></p>
    <!-- -Xms80M -Xmx2000M -->

    <p><strong>Note:</strong> Put everything on one long line.</p>

    <p>On a particular Windows XP machine I installed Jpo into c:\Program Files\Jpo. The resulting Batch
        file looks like this: (you can download it here: <a href="Jpo.bat">Jpo.bat</a></p>

    <p><code>c:\windows\system32\java -XX:+AggressiveHeap -jar "c:\Program Files\Jpo\jpo-0.13.jar"</code></p>
    <!-- -Xms80M -Xmx2000M -->


    <hr>
    <span class="para-heading" id="problems">Potential problems:</span>

    <h2>Memory settings:</h2>
    <p>If you get <font color="red">out of Memory</font> errors then you are probably best off
        using the local jar way of starting this application as you can then set the initial memory heap with 
        the -Xms and the maximum memory heap with the -Xmx parameters. Of course this can also be done on 
        the web start version but you would have to set Jpo up on your own webserver so that you 
        could specify the memory settings in the .jnlp configuration file.</p>
    <p>I have discovered the option -XX:+AggressiveHeap which sounds great for what JPO needs. Please let me know if you have issues with this.</p>

    <h2>Chinese Font</h2>
    <p>Franklin He has been kind enough to translate the User Interface to Traditional and Simplified
        Chinese. This looks really cool but poses some installation hassles if your Java system 
        is not correctly configured.
        When I had a SuSE 10 system I had to create a /usr/java/jdk1.5.0_07/jre/lib/fontconfig.SuSE.properties 
        file. Click <a href="fontconfig">here</a> to download my version.</p>

    <h2>Java Version</h2>
    <p>I develop on Linux with the latest Oracle Java. You should always keep your software
        up-to-date too because of the the vulnerabilities that keep getting fixed. But sometimes
        there are reasons (that <a href="http://en.wikipedia.org/wiki/List_of_Dilbert_characters#Mordac">Mordac</a>
        , the preventer of Information Services in Dilbert can justify)
        that prevent you from having a current Java installation. What happens then is that the 
        code I compiled is not compatible with your Java Virtual Machine and you get an error. Java 
        still gives you half a page full of cryptic junk. If the first line says 
        <code>Unsupported major.minor version 51.0</code> then that is the problem. If you can't 
        upgrade your Java due to "Mordac" or don't want to get dirty with the source-code and compile 
        your own send me an email and perhaps I can compile for an old 
        version for you. Currently I am compiling to Java version 1.8 on a Java 8 compiler.</p>

    <p>Please let me know of specific installation issues you have so that I may extend this section for the benefit of other users who might have the same issue.</p>



    <hr>
    <p>Last update to this page: 5 Dec 2015<br>
        Copyright 2003-2016 by Richard Eigenmann, Z&uuml;rich, Switzerland</p>
</div>
<?php include("page-end.php"); ?>
