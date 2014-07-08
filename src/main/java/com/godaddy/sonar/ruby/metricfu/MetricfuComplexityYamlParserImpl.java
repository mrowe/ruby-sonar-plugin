package com.godaddy.sonar.ruby.metricfu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class MetricfuComplexityYamlParserImpl implements MetricfuComplexityYamlParser
{
    private static final Logger LOG = LoggerFactory.getLogger(MetricfuComplexityYamlParserImpl.class);
    private List<Map<String, Object>> saikuroFilesResult;

    @SuppressWarnings("unchecked")
    public List<RubyFunction> parseFunctions(String fileNameFromModule, File resultsFile) throws IOException
    {

        List<RubyFunction> rubyFunctionsForFile = new ArrayList<RubyFunction>();

        String fileString = FileUtils.readFileToString(resultsFile, "UTF-8");

        // remove ":hotspots:" section of the yaml so snakeyaml can parse it
        // correctly, snakeyaml throws an error with that section intact
        // Will remove if metric_fu metric filtering works for hotspots in the
        // future
        int hotSpotIndex = fileString.indexOf(":hotspots:");
        if (hotSpotIndex >= 0)
        {
            String stringToRemove = fileString.substring(hotSpotIndex, fileString.length());
            fileString = StringUtils.remove(fileString, stringToRemove);
        }

        loadSaikuroResults(fileString);
        Map<String, Object> fileInfoToWorkWith = findFileInResults(fileNameFromModule, saikuroFilesResult);

        if (fileInfoToWorkWith.size() == 0)
        {
            // file has no methods returning empty function list
            LOG.debug("No saikuro results found for " + fileNameFromModule);
            return new ArrayList<RubyFunction>();
        }

        ArrayList<Map<String, Object>> classesInfo = (ArrayList<Map<String, Object>>) fileInfoToWorkWith.get(":classes");

        for (Map<String, Object> classInfo : classesInfo)
        {
            ArrayList<Map<String, Object>> methods = (ArrayList<Map<String, Object>>) classInfo.get(":methods");

            for (Map<String, Object> method : methods)
            {
                RubyFunction rubyFunction = new RubyFunction();
                rubyFunction.setName((String) method.get(":name"));
                rubyFunction.setComplexity((Integer) method.get(":complexity"));
                rubyFunction.setLine((Integer) method.get(":lines"));

                rubyFunctionsForFile.add(rubyFunction);
            }
        }
        return rubyFunctionsForFile;
    }

    private Map<String, Object> findFileInResults(String filename, List<Map<String, Object>> results)
    {
        for (Map<String, Object> fileInfo : results) {
            String fileNameFromResults = (String) fileInfo.get(":filename");
            if (fileNameFromResults.contains(filename)) {
                return fileInfo;
            }
        }
        return Collections.emptyMap();
    }

    @SuppressWarnings("unchecked")
    private synchronized void loadSaikuroResults(String fileString) {
        if (saikuroFilesResult == null) {
            Yaml yaml = new Yaml();

            Map<String, Object> metricfuResult = (Map<String, Object>) yaml.loadAs(fileString, Map.class);
            Map<String, Object> saikuroResult = (Map<String, Object>) metricfuResult.get(":saikuro");
            saikuroFilesResult = (List<Map<String, Object>>) saikuroResult.get(":files");
        }
    }
}
