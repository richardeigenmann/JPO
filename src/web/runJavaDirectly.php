<?php include 'page-start.php'; ?>
<div class="container">
    <h1>Java Local Installation</h1>
    <p>First download the jar file and save it in your program directory:<br>
        <a href="https://sourceforge.net/projects/j-po/files/Jpo-all.jar/download">Jpo-all.jar</a><br>
    </p>
    <p><strong>Note:</strong> Make sure the file retains the <code>.jar</code> file extension.

    <p> Then you need to create a script or batch file to run everything. On Linux the script would look like this:</p>
    <p><code>/PATH/TO/YOUR/JAVA/bin/java --enable-preview -jar /PATH/TO/YOUR/JPO/JAR/Jpo-all.jar</code></p>

    <p><strong>Note:</strong> Put everything on one long line.</p>

    <p>On a particular Windows machine I installed JPO into c:\Program Files\Jpo. The resulting
        <code>JPO.bat</code> file looks like this: (you can download it here: <a href="Jpo.bat">Jpo.bat</a></p> )

    <p><code>c:\windows\system32\java --enable preview -jar "c:\Program Files\Jpo\Jpo-all.jar"</code></p>

    <hr>
    <p>Last update to this page: 21 Feb 2021<br>
        Copyright 2003-2022 by Richard Eigenmann, Z&uuml;rich, Switzerland</p>
</div>
<?php include 'page-end.php'; ?>