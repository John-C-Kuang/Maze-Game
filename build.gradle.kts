import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"

    id("org.openjfx.javafxplugin") version "0.0.13"
}

group = "org.example"
version = "1.0-SNAPSHOT"

sourceSets {
    test {
        java {
            srcDirs("test")
        }
    }

    main {
        java {
            srcDirs("Maze")
        }
        resources {
            srcDirs("resources")
        }
    }

}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.mockito:mockito-all:1.10.19")

    testImplementation(kotlin("test"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("com.google.code.gson:gson:2.9.1")

    runtimeOnly("org.jetbrains.kotlin:kotlin-runtime:1.2.71")
    runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:1.7.0")

    runtimeOnly("org.openjfx:javafx-base:18.0.2:linux")
    runtimeOnly("org.openjfx:javafx-graphics:18.0.2:linux")
    runtimeOnly("org.openjfx:javafx-controls:18.0.2:linux")
    runtimeOnly("org.openjfx:javafx-fxml:18.0.2:linux")
    //runtimeOnly("org.openjfx:javafx-media:18.0.2:linux")
    //runtimeOnly("org.openjfx:javafx-swing:18.0.2:linux")
    //runtimeOnly("org.openjfx:javafx-web:18.0.2:linux")
}

tasks.test {
    useJUnit()
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = "MainKt"
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory()) it else zipTree(it) })

    destinationDirectory.set(File("10/Other"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.apiVersion = "1.6"
}

javafx {
    version = "19"
    modules = mutableListOf("javafx.controls", "javafx.fxml" , "javafx.base")
        //,"javafx.media", "javafx.graphics",
        //"javafx.swing", "javafx.web")
}