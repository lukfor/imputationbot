package genepi.imputationbot.util;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class Console {

	public static PrintStream out = System.out;

	public static InputStream in = System.in;

	public static Scanner scanner = new Scanner(in);

	public static String prompt(String label, String defaultValue) {
		print(label + " [" + defaultValue + "]: ");
		String value = readln();
		if (value.isEmpty()) {
			return defaultValue;
		} else {
			return value;
		}
	}

	public static String prompt(String label) {
		print(label + " [None]: ");
		String value = readln();
		return value;
	}

	public static Integer promptInt(String label) {
		String value = prompt(label);
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			error("Value '" + value + "' is not a valid input.");
			return promptInt(label);
		}
	}

	public static Integer promptInt(String label, Integer defaultValue) {
		print(label);
		if (defaultValue != null) {
			print(" [" + defaultValue + "]: ");
		}
		String value = readln();
		if (value.isEmpty()) {
			return defaultValue;
		} else {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
				error("Value '" + value + "' is not a valid input.");
				return promptInt(label, defaultValue);
			}
		}
	}

	public static int select(String label, Object[] items) {
		println(label);
		for (int i = 0; i < items.length; i++) {
			println("  [" + (i + 1) + "] " + items[i]);
		}
		Integer selection = promptInt(AnsiColors.green("> "), null);
		if (selection != null && selection >= 1 && selection <= items.length) {
			println();
			return selection;
		} else {
			if (selection != null) {
				error("Selection '" + selection + "' is not valid.");
			} else {
				error("Please enter a selection.");
			}
			return select(label, items);
		}
	}

	public static void error(String message) {
		println();
		printlnInRed("Error: " + message);
		println();
	}

	public static void printlnInRed(String text) {
		println(AnsiColors.red(text));
	}

	public static void printlnInGreen(String text) {
		println(AnsiColors.green(text));
	}

	public static void print(String message) {
		out.print(message);
	}

	public static void println() {
		out.println();
	}

	public static void println(String message) {
		out.println(message);
	}

	public static String readln() {
		String value = scanner.nextLine();
		return value;
	}

	public static void setIn(InputStream in) {
		Console.in = in;
		scanner = new Scanner(in);
	}

	public static void setOut(PrintStream out) {
		Console.out = out;
	}

}
