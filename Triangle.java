public class Triangle{
  private float[][] vertices = new float[3][3];
  private float avgZ = 0;
  private float avgX = 0;
  private float avgY = 0;
  private boolean[] hasColour = {true, true}; //0 = stroke, 1 = fill
  private int[] colour = {0xFFFFFF, 0xFFFFFF};
  
  public Triangle(){
    for(byte i = 0; i < 3; i++){
       vertices[i][0] = 0;
       vertices[i][1] = 0;
       vertices[i][2] = 0;
    }
    avgZ = 0;
    avgX = 0;
    avgY = 0;
    hasColour[0] = true;
    hasColour[1] = true;
    colour[0] = 0xFFFFFF;
    colour[1] = 0xFFFFFF;
  }
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
    hasColour[0] = hasStroke;
    hasColour[1] = hasFill;
    colour[0] = stroke;
    colour[1] = fill;
  }
  public Triangle(float[][] positions, int stroke, int fill, boolean hasStroke, boolean hasFill){
    for(byte i = 0; i < 3; i++){
       for(byte j = 0; j < 3; j++)
         vertices[i][j] = positions[i][j];
    }
    avgZ = (vertices[0][2]+vertices[1][2]+vertices[2][2])/3;
    avgX = (vertices[0][0]+vertices[1][0]+vertices[2][0])/3;
    avgY = (vertices[0][1]+vertices[1][1]+vertices[2][1])/3;
    hasColour[0] = hasStroke;
    hasColour[1] = hasFill;
    colour[0] = stroke;
    colour[1] = fill;
  }
  public void setVertices(float[][] newVertices){
     for(byte i = 0; i < 3; i++){
        for(byte j = 0; j < 3; j++)
           vertices[i][j] = newVertices[i][j]; 
     }
     avgZ = (vertices[0][2]+vertices[1][2]+vertices[2][2])/3;
     avgX = (vertices[0][0]+vertices[1][0]+vertices[2][0])/3;
     avgY = (vertices[0][1]+vertices[1][1]+vertices[2][1])/3;
  }
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
    avgZ = (vertices[0][2]+vertices[1][2]+vertices[2][2])/3;
    avgX = (vertices[0][0]+vertices[1][0]+vertices[2][0])/3;
    avgY = (vertices[0][1]+vertices[1][1]+vertices[2][1])/3;
  }
  public void setStroke(int stroke){
    colour[0] = stroke;
  }
  public void setStroke(int stroke, short alpha){
    colour[0] = (Math.min(Math.max(0, alpha), 255) << 24)|stroke;
  }
  public void setStroke(short r, short g, short b){
    r = (short)Math.min(Math.max(0, r), 255);
    g = (short)Math.min(Math.max(0, g), 255);
    b = (short)Math.min(Math.max(0, b), 255);
    colour[0] = (r << 16)|(g << 8)|b;
  }
  public void setStroke(short r, short g, short b, short alpha){
    r = (short)Math.min(Math.max(0, r), 255);
    g = (short)Math.min(Math.max(0, g), 255);
    b = (short)Math.min(Math.max(0, b), 255);
    alpha = (short)Math.min(Math.max(0, alpha), 255);
    colour[0] = (alpha << 24)|(r << 16)|(g << 8)|b;
  }
  public void setFill(int fill){
    colour[1] = fill;
  }
  public void setFill(int fill, short alpha){
    colour[1] = (Math.min(Math.max(0, alpha), 255) << 24)|fill;
  }
  public void setFill(short r, short g, short b){
    r = (short)Math.min(Math.max(0, r), 255);
    g = (short)Math.min(Math.max(0, g), 255);
    b = (short)Math.min(Math.max(0, b), 255);
    colour[1] = (r << 16)|(g << 8)|b;
  }
  public void setFill(short r, short g, short b, short alpha){
    r = (short)Math.min(Math.max(0, r), 255);
    g = (short)Math.min(Math.max(0, g), 255);
    b = (short)Math.min(Math.max(0, b), 255);
    alpha = (short)Math.min(Math.max(0, alpha), 255);
    colour[1] = (alpha << 24)|(r << 16)|(g << 8)|b;
  }
  
  public void setHasStroke(boolean hasStroke){
     hasColour[0] = hasStroke; 
  }
  
  public void setHasFill(boolean hasFill){
     hasColour[1] = hasFill; 
  }
  
  public float getAverageX(){
    return avgX; 
  }
  
  public float getAverageY(){
    return avgY;
  }
  
  public float getAverageZ(){
    return avgZ;
  }
  
  public int getStroke(){
    return colour[0];
  }
  public int getFill(){
    return colour[1]; 
  }
  
  
  public boolean getHasStroke(){
    return hasColour[0];
  }
  public boolean getHasFill(){
    return hasColour[1];
  }
  
  public float[][] getVertices(){
    return vertices;
  }
  
  
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
    hasColour[0] = t.hasColour[0];
    hasColour[1] = t.hasColour[1];
  }
}
