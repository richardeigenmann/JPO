<?php include("page-start.php"); ?>
<div class="container">
    <h1>Installing JPO on Windows</h1>

    <p>Click on the green download button and wait for the page to sort out the advertising and start the download.</p>

    <a href="https://sourceforge.net/projects/j-po/files/JPO-0.14.exe/download" rel="nofollow">
        <img alt="Download JPO Java Picture Organizer for Windows" src="https://a.fsdn.com/con/app/sf-download-button">
    </a>

    <p> Alternatively, visit the <a href="http://sourceforge.net/projects/j-po/files">SourceForge download area</a></p>

   <p>Then install the downloaded file and you will find a JPO icon in your Start Menu and on your Desktop. JPO installs
   istelf into c:\Program Files\JPO</p>

    <h2>Scary Warnings</h2>
    <img src="jpo_scr_7.jpg" width=400 height=319 alt="Scary Warning"><br>
    <p>Sorry, I have not yet figured out how to digitally sign my packages so Windows insists on splashing up
    a scary warning. If you know how I can improve this user experience, please let me know.</p>

    <p>I can assure you that JPO doesn't spy on you, doesn't send spam and doesn't try to sell you 
        anything. Better than that, it's open source software so you or someone you trust can go and
        analyse the lines of source code and compile your own version! Check out the linked code analysis tool reports to see
        how JPO is doing in terms of code quality, test coverage and other developer metrics.</p>

    <hr>
    <h1>Java Local Installation</h1>
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
    <p>Last update to this page: 14 Jan 2021<br>
        Copyright 2003-2021 by Richard Eigenmann, Z&uuml;rich, Switzerland</p>
</div>
<?php include("page-end.php"); ?>
