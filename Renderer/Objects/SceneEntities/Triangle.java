package Renderer.Objects.SceneEntities;
import Actions.StencilAction;
//Class for abstracting triangles
public class Triangle{
  private float[][] vertices = new float[3][4];
  private byte flags = 0; //bit 0 = hasStroke; bit 1 = hasFill
  private int[] colour = {0xFFFFFFFF, 0xFFFFFFFF};
  private float[][] vertexBrightness = {{1, 1, 1, 1}, {1, 1, 1, 1}, {1, 1, 1, 1}};
  private float maxFizzel = 1;
  private float fizzelThreshold = 1.1f;
  private StencilAction stencil = new StencilAction();
  //Default constructor
  public Triangle(){
    for(byte i = 0; i < 3; i++){
       vertices[i][0] = 0;
       vertices[i][1] = 0;
       vertices[i][2] = 0;
       vertices[i][3] = 0;
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
    maxFizzel = 1;
    fizzelThreshold = 1.1f;
    flags = 3;
    colour[0] = 0xFFFFFFFF;
    colour[1] = 0xFFFFFFFF;
    stencil = new StencilAction();
  }
  //Constructor made with 9 points
  public Triangle(float x1, float y1, float z1, float w1, float x2, float y2, float z2, float w2, float x3, float y3, float z3, float w3, int stroke, int fill, boolean hasStroke, boolean hasFill){
    vertices[0][0] = x1;
    vertices[0][1] = y1;
    vertices[0][2] = z1;
    vertices[0][3] = w1;
    vertices[1][0] = x2;
    vertices[1][1] = y2;
    vertices[1][2] = z2;
    vertices[1][3] = w2;
    vertices[2][0] = x3;
    vertices[2][1] = y3;
    vertices[2][2] = z3;
    vertices[2][3] = w3;
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
    maxFizzel = 1;
    fizzelThreshold = 1.1f;
    flags = (byte)(((hasStroke) ? 1 : 0)|((hasFill) ? 2 : 0));
    if((stroke >>> 24) == 0){
      if(stroke <= 0xFF)
        colour[0] = (0xFF000000) | (stroke << 16) | (stroke << 8) | stroke;
      else if(stroke <= 0xFF00)
        colour[0] = ((fill & 0xFF00) << 16) | (stroke << 16) | (stroke << 8) | stroke;
      else
        colour[0] = 0xFF000000 | stroke;
    }
    else
      colour[0] = stroke;
    if((fill >>> 24) == 0){
      if(fill <= 0xFF)
        colour[1] = (0xFF000000) | (fill << 16) | (fill << 8) | fill;
      else if(fill <= 0xFF00)
        colour[1] = ((fill & 0xFF00) << 16) | (fill << 16) | (fill << 8) | fill;
      else
        colour[1] = 0xFF000000 | fill;
    }
    else
      colour[1] = fill;
    stencil = new StencilAction();
  }
  //Constructor with 2D array
  public Triangle(float[][] positions, int stroke, int fill, boolean hasStroke, boolean hasFill){
    for(byte i = 0; i < 3; i++){
       for(byte j = 0; j < 4; j++)
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
    maxFizzel = 1;
    fizzelThreshold = 1.1f;
    flags = (byte)(((hasStroke) ? 1 : 0)|((hasFill) ? 2 : 0));
    if((stroke >>> 24) == 0){
      if(stroke <= 0xFF)
        colour[0] = (0xFF000000) | (stroke << 16) | (stroke << 8) | stroke;
      else if(stroke <= 0xFF00)
        colour[0] = ((fill & 0xFF00) << 16) | (stroke << 16) | (stroke << 8) | stroke;
      else
        colour[0] = 0xFF000000 | stroke;
    }
    else
      colour[0] = stroke;
    if((fill >>> 24) == 0){
      if(fill <= 0xFF)
        colour[1] = (0xFF000000) | (fill << 16) | (fill << 8) | fill;
      else if(fill <= 0xFF00)
        colour[1] = ((fill & 0xFF00) << 16) | (fill << 16) | (fill << 8) | fill;
      else
        colour[1] = 0xFF000000 | fill;
    }
    else
      colour[1] = fill;
    stencil = new StencilAction();
  }

  public void setStencilAction(StencilAction newAction){
    stencil = newAction;
  }

  public StencilAction returnStencilActionPtr(){
    return stencil;
  }

  //Sets the vertices and computes the centre of the triangle with a 2D array
  public void setVertices(float[][] newVertices){
     for(byte i = 0; i < 3; i++){
        for(byte j = 0; j < 4; j++)
           vertices[i][j] = newVertices[i][j]; 
     }
  }
  //Sets the vertices and computes the centre of the triangle with 9 points
  public void setVertices(float x1, float y1, float z1, float w1, float x2, float y2, float z2, float w2, float x3, float y3, float z3, float w3){
    vertices[0][0] = x1;
    vertices[0][1] = y1;
    vertices[0][2] = z1;
    vertices[0][3] = w1;
    vertices[1][0] = x2;
    vertices[1][1] = y2;
    vertices[1][2] = z2;
    vertices[1][3] = w2;
    vertices[2][0] = x3;
    vertices[2][1] = y3;
    vertices[2][2] = z3;
    vertices[2][3] = w3;
  }
  //Stores the stroke
  public void setStroke(int stroke){
    if((stroke >>> 24) == 0){
      if(stroke <= 0xFF)
        colour[0] = (0xFF000000) | (stroke << 16) | (stroke << 8) | stroke;
      else if(stroke <= 0xFF00)
        colour[0] = ((stroke & 0xFF00) << 16) | (stroke << 16) | (stroke << 8) | stroke;
      else
        colour[0] = 0xFF000000 | stroke;
    }
    else
      colour[0] = stroke;
  }
  public void setStroke(int stroke, short alpha){
    stroke&=0xFFFFFF;
    alpha&=0xFF;
    if(stroke <= 0xFF)
      colour[0] = (stroke << 16) | (stroke << 8) | stroke;
    else
      colour[0] = stroke;
    colour[0]|=(alpha << 24);
  }
  public void setStroke(short r, short g, short b){
    r = (short)Math.min(Math.max(0, r), 0xFF);
    g = (short)Math.min(Math.max(0, g), 0xFF);
    b = (short)Math.min(Math.max(0, b), 0xFF);
    colour[0] = 0xFF000000|(r << 16)|(g << 8)|b;
  }
  public void setStroke(short r, short g, short b, short alpha){
    r = (short)Math.min(Math.max(0, r), 0xFF);
    g = (short)Math.min(Math.max(0, g), 0xFF);
    b = (short)Math.min(Math.max(0, b), 0xFF);
    alpha = (short)Math.min(Math.max(0, alpha), 0xFF);
    colour[0] = (alpha << 24)|(r << 16)|(g << 8)|b;
  }
  //Stores the fill
  public void setFill(int fill){
    if((fill >>> 24) == 0){
      if(fill <= 0xFF)
        colour[1] = (0xFF000000) | (fill << 16) | (fill << 8) | fill;
      else if(fill <= 0xFF00)
        colour[1] = ((fill & 0xFF00) << 16) | (fill << 16) | (fill << 8) | fill;
      else
        colour[1] = 0xFF000000 | fill;
    }
    else
      colour[1] = fill;
  }
  public void setFill(int fill, short alpha){
    fill&=0xFFFFFF;
    alpha&=0xFF;
    if(fill <= 0xFF)
      colour[1] = (fill << 16) | (fill << 8) | fill;
    else
      colour[1] = fill;
    colour[1]|=(alpha << 24);

    
  }
  public void setFill(short r, short g, short b){
    r = (short)Math.min(Math.max(0, r), 0xFF);
    g = (short)Math.min(Math.max(0, g), 0xFF);
    b = (short)Math.min(Math.max(0, b), 0xFF);
    colour[1] = 0xFF000000|(r << 16)|(g << 8)|b;
  }
  public void setFill(short r, short g, short b, short alpha){
    r = (short)Math.min(Math.max(0, r), 0xFF);
    g = (short)Math.min(Math.max(0, g), 0xFF);
    b = (short)Math.min(Math.max(0, b), 0xFF);
    alpha = (short)Math.min(Math.max(0, alpha), 0xFF);
    colour[1] = (alpha << 24)|(r << 16)|(g << 8)|b;
  }
  
  public void setAlpha(short alpha, byte index){
    colour[index]&=0xFFFFFF;
    alpha&=0xFF;
    colour[index] = (alpha << 24) | colour[index];
  }

  public void setFizzel(float newMax, float newThreshold){
    maxFizzel = newMax;
    fizzelThreshold = newThreshold;
  }
  public float returnMaxFizzel(){
    return maxFizzel;
  }
  public float returnFizzelThreshold(){
    return fizzelThreshold;
  } 

  //Sets if the triangle has a stroke
  public void setHasStroke(boolean hasStroke){
     if(hasStroke)
       flags|=1;
     else
       flags&=-2;
  }
  //Sets if the triangle has a fill
  public void setHasFill(boolean hasFill){
     if(hasFill)
       flags|=2;
     else
       flags&=-3;
  }
  public void setDepthWrite(boolean depthWrite){
    if(depthWrite)
      flags|=4;
    else
      flags&=-5;
  }
  public void setVertexBrightness(float r, float g, float b, byte index){
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
      vertexBrightness[0][1] = brightnessLevels[0][0];
      vertexBrightness[0][2] = brightnessLevels[0][1];
      vertexBrightness[0][3] = brightnessLevels[0][2];
      vertexBrightness[1][1] = brightnessLevels[1][0];
      vertexBrightness[1][2] = brightnessLevels[1][1];
      vertexBrightness[1][3] = brightnessLevels[1][2];
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
  public float getAverageX(){
    return (vertices[0][0]+vertices[1][0]+vertices[2][0])*0.33333333333333333333333333333333333333f; 
  }
  
  public float getAverageY(){
    return (vertices[0][1]+vertices[1][1]+vertices[2][1])*0.33333333333333333333333333333333333333f;
  }
  
  public float getAverageZ(){
    return (vertices[0][2]+vertices[1][2]+vertices[2][2])*0.33333333333333333333333333333333333333f;
  }

  public float[] getCentroid(){
    float[] centroid = {(vertices[0][0]+vertices[1][0]+vertices[2][0])*0.33333333333333333333333333333333333333f,
                        (vertices[0][1]+vertices[1][1]+vertices[2][1])*0.33333333333333333333333333333333333333f,
                        (vertices[0][2]+vertices[1][2]+vertices[2][2])*0.33333333333333333333333333333333333333f};
    return centroid;
  }
  
  //Returns stroke
  public int getStroke(){
    return colour[0];
  }
  //Returns fill
  public int getFill(){
    return colour[1]; 
  }
  
  //Returns if the triangle has a stroke
  public boolean getHasStroke(){
    return (flags & 1) == 1;
  }
  
  //Returns if the triangle has a fill
  public boolean getHasFill(){
    return (flags & 2) == 2;
  }
  public boolean getHasDepthWrite(){
    return (flags & 4) == 4;
  }
  //Returns vertices as a 2D array
  public float[][] getVertices(){
    return vertices;
  }


  public boolean equals(Object o){
    if(o instanceof Triangle){
      Triangle t = (Triangle)o;
      boolean isEqual = true;
      for(byte i = 0; i < 3; i++){
        for(byte j = 0; j < 4; j++){
          isEqual&=(Math.abs(vertices[i][j] - t.vertices[i][j]) <= 0.0001);
        }
        isEqual&=(Math.abs(vertexBrightness[i][0]-t.vertexBrightness[i][0]) <= 0.0001);
        isEqual&=(Math.abs(vertexBrightness[i][1]-t.vertexBrightness[i][1]) <= 0.0001);
        isEqual&=(Math.abs(vertexBrightness[i][2]-t.vertexBrightness[i][2]) <= 0.0001);
        isEqual&=(Math.abs(vertexBrightness[i][3]-t.vertexBrightness[i][3]) <= 0.0001);
      }
      isEqual&=(colour[0] == t.colour[0]);
      isEqual&=(colour[1] == t.colour[1]);
      isEqual&=(flags == t.flags);
      isEqual&=(Math.abs(maxFizzel - t.maxFizzel) <= 0.0001);
      isEqual&=(Math.abs(fizzelThreshold - t.fizzelThreshold) <= 0.0001);
      isEqual&=(stencil == t.stencil);
      return isEqual;
    }
    else
      return false;
  }
  //Copies one triangle's data to another
  public void copy(Object o){
    if(o instanceof Triangle){
      Triangle t = (Triangle)o;
      for(byte i = 0; i < 3; i++){
        for(byte j = 0; j < 4; j++){
          vertices[i][j] = t.vertices[i][j];
        }
        vertexBrightness[i][0] = t.vertexBrightness[i][0];
        vertexBrightness[i][1] = t.vertexBrightness[i][1];
        vertexBrightness[i][2] = t.vertexBrightness[i][2];
        vertexBrightness[i][3] = t.vertexBrightness[i][3];
      }
      colour[0] = t.colour[0];
      colour[1] = t.colour[1];
      maxFizzel = t.maxFizzel;
      fizzelThreshold = t.fizzelThreshold;
      flags = t.flags;
      stencil = t.stencil;
    }
  }

  public boolean equals(Triangle t){
    boolean isEqual = true;
    for(byte i = 0; i < 3; i++){
      for(byte j = 0; j < 4; j++){
        isEqual&=(Math.abs(vertices[i][j] - t.vertices[i][j]) <= 0.0001);
      }
      isEqual&=(Math.abs(vertexBrightness[i][0]-t.vertexBrightness[i][0]) <= 0.0001);
      isEqual&=(Math.abs(vertexBrightness[i][1]-t.vertexBrightness[i][1]) <= 0.0001);
      isEqual&=(Math.abs(vertexBrightness[i][2]-t.vertexBrightness[i][2]) <= 0.0001);
      isEqual&=(Math.abs(vertexBrightness[i][0]-t.vertexBrightness[i][0]) <= 0.0001);
    }
    isEqual&=(colour[0] == t.colour[0]);
    isEqual&=(colour[1] == t.colour[1]);
    isEqual&=(flags == t.flags);
    isEqual&=(Math.abs(maxFizzel - t.maxFizzel) <= 0.0001);
    isEqual&=(Math.abs(fizzelThreshold - t.fizzelThreshold) <= 0.0001);
    isEqual&=(stencil == t.stencil);
    return isEqual;
  }
  //Copies one triangle's data to another
  public void copy(Triangle t){
    for(byte i = 0; i < 3; i++){
      for(byte j = 0; j < 4; j++){
        vertices[i][j] = t.vertices[i][j];
      }
      vertexBrightness[i][0] = t.vertexBrightness[i][0];
      vertexBrightness[i][1] = t.vertexBrightness[i][1];
      vertexBrightness[i][2] = t.vertexBrightness[i][2];
      vertexBrightness[i][3] = t.vertexBrightness[i][3];
    }
    colour[0] = t.colour[0];
    colour[1] = t.colour[1];
    maxFizzel = t.maxFizzel;
    fizzelThreshold = t.fizzelThreshold;
    flags = t.flags;
    stencil = t.stencil;
  }
  public String toString(){
    String verticesString = "Vertices: ("+vertices[0][0]+", "+vertices[0][1]+", "+vertices[0][2]+")\n";
    verticesString+="          ("+vertices[1][0]+", "+vertices[1][1]+", "+vertices[1][2]+")\n";
    verticesString+="          ("+vertices[2][0]+", "+vertices[2][1]+", "+vertices[2][2]+")\n";
    String coloursString = "Stroke: "+colour[0]+", Fill: "+colour[1];
    return verticesString+coloursString;
  }
}
