import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;

import static org.apache.commons.io.FileUtils.copyDirectory;

public class JavaFileCreator {

    private static File gradleFile; // Store the gradle.properties file
    private static File existingModDir; // Store the existing mod directory

    public static void main(String[] args) {
        // Show the mod creator GUI
        showModCreatorGUI();
    }

    // GUI for mod creation
    private static void showModCreatorGUI() {
        // Setup GUI
        JFrame frame = new JFrame("Mod File Creator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 350);
        frame.setLocationRelativeTo(null); // Center the frame

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add padding

        // Radio buttons for action selection
        JRadioButton createNewModRadio = new JRadioButton("Create New Mod", true);
        JRadioButton copyExistingRadio = new JRadioButton("Copy From Existing Mod");
        ButtonGroup actionGroup = new ButtonGroup();
        actionGroup.add(createNewModRadio);
        actionGroup.add(copyExistingRadio);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across both columns
        panel.add(createNewModRadio, gbc);

        gbc.gridy = 1; // Next row
        panel.add(copyExistingRadio, gbc);

        // Button to select gradle.properties file
        JButton selectGradleButton = new JButton("Select gradle.properties File");
        gbc.gridy = 2; // Place it below the radio buttons
        gbc.gridwidth = 2; // Span across both columns
        panel.add(selectGradleButton, gbc);

        // Mod name input
        JLabel modNameLabel = new JLabel("Enter Mod Name: ");
        gbc.gridwidth = 1; // Reset to one column
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST; // Align labels to the right
        panel.add(modNameLabel, gbc);

        JTextField modNameField = new JTextField(20); // Specify column width
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST; // Align text fields to the left
        panel.add(modNameField, gbc);

        // Username input with default value
        JLabel usernameLabel = new JLabel("Enter Username: ");
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField("awesomeshot5051", 20);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        // Create button
        JButton createButton = new JButton("Create Mod File");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2; // Span across both columns
        gbc.anchor = GridBagConstraints.CENTER; // Center the button
        panel.add(createButton, gbc);

        frame.add(panel);
        frame.setVisible(true);

        // Action listener for radio button changes
        createNewModRadio.addActionListener(e -> {
            selectGradleButton.setVisible(true);
            modNameLabel.setVisible(true);
            modNameField.setVisible(true);
            usernameLabel.setVisible(true);
            usernameField.setVisible(true);
            createButton.setText("Create Mod File");
        });

        copyExistingRadio.addActionListener(e -> {
            selectGradleButton.setVisible(false);
            modNameLabel.setVisible(false);
            modNameField.setVisible(false);
            usernameLabel.setVisible(false);
            usernameField.setVisible(false);
            createButton.setText("Copy Existing Mod");
        });

        // Action listener for select gradle.properties button
        selectGradleButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));  // Start at current directory
            fileChooser.setDialogTitle("Select gradle.properties File");

            // Filter for .properties files
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Properties Files", "properties");
            fileChooser.setFileFilter(filter);

            int result = fileChooser.showOpenDialog(frame);

            if (result == JFileChooser.APPROVE_OPTION) {
                gradleFile = fileChooser.getSelectedFile();

                // Ensure it's the gradle.properties file
                if (!gradleFile.getName().equals("gradle.properties")) {
                    JOptionPane.showMessageDialog(frame, "Please select the valid gradle.properties file.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(frame, "No file selected.");
            }
        });

