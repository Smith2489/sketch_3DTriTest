//Draws triangles to a frame buffer
public class TriangleRasterizer{
  private static int fill = 0; //Fill colour
  private static int stroke = 0; //Outline colour
  private static float[] zBuff = new float[10000]; //Z-buffer
  private static byte[] stencil = new byte[10000]; //Stencil buffer (why is stencil such a weirdly spelt word?) 00000000 = completely black (no draw), 11111111 = completely white (draw), in between values should affect the brightness
  private static int wid = 0; //Width
  private static byte stencilMask = -1; //A mask for the stencil test
  private static int heig = 0; //Height
  private static int background = 0xFF000000; //Background colour
  private static float alphaNorm = 0;
  /*
    bit 0 = stencil test results
    bit 1 = anti aliasing 
    bit 2 = depthWrite
    bit 3 = has stroke
    bit 4 = has fill
  */
  private static byte flags = 0b0001110;
  
  //Initial z-buffer and frame buffer initialization
  public static void initBuffers(int width, int height){
     wid = width;
     heig = height;
     zBuff = new float[width*height];
     stencil = new byte[width*height];
     for(int i = 0; i < width*height; i++){
       zBuff[i] = Float.intBitsToFloat(-1);
       stencil[i] = 0; 
     }
  }
  
  //Solid colour reset
  public static void initBuffers(int[] frame){
    for(int i = 0; i < zBuff.length; i++){
      zBuff[i] = Float.intBitsToFloat(-1);
      frame[i] = background; 
      stencil[i] = 0;
    }
  }
  
  //Reset with a background image
  public static void initBuffers(){
    for(int i = 0; i < zBuff.length; i++){
      zBuff[i] = Float.intBitsToFloat(-1);
      stencil[i] = 0;
    }
  }
  
  //Reseting with coloured lighting
  public static void initBuffers(int[] frame, int lightColour, byte lightIntensity, boolean solid){
    float lightAlpha = (lightIntensity & 255)*0.003921569f;
    if(solid){
      int litBack = (interpolatePixels((lightColour & 0xFFFFFF) | ((((int)lightIntensity) & 0xFF) << 24), background, lightAlpha) & 0xFFFFFF) | (background & 0xFF000000);
      for(int i = 0; i < zBuff.length; i++){
        zBuff[i] = Float.intBitsToFloat(-1);
        frame[i] = litBack; 
        stencil[i] = 0;
      }
    }
    else{
      for(int i = 0; i < zBuff.length; i++){
        zBuff[i] = Float.intBitsToFloat(-1);
        frame[i] = (interpolatePixels((lightColour & 0xFFFFFF) | ((((int)lightIntensity) & 0xFF) << 24), frame[i], lightAlpha) & 0xFFFFFF) | (frame[i] & 0xFF000000);; 
        stencil[i] = 0;
      }
    }
  }

  //Setting the background colour
  public static void background(short r, short g, short b){
    r = (short)Math.min(Math.max(0, r), 0xFF);
    g = (short)Math.min(Math.max(0, g), 0xFF);
    b = (short)Math.min(Math.max(0, b), 0xFF);
    background = 0xFF000000|(r << 16)|(g << 8)|b;
  }
  public static void background(short r, short g, short b, short a){
    r = (short)Math.min(Math.max(0, r), 0xFF);
    g = (short)Math.min(Math.max(0, g), 0xFF);
    b = (short)Math.min(Math.max(0, b), 0xFF);
    a = (short)Math.min(Math.max(0, a), 0xFF);
    background = (a << 24)|(r << 16)|(g << 8)|b;
  }
  
  //A whole boat load of cases to ensure proper setting
  public static void background(int colour){
    if(colour < 0 || colour > 0xFF){
      if(colour >= 0 && colour < 0x1000000)
        background = 0xFF000000|colour;
      else
        background = colour;
    }  
    else
      background = 0xFF000000|(colour << 16)|(colour << 8)|colour;
  }

  //Setting the fill colour of the triangles
  public static void fill(short r, short g, short b){
    flags|=16;
    r = (short)Math.min(Math.max(0, r), 0xFF);
    g = (short)Math.min(Math.max(0, g), 0xFF);
    b = (short)Math.min(Math.max(0, b), 0xFF);
    fill = 0xFF000000|(r << 16)|(g << 8)|b;
  }
  public static void fill(short r, short g, short b, short a){
    flags|=16;
    r = (short)Math.min(Math.max(0, r), 0xFF);
    g = (short)Math.min(Math.max(0, g), 0xFF);
    b = (short)Math.min(Math.max(0, b), 0xFF);
    a = (short)Math.min(Math.max(0, a), 0xFF);
    fill = (a << 24)|(r << 16)|(g << 8)|b;
  }
  public static void fill(int colour){
    flags|=16;
    if(colour < 0 || colour > 0xFF){
      if(colour >= 0 && colour < 0x1000000)
        fill = 0xFF000000|colour;
      else
        fill = colour;
    }  
    else
      fill = 0xFF000000|(colour << 16)|(colour << 8)|colour;
  }
  public static void fill(int colour, short alpha){
    flags|=16;
    alpha = (short)Math.min(Math.max(0, alpha), 255);
    colour = (colour & 0xFFFFFF);
    if(colour > 0xFF)
      fill = (alpha << 24)|colour;
    else
      fill = (alpha << 24)|(colour << 16)|(colour << 8)|colour;
  }
  
  //Setting the stroke colour of the triangles
  public static void stroke(short r, short g, short b){
    flags|=8;
    r = (short)Math.min(Math.max(0, r), 0xFF);
    g = (short)Math.min(Math.max(0, g), 0xFF);
    b = (short)Math.min(Math.max(0, b), 0xFF);
    stroke = 0xFF000000|(r << 16)|(g << 8)|b;
  }
  public static void stroke(short r, short g, short b, short a){
    flags|=8;
    r = (short)Math.min(Math.max(0, r), 0xFF);
    g = (short)Math.min(Math.max(0, g), 0xFF);
    b = (short)Math.min(Math.max(0, b), 0xFF);
    a = (short)Math.min(Math.max(0, a), 0xFF);
    stroke = (a << 24)|(r << 16)|(g << 8)|b;
  }
  public static void stroke(int colour){
    flags|=8;
   if(colour < 0 || colour > 0xFF){
     if(colour >= 0 && colour < 0x1000000)
      stroke = 0xFF000000|colour;
     else
       stroke = colour; 
   }
   else
      stroke = 0xFF000000|(colour << 16)|(colour << 8)|colour;
  }
  public static void stroke(int colour, short alpha){
    flags|=8;
    alpha = (short)Math.min(Math.max(0, alpha), 0xFF);
    colour = (colour & 0xFFFFFF);
   if(colour > 0xFF)
      stroke = (alpha << 24)|colour;
    else
      stroke = (alpha << 24)|(colour << 16)|(colour << 8)|colour;
  }
  
  //Selecting having no fill
  public static void noFill(){
    flags&=-17;
  }
  //Selecting having no stroke
  public static void noStroke(){
    flags&=-9;
  }
  public static void setAntiAlias(boolean newAntiAlias){
    // antiAliasing = newAntiAlias;
    if(newAntiAlias)
      flags|=2;
    else
      flags&=-3;
  }

  public static void setDepthWrite(boolean newDepthWrite){
    // depthWrite = newDepthWrite;
    if(newDepthWrite)
      flags|=4;
    else
      flags&=-5;
  }
  
  //Determines if the x and y are valid and plots a pixel
  public static void setPixelFill(int[] frame, int x, int y){
    if((flags & 16) == 16 && x >= 0 && x < wid && y >= 0 && y < heig)
      //Interpolate between the fill and the current pixel if the fill's alpha channel is less than 255. Otherwise, simply overwrite the current pixel with the fill
      if((fill >>> 24) < 0xFF)
        frame[y*wid+x] = interpolatePixels(fill, frame[y*wid+x]);
      else
        frame[y*wid+x] = fill;
  }
  public static void setPixelStroke(int frame[], int x, int y){
    if((flags & 8) == 8 && x >= 0 && x < wid && y >= 0 && y < heig)
    //Interpolate between the stroke and the current pixel if the stroke's alpha channel is less than 255. Otherwise, simply overwrite the current pixel with the stroke
      if((stroke >>> 24) < 0xFF)
        frame[y*wid+x] = interpolatePixels(stroke, frame[y*wid+x]);
      else
        frame[y*wid+x] = stroke;
  }
  
  //Modifies the stencil buffer
  public static void writeToStencil(int index, byte newVal){
    if(index >= 0 && index < stencil.length)
      stencil[index] = newVal;
  }
  public static void writeToStence(byte[] newStencil){
    if(newStencil.length != stencil.length){
      System.out.println("ERROR: STENCILS MUST BE THE SAME SIZE");
      return;
    }
    for(int i = 0; i < stencil.length; i++)
      stencil[i] = newStencil[i];
  }
  
