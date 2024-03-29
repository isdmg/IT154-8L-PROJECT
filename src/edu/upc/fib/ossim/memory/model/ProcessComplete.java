package edu.upc.fib.ossim.memory.model;

import edu.upc.fib.ossim.utils.ColorCell;
import edu.upc.fib.ossim.utils.SoSimException;
import edu.upc.fib.ossim.utils.StringToMap;

import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * Process definition (Memory management context), instance are cloneable to
 * clone them. In non contiguous memory management algorithms processes are
 * divided into smaller pieces (components) such as pages or segments, so every
 * process consist of a list of components, otherwise processes are treated as
 * indivisible memory objects. (Composite Pattern)
 * 
 * @author Alex Macia
 * 
 * @see ProcessMemUnit
 * @see ProcessComponent
 */
public class ProcessComplete implements ProcessMemUnit, Cloneable {
	private static int maxpid = 1;
	private int pid;
	private String name;
	private int size;
	private int duration; // -1 infinite
	private Color color;
	private List<ProcessComponent> blocks;

	int key;
	private Integer quantum;
	private String quantumOrders;

	/**
	 * Constructs a process
	 * 
	 * @param pid
	 *            process identifier
	 * @param name
	 *            process name
	 * @param size
	 *            process size
	 * @param duration
	 *            process duration
	 * @param color
	 *            process color
	 */
	public ProcessComplete(int pid, String name, int size, int duration,
			Color color) {
		this.pid = pid;
		this.name = name;
		this.size = size;
		this.duration = duration;
		this.color = color;
		this.blocks = new LinkedList<ProcessComponent>();
		if (pid == 0)
			maxpid = 1; // Restart pid
		else
			maxpid++;
		this.quantum = 3;
		this.key = 0;
		this.quantumOrders = "";

	}

	/**
	 * Gets itself. (Composite pattern)
	 * 
	 * @return itself
	 * 
	 * @see ProcessMemUnit#getParent()
	 */
	public ProcessComplete getParent() {
		return this;
	}

	/**
	 * Gets process identifier
	 * 
	 * @return process identifier
	 */
	public int getPid() {
		return pid;
	}

	/**
	 * Gets process name
	 * 
	 * @return process name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets process size
	 * 
	 * @return process size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Gets process duration
	 * 
	 * @return process duration
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * Sets process duration
	 * 
	 * @param duration
	 *            process duration
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}

	/**
	 * Gets process color
	 * 
	 * @return process color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Gets process quantum
	 * 
	 * @return process quantum
	 */
	public int getQuantum() {
		return quantum;
	}
	/**
	 * Sets process quantum
	 * 
	 * @param quantum
	 *            process quantum
	 */
	public void setQuantum(int quantum) {
		this.quantum = quantum;
	}

	/**
	 * Gets process quantum id
	 * 
	 * @return process quantum id
	 */
	public int getUpdatedKey() {
		if (key < quantum) {
			key++;
		}
		return key - 1;
	}

	/**
	 * Gets the state of loading process
	 * 
	 * @return if all the quantums of a process have been loaded
	 */
	public boolean isDone() {
		return key == quantum;
	}

	/**
	 * Gets unique process identifier
	 * 
	 * @return unique process identifier
	 */
	public static int getMaxpid() {
		return maxpid;
	}

	/**
	 * Adds a process component: a page or a segment for example.
	 * 
	 * @param block
	 *            process component to add
	 */
	public void addBlock(ProcessComponent block) {
		blocks.add(block);
	}

	public void initBlocks() {
		blocks = new LinkedList<ProcessComponent>();
	}

	/**
	 * Gets total number of component
	 * 
	 * @return total number of component
	 */
	public int getNumBlocks() {
		return blocks.size();
	}

