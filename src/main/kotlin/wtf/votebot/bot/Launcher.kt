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

import com.configcat.AutoPollingPolicy
import com.configcat.ConfigCatClient
import com.google.common.flogger.FluentLogger
import com.orbitz.consul.Consul
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import wtf.votebot.bot.io.ConfigBuilder
import wtf.votebot.bot.io.ConfigChangeListener


fun main(args: Array<String>) {
    // Use the slf4j backend for flogger
    System.setProperty(
        "flogger.backend_factory",
        "com.google.common.flogger.backend.slf4j.Slf4jBackendFactory#getInstance"
    )

    val log = FluentLogger.forEnclosingClass()

    // CLI
    val options = Options()
        .addOption(Option("D", "dev", false, "Enabled developer mode"))
    val cli = DefaultParser().parse(options, args)

    val devEnabled = cli.hasOption("dev")
    val config = ConfigBuilder()
    config.devEnabled = devEnabled

    if (devEnabled) {
        log.atInfo().log("Launching in developer mode!")
        return
    }

    val client = Consul.builder().build()
    val configCatKey = client.keyValueClient().getValueAsString("configcat/key")
        .orElseThrow {
            RuntimeException(
                "ConfigCat key not found or empty. Please make sure you added a ConfigCat key" +
                " to Consul or enable developer mode") }
    ConfigCatClient.newBuilder()
        .refreshPolicy { configFetcher, cache ->
            AutoPollingPolicy.newBuilder()
                .autoPollIntervalInSeconds(60)
                .configurationChangeListener(ConfigChangeListener())
                .build(configFetcher, cache)
        }
        .build(configCatKey)
}