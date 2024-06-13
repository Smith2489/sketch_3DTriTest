//Average position of all tris: X: 50.444444, Y: 174.666667, Z: 4.066667
/*TODO: 
TAKE CURRENT TRI-POSES AND CONVERT THEM TO LOCAL SPACE (SUBTRACT ABOVE VALUES FROM RESPECTIVE COORDINATES) O
EXPLORE DIVIDING RESULTS BY THESE AVERAGES TO GET THE POLYGONS INSIDE A CANONICAL CUBE X
ADD A W-DIMENSION TO TRIPOSES FOR HOMOGENEOUS COORDINATES O (WILL HAVE THAT HAPPEN WHEN DOING MVP)
MODIFY THE TRIANGLE DRAWING ROUTINE TO INCORPORATE MVP O

*/

//Triangle setup
//Triangle[] tris = new Triangle[9];
//Triangle[] dispTris = new Triangle[9];

//Trianlge positions
float[][][] triPoses1 = {{{129.55556, -174.66667, 0.93333}, {-20.44444, 25.33333, 0.93333}, {349.55556, -74.66667, 0.93333}}, //First tri now in local coordinates
                        {{-50.44444, -204.66667, -2.06667}, {-80.44444, -144.66667, -1.06667}, {-20.44444, -144.66667, 3.93333}}, //Second tri now in local coordinates 
                        {{-10.44444, -24.66667, 1.5}, {29.55556, -24.66667, 1.5}, {9.55556, -74.66667, 1.5}}, //Third tri now in local coordinates
                        {{129.55556, -124.66667, -2.06667}, {49.55556, 125.33333, -2.06667}, {249.55556, 125.33333, -2.06667}}, //Fourth tri now in local coordinates
                        {{249.55556, 185.33333, 0}, {209.55556, 265.33333, 0}, {289.55556, 265.33333, 0}}}; //Fifth tri now in local coordinates
float[][][] triPoses2 = {{{-1, 0, 1},{0, -1, 1},{1, 0, 0}},{{1, 0, 2},{-1, 0, 1},{0, -1, 1}},{{-1, 0, 1},{1, 0, 2},{1, 0, 0}},{{0, -1, 1},{1, 0, 2},{1, 0, 0}}};
float[][][] triPoses = {{{2, 0, -2}, {0, 1, -2}, {-2, 0, -2}}, {{3.5, -1, -2.5}, {2.5, 1.5, -2.5}, {-1, 0.5, -2.5}}};
float[][][] cubePoses = {{{-1, 1, -1}, {-1, -1, -1}, {1, -1, -1}}, {{-1, 1, -1}, {1, 1, -1}, {1, -1, -1}}, //Front side
                         {{-1, 1, 1}, {-1, -1, 1}, {1, -1, 1}}, {{-1, 1, 1}, {1, 1, 1}, {1, -1, 1}}, //Back side 
                         {{-1, 1, -1}, {-1, -1, -1}, {-1, -1, 1}}, {{-1, 1, -1}, {-1, 1, 1}, {-1, -1, 1}}, //Left side
                         {{1, 1, -1}, {1, -1, -1}, {1, -1, 1}}, {{1, 1, -1}, {1, 1, 1}, {1, -1, 1}}, //Right side
                         {{-1, -1, -1}, {-1, -1, 1}, {1, -1, 1}}, {{-1, -1, -1}, {1, -1, 1}, {1, -1, -1}}, //Top side
                         {{-1, 1, -1}, {-1, 1, 1}, {1, 1, 1}}, {{-1, 1, -1}, {1, 1, 1}, {1, 1, -1}}}; //Bottom side
