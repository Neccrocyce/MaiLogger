package logger;

/**
 * This interface should be implemented by the main class of the application. it provides a method that is called when a
 * critical error occurred, a method that is called every time a event is logged and a method which is called if
 * MaiLogger ran into an error.
 *
 * @author Neccrocyce
 * @see <a href="https://github.com/Neccrocyce">Neccrocyce - GitHub</a>
 * @see MaiLogger
 */
public interface MaiLog {
	
	/**
	 * This method is called if a critical error occurs it is supposed to stop the running application and exit it.
	 */
	public void stop();

	/**
	 * This method will be called if MaiLogger ran into an error. This is usually only the case when an I/O Exception
	 * occurred during an operation with the log files.
	 * @param msg the description of the event that led MaiLogger to run into an error
	 */
	public void sendErrMsg(String msg);

	/**
	 * This method will be called every time an event is logged.
	 * @param msg the description of the event that should be logged
	 */
	public void sendLog(String msg);
}
