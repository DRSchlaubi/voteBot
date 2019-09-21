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

import wtf.votebot.bot.exceptions.StartupError

class ConfigLoader(
    envConfig: EnvConfig,
    vaultConfig: VaultConfig?
) : Config {

    override val environment = envConfig.environment ?: ConfigDefaults.ENVIRONMENT
    override val httpPort = envConfig.httpPort ?: ConfigDefaults.HTTP_PORT
    override val sentryDSN = vaultConfig?.sentryDSN ?: envConfig.sentryDSN
    override val discordToken = vaultConfig?.discordToken ?: envConfig.discordToken ?: requiredNotFound("DiscordToken")

    companion object {
        fun requiredNotFound(option: String): Nothing = throw StartupError(
            "Could not find $option in any of the config backends. " +
                    "Please make sure to include all required options."
        )
    }
}
