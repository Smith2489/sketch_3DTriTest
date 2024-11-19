//Class for abstracting away a model's data
public class Model{
  private float[][] points = new float[8][3]; //Vertices for the model
  private int[][] polygons = new int[12][3];
  private int[][] colours = new int[12][2]; //Triangle stroke(0) and fill(1)
  private int polygonCount = 0; //How many polygons are in the model
  private float[] modelPosition = {0, 0, 0}; //Model position in space (x, y, z)
  private float[] modelAngles = {0, 0, 0}; //Model rotation
  private float[] modelScale = {1, 1, 1}; //Model scale
  private float[][] modelShear = {{0, 0}, {0, 0}, {0, 0}};
  private float[] minVals = {Float.intBitsToFloat(-1), Float.intBitsToFloat(-1), Float.intBitsToFloat(-1)}; //Min x, y, z of model
  private float[] maxVals = {Float.intBitsToFloat(-1), Float.intBitsToFloat(-1), Float.intBitsToFloat(-1)}; //Max x, y, z of model
  private byte flags = 0; //bit 0 = hasStroke, bit 1 = hasFill, bit 2 = isBillBoard, bit 3=attached to camera, bit 4 = noDepth
  private int[] backVisible = new int[0];
  private int[][] backColour = new int[0][2]; //Index 0 = stroke, index 1 = fill
  private int numberOfVisibleBacks = 0;
  private int[] ends = {-1, -1}; //0 = min, 1 = max
  //Default constructor
  public Model(){
    backVisible = new int[0];
    for(byte i = 0; i < 3; i++){
      minVals[i] = -1;
      maxVals[i] = 1;
    }
    modelPosition[0] = 0;
    modelPosition[1] = 0;
    modelPosition[2] = 0;
    modelAngles[0] = 0;
    modelAngles[1] = 0;
    modelAngles[2] = 0;
    modelScale[0] = 1;
    modelScale[1] = 1;
    modelScale[2] = 1;
    float[][] cubeVertices = {{-1, -1, -1}, {-1, 1, -1}, {1, 1, -1}, {1, -1, -1}, 
                              {-1, -1, 1}, {-1, 1, 1}, {1, 1, 1}, {1, -1, 1}};
    int[][] cubeTris = {{0, 1, 3}, {1, 2, 3},//Front side
                        {5, 4, 7}, {7, 6, 5},//Back side
                        {1, 0, 4}, {4, 5, 1},//Left side
                        {7, 3, 2}, {2, 6, 7},//Right side
                        {0, 7, 4}, {0, 3, 7},//Top side
                        {1, 5, 6}, {1, 6, 2}};//Bottom side
    for(byte i = 0; i < 8; i++){
      for(byte j = 0; j < 3; j++)
        points[i][j] = cubeVertices[i][j];
    }
    for(byte i = 0; i < 12; i++){
      for(byte j = 0; j < 3; j++)
          polygons[i][j] = cubeTris[i][j];
     colours[i][0] = 0;
     colours[i][1] = (i&1)*-1;
    }
    polygonCount = 12;
    flags = 2;
  }
  
