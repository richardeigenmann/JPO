<?php include("page-start.php"); ?>

<div class="container">
    <div id="myCarousel" class="carousel slide" data-bs-ride="carousel">
        <!-- Indicators -->
        <div class="carousel-indicators">
            <button type="button" data-bs-target="#myCarousel" data-bs-slide-to="0" class="active" aria-label="Organize your Pictures"></button>
            <button type="button" data-bs-target="#myCarousel" data-bs-slide-to="1" aria-label="Present your Pictures"></button>
            <button type="button" data-bs-target="#myCarousel" data-bs-slide-to="2" aria-label="Free Open Source Software"></button>
            <button type="button" data-bs-target="#myCarousel" data-bs-slide-to="3" aria-label="Cross Platform because of Java"></button>
        </div>

        <!-- Wrapper for slides -->
        <div class="carousel-inner">
            <div class="carousel-item active">
                <img src="jpo_scr_1.png" class="d-block" width=697 height=510 alt="Screenshot of JPO">
                <div class="carousel-caption bg-dark">
                    Organize your Pictures
                </div>
            </div>

            <div class="carousel-item">
                <img src="jpo_scr_2.jpg" class="d-block" width=659 height=505 alt="Screenshot of JPO">
                <div class="carousel-caption bg-dark">
                    Present your Pictures
                </div>
            </div>

            <div class="carousel-item">
                <img src="jpo_scr_6_foss.png" class="d-block" width=700 height=580 alt="JPO is Free Open Source Software">
                <div class="carousel-caption bg-dark">
                    Free Open Source Software
                </div>
            </div>

            <div class="carousel-item">
                <img src="jpo_scr_3.png" class="d-block" width=700 height=580 alt="Screenshot of JPO">
                <div class="carousel-caption bg-dark">
                    Cross Platform because of Java
                </div>
            </div>

        </div>

        <!-- Left and right controls -->
          <button class="carousel-control-prev" type="button" data-bs-target="#myCarousel"  data-bs-slide="prev">
            <span class="carousel-control-prev-icon" aria-hidden="true"></span>
            <span class="visually-hidden">Previous</span>
          </button>
          <button class="carousel-control-next" type="button" data-bs-target="#myCarousel"  data-bs-slide="next">
            <span class="carousel-control-next-icon" aria-hidden="true"></span>
            <span class="visually-hidden">Next</span>
          </button>
    </div>


    <p>JPO is a program that helps you organise your digital pictures by putting them in
        collections. There you can browse the pictures, skip through the thumbnails, share
        them by email or generate a website. A powerful picture viewer allows you to
        see the pictures full screen with simple Zoom-in and Zoom-out with the left and 
        right mouse buttons.</p>

    <p>A fundamental design principle is that JPO doesn't mess with your pictures. They 
        stay unchanged on your disk unless you ask JPO to move them somewhere or to delete them.</p>

    <p>JPO is not a photo editing application. There are many excellent packages out there
        with which you can touch up your pictures. You can make JPO open such a program 
        for you.</p>

    <p>Richard Eigenmann from Z&uuml;rich has spent the last 21 years building and improving
        JPO as an OpenSource project. He hopes you will find it useful and enjoys feedback.</p>

    <hr>
    <h3>Features</h3>
    <ul>
        <li> Quickly Organize digital images into collections and groups </li>
        <li> Creates web pages from your collection </li>
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
    <h3>Reviews</h3>
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
        collection information. Picture groups can be exported to a directory for backups
        and sharing by other means. JPO supports a large amount of picture formats.
     </p>

    <hr>
    <h3>Do you like JPO?</h3>
    <p>Why don't you let the author know? Send him an encouraging email at <a href="mailto:richard.eigenmann@gmail.com">richard.eigenmann@gmail.com</a></p>
    <hr>

    <p>Last update to this page: 13 Feb 2021<br>
        Copyright 2003-2021 by Richard Eigenmann, Z&uuml;rich, Switzerland</p>
</div>
<?php include("page-end.php"); ?>
