//SCREEN DIMENSIONS
final int WIDTH = 600;
final int HEIGHT = 800;
final int QUARTER_WIDTH = WIDTH >>> 2;
final int QUARTER_HEIGHT = HEIGHT >>> 2;

final int BOX_SIZE = WIDTH*50/600;
final float  DEGS_TO_RADS = (float)Math.PI/180f;

Model[] testModels = new Model[11];
Model[] smallerSet = new Model[9];
Model[] smallList = new Model[9];
Triangle[] tris = new Triangle[10];
//Model position and angles
float[][] modelPos = {{-100, 0, 1500}, {0, 0, 8}, {-2, 4.5, 10}, {0.5, 3, 9}, {-0.35, -5.5, 8}, {1, 0, 18}, {0.5, 3, 12}, {0, 0, 25}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
float[][] modelAngle = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {65, -20, 180}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}};

//Cameras
Camera eye = new Camera();
Camera eye2 = new Camera();

int[] testBack = {#FF00FFFF, #FF00FFFF, #FF00FFFF, #FF00FFFF, #FF00FFFF, #FF00FFFF, #FF00FFFF, #FF00FFFF, #FF00FFFF, #FF00FFFF,
                  #FF0000FF, #FF0000FF, #FF0000FF, #FF0000FF, #FF0000FF, #FF0000FF, #FF0000FF, #FF0000FF, #FF0000FF, #FF0000FF,
                  #FFFF0000, #FFFF0000, #FFFF0000, #FFFF0000, #FFFF0000, #FFFF0000, #FFFF0000, #FFFF0000, #FFFF0000, #FFFF0000,
                  #FFFFFFFF, #FFFFFFFF, #FFFFFFFF, #FFFFFFFF, #FFFFFFFF, #FFFFFFFF, #FFFFFFFF, #FFFFFFFF, #FFFFFFFF, #FFFFFFFF,
                  #FF000000, #FF000000, #FF000000, #FF000000, #FF000000, #FF000000, #FF000000, #FF000000, #FF000000, #FF000000,
                  #FF00FFFF, #FF00FFFF, #FF00FFFF, #FF00FFFF, #FF00FFFF, #FF00FFFF, #FF00FFFF, #FF00FFFF, #FF00FFFF, #FF00FFFF,
                  #FF0000FF, #FF0000FF, #FF0000FF, #FF0000FF, #FF0000FF, #FF0000FF, #FF0000FF, #FF0000FF, #FF0000FF, #FF0000FF,
                  #FFFF0000, #FFFF0000, #FFFF0000, #FFFF0000, #FFFF0000, #FFFF0000, #FFFF0000, #FFFF0000, #FFFF0000, #FFFF0000,
                  #FFFFFFFF, #FFFFFFFF, #FFFFFFFF, #FFFFFFFF, #FFFFFFFF, #FFFFFFFF, #FFFFFFFF, #FFFFFFFF, #FFFFFFFF, #FFFFFFFF,
                  #FF000000, #FF000000, #FF000000, #FF000000, #FF000000, #FF000000, #FF000000, #FF000000, #FF000000, #FF000000};

int[][] backgrounds = {testBack, new int[WIDTH*HEIGHT]};
int[] screen2 = new int[(WIDTH >>> 2)*(HEIGHT >>> 2)];
float[] light = {0, -50, 0};
PImage testImages[] = new PImage[2];

float[] sceneCentre = {0, 0, 0};
byte[] testStencil = new byte[WIDTH*HEIGHT];
Billboard[] test = new Billboard[2];
void setup(){
  test[0] = new Billboard("sketch_3DTriTest/billboards/testImage.png", (byte)-100);
  test[0].setPosition(-4, -6, 10);
  test[0].setScale(0.04);
  test[1] = new Billboard("sketch_3DTriTest/billboards/testImage2.png");
  test[1].setPosition(-2, -5, 12);
  test[1].setBrightness((short)-50);
  test[1].setInvisColour(0x00FF00);
  test[1].setScale(-0.06, 0.06);
  size(600, 800);
  noCursor();
  frameRate(20);
  for(int i = 0; i < tris.length; i++){
    tris[i] = new Triangle((int)(Math.random()*610)-10, (int)(Math.random()*790)+10, (int)(Math.random()*30)+10, (int)(Math.random()*610)-10, (int)(Math.random()*790)+10, (int)(Math.random()*30)+10, (int)(Math.random()*610)-10, (int)(Math.random()*790)+10, (int)(Math.random()*30)+10, (int)(Math.random()*16777215), (int)(Math.random()*16777215), true, true);
  }

  //Test models
  testModels[0] = LoadModelFile.loadModel("sketch_3DTriTest/MODELS/firstTris.txt", modelPos[0], modelAngle[0]);
  testModels[1] = LoadModelFile.loadModel("sketch_3DTriTest/MODELS/testModel.txt", modelPos[1], modelAngle[1]);
  testModels[2] = LoadModelFile.loadModel("sketch_3DTriTest/MODELS/TestTris.txt", modelPos[2], modelAngle[2]);
  testModels[3] = LoadModelFile.loadModel("sketch_3DTriTest/MODELS/cube.txt");
  testModels[4] = LoadModelFile.loadModel("sketch_3DTriTest/MODELS/cube.txt");
  testModels[5] = new Model();
  testModels[6] = LoadModelFile.loadModel("sketch_3DTriTest/MODELS/cube.txt");
  testModels[7] = LoadModelFile.loadModel("sketch_3DTriTest/MODELS/TestBillBoard.txt", modelPos[7], modelAngle[7]);
  testModels[8] = LoadModelFile.loadModel("sketch_3DTriTest/MODELS/pyramid.txt", modelPos[8], modelAngle[8]);
  testModels[9] = LoadModelFile.loadModel("sketch_3DTriTest/MODELS/rhombohedron.MODEL", modelPos[9], modelAngle[9]);
  testModels[10] = LoadModelFile.loadModel("sketch_3DTriTest/MODELS/flag.txt", modelPos[10], modelAngle[10]);
  smallList[0] = testModels[0];
  smallList[1] = testModels[1];
  smallList[2] = testModels[2];
  smallList[3] = testModels[3];
  smallList[4] = testModels[4];
  smallList[5] = testModels[5];
  smallList[6] = testModels[6];
  smallList[7] = testModels[8];
  smallList[8] = testModels[9];
  testModels[3].setScale(1, 0.5, 4);
  testModels[6].setScale(2, 0.5, 2);
  for(int i = 0; i < testModels.length; i++){
    testModels[i].setPosition(modelPos[i][0], modelPos[i][1], modelPos[i][2]);
    testModels[i].setAngle(modelAngle[i][0], modelAngle[i][1], modelAngle[i][2]);
  }

  for(int i = 0; i < smallerSet.length; i++){
    smallerSet[i] = testModels[i+1];
    sceneCentre[0]+=smallerSet[i].returnPosition()[0];
    sceneCentre[1]+=smallerSet[i].returnPosition()[1];
    sceneCentre[2]+=smallerSet[i].returnPosition()[2];
  }

  sceneCentre[0]/=smallerSet.length;
  sceneCentre[1]/=smallerSet.length;
  sceneCentre[2]/=smallerSet.length;

  switch(width){
    case 600:
    case 300:
      testImages[1] = loadImage("backgrounds/testImage.png");
      break;
    case 800:
    case 400:
      testImages[1] = loadImage("backgrounds/testImage2.png");
      break;
  }

  testImages[0] = new PImage(WIDTH, HEIGHT);
  testImages[0].loadPixels();
  for(int i = 0; i < WIDTH*HEIGHT; i++)
    testImages[0].pixels[i] = testBack[i%testBack.length];
  testImages[0].updatePixels();

  //Depth buffer initialization
  TriangleRasterizer.initBuffers(WIDTH, HEIGHT);
  TriangleRasterizer.background(#777777);
  ScreenMake.initPersp(WIDTH, HEIGHT, 90);
  ScreenMake.initOrtho(WIDTH, HEIGHT, 90);
  textSize(WIDTH*24/600);
  noSmooth();
}

void rotateModel(float[] anglesList, int axis, float angle, float speedRatio){
  anglesList[axis]+=angle*speedRatio;
  if(anglesList[axis] >= 360 || anglesList[axis] <= -360)
    anglesList[axis] = 0;
}

byte imageBack = 1; //first bit controls image, top bit controls keyboard locking
byte outlineControl = 1; //First bit controls fill, second controls outline, top controls keylock, third and fourth control the rotation in the small camera, sixth acts as a small camera key lock
int boxX = BOX_SIZE;
int boxY = BOX_SIZE;
float boxSpeedX = 1;
float speed = 60/frameRate;
void draw(){

  //Tetrahedron
  rotateModel(modelAngle[1], 0, -1.5, speed);
  rotateModel(modelAngle[1], 1, 3, speed);

  //Literally giant mess
  rotateModel(modelAngle[0], 0, -1.5, speed);
  rotateModel(modelAngle[0], 2, -1.5, speed);

  //Pyramid
  rotateModel(modelAngle[8], 1, 5, speed);

  //Rhombohedron
  rotateModel(modelAngle[9], 1, -2, speed);
  rotateModel(modelAngle[9], 0, 2, speed);

  //Two triangles above
  rotateModel(modelAngle[2], 1, -0.5, speed);

  //Rectangular prism
  rotateModel(modelAngle[3], 0, 0.5, speed);
  rotateModel(modelAngle[3], 1, 0.5, speed);

  //Bowser platform
  rotateModel(modelAngle[6], 1, 5, speed);

  //Black and white cube
  rotateModel(modelAngle[5], 0, 0.25, speed);
  rotateModel(modelAngle[5], 1, 0.25, speed);
  rotateModel(modelAngle[5], 2, 0.25, speed);
  
  rotateModel(modelAngle[7], 2, 0.25, speed);
  //Flag
  //Setting the positions and angles of all of the models
  for(int i = 0; i < testModels.length; i++){
    testModels[i].setPosition(modelPos[i][0], modelPos[i][1], modelPos[i][2]);
    testModels[i].setAngle(modelAngle[i][0], modelAngle[i][1], modelAngle[i][2]);
  }

  if(keyPressed){
    switch(key){
      case 'w':
        modelPos[3][2]+=0.5*speed;
        break;
      case 's':
        modelPos[3][2]-=0.5*speed;
        break;
      case 'd':
        modelPos[3][0]+=0.5*speed;
        break;
      case 'a':
        modelPos[3][0]-=0.5*speed;
        break;
      case 'i':
        rotateModel(eye.returnRotation(), 0, 0.5, speed);
        break;
      case 'k':
        rotateModel(eye.returnRotation(), 0, -0.5, speed);
        break;
      case 'j':
        rotateModel(eye.returnRotation(), 1, -0.5, speed);
        break;
      case 'l':
        rotateModel(eye.returnRotation(), 1, 0.5, speed);
        break;
      case ' ':
        if((imageBack & -128) == 0)
          imageBack = (byte)((imageBack & 1)^1);
        imageBack|=-128;
        break;
      case '1':
        eye.setPositionZ(eye.returnPosition()[2]+0.05f*speed);
        break;
      case '2':
        eye.setPositionZ(eye.returnPosition()[2]-0.05f*speed);
        break;
      case '9':
        eye.setPositionX(eye.returnPosition()[0]-0.05f*speed);
        break;
      case '0':
        eye.setPositionX(eye.returnPosition()[0]+0.05f*speed);
        break;
      case 'p':
        if((outlineControl & -128) == 0)
          outlineControl = (byte)(-128 | (outlineControl & -4) | ((outlineControl+1 & 3)));
        break;
      case 'c':
        if((outlineControl & 16) == 0){
          outlineControl = (byte)((outlineControl & -13) | ((outlineControl+4 & 12)));
          eye2.setPosition(0, 0, 0);
          eye2.setRotation(0, 0, 0);
        }
        outlineControl|=16;
        break;
    }
  } 
  else{
    imageBack&=127;
    outlineControl&=15;
  }
  if(mousePressed){
    if(mouseButton == LEFT){
      if(mouseX >= ((WIDTH >>> 1) + 50))
        eye.setPositionX(eye.returnPosition()[0]+0.05f*speed);
      else if(mouseX <= ((WIDTH >>> 1) - 50))
        eye.setPositionX(eye.returnPosition()[0]-0.05f*speed);
      if(mouseY >= ((HEIGHT >>> 1)+50))
        eye.setPositionZ(eye.returnPosition()[2]-0.05f*speed);
      else if(mouseY <= ((HEIGHT >>> 1)-50))
        eye.setPositionZ(eye.returnPosition()[2]+0.05f*speed);
    }
    if(mouseButton == RIGHT){
      if(mouseX >= ((WIDTH >>> 1) + 50))
        rotateModel(eye.returnRotation(), 1, 0.5f, speed);
      else if(mouseX <= ((WIDTH >>> 1) - 50))
        rotateModel(eye.returnRotation(), 1, -0.5f, speed);
      if(mouseY >= ((HEIGHT >>> 1)+50))
        rotateModel(eye.returnRotation(), 0, -0.5f, speed);
      else if(mouseY <= ((HEIGHT >>> 1)-50))
        rotateModel(eye.returnRotation(), 0, 0.5f, speed);
    }
  }



  boxX+=boxSpeedX*(BOX_SIZE/10)*speed;
  if(boxX < 50){
    boxX = 50;
    boxSpeedX*=-1;
  } 
  else if(boxX+BOX_SIZE > WIDTH-BOX_SIZE){
    boxX = WIDTH-(BOX_SIZE << 1);
    boxSpeedX*=-1;
  }
  image(testImages[imageBack & 1], 0, 0, WIDTH, HEIGHT);
  fill(#0055FF);
  rect(boxX, boxY, BOX_SIZE, BOX_SIZE);
  if((outlineControl & 12) == 0)
    eye2.copy(eye);
  else{
    eye2.setPosition(sceneCentre[0], sceneCentre[1], sceneCentre[2]);
    float[] tempPoses = {eye2.returnPosition()[0], eye2.returnPosition()[1], eye2.returnPosition()[2]};
    float[] tempAngles = {-eye2.returnRotation()[0]*DEGS_TO_RADS, -eye2.returnRotation()[1]*DEGS_TO_RADS, -eye2.returnRotation()[2]*DEGS_TO_RADS};
    //Secondary camera
    switch(outlineControl & 12){
      case 4:
        rotateModel(eye2.returnRotation(), 1, -1, speed);
        eye2.setPositionX((float)(tempPoses[0]*Math.cos(tempAngles[1])+tempPoses[2]*Math.sin(tempAngles[1]))-sceneCentre[0]);
        eye2.setPositionZ((float)(-tempPoses[0]*Math.sin(tempAngles[1])+tempPoses[2]*Math.cos(tempAngles[1]))-sceneCentre[2]);
        break;
      case 8:
        rotateModel(eye2.returnRotation(), 0, 0.5f, speed);
        eye2.setPositionY((float)(tempPoses[1]*Math.cos(tempAngles[0])-tempPoses[2]*Math.sin(tempAngles[0]))-sceneCentre[1]);
        eye2.setPositionZ((float)(tempPoses[1]*Math.sin(tempAngles[0])+tempPoses[2]*Math.cos(tempAngles[0]))-sceneCentre[2]);
        break;
      case 12:
        rotateModel(eye2.returnRotation(), 2, -0.5f, speed);
        eye2.setPositionX((float)(tempPoses[0]*Math.cos(tempAngles[2])-tempPoses[1]*Math.sin(tempAngles[2]))-sceneCentre[0]);
        eye2.setPositionY((float)(tempPoses[0]*Math.sin(tempAngles[2])+tempPoses[1]*Math.cos(tempAngles[2]))-sceneCentre[1]);
        eye2.setPositionZ(eye2.returnPosition()[2]-sceneCentre[2]);
        break;
    }
  }
  loadPixels();
  TriangleRasterizer.initBuffers(WIDTH, HEIGHT);
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
  //  TriangleRasterizer.stroke(#000000);
  //  TriangleRasterizer.draw(pixels, tris[i].getVertices()[0][0], tris[i].getVertices()[0][1],tris[i].getVertices()[0][2],tris[i].getVertices()[1][0],tris[i].getVertices()[1][1],tris[i].getVertices()[1][2],tris[i].getVertices()[2][0],tris[i].getVertices()[2][1],tris[i].getVertices()[2][2]);
  //}
  ScreenMake.setStencilTest((byte)0, 'e');
  //ScreenMake.smooth();
  ScreenMake.imgBack();
  //ScreenMake.solidBack();
  if((outlineControl & 1) == 1)
    ScreenMake.fill();
  else
    ScreenMake.noFill();
  if((outlineControl & 2) == 2)
    ScreenMake.stroke();
  else
    ScreenMake.noStroke();
  ScreenMake.setModelList(testModels);
  ScreenMake.setBillboardList(test);
  ScreenMake.drawScene(pixels, 0, 1800, eye);
  TriangleRasterizer.initBuffers(WIDTH >>> 2, HEIGHT >>> 2);
  ScreenMake.noSmooth();
  //ScreenMake.imgBack();
  //Keys out the background
  int x = mouseX-(WIDTH >>> 3);
  int y = mouseY-(HEIGHT >>> 3);

  int[] miniXEnds = {(x >= 0) ? x : 0, (x+QUARTER_WIDTH < WIDTH) ? x+QUARTER_WIDTH : WIDTH};
  int[] miniYEnds = {(y >= 0) ? y : 0, (y+QUARTER_HEIGHT < HEIGHT) ? y+QUARTER_HEIGHT : HEIGHT};
  int positionX = (x < 0) ? -x : 0;
  int positionY = (y < 0) ? -y : 0;
  x = positionX;
  y = positionY;
  
  for(int i = miniXEnds[0]; i < miniXEnds[1]; i++){
    y = positionY;
    for(int j = miniYEnds[0]; j < miniYEnds[1]; j++){
      screen2[y*QUARTER_WIDTH+x] = pixels[j*WIDTH+i];
      y++;
    }
    x++;
  }
  x = positionX;
  y = positionY;
  ScreenMake.setModelList(smallerSet);
  ScreenMake.disableBillboardList();
  ScreenMake.drawScene(screen2, 0, 1800, eye2);
  for(int i = miniXEnds[0]; i < miniXEnds[1]; i++){
    y = positionY;
    for(int j = miniYEnds[0]; j < miniYEnds[1]; j++){
      TriangleRasterizer.stencilTest(y*QUARTER_WIDTH+x, (byte)0, 'e');
      pixels[j*WIDTH+i] = screen2[y*QUARTER_WIDTH+x];
      y++;
    }
    x++;
  }
  updatePixels();
  speed = 60.0/frameRate;
  fill(#FFFFFF);
  text("FRAME RATE: "+Math.round(frameRate), WIDTH*15/600, HEIGHT*30/600);
}
