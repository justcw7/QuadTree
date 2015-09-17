import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

/**
 * This class runs all the commands from a specified file and prints results to
 * two other specified files
 * 
 * @author Chase
 * 
 */
public class CommandParser {

	private prBQuadTree<GeoCoord> tree;
	private GISHash<HashElem> hashTable;
	private bufferPool buff;
	private long xZero;
	private long xRange;
	private long yRange;
	private long offset;
	private long yZero;
	private int cmdC;
	private RandomAccessFile commandRead;
	private RandomAccessFile logger;
	private RandomAccessFile databaser;
	private String currLine;
	private DisplayTree disTree;

	/**
	 * 
	 * @param a
	 *            where the imported GISrecords will be stored
	 * @param b
	 *            The file which contains all the files to be read.
	 * @param c
	 *            Where all information about execution of commands in file b
	 *            where go.
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public CommandParser(File a, File b, File c) throws NumberFormatException,
			IOException {
		cmdC = 1;
		offset = 0;
		disTree = new DisplayTree();
		logger = new RandomAccessFile(c, "rw");
		databaser = new RandomAccessFile(a, "rw");
		commandRead = new RandomAccessFile(b, "r");
		logger.writeBytes("Justin Whitt\njustcw7\nGIS System\n\n");
		logger.writeBytes("Log File: " + c.getName() + "\n" + "Database File: "
				+ a.getName() + "\n" + "Command File: " + b.getName() + "\n\n");
		logger.writeBytes("Running commands in the command file:\n");
		logger.writeBytes("------------------------------------------------------------\n\n");
		String reader;
		long[] points = { 0, 0, 0, 0 };
		while ((currLine = commandRead.readLine()) != null) {
			if (currLine.charAt(0) != ';') {
				reader = currLine.substring(6);// the world command should be
												// the first non comment line.
				int counter = 0;
				currLine = currLine.substring(currLine.indexOf('\t') + 1);// this
																			// should
																			// only
																			// contain
																			// the
																			// four
																			// bounds
				String[] linSplits = currLine.split("\t");
				while (counter < 4) {
					points[counter] = Long.valueOf(linSplits[counter]
							.substring(0, linSplits[counter].length() - 1));
					// this will check if the bound will be negative by checking
					// for W or S.
					if (linSplits[counter]
							.charAt(linSplits[counter].length() - 1) == 'W'
							|| linSplits[counter].charAt(linSplits[counter]
									.length() - 1) == 'S') {
						points[counter] = points[counter] * (-1);
					}
					currLine = currLine.substring(currLine.indexOf('\t') + 1);
					counter++;
				}
				// convert the four bounds to seconds only format
				points[0] = toSeconds(points[0]);
				points[1] = toSeconds(points[1]);
				points[2] = toSeconds(points[2]);
				points[3] = toSeconds(points[3]);
				logger.writeBytes("Command World " + cmdC + " " + reader
						+ "\n\nQuadTree boundries: \nxLo: " + points[0]
						+ " xHi: " + points[1] + "\n" + "yLo " + points[2]
						+ " yHi: " + points[3] + "\n");
				logger.writeBytes("------------------------------------------------------------\n");
				// Tree bounds will be created from zero to the difference
				// between the respective bound pairs
				// This design choice eliminates negatives from tree
				// calculations, causes true tree bounds to be smaller numbers
				xZero = points[0];
				yZero = points[2];
				xRange = (points[1] - xZero) + 1;// one is added for edge cases
				yRange = (points[3] - yZero) + 1;
				cmdC++;
				break;
			}
		}
		// create instances of a tree, hashTable and buffer pool.
		tree = new prBQuadTree<GeoCoord>(0, xRange, 0, yRange, 4);
		hashTable = new GISHash<HashElem>(HashElem.class);
		buff = new bufferPool();

	}

	// converts a longitude/latitude long value in DDMMSS/DDDMMSS format to just
	// seconds.
	private long toSeconds(long x) {
		boolean neg = false;
		if (x < 0) {
			neg = true;
		}
		x = Math.abs(x);
		long sec = x % 100;// get first two digits
		x = x / 100;// move over two places
		long min = x % 100;// get next two digits
		x = x / 100;// move to places
		long answer = (x * 3600 + min * 60 + sec);// conversion equation
		if (neg) { // check if negative needs to be applied.
			return answer * -1;
		}
		return answer;
	}

	// converts a longitude/latitude DDMMSS/DDDMMSS String object format to just
	// seconds as a long value.
	private long toSeconds(String op) {
		long x = Long.valueOf(op.substring(0, op.length() - 1)); // get number
																	// value
		boolean neg = false;
		// check if the answer should be negative
		if (op.charAt(op.length() - 1) == 'W'
				|| op.charAt(op.length() - 1) == 'S') {
			neg = true;
		}
		long ans = toSeconds(x);// call other method
		if (neg) { // make negative if needed
			ans = ans * -1;
		}
		return ans;
	}

	/**
	 * This is basically the true 'main' method for my project. This will read
	 * from the command file provided until it reaches a 'quit' call or end of
	 * file. if a line isn't a comment ';...' this will attempt to find out
	 * which command is there and execute it to project specifications.
	 * 
	 * Pre: files given in constructor must follow specifications,
	 * incorrect/wrongly formated commands in the command file are not
	 * guaranteed to be safe or not throw an exception
	 * 
	 * @return 0 if the program was exited with 'quit' command with in the
	 *         command file, -1 if end of file is reached with no 'quit' command
	 *         or if something went horrible wrong
	 * @throws IOException
	 */
	public int run() throws IOException {
		while ((currLine = commandRead.readLine()) != null) {// make sure line
																// exists
			if (currLine.length() > 0 && currLine.charAt(0) != ';'
					&& currLine.charAt(0) != '\n') {// make sure line isn't
													// blank
				String temp = currLine.substring(0, 4); // grabs fist four
														// chars, all commands
														// are atleast four
														// chars long, so this
														// should be a safe
														// assumption.
				if (temp.equals("quit")) { // check if quit was called
					logger.writeBytes("quit command called: \nClosing Files and Exiting...\n");
					logger.close();// close files, return 0
					databaser.close();
					commandRead.close();
					return 0;
				}
				if (temp.equals("what")) {// checks if command is a 'what'
											// command
					String wutC = currLine.substring(0, currLine.indexOf("\t"));// grabs
																				// the
																				// whole
																				// command
																				// word
					switch (wutC) {// determines what 'what' command it is
									// performs appropriate actions
					case "what_is_at":
						logger.writeBytes("Command " + cmdC + " What_is_at: "
								+ currLine.substring(10) + "\n");// print
																	// command
																	// to log
						whatIA(currLine.substring(currLine.indexOf('\t') + 1));// call
																				// helper
																				// method
																				// with
																				// command
																				// parameters
						break;
					case "what_is":
						logger.writeBytes("Command " + cmdC + " What_is: "
								+ currLine.substring(8) + " \n");// print
																	// command
																	// to log
						whatI(currLine.substring(currLine.indexOf('\t') + 1));// call
																				// helper
						break;
					case "what_is_in":
						logger.writeBytes("Command " + cmdC + " What_is_in: "
								+ currLine.substring(10) + " \n\n");// print
																	// command
						whatII(currLine.substring(currLine.indexOf('\t') + 1));// call
																				// helper
						break;
					}
				} else if (temp.equals("debu")) {// checks if a 'debug' command
													// is being called
					String meh = currLine.substring(currLine.indexOf('\t') + 1)
							.trim();// get second word
					switch (meh) {
					case "quad":
						logger.writeBytes("Command " + cmdC
								+ " debug quad \n\n");// print
						// command
						// to
						// log
						logger.writeBytes(disTree.printTree(tree, 4, xZero,// call
																			// print
																			// tree
																			// in
																			// the
																			// display
																			// tree
																			// class
								yZero));
						break;
					case "pool":
						logger.writeBytes("Command " + cmdC
								+ " debug pool \n\n");// print
						// command
						// to
						// log
						logger.writeBytes(buff.Display());// print the display
															// string from
															// buffer pool
						break;
					case "hash":
						logger.writeBytes("Command " + cmdC
								+ " debug hash \n\n");// print
						// command
						// to
						// log
						logger.writeBytes(hashTable.display());// print
																// hashTable's
																// display
																// method to log
						break;
					case "world":
						logger.writeBytes("Command " + cmdC + " debug world \n");// print
						// command
						// to log
						logger.writeBytes("Not supported\n");// command not
																// required and
																// not
																// supported,
																// state in log
																// file
						break;
					}
				} else if (temp.equals("impo")) { // last command is import,
													// check for it
					logger.writeBytes("Command: " + cmdC + " import "
							+ currLine.substring(7) + "\n\n"); // print command
																// to log
					add(currLine.substring(currLine.indexOf('\t')).trim()); // pass
																			// filename
																			// to
																			// helper
																			// method
																			// add
				}
				cmdC++;
				logger.writeBytes("------------------------------------------------------------\n");
			}
		}
		logger.close();// close files, return -1
		databaser.close();
		commandRead.close();
		return -1;
	}

