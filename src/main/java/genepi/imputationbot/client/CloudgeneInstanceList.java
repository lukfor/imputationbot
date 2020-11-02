package genepi.imputationbot.client;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;

public class CloudgeneInstanceList {

	private List<CloudgeneInstance> instances = new Vector<CloudgeneInstance>();

	public void add(CloudgeneInstance instance) {
		instances.add(instance);
	}

	public Collection<CloudgeneInstance> getAll() {
		return Collections.unmodifiableCollection(instances);
	}

	public void remove(CloudgeneInstance instance) {
		instances.remove(instance);
	}

	public CloudgeneInstance getByHostname(String hostname) {
		for (CloudgeneInstance instance : instances) {
			if (instance.getHostname().equalsIgnoreCase(hostname)) {
				return instance;
			}
		}
		return null;
	}

	public CloudgeneInstance getById(int id) {
		return instances.get(id - 1);
	}

	public boolean isEmpty() {
		return instances.isEmpty();
	}

	public CloudgeneInstance getByReferencePanel(String referencePanel)
			throws CloudgeneException, CloudgeneAppException {
		for (CloudgeneInstance instance : instances) {
			if (instance.getReferencePanels().contains(referencePanel)) {
				return instance;
			}
		}
		return null;
	}

	public void save(String filename) throws IOException {
		YamlWriter writer = new YamlWriter(new FileWriter(filename));
		writer.getConfig().writeConfig.setWriteClassname(YamlConfig.WriteClassName.NEVER);
		writer.write(instances);
		writer.close();
	}

	public static CloudgeneInstanceList load(String filename) throws IOException {
		YamlReader reader = new YamlReader(new FileReader(filename));
		CloudgeneInstanceList instanceList = new CloudgeneInstanceList();
		instanceList.instances = reader.read(List.class, CloudgeneInstance.class);
		return instanceList;
	}

}
