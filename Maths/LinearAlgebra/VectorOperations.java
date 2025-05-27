package Maths.LinearAlgebra;
import Maths.Extensions.*;
//Functions that allows 1D arrays to be treated as vectors
public class VectorOperations{
  public static final float[] ELEM_I = {1, 0, 0};
  public static final float[] ELEM_J = {0, 1, 0};
  public static final float[] ELEM_K = {0, 0, 1};
  public static final double RADS_TO_DEGS = 180/Math.PI; //Conversion from radians to degrees
  public static final double TAU = 2*Math.PI;
  //Casts to a string (formatting being for homogeneous coordinates)
  public static String vectorToString(float[] vect1){
    String output = "";
    for(int i = 0; i < vect1.length; i++)
      output+=vect1[i]+"\n";
    return output;
  }

  //Vector calculations
  
  //Returns the magnitude of the cross product of two vectors
  public static float crossProductMagnitude(float[] vector1, float[] vector2){
    if(vector1.length == vector2.length)
      return (float)(vectorMagnitude(vector1)*vectorMagnitude(vector2)*Math.sin(returnAngle(vector1, vector2, false)));
    if(vector1.length <= 0 || vector2.length <= 0){
      System.out.println("ERROR: VECTORS MUST HAVE LENGTHS GREATER THAN ZERO");
      System.exit(1);
      return 0;
    }
    System.out.println("ERROR: VECTORS MUST HAVE THE SAME NUMBER OF DIMENSIONS");
    System.exit(1);
    return -1;
  }
  
  //Returns the angle of the cosine between two vectors
  public static float returnAngle(float[] vector1, float[] vector2, boolean degrees){
    if(vector1.length == vector2.length){
      float magnitudeProduct = vectorMagnitude(vector1)*vectorMagnitude(vector2);
      if(Math.abs(magnitudeProduct) > 0.0001f){
        float invTheta = vectorDotProduct(vector1, vector2)/magnitudeProduct;
        if(invTheta > 1)
          invTheta = 1;
        else if(invTheta < -1)
          invTheta = -1;
        float output = (float)Math.acos(invTheta);
        if(degrees)
          output = (float)(output*RADS_TO_DEGS);
        return output;
      }
      System.out.println("DIVISION BY ZERO");
      System.exit(1);
    }
    if(vector1.length <= 0 || vector2.length <= 0){
      System.out.println("ERROR: VECTORS MUST HAVE LENGTHS GREATER THAN ZERO");
      System.exit(1);
      return 0;
    }
    System.out.println("ERROR: VECTORS MUST HAVE THE SAME NUMBER OF DIMENSIONS");
    System.exit(1);
    return -1;
  }
  //Returns the angle of the cosine between two unit vectors
  public static float returnAngleUnit(float[] vector1, float[] vector2, boolean degrees){
    if(vector1.length == vector2.length){
      float invTheta = vectorDotProduct(vector1, vector2);
      if(invTheta > 1)
        invTheta = 1;
      else if(invTheta < -1)
        invTheta = -1;
      float output = (float)(Math.acos(invTheta)-0.0001);
      if(degrees)
        output = (float)(output*MathExtentions.RADS_TO_DEGS);
      return output;
    }
    if(vector1.length <= 0 || vector2.length <= 0){
      System.out.println("ERROR: VECTORS MUST HAVE LENGTHS GREATER THAN ZERO");
      System.exit(1);
      return 0;
    }
    System.out.println("ERROR: VECTORS MUST HAVE THE SAME NUMBER OF DIMENSIONS");
    System.exit(1);
    return -1;
  }

