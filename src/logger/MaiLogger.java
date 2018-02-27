package logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * The {@code Mylogger} class provides the recording of events that occur in a running application
 * It saves these records into log files and sends them to the mainclass
 * If a critical event is logged, the {@code MaiLog.stop()} method is called to handle this critical state of the application.
 * This class also provides log-rotation and to set the maximum number of logged information
 *
 * @author Neccrocyce
 * @see <a href="https://github.com/Neccrocyce">Neccrocyce - GitHub</a>
 * @see MaiLog
 * @see Log
 */
public class MaiLogger {
	/**
	 * A list of all logged events
	 */
	private static List<Log> log = new ArrayList<>();

	/**
	 * list of started tasks
	 */
	private static List<Log> tasks = new ArrayList<>();

	/**
	 * The number of maximum logged events. If {@code maxLogs} is -1 this setting is ignored. If there are more logged events, then the oldest one will be deleted
	 * until the number of maximum logged events is equals {@code maxLogs}
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
	 * The class which implements the {@code MaiLog} interface
	 */
	private static MaiLog mainClass;
	
	/**
	 * Sets the values which are required to use MaiLogger. Therefore this method has to be called before
	 * using any other method of this class. If it hasn't been, a warning about
	 * the missing setup of {@code MaiLogger} is logged, every time an event is logged.
	 *
	 *
	 * <pre>
	 * @param mainClass The class which implements {@code MaiLog} interface
	 * @param maxLogs The number of maximum logged events
	 *        -1: infinite
	 *        default: -1
	 * @param rotations The maximum number of files that are created by log rotation
	 *        -1: infinite
	 *        default -1
	 * @param time Decides whether the time of logging the event is logged, too
	 * @param directory The directory in which the log files are saved
	 *        default ~/logs
	 * </pre>
	 * @throws NullPointerException if {@code mainclass} is null
	 */
	public static void setUp (MaiLog mainClass, int maxLogs, int rotations, boolean time, String directory) {
		MaiLogger.mainClass = mainClass;
		MaiLogger.maxLogs = maxLogs;
		MaiLogger.rotations = rotations;
		MaiLogger.time = time;
		if (!directory.equals("")) {
			MaiLogger.directory = directory;
		}
		fileName = mainClass.getClass().getSimpleName().toLowerCase();
		if (!new File(directory + "/" + fileName + ".log").exists()) {
			save();
		}
	}

	/**
	 * Sets the values which are required to use MaiLogger. Therefore this method has to be called before
	 * using any other method of this class. If it hasn't been, a warning about
	 * the missing setup of {@code MaiLogger} is logged, every time an event is logged.
	 * It sets {@code maxLogs}, {@code rotations} and {@code directory} to default.
	 *
	 * @param mainClass The class which implements {@code MaiLog} interface
	 *
	 * @throws NullPointerException if {@code mainclass} is null
	 *
	 * @see #setUp(MaiLog, int, int, boolean, String)
	 */
	public static void setUp (MaiLog mainClass) {
		setUp(mainClass, -1, -1, true, "");
	}

	private static void logMissingMainClass () {
		log.add(new Log(1,"MaiLogger has not been set up yet"));
		reduceLog();
		save();
	}

	/**
	 * This method logs an event to the group "INFO". This group logs generally useful information.
	 * @param msg the description of the event that should be logged
	 */
	public static void logInfo (String msg) {
		log(new Log(0,msg));
	}

	/**
	 * This method logs an event to the group "WARNING". This group logs events that can potentially lead to a failure.
	 * @param msg the description of the event that should be logged
	 */
	public static void logWarning (String msg) {
		log(new Log(1, msg));
	}

	/**
	 * This method logs an event to the group "ERROR". This group logs errors that are fatal to the current operation, but not to the application.
	 * @param msg the description of the event that should be logged
	 */
	public static void logError (String msg) {
		log(new Log(2,msg));
	}
	
	/**
	 * This method logs an event to the group "CRITICAL". This group logs critical errors that is forcing a shutdown of the application.
	 * Furthermore it calls the {@code MaiLog.stop()} method.
	 * @param msg the description of the event that should be logged
	 */
	public static void logCritical (String msg) {
		log(new Log(3,msg));
		try {
			mainClass.stop();
		} catch (NullPointerException e) {
			log(new Log(3, "Cannot stop application; MaiLogger has not been set up yet"));
		}
	}
	
	private static void log (Log entry) {
		if (mainClass == null) {
			logMissingMainClass();
		} else {
			mainClass.sendLog(entry.getLog(time));
		}
		log.add(entry);		
		reduceLog();
		save();
	}

	/*
	if {@code maxLogs} is set and the maximum number of logs is exceed this method deletes old logged events until the
	number of logs is equals {@code maxLogs}
	 */
	private static void reduceLog () {
		while (maxLogs != -1 && log.size() > maxLogs) {
			log.remove(0);
		}
	}

