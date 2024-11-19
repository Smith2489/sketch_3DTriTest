public class MathExtentions{
  private static final int[] powers10 = {1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000}; //A list of non-negative powers of ten
  public static final double DEGS_TO_RADS = Math.PI/180; //Conversion formula for going from degrees to radians
  public static final double RADS_TO_DEGS = 180/Math.PI;
  public static final double TAU = 2*Math.PI;
  //Rounds a number to the nth digit
  public static double round(double num, int place){
    if(place < 0 || place > 10){
      System.out.println("ERROR: PLACE OUT OF RANGE");
      System.exit(1);
    }
    return Math.round(num*powers10[place])/powers10[place];
  }
  public static float round(float num, int place){
    if(place < 0 || place > 10){
      System.out.println("ERROR: PLACE OUT OF RANGE");
      System.exit(1);
    }
    return (float)Math.round(num*powers10[place])/powers10[place];
  }
  
  //Interpolates between two points
  public static double interpolate(double x1, double x2, int pieces, int howFar){
    if(pieces <= 0 || howFar > pieces){
      System.out.println("ERROR: MUST BE POSITIVE");
      System.exit(1);
    }
    double t = (double)howFar/pieces;
    return (x1 - x2)*t + x2;
  }
  public static float interpolate(float x1, float x2, int pieces, int howFar){
    if(pieces <= 0 || howFar > pieces){
      System.out.println("ERROR: MUST BE POSITIVE");
      System.exit(1);
    }
    float t = (float)howFar/pieces;
    return (x1 - x2)*t + x2;
  }
  
  
  //Computes the value of a new point using some time t and two known points
  //If 0 <= t <= 1, the new point is between the known points.  Otherwise it is outside of them
  public static double linearInterpolate(double point1, double point2, double t){
    return t*(point1-point2)+point2;
  }
  public static float linearInterpolate(float point1, float point2, double t){
    return (float)(t*(point1-point2)+point2);
  }
  public static float linearInterpolate(float point1, float point2, float t){
    return t*(point1-point2)+point2;
  }
  public static long linearInterpolate(long point1, long point2, double t){
    return Math.round(t*(point1-point2)+point2);
  }
  public static int linearInterpolate(int point1, int point2, float t){
    return Math.round(t*(point1-point2)+point2);
  }
  
  
  //Returns an integer power of -1
  public static byte powNegOne(int x){
    return ((x & 1) == 0) ? (byte)1 : (byte)-1;
  }
  
  //Returns the conversion of an angle from degrees to radians
  public static double degToRad(double angleDegrees){
    return angleDegrees*DEGS_TO_RADS;
  }
  public static float degToRad(float angleDegrees){
    return (float)(angleDegrees*DEGS_TO_RADS);
  }
}
