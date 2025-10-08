package de.jostnet.tpex.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.springframework.stereotype.Component;

@Component
public class Gui {

    public void open() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("tpex - Takeout Photo Exporter");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(700, 400);
            GridBagLayout layout = new GridBagLayout();
            layout.columnWidths = new int[] { 100, 300, 100 };
            frame.setLayout(layout);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;

            JLabel lbZip = new JLabel("ZIP-Verzeichnis:");
            frame.add(lbZip, gbc);

            gbc.gridx = 1;
            JTextField tfZip = new JTextField(255);
            frame.add(tfZip, gbc);

            JButton btZip = new JButton("...");
            btZip.addActionListener(e -> {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("ZIP-Verzeichnis auswählen");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = chooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    tfZip.setText(file.getAbsolutePath());
                }
            });
            gbc.gridx = 2;
            frame.add(btZip, gbc);

            /*------------- */
            gbc.gridy++;
            gbc.gridx = 0;
            JLabel lbInput = new JLabel("Input-Verzeichnis:");
            frame.add(lbInput, gbc);

            gbc.gridx = 1;
            JTextField tfInput = new JTextField(255);
            frame.add(tfInput, gbc);

            JButton btInput = new JButton("...");
            btInput.addActionListener(e -> {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Input-Verzeichnis auswählen");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = chooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    tfInput.setText(file.getAbsolutePath());
                }
            });
            gbc.gridx = 2;
            frame.add(btInput, gbc);

            /*------------- */
            gbc.gridy++;
            gbc.gridx = 0;
            JLabel lbOutput = new JLabel("Output-Verzeichnis:");
            frame.add(lbOutput, gbc);

            gbc.gridx = 1;
            JTextField tfOutput = new JTextField(255);
            frame.add(tfOutput, gbc);

            JButton btOutput = new JButton("...");
            btOutput.addActionListener(e -> {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Output-Verzeichnis auswählen");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = chooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    tfOutput.setText(file.getAbsolutePath());
                }
            });
            gbc.gridx = 2;
            frame.add(btOutput, gbc);

            gbc.gridy++;

            // frame.add(fileLabel, gbc);
            // gbc.gridy++;

            gbc.gridx = 0;
            JButton submitButton = new JButton("Exportieren");
            // submitButton.addActionListener(e -> {
            // String f1 = field1.getText();
            // String f2 = field2.getText();
            // String f3 = field3.getText();
            // // String file = fileLabel.getText();
            // JOptionPane.showMessageDialog(frame,
            // "Feld 1: " + f1 +
            // "\nFeld 2: " + f2 +
            // "\nFeld 3: " + f3 +
            // "\nZIP: " + zip,
            // "Zusammenfassung",
            // JOptionPane.INFORMATION_MESSAGE);
            // });
            frame.add(submitButton, gbc);

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
