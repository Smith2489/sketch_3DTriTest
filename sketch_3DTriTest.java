import java.util.*;
import Actions.BufferActions.*;
import Actions.ObjectActions.*;
import Maths.LinearAlgebra.*;
import Renderer.ModelDataHandler.*;
import Renderer.Objects.SceneEntities.*;
import Renderer.ScreenDraw.*;
import Renderer.Objects.Physics.*;

import Wrapper.*;


import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

public class sketch_3DTriTest extends PApplet{;
  //SCREEN DIMENSIONS
  private final int WIDTH = 480;
  private final int HEIGHT = 640;
  private final int[][] RESOLUTIONS = {{WIDTH >>> 3, WIDTH >>> 2, WIDTH >>> 1, WIDTH, WIDTH >>> 4}, 
                                       {HEIGHT >>> 3, HEIGHT >>> 2, HEIGHT >>> 1, HEIGHT, HEIGHT >>> 4}};
  private final float THREE_FOUR = 0.75f;
  private int QUARTER_WIDTH = RESOLUTIONS[0][3] >>> 2;
  private int QUARTER_HEIGHT = RESOLUTIONS[1][3] >>> 2;

  private int BOX_SIZE = RESOLUTIONS[0][3]*50/600;
  private float  DEGS_TO_RADS = (float)Math.PI/180f;
  private final String PATH_BASE = "Data/";

  private Model[] testModels = new Model[15];
  private LinkedList<Model> testModelLinked;
  private Geometry[] testGeometry = new Geometry[9];//0 = cube, 1 = Tetrahedron, 2 = Pyramid, 3 = rhomohedron, 4 = rotating tri pair, 5 = giant tris in back, 6 = test billboard, 7 = cute flag
  private ModelColours[] testPallets = new ModelColours[11]; //0 = b&w cube, 1-8 above
  private Model[] smallerSet = new Model[9];
  private LinkedList<Model> linkedSmallerSet;

  private Triangle[] tris = new Triangle[10];
  private Dot[] testDot = new Dot[2];
  private LinkedList<Dot> testDotLinked;
  private LineObj[] testLines = new LineObj[4];
  private LinkedList<LineObj> testLineLinked;

  private Light[] light = new Light[3];
  private Light secondLight;

  private float[][] endPoints1 = {{0, 1.25f, -2.5f}, {0, -1.25f, 2.5f}, {2, -1.25f, 2.5f}};
  private float[][] endPoints2 = {{-5, 0, 2}, {5, 0, 2}};
  //Cameras
  private Camera eye = new Camera();
  private Camera eye2 = new Camera();

  private int resolutionIndex = 3;
  private boolean bPressed = false;

  private int[] testBack = {0xFF00FFFF, 0xFF00FFFF, 0xFF00FFFF, 0xFF00FFFF, 0xFF00FFFF, 0xFF00FFFF, 0xFF00FFFF, 0xFF00FFFF, 0xFF00FFFF, 0xFF00FFFF,
                            0xFF0000FF, 0xFF0000FF, 0xFF0000FF, 0xFF0000FF, 0xFF0000FF, 0xFF0000FF, 0xFF0000FF, 0xFF0000FF, 0xFF0000FF, 0xFF0000FF,
                            0xFFFF0000, 0xFFFF0000, 0xFFFF0000, 0xFFFF0000, 0xFFFF0000, 0xFFFF0000, 0xFFFF0000, 0xFFFF0000, 0xFFFF0000, 0xFFFF0000,
                            0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
                            0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000,
                            0xFF00FFFF, 0xFF00FFFF, 0xFF00FFFF, 0xFF00FFFF, 0xFF00FFFF, 0xFF00FFFF, 0xFF00FFFF, 0xFF00FFFF, 0xFF00FFFF, 0xFF00FFFF,
                            0xFF0000FF, 0xFF0000FF, 0xFF0000FF, 0xFF0000FF, 0xFF0000FF, 0xFF0000FF, 0xFF0000FF, 0xFF0000FF, 0xFF0000FF, 0xFF0000FF,
                            0xFFFF0000, 0xFFFF0000, 0xFFFF0000, 0xFFFF0000, 0xFFFF0000, 0xFFFF0000, 0xFFFF0000, 0xFFFF0000, 0xFFFF0000, 0xFFFF0000,
                            0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
                            0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000};

  private int[][] backgrounds = {testBack, new int[RESOLUTIONS[0][3]*RESOLUTIONS[1][3]]};
  private int[] screen2 = new int[(RESOLUTIONS[0][3] >>> 2)*(RESOLUTIONS[1][3] >>> 2)];
  private PImage[] testImages = new PImage[2];
  private float sceneMag = 0;
  private float[] sceneCentre = {0, 0, 0};
  private byte[] testStencil = new byte[RESOLUTIONS[0][3]*RESOLUTIONS[1][3]];
  private PGraphics output;
  private BillboardImg[] sprites = new BillboardImg[2];
  private Billboard[] test = new Billboard[2];
  private LinkedList<Billboard> testBillboardLinked;
  private final int[] MAROON = {Colour.MAROON, Colour.RED, Colour.BLACK};
  private final int[] BLACK = {0x5500};
  private final int[] OPAQUE_BLACK = {Colour.BLACK};
  private int[][] endPoints = {{0, 1}};
  private int[][] endPointsSet2 = {{0, 1}, {1, 2}, {2, 0}};
  private int[][] endPointsSet3 = {{0, 1}, {1, 2}, {2, 3}, {3, 0}, {4, 5}, {5, 6}, {6, 7}, {7, 4}, {0, 4}, {1, 5}, {2, 6}, {3, 7}};
  private LinkedList<Light> lightsPrimary = new LinkedList<Light>();
  private LinkedList<Light> lightsSecondary = new LinkedList<Light>();


