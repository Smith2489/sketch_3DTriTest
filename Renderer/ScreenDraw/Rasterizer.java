package Renderer.ScreenDraw;
import Wrapper.*;
import Renderer.Objects.SceneEntities.*;
//Draws triangles to a frame buffer
public class Rasterizer{
  private static int halfWidth = 50;
  private static int halfHeight = 50;
  private static int[] frame = new int[10000];
  private static float[] zBuff = new float[10000]; //Z-buffer
  private static byte[] stencil = new byte[10000]; //Stencil buffer (why is stencil such a weirdly spelt word?) 00000000 = completely black (no draw), 11111111 = completely white (draw), in between values should affect the brightness
  private static int wid = 100; //Width
  private static byte stencilMask = -1; //A mask for the stencil test
  private static int heig = 100; //Height
  private static int background = 0xFF000000; //Background colour
  private static float maxProbability = 1;
  private static float threshold = 1.1f;
  private static float[] invZ = {0, 0, 0};
  //Weights contributed to a pixel by the vertices
  private static float alpha = 0;
  private static float beta = 0;
  private static float gamma = 1;
  private static int fill = 0; //Fill colour
  private static int stroke = 0; //Outline colour
  private static int[] brokenUpColour = {0, 0, 0, 0};
  private static int[] brokenUpFill = {0, 0, 0};
  private static float alphaNorm = 0;
  /*
    bit 0 = stencil test results
    bit 1 = anti aliasing 
    bit 2 = depthWrite
    bit 3 = has stroke
    bit 4 = has fill
  */
  private static byte flags = 0b0001110;

