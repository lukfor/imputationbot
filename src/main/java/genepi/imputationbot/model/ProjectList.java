package genepi.imputationbot.model;

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

public class ProjectList {

	private List<Project> projects;

	public ProjectList() {
		projects = new Vector<Project>();
	}

	public void add(Project project) {
		projects.add(project);
	}

	public Collection<Project> getProjects() {
		return Collections.unmodifiableCollection(projects);
	}

	public void remove(Project project) {
		projects.remove(project);
	}

	public Project getByName(String name) {
		for (Project project : projects) {
			if (project.getName().equalsIgnoreCase(name)) {
				return project;
			}
		}
		return null;
	}

	public void save(String filename) throws IOException {
		YamlWriter writer = new YamlWriter(new FileWriter(filename));
		writer.getConfig().writeConfig.setWriteClassname(YamlConfig.WriteClassName.NEVER);
		writer.write(projects);
		writer.close();
	}

	public static ProjectList load(String filename) throws IOException {
		YamlReader reader = new YamlReader(new FileReader(filename));
		ProjectList projectList = new ProjectList();
		projectList.projects = reader.read(List.class, Project.class);
		return projectList;
	}

}
