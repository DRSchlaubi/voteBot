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
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaConstructor

class ConfigLoader(
    configuration: KClass<Config>,
    vararg backendClasses: KClass<out ConfigBackend>
) {
    private val log = Logger.forEnclosingClass()
    private val values= mutableMapOf<String, Any>()

    init {
        val backends = instanceBackends(backendClasses)
        val availableBackends: List<ConfigBackend> = backends.filter(ConfigBackend::requirementsMet).toList()

        if (availableBackends.isEmpty()) {
            log.atSevere().log("[CONFIG] No suitable configuration backends found see log above for reasons!")
            TODO("Implement custom exception")
        }

        log.atInfo().log("[CONFIG] Determined following suitable configurations backends: %s (%s)",
            availableBackends.first(), // main backend
            availableBackends.subList(1, availableBackends.size) // fallbacks
        )

        val configKeys = findConfigKeys(configuration).toMutableList()

        val missingKeys = mutableListOf<String>()
        for(key in configKeys) {
            for (backend in availableBackends) {
                values[key.name] = backend[key.name] ?: continue
                break // End loop as value was set
            }
            if (!values.containsKey(key.name) and key.required) {
                missingKeys.add(key.name)
            }
        }
        log.atSevere().log("[CONFIG] No config backend could provide required config keys: %s!", missingKeys)
        TODO("Implement custom exception")
    }

    // TODO: Find better implementation for this (Guice?)
    fun buildConfig(): Config {
        return ImmutableConfig::class.primaryConstructor!!.call(values.values.toTypedArray())
    }

    private fun instanceBackends(backendClasses: Array<out KClass<out ConfigBackend>>): List<ConfigBackend> {
        fun validateConstructor(constructor: KFunction<ConfigBackend>): Boolean { // The only allowed parameter is for the mainBackend (ConfigBackend)
            if (constructor.parameters.isEmpty()) {
                return false
            }

            if (constructor.parameters.first().type.classifier != ConfigBackend::class) {
                return false
            }
            return true
        }
        val constructors = backendClasses
            .asSequence()
            .mapNotNull {
                it.constructors
                    .firstOrNull { constructor -> constructor.findAnnotation<ConfigBackendConstructor>() != null } // Search @ConfigBackendConstructor
                    ?: it.primaryConstructor // Error
            }
            .filter(::validateConstructor)
        val mainConstructor = constructors.firstOrNull() ?: return emptyList()

        if (!mainConstructor.javaConstructor!!.declaringClass.isAnnotationPresent(ConfigBackendLead::class.java)) {
            log.atSevere().log("[CONFIG] It seems like there is no main backend! See log above for errors!")
            TODO("Implement custom exception")
        }
        val mainBackend = mainConstructor.call(null)
        return constructors
            .drop(1) // Drop mainConstructor
            .map {
                it.call(mainBackend)
            } // Build backends
            .plus(mainBackend)
            .toList()

    }

    private fun findConfigKeys(configuration: KClass<Config>) = configuration.declaredMemberProperties
            .asSequence()
            .filter { (it.visibility == KVisibility.PUBLIC) and (it.findAnnotation<ConfigIgnore>() == null) } // filter out non public or ignored keys
            .map {
                val name = it.findAnnotation<ConfigKey>()?.value ?: it.name
                val required = it.findAnnotation<ConfigRequired>() != null
                ConfigEntry(name, required)
            } // Search for annotations
            .toList() //Grab results
}

private data class ConfigEntry(val name: String, val required: Boolean)