	/**
	 *
	 * @return recording of all logged events in chronologically order
	 */
	public static String getLogAll () {
		return getLog(true, true, true, true);
	}


//	public static String getLog (boolean info, boolean warning, boolean error, boolean critical) {
//		StringBuilder content = new StringBuilder();
//		for (Log entry : log) {
//			switch (entry.getGroup()) {
//			case 0:
//				if (info) {
//					content.append(entry.getLog(time));
//				}
//				break;
//			case 1:
//				if (warning) {
//					content.append(entry.getLog(time));
//				}
//				break;
//			case 2:
//				if (error) {
//					content.append(entry.getLog(time));
//				}
//				break;
//			case 3:
//				if (critical) {
//					content.append(entry.getLog(time));
//				}
//				break;
//			}
//		}
//		if (content.length() > 0) {
//			content.deleteCharAt(content.length() - 1);
//		}
//		return content.toString();
//	}

	/**
	 * This method returns the recording of all logged events of every group for which the parameter is set true. It returns in chronologically order
	 * @param info group INFO (0)
	 * @param warning group WARNING (1)
	 * @param error group ERROR (2)
	 * @param critical group CRITICAL (3)
	 * @return records of all logged events of the requested group(s)
	 */
	public static String getLog (boolean info, boolean warning, boolean error, boolean critical) {
		StringBuilder content = new StringBuilder();
		log.stream().filter(e -> (e.getGroup() == 0 && info) || (e.getGroup() == 1 && warning) || (e.getGroup() == 2 && error) || (e.getGroup() == 3 && critical)).forEach(e -> content.append(e.getLog(time)).append("\n"));

		if (content.length() > 0) {
			content.deleteCharAt(content.length() - 1);
		}
		return content.toString();
	}

	/*
	writes the {@code log} to file
	 */
	private static void save () throws IllegalStateException {
		if (!new File(directory).exists()) {
			boolean newdir = new File(directory).mkdirs();
			if (!newdir) {
				mainClass.sendErrMsg("ERROR: Cannot write logs to file: Cannot create directory: " + directory);
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
			for(int i = 0; i < content.length - 1; i++) {
				w.write(content[i]);
				w.newLine();
			}
			w.write(content[content.length - 1]);
			new File(logFile + ".backup").delete();
		} catch (IOException e) {
			if (mainClass == null) {
				throw new IllegalStateException("MaiLogger has not been set up");
			}
			mainClass.sendErrMsg("ERROR: Cannot write logs to file (" + e.toString());
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
	 * Loads all logs of the log file ({@code dir}/{@code fileName}.log) into {@code log}
	 * @throws IllegalStateException if {@code MaiLogger hasn't been set up}
	 */
	public static void load () throws IllegalStateException {
		clearLog();
		String logFile = directory + "/" + fileName + ".log";
		if (!new File(logFile).exists()) {
			if (mainClass == null) {
				throw new IllegalStateException("MaiLogger has not been set up");
			}
			mainClass.sendErrMsg("ERROR: Cannot load logfile: File does not exist");
			return;
		}
		List<String> lines = new ArrayList<>();
		try (Stream<String> stream = Files.lines(new File(logFile).toPath())) {
			stream.forEach(lines::add);
		} catch (IOException e) {
			if (mainClass == null) {
				throw new IllegalStateException("MaiLogger has not been set up");
			}
			mainClass.sendErrMsg("ERROR: Cannot load logfile");
			return;
		}

		//split each line in level and content
		for (String string : lines) {
			try {
				String date = time ? string.substring(0,17) : "";
				String group = time ? string.substring(18).split(": ")[0] : string.split(": ")[0];
				log.add(new Log(date, group, string.substring(group.length() + (time ? 20 : 2))));
			} catch (IndexOutOfBoundsException e) {
				if (mainClass == null) {
					throw new IllegalStateException("MaiLogger has not been set up");
				}
				mainClass.sendErrMsg("ERROR: Cannot load logfile");
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
			if (mainClass == null) {
				throw new IllegalStateException("MaiLogger has not been set up");
			}
			mainClass.sendErrMsg("ERROR: Cannot rotate: No files in directory");
			return;
		}
		boolean renamed;
		for (int i = files.length; i > 0; i--) {
			renamed = files[i-1].renameTo(new File(directory + "/" + fileName + ".log." + i));
			if (!renamed) {
				if (mainClass == null) {
					throw new IllegalStateException("MaiLogger has not been set up");
				}
				mainClass.sendErrMsg("ERROR: Cannot rotate: Unable to rename files" + files[files.length - 1]);
				return;
			}
		}
		clearLog();
		save();
		if (rotations != -1 && files.length > rotations) {
			files = new File(directory).listFiles(f -> f.getName().startsWith(fileName));
			if (files == null) {
				if (mainClass == null) {
					throw new IllegalStateException("MaiLogger has not been set up");
				}
				mainClass.sendErrMsg("ERROR: Cannot rotate: No files in directory after rotating");
				return;
			}
			try {
				files[files.length - 1].delete();
			} catch (Exception e) {
				if (mainClass == null) {
					throw new IllegalStateException("MaiLogger has not been set up");
				}
				mainClass.sendErrMsg("ERROR: Cannot delete file" + files[files.length - 1]);
				e.printStackTrace();
			}
		}
	}

	/**
	 * empties {@code log}
	 */
	public static void clearLog () {
		log = new ArrayList<>();
	}
}
