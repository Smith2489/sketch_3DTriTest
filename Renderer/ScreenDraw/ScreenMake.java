package Renderer.ScreenDraw;
import Wrapper.*;
import java.util.*;
import Maths.LinearAlgebra.*;
import Renderer.Objects.SceneEntities.*;
import Renderer.Objects.Parents.SceneEntity;
public class ScreenMake{

  private static float[] ditherMatrix = {-0.007936508f, 0.4920635f, 0.11706349f, 0.61706346f, 0.023313493f, 0.52331346f, 0.14831349f, 0.64831346f,
                                          0.74206346f, 0.24206349f, 0.86706346f, 0.3670635f, 0.77331346f, 0.2733135f, 0.89831346f, 0.3983135f,
                                          0.17956349f, 0.67956346f, 0.054563493f, 0.55456346f, 0.21081349f, 0.71081346f, 0.08581349f, 0.58581346f,
                                          0.92956346f, 0.4295635f, 0.80456346f, 0.3045635f, 0.96081346f, 0.4608135f, 0.83581346f, 0.3358135f,
                                          0.038938493f, 0.53893846f, 0.16393849f, 0.66393846f, 0.0076884916f, 0.50768846f, 0.13268849f, 0.63268846f,
                                          0.78893846f, 0.2889385f, 0.91393846f, 0.4139385f, 0.75768846f, 0.2576885f, 0.88268846f, 0.3826885f,
                                          0.22643849f, 0.72643846f, 0.10143849f, 0.60143846f, 0.19518849f, 0.69518846f, 0.07018849f, 0.57018846f,
                                          0.97643846f, 0.4764385f, 0.85143846f, 0.3514385f, 0.94518846f, 0.4451885f, 0.82018846f, 0.3201885f};
    //Set up for the stencil test
    private static byte stencilComp = 0;
    private static char testType = 'e';
    private static Matrix4x4 proj = MVP.perspMatrix(1, 90, -1, 1); //The projection matrix
    /*Flags
    Bit 0: dither all
    Bit 1: Solid background enable (low enable)
    Bit 2: Forced stroke enable (high enable)
    Bit 3: Forced fill enable (high enable)
    Bit 4: A billboard exists in model list (high enable)
    Bit 5: The billboarded sprite list is not empty (high enable)
    Bit 6: Projection is orthographic
    Bit 7: is interactive (actions will be executed)
    */
    private static byte flags = -119;
    /*
    Bit 0 Dither matrix size has already been modified 
    */
    private static byte flags2 = 0;
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
    private static LinkedList<SceneEntity> noDraw = new LinkedList<SceneEntity>();
    private static LinkedList<Model> modelList = new LinkedList<Model>(); //List of models
    private static LinkedList<Billboard> billboardList = new LinkedList<Billboard>(); //List of billboards
    private static LinkedList<LineObj> lineList = new LinkedList<LineObj>();
    private static LinkedList<Dot> dotList = new LinkedList<Dot>();
    private static LinkedList<Model> refListM = modelList;
    private static LinkedList<Billboard> refListB = billboardList;
    private static LinkedList<LineObj> refListL = lineList;
    private static LinkedList<Dot> refListD = dotList;
    private static LinkedList<SceneEntity> refListNoDraw = noDraw;
    private static Matrix4x4 billBoard;
    private static Matrix4x4 view;
    private static Model tempModel;
    private static LineObj tempLineObj;
    private static Triangle tempTri = new Triangle();
    private static Billboard tempBillboard = new Billboard();
    private static LineDisp tempLine = new LineDisp();
    private static Dot tempDot = new Dot();
    private static SceneEntity tempInvis = new SceneEntity();

    private static float ditherIntensity = 0;
    private static float ditherRange = 0;
    private static float ditherThreshold = 0.5f;
    private static int ditherMatrixSize = 8;
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

    private static int size = 0;

    //Solution for finding the nearest neighbouring 15-bit colour found in Acerola's video Color Quantization and Dithering
    //https://www.youtube.com/watch?v=8wOUe32Pt-E

    public static void setDitherIntensity(float newIntensity){
      ditherIntensity = Math.max(0, Math.min(newIntensity, 1));
    }

    public static void setDitherRange(float newRange){
      ditherRange = Math.max(0, Math.min(newRange*0.5f, 1));
    }

    public static void ditherOnlyObjects(){
      flags&=-2;
    }
    public static void ditherAll(){
      flags|=1;
    }

    public static void setDitherMatrixSize(int squareSize){
      if((flags2 & 1) == 0){
        ditherMatrixSize = 1 << squareSize;
        if(ditherMatrixSize*ditherMatrixSize < 4){
          ditherMatrixSize = 2;
          squareSize = 1;
        }
        ditherMatrix = new float[ditherMatrixSize*ditherMatrixSize];
        float invSqrSize = 1f/(ditherMatrix.length);
        float maxValue = 1f/(ditherMatrix.length-1);
        long doubleSquare = squareSize << 1;
        for(int i = 0; i < ditherMatrixSize; i++){
          for(int j = 0; j < ditherMatrixSize; j++){
            //Solution based on https://bisqwit.iki.fi/story/howto/dither/jy/
            //Wikipedia's mention of this method for generating dither matrices does not do a good job
            int y = i^j;
            int mask = squareSize-1;
            long output = 0;
            for(long s = 0; s < doubleSquare; mask--){
              output|=(((i >>> mask) & 1) << s) | (y >>> mask & 1) << (s+1);
              s+=2; 
            }
            ditherMatrix[j+ditherMatrixSize*i] = output*invSqrSize - 0.5f*maxValue;
          }
        }
        flags2|=1;
      }
      else
        System.out.println("CANNOT SET THE DITHER MATRIX SIZE ONCE IT'S BEEN SET");
    }

