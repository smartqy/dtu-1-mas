package searchclient.cbs.model;

import searchclient.Color;

import java.io.Serializable;
import java.util.Objects;

public class Box extends MovableObj {

    private final char boxTypeLetter;

    private Location goalLocation;

    public Box(char boxTypeLetter, Color color, Location initLocation) {
        super(ObjectType.BOX, boxTypeLetter + initLocation.toString(), color);
        this.initLocation = initLocation;
        this.boxTypeLetter = boxTypeLetter;
    }

    @Override
    public String toString() {
        return "Box{uniqueId=" + this.uniqueId + ", objType=" + this.objType + ", color=" + this.getColor() + ", initLocation=" + this.getInitLocation() + ", goalLocation=" + this.getGoalLocation() + ", currentLocation=" + this.getCurrentLocation() + '}';
    }

    public boolean originEqual(Box other) {
        if (other == null) return false;

        return this.getUniqueId().equals(other.getUniqueId())
                && this.objType == other.objType
                && this.getColor().equals(other.getColor())
                && this.getInitLocation().equals(other.getInitLocation());
    }

    public Box deepCopy() {
        Box box = new Box(this.boxTypeLetter, this.getColor(), this.getInitLocation().deepCopy());
        box.setCurrentLocation(this.getCurrentLocation().deepCopy());
        Location goalLocation = this.getGoalLocation() != null ? this.getGoalLocation().deepCopy() : null;
        box.setGoalLocation(goalLocation);
        return box;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Box box = (Box) obj;
        return (Objects.equals(uniqueId, box.uniqueId)
                && initLocation.equals(box.initLocation))
                && currentLocation.equals(box.currentLocation);
    }

    @Override
    public int hashCode() {
        int result = uniqueId.hashCode();
        result = 31 * result + initLocation.hashCode();
        result = 31 * result + currentLocation.hashCode();
        return result;
    }

    public void setGoalLocation(Location goalLocation) {
        this.goalLocation = goalLocation;
    }

    public Location getGoalLocation() {
        return goalLocation;
    }

    public char getBoxTypeLetter() {
        return boxTypeLetter;
    }
}
