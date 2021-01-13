<?php include("page-start.php"); ?>
<div class="container">
    <h3>System Requirements</h3>

    <h3>Installing on Windows</h3>

    <a href="https://sourceforge.net/projects/j-po/files/JPO-0.14.exe/download" rel="nofollow">
        <img alt="Download JPO Java Picture Organizer for Windows" src="https://a.fsdn.com/con/app/sf-download-button">
    </a>

    <a href="http://sourceforge.net/projects/j-po/files">SourceForge download area</a>

    <h3>Scary Warnings</h3>
    <img src="jpo_scr_7.jpg" width=400 height=319 alt="Scary Warning"><br>
    <p>Sorry, I have not yet figured out how to digitally sign my packages so Windows insists on splashing up
    a scary warning. If you know how I can improve this user experience, please let me know.</p>

    <p>I can assure you that JPO doesn't spy on you, doesn't send spam and doesn't try to sell you 
        anything. Better than that, it's open source software so you or someone you trust can go and
        analyse the lines of source code and compile your own version! Check out the linked code analysis tool reports to see
        how JPO is doing in terms of code quality, test coverage and other developer metrics.</p>

    <hr>
    <h3>Local Installation</h3>
    <p>First download the jar file and save it in your program directory:<br>
        <a href="http://j-po.sourceforge.net/Jpo-all.jar">Jpo-all.jar</a><br>
    </p>
    <p><strong>Note:</strong> Make sure they retain the <code>.jar</code> file extension.

    <p> Then you need to create a script or batch file to run everything. On Linux the script would look like this:</p>
    <p><code>/PATH/TO/YOUR/JAVA/bin/java -XX:+AggressiveHeap --enable-preview -jar /PATH/TO/YOUR/JPO/JAR/Jpo-all.jar</code></p>

    <p><strong>Note:</strong> Put everything on one long line.</p>

    <p>On a particular Windows machine I installed JPO into c:\Program Files\Jpo. The resulting 
        <code>JPO.bat</code> file looks like this: (you can download it here: <a href="Jpo.bat">Jpo.bat</a></p>

    <p><code>c:\windows\system32\java -XX:+AggressiveHeap --enable preview -jar "c:\Program Files\Jpo\Jpo-all.jar"</code></p>

    <hr>
    <p>Last update to this page: 10 Jan 2021<br>
        Copyright 2003-2021 by Richard Eigenmann, Z&uuml;rich, Switzerland</p>
</div>
<?php include("page-end.php"); ?>
