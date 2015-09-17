// The test harness will belong to the following package; the QuadTree
// implementation must belong to it as well. In addition, the QuadTree
// implementation must specify package access for the node types and tree
// members so that the test harness may have access to it.
//

// On my honor:
//
// - I have not discussed the Java language code in my program with
// anyone other than my instructor or the teaching assistants
// assigned to this course.
//
// - I have not used Java language code obtained from another student,
// or any other unauthorized source, either modified or unmodified.
//
// - If any Java language code or documentation used in my program
// was obtained from another source, such as a text book or course
// notes, that has been clearly noted with a proper citation in
// the comments of my program.
//
// - I have not designed this program in such a way as to defeat or
// interfere with the normal operation of the Curator System.
//
// <Justin Whitt>

import java.util.Vector;
/**
 * generic quadtree that implements a bucketSz, insert, Find, and vectorFind
 * @author Chase
 *
 * @param <T>
 */
public class prBQuadTree<T extends DCompare2D<? super T>> {

	abstract class prQuadNode {

		protected boolean isLeaf() {
			if (this.getClass().equals(prQuadLeaf.class)) {
				return true;
			}
			return false;
		}
	}

	class prQuadLeaf extends prQuadNode {
		Vector<T> Elements;

		protected prQuadLeaf(T elem) {
			Elements = new Vector<T>();
			Elements.add(elem);
		}

		protected prQuadLeaf(Vector<T> x) {
			Elements = x;
		}

		protected prQuadLeaf() {
			Elements = new Vector<T>();
		}

		protected Vector<T> getVec() {
			return Elements;
		}

		protected int getSz() {
			return Elements.size();
		}

		protected void addElem(T elem) {
			Elements.add(elem);
		}

		protected T getElem(int x) {
			return Elements.get(x);
		}
	}

	class prQuadInternal extends prQuadNode {
		prQuadNode NW, NE, SE, SW;

		protected prQuadInternal() {
			NW = null;
			NE = null;
			SE = null;
			SW = null;
		}

		protected int getCount() {
			int temp = 0;
			if (NW != null) {
				temp++;
			}
			if (NE != null) {
				temp++;
			}
			if (SW != null) {
				temp++;
			}
			if (SE != null) {
				temp++;
			}
			return temp;
		}

		protected prQuadInternal(prQuadNode NW, prQuadNode NE, prQuadNode SE,
				prQuadNode SW) {
			this.NW = NW;
			this.NE = NE;
			this.SE = SE;
			this.SW = SW;
		}

		protected int getDel(prQuadLeaf x) {
			if (NW != null) {
				NW = null;
				return 1;
			}
			if (NE != null) {
				NE = null;
				return 2;
			}
			if (SE != null) {
				SE = null;
				return 3;
			}
			if (SW != null) {
				SW = null;
				return 4;
			}
			return 5;
		}

		protected prQuadNode getDir(Direction x) {
			if (x.name().equals("NW")) {
				return this.NW;
			}
			if (x.name().equals("NE")) {
				return this.NE;
			}
			if (x.name().equals("SE")) {
				return this.SE;
			}
			if (x.name().equals("SW")) {
				return this.SW;
			}
			return null;
		}

		protected Direction getDirect(prQuadLeaf x) {
			if (NW.equals(x.getElem(0))) {
				return Direction.NW;
			}
			if (SW.equals(x.getElem(0))) {
				return Direction.SW;
			}
			if (NE.equals(x.getElem(0))) {
				return Direction.NE;
			}
			if (SE.equals(x.getElem(0))) {
				return Direction.SE;
			}
			return null;
		}

		protected int setDir(Direction x, prQuadNode y) {
			if (x.name().equals("NW")) {
				NW = y;
				return 1;
			}
			if (x.name().equals("NE")) {
				NE = y;
				return 2;
			}
			if (x.name().equals("SE")) {
				SE = y;
				return 3;
			}
			if (x.name().equals("SW")) {
				SW = y;
				return 4;
			}
			return -1;
		}

		// pre: only one is not null...
		protected prQuadNode getOther() {
			if (NW != null) {
				return NW;
			}
			if (NE != null) {
				return NE;
			}
			if (SE != null) {
				return SE;
			}
			return SW;

		}
	}

	protected boolean duplicate = false;
	prQuadNode root;
	long xMin, xMax, yMin, yMax;
	int bucketSz;

