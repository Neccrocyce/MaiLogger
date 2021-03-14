package logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * The {@code Mailogger} class provides the recording of events that occur in a running application
 * It saves these records into log files and sends them to the mainclass
 * If a critical event is logged, the {@code MaiLog.stop()} method is called to handle this critical state of the application.
 * This class also provides log-rotation and to set the maximum number of logged information
 *
 * @author Neccrocyce
 * @see <a href="https://github.com/Neccrocyce">Neccrocyce - GitHub</a>
 * @see MaiLog
 * @see Log
 * @version 2.0
 */
public class MaiLogger {
	/**
	 * A list of all logged events
	 */
	private static List<Log> log = new ArrayList<>();

	/**
	 * list of started tasks
	 */
	private static Map<Integer,Task> tasks = new HashMap<>();

	/**
	 * The number of maximum logged events. If {@code maxLogs} is -1 this setting is ignored. If there are more logged events, then the oldest one will be deleted
	 * until the number of maximum logged events is equals {@code maxLogs}.
	 */
	private static int maxLogs = -1;

	/**
	 * The name of the log file. It is either the name of the class that implements {@code MaiLog} or "Log" if
	 * {@code MaiLogger} hasn't been set up
	 */
	private static String fileName = "Log";

	/**
	 * The directory in which the log files are saved.
	 */
	private static String directory = MaiLogger.class.getResource("").getPath() + "/../logs";

	/**
	 * The maximum number of files that are created by log rotation.
	 */
	private static int rotations = -1;

	/**
	 * Decides whether the time of logging the event is logged, too
	 * true: time is logged, false: no time is logged
	 */
	private static boolean time = true;

	/**
	 * Decides whether tasks will be logged as soon as they start and the status (success/fail/abort) will be logged afterwards or whether tasks will wait until they have been finished or aborted.
	 * true: Logging at start, false: Logging when finished.
	 */
	private static boolean immediateOutput = true;

	/**
	 * The class which implements the {@code MaiLog} interface
	 */
	private static MaiLog mainClass = null;

	/**
	 * The task which has been started latest and no other event has been logged after that. Otherwise it is null.
	 */
	private static Task activeTask = null;
	
	/**
	 * Sets the values which are required to use MaiLogger. Therefore this method needs to be called before
	 * using any other method of this class.
	 *
	 *
	 * <pre>
	 * @param mainClass The class which implements {@code MaiLog} interface
	 * @param maxLogs The number of maximum logged events
	 *                It is recommended to set it to -1. Otherwise MaiLogger is less efficient, because of creating a new log file every time an event is logged, instead of adding the event to the end of the existing log file.
	 *        -1: infinite (recommended)
	 *        default: -1
	 * @param rotations The maximum number of files that are created by log rotation.
	 *        -1: infinite
	 *        default: -1
	 * @param time Decides whether the time of each event is logged, too
	 *             default: true
	 * @param immediateOutput Decides whether tasks will be logged as soon as they start and the status (success/fail/abort) will be logged afterwards or whether tasks will wait until they have been finished or aborted.
	 *                        default: true
	 * @param directory The directory in which the log files are saved
	 *                  default ~/../logs
	 * @param logFileName The filename of the logfile
	 *                    default: name of mainClass or "Log" if no setUp is called
	 *
	 * </pre>
	 */
	public static void setUp (MaiLog mainClass, int maxLogs, int rotations, boolean time, boolean immediateOutput, String directory, String logFileName) {
		MaiLogger.mainClass = mainClass;
		MaiLogger.maxLogs = maxLogs;
		MaiLogger.rotations = rotations;
		MaiLogger.time = time;
		MaiLogger.immediateOutput = immediateOutput;
		if (directory != null && !directory.equals("")) {
			MaiLogger.directory = directory;
		}
		fileName = logFileName;
		if (!new File(directory + "/" + fileName + ".log").exists()) {
			save();
		}
	}

