package Renderer.ModelDataHandler;
import Maths.LinearAlgebra.*;
public class Geometry {
  ModelVertices m = new ModelVertices();
  private int[][] polygons = new int[12][3];
  private float[][] polygonNormals = new float[12][3]; //The normals of the polygons
  private float[][] vertexNormals = new float[m.returnVertices().length][3]; //The normals of the vertices
  private int polygonCount = 0; //How many polygons are in the model
  private boolean isBillBoard = false;

  public Geometry(){
    m = new ModelVertices();
    vertexNormals = new float[m.returnVertices().length][3];
    int[] numContributedToNormal = new int[vertexNormals.length];
    int[][] cubeTris = {{3, 1, 1}, {3, 2, 1},//Front side
                        {7, 4, 5}, {5, 6, 7},//Back side
                        {1, 4, 0}, {4, 1, 5},//Left side
                        {7, 2, 3}, {2, 7, 6},//Right side
                        {7, 0, 4}, {3, 0, 7},//Top side
                        {5, 1, 6}, {6, 1, 2}};//Bottom side
      float[][] points = m.returnVertices();
      for(byte i = 0; i < 12; i++){
        for(byte j = 0; j < 3; j++)
          polygons[i][j] = cubeTris[i][j];
        float[][] sides = {{points[polygons[i][1]][0]-points[polygons[i][0]][0], points[polygons[i][1]][1]-points[polygons[i][0]][1], points[polygons[i][1]][2]-points[polygons[i][0]][2]},
                           {points[polygons[i][2]][0]-points[polygons[i][0]][0], points[polygons[i][2]][1]-points[polygons[i][0]][1], points[polygons[i][2]][2]-points[polygons[i][0]][2]}};

        polygonNormals[i] = VectorOperations.vectorNormalization3D(VectorOperations.vectorCross3D(sides[0], sides[1])); 
        for(byte j = 0; j < 3; j++){
          vertexNormals[polygons[i][j]][0]+=polygonNormals[i][0];
          vertexNormals[polygons[i][j]][1]+=polygonNormals[i][1];
          vertexNormals[polygons[i][j]][2]+=polygonNormals[i][2];
          numContributedToNormal[polygons[i][j]]++;
        }
      }
      for(int i = 0; i < vertexNormals.length; i++){
        if(numContributedToNormal[i] > 0){
          vertexNormals[i][0]/=numContributedToNormal[i];
          vertexNormals[i][1]/=numContributedToNormal[i];
          vertexNormals[i][2]/=numContributedToNormal[i];
          vertexNormals[i] = VectorOperations.vectorNormalization3D(vertexNormals[i]);
        }
      }
      polygonCount = 12;
      isBillBoard = false;
  }
  //Constructor with 3D array of vertex positions, 2D array of colours, and 2 booleans for if the model has a stroke or a fill
  public Geometry(float[][] pointSet, int[][] polygonSet, boolean isBill){
    m = new ModelVertices(pointSet);
    vertexNormals = new float[m.returnVertices().length][3];
    int[] numContributedToNormal = new int[vertexNormals.length];
    polygons = new int[polygonSet.length][3];
    polygonNormals = new float[polygons.length][3];
    if(polygons.length == 0){
      System.out.println("ERROR: CANNOT HAVE 0 POLYGONS");
      System.exit(1);
    }
    //Initializing data
    isBillBoard = isBill;

    //Adding polygons and assinging them colours and setting min and max vertex positions
    for(int i = 0; i < polygons.length; i++){
      for(byte j = 0; j < 3; j++)
        polygons[i][j] = polygonSet[i][j];
    }
    float[][] points = m.returnVertices();
    for(int i = 0; i < polygons.length; i++){
      float[][] sides = {{points[polygons[i][1]][0]-points[polygons[i][0]][0], points[polygons[i][1]][1]-points[polygons[i][0]][1], points[polygons[i][1]][2]-points[polygons[i][0]][2]},
                         {points[polygons[i][2]][0]-points[polygons[i][0]][0], points[polygons[i][2]][1]-points[polygons[i][0]][1], points[polygons[i][2]][2]-points[polygons[i][0]][2]}};
              
      polygonNormals[i] = VectorOperations.vectorNormalization3D(VectorOperations.vectorCross3D(sides[0], sides[1])); 
      for(byte j = 0; j < 3; j++){
        vertexNormals[polygons[i][j]][0]+=polygonNormals[i][0];
        vertexNormals[polygons[i][j]][1]+=polygonNormals[i][1];
        vertexNormals[polygons[i][j]][2]+=polygonNormals[i][2];
        numContributedToNormal[polygons[i][j]]++;
      }
    }
    for(int i = 0; i < vertexNormals.length; i++){
      if(numContributedToNormal[i] > 0){
        vertexNormals[i][0]/=numContributedToNormal[i];
        vertexNormals[i][1]/=numContributedToNormal[i];
        vertexNormals[i][2]/=numContributedToNormal[i];
        vertexNormals[i] = VectorOperations.vectorNormalization3D(vertexNormals[i]);
      }
    }
    //Setting the polygon count
    polygonCount = polygons.length;
    
  }
  //Constructor with 3D array of vertex positions and 2D array of colours
  public Geometry(float[][] pointSet, int[][] polygonSet){
    m = new ModelVertices(pointSet);
    vertexNormals = new float[m.returnVertices().length][3];
    int[] numContributedToNormal = new int[vertexNormals.length];
    polygons = new int[polygonSet.length][3];
    polygonNormals = new float[polygons.length][3];
    if(polygons.length == 0){
      System.out.println("ERROR: CANNOT HAVE 0 POLYGONS");
      System.exit(1);
    }

    //Initializing data
    isBillBoard = false;

    //Adding polygons and assinging them colours and setting min and max vertex positions
    for(int i = 0; i < polygons.length; i++){
      for(byte j = 0; j < 3; j++)
        polygons[i][j] = polygonSet[i][j];
    }
    float[][] points = m.returnVertices();
    for(int i = 0; i < polygons.length; i++){
      float[][] sides = {{points[polygons[i][1]][0]-points[polygons[i][0]][0], points[polygons[i][1]][1]-points[polygons[i][0]][1], points[polygons[i][1]][2]-points[polygons[i][0]][2]},
                         {points[polygons[i][2]][0]-points[polygons[i][0]][0], points[polygons[i][2]][1]-points[polygons[i][0]][1], points[polygons[i][2]][2]-points[polygons[i][0]][2]}};
              
      polygonNormals[i] = VectorOperations.vectorNormalization3D(VectorOperations.vectorCross3D(sides[0], sides[1])); 
      for(byte j = 0; j < 3; j++){
        vertexNormals[polygons[i][j]][0]+=polygonNormals[i][0];
        vertexNormals[polygons[i][j]][1]+=polygonNormals[i][1];
        vertexNormals[polygons[i][j]][2]+=polygonNormals[i][2];
        numContributedToNormal[polygons[i][j]]++;
      }
    }
    for(int i = 0; i < vertexNormals.length; i++){
      if(numContributedToNormal[i] > 0){
        vertexNormals[i][0]/=numContributedToNormal[i];
        vertexNormals[i][1]/=numContributedToNormal[i];
        vertexNormals[i][2]/=numContributedToNormal[i];
        vertexNormals[i] = VectorOperations.vectorNormalization3D(vertexNormals[i]);
      }
    }
    //Setting the polygon count
    polygonCount = polygons.length;
  }

