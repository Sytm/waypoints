package de.md5lukas.waypoints.test;

import de.md5lukas.commons.messages.MessageParser;
import de.md5lukas.waypoints.Messages;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class CheckMessagesTest {

	public void verifyCompleteMessageFile(File file) throws IOException {
		System.out.println("Testing file " + file.getName());
		MessageParser.ParseResult result = MessageParser.getDefaultParser().parse(file);
		List<String> missing = new ArrayList<>();
		for (Messages m : Messages.values()) {
			if (!result.getOriginal().containsKey(m.toString()))
				missing.add(m.toString());
		}
		if (missing.isEmpty()) {
			System.out.println("File " + file.getName() + " is complete");
		} else {
			System.err.println("Some translations are missing from the file \"" + file.getName() + "\":");
			missing.forEach(System.err::println);
			fail("Some translations are missing from the file, see above for more information");
		}
	}

	@Test
	public void verifyMessageFiles() throws IOException {
		for (File f : Objects.requireNonNull(new File("src/main/resources/lang/").listFiles())) {
			verifyCompleteMessageFile(f);
		}
	}
}
