# MaiLogger

MaiLogger is a tool for logging events representing the state of a program. MaiLogger supports following features 
• Logging 5 groups of events (info, warning, error, critical) and user-defined groups
• writing logged events to a ﬁle 
• Setting the folder of that ﬁle
• Loading log ﬁles
• Setting the maximum number of events to log (deletes old ones in case of two many events; uses FIFO manner to remove log entries) (see: Conﬁgure Mailogger) 
• Log rotation
• Setting the maximum number of ﬁles saved by the log rotation
• Setting whether the time representing the occurrence of an event is also logged
• Stopping the application if a critical error occurred
• Logging of tasks with the appendix of ”success”, ”failed” or ”abort”

For more information, see documentation
The jar file is located in the directory MaiLogger
