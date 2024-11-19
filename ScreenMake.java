public class ScreenMake{
    //Set up for the stencil test
    private static byte stencilComp = 0;
    private static char testType = 'e';
    
    private static Matrix persp = MVP.perspMatrix(1, 90, -1, 1); //The perspective projection matrix
    private static Matrix ortho = MVP.orthoMatrix(1, 90, -1, 1); //The orthographic projection matrix
    /*
    Flags
    Bit 0: anti-aliasing enable (high enable)
    Bit 1: Solid background enable (low enable)
    Bit 2: Forced stroke enable (high enable)
    Bit 3: Forced fill enable (high enable)
    Bit 4: A billboard exists in model list (high enable)
    Bit 5: The billboarded sprite list is not empty (high enable)
    */
    private static byte flags = 8; 
    private static int triListSize = 2048;
    private static int billboardListSize = 2048;
    private static Triangle[] displayList = new Triangle[triListSize]; //Full triangle list
    private static Billboard[] billboardDisplayOpaque = new Billboard[billboardListSize]; //List of opaque billboards
    private static Billboard[] billboardDisplayTranslucent = new Billboard[billboardListSize]; //List of translucent billboards
    private static Model[] modelList = new Model[0]; //List of models
    private static Billboard[] billboardList = new Billboard[0]; //List of billboards
    private static Model[] refListM = modelList;
    private static Billboard[] refListB = billboardList;
    private static Matrix billBoard;
    
    //Changes the model list's pointer to a different array's location
    public static void setModelList(Model[] newList){
      modelList = newList;
      flags&=-17;
      for(int i = 0; i < modelList.length; i++)
        if(modelList[i].returnIsBillBoard()){
          flags|=16;
          break;
        }
    }
    public static void disableModelList(){
      modelList = refListM;
      flags&=-17;
    }

    public static void setTriListSize(int newSize){
      triListSize = newSize;
      displayList = new Triangle[triListSize];
      flags&=-17;
    }
    public static void setBillboardListSize(int newSize){
      billboardListSize = newSize;
      billboardDisplayOpaque = new Billboard[billboardListSize];
      billboardDisplayTranslucent = new Billboard[billboardListSize];
      flags&=-33;
    }

    //Changes the billboard list's pointer to a different array's location
    public static void setBillboardList(Billboard[] newList){
      billboardList = newList;
      if(newList.length > 0)
        flags|=32;
      else
        flags&=-33;
    }
    public static void disableBillboardList(){
      billboardList = refListB;
      flags&=-33;
    }
    //Initializes projection matrices
    public static void initPersp(int wid, int heig, int fovY){
      persp.copy(MVP.perspMatrix((float)wid/heig, fovY, -1, 1));
    }
    public static void initOrtho(int wid, int heig, int fovY){
      ortho.copy(MVP.orthoMatrix((float)wid/heig, fovY, -1, 1));
    }
    //Sets the stencil test parameters
    public static void setStencilTest(byte newComp, char newTest){
      stencilComp = newComp;
      testType = newTest;
    }
    //Disables anti-aliasing
    public static void noSmooth(){
      flags&=-2;
    }
    //Enables anti-aliasing
    public static void smooth(){
      flags|=1;
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

    //Takes in a frame buffer, near-z, far-z, and camera and draws a 3D scene using that data plus the model and billboard lists
    public static void drawScene(int[] screen, float zNear, float zFar, Camera eye){
      //Sets the background
      TriangleRasterizer.setAntiAlias((flags & 1) == 1);
      if((flags & 2) == 0)
        TriangleRasterizer.initBuffers(screen); //Solid colour
      else
        TriangleRasterizer.initBuffers(); //Whatever is currently on the screen
      
      //Data for tracking the number of triangles in the list, and how many are opaque and how many are translucent
      int numberOfTris = 0;
      byte faceDirection = 1; //Tracks the direction of each face
      int opaqueCount = 0;
      int translusentCount = 0;
        
      //Sets up some of the MVP stuff
      MVP.setEyeAngles(eye.returnRotation());
      MVP.setEyeShear(eye.returnShear());
      //Computes the inverse of the view matrix
      if((flags & 48) != 0){
        MVP.setEyePos(0, 0, 0);
        MVP.setEyeScale(1, 1, 1);
        billBoard = MatrixOperations.invertMatrix(MVP.viewMatrix());
      }

      MVP.setEyePos(eye.returnPosition());
      MVP.setEyeScale(eye.returnScale());

      //Matrices for projection
      Matrix mvp = MatrixOperations.matrixMultiply(persp, MVP.viewMatrix());
      Matrix mvpFull = new Matrix();
      Matrix mv = new Matrix(); //For checking if a model is within zNear and zFar (using the frustum does not work for some reason)
      boolean isInClipSpace = false; //Checks if a model is in the frustum

      listCreator:
      for(int i = 0; i < modelList.length; i++){
        //Sets up the matrices for the current model
        faceDirection = 1;
        if(!modelList[i].returnAttachedToCamera())
          MVP.setModelPos(modelList[i].returnPosition()[0], modelList[i].returnPosition()[1], modelList[i].returnPosition()[2]);
        else{
          Matrix rotation = MatrixOperations.matrixMultiply(MVP.returnRotation(eye.returnRotation()[0],0,0), MatrixOperations.matrixMultiply(MVP.returnRotation(0,eye.returnRotation()[1],0), MVP.returnRotation(0,0,eye.returnRotation()[2])));
          Matrix offsetVals = MatrixOperations.matrixMultiply(MatrixOperations.matrixMultiply(rotation, MVP.returnTranslation(eye.returnPosition())), VectorOperations.from3DVecTo4DVec(modelList[i].returnPosition()));
          float[] offset = {offsetVals.returnData(0, 0), offsetVals.returnData(1, 0), offsetVals.returnData(2, 0)};
          MVP.setModelPos(offset);
        }
        
        //For if the model is not a billboard
        if(!modelList[i].returnIsBillBoard()){
          MVP.setModelShear(modelList[i].returnShear());
          MVP.setModelAngles(modelList[i].returnAngle()[0], modelList[i].returnAngle()[1], modelList[i].returnAngle()[2]);
          MVP.setModelScale(modelList[i].returnScale()[0], modelList[i].returnScale()[1], modelList[i].returnScale()[2]);
          mvpFull.copy(MatrixOperations.matrixMultiply(mvp, MVP.modelMatrix()));
          mv.copy(MatrixOperations.matrixMultiply(MVP.viewMatrix(), MVP.modelMatrix()));
        }
        //For if the model is a billboard
        else{
          //Constructs the transformation matrix for the point
          mvpFull.copy(MatrixOperations.matrixMultiply(mvp, MVP.returnTranslation(MVP.returnModelPos((byte)0), MVP.returnModelPos((byte)1), MVP.returnModelPos((byte)2))));
          mvpFull.copy(MatrixOperations.matrixMultiply(mvpFull, billBoard));
          mv.copy(MatrixOperations.matrixMultiply(MVP.viewMatrix(), MVP.returnTranslation(MVP.returnModelPos((byte)0), MVP.returnModelPos((byte)1), MVP.returnModelPos((byte)2))));
          mv.copy(MatrixOperations.matrixMultiply(mv, billBoard));
          
          //Enables rotation about the z-axis and scaling along the x and y axis
          Matrix modelMatrix = MatrixOperations.matrixMultiply(MVP.returnRotation(0, 0, modelList[i].returnAngle()[2]), MVP.returnScale(modelList[i].returnScale()[0], modelList[i].returnScale()[1], 1));
          mvpFull.copy(MatrixOperations.matrixMultiply(mvpFull, modelMatrix));
          mv.copy(MatrixOperations.matrixMultiply(mv, modelMatrix));
        }
            
        //Checking if the model is in clipspace and adjusting the face direction to account for negative scales
        isInClipSpace = isInClipSpace(mvpFull, modelList[i].returnBoundingBox()) && isInClipSpace(mv, modelList[i].returnBoundingBox(), zNear, zFar);
        for(byte j = 0; j < 3; j++){
          if(modelList[i].returnScale()[j] < 0)
            faceDirection = (byte)((~faceDirection)+1);
          if(eye.returnScale()[j] < 0)
            faceDirection = (byte)((~faceDirection)+1);
        }
    
        if(isInClipSpace){
          for(int j = 0; j < modelList[i].returnPolygonCount(); j++){
            //Breaks out of the loops if the triangle count is greater than or equal to triListSize
            if(numberOfTris >= triListSize)
              break listCreator;
            float edgeDir = 0; //The direction of the triangle
            float[][] points = new float[3][4]; //Triangle vertices
            boolean isInside = false; //If the triangle is inside the overal scene
            boolean inFrustum = false; //If the triangle's point is in front of the near plane
            byte numOfInside = 0; //Number of points in the near plane
            byte[] insidePoints = {-1, -1, -1}; //Tracking the indices of the points in the near plane
            for(byte s = 0; s < 3; s++){
              //Takes the current point and turns it into a homeogenous vector
              float[] homogeneousPoint = VectorOperations.from3DVecTo4DVec(modelList[i].returnPoints()[modelList[i].returnPolygons()[j][s]]);
              //Projects the point from 3D to 2D
              Matrix clipCheck = MatrixOperations.matrixMultiply(mv, homogeneousPoint); 
              Matrix projection = MatrixOperations.matrixMultiply(mvpFull, homogeneousPoint);
              
              //Copying the results of the projection to a set of points that are less tedious to work with
              points[s][0] = (projection.returnData(0, 0)-0.0001f); //x
              points[s][1] = (projection.returnData(1, 0)-0.0001f); //y
              points[s][2] = (projection.returnData(2, 0)-0.0001f); //z
              points[s][3] = (projection.returnData(3, 0)-0.0001f); //w

              inFrustum = (points[s][2] >= -points[s][3] && points[s][2] <= points[s][3]);
              //Homogeneous division
              if(Math.abs(points[s][3]) > 0.0001){
                points[s][0] = (points[s][0]/points[s][3]-0.0001f);
                points[s][1] = (points[s][1]/points[s][3]-0.0001f);
                points[s][2] = (points[s][2]/points[s][3]-0.0001f);
              }

              //Adjusts point positions to place the screen origin at the centre of the canvas, with scaling with respect to the screen dimensions
              points[s][0] = (0.5f*TriangleRasterizer.returnWidth()*(points[s][0]+1)-0.0001f);
              points[s][1] = (0.5f*TriangleRasterizer.returnHeight()*(points[s][1]+1)-0.0001f);
              
              //Tracks which points are in front of the near plane or behind the near plane
              if(inFrustum){
                insidePoints[numOfInside] = s;
                numOfInside++;
              }
              else
                insidePoints[2] = s;
              isInside|=(clipCheck.returnData(2,0) <= zFar && inFrustum);
            }
            //Where the triangle is on screen
            int[] xBounds = {(int)Math.min(points[0][0], Math.min(points[1][0], points[2][0])), (int)Math.max(points[0][0], Math.max(points[1][0], points[2][0]))};
            int[] yBounds = {(int)Math.min(points[0][1], Math.min(points[1][1], points[2][1])), (int)Math.max(points[0][1], Math.max(points[1][1], points[2][1]))};
            isInside&=(xBounds[1] >= 0 && xBounds[0] < TriangleRasterizer.returnWidth() && yBounds[1] >= 0 && yBounds[0] < TriangleRasterizer.returnHeight());
            //Copies the triangle from the model to the triangle array
            if(isInside){
              //Returns if the current triangle is exempt from backface culling
              int backIndex = modelList[i].returnBackTri(j);
              int[] colour = {modelList[i].returnColours()[j][0], modelList[i].returnColours()[j][1]}; //Holds the colours that get sent to the triangle rasterizer
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
                    t1 = (1f - points[insidePoints[0]][2]);
                    if(Math.abs(points[otherPoint1][2] - points[insidePoints[0]][2]) > 0.0001) 
                      t1/=(points[otherPoint1][2]-points[insidePoints[0]][2]);
                    t2 = (1f - points[insidePoints[0]][2]);
                    if(Math.abs(points[otherPoint2][2] - points[insidePoints[0]][2]) > 0.0001) 
                      t2/=(points[otherPoint2][2]-points[insidePoints[0]][2]);
                    
                    //Computing the location of the point of intersection for each intersecting side
                    intersect1[0] = points[insidePoints[0]][0] + t1*(points[otherPoint1][0]-points[insidePoints[0]][0])-0.0001f;
                    intersect1[1] = points[insidePoints[0]][1] + t1*(points[otherPoint1][1]-points[insidePoints[0]][1])-0.0001f;
                    intersect1[2] = points[insidePoints[0]][2] + t1*(points[otherPoint1][2]-points[insidePoints[0]][2])-0.0001f;
                    
                    intersect2[0] = points[insidePoints[0]][0] + t2*(points[otherPoint2][0]-points[insidePoints[0]][0])-0.0001f;
                    intersect2[1] = points[insidePoints[0]][1] + t2*(points[otherPoint2][1]-points[insidePoints[0]][1])-0.0001f;
                    intersect2[2] = points[insidePoints[0]][2] + t2*(points[otherPoint2][2]-points[insidePoints[0]][2])-0.0001f;
                    
                    //Moving the points that are behind the near plane to be at the points of intersection
                    points[otherPoint1][0] = intersect1[0];
                    points[otherPoint1][1] = intersect1[1];
                    points[otherPoint1][2] = intersect1[2];
                    
                    points[otherPoint2][0] = intersect2[0];
                    points[otherPoint2][1] = intersect2[1];
                    points[otherPoint2][2] = intersect2[2];
                    break;
                //When 2 points are inside the frustum
                case 2:
                  //Calculating how far up each side the points of intersection are
                  t1 = (1f - points[insidePoints[0]][2]);
                  if(Math.abs(points[insidePoints[0]][2] - points[insidePoints[2]][2]) > 0.0001) 
                    t1/=(points[insidePoints[2]][2]-points[insidePoints[0]][2]);
                  t2 = (1f - points[insidePoints[1]][2]);
                  if(Math.abs(points[insidePoints[1]][2] - points[insidePoints[2]][2]) > 0.0001) 
                    t2/=(points[insidePoints[2]][2]-points[insidePoints[1]][2]);
                  
                  //Calculating where each point of intersection is
                  intersect1[0] = points[insidePoints[0]][0] + t1*(points[insidePoints[2]][0]-points[insidePoints[0]][0])-0.0001f;
                  intersect1[1] = points[insidePoints[0]][1] + t1*(points[insidePoints[2]][1]-points[insidePoints[0]][1])-0.0001f;
                  intersect1[2] = points[insidePoints[0]][2] + t1*(points[insidePoints[2]][2]-points[insidePoints[0]][2])-0.0001f;
                  
                  intersect2[0] = points[insidePoints[1]][0] + t2*(points[insidePoints[2]][0]-points[insidePoints[1]][0])-0.0001f;
                  intersect2[1] = points[insidePoints[1]][1] + t2*(points[insidePoints[2]][1]-points[insidePoints[1]][1])-0.0001f;
                  intersect2[2] = points[insidePoints[1]][2] + t2*(points[insidePoints[2]][2]-points[insidePoints[1]][2])-0.0001f;
                  
                  float[][] secondPoints = new float[3][3];
                  //Constructing a triangle B'C'C
                  //C
                  secondPoints[insidePoints[1]][0] = points[insidePoints[1]][0];
                  secondPoints[insidePoints[1]][1] = points[insidePoints[1]][1];
                  secondPoints[insidePoints[1]][2] = points[insidePoints[1]][2];
                  
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
                  //Adding the new triangle to the list to account for the clipped triangle being a quad
                  edgeDir = returnEdgeDir(modelList, secondPoints, colour, i, backIndex, faceDirection, (flags & 8) == 8, (flags & 4) == 4);
                  if(edgeDir > 0){
                    //Adds increments either the translucent triangle counter or the opaque triangle counter
                    if((colour[1] >>> 24) < 0xFF && (colour[1] >>> 24) > 0)
                      translusentCount++;
                    else
                      opaqueCount++;
                      
                    displayList[numberOfTris] = new Triangle(secondPoints, colour[0], colour[1], modelList[i].returnHasStroke() || (flags & 12) != 8, modelList[i].returnHasFill() && (flags & 8) == 8);
                    displayList[numberOfTris].setDepthWrite(!modelList[i].returnNoDepth());
                    numberOfTris++;
                  }
                  break;
              }
              
              //Adding the triangle to the list
              edgeDir = returnEdgeDir(modelList, points, colour, i, backIndex, faceDirection, (flags & 8) == 8, (flags & 4) == 4);
              if(edgeDir > 0){
                //Adds increments either the translucent triangle counter or the opaque triangle counter
                if((colour[1] >>> 24) < 0xFF && (colour[1] >>> 24) > 0)
                  translusentCount++;
                else
                  opaqueCount++;
                
                displayList[numberOfTris] = new Triangle(points, colour[0], colour[1], modelList[i].returnHasStroke() || (flags & 12) != 8, modelList[i].returnHasFill() && (flags & 8) == 8);
                displayList[numberOfTris].setDepthWrite(!modelList[i].returnNoDepth());
                numberOfTris++;
              }
            }
          }
        }
      }

      int billBoardCountOpaque = 0;
      int billBoardCountTranslucent = 0;
      for(int i = 0; i < billboardList.length && (billBoardCountOpaque+billBoardCountTranslucent) < billboardListSize; i++){
        //Sets up the model matrix and the MVP matrices
        if(!billboardList[i].isAttachedToCamera())
          MVP.setModelPos(billboardList[i].returnPosition());
        else{
          Matrix rotation = MatrixOperations.matrixMultiply(MVP.returnRotation(eye.returnRotation()[0],0,0), MatrixOperations.matrixMultiply(MVP.returnRotation(0,eye.returnRotation()[1],0), MVP.returnRotation(0,0,eye.returnRotation()[2])));
          Matrix offsetVals = MatrixOperations.matrixMultiply(MatrixOperations.matrixMultiply(rotation, MVP.returnTranslation(eye.returnPosition())), VectorOperations.from3DVecTo4DVec(billboardList[i].returnPosition()));
          float[] offset = {offsetVals.returnData(0, 0), offsetVals.returnData(1, 0), offsetVals.returnData(2, 0)};
          MVP.setModelPos(offset);
        }
        //Constructs the transformation matrix for the point
        mvpFull.copy(MatrixOperations.matrixMultiply(mvp, MVP.returnTranslation(MVP.returnModelPos((byte)0), MVP.returnModelPos((byte)1), MVP.returnModelPos((byte)2))));
        mv.copy(MatrixOperations.matrixMultiply(MVP.viewMatrix(), MVP.returnTranslation(MVP.returnModelPos((byte)0), MVP.returnModelPos((byte)1), MVP.returnModelPos((byte)2))));
        mvpFull.copy(MatrixOperations.matrixMultiply(mvpFull, billBoard));
        mv.copy(MatrixOperations.matrixMultiply(mv, billBoard));
        //Multiplying the transformed matrices by the scale of the model
        mvpFull.copy(MatrixOperations.matrixMultiply(mvpFull, MVP.returnScale(billboardList[i].returnScale()[0], billboardList[i].returnScale()[1], 1)));
        mv.copy(MatrixOperations.matrixMultiply(mv, MVP.modelMatrix()));
        float[][] points = {{-(billboardList[i].returnWidth() >>> 1), -(billboardList[i].returnHeight() >>> 1), 0, 1}, 
                            {(billboardList[i].returnWidth() >>> 1), -(billboardList[i].returnHeight() >>> 1), 0, 1},
                            {(billboardList[i].returnWidth() >>> 1), (billboardList[i].returnHeight() >>> 1), 0, 1}, 
                            {-(billboardList[i].returnWidth() >>> 1), (billboardList[i].returnHeight() >>> 1), 0, 1}};
        boolean isInside = false;
        for(byte j = 0; j < 4; j++){
          //Projects the point from 3D to 2D
          Matrix clipCheck = MatrixOperations.matrixMultiply(mv, points[j]); 
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
          points[j][0] = (0.5f*TriangleRasterizer.returnWidth())*(points[j][0]+1)-0.0001f;
          points[j][1] = (0.5f*TriangleRasterizer.returnHeight())*(points[j][1]+1)-0.0001f;
          isInside|=(points[j][3] > 0 && clipCheck.returnData(2, 0) >= zNear && clipCheck.returnData(2,0) <= zFar);
        }

        //Calculates the minimum x and y coordinates and the maximum x and y coordinates
        float[] minPoints = {Math.min(points[0][0], Math.min(points[1][0], Math.min(points[2][0], points[3][0]))),
                             Math.min(points[0][1], Math.min(points[1][1], Math.min(points[2][1], points[3][1])))};
        float[] maxPoints = {Math.max(points[0][0], Math.max(points[1][0], Math.max(points[2][0], points[3][0]))),
                             Math.max(points[0][1], Math.max(points[1][1], Math.max(points[2][1], points[3][1])))};
        //Checks if any part of the image is on the screen
        isInside&=((minPoints[0] < TriangleRasterizer.returnWidth() && minPoints[1] < TriangleRasterizer.returnHeight()) && (maxPoints[0] >= 0 && maxPoints[1] >= 0));
        if(isInside){
          float sizeX = points[2][0]-points[0][0];
          float sizeY = points[2][1]-points[0][1];
          if(billboardList[i].returnTint() == -1){
            billboardDisplayOpaque[billBoardCountOpaque] = new Billboard();
            billboardDisplayOpaque[billBoardCountOpaque].copy(billboardList[i]);
            billboardDisplayOpaque[billBoardCountOpaque].setScale(sizeX, sizeY);
            billboardDisplayOpaque[billBoardCountOpaque].setPosition(points[0][0], points[0][1], points[0][2]);
            billboardDisplayOpaque[billBoardCountOpaque].setOutline(billboardList[i].hasOutline() || (flags & 12) != 8);
            billboardDisplayOpaque[billBoardCountOpaque].setInside(billboardList[i].hasImage() && (flags & 8) == 8);
            billBoardCountOpaque++;
          }
          else{
            billboardDisplayTranslucent[billBoardCountTranslucent] = new Billboard();
            billboardDisplayTranslucent[billBoardCountTranslucent].copy(billboardList[i]);
            billboardDisplayTranslucent[billBoardCountTranslucent].setScale(sizeX, sizeY);
            billboardDisplayTranslucent[billBoardCountTranslucent].setPosition(points[0][0], points[0][1], points[0][2]);
            billboardDisplayTranslucent[billBoardCountTranslucent].setOutline(billboardList[i].hasOutline() || (flags & 12) != 8);
            billboardDisplayTranslucent[billBoardCountTranslucent].setInside(billboardList[i].hasImage() && (flags & 8) == 8);
            billBoardCountTranslucent++;
          }
        }
      }
      //Triangle sorting based on average depth
      //For when there is a mix of translucent and opaque triangles
      if(opaqueCount > 0 && translusentCount > 0){
        Triangle[] sortListOpaque = new Triangle[opaqueCount]; //List of opaque triangles
        int oIndex = 0; //Index for the above list
        Triangle[] sortListTranslusent = new Triangle[translusentCount]; //List of translucent triangles
        int tIndex = 0; //Index for the above list
        //Copying the display list's pointers to the list of opaque and translucent triangles based on the current triangle's alpha channel
        for(int i = 0; i < numberOfTris; i++){
          if((displayList[i].getFill() >>> 24) < 0xFF && (displayList[i].getFill() >>> 24) > 0){
            sortListTranslusent[tIndex] = displayList[i];
            tIndex++;
          }
          else{
            sortListOpaque[oIndex] = displayList[i];
            oIndex++;
          }
        }

        //Merge sort of the two lists based on average distance
        mergeSort(sortListOpaque, 0, sortListOpaque.length-1);  
        mergeSort(sortListTranslusent, 0, sortListTranslusent.length-1);
        
        //Copying the the opaque list's pointers back into the display list, then the translucent list's pointers
        for(int i = 0; i < opaqueCount; i++)
          displayList[i] = sortListOpaque[i];
        for(int i = 0; i < translusentCount; i++)
          displayList[i+opaqueCount] = sortListTranslusent[i];
      }
      //For when there are only translucent triangles
      else if(opaqueCount <= 0){
        //If the display list is full
        if(numberOfTris >= triListSize)
          mergeSort(displayList, 0, triListSize-1);
        //If the display list is partially empty
        else{
          //Creates a new, smaller list
          Triangle[] sortListTranslusent = new Triangle[numberOfTris];
          //Copies the display list's pointers into the smaller list
          for(int i = 0; i < numberOfTris; i++)
            sortListTranslusent[i] = displayList[i];
          //Merge sort based on average distance
          mergeSort(sortListTranslusent, 0, numberOfTris-1);
          //Copying the smaller list's pointers back into the display list
          for(int i = 0; i < numberOfTris; i++)
            displayList[i] = sortListTranslusent[i];
        }
      }
      //For when there are only opaque triangles
      else if(translusentCount <= 0){
        //If the display list is full
        if(numberOfTris >= triListSize)
          mergeSort(displayList, 0, triListSize-1);
        //If the display list is partially empty
        else{
          //Creates a new, smaller list
          Triangle[] sortListOpaque = new Triangle[numberOfTris];
          //Copies the display list's pointers into the smaller list
          for(int i = 0; i < numberOfTris; i++)
          sortListOpaque[i] = displayList[i];
          //Merge sort based on average distance
          mergeSort(sortListOpaque, 0, numberOfTris-1);
          //Copying the smaller list's pointers back into the display list
          for(int i = 0; i < numberOfTris; i++)
            displayList[i] = sortListOpaque[i];
        }
      }
      mergeSort(billboardDisplayTranslucent, 0, billBoardCountTranslucent-1);
      for(int i = 0; i < billBoardCountOpaque; i++)
        TriangleRasterizer.billBoardDraw(screen, billboardDisplayOpaque[i], billboardDisplayOpaque[i].returnPosition()[0], billboardDisplayOpaque[i].returnPosition()[1], billboardDisplayOpaque[i].returnPosition()[2], billboardDisplayOpaque[i].returnScale()[0], billboardDisplayOpaque[i].returnScale()[1], stencilComp, testType);
      //Iterates over all current triangles and draws them to the screen
      for(int i = 0; i < numberOfTris; i++){
        TriangleRasterizer.setDepthWrite(displayList[i].getHasDepthWrite());
        TriangleRasterizer.draw(screen, displayList[i], stencilComp, testType);
      }
      
      for(int i = 0; i < billBoardCountTranslucent; i++)
        TriangleRasterizer.billBoardDraw(screen, billboardDisplayTranslucent[i], billboardDisplayTranslucent[i].returnPosition()[0], billboardDisplayTranslucent[i].returnPosition()[1], billboardDisplayTranslucent[i].returnPosition()[2], billboardDisplayTranslucent[i].returnScale()[0], billboardDisplayTranslucent[i].returnScale()[1], stencilComp, testType);
    }
    //The version for Blinn-Phong reflection and flat shading (1 normal per polygon)
    //TODO: Create a copy of the above function with support for lighting when everything else is done
    public static void drawScene(int[] screen, float zNear, float zFar, Camera eye, int lightColour, byte lightIntensity){
      //Sets the light colour
      if(lightColour < 0 || lightColour > 0xFF){
        if(lightColour >= 0 && lightColour < 0x1000000)
          lightColour = 0xFF000000|lightColour;
       }
       else
          lightColour = 0xFF000000|(lightColour << 16)|(lightColour << 8)|lightColour;
        TriangleRasterizer.setAntiAlias((flags & 1) == 1);
        //Sets the background
        TriangleRasterizer.initBuffers(screen, lightColour, lightIntensity, (flags & 2) == 0);

        
        //Data for tracking the number of triangles in the list, and how many are opaque and how many are translucent
        int numberOfTris = 0;
        byte faceDirection = 1; //Tracks the direction of each face
        int opaqueCount = 0;
        int translusentCount = 0;
          
        //Sets up some of the MVP stuff
        MVP.setEyeAngles(eye.returnRotation());
        MVP.setEyeShear(eye.returnShear());
        //Computes the inverse of the view matrix
        if((flags & 48) != 0){
          MVP.setEyePos(0, 0, 0);
          MVP.setEyeScale(1, 1, 1);
          billBoard = MatrixOperations.invertMatrix(MVP.viewMatrix());
        }
        MVP.setEyePos(eye.returnPosition());
        MVP.setEyeScale(eye.returnScale());
        //Matrices for projection
        Matrix mvp = MatrixOperations.matrixMultiply(persp, MVP.viewMatrix());
        Matrix mvpFull = new Matrix();
        Matrix mv = new Matrix(); //For checking if a model is within zNear and zFar (using the frustum does not work for some reason)
        boolean isInClipSpace = false; //Checks if a model is in the frustum

        listCreator:
        for(int i = 0; i < modelList.length; i++){
          //Sets up the matrices for the current model
          faceDirection = 1;
          if(!modelList[i].returnAttachedToCamera())
            MVP.setModelPos(modelList[i].returnPosition()[0], modelList[i].returnPosition()[1], modelList[i].returnPosition()[2]);
          else{
            Matrix rotation = MatrixOperations.matrixMultiply(MVP.returnRotation(eye.returnRotation()[0],0,0), MatrixOperations.matrixMultiply(MVP.returnRotation(0,eye.returnRotation()[1],0), MVP.returnRotation(0,0,eye.returnRotation()[2])));
            Matrix offsetVals = MatrixOperations.matrixMultiply(MatrixOperations.matrixMultiply(rotation, MVP.returnTranslation(eye.returnPosition())), VectorOperations.from3DVecTo4DVec(modelList[i].returnPosition()));
            float[] offset = {offsetVals.returnData(0, 0), offsetVals.returnData(1, 0), offsetVals.returnData(2, 0)};
            MVP.setModelPos(offset);
          }
          MVP.setModelAngles(modelList[i].returnAngle()[0], modelList[i].returnAngle()[1], modelList[i].returnAngle()[2]);
          MVP.setModelShear(modelList[i].returnShear());
          //For if the model is not a billboard
          if(!modelList[i].returnIsBillBoard()){
            MVP.setModelScale(modelList[i].returnScale()[0], modelList[i].returnScale()[1], modelList[i].returnScale()[2]);
            mvpFull.copy(MatrixOperations.matrixMultiply(mvp, MVP.modelMatrix()));
            mv.copy(MatrixOperations.matrixMultiply(MVP.viewMatrix(), MVP.modelMatrix()));
          }
          //For if the model is a billboard
          else{
            //Constructs the transformation matrix for the point
            mvpFull.copy(MatrixOperations.matrixMultiply(mvp, MVP.returnTranslation(MVP.returnModelPos((byte)0), MVP.returnModelPos((byte)1), MVP.returnModelPos((byte)2))));
            mvpFull.copy(MatrixOperations.matrixMultiply(mvpFull, billBoard));
            mv.copy(MatrixOperations.matrixMultiply(MVP.viewMatrix(), MVP.returnTranslation(MVP.returnModelPos((byte)0), MVP.returnModelPos((byte)1), MVP.returnModelPos((byte)2))));
            mv.copy(MatrixOperations.matrixMultiply(mv, billBoard));
            
            //Enables rotation about the z-axis and scaling along the x and y axis
            Matrix modelMatrix = MatrixOperations.matrixMultiply(MVP.returnRotation(0, 0, modelList[i].returnAngle()[2]), MVP.returnScale(modelList[i].returnScale()[0], modelList[i].returnScale()[1], 1));
            mvpFull.copy(MatrixOperations.matrixMultiply(mvpFull, modelMatrix));
            mv.copy(MatrixOperations.matrixMultiply(mv, modelMatrix));
          }
              
          //Checking if the model is in clipspace and adjusting the face direction to account for negative scales
          isInClipSpace = isInClipSpace(mvpFull, modelList[i].returnBoundingBox()) && isInClipSpace(mv, modelList[i].returnBoundingBox(), zNear, zFar);
          for(byte j = 0; j < 3; j++){
            if(modelList[i].returnScale()[j] < 0)
              faceDirection = (byte)((~faceDirection)+1);
            if(eye.returnScale()[j] < 0)
              faceDirection = (byte)((~faceDirection)+1);
          }

      
          if(isInClipSpace){
            for(int j = 0; j < modelList[i].returnPolygonCount(); j++){
              //Breaks out of the loops if the triangle count is greater than or equal to triListSize
              if(numberOfTris >= triListSize)
                break listCreator;
              float edgeDir = 0; //The direction of the triangle
              float[][] points = new float[3][4]; //Triangle vertices
              boolean isInside = false; //If the triangle is inside the overal scene
              boolean inFrustum = false; //If the triangle's point is in front of the near plane
              byte numOfInside = 0; //Number of points in the near plane
              byte[] insidePoints = {-1, -1, -1}; //Tracking the indices of the points in the near plane
              for(byte s = 0; s < 3; s++){
                //Takes the current point and turns it into a homeogenous vector
                float[] homogeneousPoint = VectorOperations.from3DVecTo4DVec(modelList[i].returnPoints()[modelList[i].returnPolygons()[j][s]]);
                //Projects the point from 3D to 2D
                Matrix clipCheck = MatrixOperations.matrixMultiply(mv, homogeneousPoint); 
                Matrix projection = MatrixOperations.matrixMultiply(mvpFull, homogeneousPoint);
                
                //Copying the results of the projection to a set of points that are less tedious to work with
                points[s][0] = (projection.returnData(0, 0)-0.0001f); //x
                points[s][1] = (projection.returnData(1, 0)-0.0001f); //y
                points[s][2] = (projection.returnData(2, 0)-0.0001f); //z
                points[s][3] = (projection.returnData(3, 0)-0.0001f); //w

                inFrustum = (points[s][2] >= -points[s][3] && points[s][2] <= points[s][3]);
                //Homogeneous division
                if(Math.abs(points[s][3]) > 0.0001){
                  points[s][0] = (points[s][0]/points[s][3]-0.0001f);
                  points[s][1] = (points[s][1]/points[s][3]-0.0001f);
                  points[s][2] = (points[s][2]/points[s][3]-0.0001f);
                }

                //Adjusts point positions to place the screen origin at the centre of the canvas, with scaling with respect to the screen dimensions
                points[s][0] = (0.5f*TriangleRasterizer.returnWidth()*(points[s][0]+1)-0.0001f);
                points[s][1] = (0.5f*TriangleRasterizer.returnHeight()*(points[s][1]+1)-0.0001f);
                
                //Tracks which points are in front of the near plane or behind the near plane
                if(inFrustum){
                  insidePoints[numOfInside] = s;
                  numOfInside++;
                }
                else
                  insidePoints[2] = s;
                isInside|=(clipCheck.returnData(2,0) <= zFar && inFrustum);
              }
              
              //Where the triangle is on screen
              int[] xBounds = {(int)Math.min(points[0][0], Math.min(points[1][0], points[2][0])), (int)Math.max(points[0][0], Math.max(points[1][0], points[2][0]))};
              int[] yBounds = {(int)Math.min(points[0][1], Math.min(points[1][1], points[2][1])), (int)Math.max(points[0][1], Math.max(points[1][1], points[2][1]))};
              isInside&=(xBounds[1] >= 0 && xBounds[0] < TriangleRasterizer.returnWidth() && yBounds[1] >= 0 && yBounds[0] < TriangleRasterizer.returnHeight());
              //Copies the triangle from the model to the triangle array
              if(isInside){
                //Returns if the current triangle is exempt from backface culling
                int backIndex = modelList[i].returnBackTri(j);
                //Holds the colours that get sent to the triangle rasterizer
                int[] colour = {((TriangleRasterizer.interpolatePixels((lightColour & 0xFFFFFF) | (lightIntensity << 24), modelList[i].returnColours()[j][0]) & 0xFFFFFF) | (modelList[i].returnColours()[j][0] & 0xFF000000)), 
                                ((TriangleRasterizer.interpolatePixels((lightColour & 0xFFFFFF) | (lightIntensity << 24), modelList[i].returnColours()[j][1]) & 0xFFFFFF) | (modelList[i].returnColours()[j][1] & 0xFF000000))}; 
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
                      t1 = (1f - points[insidePoints[0]][2]);
                      if(Math.abs(points[otherPoint1][2] - points[insidePoints[0]][2]) > 0.0001) 
                        t1/=(points[otherPoint1][2]-points[insidePoints[0]][2]);
                      t2 = (1f - points[insidePoints[0]][2]);
                      if(Math.abs(points[otherPoint2][2] - points[insidePoints[0]][2]) > 0.0001) 
                        t2/=(points[otherPoint2][2]-points[insidePoints[0]][2]);
                      
                      //Computing the location of the point of intersection for each intersecting side
                      intersect1[0] = points[insidePoints[0]][0] + t1*(points[otherPoint1][0]-points[insidePoints[0]][0])-0.0001f;
                      intersect1[1] = points[insidePoints[0]][1] + t1*(points[otherPoint1][1]-points[insidePoints[0]][1])-0.0001f;
                      intersect1[2] = points[insidePoints[0]][2] + t1*(points[otherPoint1][2]-points[insidePoints[0]][2])-0.0001f;
                      
                      intersect2[0] = points[insidePoints[0]][0] + t2*(points[otherPoint2][0]-points[insidePoints[0]][0])-0.0001f;
                      intersect2[1] = points[insidePoints[0]][1] + t2*(points[otherPoint2][1]-points[insidePoints[0]][1])-0.0001f;
                      intersect2[2] = points[insidePoints[0]][2] + t2*(points[otherPoint2][2]-points[insidePoints[0]][2])-0.0001f;
                      
                      //Moving the points that are behind the near plane to be at the points of intersection
                      points[otherPoint1][0] = intersect1[0];
                      points[otherPoint1][1] = intersect1[1];
                      points[otherPoint1][2] = intersect1[2];
                      
                      points[otherPoint2][0] = intersect2[0];
                      points[otherPoint2][1] = intersect2[1];
                      points[otherPoint2][2] = intersect2[2];
                      break;
                  //When 2 points are inside the frustum
                  case 2:
                    //Calculating how far up each side the points of intersection are
                    t1 = (1f - points[insidePoints[0]][2]);
                    if(Math.abs(points[insidePoints[0]][2] - points[insidePoints[2]][2]) > 0.0001) 
                      t1/=(points[insidePoints[2]][2]-points[insidePoints[0]][2]);
                    t2 = (1f - points[insidePoints[1]][2]);
                    if(Math.abs(points[insidePoints[1]][2] - points[insidePoints[2]][2]) > 0.0001) 
                      t2/=(points[insidePoints[2]][2]-points[insidePoints[1]][2]);
                    
                    //Calculating where each point of intersection is
                    intersect1[0] = points[insidePoints[0]][0] + t1*(points[insidePoints[2]][0]-points[insidePoints[0]][0])-0.0001f;
                    intersect1[1] = points[insidePoints[0]][1] + t1*(points[insidePoints[2]][1]-points[insidePoints[0]][1])-0.0001f;
                    intersect1[2] = points[insidePoints[0]][2] + t1*(points[insidePoints[2]][2]-points[insidePoints[0]][2])-0.0001f;
                    
                    intersect2[0] = points[insidePoints[1]][0] + t2*(points[insidePoints[2]][0]-points[insidePoints[1]][0])-0.0001f;
                    intersect2[1] = points[insidePoints[1]][1] + t2*(points[insidePoints[2]][1]-points[insidePoints[1]][1])-0.0001f;
                    intersect2[2] = points[insidePoints[1]][2] + t2*(points[insidePoints[2]][2]-points[insidePoints[1]][2])-0.0001f;
                    
                    float[][] secondPoints = new float[3][3];
                    //Constructing a triangle B'C'C
                    //C
                    secondPoints[insidePoints[1]][0] = points[insidePoints[1]][0];
                    secondPoints[insidePoints[1]][1] = points[insidePoints[1]][1];
                    secondPoints[insidePoints[1]][2] = points[insidePoints[1]][2];
                    
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
                    //Adding the new triangle to the list to account for the clipped triangle being a quad
                    edgeDir = returnEdgeDir(modelList, secondPoints, colour, i, backIndex, faceDirection, (flags & 8) == 8, (flags & 4) == 4, lightColour, lightIntensity);
                    if(edgeDir > 0){
                      //Adds increments either the translucent triangle counter or the opaque triangle counter
                      if((colour[1] >>> 24) < 0xFF && (colour[1] >>> 24) > 0)
                        translusentCount++;
                      else
                        opaqueCount++;
                        
                      displayList[numberOfTris] = new Triangle(secondPoints, colour[0], colour[1], modelList[i].returnHasStroke() || (flags & 12) != 8, modelList[i].returnHasFill() && (flags & 8) == 8);
                      displayList[numberOfTris].setDepthWrite(!modelList[i].returnNoDepth());
                      numberOfTris++;
                    }
                    break;
                }
                
                //Adding the triangle to the list
                edgeDir = returnEdgeDir(modelList, points, colour, i, backIndex, faceDirection, (flags & 8) == 8, (flags & 4) == 4, lightColour, lightIntensity);
                if(edgeDir > 0){
                  //Adds increments either the translucent triangle counter or the opaque triangle counter
                  if((colour[1] >>> 24) < 0xFF && (colour[1] >>> 24) > 0)
                    translusentCount++;
                  else
                  opaqueCount++;
                  
                  displayList[numberOfTris] = new Triangle(points, colour[0], colour[1], modelList[i].returnHasStroke() || (flags & 12) != 8, modelList[i].returnHasFill() && (flags & 8) == 8);
                  displayList[numberOfTris].setDepthWrite(!modelList[i].returnNoDepth());
                  numberOfTris++;
                }
              }
            }
          }
        }

        int billBoardCountOpaque = 0;
        int billBoardCountTranslucent = 0;
        for(int i = 0; i < billboardList.length && (billBoardCountOpaque+billBoardCountTranslucent) < billboardListSize; i++){
          //Sets up the model matrix and the MVP matrices
          if(!billboardList[i].isAttachedToCamera())
            MVP.setModelPos(billboardList[i].returnPosition());
          else{
            Matrix rotation = MatrixOperations.matrixMultiply(MVP.returnRotation(eye.returnRotation()[0],0,0), MatrixOperations.matrixMultiply(MVP.returnRotation(0,eye.returnRotation()[1],0), MVP.returnRotation(0,0,eye.returnRotation()[2])));
            Matrix offsetVals = MatrixOperations.matrixMultiply(MatrixOperations.matrixMultiply(rotation, MVP.returnTranslation(eye.returnPosition())), VectorOperations.from3DVecTo4DVec(billboardList[i].returnPosition()));
            float[] offset = {offsetVals.returnData(0, 0), offsetVals.returnData(1, 0), offsetVals.returnData(2, 0)};
            MVP.setModelPos(offset);
          }
          //Constructs the transformation matrix for the point
          mvpFull.copy(MatrixOperations.matrixMultiply(mvp, MVP.returnTranslation(MVP.returnModelPos((byte)0), MVP.returnModelPos((byte)1), MVP.returnModelPos((byte)2))));
          mv.copy(MatrixOperations.matrixMultiply(MVP.viewMatrix(), MVP.returnTranslation(MVP.returnModelPos((byte)0), MVP.returnModelPos((byte)1), MVP.returnModelPos((byte)2))));
          mvpFull.copy(MatrixOperations.matrixMultiply(mvpFull, billBoard));
          mv.copy(MatrixOperations.matrixMultiply(mv, billBoard));
          //Multiplying the transformed matrices by the scale of the model
          mvpFull.copy(MatrixOperations.matrixMultiply(mvpFull, MVP.returnScale(billboardList[i].returnScale()[0], billboardList[i].returnScale()[1], 1)));
          mv.copy(MatrixOperations.matrixMultiply(mv, MVP.modelMatrix()));
          float[][] points = {{-(billboardList[i].returnWidth() >>> 1), -(billboardList[i].returnHeight() >>> 1), 0, 1}, 
                              {(billboardList[i].returnWidth() >>> 1), -(billboardList[i].returnHeight() >>> 1), 0, 1},
                              {(billboardList[i].returnWidth() >>> 1), (billboardList[i].returnHeight() >>> 1), 0, 1}, 
                              {-(billboardList[i].returnWidth() >>> 1), (billboardList[i].returnHeight() >>> 1), 0, 1}};
          boolean isInside = false;
          for(byte j = 0; j < 4; j++){
            //Projects the point from 3D to 2D
            Matrix clipCheck = MatrixOperations.matrixMultiply(mv, points[j]); 
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
            points[j][0] = (0.5f*TriangleRasterizer.returnWidth())*(points[j][0]+1)-0.0001f;
            points[j][1] = (0.5f*TriangleRasterizer.returnHeight())*(points[j][1]+1)-0.0001f;
            isInside|=(points[j][3] > 0 && clipCheck.returnData(2, 0) >= zNear && clipCheck.returnData(2,0) <= zFar);
          }
  
          //Calculates the minimum x and y coordinates and the maximum x and y coordinates
          float[] minPoints = {Math.min(points[0][0], Math.min(points[1][0], Math.min(points[2][0], points[3][0]))),
                               Math.min(points[0][1], Math.min(points[1][1], Math.min(points[2][1], points[3][1])))};
          float[] maxPoints = {Math.max(points[0][0], Math.max(points[1][0], Math.max(points[2][0], points[3][0]))),
                               Math.max(points[0][1], Math.max(points[1][1], Math.max(points[2][1], points[3][1])))};
          //Checks if any part of the image is on the screen
          isInside&=((minPoints[0] < TriangleRasterizer.returnWidth() && minPoints[1] < TriangleRasterizer.returnHeight()) && (maxPoints[0] >= 0 && maxPoints[1] >= 0));
          if(isInside){
            float sizeX = points[2][0]-points[0][0];
            float sizeY = points[2][1]-points[0][1];
            if(billboardList[i].returnTint() == -1){
              billboardDisplayOpaque[billBoardCountOpaque] = new Billboard();
              billboardDisplayOpaque[billBoardCountOpaque].copy(billboardList[i]);
              billboardDisplayOpaque[billBoardCountOpaque].setScale(sizeX, sizeY);
              billboardDisplayOpaque[billBoardCountOpaque].setPosition(points[0][0], points[0][1], points[0][2]);
              billboardDisplayOpaque[billBoardCountOpaque].setOutline(billboardList[i].hasOutline() || (flags & 12) != 8);
              billboardDisplayOpaque[billBoardCountOpaque].setInside(billboardList[i].hasImage() && (flags & 8) == 8);
              billBoardCountOpaque++;
            }
            else{
              billboardDisplayTranslucent[billBoardCountTranslucent] = new Billboard();
              billboardDisplayTranslucent[billBoardCountTranslucent].copy(billboardList[i]);
              billboardDisplayTranslucent[billBoardCountTranslucent].setScale(sizeX, sizeY);
              billboardDisplayTranslucent[billBoardCountTranslucent].setPosition(points[0][0], points[0][1], points[0][2]);
              billboardDisplayTranslucent[billBoardCountTranslucent].setOutline(billboardList[i].hasOutline() || (flags & 12) != 8);
              billboardDisplayTranslucent[billBoardCountTranslucent].setInside(billboardList[i].hasImage() && (flags & 8) == 8);
              billBoardCountTranslucent++;
            }
          }
        }
        //Triangle sorting based on average depth
        //For when there is a mix of translucent and opaque triangles
        if(opaqueCount > 0 && translusentCount > 0){
          Triangle[] sortListOpaque = new Triangle[opaqueCount]; //List of opaque triangles
          int oIndex = 0; //Index for the above list
          Triangle[] sortListTranslusent = new Triangle[translusentCount]; //List of translucent triangles
          int tIndex = 0; //Index for the above list
          //Copying the display list's pointers to the list of opaque and translucent triangles based on the current triangle's alpha channel
          for(int i = 0; i < numberOfTris; i++){
            if((displayList[i].getFill() >>> 24) < 0xFF && (displayList[i].getFill() >>> 24) > 0){
              sortListTranslusent[tIndex] = displayList[i];
              tIndex++;
            }
            else{
              sortListOpaque[oIndex] = displayList[i];
              oIndex++;
            }
          }

          //Merge sort of the two lists based on average distance
          mergeSort(sortListOpaque, 0, sortListOpaque.length-1);  
          mergeSort(sortListTranslusent, 0, sortListTranslusent.length-1);
          
          //Copying the the opaque list's pointers back into the display list, then the translucent list's pointers
          for(int i = 0; i < opaqueCount; i++)
            displayList[i] = sortListOpaque[i];
          for(int i = 0; i < translusentCount; i++)
            displayList[i+opaqueCount] = sortListTranslusent[i];
        }
        //For when there are only translucent triangles
        else if(opaqueCount <= 0){
          //If the display list is full
          if(numberOfTris >= triListSize)
            mergeSort(displayList, 0, triListSize-1);
          //If the display list is partially empty
          else{
            //Creates a new, smaller list
            Triangle[] sortListTranslusent = new Triangle[numberOfTris];
            //Copies the display list's pointers into the smaller list
            for(int i = 0; i < numberOfTris; i++)
              sortListTranslusent[i] = displayList[i];
            //Merge sort based on average distance
            mergeSort(sortListTranslusent, 0, numberOfTris-1);
            //Copying the smaller list's pointers back into the display list
            for(int i = 0; i < numberOfTris; i++)
              displayList[i] = sortListTranslusent[i];
          }
        }
        //For when there are only opaque triangles
        else if(translusentCount <= 0){
          //If the display list is full
          if(numberOfTris >= triListSize)
            mergeSort(displayList, 0, triListSize-1);
          //If the display list is partially empty
          else{
            //Creates a new, smaller list
            Triangle[] sortListOpaque = new Triangle[numberOfTris];
            //Copies the display list's pointers into the smaller list
            for(int i = 0; i < numberOfTris; i++)
              sortListOpaque[i] = displayList[i];
            //Merge sort based on average distance
            mergeSort(sortListOpaque, 0, numberOfTris-1);
            //Copying the smaller list's pointers back into the display list
            for(int i = 0; i < numberOfTris; i++)
              displayList[i] = sortListOpaque[i];
          }
        }
        mergeSort(billboardDisplayTranslucent, 0, billBoardCountTranslucent-1);
        for(int i = 0; i < billBoardCountOpaque; i++)
          TriangleRasterizer.billBoardDraw(screen, billboardDisplayOpaque[i], billboardDisplayOpaque[i].returnPosition()[0], billboardDisplayOpaque[i].returnPosition()[1], billboardDisplayOpaque[i].returnPosition()[2], billboardDisplayOpaque[i].returnScale()[0], billboardDisplayOpaque[i].returnScale()[1], lightColour, lightIntensity, stencilComp, testType);
        //Iterates over all current triangles and draws them to the screen
        for(int i = 0; i < numberOfTris; i++){
          TriangleRasterizer.setDepthWrite(displayList[i].getHasDepthWrite());
          TriangleRasterizer.draw(screen, displayList[i], stencilComp, testType);
        }
        for(int i = 0; i < billBoardCountTranslucent; i++)
          TriangleRasterizer.billBoardDraw(screen, billboardDisplayTranslucent[i], billboardDisplayTranslucent[i].returnPosition()[0], billboardDisplayTranslucent[i].returnPosition()[1], billboardDisplayTranslucent[i].returnPosition()[2], billboardDisplayTranslucent[i].returnScale()[0], billboardDisplayTranslucent[i].returnScale()[1], lightColour, lightIntensity, stencilComp, testType);
    }

    public static void drawScene(int[] screen, float zNear, float zFar, Camera eye, int lightColour, byte lightIntensity, float[] light){
      //Sets the light colour
      if(lightColour < 0 || lightColour > 0xFF){
        if(lightColour >= 0 && lightColour < 0x1000000)
          lightColour = 0xFF000000|lightColour;
       }
       else
          lightColour = 0xFF000000|(lightColour << 16)|(lightColour << 8)|lightColour;
        TriangleRasterizer.setAntiAlias((flags & 1) == 1);
        //Sets the background
        TriangleRasterizer.initBuffers(screen, lightColour, lightIntensity, (flags & 2) == 0);

        
        //Data for tracking which model we are on, how many triangles are currently in the list, and the number of triangles worked on thus far
        int numberOfTris = 0;
        byte faceDirection = 1; //Tracks the direction of each face
        int opaqueCount = 0;
        int translusentCount = 0;
          
        //Sets up some of the MVP stuff
        MVP.setEyeAngles(eye.returnRotation());
        MVP.setEyeShear(eye.returnShear());
        //Computes the inverse of the view matrix
        if((flags & 48) != 0){
          MVP.setEyePos(0, 0, 0);
          MVP.setEyeScale(1, 1, 1);
          billBoard = MatrixOperations.invertMatrix(MVP.viewMatrix());
        }
        MVP.setEyePos(eye.returnPosition());
        MVP.setEyeScale(eye.returnScale());
        //Matrices for projection
        Matrix mvp = MatrixOperations.matrixMultiply(persp, MVP.viewMatrix());
        Matrix lightTransform = MatrixOperations.matrixMultiply(mvp, VectorOperations.from3DVecTo4DVec(light));
        float[] lightPosition = {lightTransform.returnData(0, 0), lightTransform.returnData(1, 0), lightTransform.returnData(2, 0)};
        Matrix mvpFull = new Matrix();
        Matrix mv = new Matrix(); //For checking if a model is within zNear and zFar (using the frustum does not work for some reason)
        
        boolean isInClipSpace = false; //Checks if a model is in the frustum
        listCreator:
        for(int i = 0; i < modelList.length; i++){
          //Selects every model after the first one when i is equal to the sum of every model's triangle count thus far 
          faceDirection = 1;
          if(!modelList[i].returnAttachedToCamera())
            MVP.setModelPos(modelList[i].returnPosition()[0], modelList[i].returnPosition()[1], modelList[i].returnPosition()[2]);
          else{
            Matrix rotation = MatrixOperations.matrixMultiply(MVP.returnRotation(eye.returnRotation()[0],0,0), MatrixOperations.matrixMultiply(MVP.returnRotation(0,eye.returnRotation()[1],0), MVP.returnRotation(0,0,eye.returnRotation()[2])));
            Matrix offsetVals = MatrixOperations.matrixMultiply(MatrixOperations.matrixMultiply(rotation, MVP.returnTranslation(eye.returnPosition())), VectorOperations.from3DVecTo4DVec(modelList[i].returnPosition()));
            float[] offset = {offsetVals.returnData(0, 0), offsetVals.returnData(1, 0), offsetVals.returnData(2, 0)};
            MVP.setModelPos(offset);
          }
          MVP.setModelAngles(modelList[i].returnAngle()[0], modelList[i].returnAngle()[1], modelList[i].returnAngle()[2]);
          MVP.setModelShear(modelList[i].returnShear());
          //For if the model is not a billboard
          if(!modelList[i].returnIsBillBoard()){
            MVP.setModelScale(modelList[i].returnScale()[0], modelList[i].returnScale()[1], modelList[i].returnScale()[2]);
            mvpFull.copy(MatrixOperations.matrixMultiply(mvp, MVP.modelMatrix()));
            mv.copy(MatrixOperations.matrixMultiply(MVP.viewMatrix(), MVP.modelMatrix()));
          }
          //For if the model is a billboard
          else{
            //Constructs the transformation matrix for the point
            mvpFull.copy(MatrixOperations.matrixMultiply(mvp, MVP.returnTranslation(MVP.returnModelPos((byte)0), MVP.returnModelPos((byte)1), MVP.returnModelPos((byte)2))));
            mvpFull.copy(MatrixOperations.matrixMultiply(mvpFull, billBoard));
            mv.copy(MatrixOperations.matrixMultiply(MVP.viewMatrix(), MVP.returnTranslation(MVP.returnModelPos((byte)0), MVP.returnModelPos((byte)1), MVP.returnModelPos((byte)2))));
            mv.copy(MatrixOperations.matrixMultiply(mv, billBoard));
          
            //Enables rotation about the z-axis and scaling along the x and y axis
            Matrix modelMatrix = MatrixOperations.matrixMultiply(MVP.returnRotation(0, 0, modelList[i].returnAngle()[2]), MVP.returnScale(modelList[i].returnScale()[0], modelList[i].returnScale()[1], 1));
            mvpFull.copy(MatrixOperations.matrixMultiply(mvpFull, modelMatrix));
            mv.copy(MatrixOperations.matrixMultiply(mv, modelMatrix));
          }
              
          //Checking if the model is in clipspace and adjusting the face direction to account for negative scales
          isInClipSpace = isInClipSpace(mvpFull, modelList[i].returnBoundingBox()) && isInClipSpace(mv, modelList[i].returnBoundingBox(), zNear, zFar);
          for(byte j = 0; j < 3; j++){
            if(modelList[i].returnScale()[j] < 0)
              faceDirection = (byte)((~faceDirection)+1);
            if(eye.returnScale()[j] < 0)
              faceDirection = (byte)((~faceDirection)+1);
          }
      
          if(isInClipSpace){
            for(int j = 0; j < modelList[i].returnPolygonCount(); j++){
              if(numberOfTris >= triListSize)
                break listCreator;
              float edgeDir = 0; //The direction of the triangle
              float[][] points = new float[3][4]; //Triangle vertices
              boolean isInside = false; //If the triangle is inside the overal scene
              boolean inFrustum = false; //If the triangle's point is in front of the near plane
              byte numOfInside = 0; //Number of points in the near plane
              byte[] insidePoints = {-1, -1, -1}; //Tracking the indices of the points in the near plane
              //float[][] sides = {{0, 0, 0}, {0, 0, 0}};
              byte clip = 0;
              for(byte s = 0; s < 3; s++){
                //Takes the current point and turns it into a homeogenous vector
                float[] homogeneousPoint = VectorOperations.from3DVecTo4DVec(modelList[i].returnPoints()[modelList[i].returnPolygons()[j][s]]);
                //Projects the point from 3D to 2D
                Matrix clipCheck = MatrixOperations.matrixMultiply(mv, homogeneousPoint); 
                Matrix projection = MatrixOperations.matrixMultiply(mvpFull, homogeneousPoint);
                
                //Copying the results of the projection to a set of points that are less tedious to work with
                points[s][0] = (projection.returnData(0, 0)-0.0001f); //x
                points[s][1] = (projection.returnData(1, 0)-0.0001f); //y
                points[s][2] = (projection.returnData(2, 0)-0.0001f); //z
                points[s][3] = (projection.returnData(3, 0)-0.0001f); //w
                //Homogeneous division
                if(Math.abs(points[s][3]) > 0){
                  points[s][0] = (points[s][0]/points[s][3]-0.0001f);
                  points[s][1] = (points[s][1]/points[s][3]-0.0001f);
                  points[s][2] = (points[s][2]/points[s][3]-0.0001f);
                }
                clip = ((clipCheck.returnData(2, 0) <= zFar) ? (byte)(clip|(1 << s)) : (byte)0);
              }
              float[][] sides = {{points[0][0]-points[1][0],
                                  points[0][1]-points[1][1],
                                  points[0][2]-points[1][2]},
                                {points[0][0]-points[2][0],
                                  points[0][1]-points[2][1],
                                  points[0][2]-points[2][2]}};
              for(byte s = 0; s < 3; s++){
                inFrustum = (points[s][2] >= -points[s][3] && points[s][2] <= points[s][3]);


                //Adjusts point positions to place the screen origin at the centre of the canvas, with scaling with respect to the screen dimensions
                points[s][0] = (0.5f*TriangleRasterizer.returnWidth()*(points[s][0]+1)-0.0001f);
                points[s][1] = (0.5f*TriangleRasterizer.returnHeight()*(points[s][1]+1)-0.0001f);
                
                //Tracks which points are in front of the near plane or behind the near plane
                if(inFrustum){
                  insidePoints[numOfInside] = s;
                  numOfInside++;
                }
                else
                  insidePoints[2] = s;
                isInside|=((((clip >>> s) & 1) == 1) && inFrustum);
              }
              
              //Where the triangle is on screen
              int[] xBounds = {(int)Math.min(points[0][0], Math.min(points[1][0], points[2][0])), (int)Math.max(points[0][0], Math.max(points[1][0], points[2][0]))};
              int[] yBounds = {(int)Math.min(points[0][1], Math.min(points[1][1], points[2][1])), (int)Math.max(points[0][1], Math.max(points[1][1], points[2][1]))};
              isInside&=(xBounds[1] >= 0 && xBounds[0] < TriangleRasterizer.returnWidth() && yBounds[1] >= 0 && yBounds[0] < TriangleRasterizer.returnHeight());
              //Copies the triangle from the model to the triangle array
              if(isInside){
                //Returns if the current triangle is exempt from backface culling
                int backIndex = modelList[i].returnBackTri(j);
                //Holds the colours that get sent to the triangle rasterizer
                int[] colour = {((TriangleRasterizer.interpolatePixels((lightColour & 0xFFFFFF) | (lightIntensity << 24), modelList[i].returnColours()[j][0]) & 0xFFFFFF) | (modelList[i].returnColours()[j][0] & 0xFF000000)), 
                                ((TriangleRasterizer.interpolatePixels((lightColour & 0xFFFFFF) | (lightIntensity << 24), modelList[i].returnColours()[j][1]) & 0xFFFFFF) | (modelList[i].returnColours()[j][1] & 0xFF000000))}; 
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
                      t1 = (1f - points[insidePoints[0]][2]);
                      if(Math.abs(points[otherPoint1][2] - points[insidePoints[0]][2]) > 0.0001) 
                        t1/=(points[otherPoint1][2]-points[insidePoints[0]][2]);
                      t2 = (1f - points[insidePoints[0]][2]);
                      if(Math.abs(points[otherPoint2][2] - points[insidePoints[0]][2]) > 0.0001) 
                        t2/=(points[otherPoint2][2]-points[insidePoints[0]][2]);
                      
                      //Computing the location of the point of intersection for each intersecting side
                      intersect1[0] = points[insidePoints[0]][0] + t1*(points[otherPoint1][0]-points[insidePoints[0]][0])-0.0001f;
                      intersect1[1] = points[insidePoints[0]][1] + t1*(points[otherPoint1][1]-points[insidePoints[0]][1])-0.0001f;
                      intersect1[2] = points[insidePoints[0]][2] + t1*(points[otherPoint1][2]-points[insidePoints[0]][2])-0.0001f;
                      
                      intersect2[0] = points[insidePoints[0]][0] + t2*(points[otherPoint2][0]-points[insidePoints[0]][0])-0.0001f;
                      intersect2[1] = points[insidePoints[0]][1] + t2*(points[otherPoint2][1]-points[insidePoints[0]][1])-0.0001f;
                      intersect2[2] = points[insidePoints[0]][2] + t2*(points[otherPoint2][2]-points[insidePoints[0]][2])-0.0001f;
                      
                      //Moving the points that are behind the near plane to be at the points of intersection
                      points[otherPoint1][0] = intersect1[0];
                      points[otherPoint1][1] = intersect1[1];
                      points[otherPoint1][2] = intersect1[2];
                      
                      points[otherPoint2][0] = intersect2[0];
                      points[otherPoint2][1] = intersect2[1];
                      points[otherPoint2][2] = intersect2[2];
                      break;
                  //When 2 points are inside the frustum
                  case 2:
                    //Calculating how far up each side the points of intersection are
                    t1 = (1f - points[insidePoints[0]][2]);
                    if(Math.abs(points[insidePoints[0]][2] - points[insidePoints[2]][2]) > 0.0001) 
                      t1/=(points[insidePoints[2]][2]-points[insidePoints[0]][2]);
                    t2 = (1f - points[insidePoints[1]][2]);
                    if(Math.abs(points[insidePoints[1]][2] - points[insidePoints[2]][2]) > 0.0001) 
                      t2/=(points[insidePoints[2]][2]-points[insidePoints[1]][2]);
                    
                    //Calculating where each point of intersection is
                    intersect1[0] = points[insidePoints[0]][0] + t1*(points[insidePoints[2]][0]-points[insidePoints[0]][0])-0.0001f;
                    intersect1[1] = points[insidePoints[0]][1] + t1*(points[insidePoints[2]][1]-points[insidePoints[0]][1])-0.0001f;
                    intersect1[2] = points[insidePoints[0]][2] + t1*(points[insidePoints[2]][2]-points[insidePoints[0]][2])-0.0001f;
                    
                    intersect2[0] = points[insidePoints[1]][0] + t2*(points[insidePoints[2]][0]-points[insidePoints[1]][0])-0.0001f;
                    intersect2[1] = points[insidePoints[1]][1] + t2*(points[insidePoints[2]][1]-points[insidePoints[1]][1])-0.0001f;
                    intersect2[2] = points[insidePoints[1]][2] + t2*(points[insidePoints[2]][2]-points[insidePoints[1]][2])-0.0001f;
                    
                    float[][] secondPoints = new float[3][3];
                    //Constructing a triangle B'C'C
                    //C
                    secondPoints[insidePoints[1]][0] = points[insidePoints[1]][0];
                    secondPoints[insidePoints[1]][1] = points[insidePoints[1]][1];
                    secondPoints[insidePoints[1]][2] = points[insidePoints[1]][2];
                    
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
                    //Adding the new triangle to the list
                                  //Sides used in computing the surface normal
                    // float[][] sides = {{secondPoints[0][0]-secondPoints[1][0], 
                    //                     secondPoints[0][1]-secondPoints[1][1], 
                    //                     secondPoints[0][2]-secondPoints[1][2]}, 
                    //                    {secondPoints[0][0]-secondPoints[2][0], 
                    //                     secondPoints[0][1]-secondPoints[2][1], 
                    //                     secondPoints[0][2]-secondPoints[2][2]}};
                    //Computing the surface normal
                    float[] surfaceNormal = VectorOperations.vectorNormalization(VectorOperations.vectorCross3D(sides[0], sides[1]));
                    //float[] tempVec = {surfaceNormal[0], surfaceNormal[1], surfaceNormal[2]};
                    float[] lightVec = {(secondPoints[0][0]+secondPoints[1][0]+secondPoints[2][0])/3-lightPosition[0],
                                        (secondPoints[0][1]+secondPoints[1][1]+secondPoints[2][1])/3-lightPosition[1],
                                        (secondPoints[0][2]+secondPoints[1][2]+secondPoints[2][2])/3-lightPosition[2]};
                    float[] normalLightVec = VectorOperations.vectorNormalization(lightVec);
                    edgeDir = returnEdgeDir(modelList, secondPoints, colour, i, backIndex, faceDirection, (flags & 8) == 8, (flags & 4) == 4, lightColour, lightIntensity, surfaceNormal);
                    if(edgeDir > 0){
                      //Adds increments either the translucent triangle counter or the opaque triangle counter
                      if((colour[1] >>> 24) < 0xFF && (colour[1] >>> 24) > 0)
                        translusentCount++;
                      else
                        opaqueCount++;
                        
                      displayList[numberOfTris] = new Triangle(secondPoints, colour[0], colour[1], modelList[i].returnHasStroke() || (flags & 12) != 8, modelList[i].returnHasFill() && (flags & 8) == 8);
                      displayList[numberOfTris].setDepthWrite(!modelList[i].returnNoDepth());
                      numberOfTris++;
                    }
                    break;
                }
                //Sides used in computing the surface normal
                //float[][] sides = {{points[0][0]-points[1][0], points[0][1]-points[1][1], points[0][2]-points[1][2]}, {points[0][0]-points[2][0], points[0][1]-points[2][1], points[0][2]-points[2][2]}};

                //Computing the surface normal
                float[] surfaceNormal = VectorOperations.vectorNormalization(VectorOperations.vectorCross3D(sides[0], sides[1]));
                
                //The vector from the light source to the centre of the triangle
                float[] lightVec = {(points[0][0]+points[1][0]+points[2][0])/3-lightPosition[0],
                                    (points[0][1]+points[1][1]+points[2][1])/3-lightPosition[1],
                                    (points[0][2]+points[1][2]+points[2][2])/3-lightPosition[2]};
                //The normalized light vecotr
                float[] normalLightVec = VectorOperations.vectorNormalization(lightVec);
                //System.out.println(index+"\n"+VectorOperations.vectorToString(surfaceNormal)+"\n");
                //Adding the triangle to the list
                edgeDir = returnEdgeDir(modelList, points, colour, i, backIndex, faceDirection, (flags & 8) == 8, (flags & 4) == 4, lightColour, lightIntensity, surfaceNormal);
                if(edgeDir > 0){
                  //Adds increments either the translucent triangle counter or the opaque triangle counter
                  if((colour[1] >>> 24) < 0xFF && (colour[1] >>> 24) > 0)
                    translusentCount++;
                  else
                  opaqueCount++;
                  
                  displayList[numberOfTris] = new Triangle(points, colour[0], colour[1], modelList[i].returnHasStroke() || (flags & 12) != 8, modelList[i].returnHasFill() && (flags & 8) == 8);
                  displayList[numberOfTris].setDepthWrite(!modelList[i].returnNoDepth());
                  numberOfTris++;
                }
              }
            }
          }
        }

        int billBoardCountOpaque = 0;
        int billBoardCountTranslucent = 0;
        for(int i = 0; i < billboardList.length && (billBoardCountOpaque+billBoardCountTranslucent) < billboardListSize; i++){
          //Sets up the model matrix and the MVP matrices
          if(!billboardList[i].isAttachedToCamera())
            MVP.setModelPos(billboardList[i].returnPosition());
          else{
            Matrix rotation = MatrixOperations.matrixMultiply(MVP.returnRotation(eye.returnRotation()[0],0,0), MatrixOperations.matrixMultiply(MVP.returnRotation(0,eye.returnRotation()[1],0), MVP.returnRotation(0,0,eye.returnRotation()[2])));
            Matrix offsetVals = MatrixOperations.matrixMultiply(MatrixOperations.matrixMultiply(rotation, MVP.returnTranslation(eye.returnPosition())), VectorOperations.from3DVecTo4DVec(billboardList[i].returnPosition()));
            float[] offset = {offsetVals.returnData(0, 0), offsetVals.returnData(1, 0), offsetVals.returnData(2, 0)};
            MVP.setModelPos(offset);
          }
          //Constructs the transformation matrix for the point
          mvpFull.copy(MatrixOperations.matrixMultiply(mvp, MVP.returnTranslation(MVP.returnModelPos((byte)0), MVP.returnModelPos((byte)1), MVP.returnModelPos((byte)2))));
          mv.copy(MatrixOperations.matrixMultiply(MVP.viewMatrix(), MVP.returnTranslation(MVP.returnModelPos((byte)0), MVP.returnModelPos((byte)1), MVP.returnModelPos((byte)2))));
          mvpFull.copy(MatrixOperations.matrixMultiply(mvpFull, billBoard));
          mv.copy(MatrixOperations.matrixMultiply(mv, billBoard));
          //Multiplying the transformed matrices by the scale of the model
          mvpFull.copy(MatrixOperations.matrixMultiply(mvpFull, MVP.returnScale(billboardList[i].returnScale()[0], billboardList[i].returnScale()[1], 1)));
          mv.copy(MatrixOperations.matrixMultiply(mv, MVP.modelMatrix()));
          float[][] points = {{-(billboardList[i].returnWidth() >>> 1), -(billboardList[i].returnHeight() >>> 1), 0, 1}, 
                              {(billboardList[i].returnWidth() >>> 1), -(billboardList[i].returnHeight() >>> 1), 0, 1},
                              {(billboardList[i].returnWidth() >>> 1), (billboardList[i].returnHeight() >>> 1), 0, 1}, 
                              {-(billboardList[i].returnWidth() >>> 1), (billboardList[i].returnHeight() >>> 1), 0, 1}};
          boolean isInside = false;
          for(byte j = 0; j < 4; j++){
            //Projects the point from 3D to 2D
            Matrix clipCheck = MatrixOperations.matrixMultiply(mv, points[j]); 
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
            points[j][0] = (0.5f*TriangleRasterizer.returnWidth())*(points[j][0]+1)-0.0001f;
            points[j][1] = (0.5f*TriangleRasterizer.returnHeight())*(points[j][1]+1)-0.0001f;
            isInside|=(points[j][3] > 0 && clipCheck.returnData(2, 0) >= zNear && clipCheck.returnData(2,0) <= zFar);
          }
  
          //Calculates the minimum x and y coordinates and the maximum x and y coordinates
          float[] minPoints = {Math.min(points[0][0], Math.min(points[1][0], Math.min(points[2][0], points[3][0]))),
                               Math.min(points[0][1], Math.min(points[1][1], Math.min(points[2][1], points[3][1])))};
          float[] maxPoints = {Math.max(points[0][0], Math.max(points[1][0], Math.max(points[2][0], points[3][0]))),
                               Math.max(points[0][1], Math.max(points[1][1], Math.max(points[2][1], points[3][1])))};
          //Checks if any part of the image is on the screen
          isInside&=((minPoints[0] < TriangleRasterizer.returnWidth() && minPoints[1] < TriangleRasterizer.returnHeight()) && (maxPoints[0] >= 0 && maxPoints[1] >= 0));
          if(isInside){
            float sizeX = points[2][0]-points[0][0];
            float sizeY = points[2][1]-points[0][1];
            if(billboardList[i].returnTint() == -1){
              billboardDisplayOpaque[billBoardCountOpaque] = new Billboard();
              billboardDisplayOpaque[billBoardCountOpaque].copy(billboardList[i]);
              billboardDisplayOpaque[billBoardCountOpaque].setScale(sizeX, sizeY);
              billboardDisplayOpaque[billBoardCountOpaque].setPosition(points[0][0], points[0][1], points[0][2]);
              billboardDisplayOpaque[billBoardCountOpaque].setOutline(billboardList[i].hasOutline() || (flags & 12) != 8);
              billboardDisplayOpaque[billBoardCountOpaque].setInside(billboardList[i].hasImage() && (flags & 8) == 8);
              billBoardCountOpaque++;
            }
            else{
              billboardDisplayTranslucent[billBoardCountTranslucent] = new Billboard();
              billboardDisplayTranslucent[billBoardCountTranslucent].copy(billboardList[i]);
              billboardDisplayTranslucent[billBoardCountTranslucent].setScale(sizeX, sizeY);
              billboardDisplayTranslucent[billBoardCountTranslucent].setPosition(points[0][0], points[0][1], points[0][2]);
              billboardDisplayTranslucent[billBoardCountTranslucent].setOutline(billboardList[i].hasOutline() || (flags & 12) != 8);
              billboardDisplayTranslucent[billBoardCountTranslucent].setInside(billboardList[i].hasImage() && (flags & 8) == 8);
              billBoardCountTranslucent++;
            }
          }
        }
        
        //Triangle sorting based on average depth
        //For when there is a mix of translucent and opaque triangles
        if(opaqueCount > 0 && translusentCount > 0){
          Triangle[] sortListOpaque = new Triangle[opaqueCount]; //List of opaque triangles
          int oIndex = 0; //Index for the above list
          Triangle[] sortListTranslusent = new Triangle[translusentCount]; //List of translucent triangles
          int tIndex = 0; //Index for the above list
          //Copying the display list's pointers to the list of opaque and translucent triangles based on the current triangle's alpha channel
          for(int i = 0; i < numberOfTris; i++){
            if((displayList[i].getFill() >>> 24) < 0xFF && (displayList[i].getFill() >>> 24) > 0){
              sortListTranslusent[tIndex] = displayList[i];
              tIndex++;
            }
            else{
              sortListOpaque[oIndex] = displayList[i];
              oIndex++;
            }
          }

          //Merge sort of the two lists based on average distance
          mergeSort(sortListOpaque, 0, sortListOpaque.length-1);  
          mergeSort(sortListTranslusent, 0, sortListTranslusent.length-1);
          
          //Copying the the opaque list's pointers back into the display list, then the translucent list's pointers
          for(int i = 0; i < opaqueCount; i++)
            displayList[i] = sortListOpaque[i];
          for(int i = 0; i < translusentCount; i++)
            displayList[i+opaqueCount] = sortListTranslusent[i];
        }
        //For when there are only translucent triangles
        else if(opaqueCount <= 0){
          //If the display list is full
          if(numberOfTris >= triListSize)
            mergeSort(displayList, 0, triListSize-1);
          //If the display list is partially empty
          else{
            //Creates a new, smaller list
            Triangle[] sortListTranslusent = new Triangle[numberOfTris];
            //Copies the display list's pointers into the smaller list
            for(int i = 0; i < numberOfTris; i++)
              sortListTranslusent[i] = displayList[i];
            //Merge sort based on average distance
            mergeSort(sortListTranslusent, 0, numberOfTris-1);
            //Copying the smaller list's pointers back into the display list
            for(int i = 0; i < numberOfTris; i++)
              displayList[i] = sortListTranslusent[i];
          }
        }
        //For when there are only opaque triangles
        else if(translusentCount <= 0){
          //If the display list is full
          if(numberOfTris >= triListSize)
            mergeSort(displayList, 0, triListSize-1);
          //If the display list is partially empty
          else{
            //Creates a new, smaller list
            Triangle[] sortListOpaque = new Triangle[numberOfTris];
            //Copies the display list's pointers into the smaller list
            for(int i = 0; i < numberOfTris; i++)
            sortListOpaque[i] = displayList[i];
            //Merge sort based on average distance
            mergeSort(sortListOpaque, 0, numberOfTris-1);
            //Copying the smaller list's pointers back into the display list
            for(int i = 0; i < numberOfTris; i++)
              displayList[i] = sortListOpaque[i];
          }
        }
        mergeSort(billboardDisplayTranslucent, 0, billBoardCountTranslucent-1);
        for(int i = 0; i < billBoardCountOpaque; i++)
          TriangleRasterizer.billBoardDraw(screen, billboardDisplayOpaque[i], billboardDisplayOpaque[i].returnPosition()[0], billboardDisplayOpaque[i].returnPosition()[1], billboardDisplayOpaque[i].returnPosition()[2], billboardDisplayOpaque[i].returnScale()[0], billboardDisplayOpaque[i].returnScale()[1], lightColour, lightIntensity, stencilComp, testType);
        //Iterates over all current triangles and draws them to the screen
        for(int i = 0; i < numberOfTris; i++){
          TriangleRasterizer.setDepthWrite(displayList[i].getHasDepthWrite());
          TriangleRasterizer.draw(screen, displayList[i], stencilComp, testType);
        }
        for(int i = 0; i < billBoardCountTranslucent; i++)
          TriangleRasterizer.billBoardDraw(screen, billboardDisplayTranslucent[i], billboardDisplayTranslucent[i].returnPosition()[0], billboardDisplayTranslucent[i].returnPosition()[1], billboardDisplayTranslucent[i].returnPosition()[2], billboardDisplayTranslucent[i].returnScale()[0], billboardDisplayTranslucent[i].returnScale()[1], lightColour, lightIntensity, stencilComp, testType);
    }
  
    //Triangle merge sort
    public static void mergeSort(Triangle[] arr, int l, int r){
      if(l < r){
        int mid = l+((r-l) >>> 1);
        mergeSort(arr, l, mid);
        mergeSort(arr, mid+1, r);
        merge(arr, l, r, mid);
      }
    }

    //Billboard merge sort
    public static void mergeSort(Billboard[] arr, int l, int r){
      if(l < r){
        int mid = l+((r-l) >>> 1);
        mergeSort(arr, l, mid);
        mergeSort(arr, mid+1, r);
        merge(arr, l, r, mid);
      }
    }

    //Takes in an array of triangles, splits it into two pieces, and them merges them back together with the elements in ascending order
    private static void merge(Triangle[] arr, int l, int r, int mid){
      //Finding the sizes of the left and right halfs and filling out new lists
      int leftSize = mid-l+1;
      int rightSize = r-mid;
      Triangle[] leftSide = new Triangle[leftSize];
      Triangle[] rightSide = new Triangle[rightSize];
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
        if(leftSide[i].getAverageZ() <= rightSide[j].getAverageZ()){
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
    
      //Takes in an array of billboards, splits it into two pieces, and them merges them back together with the elements in ascending order
      private static void merge(Billboard[] arr, int l, int r, int mid){
        //Finding the sizes of the left and right halfs and filling out new lists
        int leftSize = mid-l+1;
        int rightSize = r-mid;
        Billboard[] leftSide = new Billboard[leftSize];
        Billboard[] rightSide = new Billboard[rightSize];
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
          if(leftSide[i].returnPosition()[2] <= rightSide[j].returnPosition()[2]){
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
    public static boolean isInClipSpace(Matrix mvp, float[][] boundingBox){
      if(boundingBox.length < 8 || boundingBox[0].length < 3 || mvp.returnWidth() != 4 || mvp.returnHeight() != 4)
        return false;
      for(byte i = 0; i < 8; i++){
        Matrix usedCorner = MatrixOperations.matrixMultiply(mvp, VectorOperations.from3DVecTo4DVec(boundingBox[i]));
        float x = usedCorner.returnData(0, 0);
        float y = usedCorner.returnData(1, 0);
        float z = usedCorner.returnData(2, 0);
        float w = usedCorner.returnData(3, 0);
        if(x >= -w && x <= w && y >= -w && y <= w && z >= -w && z <= w)
          return true;
        }
        return false;
    }
    
    public static boolean isInClipSpace(Matrix mv, float[][] boundingBox, float zNear, float zFar){
      if(boundingBox.length < 8 || boundingBox[0].length < 3 || mv.returnWidth() != 4 || mv.returnHeight() != 4)
        return false;
      for(byte i = 0; i < 8; i++){
        Matrix usedCorner = MatrixOperations.matrixMultiply(mv, VectorOperations.from3DVecTo4DVec(boundingBox[i]));
        float z = usedCorner.returnData(2, 0);
        if(z <= zFar)
          return true;
        }
        return false;
    }
    
    //Determines the direction of the triangle for backface culling or if the triangle is exempt from backface culling
    public static float returnEdgeDir(Model[] modelList, float[][] points, int[] colour, int index, int backIndex, int faceDirection, boolean universalFill, boolean universalStroke){
      float edgeDir = 0;
      if((backIndex <= -1 || modelList[index].returnBackColourCount() > 0) && (universalFill || !universalStroke)){
        //Taking the  vectors (points[(i+1)%3][0], points[(i+1)%3][1]) and (-points[i][0], points[i][1]) to compute the direction of that particular edge 
        //and add the result to the overall direction of the whole triangle
        for(byte i = 0; i < 3; i++)
          edgeDir+=(points[(i+1)%3][0] - points[i][0])*(points[(i+1)%3][1] + points[i][1]);

        edgeDir*=faceDirection; //Hopefully will correct for when there is an odd number of negative scales
        //Determines if triangle is exempt from backface culling and if its back face should be set to a different colour than its front face
        if(backIndex >= 0 && edgeDir <= 0){
          if(backIndex < modelList[index].returnBackColourCount()){
            if(modelList[index].returnBackColour(backIndex, (byte)0) != -1)
              colour[0] = modelList[index].returnBackColour(backIndex, (byte)0);
            if(modelList[index].returnBackColour(backIndex, (byte)1) != -1)
              colour[1] = modelList[index].returnBackColour(backIndex, (byte)1);
           }
           edgeDir = 1;
         }
      }
      //If the triangle is exempt from backface culling and does not have a list of back face colours
      else
       edgeDir = 1;
      return edgeDir;
    }
    public static float returnEdgeDir(Model[] modelList, float[][] points, int[] colour, int index, int backIndex, int faceDirection, boolean universalFill, boolean universalStroke, int lightColour, byte lightIntensity){
      float edgeDir = 0;
      if((backIndex <= -1 || modelList[index].returnBackColourCount() > 0) && (universalFill || !universalStroke)){
        //Taking the  vectors (points[(i+1)%3][0], points[(i+1)%3][1]) and (-points[i][0], points[i][1]) to compute the direction of that particular edge 
        //and add the result to the overall direction of the whole triangle
        for(byte i = 0; i < 3; i++)
          edgeDir+=(points[(i+1)%3][0] - points[i][0])*(points[(i+1)%3][1] + points[i][1]);

        edgeDir*=faceDirection; //Hopefully will correct for when there is an odd number of negative scales
        //Determines if triangle is exempt from backface culling and if its back face should be set to a different colour than its front face
        if(backIndex >= 0 && edgeDir <= 0){
          if(backIndex < modelList[index].returnBackColourCount()){
            if(modelList[index].returnBackColour(backIndex, (byte)0) != -1)
              colour[0] = TriangleRasterizer.interpolatePixels((lightColour & 0xFFFFFF) | (((int)lightIntensity & 0xFF) << 24), modelList[index].returnBackColour(backIndex, (byte)0)) & 0xFFFFFF | (modelList[index].returnBackColour(backIndex, (byte)0) & 0xFF000000);
            if(modelList[index].returnBackColour(backIndex, (byte)1) != -1)
              colour[1] = TriangleRasterizer.interpolatePixels((lightColour & 0xFFFFFF) | (((int)lightIntensity & 0xFF) << 24), modelList[index].returnBackColour(backIndex, (byte)1)) & 0xFFFFFF | (modelList[index].returnBackColour(backIndex, (byte)1) & 0xFF000000);
           }
           edgeDir = 1;
         }
      }
      //If the triangle is exempt from backface culling and does not have a list of back face colours
      else
       edgeDir = 1;
      return edgeDir;
    }
    public static float returnEdgeDir(Model[] modelList, float[][] points, int[] colour, int index, int backIndex, int faceDirection, boolean universalFill, boolean universalStroke, int lightColour, byte lightIntensity, float[] vector){
      float edgeDir = 0;
      if((backIndex <= -1 || modelList[index].returnBackColourCount() > 0) && (universalFill || !universalStroke)){
        //Taking the  vectors (points[(i+1)%3][0], points[(i+1)%3][1]) and (-points[i][0], points[i][1]) to compute the direction of that particular edge 
        //and add the result to the overall direction of the whole triangle
        for(byte i = 0; i < 3; i++)
          edgeDir+=(points[(i+1)%3][0] - points[i][0])*(points[(i+1)%3][1] + points[i][1]);

        edgeDir*=faceDirection; //Hopefully will correct for when there is an odd number of negative scales
        //Determines if triangle is exempt from backface culling and if its back face should be set to a different colour than its front face
        if(backIndex >= 0 && edgeDir <= 0){
          vector[0] = -vector[0];
          vector[1] = -vector[1];
          vector[2] = -vector[2];
          if(backIndex < modelList[index].returnBackColourCount()){
            if(modelList[index].returnBackColour(backIndex, (byte)0) != -1)
              colour[0] = TriangleRasterizer.interpolatePixels((lightColour & 0xFFFFFF) | (((int)lightIntensity & 0xFF) << 24), modelList[index].returnBackColour(backIndex, (byte)0)) & 0xFFFFFF | (modelList[index].returnBackColour(backIndex, (byte)0) & 0xFF000000);
            if(modelList[index].returnBackColour(backIndex, (byte)1) != -1)
              colour[1] = TriangleRasterizer.interpolatePixels((lightColour & 0xFFFFFF) | (((int)lightIntensity & 0xFF) << 24), modelList[index].returnBackColour(backIndex, (byte)1)) & 0xFFFFFF | (modelList[index].returnBackColour(backIndex, (byte)1) & 0xFF000000);
           }
           edgeDir = 1;
         }
      }
      //If the triangle is exempt from backface culling and does not have a list of back face colours
      else
       edgeDir = 1;
      return edgeDir;
    }
}
