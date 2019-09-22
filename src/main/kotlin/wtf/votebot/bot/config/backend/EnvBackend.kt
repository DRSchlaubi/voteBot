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

import io.github.cdimascio.dotenv.dotenv
import wtf.votebot.bot.config.Config
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation

/**
 * Loads data from an .env file or from environment variables.
 */
@ConfigBackend.Priority(0)
class EnvBackend(private val config: Config) : ConfigBackend {

    // Prefix in front of the environment variable keys. Ex: BOT_DISCORD_TOKEN
    private val prefix = "BOT_"

    private val dotenv by lazy {
        dotenv {
            ignoreIfMalformed = true
            ignoreIfMissing = true
        }
    }

    @ExperimentalStdlibApi
    @Suppress("UNCHECKED_CAST")
    override fun <T> get(property: KProperty1<Config, *>) =
        dotenv[prefix + property.findAnnotation<EnvKey>()?.key] as T

    override fun requirementsMet() = true // We don't have any special requirements.
}

/**
 * The key of the environment variables.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnvKey(val key: String)