  //Modifies the stencil mask
  public static void setStencilMask(byte newMask){
    stencilMask = newMask;
  }
  //Rendering a triangle with 2D points
  public static void draw(int[] frame, float p1X, float p1Y, float p2X, float p2Y, float p3X, float p3Y){
    //Fill for the triangle
    //Setting up the bounding box
    int[][] screenBounds = {{0x80000000, 0x7FFFFFF}, {0x80000000, 0x7FFFFFF}};
    //Makes forming the BB and rasterizing the triangle easier
    float[][] poses = {{p1X, p1Y}, 
                       {p2X, p2Y}, 
                       {p3X, p3Y}};
    //Constructing the triangle's bounding box
    screenBounds[0][0] = Math.round(Math.max(0, Math.min(poses[0][0], Math.min(poses[1][0], poses[2][0]))));
    screenBounds[0][1] = Math.round(Math.min(wid, Math.max(poses[0][0], Math.max(poses[1][0], poses[2][0]))));
    screenBounds[1][0] = Math.round(Math.max(0, Math.min(poses[0][1], Math.min(poses[1][1], poses[2][1]))));
    screenBounds[1][1] = Math.round(Math.min(heig, Math.max(poses[0][1], Math.max(poses[1][1], poses[2][1]))));
    int maxX = screenBounds[0][1];
    int minX = screenBounds[0][0];
  
      
    //Used for centring a pixel
    float y = 0;
      
    //The edges that will be iterated over
    float[] interpolatedEdges = {0, 0}; //i0 = lowest-mid, i1 = mid-highest, i2 = highest-lowest
    //Filling in the triangle
    if((flags & 16) == 16){
      
      //Iterating over the BB
      for(int i = screenBounds[1][0]; i < screenBounds[1][1]; i++){
        byte currentEdge = 1;
        y = i+0.5f;
        //Holds how far along each edge the program is at the current scanline
        float[] t = {-1, -1, -1};
        //Computes time t for the two edges that go to the middle vertex at the current scanline
        float denominator = (poses[1][1]-poses[0][1]-0.00001f);
        if(Math.abs(denominator) > 0.0001)
          t[2] = (poses[1][1]-y)/denominator;
        denominator = (poses[2][1]-poses[1][1])-0.00001f;
        if(Math.abs(denominator) > 0.0001)
          t[0] = (poses[2][1]-y)/denominator;
        denominator = (poses[2][1]-poses[0][1])-0.00001f;
        if(Math.abs(denominator) > 0.0001)
          t[1] = (poses[2][1]-y)/denominator;
        
        //Uses the times to interpolate along the edges
        if(t[2] >= 0f && t[2] <= 1f){
          interpolatedEdges[currentEdge] = (poses[0][0]-poses[1][0])*t[2]+poses[1][0]-0.0001f;
          currentEdge--;
        }
        if(t[0] >= 0f && t[0] <= 1f){
          interpolatedEdges[currentEdge] = (poses[1][0]-poses[2][0])*t[0]+poses[2][0]-0.0001f;
          currentEdge--;
        }
        if(t[1] >= 0f && t[1] <= 1f)
          interpolatedEdges[0] = (poses[0][0]-poses[2][0])*t[1]+poses[2][0]-0.0001f;
        //Determines the min and max x-positions of the current scanline
        screenBounds[0][0] = Math.round(Math.max(minX, Math.min(interpolatedEdges[0], interpolatedEdges[1])));
        screenBounds[0][1] = Math.round(Math.min(maxX, Math.max(interpolatedEdges[0], interpolatedEdges[1])));
        //Filling the scanline
        for(int j = screenBounds[0][0]; j < screenBounds[0][1]; j++){

          int pixelPos = wid*i+j;
          //Interpolating the current pixel and the fill if the fill's alpha is less than 255. Otherwise, overwrite the current pixel's data with the fill
          if((fill >>> 24) < 0xFF)
            frame[pixelPos] = interpolatePixels(fill, frame[pixelPos]);
          else
            frame[pixelPos] = fill;
         }
       }
     }
     //Drawing the outline
     if((flags & 8) == 8){
       //Setting the start and end points
       int[] xE = {Math.round(p1X), Math.round(p2X), Math.round(p3X)};
       int[] yE = {Math.round(p1Y), Math.round(p2Y), Math.round(p3Y)};
       final int[] endX = {xE[1], xE[2], xE[0]};
       final int[] endY = {yE[1], yE[2], yE[0]};
       //Drawing each side using Bresenham's line algorithm
       for(byte i = 0; i < 3; i++)
         drawLine(frame, wid, heig, xE[i], yE[i], endX[i], endY[i], stroke);
    }
    antiAlias(frame, poses, interpolatedEdges, y, screenBounds, minX, maxX);
  }
  //Drawing a triangle with 3D points (points directly in parametres)
  public static void draw(int[] frame, float p1X, float p1Y, float p1Z, float p2X, float p2Y, float p2Z, float p3X, float p3Y, float p3Z){
    //Setting up the bounding box
    int[][] screenBounds = {{0x80000000, 0x7FFFFFF}, {0x80000000, 0x7FFFFFF}};
    //Makes forming the BB and rasterizing the triangle easier
    float[][] poses = {{p1X, p1Y}, 
                       {p2X, p2Y}, 
                       {p3X, p3Y}};
    //Constructing the triangle's bounding box
    screenBounds[0][0] = Math.round(Math.max(0, Math.min(poses[0][0], Math.min(poses[1][0], poses[2][0]))));
    screenBounds[0][1] = Math.round(Math.min(wid, Math.max(poses[0][0], Math.max(poses[1][0], poses[2][0]))));
    screenBounds[1][0] = Math.round(Math.max(0, Math.min(poses[0][1], Math.min(poses[1][1], poses[2][1]))));
    screenBounds[1][1] = Math.round(Math.min(heig, Math.max(poses[0][1], Math.max(poses[1][1], poses[2][1]))));
    int maxX = screenBounds[0][1];
    int minX = screenBounds[0][0];
    //Weights contributed to a pixel by the vertices
    float alpha = 0;
    float beta = 0;
    float gamma = 0;
      
    //Used for centring a pixel
    float x = 0;
    float y = 0;
      
    //The edges that will be iterated over
    float[] interpolatedEdges = {0, 0}; //i0 = lowest-mid, i1 = mid-highest, i2 = highest-lowest
    //Filling in the triangle
    if((flags & 16) == 16){
      
      //Iterating over the BB
      for(int i = screenBounds[1][0]; i < screenBounds[1][1]; i++){
        byte currentEdge = 1;
        y = i+0.5f;
        //Holds how far along each edge the program is at the current scanline
        float[] t = {-1, -1, -1};
        //Computes time t for the two edges that go to the middle vertex at the current scanline
        float denominator = (poses[1][1]-poses[0][1]-0.00001f);
        if(Math.abs(denominator) > 0.0001)
          t[2] = (poses[1][1]-y)/denominator;
        denominator = (poses[2][1]-poses[1][1])-0.00001f;
        if(Math.abs(denominator) > 0.0001)
          t[0] = (poses[2][1]-y)/denominator;
        denominator = (poses[2][1]-poses[0][1])-0.00001f;
        if(Math.abs(denominator) > 0.0001)
          t[1] = (poses[2][1]-y)/denominator;
        
        //Uses the times to interpolate along the edges
        if(t[2] >= 0f && t[2] <= 1f){
          interpolatedEdges[currentEdge] = (poses[0][0] - poses[1][0])*t[2]+poses[1][0]-0.0001f;
          currentEdge--;
        }
        if(t[0] >= 0f && t[0] <= 1f){
          interpolatedEdges[currentEdge] = (poses[1][0]-poses[2][0])*t[0]+poses[2][0]-0.0001f;
          currentEdge--;
        }
        if(t[1] >= 0f && t[1] <= 1f)
          interpolatedEdges[0] = (poses[0][0]-poses[2][0])*t[1]+poses[2][0]-0.0001f;
        //Determines the min and max x-positions of the current scanline
        screenBounds[0][0] = Math.round(Math.max(minX, Math.min(interpolatedEdges[0], interpolatedEdges[1])));
        screenBounds[0][1] = Math.round(Math.min(maxX, Math.max(interpolatedEdges[0], interpolatedEdges[1])));
        //Filling the scanline
        for(int j = screenBounds[0][0]; j < screenBounds[0][1]; j++){
          //Centring the pixel
          x = j+0.5f;
          //Calculating the weight each vertex contributes to the pixel
          alpha = returnAlpha(poses[0][0], poses[0][1], poses[1][0], poses[1][1], poses[2][0], poses[2][1], x, y);
          beta = returnBeta(poses[0][0], poses[0][1], poses[1][0], poses[1][1], poses[2][0], poses[2][1], x, y);
          gamma = returnGamma(alpha, beta);
          //Plotting the pixel
          float z = 1/(p1Z*alpha + p2Z*beta + p3Z*gamma); //Barycentric z
          //if(!depthWrite)
          z*=(((flags & 4) >>> 1)-1);
          int pixelPos = wid*i+j;
          //For when the current triangle is closest at the current pixel than any previous triangle
          if(stencil[pixelPos] == 0 && ((flags & 4) == 0 && (z > zBuff[pixelPos] || zBuff[pixelPos] >= 0) || (flags & 4) == 4 && z < zBuff[pixelPos] || Float.isNaN(zBuff[pixelPos]))){
            //Interpolating the current pixel and the fill if the fill's alpha is less than 255. Otherwise, overwrite the current pixel's data with the fill
            if((fill >>> 24) < 0xFF)
              frame[pixelPos] = interpolatePixels(fill, frame[pixelPos]);
            else
              frame[pixelPos] = fill;
            zBuff[pixelPos] = z;
          }
          //For when there were triangles drawn at the current pixel that were closer than the current triangle
          else
           //Interpolate between the fill and the current pixel if both's alpha channels are less than 255. Otherwise, skip
           if((fill >>> 24) < 0xFF && (frame[pixelPos] >>> 24) < 0xFF)
             frame[pixelPos] = interpolatePixels(frame[pixelPos], fill, pixelPos);
         }
       }
     }
     //Drawing the outline
     if((flags & 8) == 8){
       //Setting up the endpoints of each side
       int[] xE = {Math.round(p1X), Math.round(p2X), Math.round(p3X)};
       int[] yE = {Math.round(p1Y), Math.round(p2Y), Math.round(p3Y)};
       final int[] endX = {xE[1], xE[2], xE[0]};
       final int[] endY = {yE[1], yE[2], yE[0]};
       //Drawing each side with Bresenham's line algorithm
       for(byte i = 0; i < 3; i++)
         drawLine(frame, wid, heig, xE[i], yE[i], endX[i], endY[i], stroke);
    }
    antiAlias(frame, poses, interpolatedEdges, y, screenBounds, minX, maxX);
  }
  
