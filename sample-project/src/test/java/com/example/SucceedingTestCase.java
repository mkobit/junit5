/*
 * Copyright 2015 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package com.example;

import org.junit.gen5.api.After;
import org.junit.gen5.api.AfterAll;
import org.junit.gen5.api.Before;
import org.junit.gen5.api.BeforeAll;
import org.junit.gen5.api.Test;

/**
 * Named *TestCase so Gradle will not try to run it.
 */
class SucceedingTestCase {

	@BeforeAll
	void initClass() {
		System.out.println(getClass().getName() + " before all called");
	}

	@AfterAll
	void teardownClass() {
		System.out.println(getClass().getName() + " after all called");
	}

	@Before
	void before() {
		System.out.println(getClass().getName() + " before called");
	}

	@After
	void after() {
		System.out.println(getClass().getName() + " after called");
	}

	@Test(name = "A nice name for test 2")
	void test1() {
	}

	@Test(name = "A test name with umlauts äöüÄÖÜß")
	void test2() {
	}

}
