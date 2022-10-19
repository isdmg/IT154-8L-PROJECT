package edu.upc.fib.ossim.memory.model;

import java.util.*;

/**
 * Memory Management Strategy implementation for Variable-size partitions algorithm
 *
 * @author Alex Macia
 */
public class MemStrategyVAR extends MemStrategyAdapterCONT {

    public MemStrategyVAR(String policy) {
        super(policy);
    }

    private static boolean tProcessAllocation = false;

    /**
     * Gets Variable-size partitions algorithm information including allocation policy
     *
     * @return algorithm information
     */
    public String getAlgorithmInfo() {
        return super.getAlgorithmInfo("me_23");
    }

    /**
     * Returns initial algorithm partition size. All memory.
     *
     * @param OSsize      operating system size
     * @param memory_size memory size
     * @return initial algorithm partition size. All memory.
     */
    public int getInitPartitionSize(int OSsize, int memory_size) {
        return memory_size;
    }

    /**
     * No validation is needed in this strategy
     */
    public void validateMemory(List<MemPartition> memory, int memory_size) {
    }

    /**
     * Compacts and merge free memory partitions
     *
     * @param memory      partitions linked list (memory)
     * @param memory_size memory size
     */
    public void compaction(List<MemPartition> memory, int memory_size) {
        List<MemPartition> progsAllocated = new LinkedList<MemPartition>();
        Object[] memOrdered = memory.toArray();
        Arrays.sort(memOrdered);
        int i = 0;
        while (i < memOrdered.length) {
            MemPartition partition = (MemPartition) memOrdered[i];
            if (partition.getAllocated() != null) {
                progsAllocated.add(partition);
            }
            i++;
        }

        int end = 0;
        memory.clear(); // Empty memory
        Iterator<MemPartition> it = progsAllocated.iterator();
        while (it.hasNext()) {
            MemPartition memProg = it.next();
            memProg.setStart(end);
            end += memProg.getSize();
            memory.add(memProg);
        }

        if (end < memory_size) {
            // Create partition with all available memory
            MemPartition b = new MemPartition(end, memory_size - end);
            System.out.println("Start" + b.getStart());
            System.out.println("End:" + (b.getStart() + b.getSize() - 1));
            memory.add(b);
        }
    }

    // TODO: 18-10-22 Check if adjacent using end of hole + start of another hole
    // TODO: Check MemPartition (if u can edit hole color (indicate size) + process as well
    public void coalesce(List<MemPartition> memory, int memory_size) {
        List<MemPartition> progsAllocated = new LinkedList<MemPartition>();
        List<MemPartition> holes = new LinkedList<MemPartition>();
        List<MemPartition> adjHoles = new LinkedList<MemPartition>();
        Object[] memOrdered = memory.toArray();
        Arrays.sort(memOrdered);
        int i = 0;
        while (i < memOrdered.length) {
            MemPartition partition = (MemPartition) memOrdered[i];
            if (partition.getAllocated() != null) {
                progsAllocated.add(partition);
            } else {
                holes.add(partition);
            }
            i++;
        }


        int end = -1;
        for (int j = 0; j < holes.size() - 2; j++) {
            System.out.print(holes.get(j).getStart() + " " + (holes.get(j).getStart() + holes.get(j).getSize()) + "\n");
            MemPartition currentHole = holes.get(j);
            MemPartition nextHole = holes.get(j+1);
            if ((currentHole.getStart() + currentHole.getSize()) == nextHole.getStart()) {
                adjHoles.add(currentHole);
                adjHoles.add(nextHole);
            }
        }

//        for (MemPartition hole : holes) {
//            if (end == hole.getStart()) {
//                adjHoles.add(hole);
//            }
//            end = (hole.getStart() + hole.getSize());
//        }

        if (adjHoles.size() != 0) {
            System.out.println("Adjacent Holes ===>");
            MemPartition firstHole = adjHoles.get(0);
            MemPartition lastHole = adjHoles.get(adjHoles.size() - 1);

            System.out.println(firstHole.getStart());
            System.out.println(lastHole.getStart());
            System.out.println("==========>");
            System.out.println(firstHole.getStart() + " " + (lastHole.getStart() + lastHole.getSize() - 1));

            memory.removeAll(adjHoles);
            int spaceDiff = (lastHole.getStart() + lastHole.getSize()) - firstHole.getStart();

            MemPartition b = new MemPartition(firstHole.getStart(), spaceDiff);
            System.out.println("Start" + b.getStart());
            System.out.println("End:" + (b.getStart() + b.getSize() - 1));
            memory.add(b);
            MemoryManagement.coalesced = true;
        }
    }



    /**
     * Allocates allocate process into candidate partition, if allocate size < candidate size, divides partition
     *
     * @param memory    partitions linked list (memory)
     * @param candidate candidate partition
     * @param allocate  process to allocate
     */
    // NOTE: Eto yung para malaman ano yung inallocate
    public void allocateCandidate(List<MemPartition> memory, MemPartition candidate, ProcessMemUnit allocate) {
        memory.remove(candidate);
        int size = candidate.getSize();
        candidate.setSize(allocate.getSize());
        candidate.setAllocated(allocate);
        memory.add(candidate);
        if (size > candidate.getSize()) {
            // Create empty partition
            MemPartition b = new MemPartition(candidate.getStart() + candidate.getSize(), size - candidate.getSize());
            memory.add(b);
            if (candidate.getAllocated() != null) {
                ProcessComplete p = candidate.getAllocated().getParent();

                MemoryManagement.allocated = true;
                System.out.println("Allocated Job#" + p.getPid());

                p.setDuration(p.getDuration() - 1);
                if (p.getPid() != 0) {
                    MemoryManagement.jList.add(p.getPid());
                    // Note: This is important for the program to work properly
                    MemoryManagement.getInstance().shiftJobIndex(p.getPid());
                }
            }
        }
    }

    /**
     * No initial memory information needed
     *
     * @return null
     */
    public Vector<Vector<Vector<String>>> getXMLDataMemory(List<MemPartition> memory) {
        return null;
    }

    public static boolean getTProcessAllocation() {
        return tProcessAllocation;
    }

    public static void setTProcessAllocation(boolean TProcessAllocation) {
        MemStrategyVAR.tProcessAllocation = TProcessAllocation;
    }
}
