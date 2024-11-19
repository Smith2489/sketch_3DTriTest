//Class for abstracting triangles
public class Triangle{
  private float[][] vertices = new float[3][3];
  private float avgZ = 0; //Centre z
  private float avgX = 0; //Centre x
  private float avgY = 0; //Centre y
  private byte flags = 0; //bit 0 = hasStroke; bit 1 = hasFill
  private int[] colour = {0xFFFFFF, 0xFFFFFF};
  //Default constructor
  public Triangle(){
    for(byte i = 0; i < 3; i++){
       vertices[i][0] = 0;
       vertices[i][1] = 0;
       vertices[i][2] = 0;
    }
    avgZ = 0;
    avgX = 0;
    avgY = 0;
    flags = 3;
    colour[0] = 0xFFFFFF;
    colour[1] = 0xFFFFFF;
  }
  //Constructor made with 9 points
  public Triangle(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, int stroke, int fill, boolean hasStroke, boolean hasFill){
    vertices[0][0] = x1;
    vertices[0][1] = y1;
    vertices[0][2] = z1;
    vertices[1][0] = x2;
    vertices[1][1] = y2;
    vertices[1][2] = z2;
    vertices[2][0] = x3;
    vertices[2][1] = y3;
    vertices[2][2] = z3;
    avgZ = (vertices[0][2]+vertices[1][2]+vertices[2][2])/3;
    avgX = (vertices[0][0]+vertices[1][0]+vertices[2][0])/3;
    avgY = (vertices[0][1]+vertices[1][1]+vertices[2][1])/3;
    flags = (byte)(((hasStroke) ? 1 : 0)|((hasFill) ? 2 : 0));
    colour[0] = stroke;
    colour[1] = fill;
  }
  //Constructor with 2D array
  public Triangle(float[][] positions, int stroke, int fill, boolean hasStroke, boolean hasFill){
    for(byte i = 0; i < 3; i++){
       for(byte j = 0; j < 3; j++)
         vertices[i][j] = positions[i][j];
    }
    avgZ = (vertices[0][2]+vertices[1][2]+vertices[2][2])/3;
    avgX = (vertices[0][0]+vertices[1][0]+vertices[2][0])/3;
    avgY = (vertices[0][1]+vertices[1][1]+vertices[2][1])/3;
    flags = (byte)(((hasStroke) ? 1 : 0)|((hasFill) ? 2 : 0));
    colour[0] = stroke;
    colour[1] = fill;
  }
  //Sets the vertices and computes the centre of the triangle with a 2D array
  public void setVertices(float[][] newVertices){
     for(byte i = 0; i < 3; i++){
        for(byte j = 0; j < 3; j++)
           vertices[i][j] = newVertices[i][j]; 
     }
     //Centre computation
     avgZ = (vertices[0][2]+vertices[1][2]+vertices[2][2])/3;
     avgX = (vertices[0][0]+vertices[1][0]+vertices[2][0])/3;
     avgY = (vertices[0][1]+vertices[1][1]+vertices[2][1])/3;
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
    //Centre computation
    avgZ = (vertices[0][2]+vertices[1][2]+vertices[2][2])/3;
    avgX = (vertices[0][0]+vertices[1][0]+vertices[2][0])/3;
    avgY = (vertices[0][1]+vertices[1][1]+vertices[2][1])/3;
  }
  //Stores the stroke
  public void setStroke(int stroke){
    colour[0] = stroke;
  }
  public void setStroke(int stroke, short alpha){
    colour[0] = (Math.min(Math.max(0, alpha), 0xFF) << 24)|stroke;
  }
  public void setStroke(short r, short g, short b){
    r = (short)Math.min(Math.max(0, r), 0xFF);
    g = (short)Math.min(Math.max(0, g), 0xFF);
    b = (short)Math.min(Math.max(0, b), 0xFF);
    colour[0] = (r << 16)|(g << 8)|b;
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
    colour[1] = fill;
  }
  public void setFill(int fill, short alpha){
    colour[1] = (Math.min(Math.max(0, alpha), 0xFF) << 24)|fill;
  }
  public void setFill(short r, short g, short b){
    r = (short)Math.min(Math.max(0, r), 0xFF);
    g = (short)Math.min(Math.max(0, g), 0xFF);
    b = (short)Math.min(Math.max(0, b), 0xFF);
    colour[1] = (r << 16)|(g << 8)|b;
  }
  public void setFill(short r, short g, short b, short alpha){
    r = (short)Math.min(Math.max(0, r), 0xFF);
    g = (short)Math.min(Math.max(0, g), 0xFF);
    b = (short)Math.min(Math.max(0, b), 0xFF);
    alpha = (short)Math.min(Math.max(0, alpha), 0xFF);
    colour[1] = (alpha << 24)|(r << 16)|(g << 8)|b;
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
  
  //Returns centre positions
  public float getAverageX(){
    return avgX; 
  }
  
  public float getAverageY(){
    return avgY;
  }
  
  public float getAverageZ(){
    return avgZ;
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
  
  //Copies one triangle's data to another
  public void copy(Triangle t){
    for(byte i = 0; i < 3; i++){
      for(byte j = 0; j < 3; j++){
        vertices[i][j] = t.vertices[i][j];
      }
    }
    avgX = t.avgX;
    avgY = t.avgY;
    avgZ = t.avgZ;
    colour[0] = t.colour[0];
    colour[1] = t.colour[1];
    flags = t.flags;    
  }
  public String toString(){
    String verticesString = "Vertices: ("+vertices[0][0]+", "+vertices[0][1]+", "+vertices[0][2]+")\n";
    verticesString+="          ("+vertices[1][0]+", "+vertices[1][1]+", "+vertices[1][2]+")\n";
    verticesString+="          ("+vertices[2][0]+", "+vertices[2][1]+", "+vertices[2][2]+")\n";
    String middleString = "Average position: ("+avgX+", "+avgY+", "+avgZ+")\n";
    String coloursString = "Stroke: "+colour[0]+", Fill: "+colour[1];
    return verticesString+middleString+coloursString;
  }
}