  public void setup(){
    //LoadModelFile.disableMessages();
    eye.addAction(new MoveCamera());
    eye.setDrawDistance(1800);
    eye.colour(0x55AAFF);
    eye2.setDrawDistance(1800);
    eye2.addAction(new ManageSecondCamera(eye));
    eye2.alwaysPerform(true);
    light[0] = new Light(0, -50, -25);
    light[0].setLightColour(0x55AAFF, (byte)0);
    light[0].setLightColour(0x55, (byte)1);
    light[0].setLightColour(0xFF00FF, (byte)2);
    light[0].setAmbientIntensity(0.03f);
    light[0].setDiffuseIntensity(4000);
    light[0].setSpecularIntensity(16000);
    light[0].addAction(new MoveLight());
    light[0].setType('p');
    light[1] = new Light(10, 0, 40);
    light[1].setAmbientIntensity(0.03f);
    light[1].setDiffuseIntensity(1000);
    light[1].setSpecularIntensity(2000);
    light[1].setLightColour(0x00BBBB);
    light[1].setInnerSpread(45);
    light[1].setOuterSpread(90);
    light[1].setType('s');
    light[1].addAction(new RotateLight());
    light[1].addAction(new ColonThree());
    light[2] = new Light(100, 0, 1500);
    light[2].setAmbientIntensity(0);
    light[2].setDiffuseIntensity(10000000);
    light[2].setSpecularIntensity(20000000);
    light[2].setType('s');
    light[2].setInnerSpread(45);
    light[2].setOuterSpread(90);
    secondLight = new Light();
    secondLight.copy(light[0]);
    secondLight.setLightColour(0xFF);
    secondLight.setDiffuseIntensity(4000);
    secondLight.setSpecularIntensity(16000);
    secondLight.addAction(new CopyLight(light[0]));
    secondLight.alwaysPerform(true);
    for(int i = 0; i < light.length; i++)
      lightsPrimary.add(light[i]);
    lightsSecondary.add(secondLight);
    lightsSecondary.add(light[1]);
    testGeometry[0] = LoadModelFile.loadGeometry(PATH_BASE+"MODELS/cube.txt");
    ModelVertices testVertices = testGeometry[0].returnModelVerticesPtr();
    testLines[0] = new LineObj(endPoints1, endPointsSet2, MAROON);
    testLines[0].setPosition(-15, 0, 7);
    testLines[0].addAction(new SetTransparency(eye));
    testLines[1] = new LineObj(endPoints2, endPoints, BLACK);
    testLines[1].setPosition(-4, -5, 10);
    testLines[1].setRotation(0, -25, 0);
    testLines[1].addAction(new SetTransparency(eye));
    LineModel testModel = new LineModel(testVertices, endPointsSet3, OPAQUE_BLACK);
    testLines[2] = new LineObj(testModel);
    testLines[2].setPosition(15, 0, 10);
    testLines[2].addAction(new RotateAtFiveDegrees(false));
    testLines[2].addAction(new SetTransparency(eye));
    
    testLines[3] = new LineObj(testModel);
    testLines[3].setPosition(-30, 0, -10);
    testLines[3].setScale(2, 2, 2);
    testLines[3].addAction(new SetTransparency(eye));
    testDot[0] = new Dot();
    testDot[0].setPosition(0, 2, 30);
    testDot[0].addAction(new SetTransparency(eye));
    float[] dotPos = {-4, -5, 10};
    testDot[1] = new Dot(dotPos, 0xAA44BB89);
    testDot[1].addAction(new SetTransparency(eye));
    output = createGraphics(RESOLUTIONS[0][3], RESOLUTIONS[1][3]);
    
    sprites[0] = new BillboardImg(PATH_BASE+"billboards/testImage.png");
    sprites[1] = new BillboardImg(PATH_BASE+"billboards/testImage2.png");
    sprites[1].setInvisColour(0x00FF00, 0x00FF00);
    test[0] = new Billboard(sprites[0], 0x9CFF);
    test[0].setShininess(1);
    test[0].addAction(new SetTransparency(eye));
    //test[0].setAttachedToCamera(true);
    test[0].setPosition(-4, -6, 10);
    test[0].setScale(0.04f);
    test[1] = new Billboard(sprites[1]);
    test[1].setPosition(-2, -5, 12);
    test[1].fill(0x72);
    test[1].setScale(-0.08f, 0.06f);
    test[1].setShininess(1);
    test[1].addAction(new SetTransparency(eye));
    noCursor();
    frameRate(30);
    //for(int i = 0; i < tris.length; i++)
    //  tris[i] = new Triangle((int)(Math.random()*610)-10, (int)(Math.random()*790)+10, (int)(Math.random()*30)+10, (int)(Math.random()*610)-10, (int)(Math.random()*790)+10, (int)(Math.random()*30)+10, (int)(Math.random()*610)-10, (int)(Math.random()*790)+10, (int)(Math.random()*30)+10, (int)(Math.random()*16777215), (int)(Math.random()*16777215), true, true);
    //Test meshes
    testPallets[0] = new ModelColours();
    int[][] black = {{Colour.BLACK, Colour.BLACK}};

    testPallets[1] = LoadModelFile.loadPallet(PATH_BASE+"MODELS/cube.txt");
    testGeometry[1] = LoadModelFile.loadGeometry(PATH_BASE+"MODELS/testModel.txt");
    testPallets[2] = LoadModelFile.loadPallet(PATH_BASE+"MODELS/testModel.txt");
    testGeometry[2] = LoadModelFile.loadGeometry(PATH_BASE+"MODELS/pyramid.txt");
    testPallets[3] = LoadModelFile.loadPallet(PATH_BASE+"MODELS/pyramid.txt");
    testGeometry[3] = LoadModelFile.loadGeometry(PATH_BASE+"MODELS/rhombohedron.MODEL");
    testPallets[4] = LoadModelFile.loadPallet(PATH_BASE+"MODELS/rhombohedron.MODEL");
    testGeometry[4] = LoadModelFile.loadGeometry(PATH_BASE+"MODELS/TestTris.txt");
    testPallets[5] = LoadModelFile.loadPallet(PATH_BASE+"MODELS/TestTris.txt");
    testGeometry[5] = LoadModelFile.loadGeometry(PATH_BASE+"MODELS/firstTris.txt");
    testPallets[6] = LoadModelFile.loadPallet(PATH_BASE+"MODELS/firstTris.txt");
    testGeometry[6] = LoadModelFile.loadGeometry(PATH_BASE+"MODELS/TestBillBoard.txt");
    testPallets[7] = LoadModelFile.loadPallet(PATH_BASE+"MODELS/TestBillBoard.txt");
    testGeometry[7] = LoadModelFile.loadGeometry(PATH_BASE+"MODELS/flag.txt");
    testPallets[8] = LoadModelFile.loadPallet(PATH_BASE+"MODELS/flag.txt");
    testPallets[9] = new ModelColours(black, (float[][])null, 12, 8);
    testPallets[10] = LoadModelFile.loadPallet(PATH_BASE+"MODELS/slab.txt");
    testGeometry[8] = LoadModelFile.loadGeometry(PATH_BASE+"MODELS/slab.txt");
    //Test models
    
    //Giant mess in the background
    testModels[0] = new Model(testGeometry[5], testPallets[6]);
    testModels[0].setPosition(-100, 0, 1500);
    testModels[0].setShininess(10000);
    testModels[0].addAction(new RotateMess());
    
    //Tetrahedron
    testModels[1] = new Model(testGeometry[1], testPallets[2]);
    testModels[1].setPosition(0, 0, 8);
    testModels[1].setScale(2, 2, 2);
    testModels[1].addAction(new RotateTetrahedron());
    //testModels[1].setAttachedToCamera(true);
    
    //Tris that are copied from Computer Graphics homework from Spring 2024 semester
    testModels[2] = new Model(testGeometry[4], testPallets[5]);
    testModels[2].setPosition(-2, -4.5f, 10);
    testModels[2].addAction(new SpinTwoTriangles());
    
    //The long rectangular prism
    testModels[3] = new Model(testGeometry[0], testPallets[1]);
    testModels[3].setPosition(0.5f, 3, 9);
    testModels[3].setScale(1, 0.5f, 4);
    testModels[3].setShininess(100);
    testModels[3].addAction(new RotateLongModel());
    testModels[3].setGauroud(false);
    //testModels[3].addAction(new ColonThree());
    //testModels[3].addAction(new Action());
    
    //Scale (1, 1, 1) cube
    testModels[4] = new Model(testGeometry[0], testPallets[1]);
    testModels[4].setPosition(-0.35f, -5.5f, 8);
    testModels[4].setRotation(65, -20, 180);
    testModels[4].setGauroud(false);
    testModels[4].setFizzelParameters(5, 3);
    
    //Black-and-white cube
    testModels[5] = new Model(testGeometry[0], testPallets[0]);
    testModels[5].setPosition(1, 0, 18);
    testModels[5].addAction(new RotateDefaultModel());
    testModels[5].setStencilAction(new ChangeStencil());

    
    //Bowser platform
    testModels[6] = new Model(testGeometry[0], testPallets[1]);
    testModels[6].setPosition(0.5f, 3, 12);
    testModels[6].setScale(2, 0.5f, 2);
    testModels[6].addAction(new RotateAtFiveDegrees(true));
    testModels[6].setGauroud(false);
    
    //Half-green, half-translucent black billboard
    testModels[7] = new Model(testGeometry[6], testPallets[7]);
    testModels[7].setPosition(0, 0, 5);
    testModels[7].setScale(0.5f, 0.5f, 1);
    testModels[7].addAction(new RotateBillboard());
    testModels[7].setShininess(0.25f);

    //testModels[7].setBrightness(0.25);
    
    //Pyramid
    testModels[8] = new Model(testGeometry[2], testPallets[3]);
    testModels[8].setPosition(-4, 5, 25);
    testModels[8].setRotation(0, 90, 0);
    testModels[8].setScale(2.5f, 2.5f, 2.5f);
    testModels[8].addAction(new RotateAtFiveDegrees(true));

    //Rhombohedron
    testModels[9] = new Model(testGeometry[3], testPallets[4]);
    testModels[9].setPosition(6, 0, 15);
    testModels[9].setScale(1.5f, 1.5f, 1.5f);
    testModels[9].addAction(new RotateRhombohedron());
    testModels[9].setParentTransform(testModels[5]);
    testModels[7].setParentTransform(testModels[5]);
    //Flag
    testModels[10] = new Model(testGeometry[7], testPallets[8]);
    testModels[10].setPosition(0, 0, -0.5f);
    //testModels[10].setBrightness(0.1);
    testModels[10].setShininess(0.001f);
    testModels[10].setParentTransform(testModels[10]);
    //testModels[10].setGauroud(false);
    
    //Long rectangular prism inverted hull
    testModels[11] = new Model(testGeometry[0], testPallets[9]);
    testModels[11].setScale(1.1f, 0.6f, 4.1f);
    testModels[11].setInverted(true);
    testModels[11].setParentTransform(testModels[3]);
    
    //Scale (1,1,1) cube inverted hull
    testModels[12] = new Model(testGeometry[0], testPallets[9]);
    testModels[12].setPosition(-0.35f, -5.5f, 8);
    testModels[12].setRotation(65, -20, 180);
    testModels[12].setScale(1.1f, 1.1f, 1.1f);
    testModels[12].setInverted(true);
    
    //Bowser platform
    testModels[13] = new Model(testGeometry[0], testPallets[9]);
    testModels[13].setScale(2.15f, 0.65f, 2.15f);
    testModels[13].setInverted(true);
    testModels[13].setShininess(0);
    testModels[13].setParentTransform(testModels[6]);
    
    //Mario 64 slab
    testModels[14] = new Model(testGeometry[8], testPallets[10]);
    testModels[14].setPosition(7, -2, 10);
    testModels[14].setScale(2.5f, 2.5f, 2.5f);
    //testModels[14].setGauroud(false);
    testModels[14].setShininess(10);
    testModels[14].addAction(new SpinSlab());
    
    for(int i = 0; i < testModels.length; i++){
      testModels[i].addAction(new SetTransparency(eye)); 
    }
    //testModels[1].setFlags(false, true, true, false, false);
    //testModels[1].setAttachedToCamera(true);
    float[] min = {Float.intBitsToFloat(-1), Float.intBitsToFloat(-1), Float.intBitsToFloat(-1)};
    float[] max = {Float.intBitsToFloat(-1), Float.intBitsToFloat(-1), Float.intBitsToFloat(-1)};
    for(int i = 0; i < smallerSet.length; i++){
      smallerSet[i] = testModels[i+1];
      for(byte j = 0; j < 3; j++){
        sceneCentre[j]+=smallerSet[i].returnPosition()[j];
        if(Float.isNaN(min[j]))
          min[j] = smallerSet[i].returnPosition()[j];
        else
          min[j] = Math.min(smallerSet[i].returnPosition()[j], min[j]);
        if(Float.isNaN(max[j]))
          max[j] = smallerSet[i].returnPosition()[j];
        else
          max[j] = Math.max(smallerSet[i].returnPosition()[j], max[j]);
      }
    }
    testModelLinked = arrayToLinkedList(testModels);
    linkedSmallerSet = arrayToLinkedList(smallerSet);
    testBillboardLinked = arrayToLinkedList(test);
    float[] tempCentre = {(max[0]-min[0]), (max[1]-min[1]), (max[2]-min[2])};
    sceneMag = VectorOperations.vectorMagnitude(tempCentre)*0.65f;
    sceneCentre[0]/=smallerSet.length;
    sceneCentre[1]/=smallerSet.length;
    sceneCentre[2]/=smallerSet.length;
    if(Math.abs((float)width/height - THREE_FOUR) <= 0.00001)
      testImages[1] = loadImage(PATH_BASE+"backgrounds/testImage.png");
    else
      testImages[1] = loadImage(PATH_BASE+"backgrounds/testImage2.png");

    testImages[0] = new PImage(RESOLUTIONS[0][3], RESOLUTIONS[1][3]);
    testImages[0].loadPixels();
    for(int i = 0; i < RESOLUTIONS[0][3]*RESOLUTIONS[1][3]; i++)
      testImages[0].pixels[i] = testBack[i%testBack.length];
    testImages[0].updatePixels();
    //Depth buffer initialization
    Rasterizer.initBuffers(RESOLUTIONS[0][3], RESOLUTIONS[1][3]);
    Rasterizer.background(0x777777);
    testDotLinked = arrayToLinkedList(testDot);
    testLineLinked = arrayToLinkedList(testLines);
    ScreenMake.setDotList(testDotLinked);
    ScreenMake.usePersp(RESOLUTIONS[0][3], RESOLUTIONS[1][3], 75);
    //eye.setScale(10, 10, 10);
    ScreenMake.setLineList(testLineLinked);
    ScreenMake.setStencilTest((byte)0, 'p');

    ScreenMake.setDitherIntensity(0.25f);
  }

