repositories {
	maven("https://libraries.minecraft.net/")
	mavenCentral()
}

val legacyJava8 = (findProperty("legacyJava8") as String?)?.toBoolean() ?: false

dependencies {
	api(project(":api"))

	if (legacyJava8) {
		compileOnly("com.mojang:authlib:1.5.21")
	} else {
		compileOnly(libs.authlib)
	}

	compileOnly(files("lib/spigot-1.8.8.jar"))
}
