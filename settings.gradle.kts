pluginManagement {
	repositories {
		maven("https://maven.fabricmc.net/")
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.kikugie.dev/snapshots")
	}
}

plugins {
	id("dev.kikugie.stonecutter") version "0.9"
}

stonecutter {
	create(rootProject) {
		fun controlledVersions(vararg versions: String) = versions.forEach {
			if (stonecutter.eval(it, ">=26.1")) {
				version(it).buildscript = "build.noremap.gradle.kts"
			} else {
				version(it).buildscript = "build.remap.gradle.kts"
			}
		}

		controlledVersions("1.21.6", "1.21.9", "26.1")
		vcsVersion = "26.1"
	}
}