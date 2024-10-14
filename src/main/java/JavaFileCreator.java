import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JavaFileCreator {

    public static void main(String[] args) {
        // File chooser for gradle.properties file
        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));  // Start at current directory
        fileChooser.setDialogTitle("Select gradle.properties File");

        // Filter for .properties files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Properties Files", "properties");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File gradleFile = fileChooser.getSelectedFile();

            // Ensure it's the gradle.properties file
            if (!gradleFile.getName().equals("gradle.properties")) {
                JOptionPane.showMessageDialog(null, "Please select the valid gradle.properties file.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Proceed with the rest of the logic (e.g., showing the mod creator GUI)
            showModCreatorGUI(gradleFile);
        } else {
            JOptionPane.showMessageDialog(null, "No file selected. Exiting program.");
            System.exit(0);
        }
    }

    // GUI for mod creation
    private static void showModCreatorGUI(File gradleFile) {
        // Setup GUI
        JFrame frame = new JFrame("Mod File Creator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        // Mod name input
        JLabel modNameLabel = new JLabel("Enter Mod Name: ");
        JTextField modNameField = new JTextField();
        panel.add(modNameLabel);
        panel.add(modNameField);

        // Username input with default value
        JLabel usernameLabel = new JLabel("Enter Username: ");
        JTextField usernameField = new JTextField("awesomeshot5051");
        panel.add(usernameLabel);
        panel.add(usernameField);

        // Create button
        JButton createButton = new JButton("Create Mod File");
        panel.add(new JLabel()); // Placeholder
        panel.add(createButton);

        frame.add(panel);
        frame.setVisible(true);

        // Action listener for create button
        createButton.addActionListener(e -> {
            String modName = modNameField.getText().trim();
            String username = usernameField.getText().trim();

            if (modName.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Mod name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(frame, "Java file created successfully at: " + filePath);
            } catch (IOException ioException) {
                ioException.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error creating Java file.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            // Modify gradle.properties file
            try {
                modifyGradleProperties(gradleFile, camelCaseModName, username);
                JOptionPane.showMessageDialog(frame, "gradle.properties modified successfully.");
            } catch (IOException ioException) {
                ioException.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error modifying gradle.properties file.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
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
                "import net.neoforged.api.distmarker.Dist;\n" +
                "import net.neoforged.api.distmarker.OnlyIn;\n" +
                "import net.neoforged.bus.api.IEventBus;\n" +
                "import net.neoforged.fml.config.ModConfig;\n" +
                "import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;\n" +
                "import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;\n" +
                "import net.neoforged.fml.loading.FMLEnvironment;\n" +
                "import net.neoforged.neoforge.common.NeoForge;\n" +
                "import org.apache.logging.log4j.LogManager;\n" +
                "import org.apache.logging.log4j.Logger;\n" +
                "public class Main {\n\n" +
                "    public static final String MODID = \"" + modName + "\";\n" +
                "    public static final Logger LOGGER = LogManager.getLogger(MODID);\n" +
                "    //public static ServerConfig SERVER_CONFIG;\n" +
                "    //public static ClientConfig CLIENT_CONFIG;\n\n" +
                "    public Main(IEventBus eventBus) {\n" +
                "        eventBus.addListener(this::commonSetup);\n" +
                "        //SERVER_CONFIG = CommonRegistry.registerConfig(MODID, ModConfig.Type.SERVER, ServerConfig.class);\n" +
                "        //CLIENT_CONFIG = CommonRegistry.registerConfig(MODID, ModConfig.Type.CLIENT, ClientConfig.class);\n" +
                "        if (FMLEnvironment.dist.isClient()) {\n" +
                "            eventBus.addListener(Main.this::clientSetup);\n" +
                "            Containers.initClient(eventBus);\n" +
                "        }\n" +
                "    }\n\n" +
                "    public void commonSetup(FMLCommonSetupEvent event) {\n" +
                "        NeoForge.EVENT_BUS.register(new BlockEvents());\n" +
                "    }\n" +
                "    @OnlyIn(Dist.CLIENT)\n" +
                "    public void clientSetup(FMLClientSetupEvent event) {}\n" +
                "}";
    }

    // Method to modify gradle.properties file
    private static void modifyGradleProperties(@NotNull File gradleFile, String modName,String username) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(gradleFile.getPath())));
        content = content.replace("mod_id=template", "mod_id=" + modName.toLowerCase());
        Files.write(Paths.get(gradleFile.getPath()), content.getBytes());
        content = content.replace("mod_name=Template","mod_name="+modName);
        Files.write(Paths.get(gradleFile.getPath()), content.getBytes());
        content = content.replace("mod_authors=YourNameHere, OtherNameHere","mod_authors="+username);
        Files.write(Paths.get(gradleFile.getPath()), content.getBytes());
        content = content.replace("mod_group_id=com.example.examplemod","mod_group_id=com."+username+"."+modName);
        Files.write(Paths.get(gradleFile.getPath()), content.getBytes());
    }
}
