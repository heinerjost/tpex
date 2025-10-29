package de.jostnet.tpex.gui;

import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import de.jostnet.tpex.services.MessageService;
import lombok.Getter;

public class StatusPanel extends JPanel {
    private MessageService messageService;

    @Getter
    private JPanel pnStatusExport;
    @Getter
    private JLabel lbStatusExportAnzahl;
    @Getter
    private JTextField tfStatusExportAnzahl;
    @Getter
    private JLabel lbStatusExportFolder;
    @Getter
    private JTextField tfStatusExportFolder;
    @Getter
    private JLabel lbStatusExportTime;
    @Getter
    private JTextField tfStatusExportTime;

    @Getter
    private JPanel pnStatusUnzip;
    @Getter
    private JLabel lbStatusUnzipZipCount;
    @Getter
    private JTextField tfStatusUnzipZipCount;
    @Getter
    private JLabel lbStatusUnzipCount;
    @Getter
    private JTextField tfStatusUnzipCount;
    @Getter
    private JLabel lbStatusUnzipSize;
    @Getter
    private JTextField tfStatusUnzipSize;
    @Getter
    private JLabel lbStatusUnzipTime;
    @Getter
    private JTextField tfStatusUnzipTime;
    @Getter
    private JLabel lbStatus;

    public StatusPanel(MessageService messageService, Font font2) {
        this.messageService = messageService;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(new JSeparator(SwingConstants.HORIZONTAL));

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

        add(pnStatusExport);

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

        add(pnStatusUnzip);

        JPanel pnStatus = new JPanel();
        FlowLayout statusLayout = new FlowLayout();
        statusLayout.setAlignment(FlowLayout.LEFT);
        pnStatus.setLayout(statusLayout);
        lbStatus = new JLabel("");
        lbStatus.setFont(font2);
        pnStatus.add(lbStatus);
        add(pnStatus);
    }

    public void activateUnzipStatusPanel() {
        pnStatusUnzip.setVisible(true);
        pnStatusExport.setVisible(false);
        tfStatusUnzipCount.setText("");
        tfStatusUnzipSize.setText("");
        tfStatusUnzipTime.setText("");
        tfStatusUnzipZipCount.setText("");
        lbStatus.setText("");
    }

    public void activateExportStatusPanel() {
        pnStatusUnzip.setVisible(false);
        pnStatusExport.setVisible(true);
        tfStatusExportAnzahl.setText("");
        tfStatusExportFolder.setText("");
        tfStatusExportTime.setText("");
        lbStatus.setText("");
    }

    public void hideAllStatusPanels() {
        pnStatusUnzip.setVisible(false);
        pnStatusExport.setVisible(false);
        lbStatus.setText("");
    }

    public void setI18nText() {
        lbStatusExportAnzahl.setText(messageService.getMessage("gui.export.filecount"));
        lbStatusExportFolder.setText(messageService.getMessage("gui.export.foldercount"));
        lbStatusExportTime.setText(messageService.getMessage("gui.export.time"));

        lbStatusUnzipZipCount.setText(messageService.getMessage("gui.unzip.filecount"));
        lbStatusUnzipCount.setText(messageService.getMessage("gui.unzip.files"));
        lbStatusUnzipSize.setText(messageService.getMessage("gui.unzip.size"));
        lbStatusUnzipTime.setText(messageService.getMessage("gui.unzip.time"));
    }

}
