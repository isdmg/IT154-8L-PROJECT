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

    // function to sort hashmap by values
    private static HashMap<Integer, Integer> sortByValue(HashMap<Integer, Integer> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<Integer, Integer>> list =
                new LinkedList<Map.Entry<Integer, Integer>>(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
            public int compare(Map.Entry<Integer, Integer> o1,
                               Map.Entry<Integer, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<Integer, Integer> temp = new LinkedHashMap<Integer, Integer>();
        for (Map.Entry<Integer, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    /**
     * Compacts and merge free memory partitions
     *
     * @param memory      partitions linked list (memory)
     * @param memory_size memory size
     */
    public void compaction(List<MemPartition> memory, int memory_size) {
        if (!MemoryManagement.compacted) {
            MemoryManagement.compactionDone = false;
            List<MemPartition> progsAllocated = new LinkedList<MemPartition>();
            HashMap<Integer, Integer> progSize = new HashMap<>();
            List<MemPartition> progsAllocatedSorted = new LinkedList<MemPartition>();
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

            for (int z = 1; z < progsAllocated.size(); z++) {
                progSize.put(z, progsAllocated.get(z).getAllocated().getSize());
            }

            ArrayList<Integer> size = new ArrayList<>(progSize.values());
            Collections.sort(size);
            Collections.reverse(size);

            progSize = sortByValue(progSize);

            ArrayList<Integer> progSizeSorted = new ArrayList<>(progSize.keySet());

            for (int z = 0; z < progSizeSorted.size(); z++) {
                if (z == 0) {
                    progsAllocatedSorted.add(progsAllocated.get(0));
                }
                progsAllocatedSorted.add(progsAllocated.get(progSizeSorted.get(z)));
            }

            if (progsAllocatedSorted.size() != 0) {
                Iterator<MemPartition> it = progsAllocatedSorted.iterator();
                MemPartition memProg = it.next();
                memProg.setStart(end);
                end += memProg.getSize();
                memory.add(memProg);

                MemoryManagement.coalesce_it = it;
                MemoryManagement.coalesce_memory = memory;
                MemoryManagement.coalesce_end = end;
                MemoryManagement.memory_size = memory_size;
                MemoryManagement.getInstance().compact();
                MemoryManagement.compacted = true;

            } else {
                Iterator<MemPartition> it = progsAllocated.iterator();
                MemPartition memProg = it.next();
                memProg.setStart(end);
                end += memProg.getSize();
                memory.add(memProg);
                if (end < memory_size) {
                    // Create partition with all available memory
                    MemPartition b = new MemPartition(end, memory_size - end);
                    System.out.println("Start" + b.getStart());
                    System.out.println("End:" + (b.getStart() + b.getSize() - 1));
                    memory.add(b);
                    MemoryManagement.compactionDone = true;
                    MemoryManagement.jList.add(-30);
                    MemoryManagement.jIList.add(-30);
                }
            }
        } else {
            MemoryManagement.getInstance().compact();
        }
    }

    public void coalesce(List<MemPartition> memory, int memory_size) {
        List<MemPartition> progsAllocated = new LinkedList<MemPartition>();
        List<MemPartition> holes = new LinkedList<MemPartition>();
        List<MemPartition> adjHoles = new LinkedList<MemPartition>();
        List<MemPartition> partAdjHoles = new LinkedList<MemPartition>();
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
        for (int j = 0; j <= holes.size() - 2; j++) {
            System.out.print(holes.get(j).getStart() + " " + (holes.get(j).getStart() + holes.get(j).getSize()) + "\n");
            MemPartition currentHole = holes.get(j);
            MemPartition nextHole = holes.get(j + 1);
            if ((currentHole.getStart() + currentHole.getSize()) == nextHole.getStart()) {
                if (!adjHoles.contains(currentHole)) {
                    adjHoles.add(currentHole);
                }
                adjHoles.add(nextHole);
            }
        }
        // TODO: Will this work for multiple adjacent holes?

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

            for (int z = 0; z < adjHoles.size() - 1; z++) {
                boolean existNonContiguous = false;
                MemPartition currentHole = adjHoles.get(z);
                MemPartition nextHole = adjHoles.get(z + 1);

                if ((currentHole.getStart() + currentHole.getSize()) == nextHole.getStart()) {
                    if (!partAdjHoles.contains(currentHole)) {
                        partAdjHoles.add(currentHole);
                    }
                    partAdjHoles.add(nextHole);
                } else {
                    existNonContiguous = true;
                }

                if (z == adjHoles.size() - 2 || existNonContiguous) {
                    memory.removeAll(partAdjHoles);
                    firstHole = partAdjHoles.get(0);
                    lastHole = partAdjHoles.get(partAdjHoles.size() - 1);
                    int spaceDiff = (lastHole.getStart() + lastHole.getSize()) - firstHole.getStart();

                    System.out.println(firstHole.getStart());
                    System.out.println(lastHole.getStart());
                    System.out.println("==========>");
                    System.out.println(firstHole.getStart() + " " + (lastHole.getStart() + lastHole.getSize() - 1));

                    MemPartition b = new MemPartition(firstHole.getStart(), spaceDiff);
                    System.out.println("Start" + b.getStart());
                    System.out.println("End:" + (b.getStart() + b.getSize() - 1));
                    memory.add(b);
                    MemoryManagement.coalesced = true;
                    MemoryManagement.jList.add(-20);
                    MemoryManagement.jIList.add(-20);
                    partAdjHoles.clear();
                    break;
                }
            }
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
