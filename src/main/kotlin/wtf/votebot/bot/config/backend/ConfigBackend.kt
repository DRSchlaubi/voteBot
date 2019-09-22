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

import wtf.votebot.bot.config.Config
import kotlin.reflect.KProperty1

/**
 * ConfigBackend enables loading configuration
 * data from multiple data sources.
 *
 * Implementations are:
 * - [EnvBackend]
 * - [VaultBackend]
 */
interface ConfigBackend {

    /**
     * @return the value of the matchin data or ```null``` if the key is not set.
     */
    operator fun <T> get(property: KProperty1<Config, *>): T?

    /**
     * @return whether this config backend is currently usable or not.
     */
    fun requirementsMet(): Boolean

    /**
     * Enables the loading of [ConfigBackend]s in a particular order.
     */
    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Priority(val priority: Int)
}
