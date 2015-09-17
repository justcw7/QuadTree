import java.io.File;
import java.io.IOException;

public class GIS {

	/**
	 * The main method to the GIS project, checks if argument count is correct and the 
	 * supposed command file name is the name of a file. If things are in order this method
	 * passes along its argument to the command parser and then calls the run method for the 
	 * commandparser
	 * @param args three filenames for: database,commandScript,log
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		if (args.length != 3) {
			System.out.println("incorrect number of arguments: " + args.length);
			return;
		}
		File commands = new File(args[1]);
		if (!(commands.exists() && !commands.isDirectory())) {
			System.out.println("command file doesn't exist or is a directory ");
			return;
		}
		File log = new File(args[2]);
		File dataBase = new File(args[0]);
		CommandParser brain = new CommandParser(dataBase,commands, log);
		if(brain.run()!=0) {
			System.out.println("quit was never called, reached end of command file");
			return;
		}
		System.out.println("program exited normally");
		
	}
}
//On my honor:
//
//- I have not discussed the Java language code in my program with
//anyone other than my instructor or the teaching assistants
//assigned to this course.
//
//- I have not used Java language code obtained from another student,
//or any other unauthorized source, either modified or unmodified.
//
//- If any Java language code or documentation used in my program
//was obtained from another source, such as a text book or course
//notes, that has been clearly noted with a proper citation in
//the comments of my program.
//
//- I have not designed this program in such a way as to defeat or
//interfere with the normal operation of the Curator System.
//
//<Justin Whitt>