	// Initialize QuadTree to empty state, representing the specified region.
	public prBQuadTree(long xMi, long xMa, long yMi, long yMa) {
		xMin = xMi;
		xMax = xMa;
		yMin = yMi;
		yMax = yMa;
		bucketSz = 1;
		root = null;
	}

	public prBQuadTree(long xMi, long xMa, long yMi, long yMa, int x) {
		xMin = xMi;
		xMax = xMa;
		yMin = yMi;
		yMax = yMa;
		bucketSz = x;
		root = null;
	}

	// Pre: elem != null
	// Post: If elem lies within the tree's region, and elem is not already
	// present in the tree, elem has been inserted into the tree.
	// Return true iff elem is inserted into the tree.
	@SuppressWarnings("unchecked")
	public boolean insert(T elem) {
		if (!elem.inBox(xMin, xMax, yMin, yMax)) {
			return false;
		} else if (isEmpty()) {
			root = new prQuadLeaf(elem);
			return true;
		} else if (root.isLeaf()) {
			for (int k = 0; k < ((prQuadLeaf) root).getSz(); k++) {
				if (((prQuadLeaf) root).getElem(k).equals(elem)) {
					return false;
				}
			}
			if (((prQuadLeaf) root).getSz() < bucketSz) {
				((prQuadLeaf) root).addElem(elem);
				return true;
			} else {
				Vector<T> temp = new Vector<T>();
				for (int j = 0; j < ((prQuadLeaf) root).getSz(); j++) {
					temp.add(((prQuadLeaf) root).getElem(j));
				}
				root = new prQuadInternal();
				for (int i = 0; i < bucketSz; i++) {
					T temp1 = temp.get(i);
					Direction loc = temp1.directionFrom(xMax / 2, yMax / 2);
					if (((prQuadInternal) root).getDir(loc) == null) {
						prQuadLeaf toAdd = new prQuadLeaf(temp1);
						((prQuadInternal) root).setDir(loc, toAdd);
					} else {
						((prQuadLeaf) ((prQuadInternal) root).getDir(loc))
								.addElem(temp1);
					}
				}
				Direction loc = elem.directionFrom(xMax / 2, yMax / 2);
				((prQuadInternal) root).setDir(
						loc,
						recIns(elem, ((prQuadInternal) root).getDir(loc), loc,
								xMin, yMin, 1, (prQuadInternal) root));

			}
		} else {
			Direction loc = elem.directionFrom(xMax / 2, yMax / 2);
			((prQuadInternal) root).setDir(
					loc,
					recIns(elem, ((prQuadInternal) root).getDir(loc), loc,
							xMin, yMin, 1, (prQuadInternal) root));
		}
		return true;

	}
	
	private long power(long x, long y) {
		long total = 1;
		for (int i = 0; i < y; i++) {
			total = total * x;
		}
		return total;
	}

	/**
	 * Generates the bounds of the quadrant in question
	 * @param loc that quadrant we are concerned with
	 * @param x the min x value of the parent
	 * @param y the min y value of the parent
	 * @param level how many levels down is this?
	 * @return and array of the bounds and center coordinates
	 */
	private long[] getBounds(Direction loc, long x, long y, int level) {
		long xSize = (xMax / power(2, level));
		long ySize = (yMax / power(2, level));
		long[] answer = new long[6];
		if (loc.name().equals("NE")) {
			answer[0] = x + xSize;
			answer[1] = x + 2 * xSize;
			answer[2] = y + ySize;
			answer[3] = y + 2 * ySize;
			answer[4] = x + xSize + xSize / 2;
			answer[5] = y + ySize + ySize / 2;
		} else if (loc.name().equals("NW")) {
			answer[0] = x;
			answer[1] = x + xSize;
			answer[2] = y + ySize;
			answer[3] = y + 2 * ySize;
			answer[4] = x + xSize / 2;
			answer[5] = y + ySize + ySize / 2;
		} else if (loc.name().equals("SE")) {
			answer[0] = x + xSize;
			answer[1] = x + 2 * xSize;
			answer[2] = y;
			answer[3] = y + ySize;
			answer[4] = x + xSize + xSize / 2;
			answer[5] = y + ySize / 2;
		} else if (loc.name().equals("SW")) {
			answer[0] = x;
			answer[1] = x + xSize;
			answer[2] = y;
			answer[3] = y + ySize;
			answer[4] = x + xSize / 2;
			answer[5] = y + ySize / 2;
		}
		return answer;
	}

