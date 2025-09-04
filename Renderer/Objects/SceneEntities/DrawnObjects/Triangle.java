package Renderer.Objects.SceneEntities.DrawnObjects;
import Renderer.Objects.SceneEntities.DrawnObjects.Parents.FilledParent;
//Class for abstracting triangles
public class Triangle extends FilledParent{
  private float[][] vertices = new float[3][3];
  private float[][] vertexBrightness = {{1, 1, 1, 1}, {1, 1, 1, 1}, {1, 1, 1, 1}};

  //Default constructor
  public Triangle(){
    super();
    for(byte i = 0; i < 3; i++){
       vertices[i][0] = 0;
       vertices[i][1] = 0;
       vertices[i][2] = 0;
    }
    vertexBrightness[0][0] = 1;
    vertexBrightness[0][1] = 1;
    vertexBrightness[0][2] = 1;
    vertexBrightness[0][3] = 1;
    vertexBrightness[1][0] = 1;
    vertexBrightness[1][1] = 1;
    vertexBrightness[1][2] = 1;
    vertexBrightness[1][3] = 1;
    vertexBrightness[2][0] = 1;
    vertexBrightness[2][1] = 1;
    vertexBrightness[2][2] = 1;
    vertexBrightness[2][3] = 1;
  }
  //Constructor made with 9 points
  public Triangle(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, int newStroke, int newFill, boolean hasStroke, boolean hasFill){
    super(newStroke, newFill, hasStroke, hasFill);
    vertices[0][0] = x1;
    vertices[0][1] = y1;
    vertices[0][2] = z1;
    vertices[1][0] = x2;
    vertices[1][1] = y2;
    vertices[1][2] = z2;
    vertices[2][0] = x3;
    vertices[2][1] = y3;
    vertices[2][2] = z3;
    vertexBrightness[0][0] = 1;
    vertexBrightness[0][1] = 1;
    vertexBrightness[0][2] = 1;
    vertexBrightness[0][3] = 1;
    vertexBrightness[1][0] = 1;
    vertexBrightness[1][1] = 1;
    vertexBrightness[1][2] = 1;
    vertexBrightness[1][3] = 1;
    vertexBrightness[2][0] = 1;
    vertexBrightness[2][1] = 1;
    vertexBrightness[2][2] = 1;
    vertexBrightness[2][3] = 1;
  }
  //Constructor with 2D array
  public Triangle(float[][] positions, int newStroke, int newFill, boolean hasStroke, boolean hasFill){
    super(newStroke, newFill, hasStroke, hasFill);
    for(byte i = 0; i < 3; i++){
       for(byte j = 0; j < 3; j++)
         vertices[i][j] = positions[i][j];
      
    }
    vertexBrightness[0][0] = 1;
    vertexBrightness[0][1] = 1;
    vertexBrightness[0][2] = 1;
    vertexBrightness[0][3] = 1;
    vertexBrightness[1][0] = 1;
    vertexBrightness[1][1] = 1;
    vertexBrightness[1][2] = 1;
    vertexBrightness[1][3] = 1;
    vertexBrightness[2][0] = 1;
    vertexBrightness[2][1] = 1;
    vertexBrightness[2][2] = 1;
    vertexBrightness[2][3] = 1;
  }


  //Sets the vertices and computes the centre of the triangle with a 2D array
  public void setVertices(float[][] newVertices){
    for(byte i = 0; i < 3; i++){
      for(byte j = 0; j < 3; j++)
        vertices[i][j] = newVertices[i][j]; 
    }
  }
  //Sets the vertices and computes the centre of the triangle with 9 points
  public void setVertices(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3){
    vertices[0][0] = x1;
    vertices[0][1] = y1;
    vertices[0][2] = z1;
    vertices[1][0] = x2;
    vertices[1][1] = y2;
    vertices[1][2] = z2;
    vertices[2][0] = x3;
    vertices[2][1] = y3;
    vertices[2][2] = z3;
  }

