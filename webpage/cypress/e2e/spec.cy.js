describe('Test the JPO Project homepage', () => {
  beforeEach(() => {
    cy.visit('http://localhost:4321')

    cy.contains('JPO Java Picture Organizer')
      .should('be.visible');
    cy.contains('a.nav-link.dropdown-toggle', 'Download')
      .should('be.visible')
  });

  it('Check the homepage', () => {
    // Could check the images but they are part of a carousel
  });


  it('Opens the Download > Windows 10 Page', () => {
    cy.contains('a.nav-link.dropdown-toggle', 'Download')
      .should('be.visible')
      .click();

    cy.contains('a.dropdown-item', 'Windows 10')
      .should('be.visible')
      .click();
    cy.url().should('include', '/download/');

    cy.contains('h2', 'Installing JPO on Windows')
      .should('be.visible');

    const imageAlts = [
      'Download JPO Java Picture Organizer for Windows',
      'Scary Warning',
    ];
    imageAlts.forEach((altText) => {
      cy.log(`Checking image with alt text: "${altText}"`);
      cy.get(`img[alt="${altText}"]`)
        .should('be.visible')
        .and(($img) => {
          expect($img[0].naturalWidth, `Image "${altText}" should have loaded`).to.be.greaterThan(0);
        });
    });
  });

  it('Opens the Download > Linux Page', () => {
    cy.contains('a.nav-link.dropdown-toggle', 'Download')
      .should('be.visible')
      .click();

    cy.contains('a.dropdown-item', 'Linux')
      .should('be.visible')
      .click();
    cy.url().should('include', '/downloadLinux/');

    cy.contains('h2', 'Installing JPO on Linux')
      .should('be.visible');

  });

  it('Opens the Download > Run Java directly Page', () => {
    cy.contains('a.nav-link.dropdown-toggle', 'Download')
      .should('be.visible')
      .click();

    cy.contains('a.dropdown-item', 'Run Java directly')
      .should('be.visible')
      .click();
    cy.url().should('include', '/runJavaDirectly/');

    cy.contains('h2', 'Java Local Installation')
      .should('be.visible');
  });

  it('Opens the Tutorials > Getting Started Page', () => {
    cy.contains('a.nav-link.dropdown-toggle', 'Tutorials')
      .should('be.visible')
      .click();

    cy.contains('a.dropdown-item', 'Getting Started')
      .should('be.visible')
      .click();
    cy.url().should('include', '/tutorial_gettingstarted/');

    cy.contains('h1', 'Create a new collection')
      .should('be.visible');

    const imageAlts = [
      'New Collection Welcome Screen',
      'What your Windows Desktop may look like',
      'File Add Pictures Menu',
      'tutorial_add_pictures_dialog',
      'Screenshot after the file add',
      'Screenshot of Thumbnail Popup and Rotate Right',
      'Screenshot after adding descriptions',
      'Screenshot of a thumbnail being dragged',
      'Screenshot of a sort',
      'Screenshot of a new group being added',
      'Screenshot of group descriptions being added',
      'Screenshot of sample collection',
      'Screenshot of File Save menu',
      'Screenshot of the save as dialog',
      'Autoload confirmation screen',
      'Open Recent in File menu'
    ];
    imageAlts.forEach((altText) => {
      cy.log(`Checking image with alt text: "${altText}"`);
      cy.get(`img[alt="${altText}"]`)
        .should('be.visible')
        .and(($img) => {
          expect($img[0].naturalWidth, `Image "${altText}" should have loaded`).to.be.greaterThan(0);
        });
    });

  });

  it('Opens the Tutorials > Download from Camera Page', () => {
    cy.contains('a.nav-link.dropdown-toggle', 'Tutorials')
      .should('be.visible')
      .click();

    cy.contains('a.dropdown-item', 'Download from Camera')
      .should('be.visible')
      .click();
    cy.url().should('include', '/tutorial_download_from_camera/');

    cy.contains('h1', 'Download images')
      .should('be.visible');
  });


  it('Opens the Tutorials > Order prints from a lab Page', () => {
    cy.contains('a.nav-link.dropdown-toggle', 'Tutorials')
      .should('be.visible')
      .click();

    cy.contains('a.dropdown-item', 'Order prints from a lab')
      .should('be.visible')
      .click();
    cy.url().should('include', '/tutorial_order_prints/');

    cy.contains('h1', 'Order prints from a lab')
      .should('be.visible');
  });

  it('Opens the Support Page', () => {
    cy.contains('a.nav-link', 'Support')
      .should('be.visible')
      .click();

    cy.contains('h1', 'Umlauts')
      .should('be.visible');

  });

  it('Opens the License Page', () => {
    cy.contains('a.nav-link', 'License')
      .should('be.visible')
      .click();

    cy.contains('h1', 'License')
      .should('be.visible');

  });

  it('Opens the Privacy Page', () => {
    cy.contains('a.nav-link', 'Privacy')
      .should('be.visible')
      .click();

    cy.contains('h1', 'Privacy')
      .should('be.visible');

  });


  it('Opens the About the Project Page', () => {
    cy.contains('a.nav-link', 'About the Project')
      .should('be.visible')
      .click();

    cy.contains('h2', 'About the Author')
      .should('be.visible');

  });

  it('Checks that there is a menu item for Code > GitHub', () => {
    cy.contains('a.nav-link.dropdown-toggle', 'Code')
      .should('be.visible')
      .click();

    cy.contains('a.dropdown-item', 'GitHub')
      .should('be.visible');
  });

  it('Checks that there is a menu item for Code > Javadoc', () => {
    cy.contains('a.nav-link.dropdown-toggle', 'Code')
      .should('be.visible')
      .click();

    cy.contains('a.dropdown-item', 'Javadoc')
      .should('be.visible');
  });

  it('Opens the Code > Set up Dev environment on Windows 10 Page', () => {
    cy.contains('a.nav-link.dropdown-toggle', 'Code')
      .should('be.visible')
      .click();

    cy.contains('a.dropdown-item', 'Set up Dev environment on Windows 10')
      .should('be.visible')
      .click();
    cy.url().should('include', '/windows/');

    cy.contains('h2', 'Developing JPO on Windows')
      .should('be.visible');
  });


  it('Opens the Code > Set up IntelliJ IDEA Dev environment Page', () => {
    cy.contains('a.nav-link.dropdown-toggle', 'Code')
      .should('be.visible')
      .click();

    cy.contains('a.dropdown-item', 'Set up IntelliJ IDEA Dev environment')
      .should('be.visible')
      .click();
    cy.url().should('include', '/intellij/');

    cy.contains('h2', 'IntelliJ IDEA')
      .should('be.visible');
  });

  it('Opens the Code > Command Line Development Page', () => {
    cy.contains('a.nav-link.dropdown-toggle', 'Code')
      .should('be.visible')
      .click();

    cy.contains('a.dropdown-item', 'Command Line Development')
      .should('be.visible')
      .click();
    cy.url().should('include', '/commandLineDev/');

    cy.contains('h2', 'Developing JPO')
      .should('be.visible');
  });

  it('Opens the Code > Eclipse Page', () => {
    cy.contains('a.nav-link.dropdown-toggle', 'Code')
      .should('be.visible')
      .click();

    cy.contains('a.dropdown-item', 'Set up Eclipse Dev environment')
      .should('be.visible')
      .click();
    cy.url().should('include', '/eclipse/');

    cy.contains('h2', 'Eclipse')
      .should('be.visible');
  });

  it('Opens the Code > Netbeans Page', () => {
    cy.contains('a.nav-link.dropdown-toggle', 'Code')
      .should('be.visible')
      .click();

    cy.contains('a.dropdown-item', 'Set up Netbeans Dev environment')
      .should('be.visible')
      .click();
    cy.url().should('include', '/netbeans/');

    cy.contains('h2', 'Netbeans')
      .should('be.visible');
  });

  it('Checks that there is a menu item for Code > Source Code Sourceforge.net', () => {
    cy.contains('a.nav-link.dropdown-toggle', 'Code')
      .should('be.visible')
      .click();

    cy.contains('a.dropdown-item', 'Source Code Sourceforge.net')
      .should('be.visible');
  });

  it('Checks that there is a menu item for Code > SourceForge project page', () => {
    cy.contains('a.nav-link.dropdown-toggle', 'Code')
      .should('be.visible')
      .click();

    cy.contains('a.dropdown-item', 'SourceForge project page')
      .should('be.visible');
  });


  it('Checks that there is a menu item for Code > Travis CI Build status', () => {
    cy.contains('a.nav-link.dropdown-toggle', 'Code')
      .should('be.visible')
      .click();

    cy.contains('a.dropdown-item', 'Travis CI Build status')
      .should('be.visible');
  });

  it('Checks that there is a menu item for Code > Sonarcloud Dashboard', () => {
    cy.contains('a.nav-link.dropdown-toggle', 'Code')
      .should('be.visible')
      .click();

    cy.contains('a.dropdown-item', 'Sonarcloud Dashboard')
      .should('be.visible');
  });


});