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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class DataStreamHelper {

	private static final Charset cs = StandardCharsets.UTF_8;

	public static void writeString(DataOutputStream dos, String string) throws IOException {
		dos.writeInt(string.getBytes(cs).length);
		dos.write(string.getBytes(cs));
	}

	public static String readString(DataInputStream dis) throws IOException {
		byte[] buffer = new byte[dis.readInt()];
		dis.read(buffer);
		return new String(buffer, cs);
	}

	public static void writeUUID(DataOutputStream dos, UUID uuid) throws IOException {
		dos.writeLong(uuid.getMostSignificantBits());
		dos.writeLong(uuid.getLeastSignificantBits());
	}

	public static UUID readUUID(DataInputStream dis) throws IOException {
		return new UUID(dis.readLong(), dis.readLong());
	}
}
