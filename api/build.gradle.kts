repositories {
	maven("https://libraries.minecraft.net/")
	maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

val legacyJava8 = (findProperty("legacyJava8") as String?)?.toBoolean() ?: false

dependencies {
	if (legacyJava8) {
		compileOnly(files(rootProject.file("v1_8_8/lib/spigot-1.8.8.jar")))
	} else {
		compileOnly(libs.authlib) {
			isTransitive = false
		}

		compileOnly("org.spigotmc:spigot-api:1.20.6-R0.1-SNAPSHOT") {
			exclude("com.google.guava", "guava")
			exclude("com.google.code.gson", "gson")
			exclude("junit", "junit")
			exclude("net.md-5")
			exclude("org.yaml")
		}
	}
}