  private static LinkedList<Model> arrayToLinkedList(Model[] arr){
    LinkedList<Model> list = new LinkedList<Model>();
    for(int i = 0; i < arr.length; i++)
      list.add(arr[i]);
    return list;
  }
  private static LinkedList<Billboard> arrayToLinkedList(Billboard[] arr){
    LinkedList<Billboard> list = new LinkedList<Billboard>();
    for(int i = 0; i < arr.length; i++)
      list.add(arr[i]);
    return list;
  }
  private static LinkedList<LineObj> arrayToLinkedList(LineObj[] arr){
    LinkedList<LineObj> list = new LinkedList<LineObj>();
    for(int i = 0; i < arr.length; i++)
      list.add(arr[i]);
    return list;
  }
  private static LinkedList<Dot> arrayToLinkedList(Dot[] arr){
    LinkedList<Dot> list = new LinkedList<Dot>();
    for(int i = 0; i < arr.length; i++)
      list.add(arr[i]);
    return list;
  }

  private byte imageBack = 1; //first bit controls image, top bit controls keyboard locking
  private byte outlineControl = 1; //First bit controls fill, second controls outline, top controls keylock, third and fourth control the rotation in the small camera, sixth acts as a small camera key lock
  private float boxX = BOX_SIZE;
  private int boxY = BOX_SIZE;
  private float boxSpeedX = 1;
  private float speed = 30f/frameRate;
  public void draw(){
    //float[] point = {testModels[3].returnPosition()[0], testModels[3].returnPosition()[1], testModels[3].returnPosition()[2]};
    //eye.lookAt(point);

    //float[] eyeForward = {0, 0, 1};
    //float[] eyeRight = {1, 0, 0};

    //testLines[2].lookAt(eye.returnPosition());
    if(keyPressed){
      switch(key){
        case 'b':
          if(!bPressed){
            int prevResolution = resolutionIndex;
            resolutionIndex = ((resolutionIndex+1)%RESOLUTIONS[0].length);
            bPressed = true;
            output = createGraphics(RESOLUTIONS[0][resolutionIndex], RESOLUTIONS[1][resolutionIndex]);
            testStencil = new byte[RESOLUTIONS[0][resolutionIndex]*RESOLUTIONS[1][resolutionIndex]];
            screen2 = new int[(RESOLUTIONS[0][resolutionIndex] >>> 2)*(RESOLUTIONS[1][resolutionIndex] >>> 2)];
            Rasterizer.initBuffers(RESOLUTIONS[0][resolutionIndex], RESOLUTIONS[1][resolutionIndex]);
            QUARTER_WIDTH = RESOLUTIONS[0][resolutionIndex] >>> 2;
            QUARTER_HEIGHT = RESOLUTIONS[1][resolutionIndex] >>> 2;
            BOX_SIZE = RESOLUTIONS[0][resolutionIndex]*50/600;
            boxX = (RESOLUTIONS[0][resolutionIndex]*boxX)/RESOLUTIONS[0][prevResolution];
            boxY = BOX_SIZE;
          }
          break;

        case ' ':
          if((imageBack & -128) == 0)
            imageBack = (byte)((imageBack & 1)^1);
          imageBack|=-128;
          break;

        case 'p':
          if((outlineControl & -128) == 0)
            outlineControl = (byte)(-128 | (outlineControl & -4) | ((outlineControl+1 & 3)));
          break;

      }
    } 
    else{
      imageBack&=127;
      outlineControl&=15;
      bPressed = false;
    }

    output.beginDraw();
    output.textSize(RESOLUTIONS[0][resolutionIndex]*24/600);
    boxX+=((Math.round((RESOLUTIONS[0][resolutionIndex]/240f)*speed*100)*0.01f)*boxSpeedX);
    //boxX+=(boxSpeedX);
    if(boxX < BOX_SIZE){
      boxX = BOX_SIZE;
      boxSpeedX*=-1;
    } 
    else if(boxX+BOX_SIZE > RESOLUTIONS[0][resolutionIndex]-BOX_SIZE){
      boxX = RESOLUTIONS[0][resolutionIndex]-(BOX_SIZE << 1);
      boxSpeedX*=-1;
    }
    output.image(testImages[imageBack & 1], 0, 0, RESOLUTIONS[0][resolutionIndex], RESOLUTIONS[1][resolutionIndex]);
    output.fill(0xFF0055FF);
    output.rect(boxX, boxY, BOX_SIZE, BOX_SIZE);

    output.loadPixels();
    Rasterizer.initBuffers(RESOLUTIONS[0][resolutionIndex], RESOLUTIONS[1][resolutionIndex]);
    //for(int i = 0; i < tris.length; i++){
    //   float[] returnPoints = {(tris[i].getVertices()[0][0]+tris[i].getVertices()[1][0]+tris[i].getVertices()[2][0])/3-0.0001f,
    //                          (tris[i].getVertices()[0][1]+tris[i].getVertices()[1][1]+tris[i].getVertices()[2][1])/3-0.0001f};
    //  float[][] newPoints = new float[3][2];
    //  float[] tempPoses = {0, 0};
    //  for(byte j = 0; j < 3; j++){
    //    //Moving the triangle to the origin
    //    tempPoses[0] = tris[i].getVertices()[j][0]-returnPoints[0]-0.0001f;
    //    tempPoses[1] = tris[i].getVertices()[j][1]-returnPoints[1]-0.0001f;
    //    //Rotating the triangle around the z-axis
    //    newPoints[j][0] = (float)(tempPoses[0]*Math.cos(-0.05)-tempPoses[1]*Math.sin(-0.05))-0.0001f;
    //    newPoints[j][1] = (float)(tempPoses[0]*Math.sin(-0.05)+tempPoses[1]*Math.cos(-0.05))-0.0001f;
    //    //Moving the triangle back to its original location
    //    tris[i].getVertices()[j][0] = newPoints[j][0]+returnPoints[0]-0.0001f;
    //    tris[i].getVertices()[j][1] = newPoints[j][1]+returnPoints[1]-0.0001f;

    //  }
    //  TriangleRasterizer.fill(tris[i].getFill());
    //  TriangleRasterizer.stroke(0x000000);
    //  TriangleRasterizer.draw(pixels, tris[i].getVertices()[0][0], tris[i].getVertices()[0][1],tris[i].getVertices()[0][2],tris[i].getVertices()[1][0],tris[i].getVertices()[1][1],tris[i].getVertices()[1][2],tris[i].getVertices()[2][0],tris[i].getVertices()[2][1],tris[i].getVertices()[2][2]);
    //}
    //ScreenMake.setStencilTest((byte)0, 'e');
    //ScreenMake.smooth();
    ScreenMake.imgBack();
    //ScreenMake.solidBack();

    //eye.lookAt(point, (byte)2);
    //ScreenMake.setViewMatrix(eye);

    if((outlineControl & 1) == 1)
      ScreenMake.fill();
    else
      ScreenMake.noFill();
    if((outlineControl & 2) == 2)
      ScreenMake.stroke();
    else
      ScreenMake.noStroke();
    ScreenMake.setModelList(testModelLinked);
    ScreenMake.setBillboardList(testBillboardLinked);
    ScreenMake.isInteractive();
    ScreenMake.ditherAll();
    //ScreenMake.drawScene(output.pixels, eye);

    ScreenMake.drawScene(output.pixels, eye, lightsPrimary, 1.5f);
    
    ScreenMake.ditherOnlyObjects();
    // float[][] testColours = {{0, 0, 1}, {0, 1, 0}, {1, 0, 0}};
    // Rasterizer.fill(0xFF);
    // Rasterizer.setVertexBrightness(testColours);
    // Rasterizer.setDepthWrite(false);
    // Rasterizer.draw(50, 50, 0.09139264f, 200, 125, 0.9977298f, 250, 50, 0.19978599f);
    //System.out.println(output.pixels[(int)mouseY*width+(int)mouseX]);
    Rasterizer.initBuffers(RESOLUTIONS[0][resolutionIndex] >>> 2, RESOLUTIONS[1][resolutionIndex] >>> 2);
    //ScreenMake.imgBack();
    ScreenMake.isNotInteractive();
    //Keys out the background
    int x = (int)((mouseX-(width >>> 3))*((float)RESOLUTIONS[0][resolutionIndex]/width));
    int y = (int)((mouseY-(height >>> 3))*((float)RESOLUTIONS[1][resolutionIndex]/height));

    int[] miniXEnds = {(x >= 0) ? x : 0, (x+QUARTER_WIDTH < RESOLUTIONS[0][resolutionIndex]) ? x+QUARTER_WIDTH : RESOLUTIONS[0][resolutionIndex]};
    int[] miniYEnds = {(y >= 0) ? y : 0, (y+QUARTER_HEIGHT < RESOLUTIONS[1][resolutionIndex]) ? y+QUARTER_HEIGHT : RESOLUTIONS[1][resolutionIndex]};
    int positionX = (x < 0) ? -x : 0;
    int positionY = (y < 0) ? -y : 0;
    x = positionX;
    y = positionY;
    
    for(int i = miniXEnds[0]; i < miniXEnds[1]; i++){
      y = positionY;
      for(int j = miniYEnds[0]; j < miniYEnds[1]; j++){
        screen2[y*QUARTER_WIDTH+x] = output.pixels[j*RESOLUTIONS[0][resolutionIndex]+i];
        y++;
      }
      x++;
    }
    x = positionX;
    y = positionY;
    //ScreenMake.setViewMatrix(eye2);
    ScreenMake.setModelList(linkedSmallerSet);
    ScreenMake.disableBillboardList();
    ScreenMake.drawScene(screen2, eye2, lightsSecondary, 1.5f);
    for(int i = miniXEnds[0]; i < miniXEnds[1]; i++){
      y = positionY;
      for(int j = miniYEnds[0]; j < miniYEnds[1]; j++){
        Rasterizer.stencilTest(y*QUARTER_WIDTH+x, (byte)0, 'e');
        output.pixels[j*RESOLUTIONS[0][resolutionIndex]+i] = screen2[y*QUARTER_WIDTH+x];
        y++;
      }
      x++;
    }

    output.updatePixels();
    speed = 60.0f/frameRate;
    Action.setRatePerFrame(speed);
    output.endDraw();
    image(output, 0, 0, width, height);
    System.out.println(Math.round(frameRate));
  }

