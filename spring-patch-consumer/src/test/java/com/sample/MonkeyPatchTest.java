package com.sample;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import static org.junit.Assert.*;

public class MonkeyPatchTest {
	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void testMonkeyPatch() {
		File testFile = new File(tempFolder.getRoot(), "test-file.txt");
		System.setProperty("monkey-patch-test-file", testFile.getAbsolutePath());
		assertFalse(testFile.exists());
		new ClassPathXmlApplicationContext();
		assertTrue(testFile.exists());
    }
}