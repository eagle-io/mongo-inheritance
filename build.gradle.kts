plugins {
    java
    `maven-publish`
}

group = "io.eagle"
version = "1.0.10"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.data:spring-data-mongodb:3.2.3")
    implementation("org.mongodb:mongodb-driver-sync:4.2.3")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.5.3")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/eagle-io/mongo-inheritance")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
}
