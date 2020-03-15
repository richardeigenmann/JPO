<?php include("page-start.php"); ?>
<div class="container">
    <h2>Developing JPO on Windows in a Virtual Machine</h2>

    <p>If you are mainly on Linux or if you want a clean development machine you can download a temporary Windows 10 Virtual Machine image from Microsoft here: <a href=2https://developer.microsoft.com/en-us/windows/downloads/virtual-machines/">https://developer.microsoft.com/en-us/windows/downloads/virtual-machines/</a></p>

    <p>Give the VM plenty of Ram and CPU cores.</p>

<p>Download and install Git from: <a href="https://git-scm.com/download">https://git-scm.com/download</a></p>

<p>Download and install IntelliJ IDEA Community Edition: <a href="https://developer.microsoft.com/en-us/windows/downloads/virtual-machines/">https://developer.microsoft.com/en-us/windows/downloads/virtual-machines/</a></p>

<p>Download and install OpenJDK 14: <a href="http://jdk.java.net/14/">http://jdk.java.net/14/</a> Open the zip and extract the files to c:\Program Files\</p>

<p>Start up Intellij IDEA. Leave all the defaults as-is.</p>

    <p>You land on a "Welcome to IntelliJ IDEA" screen. Click on "Get from Version Control"</p>

    <p>Enter the URL <code>https://github.com/richardeigenmann/JPO.git</code> and click Clone</p>

    <p>A red exclamation mark will show up on "Event" becuase git is not found. Click on "Configure...". In the field "Path to Git executable" type "C:\Program Files\Git\bin\git.exe"</p>

<p>Do the cloning again.</p>

    <p>Confirm "You have checked out and IntelliJ IDEA project file... Open it."</p>
    <p>Note that the code was checked out to <code>c:\Users\User\IdeaProjects\JPO</code> if you didn't change anything.</p>

<p>Open Settings and Add SDK for JDK14. File > Project Structure > Project SDK > New > C:\Program Files\jdk-14</p>

    <p>The Gradle build system installs itself. Also you want to allow OpenJDK to have access to the Networks</p>

    <p>On the right margin you have a tab "Gradle" which gives you access to the tasks"</p>

    <p>Pick JPO > Tasks > build > build and it should download dependencies, 
    compile and run the tests.</p>

    <p>To run the project go to the Gradle Tasks Window click JPO > build > run.

    <hr>
    <p>Last update to this page: 15 Mar 2020<br>
        Copyright 2003-2020 by Richard Eigenmann, Z&uuml;rich, Switzerland</p>
</div>
<?php include("page-end.php"); ?>