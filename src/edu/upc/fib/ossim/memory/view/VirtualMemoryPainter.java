package edu.upc.fib.ossim.memory.view;

import edu.upc.fib.ossim.memory.MemoryPresenter;
import edu.upc.fib.ossim.template.Presenter;
import edu.upc.fib.ossim.template.view.PainterTemplate;
import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.Translation;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Vector;


/**
 * Paints Main Memory. Processes are load into memory or released from it, main memory painter 
 * shows memory occupation, internal and external fragmentation at a concrete simulation time .
 * A pop up menu allows remove or swap out processes and address translation  
 * 
 * @author Alex Macia
 */
public class VirtualMemoryPainter extends PainterTemplate {
	private static final long serialVersionUID = 1L;
	private static final int M_UNITHEIGTH = 18;
	private static final int ADDR_WIDTH = 30;
	private static final int BORDER = 20;
	public static final Color FRAG_E = Color.pink;
	public static final Color FRAG_I = Color.blue;
	private static final Color EMPTY = Color.white;
	
	/**
	 * Constructs a VirtualMemoryPainter, creates the pop up menu and and initialize memory.  
	 * 
	 * @param presenter	event manager
	 * @param menuItems	pop up menu items
	 * @param width		canvas width
	 * @param height	canvas height
	 * 
	 */
	public VirtualMemoryPainter(Presenter presenter, Vector<String[]> menuItems, int width, int height) {
		super(presenter, menuItems, width, height);
	}

	/**
	 * Draws memory occupation, a set of colored rectangles represents processes load into memory, 
	 * fragmentation is also highlighted. 
	 * When memory size height exceeds canvas height, memory enlarge and revalidates to perform scroll update
	 * 
	 * @param g	graphic context
	 */
	
	
	public void paint(Graphics g) {
		g2 = (Graphics2D) g;
		Dimension size = getSize();
		int w = (int)size.getWidth();
		int h = (int)size.getHeight();
		
		g2.setPaint(Color.white);
		g2.fillRect(0, 0, w, h);
		
		// Draw Addresses
		// Scroll control height
		//if (M_UNITHEIGTH *  memHeight + 2*BORDER > h) {
			revalidate(); // Updates scroll 
		//}
		
		
		g2.setColor(Color.LIGHT_GRAY);
		g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
		String s = Translation.getInstance().getLabel("me_003");
		FontRenderContext frc = g2.getFontRenderContext();
		Rectangle2D bounds = g2.getFont().getStringBounds(s, frc);
		g.drawString(s,  (int) (w/2 - bounds.getWidth()/2), (int) (BORDER/2 + bounds.getHeight()/2)); // Virtual memory
		
		g2.setColor(Color.BLACK);
		g2.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));

		Iterator<Integer> it = presenter.iterator(3);
		int start = 0, height = 0, prog_height = 0, memStart = 0;
		map.clear();
		
		
	}
	
	private void drawProgram(int start, int x, int y, int width, int height) {
		Rectangle2D rect = new Rectangle2D.Double(x,y,width,height); // Memory block
		


		// Bound
		g2.setColor (Color.BLACK);
		g2.draw(rect);
		
		int OSSize = ((MemoryPresenter) presenter).getOSSize();
		// Information

		FontRenderContext frc = g2.getFontRenderContext();
		Rectangle2D bounds;
		int xText = x + 5;
	    int yText = y + 15;
	}
	

	/**
 	 * Returns true when any rectangle representing a process contains position (x,y) and false otherwise    
	 * 
	 * @param o rectangle representing a process
	 * @param x	x position
	 * @param y	y position
	 * 
	 * @return	queued process contains (x,y) 
	 */
	public boolean contains(Object o, int x, int y){
		Rectangle2D r = (Rectangle2D) o;  
		return r.contains(x, y);
	}	
	
	/**
	 * Returns "virtual memory" 
	 * 
	 * @return "virtual memory"
	 */
	public String getAlias() {
		return "virtualmemory";
	}
}


