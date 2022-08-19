<?php include 'page-start.php'; ?>
<div class="container">
    <h1>Installing JPO on Linux</h1>

    <h2>RPM based systems</h2>

    <pre>
# install the rpm
sudo rpm -Uhv https://sourceforge.net/projects/j-po/files/jpo-0.18-1.x86_64.rpm

# run the program from /usr/bin/JPO
JPO
</pre>


    <h2>Debian like systems</h2>

    <pre>
# install the deb
wget https://sourceforge.net/projects/j-po/files/jpo_0.18-1_amd64.deb
su -
dpkg -i jpo_0.18-1_amd64.deb

# run the program from /usr/bin/JPO
JPO
</pre>


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
        Copyright 2003-2022 by Richard Eigenmann, Z&uuml;rich, Switzerland</p>
</div>
<?php include 'page-end.php'; ?>
