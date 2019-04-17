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

package me.schlaubi.votebot.core

import cc.hawkbot.regnum.client.util.EmbedUtil
import cc.hawkbot.regnum.client.util.Misc
import cc.hawkbot.regnum.client.util.TranslationUtil
import me.schlaubi.votebot.entities.Vote
import me.schlaubi.votebot.identifier
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent

@Suppress("unused")
class VoteExecutor(
    private val bot: VoteBot
) {

    @SubscribeEvent
    private fun voteReceived(event: GuildMessageReactionAddEvent) {
        runChecks(event.messageIdLong, event.guild.idLong) {
            if (event.member == event.guild.selfMember) {
                return@runChecks
            }
            val user = event.user
            event.reaction.removeReaction(user).queue()
            if (user.isBot) {
                return@runChecks
            }
            val index = it.emoteMapping[event.reactionEmote.name] ?: return@runChecks
            try {
                it.controller.addVote(event.user, index)
            } catch (e: IllegalArgumentException) {
                user.openPrivateChannel().queue { channel ->
                    if (e.message == "This option does not exist") {
                        channel.sendMessage(
                            EmbedUtil.error(
                                translate(user, "vote.invalid.option.title"),
                                translate(user, "vote.invalid.option.description")
                            ).build()
                        ).queue()

                    } else {
                        channel.sendMessage(
                            EmbedUtil.warn(
                                translate(user, "vote.same.title"),
                                translate(user, "vote.same.description")
                            ).build()
                        ).queue()
                    }
                }
            } catch (e: IllegalStateException) {
                user.openPrivateChannel().queue { channel ->
                    @Suppress("SpellCheckingInspection")
                    if (it.maximumChanges > 1) {
                        channel.sendMessage(
                            EmbedUtil.error(
                                translate(user, "vote.toomany.changes.title"),
                                translate(user, "vote.toomany.changes.description")
                            ).build()
                        ).queue()
                    } else {
                        channel.sendMessage(
                            EmbedUtil.error(
                                translate(user, "vote.toomany.votes.title"),
                                translate(user, "vote.toomany.votes.description")
                            ).build()
                        ).queue()
                    }
                }
            }
        }
    }

    private fun translate(user: User, key: String): String {
        return TranslationUtil.translate(bot.regnum, key, user)
    }

    @SubscribeEvent
    private fun voteMessageDeleted(event: GuildMessageDeleteEvent) {
        val messageId = event.messageIdLong
        runChecks(messageId, event.guild.idLong) {
            it.messagesIds = it.messagesIds.filterNot { entry -> entry.key == messageId }
            if (it.messagesIds.isEmpty()) {
                it.deleteAsync()
            } else {
                it.saveAsync()
            }
        }
    }

    @SubscribeEvent
    private fun voteReactionDeleted(event: GuildMessageReactionRemoveEvent) {
        runChecks(event.messageIdLong, event.guild.idLong) {
            val emote = event.reactionEmote.identifier()
            if (event.user == event.jda.selfUser && emote in it.emoteMapping.keys) {
                event.channel.retrieveMessageById(event.messageId).queue { message ->
                    Misc.addReaction(emote, message).queue()
                }
            }
        }
    }

    private fun runChecks(messageId: Long, guildId: Long, action: (vote: Vote) -> Unit) {
        val vote = bot.voteCache.getVoteByMessage(messageId, guildId) ?: return
        VoteCache.THREAD_POOL.execute {
            action(vote)
        }
    }
}