  public static void draw(int[] frame, Triangle triangle){
    //Setting up the colour
    TriangleRasterizer.stroke(triangle.getStroke());
    TriangleRasterizer.fill(triangle.getFill());
    flags = (byte)(((triangle.getHasStroke()) ? flags|8 : flags&-9));
    flags = (byte)(((triangle.getHasFill()) ? flags|16 : flags&-17));
    
    //Setting up the bounding box
    int[][] screenBounds = {{0x80000000, 0x7FFFFFF}, {0x80000000, 0x7FFFFFF}};
    //Makes forming the BB and rasterizing the triangle easier
    float[][] poses = {{triangle.getVertices()[0][0], triangle.getVertices()[0][1], triangle.getVertices()[0][2]}, 
                        {triangle.getVertices()[1][0], triangle.getVertices()[1][1], triangle.getVertices()[1][2]}, 
                        {triangle.getVertices()[2][0], triangle.getVertices()[2][1], triangle.getVertices()[2][2]}};
    //Constructing the triangle's bounding box
    screenBounds[0][0] = Math.round(Math.max(0, Math.min(poses[0][0], Math.min(poses[1][0], poses[2][0]))));
    screenBounds[0][1] = Math.round(Math.min(wid, Math.max(poses[0][0], Math.max(poses[1][0], poses[2][0]))));
    screenBounds[1][0] = Math.round(Math.max(0, Math.min(poses[0][1], Math.min(poses[1][1], poses[2][1]))));
    screenBounds[1][1] = Math.round(Math.min(heig, Math.max(poses[0][1], Math.max(poses[1][1], poses[2][1]))));
    int maxX = screenBounds[0][1];
    int minX = screenBounds[0][0];
    //Weights contributed to a pixel by the vertices
    float alpha = 0;
    float beta = 0;
    float gamma = 0;
      
    //Used for centring a pixel
    float x = 0;
    float y = 0;
      
    //The edges that will be iterated over
    float[] interpolatedEdges = {0, 0}; //i0 = lowest-mid, i1 = mid-highest, i2 = highest-lowest
    //Filling in the triangle
    if((flags & 16) == 16){
      
      //Iterating over the BB
      for(int i = screenBounds[1][0]; i < screenBounds[1][1]; i++){
        byte currentEdge = 1;
        y = i+0.5f;
        //Holds how far along each edge the program is at the current scanline
        float[] t = {-1, -1, -1};
        //Computes time t for the two edges that go to the middle vertex at the current scanline
        float denominator = (poses[1][1]-poses[0][1]-0.00001f);
        if(Math.abs(denominator) > 0.0001)
          t[2] = (poses[1][1]-y)/denominator;
        denominator = (poses[2][1]-poses[1][1])-0.00001f;
        if(Math.abs(denominator) > 0.0001)
          t[0] = (poses[2][1]-y)/denominator;
        denominator = (poses[2][1]-poses[0][1])-0.00001f;
        if(Math.abs(denominator) > 0.0001)
          t[1] = (poses[2][1]-y)/denominator;
        
        //Uses the times to interpolate along the edges
        if(t[2] >= 0f && t[2] <= 1f){
          interpolatedEdges[currentEdge] = (poses[0][0]-poses[1][0])*t[2]+poses[1][0]-0.0001f;
          currentEdge--;
        }
        if(t[0] >= 0f && t[0] <= 1f){
          interpolatedEdges[currentEdge] = (poses[1][0]-poses[2][0])*t[0]+poses[2][0]-0.0001f;
          currentEdge--;
        }
        if(t[1] >= 0f && t[1] <= 1f)
          interpolatedEdges[0] = (poses[0][0]-poses[2][0])*t[1]+poses[2][0]-0.0001f;
        //Determines the min and max x-positions of the current scanline
        screenBounds[0][0] = Math.round(Math.max(minX, Math.min(interpolatedEdges[0], interpolatedEdges[1])));
        screenBounds[0][1] = Math.round(Math.min(maxX, Math.max(interpolatedEdges[0], interpolatedEdges[1])));
        //Filling the scanline
        for(int j = screenBounds[0][0]; j < screenBounds[0][1]; j++){
          //Centring the pixel
          x = j+0.5f;
          //Calculating the weight each vertex contributes to the pixel
          alpha = returnAlpha(poses[0][0], poses[0][1], poses[1][0], poses[1][1], poses[2][0], poses[2][1], x, y);
          beta = returnBeta(poses[0][0], poses[0][1], poses[1][0], poses[1][1], poses[2][0], poses[2][1], x, y);
          gamma = returnGamma(alpha, beta);
          //Plotting the pixel
          float z = 1/(poses[0][2]*alpha + poses[1][2]*beta + poses[2][2]*gamma); //Barycentric z
          //if(!depthWrite)
          z*=(((flags & 4) >>> 1)-1);
          int pixelPos = wid*i+j;
          //For when the current triangle is closest at the current pixel than any previous triangle
          if(stencil[pixelPos] == 0 && ((flags & 4) == 0 && (z > -zBuff[pixelPos] || zBuff[pixelPos] >= 0) || (flags & 4) == 4 && z < zBuff[pixelPos] || Float.isNaN(zBuff[pixelPos]))){
            //Interpolating the current pixel and the fill if the fill's alpha is less than 255. Otherwise, overwrite the current pixel's data with the fill
            if((fill >>> 24) < 0xFF)
              frame[pixelPos] = interpolatePixels(fill, frame[pixelPos]);
            else
              frame[pixelPos] = fill;
            zBuff[pixelPos] = z;
          }
          //For when there were triangles drawn at the current pixel that were closer than the current triangle
          else
           //Interpolate between the fill and the current pixel if both's alpha channels are less than 255. Otherwise, skip
           if((fill >>> 24) < 0xFF && (frame[pixelPos] >>> 24) < 0xFF)
             frame[pixelPos] = interpolatePixels(frame[pixelPos], fill, pixelPos);
         }
       }
     }
     //Drawing the outline
     if((flags & 8) == 8){
       for(byte i = 0; i < 3; i++)
         drawLine(frame, wid, heig, Math.round(triangle.getVertices()[i][0]), Math.round(triangle.getVertices()[i][1]), triangle.getVertices()[i][2]+0.0004f, Math.round(triangle.getVertices()[(i+1)%3][0]), Math.round(triangle.getVertices()[(i+1)%3][1]), triangle.getVertices()[(i+1)%3][2]+0.0004f, stroke, (flags & 4) == 0);
    }
    antiAlias(frame, poses, interpolatedEdges, y, screenBounds, minX, maxX);
  }
  
  //Versions with modifiable stencil tests
  public static void draw(int[] frame, float p1X, float p1Y, float p1Z, float p2X, float p2Y, float p2Z, float p3X, float p3Y, float p3Z, byte compVal, char testType){
    //Triangle fill
    //Setting up the bounding box
    int[][] screenBounds = {{0x80000000, 0x7FFFFFF}, {0x80000000, 0x7FFFFFF}};
    //Makes forming the BB and rasterizing the triangle easier
    float[][] poses = {{p1X, p1Y}, 
                       {p2X, p2Y}, 
                       {p3X, p3Y}};
    //Constructing the triangle's bounding box
    screenBounds[0][0] = Math.round(Math.max(0, Math.min(poses[0][0], Math.min(poses[1][0], poses[2][0]))));
    screenBounds[0][1] = Math.round(Math.min(wid, Math.max(poses[0][0], Math.max(poses[1][0], poses[2][0]))));
    screenBounds[1][0] = Math.round(Math.max(0, Math.min(poses[0][1], Math.min(poses[1][1], poses[2][1]))));
    screenBounds[1][1] = Math.round(Math.min(heig, Math.max(poses[0][1], Math.max(poses[1][1], poses[2][1]))));
    int maxX = screenBounds[0][1];
    int minX = screenBounds[0][0];
    //Weights contributed to a pixel by the vertices
    float alpha = 0;
    float beta = 0;
    float gamma = 0;
      
    //Used for centring a pixel
    float x = 0;
    float y = 0;
      
    //The edges that will be iterated over
    float[] interpolatedEdges = {0, 0}; //i0 = lowest-mid, i1 = mid-highest, i2 = highest-lowest
    //Filling in the triangle
    if((flags & 16) == 16){

      //Iterating over the BB
      for(int i = screenBounds[1][0]; i < screenBounds[1][1]; i++){
        byte currentEdge = 1;
        y = i+0.5f;
        //Holds how far along each edge the program is at the current scanline
        float[] t = {-1, -1, -1};
        //Computes time t for the two edges that go to the middle vertex at the current scanline
        float denominator = (poses[1][1]-poses[0][1]-0.00001f);
        if(Math.abs(denominator) > 0.0001)
          t[2] = (poses[1][1]-y)/denominator;
        denominator = (poses[2][1]-poses[1][1])-0.00001f;
        if(Math.abs(denominator) > 0.0001)
          t[0] = (poses[2][1]-y)/denominator;
        denominator = (poses[2][1]-poses[0][1])-0.00001f;
        if(Math.abs(denominator) > 0.0001)
          t[1] = (poses[2][1]-y)/denominator;
        
        //Uses the times to interpolate along the edges
        if(t[2] >= 0f && t[2] <= 1f){
          interpolatedEdges[currentEdge] = (poses[0][0]-poses[1][0])*t[2]+poses[1][0]-0.0001f;
          currentEdge--;
        }
        if(t[0] >= 0f && t[0] <= 1f){
          interpolatedEdges[currentEdge] = (poses[1][0]-poses[2][0])*t[0]+poses[2][0]-0.0001f;
          currentEdge--;
        }
        if(t[1] >= 0f && t[1] <= 1f)
          interpolatedEdges[0] = (poses[0][0]-poses[2][0])*t[1]+poses[2][0]-0.0001f;
        //Determines the min and max x-positions of the current scanline
        screenBounds[0][0] = Math.round(Math.max(minX, Math.min(interpolatedEdges[0], interpolatedEdges[1])));
        screenBounds[0][1] = Math.round(Math.min(maxX, Math.max(interpolatedEdges[0], interpolatedEdges[1])));
        //Filling the scanline
        for(int j = screenBounds[0][0]; j < screenBounds[0][1]; j++){
          //Centring the pixel
          x = j+0.5f;
          //Calculating the weight each vertex contributes to the pixel
          alpha = returnAlpha(poses[0][0], poses[0][1], poses[1][0], poses[1][1], poses[2][0], poses[2][1], x, y);
          beta = returnBeta(poses[0][0], poses[0][1], poses[1][0], poses[1][1], poses[2][0], poses[2][1], x, y);
          gamma = returnGamma(alpha, beta);
          //Plotting the pixel
          float z = 1/(p1Z*alpha + p2Z*beta + p3Z*gamma); //Barycentric z
          //if(!depthWrite)
          z*=(((flags & 4) >>> 1)-1);
          int pixelPos = wid*i+j;
          stencilTest(pixelPos, compVal, testType);
          //For when the current triangle is closest at the current pixel than any previous triangle
          if((flags & 1) == 1 && ((flags & 4) == 0 && (z > zBuff[pixelPos] || zBuff[pixelPos] >= 0) || (flags & 4) == 4 && z < zBuff[pixelPos] || Float.isNaN(zBuff[pixelPos]))){
            //Interpolating the current pixel and the fill if the fill's alpha is less than 255. Otherwise, overwrite the current pixel's data with the fill
            if((fill >>> 24) < 0xFF)
              frame[pixelPos] = interpolatePixels(fill, frame[pixelPos], pixelPos);
            else{
              //Normalizes the stencil to be between 0 and 1
              float stencilNorm = ((~stencil[pixelPos]) & 0xFF)/255f;
              //Extracts the colour channel data
              int[] channels = {fill & 0xFF000000, (fill >>> 16) & 0xFF, (fill >>> 8) & 0xFF, fill & 0xFF};
              //Sets the brightness of the colour channels
              for(int k = 1; k < 4; k++)
                channels[k] = ((int)((channels[k])*stencilNorm)) << (8*(3-k));
              //Copies the data into the frame buffer
              frame[pixelPos] = channels[0]|channels[1]|channels[2]|channels[3];
            }
            zBuff[pixelPos] = z;
          }
          //For when there were triangles drawn at the current pixel that were closer than the current triangle
          else
           //Interpolate between the fill and the current pixel if both's alpha channels are less than 255. Otherwise, skip
           if((fill >>> 24) < 0xFF && (frame[pixelPos] >>> 24) < 0xFF)
             frame[pixelPos] = interpolatePixels(frame[pixelPos], fill, pixelPos);
         }
       }
     }
     //Drawing the outline
     if((flags & 8) == 8){
       //Setting up the endpoints of each side
       int[] xE = {Math.round(p1X), Math.round(p2X), Math.round(p3X)};
       int[] yE = {Math.round(p1Y), Math.round(p2Y), Math.round(p3Y)};
       final float [] zE = {p1Z+0.0004f, p2Z+0.0004f, p3Z+0.0004f};
       final int[] endX = {xE[1], xE[2], xE[0]};
       final int[] endY = {yE[1], yE[2], yE[0]};
       final float[] endZ = {zE[1], zE[2], zE[0]};
       //Drawing each side with Bresenham's line algorithm
       for(byte i = 0; i < 3; i++)
         drawLine(frame, wid, heig, xE[i], yE[i], zE[i], endX[i], endY[i], endZ[i], stroke, (flags & 4) == 0);
    }
    antiAlias(frame, poses, interpolatedEdges, y, screenBounds, minX, maxX);
  }
  
