package genepi.imputationbot.util;

public class Version {

	private String[] parts;

	private String[] tiles;

	private String version = "";
	
	private Version(String version) {

		parts = version.split("-", 2);
		tiles = parts[0].split("\\.");
		if (tiles.length != 3) {
			throw new IllegalArgumentException("String '" + version + "' is not a valid vesion number");
		}
		this.version = version;
	}

	public static Version parse(String version) {
		return new Version(version);
	}

	public int compareTo(Version other) {

		String parts1[] = parts;
		String parts2[] = other.parts;

		String tiles1[] = tiles;
		String tiles2[] = other.tiles;

		for (int i = 0; i < tiles1.length; i++) {
			int number1 = Integer.parseInt(tiles1[i].trim());
			int number2 = Integer.parseInt(tiles2[i].trim());

			if (number1 != number2) {

				return number1 > number2 ? 1 : -1;

			}

		}

		if (parts1.length > 1) {
			if (parts2.length > 1) {
				return parts1[1].compareTo(parts2[1]);
			} else {
				return -1;
			}
		} else {
			if (parts2.length > 1) {
				return 1;
			}
		}

		return 0;

	}
	
	@Override
	public String toString() {
		return version;
	}

}
