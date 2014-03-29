<?php include("page-start.php"); ?>

<img src="jpo_scr_3.png" width=700 height=580 alt="Screenshot of JPO"><br>

<span class="para-heading" id="Introduction">Introduction</span><br>
<p>JPO is a program that helps you organise your digital pictures by putting them in
    collections. There you can browse the pictures, skip through the thumbnails, share
    them by email or upload to Google Picasa. A powerful picture viewer allows you to 
    see the pictures full screen with simple Zoom-in and Zoom-out with the left and 
    right mouse buttons.</p>

<p>A fundamental design principle is that JPO doesn't mess with your pictures. They 
    stay unchanged on your disk unless you ask JPO to move them somewhere or to delete them.</p>

<p>JPO is not a photo editing application. There are many excellent packages out there
    with which you can touch up your pictures. You can make JPO open such a program 
    for you.</p>

<p>Richard Eigenmann from Z&uuml;rich has spent the last 14 years building and improving
    JPO as an OpenSource project. He hopes you will find it useful and enjoys feedback.</p>

<hr>
<span class="para-heading" id="Features">Features</span>
<ul>
    <li> Quickly Organize digital images into collections and groups </li>
    <li> Creates web pages from your collection </li>
    <li> Can upload to Google's Picasa (TM) </li>
    <li> Download pictures from Camera with the ability to load only the new ones</li>
    <li> Send rescaled images and originals via email</li>
    <li> View pictures as a slide show </li>
    <li> Simple zoom-in and zoom-out with left / right mouse buttons</li>
    <li> Rotation on the fly without modifying the original image</li>
    <li> Browse image thumbnails </li>
    <li> Automatically advancing slide shows</li>
    <li> Captures metadata and has search features</li>
    <li> Displays EXIF and IPTC metadata</li>
    <li> Export to directory facility to share via e-mail or CD-ROM </li>
    <li> Open XML data structures </li>
    <li> Pure Java, no native libraries</li>
    <li> Runs on Windows, Linux and Mac OS, anywhere Java runs</li>
    <li> Can call up outside applications</li>
    <li> Leaves your pictures where they are</li>
    <li> Can move pictures to new locations to tidy up</li>
    <li> Doesn't modify your original pictures</li>
    <li> Open source license</li>
</ul>


<hr>
<span class="para-heading" id="scaryerror">What's that scary Java error?</span><br>
<img src="jpo_scr_5.png" width=658 height=591 alt="Java Scary Error"><br>
<p>The latest versions of Java seem to be defaulting to ultra paranoid security 
    settings. This might be a good thing but it can stop you from running JPO with Java 
    Web Start. JPO does need to read the pictures on your filesystem and does need to 
    write to your disk as that pretty much is the point of a software to organise your
    pictures. As such it can't run in the Java Sandbox and needs the "all" permission.
    I have self-signed the application that I upload to Sourceforge. Obviously that 
    is not a world-trusted key so you need to consider if you can trust me.</p>
<p>I can assure you that JPO doesn't "phone home", doesn't spy on you, doesn't send 
    spam and doesn't try to sell you anything. Better than that, it's open source 
    software so you can go and read the 53'534 lines of code (23.3.2014) and compile 
    your own version!</p>
<p>If you like the convenience of the pre-packaged Java Web Start bundles then you
    need to go to your Java installation (Start > Control Panel > Java) and reduce the
    security setting to something less paranoid and accept the risk.</p>


<hr>
<span class="para-heading" id="Screenshot">Screenshots</span>

<p>The Thumbnail Browser:</p>
<img src="jpo_scr_1.png" width=697 height=510 alt="Screenshot of JPO">
<p>The slide show window:</p>
<img src="jpo_scr_2.jpg" width=659 height=505 alt="Screenshot of JPO">



<hr>
<span class="para-heading" id="Reviews">Reviews</span>
<p>I was totally amazed when I discovered that people were writing reviews about
    JPO and positive reviews at that!</p>
<p>
    The Agfanet review is no longer online but an archived version can be read 
    <a href="http://web.archive.org/web/20040530180350/http://www.agfanet.com.isp.de/en/cafe/softreview/cont_softreview.php3?id=257&archive=yes">here.</a>
</p>

<hr>
<span class="para-heading" id="Techdes">Technical description</span>
<p>The Java Picture Organizer application is a platform independent image organisation tool
    that lets a user build collections of images that they can then search, browse,
    show and share with others. An HTML export facility is available that allows
    web pages to be built from collections. JPO uses an open XML file format to store
    collection information. Picture groups can be exported to a directory for CD burning
    and sharing by other means. JPO supports all picture formats of your Java installation.
    By default this is JPEG and GIF. The application makes use of Sun's Java Web Start (TM)
    technology for easy installation and upgrading.</p>

<hr>
<span class="para-heading" id="Like">Do you like JPO?</span>>
<p>Why don't you let the author know? Send him an encouraging email at <a href="mailto:richard.eigenmann@gmail.com">richard.eigenmann@gmail.com</a></p>
<hr>

<p>Last update to this page: 23 Mar 2014<br>
    Copyright 2003-2014 by Richard Eigenmann, Z&uuml;rich, Switzerland</p>
<?php include("page-end.php"); ?>
