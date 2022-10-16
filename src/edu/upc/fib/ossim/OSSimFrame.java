package edu.upc.fib.ossim;
import edu.upc.fib.ossim.memory.MemoryPresenter;

import javax.swing.*;
import java.awt.Component;

public class OSSimFrame extends JFrame implements OSSim {

	public OSSimFrame() {
		super();
		initialize();
	}

	private void initialize() {
		this.setTitle("OS Project");
		this.setSize(800,600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(true);
		AppSession.getInstance().setApp(this);
		this.setVisible(true); // Propietat de visibilitat 
	}

	/**
	 * Removes previous views and loads new one into container.  
	 *
	 * @param view	view to load
	 */
	public void loadView(JPanel view) {
		this.getContentPane().removeAll();
		this.setContentPane(view);
		this.setVisible(true);
	}

	/**
	 * Shows a message into a message dialog 	 
	 *
	 * @param msg	message content
	 */
	public void showMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg);
	}

	/**
	 * Application (JFrame) input. 
	 *
	 * @param args	unused
	 */
	@SuppressWarnings("unused")
	public static void main (String args[]) {
		OSSimFrame mainFrame = new OSSimFrame();
		AppSession.getInstance().getApp().setDefaultSize();
		if (AppSession.getInstance().getPresenter() != null)  AppSession.getInstance().getPresenter().closeInfo();
		AppSession.getInstance().setPresenter(new MemoryPresenter(true));
	}

	/**
	 * Gets itself
	 *
	 * @return this
	 */
	public Component getComponent() {
		return this;
	}

	/**
	 * Frame allow opening and saving simulations
	 *
	 * @return true
	 */
	public boolean allowOpenSave() {
		return true;
	}


	public void setMCQSize() {
		// TODO Auto-generated method stub
		this.setSize(1100,600); //Mida 
		repaint();
	}


	public void setDefaultSize() {
		// TODO Auto-generated method stub
		this.setSize(800,600);
		repaint();
	}
} 
