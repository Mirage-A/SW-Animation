import java.awt.Font;
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

public class LayersFrame extends JFrame{
	private final int buttonSize=20;
	JPanel scrollPanel;
	JScrollPane scrollPane;
	JButton newLayerButton, deleteLayerButton, renameLayerButton, upLayerButton, downLayerButton;
	ArrayList<JButton> btns;
	final Font basicFont = new JButton().getFont();
	final Font selectedFont = new Font(basicFont.getFontName(), Font.BOLD, basicFont.getSize()+4);
	public LayersFrame(){
		setSize(200,250);
		setTitle("Layers");
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
        
		newLayerButton = new JButton(new ImageIcon("./icons/create.png"));
		newLayerButton.setBounds(0, getHeight()-37-buttonSize, buttonSize, buttonSize);
		newLayerButton.setSize(buttonSize, buttonSize);
		newLayerButton.setVisible(true);
		newLayerButton.setToolTipText("Create a new layer");
		newLayerButton.setEnabled(false);
		panel.add(newLayerButton);
		
		deleteLayerButton = new JButton(new ImageIcon("./icons/delete.png"));
		deleteLayerButton.setBounds(buttonSize, getHeight()-37-buttonSize, buttonSize, buttonSize);
		deleteLayerButton.setSize(buttonSize, buttonSize);
		deleteLayerButton.setVisible(true);
		deleteLayerButton.setToolTipText("Delete selected layer");
		deleteLayerButton.setEnabled(false);
		panel.add(deleteLayerButton);
		
		renameLayerButton = new JButton(new ImageIcon("./icons/rename.png"));
		renameLayerButton.setBounds(buttonSize*2, getHeight()-37-buttonSize, buttonSize, buttonSize);
		renameLayerButton.setSize(buttonSize, buttonSize);
		renameLayerButton.setVisible(true);
		renameLayerButton.setToolTipText("Rename selected layer");
		renameLayerButton.setEnabled(false);
		panel.add(renameLayerButton);
		
		upLayerButton = new JButton(new ImageIcon("./icons/up.png"));
		upLayerButton.setBounds(buttonSize*3, getHeight()-37-buttonSize, buttonSize, buttonSize);
		upLayerButton.setSize(buttonSize, buttonSize);
		upLayerButton.setVisible(true);
		upLayerButton.setToolTipText("Move selected layer up");
		upLayerButton.setEnabled(false);
		panel.add(upLayerButton);
		
		downLayerButton = new JButton(new ImageIcon("./icons/down.png"));
		downLayerButton.setBounds(buttonSize*4, getHeight()-37-buttonSize, buttonSize, buttonSize);
		downLayerButton.setSize(buttonSize, buttonSize);
		downLayerButton.setVisible(true);
		downLayerButton.setToolTipText("Move selected layer down");
		downLayerButton.setEnabled(false);
		panel.add(downLayerButton);
		
		addComponentListener(new ComponentAdapter() {  
	        public void componentResized(ComponentEvent evt) {
	        	scrollPane.setBounds(0, 0, getWidth()-14, getHeight()-36-buttonSize);
	    		newLayerButton.setBounds(0, getHeight()-37-buttonSize, buttonSize, buttonSize);
	    		deleteLayerButton.setBounds(buttonSize, getHeight()-37-buttonSize, buttonSize, buttonSize);
	    		renameLayerButton.setBounds(buttonSize*2, getHeight()-37-buttonSize, buttonSize, buttonSize);
	    		upLayerButton.setBounds(buttonSize*3, getHeight()-37-buttonSize, buttonSize, buttonSize);
	    		downLayerButton.setBounds(buttonSize*4, getHeight()-37-buttonSize, buttonSize, buttonSize);
	        }
		});
		scrollPane.revalidate();
		setVisible(false);
	}
}
