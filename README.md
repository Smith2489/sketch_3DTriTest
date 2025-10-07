To run this program, you will need to first install Processing from processing.org's website (https://processing.org/download). 
After this, locate core.jar in Processing's directory and link it to your install of this project.
(You may also want to add in all of the other jars, especially if you wish to mess around with this)
Finally, click run.
The reason I did this, despite the fact that you have to install Processing anyway, is because it makes breaking the code up into packages much easier.

If using the command line to compile:
Type "javac -d (compPath) -cp (classPath) @sources.txt" to compile all of the files to a specific directory
Copy over any data files (mostly stored in Data) to compPath
Type "java (compPath)/sketch_3DTriTest"

Alternatively:
Type "java -cp (classPath) @sources.txt"

The class path can be inserted directly, but you should put it in its own file and use "@(fileName)"