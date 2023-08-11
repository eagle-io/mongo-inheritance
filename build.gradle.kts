plugins {
    java
}

group = "io.eagle"
version = "1.0.0-SNAPSHOT"

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
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
}