    //Constructor with 3D array of vertex positions, 2D array of colours, and 2 booleans for if the model has a stroke or a fill
    public Geometry(ModelVertices newM, int[][] polygonSet, boolean isBill){
      m = newM;
      vertexNormals = new float[m.returnVertices().length][3];
      int[] numContributedToNormal = new int[vertexNormals.length];
      polygons = new int[polygonSet.length][3];
      polygonNormals = new float[polygons.length][3];
      if(polygons.length == 0){
        System.out.println("ERROR: CANNOT HAVE 0 POLYGONS");
        System.exit(1);
      }
      //Initializing data
      isBillBoard = isBill;
  
      //Adding polygons and assinging them colours and setting min and max vertex positions
      for(int i = 0; i < polygons.length; i++){
        for(byte j = 0; j < 3; j++)
          polygons[i][j] = polygonSet[i][j];
      }
      float[][] points = m.returnVertices();
      for(int i = 0; i < polygons.length; i++){
        float[][] sides = {{points[polygons[i][1]][0]-points[polygons[i][0]][0], points[polygons[i][1]][1]-points[polygons[i][0]][1], points[polygons[i][1]][2]-points[polygons[i][0]][2]},
                           {points[polygons[i][2]][0]-points[polygons[i][0]][0], points[polygons[i][2]][1]-points[polygons[i][0]][1], points[polygons[i][2]][2]-points[polygons[i][0]][2]}};
        
        polygonNormals[i] = VectorOperations.vectorNormalization3D(VectorOperations.vectorCross3D(sides[0], sides[1])); 
        for(byte j = 0; j < 3; j++){
          vertexNormals[polygons[i][j]][0]+=polygonNormals[i][0];
          vertexNormals[polygons[i][j]][1]+=polygonNormals[i][1];
          vertexNormals[polygons[i][j]][2]+=polygonNormals[i][2];
          numContributedToNormal[polygons[i][j]]++;
        }
      }
      for(int i = 0; i < vertexNormals.length; i++){
        if(numContributedToNormal[i] > 0){
          vertexNormals[i][0]/=numContributedToNormal[i];
          vertexNormals[i][1]/=numContributedToNormal[i];
          vertexNormals[i][2]/=numContributedToNormal[i];
          vertexNormals[i] = VectorOperations.vectorNormalization3D(vertexNormals[i]);
        }
      }
      //Setting the polygon count
      polygonCount = polygons.length;
      
    }
    //Constructor with 3D array of vertex positions and 2D array of colours
    public Geometry(ModelVertices newM, int[][] polygonSet){
      m = newM;
      vertexNormals = new float[m.returnVertices().length][3];
      int[] numContributedToNormal = new int[vertexNormals.length];
      polygons = new int[polygonSet.length][3];
      polygonNormals = new float[polygons.length][3];
      if(polygons.length == 0){
        System.out.println("ERROR: CANNOT HAVE 0 POLYGONS");
        System.exit(1);
      }
  
      //Initializing data
      isBillBoard = false;
  
      //Adding polygons and assinging them colours and setting min and max vertex positions
      for(int i = 0; i < polygons.length; i++){
        for(byte j = 0; j < 3; j++)
          polygons[i][j] = polygonSet[i][j];
      }
      float[][] points = m.returnVertices();
      for(int i = 0; i < polygons.length; i++){
        float[][] sides = {{points[polygons[i][1]][0]-points[polygons[i][0]][0], points[polygons[i][1]][1]-points[polygons[i][0]][1], points[polygons[i][1]][2]-points[polygons[i][0]][2]},
                           {points[polygons[i][2]][0]-points[polygons[i][0]][0], points[polygons[i][2]][1]-points[polygons[i][0]][1], points[polygons[i][2]][2]-points[polygons[i][0]][2]}};
                
        polygonNormals[i] = VectorOperations.vectorNormalization3D(VectorOperations.vectorCross3D(sides[0], sides[1])); 
        for(byte j = 0; j < 3; j++){
          vertexNormals[polygons[i][j]][0]+=polygonNormals[i][0];
          vertexNormals[polygons[i][j]][1]+=polygonNormals[i][1];
          vertexNormals[polygons[i][j]][2]+=polygonNormals[i][2];
          numContributedToNormal[polygons[i][j]]++;
        }
      }
      for(int i = 0; i < vertexNormals.length; i++){
        if(numContributedToNormal[i] > 0){
          vertexNormals[i][0]/=numContributedToNormal[i];
          vertexNormals[i][1]/=numContributedToNormal[i];
          vertexNormals[i][2]/=numContributedToNormal[i];
          vertexNormals[i] = VectorOperations.vectorNormalization3D(vertexNormals[i]);
        }
      }
      //Setting the polygon count
      polygonCount = polygons.length;
    }
  
    
  public void setVerticesPtr(ModelVertices newM){
    m = newM;
  }