	@Deprecated
	public static void setUp (MaiLog mainClass, int maxLogs, int rotations, boolean time, String directory) {
		MaiLogger.mainClass = mainClass;
		MaiLogger.maxLogs = maxLogs;
		MaiLogger.rotations = rotations;
		MaiLogger.time = time;
		if (directory != null && !directory.equals("")) {
			MaiLogger.directory = directory;
		}
		if (mainClass != null) {
			fileName = mainClass.getClass().getSimpleName().toLowerCase();
		}
		if (!new File(directory + "/" + fileName + ".log").exists()) {
			save();
		}
	}

	/**
	 * Sets the values which are required to use MaiLogger. Therefore this method needs to be called before
	 * using any other method of this class. Use this method if it is not intended to implement the {@code MaiLog} interface
	 *
	 *
	 * <pre>
	 * @param maxLogs The number of maximum logged events
	 *                It is recommend to set it to -1. Otherwise MaiLogger is less efficient, because of creating a new log file every time an event is logged, instead of adding the event to the end of the existing log file.
	 *        -1: infinite (recommend)
	 *        default: -1
	 * @param rotations The maximum number of files that are created by log rotation.
	 *        -1: infinite
	 *        default -1
	 * @param time Decides whether the time of each event is logged, too
	 * @param immediateOutput Decides whether tasks will be logged as soon as they start and the status (success/fail/abort) will be logged afterwards or whether tasks will wait until they have been finished or aborted.
	 * @param directory The directory in which the log files are saved
	 *        default ~/../logs
	 * @param logFileName The filename of the logfile
	 *                    default: name of mainClass or "Log" if no setUp is called
	 * </pre>
	 */
	public static void setUp (int maxLogs, int rotations, boolean time, boolean immediateOutput, String directory, String logFileName) {
		setUp(null, maxLogs, rotations, time, immediateOutput, directory, logFileName);
	}

	/**
	 * Sets the values which are required to use MaiLogger. Therefore this method needs to be called before
	 * using any other method of this class.
	 * It sets {@code maxLogs}, {@code rotations}, {@code time}, {@code immediateOutput} and {@code directory} to default.
	 *
	 * @param mainClass The class which implements {@code MaiLog} interface
	 *
	 * @see #setUp(MaiLog, int, int, boolean, boolean, String, String)
	 */
	public static void setUp (MaiLog mainClass) {
		setUp(mainClass, -1, -1, true, true, "", mainClass.getClass().getSimpleName().toLowerCase());
	}

	/**
	 * This method logs a new task to the group "TASK" and returns an id representing the task.
	 * After the task has finished successfully the method @code{succeededTask(int)} should be called with the given id.
	 * This call will add "SUCCESS" to the corresponding log entry and will switch it to the group "INFO". <br>
	 * If the task has failed the method @code{failedTask(int)} should be called with the given id.
	 * This call will add "FAILED" to the corresponding log entry and will switch it to the group "ERROR". <br>
	 * (e.g: int id = logTask("a task"); succeededTask(id) -> log entry: INFO: a task ... SUCCESS)
	 * @param msg the description of the event that should be logged
	 * @return the id representing the task
	 */
	public static int logNewTask(String msg) {
		deActiveTask();
		Task entry = new Task(msg);
		int id;
		do {
			id = (int) (Math.random() * Integer.MAX_VALUE);
		} while (tasks.putIfAbsent(id, entry) != null);
		if (immediateOutput) {
			MaiLogger.activeTask = entry;
			logTask(entry);
		}
		return id;
	}

	/**
	 * This method should be called when a task has successfully finished
     * It adds "SUCCESS" to the corresponding log entry
	 * @param id ID representing the task
	 */
	public static void succeededTask (int id) {
		Task entry = tasks.remove(id);
		if (entry == null) {
			if (mainClass != null) {
				mainClass.sendErrMsg("Task with ID " + id + " was not found");
			}
			return;
		}
		entry.setSucceeded();
		logTask(entry);
	}