  //Constructor with 3D array of vertex positions, 2D array of colours, and 2 booleans for if the model has a stroke or a fill
  public Model(float[][] pointSet, int[][] polygonSet, int[][] colourSet, boolean hasStroke, boolean hasFill, boolean isBill){
    backVisible = new int[0];
    points = new float[pointSet.length][3];
    polygons = new int[polygonSet.length][3];
    colours = new int[polygons.length][2];
    if(polygons.length == 0){
      System.out.println("ERROR: CANNOT HAVE 0 POLYGONS");
      System.exit(1);
    }
    if(colourSet.length == 0){
      System.out.println("ERROR: CANNOT HAVE 0 COLOURS");
      System.exit(1);
    }
    //Initializing min and max positions
    for(byte i = 0; i < 3; i++){
      minVals[i] = Float.intBitsToFloat(-1);
      maxVals[i] = Float.intBitsToFloat(-1);
    }
    //Initializing data
    modelPosition[0] = 0;
    modelPosition[1] = 0;
    modelPosition[2] = 0;
    modelAngles[0] = 0;
    modelAngles[1] = 0;
    modelAngles[2] = 0;
    modelScale[0] = 1;
    modelScale[1] = 1;
    modelScale[2] = 1;
    flags = (byte)(((hasStroke) ? 1 : 0)|((hasFill) ? 2 : 0)|((isBill) ? 4 : 0));
    for(int i = 0; i < points.length; i++){
      points[i][0] = pointSet[i][0];
      points[i][1] = pointSet[i][1];
      points[i][2] = pointSet[i][2];
      for(byte j = 0; j < 3; j++){
        if(Float.isNaN(minVals[j]))
          minVals[j] = points[i][j];
        else
          minVals[j] = Math.min(minVals[j], points[i][j]);
        if(Float.isNaN(maxVals[j]))
          maxVals[j] = points[i][j];
        else
          maxVals[j] = Math.max(maxVals[j], points[i][j]);
      }
    }
    //Adding polygons and assinging them colours and setting min and max vertex positions
    for(int i = 0; i < polygons.length; i++){
      colours[i][0] = colourSet[i%colourSet.length][0];
      if(colours[i][0] < 0 || colours[i][0] > 0xFF){
       if(colours[i][0] >= 0 && colours[i][0] < 0x1000000)
        colours[i][0] = 0xFF000000|colours[i][0];
     }
     else
        colours[i][0] = 0xFF000000|(colours[i][0] << 16)|(colours[i][0] << 8)|colours[i][0];
      colours[i][1] = colourSet[i%colourSet.length][1];
      if(colours[i][1] < 0 || colours[i][1] > 0xFF){
       if(colours[i][1] >= 0 && colours[i][1] < 0x1000000)
        colours[i][1] = 0xFF000000|colours[i][1];
     }
     else
        colours[i][1] = 0xFF000000|(colours[i][1] << 16)|(colours[i][1] << 8)|colours[i][1];
      for(byte j = 0; j < 3; j++)
        polygons[i][j] = polygonSet[i][j];
    }
    //Setting the polygon count
    polygonCount = polygons.length;
    
  }
  //Constructor with 3D array of vertex positions and 2D array of colours
  public Model(float[][] pointSet, int[][] polygonSet, int[][] colourSet){
    backVisible = new int[0];
    points = new float[pointSet.length][3];
    polygons = new int[polygonSet.length][3];
    colours = new int[polygons.length][2];
    if(polygons.length == 0){
      System.out.println("ERROR: CANNOT HAVE 0 POLYGONS");
      System.exit(1);
    }
    if(colourSet.length == 0){
      System.out.println("ERROR: CANNOT HAVE 0 COLOURS");
      System.exit(1);
    }
    //Initializing min and max positions
    for(byte i = 0; i < 3; i++){
      minVals[i] = Float.intBitsToFloat(-1);
      maxVals[i] = Float.intBitsToFloat(-1);
    }
    //Initializing data
    modelPosition[0] = 0;
    modelPosition[1] = 0;
    modelPosition[2] = 0;
    modelAngles[0] = 0;
    modelAngles[1] = 0;
    modelAngles[2] = 0;
    modelScale[0] = 1;
    modelScale[1] = 1;
    modelScale[2] = 1;
    flags = 2;
    for(int i = 0; i < points.length; i++){
      points[i][0] = pointSet[i][0];
      points[i][1] = pointSet[i][1];
      points[i][2] = pointSet[i][2];
      for(byte j = 0; j < 3; j++){
        if(Float.isNaN(minVals[j]))
          minVals[j] = points[i][j];
        else
          minVals[j] = Math.min(minVals[j], points[i][j]);
        if(Float.isNaN(maxVals[j]))
          maxVals[j] = points[i][j];
        else
          maxVals[j] = Math.max(maxVals[j], points[i][j]);
      }
    }
    //Adding polygons and assinging them colours and setting min and max vertex positions
    for(int i = 0; i < polygons.length; i++){
      colours[i][0] = colourSet[i%colourSet.length][0];
      if(colours[i][0] < 0 || colours[i][0] > 0xFF){
       if(colours[i][0] >= 0 && colours[i][0] < 0x1000000)
        colours[i][0] = 0xFF000000|colours[i][0];
     }
     else
        colours[i][0] = 0xFF000000|(colours[i][0] << 16)|(colours[i][0] << 8)|colours[i][0];
      colours[i][1] = colourSet[i%colourSet.length][1];
      if(colours[i][1] < 0 || colours[i][1] > 0xFF){
       if(colours[i][1] >= 0 && colours[i][1] < 0x1000000)
        colours[i][1] = 0xFF000000|colours[i][1];
     }
     else
        colours[i][1] = 0xFF000000|(colours[i][1] << 16)|(colours[i][1] << 8)|colours[i][1];
      for(byte j = 0; j < 3; j++)
        polygons[i][j] = polygonSet[i][j];
    }
    //Setting the polygon count
    polygonCount = polygons.length;
  }
  
