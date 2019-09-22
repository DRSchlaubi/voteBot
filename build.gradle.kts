/*
 * Votebot - A feature-rich bot to create votes on Discord guilds.
 *
 * Copyright (C) 2019  Michael Rittmeister & Yannick Seeger & Daniel Scherf
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.apache.tools.ant.filters.ReplaceTokens
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    pmd
    checkstyle
    id("com.github.spotbugs") version "2.0.0"
    id("com.github.johnrengelman.shadow") version "5.1.0"
    id("io.gitlab.arturbosch.detekt") version "1.0.1"
    java
    application
    kotlin("jvm") version "1.3.50"
}

group = "wtf.votebot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {

    implementation("com.google.flogger", "flogger", "0.4")
    implementation("com.google.flogger", "flogger-system-backend", "0.4")
    runtime("com.google.flogger", "flogger-slf4j-backend", "0.4")
    implementation("ch.qos.logback", "logback-classic", "1.2.3")
    implementation("com.google.inject", "guice", "4.1.0")
    implementation("dev.misfitlabs.kotlinguice4", "kotlin-guice", "1.4.0")
    implementation("com.orbitz.consul", "consul-client", "1.3.7")
    implementation("com.bettercloud", "vault-java-driver", "5.0.0")
    implementation("com.configcat", "configcat-java-client", "1.2.0")
    implementation("commons-cli", "commons-cli", "1.4")
    implementation("com.discord4j", "discord4j-core", "3.0.8")
    implementation("io.sentry", "sentry", "1.7.27")
    implementation("io.sentry", "sentry-logback", "1.7.27")
    implementation("io.github.cdimascio", "java-dotenv", "5.1.1")
    implementation("io.ktor:ktor-server-netty:1.2.3")

    detektPlugins("io.gitlab.arturbosch.detekt", "detekt-formatting", "1.0.1")
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    testCompile("junit", "junit", "4.12")
}

tasks {
    val replaceTokens = task<Copy>("replaceTokens") {
        from("src/main/java") {
            include("**/ApplicationInfo.java")
            val tokens = mapOf(
                "releaseVersion" to (project.version as? String ?: "UnknownVersion")
            )
            filter<ReplaceTokens>(mapOf("tokens" to tokens))
        }
        into("build/filteredSrc")
        includeEmptyDirs = false
    }

    val replaceTokensInSource = task<SourceTask>("replaceTokensInSource") {
        val javaSources = sourceSets["main"].allJava.filter {
            it.name != "ApplicationInfo.java"
        }
            .asFileTree
        source = javaSources + fileTree(replaceTokens.destinationDir)
        dependsOn(replaceTokens)
    }

    compileJava {
        source = replaceTokensInSource.source
        dependsOn(replaceTokensInSource)
    }

    "shadowJar"(ShadowJar::class) {
        archiveBaseName.set("shadow")
        archiveVersion.set(project.version as? String ?: "UnknownVersion")
    }

    checkstyle {
        checkstyleTest.get().enabled = false
    }

    checkstyleMain {
        configFile = file("$rootDir/config/checkstyle/google_checks.xml")
        configProperties = mapOf("config_loc" to "${rootProject.projectDir}/config/checkstyle")
    }

    spotbugs {
        toolVersion = "4.0.0-beta1"
        isIgnoreFailures = true
    }

    spotbugsMain {
        reports {
            html.isEnabled = true
            xml.isEnabled = false
        }
    }

    pmd {
        pmdTest.get().enabled = false
    }

    pmdMain {
        ignoreFailures = true
        ruleSetConfig =
            resources.text.fromFile(file("${rootProject.projectDir}/config/pmd/ruleset.xml"))
    }
}

detekt {
    input = files("src/main/kotlin")
    autoCorrect = true
}

application {
    mainClassName = "wtf.votebot.bot.LauncherKt"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_12
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "12"
}