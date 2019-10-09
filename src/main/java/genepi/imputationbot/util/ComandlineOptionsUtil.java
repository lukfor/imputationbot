package genepi.imputationbot.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.ext.html.FormData;
import org.restlet.ext.html.FormDataSet;
import org.restlet.representation.FileRepresentation;

import genepi.imputationbot.client.CloudgeneAppException;

public class ComandlineOptionsUtil {

	public static Options createOptionsFromApp(JSONArray params) {
		Options options = new Options();
		for (int i = 0; i < params.length(); i++) {

			JSONObject param = params.getJSONObject(i);

			String type = param.getString("type");
			String id = param.getString("id");

			// ignore mode!
			if (!id.equals("mode") && !type.equals("separator") && !type.equals("terms_checkbox")
					&& !type.equals("info") && !type.equals("group") && !type.equals("agbcheckbox")) {

				String description = param.getString("description");
				String value = param.getString("value");
				boolean required = param.getBoolean("required");
				boolean hasDefault = value != null && !value.trim().isEmpty();

				// remove html tags from decription (e.g. links)
				description = description.replaceAll("\\<.*?>", "");

				Option option = new Option(null, id, true, description);
				option.setRequired(required && !hasDefault);
				option.setArgName(type);

				if (type.equals("list")) {
					JSONArray values = param.getJSONArray("values");
					String typeName = "";
					for (int j = 0; j < values.length(); j++) {
						String key = values.getJSONObject(j).getString("key");
						if (j > 0) {
							typeName += "|";
						}
						typeName += key;
					}
					option.setArgName(typeName);
				} else if (type.equals("app_list")) {
					JSONArray values = param.getJSONArray("values");
					String typeName = "";
					for (int j = 0; j < values.length(); j++) {
						String key = values.getJSONObject(j).getString("key");
						if (j > 0) {
							typeName += "|";
						}
						typeName += key.replaceAll("apps@", "");
					}
					option.setArgName(typeName);
				} else if (type.equals("checkbox")) {
					JSONArray values = param.getJSONArray("values");
					String typeName = "";
					for (int j = 0; j < values.length(); j++) {
						String label = values.getJSONObject(j).getString("value");
						if (j > 0) {
							typeName += "|";
						}
						typeName += label;
					}
					option.setArgName(typeName);
				}

				if (hasDefault) {
					description += "\n[Default: " + value + "]";
				} else {
					description += "\n[Required]";
				}

				option.setDescription(description);
				options.addOption(option);

			}
		}
		return options;

	}

	public static FormDataSet createForm(JSONArray params, CommandLine line) throws Exception {
		Map<String, String> props = new HashMap<String, String>();

		for (int i = 0; i < params.length(); i++) {
			JSONObject param = params.getJSONObject(i);
			String id = param.getString("id");
			String defaultValue = param.getString("value");
			String value = line.getOptionValue(id, defaultValue);
			props.put(id, value);
		}

		System.out.println("Parameters:");

		FormDataSet form = new FormDataSet();
		form.setMultipart(true);
		for (int i = 0; i < params.length(); i++) {

			JSONObject param = params.getJSONObject(i);

			String type = param.getString("type");
			String id = param.getString("id");

			// ignore mode!
			if (!id.equals("mode") && !type.equals("separator") && !type.equals("terms_checkbox")
					&& !type.equals("info") && !type.equals("group") && !type.equals("agbcheckbox")) {

				if (props.containsKey(id)) {

					String value = props.get(id);

					if (type.equals("app_list")) {
						String value2 = "apps@" + value;
						if (!isValidValue(param, value2)) {
							throw new CloudgeneAppException("Value '" + value + "' is not a valid option for '" + param.getString("id") + "'.");
						}
						form.getEntries().add(new FormData(id, value2));
						System.out.println("  " + id + ": " + value2);
					} else if (type.equals("list")) {
						if (!isValidValue(param, value)) {
							throw new CloudgeneAppException("Value '" + value + "' is not a valid option for '" + param.getString("id") + "'.");
						}
						form.getEntries().add(new FormData(id, value));
						System.out.println("  " + id + ": " + value);
					} else if (isFile(type)) {
						if (value.startsWith("http://") || value.startsWith("https://")) {
							form.getEntries().add(new FormData(id, value));
							System.out.println("  " + id + ": " + value);
						} else {

							String[] tiles = value.split(",");
							System.out.println("  " + id + ":");
							for (String tile : tiles) {

								File file = new File(tile);

								if (file.exists()) {

									if (file.isDirectory()) {

										File[] files = file.listFiles();

										for (File subfile : files) {
											if (subfile.getAbsolutePath().endsWith(".vcf.gz")) {
												form.getEntries()
														.add(new FormData(id,
																new FileRepresentation(subfile.getAbsolutePath(),
																		MediaType.APPLICATION_OCTET_STREAM)));
												System.out.println("   - " + subfile.getAbsolutePath());
											}
										}

									} else {

										form.getEntries().add(new FormData(id,
												new FileRepresentation(tile, MediaType.APPLICATION_OCTET_STREAM)));
										System.out.println("    - " + tile);

									}

								} else {
									throw new CloudgeneAppException("File '" + file + "' not found.");
								}
							}
						}
					} else {
						form.getEntries().add(new FormData(id, value));
						System.out.println("  " + id + ": " + value);
					}
				} else {
					if (type.equals("checkbox")) {
						form.getEntries().add(new FormData(id, getValueByKey(param, "false")));
						System.out.println("  " + id + ": " + getValueByKey(param, "false"));
					}
				}
			}
		}

		return form;
	}

	public static boolean isFile(String type) {
		return (type.equals("local_folder") || type.equals("local_file") || type.equals("hdfs_folder")
				|| type.equals("hdfs_file"));
	}

	public static String getValueByKey(JSONObject param, String key) {
		JSONArray values = param.getJSONArray("values");

		for (int j = 0; j < values.length(); j++) {
			String key2 = values.getJSONObject(j).getString("key");
			String label = values.getJSONObject(j).getString("value");
			if (key.equals(key2)) {
				return label;
			}
		}

		return "unkown";
	}
	
	public static boolean isValidValue(JSONObject param, String key) {
		JSONArray values = param.getJSONArray("values");

		for (int j = 0; j < values.length(); j++) {
			String key2 = values.getJSONObject(j).getString("key");
			if (key.equals(key2)) {
				return true;
			}
		}

		return false;
	}

}
