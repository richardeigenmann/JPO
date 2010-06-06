<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <title>JPO Homepage</title>
        <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    </head>
    <body>
        <table CELLSPACING=0 CELLPADDING=10>
            <tr>
                <th colspan="2" height="60" bgcolor="#97a4da"><h1>JPO  Java Picture Organizer</h1></th>
            </tr>
            <tr>
                <td width="150" bgcolor="#97a4da" valign="top">
                        <?php include("nav.html"); ?>                </td>
                <td>
                    <h2 id="tutorials">Tutorials</h2>
                    <table>
                        <tr>
                            <td>
                                <a href="tutorial_gettingstarted.html">Tutorial 1</a>
                            </td>
                            <td>
                                Your first use of the Application: Create a new collection, add pictures and organise them.
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <a href="tutorial_download_from_camera.html">Tutorial 2</a>
                            </td>
                            <td>
                                Download images from your digital camera.
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <a href="tutorial_order_prints.html">Tutorial 3</a>
                            </td>
                            <td>
                                Order prints from a lab.
                            </td>
                        </tr>
                        <tr>
                            <td>
                                Tutorial 4
                            </td>
                            <td>
                                Reorganise the pictures on your hard disk. Consolidate function.
                            </td>
                        </tr>
                        <tr>
                            <td>
                                Tutorial 5
                            </td>
                            <td>
                                Set up a development environment and compile a modified version of JPO.
                            </td>
                        </tr>

                    </table>

                    <hr>
                    <h2>Browsing your picture collection</h2>
                    <p>In the diagram with the yellow folders on the top left of the JPO window you may select which folder of pictures you would like to look at. Simply click on the group and the pictures of that group show on the right hand side. JPO will show the thumbnails side by side if there is enough space. Use the scrollbar on the right of the window to scroll down to further pictures in your collection.</p>

                    <p>Notice that the navigation panel with the little yellow folders "unfolds" to show you the contents of a group.</p>

                    <p>If you would like to see all thumbnails at once you can reduce the size at which they are shown with the slider next to the title of the group being shown. If you drag the slider to the left the pictures will shrink and more pictures can be shown in the window underneath.</p>

                    <p>To view a picture in full resolution double click on an image. You could also right-click and choose "Show Picture" from the pop-up menu.</p>

                    <p>To move to the next picture in the group use the arrow buttons at the bottom of the page. These are the silvery arrows. If you hove your mouse over the icon it will say "Next Picture". If the picture is on the side, use the blue "Rotate left" or "Rotate right" buttons to turn the picture by 90 degrees. This does not alter the image on disk; JPO just rotates the image in memory and remembers to rotate it every time you look at it going forward.</p>

                    <h2>Reorganising your pictures</h2>
                    <h3>Using the "Move" popup menu</h3>
                    <p>Often when you are reorganising pictures you have multiple groups your pictures will get sorted to. Perhaps you are weeding out a long day's shooting session and have created three folders: "Amazing Highlights", "Not so sure" and "Utter Rubbish". After you have dragged a picture to a different folder the move menu remembers the target folder. This makes it quite easy to move another picture to that folder. Point at the picture with the mouse, right-click on the image and pick the target folder off the move menu. This pop-up menu is also available in the full screen picutre viewer.</p>

                    <h2>How to create a web-page from your pictures</h2>
                    <p>If you have a web-server somewhere to host your pictures you can use JPO to create all the files that need to go on that web-server for you. Start off by creating a group, sort the pictures into the desired sequence and label them carefully.</p>
                    <p>Right-click on the yellow group icon and choose the "Export to HTML" option. Into the Target Directory field you need to specify a (preferably empty) directory where the web site files should be created. You can tweak the size of the images should be given. Consider carfully whether you want to export the high resolution pictures and whether to generate the zipfile for download of highres pictures. This can result in some pretty enormous data files which can take ages to upload to the web-server.</p>
                    <p>When JPO have finished creating the web-site you can preview the site by going to the target directory and opening the index.htm file. You see the page as it would be show to someone viewing it from the web. If the page is in order you then need to follow the instructions you were given to upload the files to the webserver you are using.</p>



                    <hr>
                    <p>Last update to this page: 11.11.2007<br>
			Copyright 2007 by Richard Eigenmann, Z&uuml;rich, Switzerland</p>
                </td>
            </tr>
        </table>
    </body>
</html>
