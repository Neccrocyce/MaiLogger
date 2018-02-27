package test;

import logger.MaiLogger;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestWithSimpleMainClass {
    private static MainClass mc;
    private static String dir = MaiLogger.class.getResource("").getPath() + "logs";

    @BeforeClass
    public static void setUp () {
        mc = new MainClass();
        MaiLogger.setUp(mc);
    }

    @After
    public void cleanUp () {
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

    @Before
    public void init() {
        MaiLogger.clearLog();
        mc.clearLog();
        mc.stopped = false;
    }

    @Test
    public void test1LogInfo1 () {
        MaiLogger.logInfo("test1");
        String exp = "INFO: test1";

        assertEquals(exp, MaiLogger.getLogAll().substring(18));
        assertEquals(exp, readFile(dir + "/mainclass.log").substring(18));
        assertEquals(exp, mc.getLog().substring(18));
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);

        assertEquals(exp, MaiLogger.getLog(true, false, false, false).substring(18));
        assertEquals("", MaiLogger.getLog(false, true, false, false));
        assertEquals("", MaiLogger.getLog(false, false, true, false));
        assertEquals("", MaiLogger.getLog(false, false, false, true));
    }

    @Test
    public void test2LogWarning () {
        MaiLogger.logWarning("test1");
        String exp = "WARNING: test1";

        assertEquals(exp, MaiLogger.getLogAll().substring(18));
        assertEquals(exp, readFile(dir + "/mainclass.log").substring(18));
        assertEquals(exp, mc.getLog().substring(18));
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);

        assertEquals(exp, MaiLogger.getLog(false, true, false, false).substring(18));
        assertEquals("", MaiLogger.getLog(true, false, false, false));
        assertEquals("", MaiLogger.getLog(false, false, true, false));
        assertEquals("", MaiLogger.getLog(false, false, false, true));
    }

    @Test
    public void test3LogError () {
        MaiLogger.logError("test1");
        String exp = "ERROR: test1";

        assertEquals(exp, MaiLogger.getLogAll().substring(18));
        assertEquals(exp, readFile(dir + "/mainclass.log").substring(18));
        assertEquals(exp, mc.getLog().substring(18));
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);

        assertEquals(exp, MaiLogger.getLog(false, false, true, false).substring(18));
        assertEquals("", MaiLogger.getLog(true, false, false, false));
        assertEquals("", MaiLogger.getLog(false, true, false, false));
        assertEquals("", MaiLogger.getLog(false, false, false, true));
    }

    @Test
    public void test4LogCritical () {
        MaiLogger.logCritical("test1");
        String exp = "CRITICAL: test1";

        assertEquals(exp, MaiLogger.getLogAll().substring(18));
        assertEquals(exp, readFile(dir + "/mainclass.log").substring(18));
        assertEquals(exp, mc.getLog().substring(18));
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);
        assert(mc.stopped);

        assertEquals(exp, MaiLogger.getLog(false, false, false, true).substring(18));
        assertEquals("", MaiLogger.getLog(true, false, false, false));
        assertEquals("", MaiLogger.getLog(false, false, true, false));
        assertEquals("", MaiLogger.getLog(false, true, false, false));
    }

    @Test
    public void test5MultpleLog () {
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
        String exp =
                "INFO: test1\n"
                + "INFO: test2\n"
                + "WARNING: test3\n"
                + "ERROR: test4\n"
                + "INFO: test5\n"
                + "CRITICAL: test6\n"
                + "ERROR: test7\n"
                + "INFO: test8\n"
                + "WARNING: test9\n"
                + "WARNING: test10";
        assertEquals(exp, remTime(MaiLogger.getLogAll()));
        assertEquals(exp, remTime(readFile(dir + "/mainclass.log")));
        assertEquals(exp, remTime(mc.getLog()));
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);

        String expInfo = "INFO: test1\nINFO: test2\nINFO: test5\nINFO: test8";
        String expWarning = "WARNING: test3\nWARNING: test9\nWARNING: test10";
        String expError = "ERROR: test4\nERROR: test7";
        String expCritical = "CRITICAL: test6";
        assertEquals(expInfo, remTime(MaiLogger.getLog(true, false, false, false)));
        assertEquals(expWarning, remTime(MaiLogger.getLog(false, true, false, false)));
        assertEquals(expError, remTime(MaiLogger.getLog(false, false, true, false)));
        assertEquals(expCritical, remTime(MaiLogger.getLog(false, false, false, true)));

        String expInWa = "INFO: test1\n"
                + "INFO: test2\n"
                + "WARNING: test3\n"
                + "INFO: test5\n"
                + "INFO: test8\n"
                + "WARNING: test9\n"
                + "WARNING: test10";
        String expWaCr = "WARNING: test3\n"
                + "CRITICAL: test6\n"
                + "WARNING: test9\n"
                + "WARNING: test10";
        String expInEr = "INFO: test1\n"
                + "INFO: test2\n"
                + "ERROR: test4\n"
                + "INFO: test5\n"
                + "ERROR: test7\n"
                + "INFO: test8";
        assertEquals(expInWa, remTime(MaiLogger.getLog(true, true, false, false)));
        assertEquals(expWaCr, remTime(MaiLogger.getLog(false, true, false, true)));
        assertEquals(expInEr, remTime(MaiLogger.getLog(true, false, true, false)));
    }

    @Test
    public void test6ClearLog () {
        MaiLogger.logInfo("test1");
        assertEquals("INFO: test1", remTime(MaiLogger.getLogAll()));
        MaiLogger.clearLog();
        assertEquals("", MaiLogger.getLogAll());
        MaiLogger.logWarning("test2");
        MaiLogger.logError("test3");
        assertEquals("WARNING: test2\nERROR: test3", remTime(MaiLogger.getLogAll()));
        MaiLogger.clearLog();
        assertEquals("", MaiLogger.getLogAll());
        MaiLogger.logCritical("test4");
        assertEquals("CRITICAL: test4", remTime(MaiLogger.getLogAll()));
        MaiLogger.clearLog();
        assertEquals("", MaiLogger.getLogAll());
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
        String exp =
                "INFO: test1\n"
                        + "INFO: test2\n"
                        + "WARNING: test3\n"
                        + "ERROR: test4\n"
                        + "INFO: test5\n"
                        + "CRITICAL: test6\n"
                        + "ERROR: test7\n"
                        + "INFO: test8\n"
                        + "WARNING: test9\n"
                        + "WARNING: test10";
        MaiLogger.clearLog();
        assertEquals("", remTime(MaiLogger.getLogAll()));
        MaiLogger.load();
        assertEquals(exp, remTime(MaiLogger.getLogAll()));
        MaiLogger.load();
        assertEquals(exp, remTime(MaiLogger.getLogAll()));
    }

    @Test
    public void testRotate () {
        MaiLogger.logInfo("test1");
        assertEquals(1, new File(dir).listFiles().length);
        assertEquals("INFO: test1", remTime(readFile(dir + "/" + "mainclass.log")));
        MaiLogger.rotate();
        MaiLogger.logWarning("test2");
        MaiLogger.logInfo("test3");
        assertEquals(2, new File(dir).listFiles().length);
        assertEquals("WARNING: test2\nINFO: test3", remTime(readFile(dir + "/" + "mainclass.log")));
        assertEquals("INFO: test1", remTime(readFile(dir + "/" + "mainclass.log.1")));
        MaiLogger.rotate();
        MaiLogger.logError("test4");
        MaiLogger.logError("test5");
        MaiLogger.logInfo("test6");
        assertEquals(3, new File(dir).listFiles().length);
        assertEquals("ERROR: test4\nERROR: test5\nINFO: test6", remTime(readFile(dir + "/" + "mainclass.log")));
        assertEquals("WARNING: test2\nINFO: test3", remTime(readFile(dir + "/" + "mainclass.log.1")));
        assertEquals("INFO: test1", remTime(readFile(dir + "/" + "mainclass.log.2")));
        MaiLogger.rotate();
        assertEquals(4, new File(dir).listFiles().length);
        assertEquals("", remTime(readFile(dir + "/" + "mainclass.log")));
        assertEquals("ERROR: test4\nERROR: test5\nINFO: test6", remTime(readFile(dir + "/" + "mainclass.log.1")));
        assertEquals("WARNING: test2\nINFO: test3", remTime(readFile(dir + "/" + "mainclass.log.2")));
        assertEquals("INFO: test1", remTime(readFile(dir + "/" + "mainclass.log.3")));
        MaiLogger.rotate();
        MaiLogger.logCritical("test7");
        assertEquals(5, new File(dir).listFiles().length);
        assertEquals("CRITICAL: test7", remTime(readFile(dir + "/" + "mainclass.log")));
        assertEquals("", remTime(readFile(dir + "/" + "mainclass.log.1")));
        assertEquals("ERROR: test4\nERROR: test5\nINFO: test6", remTime(readFile(dir + "/" + "mainclass.log.2")));
        assertEquals("WARNING: test2\nINFO: test3", remTime(readFile(dir + "/" + "mainclass.log.3")));
        assertEquals("INFO: test1", remTime(readFile(dir + "/" + "mainclass.log.4")));
    }



    private static String readFile (String path) {
        StringBuilder content = new StringBuilder();
        try (Stream<String> str = Files.lines(new File(path).toPath())) {
            str.forEach(s -> content.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (content.length() > 0) {
            content.deleteCharAt(content.length() - 1);
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
        if (exp2.length() > 0) {
            exp2.deleteCharAt(exp2.length() - 1);
        }
        return exp2.toString();
    }



}
