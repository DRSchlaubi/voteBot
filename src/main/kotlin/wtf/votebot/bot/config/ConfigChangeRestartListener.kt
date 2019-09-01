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

import com.configcat.ConfigurationChangeListener
import com.configcat.ConfigurationParser
import com.google.common.flogger.FluentLogger
import kotlin.system.exitProcess

/**
 * An implementation of [ConfigurationChangeListener] that restarts the bot whenever the config changes.
 */
class ConfigChangeRestartListener : ConfigurationChangeListener {

    private val log = FluentLogger.forEnclosingClass()
    private var initializationDone = false

    override fun onConfigurationChanged(parser: ConfigurationParser, newConfiguration: String) {
        if (!initializationDone) {
            initializationDone = true
            log.atInfo().log("Config loaded.")
            return
        }
        log.atInfo().log("Config got updated.")
        if (parser.parseValue(String::class.java, newConfiguration, "environment") != "development") {
            log.atWarning().log("Restart...")
            exitProcess(0)
        }
    }
}
