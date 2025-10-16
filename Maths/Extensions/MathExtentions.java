package Maths.Extensions;
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
  
  //Returns the remainder of a double-precision floating point number
  public static double doubleMod(double num, double mod){
    int doubleDivMod = (int)(num/mod);
    return num - (doubleDivMod*mod);
  }
  //Returns the remainder of a single-precision floating point number
  public static float floatMod(float num, float mod){
    int floatDivMod = (int)(num/mod);
    return num - (floatDivMod*mod);
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
  
  //Computes the distance between 2 2D points
  public static double dist(double x1, double y1, double x2, double y2){
    return Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
  }
  public static float dist(float x1, float y1, float x2, float y2){
    return (float)(Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1)));
  }
  
  //Computes the distance between 2 3D points
  public static double dist(double x1, double y1, double z1, double x2, double y2, double z2){
    return Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) + (z2-z1)*(z2-z1));
  }
  public static float dist(float x1, float y1, float z1, float x2, float y2, float z2){
    return (float)(Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) + (z2-z1)*(z2-z1)));
  }
  
  
  //Returns an integer power of -1
  public static byte powNegOne(int x){
    return ((x & 1) == 0) ? (byte)1 : (byte)-1;
  }
  
  //Returns the logorithm of any base for a number
  public static double log(double n, double base){
    return Math.log(n)/Math.log(base);
  }

  //Returns the logorithm of any base for a number
  public static float log(float n, float base){
    return (float)(Math.log(n)/Math.log(base));
  }

  //Returns the conversion of an angle from degrees to radians
  public static double degToRad(double angleDegrees){
    return angleDegrees*DEGS_TO_RADS;
  }
  public static float degToRad(float angleDegrees){
    return (float)(angleDegrees*DEGS_TO_RADS);
  }
  //Tests if two lines are intersecting
  public static boolean hasIntersection(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, boolean countEnds){
    //Trying line-line intersection (https://en.wikipedia.org/wiki/Line%E2%80%93line_intersection)
    //Found this through The Coding Train's 2D raycaster video
    float denominator = (x1 - x2)*(y3 - y4) - (x3 - x4)*(y1 - y2);
    if(Math.abs(denominator) > 0.0001){
      float t = ((x1-x3)*(y3-y4) - (y1-y3)*(x3-x4))/denominator;
      float u = -((x1-x2)*(y1-y3) - (y1-y2)*(x1-x3))/denominator;
      if(countEnds)
        return t >= 0 && t <= 1 && u >= 0 && u <= 1;
      else
        return t > 0.0001 && t < 0.9999 && u > 0.0001 && u < 0.9999;
    }
    return false;
  }
  public static double[] returnIntersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4){
    //Trying line-line intersection (https://en.wikipedia.org/wiki/Line%E2%80%93line_intersection)
    //Found this through The Coding Train's 2D raycaster video
    double denominator = (x1 - x2)*(y3 - y4) - (x3 - x4)*(y1 - y2);
    double[] output = {Double.longBitsToDouble(-1), Double.longBitsToDouble(-1)};
    if(Math.abs(denominator) > 0.0001){
      double t = ((x1-x3)*(y3-y4) - (y1-y3)*(x3-x4))/denominator;
      double u = -((x1-x2)*(y1-y3) - (y1-y2)*(x1-x3))/denominator;
      if(t >= 0 && t <= 1 && u >= 0 && u <= 1){
        output[0] = x1+t;
        output[1] = y1+t*(y2 - y1);
      }
    }
    return output;
  }
  public static float[] returnIntersection(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4){
    //Trying line-line intersection (https://en.wikipedia.org/wiki/Line%E2%80%93line_intersection)
    //Found this through The Coding Train's 2D raycaster video
    float denominator = (x1 - x2)*(y3 - y4) - (x3 - x4)*(y1 - y2);
    float[] output = {Float.NaN, Float.NaN};
    if(Math.abs(denominator) > 0.0001){
      float t = ((x1-x3)*(y3-y4) - (y1-y3)*(x3-x4))/denominator;
      float u = -((x1-x2)*(y1-y3) - (y1-y2)*(x1-x3))/denominator;
      if(t >= 0 && t <= 1 && u >= 0 && u <= 1){
        output[0] = x1+t;
        output[1] = y1+t*(y2 - y1);
      }
    }
    return output;
  }
}
