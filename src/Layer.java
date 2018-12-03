import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Layer {
	final static int NO_TYPE=0,
			TYPE_HEAD=1,
			TYPE_NECK=2,
			TYPE_BODY=3,
			TYPE_HAND_TOP=4,
			TYPE_HAND_BOTTOM=5,
			TYPE_LEG_TOP=6,
			TYPE_LEG_BOTTOM=7,
			TYPE_WEAPON=8;
	int x,y,scaledWidth,scaledHeight,basicWidth,basicHeight,xsize,xwidth,xheight;
	double rotationAngle;
	BufferedImage basicImage;
	String imageName,layerName;
	public Layer(String imageName, int x, int y, int width, int height, int xsize, double angle){
		this.x=x;
		this.y=y;
		this.rotationAngle=angle;
		this.imageName=imageName;
		this.layerName=this.imageName.substring(0,this.imageName.length());
		try{
			this.basicImage=ImageIO.read(new File("./drawable/"+imageName));
			this.basicWidth=this.basicImage.getWidth();
			this.basicHeight=this.basicImage.getHeight();
			this.scaledWidth=width;
			this.scaledHeight=height;
			this.xsize=xsize;
			this.xwidth=this.scaledWidth*100/this.xsize*100/this.basicWidth;
			this.xheight=this.scaledWidth*100/this.xsize*100/this.basicWidth;
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public Layer(String imageName){
		this.x=0;
		this.y=0;
		this.rotationAngle=0;
		this.imageName=imageName;
		this.layerName=this.imageName;
		try{
			this.basicImage=ImageIO.read(new File("./drawable/"+imageName));
			this.basicWidth=this.basicImage.getWidth();
			this.basicHeight=this.basicImage.getHeight();
			this.scaledWidth=this.basicImage.getWidth();
			this.scaledHeight=this.basicImage.getHeight();
			this.xsize=100;
			this.xwidth=100;
			this.xheight=100;
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	public void updateSize(){
		scaledWidth = basicWidth*xsize*xwidth/10000;
		scaledHeight = basicHeight*xsize*xheight/10000;
	}
}
