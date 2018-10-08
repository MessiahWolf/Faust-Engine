/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.coll;

import java.awt.Point;
import java.awt.Polygon;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author rcher
 */
public class CollisionWrapper {

    // Variable Declaration
    // Java Native Classes
    private final Object[][] data;

    // Imported Project Classes
    // Project Classes
    // End of Variable Declaration
    public CollisionWrapper(int length) {

        // All we need.
        data = new Object[length][5];

        //
        for (int i = 0; i < length; i++) {
            data[i][4] = 0;
        }
    }

    public CollisionWrapper(Object[][] input) {

        //
        data = new Object[input.length][5];

        //
        for (int i = 0; i < input.length; i++) {

            //
            System.arraycopy(input[i], 0, data[i], 0, input[i].length);
        }
    }

    public void clearPointForIndex(int index) {
        data[index][1] = null;
    }

    public void clearDataForIndex(int index) {
        data[index][0] = null;
        data[index][1] = null;
        data[index][2] = null;
        data[index][3] = null;
        data[index][4] = null;
    }

    public boolean hasDataForIndex(int index) {
        int fail = 0;
        // No Polygons.
        fail = data[index][0] == null ? fail + 1 : fail;
        // No Points
        fail = data[index][1] == null ? fail + 1 : fail;
        // No Regions
        fail = data[index][2] == null ? fail + 1 : fail;
        // No Multipliers
        fail = data[index][3] == null ? fail + 1 : fail;
        // No Percisions
        fail = data[index][4] == null ? fail + 1 : fail;
        return fail == 0;
    }

    public Object[][] getData() {
        return data;
    }

    public String[] getRegionsForIndex(int index) {
        return (String[]) data[index][2];
    }

    public double[] getMultipliersForIndex(int index) {
        return (double[]) data[index][3];
    }

    public HashMap<Point, Integer> getPointsForIndex(int index) {
        return (HashMap<Point, Integer>) data[index][1];
    }

    public Polygon[] getPolygonsForIndex(int index) {
        return (Polygon[]) data[index][0];
    }

    public int getPrecisionForIndex(int index) {
        return (int) data[index][4];
    }

    public void setDataForIndex(Polygon[] polygons, HashMap<Point, Integer> map, String[] regions, double[] damageMults, int precision, int index) {

        // You cannot pass a map by reference, all the data will be lost.
        HashMap<Point, Integer> r = new HashMap(map.size());
        r.putAll(map);

        //
        data[index][0] = Arrays.copyOf(polygons, polygons.length);
        data[index][1] = r;
        data[index][2] = Arrays.copyOf(regions, regions.length);
        data[index][3] = Arrays.copyOf(damageMults, damageMults.length);
        data[index][4] = precision;

        // @TODO ADD PRECISION FOR EACH INDEX.
    }

    public void setPointForIndex(HashMap<Point, Integer> map, int index) {
        HashMap<Point, Integer> r = new HashMap(map.size());
        r.putAll(map);
        data[index][1] = r;
    }

    public void setPrecisionForIndex(int prec, int index) {
        data[index][4] = prec;
    }

    public void print() {
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                // System.out.println("Data[" + i + "][" + j + "]: " + data[i][j]);
            }
        }
    }
}
