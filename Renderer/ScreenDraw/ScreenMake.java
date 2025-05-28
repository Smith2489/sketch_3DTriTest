package Renderer.ScreenDraw;
import Wrapper.*;
import java.util.*;
import Maths.LinearAlgebra.*;
import Renderer.Objects.*;
public class ScreenMake{
    //Set up for the stencil test
    private static byte stencilComp = 0;
    private static char testType = 'e';
    private static Matrix proj = MVP.perspMatrix(1, 90, -1, 1); //The projection matrix
    /*Flags
    Bit 0: anti-aliasing enable (high enable)
    Bit 1: Solid background enable (low enable)
    Bit 2: Forced stroke enable (high enable)
    Bit 3: Forced fill enable (high enable)
    Bit 4: A billboard exists in model list (high enable)
    Bit 5: The billboarded sprite list is not empty (high enable)
    Bit 6: Projection is orthographic
    Bit 7: is interactive (actions will be executed)
    */
    private static byte flags = -120;
    private static int billBoardCountTranslucent = 0;
    private static int lineCountTranslucent = 0;
    private static int dotCountTranslucent = 0;
    //Data for tracking the number of triangles in the list, and how many are opaque and how many are translucent
    //private static int opaqueCount = 0;
    private static int translusentCount = 0;
    private static LinkedList<Triangle> triListOpaque = new LinkedList<Triangle>(); //Full triangle list
    private static LinkedList<Triangle> triListTranslucent = new LinkedList<Triangle>();
    private static LinkedList<Billboard> billboardDisplayOpaque = new LinkedList<Billboard>(); //List of opaque billboards
    private static LinkedList<Billboard> billboardDisplayTranslucent = new LinkedList<Billboard>(); //List of translucent billboards
    private static LinkedList<LineDisp> lineDisplayOpaque = new LinkedList<LineDisp>();
    private static LinkedList<LineDisp> lineDisplayTranslucent = new LinkedList<LineDisp>();
    private static LinkedList<Dot> dotDisplayOpaque = new LinkedList<Dot>();
    private static LinkedList<Dot> dotDisplayTranslucent = new LinkedList<Dot>();
    private static LinkedList<Model> modelList = new LinkedList<Model>(); //List of models
    private static LinkedList<Billboard> billboardList = new LinkedList<Billboard>(); //List of billboards
    private static LinkedList<LineObj> lineList = new LinkedList<LineObj>();
    private static LinkedList<Dot> dotList = new LinkedList<Dot>();
    private static LinkedList<Model> refListM = modelList;
    private static LinkedList<Billboard> refListB = billboardList;
    private static LinkedList<LineObj> refListL = lineList;
    private static LinkedList<Dot> refListD = dotList;
    private static Matrix billBoard;
    private static Matrix view;
    private static Model tempModel;
    private static LineObj tempLineObj;
    private static Triangle tempTri = new Triangle();
    private static Billboard tempBillboard = new Billboard();
    private static LineDisp tempLine = new LineDisp();
    private static Dot tempDot = new Dot();
    //Contains data pertaining to translucent objects
    //IDs (1 for triangle, 2 for billboarded sprite, 3 for line, 4 for dot)
    //Lighting will only interact with triangles and billboarded sprites
    //Z-positions
    //If z-position should be mirrored
    private static LinkedList<TranslucentData> translucentData = new LinkedList<TranslucentData>();
    //Will contain a set of counters for iterating over the lists of translucent objects
    //0 for translucent data objects, 1 for triangles, 2 for billboards, 3 for lines, 4 for points
    //When accessing the triangles, use displayList[opaqueCount+translucentCounters[1]]
    private static int translucentCounter = 0; 

    //Store prior computed vertex positions (instead of recomputing them, we can just copy and paste)
    //The integer will be the model list index plus the vertex index
    private static float[][] vertices;
    private static boolean[] frustumFlags;
    private static float[][] primativeVertices;
    //Same as above, but for brightness instead
    private static float[][] brightnessValues;

    //lighting stuff
    // private static float[] lightPos = new float[3];
    // private static float[] lightAngle = new float[3];
    private static Matrix lightMatrix = new Matrix();
    private static Matrix lightAngleMatrix = new Matrix();
    private static int size = 0;

    //Changes the model list's pointer to a different array's location and sets the size of the displayList
    public static void setModelList(LinkedList<Model> newList){
      modelList = newList;
      flags&=-17;
      for(int i = 0; i < modelList.size(); i++){
        tempModel = modelList.removeFirst();
        modelList.add(tempModel);
        if(tempModel.returnIsBillBoard())
          flags|=16;
      }
      triListOpaque.clear();
      triListTranslucent.clear();
    }
    public static void disableModelList(){
      modelList = refListM;
      flags&=-17;
    }

    //Changes the billboard list's pointer to a different array's location and sets the sizes of the display lists
    public static void setBillboardList(LinkedList<Billboard> newList){
      billboardList = newList;
      if(newList.size() > 0)
        flags|=32;
      else
        flags&=-33;
      billboardDisplayOpaque.clear();
      billboardDisplayTranslucent.clear();
    }
    public static void disableBillboardList(){
      billboardList = refListB;
      flags&=-33;
    }
    //Changes the line list's pointer to a different array's location and sets the sizes of the display lists
    public static void setLineList(LinkedList<LineObj> newList){
      lineList = newList;
      lineDisplayOpaque.clear();
      lineDisplayTranslucent.clear();
    }
    public static void disableLineList(){
      lineList = refListL;
    }
    public static void setDotList(LinkedList<Dot> newList){
      dotList = newList;
      dotDisplayOpaque.clear();
      dotDisplayTranslucent.clear();
    }
    public static void disableDotList(){
      dotList = refListD;
    }
    //Initializes projection matrix
    public static void usePersp(int wid, int heig, int fovY){
      flags&=-65;
      proj = MVP.perspMatrix((float)wid/heig, fovY, -1, 1);
    }
    public static void useOrtho(int wid, int heig, int fovY){
      flags|=64;
      proj = MVP.orthoMatrix((float)wid/heig, fovY, -1, 1);
    }
    //Sets the stencil test parameters
    public static void setStencilTest(byte newComp, char newTest){
      stencilComp = newComp;
      testType = newTest;
    }
    //Enables writing over the frame with solid pixels
    public static void solidBack(){
      flags&=-3;
      
    }
    //Disables writing over the frame with solid pixels
    public static void imgBack(){
      flags|=2;
    }
    //Disables the stroke
    public static void noStroke(){
      flags&=-5;
    }
    //Enables the stroke
    public static void stroke(){
      flags|=4;
    }
    //Disables the fill
    public static void noFill(){
      flags&=-9;
    }
    //Enables the fill
    public static void fill(){
      flags|=8;
    }
    public static void isInteractive(){
      flags|=-128;
    }
    public static void isNotInteractive(){
      flags&=127;
    }
    public static void setViewMatrix(Matrix newView){
      view = newView;
    }

    public static void setViewMatrix(Camera eye){
      MVP.setEyeAngles(eye.returnRotation());
      MVP.setEyePos(eye.returnPosition());
      MVP.setEyeScale(eye.returnScale());
      MVP.setEyeShear(eye.returnShear());
      view = MVP.viewMatrix();
    }

    public static void setViewMatrix(Camera eye, float[] newCamForward){
      MVP.setEyeAngles(eye.returnRotation());
      MVP.setEyePos(eye.returnPosition());
      MVP.setEyeScale(eye.returnScale());
      MVP.setEyeShear(eye.returnShear());
      view = MVP.viewMatrix();
    }
    //Takes in a frame buffer, near-z, far-z, and camera and draws a 3D scene using that data plus the model and billboard lists
    public static void drawScene(int[] screen, Camera eye){
      eye.computeInverseView();
      if((flags & -128) == -128 || eye.alwaysPerform())
        eye.executeActions();
      //Initializing translucent data
      translucentData.clear();
      translucentCounter = 0;

      //Sets the background
      if((flags & 2) == 0)
        Rasterizer.initBuffers(screen); //Solid colour
      else{
        Rasterizer.setFrameRef(screen);
        Rasterizer.initBuffers(); //Whatever is currently on the screen
      }
        
      //Sets up some of the MVP stuff

      //Computes the inverse of the view matrix
      if((flags & 48) != 0){
        MVP.setEyeScale(1, 1, 1);
        MVP.setEyePos(0, 0, 0);
        billBoard = MVP.inverseViewMatrix();
        MVP.setEyeScale(eye.returnScale());
        MVP.setEyePos(eye.returnPosition());
      }

      //Matrices for projection
      Matrix mvp = MatrixOperations.matrixMultiply(proj, view);
      Matrix mvpFull = new Matrix();
      //Matrix mv = new Matrix(); //For checking if a model is within zNear and zFar (using the frustum does not work for some reason)
      setupTrisNoLight(mvp, mvpFull, eye, eye.getDrawDistance());
      setupBillboardsNoLight(mvp, mvpFull, eye, eye.getDrawDistance());

      /*TODO: ADD IN LINE PROCESSING*/
      setupLines(mvp, mvpFull, eye, eye.getDrawDistance());
      setupDots(mvp, mvpFull, eye, eye.getDrawDistance());
      drawObjects(screen);
      for(int i = 0; i < Rasterizer.returnWidth()*Rasterizer.returnHeight(); i++)
        screen[i]|=0xFF000000;
    }

