package genepi.imputationbot.util;

public class OperatingSystem {

	public static String NAME = System.getProperty("os.name").toLowerCase();

	public static boolean isWindows() {
		return NAME.contains("win");
	}

	public static boolean isMac() {
		return NAME.contains("mac");
	}

	public static boolean isUnix() {
		return (NAME.contains("nix") || NAME.contains("nux") || NAME.contains("aix"));
	}

	public static boolean isSolaris() {
		return NAME.contains("sunos");
	}

	public static String getOS() {
		if (isWindows()) {
			return "win";
		} else if (isMac()) {
			return "osx";
		} else if (isUnix()) {
			return "uni";
		} else if (isSolaris()) {
			return "sol";
		} else {
			return "err";
		}
	}

}