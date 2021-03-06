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

package wtf.votebot.bot.core

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondTextWriter
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat

/**
 * Configuration for Ktor.
 */
fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Routing) {
        get("/") {
            context.response.status(HttpStatusCode.OK)
        }
        get("/metrics") {
            val includedParam = context.request.queryParameters["name[]"]
            context.response.status(HttpStatusCode.OK)
            context.respondTextWriter {
                TextFormat.write004(
                    this, CollectorRegistry.defaultRegistry
                        .filteredMetricFamilySamples(
                            if (includedParam == null) emptySet() else setOf(
                                includedParam
                            )
                        )
                )
            }
        }
    }
}
