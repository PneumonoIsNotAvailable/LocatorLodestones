plugins {
	id("fabric-loom") version "1.15-SNAPSHOT"
	id("maven-publish")
	id("me.modmuss50.mod-publish-plugin") version "1.0.0"
}

java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

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
	accessWidenerPath = rootProject.file("src/main/resources/locator_lodestones.accesswidener")
}

dependencies {
	minecraft("com.mojang:minecraft:${stonecutter.current.version}")
	mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
	modImplementation("net.fabricmc:fabric-loader:0.17.3")

	// Fabric API
	modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")

	// PneumonoCore
	modImplementation("maven.modrinth:pneumono_core:${property("core_version")}")

	// Mod Menu
	modImplementation("com.terraformersmc:modmenu:${property("modmenu_version")}")
}

tasks {
	processResources {
		inputs.property("version", project.version)
		inputs.property("min_supported", project.property("min_supported_version"))
		inputs.property("max_supported", project.property("max_supported_version"))

		filesMatching("fabric.mod.json") {
			expand(
				mutableMapOf(
					"version" to project.version,
					"min_supported" to project.property("min_supported_version"),
					"max_supported" to project.property("max_supported_version")
				)
			)
		}
	}

	withType<JavaCompile> {
		options.release.set(21)
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

publishMods {
	file = tasks.remapJar.get().archiveFile
	additionalFiles.from(tasks.remapSourcesJar.get().archiveFile)
	displayName = "Locator Lodestones ${project.version}"
	version = "${project.version}"
	changelog = rootProject.file("CHANGELOG.md").readText()
	type = STABLE
	modLoaders.addAll("fabric", "quilt")

	dryRun = providers.environmentVariable("MODRINTH_TOKEN").getOrNull() == null

	modrinth {
		accessToken = providers.environmentVariable("MODRINTH_TOKEN")
		projectId = "pMBcsVIg"

		minecraftVersionRange {
			start = "${property("min_supported_version")}"
			end = "${property("max_supported_version")}"
		}

		requires {
			// Fabric API
			id = "P7dR8mSH"
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