  private class SetTransparency extends ModelAction{
    private Camera object;
    //ORIGINALS: 360, 1800
    private static final float MIN_DIST = 360;
    private static final float MAX_DIST = 1800;
    public SetTransparency(Camera newCam){
      object = newCam; 
    }
    public void init(){

    }
    public void perform(){
      float tint = dist(object.returnPosition());
      if(tint > MIN_DIST)
        setModelTint(1 - ((tint)/MAX_DIST)); 
      else
        setModelTint(1);
    }
  }

  private class RotateMess extends ModelAction{
    public void init(){
      
    }
    public void perform(){
      addToRotation(-1.5f*speed, (byte)0);
      addToRotation(-1.5f*speed, (byte)2);
      float[] tempRot = getRot();
      if(tempRot[0] < 0)
        addToRotation(360, (byte)0);
      if(tempRot[2] < 0)
        addToRotation(360, (byte)2);
    }
  }

  private class RotateTetrahedron extends ModelAction{
    public void init(){
      
    }
    public void perform(){
      addToRotation(1.5f*speed, (byte)0);
      addToRotation(-3*speed, (byte)1);
      float[] tempRot = getRot();
      if(tempRot[1] < 0)
        addToRotation(360, (byte)1);
      if(tempRot[0] >= 360)
        addToRotation(-360, (byte)0);
    }
  }

