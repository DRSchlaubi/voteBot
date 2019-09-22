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

package wtf.votebot.bot.config.backend

import com.bettercloud.vault.Vault
import com.bettercloud.vault.VaultConfig
import wtf.votebot.bot.config.Config
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation

/**
 * Loads data from [Vault](https://www.vaultproject.io/).
 */
@ConfigBackend.Priority(1)
class VaultBackend(
    private val config: Config
) : ConfigBackend {

    private val vault = Vault(
        VaultConfig()
            .address(config.vaultAddress)
            .token(config.vaultToken)
            .build()
    )

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(property: KProperty1<Config, *>) =
        vault.logical().read(
            config.vaultPath +
                    property.findAnnotation<VaultKey>()?.key
        ).data[config.environmentType().key] as T

    override fun requirementsMet() = !config.isDevelopment()
}

/**
 * The key of the environment variables.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class VaultKey(val key: String)
