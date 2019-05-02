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

package me.schlaubi.votebot.commands.vote

import cc.hawkbot.regnum.client.command.Group
import cc.hawkbot.regnum.client.command.context.Arguments
import cc.hawkbot.regnum.client.command.context.Context
import cc.hawkbot.regnum.client.command.permission.CommandPermissions
import cc.hawkbot.regnum.client.util.EmbedUtil
import me.schlaubi.votebot.VOTE
import me.schlaubi.votebot.commands.VoteBotCommand
import me.schlaubi.votebot.core.VoteBot
import me.schlaubi.votebot.entities.Vote

class CloseCommand(bot: VoteBot) : VoteBotCommand(
    bot,
    Group.VOTE,
    "Close",
    arrayOf("close", "delete", "remove"),
    CommandPermissions(public = true, node = "close"),
    "[messageId]",
    description = "Closes a vot manually"
) {
    override fun execute(args: Arguments, context: Context) {
        val messageId = args.array.getOrNull(0)
        // Check if user wants to create his own vote and if it exists
        if (messageId == null) {
            hasVote(context) {
                closeVote(bot.voteCache.getVoteByMember(context.member)!!, context)
            }
        } else {
            // Check if message id is numeric
            validateLong(context, args[0]) {
                // Check if user is permitted
                if (!context.regnumUser().hasPermission(
                        CommandPermissions(
                            serverAdminExclusive = true,
                            node = "close.admin"
                        ), context.guild.idLong
                    )
                ) {
                    return@validateLong context.sendMessage(
                        EmbedUtil.error(
                            context.translate("command.close.permission.title"),
                            context.translate("command.close.permission.description")
                        )
                    ).queue()
                }
                // Verify that vote exists
                bot.voteCache.getVoteByMessage(messageId.toLong(), context.guild.idLong) ?: return@validateLong context.sendMessage(
                    EmbedUtil.error(
                        context.translate("vote.invalid.title"),
                        context.translate("vote.invalid.description")
                    )
                ).queue()
            }
        }
    }

    private fun closeVote(
        vote: Vote,
        context: Context
    ) {
        // Delete vote and catch for dupe error
        try {
            vote.controller.deleteVote()
        } catch (e: IllegalArgumentException) {
            return context.sendMessage(
                EmbedUtil.warn(
                    context.translate("vote.error.dupe.title"),
                    context.translate("vote.error.dupe.description")
                )
            ).queue()

        }
        // Confirm deletion
        context.sendMessage(
            EmbedUtil.success(
                context.translate("vote.closed.title"),
                context.translate("vote.closed.description")
            )
        ).queue()
    }
}
