plugins {
    `java-library`
    `maven-publish`
    signing
}

group = "eu.lestard"
version = "0.6.0"

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.withType<Javadoc> {
    (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("jakarta.inject:jakarta.inject-api:2.0.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.assertj:assertj-core:3.22.0")
}


val projectName: String by project
val projectDescription: String by project
val projectUrl: String by project
val projectScm: String by project
val projectLicenseName: String by project
val projectLicenseUrl: String by project
val projectLicenseDistribution: String by project
val projectDeveloperName: String by project

publishing {
    publications {
        create<MavenPublication>(projectName) {
            artifactId = projectName
            from(components["java"])

            pom {
                name.set(projectName)
                description.set(projectDescription)
                url.set(projectUrl)
                licenses {
                    license {
                        name.set(projectLicenseName)
                        url.set(projectLicenseUrl)
                        distribution.set(projectLicenseDistribution)
                    }
                }
                developers {
                    developer {
                        id.set(projectDeveloperName)
                        name.set(projectDeveloperName)
                    }
                }
                scm {
                    connection.set(projectScm)
                    developerConnection.set(projectScm)
                    url.set(projectUrl)
                }
            }
        }
    }

    repositories {
        maven {
            val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)

            val sonatypeUsername: String by project
            val sonatypePassword: String by project

            credentials {
                username = sonatypeUsername
                password = sonatypePassword
            }
        }
    }
}

signing {
    sign(publishing.publications[projectName])
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}
