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

package me.schlaubi.votebot.util;

import net.dv8tion.jda.api.entities.Guild;

import java.awt.Color;

public class Utils {
    public static final String[] EMOTES = {"🍏", "🍎", "🍐", "🍊", "🍋", "🍔", "🍟", "🌭", "🍕", "🍝", "⚽", "🏀", "🏈", "⚾", "🎾", "☎", "📟", "💿", "🖲", "🕹", "🎥", "⌚", "📱", "⏰"};

    public static final Color[] SERIES_COLORS = {Color.decode("#7289DA"), Color.decode("#ff6b6b"), Color.decode("#ff906a"), Color.decode("#f7f56c"), Color.decode("#b6f76c"), Color.decode("#6cf798"), Color.decode("#6cf2f7"), Color.decode("#6ca8f7"), Color.decode("#9e6cf7"), Color.decode("#f87cff")};

    public static String mentionEmote(String emoteId, Guild guild) {
        return guild.getEmoteById(emoteId).getAsMention();
    }
}