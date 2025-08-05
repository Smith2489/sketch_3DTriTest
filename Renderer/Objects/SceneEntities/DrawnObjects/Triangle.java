package Renderer.Objects.SceneEntities.DrawnObjects;
import Actions.BufferActions.StencilAction;
//Class for abstracting triangles
public class Triangle extends DispParent{
  private float[][] vertices = new float[3][3];
  //Flag bits: bit 1 = hasStroke; bit 2 = hasFill
  private int fill = 0xFFFFFFFF;
  private float[][] vertexBrightness = {{1, 1, 1, 1}, {1, 1, 1, 1}, {1, 1, 1, 1}};
  private float maxFizzel = 1;
  private float fizzelThreshold = 1.1f;
  private StencilAction stencil = new StencilAction();
  //Default constructor
  public Triangle(){
    super((byte)6);
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
    maxFizzel = 1;
    fizzelThreshold = 1.1f;
    fill = 0xFFFFFFFF;
    stencil = new StencilAction();
  }
  //Constructor made with 9 points
  public Triangle(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, int newStroke, int newFill, boolean hasStroke, boolean hasFill){
    super(newStroke, (byte)(((hasStroke) ? 2 : 0)|((hasFill) ? 4: 0)));
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
    maxFizzel = 1;
    fizzelThreshold = 1.1f;

    if((newFill >>> 24) == 0){
      if(newFill <= 0xFF)
        fill = (0xFF000000) | (newFill << 16) | (newFill << 8) | newFill;
      else if(newFill <= 0xFF00)
        fill = ((newFill & 0xFF00) << 16) | ((newFill & 0xFF) << 16) | ((newFill & 0xFF) << 8) | (newFill & 0xFF);
      else
        fill = 0xFF000000 | newFill;
    }
    else
      fill = newFill;
    stencil = new StencilAction();
  }
  //Constructor with 2D array
  public Triangle(float[][] positions, int newStroke, int newFill, boolean hasStroke, boolean hasFill){
    super(newStroke, (byte)(((hasStroke) ? 2 : 0)|((hasFill) ? 4 : 0)));
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
    maxFizzel = 1;
    fizzelThreshold = 1.1f;

    if((newFill >>> 24) == 0){
      if(newFill <= 0xFF)
        fill = (0xFF000000) | (newFill << 16) | (newFill << 8) | newFill;
      else if(newFill <= 0xFF00)
        fill = ((newFill & 0xFF00) << 16) | ((newFill & 0xFF) << 16) | ((newFill & 0xFF) << 8) | (newFill & 0xFF);
      else
        fill = 0xFF000000 | newFill;
    }
    else
      fill = newFill;
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


  //Stores the fill
  public void fill(int rgba){
    if((rgba >>> 24) == 0){
      if(rgba <= 0xFF)
        fill = (0xFF000000) | (rgba << 16) | (rgba << 8) | rgba;
      else if(rgba <= 0xFF00)
        fill = ((rgba & 0xFF00) << 16) | ((rgba & 0xFF) << 16) | ((rgba & 0xFF) << 8) | (rgba & 0xFF);
      else
        fill = 0xFF000000 | rgba;
    }
    else
      fill = rgba;
  }
  public void fill(int rgb, short alpha){
    fill&=0xFFFFFF;
    alpha&=0xFF;
    if(rgb <= 0xFF)
      fill = (rgb << 16) | (rgb << 8) | rgb;
    else
      fill = rgb;
    fill|=(alpha << 24);
  }

  public void setAlphaFill(short alpha){
      fill&=0xFFFFFF;
      alpha&=0xFF;
      fill = (alpha << 24)|fill;
  }

  public void fill(short r, short g, short b){
    r = (short)Math.min(Math.max(0, r), 0xFF);
    g = (short)Math.min(Math.max(0, g), 0xFF);
    b = (short)Math.min(Math.max(0, b), 0xFF);
    fill = 0xFF000000|(r << 16)|(g << 8)|b;
  }
  public void fill(short r, short g, short b, short alpha){
    r = (short)Math.min(Math.max(0, r), 0xFF);
    g = (short)Math.min(Math.max(0, g), 0xFF);
    b = (short)Math.min(Math.max(0, b), 0xFF);
    alpha = (short)Math.min(Math.max(0, alpha), 0xFF);
    fill = (alpha << 24)|(r << 16)|(g << 8)|b;
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
       flags|=2;
     else
       flags&=-3;
  }
  //Sets if the triangle has a fill
  public void setHasFill(boolean hasFill){
     if(hasFill)
       flags|=4;
     else
       flags&=-5;
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
  
  //Returns fill
  public int returnFill(){
    return fill; 
  }
  
  //Returns if the triangle has a stroke
  public boolean getHasStroke(){
    return (flags & 2) == 2;
  }
  
  //Returns if the triangle has a fill
  public boolean getHasFill(){
    return (flags & 4) == 4;
  }

  //Returns vertices as a 2D array
  public float[][] getVertices(){
    return vertices;
  }


  public boolean equals(Object o){
    if(o instanceof Triangle){
      Triangle t = (Triangle)o;
      boolean isEqual = super.equals(t);
      for(byte i = 0; i < 3; i++){
        for(byte j = 0; j < 3; j++){
          isEqual&=(Math.abs(vertices[i][j] - t.vertices[i][j]) <= 0.0001);
        }
        isEqual&=(Math.abs(vertexBrightness[i][0]-t.vertexBrightness[i][0]) <= 0.0001);
        isEqual&=(Math.abs(vertexBrightness[i][1]-t.vertexBrightness[i][1]) <= 0.0001);
        isEqual&=(Math.abs(vertexBrightness[i][2]-t.vertexBrightness[i][2]) <= 0.0001);
        isEqual&=(Math.abs(vertexBrightness[i][3]-t.vertexBrightness[i][3]) <= 0.0001);
      }
      isEqual&=(fill == t.fill);
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
      fill = t.fill;
      maxFizzel = t.maxFizzel;
      fizzelThreshold = t.fizzelThreshold;
      stencil = t.stencil;
    }
  }

  public boolean equals(Triangle t){
    boolean isEqual = super.equals(t);
    for(byte i = 0; i < 3; i++){
      for(byte j = 0; j < 3; j++){
        isEqual&=(Math.abs(vertices[i][j] - t.vertices[i][j]) <= 0.0001);
      }
      isEqual&=(Math.abs(vertexBrightness[i][0]-t.vertexBrightness[i][0]) <= 0.0001);
      isEqual&=(Math.abs(vertexBrightness[i][1]-t.vertexBrightness[i][1]) <= 0.0001);
      isEqual&=(Math.abs(vertexBrightness[i][2]-t.vertexBrightness[i][2]) <= 0.0001);
      isEqual&=(Math.abs(vertexBrightness[i][0]-t.vertexBrightness[i][0]) <= 0.0001);
    }
    isEqual&=(fill == t.fill);
    isEqual&=(Math.abs(maxFizzel - t.maxFizzel) <= 0.0001);
    isEqual&=(Math.abs(fizzelThreshold - t.fizzelThreshold) <= 0.0001);
    isEqual&=(stencil == t.stencil);
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
    fill = t.fill;
    maxFizzel = t.maxFizzel;
    fizzelThreshold = t.fizzelThreshold;
    stencil = t.stencil;
  }
  public String toString(){
    String verticesString = "Vertices: ("+vertices[0][0]+", "+vertices[0][1]+", "+vertices[0][2]+")\n";
    verticesString+="          ("+vertices[1][0]+", "+vertices[1][1]+", "+vertices[1][2]+")\n";
    verticesString+="          ("+vertices[2][0]+", "+vertices[2][1]+", "+vertices[2][2]+")\n";
    String coloursString = "Stroke: "+stroke+", Fill: "+fill;
    return verticesString+coloursString;
  }
}
