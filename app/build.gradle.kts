/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    id("com.mokkachocolata.pcsimulatorsaveeditor.java-application-conventions")
}

dependencies {
    implementation(files("libs/swt.jar"))
    implementation(files("libs/ChmWeb-0.5.4.jar"))
    implementation("com.intellij:forms_rt:7.0.3")
}


application {
    // Define the main class for the application.
    mainClass.set("com.mokkachocolata.pcsimulatorsaveeditor.app.MainGUI")
}