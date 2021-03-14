#Changelog

v3.0 -2021-03-11:
--------
#### ADDED:
- Group DEBUG
- Group TASK
- Logs with custom group

#### CHANGED:
- Optional to implement MaiLog.java
- Logfile is now named by the user
- Tasks can now be outputted immediately after starting
- methos getLog has a new signature

v 2.0 -2018-03-01:
--------
##### ADDED:
- task specific logs
- logs now contain the time when the event has been logged

##### CHANGED:
- adding logs will write them at the end of existing log files instead of creating new files
- changed project name from MyLogger to MaiLogger 

##### REFACTORED:
- logs are now saved as Log class instead of List<String[]>
- Groups are now identified by their id
- Added Class Group

v 1.0:
--------
##### ADDED:
- MyLogger