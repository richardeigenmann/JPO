<?php include("page-start.php"); ?>

<!--<img src="jpo_scr_3.png" width=700 height=580 alt="Screenshot of JPO"><br>-->

<div class="container">
    <div id="myCarousel" class="carousel slide" data-ride="carousel">
        <!-- Indicators -->
        <ol class="carousel-indicators">
            <li data-target="#myCarousel" data-slide-to="0" class="active"></li>
            <li data-target="#myCarousel" data-slide-to="1"></li>
            <li data-target="#myCarousel" data-slide-to="2"></li>
            <li data-target="#myCarousel" data-slide-to="3"></li>
            <li data-target="#myCarousel" data-slide-to="4"></li>
            <li data-target="#myCarousel" data-slide-to="5"></li>
            <li data-target="#myCarousel" data-slide-to="6"></li>
        </ol>

        <!-- Wrapper for slides -->
        <div class="carousel-inner" role="listbox">
            <div class="item active">
                <img src="jpo_scr_3.png" width=700 height=580 alt="Screenshot of JPO">
                <div class="carousel-caption">
                    <h3>Organize your Pictures</h3>
                </div>
            </div>

            <div class="item">
                <img src="jpo_scr_3.png" width=700 height=580 alt="Screenshot of JPO">
                <div class="carousel-caption">
                    <h3>Present your Pictures</h3>
                </div>
            </div>

            <div class="item">
                <img src="jpo_scr_6_foss.png" width=700 height=580 alt="JPO is Free Open Source Software">
                <div class="carousel-caption">
                    <h3>Free Open Source Software</h3>
                    <p><a href="about.php#License">GPL License</a></p>
                </div>
            </div>

            <div class="item">
                <img src="jpo_scr_3.png" width=700 height=580 alt="Screenshot of JPO">
                <div class="carousel-caption">
                    <h3>Share your Pictures</h3>
                    <p>Use Email, Generate a Website or upload them to the Google Photos Cloud</p>
                </div>
            </div>

            <div class="item">
                <img src="jpo_scr_3.png" width=700 height=580 alt="Screenshot of JPO">
                <div class="carousel-caption">
                    <h3>Cross Platform</h3>
                    <p>Runs on any Java enabled computer</p>
                </div>
            </div>

            <div class="item">
                <img src="jpo_scr_3.png" width=700 height=580 alt="Screenshot of JPO">
                <div class="carousel-caption">
                    <h3>See where the Picture was taken</h3>
                    <p>Inspect Exif Data and view on Map</p>
                </div>
            </div>

            <div class="item">
                <img src="jpo_scr_3.png" width=700 height=580 alt="Screenshot of JPO">
                <div class="carousel-caption">
                    <h3>Non destructive</h3>
                    <p>Never changes your image files</p>
                </div>
            </div>

        </div>

        <!-- Left and right controls -->
        <a class="left carousel-control" href="#myCarousel" role="button" data-slide="prev">
            <span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
            <span class="sr-only">Previous</span>
        </a>
        <a class="right carousel-control" href="#myCarousel" role="button" data-slide="next">
            <span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
            <span class="sr-only">Next</span>
        </a>
    </div>




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

    <p>Richard Eigenmann from Z&uuml;rich has spent the last 18 years building and improving
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
        software so you can go and read the lines of source code and compile
        your own version! Check out the linked Code analysis tools and their reports to see
        how JPO is doing in terms of code quality, test coverage and other developer metrics.</p>
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
    <span class="para-heading" id="Like">Do you like JPO?</span>
    <p>Why don't you let the author know? Send him an encouraging email at <a href="mailto:richard.eigenmann@gmail.com">richard.eigenmann@gmail.com</a></p>
    <hr>

    <p>Last update to this page: 3 Dec 2018<br>
        Copyright 2003-2018 by Richard Eigenmann, Z&uuml;rich, Switzerland</p>
</div>
<?php include("page-end.php"); ?>
