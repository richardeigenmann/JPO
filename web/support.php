<?php include("page-start.php"); ?>

<div class="container">
    <h3>Pictures with Umlauts don't load on Linux</h3>
    <p>Linux is perfectly happy to have Umlauts in the file name line Z&uuml;rich.jpg. Yet The Java JFileChooser
    refuses to see this as a valid filename. Not sure how to fix this.</p>

    <h3>Java Web Start doesn't launch</h3>
    <p>Your browser may not know that it should start "Java Web Start" when it downloads a
        <code>.jnlp</code> extension file (Mimetype  <code>application/x-java-jnlp-file</code>).
        If your browser doesn't have the right association you can run it from the command line or 
        by creating a desktop icon. The command looks like this:</p>
    <p><code>javaws http://j-po.sourceforge.net/jpo-stable.jnlp</code></p>

    <p>Often you will need to tell it where the javaws program was installed. On
        Windows do a search for <code>javaws.exe</code>. On Linux you can <br>
        <code>find / -name javaws -print</code></p>

    <hr>
    <h3>Memory settings:</h3>
    <p>If you get <font color="red">out of Memory</font> errors then you are probably best off
        using the local jar way of starting this application as you can then set the initial memory heap with 
        the -Xms and the maximum memory heap with the -Xmx parameters. Of course this can also be done on 
        the web start version but you would have to set Jpo up on your own webserver so that you 
        could specify the memory settings in the .jnlp configuration file.</p>
    <p>I have discovered the option -XX:+AggressiveHeap which sounds great for what JPO needs. Please let me know if you have issues with this.</p>

    <hr>
    <h3>Chinese Font</h3>
    <p>Franklin He has been kind enough to translate the User Interface to Traditional and Simplified
        Chinese. This looks really cool but poses some installation hassles if your Java system 
        is not correctly configured.
        When I had a SuSE 10 system I had to create a /usr/java/jdk1.5.0_07/jre/lib/fontconfig.SuSE.properties 
        file. Click <a href="fontconfig">here</a> to download my version.</p>


    <p>Please let me know of specific installation issues you have so that I may extend this section for the benefit of other users who might have the same issue.</p>



    <hr>
    <p>Last update to this page: 7 Dec 2018<br>
        Copyright 2003-2016 by Richard Eigenmann, Z&uuml;rich, Switzerland</p>
</div>
<?php include("page-end.php"); ?>
