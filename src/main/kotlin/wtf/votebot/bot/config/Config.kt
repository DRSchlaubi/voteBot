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

import wtf.votebot.bot.config.backend.EnvKey
import wtf.votebot.bot.config.backend.VaultKey

interface Config {
    /**
     * Current environment this application is running in.
     */
    @ConfigKey("ENVIRONMENT")
    @EnvKey("ENVIRONMENT")
    val environment: String

    /**
     * Address of the vault instance.
     */
    @ConfigKey("VAULT_ADDRESS")
    @EnvKey("VAULT_ADDRESS")
    val vaultAddress: String

    /**
     * Vault access token.
     */
    @ConfigRequired
    @ConfigKey("VAULT_TOKEN")
    @EnvKey("VAULT_TOKEN")
    val vaultToken: String?

    @ConfigKey("VAULT_PATH")
    @EnvKey("VAULT_PATH")
    val vaultPath: String

    /**
     * [Sentry](http://sentry.io/) DSN.
     */
    @ConfigKey("SENTRY_DSN")
    @EnvKey("SENTRY_DSN")
    @VaultKey("sentry_dsn")
    val sentryDSN: String?

    /**
     * Discord token of the bot.
     */
    @ConfigRequired
    @ConfigKey("DISCORD_TOKEN")
    @EnvKey("DISCORD_TOKEN")
    @VaultKey("discord_token")
    val discordToken: String?

    /**
     * HTTP port of the embedded WebServer.
     */
    @ConfigRequired
    @ConfigKey("HTTP_PORT")
    @EnvKey("HTTP_PORT")
    val httpPort: String

    /**
     * @return true if the current environment is a development environment.
     */
    fun isDevelopment() = environmentType() == Environment.DEVELOPMENT

    /**
     * @return true if the current environment is a staging environment.
     */
    fun isStaging() = environmentType() == Environment.STAGING

    /**
     * @return true if the current environment is a production environment.
     */
    fun isProduction() =
        environmentType() == Environment.PRODUCTION || !isDevelopment() && !isStaging()

    /**
     * @return the application [Environment].
     */
    fun environmentType() = Environment.valueOf(environment.toUpperCase())
}
