import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Vector;

/**
 * Hashtable that stores nameHash objects using elfHash and the string from
 * nameHash.getName() to generate key values for the hash table.
 * 
 * @author Chase
 * 
 * @param <T>
 */
public class GISHash<T extends nameHash<? super T>> {

	private int elementCount;
	private int totalElem;
	private static final int[] size = { 1019, 2027, 4079, 8123, 16267, 32503,
			65011, 130027, 260111, 520279, 1040387, 2080763, 4161539, 8323151,
			16646323 };
	private static final int[] exp = { 713, 1418, 3100, 5686, 11386, 22752,
			45507, 91018, 182077, 364195, 728270, 1456534, 2913077, 5826205,
			11652426 };
	private int szIndex;
	private T[] HashTable;
	private long iSize;
	private Class<T> hashElem;

	@SuppressWarnings("unchecked")
	public GISHash(Class<T> c) {
		elementCount = 0;
		szIndex = 0;
		iSize = size[szIndex];
		hashElem = c;
		HashTable = (T[]) Array.newInstance(hashElem, (int) iSize);
	}

	@SuppressWarnings("unchecked")
	public GISHash(Class<T> c, int x) {
		elementCount = 0;
		szIndex = 0;
		iSize = size[szIndex];
		hashElem = c;
		HashTable = (T[]) Array.newInstance(hashElem, (int) iSize);
	}

	// checks if the table should resize
	private boolean needExp() {
		if (elementCount >= exp[szIndex]) {
			return true;
		}
		return false;
	}

	/**
	 * will resize the hashtable to be the next size up in the int[] size,
	 * reinserts all values in the table prior to the method call
	 * 
	 * @return true in all cases
	 */
	@SuppressWarnings("unchecked")
	private boolean grow() {
		int temp = elementCount;
		int counter = 0;
		int place = 0;
		T[] tempa = (T[]) Array.newInstance(hashElem, (int) iSize);
		while (temp > 0) {
			if (HashTable[counter] != null) {
				tempa[place] = HashTable[counter];
				place++;
				temp--;
			}
			counter++;
		}
		counter = 0;
		szIndex++;
		iSize = size[szIndex];
		HashTable = (T[]) Array.newInstance(hashElem, (int) iSize);
		elementCount = 0;
		totalElem = 0;
		while (counter < tempa.length) {
			if (tempa[counter] != null) {
				this.add(tempa[counter]);
			}
			counter++;
		}
		return true;
	}

	/**
	 * Adds an valid T object to the hashTable using elfhash and quadratic
	 * probing to handle collisions
	 * 
	 * @param x
	 *            valid T object
	 * @return the number of probe sequences it took (number of collisions),-1
	 *         if it failed
	 */
	public int add(T x) {
		long indexb = elfHash(x.getName());
		int index = (int) (indexb % (iSize));
		if (HashTable[index] == null) {
			HashTable[index] = x;
			elementCount++;
			totalElem += (x.getOffsets().size());
			if (needExp()) {
				grow();
			}
			return 0;
		} else {
			if ((HashTable[index]).getName().equals(x.getName())) {
				HashTable[index].addOff(x.getOffset());
				totalElem++;
				return 0;
			}
			int quadCount = 1;
			int count = elementCount;
			while (count < iSize) {

				index = (int) ((indexb + QuadC(quadCount)) % iSize);
				if (HashTable[index] == null) {
					HashTable[index] = x;
					elementCount++;
					totalElem += (x.getOffsets().size());
					;
					if (needExp()) {
						grow();
					}
					return quadCount;
				} else {
					if (HashTable[index].getName().equals(x.getName())) {
						if (HashTable[index].addOff((x).getOffset())) {
							totalElem++;
							return quadCount;
						}
					}
				}
				quadCount++;
				count++;
			}
		}
		return -1;
	}
	/**
	 * 
	 * @param x cat-Name to be searched
	 * @return null if nothing is found, vector of offsets that whose record's getName()
	 * matches x
	 */
	public Vector<Long> searchWord(String x) {
		long indexb = elfHash(x);
		int index = (int) (indexb % iSize);
		if (HashTable[index] != null && HashTable[index].getName().equals(x)) {
			return HashTable[index].getOffsets();
		} else {
			int counter = 0;
			int quadCount = 1;
			while (counter < elementCount) {
				index = (int) ((indexb + QuadC(quadCount)) % iSize);
				if (HashTable[index] != null
						&& HashTable[index].getName().equals(x)) {
					return HashTable[index].getOffsets();
				} else {
					quadCount++;
				}
				counter++;
			}
		}
		return null;

	}
	/**
	 * uses stringbuilder and it's append function to make a readable display of the hashTable
	 * contents
	 * @return String that diplays contents of hashTable
	 * @throws IOException
	 */
	public String display() throws IOException {
		StringBuilder ans = new StringBuilder();
		ans.append("Table Size: " + iSize + "\nTable elements: " + elementCount
				+ "\nTotal Elements: " + totalElem + "\n\n");
		int temp = elementCount;
		int found = 0;
		int count = 0;
		while (count < iSize) {
			if (HashTable[count] != null) {
				ans.append("slot: " + count + ": "
						+ HashTable[count].toString());
				found++;
			}
			count++;
			if (found == temp) {
				break;
			}
		}
		return (ans.toString() + "\n");
	}

	// computes the quadfuntion value given a step counter
	private long QuadC(int y) {
		long x = y;
		return (((x * x) + x) / 2);
	}

	// from course notes
	public static long elfHash(String toHash) {

		long hashValue = 0;
		for (int Pos = 0; Pos < toHash.length(); Pos++) { // use all elements

			hashValue = (hashValue << 4) + toHash.charAt(Pos); // shift/mix

			long hiBits = hashValue & 0xF000000000000000L; // get high nybble

			if (hiBits != 0) {
				hashValue ^= hiBits >> 56; // xor high nybble with second nybble
			}

			hashValue &= ~hiBits; // clear high nybble
		}

		return hashValue;
	}

}
