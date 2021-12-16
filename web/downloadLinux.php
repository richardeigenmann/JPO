<?php include 'page-start.php'; ?>
<div class="container">
    <h1>Installing JPO on Linux</h1>

    <p>You can run JPO with Flatpak. First you need to install Flatpak for your distro.</p>
    <p>Next you install JPO with flatpack:</p>
    <pre>
flatpak --user install https://j-po.sourceforge.io/io.sourceforge.j-po.flatpakref

# to list your flatpak programs
flatpak list

# to uninstall
flatpak uninstall io.sourceforge.j-po
    </pre>

    <p>JPO should now show up in your application menu and possibly your desktop with its icon.
    On Ubuntu 18.0 and Debian It showed up only after a reboot. On OpenSuse it showed up right away.
    If you are impatient you can run it from the command line:
    </p>
    <pre>
flatpak run io.sourceforge.j-po
    </pre>

    <hr>
    <p>Last update to this page: 21 Feb 2021<br>
        Copyright 2003-2021 by Richard Eigenmann, Z&uuml;rich, Switzerland</p>
</div>
<?php include 'page-end.php'; ?>