	/**
	 * helper method that will create a String in the same format that 'elfHash'
	 * uses in the hashTable from the parameter it will then search the hash
	 * table for a GISrecord whose Cat-Name matches the one it made prints each
	 * match's specified parameters to log, if no match is found, it will print
	 * this to the log Pre: x must have a tab character in between at least two
	 * non space chars. 'b\tb' would work.
	 * 
	 * @param x
	 *            the parameters given on the same line as the command.
	 * @throws IOException
	 */
	private void whatI(String x) throws IOException {
		String name = x.substring(0, x.lastIndexOf("\t"));
		String stateAbv = x.substring(x.lastIndexOf("\t"));
		name = name.trim();
		stateAbv = stateAbv.trim();
		String search = stateAbv + name; // make cat-Name the same way GISrecord
											// does
		Vector<Long> matches = hashTable.searchWord(search);// gets the vector
															// of offsets from
															// the search
															// function
		if (matches == null || matches.size() == 0) {// check if matches is
														// empty
			logger.writeBytes("no matches found. \n");
			return;
		}
		GISrecord temp;
		logger.writeBytes("Found " + matches.size() // write number found
				+ " records for search for: " + x + "\n\n");
		for (int i = 0; i < matches.size(); i++) {
			temp = buff.getRecord(matches.get(i), databaser);// have buffer pool
																// make a
																// GISrecord
																// from offset
			if (temp != null) { // print specified information to log
				logger.writeBytes("offset: " + matches.get(i)
						+ " Country name: " + temp.getCountryName()
						+ "\nPrimary latitude: " + temp.getPrimaryLatitude()
						+ " Primary longitude: " + temp.getPrimaryLongitude()
						+ "\n");
			}
		}
	}

