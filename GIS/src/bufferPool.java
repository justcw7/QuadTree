
import java.io.IOException;
import java.io.RandomAccessFile;
/**
 * bufferpool class that maintains an array of up to ten GISrecord objects sorted from MRU to LRU
 * @author Chase
 *
 */
public class bufferPool {

	private static GISrecord[] pool = new GISrecord[10];
	private int count;

	public bufferPool() {
		count = 0;
	}
	/**
	 * adds the GISrecord to the pool, removing LRU object and restructuring array if needed
	 * @param x object to be added
	 * @return true if insertion was successful
	 */
	public boolean addRec(GISrecord x) {
		if (count < 10) {
			pool[count] = x;
			count++;
			return true;
		} else {
			for (int i = 9; i >= 1; i--) {
				pool[i] = pool[i - 1];
			}
			pool[0] = x;
			return true;
		}
	}
	/**
	 * same method from the command parsar with no checks for invalid records, because
	 * this only reads from the database file which will only have valid files
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
	//when object in pool is used, this moves it to be the MRU object
	private void MovetoFront(int i) {
		GISrecord temp = pool[0];
		GISrecord hold;
		pool[0] = pool[i];
		for(int k=1;k<=i;k++) {
			hold = pool[k];
			pool[k] = temp;
			temp = hold;
		}
	}
	/**
	 * checks pool for record with offset, creating a GISrecord from the offset if it is not found in the pool
	 * @param x file offset of wanted GISrecord
	 * @param data RAF of database
	 * @return GIS record with offset, null if not found
	 * @throws IOException
	 */
	public GISrecord getRecord(Long x, RandomAccessFile data)
			throws IOException {
		GISrecord ans = null;
		boolean found = false;
		data.seek(x);
		for (int i = 0; i < count; i++) {
			if (!found && pool[i].getOff().equals(x)) {

				ans = pool[i];
				MovetoFront(i);
				found = true;
			}
		}
		if (!found) {
			String temp = data.readLine();
			ans = makeGIS(temp);
			ans.setOff(x);
			addRec(ans);

		}
		return ans;
	}
	/**
	 * Creates readable display of elements in pool in String format
	 * @return String display
	 */
	public String Display() {
		StringBuilder temp = new StringBuilder();
		temp.append("MRU:\n");
		temp.append(count + " Records in pool:\n");
		for (int i = 0; i < count; i++) {
			temp.append(pool[i].toString() + "\n");
		}
		temp.append("LRU:\n");
		return temp.toString();
	}
}