        // Action listener for create/copy button
        createButton.addActionListener(e -> {
            if (createNewModRadio.isSelected()) {
                createNewMod(modNameField.getText().trim(), usernameField.getText().trim());
            } else {
                copyExistingMod();
            }
        });
    }

    // Method for creating a new mod
    private static void createNewMod(String modName, String username) {
        if (modName.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Mod name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Convert mod name to camelCase
        String camelCaseModName = toCamelCase(modName);

        // Create directory
        String directoryPath = "src/main/java/com/" + username + "/" + camelCaseModName;
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Create the new Java file
        String filePath = directoryPath + "/Main.java";
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(generateJavaFileContent(camelCaseModName, username));
            JOptionPane.showMessageDialog(null, "Java file created successfully at: " + filePath);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error creating Java file.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Modify gradle.properties file
        try {
            if (gradleFile != null) { // Check if gradleFile is selected
                modifyGradleProperties(gradleFile, camelCaseModName, username);
                JOptionPane.showMessageDialog(null, "gradle.properties modified successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "Please select a gradle.properties file first.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error modifying gradle.properties file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method for copying an existing mod
    private static void copyExistingMod() {
        JFileChooser directoryChooser = new JFileChooser(System.getProperty("user.dir"));
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = directoryChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            existingModDir = directoryChooser.getSelectedFile();

            // Perform copying logic here
            try {
                // Assuming the destination directory is the current directory for simplicity.
                File newModDir = new File(System.getProperty("user.dir") + "/" + existingModDir.getName());
                copyDirectory(existingModDir, newModDir);
                JOptionPane.showMessageDialog(null, "Mod copied successfully to: " + newModDir.getPath());
            } catch (IOException ioException) {
                ioException.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error copying mod.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No directory selected.");
        }
    }


    private static void copyAndMergeFolders(Path source, Path targetParent, String newModName, String username) throws IOException {
        // Walk through the source directory
        Files.walk(source).forEach(sourcePath -> {
            // Determine relative path in the new project (target parent + relative source path)
            Path relativePath = source.relativize(sourcePath);
            Path targetPath = targetParent.resolve(relativePath);

            try {
                if (Files.isDirectory(sourcePath)) {
                    // Create directories in the target if they don't exist
                    Files.createDirectories(targetPath);
                } else {
                    // Copy files, replacing existing ones
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (AccessDeniedException e) {
                // Skip files or directories we don't have access to
                System.err.println("Access denied to: " + sourcePath.toString() + ". Skipping...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Now modify files as needed (e.g., update the mod name in the copied files)
        updateModFiles(targetParent, newModName, username);
    }

    private static void updateModFiles(Path targetParent, String modName, String username) throws IOException {
        // Example: Rename package paths, modify class names, or update config files, etc.
        Path mainJavaFile = targetParent.resolve("Main.java");
        if (Files.exists(mainJavaFile)) {
            String content = new String(Files.readAllBytes(mainJavaFile));
            content = content.replace("template_mod_id", modName.toLowerCase())
                    .replace("com.example.examplemod", "com." + username + "." + modName);
            Files.write(mainJavaFile, content.getBytes());
        }
    }


    // Method to convert a mod name to camelCase
    private static String toCamelCase(String modName) {
        String[] words = modName.split("\\s+");
        StringBuilder camelCaseModName = new StringBuilder(words[0].toLowerCase());

        for (int i = 1; i < words.length; i++) {
            camelCaseModName.append(Character.toUpperCase(words[i].charAt(0)))
                    .append(words[i].substring(1).toLowerCase());
        }

        return camelCaseModName.toString();
    }

    // Method to generate the content of the new Java file
    private static String generateJavaFileContent(String modName, String username) {
        return "package com." + username + "." + modName + ";\n\n" +
                "import net.minecraftforge.fml.common.Mod;\n" +
                "import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;\n" +
                "import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;\n" +
                "import net.minecraftforge.fml.event.lifecycle.FMLJavaModLoadingContext;\n" +
                "import net.minecraftforge.fml.common.Mod.EventBusSubscriber;\n" +
                "import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;\n" +
                "import net.minecraftforge.api.distmarker.Dist;\n" +
                "\n" +
                "@Mod(\"" + modName.toLowerCase() + "\")\n" +
                "public class " + modName + " {\n" +
                "    public " + modName + "() {\n" +
                "        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);\n" +
                "        if (net.minecraftforge.fml.loading.FMLLoader.getDist() == Dist.CLIENT) {\n" +
                "            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);\n" +
                "        }\n" +
                "    }\n\n" +
                "    private void commonSetup(final FMLCommonSetupEvent event) {\n" +
                "        // Common setup code here\n" +
                "    }\n\n" +
                "    @OnlyIn(Dist.CLIENT)\n" +
                "    private void clientSetup(final FMLClientSetupEvent event) {\n" +
                "        // Client setup code here\n" +
                "    }\n" +
                "}\n";
    }

    // Method to modify the gradle.properties file
    private static void modifyGradleProperties(@NotNull File gradleFile, String modName, String username) throws IOException {
        // Read existing content
        String content = new String(Files.readAllBytes(Paths.get(gradleFile.getPath())));

        // Modify content (example: replace some placeholders)
        content = content.replace("mod_name_placeholder", modName.toLowerCase());
        content = content.replace("username_placeholder", username);

        // Write the modified content back to the file
        try (FileWriter writer = new FileWriter(gradleFile)) {
            writer.write(content);
        }
    }
}
