package com.godaddy.sonar.ruby.metricfu;

import java.util.Map;

/**
 * Models a "problem" from a rails_best_practices report.
 *
 * {:file=>
 * "/Users/mrowe/Projects/REA/cp/cp-agentadmin/app/controllers/agentdesktop/agencies/salespeople_controller.rb",
 * :line=>"22",
 * :problem=>"move model logic into model (@salesperson use_count > 4)",
 * :url=>
 * "http://rails-bestpractices.com/posts/7-move-model-logic-into-the-model"}
 */
public class RailsBestPracticesProblem
{
    public final String file;
    public final String line;
    public final String problem;
    public final String url;

    public static RailsBestPracticesProblem fromMap(Map<String, Object> map) {
        return new RailsBestPracticesProblem(
                safeGet(map, ":file"),
                safeGet(map, ":line"),
                safeGet(map, ":problem"),
                safeGet(map, ":url"));
    }

    public RailsBestPracticesProblem(String file, String line, String problem, String url) {
        this.file = file;
        this.line = line;
        this.problem = problem;
        this.url = url;
    }

    private static String safeGet(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v == null ? "none" : (String) v;
    }
}
