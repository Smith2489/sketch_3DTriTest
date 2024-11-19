//Functions that allows 1D arrays to be treated as vectors
public class VectorOperations{
  public static final double RADS_TO_DEGS = 180/Math.PI;
  //Casts to a string (formatting being for homogeneous coordinates)
  public static String vectorToString(float[] vect1){
    String output = "";
    for(int i = 0; i < vect1.length; i++)
      output+=vect1[i]+"\n";
    return output;
  }
  
  //Casts an array of length 3 to an array of length 4, with a 1 added to the end
  public static float[] from3DVecTo4DVec(float[] vector){
    if(vector.length != 3){
      System.out.println("ERROR: NOT A 3D VECTOR");
      System.exit(1);
      return vector;
    }
    float[] output = {vector[0], vector[1], vector[2], 1};
    return output;
    
  }
  
  //Casts an array of length 3 to an array of length 4 with a value of the programmer's choice added to the end
  public static float[] from3DVecTo4DVec(float[] vector, float w){
    if(vector.length != 3){
      System.out.println("ERROR: NOT A 3D VECTOR");
      System.exit(1);
      return vector;
    }
    float[] output = {vector[0], vector[1], vector[2], w};
    return output;
  }
  
  //Takes in a vector and converts it into a matrix
  public static float[][] fromVecToHomogeneousMatrix(float[] vector){
    float[][] newMatrix = new float[vector.length+1][1];
    for(int i = 0; i < vector.length; i++)
      newMatrix[i][0] = vector[i];
    newMatrix[vector.length][0] = 1;
    return newMatrix;
  }

  //Takes in a vector and converts it into a matrix
  public static float[][] fromVecToHomogeneousMatrix(float[] vector, float w){
    float[][] newMatrix = new float[vector.length+1][1];
    for(int i = 0; i < vector.length; i++)
      newMatrix[i][0] = vector[i];
    newMatrix[vector.length][0] = w;
    return newMatrix;
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
        float output = (float)Math.acos(vectorDotProduct(vector1, vector2)/magnitudeProduct);
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
    System.out.println("ERROR: DIVISION BY ZERO");
    System.exit(1);
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
  
  //Returns a random 2D unit vector
  public static float[] getRandomVector(){
    float angle = (float)(Math.random()*MathExtentions.TAU);
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