//Model position
//int[] modelTriCount = {5, 4};
int[][] colours = {{#44FF22, 144}, {#00FFFF, #FF33AA}, {0, #FFFF00}, {255, #FF00FF}, {0, #FE44BB}};
int[][] colours2 = {{#44FF22, 144}, {#00FFFF, #FF33AA}, {0, #FFFF00}, {255, #FF00FF}};
int[][] colours3 = {{0, 255}, {0, #00FF00}};
int[][] colours4 = {{0, #FFFF00}, {0, #00FF00}, 
                    {0, #000000}, {0, #FFFFFF}, 
                    {0, #666666}, {0, #FF0000}, 
                    {0, 0}, {0, 0},
                    {0, #FF0000}, {0, #FF0000},
                    {0, #FFFF00}, {0, #FFFF00}};

float[][] modelPos = {{-100, 0, 1500}, {0, 0, 8}, {-2, -4.5, 10}, {0.5, 3, 9}, {-0.35, -5.5, 8}, {1, 2, 18}, {0.5, 3, 12}};
float[][] modelAngle = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {65, -20, 0}, {0, 0, 0}, {0, 0, 0}};


float[] eyePos = {0, 0, 0};
float[] eyeAngle = {0, 0, 0};
float[] eyeScale = {1, 1, 1};
float speed = 1;
Model[] testModels = new Model[7];

////Camera data
//float[] cameraPosition = {0, 0, Float.intBitsToFloat(-1)};
//float[] cameraTarget = {0, 0, 0};
//float[] cameraDirection = {0, 0, 0};
//float[] up = {0, 0, 0};
//float[] cameraRight = {0, 0, 0};
//float[] cameraUp = {0, 0, 0};





void setup(){
  size(600, 800);
  noCursor();
  frameRate(30);
  //Test matrices
  testModels[0] = new Model(triPoses1, colours);
  testModels[1] = new Model(triPoses2, colours2);
  testModels[2] = new Model(triPoses, colours3);
  testModels[3] = new Model(cubePoses, colours4);
  testModels[4] = new Model(cubePoses, colours4);
  testModels[5] = new Model(cubePoses, colours4);
  testModels[6] = new Model(cubePoses, colours4);
  
  //testModels[0].setScale(1.0/750, 1.0/750, 1.0/750);
  testModels[3].setScale(1, 0.5, 4);
  testModels[2].setScale(1, -1, 1);
  for(int i = 0; i < testModels.length; i++){
    testModels[i].setPosition(modelPos[i][0], modelPos[i][1], modelPos[i][2]);
    testModels[i].setAngle(modelAngle[i][0], modelAngle[i][1], modelAngle[i][2]);
  }
  
  
  //Test triangles
  //for(int i = 0; i < triPoses1.length; i++)
  //  tris[i] = new Triangle(triPoses1[i], colours[i][0], colours[i][1], false, true);
  //for(int i = 0; i < triPoses2.length; i++)
  //  tris[i+triPoses1.length] = new Triangle(triPoses2[i], colours[i][0], colours[i][1], false, true);
  //for(int i = 0; i < dispTris.length; i++){
  //  dispTris[i] = new Triangle();
  //}
  //Depth buffer initialization
  TriangleRasterizer.initBuffers(600, 800);
  TriangleRasterizer.background(#0055FF);
}

void draw(){
  speed = 60.0/frameRate;
  modelAngle[1][1]+=3*speed;
  modelAngle[1][0]-=1.5*speed;
  if(modelAngle[1][1] >= 360 || modelAngle[1][1] <= -360)
    modelAngle[1][1] = 0;
  if(modelAngle[1][0] >= 360 || modelAngle[1][0] <= -360)
    modelAngle[1][0] = 0;
  modelAngle[0][0]-=1.5*speed;
  if(modelAngle[0][0] >= 360 || modelAngle[0][0] <= -360)
    modelAngle[0][0] = 0;
  modelAngle[0][2]-=1.5*speed;
  if(modelAngle[0][2] >= 360 || modelAngle[0][2] <= -360)
    modelAngle[0][2] = 0;
    
  modelAngle[2][1]-=0.5*speed;
  if(modelAngle[2][1] >= 360 || modelAngle[2][1] <= -360)
    modelAngle[2][1] = 0;
    
  modelAngle[3][0]+=0.5*speed;
  if(modelAngle[3][0] >= 360 || modelAngle[3][0] <= -360)
    modelAngle[3][0] = 0;
  modelAngle[3][1]+=0.5*speed;
  if(modelAngle[3][1] >= 360 || modelAngle[3][1] <= -360)
    modelAngle[3][1] = 0;
  
  modelAngle[6][1]+=5*speed;
  if(modelAngle[6][1] >= 360 || modelAngle[6][1] <= -360)
    modelAngle[6][1] = 0;
  
  //eyeAngle[2]+=1.5;
  //if(eyeAngle[2] >= 360)
  //  eyeAngle[2] = 0;
  //Rotating the large grey triangle
  for(int i = 0; i < testModels.length; i++){
    testModels[i].setPosition(modelPos[i][0], modelPos[i][1], modelPos[i][2]);
    testModels[i].setAngle(modelAngle[i][0], modelAngle[i][1], modelAngle[i][2]);
  }
  //testModels[1].setAngle(modelAngle[1][0], modelAngle[1][1], modelAngle[1][2]);
  //testModels[2].setAngle(modelAngle[2][0], modelAngle[2][1], modelAngle[2][2]);
  //testModels[3].setAngle(modelAngle[3][0], modelAngle[3][1], modelAngle[3][2]);
  //testModels[4].setAngle(modelAngle[4][0], modelAngle[4][1], modelAngle[4][2]);
  System.out.println(0.5*speed);
  if(keyPressed){
    //float[] returnPoints = {(triPoses[0][0][0]+triPoses[0][1][0]+triPoses[0][2][0])/3, 
    //                        (triPoses[0][0][1]+triPoses[0][1][1]+triPoses[0][2][1])/3,
    //                        (triPoses[0][0][2]+triPoses[0][1][2]+triPoses[0][2][2])/3};
    //float[][] newPoints = new float[3][3];
    //float[] tempPoses = {0, 0};
    //for(int i = 0; i < 3; i++){
      //newPoints[i][0] = triPoses[0][i][0]-returnPoints[0];
      //newPoints[i][1] = triPoses[0][i][1]-returnPoints[1];
      //newPoints[i][2] = triPoses[0][i][2]-returnPoints[2];
      switch(key){
        case 'w':
         //tempPoses[0] = newPoints[i][1];
         //tempPoses[1] = newPoints[i][2];
         //newPoints[i][1] = (float)(tempPoses[0]*Math.cos(-0.005)-tempPoses[1]*Math.sin(-0.005));
         //newPoints[i][2] = (float)(tempPoses[0]*Math.sin(-0.005)+tempPoses[1]*Math.cos(-0.005));
         modelPos[3][2]-=0.5*speed;
         break;
       case 's':
         //tempPoses[0] = newPoints[i][1];
         //tempPoses[1] = newPoints[i][2];
         //newPoints[i][1] = (float)(tempPoses[0]*Math.cos(0.005)-tempPoses[1]*Math.sin(0.005));
         //newPoints[i][2] = (float)(tempPoses[0]*Math.sin(0.005)+tempPoses[1]*Math.cos(0.005));
         modelPos[3][2]+=0.5*speed;
         break;
        case 'd':
          //tempPoses[0] = newPoints[i][0];
          //tempPoses[1] = newPoints[i][2];
          //newPoints[i][0] = (float)(tempPoses[0]*Math.cos(-0.005)+tempPoses[1]*Math.sin(-0.005));
          //newPoints[i][2] = (float)((-tempPoses[0]*Math.sin(-0.005))+tempPoses[1]*Math.cos(-0.005));
          modelPos[3][0]+=0.5*speed;
          break;
        case 'a':
          //tempPoses[0] = newPoints[i][0];
          //tempPoses[1] = newPoints[i][2];
          //newPoints[i][0] = (float)(tempPoses[0]*Math.cos(0.005)+tempPoses[1]*Math.sin(0.005));
          //newPoints[i][2] = (float)((-tempPoses[0]*Math.sin(0.005))+tempPoses[1]*Math.cos(0.005));
          modelPos[3][0]-=0.5*speed;
          break;
       
        case 'i':
          modelPos[3][1]-=0.5*speed;
          break;
        case 'k':
          modelPos[3][1]+=0.5*speed;
          break;
       }

  }
  

  drawTriangleList(this, testModels, 90, -5000, 5000, eyePos, eyeScale, eyeAngle);
 
  System.out.println("FRAME RATE: "+frameRate);
}