  //Computes the angle of the sine between two vectors
  public static float angleCross3D(float[] vector1, float[] vector2, boolean degrees){
    float output = 0;
    if(vector1.length == 3 && vector2.length == 3){
      float magnitudeProduct = vectorMagnitude(vector1)*vectorMagnitude(vector2);
      if(Math.abs(magnitudeProduct) > 0.0001){
        float invTheta = vectorMagnitude(vectorCross3D(vector1, vector2))/magnitudeProduct;
        if(invTheta > 1)
          invTheta = 1;
        else if(invTheta < -1)
          invTheta = -1;
        output = (float)(Math.asin(invTheta));
        if(degrees)
          output*=RADS_TO_DEGS;
      }
      else{
        System.out.println("ERROR: DIVISION BY ZERO");
        System.exit(1);
      }
    }
    else{
      System.out.println("ERROR: VECTORS MUST BE 3-DIMENSIONAL");
      System.exit(1);
    }
    return output;
  }
  //Computes the angle of the sine between two unit vectors
  public static float angleCross3DUnit(float[] vector1, float[] vector2, boolean degrees){
    float output = 0;
    if(vector1.length == 3 && vector2.length == 3){
        float invTheta = vectorMagnitude(vectorCross3D(vector1, vector2));
        if(invTheta > 1)
          invTheta = 1;
        else if(invTheta < -1)
          invTheta = -1;
        output = (float)(Math.asin(invTheta));
        if(degrees)
          output*=RADS_TO_DEGS;
      else{
        System.out.println("ERROR: DIVISION BY ZERO");
        System.exit(1);
      }
    }
    else{
      System.out.println("ERROR: VECTORS MUST BE 3-DIMENSIONAL");
      System.exit(1);
    }
    return output;
  }
  
  //Computes the angle of the tangent between two vectors
  //No unit version exists because the magnitudes cancel each other out
  public static float angleTanVec(float[] vector1, float[] vector2, boolean degrees){
    float output = 0;
    if(vector1.length == 3 && vector2.length == 3){
      float dot = vectorDotProduct(vector1, vector2);
      if(Math.abs(dot) >= 0.0001){
        float crossMag = vectorMagnitude(vectorCross3D(vector1, vector2));
        output = (float)(Math.atan(crossMag/dot));
        if(degrees)
          output*=MathExtentions.RADS_TO_DEGS;
      }
      else{
        System.out.println("ERROR: DIVISION BY ZERO");
        System.exit(1);
      }
    }
    else{
      System.out.println("ERROR: VECTORS MUST BE 3-DIMENSIONAL");
      System.exit(1);
    }
    return output;
  }

  //3D cross product
  public static float[] vectorCross3D(float[] vect1, float[] vect2){
    if(vect1.length != 3 || vect2.length != 3){
      System.out.println("ERROR: BOTH VECTORS NEED TO BE 3D");
      System.exit(1);
      return vect1;
    }
    else if(vect1.length <= 0 || vect2.length <= 0){
      System.out.println("ERROR: VECTORS MUST HAVE LENGTHS GREATER THAN ZERO");
      System.exit(1);
      return new float[0];
    }
    float[] output = {0, 0, 0};
    output[0] = (vect1[1]*vect2[2]) - (vect1[2]*vect2[1])-0.0001f;
    output[1] = (vect1[2]*vect2[0]) - (vect1[0]*vect2[2])-0.0001f;
    output[2] = (vect1[0]*vect2[1]) - (vect1[1]*vect2[0])-0.0001f;
    return output;
  }
  
  //Returns the dot product of two vectors
  public static float vectorDotProduct(float[] vect1, float[] vect2){
    if(vect1.length == vect2.length){
      float dotProduct = 0;
      for(int i = 0; i < vect1.length; i++)
        dotProduct+=(vect1[i]*vect2[i]);
      return dotProduct;
    }
    if(vect1.length <= 0 || vect2.length <= 0){
      System.out.println("ERROR: VECTORS MUST HAVE LENGTHS GREATER THAN ZERO");
      System.exit(1);
      return 0;
    }
    System.out.println("ERROR: VECTORS NEED THE SAME NUMBER OF DIMENSIONS");
    System.exit(1);
    return 0;
  }
  