    public static void setDitherThreshold(float threshold){
      ditherThreshold = Math.max(0, threshold);
    }

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
      if(billboardList.size() <= 0)
        flags&=-17;
    }

    public static void setInvisibleObjectsList(LinkedList<SceneEntity> newList){
      noDraw = newList;
    }
    public static void disableInvisibleObjects(){
      noDraw = refListNoDraw;
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




    //Takes in a frame buffer, near-z, far-z, and camera and draws a 3D scene using that data plus the model and billboard lists
    public static void drawScene(int[] screen, Camera eye){
      MVP.setEyeAngles(eye.returnRotation());
      MVP.setEyePos(eye.returnPosition());
      MVP.setEyeScale(eye.returnScale());
      MVP.setEyeShear(eye.returnShear());
      view = MatrixOperations.matrixMultiply(eye.transform(), MVP.viewMatrix());
      eye.setModelMatrix();

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
      Matrix4x4 mvp = MatrixOperations.matrixMultiply(proj, view);
      Matrix4x4 mvpFull = new Matrix4x4();
      //Matrix mv = new Matrix(); //For checking if a model is within zNear and zFar (using the frustum does not work for some reason)
      setupTrisNoLight(mvp, mvpFull, eye, eye.getDrawDistance());
      setupBillboardsNoLight(mvp, mvpFull, eye, eye.getDrawDistance());

      /*TODO: ADD IN LINE PROCESSING*/
      setupLines(mvp, mvpFull, eye, eye.getDrawDistance());
      setupDots(mvp, mvpFull, eye, eye.getDrawDistance());
      drawObjects(screen);
      if((flags & -128) == -128 || eye.alwaysPerform())
        eye.executeActions();
      for(int i = 0; i < Rasterizer.returnWidth(); i++){
        for(int j = 0; j < Rasterizer.returnHeight(); j++){
          int pixelPos = i+Rasterizer.returnWidth()*j;
          float[] tempColour = {0xFF000000,
                                ((screen[pixelPos] >>> 16) & 0xFF)*Colour.INV_255,
                                ((screen[pixelPos] >>> 8) & 0xFF)*Colour.INV_255,
                                (screen[pixelPos] & 0xFF)*Colour.INV_255};
          if(ditherIntensity >= 0.00001 && (((flags & 1) == 0 && !Float.isNaN(Rasterizer.returnDepthBuffer()[pixelPos])) || (flags & 1) == 1)){
            float dither = ditherIntensity*ditherMatrix[(i%ditherMatrixSize)*ditherMatrixSize+(j%ditherMatrixSize)];
            if(ditherRange > 0.00001)
              dither+=(float)(Math.random()*(ditherRange*2)-ditherRange);
            tempColour[1] = (int)(Math.min(1, Math.max(0, ((int)((tempColour[1]+dither)*31+ditherThreshold)*0.032258064516129)))*255)>>>3<<3;
            tempColour[2] = (int)(Math.min(1, Math.max(0, ((int)((tempColour[2]+dither)*31+ditherThreshold)*0.032258064516129)))*255)>>>3<<3;
            tempColour[3] = (int)(Math.min(1, Math.max(0, ((int)((tempColour[3]+dither)*31+ditherThreshold)*0.032258064516129)))*255)>>>3<<3;
          }
          tempColour[1] = tempColour[1]*255;
          tempColour[2] = tempColour[2]*255;
          tempColour[3] = tempColour[3]*255;
          screen[pixelPos] = (int)tempColour[0]|((int)tempColour[1] << 16)|((int)tempColour[2] << 8)|(int)tempColour[3];
          
        }
      }
    }

    public static void drawScene(int[] screen, Camera eye, int lightColour, float screenBrightness){
      MVP.setEyeAngles(eye.returnRotation());
      MVP.setEyePos(eye.returnPosition());
      MVP.setEyeScale(eye.returnScale());
      MVP.setEyeShear(eye.returnShear());
      view = MatrixOperations.matrixMultiply(eye.transform(), MVP.viewMatrix());
      eye.setModelMatrix();

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
      Matrix4x4 mvp = MatrixOperations.matrixMultiply(proj, view);
      Matrix4x4 mvpFull = new Matrix4x4();
      //Matrix mv = new Matrix(); //For checking if a model is within zNear and zFar (using the frustum does not work for some reason)

      setupTrisNoLight(mvp, mvpFull, eye, eye.getDrawDistance());
      setupBillboardsNoLight(mvp, mvpFull, eye, eye.getDrawDistance());


      /*TODO: ADD IN LINE HANDLING */
      setupLines(mvp, mvpFull, eye, eye.getDrawDistance());
      setupDots(mvp, mvpFull, eye, eye.getDrawDistance());
      drawObjects(screen);

      if((flags & -128) == -128 || eye.alwaysPerform())
        eye.executeActions();
      setLightColour(screen, lightColour, screenBrightness, Rasterizer.returnWidth(), Rasterizer.returnHeight());
    }
    //The version for Blinn-Phong reflection and flat shading (1 normal per polygon)
    public static void drawScene(int[] screen, Camera eye, LinkedList<Light> lights, float generalObjectBrightness){
      MVP.setEyeAngles(eye.returnRotation());
      MVP.setEyePos(eye.returnPosition());
      MVP.setEyeScale(eye.returnScale());
      MVP.setEyeShear(eye.returnShear());
      view = MatrixOperations.matrixMultiply(eye.transform(), MVP.viewMatrix());
      eye.setModelMatrix();

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
      Matrix4x4 mvp = MatrixOperations.matrixMultiply(proj, view);
      Matrix4x4 mvpFull = new Matrix4x4();
      //Matrix mv = new Matrix(); //For checking if a model is within zNear and zFar (using the frustum does not work for some reason)
      boolean isInClipSpace = false; //Checks if a model is in the frustum
      for(int i = 0; i < lights.size(); i++){
        Light light = lights.removeFirst();
        lights.add(light);
        //Calcluating where the light and the camera should be relative to everything else
        light.setModelMatrix();
        Matrix4x4 uniTransform = light.transform();
        if(light.returnType() == 'p'){
          lightPos[i] = dropW(MatrixOperations.matrixMultiply(view, MatrixOperations.matrixMultiply(uniTransform, from3DVecTo4DVec(light.returnPosition()))));
        }
        else if(light.returnType() == 'd')
          lightAngle[i] = dropW(MatrixOperations.matrixMultiply(view, MatrixOperations.matrixMultiply(uniTransform, from3DVecTo4DVec(light.returnLightDirection(), 0))));
        else{
          lightPos[i] = dropW(MatrixOperations.matrixMultiply(view, MatrixOperations.matrixMultiply(uniTransform, from3DVecTo4DVec(light.returnPosition()))));
          lightAngle[i] = dropW(MatrixOperations.matrixMultiply(view, MatrixOperations.matrixMultiply(uniTransform, from3DVecTo4DVec(light.returnLightDirection(), 0))));
        }
        lightColour[i] = light.returnLightColour();
        for(int j = 0; j < 3; j++){
          lightColour[i][j][0]*=invCamColour[0];
          lightColour[i][j][1]*=invCamColour[1];
          lightColour[i][j][2]*=invCamColour[2];
        }
        lightAngle[i] = VectorOperations.vectorNormalization3D(lightAngle[i]);
      }

      for(int i = 0; i < modelList.size(); i++){
        tempModel = modelList.removeFirst();
        modelList.add(tempModel);

        vertices = new float[tempModel.returnVertexNormals().length][];
        frustumFlags = new boolean[vertices.length];
        primativeVertices = new float[vertices.length][4];
        brightnessValues = new float[vertices.length][4];
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
        Matrix4x4 model;
        Matrix4x4 transform = tempModel.transform(true);
        //For if the model is not a billboard
        if(!tempModel.returnIsBillBoard())
          model = MatrixOperations.matrixMultiply(view, MatrixOperations.matrixMultiply(transform, tempModel.returnModelMatrix()));
        //For if the model is a billboard
        else{
          //Constructs the transformation matrix for the point
          model = MatrixOperations.matrixMultiply(view, MVP.returnTranslation(MatrixOperations.matrixMultiply(transform, from3DVecTo4DVec(tempModel.returnPosition()))));    
          model.copy(MatrixOperations.matrixMultiply(model, billBoard));       
          //Enables rotation about the z-axis and scaling along the x and y axis
          Matrix4x4 modelMatrix = MatrixOperations.matrixMultiply(MVP.returnRotation(0, 0, tempModel.returnRotation()[2]), MVP.returnScale(tempModel.returnScale()[0], tempModel.returnScale()[1], 1));
          model = MatrixOperations.matrixMultiply(model, MatrixOperations.matrixMultiply(tempModel.transform(false), modelMatrix));
        }
       // model.copy(MatrixOperations.matrixMultiply(tempModel.transform(), model));

        mvpFull = MatrixOperations.matrixMultiply(proj, model); 

        //Checking if the model is in clipspace and adjusting the face direction to account for negative scales
        isInClipSpace = ((flags & 64) == 64) || (((isInClipSpace(mvp, tempModel.returnPosition()) || isInClipSpace(mvpFull, tempModel.returnBoundingBox())) && distCamToModel <= eye.getDrawDistance()));
        tempModel.setPosition(position[0], position[1], position[2]);
        //System.out.println(i+" "+tempModel.alwaysPerform());
        for(byte j = 0; j < 3; j++){
          if(tempModel.returnScale()[j] < 0)
            faceDirection = (byte)((~faceDirection)+1);
          if(eye.returnScale()[j] < 0)
            faceDirection = (byte)((~faceDirection)+1);
        }

      
        if(isInClipSpace && tempModel.returnModelTint() > Rasterizer.getMinTransparency()*Colour.INV_255){

          for(int j = 0; j < tempModel.returnPolygonCount(); j++){
            float[] triCentre = {0, 0, 0, 0};
            //Breaks out of the loops if the triangle count is greater than or equal to triListSize
            float edgeDir = 0; //The direction of the triangle
            float[][] points = new float[3][4]; //Triangle vertices
            boolean isInside = ((flags & 64) == 64); //If the triangle is inside the overal scene
            boolean inFrustum = isInside; //If the triangle's point is in front of the near plane
            byte numOfInside = 0; //Number of points in the near plane
            byte[] insidePoints = {-1, -1, -1}; //Tracking the indices of the points in the near plane
            float[][] vertexBrightness = {{1, 1, 1, 1}, {1, 1, 1, 1}, {1, 1, 1, 1}};
            for(byte s = 0; s < 3; s++){
              //Takes the current point and turns it into a homeogenous vector
              float[] homogeneousPoint = from3DVecTo4DVec(tempModel.returnPoints()[tempModel.returnPolygons()[j][s]]);
              
              int vertexIndex = tempModel.returnPolygons()[j][s];
              if(vertices[vertexIndex] == null){
                vertexBrightness[s][0] = tempModel.returnVertexColours()[vertexIndex][0];
                brightnessValues[vertexIndex][0] = vertexBrightness[s][0];
                //projection = MatrixOperations.matrixMultiply(model, homogeneousPoint);
                if(!tempModel.isCompletelyBlack() && tempModel.isGauroud()){
                  float[] lightBrightness = computeLighting(lights, lightPos, lightAngle, lightColour, homogeneousPoint, tempModel.returnVertexNormals()[vertexIndex], tempModel.returnShininess(), tempModel.returnBrightness()*generalObjectBrightness, model, tempModel, j, false);
                  vertexBrightness[s][1] = lightBrightness[0]*tempModel.returnVertexColours()[vertexIndex][1];
                  vertexBrightness[s][2] = lightBrightness[1]*tempModel.returnVertexColours()[vertexIndex][2];
                  vertexBrightness[s][3] = lightBrightness[2]*tempModel.returnVertexColours()[vertexIndex][3];
                  brightnessValues[vertexIndex][1] = vertexBrightness[s][1];
                  brightnessValues[vertexIndex][2] = vertexBrightness[s][2];
                  brightnessValues[vertexIndex][3] = vertexBrightness[s][3];
                }

                points[s] = MatrixOperations.matrixMultiply(mvpFull, homogeneousPoint);

                triCentre[0]+=points[s][0];
                triCentre[1]+=points[s][1];
                triCentre[2]+=points[s][2];
                triCentre[3]+=points[s][3];
                primativeVertices[vertexIndex][0] = points[s][0];
                primativeVertices[vertexIndex][1] = points[s][1];
                primativeVertices[vertexIndex][2] = points[s][2];
                primativeVertices[vertexIndex][3] = points[s][3];
                inFrustum = (points[s][2] >= -points[s][3] && points[s][2] <= points[s][3]);
                //Tracks which points are in front of the near plane or behind the near plane
                if(inFrustum || (flags & 64) == 64){
                  insidePoints[numOfInside] = s;
                  numOfInside++;
                }
                else
                  insidePoints[2] = s;
                
                //inFrustum&=(points[s][0] >= -points[s][3] && points[s][0] <= points[s][3] && points[s][1] >= -points[s][3] && points[s][1] <= points[s][3]);
                //Homogeneous division
                if(Math.abs(points[s][3]) > 0){
                  points[s][0] = (points[s][0]/points[s][3]);
                  points[s][1] = (points[s][1]/points[s][3]);
                  points[s][2] = (points[s][2]/points[s][3]);
                }
                //System.out.println(i+" "+vertexIndex+" "+points[s][2]);
                //Adjusts point positions to place the screen origin at the centre of the canvas, with scaling with respect to the screen dimensions
                points[s][0] = (Rasterizer.halfWidth()*(points[s][0]+1)-0.5001f);
                points[s][1] = (Rasterizer.halfHeight()*(points[s][1]+1)-0.5001f);
                  

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
                vertexBrightness[s][3] = brightnessValues[vertexIndex][3];
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
                int[] vertexIndices = {tempModel.returnPolygons()[j][0], 
                                       tempModel.returnPolygons()[j][1], 
                                       tempModel.returnPolygons()[j][2]};                       
                vertexBrightness[0][1] = lightBrightness[0]*tempModel.returnVertexColours()[vertexIndices[0]][1];
                vertexBrightness[0][2] = lightBrightness[1]*tempModel.returnVertexColours()[vertexIndices[0]][2];
                vertexBrightness[0][3] = lightBrightness[2]*tempModel.returnVertexColours()[vertexIndices[0]][3];                    
                vertexBrightness[1][1] = lightBrightness[0]*tempModel.returnVertexColours()[vertexIndices[1]][1];
                vertexBrightness[1][2] = lightBrightness[1]*tempModel.returnVertexColours()[vertexIndices[1]][2];
                vertexBrightness[1][3] = lightBrightness[2]*tempModel.returnVertexColours()[vertexIndices[1]][3];      
                vertexBrightness[2][1] = lightBrightness[0]*tempModel.returnVertexColours()[vertexIndices[2]][1];
                vertexBrightness[2][2] = lightBrightness[1]*tempModel.returnVertexColours()[vertexIndices[2]][2];
                vertexBrightness[2][3] = lightBrightness[2]*tempModel.returnVertexColours()[vertexIndices[2]][3];
                
              }
              //Returns if the current triangle is exempt from backface culling
              int[] colour = {tempModel.returnColours()[j][0], tempModel.returnColours()[j][1]}; //Holds the colours that get sent to the triangle rasterizer
              short[] alpha = {(short)((colour[0] >>> 24)*tempModel.returnModelTint()), (short)((colour[1] >>> 24)*tempModel.returnModelTint())};
              if(alpha[1] > Rasterizer.getMinTransparency()){
                //Adding triangles to the list with near-plane clipping (WHOOP WHOOP!!!)
                float[] intersect1 = {0, 0, 0}; //Coordinates for the first point of intersection between the triangle and the near plane
                float[] intersect2 = {0, 0, 0}; //Coordinates for the second point of intersection between the triangle and the near plane
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

                      intersect2[0] = points[insidePoints[0]][0] + t2*(points[otherPoint2][0]-points[insidePoints[0]][0]);
                      intersect2[1] = points[insidePoints[0]][1] + t2*(points[otherPoint2][1]-points[insidePoints[0]][1]);
                      intersect2[2] = points[insidePoints[0]][2] + t2*(points[otherPoint2][2]-points[insidePoints[0]][2]);
                      float[] vertexBrightness1 = {vertexBrightness[insidePoints[0]][0] + t1*(vertexBrightness[otherPoint1][0]-vertexBrightness[insidePoints[0]][0]),
                                                    vertexBrightness[insidePoints[0]][1] + t1*(vertexBrightness[otherPoint1][1]-vertexBrightness[insidePoints[0]][1]),
                                                    vertexBrightness[insidePoints[0]][2] + t1*(vertexBrightness[otherPoint1][2]-vertexBrightness[insidePoints[0]][2]),
                                                    vertexBrightness[insidePoints[0]][3] + t1*(vertexBrightness[otherPoint1][3]-vertexBrightness[insidePoints[0]][3])};
                      float[] vertexBrightness2 = {vertexBrightness[insidePoints[0]][0] + t2*(vertexBrightness[otherPoint2][0]-vertexBrightness[insidePoints[0]][0]),
                                                    vertexBrightness[insidePoints[0]][1] + t2*(vertexBrightness[otherPoint2][1]-vertexBrightness[insidePoints[0]][1]),
                                                    vertexBrightness[insidePoints[0]][2] + t2*(vertexBrightness[otherPoint2][2]-vertexBrightness[insidePoints[0]][2]),
                                                    vertexBrightness[insidePoints[0]][3] + t2*(vertexBrightness[otherPoint2][3]-vertexBrightness[insidePoints[0]][3])};
                      //Moving the points that are behind the near plane to be at the points of intersection
                      points[otherPoint1][0] = intersect1[0];
                      points[otherPoint1][1] = intersect1[1];
                      points[otherPoint1][2] = intersect1[2];
                            
                      points[otherPoint2][0] = intersect2[0];
                      points[otherPoint2][1] = intersect2[1];
                      points[otherPoint2][2] = intersect2[2];

                      vertexBrightness[otherPoint1][0] = vertexBrightness1[0];
                      vertexBrightness[otherPoint1][1] = vertexBrightness1[1];
                      vertexBrightness[otherPoint1][2] = vertexBrightness1[2];
                      vertexBrightness[otherPoint1][3] = vertexBrightness1[3];
                      vertexBrightness[otherPoint2][0] = vertexBrightness2[0];
                      vertexBrightness[otherPoint2][1] = vertexBrightness2[1];
                      vertexBrightness[otherPoint2][2] = vertexBrightness2[2];
                      vertexBrightness[otherPoint2][3] = vertexBrightness2[3];
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
                        
                    float[][] finalBrightness = new float[3][4];
                    //Calculating where each point of intersection is
                    intersect1[0] = points[insidePoints[0]][0] + t1*(points[insidePoints[2]][0]-points[insidePoints[0]][0]);
                    intersect1[1] = points[insidePoints[0]][1] + t1*(points[insidePoints[2]][1]-points[insidePoints[0]][1]);
                    intersect1[2] = points[insidePoints[0]][2] + t1*(points[insidePoints[2]][2]-points[insidePoints[0]][2]);
                    finalBrightness[insidePoints[0]][0] = vertexBrightness[insidePoints[0]][0] + t1*(vertexBrightness[insidePoints[2]][0]-vertexBrightness[insidePoints[0]][0]);
                    finalBrightness[insidePoints[0]][1] = vertexBrightness[insidePoints[0]][1] + t1*(vertexBrightness[insidePoints[2]][1]-vertexBrightness[insidePoints[0]][1]);
                    finalBrightness[insidePoints[0]][2] = vertexBrightness[insidePoints[0]][2] + t1*(vertexBrightness[insidePoints[2]][2]-vertexBrightness[insidePoints[0]][2]);
                    finalBrightness[insidePoints[0]][3] = vertexBrightness[insidePoints[0]][3] + t1*(vertexBrightness[insidePoints[2]][3]-vertexBrightness[insidePoints[0]][3]);

                    intersect2[0] = points[insidePoints[1]][0] + t2*(points[insidePoints[2]][0]-points[insidePoints[1]][0]);
                    intersect2[1] = points[insidePoints[1]][1] + t2*(points[insidePoints[2]][1]-points[insidePoints[1]][1]);
                    intersect2[2] = points[insidePoints[1]][2] + t2*(points[insidePoints[2]][2]-points[insidePoints[1]][2]);
                    finalBrightness[insidePoints[2]][0] = vertexBrightness[insidePoints[1]][0] + t2*(vertexBrightness[insidePoints[2]][0]-vertexBrightness[insidePoints[1]][0]);
                    finalBrightness[insidePoints[2]][1] = vertexBrightness[insidePoints[1]][1] + t2*(vertexBrightness[insidePoints[2]][1]-vertexBrightness[insidePoints[1]][1]);
                    finalBrightness[insidePoints[2]][2] = vertexBrightness[insidePoints[1]][2] + t2*(vertexBrightness[insidePoints[2]][2]-vertexBrightness[insidePoints[1]][2]);
                    finalBrightness[insidePoints[2]][3] = vertexBrightness[insidePoints[1]][3] + t2*(vertexBrightness[insidePoints[2]][3]-vertexBrightness[insidePoints[1]][3]);
                    float[][] secondPoints = new float[3][3];
                    //Constructing a triangle B'C'C
                    //C
                    secondPoints[insidePoints[1]][0] = points[insidePoints[1]][0];
                    secondPoints[insidePoints[1]][1] = points[insidePoints[1]][1];
                    secondPoints[insidePoints[1]][2] = points[insidePoints[1]][2];
                    finalBrightness[insidePoints[1]][0] = vertexBrightness[insidePoints[1]][0];
                    finalBrightness[insidePoints[1]][1] = vertexBrightness[insidePoints[1]][1];
                    finalBrightness[insidePoints[1]][2] = vertexBrightness[insidePoints[1]][2];
                    finalBrightness[insidePoints[1]][3] = vertexBrightness[insidePoints[1]][3];
                    //B'
                    secondPoints[insidePoints[0]][0] = intersect1[0];
                    secondPoints[insidePoints[0]][1] = intersect1[1];
                    secondPoints[insidePoints[0]][2] = intersect1[2];
                    //C'
                    secondPoints[insidePoints[2]][0] = intersect2[0];
                    secondPoints[insidePoints[2]][1] = intersect2[1];
                    secondPoints[insidePoints[2]][2] = intersect2[2];
                    //Modifiying the original triangle to be BCB'
                    points[insidePoints[2]][0] = intersect1[0];
                    points[insidePoints[2]][1] = intersect1[1];
                    points[insidePoints[2]][2] = intersect1[2];
                    vertexBrightness[insidePoints[2]][0] = finalBrightness[insidePoints[0]][0];
                    vertexBrightness[insidePoints[2]][1] = finalBrightness[insidePoints[0]][1];
                    vertexBrightness[insidePoints[2]][2] = finalBrightness[insidePoints[0]][2];
                    vertexBrightness[insidePoints[2]][3] = finalBrightness[insidePoints[0]][3];
                    //Adding the new triangle to the list to account for the clipped triangle being a quad
                    edgeDir = returnEdgeDir(tempModel, secondPoints, colour, alpha, backIndex, faceDirection, (flags & 8) == 8, (flags & 4) == 4);
                    if(edgeDir > 0){
                      if((alpha[1] & 0xFF) < 0xFF || vertexBrightness[0][0] < 1 || vertexBrightness[1][0] < 1 || vertexBrightness[2][0] < 1){
                        triListTranslucent.add(new Triangle(secondPoints, colour[0] , colour[1], tempModel.returnHasStroke() || (flags & 12) != 8, tempModel.returnHasFill() && (flags & 8) == 8));
                        triListTranslucent.getLast().setDepthWrite(!tempModel.returnDepthWrite());
                        triListTranslucent.getLast().setVertexBrightness(finalBrightness);
                        triListTranslucent.getLast().setAlpha(alpha[0], (byte)0);
                        triListTranslucent.getLast().setAlpha(alpha[1], (byte)1);
                        triListTranslucent.getLast().setFizzel(tempModel.returnMaxFizzel(), tempModel.returnFizzelThreshold());
                        triListTranslucent.getLast().setStencilAction(tempModel.returnStencilActionPtr());
                        translucentData.add(new TranslucentData((byte)1, triListTranslucent.getLast().getAverageZ(), tempModel.returnDepthWrite(), translusentCount));
                        translusentCount++;
                        translucentCounter++;
                      }
                      else{
                        triListOpaque.add(new Triangle(secondPoints, colour[0], colour[1], tempModel.returnHasStroke() || (flags & 12) != 8, tempModel.returnHasFill() && (flags & 8) == 8));
                        triListOpaque.getLast().setDepthWrite(!tempModel.returnDepthWrite());
                        triListOpaque.getLast().setAlpha(alpha[0], (byte)0);
                        triListOpaque.getLast().setAlpha(alpha[1], (byte)1);
                        triListOpaque.getLast().setVertexBrightness(finalBrightness);
                        triListOpaque.getLast().setStencilAction(tempModel.returnStencilActionPtr());
                        triListOpaque.getLast().setFizzel(tempModel.returnMaxFizzel(), tempModel.returnFizzelThreshold());
                      }
                    }
                  break;
                }
                  
                //Adding the triangle to the list
                edgeDir = returnEdgeDir(tempModel, points, colour, alpha, backIndex, faceDirection, (flags & 8) == 8, (flags & 4) == 4);
                if(edgeDir > 0){
                  if((alpha[1] & 0xFF) < 0xFF || vertexBrightness[0][0] < 1 || vertexBrightness[1][0] < 1 || vertexBrightness[2][0] < 1){
                    triListTranslucent.add(new Triangle(points, colour[0] , colour[1] , tempModel.returnHasStroke() || (flags & 12) != 8, tempModel.returnHasFill() && (flags & 8) == 8));
                    triListTranslucent.getLast().setDepthWrite(!tempModel.returnDepthWrite());
                    triListTranslucent.getLast().setVertexBrightness(vertexBrightness);
                    triListTranslucent.getLast().setAlpha(alpha[0], (byte)0);
                    triListTranslucent.getLast().setAlpha(alpha[1], (byte)1);
                    triListTranslucent.getLast().setFizzel(tempModel.returnMaxFizzel(), tempModel.returnFizzelThreshold());
                    triListTranslucent.getLast().setStencilAction(tempModel.returnStencilActionPtr());
                    translucentData.add(new TranslucentData((byte)1, triListTranslucent.getLast().getAverageZ(), tempModel.returnDepthWrite(), translusentCount));
                    translusentCount++;
                    translucentCounter++;
                  }
                  else{
                    triListOpaque.add(new Triangle(points, colour[0], colour[1], tempModel.returnHasStroke() || (flags & 12) != 8, tempModel.returnHasFill() && (flags & 8) == 8));
                    triListOpaque.getLast().setDepthWrite(!tempModel.returnDepthWrite());
                    triListOpaque.getLast().setVertexBrightness(vertexBrightness);
                    triListOpaque.getLast().setAlpha(alpha[0], (byte)0);
                    triListOpaque.getLast().setAlpha(alpha[1], (byte)1);
                    triListOpaque.getLast().setStencilAction(tempModel.returnStencilActionPtr());
                    triListOpaque.getLast().setFizzel(tempModel.returnMaxFizzel(), tempModel.returnFizzelThreshold());
                  }
                }
              }
            }
          }
        }
      }

      billBoardCountTranslucent = 0;
      for(int i = 0; i < billboardList.size(); i++){
        tempBillboard = billboardList.removeFirst();
        billboardList.add(tempBillboard);

        float[] fromCamToBillboard = {tempBillboard.returnPosition()[0]-eye.returnPosition()[0],
                                      tempBillboard.returnPosition()[1]-eye.returnPosition()[1],
                                      tempBillboard.returnPosition()[2]-eye.returnPosition()[2]};
        float distCamToBillboard = (float)Math.sqrt(fromCamToBillboard[0]*fromCamToBillboard[0]+fromCamToBillboard[1]*fromCamToBillboard[1]+fromCamToBillboard[2]*fromCamToBillboard[2]);
        float[] position = {tempBillboard.returnPosition()[0], tempBillboard.returnPosition()[1], tempBillboard.returnPosition()[2]};
        //Sets up the model matrix and the MVP matrices
        if(tempBillboard.returnAttachedToCamera())
          attachObjectToCamera(tempBillboard.returnPosition(), eye);
        tempBillboard.setModelMatrix();
        if(distCamToBillboard <= eye.getDrawDistance() && tempBillboard.returnModelTint() > Rasterizer.getMinTransparency()*Colour.INV_255){
          


          //Constructs the transformation matrix for the point
          Matrix4x4 model = MatrixOperations.matrixMultiply(view, MVP.returnTranslation(MatrixOperations.matrixMultiply(tempBillboard.transform(true), from3DVecTo4DVec(tempBillboard.returnPosition()))));
          model.copy(MatrixOperations.matrixMultiply(model, billBoard));
          //Multiplying the transformed matrices by the scale of the model
          model.copy(MatrixOperations.matrixMultiply(model, MatrixOperations.matrixMultiply(tempBillboard.transform(false), MVP.returnScale(tempBillboard.returnScale()[0], tempBillboard.returnScale()[1], 1))));

          mvpFull = MatrixOperations.matrixMultiply(proj, model);
          tempBillboard.setPosition(position);

          float[][] points = {{-(tempBillboard.returnWidth() >>> 1), -(tempBillboard.returnHeight() >>> 1), 0, 1}, 
                              {(tempBillboard.returnWidth() >>> 1), -(tempBillboard.returnHeight() >>> 1), 0, 1},
                              {(tempBillboard.returnWidth() >>> 1), (tempBillboard.returnHeight() >>> 1), 0, 1}, 
                              {-(tempBillboard.returnWidth() >>> 1), (tempBillboard.returnHeight() >>> 1), 0, 1}};
          boolean isInside = ((flags & 64) == 64);
          for(byte j = 0; j < 4; j++){
            //Projects the point from 3D to 2D
            points[j] = MatrixOperations.matrixMultiply(mvpFull, points[j]);
            if(points[j][3] > 0){
              points[j][0]/=points[j][3];
              points[j][1]/=points[j][3];
              points[j][2]/=points[j][3];
            }
            points[j][0] = (Rasterizer.halfWidth()*(points[j][0]+1)-0.5001f);
            points[j][1] = (Rasterizer.halfHeight()*(points[j][1]+1)-0.5001f);
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
            if(alpha[1] > Rasterizer.getMinTransparency()){
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
                billboardDisplayOpaque.getLast().setHasStroke(tempBillboard.returnHasStroke() || (flags & 12) != 8);
                billboardDisplayOpaque.getLast().setHasFill(tempBillboard.hasImage() && (flags & 8) == 8);
                billboardDisplayOpaque.getLast().fill(fill, alpha[1]);
                billboardDisplayOpaque.getLast().stroke(tempBillboard.returnStroke(), alpha[0]);
              }
              else{
                billboardDisplayTranslucent.add(new Billboard());
                billboardDisplayTranslucent.getLast().copy(tempBillboard);
                billboardDisplayTranslucent.getLast().setScale(sizeX, sizeY);
                billboardDisplayTranslucent.getLast().setPosition(points[0][0], points[0][1], points[0][2]);
                billboardDisplayTranslucent.getLast().setHasStroke(tempBillboard.returnHasStroke() || (flags & 12) != 8);
                billboardDisplayTranslucent.getLast().setHasFill(tempBillboard.hasImage() && (flags & 8) == 8);
                billboardDisplayTranslucent.getLast().fill(fill, alpha[1]);
                billboardDisplayTranslucent.getLast().stroke(tempBillboard.returnStroke(), alpha[0]);
                translucentData.add(new TranslucentData((byte)2, points[0][2], tempBillboard.returnDepthWrite(), billBoardCountTranslucent));
                translucentCounter++;
                billBoardCountTranslucent++;
              }
            }
          }
        }
      }

      /*TODO: ADD IN LINE HANDLING */
      setupLines(mvp, mvpFull, eye, eye.getDrawDistance());
      setupDots(mvp, mvpFull, eye, eye.getDrawDistance());
      drawObjects(screen);
      size = lights.size();
      for(int i = 0; i < size; i++){
        Light light = lights.removeFirst();
        lights.add(light);
        if((flags & -128) == -128 || light.alwaysPerform())
          light.executeActions();
      }

      if((flags & -128) == -128 || eye.alwaysPerform())
        eye.executeActions();
      setLightColour(screen, eye.returnColour(), 1, Rasterizer.returnWidth(), Rasterizer.returnHeight());
    }

    private static void drawObjects(int[] screen){
      size = noDraw.size();
      for(int i = 0; i < size; i++){
        tempInvis = noDraw.removeFirst();
        noDraw.add(tempInvis);
        if((flags & -128) == -128 || tempInvis.alwaysPerform()){
          tempInvis.setModelMatrix();
          tempInvis.executeActions();
        }
      }
      size = modelList.size();
      for(int i = 0; i < size; i++){
        tempModel = modelList.removeFirst();
        modelList.add(tempModel);
        if((flags & -128) == -128 || tempModel.alwaysPerform())
          tempModel.executeActions();
      }
      size = billboardList.size();
      for(int i = 0; i < size; i++){
        tempBillboard = billboardList.removeFirst();
        billboardList.add(tempBillboard);
        if((flags & -128) == -128 || tempBillboard.alwaysPerform())
          tempBillboard.executeActions();
      }
      size = lineList.size();
      for(int i = 0; i < size; i++){
        tempLineObj = lineList.removeFirst();
        lineList.add(tempLineObj);
        if((flags & -128) == -128 || tempLineObj.alwaysPerform())
          tempLineObj.executeActions();
      }
      size = dotList.size();
      for(int i = 0; i < size; i++){
        tempDot = dotList.removeFirst();
        dotList.add(tempDot);
        if((flags & -128) == -128 || tempDot.alwaysPerform())
          tempDot.executeActions();
      }
      

      while(!dotDisplayOpaque.isEmpty()){
        tempDot = dotDisplayOpaque.removeFirst();
        Rasterizer.setPixel(tempDot.returnStroke(), (int)tempDot.returnPosition()[0], (int)tempDot.returnPosition()[1], tempDot.returnPosition()[2], tempDot.returnDepthWrite());
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
        Rasterizer.triangleDraw3D(tempTri, stencilComp, testType);
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
    private static boolean isInClipSpace(Matrix4x4 mvp, float[][] boundingBox){
      if(boundingBox[0].length < 3 || mvp.returnWidth() != 4 || mvp.returnHeight() != 4)
        return false;
      for(byte i = 0; i < boundingBox.length; i++){
        float[] coords = MatrixOperations.matrixMultiply(mvp, from3DVecTo4DVec(boundingBox[i]));
        if(coords[0] >= -coords[3] && coords[0] <= coords[3] && coords[1] >= -coords[3] && coords[1] <= coords[3] && coords[2] >= -coords[3] && coords[2] <= coords[3])
          return true;
        }
        return false;
    }

    private static boolean isInClipSpace(Matrix4x4 vp, float[] point){
      if(point.length < 3 || vp.returnWidth() != 4 || vp.returnHeight() != 4)
        return false;
      float[] newPoint = MatrixOperations.matrixMultiply(vp, from3DVecTo4DVec(point));
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

  private static void setLightColour(int[] screen, int lightColour, float lightIntensity, int width, int height){
    lightIntensity = Math.max(0, Math.min(lightIntensity, 1));
    lightColour&=0xFFFFFF;
    if(lightColour <= 0xFF)
      lightColour = (lightColour << 16)|(lightColour << 8)|lightColour;
    lightColour|=0xFF000000;
    float lightRed = ((lightColour >>> 16) & 0xFF)*lightIntensity*Colour.INV_255;
    float lightGreen = ((lightColour >>> 8) & 0xFF)*lightIntensity*Colour.INV_255;
    float lightBlue = (lightColour & 0xFF)*lightIntensity*Colour.INV_255;
    for(int i = 0; i < width; i++){
      for(int j = 0; j < height; j++){
        int pixelPos = i+width*j;

        float[] tempPixels = {((screen[pixelPos] >>> 16) & 0xFF)*Colour.INV_255, 
                              ((screen[pixelPos] >>> 8) & 0xFF)*Colour.INV_255,
                              (screen[pixelPos] & 0xFF)*Colour.INV_255};

        float[] adjColours = {tempPixels[0]*lightRed, tempPixels[1]*lightGreen, tempPixels[2]*lightBlue};
        int[] tempColour = {0xFF000000,
                            (int)(adjColours[0]*255),
                            (int)(adjColours[1]*255),
                            (int)(adjColours[2]*255)}; 

        if(ditherIntensity >= 0.00001 && (((flags & 1) == 0 && !Float.isNaN(Rasterizer.returnDepthBuffer()[pixelPos])) || (flags & 1) == 1)){
          float dither = ditherIntensity*ditherMatrix[(i%ditherMatrixSize)*ditherMatrixSize+(j%ditherMatrixSize)];
          if(ditherRange > 0.00001)
            dither+=(float)(Math.random()*(ditherRange*2)-ditherRange);
          tempColour[1] = (int)(Math.min(1, Math.max(0, ((int)((adjColours[0]+dither)*31+ditherThreshold)*0.032258064516129)))*255)>>>3<<3;
          tempColour[2] = (int)(Math.min(1, Math.max(0, ((int)((adjColours[1]+dither)*31+ditherThreshold)*0.032258064516129)))*255)>>>3<<3;
          tempColour[3] = (int)(Math.min(1, Math.max(0, ((int)((adjColours[2]+dither)*31+ditherThreshold)*0.032258064516129)))*255)>>>3<<3;
        }
        else{
          tempColour[1] = Math.min(255, Math.max(0, tempColour[1]));
          tempColour[2] = Math.min(255, Math.max(0, tempColour[2]));
          tempColour[3] = Math.min(255, Math.max(0, tempColour[3]));
        }

        screen[pixelPos] = tempColour[0]|(tempColour[1] << 16)|(tempColour[2] << 8)|tempColour[3];
      }
    }
  }


  private static void attachObjectToCamera(float[] modelPos, Camera eye){
    float[] tempPos = from3DVecTo4DVec(modelPos);
    Matrix4x4 rotation = MatrixOperations.matrixMultiply(MatrixOperations.matrixMultiply(MVP.returnRotation(0,0, eye.returnRotation()[2]), MVP.returnRotation(0,eye.returnRotation()[1],0)), MVP.returnRotation(eye.returnRotation()[0],0, 0));
    float[] offset = MatrixOperations.matrixMultiply(MatrixOperations.matrixMultiply(MVP.returnTranslation(eye.returnPosition()), rotation), tempPos);
    modelPos[0] = offset[0];
    modelPos[1] = offset[1];
    modelPos[2] = offset[2];
  }
  //Iterates through the triangles in each model and transforms them into a list of tris that can be drawn to the screen
  //This version does not account for lighting
  private static void setupTrisNoLight(Matrix4x4 mvp, Matrix4x4 mvpFull, Camera eye, float drawDist){
    triListOpaque.clear();
    triListTranslucent.clear();

    translusentCount = 0;
    byte faceDirection = 1;
    boolean isInClipSpace = false; //Checks if a model is in the frustum
    for(int i = 0; i < modelList.size(); i++){
      tempModel = modelList.removeFirst();
      modelList.add(tempModel);

      vertices = new float[tempModel.returnPoints().length][];
      brightnessValues = new float[vertices.length][4];
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
      Matrix4x4 transform = tempModel.transform(true);
      Matrix4x4 model = MatrixOperations.matrixMultiply(transform, tempModel.returnModelMatrix());

      //For if the model is not a billboard
      if(!tempModel.returnIsBillBoard())
        mvpFull.copy(MatrixOperations.matrixMultiply(mvp, model));
      //For if the model is a billboard
      else{
        float[] modelPos = dropW(MatrixOperations.matrixMultiply(transform, from3DVecTo4DVec(tempModel.returnPosition())));
        //Constructs the transformation matrix for the point
        mvpFull.copy(MatrixOperations.matrixMultiply(mvp, MVP.returnTranslation(modelPos)));
        mvpFull.copy(MatrixOperations.matrixMultiply(mvpFull, billBoard));
        
        //Enables rotation about the z-axis and scaling along the x and y axis
        Matrix4x4 modelMatrix = MatrixOperations.matrixMultiply(MVP.returnRotation(0, 0, tempModel.returnRotation()[2]), MVP.returnScale(tempModel.returnScale()[0], tempModel.returnScale()[1], 1));
        mvpFull.copy(MatrixOperations.matrixMultiply(mvpFull, MatrixOperations.matrixMultiply(tempModel.transform(false), modelMatrix)));
      }

      //Checking if the model is in clipspace and adjusting the face direction to account for negative scales
      isInClipSpace = ((flags & 64) == 64) || (((isInClipSpace(mvp, tempModel.returnPosition()) || isInClipSpace(mvpFull, tempModel.returnBoundingBox())) && distCamToModel <= drawDist));
      tempModel.setPosition(position[0], position[1], position[2]);
      for(byte j = 0; j < 3; j++){
        if(tempModel.returnScale()[j] < 0)
          faceDirection = (byte)((~faceDirection)+1);
        if(eye.returnScale()[j] < 0)
          faceDirection = (byte)((~faceDirection)+1);
      }
      
      if(isInClipSpace && tempModel.returnModelTint() > Rasterizer.getMinTransparency()*Colour.INV_255){
        for(int j = 0; j < tempModel.returnPolygonCount(); j++){
          float edgeDir = 0; //The direction of the triangle
          float[][] points = new float[3][4]; //Triangle vertices
          boolean isInside = ((flags & 64) == 64); //If the triangle is inside the overal scene
          boolean inFrustum = false; //If the triangle's point is in front of the near plane
          byte numOfInside = 0; //Number of points in the near plane
          byte[] insidePoints = {-1, -1, -1}; //Tracking the indices of the points in the near plane
          float[] centre = {0, 0, 0, 0};
          float[][] vertexBrightness = {{1, 1, 1, 1}, {1, 1, 1, 1}, {1, 1, 1, 1}};
          for(byte s = 0; s < 3; s++){
            int vertexIndex = tempModel.returnPolygons()[j][s];
            if(vertices[vertexIndex] == null){
              vertexBrightness[s][0] = tempModel.returnVertexColours()[vertexIndex][0];
              vertexBrightness[s][1] = tempModel.returnVertexColours()[vertexIndex][1];
              vertexBrightness[s][2] = tempModel.returnVertexColours()[vertexIndex][2];
              vertexBrightness[s][3] = tempModel.returnVertexColours()[vertexIndex][3];
              brightnessValues[vertexIndex][0] = vertexBrightness[s][0];
              brightnessValues[vertexIndex][1] = vertexBrightness[s][1];
              brightnessValues[vertexIndex][2] = vertexBrightness[s][2];
              brightnessValues[vertexIndex][3] = vertexBrightness[s][3];
              //Takes the current point and turns it into a homeogenous vector
              float[] homogeneousPoint = from3DVecTo4DVec(tempModel.returnPoints()[vertexIndex]);
              //Projects the point from 3D to 2D
              points[s] = MatrixOperations.matrixMultiply(mvpFull, homogeneousPoint);
              
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
              points[s][0] = (Rasterizer.halfWidth()*(points[s][0]+1)-0.5001f);
              points[s][1] = (Rasterizer.halfHeight()*(points[s][1]+1)-0.5001f);
              
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
              vertexBrightness[s][0] = brightnessValues[vertexIndex][0];
              vertexBrightness[s][1] = brightnessValues[vertexIndex][1];
              vertexBrightness[s][2] = brightnessValues[vertexIndex][2];
              vertexBrightness[s][3] = brightnessValues[vertexIndex][3];
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
            if(alpha[1] > Rasterizer.getMinTransparency()){
              //Adding triangles to the list with near-plane clipping (WHOOP WHOOP!!!)
              float[] intersect1 = {0, 0, 0}; //Coordinates for the first point of intersection between the triangle and the near plane
              float[] intersect2 = {0, 0, 0}; //Coordinates for the second point of intersection between the triangle and the near plane
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

                  intersect2[0] = points[insidePoints[0]][0] + t2*(points[otherPoint2][0]-points[insidePoints[0]][0]);
                  intersect2[1] = points[insidePoints[0]][1] + t2*(points[otherPoint2][1]-points[insidePoints[0]][1]);
                  intersect2[2] = points[insidePoints[0]][2] + t2*(points[otherPoint2][2]-points[insidePoints[0]][2]);
                  float[] vertexBrightness1 = {vertexBrightness[insidePoints[0]][0] + t1*(vertexBrightness[otherPoint1][0]-vertexBrightness[insidePoints[0]][0]),
                                                vertexBrightness[insidePoints[0]][1] + t1*(vertexBrightness[otherPoint1][1]-vertexBrightness[insidePoints[0]][1]),
                                                vertexBrightness[insidePoints[0]][2] + t1*(vertexBrightness[otherPoint1][2]-vertexBrightness[insidePoints[0]][2]),
                                                vertexBrightness[insidePoints[0]][3] + t1*(vertexBrightness[otherPoint1][3]-vertexBrightness[insidePoints[0]][3])};
                  float[] vertexBrightness2 = {vertexBrightness[insidePoints[0]][0] + t2*(vertexBrightness[otherPoint2][0]-vertexBrightness[insidePoints[0]][0]),
                                                vertexBrightness[insidePoints[0]][1] + t2*(vertexBrightness[otherPoint2][1]-vertexBrightness[insidePoints[0]][1]),
                                                vertexBrightness[insidePoints[0]][2] + t2*(vertexBrightness[otherPoint2][2]-vertexBrightness[insidePoints[0]][2]),
                                                vertexBrightness[insidePoints[0]][3] + t2*(vertexBrightness[otherPoint2][3]-vertexBrightness[insidePoints[0]][3])};
                  //Moving the points that are behind the near plane to be at the points of intersection
                  points[otherPoint1][0] = intersect1[0];
                  points[otherPoint1][1] = intersect1[1];
                  points[otherPoint1][2] = intersect1[2];
                        
                  points[otherPoint2][0] = intersect2[0];
                  points[otherPoint2][1] = intersect2[1];
                  points[otherPoint2][2] = intersect2[2];

                  vertexBrightness[otherPoint1][0] = vertexBrightness1[0];
                  vertexBrightness[otherPoint1][1] = vertexBrightness1[1];
                  vertexBrightness[otherPoint1][2] = vertexBrightness1[2];
                  vertexBrightness[otherPoint1][3] = vertexBrightness1[3];
                  vertexBrightness[otherPoint2][0] = vertexBrightness2[0];
                  vertexBrightness[otherPoint2][1] = vertexBrightness2[1];
                  vertexBrightness[otherPoint2][2] = vertexBrightness2[2];
                  vertexBrightness[otherPoint2][3] = vertexBrightness2[3];
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
                    
                float[][] finalBrightness = new float[3][4];
                //Calculating where each point of intersection is
                intersect1[0] = points[insidePoints[0]][0] + t1*(points[insidePoints[2]][0]-points[insidePoints[0]][0]);
                intersect1[1] = points[insidePoints[0]][1] + t1*(points[insidePoints[2]][1]-points[insidePoints[0]][1]);
                intersect1[2] = points[insidePoints[0]][2] + t1*(points[insidePoints[2]][2]-points[insidePoints[0]][2]);
                finalBrightness[insidePoints[0]][0] = vertexBrightness[insidePoints[0]][0] + t1*(vertexBrightness[insidePoints[2]][0]-vertexBrightness[insidePoints[0]][0]);
                finalBrightness[insidePoints[0]][1] = vertexBrightness[insidePoints[0]][1] + t1*(vertexBrightness[insidePoints[2]][1]-vertexBrightness[insidePoints[0]][1]);
                finalBrightness[insidePoints[0]][2] = vertexBrightness[insidePoints[0]][2] + t1*(vertexBrightness[insidePoints[2]][2]-vertexBrightness[insidePoints[0]][2]);
                finalBrightness[insidePoints[0]][3] = vertexBrightness[insidePoints[0]][3] + t1*(vertexBrightness[insidePoints[2]][3]-vertexBrightness[insidePoints[0]][3]);

                intersect2[0] = points[insidePoints[1]][0] + t2*(points[insidePoints[2]][0]-points[insidePoints[1]][0]);
                intersect2[1] = points[insidePoints[1]][1] + t2*(points[insidePoints[2]][1]-points[insidePoints[1]][1]);
                intersect2[2] = points[insidePoints[1]][2] + t2*(points[insidePoints[2]][2]-points[insidePoints[1]][2]);
                finalBrightness[insidePoints[2]][0] = vertexBrightness[insidePoints[1]][0] + t2*(vertexBrightness[insidePoints[2]][0]-vertexBrightness[insidePoints[1]][0]);
                finalBrightness[insidePoints[2]][1] = vertexBrightness[insidePoints[1]][1] + t2*(vertexBrightness[insidePoints[2]][1]-vertexBrightness[insidePoints[1]][1]);
                finalBrightness[insidePoints[2]][2] = vertexBrightness[insidePoints[1]][2] + t2*(vertexBrightness[insidePoints[2]][2]-vertexBrightness[insidePoints[1]][2]);
                finalBrightness[insidePoints[2]][3] = vertexBrightness[insidePoints[1]][3] + t2*(vertexBrightness[insidePoints[2]][3]-vertexBrightness[insidePoints[1]][3]);
                float[][] secondPoints = new float[3][4];
                //Constructing a triangle B'C'C
                //C
                secondPoints[insidePoints[1]][0] = points[insidePoints[1]][0];
                secondPoints[insidePoints[1]][1] = points[insidePoints[1]][1];
                secondPoints[insidePoints[1]][2] = points[insidePoints[1]][2];
                finalBrightness[insidePoints[1]][0] = vertexBrightness[insidePoints[1]][0];
                finalBrightness[insidePoints[1]][1] = vertexBrightness[insidePoints[1]][1];
                finalBrightness[insidePoints[1]][2] = vertexBrightness[insidePoints[1]][2];
                finalBrightness[insidePoints[1]][3] = vertexBrightness[insidePoints[1]][3];
                //B'
                secondPoints[insidePoints[0]][0] = intersect1[0];
                secondPoints[insidePoints[0]][1] = intersect1[1];
                secondPoints[insidePoints[0]][2] = intersect1[2];
                //C'
                secondPoints[insidePoints[2]][0] = intersect2[0];
                secondPoints[insidePoints[2]][1] = intersect2[1];
                secondPoints[insidePoints[2]][2] = intersect2[2];
                //Modifiying the original triangle to be BCB'
                points[insidePoints[2]][0] = intersect1[0];
                points[insidePoints[2]][1] = intersect1[1];
                points[insidePoints[2]][2] = intersect1[2];
                vertexBrightness[insidePoints[2]][0] = finalBrightness[insidePoints[0]][0];
                vertexBrightness[insidePoints[2]][1] = finalBrightness[insidePoints[0]][1];
                vertexBrightness[insidePoints[2]][2] = finalBrightness[insidePoints[0]][2];
                vertexBrightness[insidePoints[2]][3] = finalBrightness[insidePoints[0]][3];
                //Adding the new triangle to the list to account for the clipped triangle being a quad
                edgeDir = returnEdgeDir(tempModel, secondPoints, colour, alpha, backIndex, faceDirection, (flags & 8) == 8, (flags & 4) == 4);
                if(edgeDir > 0){
                  if((alpha[1] & 0xFF) < 0xFF || vertexBrightness[0][0] < 1 || vertexBrightness[1][0] < 1 || vertexBrightness[2][0] < 1){
                    triListTranslucent.add(new Triangle(secondPoints, colour[0] , colour[1], tempModel.returnHasStroke() || (flags & 12) != 8, tempModel.returnHasFill() && (flags & 8) == 8));
                    triListTranslucent.getLast().setDepthWrite(!tempModel.returnDepthWrite());
                    triListTranslucent.getLast().setVertexBrightness(finalBrightness);
                    triListTranslucent.getLast().setAlpha(alpha[0], (byte)0);
                    triListTranslucent.getLast().setAlpha(alpha[1], (byte)1);
                    triListTranslucent.getLast().setStencilAction(tempModel.returnStencilActionPtr());
                    triListTranslucent.getLast().setFizzel(tempModel.returnMaxFizzel(), tempModel.returnFizzelThreshold());
                    translucentData.add(new TranslucentData((byte)1, triListTranslucent.getLast().getAverageZ(), tempModel.returnDepthWrite(), translusentCount));
                    translusentCount++;
                    translucentCounter++;
                  }
                  else{
                    triListOpaque.add(new Triangle(secondPoints, colour[0], colour[1], tempModel.returnHasStroke() || (flags & 12) != 8, tempModel.returnHasFill() && (flags & 8) == 8));
                    triListOpaque.getLast().setDepthWrite(!tempModel.returnDepthWrite());
                    triListOpaque.getLast().setAlpha(alpha[0], (byte)0);
                    triListOpaque.getLast().setAlpha(alpha[1], (byte)1);
                    triListOpaque.getLast().setVertexBrightness(finalBrightness);
                    triListOpaque.getLast().setStencilAction(tempModel.returnStencilActionPtr());
                    triListOpaque.getLast().setFizzel(tempModel.returnMaxFizzel(), tempModel.returnFizzelThreshold());
                  }
                }
                break;
              }
              
              //Adding the triangle to the list
              edgeDir = returnEdgeDir(tempModel, points, colour, alpha, backIndex, faceDirection, (flags & 8) == 8, (flags & 4) == 4);
              if(edgeDir > 0){
                if((alpha[1] & 0xFF) < 0xFF || vertexBrightness[0][0] < 1 || vertexBrightness[1][0] < 1 || vertexBrightness[2][0] < 1){
                  triListTranslucent.add(new Triangle(points, colour[0], colour[1], tempModel.returnHasStroke() || (flags & 12) != 8, tempModel.returnHasFill() && (flags & 8) == 8));
                  triListTranslucent.getLast().setDepthWrite(!tempModel.returnDepthWrite());
                  triListTranslucent.getLast().setVertexBrightness(vertexBrightness);
                  triListTranslucent.getLast().setFizzel(tempModel.returnMaxFizzel(), tempModel.returnFizzelThreshold());
                  triListTranslucent.getLast().setAlpha(alpha[0], (byte)0);
                  triListTranslucent.getLast().setAlpha(alpha[1], (byte)1);
                  triListTranslucent.getLast().setStencilAction(tempModel.returnStencilActionPtr());
                  translucentData.add(new TranslucentData((byte)1, triListTranslucent.peekLast().getAverageZ(), tempModel.returnDepthWrite(), translusentCount));
                  translusentCount++;
                  translucentCounter++;
                }
                else{
                  triListOpaque.add(new Triangle(points, colour[0], colour[1], tempModel.returnHasStroke() || (flags & 12) != 8, tempModel.returnHasFill() && (flags & 8) == 8));
                  triListOpaque.getLast().setAlpha(alpha[0], (byte)0);
                  triListOpaque.getLast().setAlpha(alpha[1], (byte)1);
                  triListOpaque.getLast().setVertexBrightness(vertexBrightness);
                  triListOpaque.getLast().setDepthWrite(!tempModel.returnDepthWrite());
                  triListOpaque.getLast().setStencilAction(tempModel.returnStencilActionPtr());
                  triListOpaque.getLast().setFizzel(tempModel.returnMaxFizzel(), tempModel.returnFizzelThreshold());
                }
              }
            }
          }
        }
      }
    }
  }
  //Iterates through each billboard and transforms it into 3D space
  private static void setupBillboardsNoLight(Matrix4x4 mvp, Matrix4x4 mvpFull, Camera eye, float drawDist){
    billboardDisplayOpaque.clear();
    billboardDisplayTranslucent.clear();
    billBoardCountTranslucent = 0;
    for(int i = 0; i < billboardList.size(); i++){
      tempBillboard = billboardList.removeFirst();
      billboardList.add(tempBillboard);
      float[] fromCamToBillboard = {tempBillboard.returnPosition()[0]-eye.returnPosition()[0],
                                    tempBillboard.returnPosition()[1]-eye.returnPosition()[1],
                                    tempBillboard.returnPosition()[2]-eye.returnPosition()[2]};
      float distCamToBillboard = (float)Math.sqrt(fromCamToBillboard[0]*fromCamToBillboard[0]+fromCamToBillboard[1]*fromCamToBillboard[1]+fromCamToBillboard[2]*fromCamToBillboard[2]);
      float[] position = {tempBillboard.returnPosition()[0], tempBillboard.returnPosition()[1], tempBillboard.returnPosition()[2]};
      //Sets up the model matrix and the MVP matrices
      if(tempBillboard.returnAttachedToCamera())
        attachObjectToCamera(tempBillboard.returnPosition(), eye);
      tempBillboard.setModelMatrix();
      if(distCamToBillboard <= drawDist && tempBillboard.returnModelTint() > Rasterizer.getMinTransparency()*Colour.INV_255){
        //Constructs the transformation matrix for the point
        mvpFull.copy(MatrixOperations.matrixMultiply(mvp, MVP.returnTranslation(MatrixOperations.matrixMultiply(tempBillboard.transform(true), from3DVecTo4DVec(tempBillboard.returnPosition())))));
        mvpFull.copy(MatrixOperations.matrixMultiply(mvpFull, billBoard));
        //Multiplying the transformed matrices by the scale of the model
        mvpFull.copy(MatrixOperations.matrixMultiply(mvpFull, MatrixOperations.matrixMultiply(tempBillboard.transform(false), MVP.returnScale(tempBillboard.returnScale()[0], tempBillboard.returnScale()[1], 1))));
        tempBillboard.setPosition(position);
        float[][] points = {{-(tempBillboard.returnWidth() >>> 1), -(tempBillboard.returnHeight() >>> 1), 0, 1}, 
                            {(tempBillboard.returnWidth() >>> 1), -(tempBillboard.returnHeight() >>> 1), 0, 1},
                            {(tempBillboard.returnWidth() >>> 1), (tempBillboard.returnHeight() >>> 1), 0, 1}, 
                            {-(tempBillboard.returnWidth() >>> 1), (tempBillboard.returnHeight() >>> 1), 0, 1}};
        boolean isInside = ((flags & 64) == 64);
        for(byte j = 0; j < 4; j++){
          //Projects the point from 3D to 2D
          points[j] = MatrixOperations.matrixMultiply(mvpFull, points[j]);
          if(points[j][3] > 0){
            points[j][0]/=points[j][3];
            points[j][1]/=points[j][3];
            points[j][2]/=points[j][3];
          }
          points[j][0] = (Rasterizer.halfWidth()*(points[j][0]+1)-0.5001f);
          points[j][1] = (Rasterizer.halfHeight()*(points[j][1]+1)-0.5001f);
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
          if(alpha[1] > Rasterizer.getMinTransparency()){
            float sizeX = points[2][0]-points[0][0];
            float sizeY = points[2][1]-points[0][1];
            if((alpha[1] & 0xFF) == 255){
              billboardDisplayOpaque.add(new Billboard());
              billboardDisplayOpaque.getLast().copy(tempBillboard);
              billboardDisplayOpaque.getLast().setScale(sizeX, sizeY);
              billboardDisplayOpaque.getLast().stroke(tempBillboard.returnStroke(), alpha[0]);
              billboardDisplayOpaque.getLast().fill(fill, alpha[1]);
              billboardDisplayOpaque.getLast().setPosition(points[0][0], points[0][1], points[0][2]);
              billboardDisplayOpaque.getLast().setHasStroke(tempBillboard.returnHasStroke() || (flags & 12) != 8);
              billboardDisplayOpaque.getLast().setHasFill(tempBillboard.hasImage() && (flags & 8) == 8);
            }
            else{
              billboardDisplayTranslucent.add(new Billboard());
              billboardDisplayTranslucent.getLast().copy(tempBillboard);
              billboardDisplayTranslucent.getLast().setScale(sizeX, sizeY);
              billboardDisplayTranslucent.getLast().stroke(tempBillboard.returnStroke(), alpha[0]);
              billboardDisplayTranslucent.getLast().fill(fill, alpha[1]);
              billboardDisplayTranslucent.getLast().setPosition(points[0][0], points[0][1], points[0][2]);
              billboardDisplayTranslucent.getLast().setHasStroke(tempBillboard.returnHasStroke() || (flags & 12) != 8);
              billboardDisplayTranslucent.getLast().setHasFill(tempBillboard.hasImage() && (flags & 8) == 8);
              translucentData.add(new TranslucentData((byte)2, points[0][2], tempBillboard.returnDepthWrite(), billBoardCountTranslucent));
              translucentCounter++;
              billBoardCountTranslucent++;
            }
          }
        }
      }
    }
  }
  /*TODO: ADD IN LINE PROCESSING*/
  //Transforms the lines from 3D local space to 2D screen space
  private static void setupLines(Matrix4x4 mvp, Matrix4x4 mvpFull, Camera eye, float drawDist){
    lineCountTranslucent = 0;
    lineDisplayOpaque.clear();
    lineDisplayTranslucent.clear();
    primativeVertices = null;
    boolean isInClipSpace = false;
    for(int i = 0; i < lineList.size(); i++){
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
      mvpFull.copy(MatrixOperations.matrixMultiply(mvp, MatrixOperations.matrixMultiply(tempLineObj.transform(), tempLineObj.returnModelMatrix())));
      tempLineObj.setPosition(position);
      //Checking if the model is in clipspace and adjusting the face direction to account for negative scales
      isInClipSpace = ((flags & 64) == 64) || (((isInClipSpace(mvp, tempLineObj.returnPosition()) || isInClipSpace(mvpFull, tempLineObj.returnBoundingBox())) && distCamToModel <= drawDist));
      float[][] pointPair = tempLineObj.returnLineModelPtr().returnVertices();
      if(isInClipSpace && tempLineObj.returnModelTint() > Rasterizer.getMinTransparency()*Colour.INV_255){
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
              points[s] = MatrixOperations.matrixMultiply(mvpFull, points[s]);
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
              points[s][0] = (Rasterizer.halfWidth()*(points[s][0]+1)-0.5001f);
              points[s][1] = (Rasterizer.halfHeight()*(points[s][1]+1)-0.5001f);
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
            if(alpha > Rasterizer.getMinTransparency()){
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
                lineDisplayOpaque.getLast().setDepthWrite(tempLineObj.returnDepthWrite());
              }
              else{
                lineDisplayTranslucent.add(new LineDisp(points, colour));
                lineDisplayTranslucent.getLast().setAlpha(alpha);
                lineDisplayTranslucent.getLast().setDepthWrite(tempLineObj.returnDepthWrite());
                translucentData.add(new TranslucentData((byte)3, (points[1][2]+points[0][2])*0.5f, tempLineObj.returnDepthWrite(), lineCountTranslucent));
                translucentCounter++;
                lineCountTranslucent++;
              }
            }
          }
        }
      }
    }
  }
  //A function for transforming single-pixel dots to screenspace
  private static void setupDots(Matrix4x4 mvp, Matrix4x4 mvpFull, Camera eye, float drawDist){
    dotDisplayOpaque.clear();
    dotDisplayTranslucent.clear();
    dotCountTranslucent = 0;
    for(int i = 0; i < dotList.size(); i++){
      tempDot = dotList.removeFirst();
      dotList.add(tempDot);
      tempDot.setModelMatrix();
      float[] position = {tempDot.returnPosition()[0], tempDot.returnPosition()[1], tempDot.returnPosition()[2]};
      if(tempDot.returnAttachedToCamera())
        attachObjectToCamera(tempDot.returnPosition(), eye);
      tempDot.setPosition(position);
      float[] point = from3DVecTo4DVec(tempDot.returnPosition());
      boolean isInside = ((flags & 64) == 64);
      //Projects the point from 3D to 2D
      float[] clipCheck = MatrixOperations.matrixMultiply(view, MatrixOperations.matrixMultiply(tempDot.transform(), point)); 
      point = MatrixOperations.matrixMultiply(mvp, point);
      if(point[3] > 0){
        point[0]/=point[3];
        point[1]/=point[3];
        point[2]/=point[3];
      }
      point[0] = (Rasterizer.halfWidth()*(point[0]+1)-0.5001f);
      point[1] = (Rasterizer.halfHeight()*(point[1]+1)-0.5001f);
      isInside = (isInside || (point[3] > 0 && clipCheck[2] >= -1 && clipCheck[2] <= drawDist)) && (point[0] >= 0 && point[0] <= Rasterizer.returnWidth() && point[1] >= 0 && point[1] <= Rasterizer.returnHeight());
      if(isInside && tempDot.returnModelTint() > Rasterizer.getMinTransparency()*Colour.INV_255){
        int colour = tempDot.returnStroke(); 
        short alpha = (short)((tempDot.returnStroke() >>> 24)*tempDot.returnModelTint());
        if(alpha > Rasterizer.getMinTransparency()){
          if((alpha & 0xFF) == 255){
            dotDisplayOpaque.add(new Dot(point, colour));
            dotDisplayOpaque.getLast().setAlpha(alpha);
            dotDisplayOpaque.getLast().setDepthWrite(tempDot.returnDepthWrite());
          }
          else{
            dotDisplayTranslucent.add(new Dot(point, colour));
            dotDisplayTranslucent.getLast().setDepthWrite(tempDot.returnDepthWrite());
            dotDisplayTranslucent.getLast().setAlpha(alpha);
            translucentData.add(new TranslucentData((byte)4, point[2], tempDot.returnDepthWrite(), dotCountTranslucent));
            translucentCounter++;
            dotCountTranslucent++;
          }
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
          Rasterizer.triangleDraw3D(tempTris[j], stencilComp, testType);
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
          Rasterizer.setPixel(tempDots[j].returnStroke(), tempDots[j].returnPosition()[0], tempDots[j].returnPosition()[1], tempDots[j].returnPosition()[2], tempDots[j].returnDepthWrite());
          break;
      }
    }
  }
  private static float[] computeLighting(LinkedList<Light> lights, float[][] lightPos, float[][] lightAngle, float[][][] lightColour, float[] homogeneousPoint, float[] normal, float luster, float overallBrightness, Matrix4x4 model, Model tempModel, int polygonIndex, boolean alreadyComputed){
    float[] points = new float[3];
    float[] brightness = {0, 0, 0};
    if(!alreadyComputed)
      points = dropW(MatrixOperations.matrixMultiply(model, homogeneousPoint));
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
      normalizedVec = dropW(MatrixOperations.matrixMultiply(model, from3DVecTo4DVec(VectorOperations.vectorNormalization3D(normal), 0)));
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

      float[] halfVec = {0, 0, 0};

      if(light.returnType() != 'd'){
        lightToTriVec[0] = lightPos[i][0]-points[0];
        lightToTriVec[1] = lightPos[i][1]-points[1];
        lightToTriVec[2] = lightPos[i][2]-points[2];

        r2 = vectorDotProduct(lightToTriVec, lightToTriVec);
        if(Math.abs(r2) > 0.00000001)
          r2 = 1/r2;
        else if(r2 <= 0.00000001)
          r2 = 100000000;
        else
          r2 = -100000000;
        lightToTriVec = VectorOperations.vectorNormalization3D(lightToTriVec);
        halfVec = VectorOperations.vectorNormalization3D(VectorOperations.vectorAddition(camToTriVec, lightToTriVec));
      }
      else
        halfVec = VectorOperations.vectorNormalization3D(VectorOperations.vectorAddition(camToTriVec, lightAngle[i]));
      
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

  //Forces a vector with dimensions higher than three to be 3D
  private static float[] dropW(float[] vector){
    if(vector.length < 3){
      System.out.println("ERROR: WRONG NUMBER OF DIMENSIONS (MUST BE AT LEAST 3)");
      System.exit(1);
    }
    float[] output = {vector[0], vector[1], vector[2]};
    return output;
  }
}