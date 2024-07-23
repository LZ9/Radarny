import java.util.*

apply(plugin = "maven-publish")
apply(plugin = "signing")


val sourceSets = extensions.getByName("sourceSets") as SourceSetContainer

val androidSourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    println("sourceSets : $sourceSets")
//    from(sourceSets.getByName("main").java.srcDirs)
//    exclude("**/R.class")
//    exclude("**/BuildConfig.class")
}

val PUBLISH_GROUP_ID = "ink.lodz"
val PUBLISH_ARTIFACT_ID = "radarny"
val PUBLISH_VERSION = "1.0.3"

val SIGNING_KEYID = "signing.keyId"
val SIGNING_PASSWORD = "signing.password"
val SIGNING_SECRET_KEY_RING_FILE = "signing.secretKeyRingFile"
val OSSRH_USERNAME = "ossrhUsername"
val OSSRH_PASSWORD = "ossrhPassword"

var keyId = ""
var password = ""
var secretKeyRingFile = ""
var ossrhUsername = ""
var ossrhPassword = ""

val secretPropsFile = project.rootProject.file("local.properties")
if (secretPropsFile.exists()) {
    println("Found secret props file, loading props")
    val p = Properties()
    p.load(secretPropsFile.inputStream())
    p.forEach { (key, value) ->
        println("Key: $key, Value: $value")
        when(key){
            SIGNING_KEYID -> {keyId = value.toString()}
            SIGNING_PASSWORD -> {password = value.toString()}
            SIGNING_SECRET_KEY_RING_FILE -> {secretKeyRingFile = value.toString()}
            OSSRH_USERNAME -> {ossrhUsername = value.toString()}
            OSSRH_PASSWORD -> {ossrhPassword = value.toString()}
        }
    }
} else {
    println("No props file, loading env vars")
}

configure<PublishingExtension> {
    publications  {
        create<MavenPublication>("release") {
            groupId = PUBLISH_GROUP_ID
            artifactId = PUBLISH_ARTIFACT_ID
            version = PUBLISH_VERSION

            artifact(tasks["androidSourcesJar"])
            artifact("$buildDir/outputs/aar/${project.name}-release.aar")

            pom {
                name.set(PUBLISH_ARTIFACT_ID)
                description.set("Hi, this is a android base library")
                url.set("https://github.com/LZ9/Radarny")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("Lodz")
                        name.set("Lodz")
                        email.set("lz.mms@foxmail.com")
                    }
                }
                scm {
                    connection.set("scm:git@github.com:LZ9/Radarny.git")
                    developerConnection.set("scm:git:ssh://github.com/LZ9/Radarny.git")
                    url.set("https://github.com/LZ9/Radarny/tree/master")
                }
                withXml {
                    val dependenciesNode = asNode().appendNode("dependencies")

                    configurations.getByName("implementation").allDependencies.forEach {
                        val dependencyNode = dependenciesNode.appendNode("dependency")
                        dependencyNode.appendNode("groupId", it.group)
                        dependencyNode.appendNode("artifactId", it.name)
                        dependencyNode.appendNode("version", it.version)
                    }
                }
            }
        }
    }
    repositories {
        maven {
            name = "mavenCentral"
            val releasesRepoUrl = "https://s01.oss.sonatype.org/content/repositories/releases/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            var repoUrl = if (PUBLISH_VERSION.endsWith("SNAPSHOT")) {
                snapshotsRepoUrl
            } else {
                releasesRepoUrl
            }
            url = uri(repoUrl)

            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }
}

configure<SigningExtension> {
    val pubExt = checkNotNull(extensions.findByType(PublishingExtension::class.java))
    val publication = pubExt.publications["release"]
    sign(publication)
}


