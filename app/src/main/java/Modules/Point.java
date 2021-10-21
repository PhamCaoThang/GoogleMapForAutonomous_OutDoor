package Modules;

public class Point {

    private double Lat_X;
    private double Lang_Y;
    private int bearing;


    public Point(double lat_X, double lang_Y) {
        Lat_X = lat_X;
        Lang_Y = lang_Y;
    }

    public double getLat_X() {
        return Lat_X;
    }

    public void setLat_X(double lat_X) {
        Lat_X = lat_X;
    }

    public double getLang_Y() {
        return Lang_Y;
    }

    public void setLang_Y(double lang_Y) {
        Lang_Y = lang_Y;
    }

    @Override
    public String toString() {
        return Lat_X + "," + Lang_Y;
    }
    public int getBearing() {
        return bearing;
    }

    public void setBearing(int bearing) {
        this.bearing = bearing;
    }
}
