<?php include 'page-start.php'; ?>
<div class="container">
    <h2>Developing JPO with IntelliJ IDEA</h2>

    <p>Ensure you have IntelliJ IDEA set up:</p>
    <ul>
        <li>Have you downloaded a JDK? <a href="https://www.oracle.com/technetwork/java/javase/downloads/index.html">Oracle Java SDK</a></li>
        <li>Have you installed IntelliJ IDEA does it start up? <a href="https://www.jetbrains.com/idea/">https://www.jetbrains.com/idea/</a></li>
    </ul>

    <p>On the Welcome screen click on Check out from Version Control</p>

    <p>Enter the URL <code>https://github.com/richardeigenmann/JPO.git</code> and click Clone</p>

    <p>Confirm "You have checked out and IntelliJ IDEA project file: .../build.gradle"</p>

    <p>On the right margin you have a tab "Gradle" which gives you access to the tasks"</p>

    <p>Pick JPO > Tasks > build > build and it should download dependencies, 
    compile and run the tests.</p>

    <p>To run the project go to the Gradle Tasks Window click JPO > build > run.

    <hr>
    <p>Last update to this page: 8 Dec 2018<br>
        Copyright 2003-2020 by Richard Eigenmann, Z&uuml;rich, Switzerland</p>
</div>
<?php include 'page-end.php'; ?>