  //Returns vertices as a 2D array
  public float[][] getVertices(){
    return vertices;
  }

  //Returns the position of a specific vertex on a specific axis
  public float getVertexPosition(byte vertex, byte axis){
    if(vertex >= 0 && vertex < 3 && axis >= 0 && axis < 3)
      return vertices[vertex][axis];
    else{
      System.out.println("ERROR: VERTEX DOES NOT EXIST");
      return Float.NaN;
    }
  }

  public void setVertexBrightness(float r, float g, float b, byte index){
    vertexBrightness[index][0] = 1;
    vertexBrightness[index][1] = r;
    vertexBrightness[index][2] = g;
    vertexBrightness[index][3] = b;
  }
  public void setVertexBrightness(float a, float r, float g, float b, byte index){
    vertexBrightness[index][0] = a;
    vertexBrightness[index][1] = r;
    vertexBrightness[index][2] = g;
    vertexBrightness[index][3] = b;
  }
  public void setVertexBrightness(float[] brightnessLevels, byte index){
    if(brightnessLevels.length <= 3){
      vertexBrightness[index][0] = 1;
      vertexBrightness[index][1] = brightnessLevels[0];
      vertexBrightness[index][2] = brightnessLevels[1];
      vertexBrightness[index][3] = brightnessLevels[2];
    }
    else{
      vertexBrightness[index][0] = brightnessLevels[0];
      vertexBrightness[index][1] = brightnessLevels[1];
      vertexBrightness[index][2] = brightnessLevels[2];
      vertexBrightness[index][3] = brightnessLevels[3];
    }
  }

  public void setVertexBrightness(float[][] brightnessLevels){
    if(brightnessLevels[0].length <= 3){
      vertexBrightness[0][0] = 1;
      vertexBrightness[0][1] = brightnessLevels[0][0];
      vertexBrightness[0][2] = brightnessLevels[0][1];
      vertexBrightness[0][3] = brightnessLevels[0][2];
      vertexBrightness[1][0] = 1;
      vertexBrightness[1][1] = brightnessLevels[1][0];
      vertexBrightness[1][2] = brightnessLevels[1][1];
      vertexBrightness[1][3] = brightnessLevels[1][2];
      vertexBrightness[2][0] = 1;
      vertexBrightness[2][1] = brightnessLevels[2][0];
      vertexBrightness[2][2] = brightnessLevels[2][1];
      vertexBrightness[2][3] = brightnessLevels[2][2];
    }
    else{
      vertexBrightness[0][0] = brightnessLevels[0][0];
      vertexBrightness[0][1] = brightnessLevels[0][1];
      vertexBrightness[0][2] = brightnessLevels[0][2];
      vertexBrightness[0][3] = brightnessLevels[0][3];
      vertexBrightness[1][0] = brightnessLevels[1][0];
      vertexBrightness[1][1] = brightnessLevels[1][1];
      vertexBrightness[1][2] = brightnessLevels[1][2];
      vertexBrightness[1][3] = brightnessLevels[1][3];
      vertexBrightness[2][0] = brightnessLevels[2][0];
      vertexBrightness[2][1] = brightnessLevels[2][1];
      vertexBrightness[2][2] = brightnessLevels[2][2];
      vertexBrightness[2][3] = brightnessLevels[2][3];
    }
  }
  
  
  public float[] returnVertexBrightness(byte index){
    return vertexBrightness[index];
  }
  public float[][] returnVertexBrightness(){
    return vertexBrightness;
  }

  //Returns centre position
  public float returnX(){
    return (vertices[0][0]+vertices[1][0]+vertices[2][0])*0.33333333333333333333333333333333333333f; 
  }
  
  public float returnY(){
    return (vertices[0][1]+vertices[1][1]+vertices[2][1])*0.33333333333333333333333333333333333333f;
  }
  
  public float returnZ(){
    return (vertices[0][2]+vertices[1][2]+vertices[2][2])*0.33333333333333333333333333333333333333f;
  }

