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

import discord4j.core.DiscordClient
import discord4j.core.DiscordClientBuilder
import discord4j.core.`object`.presence.Presence
import discord4j.core.shard.ShardingClientBuilder
import wtf.votebot.bot.config.Config

/**
 * The bots actual main class.
 */
class VoteBot(private val config: Config) {
    init {
        ShardingClientBuilder(config.discordToken!!).build()
            .map { it.setInitialPresence(Presence.invisible()) }
            .map(DiscordClientBuilder::build)
            .flatMap(DiscordClient::login)
            .blockLast()
        Runtime.getRuntime().addShutdownHook(Thread(this::shutdown))
    }

    private fun shutdown() {
        TODO("Add shutdown hook")
    }
}
