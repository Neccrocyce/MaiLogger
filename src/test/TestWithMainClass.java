package test;

import logger.Group;
import logger.MaiLogger;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.io.File;
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
    private static String fileName = "testlog";

    @BeforeClass
    public static void setUp () {
        mc = new MainClass();
        MaiLogger.setUp(mc, 5, 3, false, false, dir, fileName);
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
        MaiLogger.setUp(mc, 5, 3, false, false, dir, fileName);
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
        assertEquals(exp, readFile());
        assertEquals(exp, MaiLogger.getLogAll());

        MaiLogger.setUp(mc, 6, 5, false, false, dir, fileName);
        assertEquals(exp, readFile());
        assertEquals(exp, MaiLogger.getLogAll());
        MaiLogger.clearLog();

        MaiLogger.setUp(mc, 6, 5, false, false, dir, fileName);
        assertEquals(exp, readFile());
        assertEquals("", MaiLogger.getLogAll());

        MaiLogger.load();
        MaiLogger.setUp(mc, 2, 5, false, false, dir, fileName);
        MaiLogger.logInfo("test1");
        assertEquals("ERROR: test3\nINFO: test1\n", readFile());
        assertEquals("ERROR: test3\nINFO: test1\n", MaiLogger.getLogAll());
    }

    @Test
    public void test1aLogDebug () {
        MaiLogger.logDebug("test1");
        String exp = "DEBUG: test1\n";

        assertEquals(exp, MaiLogger.getLogAll());
        assertEquals(exp, readFile());
        assertEquals(exp, mc.getLog());
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);

        assertEquals(exp, MaiLogger.getLog(Group.DEBUG));
    }

    @Test
    public void test1bLogInfo () {
        MaiLogger.logInfo("test1");
        String exp = "INFO: test1\n";

        assertEquals(exp, MaiLogger.getLogAll());
        assertEquals(exp, readFile());
        assertEquals(exp, mc.getLog());
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);

        assertEquals(exp, MaiLogger.getLog(Group.INFO));
    }

    @Test
    public void test2LogWarning () {
        MaiLogger.logWarning("test1");
        String exp = "WARNING: test1\n";

        assertEquals(exp, MaiLogger.getLogAll());
        assertEquals(exp, readFile());
        assertEquals(exp, mc.getLog());
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);

        assertEquals(exp, MaiLogger.getLog(Group.WARNING));
    }

    @Test
    public void test3LogError () {
        MaiLogger.logError("test1");
        String exp = "ERROR: test1\n";

        assertEquals(exp, MaiLogger.getLogAll());
        assertEquals(exp, readFile());
        assertEquals(exp, mc.getLog());
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);

        assertEquals(exp, MaiLogger.getLog(Group.ERROR));
    }

    @Test
    public void test4LogCritical () {
        MaiLogger.logCritical("test1");
        String exp = "CRITICAL: test1\n";

        assertEquals(exp, MaiLogger.getLogAll());
        assertEquals(exp, readFile());
        assertEquals(exp, mc.getLog());
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);
        assert(mc.stopped);

        assertEquals(exp, MaiLogger.getLog(Group.CRITICAL));
    }

    @Test
    public void test5MultipleLog () {
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
                "DEBUG: test10"
        };
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
        assertEquals(exp[1] + "\n" + exp[2] + "\n" + exp[3] + "\n" + exp[4] + "\n" + exp[5] + "\n", readFile());
        assertEquals(exp[0] + "\n" + exp[1] + "\n" + exp[2] + "\n" + exp[3] + "\n" + exp[4] + "\n" + exp[5] + "\n", mc.getLog());
        MaiLogger.logError("test7");
        assertEquals(exp[2] + "\n" + exp[3] + "\n" + exp[4] + "\n" + exp[5] + "\n" + exp[6] + "\n", MaiLogger.getLogAll());
        MaiLogger.logInfo("test8");
        assertEquals(exp[3] + "\n" + exp[4] + "\n" + exp[5] + "\n" + exp[6] + "\n" + exp[7] + "\n", MaiLogger.getLogAll());
        MaiLogger.logWarning("test9");
        assertEquals(exp[4] + "\n" + exp[5] + "\n" + exp[6] + "\n" + exp[7] + "\n" + exp[8] + "\n", MaiLogger.getLogAll());
        MaiLogger.logDebug("test10");
        assertEquals(exp[5] + "\n" + exp[6] + "\n" + exp[7] + "\n" + exp[8] + "\n" + exp[9] + "\n", MaiLogger.getLogAll());

        assertEquals(exp[5] + "\n" + exp[6] + "\n" + exp[7] + "\n" + exp[8] + "\n" + exp[9] + "\n", readFile());
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);

        String expDebug = exp[9] + "\n";
        String expInfo = exp[7] + "\n";
        String expWarning = exp[8] + "\n";
        String expError = exp[6] + "\n";
        String expCritical = exp[5] + "\n";

        assertEquals(expDebug, MaiLogger.getLog(Group.DEBUG));
        assertEquals(expInfo, MaiLogger.getLog(Group.INFO));
        assertEquals(expWarning, MaiLogger.getLog(Group.WARNING));
        assertEquals(expError, MaiLogger.getLog(Group.ERROR));
        assertEquals(expCritical, MaiLogger.getLog(Group.CRITICAL));

        String expInWa = exp[7] + "\n" + exp[8] + "\n";
        String expWaCr = exp[5] + "\n" + exp[8] + "\n";
        String expInDe = exp[7] + "\n" + exp[9] + "\n";

        assertEquals(expInWa, MaiLogger.getLog(Group.INFO, Group.WARNING));
        assertEquals(expWaCr, MaiLogger.getLog(Group.CRITICAL, Group.WARNING));
        assertEquals(expInDe, MaiLogger.getLog(Group.INFO, Group.DEBUG));
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
        assertEquals("INFO: test2\nWARNING: test3\nERROR: test4\nINFO: test5\nCRITICAL: test6" + "\n",readFile());
        MaiLogger.logCritical("testCr");
        assertEquals("CRITICAL: testCr" + "\n", MaiLogger.getLogAll());
        MaiLogger.clearLog();
        assertEquals("", MaiLogger.getLogAll());
    }

    @Test
    public void test7Load () {
        MaiLogger.logInfo("test2");
        MaiLogger.logWarning("test3");
        MaiLogger.logError("test4");
        MaiLogger.logCustom("test5", "USER");
        MaiLogger.logCritical("test6");
        String exp =
                        "INFO: test2\n"
                        + "WARNING: test3\n"
                        + "ERROR: test4\n"
                        + "USER: test5\n"
                        + "CRITICAL: test6\n";
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
        assertEquals("INFO: test1" + "\n", readFile ());
        MaiLogger.rotate();
        MaiLogger.logWarning("test2");
        MaiLogger.logInfo("test3");
        assertEquals(2, new File(dir).listFiles().length);
        assertEquals("WARNING: test2\nINFO: test3" + "\n", readFile ());
        assertEquals("INFO: test1" + "\n", readFile(dir + "/" + fileName + ".log.1"));
        MaiLogger.rotate();
        MaiLogger.logError("test4");
        MaiLogger.logError("test5");
        MaiLogger.logInfo("test6");
        assertEquals(3, new File(dir).listFiles().length);
        assertEquals("ERROR: test4\nERROR: test5\nINFO: test6" + "\n", readFile());
        assertEquals("WARNING: test2\nINFO: test3" + "\n", readFile(dir + "/" + fileName + ".log.1"));
        assertEquals("INFO: test1" + "\n", readFile(dir + "/" + fileName + ".log.2"));
        MaiLogger.rotate();
        assertEquals(4, new File(dir).listFiles().length);
        assertEquals("", readFile());
        assertEquals("ERROR: test4\nERROR: test5\nINFO: test6" + "\n", readFile(dir + "/" + fileName + ".log.1"));
        assertEquals("WARNING: test2\nINFO: test3" + "\n", readFile(dir + "/" + fileName + ".log.2"));
        assertEquals("INFO: test1" + "\n", readFile(dir + "/" + fileName + ".log.3"));
        MaiLogger.rotate();
        MaiLogger.logCritical("test7");
        assertEquals(4, new File(dir).listFiles().length);
        assertEquals("CRITICAL: test7" + "\n", readFile());
        assertEquals("", readFile(dir + "/" + fileName + ".log.1"));
        assertEquals("ERROR: test4\nERROR: test5\nINFO: test6" + "\n", readFile(dir + "/" + fileName + ".log.2"));
        assertEquals("WARNING: test2\nINFO: test3" + "\n", readFile(dir + "/" + fileName + ".log.3"));
    }

    @Test
    public void test9time () {
        MaiLogger.setUp(mc, 5, 3, true, false, dir, fileName);
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
        assertEquals(exp, readFile ());
    }

    @Test
    public void test10TaskSuccess () {
        int id = MaiLogger.logNewTask("test1");
        String exp = "INFO: test1 ... SUCCESS \n";

        assertEquals("", MaiLogger.getLogAll());
        assertEquals("", readFile ());
        assertEquals("", mc.getLog());
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);

        MaiLogger.succeededTask(id);

        assertEquals(exp, remDelay(MaiLogger.getLogAll()));
        assertEquals(exp, remDelay(readFile ()));
        assertEquals(exp, remDelay(mc.getLog()));
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);
    }

    @Test
    public void test11TaskFailed () {
        int id = MaiLogger.logNewTask("test1");
        String exp = "ERROR: test1 ... FAILED \n";

        assertEquals("", MaiLogger.getLogAll());
        assertEquals("", readFile ());
        assertEquals("", mc.getLog());
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);

        MaiLogger.failedTask(id);

        assertEquals(exp, remDelay(MaiLogger.getLogAll()));
        assertEquals(exp, remDelay(readFile ()));
        assertEquals(exp, remDelay(mc.getLog()));
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);
    }

    @Test
    public void test12TaskAbort () {
        MaiLogger.logNewTask("test1");
        String exp = "ERROR: test1 ... ABORTED BY USER\n";

        assertEquals("", MaiLogger.getLogAll());
        assertEquals("", readFile ());
        assertEquals("", mc.getLog());
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);

        MaiLogger.on_exit();

        assertEquals(exp, MaiLogger.getLogAll());
        assertEquals(exp, readFile ());
        assertEquals(exp, mc.getLog());
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);
    }

    @Test
    public void test13MultipleTasks () {
        MaiLogger.setUp(mc, -1, 3, false, false, dir, fileName);
        String[] exp = new String[] {
                "INFO: test1 ... SUCCESS \n",
                "ERROR: test2 ... ABORTED BY USER\n",
                "ERROR: test3 ... FAILED \n",
                "INFO: test4\n",
                "INFO: test5 ... SUCCESS \n",
                "ERROR: test6 ... ABORTED BY USER\n",
                "ERROR: test7 ... ABORTED BY CRITICAL STATE\n",
                "ERROR: test8 ... ABORTED BY USER\n",
                "CRITICAL: test9\n",
        };

        int id1 = MaiLogger.logNewTask("test1");
        MaiLogger.logNewTask("test2");
        assertEquals("", MaiLogger.getLogAll());
        assertEquals("", readFile ());

        MaiLogger.succeededTask(id1);
        assertEquals(exp[0], remDelay(MaiLogger.getLogAll()));
        assertEquals(exp[0], remDelay(readFile ()));

        int id3 = MaiLogger.logNewTask("test3");
        int id5 = MaiLogger.logNewTask("test5");

        MaiLogger.succeededTask(id5);
        MaiLogger.logInfo("test4");
        MaiLogger.failedTask(id3);
        String expl = exp[0] + exp[4] + exp[3] + exp[2];
        assertEquals(expl, remDelay(MaiLogger.getLogAll()));
        assertEquals(expl, remDelay(readFile ()));

        expl += exp[1];
        MaiLogger.on_exit();
        assertEquals(expl, remDelay(MaiLogger.getLogAll()));
        assertEquals(expl, remDelay(readFile ()));

        MaiLogger.logNewTask("test7");
        MaiLogger.logCritical("test9");
        expl += exp[8] + exp[6];
        assertEquals(expl, remDelay(MaiLogger.getLogAll()));
        assertEquals(expl, remDelay(readFile ()));
    }

    @Test
    public void test14ImmediateTaskSuccess () {
        MaiLogger.setUp(mc, -1, 3, false, true, dir, fileName);
        int id = MaiLogger.logNewTask("test1");
        String exp1 = "TASK: test1 ... \n";
        String exp2 = "TASK: test1 ... SUCCESS \n";
        String exp3 = "INFO: test1 ... SUCCESS \n";

        assertEquals("", MaiLogger.getLogAll());
        assertEquals(exp1, readFile ());
        assertEquals(exp1, mc.getLog());
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);

        MaiLogger.succeededTask(id);

        assertEquals(exp3, remDelay(MaiLogger.getLogAll()));
        assertEquals(exp2, remDelay(readFile ()));
        assertEquals(exp1 + exp3, remDelay(mc.getLog()));
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);
    }

    @Test
    public void test15ImmediateTaskFailed () {
        MaiLogger.setUp(mc, -1, 3, false, true, dir, fileName);
        int id = MaiLogger.logNewTask("test1");
        String exp1 = "TASK: test1 ... \n";
        String exp2 = "TASK: test1 ... FAILED \n";
        String exp3 = "ERROR: test1 ... FAILED \n";

        assertEquals("", MaiLogger.getLogAll());
        assertEquals(exp1, readFile ());
        assertEquals(exp1, mc.getLog());
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);

        MaiLogger.failedTask(id);

        assertEquals(exp3, remDelay(MaiLogger.getLogAll()));
        assertEquals(exp2 + exp3, remDelay(readFile ()));
        assertEquals(exp1 + exp3, remDelay(mc.getLog()));
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);
    }

    @Test
    public void test16ImmediateTaskAbort () {
        MaiLogger.setUp(mc, -1, 3, false, true, dir, fileName);
        int id = MaiLogger.logNewTask("test1");
        String exp1 = "TASK: test1 ... \n";
        String exp2 = "ERROR: test1 ... ABORTED BY USER\n";

        assertEquals("", MaiLogger.getLogAll());
        assertEquals(exp1, readFile ());
        assertEquals(exp1, mc.getLog());
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);

        MaiLogger.on_exit();

        assertEquals(exp2, remDelay(MaiLogger.getLogAll()));
        assertEquals(exp1 + exp2, remDelay(readFile ()));
        assertEquals(exp1 + exp2, remDelay(mc.getLog()));
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);
    }

    @Test
    public void test17ImmediateTaskInActiveSuccess () {
        MaiLogger.setUp(mc, -1, 3, false, true, dir, fileName);
        String exp1 = "TASK: test1 ... \n";
        String exp2 = "INFO: test1 ... SUCCESS \n";
        String exp4 = "TASK: test2 ... \n";

        int id = MaiLogger.logNewTask("test1");

        assertEquals("", MaiLogger.getLogAll());
        assertEquals(exp1, readFile ());
        assertEquals(exp1, mc.getLog());
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);

        int id2 = MaiLogger.logNewTask("test2");
        assertEquals("", MaiLogger.getLogAll());
        assertEquals(exp1 + exp4, readFile ());
        assertEquals(exp1 + exp4, mc.getLog());
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);

        MaiLogger.succeededTask(id);

        assertEquals(exp2, remDelay(MaiLogger.getLogAll()));
        assertEquals(exp1 + exp4 + exp2, remDelay(readFile ()));
        assertEquals(exp1 + exp4 + exp2, remDelay(mc.getLog()));
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);
    }

    @Test
    public void test18ImmediateTaskInActiveFailed () {
        MaiLogger.setUp(mc, -1, 3, false, true, dir, fileName);
        String exp1 = "TASK: test1 ... \n";
        String exp2 = "ERROR: test1 ... FAILED \n";
        String exp4 = "TASK: test2 ... \n";

        int id = MaiLogger.logNewTask("test1");

        assertEquals("", MaiLogger.getLogAll());
        assertEquals(exp1, readFile ());
        assertEquals(exp1, mc.getLog());
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);

        int id2 = MaiLogger.logNewTask("test2");
        assertEquals("", MaiLogger.getLogAll());
        assertEquals(exp1 + exp4, readFile ());
        assertEquals(exp1 + exp4, mc.getLog());
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);

        MaiLogger.failedTask(id);

        assertEquals(exp2, remDelay(MaiLogger.getLogAll()));
        assertEquals(exp1 + exp4 + exp2, remDelay(readFile ()));
        assertEquals(exp1 + exp4 + exp2, remDelay(mc.getLog()));
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);
    }

    @Test
    public void test19ImmediateTaskInActiveAbort () {
        MaiLogger.setUp(mc, -1, 3, false, true, dir, fileName);
        String exp1 = "TASK: test1 ... \n";
        String exp2 = "ERROR: test1 ... ABORTED BY USER\n";
        String exp4 = "TASK: test2 ... \n";
        String exp5 = "ERROR: test2 ... ABORTED BY USER\n";

        int id = MaiLogger.logNewTask("test1");

        assertEquals("", MaiLogger.getLogAll());
        assertEquals(exp1, readFile ());
        assertEquals(exp1, mc.getLog());
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);

        int id2 = MaiLogger.logNewTask("test2");
        assertEquals("", MaiLogger.getLogAll());
        assertEquals(exp1 + exp4, readFile ());
        assertEquals(exp1 + exp4, mc.getLog());
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);

        MaiLogger.on_exit();

        String expl = exp2 + exp5;
        if (!remDelay(MaiLogger.getLogAll()).equals(expl)) {
            expl = exp5 + exp2;
        }

        assertEquals(expl, remDelay(MaiLogger.getLogAll()));
        assertEquals(exp1 + exp4 + expl, remDelay(readFile ()));
        assertEquals(exp1 + exp4 + expl, remDelay(mc.getLog()));
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);
    }

    @Test
    public void test20ImmediateMultipleTasks () {
        MaiLogger.setUp(mc, -1, 3, false, true, dir, fileName);
        String[][] exp = new String[][] {{
                "TASK: test1 ... \n",
                "TASK: test1 ... SUCCESS \n",
                "INFO: test1 ... SUCCESS \n"
        }, {
                "TASK: test2 ... \n",
                "ERROR: test2 ... ABORTED BY USER\n"
        }, {
                "TASK: test3 ... \n",
                "INFO: test3 ... SUCCESS \n",
        }, {
                "INFO: test4\n",
        }, {
                "TASK: test5 ... \n",
                "TASK: test5 ... FAILED \n",
                "ERROR: test5 ... FAILED \n",
        }, {
                "TASK: test6 ... \n",
                "ERROR: test6 ... ABORTED BY CRITICAL STATE\n",
        } ,{
                "CRITICAL: test7\n"
        }
        };

        int id1 = MaiLogger.logNewTask("test1");
        assertEquals("", MaiLogger.getLogAll());
        assertEquals(exp[0][0], readFile ());

        MaiLogger.succeededTask(id1);
        MaiLogger.logNewTask("test2");

        String expl = exp[0][2];
        String expf = exp[0][1] + exp[1][0];


        assertEquals(exp[0][2], remDelay(MaiLogger.getLogAll()));
        assertEquals(exp[0][1] + exp[1][0], remDelay(readFile ()));

        int id3 = MaiLogger.logNewTask("test3");
        int id5 = MaiLogger.logNewTask("test5");

        MaiLogger.failedTask(id5);
        MaiLogger.logInfo("test4");
        MaiLogger.succeededTask(id3);

        expl += exp[4][2] + exp[3][0] + exp[2][1];
        expf += exp[2][0] + exp[4][1] + exp[4][2] + exp[3][0] + exp[2][1];
        assertEquals(expl, remDelay(MaiLogger.getLogAll()));
        assertEquals(expf, remDelay(readFile ()));

        expl += exp[1][1];
        expf += exp[1][1];
        MaiLogger.on_exit();
        assertEquals(expl, remDelay(MaiLogger.getLogAll()));
        assertEquals(expf, remDelay(readFile ()));

        MaiLogger.logNewTask("test6");
        MaiLogger.logCritical("test7");
        expl += exp[6][0] + exp[5][1];
        expf += exp[5][0] + exp[6][0] + exp[5][1];
        assertEquals(expl, remDelay(MaiLogger.getLogAll()));
        assertEquals(expf, remDelay(readFile ()));
    }

    @Test
    public void test21CustomLog () {
        MaiLogger.logCustom("test1", "USER");
        String exp = "USER: test1\n";

        assertEquals(exp, MaiLogger.getLogAll());
        assertEquals(exp, readFile());
        assertEquals(exp, mc.getLog());
        assertEquals("", mc.getErrLog());
        assertEquals(1, new File(dir).listFiles().length);

        assertEquals(exp, MaiLogger.getLog(Group.CUSTOM));
    }



    private static String readFile () {
        return readFile(dir + "/" + fileName + ".log");
    }

    private static String readFile (String file) {
        StringBuilder content = new StringBuilder();
        try (Stream<String> str = Files.lines(new File(file).toPath())) {
            str.forEach(s -> content.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    private static String remDelay (String entry) {
        StringBuilder content = new StringBuilder();
        try {
            String[] lines = entry.split("\n");
            for (String line : lines) {
                int op = line.indexOf("(");
                int en = line.indexOf(")");
                if (op == -1 || en == -1) {
                    content.append(line).append("\n");
                } else {
                    content.append(line, 0, op).append(en + 1 == line.length() ? "" : line.substring(en + 1)).append("\n");
                }
            }
            return content.toString();
        } catch (IndexOutOfBoundsException e) {
            return "Failed to remove delay in: " + entry;
        }

    }



}
