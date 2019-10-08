package genepi.imputationbot.util;

public class AnsiColors {

	public static String makeRed(String text) {
		return ((char) 27 + "[31m" + text + (char) 27 + "[0m");
	}

	public static String makeGreen(String text) {
		return ((char) 27 + "[32m" + text + (char) 27 + "[0m");
	}

	public static String makeBlue(String text) {
		return ((char) 27 + "[34m" + text + (char) 27 + "[0m");
	}

	public static String makeGray(String text) {
		return ((char) 27 + "[90m" + text + (char) 27 + "[0m");
	}

}
