/*
 * VoteBot - A unique Discord bot for surveys
 *
 * Copyright (C) 2019  Michael Rittmeister
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

package me.schlaubi.votebot.commands

import cc.hawkbot.regnum.client.Regnum
import cc.hawkbot.regnum.client.command.ICommand
import me.schlaubi.votebot.commands.general.InfoCommand
import me.schlaubi.votebot.commands.settings.SettingsCommand
import me.schlaubi.votebot.commands.vote.*
import me.schlaubi.votebot.core.VoteBot

/**
 * Container of all commands.
 */
class CommandContainer(
    private val regnum: Regnum,
    bot: VoteBot
) {
    // Register all commands
    init {
        register(InfoCommand())
        register(SettingsCommand(bot))
        register(QuickCreateCommand(bot))
        register(VoteCommand(bot))
        register(CloseCommand(bot))
        register(StatusCommand(bot))
        register(CreateCommand(bot))
        register(ChangeHeadingCommand(bot))
        register(AddOptionCommand(bot))
        register(RemoveOptionCommand(bot))
    }

    private fun register(command: ICommand) {
        regnum.commandParser.registerCommand(command)
    }
}