plugins {
	id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT"
	id("maven-publish")
	id("me.modmuss50.mod-publish-plugin") version "1.0.0"
}

java.sourceCompatibility = JavaVersion.VERSION_25
java.targetCompatibility = JavaVersion.VERSION_25

val ctFile = "26.1.classtweaker"
base.archivesName = "${property("mod_id")}"
version = "${property("mod_version")}+${stonecutter.current.project}+${property("mod_subversion")}"

repositories {
	// Core
	exclusiveContent {
		forRepository {
			maven("https://api.modrinth.com/maven")
		}
		filter {
			includeGroup("maven.modrinth")
		}
	}

	// Mod Menu
	maven("https://maven.terraformersmc.com/")
}

loom {
	accessWidenerPath = rootProject.file("src/main/resources/class_tweakers/$ctFile")
}

dependencies {
	minecraft("com.mojang:minecraft:${stonecutter.current.version}")
	implementation("net.fabricmc:fabric-loader:0.18.4")

	// Fabric API
	implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")

	// PneumonoCore
	implementation("maven.modrinth:pneumono_core:${property("core_version")}")

	// Mod Menu
	runtimeOnly("com.terraformersmc:modmenu:${property("modmenu_version")}")
}

tasks {
	processResources {
		inputs.property("version", project.version)
		inputs.property("supported_versions", ">=${project.property("min_supported_version")} <=${project.property("max_supported_version")}")

		filesMatching("fabric.mod.json") {
			expand(
				mutableMapOf(
					"version" to project.property("mod_version"),
					"supported_versions" to ">=${project.property("min_supported_version")} <=${project.property("max_supported_version")}",
					"ct_file" to ctFile
				)
			)
		}
	}

	withType<JavaCompile> {
		options.release.set(25)
	}

	java {
		withSourcesJar()
	}

	jar {
		from("LICENSE") {
			rename {"${it}_${base.archivesName.get()}"}
		}
	}
}

stonecutter {
	replacements.string {
		direction = eval(current.version, ">=1.21.11")
		replace("ResourceLocation", "Identifier")
	}
}

publishMods {
	file = tasks.jar.map { it.archiveFile.get() }
	additionalFiles.from(tasks.named<org.gradle.jvm.tasks.Jar>("sourcesJar").map { it.archiveFile.get() })
	displayName = "Locator Lodestones ${project.version}"
	version = "${project.version}"
	changelog = rootProject.file("CHANGELOG.md").readText()
	type = STABLE
	modLoaders.addAll("fabric", "quilt")

	val modrinthToken = providers.environmentVariable("MODRINTH_TOKEN")
	val discordToken = providers.environmentVariable("DISCORD_TOKEN")

	dryRun = modrinthToken.getOrNull() == null || discordToken.getOrNull() == null

	modrinth {
		accessToken = modrinthToken
		projectId = "pMBcsVIg"

		minecraftVersionRange {
			start = "${property("min_supported_version")}"
			end = "${property("max_supported_version")}"
		}

		requires {
			// PneumonoCore
			id = "ZLKQjA7t"
		}

		requires {
			// Fabric API
			id = "P7dR8mSH"
		}
	}

	if (stonecutter.current.project == "26.1") {
		discord {
			webhookUrl = discordToken

			username = "Locator Lodestones Updates"

			avatarUrl = "https://github.com/PneumonoIsNotAvailable/LocatorLodestones/blob/master/src/main/resources/assets/locator_lodestones/icon.png?raw=true"

			content = changelog.map { "# Locator Lodestones version ${project.property("mod_version")}\n<@&1472490332783378472>\n" + it }
		}
	}
}

// configure the maven publication
publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"])
		}
	}

	// See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
	repositories {
		// Add repositories to publish to here.
		// Notice: This block does NOT have the same function as the block in the top level.
		// The repositories here will be used for publishing your artifact, not for
		// retrieving dependencies.
	}
}