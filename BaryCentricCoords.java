public class BaryCentricCoords{ 
   private static float[] coords = new float[3];
   static float returnAlpha(float x1, float y1, float x2, float y2, float x3, float y3, float pX, float pY){
    float numerator = (y2 - y3)*(pX - x3) + (x3 - x2)*(pY - y3);
    float denominator = (y2 - y3)*(x1 - x3)+(x3 - x2)*(y1 - y3);
    return (denominator != 0) ? numerator/denominator : 0;
  }
  public static float returnBeta(float x1, float y1, float x2, float y2, float x3, float y3, float pX, float pY){
    float numerator = (y3 - y1)*(pX - x3) + (x1 - x3)*(pY - y3);
    float denominator = (y2 - y3)*(x1 - x3)+(x3 - x2)*(y1 - y3);
    return (denominator != 0) ? numerator/denominator : 0;
  }
  public static float returnGamma(float alpha, float beta){
     return 1-alpha-beta; 
  }
  public static float[] returnCoords(float x1, float y1, float x2, float y2, float x3, float y3, float x, float y){
    coords[0] = returnAlpha(x1, y1, x2, y2, x3, y3, x, y);
    coords[1] = returnBeta(x1, y1, x2, y2, x3, y3, x, y);
    coords[3] = returnGamma(coords[0], coords[1]);
    return coords;
  }
}
