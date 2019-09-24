/*
 *     Waypoints
 *     Copyright (C) 2019  Lukas Planz
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

package de.md5lukas.wp.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.util.UUIDTypeAdapter;
import de.md5lukas.wp.Main;
import de.md5lukas.wp.config.Config;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class UUIDUtils {

	public static final Pattern UUID_PATTERN = Pattern.compile("\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b");
	private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s";
	private static final String NAME_URL = "https://api.mojang.com/user/profiles/%s/names";
	private static final LoadingCache<String, UUID> uuidCache;
	private static final LoadingCache<UUID, String> nameCache;

	static {
		uuidCache = CacheBuilder.newBuilder().maximumSize(Config.uuidCacheMaxSize)
				.expireAfterWrite(Config.uuidCacheInvalidAfter, Config.uuidCacheInvalidTimeUnit).build(new CacheLoader<String, UUID>() {
					@Override
					public UUID load(String name) throws Exception {
						if (name.length() < 3)
							throw new IllegalArgumentException("The name provided for uuid look-up is too short");
						name = name.toLowerCase();
						if (Bukkit.getPlayerExact(name) != null) {
							return Bukkit.getPlayerExact(name).getUniqueId();
						}
						HttpURLConnection connection = (HttpURLConnection) new URL(String.format(UUID_URL, name)).openConnection();
						connection.setReadTimeout(500);
						JsonObject json = new JsonParser()
								.parse(new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine())
								.getAsJsonObject();
						return UUIDTypeAdapter.fromString(json.get("id").getAsString());
					}
				});
		nameCache = CacheBuilder.newBuilder().maximumSize(Config.uuidCacheMaxSize)
				.expireAfterWrite(Config.uuidCacheInvalidAfter, Config.uuidCacheInvalidTimeUnit).build(new CacheLoader<UUID, String>() {
					@Override
					public String load(UUID uuid) throws Exception {
						if (Bukkit.getPlayer(uuid) != null) {
							return Bukkit.getPlayer(uuid).getName();
						}
						HttpURLConnection connection = (HttpURLConnection) new URL(
								String.format(NAME_URL, UUIDTypeAdapter.fromUUID(uuid))).openConnection();
						connection.setReadTimeout(500);

						JsonArray jsonArray = new JsonParser()
								.parse(new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine())
								.getAsJsonArray();
						JsonObject json = jsonArray.get(jsonArray.size() - 1).getAsJsonObject();
						String name = json.get("name").getAsString();
						uuidCache.put(name.toLowerCase(), uuid);
						return name;
					}
				});
		uuidCache.put("sytm", UUID.fromString("68f22021-732e-4afe-9cf5-6906f07a41db"));
		nameCache.put(UUID.fromString("68f22021-732e-4afe-9cf5-6906f07a41db"), "Sytm");
	}

	public static UUID getUUID(String name) {
		try {
			return uuidCache.get(name.toLowerCase());
		} catch (ExecutionException ee) {
			if (ee.getCause() instanceof SocketTimeoutException || ee.getCause() instanceof IOException) {
				Main.logger().log(Level.SEVERE, "An error occurred while trying to retrieve the uuid of the player '" + name + "'", ee);
			}
		}
		return null;
	}

	public static String getName(UUID uuid) {
		try {
			return nameCache.get(uuid);
		} catch (ExecutionException ee) {
			if (ee.getCause() instanceof SocketTimeoutException || ee.getCause() instanceof IOException) {
				Main.logger().log(Level.SEVERE, "An error occurred while trying to retrieve the name of the player with the uuid of " + uuid, ee);
			}
		}
		return null;
	}
}