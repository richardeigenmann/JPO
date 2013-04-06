<!DOCTYPE HTML>
<html>
    <head>
        <title>JPO Homepage</title>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    </head>
    <body>
        <table CELLSPACING=0 CELLPADDING=10>
            <tr>
                <th colspan="2" height="60" bgcolor="#97a4da"><h1>JPO  Java Picture Organizer</h1></th>
    </tr>
    <tr>
        <td width="150" bgcolor="#97a4da" valign="top">
            <?php include("nav.html"); ?>
        </td>
        <td>

            <hr>
            <h2 id="windows">Windows</h2>
            <p>For Windows users there is a packaged installer version available. Download it from the 
                <a href="http://sourceforge.net/projects/j-po/files">SourceForge download area</a>.
                This version is best suited if you want to run JPO in an offline environment. There is only the one installation file. 
                You do need to have Java installed on the box before running the installer though.</p>

            <hr>
            <h2 id="javawebstart">Java Web Start</h2>

            <p>This version is great if you have Java installed and properly integrated
                with your browser. You just click on the link and Java Web start fires up
                and downloads all the pieces. It automatically keeps the application up-to-date
                when new releases are made.</p>

            <p>Click on the link you prefer:<br>
                <a href="jpo-stable.jnlp">JPO Stable</a><br>
                <a href="jpo-devel.jnlp">JPO Development</a><br></p>

            <p><strong>Note:</strong> You will see the following warning screen:</p>

            <p><img src="jpo_scr_4.jpg" width="493" height="240" border="0"></p>

            <p>Please carefully consider the security warning.
                I think it appears because I am a hobbyist developer and not a large
                corporation with an information security department that has a budget
                to go out and buy security certificates.</p>

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


            <h2 id="requirements">Requirements</h2>

            <p>JPO is a pure Java application so it runs on any computer with a display that
                runs Java.</p>

            <p>You need <strong>a recent Java runtime installation</strong>. You must have Java 
                1.4.0 or later because Sun introduced cool fast graphics routines in this release 
                which are being used. Use the command <code>java -version</code> to find out 
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
            <h2 id="Options">Installation Options</h2>

            <p>You can run JPO with Java Web Start technology (which I recommend) or you
                can install the programs locally on your machine. If you want a local installation
                you can either use the packaged Microsoft Windows version (out of date at the time
                of writing) or download the jar files from this web site. You can also download the
                source code and compile your own version.</p>

            <p>The Java Web Start option is probably the simplest choice because you can run JPO directly
                from your web browser by clicking on the "Start now!" link at the top left of this page.
                Your browser then fire up the Java Web Start program which downloads all the pieces 
                from the Web and launches JPO. Each time you launch the application Java Web Start
                checks if I have released a new version and keeps your installation up to date 
                automatically.</p>

            <p>The Microsoft Windows package is a good choice for people who want to run JPO
                on this operating system. Anyone familiar with installing software on this OS
                will feel at home with the Installer interface. Unfortunately I have not released an
                updated package for quite a while. Due to the publicity which such a release attracts
                I feel a need to fix some more bugs and perform a very thorough test before
                the next release.</p>

            <p>Downloading the jars gives you full control about memory allocations and
                any tweaks you would like to make on your JVM.</p>

            <p>Downloading the source and compiling your own version gives you the ability to
                change the source code and do whatever you like (provided you stick to the 
                lenient restrictions of the GPL license).</p>

            <hr>
            <h2 id="local">Local Installation</h2>
            <p>First download the jar files and save them in your program directory:<br>
                <a href="http://j-po.sourceforge.net/jpo-0.10.jar">jpo-0.10.jar</a> or <a href="http://j-po.sourceforge.net/jpo-0.11.jar">jpo-0.11.jar</a><br>
                <a href="http://j-po.sourceforge.net/activation.jar">activation.jar</a><br>
                <a href="http://j-po.sourceforge.net/commons-compress-1.2.jar">commons-compress-1.2.jar</a><br>
                <a href="http://j-po.sourceforge.net/gdata-core-1.0.jar">gdata-core-1.0.jar</a><br>
                <a href="http://j-po.sourceforge.net/gdata-maps-2.0.jar">gdata-maps-2.0.jar</a><br>
                <a href="http://j-po.sourceforge.net/gdata-media-1.0.jar">gdata-media-1.0.jar</a><br>
                <a href="http://j-po.sourceforge.net/gdata-photos-2.0.jar">gdata-photos-2.0.jar</a><br>
                <a href="http://j-po.sourceforge.net/guava-13.0.jar">guava-13.0.jar</a><br>
                <a href="http://j-po.sourceforge.net/jnlp.jar">jnlp.jar</a><br>
                <a href="http://j-po.sourceforge.net/jwizz-0.1.4.jar">jwizz-0.1.4.jar</a><br>
                <a href="http://j-po.sourceforge.net/mail.jar">mail.jar</a><br>
                <a href="http://j-po.sourceforge.net/metadata-extractor-2.6.4.jar">metadata-extractor-2.6.4.jar</a><br>
                <a href="http://j-po.sourceforge.net/miglayout-4.0.jar">miglayout-4.0.jar</a><br>
                <a href="http://j-po.sourceforge.net/xmpcore.jar">xmpcore.jar</a></p>

            <p><strong>Note:</strong> Make sure they retain the <code>.jar</code> file extension. I've seen
                one XP box save the files as a <code>.zip</code>. With such a wrong extension it will not work!


            <p> Then you need to create a script or batch file to run everything. On Linux the script would look like this:</p>
            <p><font color="darkRed"><code>/PATH/TO/YOUR/JAVA/bin/java -Xms80M -Xmx2000M -classpath 
                    /PATH/TO/YOUR/JPO/JARS/activation.jar:/PATH/TO/YOUR/JPO/JARS/commons-compress-1.2.jar:/PATH/TO/YOUR/JPO/JARS/gdata-core-1.0.jar:/PATH/TO/YOUR/JPO/JARS/gdata-maps-2.0.jar:/PATH/TO/YOUR/JPO/JARS/gdata-media-1.0.jar:/PATH/TO/YOUR/JPO/JARS/gdata-photos-2.0.jar:/PATH/TO/YOUR/JPO/JARS/guava-13.0.jar:/PATH/TO/YOUR/JPO/JARS/gdata-core-1.0.jar:/PATH/TO/YOUR/JPO/JARS/jnlp.jar:/PATH/TO/YOUR/JPO/JARS/jwizz-0.1.4.jar:/PATH/TO/YOUR/JPO/JARS/mail.jar:/PATH/TO/YOUR/JPO/JARS/metadata-extractor-2.6.4.jar:/PATH/TO/YOUR/JPO/JARS/miglayout-4.0.jar:/PATH/TO/YOUR/JPO/JARS/xmpcore.jar:/PATH/TO/YOUR/JPO/JARS/jpo-0.11.jar Main</code></font></p>

            <p><strong>Note:</strong> Put everything on one long line. The space characters do
                matter; don't put spaces between the jars separated by colons(:) in the classpath!</p>

            <p><strong>Note:</strong> On <font color="red">Windows</font> machines the classpath must be
                separated by semicolons (;) on Linux machines by colons (:)</p>


            <p>On a particular Windows XP machine I installed Jpo into c:\Program Files\Jpo. The resulting Batch
                file looks like this: (you can download it here: <a href="Jpo.bat">Jpo.bat</a></p>

            <p><code>c:\windows\system32\java -Xms80M -Xmx2000M -classpath "c:\Program Files\Jpo\activation.jar";"c:\Program Files\Jpo\commons-compress-1.2.jar";"c:\Program Files\Jpo\gdata-core-1.0.jar";"c:\Program Files\Jpo\gdata-maps-2.0.jar";"c:\Program Files\Jpo\gdata-media-1.0.jar";"c:\Program Files\Jpo\gdata-photos-2.0.jar";"c:\Program Files\Jpo\guava-13.0.jar";"c:\Program Files\Jpo\jnlp.jar";"c:\Program Files\Jpo\jwizz-0.1.4.jar";"c:\Program Files\Jpo\mail.jar";"c:\Program Files\Jpo\metadata-extractor-2.6.4.jar";"c:\Program Files\Jpo\miglayout-4.0.jar";"c:\Program Files\Jpo\xmpcore.jar";"c:\Program Files\Jpo\jpo-0.11.jar" Main                    </code></p>


            <p>Actually I think I have managed to improve on this on 5.2.2007: I learned that I can
                set a classpath in the manifest of the main jar. If all the jars are in the same directory
                all that is needed to run the program is then a &quot;simple&quot; command like this:</p>

            <p><font color="darkRed"><code>/PATH/TO/YOUR/JAVA/bin/java -Xms80M -Xmx2000M
                    -jar /PATH/TO/YOUR/JPO/JARS/jpo-0.11.jar </code></font></p>

            <hr>
            <h2 id="problems">Potential problems:</h2>

            <h2>Memory settings:</h2>
            <p>If you get <font color="red">out of Memory</font> errors then you are probably best off
                using the local jar way of starting this application as you can then set the initial memory heap with 
                the -Xms and the maximum memory heap with the -Xmx parameters. Of course this can also be done on 
                the web start version but you would have to set Jpo up on your own webserver so that you 
                could specify the memory settings in the .jnlp configuration file.</p>

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
            version for you. Currently I am compiling to Java version 1.6 on a Java 7 compiler.</p>

            <p>Please let me know of specific installation issues you have so that I may extend this section for the benefit of other users who might have the same issue.</p>



            <hr>
            <p>Last update to this page: 6 Apr 2013<br>
                Copyright 2003-2013 by Richard Eigenmann, Z&uuml;rich, Switzerland</p>
        </td>
    </tr>
</table>
</body>
</html>
