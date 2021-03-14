package test;

import logger.Group;
import logger.MaiLogger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestWithoutMainClass {
    private static String dir = MaiLogger.class.getResource("").getPath() + "/../logs";
    private static String[] exp;
    private static String expAll;

    @BeforeClass
    public static void setUp () {
        exp = new String[]{
                "INFO: test1",
                "INFO: test2",
                "WARNING: test3",
                "ERROR: test4",
                "INFO: test5",
                "CRITICAL: test6",
                "ERROR: test7",
                "INFO: test8",
                "WARNING: test9",
                "WARNING: test10",
                "DEBUG: test11"
        };
        StringBuilder expAll = new StringBuilder();
        for (String s : exp) {
            expAll.append(s).append("\n");
        }
        TestWithoutMainClass.expAll = expAll.toString();
    }

    @Before
    public void init () {
        MaiLogger.clearLog();
        List<File> files = new ArrayList<>();
        try {
            Files.list(new File(dir).toPath()).forEach(s -> files.add(new File(s.toString())));
            for (File f: files) {
                f.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1LogInfo () {
        MaiLogger.logInfo("test1");
        String exp = "INFO: test1\n";

        assertEquals(exp, remTime(MaiLogger.getLogAll()));
        assertEquals(exp, remTime(readFile(dir + "/Log.log")));
        assertEquals(1, new File(dir).listFiles().length);
    }

    @Test
    public void test2LogWarning () {
        MaiLogger.logWarning("test1");
        String exp = "WARNING: test1\n";

        assertEquals(exp, remTime(MaiLogger.getLogAll()));
        assertEquals(exp, remTime(readFile(dir + "/Log.log")));
        assertEquals(1, new File(dir).listFiles().length);

        assertEquals(exp, remTime(MaiLogger.getLog(Group.WARNING)));
    }

    @Test
    public void test3LogError () {
        MaiLogger.logError("test1");
        String exp = "ERROR: test1\n";

        assertEquals(exp, remTime(MaiLogger.getLogAll()));
        assertEquals(exp, remTime(readFile(dir + "/Log.log")));
        assertEquals(1, new File(dir).listFiles().length);

        assertEquals(exp, remTime(MaiLogger.getLog(Group.ERROR)));
    }

    @Test
    public void test4LogCritical () {
        MaiLogger.logCritical("test1");
        String exp = "CRITICAL: test1\n";
        assertEquals(exp, remTime(MaiLogger.getLogAll()));
        assertEquals(exp, remTime(readFile(dir + "/Log.log")));
        assertEquals(1, new File(dir).listFiles().length);

        assertEquals(exp, remTime(MaiLogger.getLog(Group.CRITICAL)));
    }

    @Test
    public void test5MultipleLog () {
        MaiLogger.logInfo("test1");
        MaiLogger.logInfo("test2");
        MaiLogger.logWarning("test3");
        MaiLogger.logError("test4");
        MaiLogger.logInfo("test5");
        MaiLogger.logCritical("test6");
        MaiLogger.logError("test7");
        MaiLogger.logInfo("test8");
        MaiLogger.logWarning("test9");
        MaiLogger.logWarning("test10");
        MaiLogger.logDebug("test11");
        assertEquals(expAll, remTime(MaiLogger.getLogAll()));
        assertEquals(expAll, remTime(readFile(dir + "/Log.log")));
        assertEquals(1, new File(dir).listFiles().length);

        String expInfo = exp[0] + "\n" + exp[1] + "\n" + exp[4] + "\n" + exp[7] + "\n";
        String expWarning = exp[2] + "\n" + exp[8] + "\n" + exp[9] + "\n";
        String expError = exp[3] + "\n" + exp[6] + "\n";
        String expCritical = exp[5] + "\n";
        String expDebug = exp[10] + "\n";
        assertEquals(expInfo, remTime(MaiLogger.getLog(Group.INFO)));
        assertEquals(expWarning, remTime(MaiLogger.getLog(Group.WARNING)));
        assertEquals(expError, remTime(MaiLogger.getLog(Group.ERROR)));
        assertEquals(expCritical, remTime(MaiLogger.getLog(Group.CRITICAL)));
        assertEquals(expDebug, remTime(MaiLogger.getLog(Group.DEBUG)));
        assertEquals("", remTime(MaiLogger.getLog(Group.TASK, Group.OTHER)));
    }

    @Test
    public void test6ClearLog () {
        MaiLogger.logInfo("test1");
        assertEquals("INFO: test1\n", remTime(MaiLogger.getLogAll()));
        MaiLogger.clearLog();
        assertEquals("", remTime(MaiLogger.getLogAll()));
        MaiLogger.logWarning("test2");
        MaiLogger.logError("test3");
        assertEquals("WARNING: test2\nERROR: test3\n", remTime(MaiLogger.getLogAll()));
        MaiLogger.clearLog();
        assertEquals("", remTime(MaiLogger.getLogAll()));
        MaiLogger.logCritical("test4");
        assertEquals("CRITICAL: test4\n", remTime(MaiLogger.getLogAll()));
        MaiLogger.clearLog();
        assertEquals("", remTime(MaiLogger.getLogAll()));
    }

    @Test
    public void test7Load () {
        MaiLogger.logInfo("test1");
        MaiLogger.logInfo("test2");
        MaiLogger.logWarning("test3");
        MaiLogger.logError("test4");
        MaiLogger.logInfo("test5");
        MaiLogger.logCritical("test6");
        MaiLogger.logError("test7");
        MaiLogger.logInfo("test8");
        MaiLogger.logWarning("test9");
        MaiLogger.logWarning("test10");
        MaiLogger.logDebug("test11");
        MaiLogger.clearLog();
        assertEquals("", remTime(MaiLogger.getLogAll()));
        MaiLogger.load();
        assertEquals(expAll, remTime(MaiLogger.getLogAll()));
        MaiLogger.load();
        assertEquals(expAll, remTime(MaiLogger.getLogAll()));
    }

    @Test
    public void test8Rotate () {
        MaiLogger.logInfo("test1");
        assertEquals(1, new File(dir).listFiles().length);
        assertEquals("INFO: test1\n", remTime(readFile(dir + "/" + "Log.log")));
        MaiLogger.rotate();
        MaiLogger.logWarning("test2");
        MaiLogger.logInfo("test3");
        assertEquals(2, new File(dir).listFiles().length);
        assertEquals("WARNING: test2\nINFO: test3\n", remTime(readFile(dir + "/" + "Log.log")));
        assertEquals("INFO: test1\n", remTime(readFile(dir + "/" + "Log.log.1")));
        MaiLogger.rotate();
        MaiLogger.logError("test4");
        MaiLogger.logError("test5");
        MaiLogger.logDebug("test6");
        assertEquals(3, new File(dir).listFiles().length);
        assertEquals("ERROR: test4\nERROR: test5\nDEBUG: test6\n", remTime(readFile(dir + "/" + "Log.log")));
        assertEquals("WARNING: test2\nINFO: test3\n", remTime(readFile(dir + "/" + "Log.log.1")));
        assertEquals("INFO: test1\n", remTime(readFile(dir + "/" + "Log.log.2")));
        MaiLogger.rotate();
        assertEquals(4, new File(dir).listFiles().length);
        assertEquals("", remTime(readFile(dir + "/" + "Log.log")));
        assertEquals("ERROR: test4\nERROR: test5\nDEBUG: test6\n", remTime(readFile(dir + "/" + "Log.log.1")));
        assertEquals("WARNING: test2\nINFO: test3\n", remTime(readFile(dir + "/" + "Log.log.2")));
        assertEquals("INFO: test1\n", remTime(readFile(dir + "/" + "Log.log.3")));
        MaiLogger.rotate();
        MaiLogger.logCritical("test7");
        assertEquals(5, new File(dir).listFiles().length);
        assertEquals("CRITICAL: test7\n", remTime(readFile(dir + "/" + "Log.log")));
        assertEquals("", remTime(readFile(dir + "/" + "Log.log.1")));
        assertEquals("ERROR: test4\nERROR: test5\nDEBUG: test6\n", remTime(readFile(dir + "/" + "Log.log.2")));
        assertEquals("WARNING: test2\nINFO: test3\n", remTime(readFile(dir + "/" + "Log.log.3")));
        assertEquals("INFO: test1\n", remTime(readFile(dir + "/" + "Log.log.4")));
    }

    @Test
    public void test9SetMainClassToNull () {
        try {
            MaiLogger.setUp(null);
            fail("should through a NullPointerException");
        } catch (NullPointerException e) {

        }
    }


    private static String readFile (String path) {
        StringBuilder content = new StringBuilder();
        try (Stream<String> str = Files.lines(new File(path).toPath())) {
            str.forEach(s -> content.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    private static String remTime (String exp) {
        if (exp.equals("")) {
            return "";
        }
        StringBuilder exp2 = new StringBuilder();
        try {
            Arrays.stream(exp.split("\n")).forEach(s -> exp2.append(s.substring(18)).append("\n"));
        } catch (IndexOutOfBoundsException e) {
            return "Failed to remove time in expression:\n" + exp;
        }
        return exp2.toString();
    }



}
