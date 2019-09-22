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

import wtf.votebot.bot.Logger
import wtf.votebot.bot.config.backend.ConfigBackend
import wtf.votebot.bot.exceptions.StartupError
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

/**
 * Loads and combines the data of multiple [ConfigBackend]s.
 */
@ExperimentalStdlibApi
class ConfigLoader(vararg backendClasses: KClass<out ConfigBackend>) {
    private val log = Logger.forEnclosingClass()
    private val values = mutableMapOf<String, Any>()

    init {
        val backends =
            backendClasses.filter { it.hasAnnotation<ConfigBackend.Priority>() }.toMutableList()
        backends.sortBy { it.findAnnotation<ConfigBackend.Priority>()?.priority }

        if (backends.isEmpty()) {
            log.atSevere()
                .log(
                    "[CONFIG] No suitable configuration " +
                            "backends found see log above for reasons!"
                )
            throw StartupError("No suitable configuration backends available.")
        }

        backends.forEach(this::loadValuesFromBackendClass)
    }

    private fun loadValuesFromBackendClass(backendClass: KClass<out ConfigBackend>) {
        val constructor = backendClass.constructors.firstOrNull()
        if (constructor == null || constructor.parameters.size > 1 || constructor.parameters
                .first().type.classifier != Config::class
        ) {
            return
        }
        val backend = constructor.call(load())
        if (!backend.requirementsMet())
            return
        Config::class.declaredMemberProperties.forEach {
            if (!it.hasAnnotation<ConfigKey>())
                return@forEach
            val cfgKey = it.findAnnotation<ConfigKey>()?.value
            val cfgValue = backend.get<Any>(it)
            if (cfgValue != null && !values.containsKey(it.name)) {
                values[it.name] = cfgValue
                log.atFinest()
                    .log("Loaded Config Value: %s from %s", cfgKey, backend::class.simpleName)
            }
        }
    }

    /**
     * Loads the config.
     * @return the loaded [Config]
     */
    fun load(): Config {
        val config = DefaultConfig()
        config::class.declaredMemberProperties.forEach {
            if (it is KMutableProperty<*>) {
                if (values.containsKey(it.name))
                    it.setter.call(config, values[it.name])
            }
        }
        return config
    }
}
