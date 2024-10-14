import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ModUpdater {
    private static File gradleFile;
    private static Map<String, String> properties = new LinkedHashMap<>();

    public static void main(String[] args) {
        // Initialize the gradle.properties file in the current directory
        gradleFile = new File(System.getProperty("user.dir"), "gradle.properties");

        // Load existing properties from the file
        loadProperties(gradleFile);

        // Setup the GUI for updating properties
        JFrame frame = new JFrame("Mod Updater");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Add some padding

        // Adding components to the panel
        int row = 0;

        // Mod version
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Mod Version:"), gbc);
        gbc.gridx = 1;
        panel.add(new JTextField(properties.getOrDefault("mod_version", "1.0.0")), gbc);

        row++;

        // Mod description
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Mod Description:"), gbc);
        gbc.gridx = 1;
        JTextArea modDescriptionArea = new JTextArea(properties.getOrDefault("mod_description", "Example mod description."));
        panel.add(new JScrollPane(modDescriptionArea), gbc);

        row++;

        // Minecraft version
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Minecraft Version:"), gbc);
        gbc.gridx = 1;
        panel.add(new JTextField(properties.getOrDefault("minecraft_version", "1.21")), gbc);

        row++;

        // Minecraft version range
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Minecraft Version Range:"), gbc);
        gbc.gridx = 1;
        panel.add(new JTextField(properties.getOrDefault("minecraft_version_range", "[1.21,1.21.1)")), gbc);

        row++;

        // Neo version
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Neo Version:"), gbc);
        gbc.gridx = 1;
        panel.add(new JTextField(properties.getOrDefault("neo_version", "21.0.167")), gbc);

        row++;

        // Neo version range
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Neo Version Range:"), gbc);
        gbc.gridx = 1;
        panel.add(new JTextField(properties.getOrDefault("neo_version_range", "[21.0.0-beta,)")), gbc);

        row++;

        // Loader version range
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel("Loader Version Range:"), gbc);
        gbc.gridx = 1;
        panel.add(new JTextField(properties.getOrDefault("loader_version_range", "[4,)")), gbc);

        row++;

        // Button to choose a new gradle.properties file
        gbc.gridx = 0;
        gbc.gridy = row;
        JButton chooseFileButton = new JButton("Choose gradle.properties File");
        panel.add(chooseFileButton, gbc);

        // Update button
        gbc.gridx = 1;
        JButton updateButton = new JButton("Update");
        panel.add(updateButton, gbc);

        frame.add(panel);
        frame.setVisible(true);

        // Action listener for choosing a different gradle.properties file
        chooseFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
            fileChooser.setDialogTitle("Select gradle.properties File");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Properties Files", "properties");
            fileChooser.setFileFilter(filter);

            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                gradleFile = fileChooser.getSelectedFile();
                loadProperties(gradleFile);
                // Update all fields with the loaded values
                // Assuming you have a reference to your input fields here to update their text
            }
        });

        // Action listener for updating the properties file
        // Action listener for updating the properties file
        // Action listener for updating the properties file
        updateButton.addActionListener(e -> {
            // Get values from all input fields
            String newModVersion = ((JTextField) panel.getComponent(1)).getText().trim();

            // Retrieve the JTextArea from the JScrollPane
            JScrollPane scrollPane = (JScrollPane) panel.getComponent(3); // Adjusted index
            JTextArea modDescriptionArea2 = (JTextArea) scrollPane.getViewport().getView();
            String newModDescription = modDescriptionArea2.getText().trim().replaceAll("\\n", "\\\\n");

            String newMinecraftVersion = ((JTextField) panel.getComponent(5)).getText().trim();
            String newMinecraftVersionRange = ((JTextField) panel.getComponent(7)).getText().trim();
            String newNeoVersion = ((JTextField) panel.getComponent(9)).getText().trim();
            String newNeoVersionRange = ((JTextField) panel.getComponent(11)).getText().trim();
            String newLoaderVersionRange = ((JTextField) panel.getComponent(13)).getText().trim();

            // Update properties in memory
            properties.put("mod_version", newModVersion);
            properties.put("mod_description", newModDescription);
            properties.put("minecraft_version", newMinecraftVersion);
            properties.put("minecraft_version_range", newMinecraftVersionRange);
            properties.put("neo_version", newNeoVersion);
            properties.put("neo_version_range", newNeoVersionRange);
            properties.put("loader_version_range", newLoaderVersionRange);

            // Save updated properties to the file
            saveProperties(gradleFile);
            JOptionPane.showMessageDialog(frame, "gradle.properties updated successfully!");
        });
    }

    // Method to load properties from a file
    private static void loadProperties(File file) {
        properties.clear(); // Clear existing properties
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    // Preserve comments and empty lines
                    properties.put(line, line);
                } else {
                    // Parse key=value pairs
                    String[] keyValue = line.split("=", 2);
                    if (keyValue.length == 2) {
                        properties.put(keyValue[0].trim(), keyValue[1].trim());
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error loading properties file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to save properties to a file
    private static void saveProperties(File file) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                writer.println(entry.getKey() + (entry.getValue() != null ? "=" + entry.getValue() : ""));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving properties file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
