/*
 * Copyright 2015-2017 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.junit.platform.console;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.platform.commons.util.ReflectionUtils.MethodSortOrder.HierarchyDown;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestReporter;
import org.junit.platform.commons.util.AnnotationUtils;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.platform.console.options.Details;
import org.junit.platform.console.options.Theme;

/**
 * @since 1.0
 */
class ConsoleDetailsTests {

	private static final String REGEX_PATTERN = "<- REGEX";

	static class Container {

		@Expect(details = Details.TREE, theme = Theme.UNICODE, //
				lines = { "╷", //
						"└─ JUnit Jupiter ✔", //
						"   └─ ConsoleDetailsTests$Container ✔", //
						"      └─ skipWithSingleLineReason() ↷ single line skip reason" //
				})
		@Expect(details = Details.TREE, theme = Theme.ASCII, //
				lines = { ".", //
						"'-- JUnit Jupiter [OK]", //
						"  '-- ConsoleDetailsTests$Container [OK]", //
						"    '-- skipWithSingleLineReason() [S] single line skip reason" //
				})
		@Expect(details = Details.FLAT, theme = Theme.UNICODE, //
				lines = { //
						"Test execution started. Number of static tests: 1", //
						"Started:     JUnit Jupiter ([engine:junit-jupiter])", //
						"Started:     ConsoleDetailsTests$Container ([engine:junit-jupiter]/[class:org.junit.platform.console.ConsoleDetailsTests$Container])", //
						"Skipped:     skipWithSingleLineReason() ([engine:junit-jupiter]/[class:org.junit.platform.console.ConsoleDetailsTests$Container]/[method:skipWithSingleLineReason()])", //
						"             => Reason: single line skip reason", //
						"Finished:    ConsoleDetailsTests$Container ([engine:junit-jupiter]/[class:org.junit.platform.console.ConsoleDetailsTests$Container])", //
						"Finished:    JUnit Jupiter ([engine:junit-jupiter])", //
						"Test execution finished.", //
				})
		@Test
		@Disabled("single line skip reason")
		void skipWithSingleLineReason() {
		}

		@Expect(details = Details.TREE, theme = Theme.UNICODE, //
				lines = { "╷", //
						"└─ JUnit Jupiter ✔", //
						"   └─ ConsoleDetailsTests$Container ✔", //
						"      └─ failWithSingleLineMessage() ✘ single line fail message" //
				})
		@Expect(details = Details.TREE, theme = Theme.ASCII, //
				lines = { ".", //
						"'-- JUnit Jupiter [OK]", //
						"  '-- ConsoleDetailsTests$Container [OK]", //
						"    '-- failWithSingleLineMessage() [X] single line fail message" //
				})
		@Expect(details = Details.FLAT, theme = Theme.UNICODE, //
				lines = { //
						"Test execution started. Number of static tests: 1", //
						"Started:     JUnit Jupiter ([engine:junit-jupiter])", //
						"Started:     ConsoleDetailsTests$Container ([engine:junit-jupiter]/[class:org.junit.platform.console.ConsoleDetailsTests$Container])", //
						"Started:     failWithSingleLineMessage() ([engine:junit-jupiter]/[class:org.junit.platform.console.ConsoleDetailsTests$Container]/[method:failWithSingleLineMessage()])", //
						"Finished:    failWithSingleLineMessage() ([engine:junit-jupiter]/[class:org.junit.platform.console.ConsoleDetailsTests$Container]/[method:failWithSingleLineMessage()])", //
						"             => Exception: org.opentest4j.AssertionFailedError: single line fail message" //
				})
		@Test
		void failWithSingleLineMessage() {
			Assertions.fail("single line fail message");
		}

		@Expect(details = Details.TREE, theme = Theme.UNICODE, //
				lines = { "╷", //
						"└─ JUnit Jupiter ✔", //
						"   └─ ConsoleDetailsTests$Container ✔", //
						"      └─ failWithMultiLineMessage() ✘ multi", //
						"               line", //
						"               fail", //
						"               message" //
				})
		@Expect(details = Details.TREE, theme = Theme.ASCII, //
				lines = { ".", //
						"'-- JUnit Jupiter [OK]", //
						"  '-- ConsoleDetailsTests$Container [OK]", //
						"    '-- failWithMultiLineMessage() [X] multi", //
						"          line", //
						"          fail", //
						"          message" //
				})
		@Expect(details = Details.FLAT, theme = Theme.UNICODE, //
				lines = { //
						"Test execution started. Number of static tests: 1", //
						"Started:     JUnit Jupiter ([engine:junit-jupiter])", //
						"Started:     ConsoleDetailsTests$Container ([engine:junit-jupiter]/[class:org.junit.platform.console.ConsoleDetailsTests$Container])", //
						"Started:     failWithMultiLineMessage() ([engine:junit-jupiter]/[class:org.junit.platform.console.ConsoleDetailsTests$Container]/[method:failWithMultiLineMessage()])", //
						"Finished:    failWithMultiLineMessage() ([engine:junit-jupiter]/[class:org.junit.platform.console.ConsoleDetailsTests$Container]/[method:failWithMultiLineMessage()])", //
						"             => Exception: org.opentest4j.AssertionFailedError: multi", //
						"             line", //
						"             fail", //
						"             message" //
				})
		@Test
		void failWithMultiLineMessage() {
			Assertions.fail("multi\nline\nfail\nmessage");
		}