  //Initializes the list of triangles that are visible regardless of if they are facing the camera or not
  public void initBackVisible(int size){
    backVisible = new int[size];
    numberOfVisibleBacks = 0;
    for(int i = 0; i < size; i++)
      backVisible[i] = -1;
  }
  
  //Initializes the list of back face colours
  public void initBackColours(int size){
    backColour = new int[size][2];
    for(int i = 0; i < size; i++){
      backColour[i][0] = -1;
      backColour[i][1] = -1; 
    }
  }
  
  //Fills in the list of back face colours
  public void setBackColour(int index, int stroke, int fill){
    if(index >= 0 && index < backColour.length){
      backColour[index][0] = stroke;
      backColour[index][1] = fill;
      return;
    }
    System.out.println("ERROR: INDEX OUT OF BOUNDS");
    System.exit(1);
  }

  //Places a triangle into the list
  public void setBackVisible(int index){
    if(backVisible.length > 0){
      if(numberOfVisibleBacks < backVisible.length){
        //Places the first triangle in to the list
        if(numberOfVisibleBacks == 0){
          ends[0] = index;
          ends[1] = index;
          backVisible[0] = index;
          numberOfVisibleBacks++;
        }
        //Places in a new triangle at the end if it is greater than current maximum
        else if(index > ends[1]){
          backVisible[numberOfVisibleBacks] = index;
          numberOfVisibleBacks++;
          ends[1] = index;
        }
        //Places a new triangle at the start if it is less than the minimum
        else if(index < ends[0]){
          for(int i = numberOfVisibleBacks-1; i >= 0; i--)
            backVisible[i+1] = backVisible[i];
          backVisible[0] = index;
          numberOfVisibleBacks++;
          ends[0] = index;
        }
        //Places a new triangle into the list and sorts it if it is between the maximum and minimum
        else{
          if(!ListFunctions.binarySearch(backVisible, index, 0, numberOfVisibleBacks)){
            //Placing the triangle into the list
            backVisible[numberOfVisibleBacks] = index;
            numberOfVisibleBacks++;
            //Constructing a temporary array for sorting
            int[] indices = new int[numberOfVisibleBacks];
            for(int i = 0; i < numberOfVisibleBacks; i++)
              indices[i] = backVisible[i];
            ListFunctions.mergeSort(indices, 0, indices.length-1);     
            //Copying the temporary array's elements back into the main array
            for(int i = 0; i < numberOfVisibleBacks; i++)
              backVisible[i] = indices[i];
            ends[0] = backVisible[0];
            ends[1] = backVisible[numberOfVisibleBacks-1];
          }
        }
        return;
      }
      System.out.println("ERROR: "+index+" OUT OF BOUNDS");
      System.exit(1);
    }
    System.out.println("ERROR: LIST OF POLYGONS WITH VISIBLE BACKS NOT INITIALIZED");
    System.exit(1);
  }
  
  //Setting the model's position
  public void setPosition(float x, float y, float z){
    modelPosition[0] = x;
    modelPosition[1] = y;
    modelPosition[2] = z;
  }
  //Setting the model's rotation
  public void setAngle(float alpha, float beta, float gamma){
    modelAngles[0] = alpha;
    modelAngles[1] = beta;
    modelAngles[2] = gamma;
  }
  //Setting the model's scale
  public void setScale(float sX, float sY, float sZ){
    modelScale[0] = sX;
    modelScale[1] = sY;
    modelScale[2] = sZ;
  }
  
  public void setShear(float[][] shear){
    for(byte i = 0; i < 3; i++){
      modelShear[i][0] = shear[i][0];
      modelShear[i][1] = shear[i][1];
    }
  }
  
