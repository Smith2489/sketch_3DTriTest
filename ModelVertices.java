public class ModelVertices {
    private float[][] points = new float[8][3]; //The vertices of the mesh
    private float[] minPoints = {Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE}; //The minimum x, y, and z positions
    private float[] maxPoints = {Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE}; //The maximum x, y, and z positions
    private float[] modelCentre = {0, 0, 0}; //The actual centre of the model
    private float[][] corners = new float[8][3];
    public ModelVertices(){
        points = new float[8][3];
        for(byte i = 0; i < 3; i++){
            minPoints[i] = -1;
            maxPoints[i] = 1;
            modelCentre[i] = 0;
        }
        float[][] cubeVertices = {{-1, -1, -1}, {-1, 1, -1}, {1, 1, -1}, {1, -1, -1}, 
                                  {-1, -1, 1}, {-1, 1, 1}, {1, 1, 1}, {1, -1, 1}};
        for(byte i = 0; i < 8; i++){
            for(byte j = 0; j < 3; j++)
                points[i][j] = cubeVertices[i][j];
        }
        for(byte i = 0; i < 2; i++){
            corners[i << 2][0] = minPoints[0];
            corners[(i << 2)+1][0] = maxPoints[0];
            corners[(i << 2)+2][0] = maxPoints[0];
            corners[(i << 2)+3][0] = minPoints[0];
        }
        for(byte i = 0; i < 4; i++){
            corners[((i & 2) << 1)+(i & 1)][1] = minPoints[1];
            corners[((i & 2) << 1)+(i & 1)+2][1] = maxPoints[1];
        }
        for(byte i = 0; i < 4; i++){
            corners[i][2] = minPoints[2];
            corners[i+4][2] = maxPoints[2];
        }
    }

    public ModelVertices(float[][] newVertices){
        if(newVertices.length <= 0 || newVertices[0].length < 3){
            System.out.println("ERROR: TOO LITTLE VERTEX DATA");
            System.exit(1);
        }
        minPoints[0] = Float.MAX_VALUE;
        minPoints[1] = Float.MAX_VALUE;
        minPoints[2] = Float.MAX_VALUE;
        maxPoints[0] = Float.MIN_VALUE;
        maxPoints[1] = Float.MIN_VALUE;
        maxPoints[2] = Float.MIN_VALUE;
        modelCentre[0] = 0;
        modelCentre[1] = 0;
        modelCentre[2] = 0;
        points = new float[newVertices.length][3];
        for(int i = 0; i < points.length; i++){
            for(byte j = 0; j < 3; j++){
                points[i][j] = newVertices[i][j];
                if(points[i][j] <= minPoints[j])
                    minPoints[j] = points[i][j];
                if(points[i][j] > maxPoints[j])
                    maxPoints[j] = points[i][j];
            }
        }
        for(int i = 0; i < points.length; i++){
            modelCentre[0]+=points[i][0];
            modelCentre[1]+=points[i][1];
            modelCentre[2]+=points[i][2];
        }
        modelCentre[0]/=points.length;
        modelCentre[1]/=points.length;
        modelCentre[2]/=points.length;
        for(byte i = 0; i < 2; i++){
            corners[i << 2][0] = minPoints[0];
            corners[(i << 2)+1][0] = maxPoints[0];
            corners[(i << 2)+2][0] = maxPoints[0];
            corners[(i << 2)+3][0] = minPoints[0];
        }
        for(byte i = 0; i < 4; i++){
            corners[((i & 2) << 1)+(i & 1)][1] = minPoints[1];
            corners[((i & 2) << 1)+(i & 1)+2][1] = maxPoints[1];
        }
        for(byte i = 0; i < 4; i++){
            corners[i][2] = minPoints[2];
            corners[i+4][2] = maxPoints[2];
        }
    }
    public void setVertices(float[][] newVertices){
        if(newVertices.length <= 0 || newVertices[0].length < 3){
            System.out.println("ERROR: TOO LITTLE VERTEX DATA");
            System.exit(1);
        }
        minPoints[0] = Float.MAX_VALUE;
        minPoints[1] = Float.MAX_VALUE;
        minPoints[2] = Float.MAX_VALUE;
        maxPoints[0] = Float.MIN_VALUE;
        maxPoints[1] = Float.MIN_VALUE;
        maxPoints[2] = Float.MIN_VALUE;
        modelCentre[0] = 0;
        modelCentre[1] = 0;
        modelCentre[2] = 0;
        points = new float[newVertices.length][3];
        for(int i = 0; i < points.length; i++){
            for(byte j = 0; j < 3; j++){
                points[i][j] = newVertices[i][j];
                if(points[i][j] <= minPoints[j])
                    minPoints[j] = points[i][j];
                if(points[i][j] > maxPoints[j])
                    maxPoints[j] = points[i][j];
            }
        }
        for(int i = 0; i < points.length; i++){
            modelCentre[0]+=points[i][0];
            modelCentre[1]+=points[i][1];
            modelCentre[2]+=points[i][2];
        }
        modelCentre[0]/=points.length;
        modelCentre[1]/=points.length;
        modelCentre[2]/=points.length;
        for(byte i = 0; i < 2; i++){
            corners[i << 2][0] = minPoints[0];
            corners[(i << 2)+1][0] = maxPoints[0];
            corners[(i << 2)+2][0] = maxPoints[0];
            corners[(i << 2)+3][0] = minPoints[0];
        }
        for(byte i = 0; i < 4; i++){
            corners[((i & 2) << 1)+(i & 1)][1] = minPoints[1];
            corners[((i & 2) << 1)+(i & 1)+2][1] = maxPoints[1];
        }
        for(byte i = 0; i < 4; i++){
            corners[i][2] = minPoints[2];
            corners[i+4][2] = maxPoints[2];
        }
    }
    public void setVertex(int index, float[] newPosition){
        if(index >= points.length){
          System.out.println("ERROR: INDEX OUT OF RANGE");
          System.exit(1);
        }
        points[index][0] = newPosition[0];
        points[index][1] = newPosition[1];
        points[index][2] = newPosition[2];
        changeBoundingBox();
    }
    public void setVertex(int index, float newX, float newY, float newZ){
        if(index >= points.length){
          System.out.println("ERROR: INDEX OUT OF RANGE");
          System.exit(1);
        }
        points[index][0] = newX;
        points[index][1] = newY;
        points[index][2] = newZ;
        changeBoundingBox();
    }
      //Resizes the bounding box
  private void changeBoundingBox(){
    for(byte i = 0; i < 3; i++){
      minPoints[i] = Float.MAX_VALUE;
      maxPoints[i] = Float.MIN_VALUE;
      modelCentre[i] = 0;
    }
    for(int i = 0; i < points.length; i++){
      for(byte j = 0; j < 3; j++){
        minPoints[j] = Math.min(minPoints[j], points[i][j]);
        maxPoints[j] = Math.max(maxPoints[j], points[i][j]);
        modelCentre[j]+=points[i][j];
      }
    }
    modelCentre[0]/=points.length;
    modelCentre[1]/=points.length;
    modelCentre[2]/=points.length;
    computeBoundingVolume();
  }

  //Computes the vertices for the bounding volume
  private void computeBoundingVolume(){
    for(byte i = 0; i < 2; i++){
        corners[i << 2][0] = minPoints[0];
        corners[(i << 2)+1][0] = maxPoints[0];
        corners[(i << 2)+2][0] = maxPoints[0];
        corners[(i << 2)+3][0] = minPoints[0];
    }
    for(byte i = 0; i < 4; i++){
        corners[((i & 2) << 1)+(i & 1)][1] = minPoints[1];
        corners[((i & 2) << 1)+(i & 1)+2][1] = maxPoints[1];
    }
    for(byte i = 0; i < 4; i++){
        corners[i][2] = minPoints[2];
        corners[i+4][2] = maxPoints[2];
    }
  }

  public float[] returnModelCentre(){
    return modelCentre;
  }
    public float[][] returnVertices(){
        return points;
    }
    public float[] returnMinVertices(){
        return minPoints;
    }
    public float[] returnMaxVertices(){
        return maxPoints;
    }
    //Returns the bounding box
    public float[][] returnBoundingBox(){
        return corners;
    }
    public boolean equals(Object o){
        if(o instanceof ModelVertices){
            ModelVertices mV = (ModelVertices)o;
            if(points.length != mV.points.length)
                return false;
            boolean isEquals = true;
            for(int i = 0; i < points.length; i++){
                isEquals&=(Math.abs(points[i][0] - mV.points[i][0]) <= 0.0001);
                isEquals&=(Math.abs(points[i][1] - mV.points[i][1]) <= 0.0001);
                isEquals&=(Math.abs(points[i][2] - mV.points[i][2]) <= 0.0001);
            }
            isEquals&=(Math.abs(minPoints[0] - mV.minPoints[0]) <= 0.0001);
            isEquals&=(Math.abs(minPoints[1] - mV.minPoints[1]) <= 0.0001);
            isEquals&=(Math.abs(minPoints[2] - mV.minPoints[2]) <= 0.0001);
            isEquals&=(Math.abs(maxPoints[0] - mV.maxPoints[0]) <= 0.0001);
            isEquals&=(Math.abs(maxPoints[1] - mV.maxPoints[1]) <= 0.0001);
            isEquals&=(Math.abs(maxPoints[2] - mV.maxPoints[2]) <= 0.0001);
            return isEquals;
        }
        else
            return false;
    }

    public void copy(Object o){
        if(o instanceof ModelVertices){
            ModelVertices mV = (ModelVertices)o;
            points = new float[mV.points.length][3];
            for(int i = 0; i < points.length; i++){
                points[i][0] = mV.points[i][0];
                points[i][1] = mV.points[i][1];
                points[i][2] = mV.points[i][2];
            }
            minPoints[0] = mV.minPoints[0];
            minPoints[1] = mV.minPoints[1];
            minPoints[2] = mV.minPoints[2];
            maxPoints[0] = mV.maxPoints[0];
            maxPoints[1] = mV.maxPoints[1];
            maxPoints[2] = mV.maxPoints[2];
        }
    }
    public boolean equals(ModelVertices mV){
        if(points.length != mV.points.length)
            return false;
        boolean isEquals = true;
        for(int i = 0; i < points.length; i++){
            isEquals&=(Math.abs(points[i][0] - mV.points[i][0]) <= 0.0001);
            isEquals&=(Math.abs(points[i][1] - mV.points[i][1]) <= 0.0001);
            isEquals&=(Math.abs(points[i][2] - mV.points[i][2]) <= 0.0001);
        }
        isEquals&=(Math.abs(minPoints[0] - mV.minPoints[0]) <= 0.0001);
        isEquals&=(Math.abs(minPoints[1] - mV.minPoints[1]) <= 0.0001);
        isEquals&=(Math.abs(minPoints[2] - mV.minPoints[2]) <= 0.0001);
        isEquals&=(Math.abs(maxPoints[0] - mV.maxPoints[0]) <= 0.0001);
        isEquals&=(Math.abs(maxPoints[1] - mV.maxPoints[1]) <= 0.0001);
        isEquals&=(Math.abs(maxPoints[2] - mV.maxPoints[2]) <= 0.0001);
        return isEquals;
    }

    public void copy(ModelVertices mV){
        points = new float[mV.points.length][3];
        for(int i = 0; i < points.length; i++){
            points[i][0] = mV.points[i][0];
            points[i][1] = mV.points[i][1];
            points[i][2] = mV.points[i][2];
        }
        minPoints[0] = mV.minPoints[0];
        minPoints[1] = mV.minPoints[1];
        minPoints[2] = mV.minPoints[2];
        maxPoints[0] = mV.maxPoints[0];
        maxPoints[1] = mV.maxPoints[1];
        maxPoints[2] = mV.maxPoints[2];
    }
}