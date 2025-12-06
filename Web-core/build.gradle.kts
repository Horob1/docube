plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("java-library")
    id("maven-publish")
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.horob1"
version = "0.0.1-SNAPSHOT"
description = "Web core lib for Horob1 Docube"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
    withJavadocJar()
}
repositories {
    mavenCentral()
    mavenLocal()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.6")
    }
}

dependencies {
    api("com.horob1:common:0.0.1-SNAPSHOT")
    api("org.springframework.boot:spring-boot-starter-web")
    // Implementation dependencies - internal only
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.security:spring-security-test")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.named<Jar>("jar") {
    enabled = true
    archiveClassifier.set("")
    // Exclude application configuration files if any
    exclude("application*.properties", "application*.yml", "application*.yaml")
}

// Publish configuration
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = project.group.toString()
            artifactId = "web_core"
            version = project.version.toString()

            pom {
                name.set("Horob1 Web Core Library")
                description.set("Web Core shared classes for Horob1 services")
                url.set("https://github.com/horob1/docub-web_core")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("horob1")
                        name.set("Horob1 Team")
                    }
                }
            }
        }
    }

    repositories {
        mavenLocal()
        // Uncomment and configure for publishing to remote repository
        // maven {
        //     name = "GitHubPackages"
        //     url = uri("https://maven.pkg.github.com/horob1/docub-web_core")
        //     credentials {
        //         username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
        //         password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        //     }
        // }
    }
}