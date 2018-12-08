<?php include("page-start.php"); ?>
<div class="container">
    <h2>Developing JPO with Eclipse</h2>

    <p>Ensure you have Eclipse set up:</p>
    <ul>
        <li>Have you downloaded a JDK?
        <li>Have you installed Eclipse and does Eclipse start up? <a href="https://www.eclipse.org">https://www.eclipse.org</a></li>
        <li>Have you installed The Git Repositories Plug-In?</li>
        <li>Have you got Gradle installed and running?</li>
        <li>Have you got the Buildship Gradle Integration Plug In? <
            <a href="https://marketplace.eclipse.org/content/buildship-gradle-integration#group-details">link</a></li>
    </ul>

    <p>Close the Welcome window if it is showing and switch to the Java perspective.</p>

    <p>Click File > Import > Git > Projects from Git</p>

    <p>Click Next > Clone URI > Enter the URI <code>https://github.com/richardeigenmann/JPO.git</code> and click Next 
    then pick Branch "master" then Next and specify the directory to use > Next > (wait) 
    >  Import as a general project > Next > Finish</p>

    <p>Expand JPO > Branches > Local > doubleclick on master</p>

    <p>File > Import > Gradle > Existing Gradle Project > Next > 
        Enter the Project root directory from your git checkout above > Finish</p>

    <p>In the Package Explorer right click on JPO > Configure > Add Gradle Nature</p>

    <p>Window > Show View > Gradle > Gradle Tasks and a Gradle Tasks tab will open in the bottom window</p>

    <p>In the Gradle Tasks Window click JPO > build > build and it should download dependencies, 
    compile and run the tests. See the output in the "Console" tab.

    <p>To run the project go to the Gradle Tasks Window click JPO > build > run.

    <hr>
    <p>Last update to this page: 8 Dec 2018<br>
        Copyright 2003-2018 by Richard Eigenmann, Z&uuml;rich, Switzerland</p>
</div>
<?php include("page-end.php"); ?>