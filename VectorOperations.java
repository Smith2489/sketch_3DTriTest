public class VectorOperations{
  public static String vectorToString(float[] vect1){
    String output = "";
    for(int i = 0; i < vect1.length; i++)
      output+=vect1[i]+"\n";
    return output;
  }
  
  public static float[] from3DVecTo4DVec(float[] vector){
    if(vector.length != 3){
      System.out.println("ERROR: NOT A 3D VECTOR");
      return vector;
    }
    float[] output = {vector[0], vector[1], vector[2], 1};
    return output;
    
  }
  
  public static float[] from3DVecTo4DVec(float[] vector, float w){
    if(vector.length != 3){
      System.out.println("ERROR: NOT A 3D VECTOR");
      return vector;
    }
    float[] output = {vector[0], vector[1], vector[2], w};
    return output;
  }
  
  //Vector calculations
  public static float[] vectorCross3D(float[] vect1, float[] vect2){
    if(vect1.length != 3 || vect2.length != 3){
      System.out.println("ERROR: BOTH VECTORS NEED TO BE 3D");
      return vect1;
    }
    float[] output = {0, 0, 0};
    output[0] = vect1[1]*vect2[2] - vect1[2]*vect2[1];
    output[1] = vect1[2]*vect2[0] - vect1[2]*vect2[0];
    output[2] = vect1[0]*vect2[1] - vect1[1]*vect2[0];
    return output;
  }
  public static float[] vectorNormalization(float[] vect1){
    float magnitude = 0;
    for(int i = 0; i < vect1.length; i++)
      magnitude+=(vect1[i]*vect1[i]);
    magnitude = (float)Math.sqrt(magnitude);
    float[] output = new float[vect1.length];
    if(magnitude != 0)
      for(int i = 0; i < vect1.length; i++)
        output[i] = vect1[i]/magnitude;
    else
      for(int i = 0; i < vect1.length; i++)
        output[i] = 0;
    return output;
  }
  public static float vectorMagnitude(float[] vect1){
    float magnitude = 0;
    for(int i = 0; i < vect1.length; i++)
      magnitude+=(vect1[i]*vect1[i]);
    return (float)Math.sqrt(magnitude);
  }
  
  public static float[] vectorAddition(float[] vect1, float[] vect2){
    if(vect1.length == vect2.length){
      float[] output = new float[vect1.length];
      for(int i = 0; i < vect1.length; i++)
        output[i] = vect1[i]+vect2[i];
      return output;
    }
    else{
      System.out.println("ERROR: VECTORS MUST HAVE THE SAME DIMENTIONS");
      float[] output = new float[vect1.length];
      return output;
    }
  }
  
}