	/**
	 * Helper method that will see if the are any stored records with the
	 * coordinates given and prints any found records to log
	 * 
	 * Pre: x must be two DD/MM/SS/DDD/MM/SS formated strings with a space
	 * between them
	 * 
	 * @param x
	 *            the parameters given on the same line as the command.
	 * @throws IOException
	 */
	private void whatIA(String x) throws IOException {
		String lat = x.substring(0, x.indexOf('	'));
		String longi = x.substring(x.indexOf('	') + 1);
		long latD = toSeconds(lat) - yZero;
		long longD = toSeconds(longi) - xZero;
		GeoCoord search = new GeoCoord(longD, latD, -1);
		GeoCoord ans = tree.find(search);
		if (ans == null) {
			logger.writeBytes("no matches found\n");
			return;
		}
		logger.writeBytes("Found " + ans.getSz() + " records at " + lat + " "
				+ longi + "\n\n");
		Vector<Long> tempa = ans.getOffset();
		for (int k = 0; k < ans.getSz(); k++) {
			GISrecord toAdd = buff.getRecord(tempa.get(k), databaser);
			logger.writeBytes("Offset: " + tempa.get(k) + " Feature Name: "
					+ toAdd.getFeatureName() + "\n" + "Country Name: "
					+ toAdd.getCountryName() + "  State: "
					+ toAdd.getStateAlphabeticCode() + "\n");
			if (k < ans.getSz() - 1) {
				logger.writeBytes("=================================\n");
			}
		}
	}

	/**
	 * Helper method that determines what 'what_is_in' command is being call and
	 * acts appropriatly
	 * 
	 * @param x
	 *            the parameters given on the same line as the command.
	 * @throws IOException
	 */
	private void whatII(String x) throws IOException {
		String which = x.substring(0, x.indexOf('\t'));// gets next word
		switch (which) {
		case "-l":
			printwII(whatHelp(x.substring(x.indexOf('\t') + 1)), 2);
			break;
		case "-c":
			printwII(whatHelp(x.substring(x.indexOf('\t') + 1)), 3);
			break;
		default:
			printwII(whatHelp(x), 1);
			break;
		}
	}