  //Setting polygons, assinging them colours, and setting the min and max vertex positions
  public void setPolygons(float[][] pointSet, int[][] polygonSet){
    m = new ModelVertices(pointSet);
    vertexNormals = new float[m.returnVertices().length][3]; 
    int[] numContributedToNormal = new int[vertexNormals.length];
    //Adding polygons and assinging them colours and setting min and max vertex positions
    for(int i = 0; i < polygons.length; i++){

      for(byte j = 0; j < 3; j++)
        polygons[i][j] = polygonSet[i][j];
    }
    float[][] points = m.returnVertices();
    for(int i = 0; i < points.length; i++){
      float[][] sides = {{points[polygons[i][1]][0]-points[polygons[i][0]][0], points[polygons[i][1]][1]-points[polygons[i][0]][1], points[polygons[i][1]][2]-points[polygons[i][0]][2]},
                        {points[polygons[i][2]][0]-points[polygons[i][0]][0], points[polygons[i][2]][1]-points[polygons[i][0]][1], points[polygons[i][2]][2]-points[polygons[i][0]][2]}};
              
      polygonNormals[i] = VectorOperations.vectorNormalization3D(VectorOperations.vectorCross3D(sides[0], sides[1])); 
      for(byte j = 0; j < 3; j++){
        vertexNormals[polygons[i][j]][0]+=polygonNormals[i][0];
        vertexNormals[polygons[i][j]][1]+=polygonNormals[i][1];
        vertexNormals[polygons[i][j]][2]+=polygonNormals[i][2];
        numContributedToNormal[polygons[i][j]]++;
      }
    }
    for(int i = 0; i < vertexNormals.length; i++){
      if(numContributedToNormal[i] > 0){
        vertexNormals[i][0]/=numContributedToNormal[i];
        vertexNormals[i][1]/=numContributedToNormal[i];
        vertexNormals[i][2]/=numContributedToNormal[i];
        vertexNormals[i] = VectorOperations.vectorNormalization3D(vertexNormals[i]);
      }
    }
    //Setting the polygon count
    polygonCount = polygons.length;
  }
  public void setVertex(int index, float[] newPosition){
    vertexNormals = new float[m.returnVertices().length][3];
    int[] numContributedToNormal = new int[vertexNormals.length];
    m.setVertex(index, newPosition);
    float[][] points = m.returnVertices();
    for(int i = 0; i < polygons.length; i++){
      float[][] sides = {{points[polygons[i][1]][0]-points[polygons[i][0]][0], points[polygons[i][1]][1]-points[polygons[i][0]][1], points[polygons[i][1]][2]-points[polygons[i][0]][2]},
                         {points[polygons[i][2]][0]-points[polygons[i][0]][0], points[polygons[i][2]][1]-points[polygons[i][0]][1], points[polygons[i][2]][2]-points[polygons[i][0]][2]}};
                  
      polygonNormals[i] = VectorOperations.vectorNormalization3D(VectorOperations.vectorCross3D(sides[0], sides[1]));  
      for(byte j = 0; j < 3; j++){
        vertexNormals[polygons[i][j]][0]+=polygonNormals[i][0];
        vertexNormals[polygons[i][j]][1]+=polygonNormals[i][1];
        vertexNormals[polygons[i][j]][2]+=polygonNormals[i][2];
        numContributedToNormal[polygons[i][j]]++;
      }
    }
    for(int i = 0; i < vertexNormals.length; i++){
      if(numContributedToNormal[i] > 0){
        vertexNormals[i][0]/=numContributedToNormal[i];
        vertexNormals[i][1]/=numContributedToNormal[i];
        vertexNormals[i][2]/=numContributedToNormal[i];
        vertexNormals[i] = VectorOperations.vectorNormalization3D(vertexNormals[i]);
      }
    }
  }
  public void setTriangle(int index, int[] newConnections){
    if(index >= polygons.length){
      System.out.println("ERROR: INDEX OUT OF RANGE");
      System.exit(1);
    }
    float[][] points = m.returnVertices();
    polygons[index][0] = newConnections[0];
    polygons[index][1] = newConnections[1];
    polygons[index][2] = newConnections[2];
    int[] numContributedToNormal = new int[vertexNormals.length];
    for(int i = 0; i < polygons.length; i++){
      float[][] sides = {{points[polygons[i][1]][0]-points[polygons[i][0]][0], points[polygons[i][1]][1]-points[polygons[i][0]][1], points[polygons[i][1]][2]-points[polygons[i][0]][2]},
                         {points[polygons[i][2]][0]-points[polygons[i][0]][0], points[polygons[i][2]][1]-points[polygons[i][0]][1], points[polygons[i][2]][2]-points[polygons[i][0]][2]}};
                  
      polygonNormals[i] = VectorOperations.vectorNormalization3D(VectorOperations.vectorCross3D(sides[0], sides[1])); 
      for(byte j = 0; j < 3; j++){
        vertexNormals[polygons[i][j]][0]+=polygonNormals[i][0];
        vertexNormals[polygons[i][j]][1]+=polygonNormals[i][1];
        vertexNormals[polygons[i][j]][2]+=polygonNormals[i][2];
        numContributedToNormal[polygons[i][j]]++;
      }
    }
    for(int i = 0; i < vertexNormals.length; i++){
      if(numContributedToNormal[i] > 0){
        vertexNormals[i][0]/=numContributedToNormal[i];
        vertexNormals[i][1]/=numContributedToNormal[i];
        vertexNormals[i][2]/=numContributedToNormal[i];
        vertexNormals[i] = VectorOperations.vectorNormalization3D(vertexNormals[i]);
      }
    }
  }

