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
import wtf.votebot.bot.Logger
import java.nio.file.Files
import java.nio.file.Path

@Suppress("UNCHECKED_CAST", "SpellCheckingInspection")
@ConfigBackendLead
class EnvConfig(@Suppress("UNUSED") mainConfig: ConfigBackend?) : ConfigBackend {

    private val log = Logger.forEnclosingClass()
    private val dotenv by lazy { dotenv() }

    override fun <T> get(key: String) = dotenv[key] as T

    override fun requirementsMet(): Boolean {
        if (!Files.exists(Path.of(".env"))) {
            log.atSevere().log("[CONFIG] Place make sure you placed a .env file in the bot's root directory.")
            return false
        }
        return true
    }

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