	/**
	 * Helper method that will determine that coordinate location, ranges and
	 * pass along this information to the tree's find method, returning the
	 * ressult Pre: x must be in ycoordinate,xcoordinate,yrange,xrange format
	 * 
	 * @param x
	 *            the parameters given on the same line as the command.
	 * @throws IOException
	 */
	private Vector<GeoCoord> whatHelp(String x) {
		String lat = x.substring(0, x.indexOf('	'));
		String longi = x.substring(x.indexOf('	') + 1, x.indexOf('	') + 9)
				.trim();
		long latD = this.toSeconds(lat);
		long longD = this.toSeconds(longi);
		x = x.substring(x.indexOf('\t') + 1);
		long halfLat = Long.valueOf(x.substring(x.indexOf('\t') + 1,
				x.indexOf('\t', x.indexOf('\t') + 1))) + 1;
		long halfLong = Long.valueOf(x.substring(
				x.indexOf("\t", x.indexOf('\t') + 1)).trim()) + 1;
		long xLo = (longD - halfLong) - xZero;
		long xHi = (longD + halfLong) - xZero;
		long yLo = (latD - halfLat) - yZero;
		long yHi = (latD + halfLat) - yZero;
		return tree.find(xLo, xHi, yLo, yHi);
	}

	/**
	 * Prints the right information of each element in ans based on the type
	 * number given
	 * 
	 * @throws IOException
	 */
	private void printwII(Vector<GeoCoord> ans, int type) throws IOException {
		GISrecord temp;
		int count = 0;
		if (ans.size() == 0) {
			logger.writeBytes("No records found with in specified region\n");
		}
		for (int i = 0; i < ans.size(); i++) {
			Vector<Long> offsets = ans.get(i).getOffset();
			for (int j = 0; j < offsets.size(); j++) {
				temp = buff.getRecord(offsets.get(j), databaser);
				count++;
				if (type == 1) {
					logger.writeBytes("Offset      : " + temp.getOff() + "\n"
							+ "Feature name: " + temp.getFeatureName() + "\n"
							+ "State name  : " + temp.getStateAlphabeticCode()
							+ "\n" + "Latitude    : "
							+ temp.getPrimaryLatitude() + "\n"
							+ "Longitude   : " + temp.getPrimaryLongitude()
							+ "\n");
					if (ans.size() > 1) {
						logger.writeBytes("=================================\n");
					}
				}
				if (type == 2) {
					logger.writeBytes("Record: " + count + ":" + "\n");
					if (temp.getFeatureID() != 0) {
						logger.writeBytes("Feature ID         : "
								+ temp.getFeatureID() + "\n");
					}
					if (!temp.getFeatureName().equals("")) {
						logger.writeBytes("Feature Name       : "
								+ temp.getFeatureName() + "\n");
					}
					if (!temp.getName().equals("")) {
						logger.writeBytes("Feature cat        : "
								+ temp.getName() + "\n");
					}
					if (!temp.getStateAlphabeticCode().equals("")) {
						logger.writeBytes("State              : "
								+ temp.getStateAlphabeticCode() + "\n");
					}
					if (!temp.getCountryName().equals("")) {
						logger.writeBytes("County             : "
								+ temp.getCountryName() + "\n");
					}
					if (!temp.getPrimaryLatitude().equals("")) {
						logger.writeBytes("Latitude           : "
								+ temp.getPrimaryLatitude() + "\n");
					}
					if (!temp.getPrimaryLongitude().equals("")) {
						logger.writeBytes("Longitude          : "
								+ temp.getPrimaryLongitude() + "\n");
					}
					if (temp.getFeetElev() != 0) {
						logger.writeBytes("Elev in feet       : "
								+ temp.getFeetElev() + "\n");
					}
					if (!temp.getDateCreated().equals("")) {
						logger.writeBytes("Date Created       : "
								+ temp.getDateCreated() + "\n");
					}
					if (ans.size() > 1) {
						logger.writeBytes("=================================\n");
					}
				}
			}
		}

		logger.writeBytes("records found within reigon: " + count + "\n");

	}

