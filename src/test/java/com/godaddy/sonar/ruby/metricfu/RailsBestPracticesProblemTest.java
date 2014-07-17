package com.godaddy.sonar.ruby.metricfu;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class RailsBestPracticesProblemTest
{

    @Test
    @SuppressWarnings("serial")
    public void testFromString() {
        Map<String,Object> map = new HashMap<String, Object>() {{
            put(":file", "a_file.rb");
            put(":line", "22");
            put(":problem", "This is bad code");
            put(":url", "http://rails-bestpractices.com/posts/baz.html");
        }};
        RailsBestPracticesProblem actual = RailsBestPracticesProblem.fromMap(map);

        assertEquals("a_file.rb", actual.file);
        assertEquals("22", actual.line);
        assertEquals("This is bad code", actual.problem);
        assertEquals("http://rails-bestpractices.com/posts/baz.html", actual.url);
    }

	@Test
    @SuppressWarnings("serial")
    public void testWithMissingValue() {
        Map<String,Object> map = new HashMap<String, Object>() {{
            put(":file", "a_file.rb");
            put(":line", "22");
            put(":problem", "This is bad code");
        }};
        RailsBestPracticesProblem actual = RailsBestPracticesProblem.fromMap(map);

        assertEquals("none", actual.url);
    }
}
