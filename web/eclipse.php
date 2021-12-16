<?php include 'page-start.php'; ?>
<div class="container">
    <h2>Developing JPO with Eclipse</h2>

    <p>Ensure you have Eclipse set up:</p>
    <ul>
        <li>Have you downloaded a JDK? <a href="https://www.oracle.com/technetwork/java/javase/downloads/index.html">Oracle Java SDK</a></li>
        <li>Have you installed Eclipse and does Eclipse start up? <a href="https://www.eclipse.org">https://www.eclipse.org</a></li>
    </ul>

    <p>Close the Welcome window if it is showing and switch to the Java perspective.</p>

    <p>Click File > Import > Git > Projects from Git</p>

    <p>Click Next > Clone URI > Enter the URI <code>https://github.com/richardeigenmann/JPO.git</code> and click Next 
    then pick Branch "master" then Next and specify the directory to use > Next > (wait) 
    >  Import as a general project > Next > Finish</p>

    <p>In the Package Explorer right click on JPO > Configure > Add Gradle Nature > (wait) > (deal with Windows Defender alert...)</p>

    <p>Window > Show View > Other > Gradle > Gradle Tasks > Open and a Gradle Tasks tab will open in the bottom window</p>

    <p>In the Gradle Tasks Window click JPO > build > build and it should download dependencies, 
    compile and run the tests. See the output in the "Console" tab.</p>

    <p>To run the project go to the Gradle Tasks Window click JPO > build > run.</p>

    <hr>
    <p>Last update to this page: 8 Dec 2018<br>
        Copyright 2003-2018 by Richard Eigenmann, Z&uuml;rich, Switzerland</p>
</div>
<?php include 'page-end.php'; ?>