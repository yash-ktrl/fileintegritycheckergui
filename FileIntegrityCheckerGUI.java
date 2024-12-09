public class FileIntegrityCheckerGUI {
    public static void main(String[] args) {
        
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

       
        JFrame frame = new JFrame("File Integrity Checker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 500);
        frame.setLayout(null);

        
        Color backgroundColor = Color.BLACK;
        Color textColor = Color.WHITE;
        Color buttonColor = Color.GRAY;
        Color dropdownColor = Color.LIGHT_GRAY;
        Font customFont = new Font("Verdana", Font.PLAIN, 16);

        frame.getContentPane().setBackground(backgroundColor);

        JLabel fileLabel = new JLabel("Select File:");
        fileLabel.setForeground(textColor);
        fileLabel.setFont(customFont);
        fileLabel.setBounds(20, 20, 120, 30);

        JTextField fileField = new JTextField(30);
        fileField.setFont(customFont);
        fileField.setBounds(150, 20, 550, 30);

        JButton browseButton = new JButton("Browse");
        browseButton.setFont(customFont);
        browseButton.setBackground(buttonColor);
        browseButton.setForeground(textColor);
        browseButton.setBounds(720, 20, 120, 30);

        JLabel algorithmLabel = new JLabel("Select Algorithm:");
        algorithmLabel.setForeground(textColor);
        algorithmLabel.setFont(customFont);
        algorithmLabel.setBounds(20, 70, 150, 30);

        String[] algorithms = {"SHA-1", "MD5", "SHA-256", "SHA-512"};
        JComboBox<String> algorithmDropdown = new JComboBox<>(algorithms);
        algorithmDropdown.setFont(customFont);
        algorithmDropdown.setBackground(dropdownColor);
        algorithmDropdown.setBounds(180, 70, 200, 30);

        JLabel expectedHashLabel = new JLabel("Enter Expected Hash:");
        expectedHashLabel.setForeground(textColor);
        expectedHashLabel.setFont(customFont);
        expectedHashLabel.setBounds(20, 120, 200, 30);

        JTextField expectedHashField = new JTextField(40);
        expectedHashField.setFont(customFont);
        expectedHashField.setBounds(220, 120, 620, 30);

        JTextArea outputArea = new JTextArea();
        outputArea.setFont(customFont);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setBackground(Color.DARK_GRAY);
        outputArea.setForeground(textColor);

        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBounds(20, 170, 820, 150);

        JButton checkButton = new JButton("Check Integrity");
        checkButton.setFont(customFont);
        checkButton.setBackground(buttonColor);
        checkButton.setForeground(textColor);
        checkButton.setBounds(20, 340, 180, 40);

        JButton copyButton = new JButton("Copy");
        copyButton.setFont(customFont);
        copyButton.setBackground(buttonColor);
        copyButton.setForeground(textColor);
        copyButton.setBounds(220, 340, 120, 40);

        JButton saveButton = new JButton("Save");
        saveButton.setFont(customFont);
        saveButton.setBackground(buttonColor);
        saveButton.setForeground(textColor);
        saveButton.setBounds(360, 340, 120, 40);

        frame.add(fileLabel);
        frame.add(fileField);
        frame.add(browseButton);
        frame.add(algorithmLabel);
        frame.add(algorithmDropdown);
        frame.add(expectedHashLabel);
        frame.add(expectedHashField);
        frame.add(checkButton);
        frame.add(scrollPane);
        frame.add(copyButton);
        frame.add(saveButton);

        
        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                fileField.setText(selectedFile.getAbsolutePath());
            }
        });

        checkButton.addActionListener(e -> {
            String filePath = fileField.getText();
            String algorithm = (String) algorithmDropdown.getSelectedItem();
            String expectedHash = expectedHashField.getText().trim();

            if (filePath.isEmpty()) {
                outputArea.setText("Please select a file.");
                return;
            }

            try {
                String checksum = getFileChecksum(filePath, algorithm);
                outputArea.setText("Calculated Checksum (" + algorithm + "): " + checksum);

                if (!expectedHash.isEmpty()) {
                    if (checksum.equalsIgnoreCase(expectedHash)) {
                        outputArea.append("\nStatus: Integrity check PASSED. The hashes match.");
                    } else {
                        outputArea.append("\nStatus: Integrity check FAILED. The hashes do not match.");
                    }
                }
            } catch (Exception ex) {
                outputArea.setText("Error calculating checksum: " + ex.getMessage());
            }
        });

        copyButton.addActionListener(e -> {
            String output = outputArea.getText();
            if (!output.isEmpty()) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(output), null);
                JOptionPane.showMessageDialog(frame, "Copied to clipboard!");
            } else {
                JOptionPane.showMessageDialog(frame, "No output to copy.");
            }
        });

        saveButton.addActionListener(e -> {
            String output = outputArea.getText();
            if (!output.isEmpty()) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showSaveDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File saveFile = fileChooser.getSelectedFile();
                    try (FileWriter writer = new FileWriter(saveFile)) {
                        writer.write(output);
                        JOptionPane.showMessageDialog(frame, "Output saved to file!");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "Error saving file: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame, "No output to save.");
            }
        });

        frame.setVisible(true);
    }

    private static String getFileChecksum(String filePath, String algorithm) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        File file = new File(filePath);
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        byte[] hashBytes = digest.digest(fileBytes);

        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
