package test;

import de.md5lukas.wp.config.Message;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MessageTest {

	@Test
	public void testEN() {
		testMessageFile(new File("src/main/resources/messages_en.cfg"));
	}

	@Test
	public void testDE() {
		testMessageFile(new File("src/main/resources/messages_de.cfg"));
	}

	private void testMessageFile(File file) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			List<String> messages = new ArrayList<>();
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#") || line.startsWith("|") || line.trim().isEmpty()) continue;
				int index = line.indexOf('=');
				if (index == -1) index = line.indexOf('|');
				if (index == -1) continue;
				messages.add(line.substring(0, index).toLowerCase());
			}
			int missing = 0;
			for (Message m : Message.values()) {
				if (!messages.contains(m.getInFilePath())) {
					++missing;
					System.out.println(m.getInFilePath());
				}
			}
			assertEquals("There are translations missing in the file " + file.getAbsolutePath(), 0, missing);
		} catch (IOException io) {
			fail("An IOException occured");
		}
	}
}