    public static void drawScene(int[] screen, Camera eye, int lightColour, float screenBrightness){
      eye.computeInverseView();
      if((flags & -128) == -128 || eye.alwaysPerform())
        eye.executeActions();
      //Initializing translucent data
      translucentData.clear();
      translucentCounter = 0;

      //Sets the background
      if((flags & 2) == 0)
        Rasterizer.initBuffers(screen);
      else{
        Rasterizer.setFrameRef(screen);
        Rasterizer.initBuffers();
      }
          
      //Sets up some of the MVP stuff

      //Computes the inverse of the view matrix
      if((flags & 48) != 0){
        MVP.setEyeScale(1, 1, 1);
        MVP.setEyePos(0, 0, 0);
        billBoard = MVP.inverseViewMatrix();
        MVP.setEyeScale(eye.returnScale());
        MVP.setEyePos(eye.returnPosition());
      }

      //Matrices for projection
      Matrix mvp = MatrixOperations.matrixMultiply(proj, view);
      Matrix mvpFull = new Matrix();
      //Matrix mv = new Matrix(); //For checking if a model is within zNear and zFar (using the frustum does not work for some reason)

      setupTrisNoLight(mvp, mvpFull, eye, eye.getDrawDistance());
      setupBillboardsNoLight(mvp, mvpFull, eye, eye.getDrawDistance());


      /*TODO: ADD IN LINE HANDLING */
      setupLines(mvp, mvpFull, eye, eye.getDrawDistance());
      setupDots(mvp, mvpFull, eye, eye.getDrawDistance());
    
      drawObjects(screen);
      setLightColour(screen, lightColour, screenBrightness, Rasterizer.returnWidth()*Rasterizer.returnHeight());
    }
    //The version for Blinn-Phong reflection and flat shading (1 normal per polygon)
    public static void drawScene(int[] screen, Camera eye, LinkedList<Light> lights, float generalObjectBrightness){
      eye.computeInverseView();
      if((flags & -128) == -128 || eye.alwaysPerform())
        eye.executeActions();
      generalObjectBrightness = Math.max(0, generalObjectBrightness);
      //Initializing translucent data
      translucentData.clear();
      billboardDisplayOpaque.clear();
      billboardDisplayTranslucent.clear();
      triListOpaque.clear();
      triListTranslucent.clear();

      translucentCounter = 0;


      //Sets the background
      if((flags & 2) == 0)
        Rasterizer.initBuffers(screen);
      else{
        Rasterizer.setFrameRef(screen);
        Rasterizer.initBuffers();
      }

        

      byte faceDirection = 1; //Tracks the direction of each face
      //Data for how many triangles are translucent
      translusentCount = 0;
          
      //Sets up some of the MVP stuff
      //Computes the inverse of the view matrix
      if((flags & 48) != 0){
        MVP.setEyeScale(1, 1, 1);
        MVP.setEyePos(0, 0, 0);
        billBoard = MVP.inverseViewMatrix();
        MVP.setEyeScale(eye.returnScale());
        MVP.setEyePos(eye.returnPosition());
      }
      float[][] lightPos = new float[lights.size()][3];
      float[][] lightAngle = new float[lights.size()][3];
      float[][][] lightColour = new float[lights.size()][3][3];

      float[] invCamColour = eye.returnInvColour(lights.getFirst().returnLightColour((byte)0));
      //Matrices for projection
      Matrix mvp = MatrixOperations.matrixMultiply(proj, view);
      Matrix mvpFull = new Matrix();
      //Matrix mv = new Matrix(); //For checking if a model is within zNear and zFar (using the frustum does not work for some reason)
      boolean isInClipSpace = false; //Checks if a model is in the frustum
      size = lights.size();
      for(int i = 0; i < size; i++){
        Light light = lights.removeFirst();
        lights.add(light);
        //Calcluating where the light and the camera should be relative to everything else
        light.computeDirection();
        if(light.returnType() == 'p')
          lightMatrix = MatrixOperations.matrixMultiply(view, from3DVecTo4DVec(light.returnPosition()));
        else if(light.returnType() == 'd')
          lightAngleMatrix = MatrixOperations.matrixMultiply(view, from3DVecTo4DVec(light.returnLightDirection(), 0));
        else{
          lightMatrix = MatrixOperations.matrixMultiply(view, from3DVecTo4DVec(light.returnPosition()));
          lightAngleMatrix = MatrixOperations.matrixMultiply(view, from3DVecTo4DVec(light.returnLightDirection(), 0));
        }
        lightColour[i] = light.returnLightColour();
        for(int j = 0; j < 3; j++){
          lightColour[i][j][0]*=invCamColour[0];
          lightColour[i][j][1]*=invCamColour[1];
          lightColour[i][j][2]*=invCamColour[2];
        }
        lightPos[i][0] = lightMatrix.returnData(0, 0);
        lightPos[i][1] = lightMatrix.returnData(1, 0);
        lightPos[i][2] = lightMatrix.returnData(2, 0);
        lightAngle[i][0] = lightAngleMatrix.returnData(0, 0);
        lightAngle[i][1] = lightAngleMatrix.returnData(1, 0);
        lightAngle[i][2] = lightAngleMatrix.returnData(2, 0);
        lightAngle[i] = VectorOperations.vectorNormalization3D(lightAngle[i]);
        if((flags & -128) == -128 || light.alwaysPerform())
          light.executeActions();
      }

      size = modelList.size();
      for(int i = 0; i < size; i++){
        tempModel = modelList.removeFirst();
        modelList.add(tempModel);

        vertices = new float[tempModel.returnVertexNormals().length][];
        frustumFlags = new boolean[vertices.length];
        primativeVertices = new float[vertices.length][4];
        brightnessValues = new float[vertices.length][3];
        float[] fromCamToModel = {(tempModel.returnModelCentre()[0]+tempModel.returnPosition()[0])-eye.returnPosition()[0],
                                  (tempModel.returnModelCentre()[1]+tempModel.returnPosition()[1])-eye.returnPosition()[1],
                                  (tempModel.returnModelCentre()[2]+tempModel.returnPosition()[2])-eye.returnPosition()[2]};
        float distCamToModel = (float)Math.sqrt(fromCamToModel[0]*fromCamToModel[0]+fromCamToModel[1]*fromCamToModel[1]+fromCamToModel[2]*fromCamToModel[2]);
        float[] position = {tempModel.returnPosition()[0], tempModel.returnPosition()[1], tempModel.returnPosition()[2]};
        //Sets up the matrices for the current model
        faceDirection = 1;
        if(tempModel.returnAttachedToCamera())
          attachObjectToCamera(tempModel.returnPosition(), eye);
        tempModel.setModelMatrix();
        Matrix model;
        //For if the model is not a billboard
        if(!tempModel.returnIsBillBoard())
          model = MatrixOperations.matrixMultiply(view, tempModel.returnModelMatrix());
        //For if the model is a billboard
        else{
          //Constructs the transformation matrix for the point
          model = MatrixOperations.matrixMultiply(view, MVP.returnTranslation(tempModel.returnPosition()));    
          model.copy(MatrixOperations.matrixMultiply(model, billBoard));       
          //Enables rotation about the z-axis and scaling along the x and y axis
          Matrix modelMatrix = MatrixOperations.matrixMultiply(MVP.returnRotation(0, 0, tempModel.returnAngle()[2]), MVP.returnScale(tempModel.returnScale()[0], tempModel.returnScale()[1], 1));
          model = MatrixOperations.matrixMultiply(model, modelMatrix);
        }
        mvpFull = MatrixOperations.matrixMultiply(proj, model); 

        //Checking if the model is in clipspace and adjusting the face direction to account for negative scales
        isInClipSpace = ((flags & 64) == 64) || (((isInClipSpace(mvp, tempModel.returnPosition()) || isInClipSpace(mvpFull, tempModel.returnBoundingBox())) && distCamToModel <= eye.getDrawDistance()));
        tempModel.setPosition(position[0], position[1], position[2]);
        //System.out.println(i+" "+tempModel.alwaysPerform());
        if((flags & -128) == -128 || tempModel.alwaysPerform())
          tempModel.executeActions();
        for(byte j = 0; j < 3; j++){
          if(tempModel.returnScale()[j] < 0)
            faceDirection = (byte)((~faceDirection)+1);
          if(eye.returnScale()[j] < 0)
            faceDirection = (byte)((~faceDirection)+1);
        }

      
        if(isInClipSpace && tempModel.returnModelTint() > 0){

          for(int j = 0; j < tempModel.returnPolygonCount(); j++){
            float[] triCentre = {0, 0, 0, 0};
            //Breaks out of the loops if the triangle count is greater than or equal to triListSize
            float edgeDir = 0; //The direction of the triangle
            float[][] points = new float[3][4]; //Triangle vertices
            boolean isInside = ((flags & 64) == 64); //If the triangle is inside the overal scene
            boolean inFrustum = false; //If the triangle's point is in front of the near plane
            byte numOfInside = 0; //Number of points in the near plane
            byte[] insidePoints = {-1, -1, -1}; //Tracking the indices of the points in the near plane
            float[][] vertexBrightness = {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}};
            for(byte s = 0; s < 3; s++){
              //Takes the current point and turns it into a homeogenous vector
              float[] homogeneousPoint = from3DVecTo4DVec(tempModel.returnPoints()[tempModel.returnPolygons()[j][s]]);
              Matrix projection; 
              
              int vertexIndex = tempModel.returnPolygons()[j][s];
              if(vertices[vertexIndex] == null){
                //projection = MatrixOperations.matrixMultiply(model, homogeneousPoint);
                if(!tempModel.isCompletelyBlack() && tempModel.isGauroud()){
                  vertexBrightness[s] = computeLighting(lights, lightPos, lightAngle, lightColour, homogeneousPoint, tempModel.returnVertexNormals()[vertexIndex], tempModel.returnShininess(), tempModel.returnBrightness()*generalObjectBrightness, model, tempModel, j, false);
                  brightnessValues[vertexIndex][0] = vertexBrightness[s][0];
                  brightnessValues[vertexIndex][1] = vertexBrightness[s][1];
                  brightnessValues[vertexIndex][2] = vertexBrightness[s][2];
                }

                projection = MatrixOperations.matrixMultiply(mvpFull, homogeneousPoint);

                //Copying the results of the projection to a set of points that are less tedious to work with
                points[s][0] = (projection.returnData(0, 0)-0.0001f); //x
                points[s][1] = (projection.returnData(1, 0)-0.0001f); //y
                points[s][2] = (projection.returnData(2, 0)-0.0001f); //z
                points[s][3] = (projection.returnData(3, 0)-0.0001f); //w

                triCentre[0]+=points[s][0];
                triCentre[1]+=points[s][1];
                triCentre[2]+=points[s][2];
                triCentre[3]+=points[s][3];
                primativeVertices[vertexIndex][0] = points[s][0];
                primativeVertices[vertexIndex][1] = points[s][1];
                primativeVertices[vertexIndex][2] = points[s][2];
                primativeVertices[vertexIndex][3] = points[s][3];
                inFrustum = (points[s][2] >= -points[s][3] && points[s][2] <= points[s][3]) || ((flags & 64) == 64);
                //Homogeneous division
                if(Math.abs(points[s][3]) > 0){
                  points[s][0] = (points[s][0]/points[s][3]);
                  points[s][1] = (points[s][1]/points[s][3]);
                  points[s][2] = (points[s][2]/points[s][3]);
                }
                //System.out.println(i+" "+vertexIndex+" "+points[s][2]);
                //Adjusts point positions to place the screen origin at the centre of the canvas, with scaling with respect to the screen dimensions
                points[s][0] = (0.5f*Rasterizer.returnWidth()*(points[s][0]+1)-0.0001f);
                points[s][1] = (0.5f*Rasterizer.returnHeight()*(points[s][1]+1)-0.0001f);
                  
                //Tracks which points are in front of the near plane or behind the near plane
                if(inFrustum){
                  insidePoints[numOfInside] = s;
                  numOfInside++;
                }
                else
                  insidePoints[2] = s;
                isInside|=inFrustum;
                vertices[vertexIndex] = new float[4];
                vertices[vertexIndex][0] = points[s][0];
                vertices[vertexIndex][1] = points[s][1];
                vertices[vertexIndex][2] = points[s][2];
                vertices[vertexIndex][3] = points[s][3];
                frustumFlags[vertexIndex] = inFrustum;
              }
              else{
                points[s][0] = vertices[vertexIndex][0];
                points[s][1] = vertices[vertexIndex][1];
                points[s][2] = vertices[vertexIndex][2];
                points[s][3] = vertices[vertexIndex][3];
                triCentre[0]+=primativeVertices[vertexIndex][0];
                triCentre[1]+=primativeVertices[vertexIndex][1];
                triCentre[2]+=primativeVertices[vertexIndex][2];
                triCentre[3]+=primativeVertices[vertexIndex][3];
                vertexBrightness[s][0] = brightnessValues[vertexIndex][0];
                vertexBrightness[s][1] = brightnessValues[vertexIndex][1];
                vertexBrightness[s][2] = brightnessValues[vertexIndex][2];
                if(frustumFlags[vertexIndex]){
                  insidePoints[numOfInside] = s;
                  numOfInside++;
                }
                else
                  insidePoints[2] = s;
                isInside|=frustumFlags[vertexIndex];
              }
            }

            triCentre[0]*=0.33333333333333333333333333333333333333f;
            triCentre[1]*=0.33333333333333333333333333333333333333f;
            triCentre[2]*=0.33333333333333333333333333333333333333f;
            triCentre[3]*=0.33333333333333333333333333333333333333f;

            isInside|=(triCentre[0] >= -triCentre[3] && triCentre[0] <= triCentre[3] && triCentre[1] >= -triCentre[3] && triCentre[1] <= triCentre[3] && triCentre[2] >= -triCentre[3] && triCentre[2] <= triCentre[3]);
            //Copies the triangle from the model to the triangle array
            if(isInside){
              int backIndex = tempModel.returnPalletPtr().returnBackTri(j);
              if(!tempModel.isCompletelyBlack() && !tempModel.isGauroud()){
                //Computes flat shading
                float[] lightBrightness = computeLighting(lights, lightPos, lightAngle, lightColour, triCentre, tempModel.returnNormals(j), tempModel.returnShininess(), tempModel.returnBrightness()*generalObjectBrightness, model, tempModel, j, true);

                vertexBrightness[0][0] = lightBrightness[0];
                vertexBrightness[0][1] = lightBrightness[1];
                vertexBrightness[0][2] = lightBrightness[2];
                vertexBrightness[1][0] = lightBrightness[0];
                vertexBrightness[1][1] = lightBrightness[1];
                vertexBrightness[1][2] = lightBrightness[2];
                vertexBrightness[2][0] = lightBrightness[0];
                vertexBrightness[2][1] = lightBrightness[1];
                vertexBrightness[2][2] = lightBrightness[2];
                
              }
              //Returns if the current triangle is exempt from backface culling
              int[] colour = {tempModel.returnColours()[j][0], tempModel.returnColours()[j][1]}; //Holds the colours that get sent to the triangle rasterizer
              short[] alpha = {(short)((colour[0] >>> 24)*tempModel.returnModelTint()), (short)((colour[1] >>> 24)*tempModel.returnModelTint())};
              //Adding triangles to the list with near-plane clipping (WHOOP WHOOP!!!)
              float[] intersect1 = {0, 0, 0, 0}; //Coordinates for the first point of intersection between the triangle and the near plane
              float[] intersect2 = {0, 0, 0, 0}; //Coordinates for the second point of intersection between the triangle and the near plane
              float t1 = 0; //How far up side AB is the intersection
              float t2 = 0; //How far up side AC is the intersection
              //Code for handling triangles which clip the near plane
              switch(numOfInside){
                //When 1 point is inside the frustum
                case 1:
                  //Calculating how far up each side the intersection occurs
                  byte otherPoint1 = (byte)((insidePoints[0]+1)%3);
                  byte otherPoint2 = (byte)((insidePoints[0]+2)%3);
                  t1 = (1 - points[insidePoints[0]][2]);
                  if(Math.abs(points[otherPoint1][2] - points[insidePoints[0]][2]) > 0) 
                    t1/=(points[otherPoint1][2]-points[insidePoints[0]][2]);
                  t2 = (1 - points[insidePoints[0]][2]);
                  if(Math.abs(points[otherPoint2][2] - points[insidePoints[0]][2]) > 0) 
                    t2/=(points[otherPoint2][2]-points[insidePoints[0]][2]);
                      
                  //Computing the location of the point of intersection for each intersecting side
                  intersect1[0] = points[insidePoints[0]][0] + t1*(points[otherPoint1][0]-points[insidePoints[0]][0]);
                  intersect1[1] = points[insidePoints[0]][1] + t1*(points[otherPoint1][1]-points[insidePoints[0]][1]);
                  intersect1[2] = points[insidePoints[0]][2] + t1*(points[otherPoint1][2]-points[insidePoints[0]][2]);
                  intersect1[3] = points[insidePoints[0]][3] + t1*(points[otherPoint1][3]-points[insidePoints[0]][3]);

                  intersect2[0] = points[insidePoints[0]][0] + t2*(points[otherPoint2][0]-points[insidePoints[0]][0]);
                  intersect2[1] = points[insidePoints[0]][1] + t2*(points[otherPoint2][1]-points[insidePoints[0]][1]);
                  intersect2[2] = points[insidePoints[0]][2] + t2*(points[otherPoint2][2]-points[insidePoints[0]][2]);
                  intersect2[3] = points[insidePoints[0]][3] + t2*(points[otherPoint2][3]-points[insidePoints[0]][3]);
                  float[] vertexBrightness1 = {vertexBrightness[insidePoints[0]][0] + t1*(vertexBrightness[otherPoint1][0]-vertexBrightness[insidePoints[0]][0]),
                                               vertexBrightness[insidePoints[0]][1] + t1*(vertexBrightness[otherPoint1][1]-vertexBrightness[insidePoints[0]][1]),
                                               vertexBrightness[insidePoints[0]][2] + t1*(vertexBrightness[otherPoint1][2]-vertexBrightness[insidePoints[0]][2])};
                  float[] vertexBrightness2 = {vertexBrightness[insidePoints[0]][0] + t2*(vertexBrightness[otherPoint2][0]-vertexBrightness[insidePoints[0]][0]),
                                               vertexBrightness[insidePoints[0]][1] + t2*(vertexBrightness[otherPoint2][1]-vertexBrightness[insidePoints[0]][1]),
                                               vertexBrightness[insidePoints[0]][2] + t2*(vertexBrightness[otherPoint2][2]-vertexBrightness[insidePoints[0]][2])};
                  //Moving the points that are behind the near plane to be at the points of intersection
                  points[otherPoint1][0] = intersect1[0];
                  points[otherPoint1][1] = intersect1[1];
                  points[otherPoint1][2] = intersect1[2];
                  points[otherPoint1][3] = intersect1[3];
                      
                  points[otherPoint2][0] = intersect2[0];
                  points[otherPoint2][1] = intersect2[1];
                  points[otherPoint2][2] = intersect2[2];
                  points[otherPoint2][3] = intersect2[3];

                  vertexBrightness[otherPoint1][0] = vertexBrightness1[0];
                  vertexBrightness[otherPoint1][1] = vertexBrightness1[1];
                  vertexBrightness[otherPoint1][2] = vertexBrightness1[2];
                  vertexBrightness[otherPoint2][0] = vertexBrightness2[0];
                  vertexBrightness[otherPoint2][1] = vertexBrightness2[1];
                  vertexBrightness[otherPoint2][2] = vertexBrightness2[2];
                  break;
                //When 2 points are inside the frustum
                case 2:
                  //Calculating how far up each side the points of intersection are
                  t1 = (1 - points[insidePoints[0]][2]);
                  if(Math.abs(points[insidePoints[2]][2] - points[insidePoints[0]][2]) > 0) 
                    t1/=(points[insidePoints[2]][2]-points[insidePoints[0]][2]);
                  t2 = (1 - points[insidePoints[1]][2]);
                  if(Math.abs(points[insidePoints[2]][2] - points[insidePoints[1]][2]) > 0) 
                    t2/=(points[insidePoints[2]][2]-points[insidePoints[1]][2]);
                  
                  float[][] finalBrightness = new float[3][3];
                  //Calculating where each point of intersection is
                  intersect1[0] = points[insidePoints[0]][0] + t1*(points[insidePoints[2]][0]-points[insidePoints[0]][0]);
                  intersect1[1] = points[insidePoints[0]][1] + t1*(points[insidePoints[2]][1]-points[insidePoints[0]][1]);
                  intersect1[2] = points[insidePoints[0]][2] + t1*(points[insidePoints[2]][2]-points[insidePoints[0]][2]);
                  intersect1[3] = points[insidePoints[0]][3] + t1*(points[insidePoints[2]][3]-points[insidePoints[0]][3]);
                  finalBrightness[insidePoints[0]][0] = vertexBrightness[insidePoints[0]][0] + t1*(vertexBrightness[insidePoints[2]][0]-vertexBrightness[insidePoints[0]][0]);
                  finalBrightness[insidePoints[0]][1] = vertexBrightness[insidePoints[0]][1] + t1*(vertexBrightness[insidePoints[2]][1]-vertexBrightness[insidePoints[0]][1]);
                  finalBrightness[insidePoints[0]][2] = vertexBrightness[insidePoints[0]][2] + t1*(vertexBrightness[insidePoints[2]][2]-vertexBrightness[insidePoints[0]][2]);

                  intersect2[0] = points[insidePoints[1]][0] + t2*(points[insidePoints[2]][0]-points[insidePoints[1]][0]);
                  intersect2[1] = points[insidePoints[1]][1] + t2*(points[insidePoints[2]][1]-points[insidePoints[1]][1]);
                  intersect2[2] = points[insidePoints[1]][2] + t2*(points[insidePoints[2]][2]-points[insidePoints[1]][2]);
                  intersect2[3] = points[insidePoints[1]][3] + t2*(points[insidePoints[2]][3]-points[insidePoints[1]][3]);
                  finalBrightness[insidePoints[2]][0] = vertexBrightness[insidePoints[1]][0] + t2*(vertexBrightness[insidePoints[2]][0]-vertexBrightness[insidePoints[1]][0]);
                  finalBrightness[insidePoints[2]][1] = vertexBrightness[insidePoints[1]][1] + t2*(vertexBrightness[insidePoints[2]][1]-vertexBrightness[insidePoints[1]][1]);
                  finalBrightness[insidePoints[2]][2] = vertexBrightness[insidePoints[1]][2] + t2*(vertexBrightness[insidePoints[2]][2]-vertexBrightness[insidePoints[1]][2]);
                  float[][] secondPoints = new float[3][4];
                  //Constructing a triangle B'C'C
                  //C
                  secondPoints[insidePoints[1]][0] = points[insidePoints[1]][0];
                  secondPoints[insidePoints[1]][1] = points[insidePoints[1]][1];
                  secondPoints[insidePoints[1]][2] = points[insidePoints[1]][2];
                  secondPoints[insidePoints[1]][3] = points[insidePoints[1]][3];
                  finalBrightness[insidePoints[1]][0] = vertexBrightness[insidePoints[1]][0];
                  finalBrightness[insidePoints[1]][1] = vertexBrightness[insidePoints[1]][1];
                  finalBrightness[insidePoints[1]][2] = vertexBrightness[insidePoints[1]][2];
                  //B'
                  secondPoints[insidePoints[0]][0] = intersect1[0];
                  secondPoints[insidePoints[0]][1] = intersect1[1];
                  secondPoints[insidePoints[0]][2] = intersect1[2];
                  secondPoints[insidePoints[0]][3] = intersect1[3];
                  //C'
                  secondPoints[insidePoints[2]][0] = intersect2[0];
                  secondPoints[insidePoints[2]][1] = intersect2[1];
                  secondPoints[insidePoints[2]][2] = intersect2[2];
                  secondPoints[insidePoints[2]][3] = intersect2[3];
                  //Modifiying the original triangle to be BCB'
                  points[insidePoints[2]][0] = intersect1[0];
                  points[insidePoints[2]][1] = intersect1[1];
                  points[insidePoints[2]][2] = intersect1[2];
                  points[insidePoints[2]][3] = intersect1[3];
                  vertexBrightness[insidePoints[2]][0] = finalBrightness[insidePoints[0]][0];
                  vertexBrightness[insidePoints[2]][1] = finalBrightness[insidePoints[0]][1];
                  vertexBrightness[insidePoints[2]][2] = finalBrightness[insidePoints[0]][2];
                  //Adding the new triangle to the list to account for the clipped triangle being a quad
                  edgeDir = returnEdgeDir(tempModel, secondPoints, colour, alpha, backIndex, faceDirection, (flags & 8) == 8, (flags & 4) == 4);
                  if(edgeDir > 0){
                    if((alpha[1] & 0xFF) < 0xFF){
                      triListTranslucent.add(new Triangle(secondPoints, colour[0] , colour[1], tempModel.returnHasStroke() || (flags & 12) != 8, tempModel.returnHasFill() && (flags & 8) == 8));
                      triListTranslucent.getLast().setDepthWrite(!tempModel.returnNoDepth());
                      triListTranslucent.getLast().setVertexBrightness(finalBrightness);
                      triListTranslucent.getLast().setAlpha(alpha[0], (byte)0);
                      triListTranslucent.getLast().setAlpha(alpha[1], (byte)1);
                      triListTranslucent.getLast().setFizzel(tempModel.returnMaxFizzel(), tempModel.returnFizzelThreshold());
                      translucentData.add(new TranslucentData((byte)1, triListTranslucent.getLast().getAverageZ(), tempModel.returnNoDepth(), translusentCount));
                      translusentCount++;
                      translucentCounter++;
                    }
                    else{
                      triListOpaque.add(new Triangle(secondPoints, colour[0], colour[1], tempModel.returnHasStroke() || (flags & 12) != 8, tempModel.returnHasFill() && (flags & 8) == 8));
                      triListOpaque.getLast().setDepthWrite(!tempModel.returnNoDepth());
                      triListOpaque.getLast().setAlpha(alpha[0], (byte)0);
                      triListOpaque.getLast().setAlpha(alpha[1], (byte)1);
                      triListOpaque.getLast().setVertexBrightness(finalBrightness);
                      triListOpaque.getLast().setFizzel(tempModel.returnMaxFizzel(), tempModel.returnFizzelThreshold());
                    }
                  }
                break;
              }
                
              //Adding the triangle to the list
              edgeDir = returnEdgeDir(tempModel, points, colour, alpha, backIndex, faceDirection, (flags & 8) == 8, (flags & 4) == 4);
              if(edgeDir > 0){
                if((colour[1]  >>> 24) < 0xFF){
                  triListTranslucent.add(new Triangle(points, colour[0] , colour[1] , tempModel.returnHasStroke() || (flags & 12) != 8, tempModel.returnHasFill() && (flags & 8) == 8));
                  triListTranslucent.getLast().setDepthWrite(!tempModel.returnNoDepth());
                  triListTranslucent.getLast().setVertexBrightness(vertexBrightness);
                  triListTranslucent.getLast().setAlpha(alpha[0], (byte)0);
                  triListTranslucent.getLast().setAlpha(alpha[1], (byte)1);
                  triListTranslucent.getLast().setFizzel(tempModel.returnMaxFizzel(), tempModel.returnFizzelThreshold());
                  translucentData.add(new TranslucentData((byte)1, triListTranslucent.getLast().getAverageZ(), tempModel.returnNoDepth(), translusentCount));
                  translusentCount++;
                  translucentCounter++;
                }
                else{
                  triListOpaque.add(new Triangle(points, colour[0], colour[1], tempModel.returnHasStroke() || (flags & 12) != 8, tempModel.returnHasFill() && (flags & 8) == 8));
                  triListOpaque.getLast().setDepthWrite(!tempModel.returnNoDepth());
                  triListOpaque.getLast().setVertexBrightness(vertexBrightness);
                  triListOpaque.getLast().setAlpha(alpha[0], (byte)0);
                  triListOpaque.getLast().setAlpha(alpha[1], (byte)1);
                  triListOpaque.getLast().setFizzel(tempModel.returnMaxFizzel(), tempModel.returnFizzelThreshold());
                }
              }
            }
          }
        }
      }

