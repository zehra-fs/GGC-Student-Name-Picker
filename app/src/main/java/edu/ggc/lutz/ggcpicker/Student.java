package edu.ggc.lutz.ggcpicker;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Random;

public class Student implements Comparable<Student>  {

    private static final String DELIM = ":";

    private String name;
    private boolean enabled;

    Student(String s) {
        this(s, true);
    }

    Student(String s, boolean enable) {
        this.name = s;
        this.enabled = enable;
    }

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public static Student random(ArrayList<Student> list) {
        boolean found = false;
        Student pick = null;
        while (list.size() > 0 && !found) {
            int index = new Random().nextInt(list.size());
            pick = list.get(index);
            if (pick.isEnabled())
                found = true;
        }
        return pick;
    }

    @Override
    public int compareTo(@NonNull Student o) {
        String s1 = getName().toLowerCase();
        String s2 = o.getName().toLowerCase();
        return s1.compareTo(s2);
    }

    public String toCSV() {
        return isEnabled() + DELIM + getName();
    }
    @Override
    public String toString() {
        return name;
    }

}
