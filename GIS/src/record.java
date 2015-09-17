
public class record {

	double x1;
	double y1;
	String name;
	public record(double x, double y, String nam) {
		x1=x;
		y1 =y;
		name = nam;
	}
	public record() {

	}
	public double getX() {
		return x1;
	}
	public double getY() {
		return y1;
	}
	public String toString(){
		return name;
	}
	public boolean equals(Object o) {
		if (o.getClass() == this.getClass()) {
			if (x1 == ((record) o).getX() && y1 == ((record) o).getY()) {
				return true;
			}
		}
		return false;
	}

}
