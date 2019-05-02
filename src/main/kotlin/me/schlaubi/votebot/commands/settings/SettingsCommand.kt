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

package me.schlaubi.votebot.commands.settings

import cc.hawkbot.regnum.client.Regnum
import cc.hawkbot.regnum.client.command.Group
import cc.hawkbot.regnum.client.command.context.Arguments
import cc.hawkbot.regnum.client.command.context.Context
import cc.hawkbot.regnum.client.command.permission.CommandPermissions
import cc.hawkbot.regnum.client.interaction.ReactableMessage
import cc.hawkbot.regnum.client.interaction.ReactableMessageBuilder
import cc.hawkbot.regnum.client.util.*
import me.schlaubi.votebot.commands.VoteBotCommand
import me.schlaubi.votebot.core.VoteBot
import me.schlaubi.votebot.identifier
import me.schlaubi.votebot.util.Utils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import java.net.URL
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class SettingsCommand(
    bot: VoteBot
) : VoteBotCommand(
    bot,
    Group.SETTINGS,
    "Settings",
    arrayOf("settings", "preferences", "pref"),
    CommandPermissions(public = true, node = "settings"),
    description = "Allows you to change your default settings for votes and server settings"
) {
    override fun execute(args: Arguments, context: Context) {
        context.sendMessage(
            EmbedUtil.info(
                context.translate("command.settings.loading.title"),
                context.translate("command.settings.loading.description")
                    .format(Emotes.LOADING)
            )
        ).queue {
            val builder = SettingsBuilder(bot)
            builder.message = it
            builder.authorizedUsers = listOf(context.author)
            builder.build()
        }
    }
}