  private class SpinTwoTriangles extends ModelAction{
    private int direction = 0;
    public void init(){
      direction = (int)(Math.random()*2)-1;
      if(direction == 0)
        direction = 1;
    }
    public void perform(){
      addToRotation(-0.5f*direction*speed, (byte)1);
      float[] tempRot = getRot();
      if(tempRot[1] < 0)
        addToRotation(360, (byte)1);
      else if(tempRot[1] >= 360)
        addToRotation(-360, (byte)1);
    }
  }

  private class ColonThree extends LightAction{
    public void init(){
      System.out.println("Started. :3");
    }
    public void perform(){

    }
  }

  private class RotateLongModel extends ModelAction{
    public void init(){
    }
    public void perform(){
      matrixTransform();
      float[] modelForward = getForward();
      if(keyPressed){
        switch(key){
          case 'w':
            addToPosition(0.5f*speed, modelForward);
            break;
          case 's':
            addToPosition(-0.5f*speed, modelForward);
            break;
          case 'd':
            addToRotation(0.5f*speed, (byte)1);
            break;
          case 'a':
            addToRotation(-0.5f*speed, (byte)1);
            break;
          case 'g':
            addToRotation(0.5f*speed, (byte)0);
            break;
          case 'h':
            addToRotation(-0.5f*speed, (byte)0);
            break;
          case '=':
            initPositionShake(0.6f, 50);
            initRotationShake(15, 50);
            break;
        }
      }
      shakePosition();
      shakeRotation();
      float[] tempRot = getRot();
      if(tempRot[1] < 0)
        addToRotation(360, (byte)1);
      else if(tempRot[1] >= 360)
        addToRotation(-360, (byte)1);
      if(tempRot[0] < 0)
        addToRotation(360, (byte)0);
      else if(tempRot[0] >= 360)
        addToRotation(-360, (byte)0);
    }
  }