	/**
	 * Transforms the page orders into component blocks,
	 * key represents the quantum id
	 * value represents the pages used of each quantum 
	 * 
	 * @return new form of blocks
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, List<ProcessComponent>> getQuantumBlocks()
			throws SoSimException {
		Map<Integer, List<ProcessComponent>> res = new HashMap<Integer, List<ProcessComponent>>();
		StringToMap stm = new StringToMap(quantumOrders);
		Map<Integer, List<Integer>> pagesOrder = stm.transformToMap();
		Set mappings = pagesOrder.entrySet();
		for (Iterator i = mappings.iterator(); i.hasNext();) {
			List<ProcessComponent> quantumBlocks = new LinkedList<ProcessComponent>();
			List<Integer> quantumIDs = new ArrayList<Integer>();
			Map.Entry me = (Map.Entry) i.next();
			Integer key = (Integer) me.getKey();
			Object value = me.getValue();
			quantumIDs = (ArrayList<Integer>) value;
			for (int id : quantumIDs) {
				quantumBlocks.add(blocks.get(id));
			}
			res.put(key, quantumBlocks);
		}
		return res;
	}

	/**
	 * Gets the pages orders
	 * 
	 * @return the pageOrders
	 */
	public void setQuantumOrders(String quantumOrders) {
		this.quantumOrders = quantumOrders;
	}

	public String getQuantumOrders() {
		return this.quantumOrders;
	}

	public String initQuantumOrders() {
		return "";
	}

	/**
	 * Gets component at position i of components list
	 * 
	 * @param i
	 *            component's position
	 * @return component at position i of components list
	 */
	public ProcessComponent getBlock(int i) {
		return blocks.get(i);
	}

	/**
	 * Returns process information table row, cells are ColorCell instances, pid
	 * cell background color is process color, other cell are painted in white
	 * 
	 * @return process information table data row
	 * 
	 * @see ColorCell
	 */
	public Vector<Object> getInfo() {
		// Process information table data
		Vector<Object> info = new Vector<Object>();
		info.add(new ColorCell(new Integer(pid).toString(), color));
		info.add(new ColorCell(name, Color.WHITE));
		info.add(new ColorCell(new Integer(size).toString(), Color.WHITE));
		if (duration == -1)
			info.add(new ColorCell("\u221e", Color.WHITE));
		else
			info.add(new ColorCell(new Integer(duration).toString(),
					Color.WHITE));
		return info;
	}

	/**
	 * Returns process xml information, pairs attribute name - attribute value,
	 * includes also components xml information
	 * 
	 * @return process xml information
	 */
	public Vector<Vector<String>> getXMLInfo() {
		// MemBlock xml information
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		Vector<String> attribute;

		attribute = new Vector<String>();
		attribute.add("pid");
		attribute.add(Integer.toString(pid));
		data.add(attribute);
		attribute = new Vector<String>();
		attribute.add("name");
		attribute.add(name);
		data.add(attribute);
		attribute = new Vector<String>();
		attribute.add("size");
		attribute.add(Integer.toString(size));
		data.add(attribute);
		attribute = new Vector<String>();
		attribute.add("duration");
		attribute.add(Integer.toString(duration));
		data.add(attribute);
		attribute = new Vector<String>();
		attribute.add("color");
		attribute.add(Integer.toString(color.getRGB()));
		data.add(attribute);
		attribute = new Vector<String>();
		attribute.add("quantumOrders");
		attribute.add(quantumOrders);
		data.add(attribute);
		attribute = new Vector<String>();
		attribute.add("quantum");
		attribute.add(Integer.toString(quantum));
		data.add(attribute);
		// Components information if exists
		Iterator<ProcessComponent> it = blocks.iterator();
		while (it.hasNext()) {
			data.addAll(it.next().getXMLInfo());
		}

		return data;
	}

	/**
	 * Clones this process
	 * 
	 * @return cloned process
	 */
	public ProcessComplete clone() {
		ProcessComplete clone = null;
		try {
			clone = (ProcessComplete) super.clone();

			clone.initBlocks();
			clone.initQuantumOrders();

			// Must clone all blocks one by one
			Iterator<ProcessComponent> it = blocks.iterator();
			while (it.hasNext()) {
				ProcessComponent clonedBlock = it.next().clone();
				clonedBlock.setParent(clone);
				clone.addBlock(clonedBlock);
			}
			clone.setQuantumOrders(quantumOrders);

		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return clone;
	}

}