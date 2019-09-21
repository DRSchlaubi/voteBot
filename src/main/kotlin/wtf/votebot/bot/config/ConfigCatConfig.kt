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

package wtf.votebot.bot.config

import com.configcat.AutoPollingPolicy
import com.configcat.ConfigCatClient
import com.google.common.flogger.FluentLogger
import io.github.cdimascio.dotenv.dotenv
import kotlin.system.exitProcess

/**
 * A [Config] implementation that loads data from https://configcat.com
 */
class ConfigCatConfig(configCatKey: String? = dotenv()["BOT_CONFIG_CAT_KEY"]) : Config {
    private val log = FluentLogger.forEnclosingClass()

    override val environment: String
    override val sentryDSN: String
    override val discordToken: String
    override val serviceName: String
    override val httpPort: String

    init {
        if (configCatKey == null) {
            log.atSevere()
                .log(
                    "ConfigCat API key is not set as environment variable." +
                            "Please make sure you set it or enable the environment" +
                            "variable configuration backend."
                )
            exitProcess(1)
        }
        val client = ConfigCatClient.newBuilder()
            .refreshPolicy { configFetcher, cache ->
                AutoPollingPolicy.newBuilder()
                    .autoPollIntervalInSeconds(60)
                    .configurationChangeListener(ConfigChangeRestartListener())
                    .build(configFetcher, cache)
            }
            .build(configCatKey)
        environment = client.getValue(String::class.java, "environment", "production")
        sentryDSN = client.getValue(String::class.java, "sentry_dsn", null)
        discordToken = client.getValue(String::class.java, "discord_token", null)
        serviceName = client.getValue(String::class.java, "service_name", "bot")
        httpPort = client.getValue(String::class.java, "http_port", "3245")
    }
}
