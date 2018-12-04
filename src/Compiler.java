import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

public class Compiler {

	static class Element {
		float x, y, width, height, angle;
		String name;
		Element(float x, float y, float width, float height, float angle, String name) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.angle = angle;
			this.name = name;
		}
	}
	public static void main(String[] args) {
		File animationsFolder = new File("./animations");
		File file = new File("./HumanoidDrawer.kt");
		if(!file.exists()){
			try{
				file.createNewFile();
			}
			catch(Exception ex){ex.printStackTrace();}
		}
		String lineSeparator = System.getProperty("line.separator");
		try{
			FileWriter out = new FileWriter(file);
			out.write("package com.mirage.view.scene.objects.humanoid\n" +
					"\n" +
					"import com.badlogic.gdx.Gdx\n" +
					"import com.badlogic.gdx.graphics.Texture\n" +
					"import com.badlogic.gdx.graphics.g2d.SpriteBatch\n" +
					"import com.mirage.view.scene.objects.AnimatedObjectDrawer\n" +
					"import java.util.HashMap\n" +
					"\n" +
					"\n" +
					"/**\n" +
					" * √енерируемый с помощью Animation Editor-а класс дл€ работы со скелетной анимацией гуманоидов\n" +
					" * (анимаци€ остальных существ задаетс€ покадрово классом SpriteAnimation)\n" +
					" */\n" +
					"class HumanoidDrawer : AnimatedObjectDrawer {\n" +
					"    /**\n" +
					"     * ƒействие, которое анимируетс€ (ожидание, бег, атака и т.д.)\n" +
					"     */\n" +
					"    var action: Action = Action.IDLE\n" +
					"    /**\n" +
					"     * Ќаправление движени€\n" +
					"     */\n" +
					"    var moveDirection: MoveDirection = MoveDirection.RIGHT\n" +
					"    /**\n" +
					"     * “ип оружи€ гуманоида (одноручное, двуручное, два одноручных, одноручное и щит, лук и т.д.)\n" +
					"     */\n" +
					"    var weaponType: WeaponType = WeaponType.ONE_HANDED\n" +
					"\n" +
					"    /**\n" +
					"     * —ловарь из текстур экипировки данного гуманоида\n" +
					"     * ƒолжен содержать ключи head[0-7], body, handtop, handbottom, legtop, legbottom, cloak, weapon1, weapon2\n" +
					"     */\n" +
					"    var textures: MutableMap<String, AnimatedTexture>\n" +
					"\n" +
					"    constructor() {\n" +
					"        textures = HashMap()\n" +
					"        for (i in 0..0) { // TODO помен€ть 0 на 7\n" +
					"            textures[\"head$i\"] = StaticTexture(Texture(Gdx.files.internal(\"android/assets/equipment/head/0000$i.png\")))\n" +
					"        }\n" +
					"        textures[\"body\"] = StaticTexture(Texture(Gdx.files.internal(\"android/assets/equipment/body/0000.png\")))\n" +
					"        textures[\"handtop\"] = StaticTexture(Texture(Gdx.files.internal(\"android/assets/equipment/handtop/0000.png\")))\n" +
					"        textures[\"handbottom\"] = StaticTexture(Texture(Gdx.files.internal(\"android/assets/equipment/handbottom/0000.png\")))\n" +
					"        textures[\"legtop\"] = StaticTexture(Texture(Gdx.files.internal(\"android/assets/equipment/legtop/0000.png\")))\n" +
					"        textures[\"legbottom\"] = StaticTexture(Texture(Gdx.files.internal(\"android/assets/equipment/legbottom/0000.png\")))\n" +
					"        textures[\"cloak\"] = StaticTexture(Texture(Gdx.files.internal(\"android/assets/equipment/cloak/0000.png\")))\n" +
					"        textures[\"weapon1\"] = StaticTexture(Texture(Gdx.files.internal(\"android/assets/equipment/twohanded/0000.png\")))\n" +
					"        textures[\"weapon2\"] = StaticTexture(Texture(Gdx.files.internal(\"android/assets/equipment/onehanded/0000.png\")))\n" +
					"    }\n" +
					"\n" +
					"    constructor(textures: MutableMap<String, AnimatedTexture>) {\n" +
					"        this.textures = textures\n" +
					"    }\n" +
					"\n" +
					"    constructor(textures: MutableMap<String, AnimatedTexture>, action: Action, moveDirection: MoveDirection, weaponType: WeaponType) {\n" +
					"        this.textures = textures\n" +
					"        this.action = action\n" +
					"        this.moveDirection = moveDirection\n" +
					"        this.weaponType = weaponType\n" +
					"    }\n" +
					"\n\n" +
					"    fun curValue(startValue: Float, endValue : Float, progress : Float) : Float {\n" +
					"        return startValue + (endValue - startValue) * progress / 1000f\n" +
					"    }" +lineSeparator +

					"    override fun draw(batch: SpriteBatch, x: Float, y: Float, timePassedSinceStart: Long) {" + lineSeparator +
					"        var timePassed = 0f + timePassedSinceStart % 1000" + lineSeparator);

			out.write("when (action) {"+lineSeparator);


			for(int animationID = 0; animationID < animationsFolder.list().length; ++animationID){
				String tmp1 = animationsFolder.list()[animationID];
				Scanner tmp11 = new Scanner(tmp1);
				tmp11.nextInt();
				String action = tmp11.next();
				out.write("Action." + action + " -> "+lineSeparator);
				out.write("when (moveDirection) {"+lineSeparator);
				File moveDirectionsFolder = findFile(animationsFolder, animationID);


				for(int moveDirectionID = 0; moveDirectionID < moveDirectionsFolder.list().length; ++moveDirectionID){
					String tmp2 = moveDirectionsFolder.list()[moveDirectionID];
					Scanner tmp21 = new Scanner(tmp2);
					String moveDirection = getMoveDirection(tmp21.nextInt());
					out.write("MoveDirection." + moveDirection + " -> "+lineSeparator);
					File weaponsFolder = findFile(moveDirectionsFolder, moveDirectionID);
						out.write("when (weaponType) {"+lineSeparator);


						for(int weaponTypeID=0; weaponTypeID < weaponsFolder.list().length; ++weaponTypeID){
							String tmp3 = weaponsFolder.list()[weaponTypeID];
							Scanner tmp31 = new Scanner(tmp3);
							String weaponType = getWeaponType(tmp31.nextInt());
							out.write("WeaponType."+weaponType+" -> "+lineSeparator + "{" + lineSeparator);


							// FRAME LOGIC START


							File framesFolder = findFile(weaponsFolder, weaponTypeID);
							File[] frames = framesFolder.listFiles();

							ArrayList<ArrayList<Element>> arr = new ArrayList<>();

							for(int curFrame = 0; curFrame < frames.length; ++curFrame){
								File frame = new File(framesFolder.getAbsolutePath()+"/frame"+curFrame+".swanim");
								Scanner in = new Scanner(frame);
								int layersKol = in.nextInt();
								if (curFrame == 0) {
									for (int wtf = 0; wtf < frames.length; ++wtf) {
										arr.add(new ArrayList<>());
									}
								}
								for(int curLayer = 0; curLayer < layersKol; ++curLayer){
									System.out.println(
											action + " " + moveDirection + " " + weaponType + " " + curFrame + " " +curLayer
									);
									String imageName = in.next();
									imageName = imageName.substring(0, imageName.length()-4);
									int x = in.nextInt();
									int y = in.nextInt();
									int width = in.nextInt();
									int height = in.nextInt();
									in.nextInt();//ѕропускаем увеличение размера
									float angle = (float)(Math.toDegrees(((double)in.nextInt())/1000000));

									System.out.println("x = " + x + ", y = " + y + ", width = " + width + ", height = " + height + ", angle = " + angle);

									if(isBodyPart(imageName)){
										if(imageName.startsWith("head")){
											imageName=imageName.substring(0, 5);
										}
										else if(imageName.startsWith("body")){
											imageName="body";
										}
										else if(imageName.startsWith("cloak")){
											imageName="cloak";
										}
										else if(imageName.startsWith("neck")){
											imageName="neck";
										}
										else if(imageName.startsWith("legtop")){
											imageName="legtop";
										}
										else if(imageName.startsWith("legbottom")){
											imageName="legbottom";
										}
										else if(imageName.startsWith("handtop")){
											imageName="handtop";
										}
										else if(imageName.startsWith("handbottom")){
											imageName="handbottom";
										}
										else if(imageName.equals("onehandedright")|imageName.equals("twohanded")|imageName.equals("bow")|imageName.equals("staff")|imageName.startsWith("weapon1")){
											imageName="weapon1";
										}
										else if(imageName.equals("onehandedleft")|imageName.equals("shield")|imageName.startsWith("weapon2")){
											imageName="weapon2";
										}
									}
									arr.get(curFrame).add(new Element(x, y, width, height, angle, imageName));
								}
								in.close();
							}
							int framesKol = arr.size();
							int period = 1000;
							int interval = framesKol < 2 ? 0 : period / (framesKol - 1) + 1;
							for (int i = 0; i < framesKol-1; ++i) {
								ArrayList<Element> frame = arr.get(i);
								int layersKol = frame.size();
								out.write((i == 0 ? "" : "else ") + "if (timePassed < " + (interval * (i+1)) + ")" +
										lineSeparator + "{" + lineSeparator);
								for (int j = 0; j < layersKol; ++j) {
									Element cur = frame.get(j);
									Element next = arr.get(i+1).get(j);
									out.write("batch.draw(textures[\"" + cur.name +"\"]!!.getTexture(timePassedSinceStart)," +
											"x + curValue("+cur.x+"f, "+next.x+"f, timePassed)," +
											"y + curValue("+cur.y+"f, "+next.y+"f, timePassed)," +
											"curValue("+cur.width+"f, "+next.width+"f, timePassed)/2," +
											"curValue("+cur.height+"f, "+next.height+"f,timePassed)/2," +
											"curValue("+cur.width+"f, "+next.width+"f, timePassed)," +
											"curValue("+cur.height+"f, "+next.height+"f, timePassed)," +
											"1f, 1f," +
											"curValue("+cur.angle+"f, "+next.angle+"f, timePassed)," +
											"0, 0," +
											"DefaultSizes.defaultWidth[\""+cur.name+"\"]!!, " +
											"DefaultSizes.defaultHeight[\""+cur.name+"\"]!!, false, false)");
								}
								out.write(lineSeparator + "}" + lineSeparator);
							}

							// FRAME LOGIC END


							out.write("}"+lineSeparator);
						}
						out.write("}"+lineSeparator);
				}
				out.write("}"+lineSeparator);
			}
			out.write("}"+lineSeparator);//«акрываем switch(animationID)




			out.write("    }\n" +
					"}");
			out.close();
			
			StringSelection ss = new StringSelection(new String(Files.readAllBytes(file.toPath())));
		    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
			JOptionPane.showMessageDialog(null, "Compilation completed!\nThe code has been copied to clipboard.", "Shattered World Animation Compiler", JOptionPane.INFORMATION_MESSAGE);
			
			
		}
		catch(Exception ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "ERROR", "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	private static float curValue(float startValue, float endValue, float progress) {
		return startValue + (endValue - startValue) * progress;
	}

	private static boolean isBodyPart(String layerName){
		return (layerName.equals("neck")|layerName.equals("handtop")|layerName.equals("handbottom")|layerName.equals("cloak")|
				layerName.startsWith("onehanded")|layerName.equals("twohanded")|layerName.equals("shield")|layerName.equals("bow")|layerName.equals("staff")|
				layerName.startsWith("head")|layerName.startsWith("body"));
	}

	private static String getMoveDirection(int i) {
		switch (i) {
			case 0 : return "RIGHT";
			case 1 : return "UP_RIGHT";
			case 2 : return "UP";
			case 3 : return "UP_LEFT";
			case 4 : return "LEFT";
			case 5 : return "DOWN_LEFT";
			case 6 : return "DOWN";
			case 7 : return "DOWN_RIGHT";
		}
		return null;
	}

	private static String getWeaponType(int i) {
		switch(i){
			case 0: return "ONE_HANDED";
			case 1: return "ONE_HANDED_AND_SHIELD";
			case 2: return "DUAL";
			case 3: return "TWO_HANDED";
			case 4: return "BOW";
			case 5: return "STAFF";
		}
		return null;
	}
	private static File findFile(File folder, int index){
		File[] files = folder.listFiles();
		for(int i=0;i<files.length;i++){
			if(files[i].getName().startsWith(index+" ")){
				return files[i];
			}
		}
		System.out.println(index+" "+folder.getAbsolutePath());
		return null;
	}
	
}
