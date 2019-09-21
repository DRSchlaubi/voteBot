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

import io.github.cdimascio.dotenv.dotenv
import wtf.votebot.bot.exceptions.StartupError

/**
 * A [Config] implementation that loads data from an env file or environment variables.
 */
class EnvConfig : Config {

    private val dotenv = dotenv()

    override val environment: String
        get() = dotenv[ENVIRONMENT] ?: notFound(ENVIRONMENT)
    override val sentryDSN: String
        get() = dotenv[SENTRY_DSN] ?: notFound(SENTRY_DSN)
    override val discordToken: String
        get() = dotenv[DISCORD_TOKEN] ?: notFound(DISCORD_TOKEN)
    override val serviceName: String
        get() = dotenv[SERVICE_NAME] ?: "bot"
    override val httpPort: String
        get() = dotenv[HTTP_PORT] ?: "3245"

    private fun notFound(option: String): Nothing = throw StartupError(
        "Could not find $option in .env file." +
                "Please make sure to include all options from the example"
    )

    companion object {
        private const val BASE = "BOT_"

        /**
         * Environment variable key for the bot environment.
         */
        const val ENVIRONMENT = "${BASE}ENVIRONMENT"
        /**
         * Environment variable key for the Discord API token.
         */
        const val DISCORD_TOKEN = "${BASE}DISCORD_TOKEN"
        /**
         * Environment variable key for the sentry dsn.
         */
        const val SENTRY_DSN = "${BASE}SENTRY_DSN"

        /**
         * Name that is used for service registry.
         */
        const val SERVICE_NAME = "${BASE}SERVICE_NAME"

        /**
         * POrt of the embedded web server.
         */
        const val HTTP_PORT = "${BASE}HTTP_PORT"
    }
}