	@SuppressWarnings("unchecked")
	private prQuadNode recIns(T elem, prQuadNode x, Direction loc, long xStart,
			long yStart, int level, prQuadInternal parent) {
		if (x == null) {
			x = new prQuadLeaf(elem);
			parent.setDir(loc, x);
			return x;
		}
		long[] bounds = getBounds(loc, xStart, yStart, level);
		if (x.isLeaf()) {
			for (int k = 0; k < ((prQuadLeaf) x).getSz(); k++) {
				if (((prQuadLeaf) x).getElem(k).equals(elem)) {
					return x;
				}
			}
			if (((prQuadLeaf) x).getSz() < bucketSz) {
				((prQuadLeaf) x).addElem(elem);
				return x;
			} else {
				Vector<T> temp = new Vector<T>();
				for (int j = 0; j < ((prQuadLeaf) x).getSz(); j++) {
					temp.add(((prQuadLeaf) x).getElem(j));
				}
				x = new prQuadInternal();
				parent.setDir(loc, x);
				for (int i = 0; i < bucketSz; i++) {
					T tempa = temp.get(i);
					Direction locat = tempa.directionFrom(bounds[4], bounds[5]);
					if (((prQuadInternal) x).getDir(locat) == null) {
						prQuadLeaf toAdd = new prQuadLeaf(tempa);
						((prQuadInternal) x).setDir(locat, toAdd);
					} else {
						((prQuadLeaf) ((prQuadInternal) x).getDir(locat))
								.addElem(tempa);
					}

				}
				Direction locat = elem.directionFrom(bounds[4], bounds[5]);
				((prQuadInternal) x).setDir(
						locat,
						recIns(elem, ((prQuadInternal) x).getDir(locat), locat,
								bounds[0], bounds[2], 1 + level,
								(prQuadInternal) x));
				return x;

			}
		} else {
			Direction locat = elem.directionFrom(bounds[4], bounds[5]);
			((prQuadInternal) x)
					.setDir(locat,
							recIns(elem, ((prQuadInternal) x).getDir(locat),
									locat, bounds[0], bounds[2], 1 + level,
									(prQuadInternal) x));
			return x;
		}

	}

	private boolean isEmpty() {
		if (root == null) {
			return true;
		}
		return false;
	}

	// Pre: elem != null
	// Returns reference to an element x within the tree such that
	// elem.equals(x)is true, provided such a matching element occurs within
	// the tree; returns null otherwise.
	@SuppressWarnings("unchecked")
	public T find(T Elem) {
		if (isEmpty()) {
			return null;
		} else if (root.isLeaf()) {
			Vector<T> temp = ((prQuadLeaf) root).getVec();
			for (int i = 0; i < temp.size(); i++) {

				if (temp.get(i).getX() == Elem.getX()
						&& temp.get(i).getY() == Elem.getY()) {
					return temp.get(i);

				}
			}
		} else {
			Direction loc = Elem.directionFrom(xMax / 2, yMax / 2);
			return recFind(Elem, ((prQuadInternal) root).getDir(loc), loc,
					xMin, yMin, 1);
		}
		return null;
	}

	public void setBucketSz(int x) {
		bucketSz = x;
	}

	public int getBucketSz() {
		return bucketSz;
	}

	@SuppressWarnings("unchecked")
	private T recFind(T elem, prQuadNode x, Direction loc, long xStart,
			long yStart, int level) {
		if (x == null) {
			return null;
		}
		long[] bounds = getBounds(loc, xStart, yStart, level);
		if (x.isLeaf()) {
			Vector<T> temp = ((prQuadLeaf) x).getVec();
			for (int i = 0; i < temp.size(); i++) {

				if (temp.get(i).getX() == elem.getX()
						&& temp.get(i).getY() == elem.getY()) {
					return temp.get(i);
				}

			}
		} else {
			Direction locat = elem.directionFrom(bounds[4], bounds[5]);
			return recFind(elem, ((prQuadInternal) x).getDir(locat), locat,
					bounds[0], bounds[2], 1 + level);
		}
		return null;
	}

