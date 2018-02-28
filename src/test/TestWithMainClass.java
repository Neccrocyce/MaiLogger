package test;

import logger.MaiLogger;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestWithMainClass {
    private static MainClass mc;
    private static String dir = MaiLogger.class.getResource("").getPath() + "/../logs";

    @BeforeClass
    public static void setUp () {
        mc = new MainClass();
        MaiLogger.setUp(mc, 5, 3, false, dir);
    }

    public void cleanUp () {
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
        MaiLogger.setUp(mc, 5, 3, false, dir);
    }

    @Before
    public void init() {
        cleanUp();
        mc.clearLog();
        mc.stopped = false;
    }

    @Test
    public void test0SetUp () {
        MaiLogger.logInfo("test1");
        MaiLogger.logWarning("test2");
        MaiLogger.logError("test3");
        String exp = "INFO: test1\nWARNING: test2\nERROR: test3" + "\n";
        assertEquals(exp, readFile(dir + "/mainclass.log"));
        assertEquals(exp, MaiLogger.getLogAll());

        MaiLogger.setUp(mc, 6, 5, false, dir);
        assertEquals(exp, readFile(dir + "/mainclass.log"));
        assertEquals(exp, MaiLogger.getLogAll());
        MaiLogger.clearLog();

        MaiLogger.setUp(mc, 6, 5, false, dir);
        assertEquals(exp, readFile(dir + "/mainclass.log"));
        assertEquals("", MaiLogger.getLogAll());

        MaiLogger.load();
        MaiLogger.setUp(mc, 2, 5, false, dir);
        MaiLogger.logInfo("test1");
        assertEquals("ERROR: test3\nINFO: test1\n", readFile(dir + "/mainclass.log"));
        assertEquals("ERROR: test3\nINFO: test1\n", MaiLogger.getLogAll());
    }

    @Test
    public void test1LogInfo () {
        MaiLogger.logInfo("test1");
        String exp = "INFO: test1\n";

        assertEquals(exp, MaiLogger.getLogAll());
        assertEquals(exp, readFile(dir + "/mainclass.log"));
        assertEquals(exp, mc.getLog());
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);

        assertEquals(exp, MaiLogger.getLog(true, false, false, false));
        assertEquals("", MaiLogger.getLog(false, true, false, false));
        assertEquals("", MaiLogger.getLog(false, false, true, false));
        assertEquals("", MaiLogger.getLog(false, false, false, true));
    }

    @Test
    public void test2LogWarning () {
        MaiLogger.logWarning("test1");
        String exp = "WARNING: test1\n";

        assertEquals(exp, MaiLogger.getLogAll());
        assertEquals(exp, readFile(dir + "/mainclass.log"));
        assertEquals(exp, mc.getLog());
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);

        assertEquals(exp, MaiLogger.getLog(false, true, false, false));
        assertEquals("", MaiLogger.getLog(true, false, false, false));
        assertEquals("", MaiLogger.getLog(false, false, true, false));
        assertEquals("", MaiLogger.getLog(false, false, false, true));
    }

    @Test
    public void test3LogError () {
        MaiLogger.logError("test1");
        String exp = "ERROR: test1\n";

        assertEquals(exp, MaiLogger.getLogAll());
        assertEquals(exp, readFile(dir + "/mainclass.log"));
        assertEquals(exp, mc.getLog());
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);

        assertEquals(exp, MaiLogger.getLog(false, false, true, false));
        assertEquals("", MaiLogger.getLog(true, false, false, false));
        assertEquals("", MaiLogger.getLog(false, true, false, false));
        assertEquals("", MaiLogger.getLog(false, false, false, true));
    }

    @Test
    public void test4LogCritical () {
        MaiLogger.logCritical("test1");
        String exp = "CRITICAL: test1\n";

        assertEquals(exp, MaiLogger.getLogAll());
        assertEquals(exp, readFile(dir + "/mainclass.log"));
        assertEquals(exp, mc.getLog());
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);
        assert(mc.stopped);

        assertEquals(exp, MaiLogger.getLog(false, false, false, true));
        assertEquals("", MaiLogger.getLog(true, false, false, false));
        assertEquals("", MaiLogger.getLog(false, false, true, false));
        assertEquals("", MaiLogger.getLog(false, true, false, false));
    }

    @Test
    public void test5MultpleLog () {
        String[] exp = new String[] {
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
        MaiLogger.logInfo("test1");
        assertEquals(exp[0] + "\n", MaiLogger.getLogAll());
        MaiLogger.logInfo("test2");
        assertEquals(exp[0] + "\n" + exp[1] + "\n", MaiLogger.getLogAll());
        MaiLogger.logWarning("test3");
        assertEquals(exp[0] + "\n" + exp[1] + "\n" + exp[2] + "\n", MaiLogger.getLogAll());
        MaiLogger.logError("test4");
        assertEquals(exp[0] + "\n" + exp[1] + "\n" + exp[2] + "\n" + exp[3] + "\n", MaiLogger.getLogAll());
        MaiLogger.logInfo("test5");
        assertEquals(exp[0] + "\n" + exp[1] + "\n" + exp[2] + "\n" + exp[3] + "\n" + exp[4] + "\n", MaiLogger.getLogAll());
        MaiLogger.logCritical("test6");
        assertEquals(exp[1] + "\n" + exp[2] + "\n" + exp[3] + "\n" + exp[4] + "\n" + exp[5] + "\n", MaiLogger.getLogAll());
        assertEquals(exp[1] + "\n" + exp[2] + "\n" + exp[3] + "\n" + exp[4] + "\n" + exp[5] + "\n", readFile(dir + "/mainclass.log"));
        assertEquals(exp[0] + "\n" + exp[1] + "\n" + exp[2] + "\n" + exp[3] + "\n" + exp[4] + "\n" + exp[5] + "\n", mc.getLog());
        MaiLogger.logError("test7");
        assertEquals(exp[2] + "\n" + exp[3] + "\n" + exp[4] + "\n" + exp[5] + "\n" + exp[6] + "\n", MaiLogger.getLogAll());
        MaiLogger.logInfo("test8");
        assertEquals(exp[3] + "\n" + exp[4] + "\n" + exp[5] + "\n" + exp[6] + "\n" + exp[7] + "\n", MaiLogger.getLogAll());
        MaiLogger.logWarning("test9");
        assertEquals(exp[4] + "\n" + exp[5] + "\n" + exp[6] + "\n" + exp[7] + "\n" + exp[8] + "\n", MaiLogger.getLogAll());
        MaiLogger.logWarning("test10");
        assertEquals(exp[5] + "\n" + exp[6] + "\n" + exp[7] + "\n" + exp[8] + "\n" + exp[9] + "\n", MaiLogger.getLogAll());

        assertEquals(exp[5] + "\n" + exp[6] + "\n" + exp[7] + "\n" + exp[8] + "\n" + exp[9] + "\n", readFile(dir + "/mainclass.log"));
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);

        String expInfo = exp[7] + "\n";
        String expWarning = exp[8] + "\n" + exp[9] + "\n";
        String expError = exp[6] + "\n";
        String expCritical = exp[5] + "\n";
        assertEquals(expInfo, MaiLogger.getLog(true, false, false, false));
        assertEquals(expWarning, MaiLogger.getLog(false, true, false, false));
        assertEquals(expError, MaiLogger.getLog(false, false, true, false));
        assertEquals(expCritical, MaiLogger.getLog(false, false, false, true));

        String expInWa = exp[7] + "\n" + exp[8] + "\n" + exp[9] + "\n";
        String expWaCr = exp[5] + "\n" + exp[8] + "\n" + exp[9] + "\n";
        String expInEr = exp[6] + "\n" + exp[7] + "\n";
        assertEquals(expInWa, MaiLogger.getLog(true, true, false, false));
        assertEquals(expWaCr, MaiLogger.getLog(false, true, false, true));
        assertEquals(expInEr, MaiLogger.getLog(true, false, true, false));
    }

    @Test
    public void test6ClearLog () {
        MaiLogger.logInfo("testIn");
        assertEquals("INFO: testIn" + "\n", MaiLogger.getLogAll());
        MaiLogger.clearLog();
        assertEquals("", MaiLogger.getLogAll());
        MaiLogger.logInfo("test1");
        MaiLogger.logInfo("test2");
        MaiLogger.logWarning("test3");
        MaiLogger.logError("test4");
        MaiLogger.logInfo("test5");
        MaiLogger.logCritical("test6");
        assertEquals("INFO: test2\nWARNING: test3\nERROR: test4\nINFO: test5\nCRITICAL: test6" + "\n", MaiLogger.getLogAll());
        MaiLogger.clearLog();
        assertEquals("", MaiLogger.getLogAll());
        assertEquals("INFO: test2\nWARNING: test3\nERROR: test4\nINFO: test5\nCRITICAL: test6" + "\n",readFile(dir + "/" + "mainclass.log"));
        MaiLogger.logCritical("testCr");
        assertEquals("CRITICAL: testCr" + "\n", MaiLogger.getLogAll());
        MaiLogger.clearLog();
        assertEquals("", MaiLogger.getLogAll());
    }

    @Test
    public void test7Load () {
        MaiLogger.logInfo("test1");
        MaiLogger.logInfo("test2");
        MaiLogger.logWarning("test3");
        MaiLogger.logError("test4");
        MaiLogger.logCritical("test5");
        String exp =
                "INFO: test1\n"
                        + "INFO: test2\n"
                        + "WARNING: test3\n"
                        + "ERROR: test4\n"
                        + "CRITICAL: test5\n";
        MaiLogger.clearLog();
        assertEquals("", MaiLogger.getLogAll());
        MaiLogger.load();
        assertEquals(exp, MaiLogger.getLogAll());
        MaiLogger.load();
        assertEquals(exp, MaiLogger.getLogAll());
    }

    @Test
    public void test8Rotate () {
        MaiLogger.logInfo("test1");
        assertEquals(1, new File(dir).listFiles().length);
        assertEquals("INFO: test1" + "\n", readFile(dir + "/" + "mainclass.log"));
        MaiLogger.rotate();
        MaiLogger.logWarning("test2");
        MaiLogger.logInfo("test3");
        assertEquals(2, new File(dir).listFiles().length);
        assertEquals("WARNING: test2\nINFO: test3" + "\n", readFile(dir + "/" + "mainclass.log"));
        assertEquals("INFO: test1" + "\n", readFile(dir + "/" + "mainclass.log.1"));
        MaiLogger.rotate();
        MaiLogger.logError("test4");
        MaiLogger.logError("test5");
        MaiLogger.logInfo("test6");
        assertEquals(3, new File(dir).listFiles().length);
        assertEquals("ERROR: test4\nERROR: test5\nINFO: test6" + "\n", readFile(dir + "/" + "mainclass.log"));
        assertEquals("WARNING: test2\nINFO: test3" + "\n", readFile(dir + "/" + "mainclass.log.1"));
        assertEquals("INFO: test1" + "\n", readFile(dir + "/" + "mainclass.log.2"));
        MaiLogger.rotate();
        assertEquals(4, new File(dir).listFiles().length);
        assertEquals("", readFile(dir + "/" + "mainclass.log"));
        assertEquals("ERROR: test4\nERROR: test5\nINFO: test6" + "\n", readFile(dir + "/" + "mainclass.log.1"));
        assertEquals("WARNING: test2\nINFO: test3" + "\n", readFile(dir + "/" + "mainclass.log.2"));
        assertEquals("INFO: test1" + "\n", readFile(dir + "/" + "mainclass.log.3"));
        MaiLogger.rotate();
        MaiLogger.logCritical("test7");
        assertEquals(4, new File(dir).listFiles().length);
        assertEquals("CRITICAL: test7" + "\n", readFile(dir + "/" + "mainclass.log"));
        assertEquals("", readFile(dir + "/" + "mainclass.log.1"));
        assertEquals("ERROR: test4\nERROR: test5\nINFO: test6" + "\n", readFile(dir + "/" + "mainclass.log.2"));
        assertEquals("WARNING: test2\nINFO: test3" + "\n", readFile(dir + "/" + "mainclass.log.3"));
    }

    @Test
    public void test9time () {
        MaiLogger.setUp(mc, 5, 3, true, dir);
        Date din = new Date();
        MaiLogger.logInfo("test1");
        Date dwa = new Date();
        MaiLogger.logWarning("test2");
        Date der = new Date();
        MaiLogger.logError("test3");
        Date dcr = new Date();
        MaiLogger.logCritical("test4");
        DateFormat df = new SimpleDateFormat("dd.MM.yy HH:mm:ss ");
        String exp = df.format(din) + "INFO: test1\n" + df.format(dwa) + "WARNING: test2\n" + df.format(der) + "ERROR: test3\n" + df.format(dcr) + "CRITICAL: test4" + "\n";
        assertEquals(exp, MaiLogger.getLogAll());
        assertEquals(exp, readFile(dir + "/mainclass.log"));
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



}
