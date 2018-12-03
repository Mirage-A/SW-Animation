import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class FramesFrame extends JFrame{
	private final int buttonSize=20;
	JPanel scrollPanel;
	JScrollPane scrollPane;
	JButton newFrameButton, copyLastFrameButton, deleteFrameButton, loadFrameButton;
	ArrayList<JButton> btns;
	public FramesFrame(){
		setSize(200,250);
		setTitle("Frames");
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		btns = new ArrayList<JButton>();
		JPanel panel = new JPanel();
		panel.setLayout(null);
		getContentPane().add(panel);
		
		scrollPanel = new JPanel();
		scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));
		scrollPane = new JScrollPane(scrollPanel);
		scrollPane.setBounds(0, 0, getWidth()-14, getHeight()-38-buttonSize);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setVisible(true);
		panel.add(scrollPane);
        
		newFrameButton = new JButton(new ImageIcon("./icons/create.png"));
		newFrameButton.setBounds(0, getHeight()-37-buttonSize, buttonSize, buttonSize);
		newFrameButton.setSize(buttonSize, buttonSize);
		newFrameButton.setVisible(true);
		newFrameButton.setToolTipText("Create a new frame");
		panel.add(newFrameButton);
		
		copyLastFrameButton = new JButton(new ImageIcon("./icons/createCopy.png"));
		copyLastFrameButton.setBounds(buttonSize, getHeight()-37-buttonSize, buttonSize, buttonSize);
		copyLastFrameButton.setSize(buttonSize, buttonSize);
		copyLastFrameButton.setVisible(true);
		copyLastFrameButton.setToolTipText("Create a copy of selected frame");
		panel.add(copyLastFrameButton);
		
		deleteFrameButton = new JButton(new ImageIcon("./icons/delete.png"));
		deleteFrameButton.setBounds(buttonSize*2, getHeight()-37-buttonSize, buttonSize, buttonSize);
		deleteFrameButton.setSize(buttonSize, buttonSize);
		deleteFrameButton.setVisible(true);
		deleteFrameButton.setToolTipText("Delete selected frame");
		deleteFrameButton.setEnabled(false);
		panel.add(deleteFrameButton);
		
		loadFrameButton = new JButton(new ImageIcon("./icons/copy.png"));
		loadFrameButton.setBounds(buttonSize*3, getHeight()-37-buttonSize, buttonSize, buttonSize);
		loadFrameButton.setSize(buttonSize, buttonSize);
		loadFrameButton.setVisible(true);
		loadFrameButton.setToolTipText("Create a copy of another frame");
		panel.add(loadFrameButton);
		
		addComponentListener(new ComponentAdapter() {  
	        public void componentResized(ComponentEvent evt) {
	        	scrollPane.setBounds(0, 0, getWidth()-14, getHeight()-36-buttonSize);
	        	newFrameButton.setBounds(0, getHeight()-37-buttonSize, buttonSize, buttonSize);
	    		copyLastFrameButton.setBounds(buttonSize, getHeight()-37-buttonSize, buttonSize, buttonSize);
	    		deleteFrameButton.setBounds(buttonSize*2, getHeight()-37-buttonSize, buttonSize, buttonSize);
	    		loadFrameButton.setBounds(buttonSize*3, getHeight()-37-buttonSize, buttonSize, buttonSize);
	        }
		});
		scrollPane.revalidate();
		setVisible(false);
	}
}