  public void setVertex(int index, float newX, float newY, float newZ){
    m.setVertex(index, newX, newY, newZ);
    float[][] points = m.returnVertices();
    int[] numContributedToNormal = new int[vertexNormals.length];
    for(int i = 0; i < polygons.length; i++){
      float[][] sides = {{points[polygons[i][1]][0]-points[polygons[i][0]][0], points[polygons[i][1]][1]-points[polygons[i][0]][1], points[polygons[i][1]][2]-points[polygons[i][0]][2]},
                         {points[polygons[i][2]][0]-points[polygons[i][0]][0], points[polygons[i][2]][1]-points[polygons[i][0]][1], points[polygons[i][2]][2]-points[polygons[i][0]][2]}};
                  
      polygonNormals[i] = VectorOperations.vectorNormalization3D(VectorOperations.vectorCross3D(sides[0], sides[1])); 
      for(byte j = 0; j < 3; j++){
        vertexNormals[polygons[i][j]][0]+=polygonNormals[i][0];
        vertexNormals[polygons[i][j]][1]+=polygonNormals[i][1];
        vertexNormals[polygons[i][j]][2]+=polygonNormals[i][2];
        numContributedToNormal[polygons[i][j]]++;
      }
    }
    for(int i = 0; i < vertexNormals.length; i++){
      if(numContributedToNormal[i] > 0){
        vertexNormals[i][0]/=numContributedToNormal[i];
        vertexNormals[i][1]/=numContributedToNormal[i];
        vertexNormals[i][2]/=numContributedToNormal[i];
        vertexNormals[i] = VectorOperations.vectorNormalization3D(vertexNormals[i]);
      }
    }
  }
  public void setTriangle(int index, int newXConnect, int newYConnect, int newZConnect){
    if(index >= polygons.length){
      System.out.println("ERROR: INDEX OUT OF RANGE");
      System.exit(1);
    }
    float[][] points = m.returnVertices();
    polygons[index][0] = newXConnect;
    polygons[index][1] = newYConnect;
    polygons[index][2] = newZConnect;
    int[] numContributedToNormal = new int[vertexNormals.length];
    for(int i = 0; i < polygons.length; i++){
      float[][] sides = {{points[polygons[i][1]][0]-points[polygons[i][0]][0], points[polygons[i][1]][1]-points[polygons[i][0]][1], points[polygons[i][1]][2]-points[polygons[i][0]][2]},
                         {points[polygons[i][2]][0]-points[polygons[i][0]][0], points[polygons[i][2]][1]-points[polygons[i][0]][1], points[polygons[i][2]][2]-points[polygons[i][0]][2]}};
      polygonNormals[i] = VectorOperations.vectorNormalization3D(VectorOperations.vectorCross3D(sides[0], sides[1])); 
      for(byte j = 0; j < 3; j++){
        vertexNormals[polygons[i][j]][0]+=polygonNormals[i][0];
        vertexNormals[polygons[i][j]][1]+=polygonNormals[i][1];
        vertexNormals[polygons[i][j]][2]+=polygonNormals[i][2];
        numContributedToNormal[polygons[i][j]]++;
      }
    }
    for(int i = 0; i < vertexNormals.length; i++){
      if(numContributedToNormal[i] > 0){
        vertexNormals[i][0]/=numContributedToNormal[i];
        vertexNormals[i][1]/=numContributedToNormal[i];
        vertexNormals[i][2]/=numContributedToNormal[i];
        vertexNormals[i] = VectorOperations.vectorNormalization3D(vertexNormals[i]);
      }
    }
  }

