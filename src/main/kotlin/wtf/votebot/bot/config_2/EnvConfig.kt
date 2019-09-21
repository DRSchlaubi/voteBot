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

package wtf.votebot.bot.config_2

import io.github.cdimascio.dotenv.dotenv

/**
 * A [Config] implementation that loads data from an env file or environment variables.
 */
class EnvConfig {

    private val dotenv = dotenv()

    val vaultAddress: String? = dotenv[VAULT_ADDRESS]
    val vaultPath: String? = dotenv[VAULT_PATH]
    val vaultToken: String? = dotenv[VAULT_TOKEN]
    val environment: String? = dotenv[ENVIRONMENT]
    val sentryDSN: String? = dotenv[SENTRY_DSN]
    val discordToken: String? = dotenv[DISCORD_TOKEN]
    val httpPort: String? = dotenv[HTTP_PORT]

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
         * Port of the embedded web server.
         */
        const val HTTP_PORT = "${BASE}HTTP_PORT"

        const val VAULT_ADDRESS = "${BASE}VAULT_ADDRESS"
        const val VAULT_PATH = "${BASE}VAULT_PATH"
        const val VAULT_TOKEN = "${BASE}VAULT_TOKEN"
    }
}