  public void setShear(float[] shearX, float[] shearY, float[] shearZ){
    modelShear[0][0] = shearX[0];
    modelShear[0][1] = shearX[1];
    modelShear[1][0] = shearY[0];
    modelShear[1][1] = shearY[1];
    modelShear[2][0] = shearZ[0];
    modelShear[2][1] = shearZ[1];
  }
  public void setShearX(float x1, float x2){
    modelShear[0][0] = x1;
    modelShear[0][1] = x2;
  }
  public void setShearX(float[] shearX){
    modelShear[0][0] = shearX[0];
    modelShear[0][1] = shearX[1];
  }
  public void setShearY(float y1, float y2){
    modelShear[1][0] = y1;
    modelShear[1][1] = y2;
  }
  public void setShearY(float[] shearY){
    modelShear[1][0] = shearY[0];
    modelShear[1][1] = shearY[1];
  }
    public void setShearZ(float z1, float z2){
    modelShear[2][0] = z1;
    modelShear[2][1] = z2;
  }
  public void setShearZ(float[] shearZ){
    modelShear[2][0] = shearZ[0];
    modelShear[2][1] = shearZ[1];
  }

  //Setting if a model has stroke
  public void setHasStrokes(boolean hasStroke){
    if(hasStroke)
      flags|=1;
    else
      flags&=-2;
  }
  //Setting if a model has fill
  public void setHasFill(boolean hasFill){
    if(hasFill)
      flags|=2;
    else
      flags&=-3;
  }
  public void setIsBillBoard(boolean isBill){
    if(isBill)
      flags|=4;
    else
      flags&=-5;
  }
  public void setAttachedToCamera(boolean isAttached){
    if(isAttached)
      flags|=8;
    else
      flags&=-9;
  }
  public void disableDepth(boolean noDepth){
    if(noDepth)
      flags|=16;
    else
      flags&=-17;
  }

  //Setting polygons, assinging them colours, and setting the min and max vertex positions
  public void setPolygons(float[][] pointSet, int[][] polygonSet, int[][] colourSet){
    //Resetting the polygon and colour list
    backVisible = new int[0];
    polygons = new int[polygonSet.length][3];
    colours = new int[polygons.length][2];
    if(polygons.length == 0){
      System.out.println("ERROR: CANNOT HAVE 0 POLYGONS");
      System.exit(1);
    }
    if(colourSet.length == 0){
      System.out.println("ERROR: CANNOT HAVE 0 COLOURS");
      System.exit(1);
    }
    for(int i = 0; i < points.length; i++){
      points[i][0] = pointSet[i][0];
      points[i][1] = pointSet[i][1];
      points[i][2] = pointSet[i][2];
      for(byte j = 0; j < 3; j++){
        if(Float.isNaN(minVals[j]))
          minVals[j] = points[i][j];
        else
          minVals[j] = Math.min(minVals[j], points[i][j]);
        if(Float.isNaN(maxVals[j]))
          maxVals[j] = points[i][j];
        else
          maxVals[j] = Math.max(maxVals[j], points[i][j]);
      }
    }
    //Adding polygons and assinging them colours and setting min and max vertex positions
    for(int i = 0; i < polygons.length; i++){
      colours[i][0] = colourSet[i%colourSet.length][0];
      if(colours[i][0] < 0 || colours[i][0] > 0xFF){
       if(colours[i][0] >= 0 && colours[i][0] < 0x1000000)
        colours[i][0] = 0xFF000000|colours[i][0];
     }
     else
        colours[i][0] = 0xFF000000|(colours[i][0] << 16)|(colours[i][0] << 8)|colours[i][0];
      colours[i][1] = colourSet[i%colourSet.length][1];
      if(colours[i][1] < 0 || colours[i][1] > 0xFF){
       if(colours[i][1] >= 0 && colours[i][1] < 0x1000000)
        colours[i][1] = 0xFF000000|colours[i][1];
     }
     else
        colours[i][1] = 0xFF000000|(colours[i][1] << 16)|(colours[i][1] << 8)|colours[i][1];
      for(byte j = 0; j < 3; j++)
        polygons[i][j] = polygonSet[i][j];
    }
    //Setting the polygon count
    polygonCount = polygons.length;
  }
  public void setVertex(int index, float[] newPosition){
    if(index >= points.length){
      System.out.println("ERROR: INDEX OUT OF RANGE");
      System.exit(1);
    }
    points[index][0] = newPosition[0];
    points[index][1] = newPosition[1];
    points[index][2] = newPosition[2];
    changeBoundingBox();
  }
  public void setTriangle(int index, int[] newConnections){
    if(index >= polygons.length){
      System.out.println("ERROR: INDEX OUT OF RANGE");
      System.exit(1);
    }
    polygons[index][0] = newConnections[0];
    polygons[index][1] = newConnections[1];
    polygons[index][2] = newConnections[2];
  }
  public void setVertex(int index, float newX, float newY, float newZ){
    if(index >= points.length){
      System.out.println("ERROR: INDEX OUT OF RANGE");
      System.exit(1);
    }
    points[index][0] = newX;
    points[index][1] = newY;
    points[index][2] = newZ;
    changeBoundingBox();
  }
  public void setTriangle(int index, int newXConnect, int newYConnect, int newZConnect){
    if(index >= polygons.length){
      System.out.println("ERROR: INDEX OUT OF RANGE");
      System.exit(1);
    }
    polygons[index][0] = newXConnect;
    polygons[index][1] = newYConnect;
    polygons[index][2] = newZConnect;
  }
  public int returnPolygonCount(){
    return polygonCount;
  }
  public int[][] returnColours(){
    return colours;
  }
  public boolean returnHasStroke(){
    return (flags & 1) == 1;
  }
  public boolean returnHasFill(){
    return (flags & 2) == 2;
  }
  public boolean returnIsBillBoard(){
    return (flags & 4) == 4;
  }
  public boolean returnAttachedToCamera(){
    return (flags & 8) == 8;
  }
  public boolean returnNoDepth(){
    return (flags & 16) == 16;
  }
  public float[][] returnPoints(){
    return points;
  }
  public int[][] returnPolygons(){
    return polygons;
  }
  public float[] returnPosition(){
    return modelPosition;
  }
  public float[] returnAngle(){
    return modelAngles;
  }
  public float[] returnScale(){
    return modelScale;
  }
  public float[][] returnShear(){
    return modelShear;
  }
  public float[] returnShearX(){
    return modelShear[0];
  }
  public float[] returnShearY(){
    return modelShear[1];
  }
  public float[] returnShearZ(){
    return modelShear[2];
  }
  public float[] returnMinVertices(){
    return minVals;
  }
  public float[] returnMaxVertices(){
    return maxVals;
  }
  
