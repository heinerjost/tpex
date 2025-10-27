package de.jostnet.tpex.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.jostnet.tpex.events.InfoEventData;
import de.jostnet.tpex.events.InfoEventListener;
import de.jostnet.tpex.events.InfoEventType;
import de.jostnet.tpex.services.ExportService;
import de.jostnet.tpex.services.MessageService;
import de.jostnet.tpex.services.UnzipService;

public class Gui extends JFrame implements InfoEventListener {
    private MessageService messageService;

    private ExportService exportService;
    private UnzipService unzipService;

    private JPanel mainPanel;

    private JLabel lbSprache;
    private JComboBox<String> cbSprachen;

    private JLabel lbZip;
    private JTextField tfZip;
    private JFileChooser chooserZip;
    private JButton btZip;

    private JLabel lbWork;
    private JTextField tfWork;
    private JFileChooser chooserWork;
    private JButton btWork;

    private JLabel lbExport;
    private JTextField tfExport;
    private JFileChooser chooserExport;
    private JButton btExport;

    private JButton btStartUnzip;
    private JButton btStartExport;

    private JPanel pnStatusExport;
    private JLabel lbStatusExportAnzahl;
    private JTextField tfStatusExportAnzahl;
    private JLabel lbStatusExportFolder;
    private JTextField tfStatusExportFolder;
    private JLabel lbStatusExportTime;
    private JTextField tfStatusExportTime;

    private JPanel pnStatusUnzip;
    private JLabel lbStatusUnzipZipCount;
    private JTextField tfStatusUnzipZipCount;
    private JLabel lbStatusUnzipCount;
    private JTextField tfStatusUnzipCount;
    private JLabel lbStatusUnzipSize;
    private JTextField tfStatusUnzipSize;
    private JLabel lbStatusUnzipTime;
    private JTextField tfStatusUnzipTime;

    private JLabel lbStatus;

    private Font font1;
    private Font font2;

    // Bezeichnungen für die Properties
    private static final String LANGUAGE = "language";
    private static final String ZIPFOLDER = "zipfolder";
    private static final String WORKFOLDER = "workfolder";
    private static final String EXPORTFOLDER = "exportfolder";

