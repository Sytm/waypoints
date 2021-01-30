package de.md5lukas.waypoints.test;

import de.md5lukas.commons.messages.MessageParser;
import de.md5lukas.waypoints.Messages;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

public class CheckMessagesTest {

	public void verifyCompleteMessageFile(File file) throws IOException {
		MessageParser.ParseResult result = MessageParser.getDefaultParser().parse(file);
		List<String> missing = new ArrayList<>();
		for (Messages m : Messages.values())
			if (!result.getOriginal().containsKey(m.toString()))
				fail("Some translations are missing from the file " + file.getName());
	}

	@Test
	public void verifyEnglish() throws IOException {
		verifyCompleteMessageFile(new File("src/main/resources/lang/messages_en.msg"));
	}

	@Test
	public void verifyGerman() throws IOException {
		verifyCompleteMessageFile(new File("src/main/resources/lang/messages_de.msg"));
	}
}