  //Multiplies a vector by a scalar k
  public static float[] scalarMult(float[] vect1, float k){
    if(vect1.length <= 0){
      System.out.println("ERROR: VECTORS MUST HAVE LENGTHS GREATER THAN ZERO");
      System.exit(1);
      return new float[0];
    }
    float[] outputVec = new float[vect1.length];
    //Runs through each element and multiplies it by k
    for(int i = 0; i < vect1.length; i++)
      outputVec[i] = k*vect1[i];
    return outputVec;
  }
  //Divides a vector by a scalar k
  public static float[] scalarDiv(float[] vect1, float k){
    if(Math.abs(k) > 0.0001){
      //Takes the inverse of k
      k = 1/k;
      float[] outputVec = new float[vect1.length];
      //Runs through each element and multiplies it by 1/k
      for(int i = 0; i < vect1.length; i++)
        outputVec[i] = k*vect1[i];
      return outputVec;
    }
    if(vect1.length <= 0){
      System.out.println("ERROR: VECTORS MUST HAVE LENGTHS GREATER THAN ZERO");
      System.exit(1);
      return new float[0];
    }
    System.out.println("ERROR: DIV BY ZERO");
    System.exit(1);
    return new float[0];
  }
  
  
  //Returns if two vectors are orthogonal
  public static boolean isOrthogonal(float[] vect1, float[] vect2){
    if(vect1.length == vect2.length){
      float dotProduct = 0;
      for(int i = 0; i < vect1.length; i++)
        dotProduct+=(vect1[i]*vect2[i]);
      return (Math.abs(dotProduct) <= 0.0001f);
    }
    if(vect1.length <= 0 || vect2.length <= 0){
      System.out.println("ERROR: VECTORS MUST HAVE LENGTHS GREATER THAN ZERO");
      System.exit(1);
      return false;
    }
    System.out.println("ERROR: VECTORS NEED THE SAME NUMBER OF DIMENSIONS");
    System.exit(1);
    return false;
  }

  //Shrinks the vector down to a length of 1
  public static float[] vectorNormalization(float[] vect1){
     if(vect1.length <= 0){
      System.out.println("ERROR: VECTORS MUST HAVE LENGTHS GREATER THAN ZERO");
      System.exit(1);
      return new float[0];
    }
    float magnitude = 0;
    for(int i = 0; i < vect1.length; i++)
      magnitude+=(vect1[i]*vect1[i]);
    magnitude = (float)Math.sqrt(magnitude);
    float[] output = new float[vect1.length];
    if(magnitude > 0.0001){
      if(Math.abs(magnitude-1) > 0.0001){
        for(int i = 0; i < vect1.length; i++){
          output[i] = vect1[i]/magnitude;
          if(Math.abs(output[i]) < 0.0001f)
            output[i] = 0;
        }

      }
      else
        return vect1;
      return output;
    }
    return output;
  }
  public static float[] vectorNormalization3D(float[] vect1){
    float[] output = {0, 0, 0};
    if(vect1.length == 3){
      float magnitude = (float)Math.sqrt(vect1[0]*vect1[0]+vect1[1]*vect1[1]+vect1[2]*vect1[2]);
      if(magnitude > 0.0001){
        output[0] = vect1[0]/magnitude;
        output[1] = vect1[1]/magnitude;
        output[2] = vect1[2]/magnitude;
      }
    }
    return output;
  }
  
  //Calculates the length of a vector
  public static float vectorMagnitude(float[] vect1){
    if(vect1.length <= 0){
      System.out.println("ERROR: VECTORS MUST HAVE LENGTHS GREATER THAN ZERO");
      System.exit(1);
      return 0;
    }
    float magnitude = 0;
    for(int i = 0; i < vect1.length; i++)
      magnitude+=(vect1[i]*vect1[i]);
    magnitude = (float)Math.sqrt(magnitude);
    magnitude = Math.round(magnitude*10000)*0.0001f;
    return magnitude;
  }
  
