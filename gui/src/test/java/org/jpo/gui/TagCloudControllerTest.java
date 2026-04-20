package org.jpo.gui;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class TagCloudControllerTest {

    @Test
    void testAddWord() {
        final Map<String, Integer> wordCountMap = new HashMap<>();

        try {
            // user reflection to find the inner class
            final Class<?> innerClass = Class.forName("org.jpo.gui.TagCloudController$NodeWordMapper");
            // the private method
            final Method addWordMethod = innerClass.getDeclaredMethod("addWord", Map.class, String.class);
            addWordMethod.setAccessible(true);
            // and invoke the static method on the null object
            addWordMethod.invoke(null, wordCountMap, "Hello");
            assertEquals(1, wordCountMap.get("Hello"));
        } catch (final NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testSplitAndAdd() {
        final Map<String, Integer> wordCountMap = new HashMap<>();

        try {
            // user reflection to find the inner class
            final Class<?> innerClass = Class.forName("org.jpo.gui.TagCloudController$NodeWordMapper");
            // the private method
            final Method addWordMethod = innerClass.getDeclaredMethod("splitAndAdd", Map.class, String.class);
            addWordMethod.setAccessible(true);
            // and invoke the static method on the null object
            addWordMethod.invoke(null, wordCountMap, "Hello World");
            assertEquals(1, wordCountMap.get("Hello"));
            assertEquals(1, wordCountMap.get("World"));
        } catch (final NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testSplitAndAddApostrophe() {
        final Map<String, Integer> wordCountMap = new HashMap<>();

        try {
            // user reflection to find the inner class
            final Class<?> innerClass = Class.forName("org.jpo.gui.TagCloudController$NodeWordMapper");
            // the private method
            final Method addWordMethod = innerClass.getDeclaredMethod("splitAndAdd", Map.class, String.class);
            addWordMethod.setAccessible(true);
            // and invoke the static method on the null object
            addWordMethod.invoke(null, wordCountMap, "Fingal's Cave");
            assertEquals(1, wordCountMap.get("Fingal"));
            assertEquals(1, wordCountMap.get("Cave"));
        } catch (final NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void testSplitAndAddNoNumbers() {
        final Map<String, Integer> wordCountMap = new HashMap<>();

        try {
            // user reflection to find the inner class
            final Class<?> innerClass = Class.forName("org.jpo.gui.TagCloudController$NodeWordMapper");
            // the private method
            final Method addWordMethod = innerClass.getDeclaredMethod("splitAndAdd", Map.class, String.class);
            addWordMethod.setAccessible(true);
            // and invoke the static method on the null object
            addWordMethod.invoke(null, wordCountMap, "Fingal's Cave, has - more - than 22 Basalt Columns! Really!");
            assertEquals(1, wordCountMap.get("Fingal"));
            assertEquals(1, wordCountMap.get("Cave"));
            assertEquals(1, wordCountMap.get("Basalt"));
            assertEquals(1, wordCountMap.get("Columns"));
            assertEquals(1, wordCountMap.get("Really"));
        } catch (final NoSuchMethodException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
            fail(e.getMessage());
        }
    }


}