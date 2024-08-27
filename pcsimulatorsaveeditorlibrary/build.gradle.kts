plugins {
    id("java")
}

group = "com.mokkachocolata"
version = "v1.1"

sourceSets {
    main {
        java {
            srcDir("src")
        }
    }
}

dependencies {
    implementation(files("libs/ChmWeb-0.5.4.jar"))
}