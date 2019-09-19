package genepi.imputationbutler.util;

import java.io.FileNotFoundException;
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

public class ComandlineOptionsUtil {

	public static Options createOptionsFromApp(JSONArray params) {
		Options options = new Options();
		for (int i = 0; i < params.length(); i++) {

			JSONObject param = params.getJSONObject(i);

			String type = param.getString("type");

			if (!type.equals("separator") && !type.equals("terms_checkbox") && !type.equals("info")
					&& !type.equals("group") && !type.equals("agbcheckbox")) {

				String description = param.getString("description");
				String id = param.getString("id");
				String value = param.getString("value");
				boolean required = param.getBoolean("required");
				boolean hasDefault = value != null && !value.trim().isEmpty();

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

	public static FormDataSet createForm(JSONArray params, CommandLine line) throws FileNotFoundException {
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

			if (!type.equals("separator") && !type.equals("terms_checkbox") && !type.equals("info")
					&& !type.equals("group") && !type.equals("agbcheckbox")) {

				if (props.containsKey(id)) {

					String value = props.get(id);

					if (type.equals("app_list")) {
						form.getEntries().add(new FormData(id, "apps@" + value));
						System.out.println("  " + id + ": " + "apps@" + value);
					} else if (isFile(type)) {
						if (value.startsWith("http://") || value.startsWith("https://")) {
							form.getEntries().add(new FormData(id, value));
							System.out.println("  " + id + ": " + value);
						} else {

							//split by,
							//if folder: add all vcf.gz files
							//if txt file: --> each line one file
							
							form.getEntries().add(new FormData(id,
									new FileRepresentation(value, MediaType.APPLICATION_OCTET_STREAM)));
							System.out.println("  " + id + ": " + value);
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

}
