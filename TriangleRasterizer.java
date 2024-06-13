public class TriangleRasterizer{
  private static int fill = 0;
  private static int stroke = 0;
  private static byte hasColour = 3;//1 = hasStroke, 2 = hasFill
  private static float[] zBuff = new float[10000];
  private static int[] frame = new int[10000];
  private static int wid = 0;
  private static int heig = 0;
  private static int background = (255 << 24);
  public static void initBuffers(int width, int height){
     wid = width;
     heig = height;
     zBuff = new float[width*height];
     frame = new int[width*height];
     for(int i = 0; i < width*height; i++){
       zBuff[i] = Float.intBitsToFloat(-1);
       frame[i] = background;
     }
  }
  public static void initBuffers(){
    for(int i = 0; i < zBuff.length; i++){
      zBuff[i] = Float.intBitsToFloat(-1);
      frame[i] = background; 
    }
  }


  public static void background(short r, short g, short b){
    hasColour|=2;
    r = (short)Math.min(Math.max(0, r), 255);
    g = (short)Math.min(Math.max(0, g), 255);
    b = (short)Math.min(Math.max(0, b), 255);
    background = (255 << 24)|(r << 16)|(g << 8)|b;
  }
  public static void background(short r, short g, short b, short a){
    hasColour|=2;
    r = (short)Math.min(Math.max(0, r), 255);
    g = (short)Math.min(Math.max(0, g), 255);
    b = (short)Math.min(Math.max(0, b), 255);
    a = (short)Math.min(Math.max(0, a), 255);
    background = (a << 24)|(r << 16)|(g << 8)|b;
  }
  public static void background(int colour){
    hasColour|=2;
    if(colour < 0 || colour > 255){
      if(colour >= 0 && colour < 16777216)
        background = (255 << 24)|colour;
      else
        background = colour;
    }  
    else
      background = (255 << 24)|(colour << 16)|(colour << 8)|colour;
  }

  public static void fill(short r, short g, short b){
    hasColour|=2;
    r = (short)Math.min(Math.max(0, r), 255);
    g = (short)Math.min(Math.max(0, g), 255);
    b = (short)Math.min(Math.max(0, b), 255);
    fill = (255 << 24)|(r << 16)|(g << 8)|b;
  }
  public static void fill(short r, short g, short b, short a){
    hasColour|=2;
    r = (short)Math.min(Math.max(0, r), 255);
    g = (short)Math.min(Math.max(0, g), 255);
    b = (short)Math.min(Math.max(0, b), 255);
    a = (short)Math.min(Math.max(0, a), 255);
    fill = (a << 24)|(r << 16)|(g << 8)|b;
  }
  public static void fill(int colour){
    hasColour|=2;
    if(colour < 0 || colour > 255){
      if(colour >= 0 && colour < 16777216)
        fill = (255 << 24)|colour;
      else
        fill = colour;
    }  
    else
      fill = (255 << 24)|(colour << 16)|(colour << 8)|colour;
  }
  public static void fill(int colour, short alpha){
    hasColour|=2;
    alpha = (short)Math.min(Math.max(0, alpha), 255);
    colour = (colour & 16777215);
    if(colour > 255)
      fill = (alpha << 24)|colour;
    else
      fill = (alpha << 24)|(colour << 16)|(colour << 8)|colour;
  }
  public static void stroke(short r, short g, short b){
    hasColour|=1;
    r = (short)Math.min(Math.max(0, r), 255);
    g = (short)Math.min(Math.max(0, g), 255);
    b = (short)Math.min(Math.max(0, b), 255);
    stroke = (255 << 24)|(r << 16)|(g << 8)|b;
  }
  public static void stroke(short r, short g, short b, short a){
    hasColour|=1;
    r = (short)Math.min(Math.max(0, r), 255);
    g = (short)Math.min(Math.max(0, g), 255);
    b = (short)Math.min(Math.max(0, b), 255);
    a = (short)Math.min(Math.max(0, a), 255);
    stroke = (a << 24)|(r << 16)|(g << 8)|b;
  }
  public static void stroke(int colour){
    hasColour|=1;
   if(colour < 0 || colour > 255){
     if(colour >= 0 && colour < 16777216)
      stroke = (255 << 24)|colour;
     else
       stroke = colour; 
   }
   else
      stroke = (255 << 24)|(colour << 16)|(colour << 8)|colour;
  }
  public static void stroke(int colour, short alpha){
    hasColour|=1;
    alpha = (short)Math.min(Math.max(0, alpha), 255);
    colour = (colour & 16777215);
   if(colour > 255)
      stroke = (alpha << 24)|colour;
    else
      stroke = (alpha << 24)|(colour << 16)|(colour << 8)|colour;
  }
  public static void noFill(){
    hasColour&=-3;
  }
  public static void noStroke(){
    hasColour&=-2;
  }
  public static void draw(float p1X, float p1Y, float p2X, float p2Y, float p3X, float p3Y){
    if((hasColour & 2) == 2){
      //float[][] points = {{p1X, p1Y, p1Z}, {p2X, p2Y, p2Z}, {p3X, p3Y, p3Z}};
      //float[] centre = {(points[0][0]+points[1][0]+points[2][0])/3, (points[0][1]+points[1][1]+points[2][1])/3};
      //double[] angle = {Math.atan(((double)(p1X-centre[0]))/(p1Y-centre[1])), Math.atan(((double)(p2X-centre[0]))/(p2Y-centre[1])), Math.atan(((double)(p3X-centre[0]))/(p3Y-centre[1]))};
      //for(int i = 0; i < 3; i++){
      //   for(int j = 0; j < 3; j++){
      //      double tempAngle = angle[j];
      //      float tempX = points[j][0];
      //      float tempY = points[j][1];
      //      float tempZ = points[j][2];
      //      if(angle[j] < angle[i]){
      //        angle[j] = angle[i];
      //        angle[i] = tempAngle;
      //        points[j][0] = points[i][0];
      //        points[j][1] = points[i][1];
      //        points[j][2] = points[i][2];
      //        points[i][0] = tempX;
      //        points[i][1] = tempY;
      //        points[i][2] = tempZ;
      //      }
      //   }
      //}
      //p1X = points[0][0];
      //p1Y = points[0][1];
      //p1Z = points[0][2];
      //p2X = points[1][0];
      //p2Y = points[1][1];
      //p2Z = points[1][2];
      //p3X = points[2][0];
      //p3Y = points[2][1];
      //p3Z = points[2][2];
      int max[] = {0, 0};
      int min[] = {0, 0};
      max[0] = (int)Math.min(wid-1, Math.max(p1X, Math.max(p2X, p3X)));
      max[1] = (int)Math.min(heig-1, Math.max(p1Y, Math.max(p2Y, p3Y)));
      min[0] = (int)Math.max(0, Math.min(p1X, Math.min(p2X, p3X)));
      min[1] = (int)Math.max(0, Math.min(p1Y, Math.min(p2Y, p3Y)));
      for(int i = min[0]; i < max[0]; i++){
        for(int j = min[1]; j < max[1]; j++){
           float x = (float)(i+0.5);
           float y = (float)(j+0.5);
           float alpha = BaryCentricCoords.returnAlpha(p1X, p1Y, p2X, p2Y, p3X, p3Y, x, y);
           float beta = BaryCentricCoords.returnBeta(p1X, p1Y, p2X, p2Y, p3X, p3Y, x, y);
           float gamma = BaryCentricCoords.returnGamma(alpha, beta);
           if(alpha >= 0 && beta >= 0 && gamma >= 0)
             if(j >= 0 && j < heig && i >= 0 && i < wid)
               frame[(int)(wid*j+i)] = fill;
         }
       }
     }
      if((hasColour & 1) == 1){
         int[] x = {(int)(p1X), (int)(p2X), (int)(p3X)};
         int[] y = {(int)(p1Y), (int)(p2Y), (int)(p3Y)};
         final int[] endX = {x[1], x[2], x[0]};
         final int[] endY = {y[1], y[2], y[0]};
         for(int i = 0; i < 3; i++){
           int edgeDirX = x[i] < endX[i] ? 1 : -1;
           int edgeDirY = y[i] < endY[i] ? 1 : -1;
           int dx = Math.abs(endX[i]-x[i]);
           int dy = -Math.abs(endY[i]-y[i]);
           int error = dx+dy;
           while(true){
             if(y[i] >= 0 && y[i] < heig && x[i] >= 0 && x[i] < wid)
               frame[wid*y[i]+x[i]] = stroke;
             if(x[i] == endX[i] && y[i] == endY[i])
               break;
             final int error2 = 2*error;
             if(error2 >= dy){
                if(x[i] == endX[i])
                  break;
                error+=dy;
                x[i]+=edgeDirX;
             }
             if(error2 <= dx){
                if(y[i] == endY[i])
                  break;
                error+=dx;
                y[i]+=edgeDirY;
             }

         }
      }
    }
  }
  public static void draw(float p1X, float p1Y, float p1Z, float p2X, float p2Y, float p2Z, float p3X, float p3Y, float p3Z){
    if((hasColour & 2) == 2){
      //float[][] points = {{p1X, p1Y, p1Z}, {p2X, p2Y, p2Z}, {p3X, p3Y, p3Z}};
      //float[] centre = {(points[0][0]+points[1][0]+points[2][0])/3, (points[0][1]+points[1][1]+points[2][1])/3};
      //double[] angle = {Math.atan(((double)(p1X-centre[0]))/(p1Y-centre[1])), Math.atan(((double)(p2X-centre[0]))/(p2Y-centre[1])), Math.atan(((double)(p3X-centre[0]))/(p3Y-centre[1]))};
      //for(int i = 0; i < 3; i++){
      //   for(int j = 0; j < 3; j++){
      //      double tempAngle = angle[j];
      //      float tempX = points[j][0];
      //      float tempY = points[j][1];
      //      float tempZ = points[j][2];
      //      if(angle[j] < angle[i]){
      //        angle[j] = angle[i];
      //        angle[i] = tempAngle;
      //        points[j][0] = points[i][0];
      //        points[j][1] = points[i][1];
      //        points[j][2] = points[i][2];
      //        points[i][0] = tempX;
      //        points[i][1] = tempY;
      //        points[i][2] = tempZ;
      //      }
      //   }
      //}
      //p1X = points[0][0];
      //p1Y = points[0][1];
      //p1Z = points[0][2];
      //p2X = points[1][0];
      //p2Y = points[1][1];
      //p2Z = points[1][2];
      //p3X = points[2][0];
      //p3Y = points[2][1];
      //p3Z = points[2][2];
      int max[] = {0, 0};
      int min[] = {0, 0};
      max[0] = (int)Math.min(wid-1, Math.max(p1X, Math.max(p2X, p3X)));
      max[1] = (int)Math.min(heig-1, Math.max(p1Y, Math.max(p2Y, p3Y)));
      min[0] = (int)Math.max(0, Math.min(p1X, Math.min(p2X, p3X)));
      min[1] = (int)Math.max(0, Math.min(p1Y, Math.min(p2Y, p3Y)));
      for(int i = min[0]; i < max[0]; i++){
        for(int j = min[1]; j < max[1]; j++){
           float x = (float)(i+0.5);
           float y = (float)(j+0.5);
           float alpha = BaryCentricCoords.returnAlpha(p1X, p1Y, p2X, p2Y, p3X, p3Y, x, y);
           float beta = BaryCentricCoords.returnBeta(p1X, p1Y, p2X, p2Y, p3X, p3Y, x, y);
           float gamma = BaryCentricCoords.returnGamma(alpha, beta);
           float z = ((p1Z)*alpha + (p2Z)*beta + (p3Z)*gamma);
           if(alpha >= 0 && beta >= 0 && gamma >= 0){
             if(j >= 0 && j < heig && i >= 0 && i < wid && (z > zBuff[(int)(wid*j+i)] || (Float.isNaN(zBuff[(int)(wid*j+i)])))){
               frame[(int)(wid*j+i)] = fill;
               zBuff[(int)(wid*j+i)] = z;
             }
           }
         }
       }
     }
      if((hasColour & 1) == 1){
         int[] x = {(int)(p1X), (int)(p2X), (int)(p3X)};
         int[] y = {(int)(p1Y), (int)(p2Y), (int)(p3Y)};
         final int[] endX = {x[1], x[2], x[0]};
         final int[] endY = {y[1], y[2], y[0]};
         for(int i = 0; i < 3; i++){
           int edgeDirX = x[i] < endX[i] ? 1 : -1;
           int edgeDirY = y[i] < endY[i] ? 1 : -1;
           int dx = Math.abs(endX[i]-x[i]);
           int dy = -Math.abs(endY[i]-y[i]);
           int error = dx+dy;
           while(true){
             if(y[i] >= 0 && y[i] < heig && x[i] >= 0 && x[i] < wid)
               frame[wid*y[i]+x[i]] = stroke;
             if(x[i] == endX[i] && y[i] == endY[i])
               break;
             final int error2 = 2*error;
             if(error2 >= dy){
                if(x[i] == endX[i])
                  break;
                error+=dy;
                x[i]+=edgeDirX;
             }
             if(error2 <= dx){
                if(y[i] == endY[i])
                  break;
                error+=dx;
                y[i]+=edgeDirY;
             }

         }
      }
    }
  }
  public static void draw(Triangle triangle){
    if((hasColour & 2) == 2){
      //float[][] points = {{p1X, p1Y, p1Z}, {p2X, p2Y, p2Z}, {p3X, p3Y, p3Z}};
      //float[] centre = {(points[0][0]+points[1][0]+points[2][0])/3, (points[0][1]+points[1][1]+points[2][1])/3};
      //double[] angle = {Math.atan(((double)(p1X-centre[0]))/(p1Y-centre[1])), Math.atan(((double)(p2X-centre[0]))/(p2Y-centre[1])), Math.atan(((double)(p3X-centre[0]))/(p3Y-centre[1]))};
      //for(int i = 0; i < 3; i++){
      //   for(int j = 0; j < 3; j++){
      //      double tempAngle = angle[j];
      //      float tempX = points[j][0];
      //      float tempY = points[j][1];
      //      float tempZ = points[j][2];
      //      if(angle[j] < angle[i]){
      //        angle[j] = angle[i];
      //        angle[i] = tempAngle;
      //        points[j][0] = points[i][0];
      //        points[j][1] = points[i][1];
      //        points[j][2] = points[i][2];
      //        points[i][0] = tempX;
      //        points[i][1] = tempY;
      //        points[i][2] = tempZ;
      //      }
      //   }
      //}
      //p1X = points[0][0];
      //p1Y = points[0][1];
      //p1Z = points[0][2];
      //p2X = points[1][0];
      //p2Y = points[1][1];
      //p2Z = points[1][2];
      //p3X = points[2][0];
      //p3Y = points[2][1];
      //p3Z = points[2][2];
      TriangleRasterizer.stroke(triangle.getStroke());
      TriangleRasterizer.fill(triangle.getFill());
      hasColour = (byte)(((triangle.getHasStroke()) ? 1 : 0) | ((triangle.getHasFill()) ? 2 : 0));

      int max[] = {0, 0};
      int min[] = {0, 0};
      max[0] = (int)Math.min(wid-1, Math.max(triangle.getVertices()[0][0], Math.max(triangle.getVertices()[1][0], triangle.getVertices()[2][0])));
      max[1] = (int)Math.min(heig-1, Math.max(triangle.getVertices()[0][1], Math.max(triangle.getVertices()[1][1], triangle.getVertices()[2][1])));
      min[0] = (int)Math.max(0, Math.min(triangle.getVertices()[0][0], Math.min(triangle.getVertices()[1][0], triangle.getVertices()[2][0])));
      min[1] = (int)Math.max(0, Math.min(triangle.getVertices()[0][1], Math.min(triangle.getVertices()[1][1], triangle.getVertices()[2][1])));
      for(int i = min[0]; i < max[0]; i++){
        for(int j = min[1]; j < max[1]; j++){
           float x = (float)(i+0.5);
           float y = (float)(j+0.5);
           float alpha = BaryCentricCoords.returnAlpha(triangle.getVertices()[0][0], triangle.getVertices()[0][1], triangle.getVertices()[1][0], triangle.getVertices()[1][1], triangle.getVertices()[2][0], triangle.getVertices()[2][1], x, y);
           float beta = BaryCentricCoords.returnBeta(triangle.getVertices()[0][0], triangle.getVertices()[0][1], triangle.getVertices()[1][0], triangle.getVertices()[1][1], triangle.getVertices()[2][0], triangle.getVertices()[2][1], x, y);
           float gamma = BaryCentricCoords.returnGamma(alpha, beta);
           float z = ((triangle.getVertices()[0][2])*alpha + (triangle.getVertices()[1][2])*beta + (triangle.getVertices()[2][2])*gamma);
           if(alpha >= 0 && beta >= 0 && gamma >= 0){
             if(j >= 0 && j < heig && i >= 0 && i < wid && (z > zBuff[(int)(wid*j+i)] || (Float.isNaN(zBuff[(int)(wid*j+i)])))){
               frame[(int)(wid*j+i)] = fill;
               zBuff[(int)(wid*j+i)] = z;
             }
           }
         }
       }
     }
      if((hasColour & 1) == 1){
         int[] x = {(int)(triangle.getVertices()[0][0]), (int)(triangle.getVertices()[1][0]), (int)(triangle.getVertices()[2][0])};
         int[] y = {(int)(triangle.getVertices()[0][1]), (int)(triangle.getVertices()[1][1]), (int)(triangle.getVertices()[2][1])};
         final int[] endX = {x[1], x[2], x[0]};
         final int[] endY = {y[1], y[2], y[0]};
         for(int i = 0; i < 3; i++){
           int edgeDirX = x[i] < endX[i] ? 1 : -1;
           int edgeDirY = y[i] < endY[i] ? 1 : -1;
           int dx = Math.abs(endX[i]-x[i]);
           int dy = -Math.abs(endY[i]-y[i]);
           int error = dx+dy;
           while(true){
             if(y[i] >= 0 && y[i] < heig && x[i] >= 0 && x[i] < wid)
              frame[wid*y[i]+x[i]] = stroke;
             if(x[i] == endX[i] && y[i] == endY[i])
               break;
             final int error2 = 2*error;
             if(error2 >= dy){
                if(x[i] == endX[i])
                  break;
                error+=dy;
                x[i]+=edgeDirX;
             }
             if(error2 <= dx){
                if(y[i] == endY[i])
                  break;
                error+=dx;
                y[i]+=edgeDirY;
             }

         }
      }
    }
  }
  
  public static float[] returnDepthBuffer(){
    return zBuff;
  }
  public static int[] returnFrame(){
    return frame;
  }
  public static int returnHeight(){
    return heig;
  }
  public static int returnWidth(){
    return wid;
  }
}
