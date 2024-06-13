public class Model{
  private float[][][] polygons = new float[0][3][3];
  private int[][] colours = new int[0][2];
  private int polygonCount = 0;
  private float[] modelPosition = {0, 0, 0};
  private float[] modelAngles = {0, 0, 0};
  private float[] modelScale = {1, 1, 1};
  private boolean[] hasColour = {false, true};
  public Model(){
    modelPosition[0] = 0;
    modelPosition[1] = 0;
    modelPosition[2] = 0;
    modelAngles[0] = 0;
    modelAngles[1] = 0;
    modelAngles[2] = 0;
    modelScale[0] = 1;
    modelScale[1] = 1;
    modelScale[2] = 1;
    polygons = new float[0][3][3];
    colours = new int[0][2];
    polygonCount = 0;
    hasColour[0] = false;
    hasColour[1] = true;
  }
  
  public Model(float[][][] polygonSet, int[][] colourSet, boolean hasStroke, boolean hasFill){
    modelPosition[0] = 0;
    modelPosition[1] = 0;
    modelPosition[2] = 0;
    modelAngles[0] = 0;
    modelAngles[1] = 0;
    modelAngles[2] = 0;
    modelScale[0] = 1;
    modelScale[1] = 1;
    modelScale[2] = 1;
    hasColour[0] = hasStroke;
    hasColour[1] = hasFill;
    polygons = new float[polygonSet.length][polygonSet[0].length][3];
    colours = new int[polygons.length][2];
    for(int i = 0; i < polygons.length; i++){
      colours[i%colourSet.length][0] = colourSet[i%polygons.length][0];
      colours[i%colourSet.length][1] = colourSet[i%polygons.length][1];
      for(int j = 0; j < polygons[i].length; i++){
        polygons[i][j][0] = polygonSet[i][j][0];
        polygons[i][j][1] = polygonSet[i][j][1];
        polygons[i][j][2] = polygonSet[i][j][2];
      }
    }
    polygonCount = polygons.length;
    
  }
  
  public Model(float[][][] polygonSet, int[][] colourSet){
    modelPosition[0] = 0;
    modelPosition[1] = 0;
    modelPosition[2] = 0;
    modelAngles[0] = 0;
    modelAngles[1] = 0;
    modelAngles[2] = 0;
    modelScale[0] = 1;
    modelScale[1] = 1;
    modelScale[2] = 1;
    hasColour[0] = false;
    hasColour[1] = true;
    polygons = new float[polygonSet.length][polygonSet[0].length][3];
    colours = new int[polygons.length][2];
    for(int i = 0; i < polygons.length; i++){
      colours[i%colourSet.length][0] = colourSet[i%polygons.length][0];
      colours[i%colourSet.length][1] = colourSet[i%polygons.length][1];
      for(int j = 0; j < polygons[i].length; j++){
        polygons[i][j][0] = polygonSet[i][j][0];
        polygons[i][j][1] = polygonSet[i][j][1];
        polygons[i][j][2] = polygonSet[i][j][2];
      }
    }
    polygonCount = polygons.length;
  }
  
  public void setPosition(float x, float y, float z){
    modelPosition[0] = x;
    modelPosition[1] = y;
    modelPosition[2] = z;
  }
  public void setAngle(float alpha, float beta, float gamma){
    modelAngles[0] = alpha;
    modelAngles[1] = beta;
    modelAngles[2] = gamma;
  }
  public void setScale(float sX, float sY, float sZ){
    modelScale[0] = sX;
    modelScale[1] = sY;
    modelScale[2] = sZ;
  }
  public void setHasStrokes(boolean hasStroke){
    hasColour[0] = hasStroke;
  }
  public void setHasFill(boolean hasFill){
    hasColour[1] = hasFill;
  }
  public void setPolygons(float[][][] polygonSet, int[][] colourSet){
    polygons = new float[polygonSet.length][polygonSet[0].length][3];
    colours = new int[polygons.length][2];
    for(int i = 0; i < polygons.length; i++){
      colours[i%colourSet.length][0] = colourSet[i%polygons.length][0];
      colours[i%colourSet.length][1] = colourSet[i%polygons.length][1];
      for(int j = 0; j < polygons[i].length; j++){
        polygons[i][j][0] = polygonSet[i][j][0];
        polygons[i][j][1] = polygonSet[i][j][1];
        polygons[i][j][2] = polygonSet[i][j][2];
      }
    }
    polygonCount = polygons.length;
  }
  
  public int returnPolygonCount(){
    return polygonCount;
  }
  public int[][] returnColours(){
    return colours;
  }
  public boolean returnHasStroke(){
    return hasColour[0];
  }
  public boolean returnHasFill(){
    return hasColour[1];
  }
  public float[][][] returnPolygons(){
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
  
}