  public float[] returnPosition(){
    float[] centroid = {(vertices[0][0]+vertices[1][0]+vertices[2][0])*0.33333333333333333333333333333333333333f,
                        (vertices[0][1]+vertices[1][1]+vertices[2][1])*0.33333333333333333333333333333333333333f,
                        (vertices[0][2]+vertices[1][2]+vertices[2][2])*0.33333333333333333333333333333333333333f};
    return centroid;
  }
  

  public boolean equals(Object o){
    if(o instanceof Triangle){
      Triangle t = (Triangle)o;
      boolean isEqual = super.equals(t);
      for(byte i = 0; i < 3; i++){
        for(byte j = 0; j < 3; j++){
          isEqual&=(Math.abs(vertices[i][j] - t.vertices[i][j]) <= EPSILON);
        }
        isEqual&=(Math.abs(vertexBrightness[i][0]-t.vertexBrightness[i][0]) <= EPSILON);
        isEqual&=(Math.abs(vertexBrightness[i][1]-t.vertexBrightness[i][1]) <= EPSILON);
        isEqual&=(Math.abs(vertexBrightness[i][2]-t.vertexBrightness[i][2]) <= EPSILON);
        isEqual&=(Math.abs(vertexBrightness[i][3]-t.vertexBrightness[i][3]) <= EPSILON);
      }
      return isEqual;
    }
    else
      return false;
  }
  //Copies one triangle's data to another
  public void copy(Object o){
    if(o instanceof Triangle){
      Triangle t = (Triangle)o;
      super.copy(t);
      for(byte i = 0; i < 3; i++){
        for(byte j = 0; j < 3; j++){
          vertices[i][j] = t.vertices[i][j];
        }
        vertexBrightness[i][0] = t.vertexBrightness[i][0];
        vertexBrightness[i][1] = t.vertexBrightness[i][1];
        vertexBrightness[i][2] = t.vertexBrightness[i][2];
        vertexBrightness[i][3] = t.vertexBrightness[i][3];
      }
    }
  }

  public boolean equals(Triangle t){
    boolean isEqual = super.equals(t);
    for(byte i = 0; i < 3; i++){
      for(byte j = 0; j < 3; j++){
        isEqual&=(Math.abs(vertices[i][j] - t.vertices[i][j]) <= EPSILON);
      }
      isEqual&=(Math.abs(vertexBrightness[i][0]-t.vertexBrightness[i][0]) <= EPSILON);
      isEqual&=(Math.abs(vertexBrightness[i][1]-t.vertexBrightness[i][1]) <= EPSILON);
      isEqual&=(Math.abs(vertexBrightness[i][2]-t.vertexBrightness[i][2]) <= EPSILON);
      isEqual&=(Math.abs(vertexBrightness[i][0]-t.vertexBrightness[i][0]) <= EPSILON);
    }
    return isEqual;
  }

  //Copies one triangle's data to another
  public void copy(Triangle t){
    super.copy(t);
    for(byte i = 0; i < 3; i++){
      for(byte j = 0; j < 3; j++){
        vertices[i][j] = t.vertices[i][j];
      }
      vertexBrightness[i][0] = t.vertexBrightness[i][0];
      vertexBrightness[i][1] = t.vertexBrightness[i][1];
      vertexBrightness[i][2] = t.vertexBrightness[i][2];
      vertexBrightness[i][3] = t.vertexBrightness[i][3];
    }
  }

  public String toString(){
    String verticesString = "Vertices: ("+vertices[0][0]+", "+vertices[0][1]+", "+vertices[0][2]+")\n";
    verticesString+="          ("+vertices[1][0]+", "+vertices[1][1]+", "+vertices[1][2]+")\n";
    verticesString+="          ("+vertices[2][0]+", "+vertices[2][1]+", "+vertices[2][2]+")\n";
    String coloursString = "Stroke: "+stroke+", Fill: "+fill;
    return verticesString+coloursString;
  }
}