  public static void draw(int[] frame, Triangle triangle, byte compVal, char testType){
    //Setting up the colour
    TriangleRasterizer.stroke(triangle.getStroke());
    TriangleRasterizer.fill(triangle.getFill());
    flags = (byte)((triangle.getHasStroke()) ? flags|8 : flags&-9);
    flags = (byte)((triangle.getHasFill()) ? flags|16 : flags&-17);
    //Setting up the bounding box
    int[][] screenBounds = {{0x80000000, 0x7FFFFFF}, {0x80000000, 0x7FFFFFF}};
    //Makes forming the BB and rasterizing the triangle easier
    float[][] poses = {{triangle.getVertices()[0][0], triangle.getVertices()[0][1], triangle.getVertices()[0][2]}, 
                        {triangle.getVertices()[1][0], triangle.getVertices()[1][1], triangle.getVertices()[1][2]}, 
                        {triangle.getVertices()[2][0], triangle.getVertices()[2][1], triangle.getVertices()[2][2]}};
    //Constructing the triangle's bounding box
    screenBounds[0][0] = Math.round(Math.max(0, Math.min(poses[0][0], Math.min(poses[1][0], poses[2][0]))));
    screenBounds[0][1] = Math.round(Math.min(wid, Math.max(poses[0][0], Math.max(poses[1][0], poses[2][0]))));
    screenBounds[1][0] = Math.round(Math.max(0, Math.min(poses[0][1], Math.min(poses[1][1], poses[2][1]))));
    screenBounds[1][1] = Math.round(Math.min(heig, Math.max(poses[0][1], Math.max(poses[1][1], poses[2][1]))));
    int maxX = screenBounds[0][1];
    int minX = screenBounds[0][0];
    //Weights contributed to a pixel by the vertices
    float alpha = 0;
    float beta = 0;
    float gamma = 0;
      
    //Used for centring a pixel
    float x = 0;
    float y = 0;
      
    //The edges that will be iterated over
    float[] interpolatedEdges = {0, 0}; //i0 = lowest-mid, i1 = mid-highest, i2 = highest-lowest
    //Filling in the triangle
    if((flags & 16) == 16){
      //Iterating over the BB
      for(int i = screenBounds[1][0]; i < screenBounds[1][1]; i++){
        byte currentEdge = 1;
        y = i+0.5f;
        //Holds how far along each edge the program is at the current scanline
        float[] t = {-1, -1, -1};
        //Computes time t for the two edges that go to the middle vertex at the current scanline
        float denominator = (poses[1][1]-poses[0][1]-0.00001f);
        if(Math.abs(denominator) > 0.0001)
          t[2] = (poses[1][1]-y)/denominator;
        denominator = (poses[2][1]-poses[1][1])-0.00001f;
        if(Math.abs(denominator) > 0.0001)
          t[0] = (poses[2][1]-y)/denominator;
        denominator = (poses[2][1]-poses[0][1])-0.00001f;
        if(Math.abs(denominator) > 0.0001)
          t[1] = (poses[2][1]-y)/denominator;
        
        //Uses the times to interpolate along the edges
        if(t[2] >= 0f && t[2] <= 1f){
          interpolatedEdges[currentEdge] = (poses[0][0]-poses[1][0])*t[2]+poses[1][0]-0.0001f;
          currentEdge--;
        }
        if(t[0] >= 0f && t[0] <= 1f){
          interpolatedEdges[currentEdge] = (poses[1][0]-poses[2][0])*t[0]+poses[2][0]-0.0001f;
          currentEdge--;
        }
        if(t[1] >= 0f && t[1] <= 1f)
          interpolatedEdges[0] = (poses[0][0]-poses[2][0])*t[1]+poses[2][0]-0.0001f;
        //Determines the min and max x-positions of the current scanline
        screenBounds[0][0] = Math.round(Math.max(minX, Math.min(interpolatedEdges[0], interpolatedEdges[1])));
        screenBounds[0][1] = Math.round(Math.min(maxX, Math.max(interpolatedEdges[0], interpolatedEdges[1])));
        //Filling the scanline
        for(int j = screenBounds[0][0]; j < screenBounds[0][1]; j++){
          //Centring the pixel
          x = j+0.5f;
          //Calculating the weight each vertex contributes to the pixel
          alpha = returnAlpha(poses[0][0], poses[0][1], poses[1][0], poses[1][1], poses[2][0], poses[2][1], x, y);
          beta = returnBeta(poses[0][0], poses[0][1], poses[1][0], poses[1][1], poses[2][0], poses[2][1], x, y);
          gamma = returnGamma(alpha, beta);
          //Plotting the pixel
          float z = 1/(poses[0][2]*alpha + poses[1][2]*beta + poses[2][2]*gamma); //Barycentric z
          //if(!depthWrite)
          z*=(((flags & 4) >>> 1)-1);
          int pixelPos = wid*i+j;
          stencilTest(pixelPos, compVal, testType);
          //For when the current triangle is closest at the current pixel than any previous triangle
          if((flags & 1) == 1 && ((flags & 4) == 0 && (z > zBuff[pixelPos] || zBuff[pixelPos] >= 0) || (flags & 4) == 4 && z < zBuff[pixelPos] || Float.isNaN(zBuff[pixelPos]))){
            //Interpolating the current pixel and the fill if the fill's alpha is less than 255. Otherwise, overwrite the current pixel's data with the fill
            if((fill >>> 24) < 0xFF)
              frame[pixelPos] = interpolatePixels(fill, frame[pixelPos], pixelPos);
            else{
              //Normalizes the stencil to be between 0 and 1
              float stencilNorm = ((~stencil[pixelPos]) & 0xFF)/255f;
              //Extracts the colour channel data
              int[] channels = {fill & 0xFF000000, (fill >>> 16) & 0xFF, (fill >>> 8) & 0xFF, fill & 0xFF};
              //Sets the brightness of the colour channels
              for(int k = 1; k < 4; k++)
                channels[k] = ((int)((channels[k])*stencilNorm)) << (8*(3-k));
              //Copies the data into the frame buffer
              frame[pixelPos] = channels[0]|channels[1]|channels[2]|channels[3];
            }
            zBuff[pixelPos] = z;
          }
          //For when there were triangles drawn at the current pixel that were closer than the current triangle
          else
           //Interpolate between the fill and the current pixel if both's alpha channels are less than 255. Otherwise, skip
           if((fill >>> 24) < 0xFF && (frame[pixelPos] >>> 24) < 0xFF)
             frame[pixelPos] = interpolatePixels(frame[pixelPos], fill, pixelPos);
         }
       }
     }
     //Drawing the outline
     if((flags & 8) == 8){
       for(byte i = 0; i < 3; i++)
         drawLine(frame, wid, heig, Math.round(triangle.getVertices()[i][0]), Math.round(triangle.getVertices()[i][1]), triangle.getVertices()[i][2]+0.0004f, Math.round(triangle.getVertices()[(i+1)%3][0]), Math.round(triangle.getVertices()[(i+1)%3][1]), triangle.getVertices()[(i+1)%3][2]+0.0004f, stroke, (flags & 4) == 0);
    }
    antiAlias(frame, poses, interpolatedEdges, y, screenBounds, minX, maxX);
  }

