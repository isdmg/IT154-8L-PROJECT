package edu.upc.fib.ossim.memory.model;

import java.util.Collections;
import java.util.Iterator;
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
    public static boolean compacted = false;
    public static boolean compactionDone = true;
    // contains gantt chart
    public static LinkedList<Integer> jList = new LinkedList<>();
    // contains gantt chart (next job)
    public static LinkedList<Integer> jIList = new LinkedList<>();
    public static int jobIndex = 1;

    public static Iterator<MemPartition> coalesce_it;
    public static List<MemPartition> coalesce_memory;
    public static int coalesce_end;
    public static int memory_size;


    // assumes that we already have size of job queue
    private static int jSize;

    public void initialize(int jSize) {
        this.jSize = jSize;
    }

    public static void reset() {
        allocated = false;
        coalesced = false;
        compacted = false;
        jList = new LinkedList<>();
        jIList = new LinkedList<>();
        coalesce_it = null;
        coalesce_memory = null;
        coalesce_end = 0;
        memory_size = 0;
        jobIndex = 1;
    }

    public int getJobSubtract(List<ProcessMemUnit> js) {

        int y = jobIndex;

        // get max pid
        int max = 0;
        for (int i = 0; i < js.size(); i++) {
            int x = js.get(i).getPid();
            if (js.get(i).getPid() > max) {
                max = js.get(i).getPid();
            }
        }

        // get min pid
        int min = jSize + 1;
        for (int i = 0; i < js.size(); i++) {
            int x = js.get(i).getPid();
            if (js.get(i).getPid() < min) {
                min = js.get(i).getPid();
            }
        }

        // if job index is out of bounds (right)
        if (max < jobIndex) {
            jobIndex = 0;
            min = max;
            for (int i = 0; i < js.size(); i++) {
                int x = js.get(i).getPid();
                if (js.get(i).getPid() < min) {
                    min = js.get(i).getPid();
                }
            }
            // get min if resetted to 0
            jobIndex = min;
        } else if (min > jobIndex) {
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
            for (int i = 0; i < js.size(); i++) {
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

                for (int i = 0; i < js.size(); i++) {
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

    public void compact() {
        if (coalesce_it.hasNext()) {
            MemPartition memProg = coalesce_it.next();
            System.out.println("MemProg = " + memProg.getSize() + " " + memProg.getAllocated().getPid());
            memProg.setStart(coalesce_end);
            coalesce_end += memProg.getSize();
            coalesce_memory.add(memProg);
            MemoryManagement.jList.add(-30);
            MemoryManagement.jIList.add(-30);
        }  else if (compactionDone) {
            MemoryManagement.compactionDone = false;
            MemoryManagement.compacted = false;
        } else {
            if (coalesce_end < memory_size) {
                // Create partition with all available memory
                MemPartition b = new MemPartition(coalesce_end, memory_size - coalesce_end);
                System.out.println("Start" + b.getStart());
                System.out.println("End:" + (b.getStart() + b.getSize() - 1));
                coalesce_memory.add(b);
                MemoryManagement.compactionDone = true;
                MemoryManagement.jList.add(-30);
                MemoryManagement.jIList.add(-30);
            }
        }
     }
}

