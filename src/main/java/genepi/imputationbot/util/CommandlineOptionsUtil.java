package genepi.imputationbot.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import genepi.imputationbot.client.CloudgeneAppException;

public class CommandlineOptionsUtil {

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
					// details in print AppList and bindedList
					description += "\n(see below)";
					option.setArgName(id);
				} else if (type.equals("binded_list")) {
					option.setArgName(id);
					description += "\n(see below)";
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
					description += "\n(default: " + value + ")";
				} else {
					// description += "\n[Required]";
				}

				option.setDescription(description);
				options.addOption(option);

			}
		}
		return options;

	}

	public static MultipartEntityBuilder createForm(JSONArray params, CommandLine line) throws Exception {
		Map<String, String> props = new HashMap<String, String>();

		for (int i = 0; i < params.length(); i++) {
			JSONObject param = params.getJSONObject(i);
			String id = param.getString("id");
			String defaultValue = param.getString("value");
			String value = line.getOptionValue(id, defaultValue);
			props.put(id, value);
		}

		System.out.println("  Parameters:");

		MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();

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
						String[] tiles = value.split(",");
						String temp = "";
						for (int j = 0; j < tiles.length; j++) {
							String tile = tiles[j];
							String value2 = createAppId(param, tile);
							if (!isValidValue(param, value2)) {
								throw new CloudgeneAppException("Value '" + value + "' is not a valid option for '"
										+ param.getString("id") + "'.");
							}
							if (j > 0) {
								temp += ",";
							}
							temp += value2;

						}
						multipartEntityBuilder.addTextBody(id, temp);
						System.out.println("    " + id + ": " + temp);
					} else if (type.equals("list")) {
						if (!isValidValue(param, value)) {
							throw new CloudgeneAppException(
									"Value '" + value + "' is not a valid option for '" + param.getString("id") + "'.");
						}
						multipartEntityBuilder.addTextBody(id, value);
						System.out.println("    " + id + ": " + value);
					} else if (type.equals("binded_list")) {
						String bind = param.getString("bind");
						String[] tiles = props.get(bind).split(",");
						for (int j = 0; j < tiles.length; j++) {
							String valueBind = createAppId(params, bind, tiles[j]);
							if (!isValidBindedValue(param, valueBind, value)) {
								throw new CloudgeneAppException(
										"Value '" + value + "' is not a valid option for '" + param.getString("id")
												+ "' in combination with " + bind + " '" + valueBind + "'");
							}
						}
						multipartEntityBuilder.addTextBody(id, value);
						System.out.println("    " + id + ": " + value);
					} else if (isFile(type)) {
						if (value.startsWith("http://") || value.startsWith("https://")) {
							multipartEntityBuilder.addTextBody(id, value);
							System.out.println("    " + id + ": " + value);
						} else {

							String[] tiles = value.split(",");
							System.out.println("    " + id + ":");
							for (String tile : tiles) {

								File file = new File(tile);

								if (file.exists()) {

									if (file.isDirectory()) {

										File[] files = file.listFiles();

										for (File subfile : files) {
											if (subfile.getAbsolutePath().endsWith(".vcf.gz")) {
												multipartEntityBuilder.addBinaryBody(id, subfile);
												System.out.println("    - " + subfile.getAbsolutePath());
											}
										}

									} else {

										multipartEntityBuilder.addBinaryBody(id, file);
										System.out.println("      - " + tile);

									}

								} else {
									throw new CloudgeneAppException("File '" + file + "' not found.");
								}
							}
						}
					} else if (type.equals("checkbox")) {
						String trueValue = getValueByKey(param, "true");
						if (value.equals(trueValue)) {
							multipartEntityBuilder.addTextBody(id, trueValue);
						}
						System.out.println("    " + id + ": " + trueValue);
					} else {
						System.out.println("    " + id + ": " + getValueByKey(param, "false"));
					}
				} else {
					if (type.equals("checkbox")) {
						System.out.println("    " + id + ": " + getValueByKey(param, "false"));
					}
				}
			}
		}

		return multipartEntityBuilder;
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

	public static String createAppId(JSONObject param, String key) {
		JSONArray values = param.getJSONArray("values");

		for (int j = 0; j < values.length(); j++) {
			String key2 = values.getJSONObject(j).getString("key");
			if (key.equals(prettyAppId(key2))) {
				return key2;
			}
		}

		return key;
	}

	public static String createAppId(JSONArray params, String paramId, String key) {
		for (int i = 0; i < params.length(); i++) {
			JSONObject param = params.getJSONObject(i);
			if (param.getString("id").equals(paramId)) {
				return createAppId(param, key);
			}
		}
		return key;
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

	public static boolean isValidBindedValue(JSONObject param, String valueBind, String key) {

		for (int i = 0; i < param.getJSONArray("values").length(); i++) {

			JSONObject object = param.getJSONArray("values").getJSONObject(i);

			if (object.getString("key").equals(valueBind)) {

				JSONArray values = object.getJSONArray("values");

				for (int j = 0; j < values.length(); j++) {
					String key2 = values.getJSONObject(j).getString("key");
					if (key.equals(key2)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static String prettyAppId(String id) {
		String[] tiles = id.split("@");
		return (tiles.length > 1 ? tiles[1] : id);
	}

}
