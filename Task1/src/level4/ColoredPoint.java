package src.level4;

public class ColoredPoint extends Point {
    private final String color;

    public ColoredPoint(int x, int y, String color) {
        super(x, y);
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ColoredPoint that = (ColoredPoint) o;
        return color.equals(that.color);
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + color.hashCode();
    }
}