  public static void billBoardDraw(int[] frame, Billboard sprite, float x, float y, float z, float sizeX, float sizeY, int lightColour, byte lightIntensity){
    if(Math.abs(z) > 0.0001)
      z = 1/z;
    else
      z = 0;
    //Sets the sprite to be drawn in front of everything else
    if(sprite.noDraw())
      z*=-1;

    if(Math.abs(sizeX) > 0.0001 && Math.abs(sizeY) > 0.0001){
      //Grabs the recipricol of the scale of the image
      float scaleX = sprite.returnWidth()/sizeX;
      float scaleY = sprite.returnHeight()/sizeY;
      //Sets the sizes to integers
      sizeX = Math.round(sizeX);
      sizeY = Math.round(sizeY);
      
      //Determines where the start of the scale indices are (these track which pixel of the original image we are currently looking at)
      float scX = ((sizeX >= 0) ? (x >= 0) ? 0 : -x*scaleX : (x+sprite.returnWidth()/scaleX >= 0) ? sprite.returnWidth() : -x*scaleX-1f/sprite.returnWidth())-0.0001f;
      float scY = ((sizeY >= 0) ? (y >= 0) ? 0 : -y*scaleY : (y+sprite.returnHeight()/scaleY >= 0) ? sprite.returnHeight() : -y*scaleY-1f/sprite.returnHeight())-0.0001f;
      float oldScY = scY; //Used for resetting scY
  
      //Computes the start and end pixels of the output image
      int[] start = {Math.round(Math.min(Math.max(0, Math.min(x, x+sizeX)), wid)), Math.round(Math.min(Math.max(0, Math.min(y, y+sizeY)), heig))};
      int[] end = {Math.round(Math.min(Math.max(0, Math.max(x, x+sizeX)), wid)), Math.round(Math.min(Math.max(0, Math.max(y, y+sizeY)), heig))};
      float lightAlpha = (lightIntensity & 255)*0.003921568f;
      if(sprite.hasImage()){
  
        alphaNorm = (sprite.returnTint() & 255)*0.003921568f;
        //Actually drawing the sprite
        for(int i = start[0]; i < end[0] && scX > -1; i++){
          scY = oldScY; //Resetting scY
          for(int j = start[1]; j < end[1] && scY > -1; j++){
            int pixelPos = j*wid+i; //Determining a pixel's index
            int imgPixel = (int)(scY)*sprite.returnWidth()+(int)(scX); //Determining where in the image the current desired pixel is
            //Checks if a pixel is not a specific colour defined in the Billboard object and if there is nothing already in front of where the sprite is being drawn
            //If it passes, it draws the sprite's pixel to the new location
            if((sprite.returnPixels()[imgPixel] & 0xFFFFFF) != sprite.returnInvisColour() || sprite.hasRemoval()){
              //Adjusting the brightness level of each pixel
              int colour = (sprite.returnTint() << 24);
              colour|=((Math.min(Math.max(0, (((sprite.returnPixels()[imgPixel] >>> 16) & 255)+sprite.returnBrightness())), 255) & 255) << 16);
              colour|=((Math.min(Math.max(0, (((sprite.returnPixels()[imgPixel] >>> 8) & 255)+sprite.returnBrightness())), 255) & 255) << 8);
              colour|=(Math.min(Math.max(0, ((sprite.returnPixels()[imgPixel] & 255)+sprite.returnBrightness())), 255) & 255);
              if((sprite.noDraw() && (z > zBuff[pixelPos] || zBuff[pixelPos] >= 0) || !sprite.noDraw() && z < zBuff[pixelPos] || Float.isNaN(zBuff[pixelPos]))){
                //Interpolating between the light colour and the current pixel's colour
                colour = interpolatePixels((lightColour & 0xFFFFFF) | (lightIntensity << 24), colour, lightAlpha); 
                //Copying the results to the screen
                if((colour >>> 24) == 255)
                  frame[pixelPos] = colour;
                else
                  frame[pixelPos] = interpolatePixels(colour, frame[pixelPos], alphaNorm);
                zBuff[pixelPos] = z;//Copying the z-position of the image to the depth buffer
              }
              else
                if(sprite.returnTint() != -1 && (frame[pixelPos] >>> 24) < 255){
                  colour = interpolatePixels((lightColour & 0xFFFFFF) | (lightIntensity << 24), (colour & 0xFFFFFF)) | (colour & 0xFF000000); 
                  frame[pixelPos] = interpolatePixels(frame[pixelPos], colour);
                }
            }
            scY+=scaleY;
          }
          scX+=scaleX;
        }
      }
      if(sprite.hasOutline()){
        //Sets the colour of the outline to be tinted by the full screen tint
        int colour = (interpolatePixels((lightColour & 0xFFFFFF) | (lightIntensity << 24), (sprite.returnStroke() & 0xFFFFFF), lightAlpha) & 0xFFFFFF) | (sprite.returnStroke() & 0xFF000000);
        //Doing the vertical sides
        int side = 0;
        z = (sprite.noDraw()) ? z-0.0004f : z+0.0004f;
        if(start[0]-1 >= 0){
          
          if((sprite.returnStroke() >>> 24) < 255){
            alphaNorm = (sprite.returnStroke() & 255)*0.003921569f;
            for(int i = start[1]; i < end[1]; i++){
              side = i*wid+start[0]-1;
              if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = interpolatePixels(colour, frame[side], alphaNorm);
            }
          }
         else
           for(int i = start[1]; i < end[1]; i++){
              side = i*wid+start[0]-1;
              if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = colour;
            }
        }
        if(end[0] < wid){
          if((sprite.returnStroke() >>> 24) < 255){
            alphaNorm = (sprite.returnStroke() & 255)*0.003921569f;
            for(int i = start[1]; i < end[1]; i++){
              side = i*wid+end[0];
              if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = interpolatePixels(colour, frame[side], alphaNorm);
            }
          }
           else
             for(int i = start[1]; i < end[1]; i++){
               side = i*wid+end[0];
               if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = colour;
           }
        }
        //Doing the horizontal sides
        if(start[1]-1 >= 0){
          if((sprite.returnStroke() >>> 24) < 255){
            alphaNorm = (sprite.returnStroke() & 255)*0.003921569f;
            for(int i = start[0]; i < end[0]; i++){
              side = (start[1]-1)*wid+i;
              if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = interpolatePixels(colour, frame[side], alphaNorm);
            }
          }
         else
           for(int i = start[0]; i < end[0]; i++){
              side = (start[1]-1)*wid+i;
              if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = colour;
            }
        }
        if(end[1] < heig){
          if((sprite.returnStroke() >>> 24) < 255){
            alphaNorm = (sprite.returnStroke() & 255)*0.003921569f;
            for(int i = start[0]; i < end[0]; i++){
              side = end[1]*wid+i;
              if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = interpolatePixels(colour, frame[side], alphaNorm);
            }
          }
           else
             for(int i = start[0]; i < end[0]; i++){
               side = end[1]*wid+i;
               if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = colour;
           }
        }
      }
    }
  }
  public static void billBoardDraw(int[] frame, Billboard sprite, float x, float y, float z, float sizeX, float sizeY){
    if(Math.abs(z) > 0.0001) 
      z = 1/z;
    else
      z = 0;
    //Sets the sprite to be drawn in front of everything else
    if(sprite.noDraw())
      z*=-1;
    if(Math.abs(sizeX) > 0.0001 && Math.abs(sizeY) > 0.0001){
      //Grabs the recipricol of the scale of the image
      float scaleX = sprite.returnWidth()/sizeX;
      float scaleY = sprite.returnHeight()/sizeY;
      //Sets the sizes to integers
      sizeX = Math.round(sizeX);
      sizeY = Math.round(sizeY);
  
      //Determines where the start of the scale indices are (these track which pixel of the original image we are currently looking at)
      float scX = ((sizeX >= 0) ? (x >= 0) ? 0 : -x*scaleX : (x+sprite.returnWidth()/scaleX >= 0) ? sprite.returnWidth() : -x*scaleX-1f/sprite.returnWidth())-0.0001f;
      float scY = ((sizeY >= 0) ? (y >= 0) ? 0 : -y*scaleY : (y+sprite.returnHeight()/scaleY >= 0) ? sprite.returnHeight() : -y*scaleY-1f/sprite.returnHeight())-0.0001f;
      float oldScY = scY; //Used for resetting scY
      
      //Computes the start and end pixels of the output image
      int[] start = {Math.round(Math.min(Math.max(0, Math.min(x, x+sizeX)), wid)), Math.round(Math.min(Math.max(0, Math.min(y, y+sizeY)), heig))};
      int[] end = {Math.round(Math.min(Math.max(0, Math.max(x, x+sizeX)), wid)), Math.round(Math.min(Math.max(0, Math.max(y, y+sizeY)), heig))};
      if(sprite.hasImage()){
        alphaNorm = (sprite.returnTint() & 255)*0.003921568f;
        //Actually drawing the sprite
        for(int i = start[0]; i < end[0] && scX > -1; i++){
          scY = oldScY; //Resetting scY
          for(int j = start[1]; j < end[1] && scY > -1; j++){
            int pixelPos = j*wid+i; //Determining a pixel's index
            int imgPixel = (int)(scY)*sprite.returnWidth()+(int)(scX); //Determining where in the image the current desired pixel is
            //Checks if a pixel is not a specific colour defined in the Billboard object and if there is nothing already in front of where the sprite is being drawn
            //If it passes, it draws the sprite's pixel to the new location
            if(((sprite.returnPixels()[imgPixel] & 0xFFFFFF) != sprite.returnInvisColour() || sprite.hasRemoval())){
              //Adjusting the brightness level of each pixel
              int colour = (sprite.returnTint() << 24);
              colour|=((Math.min(Math.max(0, (((sprite.returnPixels()[imgPixel] >>> 16) & 255)+sprite.returnBrightness())), 255) & 255) << 16);
              colour|=((Math.min(Math.max(0, (((sprite.returnPixels()[imgPixel] >>> 8) & 255)+sprite.returnBrightness())), 255) & 255) << 8);
              colour|=(Math.min(Math.max(0, ((sprite.returnPixels()[imgPixel] & 255)+sprite.returnBrightness())), 255) & 255);
              if((sprite.noDraw() && (z > zBuff[pixelPos] || zBuff[pixelPos] >= 0) || !sprite.noDraw() && z < zBuff[pixelPos] || Float.isNaN(zBuff[pixelPos]))){
                //Copying the image's pixel to the frame buffer
                if(sprite.returnTint() == -1)
                  frame[pixelPos] = 0xFF000000|colour;
                else
                  frame[pixelPos] = interpolatePixels(colour, frame[pixelPos], alphaNorm);
                zBuff[pixelPos] = z;//Copying the z-position of the image to the depth buffer
              }
              else
                if(sprite.returnTint() != -1 && (frame[pixelPos] >>> 24) < 255)
                  frame[pixelPos] = interpolatePixels(frame[pixelPos], colour);
              }
              scY+=scaleY;
            }
            scX+=scaleX;
        }
      }
      if(sprite.hasOutline()){
        //Doing the vertical sides
        int side = 0;
        z = (sprite.noDraw()) ? z-0.0004f : z+0.0004f;
        if(start[0]-1 >= 0){
          if((sprite.returnStroke() >>> 24) < 255){
            alphaNorm = ((sprite.returnStroke() >>> 24) & 255)*0.003921568f;
            for(int i = start[1]; i < end[1]; i++){
                side = i*wid+start[0]-1;
                if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                  frame[side] = interpolatePixels(sprite.returnStroke(), frame[side], alphaNorm);
              }
            }
          else
           for(int i = start[1]; i < end[1]; i++){
              side = i*wid+start[0]-1;
              if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = sprite.returnStroke();
            }
        }
        if(end[0] < wid){
          if((sprite.returnStroke() >>> 24) < 255){
            alphaNorm = ((sprite.returnStroke() >>> 24) & 255)*0.003921568f;
            for(int i = start[1]; i < end[1]; i++){
              side = i*wid+end[0];
              if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = interpolatePixels(sprite.returnStroke(), frame[side], alphaNorm);
            }
          }
           else
             for(int i = start[1]; i < end[1]; i++){
               side = i*wid+end[0];
               if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = sprite.returnStroke();
           }
        }
        //Doing the horizontal sides
        if(start[1]-1 >= 0){
          if((sprite.returnStroke() >>> 24) < 255){
            alphaNorm = ((sprite.returnStroke() >>> 24) & 255)*0.003921568f;
            for(int i = start[0]; i < end[0]; i++){
              side = (start[1]-1)*wid+i;
              if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = interpolatePixels(sprite.returnStroke(), frame[side], alphaNorm);
            }
        }
         else
           for(int i = start[0]; i < end[0]; i++){
              side = (start[1]-1)*wid+i;
              if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = sprite.returnStroke();
            }
        }
        if(end[1] < heig){
          if((sprite.returnStroke() >>> 24) < 255){
            alphaNorm = ((sprite.returnStroke() >>> 24) & 255)*0.003921568f;
            for(int i = start[0]; i < end[0]; i++){
              side = end[1]*wid+i;
              if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = interpolatePixels(sprite.returnStroke(), frame[side], alphaNorm);
            }
          }
           else
             for(int i = start[0]; i < end[0]; i++){
               side = end[1]*wid+i;
               if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = sprite.returnStroke();
           }
        }
      }
    }
  }
  public static void billBoardDraw(int[] frame, Billboard sprite, float x, float y, float z, float sizeX, float sizeY, int lightColour, byte lightIntensity, byte compVal, char testType){
    if(Math.abs(z) > 0.0001)
      z = 1/z;
    else
      z = 0;
    //Sets the sprite to be drawn in front of everything else
    if(sprite.noDraw())
      z*=-1;
    if(Math.abs(sizeX) > 0.0001 && Math.abs(sizeY) > 0.0001){
      //Grabs the recipricol of the scale of the image
      float scaleX = sprite.returnWidth()/sizeX;
      float scaleY = sprite.returnHeight()/sizeY;
      //Sets the sizes to integers
      sizeX = Math.round(sizeX);
      sizeY = Math.round(sizeY);
      
      //Determines where the start of the scale indices are (these track which pixel of the original image we are currently looking at)
      float scX = ((sizeX >= 0) ? (x >= 0) ? 0 : -x*scaleX : (x+sprite.returnWidth()/scaleX >= 0) ? sprite.returnWidth() : -x*scaleX-1f/sprite.returnWidth())-0.0001f;
      float scY = ((sizeY >= 0) ? (y >= 0) ? 0 : -y*scaleY : (y+sprite.returnHeight()/scaleY >= 0) ? sprite.returnHeight() : -y*scaleY-1f/sprite.returnHeight())-0.0001f;
      float oldScY = scY; //Used for resetting scY
      
      //Computes the start and end pixels of the output image
      int[] start = {Math.round(Math.min(Math.max(0, Math.min(x, x+sizeX)), wid)), Math.round(Math.min(Math.max(0, Math.min(y, y+sizeY)), heig))};
      int[] end = {Math.round(Math.min(Math.max(0, Math.max(x, x+sizeX)), wid)), Math.round(Math.min(Math.max(0, Math.max(y, y+sizeY)), heig))};
      float lightAlpha = (lightIntensity & 255)*0.003921568f;
      if(sprite.hasImage()){
        alphaNorm = (sprite.returnTint() & 255)*0.003921568f;
        //Actually drawing the sprite
        for(int i = start[0]; i < end[0] && scX > -1; i++){
          scY = oldScY; //Resetting scY
          for(int j = start[1]; j < end[1] && scY > -1; j++){
            int pixelPos = j*wid+i; //Determining a pixel's index
            int imgPixel = (int)(scY)*sprite.returnWidth()+(int)(scX); //Determining where in the image the current desired pixel is
            stencilTest(pixelPos, compVal, testType);
            //Checks if a pixel is not a specific colour defined in the Billboard object and if there is nothing already in front of where the sprite is being drawn
            //If it passes, it draws the sprite's pixel to the new location
            if(((flags & 1) == 1) && ((sprite.returnPixels()[imgPixel] & 0xFFFFFF) != sprite.returnInvisColour() || sprite.hasRemoval())){
              //Adjusting the brightness level of each pixel
              int colour = (sprite.returnTint() << 24);
              colour|=((Math.min(Math.max(0, (((sprite.returnPixels()[imgPixel] >>> 16) & 255)+sprite.returnBrightness())), 255) & 255) << 16);
              colour|=((Math.min(Math.max(0, (((sprite.returnPixels()[imgPixel] >>> 8) & 255)+sprite.returnBrightness())), 255) & 255) << 8);
              colour|=(Math.min(Math.max(0, ((sprite.returnPixels()[imgPixel] & 255)+sprite.returnBrightness())), 255) & 255);
              if((sprite.noDraw() && (z > zBuff[pixelPos] || zBuff[pixelPos] >= 0) || !sprite.noDraw() && z < zBuff[pixelPos] || Float.isNaN(zBuff[pixelPos]))){
                //Interpolating between the light colour and the current pixel's colour
                colour = interpolatePixels((lightColour & 0xFFFFFF) | (lightIntensity << 24), (colour & 0xFFFFFF), lightAlpha) | (colour & 0xFF000000); 
                //Copying the results to the screen
                if((colour >>> 24) == 255)
                  frame[pixelPos] = colour;
                else
                  frame[pixelPos] = interpolatePixels(colour, frame[pixelPos], alphaNorm);
                zBuff[pixelPos] = z;//Copying the z-position of the image to the depth buffer
              }
              else
                if(sprite.returnTint() != -1 && (frame[pixelPos] >>> 24) < 255){
                  colour = interpolatePixels((lightColour & 0xFFFFFF) | (lightIntensity << 24), (colour & 0xFFFFFF), lightAlpha) | (colour & 0xFF000000);
                  frame[pixelPos] = interpolatePixels(frame[pixelPos], colour);
                }
            }
            scY+=scaleY;
          }
          scX+=scaleX;
        }
      }
      if(sprite.hasOutline()){
        //Sets the colour of the outline to be tinted by the full screen tint
        int colour = (interpolatePixels((lightColour & 0xFFFFFF) | (lightIntensity << 24), (sprite.returnStroke() & 0xFFFFFF), lightAlpha) & 0xFFFFFF) | (sprite.returnStroke() & 0xFF000000);
        //Doing the vertical sides
        int side = 0;
        z = (sprite.noDraw()) ? z-0.0004f : z+0.0004f;
        if(start[0]-1 >= 0){
          
          if((sprite.returnStroke() >>> 24) < 255){
            alphaNorm = (sprite.returnStroke() & 255)*0.003921569f;
            for(int i = start[1]; i < end[1]; i++){
              side = i*wid+start[0]-1;
              if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = interpolatePixels(colour, frame[side], alphaNorm);
            }
          }
         else
           for(int i = start[1]; i < end[1]; i++){
              side = i*wid+start[0]-1;
              if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = colour;
            }
        }
        if(end[0] < wid){
          if((sprite.returnStroke() >>> 24) < 255){
            alphaNorm = (sprite.returnStroke() & 255)*0.003921569f;
            for(int i = start[1]; i < end[1]; i++){
              side = i*wid+end[0];
              if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = interpolatePixels(colour, frame[side], alphaNorm);
            }
          }
           else
             for(int i = start[1]; i < end[1]; i++){
               side = i*wid+end[0];
               if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = colour;
           }
        }
        //Doing the horizontal sides
        if(start[1]-1 >= 0){
          if((sprite.returnStroke() >>> 24) < 255){
            alphaNorm = (sprite.returnStroke() & 255)*0.003921569f;
            for(int i = start[0]; i < end[0]; i++){
              side = (start[1]-1)*wid+i;
              if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = interpolatePixels(colour, frame[side], alphaNorm);
            }
          }
         else
           for(int i = start[0]; i < end[0]; i++){
              side = (start[1]-1)*wid+i;
              if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = colour;
            }
        }
        if(end[1] < heig){
          if((sprite.returnStroke() >>> 24) < 255){
            alphaNorm = (sprite.returnStroke() & 255)*0.003921569f;
            for(int i = start[0]; i < end[0]; i++){
              side = end[1]*wid+i;
              if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = interpolatePixels(colour, frame[side], alphaNorm);
            }
          }
           else
             for(int i = start[0]; i < end[0]; i++){
               side = end[1]*wid+i;
               if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = colour;
           }
        }
      }
    }
  }
  public static void billBoardDraw(int[] frame, Billboard sprite, float x, float y, float z, float sizeX, float sizeY, byte compVal, char testType){
    if(Math.abs(z) > 0.0001)
       z = 1/z;
    else
      z = 0;
    //Sets the sprite to be drawn in front of everything else
    if(sprite.noDraw())
      z*=-1;
    if(Math.abs(sizeX) > 0.0001 && Math.abs(sizeY) > 0.0001){
      //Grabs the recipricol of the scale of the image
      float scaleX = sprite.returnWidth()/sizeX;
      float scaleY = sprite.returnHeight()/sizeY;
      //Sets the sizes to integers
      sizeX = Math.round(sizeX);
      sizeY = Math.round(sizeY);
  
      //Determines where the start of the scale indices are (these track which pixel of the original image we are currently looking at)
      float scX = ((sizeX >= 0) ? (x >= 0) ? 0 : -x*scaleX : (x+sprite.returnWidth()/scaleX >= 0) ? sprite.returnWidth() : -x*scaleX-1f/sprite.returnWidth())-0.0001f;
      float scY = ((sizeY >= 0) ? (y >= 0) ? 0 : -y*scaleY : (y+sprite.returnHeight()/scaleY >= 0) ? sprite.returnHeight() : -y*scaleY-1f/sprite.returnHeight())-0.0001f;
      float oldScY = scY; //Used for resetting scY
      
      //Computes the start and end pixels of the output image
      int[] start = {Math.round(Math.min(Math.max(0, Math.min(x, x+sizeX)), wid)), Math.round(Math.min(Math.max(0, Math.min(y, y+sizeY)), heig))};
      int[] end = {Math.round(Math.min(Math.max(0, Math.max(x, x+sizeX)), wid)), Math.round(Math.min(Math.max(0, Math.max(y, y+sizeY)), heig))};
      if(sprite.hasImage()){
        alphaNorm = (sprite.returnTint() & 255)*0.003921568f;
        //Actually drawing the sprite
        for(int i = start[0]; i < end[0] && scX > -1; i++){
          scY = oldScY; //Resetting scY
          for(int j = start[1]; j < end[1] && scY > -1; j++){
            int pixelPos = j*wid+i; //Determining a pixel's index
            int imgPixel = (int)(scY)*sprite.returnWidth()+(int)(scX); //Determining where in the image the current desired pixel is
            stencilTest(pixelPos, compVal, testType);
            //Checks if a pixel is not a specific colour defined in the Billboard object and if there is nothing already in front of where the sprite is being drawn
            //If it passes, it draws the sprite's pixel to the new location
            if((flags & 1) == 1 && ((sprite.returnPixels()[imgPixel] & 0xFFFFFF) != sprite.returnInvisColour() || sprite.hasRemoval())){
              //Adjusting the brightness level of each pixel
              int colour = (sprite.returnTint() << 24);
              colour|=((Math.min(Math.max(0, (((sprite.returnPixels()[imgPixel] >>> 16) & 255)+sprite.returnBrightness())), 255) & 255) << 16);
              colour|=((Math.min(Math.max(0, (((sprite.returnPixels()[imgPixel] >>> 8) & 255)+sprite.returnBrightness())), 255) & 255) << 8);
              colour|=(Math.min(Math.max(0, ((sprite.returnPixels()[imgPixel] & 255)+sprite.returnBrightness())), 255) & 255);
              if((sprite.noDraw() && (z > zBuff[pixelPos] || zBuff[pixelPos] >= 0) || !sprite.noDraw() && z < zBuff[pixelPos] || Float.isNaN(zBuff[pixelPos]))){
                //Copying the image's pixel to the frame buffer
                if(sprite.returnTint() == -1)
                  frame[pixelPos] = 0xFF000000|colour;
                else
                  frame[pixelPos] = interpolatePixels(colour, frame[pixelPos], alphaNorm);
                zBuff[pixelPos] = z;//Copying the z-position of the image to the depth buffer
              }
              else
                if(sprite.returnTint() != -1 && (frame[pixelPos] >>> 24) < 255)
                  frame[pixelPos] = interpolatePixels(frame[pixelPos], colour);
              }
              scY+=scaleY;
            }
            scX+=scaleX;
        }
      }
      if(sprite.hasOutline()){
        z = (sprite.noDraw()) ? z-0.0004f : z+0.0004f;
        //Doing the vertical sides
        int side = 0;
        if(start[0]-1 >= 0){
          if((sprite.returnStroke() >>> 24) < 255){
            alphaNorm = ((sprite.returnStroke() >>> 24) & 255)*0.003921568f;
            for(int i = start[1]; i < end[1]; i++){
                side = i*wid+start[0]-1;
                if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                  frame[side] = interpolatePixels(sprite.returnStroke(), frame[side], alphaNorm);
              }
            }
          else
           for(int i = start[1]; i < end[1]; i++){
              side = i*wid+start[0]-1;
              if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = sprite.returnStroke();
            }
        }
        if(end[0] < wid){
          if((sprite.returnStroke() >>> 24) < 255){
            alphaNorm = ((sprite.returnStroke() >>> 24) & 255)*0.003921568f;
            for(int i = start[1]; i < end[1]; i++){
              side = i*wid+end[0];
              if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = interpolatePixels(sprite.returnStroke(), frame[side], alphaNorm);
            }
          }
           else
             for(int i = start[1]; i < end[1]; i++){
               side = i*wid+end[0];
               if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = sprite.returnStroke();
           }
        }
        //Doing the horizontal sides
        if(start[1]-1 >= 0){
          if((sprite.returnStroke() >>> 24) < 255){
            alphaNorm = ((sprite.returnStroke() >>> 24) & 255)*0.003921568f;
            for(int i = start[0]; i < end[0]; i++){
              side = (start[1]-1)*wid+i;
              if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = interpolatePixels(sprite.returnStroke(), frame[side], alphaNorm);
            }
        }
         else
           for(int i = start[0]; i < end[0]; i++){
              side = (start[1]-1)*wid+i;
              if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = sprite.returnStroke();
            }
        }
        if(end[1] < heig){
          if((sprite.returnStroke() >>> 24) < 255){
            alphaNorm = ((sprite.returnStroke() >>> 24) & 255)*0.003921568f;
            for(int i = start[0]; i < end[0]; i++){
              side = end[1]*wid+i;
              if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = interpolatePixels(sprite.returnStroke(), frame[side], alphaNorm);
            }
          }
           else
             for(int i = start[0]; i < end[0]; i++){
               side = end[1]*wid+i;
               if((!sprite.noDraw() && z <= zBuff[side]) || (sprite.noDraw() && (z >= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = sprite.returnStroke();
           }
        }
      }
    }
  }
  //Returns the depth buffer
  public static float[] returnDepthBuffer(){
    return zBuff;
  }
  
  //Returns the height
  public static int returnHeight(){
    return heig;
  }
  
  //Returns the width
  public static int returnWidth(){
    return wid;
  }
  
  //Returns the background colour
  public static int returnBackgroundColour(){
    return background;
  }
  
  //Returns the stencil buffer value at a particular pixel
  public static byte returnStencil(int index){
    return stencil[index];
  }
  
  //Returns the stencil mask
  public static byte returnStencilMask(){
    return stencilMask;
  }
  
  //Antialiasing
  public static void antiAlias(int[] frame, float[][] poses, float[] interpolatedEdges, float y, int[][] screenBounds, int maxX, int minX){
    if((flags & 2) == 2){
      //Iterating over the BB
      for(int i = screenBounds[1][0]; i < screenBounds[1][1]; i++){
        byte currentEdge = 1;
        y = i+0.5f;
        //Holds how far along each edge the program is at the current scanline
        float[] t = {-1, -1, -1};
        //Computes time t for the two edges that go to the middle vertex at the current scanline
        float denominator = (poses[1][1]-poses[0][1]-0.00001f);
        if(Math.abs(denominator) > 0.0001)
          t[2] = (poses[1][1]-y)/denominator;
        denominator = (poses[2][1]-poses[1][1])-0.00001f;
        if(Math.abs(denominator) > 0.0001)
          t[0] = (poses[2][1]-y)/denominator;
        denominator = (poses[2][1]-poses[0][1])-0.00001f;
        if(Math.abs(denominator) > 0.0001)
          t[1] = (poses[2][1]-y)/denominator;
        
        //Uses the times to interpolate along the edges
        if(t[2] >= 0f && t[2] <= 1f){
          interpolatedEdges[currentEdge] = (poses[0][0] - poses[1][0])*t[2] + poses[1][0]-0.0001f;
          currentEdge--;
        }
        if(t[0] >= 0f && t[0] <= 1f){
          interpolatedEdges[currentEdge] = (poses[1][0]-poses[2][0])*t[0]+poses[2][0]-0.0001f;
          currentEdge--;
        }
        if(t[1] >= 0f && t[1] <= 1f)
          interpolatedEdges[0] = (poses[0][0]-poses[2][0])*t[1]+poses[2][0]-0.0001f;
        //Determines the min and max x-positions of the current scanline
        screenBounds[0][0] = Math.round(Math.max(minX, Math.min(interpolatedEdges[0], interpolatedEdges[1])));
        if(screenBounds[0][0] < 0)
          screenBounds[0][0] = 0;
        else if(screenBounds[0][0] >= wid)
          screenBounds[0][0] = wid-1;
        screenBounds[0][1] = Math.round(Math.min(maxX, Math.max(interpolatedEdges[0], interpolatedEdges[1])));
        if(screenBounds[0][1] < 0)
          screenBounds[0][1] = 0;
        else if(screenBounds[0][1] >= wid)
          screenBounds[0][1] = wid-1;
          
        for(byte j = 0; j < 2; j++){
        //Extracts each channel's byte
        int averagedA = frame[i*wid+screenBounds[0][j]] >>> 24;
        int averagedR = (frame[i*wid+screenBounds[0][j]] >>> 16) & 0xFF;
        int averagedG = (frame[i*wid+screenBounds[0][j]] >>> 8) & 0xFF;
        int averagedB = frame[i*wid+screenBounds[0][j]] & 0xFF;
  
        //Taking the average of the four colour channels of nine pixels (the current pixel plus eight surrounding pixels)
        int[][] bounds = {{Math.max(0, screenBounds[0][j]-1), Math.min(screenBounds[0][j]+1, wid-1)}, {Math.max(0, i-1), Math.min(i+1, heig-1)}};
        for(int s = 0; s < 2; s++)
          for(int k = 0; k < 2; k++){
            averagedA+=(frame[bounds[1][k]*wid+bounds[0][s]] >>> 24);
            averagedR+=((frame[bounds[1][k]*wid+bounds[0][s]] >>> 16) & 0xFF);
            averagedG+=((frame[bounds[1][k]*wid+bounds[0][s]] >>> 8) & 0xFF);
            averagedB+=(frame[bounds[1][k]*wid+bounds[0][s]] & 0xFF);
          }
             
          averagedA+=(frame[bounds[1][0]*wid+screenBounds[0][j]] >>> 24);
          averagedA+=(frame[bounds[1][1]*wid+screenBounds[0][j]] >>> 24);
          averagedA+=(frame[i*wid+bounds[0][0]] >>> 24);
          averagedA+=(frame[i*wid+bounds[0][1]] >>> 24);
          
          averagedR+=((frame[bounds[1][0]*wid+screenBounds[0][j]] >>> 16) & 0xFF);
          averagedR+=((frame[bounds[1][1]*wid+screenBounds[0][j]] >>> 16) & 0xFF);
          averagedR+=((frame[i*wid+bounds[0][0]] >>> 16) & 0xFF);
          averagedR+=((frame[i*wid+bounds[0][1]] >>> 16) & 0xFF);
                
          averagedG+=((frame[bounds[1][0]*wid+screenBounds[0][j]] >>> 8) & 0xFF);
          averagedG+=((frame[bounds[1][1]*wid+screenBounds[0][j]] >>> 8) & 0xFF);
          averagedG+=((frame[i*wid+bounds[0][0]] >>> 8) & 0xFF);
          averagedG+=((frame[i*wid+bounds[0][1]] >>> 8) & 0xFF);
                
          averagedB+=(frame[bounds[1][0]*wid+screenBounds[0][j]] & 0xFF);
          averagedB+=(frame[bounds[1][1]*wid+screenBounds[0][j]] & 0xFF);
          averagedB+=(frame[i*wid+bounds[0][0]] & 0xFF);
          averagedB+=(frame[i*wid+bounds[0][1]] & 0xFF);

          averagedA = ((averagedA/9) & 255) << 24;
          averagedR = ((averagedR/9) & 255) << 16;
          averagedG = ((averagedG/9) & 255) << 8;
          averagedB = ((averagedB/9) & 255);
          frame[i*wid+screenBounds[0][j]] = averagedA|averagedR|averagedG|averagedB;
        }
      }
    }
  }
  
  /*
  Selects a return state and returns if the stencil buffer at that pixel passes
  
  STATES
  p = always pass
  f = always fail
  l = less than
  e = equal to
  g = greater than
  m = greater than or equal to
  q = less than or equal to
  n = not equal to
  */
  public static void stencilTest(int index, byte compVal, char state){
    switch(state){
       case 'p':
         flags|=1;
         break;
       case 'f':
         flags&=-2;
         break;
       case 'l':
         flags = (byte)(((stencil[index] & stencilMask) < (compVal & stencilMask)) ? flags|1 : flags&-2);
         break;
       case 'e':
         flags = (byte)(((stencil[index] & stencilMask) == (compVal & stencilMask)) ? flags|1 : flags&-2);
         break;
       case 'g':
         flags = (byte)(((stencil[index] & stencilMask) > (compVal & stencilMask)) ? flags|1 : flags&-2);
         break;
       case 'n':
         flags = (byte)(((stencil[index] & stencilMask) != (compVal & stencilMask)) ? flags|1 : flags&-2);
         break;
       case 'q':
         flags = (byte)(((stencil[index] & stencilMask) <= (compVal & stencilMask)) ? flags|1 : flags&-2);
         break;
       case 'm':
         flags = (byte)(((stencil[index] & stencilMask) >= (compVal & stencilMask)) ? flags|1 : flags&-2);
         break;
    }
  }
  
  /*
  Determines what should happen if the stencil test passes or fails
  Zeros out the pixel's stencil if action is zero.  Otherwise, bits
  1 increments the pixel's stencil
  2 decrements the pixel's stencil
  4 wraps the stencil around
  8 inverts the bits
  16 hard sets the stencil to the comparison value
  */
  public static void stencilTestAction(int index, byte action, byte compVal, char state){
    if((state == 'p' && (flags & 1) == 1) || (state == 'f' && (flags & 1) == 0)){
      switch(action){
        case 0:
          stencil[index] = 0;
          break;
        case 1:
          stencil[index]++;
          break;
        case 5:
          stencil[index] = (byte)((stencil[index]+1 >= 0) ? stencil[index]+1 : 0);
          break;
        case 2:
          stencil[index]--;
          break;
        case 6:
          stencil[index] = (byte)((stencil[index]-1 >= 0) ? stencil[index]-1 : 127);
          break;
        case 8:
          stencil[index] = (byte)(~stencil[index]);
          break; 
        case 16:
          stencil[index] = compVal;
          break;
      }
    }
  }
  public static boolean returnStencilTestResult(){
    return (flags & 1) == 1;
  }
  //Baarycentric coordinates
  //Weight contributed by the first vertex
  public static float returnAlpha(float x1, float y1, float x2, float y2, float x3, float y3, float pX, float pY){
    float denominator = (y2 - y3)*(x1 - x3) + (x3 - x2)*(y1 - y3);
    if(Math.abs(denominator) <= 0.0001){
      System.out.println("ERROR: DIV BY 0");
      System.exit(1);
    }
    float numerator = (y2 - y3)*(pX - x3) + (x3 - x2)*(pY - y3);
    return numerator/denominator;
  }
  //Weight contributed by the second vertex
  public static float returnBeta(float x1, float y1, float x2, float y2, float x3, float y3, float pX, float pY){
    float denominator = (y2 - y3)*(x1 - x3) + (x3 - x2)*(y1 - y3);
    if(Math.abs(denominator) <= 0.0001){
      System.out.println("ERROR: DIV BY 0");
      System.exit(1);
    }
    float numerator = (y3 - y1)*(pX - x3) + (x1 - x3)*(pY - y3);
    return numerator/denominator;
  }
  //Weight contributed by the third vertex (exploits how Barycentric coordinates all add up to one)
  public static float returnGamma(float alpha, float beta){
     return 1-alpha-beta; 
  }
  //Returns the weights in an array
  public static float[] returnCoords(float x1, float y1, float x2, float y2, float x3, float y3, float x, float y){
    //Alpha weight
    float denominator = (y2 - y3)*(x1 - x3) + (x3 - x2)*(y1 - y3);
    if(Math.abs(denominator) <= 0.0001){
      System.out.println("ERROR: DIV BY 0");
      System.exit(1);
    }
    float numerator = (y2 - y3)*(x - x3) + (x3 - x2)*(y - y3);
    float[] coords = {0, 0, 0};
    coords[0] = numerator/denominator;
    //Beta weight
    denominator = (y2 - y3)*(x1 - x3) + (x3 - x2)*(y1 - y3);
    if(Math.abs(denominator) <= 0.0001){
      System.out.println("ERROR: DIV BY 0");
      System.exit(1);
    }
    numerator = (y3 - y1)*(x - x3) + (x1 - x3)*(y - y3);
    coords[1] = numerator/denominator;
    //Gamma weight
    coords[3] = 1-coords[0]-coords[1];
    return coords;
  }
  //End of Barycentric coordinates
  
  //Computes the average of two pixels using pixel A's alpha channel
  public static int interpolatePixels(int pixelA, int pixelB){
    //Computing t and 1-t for pixelA
    float alpha = (pixelA >>> 24)*0.003921569f;
    //Extracting the individual channels' data for pixel A and pixel B
    int[][] channels = {{pixelA >>> 24, pixelB >>> 24}, {(pixelA >>> 16) & 0xFF, (pixelB >>> 16) & 0xFF}, {(pixelA >>> 8) & 0xFF, (pixelB >>> 8) & 0xFF}, {pixelA & 0xFF, pixelB & 0xFF}};
    int[] returnChannels = {0, 0, 0, 0};
    //Linearly interpolating between pixel B's channels and pixel A's channels
    for(byte i = 0; i < 4; i++)
      returnChannels[i] = (int)((channels[i][0]-channels[i][1])*alpha+channels[i][1]) << (8*(3-i));

    //Combining the results
    return returnChannels[0]|returnChannels[1]|returnChannels[2]|returnChannels[3];
  }
  public static int interpolatePixels(int pixelA, int pixelB, int stencilIndex){
    //Computing t and 1-t for pixelA
    float alpha = (pixelA >>> 24)*0.003921569f;
    //Getting the normalized stencil value
    float stencilNorm = ((~stencil[stencilIndex]) & 0xFF)*0.003921569f;
    //Extracting the individual channels' data for pixel A and pixel B
    int[][] channels = {{pixelA >>> 24, pixelB >>> 24}, {(pixelA >>> 16) & 0xFF, (pixelB >>> 16) & 0xFF}, {(pixelA >>> 8) & 0xFF, (pixelB >>> 8) & 0xFF}, {pixelA & 0xFF, pixelB & 0xFF}};
    int[] returnChannels = {0, 0, 0, 0};
    //Linearly interpolating between pixel B's channels and pixel A's channels
    for(byte i = 0; i < 4; i++){
      returnChannels[i] = (int)((channels[i][0]-channels[i][1])*alpha+channels[i][1]);
      if(i > 0)
        returnChannels[i] = (int)(returnChannels[i]*stencilNorm);
      returnChannels[i]<<=(8*(3-i));
    }
    
    //Combining the results
    return returnChannels[0]|returnChannels[1]|returnChannels[2]|returnChannels[3];
  }

  //Computes the average of two pixels using pixel A's alpha channel
  public static int interpolatePixels(int pixelA, int pixelB, float alpha){
      //Extracting the individual channels' data for pixel A and pixel B
      int[][] channels = {{pixelA >>> 24, pixelB >>> 24}, {(pixelA >>> 16) & 0xFF, (pixelB >>> 16) & 0xFF}, {(pixelA >>> 8) & 0xFF, (pixelB >>> 8) & 0xFF}, {pixelA & 0xFF, pixelB & 0xFF}};
      int[] returnChannels = {0, 0, 0, 0};
      //Linearly interpolating between pixel B's channels and pixel A's channels
      for(byte i = 0; i < 4; i++)
        returnChannels[i] = (int)((channels[i][0]-channels[i][1])*alpha+channels[i][1]) << (8*(3-i));
  
      //Combining the results
      return returnChannels[0]|returnChannels[1]|returnChannels[2]|returnChannels[3];
    }
    public static int interpolatePixels(int pixelA, int pixelB, int stencilIndex, float alpha){
      //Getting the normalized stencil value
      float stencilNorm = ((~stencil[stencilIndex]) & 0xFF)*0.003921569f;
      //Extracting the individual channels' data for pixel A and pixel B
      int[][] channels = {{pixelA >>> 24, pixelB >>> 24}, {(pixelA >>> 16) & 0xFF, (pixelB >>> 16) & 0xFF}, {(pixelA >>> 8) & 0xFF, (pixelB >>> 8) & 0xFF}, {pixelA & 0xFF, pixelB & 0xFF}};
      int[] returnChannels = {0, 0, 0, 0};
      //Linearly interpolating between pixel B's channels and pixel A's channels
      for(byte i = 0; i < 4; i++){
        returnChannels[i] = (int)((channels[i][0]-channels[i][1])*alpha+channels[i][1]);
        if(i > 0)
          returnChannels[i] = (int)(returnChannels[i]*stencilNorm);
        returnChannels[i]<<=(8*(3-i));
      }
      
      //Combining the results
      return returnChannels[0]|returnChannels[1]|returnChannels[2]|returnChannels[3];
    }


  //Bresenham's line algorithm
  //https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
  public static void drawLine(int[] surface, int surWid, int surHeig, int x1, int y1, int x2, int y2, int lineColour){
    int edgeDirX = x1 < x2 ? 1 : -1; //The direction of the line along the x-axis
    int edgeDirY = y1 < y2 ? 1 : -1; //The direction of the line along the y-axis
    int dx = Math.abs(x2-x1); //Difference between x2 and x1
    int dy = -Math.abs(y2-y1); //Difference between y2 and y1 (negated to account for how down is positive and up is negative)
    int error = dx+dy; //Sum of the differences between x2 and x1 and y2 and y1
    while(true){
      if(y1 >= 0 && y1 < surHeig && x1 >= 0 && x1 < surWid){
        //Interpolate between the line colour and the current pixel if the line colour's alpha channel is less than 255. Otherwise, simply overwrite the current pixel with the line colour
        if((lineColour >>> 24) < 0xFF){
          int pixelPos = surWid*y1+x1;
          surface[pixelPos] = interpolatePixels(lineColour, surface[pixelPos]);
        }
        else
          surface[surWid*y1+x1] = lineColour;
      }
      //Early break when the two points are the same
      if(x1 == x2 && y1 == y2)
        break;
        
      final int error2 = 2*error; //Taking twice the error
      //For when twice the error is greater than the negative difference between y2 and y1
      if(error2 >= dy){
        //Verticle line
        if(x1 == x2)
           break;
        error+=dy;
        x1+=edgeDirX;
      }
      //For when twice the error is less than the difference between x2 and x1
      if(error2 <= dx){
        //Horizontal line
        if(y1 == y2)
          break;
        error+=dx;
        y1+=edgeDirY;
      }
    }
  }
  public static void drawLine(int[] surface, int surWid, int surHeig, int x1, int y1, float z1, int x2, int y2, float z2, int lineColour, boolean noDepth){
    int edgeDirX = x1 < x2 ? 1 : -1; //The direction of the line along the x-axis
    int edgeDirY = y1 < y2 ? 1 : -1; //The direction of the line along the y-axis
    int dx = Math.abs(x2-x1); //Difference between x2 and x1
    int dy = -Math.abs(y2-y1); //Difference between y2 and y1 (negated to account for how down is positive and up is negative)
    int error = dx+dy; //Sum of the differences between x2 and x1 and y2 and y1
    float vectorMag = (float)(Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1))); //Calculating the length of the line as if it were 2D
    if(Math.abs(vectorMag) > 0.0001f){
      float z = (Math.abs(z1) > 0.0001) ? 1f/z1 : 0;
      while(true){
        if(noDepth)
          z*=-1;
        if(y1 >= 0 && y1 < surHeig && x1 >= 0 && x1 < surWid && (((noDepth && (z >= zBuff[surWid*y1+x1] || zBuff[surWid*y1+x1] >= 0)) || (!noDepth && z <= zBuff[surWid*y1+x1])) || Float.isNaN(zBuff[surWid*y1+x1]))){
          //Interpolate between the line colour and the current pixel if the line colour's alpha channel is less than 255. Otherwise, simply overwrite the current pixel with the line colour
          if((lineColour >>> 24) < 0xFF){
            int pixelPos = surWid*y1+x1;
            surface[pixelPos] = interpolatePixels(lineColour, surface[pixelPos]);
          }
          else
            surface[surWid*y1+x1] = lineColour;
        }
        //Early break when the two points are the same
        if(x1 == x2 && y1 == y2)
          break;
          
        final int error2 = 2*error; //Taking twice the error
        //For when twice the error is greater than the negative difference between y2 and y1
        if(error2 >= dy){
          //Verticle line
          if(x1 == x2)
            break;
          error+=dy;
          x1+=edgeDirX;
        }
        //For when twice the error is less than the difference between x2 and x1
        if(error2 <= dx){
          //Horizontal line
          if(y1 == y2)
            break;
          error+=dx;
          y1+=edgeDirY;
        }
        //Calculating the depth of the line at a particular pixel
        float numerator = (float)(Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1)));
        if(Math.abs(numerator) > 0.0001 && Math.abs(z1 - z2) > 0.0001)
          z = 1/((z1 - z2)*(numerator/vectorMag) + z2)-0.0001f;
      }
    }
  }
}