    public Gui(MessageService messageService, UnzipService unzipService, ExportService exportService) {
        super();
        this.messageService = messageService;
        this.exportService = exportService;
        this.exportService.registerListener(this);
        this.unzipService = unzipService;
        this.unzipService.registerListener(this);

        setTitle("tpex - Takeout Photo Exporter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(900, 550);
        loadFont();
        add(getHeader(), BorderLayout.NORTH);
        add(getMain(), BorderLayout.CENTER);
        add(getSidebar(), BorderLayout.EAST);
        add(getStatusbar(), BorderLayout.SOUTH);
        setApplicationIcon("/images/logo/logo");
        setLocationRelativeTo(null);
        setI18nText();

        setVisible(true);
    }

    private JPanel getMain() {

        mainPanel = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        layout.columnWidths = new int[] { 150, 300, 50 };
        mainPanel.setLayout(layout);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        gbc.gridwidth = 1; // Zurücksetzen auf eine Spalte
        gbc.gridy++;
        gbc.gridx = 0;
        lbSprache = new JLabel();
        lbSprache.setFont(font1);
        mainPanel.add(lbSprache, gbc);

        String[] sprachen = { "de", "en", "es" };

        cbSprachen = new JComboBox<>(sprachen);
        cbSprachen.setFont(font1);
        cbSprachen.addActionListener(e -> {
            JComboBox<?> cb = (JComboBox<?>) e.getSource();
            Object sel = cb.getSelectedItem();
            String language = sel != null ? sel.toString() : null;
            System.out.println("Ausgewählte Sprache: " + language);
            messageService.setLocale(language);
            setI18nText();
        });

        gbc.gridx = 1;
        mainPanel.add(cbSprachen, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        lbZip = new JLabel();
        lbZip.setFont(font1);
        mainPanel.add(lbZip, gbc);

        gbc.gridx = 1;
        tfZip = new JTextField(255);
        tfZip.setFont(font1);
        mainPanel.add(tfZip, gbc);

        btZip = new JButton("...");
        btZip.setFont(font1);
        btZip.addActionListener(e -> {
            chooserZip = new JFileChooser();
            chooserZip.setDialogTitle(messageService.getMessage("gui.select.zip"));
            chooserZip.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = chooserZip.showOpenDialog(mainPanel);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooserZip.getSelectedFile();
                tfZip.setText(file.getAbsolutePath());
            }
        });
        gbc.gridx = 2;
        mainPanel.add(btZip, gbc);

        /*------------- */
        gbc.gridy++;
        gbc.gridx = 0;
        lbWork = new JLabel();
        lbWork.setFont(font1);
        mainPanel.add(lbWork, gbc);

        gbc.gridx = 1;
        tfWork = new JTextField(255);
        tfWork.setFont(font1);
        mainPanel.add(tfWork, gbc);

        btWork = new JButton("...");
        btWork.setFont(font1);
        btWork.addActionListener(e -> {
            chooserWork = new JFileChooser();
            chooserWork.setAcceptAllFileFilterUsed(false); // Nur Ordner anzeigen
            chooserWork.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = chooserWork.showSaveDialog(mainPanel);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooserWork.getSelectedFile();
                tfWork.setText(file.getAbsolutePath());
            }
        });
        gbc.gridx = 2;
        mainPanel.add(btWork, gbc);

        /*------------- */
        gbc.gridy++;
        gbc.gridx = 0;
        lbExport = new JLabel();
        lbExport.setFont(font1);
        mainPanel.add(lbExport, gbc);

        gbc.gridx = 1;
        tfExport = new JTextField(255);
        tfExport.setFont(font1);
        mainPanel.add(tfExport, gbc);

        btExport = new JButton("...");
        btExport.setFont(font1);
        btExport.addActionListener(e -> {
            chooserExport = new JFileChooser();
            chooserExport.setAcceptAllFileFilterUsed(false); // Nur Ordner anzeigen
            chooserExport.setDialogType(JFileChooser.SAVE_DIALOG);
            chooserExport.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = chooserExport.showSaveDialog(mainPanel);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = chooserExport.getSelectedFile();
                tfExport.setText(file.getAbsolutePath());
            }
        });
        gbc.gridx = 2;
        mainPanel.add(btExport, gbc);

        gbc.gridy++;
        gbc.gridx = 1;