	/**
	 * helper method that will attempt to make a GISrecord object from a string,
	 * it assign what values that are available and return null if the file if
	 * deem to be missing key components
	 * 
	 * Pre: x is in the specified GIS format '|' = delimiter
	 * 
	 * @param x
	 *            the parameters given on the same line as the command.
	 * @throws IOException
	 */
	private GISrecord makeGIS(String line) {
		GISrecord ans = new GISrecord();
		String temp = line;
		for (int i = 0; i < 19; i++) {
			if (temp.indexOf('|') != -1) {
				if (temp.substring(0, temp.indexOf('|')).length() > 1) {
					switch (i) {
					case 0:
						ans.setFeatureID(Integer.valueOf(temp.substring(0,
								temp.indexOf('|'))));
						break;
					case 1:
						ans.setFeatureName(temp.substring(0, temp.indexOf('|')));
						break;
					case 2:
						ans.setFeatureClass(temp.substring(0, temp.indexOf('|')));
						break;
					case 3:
						ans.setStateAlphabeticCode(temp.substring(0,
								temp.indexOf('|')));
						break;
					case 4:
						ans.setStateNumericCode(Integer.valueOf(temp.substring(
								0, temp.indexOf('|'))));
						break;
					case 5:
						ans.setCountryName(temp.substring(0, temp.indexOf('|')));
						break;
					case 6:
						ans.setCountryNumericCode(Integer.valueOf(temp
								.substring(0, temp.indexOf('|'))));
						break;
					case 7:
						ans.setPrimaryLatitude(temp.substring(0,
								temp.indexOf('|')));
						break;
					case 8:
						ans.setPrimaryLongitude(temp.substring(0,
								temp.indexOf('|')));
						break;
					case 9:
						ans.setLat(Double.valueOf(temp.substring(0,
								temp.indexOf('|'))));
						break;
					case 10:
						ans.setPrimaryLongitudeDec(Double.valueOf(temp
								.substring(0, temp.indexOf('|'))));
						break;
					case 11:
						ans.setSourceLatitude(temp.substring(0,
								temp.indexOf('|')));
						break;
					case 12:
						ans.setSourceLongitude(temp.substring(0,
								temp.indexOf('|')));
						break;
					case 13:
						ans.setSourceLat(Double.valueOf(temp.substring(0,
								temp.indexOf('|'))));
						break;
					case 14:
						ans.setSourceLong(Double.valueOf(temp.substring(0,
								temp.indexOf('|'))));
						break;
					case 15:
						ans.setMeterElev(Integer.valueOf(temp.substring(0,
								temp.indexOf('|'))));
						break;
					case 16:
						ans.setFeetElev(Integer.valueOf(temp.substring(0,
								temp.indexOf('|'))));
						break;
					case 17:
						ans.setMapName(temp.substring(0, temp.indexOf('|')));
						break;
					case 18:
						ans.setDateCreated(temp.substring(0, temp.indexOf('|')));
						break;
					}
				}
				temp = temp.substring(temp.indexOf("|") + 1);
			}

		}
		
		return ans;
	}

	/**
	 * adds all valid records from an import file line by line. record offsets
	 * are stored in the hashTable and Tree. Pre: x is the name to a valid File
	 * 
	 * @param x
	 *            the file name for a file containing GISrecords
	 * @throws IOException
	 */
	public void add(String x) throws IOException {
		int totalCount = 0;
		int invalid = 0;
		int maxProbe = 0;
		File gisRecords = new File(x);
		RandomAccessFile reader = new RandomAccessFile(gisRecords, "r");
		String gis;
		GISrecord temp;
		reader.readLine();
		databaser.seek(offset);
		while ((gis = reader.readLine()) != null) {
			if ((temp = makeGIS(gis)) != null) {// attempt to make GISrecord
				offset = databaser.getFilePointer();// update offset // from
													// line
				temp.setOff(offset);// set GISrecords offset
				databaser.writeBytes(temp.toString() + "\n");// write record to
				// database file
				if (!(temp.getPrimaryLatitude().equals("Unknown")
						|| temp.getPrimaryLongitude().equals("Unknown"))) {
					GeoCoord toAdd = new GeoCoord(
						// create instance of GeoCoord to add to tree
						(toSeconds(temp.getPrimaryLongitude()) - xZero),
						(toSeconds(temp.getPrimaryLatitude()) - yZero), offset);
					tree.insert(toAdd);
				}

				HashElem toAdd2 = new HashElem(temp.getName(), offset);// create
																		// instance
																		// of
																		// HashElem
																		// to
																		// be
																		// added
																		// to
																		// hashTable
				maxProbe = Math.max(maxProbe, hashTable.add(toAdd2));// add
																		// to
																		// table,
																		// keeping
																		// track
																		// of
																		// max
																		// probe
																		// sequence
				totalCount++;

			} else {
				invalid++;
			}

		}
		offset = databaser.getFilePointer();
		logger.writeBytes("Imported " + totalCount
				+ " GIS records from the given file. \nMax probe sequences:  "
				+ maxProbe + " Number of invalid records found:  " + invalid
				+ "\n");
		reader.close();
	}// log the specified information
}