  //Returns if a triangle is visible when facing away from the camera or not
  public boolean returnBackVisible(int index){
    if(numberOfVisibleBacks == 0 || index <= -1 || index >= backVisible.length)
      return false;
    if(numberOfVisibleBacks == polygonCount)
      return true;
    return ListFunctions.binarySearch(backVisible, index, 0, numberOfVisibleBacks);
  }
  public int returnBackTri(int index){
    if(numberOfVisibleBacks == 0 || index <= -1 || index >= backVisible.length)
      return -1;
    if(numberOfVisibleBacks == polygonCount)
      return index;
    return ListFunctions.binarySearchIndex(backVisible, index, 0, numberOfVisibleBacks);
  }
  
  //Returns the length of the list of back face colours
  public int returnBackColourCount(){
    return backColour.length;
  }
  
  //Returns the stroke (sub=0) or fill (sub=1) of the triangle of interest
  public int returnBackColour(int index, byte sub){
    if(sub >= 0 && sub <= 1 && index >= 0 && index < backColour.length)
      return backColour[index][sub];
    System.out.println("ERROR: INDEXING OUT OF RANGE");
    return -1;
  }
  
  //Returns the bounding box
  public float[][] returnBoundingBox(){
    float[][] corners = {{minVals[0], minVals[1], minVals[2]},
                         {maxVals[0], minVals[1], minVals[2]},
                         {maxVals[0], maxVals[1], minVals[2]},
                         {minVals[0], maxVals[1], minVals[2]},
                         {minVals[0], minVals[1], maxVals[2]},
                         {maxVals[0], minVals[1], maxVals[2]},
                         {maxVals[0], maxVals[1], maxVals[2]},
                         {minVals[0], maxVals[1], maxVals[2]}};
    return corners;
  }
  //Resizes the bounding box
  private void changeBoundingBox(){
    for(byte i = 0; i < 3; i++){
      minVals[i] = Float.intBitsToFloat(-1);
      maxVals[i] = Float.intBitsToFloat(-1);
     }
     for(int i = 0; i < points.length; i++){
       for(byte j = 0; j < 3; j++){
           if(Float.isNaN(minVals[j]))
             minVals[j] = points[i][j];
            else
              minVals[j] = Math.min(minVals[j], points[i][j]);
            if(Float.isNaN(maxVals[j]))
              maxVals[j] = points[i][j];
            else
              maxVals[j] = Math.max(maxVals[j], points[i][j]);
      }
    }
  }
}
