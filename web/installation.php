<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
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

                <h2 id="Requirements">Requirements</h2>

                <p>JPO is a pure Java application so it runs on any computer with a display that
			runs Java. (Note: I'm not sure what happens on a cell phone or pda but it hardly makes 
			sense in such an environment.)</p>

                <p>You need <strong>a recent Java runtime installation</strong>. You must have Java 1.4.0 or later because
			Sun introduced cool fast graphics routines in this release which are being used.
			Use the command <code>java -version</code> to find out what version you are running
			if you aren't sure.</p>


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
			change the source code and do whatever you like (under the lenient restrictions 
			of the GPL, of course).</p>




                <hr>
                <h2 id="javawebstart">Using Java Web Start</h2>

                <p>If everything is configured correctly all you do is click this <strong>
                        <a href="jpo-stable.jnlp">link</a></strong> and off you go.</p>
                <p>You can also start other versions of the program:<br>
                    <a href="jpo-stable.jnlp">JPO Stable</a><br>
                    <a href="jpo-devel.jnlp">JPO Development</a><br>


                <p><strong>Note:</strong> You will see the following warning screen:</p>

                <p><img src="jpo_scr_4.jpg" width="493" height="240" border="0"></p>

                <p>You get this warning message because I haven't bought myself
			a certificate with which to sign the packaged programs. JPO does need 
			access to your hard disk so you have to accept this "sandbox" warning or it 
			will not run.</p>


                <p><strong>Note:</strong> Please also note that on some XP machines and Mac OS machines
			The Java Web Start version is not working. A general exception is thrown with
			no helpful information. I am currently at a loss what is wrong as it used to work
			and I haven't changed this part of the code. It continues to work on all machines
			I have ready access to. If you have an idea what is wrong and how to fix it, please
			let me know.</p>


                <p><strong>Potential problem:</strong> Your browser needs to know that it should
			start Java Web Start when it downloads a <code>.jnlp</code> extension file (Mimetype 
                    <code>application/x-java-jnlp-file</code>). I have seen browsers that didn't know this.
			You can teach the browser the association or run it from hand.</p>
                <p>You can run Java Webstart Programs from outside the browser by starting the program and
			passing in the location of the jnlp file: <br><font color="darkRed">
                        <code>javaws http://j-po.sourceforge.net/jpo-stable.jnlp</code></font></p>

                <p>To do this you do need to know where the javaws program was installed. On
			Windows do a search for javaws.exe. On Linux you can do <br>
                    <code><font color="darkRed">
			find / -name javaws -print</font></code></p>


                <hr>
                <h2 id="windows">Windows Installer</h2>
                <p><b><font color="red"></font></b> For Windows users there is an outdated packaged Windows
			version available. Download it from the regular SourceForge download area:</p>
                <p><a href="http://sourceforge.net/project/showfiles.php?group_id=71359&package_id=70920">Windows Executable</a></p>



                <hr>
                <h2 id="local">Local Installation</h2>
                <p>First download the jar files and save them in your program directory:<br>
                    <a href="http://j-po.sourceforge.net/jpo-0.9.jar">jpo-0.9.jar</a><br>
                    <a href="http://j-po.sourceforge.net/metadata-extractor-2.3.0.jar">metadata-extractor-2.3.0.jar</a><br>
                    <a href="http://j-po.sourceforge.net/jnlp.jar">jnlp.jar</a><br>
                    <a href="http://j-po.sourceforge.net/activation.jar">activation.jar</a><br>
                    <a href="http://j-po.sourceforge.net/miglayout-3.7.1.jar">miglayout-3.7.1.jar</a><br>
                    <a href="http://j-po.sourceforge.net/mail.jar">mail.jar</a>.</p>

                <p><strong>Note:</strong> Make sure they retain the <code>.jar</code> file extension. I've seen
			one XP box save the files as a <code>.zip</code>. With such a wrong extension it will not work!


                <p> Then you need to create a script or batch file to run everything. On Linux the script would look like this:</p>
                <p><font color="darkRed"><code>/PATH/TO/YOUR/JAVA/bin/java -Xms80M -Xmx800M
			-classpath /PATH/TO/YOUR/JPO/JARS/jnlp.jar:
			/PATH/TO/YOUR/JPO/JARS/metadata-extractor-2.3.0.jar:
			/PATH/TO/YOUR/JPO/JARS/mail.jar:
			/PATH/TO/YOUR/JPO/JARS/activation.jar:
                        /PATH/TO/YOUR/JPO/JARS/miglayout-3.7.1.jar:
			/PATH/TO/YOUR/JPO/JARS/jpo-0.9.jar Main</code></font></p>

                <p><strong>Note:</strong> Put everything on one long line. The space characters do
			matter; don't put spaces between the jars separated by colons(:) in the classpath! In the example 
			this was done to make the web page work.</p>

                <p><strong>Note:</strong> On <font color="red">Windows</font> machines the classpath must be
			separated by semicolons (;) on Linux machines by colons (:)</p>


                <p>On a particular Windows XP machine I installed Jpo into c:\Program Files\Jpo. The resulting Batch
			file looks like this: (you can download it here: <a href="Jpo.bat">Jpo.bat</a></p>

                <p><code>
			c:\windows\system32\java -Xms80M -Xmx800M -classpath
			"c:\Program Files\Jpo\jnlp.jar";
			"c:\Program Files\Jpo\metadata-extractor-2.3.0.jar";
			"c:\Program Files\Jpo\mail.jar";
			"c:\Program Files\Jpo\activation.jar";
			"c:\Program Files\Jpo\miglayout-3.7.1.jar";
			"c:\Program Files\Jpo\jpo-0.9.jar" Main
                    </code></p>


                <p>Actually I think I have managed to improve on this on 5.2.2007: I learned that I can
			set a classpath in the manifest of the main jar. If all the jars are in the same directory
			all that is needed to run the program is then a &quot;simple&quot; command like this:</p>

                <p><font color="darkRed"><code>/PATH/TO/YOUR/JAVA/bin/java -Xms80M -Xmx800M
			-jar /PATH/TO/YOUR/JPO/JARS/jpo-0.9.jar </code></font></p>

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
			On my SuSE 10 system I had to create a /usr/java/jdk1.5.0_07/jre/lib/fontconfig.SuSE.properties 
			file. Click
                    <a href="fontconfig">here</a> to download my version.</p>


                <h2>Helpful Links</h2>
                <p>Sun's Web start FAQ: <a href="http://java.sun.com/products/javawebstart/faq.html">http://java.sun.com/products/javawebstart/faq.html</a><br>
		        Sun's Java Web start download page: <a href="http://java.sun.com/products/javawebstart/download.html">http://java.sun.com/products/javawebstart/download.html</a><br>
		        Unofficial Java Web Start/JNLP FAQ: <a href="http://www.vamphq.com/jwsfaq.html">http://www.vamphq.com/jwsfaq.html</a></p>

                <p>Please let me know of specific installation issues you have so that I may extend this section for the benefit of other users who might have the same issue.</p>



                <hr>
                <p>Last update to this page: 6.6.2010<br>
			Copyright 2003-2010 by Richard Eigenmann, Z&uuml;rich, Switzerland</p>
            </td>
        </tr>
    </table>
</body>
</html>