  public void setIsBillBoard(boolean newIsBillBoard){
    isBillBoard = newIsBillBoard;
  } 
  public int returnPolygonCount(){
    return polygonCount;
  }
  public boolean returnIsBillBoard(){
    return isBillBoard;
  }
  public float[][] returnVertexNormals(){
    return vertexNormals;
  }
  public float[] returnNormals(int index){
    return polygonNormals[index];
  }
  public float[][] returnPoints(){
    return m.returnVertices();
  }
  public ModelVertices returnModelVerticesPtr(){
    return m;
  }
  public int[][] returnPolygons(){
    return polygons;
  }
  public float[] returnMinVertices(){
    return m.returnMinVertices();
  }
  public float[] returnMaxVertices(){
    return m.returnMaxVertices();
  }
  public float[] returnModelCentre(){
    return m.returnModelCentre();
  }

  //Returns the bounding box
  public float[][] returnBoundingBox(){
    return m.returnBoundingBox();
  }

  public boolean equals(Object o){
    if(o instanceof Geometry){
      Geometry g = (Geometry)o;
      boolean isEqual = true;
      isEqual&=(polygonCount == g.polygonCount);
      isEqual&=(isBillBoard == g.isBillBoard);
      isEqual&=(polygons.length == g.polygons.length);
      isEqual&=(polygonNormals.length == g.polygonNormals.length);
      isEqual&=(vertexNormals.length == g.vertexNormals.length);
      if(isEqual){
        isEqual&=m.equals(g.m);
        for(int i = 0; i < g.polygons.length; i++){
          isEqual&=(polygons[i][0] == g.polygons[i][0]);
          isEqual&=(polygons[i][1] == g.polygons[i][1]);
          isEqual&=(polygons[i][2] == g.polygons[i][2]);
    
          isEqual&=(Math.abs(polygonNormals[i][0] - g.polygonNormals[i][0]) <= 0.0001);
          isEqual&=(Math.abs(polygonNormals[i][1] - g.polygonNormals[i][1]) <= 0.0001);
          isEqual&=(Math.abs(polygonNormals[i][2] - g.polygonNormals[i][2]) <= 0.0001);
        }
        for(int i = 0; i < vertexNormals.length; i++){
          isEqual&=(Math.abs(vertexNormals[i][0] - g.vertexNormals[i][0]) <= 0.0001);
          isEqual&=(Math.abs(vertexNormals[i][1] - g.vertexNormals[i][1]) <= 0.0001);
          isEqual&=(Math.abs(vertexNormals[i][2] - g.vertexNormals[i][2]) <= 0.0001);
        }
      }
      return isEqual;
    }
    else
      return false;
  }
  public boolean equals(Geometry g){
    boolean isEqual = true;
    isEqual&=(polygonCount == g.polygonCount);
    isEqual&=(isBillBoard == g.isBillBoard);
    isEqual&=(polygons.length == g.polygons.length);
    isEqual&=(polygonNormals.length == g.polygonNormals.length);
    isEqual&=(vertexNormals.length == g.vertexNormals.length);
    if(isEqual){
      isEqual&=m.equals(g.m);
      for(int i = 0; i < g.polygons.length; i++){
        isEqual&=(polygons[i][0] == g.polygons[i][0]);
        isEqual&=(polygons[i][1] == g.polygons[i][1]);
        isEqual&=(polygons[i][2] == g.polygons[i][2]);
  
        isEqual&=(Math.abs(polygonNormals[i][0] - g.polygonNormals[i][0]) <= 0.0001);
        isEqual&=(Math.abs(polygonNormals[i][1] - g.polygonNormals[i][1]) <= 0.0001);
        isEqual&=(Math.abs(polygonNormals[i][2] - g.polygonNormals[i][2]) <= 0.0001);
      }
      for(int i = 0; i < vertexNormals.length; i++){
        isEqual&=(Math.abs(vertexNormals[i][0] - g.vertexNormals[i][0]) <= 0.0001);
        isEqual&=(Math.abs(vertexNormals[i][1] - g.vertexNormals[i][1]) <= 0.0001);
        isEqual&=(Math.abs(vertexNormals[i][2] - g.vertexNormals[i][2]) <= 0.0001);
      }
    }
    return isEqual;
  }

