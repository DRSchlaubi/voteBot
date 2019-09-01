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
import io.sentry.Sentry
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import wtf.votebot.bot.config.ConfigCatConfig
import wtf.votebot.bot.config.EnvConfig

fun main(args: Array<String>) {
    System.setProperty(
        "flogger.backend_factory",
        "com.google.common.flogger.backend.slf4j.Slf4jBackendFactory#getInstance"
    )
    val log = FluentLogger.forEnclosingClass()

    // Parse CLI flags
    val options = Options()
        .addOption(Option("C", "config", true, "Configure the configuration backend."))
    val cli = DefaultParser().parse(options, args)

    // Load Config
    val configBackend = cli.getOptionObject("config")
    val config = if (configBackend == "env") EnvConfig() else ConfigCatConfig()

    // Initialize Sentry
    Sentry.init(config.sentryDSN)
}