        btStartUnzip = new JButton();
        btStartUnzip.setFont(font1);
        btStartUnzip.addActionListener(e -> {
            try {
                pnStatusUnzip.setVisible(true);
                pnStatusExport.setVisible(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                saveProperties();
                unzipService.setZip(tfZip.getText());
                unzipService.setWork(tfWork.getText());
                unzipService.start();
            } catch (Exception e1) {
                showErrorMessage(tfExport, e1.getMessage());
                e1.printStackTrace();
            }
        });
        btStartExport = new JButton();
        btStartExport.setFont(font1);
        btStartExport.addActionListener(e -> {
            try {
                pnStatusExport.setVisible(true);
                pnStatusUnzip.setVisible(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                saveProperties();
                exportService.setWork(tfWork.getText());
                exportService.setExport(tfExport.getText());
                exportService.start();
            } catch (Exception e1) {
                showErrorMessage(tfExport, e1.getMessage());
                e1.printStackTrace();
            }
        });

        JPanel pnButtons = new JPanel();
        FlowLayout pnLayout = new FlowLayout();
        pnLayout.setAlignment(FlowLayout.LEADING);
        pnLayout.setVgap(0);
        pnButtons.setLayout(pnLayout);
        pnButtons.add(btStartUnzip);
        pnButtons.add(btStartExport);
        mainPanel.add(pnButtons, gbc);

        Properties props = new Properties();

        try {
            props = loadProperties();
            cbSprachen.setSelectedItem(props.getProperty(LANGUAGE, "de"));
            tfZip.setText(props.getProperty(ZIPFOLDER, ""));
            tfWork.setText(props.getProperty(WORKFOLDER, ""));
            tfExport.setText(props.getProperty(EXPORTFOLDER, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mainPanel;
    }

    private JComponent getHeader() {
        ImageIcon icon = new ImageIcon(getClass().getResource("/images/toplogo.png"));
        JLabel lbImage = new JLabel(icon);
        return lbImage;
    }

    private JComponent getSidebar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        // Button erstellen
        JButton openButton = new JButton("Takeout anfordern");

        // Aktion beim Klick hinzufügen
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Hier die gewünschte URL einsetzen
                    URI uri = new URI("https://takeout.google.com/");
                    Desktop.getDesktop().browse(uri);
                } catch (IOException | URISyntaxException ex) {
                    JOptionPane.showMessageDialog(Gui.this,
                            "Fehler beim Öffnen der Website: " + ex.getMessage(),
                            "Fehler",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(openButton);
        return panel;
    }

    private JComponent getStatusbar() {

        GridLayout statusLay = new GridLayout(3, 1, 0, 0);
        JPanel statusPanel = new JPanel(statusLay);
        statusPanel.setPreferredSize(new Dimension(0, 100));

        statusPanel.add(new JSeparator(SwingConstants.HORIZONTAL));

        pnStatusExport = new JPanel();
        FlowLayout statusExportLayout = new FlowLayout();
        statusExportLayout.setAlignment(FlowLayout.LEFT);
        pnStatusExport.setLayout(statusExportLayout);
        pnStatusExport.setVisible(false);

        lbStatusExportAnzahl = new JLabel();
        lbStatusExportAnzahl.setFont(font2);
        pnStatusExport.add(lbStatusExportAnzahl);
        tfStatusExportAnzahl = new JTextField(7);
        tfStatusExportAnzahl.setFont(font2);
        tfStatusExportAnzahl.setEditable(false);
        pnStatusExport.add(tfStatusExportAnzahl);
        lbStatusExportFolder = new JLabel();
        lbStatusExportFolder.setFont(font2);
        pnStatusExport.add(lbStatusExportFolder);
        tfStatusExportFolder = new JTextField(7);
        tfStatusExportFolder.setFont(font2);
        tfStatusExportFolder.setEditable(false);
        pnStatusExport.add(tfStatusExportFolder);
        lbStatusExportTime = new JLabel();
        lbStatusExportTime.setFont(font2);
        pnStatusExport.add(lbStatusExportTime);
        tfStatusExportTime = new JTextField(7);
        tfStatusExportTime.setFont(font2);
        tfStatusExportTime.setEditable(false);
        pnStatusExport.add(tfStatusExportTime);

        statusPanel.add(pnStatusExport);

        pnStatusUnzip = new JPanel();
        FlowLayout statusUnzipLayout = new FlowLayout();
        statusUnzipLayout.setAlignment(FlowLayout.LEFT);
        pnStatusUnzip.setLayout(statusUnzipLayout);
        // Anzahl ZIP-Dateien (n/m)
        lbStatusUnzipZipCount = new JLabel();
        lbStatusUnzipZipCount.setFont(font2);
        pnStatusUnzip.add(lbStatusUnzipZipCount);
        tfStatusUnzipZipCount = new JTextField(5);
        tfStatusUnzipZipCount.setFont(font2);
        tfStatusUnzipZipCount.setEditable(false);
        pnStatusUnzip.add(tfStatusUnzipZipCount);

        // Anzahl entpackter Dateien
        lbStatusUnzipCount = new JLabel();
        lbStatusUnzipCount.setFont(font2);
        pnStatusUnzip.add(lbStatusUnzipCount);
        tfStatusUnzipCount = new JTextField(5);
        tfStatusUnzipCount.setFont(font2);
        tfStatusUnzipCount.setEditable(false);
        pnStatusUnzip.add(tfStatusUnzipCount);

        // Gesamtgröße entpackter Dateien
        lbStatusUnzipSize = new JLabel();
        lbStatusUnzipSize.setFont(font2);
        pnStatusUnzip.add(lbStatusUnzipSize);
        tfStatusUnzipSize = new JTextField(7);
        tfStatusUnzipSize.setFont(font2);
        tfStatusUnzipSize.setEditable(false);
        pnStatusUnzip.add(tfStatusUnzipSize);

        lbStatusUnzipTime = new JLabel();
        lbStatusUnzipTime.setFont(font2);
        pnStatusUnzip.add(lbStatusUnzipTime);
        tfStatusUnzipTime = new JTextField(5);
        tfStatusUnzipTime.setFont(font2);
        tfStatusUnzipTime.setEditable(false);
        pnStatusUnzip.add(tfStatusUnzipTime);

        pnStatusUnzip.setVisible(false);

        statusPanel.add(pnStatusUnzip);

        JPanel pnStatus = new JPanel();
        FlowLayout statusLayout = new FlowLayout();
        statusLayout.setAlignment(FlowLayout.LEFT);
        pnStatus.setLayout(statusLayout);
        lbStatus = new JLabel("");
        lbStatus.setFont(font2);
        pnStatus.add(lbStatus);
        statusPanel.add(pnStatus);
        return statusPanel;
    }

    private void setI18nText() {

        lbSprache.setText(messageService.getMessage("gui.language"));

        lbZip.setText(messageService.getMessage("gui.zipfolder"));
        if (chooserZip != null) {
            chooserZip.setDialogTitle(messageService.getMessage("gui.select.zip"));
        }
        btZip.setToolTipText(messageService.getMessage("gui.select.zip"));

        lbWork.setText(messageService.getMessage("gui.workfolder"));
        if (chooserWork != null) {
            chooserWork.setDialogTitle(messageService.getMessage("gui.select.workfolder"));
        }
        btWork.setToolTipText(messageService.getMessage("gui.select.workfolder"));

        lbExport.setText(messageService.getMessage("gui.exportfolder"));
        if (chooserExport != null) {
            chooserExport.setDialogTitle(messageService.getMessage("gui.select.exportfolder"));
        }
        btExport.setToolTipText(messageService.getMessage("gui.select.exportfolder"));

        btStartUnzip.setText(messageService.getMessage("gui.start.unzip"));
        btStartExport.setText(messageService.getMessage("gui.start.export"));

        if (lbStatusExportAnzahl != null) {
            lbStatusExportAnzahl.setText(messageService.getMessage("gui.export.filecount"));
            lbStatusExportFolder.setText(messageService.getMessage("gui.export.foldercount"));
            lbStatusExportTime.setText(messageService.getMessage("gui.export.time"));

            lbStatusUnzipZipCount.setText(messageService.getMessage("gui.unzip.filecount"));
            lbStatusUnzipCount.setText(messageService.getMessage("gui.unzip.files"));
            lbStatusUnzipSize.setText(messageService.getMessage("gui.unzip.size"));
            lbStatusUnzipTime.setText(messageService.getMessage("gui.unzip.time"));
        }
    }

    private void showErrorMessage(JComponent parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Setzt das Anwendungssymbol (Titelleiste + Taskbar).
     *
     * @param frame    das JFrame
     * @param basePath Basis-Pfad oder -URL oder Ressourcenbasis (z.B.
     *                 "/images/app_icon" oder "C:/icons/app" oder
     *                 "https://.../app_icon")
     *                 Methode versucht automatisch Varianten wie basePath16.png,
     *                 basePath-16.png, basePath_16.png und basePath.png.
     */
    private void setApplicationIcon(String basePath) {
        List<Image> images = new ArrayList<>();
        int[] sizes = new int[] { 16, 32, 48, 64, 128 };

        // Versucht mehrere Namensvarianten pro Größe
        String[] variants = new String[] {
                "%s%d.png", // basePath16.png
                "%s-%d.png", // basePath-16.png
                "%s_%d.png", // basePath_16.png
                "%s%d.ico", // basePath16.ico (optional, ImageIO kann .ico evtl. nicht lesen)
                "%s.png" // basePath.png (fallback)
        };

        for (int size : sizes) {
            boolean foundForSize = false;
            for (String v : variants) {
                String candidate = String.format(v, basePath, size);
                Image img = loadImage(candidate);
                if (img != null) {
                    images.add(img);
                    foundForSize = true;
                    break;
                }
            }
            // Falls keine variant mit size gefunden, versuchen einfach basePath.png einmal
            // (nur beim ersten Durchlauf)
            if (!foundForSize && images.isEmpty()) {
                Image fallback = loadImage(String.format("%s.png", basePath));
                if (fallback != null)
                    images.add(fallback);
            }
        }

        // Wenn noch keine Bilder gefunden, versuche direkt basePath (z.B. wenn user gab
        // vollständigen Pfad mit extension an)
        if (images.isEmpty()) {
            Image direct = loadImage(basePath);
            if (direct != null)
                images.add(direct);
        }

        if (images.isEmpty()) {
            // Debug-Ausgabe — hilft festzustellen, warum Duke-Icon bleibt
            System.err.println("Kein Icon geladen. Prüfe Pfad/Resource: " + basePath);
            return;
        }

        // Setze die Liste (OS kann passende Größe wählen)
        setIconImages(images);

        // Zusätzlich: Taskbar API (Java 9+). Manche OS/Java-Konfigurationen verwenden
        // dies für Taskbar-Icon.
        try {
            // Java 9+: java.awt.Taskbar
            Class<?> taskbarClass = Class.forName("java.awt.Taskbar");
            Object taskbar = taskbarClass.getMethod("getTaskbar").invoke(null);
            // setIconImage(Image)
            taskbarClass.getMethod("setIconImage", Image.class).invoke(taskbar, images.get(0));
        } catch (Throwable t) {
            // ignore — Taskbar nicht verfügbar (z.B. Java 8) oder Aufruffehler
        }
    }

    /**
     * Versucht ein Image zu laden: URL, lokale Datei, Klassepath-Resource (mit und
     * ohne führendem '/').
     * Gibt null zurück wenn nicht gefunden oder nicht lesbar.
     */
    private Image loadImage(String path) {
        try {

            // Versuch: resource als URL (besser für ImageIO)
            URL res = getClass().getResource(path);
            if (res == null) {
                // versuche ohne führenden '/' falls angegeben
                if (!path.startsWith("/"))
                    res = getClass().getResource("/" + path);
                else {
                    // falls mit '/' nicht gefunden, versuche ohne
                    res = getClass().getResource(path.substring(1));
                }
            }
            if (res != null) {
                return ImageIO.read(res);
            }

            // letzte Chance: InputStream
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null && !path.startsWith("/"))
                is = getClass().getResourceAsStream("/" + path);
            if (is != null) {
                return ImageIO.read(is);
            }
        } catch (Exception e) {
            // Debug: zeige warum Laden fehlschlug (nur in stderr)
            System.err.println("Fehler beim Laden des Icons (" + path + "): " + e.getMessage());
        }
        return null;
    }

    private Properties loadProperties() {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(getUserHomeDirectory()));
        } catch (Exception e) {
            return new Properties();
        }
        return props;
    }

    private void saveProperties() {
        Properties props = new Properties();
        props.setProperty(LANGUAGE, cbSprachen.getSelectedItem().toString());
        props.setProperty(ZIPFOLDER, tfZip.getText());
        props.setProperty(WORKFOLDER, tfWork.getText());
        props.setProperty(EXPORTFOLDER, tfExport.getText());
        try (OutputStream out = new FileOutputStream(getUserHomeDirectory())) {
            props.store(out, "tpex configuration");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File getUserHomeDirectory() {
        String userHome = System.getProperty("user.home");
        return new File(userHome + "/tpex.properties");
    }

    @Override
    public void onEvent(InfoEventData event) {
        if (event.getType() == InfoEventType.STOPPED_UNZIP || event.getType() == InfoEventType.EXPORT_STOPPED) {
            setCursor(Cursor.getDefaultCursor());
        }
        switch (event.getType()) {
            case EXPORT_FILE_COUNT:
                tfStatusExportAnzahl.setText(event.getValue());
                break;
            case EXPORT_FOLDER_COUNT:
                tfStatusExportFolder.setText(event.getValue());
                break;
            case EXPORT_TIME:
                tfStatusExportTime.setText(event.getValue());
                break;
            case UNZIP_FILE_COUNT:
                tfStatusUnzipZipCount.setText(event.getValue());
                break;
            case EXPORT_STOPPED:
                lbStatus.setText(event.getValue());
                break;
            case STOPPED_UNZIP:
                lbStatus.setText(event.getValue());
                break;
            case UNZIP_EXTRACTED:
                tfStatusUnzipCount.setText(event.getValue());
                break;
            case UNZIPPED_SIZE:
                tfStatusUnzipSize.setText(event.getValue());
                break;
            case UNZIP_TIME:
                tfStatusUnzipTime.setText(event.getValue());
                break;
            default:
                System.out.println(event);
                break;
        }
    }

    private void loadFont() {
        InputStream is = getClass().getResourceAsStream("/fonts/Roboto-VariableFont_wdth,wght.ttf");
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            font1 = font.deriveFont(Font.BOLD, 12f);
            font2 = font.deriveFont(Font.PLAIN, 11f);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
