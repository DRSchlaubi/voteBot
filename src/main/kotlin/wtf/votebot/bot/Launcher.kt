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

package wtf.votebot.bot

import com.google.common.flogger.FluentLogger
import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.sentry.Sentry
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import wtf.votebot.bot.config.ConfigLoader
import wtf.votebot.bot.config.backend.EnvBackend
import wtf.votebot.bot.config.backend.VaultBackend
import wtf.votebot.bot.core.ServiceRegistry
import wtf.votebot.bot.core.module
import kotlin.system.exitProcess

private val options = Options()
    .addOption(
        Option.builder("H")
            .longOpt("help")
            .desc("Display a list of all CLI options")
            .build()
    )
    .addOption(
        Option.builder("FSR")
            .longOpt("force-service-registry")
            .desc("Forces the service to use a service registry (also development)")
            .build()
    )

/**
 * Application entry point.
 */
@ExperimentalStdlibApi
fun main(args: Array<String>) {
    System.setProperty(
        "flogger.backend_factory",
        "com.google.common.flogger.backend.slf4j.Slf4jBackendFactory#getInstance"
    )

    // Parse CLI flags
    val cli = DefaultParser().parse(options, args)
    if (cli.hasOption("H")) {
        sendHelp()
        exitProcess(0)
    }

    val configLoader = ConfigLoader(EnvBackend::class, VaultBackend::class)
    val config = configLoader.build()

    // Initialize Sentry
    Sentry.init(config.sentryDSN)
    Sentry.getStoredClient().environment = config.environment
    Sentry.getStoredClient().release = ApplicationInfo.RELEASE

    // WebServer
    embeddedServer(Netty, config.httpPort.toInt(), module = Application::module).start()

    // Service Registry
    if (!config.isDevelopment() || cli.hasOption("FSR")) {
        ServiceRegistry(ApplicationInfo.SERVICE_NAME, config.httpPort)
    }
}

private fun parseCliOptions(args: Array<String>): CommandLine {
    val cli: CommandLine
    try {
        cli = DefaultParser().parse(options, args)
    } catch (e: ParseException) {
        FluentLogger.forEnclosingClass().atSevere().withCause(e).log("Could not parse CLI options")
        sendHelp()
        exitProcess(1)
    }
    return cli
}

/**
 * Sends a formatted message of all CLI options.
 */
private fun sendHelp() = HelpFormatter().printHelp("[options]", options)
