import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;

public class Panel extends JPanel{
	boolean drawPlayer=true;
	JScrollPane verticalScroll,horizontalScroll;
	Image player;
	int curLayer=-1;
	BufferedImage ram;
	ArrayList<Layer> layers;
	int zoom=600;
	final int centerY=106-64;
	final int pointRadius=4;
	Timer t;
	public Panel(){
		setLayout(null);
		layers = new ArrayList<Layer>();
		try{
			player = ImageIO.read(new File("./icons/player.png"));
			player = player.getScaledInstance(player.getWidth(null)*zoom/100, player.getHeight(null)*zoom/100, Image.SCALE_SMOOTH);
			ram = ImageIO.read(new File("./icons/ram.png"));
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		t = new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
		t.start();
	}
	
	public void paintComponent(Graphics gr){
		super.paintComponent(gr);
		int scrW=getWidth();
		int scrH=getHeight();
		for(int i=0;i<layers.size();i++){
			Layer layer = layers.get(i);
			AffineTransform at = AffineTransform.getTranslateInstance((layer.x-layer.scaledWidth/2)*zoom/100+scrW/2, (layer.y-layer.scaledHeight/2+centerY)*zoom/100+scrH/2);
			at.rotate(-layer.rotationAngle,layer.scaledWidth/2*zoom/100,layer.scaledHeight/2*zoom/100);
			Graphics2D g2d = (Graphics2D) gr;
			g2d.drawImage(layer.basicImage.getScaledInstance(layer.scaledWidth*zoom/100, layer.scaledHeight*zoom/100, Image.SCALE_SMOOTH), at, null);
		}
		gr.setColor(Color.BLACK);
		gr.drawLine(scrW/2, 0, scrW/2, scrH);
		gr.drawLine(0, scrH/2+centerY*zoom/100, scrW, scrH/2+centerY*zoom/100);
		if(curLayer!=-1){
			Layer layer = layers.get(curLayer);
			gr.setColor(Color.BLUE);
			Graphics2D g = (Graphics2D) gr;
			AffineTransform at = AffineTransform.getTranslateInstance((layer.x-layer.scaledWidth/2)*zoom/100+scrW/2, (layer.y-layer.scaledHeight/2+centerY)*zoom/100+scrH/2);
			at.rotate(-layer.rotationAngle,layer.scaledWidth/2*zoom/100,layer.scaledHeight/2*zoom/100);
			Graphics2D g2d = (Graphics2D) gr;
			g2d.drawImage(ram.getScaledInstance(layer.scaledWidth*zoom/100, layer.scaledHeight*zoom/100, Image.SCALE_SMOOTH), at, null);
		}
		if(drawPlayer){
			gr.drawImage(player, scrW/2-player.getWidth(null)/2, scrH/2-player.getHeight(null)/2, null);
		}
	}
}
