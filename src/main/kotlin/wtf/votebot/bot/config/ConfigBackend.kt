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

interface ConfigBackend {

    /**
     * @return the value corresponding to the [key] or ```null``` if the key is not set.
     */
    operator fun <T> get(key: String): T?

    /**
     * @return the value corresponding to the [key] or [default] if the key is not set.
     * @see ConfigBackend.get
     */
    fun <T> getOrDefault(key: String, default: T) = get(key) ?: default

    /**
     * @return whether this config backend is currently usable or not.
     */
    fun requirementsMet(): Boolean
}