# Installation Information

This template repository can be cloned directly to get you started with a new mod. To create a new repository from this one, follow the instructions at [GitHub](https://docs.github.com/en/repositories/creating-and-managing-repositories/creating-a-repository-from-a-template).

Once you have your clone, simply open the repository in the IDE of your choice. The recommended IDEs are IntelliJ IDEA or Eclipse. This template is custom and differs from the NeoForged template. If you are new to modding, it's recommended to use [the NeoForged template](https://github.com/orgs/NeoForgeMDKs/repositories?q=template%3Atrue+archived%3Afalse) instead.

When the template is opened in your IDE, run the `JavaFileCreator` to generate your main class and set up your `gradle.properties` file, which is essential for releases.

# Updating the Mod

After updating your mod, if you need to quickly modify the values in the `gradle.properties` file, you can run the `ModUpdater` Java file. This will help you efficiently update properties crucial for releasing new versions.

> **Note:** For Eclipse, use tasks in `Launch Group` instead of those found in `Java Application`. A preparation task must run before launching the game. NeoGradle uses launch groups to ensure these steps are executed in sequence.

If you're missing libraries in your IDE or encounter problems, you can run `gradlew --refresh-dependencies` to refresh the local cache or `gradlew clean` to reset everything (this will not affect your code). Afterward, restart the setup process.

# Mapping Names

By default, the MDK is configured to use Mojang’s official mapping names for methods and fields in the Minecraft codebase. These names are covered by a specific license. Modders should be aware of this license. For the latest text, refer to the mapping file itself, or view a reference copy here:  
[NeoForged Mapping License](https://github.com/NeoForged/NeoForm/blob/main/Mojang.md).

# Additional Resources

- Community Documentation: https://docs.neoforged.net/
- NeoForged Discord: https://discord.neoforged.net/
