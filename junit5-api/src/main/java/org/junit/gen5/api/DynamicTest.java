/*
 * Copyright 2015-2016 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.junit.gen5.api;

import static java.util.Spliterator.ORDERED;
import static java.util.Spliterators.spliteratorUnknownSize;
import static org.junit.gen5.commons.meta.API.Usage.Experimental;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.gen5.commons.meta.API;
import org.junit.gen5.commons.util.Preconditions;
import org.junit.gen5.commons.util.ToStringBuilder;

/**
 * A {@code DynamicTest} is a test case generated at runtime.
 *
 * <p>It is composed of a {@linkplain #getDisplayName display name} and an
 * {@link #getExecutable Executable}.
 *
 * <p>Instances of {@code DynamicTest} must be generated by factory methods
 * annotated with {@link TestFactory @TestFactory}.
 *
 * <p>Note that dynamic tests are quite different from standard {@link Test @Test}
 * cases since callbacks such as {@link BeforeEach @BeforeEach} and
 * {@link AfterEach @AfterEach} methods are not executed for dynamic tests.
 *
 * @since 5.0
 * @see Test
 * @see TestFactory
 * @see Executable
 */
@API(Experimental)
public class DynamicTest {

	public static <T> Stream<DynamicTest> stream(Iterator<T> inputGenerator,
			Function<? super T, String> displayNameGenerator, Consumer<? super T> testGenerator) {

		Preconditions.notNull(inputGenerator, "inputGenerator must not be null");
		Preconditions.notNull(displayNameGenerator, "displayNameGenerator must not be null");
		Preconditions.notNull(testGenerator, "testGenerator must not be null");

		// @formatter:off
		return StreamSupport.stream(spliteratorUnknownSize(inputGenerator, ORDERED), false)
				.map(input -> new DynamicTest(displayNameGenerator.apply(input), () -> testGenerator.accept(input)));
		// @formatter:on
	}

	private final String displayName;
	private final Executable executable;

	public DynamicTest(String displayName, Executable executable) {
		this.displayName = displayName;
		this.executable = executable;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public Executable getExecutable() {
		return this.executable;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("displayName", displayName).toString();
	}

}
