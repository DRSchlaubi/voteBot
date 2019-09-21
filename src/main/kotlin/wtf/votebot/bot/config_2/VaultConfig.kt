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

import com.bettercloud.vault.Vault
import com.bettercloud.vault.VaultConfig

class VaultConfig(
    token: String,
    kvPath: String,
    address: String
) {

    private val vault = Vault(
        VaultConfig()
            .address(address)
            .token(token)
            .build()
    )

    val discordToken: String?

    init {
        val listRes = vault.logical().list(kvPath)
        println(listRes)
        val res = vault.logical().read(kvPath)
        val body = res.restResponse.body.toString(Charsets.UTF_8)
        println(vault.logical().write(kvPath, mutableMapOf<String, Any>("abc" to "adawd")).data)
        discordToken = vault.logical().read(kvPath).data["discord_token"]
    }
    val sentryDSN: String? = vault.logical().read(kvPath).data["sentry_dsn"]
}