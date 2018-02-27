package test;

import Logger.MaiLogger;
import org.junit.*;
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
    private static String dir = MaiLogger.class.getResource("").getPath() + "logs";
    private static String[] exp;
    private static String[] expAdd;
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
                "WARNING: test10"};
        expAdd = new String[]{
                "WARNING: MaiLogger has not been set up yet",
                "CRITICAL: Cannot stop application; MaiLogger has not been set up yet"};
        StringBuilder expAll = new StringBuilder();
        for (String s : exp) {
            expAll.append(expAdd[0]).append("\n");
            expAll.append(s).append("\n");
        }
        expAll.insert(338, expAdd[0] + "\n");
        expAll.insert(381, expAdd[1] + "\n");
        expAll.deleteCharAt(expAll.length() - 1);
        TestWithoutMainClass.expAll = expAll.toString();
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
    }

    @Test
    public void test1LogInfo () {
        MaiLogger.logInfo("test1");
        String exp = "WARNING: MaiLogger has not been set up yet\nINFO: test1";

        assertEquals(exp, remTime(MaiLogger.getLogAll()));
        assertEquals(exp, remTime(readFile(dir + "/Log.log")));
        assertEquals(1, new File(dir).listFiles().length);

        assertEquals("INFO: test1", remTime(MaiLogger.getLog(true, false, false, false)));
        assertEquals("WARNING: MaiLogger has not been set up yet", remTime(MaiLogger.getLog(false, true, false, false)));
        assertEquals("", MaiLogger.getLog(false, false, true, false));
        assertEquals("", MaiLogger.getLog(false, false, false, true));
    }

    @Test
    public void test2LogWarning () {
        MaiLogger.logWarning("test1");
        String exp = "WARNING: MaiLogger has not been set up yet\nWARNING: test1";

        assertEquals(exp, remTime(MaiLogger.getLogAll()));
        assertEquals(exp, remTime(readFile(dir + "/Log.log")));
        assertEquals(1, new File(dir).listFiles().length);

        assertEquals(exp, remTime(MaiLogger.getLog(false, true, false, false)));
        assertEquals("", MaiLogger.getLog(true, false, false, false));
        assertEquals("", MaiLogger.getLog(false, false, true, false));
        assertEquals("", MaiLogger.getLog(false, false, false, true));
    }

    @Test
    public void test3LogError () {
        MaiLogger.logError("test1");
        String exp = "WARNING: MaiLogger has not been set up yet\nERROR: test1";

        assertEquals(exp, remTime(MaiLogger.getLogAll()));
        assertEquals(exp, remTime(readFile(dir + "/Log.log")));
        assertEquals(1, new File(dir).listFiles().length);

        assertEquals("ERROR: test1", remTime(MaiLogger.getLog(false, false, true, false)));
        assertEquals("", MaiLogger.getLog(true, false, false, false));
        assertEquals("WARNING: MaiLogger has not been set up yet", remTime(MaiLogger.getLog(false, true, false, false)));
        assertEquals("", MaiLogger.getLog(false, false, false, true));
    }

    @Test
    public void test4LogCritical () {
        MaiLogger.logCritical("test1");
        String[] exp = new String[] {"WARNING: MaiLogger has not been set up yet",
                "CRITICAL: test1",
                "CRITICAL: Cannot stop application; MaiLogger has not been set up yet"};
        String expAll = exp[0] + "\n" + exp[1] + "\n" + exp[0] + "\n" + exp[2];
        assertEquals(expAll, remTime(MaiLogger.getLogAll()));
        assertEquals(expAll, remTime(readFile(dir + "/Log.log")));
        assertEquals(1, new File(dir).listFiles().length);

        assertEquals(exp[1]+ "\n" + exp[2], remTime(MaiLogger.getLog(false, false, false, true)));
        assertEquals("", MaiLogger.getLog(true, false, false, false));
        assertEquals("", MaiLogger.getLog(false, false, true, false));
        assertEquals(exp[0] + "\n" + exp[0], remTime(MaiLogger.getLog(false, true, false, false)));
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
        assertEquals(expAll, remTime(MaiLogger.getLogAll()));
        assertEquals(expAll, remTime(readFile(dir + "/Log.log")));
        assertEquals(1, new File(dir).listFiles().length);

        String expInfo = exp[0] + "\n" + exp[1] + "\n" + exp[4] + "\n" + exp[7];
        StringBuilder expWarning = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            if (i == 2 || i > 7) {
                expWarning.append(expAdd[0]).append("\n");
                expWarning.append(exp[i]).append("\n");
            } else {
                expWarning.append(expAdd[0]).append("\n");
            }
            if (i == 5) {
                expWarning.append(expAdd[0]).append("\n");
            }
        }
        expWarning.deleteCharAt(expWarning.length() - 1);
        String expError = exp[3] + "\n" + exp[6];
        String expCritical = exp[5] + "\n" + expAdd[1];
        assertEquals(expInfo, remTime(MaiLogger.getLog(true, false, false, false)));
        assertEquals(expWarning.toString(), remTime(MaiLogger.getLog(false, true, false, false)));
        assertEquals(expError, remTime(MaiLogger.getLog(false, false, true, false)));
        assertEquals(expCritical, remTime(MaiLogger.getLog(false, false, false, true)));
    }

    @Test
    public void test6ClearLog () {
        MaiLogger.logInfo("test1");
        assertEquals("WARNING: MaiLogger has not been set up yet\nINFO: test1", remTime(MaiLogger.getLogAll()));
        MaiLogger.clearLog();
        assertEquals("", remTime(MaiLogger.getLogAll()));
        MaiLogger.logWarning("test2");
        MaiLogger.logError("test3");
        assertEquals("WARNING: MaiLogger has not been set up yet\nWARNING: test2\nWARNING: MaiLogger has not been set up yet\nERROR: test3", remTime(MaiLogger.getLogAll()));
        MaiLogger.clearLog();
        assertEquals("", remTime(MaiLogger.getLogAll()));
        MaiLogger.logCritical("test4");
        assertEquals("WARNING: MaiLogger has not been set up yet\nCRITICAL: test4\nWARNING: MaiLogger has not been set up yet\nCRITICAL: Cannot stop application; MaiLogger has not been set up yet", remTime(MaiLogger.getLogAll()));
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
        MaiLogger.clearLog();
        assertEquals("", remTime(MaiLogger.getLogAll()));
        MaiLogger.load();
        assertEquals(expAll, remTime(MaiLogger.getLogAll()));
        MaiLogger.load();
        assertEquals(expAll, remTime(MaiLogger.getLogAll()));
    }

    @Test
    public void testRotate () {
        MaiLogger.logInfo("test1");
        assertEquals(1, new File(dir).listFiles().length);
        assertEquals("WARNING: MaiLogger has not been set up yet\nINFO: test1", remTime(readFile(dir + "/" + "Log.log")));
        MaiLogger.rotate();
        MaiLogger.logWarning("test2");
        MaiLogger.logInfo("test3");
        assertEquals(2, new File(dir).listFiles().length);
        assertEquals("WARNING: MaiLogger has not been set up yet\nWARNING: test2\nWARNING: MaiLogger has not been set up yet\nINFO: test3", remTime(readFile(dir + "/" + "Log.log")));
        assertEquals("WARNING: MaiLogger has not been set up yet\nINFO: test1", remTime(readFile(dir + "/" + "Log.log.1")));
        MaiLogger.rotate();
        MaiLogger.logError("test4");
        MaiLogger.logError("test5");
        MaiLogger.logInfo("test6");
        assertEquals(3, new File(dir).listFiles().length);
        assertEquals("WARNING: MaiLogger has not been set up yet\nERROR: test4\nWARNING: MaiLogger has not been set up yet\nERROR: test5\nWARNING: MaiLogger has not been set up yet\nINFO: test6", remTime(readFile(dir + "/" + "Log.log")));
        assertEquals("WARNING: MaiLogger has not been set up yet\nWARNING: test2\nWARNING: MaiLogger has not been set up yet\nINFO: test3", remTime(readFile(dir + "/" + "Log.log.1")));
        assertEquals("WARNING: MaiLogger has not been set up yet\nINFO: test1", remTime(readFile(dir + "/" + "Log.log.2")));
        MaiLogger.rotate();
        assertEquals(4, new File(dir).listFiles().length);
        assertEquals("", remTime(readFile(dir + "/" + "Log.log")));
        assertEquals("WARNING: MaiLogger has not been set up yet\nERROR: test4\nWARNING: MaiLogger has not been set up yet\nERROR: test5\nWARNING: MaiLogger has not been set up yet\nINFO: test6", remTime(readFile(dir + "/" + "Log.log.1")));
        assertEquals("WARNING: MaiLogger has not been set up yet\nWARNING: test2\nWARNING: MaiLogger has not been set up yet\nINFO: test3", remTime(readFile(dir + "/" + "Log.log.2")));
        assertEquals("WARNING: MaiLogger has not been set up yet\nINFO: test1", remTime(readFile(dir + "/" + "Log.log.3")));
        MaiLogger.rotate();
        MaiLogger.logCritical("test7");
        assertEquals(5, new File(dir).listFiles().length);
        assertEquals("WARNING: MaiLogger has not been set up yet\nCRITICAL: test7\nWARNING: MaiLogger has not been set up yet\nCRITICAL: Cannot stop application; MaiLogger has not been set up yet", remTime(readFile(dir + "/" + "Log.log")));
        assertEquals("", remTime(readFile(dir + "/" + "Log.log.1")));
        assertEquals("WARNING: MaiLogger has not been set up yet\nERROR: test4\nWARNING: MaiLogger has not been set up yet\nERROR: test5\nWARNING: MaiLogger has not been set up yet\nINFO: test6", remTime(readFile(dir + "/" + "Log.log.2")));
        assertEquals("WARNING: MaiLogger has not been set up yet\nWARNING: test2\nWARNING: MaiLogger has not been set up yet\nINFO: test3", remTime(readFile(dir + "/" + "Log.log.3")));
        assertEquals("WARNING: MaiLogger has not been set up yet\nINFO: test1", remTime(readFile(dir + "/" + "Log.log.4")));
    }

    @Test
    public void test9SetMainClasstoNull () {
        try {
            MaiLogger.setUp(null);
            fail("should through a NullpointerException");
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
