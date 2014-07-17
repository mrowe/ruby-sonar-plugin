package com.godaddy.sonar.ruby.metricfu;

import static com.godaddy.sonar.ruby.metricfu.RailsBestPracticesProblem.fromMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.issue.Issue;
import org.sonar.api.issue.internal.DefaultIssue;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.File;
import org.sonar.api.resources.Project;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rule.Severity;
import org.sonar.api.scan.filesystem.FileQuery;
import org.sonar.api.scan.filesystem.ModuleFileSystem;
import org.yaml.snakeyaml.Yaml;

import com.godaddy.sonar.ruby.core.Ruby;

public class FlayDuplicationSensor implements Sensor
{

	    private static final Logger LOG = LoggerFactory.getLogger(FlayDuplicationSensor.class);

	    private final ModuleFileSystem moduleFileSystem;
	    private final ResourcePerspectives resourcePerspectives;
	    private List<Map<String, Object>> problems;

	    public FlayDuplicationSensor(ModuleFileSystem moduleFileSystem, ResourcePerspectives resourcePerspectives)
	    {
	        this.moduleFileSystem = moduleFileSystem;
	        this.resourcePerspectives = resourcePerspectives;
	    }

	    @Override
	    public void analyse(Project project, SensorContext context) {
	    	
	    	LOG.debug(String.format("Analysing project: %s", project));
	    	
	        String results_file = System.getProperty("project.home") + java.io.File.separator + "tmp/metric_fu/report.yml";
	        try {
	            loadProblemsFromResultsFile(results_file);
	        } catch (IOException e) {
	            LOG.error(String.format("Cannot load metric_fu results from %s: %s", results_file, e.getLocalizedMessage()), e);
	        }

	        //List<File> sourceDirs = moduleFileSystem.sourceDirs();
	        List<java.io.File> rubyFilesInProject = moduleFileSystem.files(FileQuery.onSource().onLanguage(project.getLanguageKey()));

	        for (java.io.File file : rubyFilesInProject) {
	            String relativePath = file.getAbsolutePath().replaceFirst(System.getProperty("project.home"), "");
	            
	            File sonarFile = new File(relativePath);
	            LOG.debug(String.format("Got SonarFile for %s: %s", relativePath, sonarFile));
	            sonarFile.setEffectiveKey(relativePath);
	            
	            Double duplication_mass = analyzeFile(file.getName(), sonarFile);
	            context.saveMeasure(sonarFile, CoreMetrics.DUPLICATED_LINES_DENSITY, duplication_mass);

	        }
	    }
	    
	    private Double analyzeFile(final String filename, final File sourceFile) {
	        List<RailsBestPracticesProblem> problems = findProblemsForFile(filename);
	        for (RailsBestPracticesProblem problem : problems) {

	            
	            }
	        return 0.0;
	    }

	    @Override
	    public boolean shouldExecuteOnProject(Project project) {
	        return Ruby.KEY.equals(project.getLanguageKey());
	    }

	    private List<RailsBestPracticesProblem> findProblemsForFile(String filename)
	    {
	        List<RailsBestPracticesProblem> fileProblems = new ArrayList<RailsBestPracticesProblem>();
	        for (Map<String, Object> problem : problems) {
	            String file = (String) problem.get(":file");
	            if (file.contains(filename)) {
	                fileProblems.add(fromMap(problem));
	            }
	        }
	        
	        return fileProblems;
	    }

	    @SuppressWarnings("unchecked")
	    private synchronized void loadProblemsFromResultsFile(String filepath) throws IOException {
	        if (problems == null) {
	            Yaml yaml = new Yaml();

	            String content = FileUtils.readFileToString(new java.io.File(filepath), "UTF-8");

	            Map<String, Object> metricfuResult = (Map<String, Object>) yaml.loadAs(content, Map.class);
	            Map<String, Object> railsBestPracticesResults = (Map<String, Object>) metricfuResult.get(":flay");
	            problems = (List<Map<String, Object>>) railsBestPracticesResults.get(":matches");
	        }
	    }
	}
