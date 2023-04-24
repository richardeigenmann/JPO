<?php include_once 'page-start.php'; ?>

<div class="container">
    <h1>Pictures with Umlauts don't load on Linux</h1>
    <p>Linux is perfectly happy to have Umlauts in the file name line Z&uuml;rich.jpg. Yet the Java JFileChooser
    refuses to see this as a valid filename. Not sure how to fix this. Update: I am experiencing issues with the
    Locale on my distro. Setting <code>export LANG=en</code> may help.</p>

    <p>Often you will need to tell it where the javaws program was installed. On
        Windows do a search for <code>javaws.exe</code>. On Linux you can <br>
        <code>find / -name javaws -print</code></p>

    <hr>
    <h1>Memory settings:</h1>
    <p>If you get <span style="color:red">out of Memory</span> errors then you are probably best off
        using the local jar way of starting this application as you can then set the initial memory heap wit
        the -Xms and the maximum memory heap with the -Xmx parameters. </p>
    <p>I have discovered the option -XX:+AggressiveHeap which sounds great for what JPO needs. Please let me know if you have issues with this.</p>

    <hr>
    <h1>Chinese Font</h1>
    <p>Franklin He has been kind enough to translate the User Interface to Traditional and Simplified
        Chinese. This looks really cool but poses some installation hassles if your Java system
        is not correctly configured.
        When I had a SuSE 10 system I had to create a /usr/java/jdk1.5.0_07/jre/lib/fontconfig.SuSE.properties
        file. Click <a href="fontconfig">here</a> to download my version.</p>


    <p>Please let me know of specific installation issues you have so that I may extend this section for the benefit of other users who might have the same issue.</p>



    <hr>
    <p>Last update to this page: 25 April 2023<br>
        Copyright 2003-2023 by Richard Eigenmann, Z&uuml;rich, Switzerland</p>
</div>
<?php include_once 'page-end.php'; ?>
