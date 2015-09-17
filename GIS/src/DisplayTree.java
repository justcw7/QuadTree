import java.util.Vector;
import java.util.Random;
/**
 * Class that will give a 'readable' display of a quadTree in string format
 * @author Chase
 *
 */
public class DisplayTree {

	double xMin = 0, xMax = 0, yMin = 0, yMax = 0;
	Vector<GeoCoord> data = null;
	Random source;
	int totalPtsAssigned;
	String pad;

	public DisplayTree() {
		this.totalPtsAssigned = 0;
		pad = new String("--");
	}

	public String writeGeoCoords(int Pts) {
		return "";
	}
	/**
	 *  prints the contents of the parameter
	 * @param Tree the tree to be printed
	 * @param ptsPerDataItem the bucket size of the tree
	 * @param x the offset used to zero the quadtree's xLo
	 * @param y the offset used to zero the quadtree's x=yLo
	 * @return A string object that will display te tree's contents
	 */
	public String printTree(prBQuadTree<GeoCoord> Tree, int ptsPerDataItem,
			long x, long y) {
		StringBuilder mac = new StringBuilder();
		if (Tree.root == null)
			mac.append(writeGeoCoords(ptsPerDataItem) + "  Empty tree.\n");
		else
			mac.append(printTreeHelper(Tree.root, "", ptsPerDataItem, x, y));

		return mac.toString();
	}

	@SuppressWarnings("unchecked")
	public String printTreeHelper(prBQuadTree<GeoCoord>.prQuadNode sRoot,
			String Padding, int ptsPerDataItem, long x, long y) {
		StringBuilder toAdd = new StringBuilder();

		if (sRoot == null) {
			toAdd.append(writeGeoCoords(0) + " " + Padding + "*\n");
			return toAdd.toString();
		}
		if (!sRoot.isLeaf()) {
			prBQuadTree<GeoCoord>.prQuadInternal p = (prBQuadTree<GeoCoord>.prQuadInternal) sRoot;
			toAdd.append(printTreeHelper(p.SW, Padding + pad, ptsPerDataItem,
					x, y));
			toAdd.append(printTreeHelper(p.SE, Padding + pad, ptsPerDataItem,
					x, y));
		}
		if (sRoot.isLeaf()) {
			prBQuadTree<GeoCoord>.prQuadLeaf p = (prBQuadTree<GeoCoord>.prQuadLeaf) sRoot;
			toAdd.append((writeGeoCoords(ptsPerDataItem) + " " + Padding));
			for (int pos = 0; pos < p.Elements.size(); pos++) {
				toAdd.append((pos + 1) + ": ("
						+ (p.Elements.get(pos).getX() + x) + ","
						+ (p.Elements.get(pos).getY() + y) + ")"
						+ p.Elements.get(pos).getOffset().toString() + "");
			}
			toAdd.append("\n");
		} else if (!sRoot.isLeaf())
			toAdd.append(writeGeoCoords(1) + " " + Padding + "@\n");
		else
			toAdd.append(writeGeoCoords(0) + " " + sRoot.getClass().getName()
					+ "#\n");

		// Check for and process NE and NW subtrees
		if (!sRoot.isLeaf()) {
			prBQuadTree<GeoCoord>.prQuadInternal p = (prBQuadTree<GeoCoord>.prQuadInternal) sRoot;
			toAdd.append(printTreeHelper(p.NE, Padding + pad, ptsPerDataItem,
					x, y));
			toAdd.append(printTreeHelper(p.NW, Padding + pad, ptsPerDataItem,
					x, y));
		}
		return toAdd.toString();
	}

}
