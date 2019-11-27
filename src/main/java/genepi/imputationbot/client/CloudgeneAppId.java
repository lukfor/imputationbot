package genepi.imputationbot.client;

import genepi.imputationbot.util.Version;

public class CloudgeneAppId {

	private String id;
	
	private Version version;
	
	public CloudgeneAppId (String app) {
		String[] tiles = app.split("@",2);
		id = tiles[0];
		if (tiles.length > 1) {
			version = Version.parse(tiles[1]);
		} else {
			version = Version.parse("0.0.0");
		}
		
	}
	
	public String getId() {
		return id;
	}
	
	public Version getVersion() {
		return version;
	}
	
}
