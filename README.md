To run this program, you will need to first install Processing from processing.org's website (https://processing.org/download).<br>
After this, locate core.jar in Processing's directory and link it to your install of this project.<br>
(You may also want to add in all of the other jars, especially if you wish to mess around with this)<br>
Finally, click run.<br>
The reason I did this, despite the fact that you have to install Processing anyway, is because it makes breaking the code up into packages much easier.<br>
<br>
If using the command line to compile:<br>
Type "javac -d (compPath) -cp (classPath) @sources.txt" to compile all of the files to a specific directory<br>
Copy over any data files (mostly stored in Data) to compPath<br>
Type "java (compPath)/sketch_3DTriTest"<br>
<br>
Alternatively:<br>
Type "java -cp (classPath) @sources.txt"<br>
The class path can be inserted directly, but you should put it in its own file and use "@(fileName)"