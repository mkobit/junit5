/*
 * Copyright 2015-2017 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.junit.platform.console.tasks;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.junit.platform.commons.meta.API.Usage.Internal;
import static org.junit.platform.console.tasks.Color.CONTAINER;
import static org.junit.platform.console.tasks.Color.FAILED;
import static org.junit.platform.console.tasks.Color.GREEN;
import static org.junit.platform.console.tasks.Color.NONE;
import static org.junit.platform.console.tasks.Color.SKIPPED;
import static org.junit.platform.console.tasks.Color.YELLOW;

import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;

import org.junit.platform.commons.meta.API;
import org.junit.platform.commons.util.StringUtils;
import org.junit.platform.console.options.Theme;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestExecutionResult.Status;
import org.junit.platform.engine.reporting.ReportEntry;

/**
 * @since 1.0
 */
@API(Internal)
class TreePrinter {

	private final static DateTimeFormatter REPORT_ENTRY_FORMATTER = ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSS");

	private final PrintWriter out;
	private final Theme theme;
	private final boolean disableAnsiColors;

	TreePrinter(PrintWriter out, Theme theme, boolean disableAnsiColors) {
		this.out = out;
		this.theme = theme;
		this.disableAnsiColors = disableAnsiColors;
	}

	void print(TreeNode node) {
		out.println(color(CONTAINER, theme.root()));
		print(node, "", true);
		out.flush();
	}

	private void print(TreeNode node, String indent, boolean continuous) {
		if (node.visible) {
			printVisible(node, indent, continuous);
		}
		if (node.children.isEmpty()) {
			return;
		}
		if (node.visible) {
			indent += continuous ? theme.vertical() : theme.blank();
		}
		Iterator<TreeNode> iterator = node.children.iterator();
		while (iterator.hasNext()) {
			print(iterator.next(), indent, iterator.hasNext());
		}
	}

	private void printVisible(TreeNode node, String indent, boolean continuous) {
		String bullet = continuous ? theme.entry() : theme.end();
		String prefix = color(CONTAINER, indent + bullet);
		String tabbed = color(CONTAINER, indent + (continuous ? theme.vertical() : theme.blank()) + theme.blank());
		String caption = colorCaption(node);
		String duration = color(CONTAINER, node.duration + " ms");
		String icon = color(SKIPPED, theme.skipped());
		if (node.result().isPresent()) {
			TestExecutionResult result = node.result().get();
			Color resultColor = Color.valueOf(result);
			icon = color(resultColor, theme.status(result));
		}
		out.print(prefix);
		out.print(" ");
		out.print(caption);
		if (node.duration > 10000 && node.children.isEmpty()) {
			// out.print(new String(new char[60 - (indent + bullet + node.caption).length()]).replace('\0', ' '));
			out.print(" ");
			out.print(duration);
		}
		out.print(" ");
		out.print(icon);
		node.result().ifPresent(result -> printThrowable(tabbed, result));
		node.reason().ifPresent(reason -> printMessage(SKIPPED, tabbed, reason));
		node.reports.forEach(e -> printReportEntry(tabbed, e));
		out.println();
	}

	private String colorCaption(TreeNode node) {
		String caption = node.caption();
		if (node.result().isPresent()) {
			TestExecutionResult result = node.result().get();
			Color resultColor = Color.valueOf(result);
			if (result.getStatus() != Status.SUCCESSFUL) {
				return color(resultColor, caption);
			}
		}
		if (node.reason().isPresent()) {
			return color(SKIPPED, caption);
		}
		return color(Color.valueOf(node.identifier().orElseThrow(AssertionError::new)), caption);
	}

	private void printThrowable(String indent, TestExecutionResult result) {
		if (!result.getThrowable().isPresent()) {
			return;
		}
		Throwable throwable = result.getThrowable().get();
		printMessage(FAILED, indent, throwable.getMessage());
		// ...or print entire stacktrace?
		// out.println();
		// out.print(indent + color(FAILED, theme.vertical()));
		// printMessage(FAILED, indent, ExceptionUtils.readStackTrace(throwable));
	}

	private void printReportEntry(String indent, ReportEntry reportEntry) {
		out.println();
		out.print(indent);
		out.print(reportEntry.getTimestamp().format(REPORT_ENTRY_FORMATTER));
		if (reportEntry.getKeyValuePairs().size() == 1) {
			printReportEntry(" ", reportEntry.getKeyValuePairs().entrySet().iterator().next());
			return;
		}
		for (Map.Entry<String, String> entry : reportEntry.getKeyValuePairs().entrySet()) {
			out.println();
			printReportEntry(indent + theme.blank(), entry);
		}
	}

	private void printReportEntry(String indent, Map.Entry<String, String> mapEntry) {
		out.print(indent);
		out.print(color(YELLOW, mapEntry.getKey()));
		out.print(" = `");
		out.print(color(GREEN, mapEntry.getValue()));
		out.print("`");
	}

	/**
	 * Prints potential multi-line message.
	 */
	private void printMessage(Color color, String indent, String message) {
		String[] lines = message.split("\\R");
		out.print(" ");
		out.print(color(color, lines[0]));
		if (lines.length > 1) {
			for (int i = 1; i < lines.length; i++) {
				out.println();
				out.print(indent);
				if (StringUtils.isNotBlank(lines[i])) {
					String extra = theme.blank(); // i + 1 < lines.length ? theme.vertical() : theme.end() + " ";
					out.print(color(color, extra + lines[i]));
				}
			}
		}
	}

	private String color(Color color, String text) {
		if (disableAnsiColors || color == NONE) {
			return text;
		}
		return color.toString() + text + NONE.toString();
	}

}