      billBoardCountTranslucent = 0;
      size = billboardList.size();
      for(int i = 0; i < size; i++){
        tempBillboard = billboardList.removeFirst();
        billboardList.add(tempBillboard);

        float[] fromCamToBillboard = {tempBillboard.returnPosition()[0]-eye.returnPosition()[0],
                                      tempBillboard.returnPosition()[1]-eye.returnPosition()[1],
                                      tempBillboard.returnPosition()[2]-eye.returnPosition()[2]};
        float distCamToBillboard = (float)Math.sqrt(fromCamToBillboard[0]*fromCamToBillboard[0]+fromCamToBillboard[1]*fromCamToBillboard[1]+fromCamToBillboard[2]*fromCamToBillboard[2]);
        float[] position = {tempBillboard.returnPosition()[0], tempBillboard.returnPosition()[1], tempBillboard.returnPosition()[2]};
        //Sets up the model matrix and the MVP matrices
        if(tempBillboard.isAttachedToCamera())
          attachObjectToCamera(tempBillboard.returnPosition(), eye);
        tempBillboard.setBillBoardModelMatrix();
        if(distCamToBillboard <= eye.getDrawDistance() && tempBillboard.returnModelTint() > 0){
          


          //Constructs the transformation matrix for the point
          Matrix model = MatrixOperations.matrixMultiply(view, MVP.returnTranslation(tempBillboard.returnPosition()));
          model.copy(MatrixOperations.matrixMultiply(model, billBoard));
          //Multiplying the transformed matrices by the scale of the model
          model.copy(MatrixOperations.matrixMultiply(model, MVP.returnScale(tempBillboard.returnScale()[0], tempBillboard.returnScale()[1], 1)));
          mvpFull = MatrixOperations.matrixMultiply(proj, model);
          tempBillboard.setPosition(position);

          float[][] points = {{-(tempBillboard.returnWidth() >>> 1), -(tempBillboard.returnHeight() >>> 1), 0, 1}, 
                              {(tempBillboard.returnWidth() >>> 1), -(tempBillboard.returnHeight() >>> 1), 0, 1},
                              {(tempBillboard.returnWidth() >>> 1), (tempBillboard.returnHeight() >>> 1), 0, 1}, 
                              {-(tempBillboard.returnWidth() >>> 1), (tempBillboard.returnHeight() >>> 1), 0, 1}};
          boolean isInside = ((flags & 64) == 64);
          for(byte j = 0; j < 4; j++){
            //Projects the point from 3D to 2D
            Matrix projection = MatrixOperations.matrixMultiply(mvpFull, points[j]);
            points[j][0] = projection.returnData(0, 0) - 0.0001f;
            points[j][1] = projection.returnData(1, 0) - 0.0001f;
            points[j][2] = projection.returnData(2, 0) - 0.0001f;
            points[j][3] = projection.returnData(3, 0) - 0.0001f;
            if(points[j][3] > 0){
              points[j][0]/=points[j][3];
              points[j][1]/=points[j][3];
              points[j][2]/=points[j][3];
            }
            points[j][0] = (0.5f*Rasterizer.returnWidth())*(points[j][0]+1)-0.0001f;
            points[j][1] = (0.5f*Rasterizer.returnHeight())*(points[j][1]+1)-0.0001f;
            isInside|=(points[j][3] > 0);
          }
    
          //Calculates the minimum x and y coordinates and the maximum x and y coordinates
          float[] minPoints = {Math.min(points[0][0], Math.min(points[1][0], Math.min(points[2][0], points[3][0]))),
                              Math.min(points[0][1], Math.min(points[1][1], Math.min(points[2][1], points[3][1])))};
          float[] maxPoints = {Math.max(points[0][0], Math.max(points[1][0], Math.max(points[2][0], points[3][0]))),
                              Math.max(points[0][1], Math.max(points[1][1], Math.max(points[2][1], points[3][1])))};
          //Checks if any part of the image is on the screen
          isInside&=((minPoints[0] < Rasterizer.returnWidth() && minPoints[1] < Rasterizer.returnHeight()) && (maxPoints[0] >= 0 && maxPoints[1] >= 0));
          if(isInside){
            int fill = tempBillboard.returnFill();
            short[] alpha = {(short)((tempBillboard.returnStroke() >>> 24)*tempBillboard.returnModelTint()), 
                             (short)((tempBillboard.returnFill() >>> 24)*tempBillboard.returnModelTint())};
            if((fill & 0xFFFFFF) != 0){
              float[] lightBrightness = computeLighting(lights, lightPos, lightAngle, lightColour, from3DVecTo4DVec(tempBillboard.returnPosition()), (float[])null, tempBillboard.returnShininess(), tempBillboard.returnBrightness()*generalObjectBrightness, model, (Model)null, 0, false);
              int[] tempFill = {(int)(Math.min(255, ((fill >>> 16) & 0xFF)*lightBrightness[0])) << 16,
                                (int)(Math.min(255, ((fill >>> 8) & 0xFF)*lightBrightness[1])) << 8,
                                (int)(Math.min(255, (fill & 0xFF)*lightBrightness[2]))};
              fill = (fill & 0xFF000000)|tempFill[0]|tempFill[1]|tempFill[2];
            }
            float sizeX = points[2][0]-points[0][0];
            float sizeY = points[2][1]-points[0][1];
            if((alpha[1] & 0xFF) == 255){
              billboardDisplayOpaque.add(new Billboard());
              billboardDisplayOpaque.getLast().copy(tempBillboard);
              billboardDisplayOpaque.getLast().setScale(sizeX, sizeY);
              billboardDisplayOpaque.getLast().setPosition(points[0][0], points[0][1], points[0][2]);
              billboardDisplayOpaque.getLast().setOutline(tempBillboard.hasOutline() || (flags & 12) != 8);
              billboardDisplayOpaque.getLast().setInside(tempBillboard.hasImage() && (flags & 8) == 8);
              billboardDisplayOpaque.getLast().fill(fill, alpha[1]);
              billboardDisplayOpaque.getLast().stroke(tempBillboard.returnStroke(), alpha[0]);
            }
            else{
              billboardDisplayTranslucent.add(new Billboard());
              billboardDisplayTranslucent.getLast().copy(tempBillboard);
              billboardDisplayTranslucent.getLast().setScale(sizeX, sizeY);
              billboardDisplayTranslucent.getLast().setPosition(points[0][0], points[0][1], points[0][2]);
              billboardDisplayTranslucent.getLast().setOutline(tempBillboard.hasOutline() || (flags & 12) != 8);
              billboardDisplayTranslucent.getLast().setInside(tempBillboard.hasImage() && (flags & 8) == 8);
              billboardDisplayTranslucent.getLast().fill(fill, alpha[1]);
              billboardDisplayTranslucent.getLast().stroke(tempBillboard.returnStroke(), alpha[0]);
              translucentData.add(new TranslucentData((byte)2, points[0][2], tempBillboard.noDraw(), billBoardCountTranslucent));
              translucentCounter++;
              billBoardCountTranslucent++;
            }
          }
        }
        if((flags & -128) == -128 || tempBillboard.alwaysPerform())
          tempBillboard.executeActions();
      }