  private class ChangeStencil extends StencilAction{
    public void updateStencil(){
      stencilPixel = -1;
    }
  }


  private class RotateAtFiveDegrees extends ModelAction{
    private int direction = 1;
    public RotateAtFiveDegrees(boolean positive){
      if(positive)
        direction = 1;
      else
        direction = -1;
    }
    public void init(){
      
    }
    public void perform(){
      addToRotation(5*direction*speed, (byte)1);
      float[] tempRot = getRot();
      if(tempRot[1] < 0)
        addToRotation(360, (byte)1);
      else if(tempRot[1] >= 360)
        addToRotation(-360, (byte)1);
    }
  }

  private class RotateBillboard extends ModelAction{
    public void init(){
      
    }
    public void perform(){
      addToRotation(0.25f*speed, (byte)2);
      if(getRot()[2] >= 360)
        addToRotation(-360, (byte)2);
    }
  }
  private class RotateDefaultModel extends ModelAction{
    public void init(){
      
    }
    public void perform(){
      addToRotation(0.25f*speed, (byte)0);
      addToRotation(0.25f*speed, (byte)1);
      addToRotation(0.25f*speed, (byte)2);
      float[] tempRot = getRot();
      if(tempRot[0] >= 360)
        addToRotation(-360, (byte)0);
      if(tempRot[1] >= 360)
        addToRotation(-360, (byte)1);
      if(tempRot[2] >= 360)
        addToRotation(-360, (byte)2);
    }
  }
  private class RotateRhombohedron extends ModelAction{
    public void init(){
      
    }
    public void perform(){
      addToRotation(-2*speed, (byte)0);
      addToRotation(2*speed, (byte)1);
      float[] tempRot = getRot();
      if(tempRot[0] < 0)
        addToRotation(360, (byte)0);
      if(tempRot[1] >= 360)
        addToRotation(-360, (byte)1);
    }
  }