		@Expect(details = Details.TREE, theme = Theme.UNICODE, //
				lines = { "╷", //
						"└─ JUnit Jupiter ✔", //
						"   └─ ConsoleDetailsTests$Container ✔", //
						"      └─ reportSingleEntryWithSingleMapping(TestReporter) ✔", //
						"            ....-..-..T..:..:...... foo = `bar`" + REGEX_PATTERN, //
				})
		@Test
		void reportSingleEntryWithSingleMapping(TestReporter reporter) {
			reporter.publishEntry("foo", "bar");
		}

		@Expect(details = Details.TREE, theme = Theme.UNICODE, //
				lines = { "╷", //
						"└─ JUnit Jupiter ✔", //
						"   └─ ConsoleDetailsTests$Container ✔", //
						"      └─ reportMultiEntriesWithSingleMapping(TestReporter) ✔", //
						"            ....-..-..T..:..:...... foo = `bar`" + REGEX_PATTERN, //
						"            ....-..-..T..:..:...... far = `boo`" + REGEX_PATTERN, //
				})
		@Test
		void reportMultiEntriesWithSingleMapping(TestReporter reporter) {
			reporter.publishEntry("foo", "bar");
			reporter.publishEntry("far", "boo");
		}

		@Expect(details = Details.TREE, theme = Theme.UNICODE, //
				lines = { "╷", //
						"└─ JUnit Jupiter ✔", //
						"   └─ ConsoleDetailsTests$Container ✔", //
						"      └─ reportMultiEntriesWithMultiMappings(TestReporter) ✔", //
						"            ....-..-..T..:..:......" + REGEX_PATTERN, //
						"               user name = `dk38`", //
						"               award year = `1974`", //
						"            ....-..-..T..:..:...... single = `mapping`" + REGEX_PATTERN, //
						"            ....-..-..T..:..:......" + REGEX_PATTERN, //
						"               user name = `st77`", //
						"               award year = `1977`", //
						"               last seen = `2001`", //
				})
		@Test
		void reportMultiEntriesWithMultiMappings(TestReporter reporter) {
			Map<String, String> values = new LinkedHashMap<>();
			values.put("user name", "dk38");
			values.put("award year", "1974");
			reporter.publishEntry(values);
			reporter.publishEntry("single", "mapping");
			Map<String, String> more = new LinkedHashMap<>();
			more.put("user name", "st77");
			more.put("award year", "1977");
			more.put("last seen", "2001");
			reporter.publishEntry(more);
		}

	}

	@Retention(RetentionPolicy.RUNTIME)
	@interface Expectations {
		Expect[] value();
	}

	@Repeatable(Expectations.class)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Expect {
		Details details();

		Theme theme();

		String[] lines();
	}

	@TestFactory
	List<DynamicTest> foreach() throws Exception {
		List<DynamicTest> tests = new ArrayList<>();
		for (Method method : AnnotationUtils.findAnnotatedMethods(Container.class, Test.class, HierarchyDown)) {
			for (Expect expect : method.getAnnotationsByType(Expect.class)) {
				String displayName = method.getName() + " " + expect.details() + " " + expect.theme();
				DynamicTest test = DynamicTest.dynamicTest(displayName, () -> {
					String[] args = { //
							"--include-engine", "junit-jupiter", //
							"--details", expect.details().name(), //
							"--details-theme", expect.theme().name(), //
							"--disable-ansi-colors", "true", //
							"--include-classname", ".*", //
							"--select-method", ReflectionUtils.getFullyQualifiedMethodName(method) //
					};
					ConsoleLauncherWrapper wrapper = new ConsoleLauncherWrapper();
					ConsoleLauncherWrapperResult result = wrapper.execute(Optional.empty(), args);

					int max = expect.lines().length;
					List<String> actualLines = Arrays.asList(result.out.split("\\R", max + 1)).subList(0, max);
					for (int i = 0; i < max; i++) {
						String actualLine = actualLines.get(i);
						String expectedLine = expect.lines()[i];
						if (expectedLine.endsWith(REGEX_PATTERN)) {
							expectedLine = expectedLine.substring(0, expectedLine.length() - REGEX_PATTERN.length());
							assertTrue(actualLine.matches(expectedLine), "\nactual string = " + actualLine
									+ "\nregex pattern = " + expectedLine + "\n" + result.out + "\n" + result.err);
							continue;
						}
						assertEquals(expectedLine, actualLine, "\n" + result.out + "\n" + result.err);
					}
				});
				tests.add(test);
			}
		}
		return tests;
	}

}
