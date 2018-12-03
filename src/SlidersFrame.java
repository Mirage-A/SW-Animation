import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class SlidersFrame extends JFrame{
	JSlider sizeSlider, widthSlider, heightSlider;
	public SlidersFrame(){
		final int sliderWidth = 200;
		final int sliderHeight = 20;
		final int space = 0;
		
		setSize(sliderWidth+space*2+42,42+sliderHeight*3+space*4);
		setTitle("Layer size");
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		getContentPane().add(panel);
		setVisible(false);
		
		sizeSlider = new JSlider(JSlider.HORIZONTAL, 25, 200, 100);
		widthSlider = new JSlider(JSlider.HORIZONTAL, 25, 200, 100);
		heightSlider = new JSlider(JSlider.HORIZONTAL, 25, 200, 100);
		
		sizeSlider.setBounds(space, 4+space, sliderWidth, sliderHeight);
		widthSlider.setBounds(space, 4+space*2+sliderHeight, sliderWidth, sliderHeight);
		heightSlider.setBounds(space, 4+space*3+sliderHeight*2, sliderWidth, sliderHeight);
		
		sizeSlider.setVisible(true);
		widthSlider.setVisible(true);
		heightSlider.setVisible(true);
		
		panel.add(sizeSlider);
		panel.add(widthSlider);
		panel.add(heightSlider);
		try{
			JLabel sizeLabel = new JLabel(new ImageIcon(ImageIO.read(new File("./icons/size.png"))));
			sizeLabel.setBounds(sizeSlider.getX()+sizeSlider.getWidth(), sizeSlider.getY(), sliderHeight, sliderHeight);
			sizeLabel.setVisible(true);
			panel.add(sizeLabel);

			JLabel widthLabel = new JLabel(new ImageIcon(ImageIO.read(new File("./icons/width.png"))));
			widthLabel.setBounds(widthSlider.getX()+widthSlider.getWidth(), widthSlider.getY(), sliderHeight, sliderHeight);
			widthLabel.setVisible(true);
			panel.add(widthLabel);

			JLabel heightLabel = new JLabel(new ImageIcon(ImageIO.read(new File("./icons/height.png"))));
			heightLabel.setBounds(heightSlider.getX()+heightSlider.getWidth(), heightSlider.getY(), sliderHeight, sliderHeight);
			heightLabel.setVisible(true);
			panel.add(heightLabel);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