  private class RotateLight extends LightAction{
    public void init(){
      
    }
    private float angularVelocity = 1f;
    public void perform(){
      addToRotation(angularVelocity, (byte)1);
      float[] tempRot = getRot();
      if(tempRot[1] > 360){
        addToRotation(-360, (byte)1);
      }
      else if(tempRot[1] < 0){
          addToRotation(360, (byte)1);
      }
    }
  }

  private class MoveLight extends LightAction{
    private float velocity = 0.05f;
    private final float[] DIR = {0, 0, 1};
    public void init(){

      
    }
    public void perform(){
        addToPosition(velocity*speed, DIR);
        float[] tempPos = getPos();
        if(tempPos[2] > 100){
          hardSetPosition(tempPos[0], tempPos[1], 100);
          velocity*=-1;
        }
        else if(tempPos[2] < -100){
          hardSetPosition(tempPos[0], tempPos[1], 100);
          velocity*=-1;
        }
    }
  }

  private class SpinSlab extends ModelAction{
    private boolean keyLocked = false;
    private int spinSpeed = 2;
    private float[] startPosition = {0, 0, 0};
    private float[] scale = new float[3];
    public void init(){
      startPosition[0] = getPos()[0];
      startPosition[1] = getPos()[1];
      startPosition[2] = getPos()[2];
      Physics.fluidDensity = 0.25f;
      Physics.gravityAcceleration = 0.005f;
      physics.terminalVelocity = 0.5f;
      physics.setGravityVelocity();
      scale = getScale();
    }
    public void perform(){
      physics.applyGravity();
      if(keyPressed){
        if(key == 'r'){
          hardSetPosition(startPosition);
          physics.setGravityVelocity();
        }
        if(!keyLocked){
          keyLocked = true;
          switch(key){
            case '5':
              if(Math.abs(spinSpeed) == 2)
                spinSpeed = 0;
              else
                spinSpeed = 2;
              break;
            case '6':
              if(Math.abs(spinSpeed) == 2)
                spinSpeed = 0;
              else
                spinSpeed = -2;
              break;
          }
        }
      }
      else
        keyLocked = false;
      float[] tempScale = getScale();
      if(keyPressed){
        if(key == '8'){
          if(tempScale[0] < 10)
            scale[0]+=0.5;
        }
        else if(key == '3'){
          if(scale[0] > 0.5)
            scale[0]-=0.5;
        }
        scale[1] = scale[0];
        scale[2] = scale[0];
        hardSetScale(scale);
      }
      addToRotation(spinSpeed*speed, (byte)2);
      addToRotation(spinSpeed*speed+0.0001f, (byte)1);
      float[] tempRot = getRot();
      if(tempRot[2] > 360)
        addToRotation(-360, (byte)2);
      if(tempRot[2] < 0)
        addToRotation(360, (byte)2);
      if(tempRot[1] > 360)
        addToRotation(-360, (byte)1);
      if(tempRot[1] < 0)
        addToRotation(360, (byte)1);
    }
  }


