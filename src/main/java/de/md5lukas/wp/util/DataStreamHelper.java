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