	// FIX
	// Pre: xLo, xHi, yLo and yHi define a rectangular region
	// Returns a collection of (references to) all elements x such that x is
	// in the tree and x lies at coordinates within the defined rectangular
	// region, including the boundary of the region.
	@SuppressWarnings("unchecked")
	public Vector<T> find(long xLo, long xHi, long yLo, long yHi) {
		Vector<T> answer = new Vector<T>();
		if (isEmpty()) {
			return answer;
		} else {
			if (root.isLeaf()) {
				return this.addValid(xLo, xHi, yLo, yHi, answer,
						(prQuadLeaf) root);
			} else {
				if ((xMax / 2 <= xHi && yMax / 2 <= yHi)
						&& ((prQuadInternal) root).NE != null) {
					answer = recFind(((prQuadInternal) root).NE, xLo, xHi, yLo,
							yHi, Direction.NE, xMax / 2, yMax / 2, 2, answer);
				}
				if ((xMax / 2 >= xLo && yMax / 2 <= yHi)
						&& ((prQuadInternal) root).NW != null) {
					answer = recFind(((prQuadInternal) root).NW, xLo, xHi, yLo,
							yHi, Direction.NW, xMin, yMax / 2, 2, answer);
				}
				if ((xMax / 2 >= xLo && yMax / 2 >= yLo)
						&& ((prQuadInternal) root).SW != null) {
					answer = recFind(((prQuadInternal) root).SW, xLo, xHi, yLo,
							yHi, Direction.SW, xMin, yMin, 2, answer);
				}
				if ((xMax / 2 <= xHi && yMax / 2 >= yLo)
						&& ((prQuadInternal) root).SE != null) {
					answer = recFind(((prQuadInternal) root).SE, xLo, xHi, yLo,
							yHi, Direction.SE, xMax / 2, yMin, 2, answer);
				}

			}
		}
		return answer;
	}
	/**
	 * helper function tht will only add apporpriate values from the bucketed leaf
	 * @param xLo bounds...
	 * @param xHi
	 * @param yLo
	 * @param yHi
	 * @param ans the current valid answers
	 * @param holder the leaf with potentially bucketSz elements that might be valid
	 * @return
	 */
	private Vector<T> addValid(long xLo, long xHi, long yLo, long yHi,
			Vector<T> ans, prQuadLeaf holder) {
		Vector<T> nAns = ans;
		Vector<T> poten = holder.Elements;
		for (int j = 0; j < poten.size(); j++) {
			T maybe = poten.get(j);
			if (maybe.inBox(xLo, xHi, yLo, yHi)) {
				nAns.add(maybe);
			}
		}
		return nAns;
	}

	@SuppressWarnings("unchecked")
	private Vector<T> recFind(prQuadNode x, long xLo, long xHi, long yLo,
			long yHi, Direction lo, long xStart, long yStart, int level,
			Vector<T> answer) {
		long[] bounds = getBounds(lo, xStart, yStart, level);
		if (x.isLeaf()) {
			return this.addValid(xLo, xHi, yLo, yHi, answer, (prQuadLeaf) x);
		} else {
			if ((bounds[0] / 2 <= xHi && bounds[2] / 2 <= yHi)
					&& ((prQuadInternal) x).NE != null) {

				answer = recFind(((prQuadInternal) x).NE, xLo, xHi, yLo, yHi,
						Direction.NE, bounds[4], bounds[5], level + 1, answer);
			}
			if ((bounds[1] >= xLo && bounds[2] / 2 <= yHi)
					&& ((prQuadInternal) x).NW != null) {

				answer = recFind(((prQuadInternal) x).NW, xLo, xHi, yLo, yHi,
						Direction.NW, bounds[0], bounds[5], level + 1, answer);
			}
			if ((bounds[1] >= xLo && bounds[3] >= yLo)
					&& ((prQuadInternal) x).SW != null) {

				answer = recFind(((prQuadInternal) x).SW, xLo, xHi, yLo, yHi,
						Direction.SW, bounds[0], bounds[2], level + 1, answer);
			}
			if ((bounds[0] / 2 <= xHi && bounds[3] >= yLo)
					&& ((prQuadInternal) x).SE != null) {

				answer = recFind(((prQuadInternal) x).SE, xLo, xHi, yLo, yHi,
						Direction.SE, bounds[4], bounds[2], level + 1, answer);
			}
		}

		return answer;
	}
}