/*
 *     Waypoints2, A plugin for spigot to add waypoints functionality
 *     Copyright (C) 2019-2020 Lukas Planz
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.md5lukas.waypoints.util;

import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

import static de.md5lukas.waypoints.Messages.*;
import static java.util.concurrent.TimeUnit.*;

public class TimeHelper {

    public static String formatDuration(Player player, long millis) {
        StringBuilder builder = new StringBuilder();
        if (millis < 0) {
            throw new IllegalArgumentException("The duration to convert to a string must be positive");
        }
        long days = MILLISECONDS.toDays(millis);
        if (days > 0) {
            builder.append(days).append(' ').append(pluralizationHelper(player, days, DAYS));
            millis -= DAYS.toMillis(days);
        }
        long hours = MILLISECONDS.toHours(millis);
        if (hours > 0) {
            if (days > 0)
                builder.append(' ');
            builder.append(hours).append(' ').append(pluralizationHelper(player, hours, HOURS));
            millis -= HOURS.toMillis(hours);
        }
        long minutes = MILLISECONDS.toMinutes(millis);
        if (minutes > 0) {
            if (days > 0 || hours > 0)
                builder.append(' ');
            builder.append(minutes).append(' ').append(pluralizationHelper(player, minutes, MINUTES));
            millis -= MINUTES.toMillis(minutes);
        }
        long seconds = MILLISECONDS.toSeconds(millis);
        if (seconds > 0) {
            if (days > 0 || hours > 0 || minutes > 0)
                builder.append(' ');
            builder.append(seconds).append(' ').append(pluralizationHelper(player, seconds, SECONDS));
        } else if (days == 0 && hours == 0 && minutes == 0) {
            builder.append("1 ").append(pluralizationHelper(player, 1, SECONDS));
        }

        return builder.toString();
    }

    public static String pluralizeSeconds(Player player, long seconds) {
        return seconds + " " + pluralizationHelper(player, seconds, SECONDS);
    }

    private static String pluralizationHelper(Player player, long value, TimeUnit timeUnit) {
        boolean isPlural = value > 1;
        switch (timeUnit) {
            case SECONDS:
                return isPlural ? CHAT_DURATION_SECONDS.getRaw(player) : CHAT_DURATION_SECOND.getRaw(player);
            case MINUTES:
                return isPlural ? CHAT_DURATION_MINUTES.getRaw(player) : CHAT_DURATION_MINUTE.getRaw(player);
            case HOURS:
                return isPlural ? CHAT_DURATION_HOURS.getRaw(player) : CHAT_DURATION_HOUR.getRaw(player);
            case DAYS:
                return isPlural ? CHAT_DURATION_DAYS.getRaw(player) : CHAT_DURATION_DAY.getRaw(player);
            default:
                throw new UnsupportedOperationException("The TimeUnit " + timeUnit + " is not supported by this method!");
        }
    }
}