  //Adds 2 vectors of the same length together
  public static float[] vectorAddition(float[] vect1, float[] vect2){
    float[] output = new float[vect1.length];
    if(vect1.length == vect2.length){
      for(int i = 0; i < vect1.length; i++)
        output[i] = vect1[i]+vect2[i];
      return output;
    }
    if(vect1.length <= 0 || vect2.length <= 0){
      System.out.println("ERROR: VECTORS MUST HAVE LENGTHS GREATER THAN ZERO");
      System.exit(1);
      return new float[0];
    }
    System.out.println("ERROR: VECTORS MUST HAVE THE SAME DIMENTIONS");
    System.exit(1);
    return output;
  }
  
  
  //Subtracts a vector from another vector of the same length
  public static float[] vectorSubtraction(float[] vect1, float[] vect2){
    float[] output = new float[vect1.length];
    if(vect1.length == vect2.length){
      for(int i = 0; i < vect1.length; i++)
        output[i] = vect1[i]-vect2[i];
      return output;
    }
    if(vect1.length <= 0 || vect2.length <= 0){
      System.out.println("ERROR: VECTORS MUST HAVE LENGTHS GREATER THAN ZERO");
      System.exit(1);
      return new float[0];
    }
    System.out.println("ERROR: VECTORS MUST HAVE THE SAME DIMENTIONS");
    System.exit(1);
    return output;
  }
  
  //Projects u onto a
  public static float[] proj(float[] u, float[] v){
    //Length checks
    if(u.length != v.length){
      System.out.println("ERROR: LENGTHS MUST MATCH");
      System.exit(1);
    }
    if(u.length <= 0 || v.length <= 0){
      System.out.println("ERROR: LENGTHS MUST BE GREATER THAN ONE");
      System.exit(1);
    }
    //Calculating the dot product between u and a
    float numerator = 0;
    for(int i = 0; i < u.length; i++)
      numerator+=(u[i]*v[i]);
    
    //Calculating the magnitude of a
    float denominator = 0;
    for(int i = 0; i < v.length; i++)
      denominator+=(v[i]*v[i]);
    denominator = (float)Math.sqrt(denominator);
    //Initializing an output vector
    float[] output = new float[v.length];
    //If the magntitude of a is greater than a threshold, compute the scalar component, then multiply a by the scalar.
    //Otherwise, return an error
    if(denominator > 0.0001){
      float scalar = numerator/denominator;
      for(int i = 0; i < v.length; i++)
        output[i] = v[i]*scalar;
    }
    else{
      System.out.println("ERROR: MAGNITUDE OF a IS ZERO");
      System.exit(1);
    }
    return output;
  }
  
  //Returns a random 2D unit vector
  public static float[] getRandomVector(){
    float angle = (float)(Math.random()*TAU);
    float[] output = {(float)Math.cos(angle), (float)Math.sin(angle)}; //Stores the vector's elements
    return output;
  }
  //Returns a random n-dimensional unit vector
  public static float[] getRandomVector(int dim){
    float[] output = new float[dim]; //Stores the vector's elements
    float magnitude = 0; //Stores the current norm
    for(int i = 0; i < dim; i++){
      //Gets a random element and then checks if it is around zero; if it is, then it gets hard-set to zero
      output[i] = (float)Math.random()*2-1;
      if(Math.abs(output[i]) <= 0.0001)
        output[i] = 0;
      //Adding the square of the new element to the magntiude
      magnitude+=(output[i]*output[i]);
    }
    //Takes the square root to find the correct norm
    magnitude = (float)Math.sqrt(magnitude);
    //Divides each element by the norm to turn the vector into a unit vector
    if(magnitude > 0.0001)
      for(int i = 0; i < dim; i++)
        output[i]/=magnitude;
    return output;
  }
}