  private class MoveCamera extends CameraAction{
    public void init(){
      
    }
    public void perform(){
      float[] eyeForward = getForward();
      float[] eyeRight = getRight();
      if(keyPressed){
        switch(key){
          case 'i':
            addToRotation(0.5f*speed, (byte)0);
            break;
          case 'k':
            addToRotation(-0.5f*speed, (byte)0);
            break;
          case 'j':
            addToRotation(-0.5f*speed, (byte)1);
            break;
          case 'l':
            addToRotation(0.5f*speed, (byte)1);
            break; 
          case '1':
            addToPosition(0.05f*speed, eyeForward);
          break;
          case '2':
            addToPosition(-0.05f*speed, eyeForward);
            break;
          case '9':
            addToPosition(-0.05f*speed, eyeRight);
            break;
          case '0':
            addToPosition(0.05f*speed, eyeRight);
            break;
        }
      }
      
      if(mousePressed){
        if(mouseButton == LEFT){
          if(mouseX >= ((width >>> 1) + 50))
            addToPosition(0.05f*speed, eyeRight);
          else if(mouseX <= ((width >>> 1) - 50))
            addToPosition(-0.05f*speed, eyeRight);
          if(mouseY >= ((height >>> 1)+50))
            addToPosition(-0.05f*speed, eyeForward);
          else if(mouseY <= ((height >>> 1)-50))
            addToPosition(0.05f*speed, eyeForward);
        }
        if(mouseButton == RIGHT){
          if(mouseX >= ((width >>> 1) + 50))
            addToRotation(0.5f*speed, (byte)1);
          else if(mouseX <= ((width >>> 1) - 50))
            addToRotation(-0.5f*speed, (byte)1);
          if(mouseY >= ((height >>> 1)+50))
            addToRotation(-0.5f*speed, (byte)0);
          else if(mouseY <= ((height >>> 1)-50))
            addToRotation(0.5f*speed, (byte)0);
        }
      }
      float[] tempRot = getRot();
      if(tempRot[0] >= 360)
        addToRotation(-360, (byte)0);
      else if(tempRot[0] < 0)
        addToRotation(360, (byte)0);
      if(tempRot[1] >= 360)
        addToRotation(-360, (byte)1);
      else if(tempRot[1] < 0)
        addToRotation(360, (byte)1);
    }
  }

  private class ManageSecondCamera extends CameraAction{
    private boolean cPressed = false;
    private Camera other;
    public ManageSecondCamera(Camera newCamera){
      cPressed = false;
      other = newCamera;
    }
    public void init(){
      
    }
    public void perform(){
      if(keyPressed){
        if(key == 'c' && !cPressed){
            if((outlineControl & 16) == 0){
              outlineControl = (byte)((outlineControl & -13) | ((outlineControl+4 & 12)));
              hardSetPosition(0, 0, 0);
              hardSetRotation(0, 0, 0);
              cPressed = true;
          }
        }
      }
      else
        cPressed = false;
        
    if((outlineControl & 12) == 0){
      model.copy(other.returnModelMatrix());
      hardSetPosition(other.returnPosition());
      hardSetRotation(other.returnRotation());
      hardSetScale(other.returnScale());
      hardSetShear(other.returnShear());
    }
    else{
        //Secondary camera
        switch(outlineControl & 12){
          case 4:
            addToRotation(-speed, (byte)1);
            break;
          case 8:
            addToRotation(0.5f*speed, (byte)0);
            break;
          case 12:
            addToRotation(-0.5f*speed, (byte)2);
            break;
        }
        float[] eye2Back = getBackward();
        hardSetPosition(sceneMag*eye2Back[0]+sceneCentre[0], sceneMag*eye2Back[1]+sceneCentre[1], sceneMag*eye2Back[2]+sceneCentre[2]);
      }
    }
  }

  private class CopyLight extends LightAction{
    private Light tempLight;
    public CopyLight(Light newLight){
      tempLight = newLight; 
    }
    public void init(){
      
    }
    public void perform(){
      hardSetPosition(tempLight.returnPosition());
      hardSetRotation(tempLight.returnRotation());
    }
  }

  public void settings(){
    size(WIDTH, HEIGHT);
    noSmooth();
  }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "sketch_3DTriTest" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