private class Settings(
    bot: VoteBot,
    regnum: Regnum,
    message: Message,
    users: List<User>
) : ReactableMessage(
    regnum,
    message,
    users,
    3,
    TimeUnit.MINUTES,
    listOf(Permission.MESSAGE_ADD_REACTION, Permission.MESSAGE_MANAGE),
    true,
    false
) {

    private var page = Page.HOME
    private val admin: Boolean
    private val guild = bot.guildCache[message.guild]
    private val user = bot.userCache[users.first()]

    init {
        renderPage()
        admin = regnum.user(users.first())
            .hasPermission(
                CommandPermissions(serverAdminExclusive = true, node = "settings.server"),
                message.guild.idLong
            )
    }

    override fun handleReaction(event: GuildMessageReactionAddEvent) {
        finish()
        when (event.reactionEmote.identifier()) {
            // Custom emotes
            "570276037167153155" -> {
                if (!admin) {
                    return SafeMessage.sendMessage("Nice try man", message.channel as TextChannel, 5).queue { retry() }
                }
                val usesCustomEmotes = guild.usesCustomEmotes()
                guild.setUseCustomEmotes(!usesCustomEmotes)
                if (usesCustomEmotes) {
                    SafeMessage.sendMessage(
                        EmbedUtil.success(
                            translate("settings.customemotes.disabled.title"),
                            translate("settings.customemotes.disabled.description")
                        ),
                        message.channel as TextChannel, 5
                    )
                } else {
                    SafeMessage.sendMessage(
                        EmbedUtil.success(
                            translate("settings.customemotes.enabled.title"),
                            translate("settings.customemotes.enabled.description")
                        ),
                        message.channel as TextChannel, 5
                    )
                }
                guild.saveAsync().thenAccept {
                    renderPage(false)
                }
            }
            // Max changes
            "\uD83D\uDE31" -> {
                if (user.defaultMaximumVotes > 1) {
                    return SafeMessage.sendMessage(
                        EmbedUtil.error(
                            translate("settings.max_changes.max_votes.title"),
                            translate("settings.max_changes.max_votes.description")
                        ), message.channel as TextChannel, 5
                    ).run { retry() }
                }
                page = Page.MAX_CHANGES
                renderPage()
            }
            // Max votes
            "\uD83D\uDE0E" -> {
                if (user.defaultMaximumChanges > 1) {
                    return SafeMessage.sendMessage(
                        EmbedUtil.error(
                            translate("settings.max_votes.max_changes.title"),
                            translate("settings.max_votes.max_changes.description")
                        ), message.channel as TextChannel, 5
                    ).run { retry() }
                }
                page = Page.MAX_VOTES
                renderPage()
            }
            // No entry sign (close)
            "\uD83D\uDEAB" -> {
                close()
            }
            "\uD83D\uDD19" -> {
                if (page == Page.HOME) {
                    return message.channel.sendFile(
                        URL("https://www.minecraftskinstealer.com/achievement/a.php?i=14&h=Achievement+Get%21&t=Bot+breaker").openStream(),
                        "achievement.png"
                    ).queue {
                        it.delete().queueAfter(3, TimeUnit.SECONDS)
                    }.also { retry() }
                }
                page = Page.HOME
                renderPage()
            }
        }
    }

    override fun onMessage(event: GuildMessageReceivedEvent) {
        finish()
        event.message.delete().reason("Interaction user input is getting cleared for cleaning the flow").queue()
        if (page != Page.HOME) {
            val input = event.message.contentDisplay
            if (!Misc.isNumeric(input)) {
                return
            }
            val value = input.toInt()
            when (page) {
                Page.MAX_CHANGES -> {
                    if (value == user.defaultMaximumChanges) {
                        return SafeMessage.sendMessage(
                            EmbedUtil.error(
                                translate("settings.max_changes.same.title"),
                                translate("settings.max_changes.same.description")
                            ),
                            message.channel as TextChannel, 5
                        ).run { retry() }
                    }
                    user.defaultMaximumChanges = value
                }
                Page.MAX_VOTES -> {
                    if (value == user.defaultMaximumVotes) {
                        return SafeMessage.sendMessage(
                            EmbedUtil.error(
                                translate("settings.max_votes.same.title"),
                                translate("settings.max_votes.same.description")
                            ), message.channel as TextChannel, 5
                        ).run { retry() }
                    }
                    user.defaultMaximumVotes = value
                }
                else -> return SafeMessage.sendMessage("HOW DID YOU DO THAT?", message.channel as TextChannel, 5)
                    .queue()
            }
            user.saveAsync().thenAccept {
                page = Page.HOME
                renderPage()
            }
        }
    }

    private enum class Page(val title: String, val emotes: Array<String>) {
        HOME("settings.home.title", arrayOf("\uD83D\uDE31", "\uD83D\uDE0E", "\uD83D\uDEAB")),
        MAX_VOTES("settings.max_votes.edit.title", arrayOf("\uD83D\uDD19")),
        MAX_CHANGES("settings.max_changes.edit.title", arrayOf("\uD83D\uDD19")),
    }

    private fun renderPage(rerender: Boolean = true) {
        val embed = EmbedBuilder()
        val title = translate(page)
        if (!rerender) {
            return rerenderPage(embed, rerender).thenRun { retry() }.run { Unit }
        }
        message.clearReactions().queue {
            embed.setTitle(title)
            embed.setDescription(
                translate("settings.loading.description")
                    .format(Emotes.LOADING, title)
            )
            editMessage(embed).queue {
                editMessage(embed).queue {
                    var futures = arrayOf<CompletableFuture<Void>>()
                    for (emote in page.emotes) {
                        futures += Misc.addReaction(emote, message).submit()
                    }
                    CompletableFuture.allOf(*futures).thenRun {
                        rerenderPage(embed, rerender).thenRun {
                            retry()
                        }
                    }
                }
            }
        }
    }

    private fun rerenderPage(embed: EmbedBuilder, rerender: Boolean): CompletableFuture<Message> {
        embed.setColor(Colors.BLURLPLE)
        when (page) {
            Page.HOME -> {
                embed.setDescription(translate("settings.home.description"))
                if (admin) {
                    if (rerender) {
                        Misc.addReaction("570276037167153155", message).queue()
                    }
                    embed.addField(
                        translate("settings.emotes.title")
                            .format(
                                "<:votebot:570276037167153155>",
                                translate(Utils.translateBoolean(guild.usesCustomEmotes()))
                            ),
                        translate("settings.emotes.description"),
                        false
                    )
                }
                embed.addField(
                    translate("settings.max_changes.title")
                        .format("\uD83D\uDE31", user.defaultMaximumChanges),
                    translate("settings.max_changes.description"),
                    false
                )
                embed.addField(
                    translate("settings.max_votes.title")
                        .format("\uD83D\uDE0E", user.defaultMaximumVotes),
                    translate("settings.max_votes.description"),
                    false
                )
            }
            else -> embed.setDescription(translate(page.title.replace("title", "description")))
        }
        return editMessage(embed).submit()
    }

    private fun close() {
        message.clearReactions().queue()
        finish()
    }

    private fun translate(page: Page) = translate(page.title)
}

private class SettingsBuilder(private val bot: VoteBot) :
    ReactableMessageBuilder<Settings, SettingsBuilder>(bot.regnum) {

    override fun build(): Settings {
        return Settings(
            bot,
            regnum,
            message,
            authorizedUsers
        )
    }
}