  //Stores how much light is reflected off of each vertex for Gouraud shading
  private static float[][] vertexBrightness = {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}};

  //Sets the brightness of each vertex
  public static void setVertexBrightness(float[][] brightnessLevels){
    vertexBrightness[0][0] = brightnessLevels[0][0];
    vertexBrightness[0][1] = brightnessLevels[0][1];
    vertexBrightness[0][2] = brightnessLevels[0][2];
    vertexBrightness[1][0] = brightnessLevels[1][0];
    vertexBrightness[1][1] = brightnessLevels[1][1];
    vertexBrightness[1][2] = brightnessLevels[1][2];
    vertexBrightness[2][0] = brightnessLevels[2][0];
    vertexBrightness[2][1] = brightnessLevels[2][1];
    vertexBrightness[2][2] = brightnessLevels[2][2];
  }

  //Initial z-buffer and frame buffer initialization
  public static void initBuffers(int width, int height){
     wid = width;
     heig = height;
     halfWidth = width >>> 1;
     halfHeight = height >>> 1;
     zBuff = new float[width*height];
     stencil = new byte[width*height];
     for(int i = 0; i < width*height; i++){
       zBuff[i] = Float.NaN;
       stencil[i] = 0; 
     }
  }
  
  //Solid colour reset
  public static void initBuffers(int[] newFrameBuff){
    frame = newFrameBuff;
    for(int i = 0; i < zBuff.length; i++){
      zBuff[i] = Float.NaN;
      frame[i] = background; 
      stencil[i] = 0;
    }
  }
  
  //Reset with a background image
  public static void initBuffers(){
    for(int i = 0; i < zBuff.length; i++){
      zBuff[i] = Float.NaN;
      stencil[i] = 0;
    }
  }
  public static void setFrameRef(int[] newFrameBuff){
    frame = newFrameBuff;
  }
  public static void setProbabilities(float newMax, float newThreshold){
    maxProbability = newMax;
    threshold = newThreshold;
  }

  public static int halfWidth(){
    return halfWidth;
  }
  public static int halfHeight(){
    return halfHeight;
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

  //Setting the fill for the triangles
  public static void fill(short r, short g, short b){
    flags|=16;
    r&=0xFF;
    g&=0xFF;
    b&=0xFF;
    fill = 0xFF000000|(r << 16)|(g << 8)|b;
    brokenUpColour[0] = 0xFF;
    alphaNorm = 1;
    brokenUpFill[0] = r;
    brokenUpFill[1] = g;
    brokenUpFill[2] = b;
  }
  public static void fill(short r, short g, short b, short a){
    flags|=16;
    r&=0xFF;
    g&=0xFF;
    b&=0xFF;
    a&=0xFF;
    fill = (a << 24)|(r << 16)|(g << 8)|b;
    brokenUpColour[0] = a;
    alphaNorm = a*Colour.INV_255;
    brokenUpFill[0] = r;
    brokenUpFill[1] = g;
    brokenUpFill[2] = b;
  }
  public static void fill(int colour){
    flags|=16;
    if((colour >>> 24) == 0){
      if(colour <= 0xFF)
          colour = 0xFF000000 | (colour << 16) | (colour << 8) | colour;
      else if(colour <= 0xFFFF)
          colour = ((colour & 0xFF00) << 16) | ((colour & 0xFF) << 16) | ((colour & 0xFF) << 8) | (colour & 0xFF);
      else
          colour = 0xFF000000 | colour;
    }
    fill = colour;
    brokenUpColour[0] = fill >>> 24;
    alphaNorm = brokenUpColour[0]*Colour.INV_255;
    brokenUpFill[0] = (fill >>> 16) & 0xFF;
    brokenUpFill[1] = (fill >>> 8) & 0xFF;
    brokenUpFill[2] = fill & 0xFF;
  }
  public static void fill(int colour, short alpha){
    flags|=16;
    alpha&=0xFF;
    colour = (colour & 0xFFFFFF);
    if(colour > 0xFF)
      fill = (alpha << 24)|colour;
    else
      fill = (alpha << 24)|(colour << 16)|(colour << 8)|colour;
    brokenUpColour[0] = alpha;
    alphaNorm = alpha*Colour.INV_255;
    brokenUpFill[0] = (fill >>> 16) & 0xFF;
    brokenUpFill[1] = (fill >>> 8) & 0xFF;
    brokenUpFill[2] = fill & 0xFF;
  }
  
  //Setting the stroke colour of the triangles
  public static void stroke(short r, short g, short b){
    flags|=8;
    r&=0xFF;
    g&=0xFF;
    b&=0xFF;
    stroke = 0xFF000000|(r << 16)|(g << 8)|b;
  }
  public static void stroke(short r, short g, short b, short a){
    flags|=8;
    r&=0xFF;
    g&=0xFF;
    b&=0xFF;
    a&=0xFF;
    stroke = (a << 24)|(r << 16)|(g << 8)|b;
  }
  public static void stroke(int colour){
    flags|=8;
    if((colour >>> 24) == 0){
      if(colour <= 0xFF)
          colour = 0xFF000000 | (colour << 16) | (colour << 8) | colour;
      else if(colour <= 0xFFFF)
          colour = ((colour & 0xFF00) << 16) | ((colour & 0xFF) << 16) | ((colour & 0xFF) << 8) | (colour & 0xFF);
      else
          colour = 0xFF000000 | colour;
    }
    stroke = colour;
  }
  public static void stroke(int colour, short alpha){
    flags|=8;
    alpha&=0xFF;
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

  public static void setDepthWrite(boolean newDepthWrite){
    // depthWrite = newDepthWrite;
    if(newDepthWrite)
      flags|=4;
    else
      flags&=-5;
  }
  
  //Determines if the x and y are valid and plots a pixel
  public static void setPixelFill(int x, int y){
    if((flags & 16) == 16 && x >= 0 && x < wid && y >= 0 && y < heig)
      //Interpolate between the fill and the current pixel if the fill's alpha channel is less than 255. Otherwise, simply overwrite the current pixel with the fill
      if((fill >>> 24) < 0xFF)
        frame[y*wid+x] = Colour.interpolateColours(fill, frame[y*wid+x]);
      else
        frame[y*wid+x] = fill;
  }
  public static void setPixelStroke(int x, int y){
    if((flags & 8) == 8 && x >= 0 && x < wid && y >= 0 && y < heig)
    //Interpolate between the stroke and the current pixel if the stroke's alpha channel is less than 255. Otherwise, simply overwrite the current pixel with the stroke
      if((stroke >>> 24) < 0xFF)
        frame[y*wid+x] = Colour.interpolateColours(stroke, frame[y*wid+x]);
      else
        frame[y*wid+x] = stroke;
  }
  
  //Determines if the x and y are valid and plots a pixel
  public static void setPixelFill(float xPos, float yPos, float zPos, boolean noDraw){
    int x = (int)xPos;
    int y = (int)yPos;
    if((flags & 16) == 16 && x >= 0 && x < wid && y >= 0 && y < heig){
      if(noDraw)
        zPos*=-1;
      int pixelPos = x+y*wid;
      if((noDraw && zPos < zBuff[pixelPos]) || (!noDraw && zPos > zBuff[pixelPos]) || Float.isNaN(zBuff[pixelPos])){
          //Interpolate between the fill and the current pixel if the fill's alpha channel is less than 255. Otherwise, simply overwrite the current pixel with the fill
          if((fill >>> 24) < 0xFF)
            frame[pixelPos] = Colour.interpolateColours(fill, frame[pixelPos]);
          else
            frame[pixelPos] = fill;
          zBuff[pixelPos] = zPos;
      }
      else{
        if((frame[pixelPos] >>> 24) < 0xFF)
          frame[pixelPos] = Colour.interpolateColours(frame[pixelPos], fill);
      }
    }
  }
  public static void setPixelStroke(float xPos, float yPos, float zPos, boolean noDraw){
    int x = (int)xPos;
    int y = (int)yPos;
    if((flags & 8) == 8 && x >= 0 && x < wid && y >= 0 && y < heig){
      if(noDraw)
        zPos*=-1;
      int pixelPos = x+y*wid;
      if((noDraw && zPos < zBuff[pixelPos]) || (!noDraw && zPos > zBuff[pixelPos]) || Float.isNaN(zBuff[pixelPos])){
          //Interpolate between the stroke and the current pixel if the stroke's alpha channel is less than 255. Otherwise, simply overwrite the current pixel with the stroke
          if((stroke >>> 24) < 0xFF)
            frame[pixelPos] = Colour.interpolateColours(stroke, frame[pixelPos]);
          else
            frame[pixelPos] = stroke;
          zBuff[pixelPos] = zPos;
      }
      else{
        if((frame[pixelPos] >>> 24) < 0xFF)
          frame[pixelPos] = Colour.interpolateColours(frame[pixelPos], stroke);
      }
    }
  }
  public static void setPixel(int colour, float xPos, float yPos, float zPos, boolean noDraw){
    int x = (int)xPos;
    int y = (int)yPos;
    if(x >= 0 && x < wid && y >= 0 && y < heig){
      if(noDraw)
        zPos*=-1;
      int pixelPos = x+y*wid;
      if((noDraw && zPos < zBuff[pixelPos]) || (!noDraw && zPos > zBuff[pixelPos]) || Float.isNaN(zBuff[pixelPos])){
        //Interpolate between the stroke and the current pixel if the colour's alpha channel is less than 255. Otherwise, simply overwrite the current pixel with the stroke
        if((colour >>> 24) < 0xFF)
          frame[pixelPos] = Colour.interpolateColours(colour, frame[pixelPos]);
        else
          frame[pixelPos] = colour;
        zBuff[pixelPos] = zPos;
      }
      else{
        if((frame[pixelPos] >>> 24) < 0xFF)
          frame[pixelPos] = Colour.interpolateColours(frame[pixelPos], colour);
      }
    }
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
  public static void draw(float p1X, float p1Y, float p2X, float p2Y, float p3X, float p3Y){
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
        y = i+0.5f;
        computeEdges(y, minX, maxX, screenBounds, poses, interpolatedEdges);

        //Filling the scanline
        for(int j = screenBounds[0][0]; j < screenBounds[0][1]; j++){
          if(maxProbability <= threshold || Math.random()*maxProbability < threshold){
            int pixelPos = wid*i+j;
            int[] brokenUpFrame = {frame[pixelPos] >>> 24, 
                                  (frame[pixelPos] >>> 16) & 0xFF, 
                                  (frame[pixelPos] >>> 8) & 0xFF, 
                                  frame[pixelPos] & 0xFF};
            //Interpolating the current pixel and the fill if the fill's alpha is less than 255. Otherwise, overwrite the current pixel's data with the fill
            if(brokenUpColour[0] < 0xFF)
              frame[pixelPos] = Colour.interpolateColours(brokenUpFill, brokenUpFrame, alphaNorm);
            else
              frame[pixelPos] = fill;
          }
         }
       }
     }
     //Drawing the outline
     if((flags & 8) == 8){
       //Setting the start and end points
       IntWrapper[] xE = {new IntWrapper(Math.round(p1X)), new IntWrapper(Math.round(p2X)), new IntWrapper(Math.round(p3X))};
       IntWrapper[] yE = {new IntWrapper(Math.round(p1Y)), new IntWrapper(Math.round(p2Y)), new IntWrapper(Math.round(p3Y))};
       final IntWrapper[] endX = {new IntWrapper(xE[1].val), new IntWrapper(xE[2].val), new IntWrapper(xE[0].val)};
       final IntWrapper[] endY = {new IntWrapper(yE[1].val), new IntWrapper(yE[2].val), new IntWrapper(yE[0].val)};
       //Drawing each side using Bresenham's line algorithm
       for(byte i = 0; i < 3; i++)
         drawLine(xE[i], yE[i], endX[i], endY[i], stroke);
    }
  }
  //Drawing a triangle with 3D points (points directly in parametres)
  public static void draw(float p1X, float p1Y, float p1Z, float p1W, float p2X, float p2Y, float p2Z, float p2W, float p3X, float p3Y, float p3Z, float p3W){
    //Setting up the bounding box
    int[][] screenBounds = {{0x80000000, 0x7FFFFFF}, {0x80000000, 0x7FFFFFF}};
    //Makes forming the BB and rasterizing the triangle easier
    float[][] poses = {{p1X, p1Y}, 
                       {p2X, p2Y}, 
                       {p3X, p3Y}};
    if(Math.abs(p1Z*p1W) > 0.0000001)
      invZ[0] = 1/(p1Z*p1W);
    else
      invZ[0] = 0.0000001f;
    if(Math.abs(p2Z*p2W) > 0.0000001)
      invZ[1] = 1/(p2Z*p2W);
    else
      invZ[1] = 0.0000001f;
    if(Math.abs(p3Z*p3W) > 0.0000001)
      invZ[2] = 1/(p3Z*p3W);
    else
      invZ[2] = 0.0000001f;

    p1Z*=(((flags & 4) >>> 1)-1);
    p2Z*=(((flags & 4) >>> 1)-1);
    p3Z*=(((flags & 4) >>> 1)-1);

    //Constructing the triangle's bounding box
    screenBounds[0][0] = Math.round(Math.max(0, Math.min(poses[0][0], Math.min(poses[1][0], poses[2][0]))));
    screenBounds[0][1] = Math.round(Math.min(wid, Math.max(poses[0][0], Math.max(poses[1][0], poses[2][0]))));
    screenBounds[1][0] = Math.round(Math.max(0, Math.min(poses[0][1], Math.min(poses[1][1], poses[2][1]))));
    screenBounds[1][1] = Math.round(Math.min(heig, Math.max(poses[0][1], Math.max(poses[1][1], poses[2][1]))));
    int maxX = screenBounds[0][1];
    int minX = screenBounds[0][0];

      
    //Used for centring a pixel
    float x = 0;
    float y = 0;
      
    //The edges that will be iterated over
    float[] interpolatedEdges = {0, 0}; //i0 = lowest-mid, i1 = mid-highest, i2 = highest-lowest
    //Filling in the triangle
    if((flags & 16) == 16){
      
      //Iterating over the BB
      for(int i = screenBounds[1][0]; i < screenBounds[1][1]; i++){
        y = i+0.5f;
        computeEdges(y, minX, maxX, screenBounds, poses, interpolatedEdges);
        //Filling the scanline
        for(int j = screenBounds[0][0]; j < screenBounds[0][1]; j++){
          int pixelPos = wid*i+j;
          if(stencil[pixelPos] == 0 && (maxProbability <= threshold || Math.random()*maxProbability < threshold)){
            //Centring the pixel
            x = j+0.5f;
            //Calculating the weight each vertex contributes to the pixel
            alpha = returnAlpha(poses[0][0], poses[0][1], poses[1][0], poses[1][1], poses[2][0], poses[2][1], x, y);
            beta = returnBeta(poses[0][0], poses[0][1], poses[1][0], poses[1][1], poses[2][0], poses[2][1], x, y);
            gamma = returnGamma(alpha, beta);
            //Plotting the pixel
            float z = (p1Z*alpha + p2Z*beta + p3Z*gamma); //Barycentric z
            float tempZ = invZ[0]*alpha + invZ[1]*beta + invZ[2]*gamma-0.0000001f;
            if(tempZ > 0.0000001f)
              tempZ = 1/tempZ;
            else
              tempZ = 0.0000001f;
            //if(!depthWrite)
            //z*=(((flags & 4) >>> 1)-1);
            int[] brokenUpFrame = {frame[pixelPos] >>> 24, 
                                  (frame[pixelPos] >>> 16) & 0xFF, 
                                  (frame[pixelPos] >>> 8) & 0xFF, 
                                  frame[pixelPos] & 0xFF};
            //For when the current triangle is closest at the current pixel than any previous triangle
            if(((flags & 4) == 0 && z < zBuff[pixelPos] || (flags & 4) == 4 && z > zBuff[pixelPos] || Float.isNaN(zBuff[pixelPos]))){
              computeLighting(tempZ, invZ, vertexBrightness);
              //Interpolating the current pixel and the fill if the fill's alpha is less than 255. Otherwise, overwrite the current pixel's data with the fill
              if(brokenUpColour[0] < 0xFF)
                frame[pixelPos] = Colour.interpolateColours(brokenUpColour, brokenUpFrame, alphaNorm);
              else
                frame[pixelPos] = (brokenUpColour[0] << 24)|(brokenUpColour[1] << 16)|(brokenUpColour[2] << 8)|brokenUpColour[3];
              zBuff[pixelPos] = z;
            }
            //For when there were triangles drawn at the current pixel that were closer than the current triangle
            else if(brokenUpFrame[0] < 0xFF){
                computeLighting(tempZ, invZ, vertexBrightness);
                frame[pixelPos] = Colour.interpolateColours(brokenUpFrame, brokenUpColour);
            }
          }
         }
       }
     }
     //Drawing the outline
     if((flags & 8) == 8){
       //Setting up the endpoints of each side
       IntWrapper[] xE = {new IntWrapper(Math.round(p1X)), new IntWrapper(Math.round(p2X)), new IntWrapper(Math.round(p3X))};
       IntWrapper[] yE = {new IntWrapper(Math.round(p1Y)), new IntWrapper(Math.round(p2Y)), new IntWrapper(Math.round(p3Y))};
       float[] zE = {p1Z+0.0004f, p2Z+0.0004f, p3Z+0.0004f};
       final IntWrapper[] endX = {new IntWrapper(xE[1].val), new IntWrapper(xE[2].val), new IntWrapper(xE[0].val)};
       final IntWrapper[] endY = {new IntWrapper(yE[1].val), new IntWrapper(yE[2].val), new IntWrapper(yE[0].val)};
       final float[] endZ = {zE[1], zE[2], zE[0]};
       //Drawing each side with Bresenham's line algorithm
       for(byte i = 0; i < 3; i++)
         drawLine(xE[i], yE[i], zE[i], endX[i], endY[i], endZ[i], stroke, (flags & 4) == 0);
    }
  }
  
  public static void draw(Triangle triangle){
    //Setting up the colour
    stroke = triangle.getStroke();
    fill = triangle.getFill();
    brokenUpColour[0] = fill >>> 24;
    alphaNorm = brokenUpColour[0]*Colour.INV_255;
    brokenUpFill[0] = (fill >>> 16) & 0xFF;
    brokenUpFill[1] = (fill >>> 8) & 0xFF;
    brokenUpFill[2] = fill & 0xFF;
    flags = (byte)(((triangle.getHasStroke()) ? flags|8 : flags&-9));
    flags = (byte)(((triangle.getHasFill()) ? flags|16 : flags&-17));
    
    //Setting up the bounding box
    int[][] screenBounds = {{0x80000000, 0x7FFFFFF}, {0x80000000, 0x7FFFFFF}};
    //Makes forming the BB and rasterizing the triangle easier
    float[][] poses = {{triangle.getVertices()[0][0], triangle.getVertices()[0][1], triangle.getVertices()[0][2]}, 
                        {triangle.getVertices()[1][0], triangle.getVertices()[1][1], triangle.getVertices()[1][2]}, 
                        {triangle.getVertices()[2][0], triangle.getVertices()[2][1], triangle.getVertices()[2][2]}};
    if(Math.abs(poses[0][2]*triangle.getVertices()[0][3]) > 0.0000001)
      invZ[0] = 1/(poses[0][2]*triangle.getVertices()[0][3]);
    else
      invZ[0] = 0.0000001f;
    if(Math.abs(poses[1][2]*triangle.getVertices()[1][3]) > 0.0000001)
      invZ[1] = 1/(poses[1][2]*triangle.getVertices()[1][3]);
    else
      invZ[1] = 0.0000001f;
    if(Math.abs(poses[2][2]*triangle.getVertices()[2][3]) > 0.0000001)
      invZ[2] = 1/(poses[2][2]*triangle.getVertices()[2][3]);
    else
      invZ[2] = 0.0000001f;
    poses[0][2]*=(((flags & 4) >>> 1)-1);
    poses[1][2]*=(((flags & 4) >>> 1)-1);
    poses[2][2]*=(((flags & 4) >>> 1)-1);


    //Constructing the triangle's bounding box
    screenBounds[0][0] = Math.round(Math.max(0, Math.min(poses[0][0], Math.min(poses[1][0], poses[2][0]))));
    screenBounds[0][1] = Math.round(Math.min(wid, Math.max(poses[0][0], Math.max(poses[1][0], poses[2][0]))));
    screenBounds[1][0] = Math.round(Math.max(0, Math.min(poses[0][1], Math.min(poses[1][1], poses[2][1]))));
    screenBounds[1][1] = Math.round(Math.min(heig, Math.max(poses[0][1], Math.max(poses[1][1], poses[2][1]))));
    int maxX = screenBounds[0][1];
    int minX = screenBounds[0][0];
      
    //Used for centring a pixel
    float x = 0;
    float y = 0;
      
    //The edges that will be iterated over
    float[] interpolatedEdges = {0, 0}; //i0 = lowest-mid, i1 = mid-highest, i2 = highest-lowest
    //Filling in the triangle
    if((flags & 16) == 16){
      
      //Iterating over the BB
      for(int i = screenBounds[1][0]; i < screenBounds[1][1]; i++){
        y = i+0.5f;
        computeEdges(y, minX, maxX, screenBounds, poses, interpolatedEdges);
        //Filling the scanline
        for(int j = screenBounds[0][0]; j < screenBounds[0][1]; j++){
          int pixelPos = wid*i+j;
          if(stencil[pixelPos] == 0 && (maxProbability <= threshold || Math.random()*maxProbability < threshold)){
            //Centring the pixel
            x = j+0.5f;
            //Calculating the weight each vertex contributes to the pixel
            alpha = returnAlpha(poses[0][0], poses[0][1], poses[1][0], poses[1][1], poses[2][0], poses[2][1], x, y);
            beta = returnBeta(poses[0][0], poses[0][1], poses[1][0], poses[1][1], poses[2][0], poses[2][1], x, y);
            gamma = returnGamma(alpha, beta);
            //Plotting the pixel
            float z = (poses[0][2]*alpha + poses[1][2]*beta + poses[2][2]*gamma); //Barycentric z
            float tempZ = invZ[0]*alpha + invZ[1]*beta + invZ[2]*gamma-0.0000001f;
            if(tempZ > 0.0000001f)
              tempZ = 1/tempZ;
            else
              tempZ = 0.0000001f;
            int[] brokenUpFrame = {frame[pixelPos] >>> 24, 
                                   (frame[pixelPos] >>> 16) & 0xFF, 
                                   (frame[pixelPos] >>> 8) & 0xFF, 
                                   frame[pixelPos] & 0xFF};
            //For when the current triangle is closest at the current pixel than any previous triangle
            if(((flags & 4) == 0 && z < zBuff[pixelPos] || (flags & 4) == 4 && z > zBuff[pixelPos] || Float.isNaN(zBuff[pixelPos]))){
              computeLighting(tempZ, invZ, vertexBrightness);
              //Interpolating the current pixel and the fill if the fill's alpha is less than 255. Otherwise, overwrite the current pixel's data with the fill
              if(brokenUpColour[0] < 0xFF)
                frame[pixelPos] = Colour.interpolateColours(brokenUpColour, brokenUpFrame, alphaNorm);
              else
                frame[pixelPos] = (brokenUpColour[0] << 24)|(brokenUpColour[1] << 16)|(brokenUpColour[2] << 8)|brokenUpColour[3];
              zBuff[pixelPos] = z;
            }
            //For when there were triangles drawn at the current pixel that were closer than the current triangle
            else if(brokenUpFrame[0] < 0xFF){
              computeLighting(tempZ, invZ, vertexBrightness);
              frame[pixelPos] = Colour.interpolateColours(brokenUpFrame, brokenUpColour);
            }
          }
        }
       }
     }
     //Drawing the outline
     if((flags & 8) == 8){
       for(byte i = 0; i < 3; i++)
         drawLine(new IntWrapper(Math.round(triangle.getVertices()[i][0])), new IntWrapper(Math.round(triangle.getVertices()[i][1])), triangle.getVertices()[i][2]+0.0004f, new IntWrapper(Math.round(triangle.getVertices()[(i+1)%3][0])), new IntWrapper(Math.round(triangle.getVertices()[(i+1)%3][1])), triangle.getVertices()[(i+1)%3][2]+0.0004f, stroke, (flags & 4) == 0);
    }
  }
  
  //Versions with modifiable stencil tests
  public static void draw(float p1X, float p1Y, float p1Z, float p1W, float p2X, float p2Y, float p2Z, float p2W, float p3X, float p3Y, float p3Z, float p3W, byte compVal, char testType){
    //Triangle fill
    //Setting up the bounding box
    int[][] screenBounds = {{0x80000000, 0x7FFFFFF}, {0x80000000, 0x7FFFFFF}};
    //Makes forming the BB and rasterizing the triangle easier
    float[][] poses = {{p1X, p1Y}, 
                       {p2X, p2Y}, 
                       {p3X, p3Y}};
    if(Math.abs(p1Z*p1W) > 0.0000001)
      invZ[0] = 1/(p1Z*p1W);
    else
      invZ[0] = 0.0000001f;
    if(Math.abs(p2Z*p2W) > 0.0000001)
      invZ[1] = 1/(p2Z*p2W);
    else
      invZ[1] = 0.0000001f;
    if(Math.abs(p3Z*p3W) > 0.0000001)
      invZ[2] = 1/(p3Z*p3W);
    else
      invZ[2] = 0.0000001f;
    p1Z*=(((flags & 4) >>> 1)-1);
    p2Z*=(((flags & 4) >>> 1)-1);
    p3Z*=(((flags & 4) >>> 1)-1);

    //Constructing the triangle's bounding box
    screenBounds[0][0] = Math.round(Math.max(0, Math.min(poses[0][0], Math.min(poses[1][0], poses[2][0]))));
    screenBounds[0][1] = Math.round(Math.min(wid, Math.max(poses[0][0], Math.max(poses[1][0], poses[2][0]))));
    screenBounds[1][0] = Math.round(Math.max(0, Math.min(poses[0][1], Math.min(poses[1][1], poses[2][1]))));
    screenBounds[1][1] = Math.round(Math.min(heig, Math.max(poses[0][1], Math.max(poses[1][1], poses[2][1]))));
    int maxX = screenBounds[0][1];
    int minX = screenBounds[0][0];
      
    //Used for centring a pixel
    float x = 0;
    float y = 0;
      
    //The edges that will be iterated over
    float[] interpolatedEdges = {0, 0}; //i0 = lowest-mid, i1 = mid-highest, i2 = highest-lowest
    //Filling in the triangle
    if((flags & 16) == 16){

      //Iterating over the BB
      for(int i = screenBounds[1][0]; i < screenBounds[1][1]; i++){
        y = i+0.5f;
        computeEdges(y, minX, maxX, screenBounds, poses, interpolatedEdges);

        //Filling the scanline
        for(int j = screenBounds[0][0]; j < screenBounds[0][1]; j++){
          int pixelPos = wid*i+j;

          stencilTest(pixelPos, compVal, testType);
          if((flags & 1) == 1 && (maxProbability <= threshold || Math.random()*maxProbability < threshold)){
            //Centring the pixel
            x = j+0.5f;
            //Calculating the weight each vertex contributes to the pixel
            alpha = returnAlpha(poses[0][0], poses[0][1], poses[1][0], poses[1][1], poses[2][0], poses[2][1], x, y);
            beta = returnBeta(poses[0][0], poses[0][1], poses[1][0], poses[1][1], poses[2][0], poses[2][1], x, y);
            gamma = returnGamma(alpha, beta);
            //Plotting the pixel
            float z = (p1Z*alpha + p2Z*beta + p3Z*gamma); //Barycentric z
            float tempZ = invZ[0]*alpha + invZ[1]*beta + invZ[2]*gamma-0.0000001f;
            if(tempZ > 0.0000001f)
              tempZ = 1/tempZ;
            else
              tempZ = 0.0000001f;
            //if(!depthWrite)
            // z*=(((flags & 4) >>> 1)-1);
            int[] brokenUpFrame = {frame[pixelPos] >>> 24, 
                                   (frame[pixelPos] >>> 16) & 0xFF, 
                                   (frame[pixelPos] >>> 8) & 0xFF, 
                                   frame[pixelPos] & 0xFF};
            //For when the current triangle is closest at the current pixel than any previous triangle
            if(((flags & 4) == 0 && z < zBuff[pixelPos] || (flags & 4) == 4 && z > zBuff[pixelPos] || Float.isNaN(zBuff[pixelPos]))){
              computeLighting(tempZ, invZ, vertexBrightness);
              //Interpolating the current pixel and the fill if the fill's alpha is less than 255. Otherwise, overwrite the current pixel's data with the fill
              if(brokenUpColour[0] < 0xFF)
                frame[pixelPos] = interpolatePixels(brokenUpColour, brokenUpFrame, pixelPos, alphaNorm);
              else{
                //Normalizes the stencil to be between 0 and 1
                float stencilNorm = ((~stencil[pixelPos]) & 0xFF)*Colour.INV_255;
                //Computes the brightness of each pixel
                brokenUpColour[1] = (int)(brokenUpColour[1]*stencilNorm);
                brokenUpColour[2] = (int)(brokenUpColour[2]*stencilNorm);
                brokenUpColour[3] = (int)(brokenUpColour[3]*stencilNorm);
                //Extracts the colour channel data
                //Copies the data into the frame buffer
                frame[pixelPos] = (brokenUpColour[0] << 24)|(brokenUpColour[1] << 16)|(brokenUpColour[2] << 8)|brokenUpColour[3];
              }
              zBuff[pixelPos] = z;
            }
            //For when there were triangles drawn at the current pixel that were closer than the current triangle
            else if(brokenUpFrame[0] < 0xFF){
              computeLighting(tempZ, invZ, vertexBrightness);
              frame[pixelPos] = interpolatePixels(brokenUpFrame, brokenUpColour, pixelPos);
            }
          }
        }
       }
     }
     //Drawing the outline
     if((flags & 8) == 8){
       //Setting up the endpoints of each side
       IntWrapper[] xE = {new IntWrapper(Math.round(p1X)), new IntWrapper(Math.round(p2X)), new IntWrapper(Math.round(p3X))};
       IntWrapper[] yE = {new IntWrapper(Math.round(p1Y)), new IntWrapper(Math.round(p2Y)), new IntWrapper(Math.round(p3Y))};
       final float [] zE = {p1Z+0.0004f, p2Z+0.0004f, p3Z+0.0004f};
       final IntWrapper[] endX = {new IntWrapper(xE[1].val), new IntWrapper(xE[2].val), new IntWrapper(xE[0].val)};
       final IntWrapper[] endY = {new IntWrapper(yE[1].val), new IntWrapper(yE[2].val), new IntWrapper(yE[0].val)};
       final float[] endZ = {zE[1], zE[2], zE[0]};
       //Drawing each side with Bresenham's line algorithm
       for(byte i = 0; i < 3; i++)
         drawLine(xE[i], yE[i], zE[i], endX[i], endY[i], endZ[i], stroke, (flags & 4) == 0);
    }
  }
  
  public static void draw(Triangle triangle, byte compVal, char testType){
    //Setting up the colour
    stroke = triangle.getStroke();
    fill = triangle.getFill();
    brokenUpColour[0] = fill >>> 24;
    alphaNorm = brokenUpColour[0]*Colour.INV_255;
    brokenUpFill[0] = (fill >>> 16) & 0xFF;
    brokenUpFill[1] = (fill >>> 8) & 0xFF;
    brokenUpFill[2] = fill & 0xFF;
    flags = (byte)((triangle.getHasStroke()) ? flags|8 : flags&-9);
    flags = (byte)((triangle.getHasFill()) ? flags|16 : flags&-17);
    //Setting up the bounding box
    int[][] screenBounds = {{0x80000000, 0x7FFFFFF}, {0x80000000, 0x7FFFFFF}};
    //Makes forming the BB and rasterizing the triangle easier
    float[][] poses = {{triangle.getVertices()[0][0], triangle.getVertices()[0][1], triangle.getVertices()[0][2]}, 
                        {triangle.getVertices()[1][0], triangle.getVertices()[1][1], triangle.getVertices()[1][2]}, 
                        {triangle.getVertices()[2][0], triangle.getVertices()[2][1], triangle.getVertices()[2][2]}};
    if(Math.abs(poses[0][2]*triangle.getVertices()[0][3]) > 0.0000001)
      invZ[0] = 1/(poses[0][2]*triangle.getVertices()[0][3]);
    else
      invZ[0] = 0.0000001f;
    if(Math.abs(poses[1][2]*triangle.getVertices()[1][3]) > 0.0000001)
      invZ[1] = 1/(poses[1][2]*triangle.getVertices()[1][3]);
    else
      invZ[1] = 0/0000001f;
    if(Math.abs(poses[2][2]*triangle.getVertices()[2][3]) > 0.0000001)
      invZ[2] = 1/(poses[2][2]*triangle.getVertices()[2][3]);
    else
      invZ[2] = 0.0000001f;
    poses[0][2]*=(((flags & 4) >>> 1)-1);
    poses[1][2]*=(((flags & 4) >>> 1)-1);
    poses[2][2]*=(((flags & 4) >>> 1)-1);
    //Constructing the triangle's bounding box
    screenBounds[0][0] = Math.round(Math.max(0, Math.min(poses[0][0], Math.min(poses[1][0], poses[2][0]))));
    screenBounds[0][1] = Math.round(Math.min(wid, Math.max(poses[0][0], Math.max(poses[1][0], poses[2][0]))));
    screenBounds[1][0] = Math.round(Math.max(0, Math.min(poses[0][1], Math.min(poses[1][1], poses[2][1]))));
    screenBounds[1][1] = Math.round(Math.min(heig, Math.max(poses[0][1], Math.max(poses[1][1], poses[2][1]))));
    int maxX = screenBounds[0][1];
    int minX = screenBounds[0][0];
      
    //Used for centring a pixel
    float x = 0;
    float y = 0;
      
    //The edges that will be iterated over
    float[] interpolatedEdges = {0, 0}; //i0 = lowest-mid, i1 = mid-highest, i2 = highest-lowest
    //Filling in the triangle
    if((flags & 16) == 16){
      //Iterating over the BB
      for(int i = screenBounds[1][0]; i < screenBounds[1][1]; i++){
        y = i+0.5f;
        computeEdges(y, minX, maxX, screenBounds, poses, interpolatedEdges);
        //Filling the scanline
        for(int j = screenBounds[0][0]; j < screenBounds[0][1]; j++){
          int pixelPos = wid*i+j;
          stencilTest(pixelPos, compVal, testType);
          if((flags & 1) == 1 && (maxProbability <= threshold || Math.random()*maxProbability < threshold)){
            //Centring the pixel
            x = j+0.5f;
            //Calculating the weight each vertex contributes to the pixel
            alpha = returnAlpha(poses[0][0], poses[0][1], poses[1][0], poses[1][1], poses[2][0], poses[2][1], x, y);
            beta = returnBeta(poses[0][0], poses[0][1], poses[1][0], poses[1][1], poses[2][0], poses[2][1], x, y);
            gamma = returnGamma(alpha, beta);
            //Plotting the pixel
            float z = (poses[0][2]*alpha + poses[1][2]*beta + poses[2][2]*gamma); //Barycentric z
            float tempZ = invZ[0]*alpha + invZ[1]*beta + invZ[2]*gamma-0.0000001f;
            if(tempZ > 0.0000001f)
              tempZ = 1/tempZ;
            else
              tempZ = 0.0000001f;
            //if(!depthWrite)
            //z*=(((flags & 4) >>> 1)-1);

            int[] brokenUpFrame = {frame[pixelPos] >>> 24, 
                                  (frame[pixelPos] >>> 16) & 0xFF, 
                                  (frame[pixelPos] >>> 8) & 0xFF, 
                                  frame[pixelPos] & 0xFF};

            //For when the current triangle is closest at the current pixel than any previous triangle
            if(((flags & 4) == 0 && z < zBuff[pixelPos] || (flags & 4) == 4 && z > zBuff[pixelPos] || Float.isNaN(zBuff[pixelPos]))){
              computeLighting(tempZ, invZ, vertexBrightness);
              //Interpolating the current pixel and the fill if the fill's alpha is less than 255. Otherwise, overwrite the current pixel's data with the fill
              if(brokenUpColour[0] < 0xFF)
                frame[pixelPos] = interpolatePixels(brokenUpColour, brokenUpFrame, pixelPos, alphaNorm);
              else{
                //Normalizes the stencil to be between 0 and 1
                float stencilNorm = ((~stencil[pixelPos]) & 0xFF)*Colour.INV_255;
                //Computes the brightness of each pixel
                brokenUpColour[1] = (int)(brokenUpColour[1]*stencilNorm);
                brokenUpColour[2] = (int)(brokenUpColour[2]*stencilNorm);
                brokenUpColour[3] = (int)(brokenUpColour[3]*stencilNorm);
                //Copies the data into the frame buffer
                frame[pixelPos] = (brokenUpColour[0] << 24)|(brokenUpColour[1] << 16)|(brokenUpColour[2] << 8)|brokenUpColour[3];
              }
              zBuff[pixelPos] = z;
            }
            //For when there were triangles drawn at the current pixel that were closer than the current triangle
            else if((frame[pixelPos] >>> 24) < 0xFF){
              computeLighting(tempZ, invZ, vertexBrightness);
              frame[pixelPos] = interpolatePixels(brokenUpFrame, brokenUpColour, pixelPos);
            }
          }
         }
       }
     }
     //Drawing the outline
     if((flags & 8) == 8){
       for(byte i = 0; i < 3; i++)
         drawLine(new IntWrapper(Math.round(triangle.getVertices()[i][0])), new IntWrapper(Math.round(triangle.getVertices()[i][1])), triangle.getVertices()[i][2]+0.0004f, new IntWrapper(Math.round(triangle.getVertices()[(i+1)%3][0])), new IntWrapper(Math.round(triangle.getVertices()[(i+1)%3][1])), triangle.getVertices()[(i+1)%3][2]+0.0004f, stroke, (flags & 4) == 0);
    }
  }

  public static void billBoardDraw(Billboard sprite, float x, float y, float z, float sizeX, float sizeY){
    //Sets the sprite to be drawn in front of everything else
    if(sprite.returnDepthWrite())
      z*=-1;
    if(Math.abs(sizeX) > 0.0001 && Math.abs(sizeY) > 0.0001){
      //Grabs the recipricol of the scale of the image
      float scaleX = sprite.returnWidth()/sizeX;
      float scaleY = sprite.returnHeight()/sizeY;

  
      //Determines where the start of the scale indices are (these track which pixel of the original image we are currently looking at)
      float scX = ((sizeX >= 0) ? (x >= 0) ? 0 : -x*scaleX : (x+sizeX >= 0) ? sprite.returnWidth() : -x*scaleX-1f/sprite.returnWidth())-0.0001f;
      float scY = ((sizeY >= 0) ? (y >= 0) ? 0 : -y*scaleY : (y+sizeY >= 0) ? sprite.returnHeight() : -y*scaleY-1f/sprite.returnHeight())-0.0001f;
      float oldScY = scY; //Used for resetting scY
      
      //Sets the sizes to integers
      sizeX = Math.round(sizeX);
      sizeY = Math.round(sizeY);

      //Computes the start and end pixels of the output image
      int[] start = {Math.round(Math.min(Math.max(0, Math.min(x, x+sizeX)), wid)), Math.round(Math.min(Math.max(0, Math.min(y, y+sizeY)), heig))};
      int[] end = {Math.round(Math.min(Math.max(0, Math.max(x, x+sizeX)), wid)), Math.round(Math.min(Math.max(0, Math.max(y, y+sizeY)), heig))};
      if(sprite.hasImage()){
        alphaNorm = (sprite.returnFill() >>> 24)*0.003921568f;
        //Actually drawing the sprite
        for(int i = start[0]; i < end[0] && scX > -1 && scX < sprite.returnWidth(); i++){
          scY = oldScY; //Resetting scY
          for(int j = start[1]; j < end[1] && scY > -1 && scY < sprite.returnHeight(); j++){
            int pixelPos = j*wid+i; //Determining a pixel's index
            int imgPixel = (int)(scY)*sprite.returnWidth()+(int)(scX); //Determining where in the image the current desired pixel is
            //Checks if a pixel is not a specific colour defined in the Billboard object and if there is nothing already in front of where the sprite is being drawn
            //If it passes, it draws the sprite's pixel to the new location
            if((maxProbability <= threshold || Math.random()*maxProbability < threshold) && stencil[pixelPos] == 0 && (sprite.shouldDrawPixel(imgPixel) || sprite.hasRemoval())){
              //Adjusting the brightness level of each pixel
              int colour = (sprite.returnFill() & 0xFF000000);
              colour|=((int)Math.min(((sprite.returnPixels()[imgPixel] >>> 16) & 255)*((sprite.returnFill() >>> 16) & 255)*0.003921568f, 255)) << 16;
              colour|=((int)Math.min(((sprite.returnPixels()[imgPixel] >>> 8) & 255)*((sprite.returnFill() >>> 8) & 255)*0.003921568f, 255)) << 8;
              colour|=((int)Math.min((sprite.returnPixels()[imgPixel] & 255)*(sprite.returnFill() & 255)*0.003921568f, 255));
              if((sprite.returnDepthWrite() && (z < zBuff[pixelPos] || zBuff[pixelPos] <= 0) || !sprite.returnDepthWrite() && z > zBuff[pixelPos] || Float.isNaN(zBuff[pixelPos]))){
                //Copying the image's pixel to the frame buffer
                if((sprite.returnFill() >>> 24) == 255)
                  frame[pixelPos] = 0xFF000000|colour;
                else
                  frame[pixelPos] = Colour.interpolateColours(colour, frame[pixelPos], alphaNorm);
                zBuff[pixelPos] = z;//Copying the z-position of the image to the depth buffer
              }
              else
                if((sprite.returnFill() >>> 24) < 255 && (frame[pixelPos] >>> 24) < 255)
                  frame[pixelPos] = Colour.interpolateColours(frame[pixelPos], colour);
              }
              scY+=scaleY;
            }
            scX+=scaleX;
        }
      }
      if(sprite.returnHasStroke()){
        start[0] = Math.round(Math.min(x, x+sizeX));
        end[0] = Math.round(Math.max(x, x+sizeX));

        //Doing the vertical sides
        int side = 0;
        if(start[0] >= 0 && start[0] < wid){
          if((sprite.returnStroke() >>> 24) < 255){
            alphaNorm = ((sprite.returnStroke() >>> 24) & 255)*0.003921568f;
            for(int i = start[1]; i < end[1]; i++){
                side = i*wid+start[0];
                if((!sprite.returnDepthWrite() && z >= zBuff[side]) || (sprite.returnDepthWrite() && (z <= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                  frame[side] = Colour.interpolateColours(sprite.returnStroke(), frame[side], alphaNorm);
              }
            }
          else
           for(int i = start[1]; i < end[1]; i++){
              side = i*wid+start[0];
              if((!sprite.returnDepthWrite() && z >= zBuff[side]) || (sprite.returnDepthWrite() && (z <= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = sprite.returnStroke();
            }
        }
        if(end[0] < wid && end[0] >= 0){
          if((sprite.returnStroke() >>> 24) < 255){
            alphaNorm = ((sprite.returnStroke() >>> 24) & 255)*0.003921568f;
            for(int i = start[1]; i < end[1]; i++){
              side = i*wid+end[0];
              if((!sprite.returnDepthWrite() && z >= zBuff[side]) || (sprite.returnDepthWrite() && (z <= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = Colour.interpolateColours(sprite.returnStroke(), frame[side], alphaNorm);
            }
          }
          else
            for(int i = start[1]; i < end[1]; i++){
              side = i*wid+end[0];
              if((!sprite.returnDepthWrite() && z >= zBuff[side]) || (sprite.returnDepthWrite() && (z <= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = sprite.returnStroke();
           }
        }
        start[0] = Math.max(0, (Math.min(start[0], wid)));
        end[0] = Math.max(0, (Math.min(end[0], wid)));
        start[1] = Math.round(Math.min(y, y+sizeY));
        end[1] = Math.round(Math.max(y, y+sizeY))-1;
        //Doing the horizontal sides
        if(start[1] >= 0 && start[1] < heig){
          if((sprite.returnStroke() >>> 24) < 255){
            alphaNorm = ((sprite.returnStroke() >>> 24) & 255)*0.003921568f;
            for(int i = start[0]; i < end[0]; i++){
              side = start[1]*wid+i;
              if((!sprite.returnDepthWrite() && z >= zBuff[side]) || (sprite.returnDepthWrite() && (z <= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = Colour.interpolateColours(sprite.returnStroke(), frame[side], alphaNorm);
            }
        }
        else
           for(int i = start[0]; i < end[0]; i++){
              side = start[1]*wid+i;
              if((!sprite.returnDepthWrite() && z >= zBuff[side]) || (sprite.returnDepthWrite() && (z <= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = sprite.returnStroke();
            }
        }
        if(end[1] < heig && end[1] >= 0){
          if((sprite.returnStroke() >>> 24) < 255){
            alphaNorm = ((sprite.returnStroke() >>> 24) & 255)*0.003921568f;
            for(int i = start[0]; i < end[0]; i++){
              side = end[1]*wid+i;
              if((!sprite.returnDepthWrite() && z >= zBuff[side]) || (sprite.returnDepthWrite() && (z <= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = Colour.interpolateColours(sprite.returnStroke(), frame[side], alphaNorm);
            }
          }
          else
            for(int i = start[0]; i < end[0]; i++){
              side = end[1]*wid+i;
              if((!sprite.returnDepthWrite() && z >= zBuff[side]) || (sprite.returnDepthWrite() && (z <= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = sprite.returnStroke();
           }
        }
      }
    }
  }
  public static void billBoardDraw(Billboard sprite, float x, float y, float z, float sizeX, float sizeY, byte compVal, char testType){
    //Sets the sprite to be drawn in front of everything else
    if(sprite.returnDepthWrite())
      z*=-1;
    if(Math.abs(sizeX) > 0.0001 && Math.abs(sizeY) > 0.0001){
      //Grabs the recipricol of the scale of the image
      float scaleX = sprite.returnWidth()/sizeX;
      float scaleY = sprite.returnHeight()/sizeY;

  
      //Determines where the start of the scale indices are (these track which pixel of the original image we are currently looking at)
      float scX = ((sizeX >= 0) ? (x >= 0) ? 0 : -x*scaleX : (x+sizeX >= 0) ? sprite.returnWidth() : -x*scaleX-1f/sprite.returnWidth())-0.0001f;
      float scY = ((sizeY >= 0) ? (y >= 0) ? 0 : -y*scaleY : (y+sizeY >= 0) ? sprite.returnHeight() : -y*scaleY-1f/sprite.returnHeight())-0.0001f;
      float oldScY = scY; //Used for resetting scY

      //Sets the sizes to integers
      sizeX = Math.round(sizeX);
      sizeY = Math.round(sizeY);
      //Computes the start and end pixels of the output image
      int[] start = {Math.round(Math.min(Math.max(0, Math.min(x, x+sizeX)), wid)), Math.round(Math.min(Math.max(0, Math.min(y, y+sizeY)), heig))};
      int[] end = {Math.round(Math.min(Math.max(0, Math.max(x, x+sizeX)), wid)), Math.round(Math.min(Math.max(0, Math.max(y, y+sizeY)), heig))};
      if(sprite.hasImage()){
        alphaNorm = (sprite.returnFill() >>> 24)*0.003921568f;
        //Actually drawing the sprite
        for(int i = start[0]; i < end[0] && scX > -1 && scX < sprite.returnWidth(); i++){
          scY = oldScY; //Resetting scY
          for(int j = start[1]; j < end[1] && scY > -1 && scY < sprite.returnHeight(); j++){
            int pixelPos = j*wid+i; //Determining a pixel's index
            int imgPixel = (int)(scY)*sprite.returnWidth()+(int)(scX); //Determining where in the image the current desired pixel is
            stencilTest(pixelPos, compVal, testType);
            //Checks if a pixel is not a specific colour defined in the Billboard object and if there is nothing already in front of where the sprite is being drawn
            //If it passes, it draws the sprite's pixel to the new location
            if((maxProbability <= threshold || Math.random()*maxProbability < threshold) && (flags & 1) == 1 && (sprite.shouldDrawPixel(imgPixel) || sprite.hasRemoval())){
              //Adjusting the brightness level of each pixel
              int colour = (sprite.returnFill() & 0xFF000000);
              colour|=((int)Math.min(((sprite.returnPixels()[imgPixel] >>> 16) & 255)*((sprite.returnFill() >>> 16) & 255)*0.003921568f, 255)) << 16;
              colour|=((int)Math.min(((sprite.returnPixels()[imgPixel] >>> 8) & 255)*((sprite.returnFill() >>> 8) & 255)*0.003921568f, 255)) << 8;
              colour|=((int)Math.min((sprite.returnPixels()[imgPixel] & 255)*(sprite.returnFill() & 255)*0.003921568f, 255));
              if((sprite.returnDepthWrite() && (z < zBuff[pixelPos] || zBuff[pixelPos] <= 0) || !sprite.returnDepthWrite() && z > zBuff[pixelPos] || Float.isNaN(zBuff[pixelPos]))){
                //Copying the image's pixel to the frame buffer
                if((sprite.returnFill() >>> 24) == 255){
                  float stencilNorm = ((~stencil[pixelPos]) & 255)*0.003921568f;
                  int[] tempColours = {(int)(((colour >>> 16) & 255)*stencilNorm) << 16,
                                       (int)(((colour >>> 8) & 255)*stencilNorm) << 8,
                                       (int)((colour & 255)*stencilNorm)};
                  frame[pixelPos] = 0xFF000000|tempColours[0]|tempColours[1]|tempColours[2];
                }
                else
                  frame[pixelPos] = interpolatePixels(colour, frame[pixelPos], pixelPos, alphaNorm);
                zBuff[pixelPos] = z;//Copying the z-position of the image to the depth buffer
              }
              else
                if((sprite.returnFill() >>> 24) < 255 && (frame[pixelPos] >>> 24) < 255)
                  frame[pixelPos] = interpolatePixels(frame[pixelPos], colour, pixelPos);
              }
              scY+=scaleY;
            }
            scX+=scaleX;
        }
      }
      if(sprite.returnHasStroke()){
        z = (sprite.returnDepthWrite()) ? z-0.0004f : z+0.0004f;
        start[0] = Math.round(Math.min(x, x+sizeX));
        end[0] = Math.round(Math.max(x, x+sizeX));

        //System.out.println(start[0]+", "+start[1]);
        //Doing the vertical sides
        int side = 0;
        if(start[0] >= 0 && start[0] < wid){
          if((sprite.returnStroke() >>> 24) < 255){
            alphaNorm = ((sprite.returnStroke() >>> 24) & 255)*0.003921568f;
            for(int i = start[1]; i < end[1]; i++){
                side = i*wid+start[0];
                if((!sprite.returnDepthWrite() && z >= zBuff[side]) || (sprite.returnDepthWrite() && (z <= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                  frame[side] = Colour.interpolateColours(sprite.returnStroke(), frame[side], alphaNorm);
              }
            }
          else
           for(int i = start[1]; i < end[1]; i++){
              side = i*wid+start[0];
              if((!sprite.returnDepthWrite() && z >= zBuff[side]) || (sprite.returnDepthWrite() && (z <= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = sprite.returnStroke();
            }
        }
        if(end[0] < wid && end[0] >= 0){
          if((sprite.returnStroke() >>> 24) < 255){
            alphaNorm = ((sprite.returnStroke() >>> 24) & 255)*0.003921568f;
            for(int i = start[1]; i < end[1]; i++){
              side = i*wid+end[0];
              if((!sprite.returnDepthWrite() && z >= zBuff[side]) || (sprite.returnDepthWrite() && (z <= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = Colour.interpolateColours(sprite.returnStroke(), frame[side], alphaNorm);
            }
          }
          else
            for(int i = start[1]; i < end[1]; i++){
              side = i*wid+end[0];
              if((!sprite.returnDepthWrite() && z >= zBuff[side]) || (sprite.returnDepthWrite() && (z <= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = sprite.returnStroke();
           }
        }
        start[0] = Math.max(0, (Math.min(start[0], wid)));
        end[0] = Math.max(0, (Math.min(end[0], wid)));
        start[1] = Math.round(Math.min(y, y+sizeY));
        end[1] = Math.round(Math.max(y, y+sizeY))-1;
        //Doing the horizontal sides
        if(start[1] >= 0 && start[1] < heig){
          if((sprite.returnStroke() >>> 24) < 255){
            alphaNorm = ((sprite.returnStroke() >>> 24) & 255)*0.003921568f;
            for(int i = start[0]; i < end[0]; i++){
              side = start[1]*wid+i;
              if((!sprite.returnDepthWrite() && z >= zBuff[side]) || (sprite.returnDepthWrite() && (z <= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = Colour.interpolateColours(sprite.returnStroke(), frame[side], alphaNorm);
            }
        }
        else
           for(int i = start[0]; i < end[0]; i++){
              side = start[1]*wid+i;
              if((!sprite.returnDepthWrite() && z >= zBuff[side]) || (sprite.returnDepthWrite() && (z <= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = sprite.returnStroke();
            }
        }
        if(end[1] < heig && end[1] >= 0){
          if((sprite.returnStroke() >>> 24) < 255){
            alphaNorm = ((sprite.returnStroke() >>> 24) & 255)*0.003921568f;
            for(int i = start[0]; i < end[0]; i++){
              side = end[1]*wid+i;
              if((!sprite.returnDepthWrite() && z >= zBuff[side]) || (sprite.returnDepthWrite() && (z <= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
                frame[side] = Colour.interpolateColours(sprite.returnStroke(), frame[side], alphaNorm);
            }
          }
          else
            for(int i = start[0]; i < end[0]; i++){
              side = end[1]*wid+i;
              if((!sprite.returnDepthWrite() && z >= zBuff[side]) || (sprite.returnDepthWrite() && (z <= zBuff[side] || zBuff[side] >= 0)) || Float.isNaN(zBuff[side]))
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
    if(Math.abs(denominator) <= 0.00000001){
      System.out.println("ERROR: DIV BY 0");
      System.exit(1);
    }
    float numerator = (y2 - y3)*(pX - x3) + (x3 - x2)*(pY - y3);
    return numerator/denominator;
  }
  //Weight contributed by the second vertex
  public static float returnBeta(float x1, float y1, float x2, float y2, float x3, float y3, float pX, float pY){
    float denominator = (y2 - y3)*(x1 - x3) + (x3 - x2)*(y1 - y3);
    if(Math.abs(denominator) <= 0.000000001){
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
    if(Math.abs(denominator) <= 0.000000001){
      System.out.println("ERROR: DIV BY 0");
      System.exit(1);
    }
    float numerator = (y2 - y3)*(x - x3) + (x3 - x2)*(y - y3);
    float[] coords = {0, 0, 0};
    coords[0] = numerator/denominator;
    //Beta weight
    denominator = (y2 - y3)*(x1 - x3) + (x3 - x2)*(y1 - y3);
    if(Math.abs(denominator) <= 0.000000001){
      System.out.println("ERROR: DIV BY 0");
      System.exit(1);
    }
    numerator = (y3 - y1)*(x - x3) + (x1 - x3)*(y - y3);
    coords[1] = numerator/denominator;
    //Gamma weight
    coords[2] = 1-coords[0]-coords[1];
    return coords;
  }
  //End of Barycentric coordinates
  public static int interpolatePixels(int pixelA, int pixelB, int stencilIndex){
    //Computing t and 1-t for pixelA
    float alpha = (pixelA >>> 24)*Colour.INV_255;
    //Getting the normalized stencil value
    float stencilNorm = ((~stencil[stencilIndex]) & 0xFF)*Colour.INV_255;
    //Extracting the individual channels' data for pixel A and pixel B
    int[][] channels = {{(pixelA >>> 16) & 0xFF, (pixelB >>> 16) & 0xFF}, {(pixelA >>> 8) & 0xFF, (pixelB >>> 8) & 0xFF}, {pixelA & 0xFF, pixelB & 0xFF}};
    int[] returnChannels = {0, 0, 0};
    //Linearly interpolating between pixel B's channels and pixel A's channels
    //returnChannels[0] = (int)((channels[0][0]-channels[0][1])*alpha + channels[0][1]);
    for(byte i = 0; i < 3; i++)
      returnChannels[i] = (int)(((channels[i][0]-channels[i][1])*alpha+channels[i][1])*stencilNorm);
      
    //Combining the results
    return (pixelA & 0xFF000000)|(returnChannels[0] << 16)|(returnChannels[1] << 8)|returnChannels[2];
  }

  public static int interpolatePixels(int pixelA, int pixelB, int stencilIndex, float alpha){
    //Getting the normalized stencil value
    float stencilNorm = ((~stencil[stencilIndex]) & 0xFF)*Colour.INV_255;
    //Extracting the individual channels' data for pixel A and pixel B
    int[][] channels = {{(pixelA >>> 16) & 0xFF, (pixelB >>> 16) & 0xFF}, {(pixelA >>> 8) & 0xFF, (pixelB >>> 8) & 0xFF}, {pixelA & 0xFF, pixelB & 0xFF}};
    int[] returnChannels = {0, 0, 0};
    //Linearly interpolating between pixel B's channels and pixel A's channels
    //returnChannels[0] = (int)((channels[0][0]-channels[0][1])*alpha + channels[0][1]);
    for(byte i = 0; i < 3; i++)
      returnChannels[i] = (int)(((channels[i][0]-channels[i][1])*alpha+channels[i][1])*stencilNorm);
      
    //Combining the results
    return (pixelA & 0xFF000000)|(returnChannels[0] << 16)|(returnChannels[1] << 8)|returnChannels[2];
  }
  public static int interpolatePixels(int[] pixelA, int[] pixelB, int stencilIndex){
    float alphaNorm = pixelA[0]*Colour.INV_255;
    float stencilNorm = ((~stencil[stencilIndex]) & 0xFF)*Colour.INV_255;
    int[] colour = {(int)(((pixelA[0] - pixelB[0])*alphaNorm + pixelB[0])*stencilNorm) << 24,
                    (int)(((pixelA[1] - pixelB[1])*alphaNorm + pixelB[1])*stencilNorm) << 16,
                    (int)(((pixelA[2] - pixelB[2])*alphaNorm + pixelB[2])*stencilNorm) << 8,
                    (int)(((pixelA[3] - pixelB[3])*alphaNorm + pixelB[3])*stencilNorm)};
    return colour[0]|colour[1]|colour[2]|colour[3];
  }
  public static int interpolatePixels(int[] pixelA, int[] pixelB, int stencilIndex, float alphaNorm){
    float stencilNorm = ((~stencil[stencilIndex]) & 0xFF)*Colour.INV_255;
    int[] colour = {(int)(((pixelA[0] - pixelB[0])*alphaNorm + pixelB[0])*stencilNorm) << 24,
                    (int)(((pixelA[1] - pixelB[1])*alphaNorm + pixelB[1])*stencilNorm) << 16,
                    (int)(((pixelA[2] - pixelB[2])*alphaNorm + pixelB[2])*stencilNorm) << 8,
                    (int)(((pixelA[3] - pixelB[3])*alphaNorm + pixelB[3])*stencilNorm)};
    return colour[0]|colour[1]|colour[2]|colour[3];
  }

  private static void interpLine(IntWrapper p1, IntWrapper p2, IntWrapper oldP1, IntWrapper oldP2, int oldOppP1, int oldOppP2, int farEdge){
    float t1 = -1;
    float t2 = -1;
    float denom = oldOppP2 - oldOppP1;
    if(Math.abs(denom) > 0.000000001){
      t1 = -oldOppP1/denom;
      t2 = (farEdge - 1 - oldOppP1)/denom;
      if(t1 >= 0 && t1 <= 1){
        if(oldOppP1 < 0)
          p1.val = Math.round((oldP2.val - oldP1.val)*t1 + oldP1.val);
        if(oldOppP2 < 0)
          p2.val = Math.round((oldP2.val - oldP1.val)*t1 + oldP1.val);
      }
      if(t2 >= 0 && t2 <= 1){
        if(oldOppP1 >= farEdge)
          p1.val = Math.round((oldP2.val - oldP1.val)*t2 + oldP1.val);
        if(oldOppP2 >= farEdge)
          p2.val = Math.round((oldP2.val - oldP1.val)*t2 + oldP1.val);
      }
    }
  }
  //Bresenham's line algorithm
  //https://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
  public static void drawLine(IntWrapper x1, IntWrapper y1, IntWrapper x2, IntWrapper y2, int lineColour){
    int edgeDirX = x1.val < x2.val ? 1 : -1; //The direction of the line along the x-axis
    int edgeDirY = y1.val < y2.val ? 1 : -1; //The direction of the line along the y-axis
    int dx = Math.abs(x2.val-x1.val); //Difference between x2 and x1
    int dy = -Math.abs(y2.val-y1.val); //Difference between y2 and y1 (negated to account for how down is positive and up is negative)
    int error = dx+dy; //Sum of the differences between x2 and x1 and y2 and y1
    if((x1.val < 0 && x2.val < 0) || (y1.val < 0 && y2.val < 0) || (x1.val >= wid && x2.val >= wid) || (y1.val >= heig && y2.val >= heig))
      return;
    else if(!(Math.min(x1.val, x2.val) >= 0 && Math.max(x1.val, x2.val) < wid && Math.min(y1.val, y2.val) >= 0 && Math.max(y1.val, y2.val) < heig)){
      IntWrapper oldX1 = new IntWrapper(x1.val);
      IntWrapper oldX2 = new IntWrapper(x2.val);
      IntWrapper oldY1 = new IntWrapper(y1.val);
      IntWrapper oldY2 = new IntWrapper(y2.val);
      interpLine(y1, y2, oldY1, oldY2, oldX1.val, oldX2.val, wid);
      interpLine(x1, x2, oldX1, oldX2, oldY1.val, oldY2.val, heig);
  
      x1.val = Math.max(0, (Math.min(x1.val, wid-1)));
      x2.val = Math.max(0, (Math.min(x2.val, wid-1)));
      y1.val = Math.max(0, (Math.min(y1.val, heig-1)));
      y2.val = Math.max(0, (Math.min(y2.val, heig-1)));
    }
    while(true){
      //Interpolate between the line colour and the current pixel if the line colour's alpha channel is less than 255. Otherwise, simply overwrite the current pixel with the line colour
      if((lineColour >>> 24) < 0xFF){
        int pixelPos = wid*y1.val+x1.val;
        frame[pixelPos] = Colour.interpolateColours(lineColour, frame[pixelPos]);
      }
      else
        frame[wid*y1.val+x1.val] = lineColour;
      //Early break when the two points are the same
      if(x1.val == x2.val && y1.val == y2.val)
        break;
        
      final int error2 = error << 1; //Taking twice the error
      //For when twice the error is greater than the negative difference between y2 and y1
      if(error2 >= dy){
        //Verticle line
        if(x1.val == x2.val)
           break;
        error+=dy;
        x1.val+=edgeDirX;
      }
      //For when twice the error is less than the difference between x2 and x1
      if(error2 <= dx){
        //Horizontal line
        if(y1.val == y2.val)
          break;
        error+=dx;
        y1.val+=edgeDirY;
      }
    }
  }
  public static void drawLine(IntWrapper x1, IntWrapper y1, float z1, IntWrapper x2, IntWrapper y2, float z2, int lineColour, boolean noDepth){
    int edgeDirX = x1.val < x2.val ? 1 : -1; //The direction of the line along the x-axis
    int edgeDirY = y1.val < y2.val ? 1 : -1; //The direction of the line along the y-axis
    int dx = Math.abs(x2.val-x1.val); //Difference between x2 and x1
    int dy = -Math.abs(y2.val-y1.val); //Difference between y2 and y1 (negated to account for how down is positive and up is negative)
    int error = dx+dy; //Sum of the differences between x2 and x1 and y2 and y1
    float vectorMag = (Math.abs(x2.val-x1.val) > 0.0001) ? (x2.val-x1.val) : (y2.val-y1.val); //Calculating the length of the line as if it were 2D
    float oldX = Math.abs(x2.val-x1.val);
    if((x1.val < 0 && x2.val < 0) || (y1.val < 0 && y2.val < 0) || (x1.val >= wid && x2.val >= wid) || (y1.val >= heig && y2.val >= heig))
      return;
    else if(!(Math.min(x1.val, x2.val) >= 0 && Math.max(x1.val, x2.val) < wid && Math.min(y1.val, y2.val) >= 0 && Math.max(y1.val, y2.val) < heig)){
      IntWrapper oldX1 = new IntWrapper(x1.val);
      IntWrapper oldX2 = new IntWrapper(x2.val);
      IntWrapper oldY1 = new IntWrapper(y1.val);
      IntWrapper oldY2 = new IntWrapper(y2.val);
      interpLine(y1, y2, oldY1, oldY2, oldX1.val, oldX2.val, wid);
      interpLine(x1, x2, oldX1, oldX2, oldY1.val, oldY2.val, heig);
  
      x1.val = Math.max(0, (Math.min(x1.val, wid-1)));
      x2.val = Math.max(0, (Math.min(x2.val, wid-1)));
      y1.val = Math.max(0, (Math.min(y1.val, heig-1)));
      y2.val = Math.max(0, (Math.min(y2.val, heig-1)));
    }
    float z = z1;
    if(Math.abs(vectorMag) > 0.000000001){
      vectorMag = 1/vectorMag;
      while(true){
        if(noDepth)
          z*=-1;
        int pixelPos = wid*y1.val+x1.val;
        if(((noDepth && (z <= zBuff[pixelPos] || zBuff[pixelPos] <= 0)) || (!noDepth && z >= zBuff[pixelPos])) || Float.isNaN(zBuff[pixelPos])){
          //Interpolate between the line colour and the current pixel if the line colour's alpha channel is less than 255. Otherwise, simply overwrite the current pixel with the line colour
          if((lineColour >>> 24) < 0xFF)
            frame[pixelPos] = Colour.interpolateColours(lineColour, frame[pixelPos]);
          else
            frame[pixelPos] = lineColour;
          zBuff[pixelPos] = z;
        }
        else
          if((lineColour >>> 24) < 0xFF)
            frame[pixelPos] = Colour.interpolateColours(frame[pixelPos], lineColour);
          
        //Early break when the two points are the same
        if(x1.val == x2.val && y1.val == y2.val)
          break;
            
        final int error2 = error << 1; //Taking twice the error
        //For when twice the error is greater than the negative difference between y2 and y1
        if(error2 >= dy){
          //Verticle line
          if(x1.val == x2.val)
            break;
          error+=dy;
          x1.val+=edgeDirX;
        }
        //For when twice the error is less than the difference between x2 and x1
        if(error2 <= dx){
          //Horizontal line
          if(y1.val == y2.val)
            break;
          error+=dx;
          y1.val+=edgeDirY;
        }
        //Calculating the depth of the line at a particular pixel
        float numerator = (oldX > 0.0001) ? (x2.val-x1.val) : (y2.val-y1.val);
        z = ((z1 - z2)*(numerator*vectorMag) + z2)-0.0001f;
      }
    }
  }
  //Gouraud shading
  private static void computeLighting(float z, float[] invZ, float[][] vertexBrighness){
    float adjustedAlpha = invZ[0]*alpha;
    float adjustedBeta = invZ[1]*beta;
    float adjustedGamma = invZ[2]*gamma;
    float[] overallBrightness = {z*(Math.max(0, vertexBrightness[0][0]*adjustedAlpha+vertexBrightness[1][0]*adjustedBeta+vertexBrightness[2][0]*adjustedGamma)),
                                 z*(Math.max(0, vertexBrightness[0][1]*adjustedAlpha+vertexBrightness[1][1]*adjustedBeta+vertexBrightness[2][1]*adjustedGamma)),
                                 z*(Math.max(0, vertexBrightness[0][2]*adjustedAlpha+vertexBrightness[1][2]*adjustedBeta+vertexBrightness[2][2]*adjustedGamma))};
    // float[] overallBrightness = {Math.max(0, vertexBrightness[0][0]*alpha+vertexBrightness[1][0]*beta+vertexBrightness[2][0]*gamma),
    //                              Math.max(0, vertexBrightness[0][1]*alpha+vertexBrightness[1][1]*beta+vertexBrightness[2][1]*gamma),
    //                              Math.max(0, vertexBrightness[0][2]*alpha+vertexBrightness[1][2]*beta+vertexBrightness[2][2]*gamma)};
    brokenUpColour[1] = (int)Math.min(255, (brokenUpFill[0]*overallBrightness[0]));
    brokenUpColour[2] = (int)Math.min(255, (brokenUpFill[1]*overallBrightness[1]));
    brokenUpColour[3] = (int)Math.min(255, (brokenUpFill[2]*overallBrightness[2]));
  }

  //Finds the left and right edges of a triangle at a given scanline
  private static void computeEdges(float y, int minX, int maxX, int[][] screenBounds, float[][] poses, float[] interpolatedEdges){
    byte currentEdge = 1;
    //Holds how far along each edge the function is at the current scanline
    float[] t = {-1, -1, -1};
    //Computes time t for the two edges that go to the middle vertex at the current scanline
    float denominator = (poses[1][1]-poses[0][1])-0.0000001f;
    if(Math.abs(denominator) > 0.0000001)
      t[2] = (poses[1][1]-y)/denominator-0.0000001f;
    denominator = (poses[2][1]-poses[1][1])-0.0000001f;
    if(Math.abs(denominator) > 0.0000001)
      t[0] = (poses[2][1]-y)/denominator-0.0000001f;
    denominator = (poses[2][1]-poses[0][1])-0.0000001f;
    if(Math.abs(denominator) > 0.0000001)
      t[1] = (poses[2][1]-y)/denominator-0.0000001f;
    
    //Uses the times to interpolate along the edges
    if(t[2] >= 0f && t[2] <= 1f){
      interpolatedEdges[currentEdge] = (poses[0][0]-poses[1][0])*t[2]+poses[1][0]-0.0000001f;
      currentEdge--;
    }
    if(t[0] >= 0f && t[0] <= 1f){
      interpolatedEdges[currentEdge] = (poses[1][0]-poses[2][0])*t[0]+poses[2][0]-0.0000001f;
      currentEdge--;
    }
    if(t[1] >= 0f && t[1] <= 1f)
      interpolatedEdges[0] = (poses[0][0]-poses[2][0])*t[1]+poses[2][0]-0.0000001f;
    //Determines the min and max x-positions of the current scanline
    screenBounds[0][0] = Math.round(Math.max(minX, Math.min(interpolatedEdges[0], interpolatedEdges[1])));
    screenBounds[0][1] = Math.round(Math.min(maxX, Math.max(interpolatedEdges[0], interpolatedEdges[1])));
  }
}