    /**
     * This method should be called when a task has failed
     * It adds "FAILED" to the corresponding log entry
     * @param id ID representing the task
     */
	public static void failedTask (int id) {
		Task entry = tasks.remove(id);
		if (entry == null) {
			if (mainClass != null) {
				mainClass.sendErrMsg("Task with ID " + id + " was not found");
			}
			return;
		}
		entry.setFailed();
		logTask(entry);
	}

	/**
	 * This method sets the attribute {@code activeTask} to null and logs a line break if there was an active task
	 */
	private static void deActiveTask () {
		if (activeTask != null) {
			try {
				addLineToFile("");
			} catch (NoSuchFileException e) {
				save();
			}
			activeTask = null;
		}

	}

	/**
	 * This method sets the attribute {@code activeTask} to null and logs a line break if it is not equal {@code task}
	 * @param task
	 */
	private static void deActiveTask (Task task) {
		if (MaiLogger.activeTask != null && MaiLogger.activeTask != task) {
			try {
				addLineToFile("");
			} catch (NoSuchFileException e) {
				save();
			}
			MaiLogger.activeTask = null;
		}
	}

	static boolean equalsActiveTask (Task task) {
		return activeTask == task;
	}

	/**
	 * This method logs an event to the group "DEBUG". This group logs information used for debugging.
	 * @param msg the description of the event that should be logged
	 */
	public static void logDebug (String msg) {
		deActiveTask();
		log(new Log(Group.DEBUG,msg));
	}

	/**
	 * This method logs an event to the group "INFO". This group logs generally useful information.
	 * @param msg the description of the event that should be logged
	 */
	public static void logInfo (String msg) {
		deActiveTask();
		log(new Log(Group.INFO,msg));
	}

	/**
	 * This method logs an event to the group "WARNING". This group logs events that can potentially lead to a failure.
	 * @param msg the description of the event that should be logged
	 */
	public static void logWarning (String msg) {
		deActiveTask();
		log(new Log(Group.WARNING, msg));
	}

	/**
	 * This method logs an event to the group "ERROR". This group logs errors that are fatal to the current operation, but not to the application.
	 * @param msg the description of the event that should be logged
	 */
	public static void logError (String msg) {
		deActiveTask();
		log(new Log(Group.ERROR,msg));
	}
	
	/**
	 * This method logs an event to the group "CRITICAL". This group logs critical errors that is forcing a shutdown of the application.
	 * Furthermore it calls the {@code MaiLog.stop()} method.
	 * @param msg the description of the event that should be logged
	 */
	public static void logCritical (String msg) {
		deActiveTask();
		log(new Log(Group.CRITICAL,msg));
		on_critical();
		if (mainClass != null) {
			mainClass.stop();
		}
	}

	/**
	 * This method logs an event to a user-defined group.
	 * @param msg the description of the event that should be logged
	 * @param group The user-defined group
	 */
	public static void logCustom (String msg, String group) {
		deActiveTask();
		log(new LogCustom(group, msg));
	}


	private static void logTask(Task entry) {
		if (!entry.isFinished()) {
			if (mainClass != null) {
				mainClass.sendLog(entry.getLogImmediate());
			}
			try {
				addToFile(entry.getLogImmediate());
			} catch (NoSuchFileException e) {
				save();
			}
		} else {
			deActiveTask(entry);
			if (mainClass != null) {
				mainClass.sendLog(entry.getLog());
			}
			log.add(entry);
			try {
				addLineToFile(entry.getLogImmediate());
			} catch (NoSuchFileException e) {
				save();
			}
			if (entry.getGroup() == Group.ERROR && equalsActiveTask(entry)) {
				try {
					addLineToFile(entry.getLog());
				} catch (NoSuchFileException e) {
					save();
				}
			}
			activeTask = null;
		}
	}
	
	private static void log (Log entry) {
		if (mainClass != null) {
			mainClass.sendLog(entry.getLog());
		}
		log.add(entry);
		try {
			addLineToFile(entry.getLog());
		} catch (NoSuchFileException e) {
			save();
		}
		reduceLog();
	}

