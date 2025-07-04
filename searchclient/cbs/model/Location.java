package searchclient.cbs.model;

/**
 * Base Model
 * Location representation
 */
public class Location {
    private int row;
    private int col;

    public Location() {
    }

    public Location deepCopy() {
        return new Location(this.row, this.col);
    }

    public Location(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void update(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public String toString() {
        return "Location{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;
        if (row != location.row) return false;
        return col == location.col;
    }

    @Override
    public int hashCode() {
        return 31 * row + col;
    }
}
