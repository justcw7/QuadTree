import java.util.Vector;
/**
 * class that the tree stores, contains two long variables for coordinate and a vector of offsets 
 * that with those coordinates, implements methods from interface DCompare, has setters and getters 
 * @author Chase
 *
 */
public class GeoCoord implements DCompare2D<GeoCoord> {

	private Vector<Long> offsets;
	private long x;
	private long y;

	public GeoCoord() {
		offsets = new Vector<Long>();
	}

	public GeoCoord(long x1, long y1, long offset) {
		x = x1;
		y = y1;
		offsets = new Vector<Long>();
		offsets.add(offset);
	}

	public long getX() {
		return x;
	}

	public long getY() {
		return y;
	}

	public Direction directionFrom(long X, long Y) {
		if (X == x && Y == y) {
			return Direction.NOQUADRANT;
		}
		boolean posX = false;
		boolean posY = false;
		if (x >= X) {
			posX = true;
		}
		if (y >= Y) {
			posY = true;
		}
		if (posY && posX) {
			return Direction.NE;
		}
		if (posY && !posX) {
			return Direction.NW;
		}
		if (posX && !posY) {
			return Direction.SE;
		}
		if (!posX && !posY) {
			return Direction.SW;
		}
		return null;
	}

	public Direction inQuadrant(long xLo, long xHi, long yLo, long yHi) {
		return null;
	}

	public boolean inBox(long xLo, long xHi, long yLo, long yHi) {
		if ((xLo < this.getX() && this.getX() < xHi)
				&& (yLo < this.getY() && this.getY() < yHi)) {
			return true;
		}
		return false;
	}

	public String toString() {
		StringBuilder temp = new StringBuilder();
		temp.append("X:" + x + " Y:" + y);
		if (offsets.size() > 1) {
			temp.append(" " + offsets.size() + " time(s).");
		}
		return temp.toString();
	}
	/**
	 * this overridden equals method will add an offset to the vector if it's coordinates are equal
	 * to this object's coordinates, This means that it is not the tree's responsibility to deal
	 * with offsets and can work with both cases.
	 * 
	 */
	public boolean equals(Object o) {
		if (o.getClass() == this.getClass()) {
			GeoCoord obj = ((GeoCoord) o);
			Long temp = obj.getOffset().get(0);
			if (obj.getX() == this.x && obj.getY() == this.y) {
				for (int i = 0; i < offsets.size(); i++) {
					if (temp.longValue() == offsets.get(i).longValue()) {
						return true;
					}
				}
				offsets.add(temp);
				return true;
			}

		}
		return false;
	}

	public int getSz() {
		return offsets.size();
	}

	public Vector<Long> getOffset() {
		return offsets;
	}
}
