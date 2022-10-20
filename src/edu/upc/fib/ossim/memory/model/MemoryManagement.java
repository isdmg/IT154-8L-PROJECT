package edu.upc.fib.ossim.memory.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MemoryManagement {

    // Singleton
    private static MemoryManagement memMan;
    private MemoryManagement() {
    }
    public static MemoryManagement getInstance() {
        if (memMan == null) {
            memMan = new MemoryManagement();
        }
        return memMan;
    }

    // contains allocated status
    public static boolean allocated = false;
    public static boolean coalesced = false;
    // contains gantt chart
    public static LinkedList<Integer> jList = new LinkedList<>();
    // contains gantt chart (next job)
    public static LinkedList<Integer> jIList = new LinkedList<>();
    public static int jobIndex = 1;

    // assumes that we already have size of job queue
    private static int jSize;

    public void initialize(int jSize) {
        this.jSize = jSize;
    }

    public static void reset() {
        allocated = false;
        coalesced = false;
        jList = new LinkedList<>();
        jIList = new LinkedList<>();
        jobIndex = 1;
    }

    public int getJobSubtract(List<ProcessMemUnit> js) {

        int y = jobIndex;

        // get max pid
        int max = 0;
        for (int i =0; i < js.size(); i++) {
            int x = js.get(i).getPid();
            if(js.get(i).getPid() > max) {
                max = js.get(i).getPid();
            }
        }

        // get min pid
        int min = jSize + 1;
        for (int i =0; i < js.size(); i++) {
            int x = js.get(i).getPid();
            if(js.get(i).getPid() < min) {
                min = js.get(i).getPid();
            }
        }

        // if job index is out of bounds (right)
        if (max < jobIndex) {
            jobIndex = 0;
            min = max;
            for(int i = 0; i < js.size(); i++) {
                int x = js.get(i).getPid();
                if(js.get(i).getPid() < min) {
                    min = js.get(i).getPid();
                }
            }
            // get min if resetted to 0
            jobIndex = min;
        } else if (min > jobIndex){
            // if job index is out of bounds (left)
            jobIndex = min;
        }

        // brute force
        for (int i = 0; i < js.size(); i++) {
            int x = js.get(i).getPid();
            if (js.get(i).getPid() != jobIndex) {
                continue;
            } else {
                shiftJobIndex();
                return i;
            }
        }

        // last resort
        LinkedList<Integer> sortedJs = new LinkedList<>();
        int z = 0;
        if (js.size() != 0) {
            for(int i = 0; i < js.size(); i++) {
                sortedJs.add(js.get(i).getPid());
            }
            Collections.sort(sortedJs);
            if (!sortedJs.contains(jobIndex)) {
//                for(int i = 0; i < js.size(); i++) {
//                    for (int j = 0; j < jSize; j++) {
//                        if (sortedJs.get(i) != jobIndex) {
//                            shiftJobIndex();
//                        } else {
//                            return i;
//                        }
//                    }
//                }

                for(int i = 0; i < js.size(); i++) {
                    shiftJobIndex();
                    if (sortedJs.contains(jobIndex)) {
                        int returnIndex = jobIndex;
                        shiftJobIndex();
                        return sortedJs.indexOf(returnIndex);
                    }
//                    for (int j = 0; j < jSize; j++) {
//                        if (sortedJs.get(i) != jobIndex) {
//                            shiftJobIndex();
//                        } else {
//                            return i;
//                        }
//                    }
                }
            }
        }
        return -1;
    }

    public void shiftJobIndex() {
        // if out of range, go back to 1
        if (jobIndex > jSize) {
            jobIndex = 1;
        } else {
            jobIndex++;
        }
        MemoryManagement.jIList.add(MemoryManagement.jobIndex);
    }

    public void shiftJobIndex(int pid) {
        // if out of range, go back to 1
        if (pid < jobIndex) {
            jobIndex++;
        } else if (pid == jobIndex) {
            jobIndex++;
            if (jobIndex > jSize) {
                jobIndex = 1;
            }
        }
        MemoryManagement.jIList.add(MemoryManagement.jobIndex);
    }
}