	/*
	if {@code maxLogs} is set and the maximum number of logs is exceed this method deletes old logged events until the
	number of logs is equals {@code maxLogs}
	 */
	private static void reduceLog () {
		if (maxLogs == -1 || log.size() <= maxLogs) {
			return;
		}

		while (maxLogs != -1 && log.size() > maxLogs) {
			log.remove(0);
		}
		save();
	}

	/**
	 *
	 * @return recording of all logged events in chronologically order
	 */
	public static String getLogAll () {
		StringBuilder content = new StringBuilder();
		log.forEach(e -> content.append(e.getLog()).append("\n"));
		return content.toString();
	}

	/**
	 * This method returns the recording of all logged events of every group for which the parameter is set true. It returns in chronologically order
	 * @param info group INFO (0)
	 * @param warning group WARNING (1)
	 * @param error group ERROR (2)
	 * @param critical group CRITICAL (3)
	 * @return records of all logged events of the requested group(s)
	 */
	@Deprecated
	public static String getLog (boolean info, boolean warning, boolean error, boolean critical) {
		StringBuilder content = new StringBuilder();
		log.stream().filter(e -> (e.getGroup() == 1 && info) || (e.getGroup() == 2 && warning) || (e.getGroup() == 3 && error) || (e.getGroup() == 4 && critical)).forEach(e -> content.append(e.getLog()).append("\n"));
		return content.toString();
	}

	/**
	 * This method returns the recording of all logged events of every group which is mentioned in {@code groups}. It returns in chronologically order
	 * @param groups An array of groups for which the logs should be returned
	 * @return records of all logged events of the requested group(s)
	 */
	public static String getLog (int... groups) {
		StringBuilder content = new StringBuilder();
		for (Log entry : log) {
			for (int group : groups) {
				if (entry.getGroup() == group) {
					content.append(entry.getLog()).append("\n");
					break;
				}
			}
		}
		return content.toString();
	}

	static boolean isTime() {
		return time;
	}

