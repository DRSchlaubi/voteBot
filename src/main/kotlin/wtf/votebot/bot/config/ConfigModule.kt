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

import com.google.inject.AbstractModule
import com.google.inject.name.Names
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

/**
 * [AbstractModule] implementation that provides config values
 * for usage in an [com.google.inject.Injector].
 */
@ExperimentalStdlibApi
class ConfigModule(private val config: Config) : AbstractModule() {

    override fun configure() {
        bind(Config::class.java).toInstance(config)
        bindConfigValues()
    }

    @Suppress("UNCHECKED_CAST")
    private fun bindConfigValues() {
        config::class.declaredMemberProperties.filter { it.hasAnnotation<ConfigKey>() }
            .forEach { prop ->
                val property = prop as KProperty1<Any, *>
                val annotation = property.findAnnotation<ConfigKey>()
                if (property.returnType.classifier == Int::class) {
                    bind(Int::class.java)
                        .annotatedWith(Names.named(annotation?.value))
                        .toInstance(property.get(config) as Int)
                } else if (property.returnType.classifier == String::class) {
                    bind(String::class.java)
                        .annotatedWith(Names.named(annotation?.value))
                        .toInstance(property.get(config) as String)
                }
            }
    }
}
