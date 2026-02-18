plugins {
    id("java-library")
}

val legacyJava8 = (findProperty("legacyJava8") as String?)?.toBoolean() ?: false

allprojects {
    apply(plugin = "java-library")

    if (legacyJava8) {
        tasks.withType<JavaCompile>().configureEach {
            sourceCompatibility = "1.8"
            targetCompatibility = "1.8"
            options.encoding = "UTF-8"
        }
    }
}