	/*
        writes the {@code log} to file
         */
	private static void save () {
		if (!new File(directory).exists()) {
			boolean newdir = new File(directory).mkdirs();
			if (!newdir) {
				if (mainClass != null) {
					mainClass.sendErrMsg("ERROR: Cannot write logs to file: Cannot create directory: " + directory);
				}
				return;
			}
		}
		String logFile = directory + "/" + fileName + ".log";
		BufferedWriter w = null;
		try {
			if (new File(logFile).exists()) {
				Files.copy(new File(logFile).toPath(), new File(logFile + ".backup").toPath(),StandardCopyOption.REPLACE_EXISTING);
			}
			w = new BufferedWriter(new FileWriter(logFile));
			String[] content = getLogAll().split("\n");
			if (!content[0].equals("")) {
                for (String aContent : content) {
                    w.write(aContent);
                    w.newLine();
                }
			}
			new File(logFile + ".backup").delete();
		} catch (IOException e) {
			if (mainClass != null) {
				mainClass.sendErrMsg("ERROR: Cannot write logs to file (" + e.toString());
			}
			e.printStackTrace();
		} finally {
			try {
				w.close();
			} catch (IOException | NullPointerException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method adds a text to the end of a file without line break.
	 * @param line the line to add
	 * @throws NoSuchFileException if the log file does not exist
	 */
	private static void addToFile (String line) throws NoSuchFileException {
		String logfile = directory + "/" + fileName + ".log";
		if (!new File(logfile).exists()) {
			throw new NoSuchFileException(logfile + "does not exist");
		}

		FileWriter wout;
		try {
			wout = new FileWriter(logfile,true);
			wout.append(line);
			wout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method adds a line to the end of a file.
	 * @param line the line to add
	 * @throws NoSuchFileException if the log file does not exist
	 */
	private static void addLineToFile (String line) throws NoSuchFileException {
		addToFile(line + "\n");
	}

	/**
	 * Loads all logs of the log file ({@code dir}/{@code fileName}.log) into {@code log}
	 * @throws IllegalStateException if {@code MaiLogger hasn't been set up}
	 */
	public static void load () throws IllegalStateException {
		clearLog();
		String logFile = directory + "/" + fileName + ".log";
		if (!new File(logFile).exists()) {
			if (mainClass != null) {
				mainClass.sendErrMsg("ERROR: Cannot load logfile: File does not exist");
			}
			return;
		}
		List<String> lines = new ArrayList<>();
		try (Stream<String> stream = Files.lines(new File(logFile).toPath())) {
			stream.forEach(lines::add);
		} catch (IOException e) {
			if (mainClass != null) {
				mainClass.sendErrMsg("ERROR: Cannot load logfile");
			}
			return;
		}

		//split each line in level and content
		for (String string : lines) {
			try {
				String date = time ? string.substring(0,17) : "";
				String group = time ? string.substring(18).split(": ")[0] : string.split(": ")[0];
				if (Group.getGroupFromString(group) == Group.OTHER) {
					log.add(new LogCustom(date, group, string.substring(group.length() + (time ? 20 : 2))));
				} else {
					log.add(new Log(date, group, string.substring(group.length() + (time ? 20 : 2))));
				}
			} catch (IndexOutOfBoundsException e) {
				if (mainClass != null) {
					mainClass.sendErrMsg("ERROR: Cannot load logfile");
				}
			}
		}
	}

	/**
	 * Rotates the log files and empties {@code log}
	 * "rotate" means:  {@code dir}/{@code fileName}.log is renamed to {@code dir}/{@code fileName}.log.1
	 * 					{@code dir}/{@code fileName}.log.1 is renamed to {@code dir}/{@code fileName}.log.2
	 * 					...
	 * if {@code rotations} is set and exceed, the oldest file will be deleted
	 */
	public static void rotate () {
		File[] files = new File(directory).listFiles(f -> f.getName().startsWith(fileName));
		if (files == null) {
			if (mainClass != null) {
				mainClass.sendErrMsg("ERROR: Cannot rotate: No files in directory");
			}
			return;
		}
		boolean renamed;
		for (int i = files.length; i > 0; i--) {
			renamed = files[i-1].renameTo(new File(directory + "/" + fileName + ".log." + i));
			if (!renamed) {
				if (mainClass != null) {
					mainClass.sendErrMsg("ERROR: Cannot rotate: Unable to rename files" + files[files.length - 1]);
				}
				return;
			}
		}
		clearLog();
		save();
		if (rotations != -1 && files.length > rotations) {
			files = new File(directory).listFiles(f -> f.getName().startsWith(fileName));
			if (files == null) {
				if (mainClass != null) {
					mainClass.sendErrMsg("ERROR: Cannot rotate: No files in directory after rotating");
				}
				return;
			}
			try {
				files[files.length - 1].delete();
			} catch (Exception e) {
				if (mainClass != null) {
					mainClass.sendErrMsg("ERROR: Cannot delete file" + files[files.length - 1]);
				}
				e.printStackTrace();
			}
		}
	}

	/**
	 * empties {@code log}
	 */
	public static void clearLog () {
		log = new ArrayList<>();
		tasks = new HashMap<>();
	}

	/**
	 * This method should be called after the last call to log an entry and before the application will be stopped
	 * It logs every running task with the suffix "ABORTED BY USER"
	 * If no tasks have been started, this method hasn't to be called
	 */
	public static void on_exit () {
			abortTasks("USER");
	}

	/**
	 * This method is called after a critical event has been logged.
	 * It logs every running task with the suffix "ABORTED BY CRITICAL STATE"
	 */
	private static void on_critical () {
			abortTasks("CRITICAL STATE");
	}

	/**
	 * This method logs every running task with the suffix {@code msg}
	 * @param msg reason for the abortion
	 */
	private static void abortTasks (String msg) {
		deActiveTask();
		for (Map.Entry<Integer,Task> task : tasks.entrySet()) {
			Task entry = task.getValue();
			entry.setAbort(msg);
			log(entry);
		}
		tasks = new HashMap<>();
	}
}