  public void copy(Object o){
    if(o instanceof Geometry){
      Geometry g = (Geometry)o;
      polygonCount = g.polygonCount;
      isBillBoard = g.isBillBoard;
      polygons = new int[g.polygons.length][3];
      vertexNormals = new float[g.vertexNormals.length][3];
      polygonNormals = new float[g.polygonNormals.length][3];
      m = g.m;
      for(int i = 0; i < g.polygons.length; i++){
        polygons[i][0] = g.polygons[i][0];
        polygons[i][1] = g.polygons[i][1];
        polygons[i][2] = g.polygons[i][2];

        polygonNormals[i][0] = g.polygonNormals[i][0];
        polygonNormals[i][1] = g.polygonNormals[i][1];
        polygonNormals[i][2] = g.polygonNormals[i][2];
      }
      for(int i = 0; i < vertexNormals.length; i++){
        vertexNormals[i][0] = g.vertexNormals[i][0];
        vertexNormals[i][1] = g.vertexNormals[i][1];
        vertexNormals[i][2] = g.vertexNormals[i][2];
      }
      
    }
  }

  public void copy(Geometry g){
    polygonCount = g.polygonCount;
    isBillBoard = g.isBillBoard;
    polygons = new int[g.polygons.length][3];
    vertexNormals = new float[g.vertexNormals.length][3];
    polygonNormals = new float[g.polygonNormals.length][3];
    m = g.m;
    for(int i = 0; i < g.polygons.length; i++){
      polygons[i][0] = g.polygons[i][0];
      polygons[i][1] = g.polygons[i][1];
      polygons[i][2] = g.polygons[i][2];

      polygonNormals[i][0] = g.polygonNormals[i][0];
      polygonNormals[i][1] = g.polygonNormals[i][1];
      polygonNormals[i][2] = g.polygonNormals[i][2];
    }
    for(int i = 0; i < vertexNormals.length; i++){
      vertexNormals[i][0] = g.vertexNormals[i][0];
      vertexNormals[i][1] = g.vertexNormals[i][1];
      vertexNormals[i][2] = g.vertexNormals[i][2];
    }
  }
}
