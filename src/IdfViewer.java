import processing.opengl.*;

import java.io.*;
import java.util.ArrayList;

import javax.swing.*;

import controlP5.ControlEvent;
import controlP5.ControlP5;
import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics3D;
import processing.core.PImage;
import processing.core.PMatrix3D;
import processing.core.PVector;


//_______________________________________________________________________________________________________________
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////		MAIN CLASS		/////////////////////////////////////////////	

		File[] files;

	PGraphics3D g3;
	PeasyCam cam;
	ControlP5 cp5;
	PFont font;

	JFileChooser fc = new JFileChooser(); 

	
	boolean GUI_OnOff;
	float[] GUI_BackCol = new float[4];

	boolean blnObst = false;			// IDF files loaded?
	boolean blnAssociatedData = false;	// associated Data (fitness, variables, etc) loaded?
	boolean blnstart = false;			//this is just bug fixing for the HUD/GUI
	boolean blnMouseWithinBox = false;

	float [] FaceCols = new float[4];//colors for Face Objects
	float [] FaceCols1 = new float[4];
	float [] FaceCols3 = new float[4];//colors for locked building
	float [] FaceCols4 = new float[4];
	Face [] IDFbuilding;	//geometry from an IDF file
	Face [] IDFwindows;
	PVector[][] Allpts;
	
	
	float Costs1Min, Costs1Max, Costs2Min, Costs2Max;
	float [] VarMin;
	float [] VarMax;
	String M1;
	String M2;

	
	
	boolean bldgSelected = false;
	int intBldgSel;
	
	ArrayList IDFClassArray;// ArrayList(), containing all IDFClass-instances
	IDFClass [] IDFinstances;	//array of IDF-file created as IDFClass-instances... need this to "build" instance
	
	// _______________________________________________________________________________________________________________
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////// SETUP
	// /////////////////////////////////////////////
	public void setup() {
		size(1100, 600, OPENGL);
		cam = new PeasyCam(this, 0, 0, 0, 100);
		perspective(PI / 3, width / (float) height, 0.01f, 1000);
		cp5 = new ControlP5(this);
		g3 = (PGraphics3D)g;
		cp5AddButtons();

		fc.setMultiSelectionEnabled(true);
		
		
		font = createFont("ArialMT", 10, false);
		textFont(font);

		blnstart = true;

		GUI_BackCol[0] = 1;
		GUI_BackCol[1] = 255;
		GUI_BackCol[2] = 255;
		GUI_BackCol[3] = 255;
		// hint(DISABLE_OPENGL_2X_SMOOTH);
		GUI_OnOff = true;

		FaceCols[0] = 255f;
		FaceCols[1] = 0f;
		FaceCols[2] = 0f;
		FaceCols[3] = 200f;
		
		FaceCols1[0] = 255f;
		FaceCols1[1] = 150f;
		FaceCols1[2] = 150f;
		FaceCols1[3] = 200f;
		
		
		FaceCols3[0] = 255f;
		FaceCols3[1] = 0f;
		FaceCols3[2] = 0f;
		FaceCols3[3] = 20f;
		
		FaceCols4[0] = 255f;
		FaceCols4[1] = 150f;
		FaceCols4[2] = 150f;
		FaceCols4[3] = 20f;


			int returnVal=fc.showOpenDialog(frame);

			if (returnVal == JFileChooser.APPROVE_OPTION) { 
				files = fc.getSelectedFiles();	
}
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	// _______________________________________________________________________________________________________________
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////// DRAW
	// /////////////////////////////////////////////
	public void draw() {
		background(GUI_BackCol[1], GUI_BackCol[2], GUI_BackCol[3]);

		fill(255, 0, 0, 120);
		stroke(0);
		strokeWeight(1);



		// drawing 3D buildings
		if (blnObst == true) {
			for (int k=0; k<IDFinstances.length; k++){
				if (blnAssociatedData == false){
					IDFinstances[k].draw();	
				/*} else if (blnAssociatedData == true && blnMouseWithinBox == true){// && blnMouseWithinBox == true){
					//draw only building, which is selected. 
					//hover can change. click will lock/unlock it->unlock allows hover
					//IDFinstances[k].draw();
					if (IDFinstances[k].over()){
						IDFinstances[k].draw();
					} else if(bldgSelected == true){
						IDFinstances[intBldgSel].draw(FaceCols3, FaceCols4);
					}
				} else if (blnAssociatedData == true && blnMouseWithinBox == false && bldgSelected == true){
					IDFinstances[intBldgSel].draw(FaceCols3, FaceCols4);*/
				} else {
					if (blnMouseWithinBox == true){
						if (IDFinstances[k].over()){
							IDFinstances[k].draw();
						} else if(bldgSelected == true){
							IDFinstances[intBldgSel].draw(FaceCols3, FaceCols4);
						}
					} else if (blnMouseWithinBox == false && bldgSelected == true){
						IDFinstances[intBldgSel].draw(FaceCols3, FaceCols4);
					}
				}
			}	
		}
		

	

		if (GUI_OnOff == true) gui();
		else cam.setActive(true);

	}


	
	
//	public void mouseReleased(){
//		if (mouseButton ==CENTER){
//			fill(255,0,0);
//			ellipse(mouseX, mouseY, 100, 100);
//		}
//	}
//	
	
	
	
	
	
	
	
	
	
	
	
	// ///////////////////////////////////////////////////////////////////////////
	// ////////////////////////////			CLASSES			//////////////////////
	// ///////////////////////////////////////////////////////////////////////////
	//FACES
	class Face{
		int corners;
		PVector [] pts;
		float rr, gg, bb, oo;
		float rr2,gg2,bb2,oo2;
		boolean DrawFace;
		
		// Constructor: _pts 		= 	Coordinates; 
		//				cols4 		= 	4 colors r,g,b,opacity; 
		//				_DrawFace 	= 	surface or just lines 
		Face(PVector [] _pts, float [] cols4, boolean _DrawFace){
			DrawFace =_DrawFace;
			rr = cols4[0];
			gg = cols4[1];
			bb = cols4[2];
			oo = cols4[3];
			corners = _pts.length;
			pts = new PVector [corners];
			for(int i=0; i<corners; i++){
				pts[i] = _pts[i];
			}
		}
		
		void draw(){
			stroke(0);
			
			if(DrawFace == true){
				fill(rr, gg, bb ,oo);
				beginShape();
				for (int i=0; i<corners; i++){
					vertex(pts[i].x, pts[i].y, pts[i].z);
				}
				endShape(CLOSE);
			}else{
				noFill();
				beginShape();
				for (int i=0; i<corners; i++){
					vertex(pts[i].x, pts[i].y, pts[i].z);
				}
				endShape(CLOSE);
			}
			fill(0);
//			for (int i=0; i<corners; i++){
//				text(i, pts[i].x, pts[i].y, pts[i].z);
//			}
		}	
		
		void draw(float [] cols4){
			stroke(0);
			rr2 = cols4[0];
			gg2 = cols4[1];
			bb2 = cols4[2];
			oo2 = cols4[3];
			
			if(DrawFace == true){
				fill(rr2, gg2, bb2 ,oo2);
				beginShape();
				for (int i=0; i<corners; i++){
					vertex(pts[i].x, pts[i].y, pts[i].z);
				}
				endShape(CLOSE);
			}else{
				noFill();
				beginShape();
				for (int i=0; i<corners; i++){
					vertex(pts[i].x, pts[i].y, pts[i].z);
				}
				endShape(CLOSE);
			}
			fill(0);
//			for (int i=0; i<corners; i++){
//				text(i, pts[i].x, pts[i].y, pts[i].z);
//			}
		}
	}
	
	
	//IDFObejcts
		class IDFClass{
			String [] strLines;
			Face [] walls;
			Face [] windows;
			Float [] Variables;
			Float [] Costs;
			String FileName;
			int HUDXpos;
			int HUDYpos;
			int [] HUDVar;
			
			// Constructor: 
			IDFClass(String [] _strLines, String _FileName){
				strLines = _strLines;
				loadIDFfile();
				FileName = _FileName;
			}
			
			
			void draw() {
				for (int k=0; k<walls.length; k++){
					walls[k].draw();
				}
				for (int k=0; k<windows.length; k++){
					windows[k].draw();
				}
			}
			
			void draw(float [] cols4a, float [] cols4b) {
				for (int k=0; k<walls.length; k++){
					walls[k].draw(cols4a);
				}
				for (int k=0; k<windows.length; k++){
					windows[k].draw(cols4b);
				}
			}
			
			
			// Test to see if mouse is over the circle at the pareto graph of this idf file
			  boolean over() {
			    float disX = (width-HUDXpos-20) - mouseX;
			    float disY = (HUDYpos+80) - mouseY;
			    if (sqrt(sq(disX) + sq(disY)) < 8/2 ) {
			      return true;
			    } else {
			      return false;
			    }
			  }
			
			
			void loadIDFfile(){
				walls=createFacesFromStringLines("BuildingSurface:Detailed,", FaceCols);
				windows=createFacesFromStringLines("FenestrationSurface:Detailed,", FaceCols1);
			}
			
			
			Face [] createFacesFromStringLines(String IDFObject, float [] _FaceCol){
				Face [] FacesToCreate;
				
				String[] In_strTmp;
				PVector[][] Allpts;
				float [] xyz;
				int intFaces = 0;
				int [] Allpts_vertices;		//this is to identify, how many vertices each Face will have. can be 3, 4, 5... max 10 here
				PVector [] TmpAllpts;
				int vertices;
				
				// finding out, how many times we have this object (wall, window, etc. -> IDFObject) in this IDF file
				for (int i=0; i<strLines.length; i++) {
					if (strLines[i].equals(IDFObject)){
						intFaces+=1;
					}
				}		
				
				Allpts = new PVector[intFaces][10];		// just make more space with 10, even though it might be only 3, 4, ...
				Allpts_vertices = new int [intFaces];	// each Face will have its integer Vertices identifier
				intFaces=0;
				
				for (int i=0; i<strLines.length; i++) {				// go through each line of the IDF file
					if (strLines[i].equals(IDFObject)){				// if the line equals the IDF objcet, we're looking for...
						In_strTmp = splitTokens(strLines[i+10], ",");// check, how many vertices this face has. for windows and buildingsurfaces its always the 10th line
						vertices = (int)Float.valueOf(In_strTmp[0].trim()).floatValue(); //number of vertices of this face
						xyz=new float[0];								// initialize coordinates
						
						// fill coordinates into PVectors and finally define, how many vertices this face has
						if (vertices >= 3 && vertices <=10){		// min 3 vertices, max 10
							Allpts_vertices[intFaces] = vertices;
							xyz = new float [vertices*3];
							for (int u=0; u<xyz.length; u++){
								In_strTmp = splitTokens(strLines[i+11+u], ", ;");
								xyz[u]=Float.valueOf(In_strTmp[0].trim()).floatValue();
							}
							for (int m=0; m<Allpts_vertices[intFaces]; m++){
								Allpts[intFaces][m] = new PVector(xyz[m*3], xyz[m*3+1], xyz[m*3+2]);
							}
							i+=Allpts_vertices[intFaces]*3+10;
							intFaces+=1;
						}
					}
				}
				
				// create Faces
				FacesToCreate = new Face[intFaces];
				for (int k=0; k<FacesToCreate.length; k++){
					if (Allpts_vertices[k] >= 3 && Allpts_vertices[k] <=10){
						TmpAllpts = new PVector [Allpts_vertices[k]];	// only create as many PVectors, as we have vertices
						for (int m=0; m<Allpts_vertices[k]; m++){
							TmpAllpts[m] = Allpts[k][m];				// fill only existing vertices. rest of Allpts[k][m] might be empty
						}
						FacesToCreate[k] = new Face(TmpAllpts, _FaceCol, true);
					}
				}
				return FacesToCreate;
			}
			
			
			
			
		}
		


	
	
	
	
	
	
	
	// _______________________________________________________________________________________________________________
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////// KEY / MOUSE COMMANDS
	// /////////////////////////////////////////////
	public void keyReleased() {
		if (key == ' ') {
			if (GUI_OnOff)
				GUI_OnOff = false;
			else
				GUI_OnOff = true;
		}

		if (key == 'b' || key == 'B') {
			if (GUI_BackCol[0] == 0) {
				GUI_BackCol[0] = 1;
				GUI_BackCol[1] = 255;
				GUI_BackCol[2] = 255;
				GUI_BackCol[3] = 255;
			} else {
				GUI_BackCol[0] = 0;
				GUI_BackCol[1] = 0;
				GUI_BackCol[2] = 0;
				GUI_BackCol[3] = 50;
			}

		}
		

	}


	
	
	
	
	public void mouseClicked(){
		if (blnAssociatedData == true){
			for (int k=0; k<IDFinstances.length; k++){
				if (IDFinstances[k].over() && blnMouseWithinBox == true){		
					bldgSelected = true;
					intBldgSel = k;
				}else if (blnMouseWithinBox == false){
					bldgSelected = false;	
				}
			}			
		}
	}
	
	
	

	
	
	
	
	
	// _______________________________________________________________________________________________________________
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////// GUI
	// /////////////////////////////////////////////
	public void gui() {
		
		cam.beginHUD();
		pushStyle();

		strokeWeight(0.5f); // HUD on right side of window
		line(width - 200, 0, width - 200, height);
		noStroke();
		fill(255, 150);
		rect(width - 200, 0, width, height);

		////////////////////////////////////////////////////
		// HUD, if idf files loaded
		if (blnObst == true) { // showing Name of input FDS File
			fill(255 - GUI_BackCol[1]);
			font = createFont("Calibri light", 11, true);
			textFont(font);
			text("IDF files read:   " + IDFClassArray.size(), 10, 10);
		}
		
		
		////////////////////////////////////////////////////
		// HUD, if associated data loaded

		if (blnAssociatedData == true){
			fill(255 - GUI_BackCol[1]);
			font = createFont("Calibri light", 11, true);
			textFont(font);
			text("associated optimisation data loaded", 10, 22);
			//textAlign(CENTER);
			text("    " + M1, width-190,250);
			text(Costs1Max, width-190,260);
			text(Costs1Min, width-50,260);
			
			pushMatrix();
			translate(width-180, 160);
			rotate(-PI/2);
			translate(-90,0);
			text("    " +M2, 0,0);
			translate(0,-10);
			text(Costs2Max,0,0);
			translate(140,0);
			text(Costs2Min,0,0);
			popMatrix();
			
			noStroke();
			if (mouseX > width-190 && mouseX < width-10 && mouseY < 250 && mouseY > 70) {
				fill(0,0,0,10);
				rect(width - 190, 70, 180, 180);
				blnMouseWithinBox = true;
			} else {
				noFill();
				rect(width - 190, 70, 180, 180);
				blnMouseWithinBox = false;
			}
			
			stroke(255,0,0);
			strokeWeight(1.5f);

			
			for (int k=0; k<IDFinstances.length; k++){
				if (IDFinstances[k].over()){		
					fill(255,150,150);
					ellipse(width-IDFinstances[k].HUDXpos-20,IDFinstances[k].HUDYpos+80 , 8, 8);	
					//IDFinstances[k].draw();
					fill(255,0,0);
					textAlign(RIGHT);
					text("F1: " + IDFinstances[k].Costs[0], mouseX, mouseY+10);
					text("F2: " + IDFinstances[k].Costs[1], mouseX, mouseY+22);
					textAlign(LEFT);
					noFill();

					for (int m=0; m<VarMin.length; m++){
						fill(255,0,0,100);
						rect(width-190, 300+m*15, IDFinstances[k].HUDVar [m], 10);
						fill(0);
						text("x"+(m+1),width-200,310+m*15);
						fill(255,0,0);
//						text(VarMin[m], width-190, 310+m*15);
//						text(VarMax[m], width-30, 310+m*15);
						text(IDFinstances[k].Variables [m], width-190+IDFinstances[k].HUDVar [m], 310+m*15);
					}
					
				}else {
					noFill();
					ellipse(width-IDFinstances[k].HUDXpos-20,IDFinstances[k].HUDYpos+80 , 8, 8);				
				}
	
			}

			if (bldgSelected==true){
				stroke(255,150,150);
				strokeWeight(2.5f);
				fill(255,0,0);
				ellipse(width-IDFinstances[intBldgSel].HUDXpos-20,IDFinstances[intBldgSel].HUDYpos+80 , 8, 8  );
				
				text("current locked File:    " + IDFinstances[intBldgSel].FileName , 10, 34);
				
				
				for (int m=0; m<VarMin.length; m++){
					fill(255,0,0,20);
					rect(width-190, 300+m*15, IDFinstances[intBldgSel].HUDVar [m], 10);
					fill(0);
					text("x"+(m+1),width-200,310+m*15);
					fill(255,0,0);
//					text(VarMin[m], width-190, 310+m*15);
//					text(VarMax[m], width-30, 310+m*15);
					text(IDFinstances[intBldgSel].Variables [m], width-190+IDFinstances[intBldgSel].HUDVar [m], 310+m*15);
				}
			}
		}


		if (GUI_OnOff == true){
			if (mouseX > width-200) cam.setActive(false);
			else cam.setActive(true);			
		}

		cp5.draw();
		popStyle();
		cam.endHUD();
	}

	
	// _______________________________________________________________________________________________________________
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////// GUI-Buttons
	// /////////////////////////////////////////////
	public void cp5AddButtons() {

		cp5.addButton("Load EnergyPlus-File", 10, width - 190, 10, 180, 20).setId(1);

		cp5.addButton("Load associated Data", 10, width - 190, 40, 180, 20).setId(2);
		
		
		PImage[] imgs = { loadImage("ETH-Logo-1.gif"), loadImage("ETH-Logo-2.gif"), loadImage("ETH-Logo-3.gif") };
		cp5.addButton("play").setValue(128).setPosition(width - 180, height - 50).setImages(imgs).updateSize();

		
		
		cp5.setAutoDraw(false);

	}

	
	// _______________________________________________________________________________________________________________
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////// GUI-Buttons-Click
	// /////////////////////////////////////////////
	public void controlEvent(ControlEvent theEvent) throws IOException {
		println(theEvent.getController().getName());

		////////////////////////////////////////////////////
		// Button to Load Website
		// blnstart is bug-fixing... otherwise, buttons are triggered already when starting tool
		if (theEvent.getController().getName() == "play"  && blnstart == true) {		
			String url = "http://www.carmeliet.arch.ethz.ch/Team/Team";
			java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
		}
		
		
		////////////////////////////////////////////////////
		// This one loads the associated data, like variables, objective function values, ...
		String strAssOptData;
		String [] OptDataLines;
		String [] OptDataOneLine;
		

		float xBoxf, yBoxf, xBoxVarf;
		float xBoxStep, yBoxStep;
		float [] xBoxVarStep;
		
		if (theEvent.getController().getName() == "Load associated Data" && blnstart == true) {
			if (blnObst == true){
			
				strAssOptData=selectInput("Select associated optimisation data");
				if (strAssOptData != null) {

				//	float Costs1Min, Costs1Max, Costs2Min, Costs2Max;
					VarMin = new float [5];
					VarMax = new float [5];
					
					OptDataLines = loadStrings(strAssOptData);
					OptDataOneLine=splitTokens(OptDataLines[0], ";");
					M1 = OptDataOneLine[6];
					M2 = OptDataOneLine[7];
					
					for (int i=1; i<OptDataLines.length; i++){
						OptDataOneLine = splitTokens(OptDataLines[i], ";");
//						for (int k=0; k<OptDataOneLine.length; k++){
//							//println(OptDataOneLine[k]);
//							IDFinstances[k]
//						}
						
						for (int k=0; k<IDFinstances.length; k++){
							if (OptDataOneLine[0].equals(IDFinstances[k].FileName)){
								IDFinstances[k].Variables = new Float [5];
								//loading variables into class-instance
								for (int kk=0; kk<IDFinstances[k].Variables .length; kk++){
									IDFinstances[k].Variables [kk]=Float.valueOf(OptDataOneLine[kk+1].trim()).floatValue();
								}
								
								//loading objective function values
								IDFinstances[k].Costs = new Float [2];
								IDFinstances[k].Costs[0] = Float.valueOf(OptDataOneLine[6].trim()).floatValue();
								IDFinstances[k].Costs[1] = Float.valueOf(OptDataOneLine[7].trim()).floatValue();
								
								// checking variable bounds, for HUD visualisation
								// checking cost bounds, for showing idf files in Pareto front graph
								if (k==0){
									Costs1Min =IDFinstances[k].Costs[0];
									Costs1Max =IDFinstances[k].Costs[0];
									Costs2Min =IDFinstances[k].Costs[1];
									Costs2Max =IDFinstances[k].Costs[1];
									for (int kk=0; kk<VarMin.length ; kk++){
										VarMin[kk]= IDFinstances[k].Variables [kk];
										VarMax[kk]= IDFinstances[k].Variables [kk];
									}
								} else {
									if (IDFinstances[k].Costs[0] < Costs1Min) {
										Costs1Min =IDFinstances[k].Costs[0];
									}
									if (IDFinstances[k].Costs[1] < Costs2Min) {
										Costs2Min =IDFinstances[k].Costs[1];
									}
									if (IDFinstances[k].Costs[0] > Costs1Max) {
										Costs1Max =IDFinstances[k].Costs[0];
									}
									if (IDFinstances[k].Costs[1] > Costs2Max) {
										Costs2Max =IDFinstances[k].Costs[1];
									}
									for (int kk=0; kk<VarMin.length ; kk++){
										if (IDFinstances[k].Variables[kk] < VarMin[kk]){
											VarMin[kk]= IDFinstances[k].Variables [kk];										
										}
										if (IDFinstances[k].Variables[kk] > VarMax[kk]){
											VarMax[kk]= IDFinstances[k].Variables [kk];
										}

									}
								}
								
							}
						}
					}
					
					xBoxStep = 160 / (Costs1Max - Costs1Min);
					yBoxStep = 160 / (Costs2Max - Costs2Min);

					xBoxVarStep = new float [VarMin.length];
					for (int m=0; m<VarMin.length; m++){
						xBoxVarStep[m] = 180 / (VarMax[m] - VarMin[m]);
					}
					
					for (int k=0; k<IDFinstances.length; k++){
						xBoxf=(IDFinstances[k].Costs[0] - Costs1Min) * xBoxStep;
						yBoxf=(IDFinstances[k].Costs[1] - Costs2Min) * yBoxStep;
						
						// position translated into pixels
						IDFinstances[k].HUDXpos =(int)xBoxf;
						IDFinstances[k].HUDYpos = (int)yBoxf;

						IDFinstances[k].HUDVar = new int [VarMin.length];
						for (int m=0; m<VarMin.length ; m++){
							xBoxVarf= (IDFinstances[k].Variables [m] - VarMin[m]) * xBoxVarStep[m];
							IDFinstances[k].HUDVar[m] = (int)xBoxVarf;
						}

					}
					
					
					
					blnAssociatedData = true;	
				}
			}		
		}
		
		
		
		////////////////////////////////////////////////////
		// Button to Load IDF File
		// blnstart, same here...otherwise, "load idf-file"-button is triggered when starting tool
		String [] tmpLines;
		int counter = 0;
		String OldFile = "";


		String[] In_strLines;
		String strInputFile;
		String [] FileName;
		
		if (theEvent.getController().getName() == "Load EnergyPlus-File" && blnstart == true) {

			// loading IDF file
			


  
  IDFClassArray = new ArrayList();
				

				for (int m=0; m<files.length; m++){
					
					strInputFile = files[m].getAbsolutePath();
println(strInputFile);
					if (strInputFile != null) {
						FileName = splitTokens(strInputFile, "\\ .");
						OldFile = strInputFile;
						// counting blank lines in the IDF file. ...if I just had redim
						// preserve...
						counter =0;
						tmpLines = loadStrings(strInputFile);
						for (int k = 0; k < tmpLines.length; k++) {
							if (tmpLines[k].trim().length() > 0) {
								counter++;
							}
						}
						In_strLines = new String[counter];

						// getting rid of blank lines in the IDF file and putting only
						// lines with contents into In_strLines
						counter = 0;
						for (int k = 0; k < tmpLines.length; k++) {
							if (tmpLines[k].trim().length() > 0) {
								In_strLines[counter] = tmpLines[k];
								counter++;
							}
						}

						// getting rid of blanks within a line
						for (int i = 0; i < In_strLines.length; i++) {
							In_strLines[i] = In_strLines[i].replaceAll("\\s+", "");
						}

						println("Load: " + strInputFile);

						// loading IDF file into variables. loadIDFfile is a subroutine
						// to create the idf geometry objects
						//loadIDFfile();
					    IDFClassArray.add(new IDFClass(In_strLines, FileName[FileName.length-2]));
					} 
					/*else {
						if (blnObst == true) strInputFile = OldFile;
					}*/
				}
				IDFinstances = new IDFClass[IDFClassArray.size()];
				for (int m=0; m< IDFClassArray.size(); m++){
				    IDFClass test = (IDFClass) IDFClassArray.get(m);				
					//test.draw();			
					IDFinstances [m] = new IDFClass(test.strLines, test.FileName );					
				}
				blnObst = true;
			   
			
			
			
		}
	}

	
	
	
	
	

