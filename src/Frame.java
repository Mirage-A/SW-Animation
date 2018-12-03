import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

public class Frame extends JFrame{
	Panel panel;
	LayersFrame layersFrame;
	FramesFrame framesFrame;
	SlidersFrame slidersFrame;
	boolean isPlayingAnimation=false;
	File framesFolder;
	int curFrame=-1;
	boolean isMoving=false;
	int x1,y1;
	Timer animTimer;
	final int animDelay=1000;
	public static void main(String[] args) {
		Frame mainFrame = new Frame();
	}
	
	public Frame(){
		animTimer = new Timer(animDelay, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadFrame(curFrame);
				framesFrame.btns.get(curFrame).setFont(layersFrame.basicFont);
				panel.repaint();
				curFrame++;
				if(curFrame>=framesFolder.list().length){
					curFrame=0;
				}
				framesFrame.btns.get(curFrame).setFont(layersFrame.selectedFont);
			}
		});
		setTitle("Animation editor");
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		panel = new Panel();
		getContentPane().add(panel);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		JCheckBox player = new JCheckBox("Show shape");
		player.setSelected(true);
		player.setBounds(8, 10, 160, 24);
		player.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				panel.drawPlayer=player.isSelected();
			}
		});
		player.setVisible(true);
		panel.add(player);
		JSlider zoomSlider = new JSlider(100, 800, panel.zoom);
		zoomSlider.setBounds(player.getX(), player.getY()+player.getHeight()+2, player.getWidth(), player.getHeight());
		zoomSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				panel.zoom = zoomSlider.getValue();
				try{
					panel.player = ImageIO.read(new File("./icons/player.png"));
					panel.player = panel.player.getScaledInstance(panel.player.getWidth(null)*panel.zoom/100, panel.player.getHeight(null)*panel.zoom/100, Image.SCALE_SMOOTH);
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});
		zoomSlider.setVisible(true);
		panel.add(zoomSlider);
		
		JCheckBox secanim = new JCheckBox("Animation lasts 1 sec");
		secanim.setBounds(zoomSlider.getX(), zoomSlider.getY()+zoomSlider.getHeight()+2, zoomSlider.getWidth(), zoomSlider.getHeight());
		secanim.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(isPlayingAnimation){
					if(secanim.isSelected()){
						animTimer.setDelay(animDelay/framesFolder.list().length);
					}
					else{
						animTimer.setDelay(40);
					}
				}
			}
		});
		secanim.setVisible(true);
		panel.add(secanim);
		
		
		JButton anim = new JButton("Start animation");
		anim.setBounds(secanim.getX(), secanim.getY()+secanim.getHeight()+2, secanim.getWidth(), secanim.getHeight());
		anim.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!isPlayingAnimation){
					panel.t.stop();
					anim.setText("Stop animation");
					saveFrame();
					if(curFrame==-1)curFrame=0;
					if(secanim.isSelected()){
						animTimer.setDelay(animDelay/framesFolder.list().length);
					}
					else{
						animTimer.setDelay(40);
					}
					animTimer.restart();
				}
				else{
					animTimer.stop();
					anim.setText("Start animation");
					if(curFrame!=-1){
						framesFrame.btns.get(curFrame).setFont(layersFrame.basicFont);
					}
					curFrame = 0;
					framesFrame.btns.get(0).setFont(layersFrame.selectedFont);
					loadFrame(curFrame);
					panel.t.restart();
				}
				isPlayingAnimation=!isPlayingAnimation;
			}
		});
		anim.setVisible(true);
		panel.add(anim);
		
		JButton reverse = new JButton("Mirror animation");
		reverse.setBounds(anim.getX(), anim.getY()+anim.getHeight()+4, anim.getWidth(), anim.getHeight());
		reverse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(JOptionPane.showConfirmDialog(Frame.this, "Do you want to create a mirrored animation?", "Mirroring animation", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION){
					saveAll();
				}
			}
		});
		reverse.setVisible(true);
		panel.add(reverse);
		
		addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {}
			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowDeactivated(WindowEvent e) {}
			@Override
			public void windowClosing(WindowEvent e) {
				int ans = JOptionPane.showOptionDialog(getContentPane(), "What do you want to do?", "Exit", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"Open another animation","Continue work","Save and exit"}, 0);
				if(ans==JOptionPane.YES_OPTION){
					saveFrame();
					loadAnimation();
				}
				else if(ans==JOptionPane.CANCEL_OPTION){
					saveFrame();
					System.exit(0);
				}
				
			}
			@Override
			public void windowClosed(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
		});
		setVisible(true);
		layersFrame = new LayersFrame();
		framesFrame = new FramesFrame();
		slidersFrame = new SlidersFrame();
		int ans = JOptionPane.showOptionDialog(getContentPane(), "Welcome to Shattered World animation editor!", "Welcome!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"Create new animation","Load animation","Exit editor"}, 0);
		if(ans==JOptionPane.YES_OPTION){
			createNewAnimation();
			
		}
		else if(ans==JOptionPane.NO_OPTION){
			loadAnimation();
		}
		else if(ans==JOptionPane.CANCEL_OPTION){
			System.exit(0);
		}
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		layersFrame.setLocation(screen.width-layersFrame.getWidth()-20, screen.height-layersFrame.getHeight()-60);
		layersFrame.setVisible(true);
		framesFrame.setLocation(screen.width-framesFrame.getWidth()-20, screen.height-layersFrame.getHeight()-framesFrame.getHeight()-80);
		framesFrame.setVisible(true);
		slidersFrame.setLocation(layersFrame.getX()-20-slidersFrame.getWidth(), screen.height-60-slidersFrame.getHeight());
		slidersFrame.setVisible(false);
		
		layersFrame.newLayerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser("./drawable");
				fc.addChoosableFileFilter(new FileFilter() {
					@Override
					public String getDescription() {
						return "Images (.PNG)";
					}
					
					@Override
					public boolean accept(File f) {
						if(f.getName().endsWith(".png")) return true;
						else return false;
					}
				});
				fc.setDialogTitle("Choose an image for the new layer");
				
				if(fc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
					File image = fc.getSelectedFile();
					if(image.getName().endsWith(".png")){
						Layer layer = new Layer(image.getName());
						panel.layers.add(layer);
						JButton tmp = new JButton(layer.layerName);
						tmp.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								loadLayer(layersFrame.btns.indexOf(tmp));
							}
						});
						layersFrame.btns.add(tmp);
						layersFrame.scrollPanel.add(tmp);
						layersFrame.scrollPanel.revalidate();
						loadLayer(layersFrame.btns.size()-1);
					}
				}
				
			}
		});
		layersFrame.deleteLayerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(JOptionPane.showConfirmDialog(layersFrame, "Delete the layer "+panel.layers.get(panel.curLayer).layerName+"?", "Delete layer", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE)==JOptionPane.YES_OPTION){
					layersFrame.scrollPanel.remove(layersFrame.btns.get(panel.curLayer));
					layersFrame.btns.remove(panel.curLayer);
					panel.layers.remove(panel.curLayer);
					panel.curLayer=-1;
					layersFrame.deleteLayerButton.setEnabled(false);
					layersFrame.renameLayerButton.setEnabled(false);
					layersFrame.upLayerButton.setEnabled(false);
					layersFrame.downLayerButton.setEnabled(false);
					layersFrame.scrollPanel.repaint();
					layersFrame.scrollPane.revalidate();
				}
			}
		});
		layersFrame.renameLayerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String newName = JOptionPane.showInputDialog(layersFrame, "Create a new name for the layer "+panel.layers.get(panel.curLayer).layerName, "Rename the layer", JOptionPane.PLAIN_MESSAGE).trim();
				panel.layers.get(panel.curLayer).layerName = newName;
				layersFrame.btns.get(panel.curLayer).setText(newName);
			}
		});
		layersFrame.upLayerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(panel.curLayer>0){
					ArrayList<Layer> layers = panel.layers;
					ArrayList<JButton> btns = layersFrame.btns;
					panel.layers = new ArrayList<Layer>();
					layersFrame.btns = new ArrayList<JButton>();
					for(int i=0; i<panel.curLayer-1; i++){
						panel.layers.add(layers.get(i));
					}
					panel.layers.add(layers.get(panel.curLayer));
					panel.layers.add(layers.get(panel.curLayer-1));
					for(int i=panel.curLayer+1; i<layers.size(); i++){
						panel.layers.add(layers.get(i));
					}
					
					for(int i=0; i<panel.curLayer-1; i++){
						layersFrame.btns.add(btns.get(i));
					}
					layersFrame.btns.add(btns.get(panel.curLayer));
					layersFrame.btns.add(btns.get(panel.curLayer-1));
					for(int i=panel.curLayer+1; i<btns.size(); i++){
						layersFrame.btns.add(btns.get(i));
					}
					

					layersFrame.scrollPanel.removeAll();
					for(int i=0; i<layersFrame.btns.size(); i++){
						layersFrame.scrollPanel.add(layersFrame.btns.get(i));
					}
					layersFrame.scrollPanel.repaint();
					layersFrame.scrollPane.revalidate();
					panel.curLayer--;
				}
			}
		});
		layersFrame.downLayerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(panel.curLayer<panel.layers.size()-1){
					ArrayList<Layer> layers = panel.layers;
					ArrayList<JButton> btns = layersFrame.btns;
					panel.layers = new ArrayList<Layer>();
					layersFrame.btns = new ArrayList<JButton>();
					for(int i=0; i<panel.curLayer; i++){
						panel.layers.add(layers.get(i));
					}
					panel.layers.add(layers.get(panel.curLayer+1));
					panel.layers.add(layers.get(panel.curLayer));
					for(int i=panel.curLayer+2; i<layers.size(); i++){
						panel.layers.add(layers.get(i));
					}
					
					for(int i=0; i<panel.curLayer; i++){
						layersFrame.btns.add(btns.get(i));
					}
					layersFrame.btns.add(btns.get(panel.curLayer+1));
					layersFrame.btns.add(btns.get(panel.curLayer));
					for(int i=panel.curLayer+2; i<btns.size(); i++){
						layersFrame.btns.add(btns.get(i));
					}
					

					layersFrame.scrollPanel.removeAll();
					for(int i=0; i<layersFrame.btns.size(); i++){
						layersFrame.scrollPanel.add(layersFrame.btns.get(i));
					}
					layersFrame.scrollPanel.repaint();
					layersFrame.scrollPane.revalidate();
					panel.curLayer++;
				}
			}
		});
		framesFrame.newFrameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.print("new frame "+framesFolder.list().length);
				File frame = new File(framesFolder.getAbsolutePath()+"/"+"frame"+framesFolder.list().length+".swanim");
				try{
					saveFrame();
					frame.createNewFile();
					FileWriter out = new FileWriter(frame);
					out.write("0");
					out.close();
					JButton tmp = new JButton("frame"+(framesFolder.list().length-1));
					tmp.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							saveFrame();
							if(curFrame!=-1){
								framesFrame.btns.get(curFrame).setFont(layersFrame.basicFont);
							}
							curFrame = Integer.parseInt(tmp.getText().substring(5));
							tmp.setFont(layersFrame.selectedFont);
							loadFrame(curFrame);
							framesFrame.deleteFrameButton.setEnabled(true);
						}
					});
					framesFrame.btns.add(tmp);
					framesFrame.scrollPanel.add(tmp);
					framesFrame.scrollPanel.repaint();
					framesFrame.scrollPane.revalidate();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			}
		});
		framesFrame.copyLastFrameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(framesFolder.list().length>0 && curFrame!=-1){
					System.out.println("copy frame "+curFrame);
						try{
							saveFrame();
							File frame = new File(framesFolder.getAbsolutePath()+"/"+"frame"+framesFolder.list().length+".swanim");
							frame.createNewFile();
							JButton tmp = new JButton("frame"+(framesFolder.list().length-1));
							tmp.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									saveFrame();
									if(curFrame!=-1){
										framesFrame.btns.get(curFrame).setFont(layersFrame.basicFont);
									}
									curFrame = Integer.parseInt(tmp.getText().substring(5));
									tmp.setFont(layersFrame.selectedFont);
									loadFrame(curFrame);
									framesFrame.deleteFrameButton.setEnabled(true);
								}
							});
							framesFrame.btns.add(tmp);
							framesFrame.scrollPanel.add(tmp);
							framesFrame.scrollPanel.repaint();
							framesFrame.scrollPane.revalidate();
							File[] frames = framesFolder.listFiles();
							for(int i=frames.length-1;i>curFrame;i--){
								FileWriter out = new FileWriter(new File(framesFolder.getAbsolutePath()+"/frame"+i+".swanim"));
								out.write(new String(Files.readAllBytes(new File(framesFolder.getAbsolutePath()+"/frame"+(i-1)+".swanim").toPath())));
								out.close();
							}
						}
						catch(Exception ex){
							ex.printStackTrace();
						}
				}
				
			}
		});
		framesFrame.deleteFrameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(JOptionPane.showConfirmDialog(framesFrame, "Delete the frame "+curFrame+"?", "Delete frame", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE)==JOptionPane.YES_OPTION){
					System.out.println("delete frame "+curFrame);
					if(curFrame!=-1){
						framesFrame.btns.get(curFrame).setFont(layersFrame.basicFont);
					}
					framesFrame.deleteFrameButton.setEnabled(false);
					File[] files = framesFolder.listFiles();
					for(int i=curFrame; i<files.length-1; i++){
						try{
							FileWriter out = new FileWriter(new File(framesFolder.getAbsolutePath()+"/frame"+i+".swanim"));
							out.write(new String(Files.readAllBytes(new File(framesFolder.getAbsolutePath()+"/frame"+(i+1)+".swanim").toPath())));
							out.close();
						}
						catch(Exception ex){
							ex.printStackTrace();
						}
					}
					new File(framesFolder.getAbsolutePath()+"/frame"+(framesFolder.list().length-1)+".swanim").delete();
					framesFrame.scrollPanel.remove(framesFrame.btns.get(framesFrame.btns.size()-1));
					framesFrame.btns.remove(framesFrame.btns.size()-1);
					framesFrame.scrollPanel.repaint();
					framesFrame.scrollPane.revalidate();
					curFrame=-1;
					panel.layers.clear();
					layersFrame.btns.clear();
					layersFrame.scrollPanel.removeAll();
					layersFrame.scrollPane.revalidate();
					layersFrame.deleteLayerButton.setEnabled(false);
					layersFrame.renameLayerButton.setEnabled(false);
					layersFrame.upLayerButton.setEnabled(false);
					layersFrame.downLayerButton.setEnabled(false);
				}
			}
		});
		framesFrame.loadFrameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser("./animations");
				fc.addChoosableFileFilter(new FileFilter() {
					
					@Override
					public String getDescription() {
						return "Shattered World animations (.SWANIM)";
					}
					
					@Override
					public boolean accept(File f) {
						if(f.getName().endsWith(".swanim")) return true;
						else return false;
					}
				});
				fc.setDialogTitle("Choose a frame to create a copy of it");
				
				if(fc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
					File file = fc.getSelectedFile();
					if(file.getName().endsWith(".swanim")){
						System.out.println("copy frame "+file.getName());
						File frame = new File(framesFolder.getAbsolutePath()+"/"+"frame"+framesFolder.list().length+".swanim");
						try{
							saveFrame();
							frame.createNewFile();
							FileWriter out = new FileWriter(frame);
							out.write(new String(Files.readAllBytes(file.toPath())));
							out.close();
							JButton tmp = new JButton("frame"+(framesFolder.list().length-1));
							tmp.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									saveFrame();
									if(curFrame!=-1){
										framesFrame.btns.get(curFrame).setFont(layersFrame.basicFont);
									}
									curFrame = Integer.parseInt(tmp.getText().substring(5));
									tmp.setFont(layersFrame.selectedFont);
									loadFrame(curFrame);
									framesFrame.deleteFrameButton.setEnabled(true);
								}
							});
							framesFrame.btns.add(tmp);
							framesFrame.scrollPanel.add(tmp);
							framesFrame.scrollPanel.repaint();
							framesFrame.scrollPane.revalidate();
						}
						catch(Exception ex){
							ex.printStackTrace();
						}
					}
				}
			}
		});
		slidersFrame.sizeSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Layer layer = panel.layers.get(panel.curLayer);
				layer.xsize = slidersFrame.sizeSlider.getValue();
				layer.updateSize();
			}
		});
		slidersFrame.widthSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Layer layer = panel.layers.get(panel.curLayer);
				layer.xwidth = slidersFrame.widthSlider.getValue();
				layer.updateSize();
			}
		});
		slidersFrame.heightSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				Layer layer = panel.layers.get(panel.curLayer);
				layer.xheight = slidersFrame.heightSlider.getValue();
				layer.updateSize();
			}
		});
		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				isMoving=false;
			}
			@Override
			public void mousePressed(MouseEvent e) {
				if(panel.curLayer!=-1){
					Layer layer = panel.layers.get(panel.curLayer);
					int scrW = panel.getWidth();
					int scrH = panel.getHeight();
					int range = (int)(Math.sqrt(sqr((layer.x)*panel.zoom/100+scrW/2-e.getX())+sqr((layer.y+panel.centerY)*panel.zoom/100+scrH/2-e.getY())));
					if(range<Math.min(layer.scaledWidth, layer.scaledHeight)*panel.zoom/200){
						isMoving=true;
						x1=(e.getX()-(layer.x*panel.zoom/100+scrW/2))*100/panel.zoom;
						y1=((layer.y+panel.centerY)*panel.zoom/100+scrH/2-e.getY())*100/panel.zoom;
					}
					else{
						isMoving=false;
					}
				}
			}
			@Override
			public void mouseExited(MouseEvent e) {
				
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				
			}
		});
		addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if(panel.curLayer!=-1){
					Layer layer = panel.layers.get(panel.curLayer);
					int scrW = panel.getWidth();
					int scrH = panel.getHeight();
					int range = (int)(Math.sqrt(sqr((layer.x)*panel.zoom/100+scrW/2-e.getX())+sqr((layer.y+panel.centerY)*panel.zoom/100+scrH/2-e.getY())));
					if(range<Math.min(layer.scaledWidth, layer.scaledHeight)*panel.zoom/200){
						panel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
					}
					else{
						panel.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
					}
				}
				else{
					panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				if(panel.curLayer!=-1){
					Layer layer = panel.layers.get(panel.curLayer);
					int scrW = panel.getWidth();
					int scrH = panel.getHeight();
					if(isMoving){
						layer.x = (e.getX()-scrW/2)*100/panel.zoom-x1;
						layer.y = (e.getY()-scrH/2)*100/panel.zoom-panel.centerY+y1;
					}
					else{
						double sy = ((layer.y+panel.centerY)*panel.zoom/100+scrH/2-e.getY());
						double sx = (e.getX()-layer.x*panel.zoom/100-scrW/2);
						if(Math.abs(sy)>=Math.abs(sx)){
							if(sx>0){
								layer.rotationAngle=Math.atan(sy/sx);
							}
							else if(sx<0){
								layer.rotationAngle=Math.PI+Math.atan(sy/sx);
							}
							else if(sy>0){
								layer.rotationAngle=Math.PI/2;
							}
							else{
								layer.rotationAngle=-Math.PI/2;
							}
						}
						else{
							if(sy!=0){
								layer.rotationAngle=-Math.atan(sx/sy)-Math.PI/2;
								if(sy>0){
									layer.rotationAngle+=Math.PI;
								}
							}
							else if(sx>0){
								layer.rotationAngle=0;
							}
							else{
								layer.rotationAngle=Math.PI;
							}
						}
						double a = Math.toDegrees(layer.rotationAngle);
					}
				}
			}
		});
	}
	private void saveAll(){
		//TODO Отражение анимаций
		String weapon = framesFolder.getName();
		String moveDirection = framesFolder.getParentFile().getParentFile().getName();
		String folder = framesFolder.getParentFile().getParentFile().getParentFile().getAbsolutePath();
		File mirror = null;
		boolean isDiag=false;
		//mirror - Полный поворот
		//mirror2 - Поворот горизонтально
		//mirror3 - Поворот спереди назад
		File mirror2=null,mirror3=null;
		if(moveDirection.equals("0 Right")){
			mirror = new File(folder+"/4 Left"+"/"+weapon);
		}
		else if(moveDirection.equals("1 UpRight")){
			isDiag=true;
			mirror = new File(folder+"/5 DownLeft"+"/"+weapon);
			mirror2 = new File(folder+"/3 UpLeft"+"/"+weapon);
			mirror3 = new File(folder+"/7 DownRight"+"/"+weapon);
		}
		else if(moveDirection.equals("2 Up")){
			mirror = new File(folder+"/6 Down"+"/"+weapon);
		}
		else if(moveDirection.equals("3 UpLeft")){
			isDiag=true;
			mirror = new File(folder+"/7 DownRight"+"/"+weapon);
			mirror2 = new File(folder+"/1 UpRight"+"/"+weapon);
			mirror3 = new File(folder+"/5 DownLeft"+"/"+weapon);
		}
		else if(moveDirection.equals("4 Left")){
			mirror = new File(folder+"/0 Right"+"/"+weapon);
		}
		else if(moveDirection.equals("5 DownLeft")){
			isDiag=true;
			mirror = new File(folder+"/1 UpRight"+"/"+weapon);
			mirror2 = new File(folder+"/7 DownRight"+"/"+weapon);
			mirror3 = new File(folder+"/3 UpLeft"+"/"+weapon);
		}
		else if(moveDirection.equals("6 Down")){
			mirror = new File(folder+"/2 Up"+"/"+weapon);
		}
		else if(moveDirection.equals("7 DownRight")){
			isDiag=true;
			mirror = new File(folder+"/3 UpLeft"+"/"+weapon);
			mirror2 = new File(folder+"/5 DownLeft"+"/"+weapon);
			mirror3 = new File(folder+"/1 UpRight"+"/"+weapon);
		}
		ArrayList<ArrayList<Layer>> frames = new ArrayList<ArrayList<Layer>>();
		int framesKol = framesFolder.list().length;
		for(int i=0;i<framesKol;i++){
			ArrayList<Layer> frame = new ArrayList<Layer>();
			try{
				Scanner in = new Scanner(new File(framesFolder.getAbsolutePath()+"/frame"+i+".swanim"));
				int layersKol = in.nextInt();
				for(int j=0; j<layersKol; j++){
					Layer layer = new Layer(in.next(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), (double)(in.nextInt())/1000000);
					frame.add(layer);
				}
				in.close();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
			frames.add(frame);
		}
		//Перезаписываем mirror
		for(int frame=0;frame<framesKol;frame++){
			try{
				File file = new File(mirror.getAbsolutePath()+"/frame"+frame+".swanim");
				if(!file.exists()){
					file.createNewFile();
				}
				FileWriter out = new FileWriter(file);
				ArrayList<Layer> layers = frames.get(frame);
				out.write(layers.size()+"");
				for(int i=0;i<layers.size();i++){
					Layer layer = layers.get(layers.size()-i-1);
					String imageName = layer.imageName;
					//TODO Изменение названия изображения
					if(imageName.startsWith("head")){
						int k = Integer.parseInt(imageName.charAt(4)+"")-4;
						if(k<0){
							k+=8;
						}
						imageName="head"+k+".png";
					}
					out.write(" "+imageName+" "+(-layer.x)+" "+layer.y+" "+layer.scaledWidth+" "+layer.scaledHeight+" "+layer.xsize+" "+(int)((Math.PI-layer.rotationAngle)*1000000));
				}
				out.flush();
				out.close();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		/*
		if(isDiag){
			//Перезаписываем mirror2
			//Отражение горизонтально
			for(int frame=0;frame<framesKol;frame++){
				try{
					File file = new File(mirror2.getAbsolutePath()+"/frame"+frame+".swanim");
					if(!file.exists()){
						file.createNewFile();
					}
					FileWriter out = new FileWriter(file);
					ArrayList<Layer> layers = frames.get(frame);
					out.write(layers.size()+"");
					for(int i=0;i<layers.size();i++){
						Layer layer = layers.get(layers.size()-i-1);
						String imageName = layer.imageName;
						//TODO Изменение названия изображения
						if(imageName.startsWith("head")){
							int k = Integer.parseInt(imageName.charAt(4)+"");
							if(k==1)k=3;
							else if(k==3)k=1;
							else if(k==5)k=7;
							else if(k==7)k=5;
							imageName="head"+k+".png";
						}
						out.write(" "+imageName+" "+(-layer.x)+" "+layer.y+" "+layer.scaledWidth+" "+layer.scaledHeight+" "+layer.xsize+" "+(int)((Math.PI-layer.rotationAngle)*1000000));
					}
					out.flush();
					out.close();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			}
			//Перезаписываем mirror3
			//Отражение спереди назад
			for(int frame=0;frame<framesKol;frame++){
				try{
					File file = new File(mirror2.getAbsolutePath()+"/frame"+frame+".swanim");
					if(!file.exists()){
						file.createNewFile();
					}
					FileWriter out = new FileWriter(file);
					ArrayList<Layer> layers = frames.get(frame);
					out.write(layers.size()+"");
					for(int i=0;i<layers.size();i++){
						Layer layer = layers.get(layers.size()-i-1);
						String imageName = layer.imageName;
						//TODO Изменение названия изображения
						if(imageName.startsWith("head")){
							int k = Integer.parseInt(imageName.charAt(4)+"");
							if(k==1)k=7;
							else if(k==7)k=1;
							else if(k==5)k=3;
							else if(k==3)k=5;
							imageName="head"+k+".png";
						}
						out.write(" "+imageName+" "+(-layer.x)+" "+layer.y+" "+layer.scaledWidth+" "+layer.scaledHeight+" "+layer.xsize+" "+(int)((Math.PI-layer.rotationAngle)*1000000));
					}
					out.flush();
					out.close();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
		*/
	}
	private void createNewAnimation(){
		String animationName = JOptionPane.showInputDialog(this, "Enter the new animation's name (for example, Fireball)", "New animation", JOptionPane.PLAIN_MESSAGE).trim();
		int framesKol = Integer.parseInt(JOptionPane.showInputDialog(this, "Input the number of frames", animationName, JOptionPane.PLAIN_MESSAGE).trim());
		File animationsFolder = new File("./animations");
		if(!animationsFolder.exists()){
			animationsFolder.mkdir();
		}
		
		
		File nomoveAnimationsFolder = new File("./animationsbottomnomove");
		if(!nomoveAnimationsFolder.exists()){
			nomoveAnimationsFolder.mkdir();
			for(int moveDirection=0; moveDirection<8; moveDirection++){
				File framesFolder = new File(nomoveAnimationsFolder.getAbsolutePath()+"/"+getMoveDirectionFolderName(moveDirection));
				framesFolder.mkdir();
				File frame = new File(framesFolder.getAbsolutePath()+"/"+"frame0.swanim");
				try{
					frame.createNewFile();
					FileWriter out = new FileWriter(frame);
					out.write("0");
					out.close();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}

		File moveAnimationsFolder = new File("./animationsbottommove");
		if(!moveAnimationsFolder.exists()){
			moveAnimationsFolder.mkdir();
			for(int moveDirection=0; moveDirection<8; moveDirection++){
				File framesFolder = new File(moveAnimationsFolder.getAbsolutePath()+"/"+getMoveDirectionFolderName(moveDirection));
				framesFolder.mkdir();
				File frame = new File(framesFolder.getAbsolutePath()+"/"+"frame0.swanim");
				try{
					frame.createNewFile();
					FileWriter out = new FileWriter(frame);
					out.write("0");
					out.close();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
		
		
		
		
		
		
		
		
		File moveDirectionsFolder = new File("./animations/"+animationsFolder.list().length+" "+animationName);
		moveDirectionsFolder.mkdir();
		for(int moveDirection=0; moveDirection<8; moveDirection++){
			File weaponsFolder = new File(moveDirectionsFolder.getAbsolutePath()+"/"+getMoveDirectionFolderName(moveDirection));
			weaponsFolder.mkdir();
			for(int weaponID=0; weaponID<=5; weaponID++){
				File framesFolder = new File(weaponsFolder.getAbsolutePath()+"/"+getWeaponFolderName(weaponID));
				framesFolder.mkdir();
				for(int frameID=0; frameID<framesKol; frameID++){
					File frame = new File(framesFolder.getAbsolutePath()+"/"+"frame"+frameID+".swanim");
					try{
						frame.createNewFile();
						FileWriter out = new FileWriter(frame);
						out.write("0");
						out.close();
					}
					catch(Exception ex){
						ex.printStackTrace();
					}
				}
			}
		}
		JOptionPane.showMessageDialog(this, "Animation created!", "New animation", JOptionPane.PLAIN_MESSAGE);
		int ans = JOptionPane.showOptionDialog(getContentPane(), "Welcome to Shattered World animation editor!", "Welcome!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[]{"Create new animation","Load animation","Exit editor"}, 0);
		if(ans==JOptionPane.YES_OPTION){
			createNewAnimation();
		}
		else if(ans==JOptionPane.NO_OPTION){
			loadAnimation();
		}
		else if(ans==JOptionPane.CANCEL_OPTION){
			System.exit(0);
		}
	}
	private void loadAnimation(){
		JFileChooser fc = new JFileChooser("./animations");
		fc.addChoosableFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				return "Shattered World animations (.SWANIM)";
			}
			
			@Override
			public boolean accept(File f) {
				if(f.getName().endsWith(".swanim")) return true;
				else return false;
			}
		});
		fc.setDialogTitle("Open animation");
		
		if(fc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
			try{
			File file = fc.getSelectedFile();
			if(file.getName().endsWith(".swanim")){
				saveFrame();
				framesFolder = file.getParentFile();
				File[] frames = framesFolder.listFiles();
				framesFrame.btns.clear();
				framesFrame.scrollPanel.removeAll();
				for(int i=0;i<frames.length;i++){
					JButton tmp = new JButton("frame"+i);
					tmp.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							saveFrame();
							if(curFrame!=-1){
								framesFrame.btns.get(curFrame).setFont(layersFrame.basicFont);
							}
							curFrame = Integer.parseInt(tmp.getText().substring(5));
							tmp.setFont(layersFrame.selectedFont);
							loadFrame(curFrame);
							framesFrame.deleteFrameButton.setEnabled(true);
						}
					});
					framesFrame.btns.add(tmp);
					framesFrame.scrollPanel.add(tmp);
				}
				framesFrame.scrollPane.revalidate();
				loadFrame(Integer.parseInt(file.getName().substring(5, file.getName().length()-7)));
				framesFrame.btns.get(Integer.parseInt(file.getName().substring(5, file.getName().length()-7))).setFont(layersFrame.selectedFont);
			}
			}
			catch(Exception ex){
				JOptionPane.showMessageDialog(this, "Invalid file", "Error", JOptionPane.ERROR_MESSAGE);
				loadAnimation();
			}
		}
	}
	private void loadLayer(int layerID){
		if(panel.curLayer!=-1){
			layersFrame.btns.get(panel.curLayer).setFont(layersFrame.basicFont);
		}
		layersFrame.btns.get(layerID).setFont(layersFrame.selectedFont);
		panel.curLayer = layerID;
		layersFrame.deleteLayerButton.setEnabled(true);
		layersFrame.renameLayerButton.setEnabled(true);
		layersFrame.upLayerButton.setEnabled(true);
		layersFrame.downLayerButton.setEnabled(true);
		
		Layer layer = panel.layers.get(layerID);
		slidersFrame.sizeSlider.setValue(layer.xsize);
		slidersFrame.widthSlider.setValue(layer.xwidth);
		slidersFrame.heightSlider.setValue(layer.xheight);
		slidersFrame.setVisible(true);
	}
	private void loadFrame(int frameID){
		slidersFrame.setVisible(false);
		curFrame=frameID;
		File frame = new File(framesFolder.getAbsolutePath()+"/frame"+frameID+".swanim");
		panel.curLayer=-1;
		panel.layers.clear();
		layersFrame.newLayerButton.setEnabled(true);
		layersFrame.deleteLayerButton.setEnabled(false);
		layersFrame.renameLayerButton.setEnabled(false);
		layersFrame.upLayerButton.setEnabled(false);
		layersFrame.downLayerButton.setEnabled(false);
		try{
			Scanner in = new Scanner(frame);
			int layersKol = in.nextInt();
			layersFrame.scrollPanel.removeAll();
			layersFrame.btns.clear();
			for(int i=0; i<layersKol; i++){
				Layer layer = new Layer(in.next(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), (double)(in.nextInt())/1000000);
				JButton tmp = new JButton(layer.layerName);
				tmp.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						loadLayer(layersFrame.btns.indexOf(tmp));
					}
				});
				layersFrame.btns.add(tmp);
				layersFrame.scrollPanel.add(tmp);
				panel.layers.add(layer);
			}
			in.close();
			layersFrame.scrollPanel.repaint();
			layersFrame.scrollPane.revalidate();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	private void saveFrame(){
		if(curFrame!=-1){
			try{
				File file = new File(framesFolder.getAbsolutePath()+"/frame"+curFrame+".swanim");
				FileWriter out = new FileWriter(file);
				out.write(panel.layers.size()+"");
				for(int i=0;i<panel.layers.size();i++){
					Layer layer = panel.layers.get(i);
					out.write(" "+layer.imageName+" "+layer.x+" "+layer.y+" "+layer.scaledWidth+" "+layer.scaledHeight+" "+layer.xsize+" "+(int)(layer.rotationAngle*1000000));
				}
				out.flush();
				out.close();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	private String getMoveDirectionFolderName(int moveDirection){
		switch(moveDirection){
		case 0: return moveDirection+" Right";
		case 1: return moveDirection+" UpRight";
		case 2: return moveDirection+" Up";
		case 3: return moveDirection+" UpLeft";
		case 4: return moveDirection+" Left";
		case 5: return moveDirection+" DownLeft";
		case 6: return moveDirection+" Down";
		case 7: return moveDirection+" DownRight";
		}
		return null;
	}
	private String getIsMovingName(int isMoving){
		switch(isMoving){
		case 0: return isMoving+" NoMoving";
		case 1: return isMoving+" Moving";
		}
		return null;
	}
	private String getWeaponFolderName(int weaponID){
		switch(weaponID){
		case 0: return weaponID+" OneHanded";
		case 1: return weaponID+" SwordAndShield";
		case 2: return weaponID+" Dual";
		case 3: return weaponID+" TwoHanded";
		case 4: return weaponID+" Bow";
		case 5: return weaponID+" Staff";
		}
		return null;
	}
	private double sqr(double a){
		return a*a;
	}
}
