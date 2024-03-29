package edu.upc.fib.ossim;

import edu.upc.fib.ossim.utils.Functions;
import edu.upc.fib.ossim.utils.Translation;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;


/**
 * Welcome application panel, four buttons allow access to simulations: 
 * process scheduling, memory management, file system management and disk scheduling
 * 
 * @author Àlex
 */
public class Home extends JPanel implements Observer {
	private static final long serialVersionUID = 1L;

	private JButton scheduler;
	private JButton memory;
	private JButton fileSystem;
	private JButton disk;
	//QCM creator
	private JButton qcm_creator;
	//QCM response
	private JButton qcm_loader;
	private Menu menu;
	
	/**
	 * Constructs Home panel, initializes buttons
	 * 
	 * @param menu				application menu
	 */
	public Home(Menu menu) { 
		super();
		this.menu = menu;
		AppSession.getInstance().getLangNotifier().addObserver(this);
		initLayout();
	}

	private void initLayout() {
		SpringLayout layout = new SpringLayout();
		this.setLayout(layout);
		
		// Buttons
		JPanel grid = new JPanel(new SpringLayout());
		grid.setBackground(Color.WHITE);
		scheduler = new JButton(Functions.getInstance().createImageIcon("cpu.png"));
		scheduler.setToolTipText(Translation.getInstance().getLabel("all_53"));
		scheduler.setActionCommand("sch");
		scheduler.addActionListener(menu);
		memory = new JButton(Functions.getInstance().createImageIcon("ram.png"));
		memory.setToolTipText(Translation.getInstance().getLabel("all_54"));
		memory.setActionCommand("mngc");
		memory.addActionListener(menu);
		fileSystem = new JButton(Functions.getInstance().createImageIcon("tree.png"));
		fileSystem.setToolTipText(Translation.getInstance().getLabel("all_58"));
		fileSystem.setActionCommand("fls");
		fileSystem.addActionListener(menu);
		disk = new JButton(Functions.getInstance().createImageIcon("hdd.png"));
		disk.setToolTipText(Translation.getInstance().getLabel("all_57"));
		disk.setActionCommand("mngd");
		disk.addActionListener(menu);
		qcm_creator =  new JButton(Functions.getInstance().createImageIcon("qcm_creator.jpg"));
		qcm_creator.setToolTipText(Translation.getInstance().getLabel("all_73"));
		qcm_creator.setActionCommand("qcmc");
		qcm_creator.addActionListener(menu);
		qcm_loader =  new JButton(Functions.getInstance().createImageIcon("qcm_loader.jpg"));
		qcm_loader.setToolTipText(Translation.getInstance().getLabel("all_74"));
		qcm_loader.setActionCommand("qcml");
		qcm_loader.addActionListener(menu);

		grid.add(scheduler);
		grid.add(memory);
		grid.add(fileSystem);
		grid.add(disk);
		grid.add(qcm_creator);
		grid.add(qcm_loader);
		
		Functions.getInstance().makeCompactGrid(grid, 2, 3, 6, 6, 6, 6);
		
		layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, grid, 0, SpringLayout.HORIZONTAL_CENTER, this);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, grid, 0, SpringLayout.VERTICAL_CENTER, this);
		add(grid);
	}

	/**
	 * Translate home components
	 * 
	 * @param o		observable
	 * @param arg	unused
	 */
	public void update(Observable o, Object arg) {
		scheduler.setToolTipText(Translation.getInstance().getLabel("all_53"));
		memory.setToolTipText(Translation.getInstance().getLabel("all_54"));
		fileSystem.setToolTipText(Translation.getInstance().getLabel("all_58"));
		disk.setToolTipText(Translation.getInstance().getLabel("all_57"));
	}
	
	/**
	 * 
	 * Shows panel background image
	 * 
	 * @param	g	graphic context
	 */
	public void paintComponent(Graphics g) {
		ImageIcon fons = Functions.getInstance().createImageIcon("fonsfooter.jpg");
		g.drawImage(fons.getImage(), 0, 0, getSize().width, getSize().height, null);
	}
}
