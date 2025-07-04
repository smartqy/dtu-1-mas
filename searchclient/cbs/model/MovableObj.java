package searchclient.cbs.model;

import searchclient.Color;

public class MovableObj{

    protected ObjectType objType;
    protected String uniqueId;
    protected Color color;
    protected Location initLocation;
    protected Location currentLocation;

    public MovableObj(ObjectType objType, String uniqueId, Color color) {
        this.objType = objType;
        this.uniqueId = uniqueId;
        this.color = color;
    }

    public boolean isAgent() {
        return objType == ObjectType.AGENT;
    }

    public boolean isBox() {
        return objType == ObjectType.BOX;
    }

    public Location getInitLocation() {
        return initLocation;
    }

    public void setInitLocation(Location initLocation) {
        this.initLocation = initLocation;
    }

    public Color getColor() {
        return color;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

}
