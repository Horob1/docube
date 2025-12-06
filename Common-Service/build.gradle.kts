plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("java-library")
    id("maven-publish")
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.horob1"
version = "0.0.1-SNAPSHOT"
description = "Common lib for Horob1 Docub"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.6")
    }
}

dependencies {
    api("io.jsonwebtoken:jjwt-api:0.12.6")

    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // API dependencies - exposed to consumers
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springframework.boot:spring-boot-starter-security")

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
            artifactId = "common"
            version = project.version.toString()

            pom {
                name.set("Horob1 Common Library")
                description.set("Common shared classes for Horob1 services")
                url.set("https://github.com/horob1/docub-common")

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
        //     url = uri("https://maven.pkg.github.com/horob1/docub-common")
        //     credentials {
        //         username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
        //         password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        //     }
        // }
    }
}