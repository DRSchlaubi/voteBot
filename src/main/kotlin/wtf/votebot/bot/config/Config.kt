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

/**
 * Config holds data that can be configured and loaded by multiple implementations.
 *
 * Currently supported:
 * - [ConfigCatConfig]
 * - [EnvConfig]
 */
interface Config {
    val environment: String
    val sentryDSN: String
    val discordToken: String
    val serviceName: String
    val httpPort: String

    /**
     * @return true if the current environment is a development environment.
     */
    fun development() = environmentType() == Environment.DEVELOPMENT

    /**
     * @return true if the current environment is a staging environment.
     */
    fun staging() = environmentType() == Environment.STAGING

    /**
     * @return true if the current environment is a production environment.
     */
    fun production() = environmentType() == Environment.PRODUCTION

    /**
     * @return the application [Environment].
     */
    fun environmentType() = Environment.valueOf(environment.toUpperCase())
}