      /*TODO: ADD IN LINE HANDLING */
      setupLines(mvp, mvpFull, eye, eye.getDrawDistance());
      setupDots(mvp, mvpFull, eye, eye.getDrawDistance());
      drawObjects(screen);
      setLightColour(screen, eye.returnColour(), 1, Rasterizer.returnWidth()*Rasterizer.returnHeight());
    }

    private static void drawObjects(int[] screen){
      while(!dotDisplayOpaque.isEmpty()){
        tempDot = dotDisplayOpaque.removeFirst();
        Rasterizer.setPixel(tempDot.returnStroke(), (int)tempDot.returnPosition()[0], (int)tempDot.returnPosition()[1], tempDot.returnPosition()[2], tempDot.returnDepthDisable());
      }
      while(!lineDisplayOpaque.isEmpty()){
        tempLine = lineDisplayOpaque.removeFirst();
        Rasterizer.drawLine(new IntWrapper(Math.round(tempLine.returnEndPoints()[0][0])), new IntWrapper(Math.round(tempLine.returnEndPoints()[0][1])), tempLine.returnEndPoints()[0][2], new IntWrapper(Math.round(tempLine.returnEndPoints()[1][0])), new IntWrapper(Math.round(tempLine.returnEndPoints()[1][1])), tempLine.returnEndPoints()[1][2], tempLine.returnStroke(), tempLine.returnDepthDisable());
      }
      while(!billboardDisplayOpaque.isEmpty()){
        tempBillboard = billboardDisplayOpaque.removeFirst();
        Rasterizer.setProbabilities(tempBillboard.returnMaxFizzel(), tempBillboard.returnFizzelThreshold());
        Rasterizer.billBoardDraw(tempBillboard, tempBillboard.returnPosition()[0], tempBillboard.returnPosition()[1], tempBillboard.returnPosition()[2], tempBillboard.returnScale()[0], tempBillboard.returnScale()[1], stencilComp, testType);
      }
      //Iterates over all current triangles and draws them to the screen
      while(!triListOpaque.isEmpty()){
        tempTri = triListOpaque.removeFirst();
        Rasterizer.setProbabilities(tempTri.returnMaxFizzel(), tempTri.returnFizzelThreshold());
        Rasterizer.setVertexBrightness(tempTri.returnVertexBrightness());
        Rasterizer.setDepthWrite(tempTri.getHasDepthWrite());
        Rasterizer.draw(tempTri, stencilComp, testType);
      }
      sortAndDrawTranslucent(screen);
    }

    //Pushes items in the translucent lists to the end if they are "no draw" (Must be called after the merge sort)
    //If there are any 0 ids in the main ID list, this also must be called before copying items from the temp lists 
    //back to the main lists
    public static void pushToRear(TranslucentData[] td){
      for(int i = 0; i < td.length; i++){
        if(td[i].returnNoDepth()){
          TranslucentData tempTD = td[i];
          for(int j = i+1; j < td.length; j++){
            td[j-1] = td[j];
          }
          td[td.length-1] = tempTD;
        }
      }
    }



    //Translucent objects merge sort
    public static void mergeSort(TranslucentData[] arr, int l, int r){
      if(l < r){
        int mid = l+((r-l) >>> 1);
        mergeSort(arr, l, mid);
        mergeSort(arr, mid+1, r);
        merge(arr, l, r, mid);
      }
    }
        
    //Takes in an array of translucent object data, splits them each into two pieces, and them merges 
    //each of them back together with the elements in ascending order
    private static void merge(TranslucentData[] arr, int l, int r, int mid){
      //Finding the sizes of the left and right halfs and filling out new lists
      int leftSize = mid-l+1;
      int rightSize = r-mid;
      TranslucentData[] leftSide = new TranslucentData[leftSize];
      TranslucentData[] rightSide = new TranslucentData[rightSize];
      for(int i = 0; i < leftSize; i++)
        leftSide[i] = arr[i+l];
      for(int i = 0; i < rightSize; i++)
        rightSide[i] = arr[i+mid+1];
         
      //Merging the two arrays together
      int i = 0;//leftSide index
      int j = 0;//rightSide index
      int k = l;//arr index
      //Filling out the list whilst there are still unconsidered elements in both leftSide and rightSide
      while(i < leftSize && j < rightSize){
        if(leftSide[i].returnZ() <= rightSide[j].returnZ()){
          arr[k] = leftSide[i];
          i++;
        }
        else{
          arr[k] = rightSide[j];
          j++;
        }
        k++;
      }
      //Filling out the list with left over elements from leftSide
      while(i < leftSize){
        arr[k] = leftSide[i];
        i++;
        k++;
      }
      //Filling out the list with left over elements from rightSide
      while(j < rightSize){
        arr[k] = rightSide[j];
        j++;
        k++;
      }
    }
        
    
    //Calculates the direction of an edge
    public static float calcEdgeDir(float x1, float y1, float x2, float y2){
      return (x2-x1)*(y1+y2);
    }
    //Checks if a model is in the frustum
    private static boolean isInClipSpace(Matrix mvp, float[][] boundingBox){
      if(boundingBox[0].length < 3 || mvp.returnWidth() != 4 || mvp.returnHeight() != 4)
        return false;
      for(byte i = 0; i < boundingBox.length; i++){
        Matrix usedCorner = MatrixOperations.matrixMultiply(mvp, from3DVecTo4DVec(boundingBox[i]));
        float x = usedCorner.returnData(0, 0);
        float y = usedCorner.returnData(1, 0);
        float z = usedCorner.returnData(2, 0);
        float w = usedCorner.returnData(3, 0);
        if(x >= -w && x <= w && y >= -w && y <= w && z >= -w && z <= w)
          return true;
        }
        return false;
    }

    private static boolean isInClipSpace(Matrix vp, float[] point){
      if(point.length < 3 || vp.returnWidth() != 4 || vp.returnHeight() != 4)
        return false;
      Matrix transformedPoint = MatrixOperations.matrixMultiply(vp, from3DVecTo4DVec(point));
      float[] newPoint = {transformedPoint.returnData(0, 0), transformedPoint.returnData(1, 0),
                          transformedPoint.returnData(2, 0), transformedPoint.returnData(3, 0)};
      if(newPoint[0] >= -newPoint[3] && newPoint[0] <= newPoint[3] && newPoint[1] >= -newPoint[3] && newPoint[1] <= newPoint[3] && newPoint[2] >= -newPoint[3] && newPoint[2] <= newPoint[3])
        return true;
      else
        return false;
    }
    
    //Determines the direction of the triangle for backface culling or if the triangle is exempt from backface culling
    public static float returnEdgeDir(Model model, float[][] points, int[] colour, short[] tint, int backIndex, int faceDirection, boolean universalFill, boolean universalStroke){
      float edgeDir = 0;
      if((backIndex <= -1 || model.returnBackColourCount() > 0) && (universalFill || !universalStroke)){
        //Taking the  vectors (points[(i+1)%3][0], points[(i+1)%3][1]) and (-points[i][0], points[i][1]) to compute the direction of that particular edge 
        //and add the result to the overall direction of the whole triangle
        for(byte i = 0; i < 3; i++)
          edgeDir+=(points[(i+1)%3][0] - points[i][0])*(points[(i+1)%3][1] + points[i][1]);

        edgeDir*=faceDirection; //Hopefully will correct for when there is an odd number of negative scales
        //Determines if triangle is exempt from backface culling and if its back face should be set to a different colour than its front face
        if(backIndex >= 0 && edgeDir < 0){
          if(backIndex < model.returnBackColourCount()){
            if(model.returnBackColour(backIndex, (byte)0) != -1){
              colour[0] = model.returnBackColour(backIndex, (byte)0);
              tint[0] = (short)((colour[0] >>> 24)*tempModel.returnModelTint()); 
            }
            if(model.returnBackColour(backIndex, (byte)1) != -1){
              colour[1] = model.returnBackColour(backIndex, (byte)1);
              tint[1] = (short)((colour[1] >>> 24)*tempModel.returnModelTint());
            }
           }
           edgeDir = 1;
         }
         else if(model.returnIsInverted())
          edgeDir*=-1;
      }
      //If the triangle is exempt from backface culling and does not have a list of back face colours
      else
       edgeDir = 1;
      return edgeDir;
    }

  //Casts an array of length 3 to an array of length 4, with a 1 added to the end
  public static float[] from3DVecTo4DVec(float[] vector){
    if(vector.length != 3){
      System.out.println("ERROR: NOT A 3D VECTOR");
      System.exit(1);
      return vector;
    }
    float[] output = {vector[0], vector[1], vector[2], 1};
    return output;
    
  }
  
  //Casts an array of length 3 to an array of length 4 with a value of the programmer's choice added to the end
  public static float[] from3DVecTo4DVec(float[] vector, float w){
    if(vector.length != 3){
      System.out.println("ERROR: NOT A 3D VECTOR");
      System.exit(1);
      return vector;
    }
    float[] output = {vector[0], vector[1], vector[2], w};
    return output;
  }
  
  //Takes in a vector and converts it into a matrix
  public static float[][] fromVecToHomogeneousMatrix(float[] vector){
    float[][] newMatrix = new float[vector.length+1][1];
    for(int i = 0; i < vector.length; i++)
      newMatrix[i][0] = vector[i];
    newMatrix[vector.length][0] = 1;
    return newMatrix;
  }

  //Takes in a vector and converts it into a matrix
  public static float[][] fromVecToHomogeneousMatrix(float[] vector, float w){
    float[][] newMatrix = new float[vector.length+1][1];
    for(int i = 0; i < vector.length; i++)
      newMatrix[i][0] = vector[i];
    newMatrix[vector.length][0] = w;
    return newMatrix;
  }

  private static void setLightColour(int[] screen, int lightColour, float lightIntensity, int totalPixelCount){
    lightIntensity = Math.max(0, Math.min(lightIntensity, 1));
    lightColour&=0xFFFFFF;
    if(lightColour <= 0xFF)
      lightColour = (lightColour << 16)|(lightColour << 8)|lightColour;
    lightColour|=0xFF000000;
    float lightRed = ((lightColour >>> 16) & 0xFF)*lightIntensity;
    float lightGreen = ((lightColour >>> 8) & 0xFF)*lightIntensity;
    float lightBlue = (lightColour & 0xFF)*lightIntensity;
    for(int i = 0; i < totalPixelCount; i++){
      int[] tempColour = {0xFF000000,
                          (int)(Math.min(255, (((((screen[i] >>> 16) & 0xFF))*lightRed)*0.003921569f))) << 16,
                          (int)(Math.min(255, ((((screen[i] >>> 8) & 0xFF))*lightGreen)*0.003921569f)) << 8,
                          (int)(Math.min(255, (((screen[i] & 0xFF))*lightBlue)*0.003921569f))};
      screen[i] = tempColour[0]|tempColour[1]|tempColour[2]|tempColour[3];
    }
  }


  private static void attachObjectToCamera(float[] modelPos, Camera eye){
    float[] tempPos = from3DVecTo4DVec(modelPos);
    Matrix rotation = MatrixOperations.matrixMultiply(MatrixOperations.matrixMultiply(MVP.returnRotation(0,0, eye.returnRotation()[2]), MVP.returnRotation(0,eye.returnRotation()[1],0)), MVP.returnRotation(eye.returnRotation()[0],0, 0));
    Matrix offsetVals = MatrixOperations.matrixMultiply(MatrixOperations.matrixMultiply(MVP.returnTranslation(eye.returnPosition()), rotation), tempPos);
    float[] offset = {offsetVals.returnData(0, 0), offsetVals.returnData(1, 0), offsetVals.returnData(2, 0)};
    modelPos[0] = offset[0];
    modelPos[1] = offset[1];
    modelPos[2] = offset[2];
  }
  //Iterates through the triangles in each model and transforms them into a list of tris that can be drawn to the screen
  //This version does not account for lighting
  private static void setupTrisNoLight(Matrix mvp, Matrix mvpFull, Camera eye, float drawDist){
    triListOpaque.clear();
    triListTranslucent.clear();
    brightnessValues = null;
    translusentCount = 0;
    byte faceDirection = 1;
    boolean isInClipSpace = false; //Checks if a model is in the frustum
    size = modelList.size();
    for(int i = 0; i < size; i++){
      tempModel = modelList.removeFirst();
      modelList.add(tempModel);

      vertices = new float[tempModel.returnVertexNormals().length][];
      frustumFlags = new boolean[vertices.length];
      primativeVertices = new float[vertices.length][4];
      float[] fromCamToModel = {(tempModel.returnModelCentre()[0]+tempModel.returnPosition()[0])-eye.returnPosition()[0],
                                (tempModel.returnModelCentre()[1]+tempModel.returnPosition()[1])-eye.returnPosition()[1],
                                (tempModel.returnModelCentre()[2]+tempModel.returnPosition()[2])-eye.returnPosition()[2]};
      float distCamToModel = (float)Math.sqrt(fromCamToModel[0]*fromCamToModel[0]+fromCamToModel[1]*fromCamToModel[1]+fromCamToModel[2]*fromCamToModel[2]);
      //Sets up the matrices for the current model
      faceDirection = 1;
      float[] position = {tempModel.returnPosition()[0], tempModel.returnPosition()[1], tempModel.returnPosition()[2]};
      if(tempModel.returnAttachedToCamera())
        attachObjectToCamera(tempModel.returnPosition(), eye);
      tempModel.setModelMatrix();
      //For if the model is not a billboard
      if(!tempModel.returnIsBillBoard())
        mvpFull.copy(MatrixOperations.matrixMultiply(mvp, tempModel.returnModelMatrix()));
      //For if the model is a billboard
      else{
        //Constructs the transformation matrix for the point
        mvpFull.copy(MatrixOperations.matrixMultiply(mvp, MVP.returnTranslation(tempModel.returnPosition())));
        mvpFull.copy(MatrixOperations.matrixMultiply(mvpFull, billBoard));
        
        //Enables rotation about the z-axis and scaling along the x and y axis
        Matrix modelMatrix = MatrixOperations.matrixMultiply(MVP.returnRotation(0, 0, tempModel.returnAngle()[2]), MVP.returnScale(tempModel.returnScale()[0], tempModel.returnScale()[1], 1));
        mvpFull.copy(MatrixOperations.matrixMultiply(mvpFull, modelMatrix));
      }

      //Checking if the model is in clipspace and adjusting the face direction to account for negative scales
      isInClipSpace = ((flags & 64) == 64) || (((isInClipSpace(mvp, tempModel.returnPosition()) || isInClipSpace(mvpFull, tempModel.returnBoundingBox())) && distCamToModel <= drawDist));
      tempModel.setPosition(position[0], position[1], position[2]);
      if((flags & -128) == -128 || tempModel.alwaysPerform())
        tempModel.executeActions();
      for(byte j = 0; j < 3; j++){
        if(tempModel.returnScale()[j] < 0)
          faceDirection = (byte)((~faceDirection)+1);
        if(eye.returnScale()[j] < 0)
          faceDirection = (byte)((~faceDirection)+1);
      }
      
      if(isInClipSpace && tempModel.returnModelTint() > 0){
        for(int j = 0; j < tempModel.returnPolygonCount(); j++){
          float edgeDir = 0; //The direction of the triangle
          float[][] points = new float[3][4]; //Triangle vertices
          boolean isInside = ((flags & 64) == 64); //If the triangle is inside the overal scene
          boolean inFrustum = false; //If the triangle's point is in front of the near plane
          byte numOfInside = 0; //Number of points in the near plane
          byte[] insidePoints = {-1, -1, -1}; //Tracking the indices of the points in the near plane
          float[] centre = {0, 0, 0, 0};
          for(byte s = 0; s < 3; s++){
            int vertexIndex = tempModel.returnPolygons()[j][s];
            if(vertices[vertexIndex] == null){
              //Takes the current point and turns it into a homeogenous vector
              float[] homogeneousPoint = from3DVecTo4DVec(tempModel.returnPoints()[vertexIndex]);
              //Projects the point from 3D to 2D
              Matrix projection = MatrixOperations.matrixMultiply(mvpFull, homogeneousPoint);
              
              //Copying the results of the projection to a set of points that are less tedious to work with
              points[s][0] = (projection.returnData(0, 0)-0.0001f); //x
              points[s][1] = (projection.returnData(1, 0)-0.0001f); //y
              points[s][2] = (projection.returnData(2, 0)-0.0001f); //z
              points[s][3] = (projection.returnData(3, 0)-0.0001f); //w
              centre[0]+=points[s][0];
              centre[1]+=points[s][1];
              centre[2]+=points[s][2];
              centre[3]+=points[s][3];
              primativeVertices[vertexIndex][0] = points[s][0];
              primativeVertices[vertexIndex][1] = points[s][1];
              primativeVertices[vertexIndex][2] = points[s][2];
              primativeVertices[vertexIndex][3] = points[s][3];
              inFrustum = (points[s][2] >= -points[s][3] && points[s][2] <= points[s][3]);
              //Homogeneous division
              if(Math.abs(points[s][3]) > 0){
                points[s][0] = (points[s][0]/points[s][3]);
                points[s][1] = (points[s][1]/points[s][3]);
                points[s][2] = (points[s][2]/points[s][3]);
              }
              
              //Adjusts point positions to place the screen origin at the centre of the canvas, with scaling with respect to the screen dimensions
              points[s][0] = (0.5f*Rasterizer.returnWidth()*(points[s][0]+1)-0.0001f);
              points[s][1] = (0.5f*Rasterizer.returnHeight()*(points[s][1]+1)-0.0001f);
              
              //Tracks which points are in front of the near plane or behind the near plane
              if(inFrustum){
                insidePoints[numOfInside] = s;
                numOfInside++;
              }
              else
                insidePoints[2] = s;
              isInside = isInside || ((inFrustum) || ((flags & 64) == 64));
              isInside|=inFrustum;
              vertices[vertexIndex] = new float[4];
              vertices[vertexIndex][0] = points[s][0];
              vertices[vertexIndex][1] = points[s][1];
              vertices[vertexIndex][2] = points[s][2];
              vertices[vertexIndex][3] = points[s][3];
              frustumFlags[vertexIndex] = inFrustum;
            }
            else{
              points[s][0] = vertices[vertexIndex][0];
              points[s][1] = vertices[vertexIndex][1];
              points[s][2] = vertices[vertexIndex][2];
              points[s][3] = vertices[vertexIndex][3];
              centre[0]+=primativeVertices[vertexIndex][0];
              centre[1]+=primativeVertices[vertexIndex][1];
              centre[2]+=primativeVertices[vertexIndex][2];
              centre[3]+=primativeVertices[vertexIndex][3];
              if(frustumFlags[vertexIndex]){
                insidePoints[numOfInside] = s;
                numOfInside++;
              }
              else
                insidePoints[2] = s;
              isInside|=(frustumFlags[vertexIndex] || ((flags & 64) == 64));
            }
          }
          centre[0]*=0.333333333333333f;
          centre[1]*=0.333333333333333f;
          centre[2]*=0.333333333333333f;
          centre[3]*=0.333333333333333f;
          isInside|=(centre[0] >= -centre[3] && centre[0] <= centre[3] && centre[1] >= -centre[3] && centre[1] <= centre[3] && centre[2] >= -centre[3] && centre[2] <= centre[3]);
          //Copies the triangle from the model to the triangle array
          if(isInside){
            //Returns if the current triangle is exempt from backface culling
            int backIndex = tempModel.returnPalletPtr().returnBackTri(j);
            int[] colour = {tempModel.returnColours()[j][0], tempModel.returnColours()[j][1]}; //Holds the colours that get sent to the triangle rasterizer
            short[] alpha = {(short)((colour[0] >>> 24)*tempModel.returnModelTint()), (short)((colour[1] >>> 24)*tempModel.returnModelTint())};
            //Adding triangles to the list with near-plane clipping (WHOOP WHOOP!!!)
            float[] intersect1 = {0, 0, 0, 0}; //Coordinates for the first point of intersection between the triangle and the near plane
            float[] intersect2 = {0, 0, 0, 0}; //Coordinates for the second point of intersection between the triangle and the near plane
            float t1 = 0; //How far up side AB is the intersection
            float t2 = 0; //How far up side AC is the intersection
            //Code for handling triangles which clip the near plane
            switch(numOfInside){
              //When 1 point is inside the frustum
              case 1:
              //Calculating how far up each side the intersection occurs
              byte otherPoint1 = (byte)((insidePoints[0]+1)%3);
              byte otherPoint2 = (byte)((insidePoints[0]+2)%3);
              t1 = (1 - points[insidePoints[0]][2]);
              if(Math.abs(points[otherPoint1][2] - points[insidePoints[0]][2]) > 0) 
                t1/=(points[otherPoint1][2]-points[insidePoints[0]][2]);
              t2 = (1 - points[insidePoints[0]][2]);
              if(Math.abs(points[otherPoint2][2] - points[insidePoints[0]][2]) > 0) 
                t2/=(points[otherPoint2][2]-points[insidePoints[0]][2]);
                  
              //Computing the location of the point of intersection for each intersecting side
              intersect1[0] = points[insidePoints[0]][0] + t1*(points[otherPoint1][0]-points[insidePoints[0]][0]);
              intersect1[1] = points[insidePoints[0]][1] + t1*(points[otherPoint1][1]-points[insidePoints[0]][1]);
              intersect1[2] = points[insidePoints[0]][2] + t1*(points[otherPoint1][2]-points[insidePoints[0]][2]);
              intersect1[3] = points[insidePoints[0]][3] + t1*(points[otherPoint1][3]-points[insidePoints[0]][3]);

              intersect2[0] = points[insidePoints[0]][0] + t2*(points[otherPoint2][0]-points[insidePoints[0]][0]);
              intersect2[1] = points[insidePoints[0]][1] + t2*(points[otherPoint2][1]-points[insidePoints[0]][1]);
              intersect2[2] = points[insidePoints[0]][2] + t2*(points[otherPoint2][2]-points[insidePoints[0]][2]);
              intersect2[3] = points[insidePoints[0]][3] + t2*(points[otherPoint2][3]-points[insidePoints[0]][3]);
              //Moving the points that are behind the near plane to be at the points of intersection
              points[otherPoint1][0] = intersect1[0];
              points[otherPoint1][1] = intersect1[1];
              points[otherPoint1][2] = intersect1[2];
              points[otherPoint1][3] = intersect1[3];
                  
              points[otherPoint2][0] = intersect2[0];
              points[otherPoint2][1] = intersect2[1];
              points[otherPoint2][2] = intersect2[2];
              points[otherPoint2][3] = intersect2[3];
              break;
            //When 2 points are inside the frustum
            case 2:
              //Calculating how far up each side the points of intersection are
              t1 = (1 - points[insidePoints[0]][2]);
              if(Math.abs(points[insidePoints[2]][2] - points[insidePoints[0]][2]) > 0) 
                t1/=(points[insidePoints[2]][2]-points[insidePoints[0]][2]);
              t2 = (1 - points[insidePoints[1]][2]);
              if(Math.abs(points[insidePoints[2]][2] - points[insidePoints[1]][2]) > 0) 
                t2/=(points[insidePoints[2]][2]-points[insidePoints[1]][2]);
              
              //Calculating where each point of intersection is
              intersect1[0] = points[insidePoints[0]][0] + t1*(points[insidePoints[2]][0]-points[insidePoints[0]][0]);
              intersect1[1] = points[insidePoints[0]][1] + t1*(points[insidePoints[2]][1]-points[insidePoints[0]][1]);
              intersect1[2] = points[insidePoints[0]][2] + t1*(points[insidePoints[2]][2]-points[insidePoints[0]][2]);
              intersect1[3] = points[insidePoints[0]][3] + t1*(points[insidePoints[2]][3]-points[insidePoints[0]][3]);

              intersect2[0] = points[insidePoints[1]][0] + t2*(points[insidePoints[2]][0]-points[insidePoints[1]][0]);
              intersect2[1] = points[insidePoints[1]][1] + t2*(points[insidePoints[2]][1]-points[insidePoints[1]][1]);
              intersect2[2] = points[insidePoints[1]][2] + t2*(points[insidePoints[2]][2]-points[insidePoints[1]][2]);
              intersect2[3] = points[insidePoints[1]][3] + t2*(points[insidePoints[2]][3]-points[insidePoints[1]][3]);
              float[][] secondPoints = new float[3][4];
              //Constructing a triangle B'C'C
              //C
              secondPoints[insidePoints[1]][0] = points[insidePoints[1]][0];
              secondPoints[insidePoints[1]][1] = points[insidePoints[1]][1];
              secondPoints[insidePoints[1]][2] = points[insidePoints[1]][2];
              secondPoints[insidePoints[1]][3] = points[insidePoints[1]][3];
              //B'
              secondPoints[insidePoints[0]][0] = intersect1[0];
              secondPoints[insidePoints[0]][1] = intersect1[1];
              secondPoints[insidePoints[0]][2] = intersect1[2];
              secondPoints[insidePoints[0]][3] = intersect1[3];
              //C'
              secondPoints[insidePoints[2]][0] = intersect2[0];
              secondPoints[insidePoints[2]][1] = intersect2[1];
              secondPoints[insidePoints[2]][2] = intersect2[2];
              secondPoints[insidePoints[2]][3] = intersect2[3];
              //Modifiying the original triangle to be BCB'
              points[insidePoints[2]][0] = intersect1[0];
              points[insidePoints[2]][1] = intersect1[1];
              points[insidePoints[2]][2] = intersect1[2];
              points[insidePoints[2]][3] = intersect1[3];
              //Adding the new triangle to the list to account for the clipped triangle being a quad
              edgeDir = returnEdgeDir(tempModel, secondPoints, colour, alpha, backIndex, faceDirection, (flags & 8) == 8, (flags & 4) == 4);
              if(edgeDir > 0){
                if((alpha[1] & 0xFF) < 0xFF){
                  triListTranslucent.add(new Triangle(secondPoints, colour[0], colour[1], tempModel.returnHasStroke() || (flags & 12) != 8, tempModel.returnHasFill() && (flags & 8) == 8));
                  triListTranslucent.getLast().setDepthWrite(!tempModel.returnNoDepth());
                  triListTranslucent.getLast().setFizzel(tempModel.returnMaxFizzel(), tempModel.returnFizzelThreshold());
                  triListTranslucent.getLast().setAlpha(alpha[0], (byte)0);
                  triListTranslucent.getLast().setAlpha(alpha[1], (byte)1);
                  translucentData.add(new TranslucentData((byte)1, triListTranslucent.getLast().getAverageZ(), tempModel.returnNoDepth(), translusentCount));
                  translusentCount++;
                  translucentCounter++;
                }
                else{
                  triListOpaque.add(new Triangle(secondPoints, colour[0], colour[1], tempModel.returnHasStroke() || (flags & 12) != 8, tempModel.returnHasFill() && (flags & 8) == 8));
                  triListOpaque.getLast().setAlpha(alpha[0], (byte)0);
                  triListOpaque.getLast().setAlpha(alpha[1], (byte)1);
                  triListOpaque.getLast().setDepthWrite(!tempModel.returnNoDepth());
                  triListOpaque.getLast().setFizzel(tempModel.returnMaxFizzel(), tempModel.returnFizzelThreshold());
                }
              }
              break;
            }
            
            //Adding the triangle to the list
            edgeDir = returnEdgeDir(tempModel, points, colour, alpha, backIndex, faceDirection, (flags & 8) == 8, (flags & 4) == 4);
            if(edgeDir > 0){
              if((alpha[1] & 0xFF) < 0xFF){
                triListTranslucent.add(new Triangle(points, colour[0], colour[1], tempModel.returnHasStroke() || (flags & 12) != 8, tempModel.returnHasFill() && (flags & 8) == 8));
                triListTranslucent.getLast().setDepthWrite(!tempModel.returnNoDepth());
                triListTranslucent.getLast().setFizzel(tempModel.returnMaxFizzel(), tempModel.returnFizzelThreshold());
                triListTranslucent.getLast().setAlpha(alpha[0], (byte)0);
                triListTranslucent.getLast().setAlpha(alpha[1], (byte)1);
                translucentData.add(new TranslucentData((byte)1, triListTranslucent.peekLast().getAverageZ(), tempModel.returnNoDepth(), translusentCount));
                translusentCount++;
                translucentCounter++;
              }
              else{
                triListOpaque.add(new Triangle(points, colour[0], colour[1], tempModel.returnHasStroke() || (flags & 12) != 8, tempModel.returnHasFill() && (flags & 8) == 8));
                triListOpaque.getLast().setAlpha(alpha[0], (byte)0);
                triListOpaque.getLast().setAlpha(alpha[1], (byte)1);
                triListOpaque.getLast().setDepthWrite(!tempModel.returnNoDepth());
                triListOpaque.getLast().setFizzel(tempModel.returnMaxFizzel(), tempModel.returnFizzelThreshold());
              }
            }
          }
        }
      }
    }
  }
  //Iterates through each billboard and transforms it into 3D space
  private static void setupBillboardsNoLight(Matrix mvp, Matrix mvpFull, Camera eye, float drawDist){
    billboardDisplayOpaque.clear();
    billboardDisplayTranslucent.clear();
    billBoardCountTranslucent = 0;
    size = billboardList.size();
    for(int i = 0; i < size; i++){
      tempBillboard = billboardList.removeFirst();
      billboardList.add(tempBillboard);
      float[] fromCamToBillboard = {tempBillboard.returnPosition()[0]-eye.returnPosition()[0],
                                    tempBillboard.returnPosition()[1]-eye.returnPosition()[1],
                                    tempBillboard.returnPosition()[2]-eye.returnPosition()[2]};
      float distCamToBillboard = (float)Math.sqrt(fromCamToBillboard[0]*fromCamToBillboard[0]+fromCamToBillboard[1]*fromCamToBillboard[1]+fromCamToBillboard[2]*fromCamToBillboard[2]);
      float[] position = {tempBillboard.returnPosition()[0], tempBillboard.returnPosition()[1], tempBillboard.returnPosition()[2]};
      //Sets up the model matrix and the MVP matrices
      if(tempBillboard.isAttachedToCamera())
        attachObjectToCamera(tempBillboard.returnPosition(), eye);
      tempBillboard.setBillBoardModelMatrix();
      if(distCamToBillboard <= drawDist && tempBillboard.returnModelTint() > 0){
        //Constructs the transformation matrix for the point
        mvpFull.copy(MatrixOperations.matrixMultiply(mvp, MVP.returnTranslation(tempBillboard.returnPosition())));
        mvpFull.copy(MatrixOperations.matrixMultiply(mvpFull, billBoard));
        //Multiplying the transformed matrices by the scale of the model
        mvpFull.copy(MatrixOperations.matrixMultiply(mvpFull, MVP.returnScale(tempBillboard.returnScale()[0], tempBillboard.returnScale()[1], 1)));
        tempBillboard.setPosition(position);
        float[][] points = {{-(tempBillboard.returnWidth() >>> 1), -(tempBillboard.returnHeight() >>> 1), 0, 1}, 
                            {(tempBillboard.returnWidth() >>> 1), -(tempBillboard.returnHeight() >>> 1), 0, 1},
                            {(tempBillboard.returnWidth() >>> 1), (tempBillboard.returnHeight() >>> 1), 0, 1}, 
                            {-(tempBillboard.returnWidth() >>> 1), (tempBillboard.returnHeight() >>> 1), 0, 1}};
        boolean isInside = ((flags & 64) == 64);
        for(byte j = 0; j < 4; j++){
          //Projects the point from 3D to 2D
          Matrix projection = MatrixOperations.matrixMultiply(mvpFull, points[j]);
          points[j][0] = projection.returnData(0, 0) - 0.0001f;
          points[j][1] = projection.returnData(1, 0) - 0.0001f;
          points[j][2] = projection.returnData(2, 0) - 0.0001f;
          points[j][3] = projection.returnData(3, 0) - 0.0001f;
          if(points[j][3] > 0){
            points[j][0]/=points[j][3];
            points[j][1]/=points[j][3];
            points[j][2]/=points[j][3];
          }
          points[j][0] = (0.5f*Rasterizer.returnWidth())*(points[j][0]+1)-0.0001f;
          points[j][1] = (0.5f*Rasterizer.returnHeight())*(points[j][1]+1)-0.0001f;
          isInside|=(points[j][3] > 0);
        }
  
        //Calculates the minimum x and y coordinates and the maximum x and y coordinates
        float[] minPoints = {Math.min(points[0][0], Math.min(points[1][0], Math.min(points[2][0], points[3][0]))),
                            Math.min(points[0][1], Math.min(points[1][1], Math.min(points[2][1], points[3][1])))};
        float[] maxPoints = {Math.max(points[0][0], Math.max(points[1][0], Math.max(points[2][0], points[3][0]))),
                            Math.max(points[0][1], Math.max(points[1][1], Math.max(points[2][1], points[3][1])))};
        //Checks if any part of the image is on the screen
        isInside&=((minPoints[0] < Rasterizer.returnWidth() && minPoints[1] < Rasterizer.returnHeight()) && (maxPoints[0] >= 0 && maxPoints[1] >= 0));
        if(isInside){
          int fill = tempBillboard.returnFill();
          short[] alpha = {(short)((tempBillboard.returnStroke() >>> 24)*tempBillboard.returnModelTint()), 
                           (short)((tempBillboard.returnFill() >>> 24)*tempBillboard.returnModelTint())};
          float sizeX = points[2][0]-points[0][0];
          float sizeY = points[2][1]-points[0][1];
          if((alpha[1] & 0xFF) == 255){
            billboardDisplayOpaque.add(new Billboard());
            billboardDisplayOpaque.getLast().copy(tempBillboard);
            billboardDisplayOpaque.getLast().setScale(sizeX, sizeY);
            billboardDisplayOpaque.getLast().stroke(tempBillboard.returnStroke(), alpha[0]);
            billboardDisplayOpaque.getLast().fill(fill, alpha[1]);
            billboardDisplayOpaque.getLast().setPosition(points[0][0], points[0][1], points[0][2]);
            billboardDisplayOpaque.getLast().setOutline(tempBillboard.hasOutline() || (flags & 12) != 8);
            billboardDisplayOpaque.getLast().setInside(tempBillboard.hasImage() && (flags & 8) == 8);
          }
          else{
            billboardDisplayTranslucent.add(new Billboard());
            billboardDisplayTranslucent.getLast().copy(tempBillboard);
            billboardDisplayTranslucent.getLast().setScale(sizeX, sizeY);
            billboardDisplayTranslucent.getLast().stroke(tempBillboard.returnStroke(), alpha[0]);
            billboardDisplayTranslucent.getLast().fill(fill, alpha[1]);
            billboardDisplayTranslucent.getLast().setPosition(points[0][0], points[0][1], points[0][2]);
            billboardDisplayTranslucent.getLast().setOutline(tempBillboard.hasOutline() || (flags & 12) != 8);
            billboardDisplayTranslucent.getLast().setInside(tempBillboard.hasImage() && (flags & 8) == 8);
            translucentData.add(new TranslucentData((byte)2, points[0][2], tempBillboard.noDraw(), billBoardCountTranslucent));
            translucentCounter++;
            billBoardCountTranslucent++;
          }
        }
      }
      if((flags & -128) == -128 || tempBillboard.alwaysPerform())
        tempBillboard.executeActions();
    }
  }
  /*TODO: ADD IN LINE PROCESSING*/
  //Transforms the lines from 3D local space to 2D screen space
  private static void setupLines(Matrix mvp, Matrix mvpFull, Camera eye, float drawDist){
    lineCountTranslucent = 0;
    lineDisplayOpaque.clear();
    lineDisplayTranslucent.clear();
    primativeVertices = null;
    boolean isInClipSpace = false;
    size = lineList.size();
    for(int i = 0; i < size; i++){
      tempLineObj = lineList.removeFirst();
      lineList.add(tempLineObj);
      vertices = new float[tempLineObj.returnLineModelPtr().returnVertices().length][];
      frustumFlags = new boolean[vertices.length];
      float[] fromCamToModel = {(tempLineObj.returnModelCentre()[0]+tempLineObj.returnPosition()[0])-eye.returnPosition()[0],
                                (tempLineObj.returnModelCentre()[1]+tempLineObj.returnPosition()[1])-eye.returnPosition()[1],
                                (tempLineObj.returnModelCentre()[2]+tempLineObj.returnPosition()[2])-eye.returnPosition()[2]};
      float distCamToModel = (float)Math.sqrt(fromCamToModel[0]*fromCamToModel[0]+fromCamToModel[1]*fromCamToModel[1]+fromCamToModel[2]*fromCamToModel[2]);
      float[] position = {tempLineObj.returnPosition()[0], tempLineObj.returnPosition()[1], tempLineObj.returnPosition()[2]};
      if(tempLineObj.returnAttachedToCamera())
        attachObjectToCamera(tempLineObj.returnPosition(), eye);
      tempLineObj.setModelMatrix();
      //Constructs the transformation matrix for the point
      mvpFull.copy(MatrixOperations.matrixMultiply(mvp, tempLineObj.returnModelMatrix()));
      tempLineObj.setPosition(position);
      if((flags & -128) == -128 || tempLineObj.alwaysPerform())
        tempLineObj.executeActions();
      //Checking if the model is in clipspace and adjusting the face direction to account for negative scales
      isInClipSpace = ((flags & 64) == 64) || (((isInClipSpace(mvp, tempLineObj.returnPosition()) || isInClipSpace(mvpFull, tempLineObj.returnBoundingBox())) && distCamToModel <= drawDist));
      float[][] pointPair = tempLineObj.returnLineModelPtr().returnVertices();
      if(isInClipSpace && tempLineObj.returnModelTint() > 0){
        for(int j = 0; j < tempLineObj.returnLineCount(); j++){

          int[] endPoints = {tempLineObj.returnLineModelPtr().returnPoints()[j][0], 
                            tempLineObj.returnLineModelPtr().returnPoints()[j][1]};
          //Copying the endpoints from the original line object to a list to work on
          float[][] points = {{pointPair[endPoints[0]][0], pointPair[endPoints[0]][1], pointPair[endPoints[0]][2], 1},
                              {pointPair[endPoints[1]][0], pointPair[endPoints[1]][1], pointPair[endPoints[1]][2], 1}};
          byte insideCount = 0; //Will be used to track how many points are in the frustum
          byte insidePoint = -1; //Will be used to track which point (or points) is in the frustum
          boolean isInside = ((flags & 64) == 64);
          for(byte s = 0; s < 2; s++){
            if(vertices[endPoints[s]] == null){
              Matrix projection = MatrixOperations.matrixMultiply(mvpFull, points[s]);
              points[s][0] = projection.returnData(0, 0)-0.0001f;
              points[s][1] = projection.returnData(1, 0)-0.0001f;
              points[s][2] = projection.returnData(2, 0)-0.0001f;
              points[s][3] = projection.returnData(3, 0)-0.0001f;
              if((points[s][2] >= -points[s][3] && points[s][2] <= points[s][3]) || ((flags & 64) == 64)){
                insideCount++;
                insidePoint = s;
              }
              frustumFlags[endPoints[s]] = (points[s][2] >= -points[s][3] && points[s][2] <= points[s][3]);
              isInside|=frustumFlags[endPoints[s]];
              if(Math.abs(points[s][3]) > 0.0001f){
                points[s][0]/=points[s][3];
                points[s][1]/=points[s][3];
                points[s][2]/=points[s][3];
              }
              points[s][0] = 0.5f*Rasterizer.returnWidth()*(points[s][0]+1)-0.0001f;
              points[s][1] = 0.5f*Rasterizer.returnHeight()*(points[s][1]+1)-0.0001f;
              vertices[endPoints[s]] = new float[4];
              vertices[endPoints[s]][0] = points[s][0];
              vertices[endPoints[s]][1] = points[s][1];
              vertices[endPoints[s]][2] = points[s][2];
              vertices[endPoints[s]][3] = points[s][3];
            }
            else{
              if(frustumFlags[endPoints[s]] || ((flags & 64) == 64)){
                insideCount++;
                insidePoint = s;
              }
              isInside|=frustumFlags[endPoints[s]];
              points[s][0] = vertices[endPoints[s]][0];
              points[s][1] = vertices[endPoints[s]][1];
              points[s][2] = vertices[endPoints[s]][2];
              points[s][3] = vertices[endPoints[s]][3];
            }
          }            
          //Calculates the minimum x and y coordinates and the maximum x and y coordinates
          float[] minPoints = {Math.min(points[0][0], points[1][0]),
                              Math.min(points[0][1], points[1][1])};
          float[] maxPoints = {Math.max(points[0][0], points[1][0]),
                               Math.max(points[0][1], points[1][1])};
          //Checks if any part of the image is on the screen
          isInside&=((minPoints[0] < Rasterizer.returnWidth() && minPoints[1] < Rasterizer.returnHeight()) && (maxPoints[0] >= 0 && maxPoints[1] >= 0));
          if(isInside){
            int colour = tempLineObj.returnStroke(j); 
            short alpha = (short)((tempLineObj.returnStroke(j) >>> 24)*tempLineObj.returnModelTint());
            if(insideCount == 1){
              float denominator = (points[insidePoint][2] - points[insidePoint^1][2]);
              float t = 0;
              if(Math.abs(denominator) > 0.0001f)
                t = (1f - points[insidePoint][2])/denominator;
              points[insidePoint^1][0] = points[insidePoint][0] + t*(points[insidePoint][0]-points[insidePoint^1][0]);
              points[insidePoint^1][1] = points[insidePoint][1] + t*(points[insidePoint][1]-points[insidePoint^1][1]);
              points[insidePoint^1][2] = points[insidePoint][2] + t*(points[insidePoint][2]-points[insidePoint^1][2]);
            }
            if((alpha & 0xFF) == 255){
              lineDisplayOpaque.add(new LineDisp(points, colour));
              lineDisplayOpaque.getLast().setAlpha(alpha);
              lineDisplayOpaque.getLast().setDepthWrite(tempLineObj.returnDepthDisable());
            }
            else{
              lineDisplayTranslucent.add(new LineDisp(points, colour));
              lineDisplayTranslucent.getLast().setAlpha(alpha);
              lineDisplayTranslucent.getLast().setDepthWrite(tempLineObj.returnDepthDisable());
              translucentData.add(new TranslucentData((byte)3, (points[1][2]+points[0][2])*0.5f, tempLineObj.returnDepthDisable(), lineCountTranslucent));
              translucentCounter++;
              lineCountTranslucent++;
            }
          }
        }
      }
    }
  }
  //A function for transforming single-pixel dots to screenspace
  private static void setupDots(Matrix mvp, Matrix mvpFull, Camera eye, float drawDist){
    dotDisplayOpaque.clear();
    dotDisplayTranslucent.clear();
    dotCountTranslucent = 0;
    size = dotList.size();
    for(int i = 0; i < size; i++){
      tempDot = dotList.removeFirst();
      dotList.add(tempDot);
      tempDot.setModelMatrix();
      if((flags & -128) == -128 || tempDot.alwaysPerform())
        tempDot.executeActions();
      float[] position = {tempDot.returnPosition()[0], tempDot.returnPosition()[1], tempDot.returnPosition()[2]};
      if(tempDot.returnAttachedToCamera())
        attachObjectToCamera(tempDot.returnPosition(), eye);
      tempDot.setPosition(position);
      float[] point = from3DVecTo4DVec(tempDot.returnPosition());
      boolean isInside = ((flags & 64) == 64);
      //Projects the point from 3D to 2D
      Matrix clipCheck = MatrixOperations.matrixMultiply(view, point); 
      Matrix projection = MatrixOperations.matrixMultiply(mvp, point);
      point[0] = projection.returnData(0, 0) - 0.0001f;
      point[1] = projection.returnData(1, 0) - 0.0001f;
      point[2] = projection.returnData(2, 0) - 0.0001f;
      point[3] = projection.returnData(3, 0) - 0.0001f;
      if(point[3] > 0){
        point[0]/=point[3];
        point[1]/=point[3];
        point[2]/=point[3];
      }
      point[0] = (0.5f*Rasterizer.returnWidth())*(point[0]+1)-0.0001f;
      point[1] = (0.5f*Rasterizer.returnHeight())*(point[1]+1)-0.0001f;
      isInside = (isInside || (point[3] > 0 && clipCheck.returnData(2, 0) >= -1 && clipCheck.returnData(2,0) <= drawDist)) && (point[0] >= 0 && point[0] <= Rasterizer.returnWidth() && point[1] >= 0 && point[1] <= Rasterizer.returnHeight());
      if(isInside && tempDot.returnModelTint() > 0){
        int colour = tempDot.returnStroke(); 
        short alpha = (short)((tempDot.returnStroke() >>> 24)*tempDot.returnModelTint());
        if((alpha & 0xFF) == 255){
          dotDisplayOpaque.add(new Dot(point, colour));
          dotDisplayOpaque.getLast().setAlpha(alpha);
          dotDisplayOpaque.getLast().setDepthWrite(tempDot.returnDepthDisable());
        }
        else{
          dotDisplayTranslucent.add(new Dot(point, colour));
          dotDisplayTranslucent.getLast().setDepthWrite(tempDot.returnDepthDisable());
          dotDisplayTranslucent.getLast().setAlpha(alpha);
          translucentData.add(new TranslucentData((byte)4, point[2], tempDot.returnDepthDisable(), dotCountTranslucent));
          translucentCounter++;
          dotCountTranslucent++;
        }
      }
    }
  }

  private static void sortAndDrawTranslucent(int[] screen){
    //Copying the items in the translucent linked lists into temporary arrays
    TranslucentData[] tempData = new TranslucentData[0];
    Triangle[] tempTris = new Triangle[0];
    Billboard[] tempBillboards = new Billboard[0];
    LineDisp[] tempLines = new LineDisp[0];
    Dot[] tempDots = new Dot[0];

    tempData = translucentData.toArray(tempData);
    tempTris = triListTranslucent.toArray(tempTris);
    tempBillboards = billboardDisplayTranslucent.toArray(tempBillboards);
    tempLines = lineDisplayTranslucent.toArray(tempLines);
    tempDots = dotDisplayTranslucent.toArray(tempDots);

    //Performing a merge sort and pushing any items that have noDraw enabled to the rear of the list
    mergeSort(tempData, 0, translucentCounter-1);
    pushToRear(tempData);
    
    //Iterating over translucent objects
    for(int i = 0; i < tempData.length && tempData[i].returnID() != 0; i++){
      int j = tempData[i].returnOriginalIndex();
      switch(tempData[i].returnID()){
        case 1:
          Rasterizer.setDepthWrite(tempTris[j].getHasDepthWrite());
          Rasterizer.setVertexBrightness(tempTris[j].returnVertexBrightness());
          Rasterizer.setProbabilities(tempTris[j].returnMaxFizzel(), tempTris[j].returnFizzelThreshold());
          Rasterizer.draw(tempTris[j], stencilComp, testType);
          break;
        case 2:
          Rasterizer.setProbabilities(tempBillboards[j].returnMaxFizzel(), tempBillboards[j].returnFizzelThreshold());
          Rasterizer.billBoardDraw(tempBillboards[j], tempBillboards[j].returnPosition()[0], tempBillboards[j].returnPosition()[1], tempBillboards[j].returnPosition()[2], tempBillboards[j].returnScale()[0], tempBillboards[j].returnScale()[1], stencilComp, testType);
          break;
        case 3:
          float[][] endPoints = tempLines[j].returnEndPoints();
          Rasterizer.drawLine(new IntWrapper(Math.round(endPoints[0][0])), new IntWrapper(Math.round(endPoints[0][1])), endPoints[0][2], new IntWrapper(Math.round(endPoints[1][0])), new IntWrapper(Math.round(endPoints[1][1])), endPoints[1][2], tempLines[j].returnStroke(), tempLines[j].returnDepthDisable());
          break;
        case 4:
          Rasterizer.setPixel(tempDots[j].returnStroke(), tempDots[j].returnPosition()[0], tempDots[j].returnPosition()[1], tempDots[j].returnPosition()[2], tempDots[j].returnDepthDisable());
          break;
      }
    }
  }
  private static float[] computeLighting(LinkedList<Light> lights, float[][] lightPos, float[][] lightAngle, float[][][] lightColour, float[] homogeneousPoint, float[] normal, float luster, float overallBrightness, Matrix model, Model tempModel, int polygonIndex, boolean alreadyComputed){
    float[] points = new float[3];
    float[] brightness = {0, 0, 0};
    if(!alreadyComputed){
      Matrix projection = MatrixOperations.matrixMultiply(model, homogeneousPoint);
      points[0] = projection.returnData(0, 0); 
      points[1] = projection.returnData(1, 0);
      points[2] = projection.returnData(2, 0);
    }
    else{
      points[0] = homogeneousPoint[0];
      points[1] = homogeneousPoint[1];
      points[2] = homogeneousPoint[2];
    }
    float[] camToTriVec = {points[0], points[1], points[2]};
    camToTriVec = VectorOperations.vectorNormalization3D(camToTriVec);
    
    float[] normalizedVec = {0, 0, -1};
    if(tempModel != null){
      //Transforms the homogeneous vector
      float[] homogeneousNormal = from3DVecTo4DVec(VectorOperations.vectorNormalization3D(normal), 0);
      Matrix transformedVector = MatrixOperations.matrixMultiply(model, homogeneousNormal);
      //Returns the 4D vector to a 3D vector and normalizes
      normalizedVec[0] = transformedVector.returnData(0, 0);
      normalizedVec[1] = transformedVector.returnData(1, 0);
      normalizedVec[2] = transformedVector.returnData(2, 0);
            
      
      normalizedVec = VectorOperations.vectorNormalization3D(normalizedVec);
      if(tempModel.returnIsInverted()){
        normalizedVec[0]*=-1;
        normalizedVec[1]*=-1;
        normalizedVec[2]*=-1;
      }
    }

    for(int i = 0; i < lights.size(); i++){
      Light light = lights.removeFirst();
      lights.add(light);
      float[] lightToTriVec = new float[3];
      float r2 = 1;

      if(light.returnType() != 'd'){
        lightToTriVec[0] = lightPos[i][0]-points[0];
        lightToTriVec[1] = lightPos[i][1]-points[1];
        lightToTriVec[2] = lightPos[i][2]-points[2];

        r2 = vectorDotProduct(lightToTriVec, lightToTriVec);
      }
      if(Math.abs(r2) > 0.00000001)
        r2 = 1/r2;
      else
        r2 = 0;
      lightToTriVec = VectorOperations.vectorNormalization3D(lightToTriVec);

      float[] halfVec = VectorOperations.vectorNormalization3D(VectorOperations.vectorAddition(camToTriVec, lightToTriVec));
      if(tempModel != null && vectorDotProduct(halfVec, normalizedVec) > 0 && tempModel.returnPalletPtr().returnBackVisible(polygonIndex)){
        normalizedVec[0]*=-1;
        normalizedVec[1]*=-1;
        normalizedVec[2]*=-1;
      }
      float diffuse = 1;
      float specular = Math.max(0, vectorDotProduct(normalizedVec, halfVec));
      if(light.returnType() == 'p')
        diffuse = Math.max(0, vectorDotProduct(normalizedVec, lightToTriVec));
      else if(light.returnType() == 'd')
        diffuse = Math.max(0, vectorDotProduct(normalizedVec, lightAngle[i]));
      else{
        float bright = vectorDotProduct(lightToTriVec, lightAngle[i]);
        diffuse = light.spreadBrightness(bright)*Math.max(0, vectorDotProduct(normalizedVec, lightToTriVec));
        specular*=light.spreadBrightness(bright);
      }
      diffuse*=(light.returnDiffuseIntensity()*r2);
      specular = ((float)Math.pow(specular, luster)*light.returnSpecularIntensity()*r2);
      brightness[0]+=((lightColour[i][0][0]*light.returnAmbientIntensity()) + ((lightColour[i][1][0]*diffuse) + (lightColour[i][2][0]*specular)))*overallBrightness;
      brightness[1]+=((lightColour[i][0][1]*light.returnAmbientIntensity()) + ((lightColour[i][1][1]*diffuse) + (lightColour[i][2][1]*specular)))*overallBrightness;
      brightness[2]+=((lightColour[i][0][2]*light.returnAmbientIntensity()) + ((lightColour[i][1][2]*diffuse) + (lightColour[i][2][2]*specular)))*overallBrightness;
    }
    return brightness;
  
  }
  private static float vectorDotProduct(float[] vect1, float[] vect2){
    if(vect1.length != 3 || vect2.length != 3){
      System.out.println("ERROR: WRONG NUMBER OF DIMESIONS (MUST BE 3)");
      System.exit(1);
    }
    return vect1[0]*vect2[0]+vect1[1]*vect2[1]+vect1[2]*vect2[2];
  }
}