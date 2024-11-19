import java.util.*;
import java.io.*;

//A class which loads and holds model data, which will be copied into the models
public class LoadModelFile {
  public static Model loadModel(String dir){
    float[][] model = new float[0][3]; //Vertex list
    int[][] poly = new int[0][3]; //Polygon list
    int[][] colour = new int[1][2]; //i, 0 = stroke; i, 1 = fill
    float[][] polygonNormals = new float[0][3]; //Polygon normals list
    File file; //Stores the current file
    Scanner fileReader; //Reads each line of the file
    int polygonIndex = -1; //Tracks the current polygon's vertices
    int vertexIndex = -1; //Tracks the current vertex
    int vertexAxisIndex = 0;//Stores the current axis for the current vertex for the current polygon
    int strokeIndex = -1; //Tracks the current stroke
    int fillIndex = -1; //Tracks the current fill
    int backIndex = 0; //Tracks the current index of the list of polygons that are exempt from backface culling
    int backFillIndex = -1;//Tracks the current back face fill index
    int backStrokeIndex = -1; //Tracks the current back face stroke index
    byte[] auxIndices = {0, 0, 0, 0, 0}; //i0 = angles index, i1 = position index, i2 = scale index, i3 = corse shear index, i4 = fine shear index
    boolean hasStroke = false; //Decides whether or not a polygon has stroke (defaults to false)
    boolean hasFill = true; //Decides whether or not a polygon has fill (defaults to true)
    boolean attachedToCamera = false; //Stores whether or not the model is attached to the camera
    boolean noDepth = false; //Stores whether or not the model is drawn in front of everything else
    String currLine = ""; //Holds the current line
    char currArray = 0; //Holds the array currently being filled (taken from the start of the line)
    float[] angles = {0, 0, 0}; //Holds the model's angles (will be stored similarly to model[][])
    float[] position = {0, 0, 0}; //Holds the model's position in world space (will be stored similarly to model[][])
    float[] scale = {1, 1, 1}; //Holds the model's scale (will be stored similarly to model[][])
    float[][] shear = {{0, 0}, {0, 0}, {0, 0}};
    boolean[] sizeSet = {false, false, false, false, false}; //0 = vertex array, 1 = colour array
                                                             //2 = back visible array, 3 = back colour array, 
                                                             //4 = polygon array; If true, tells the program to skip setting the array's size
    boolean isBillBoard = false; //Stores whether or not the model is a billboard (essentially a 3D sprite)
    int[] backVisible = new int[0]; //Stores whether or not each polygon has backface culling (false = has; true = does not have)
    int[][] backColour = new int[0][2];
    file = new File(dir);
    try{
      //Loads in the for reading
      fileReader = new Scanner(file);
      while(fileReader.hasNextLine()){
        //Extracts the current line of the file and reads its tokens
        currLine = fileReader.nextLine();
        Scanner lineReader = new Scanner(currLine);

        while(lineReader.hasNext()){
          String line = lineReader.next();
          Scanner lineRead = new Scanner(line);
          if(line.length() <= 1 && (line.charAt(0)  < '0' || line.charAt(0)  > '9')){
            //Reads if the length of the current token is 1 and if it is outside of the range of ASCII 48 - 57
            switch(line.charAt(0)) {
              //Cases for setting polygon and colour counts
              case 'V':
                if(!sizeSet[0]){
                  if(lineReader.hasNextInt()){
                    model = new float[lineReader.nextInt()][3];
                    sizeSet[0] = true;
                    break;
                  }
                  System.out.println("ERROR: NOT AN INT");
                  lineReader.close();
                  fileReader.close();
                  lineRead.close();
                  System.exit(1);
                }

                System.out.println("VERTEX ARRAY SIZE ALREADY SET");
                if(lineReader.hasNext())
                  lineReader.next();
                break;
              case 'p':
                if(!sizeSet[4]){
                  if(lineReader.hasNextInt()){
                    int newSize = lineReader.nextInt();
                    poly = new int[newSize][3];
                    polygonNormals = new float[newSize][3];
                    for(int i = 0; i < newSize; i++){
                      polygonNormals[i][0] = Float.intBitsToFloat(-1);
                      polygonNormals[i][1] = Float.intBitsToFloat(-1);
                      polygonNormals[i][2] = Float.intBitsToFloat(-1);
                    }
                    sizeSet[4] = true;
                    break;
                  }
                  System.out.println("ERROR: NOT AN INT");
                  lineReader.close();
                  fileReader.close();
                  lineRead.close();
                  System.exit(1);
                }

                System.out.println("POLYGON ARRAY SIZE ALREADY SET");
                if(lineReader.hasNext())
                  lineReader.next();
                break;
              case 'c':
                if(!sizeSet[1]){
                  if(lineReader.hasNextInt()){
                    colour = new int[lineReader.nextInt()][2];
                    sizeSet[1] = true;
                    break;
                  }
                  System.out.println("ERROR: NOT AN INT");
                  lineReader.close();
                  fileReader.close();
                  System.exit(1);

                } 
                System.out.println("COLOUR ARRAY SIZE ALREADY SET");
                if(lineRead.hasNext())
                  lineRead.next();
                break;
            case 'i':
              if(!sizeSet[2]){
                if(lineReader.hasNextInt()){
                  backVisible = new int[lineReader.nextInt()];
                  for(int i = 0; i < backVisible.length; i++)
                    backVisible[i] = -1;
                  sizeSet[2] = true;
                  break;
                }
                System.out.println("ERROR: NOT AN INT");
                lineReader.close();
                fileReader.close();
                lineRead.close();
                System.exit(1);
              }

              System.out.println("VISIBLE BACK ARRAY SIZE ALREADY SET");
              if(lineReader.hasNext())
                lineReader.next();
              break;
            case 'b':
              if(!sizeSet[3]){
                if(lineReader.hasNextInt()){
                  backColour = new int[lineReader.nextInt()][2];
                  for(int i = 0; i < backColour.length; i++){
                    backColour[i][0] = -1;
                    backColour[i][1] = -1;
                  }
                  sizeSet[3] = true;
                  break;
                }
                System.out.println("ERROR: NOT AN INT");
                lineReader.close();
                fileReader.close();
                lineRead.close();
                System.exit(1);
              }

              System.out.println("BACK COLOUR ARRAY SIZE ALREADY SET");
              if(lineReader.hasNext())
                lineReader.next();
            default:
              //Cases for setting the current array to be modified
              currArray = line.charAt(0);
              switch(currArray){
                //Iterating through the arrays
                case 't':
                  polygonIndex++;
                  vertexAxisIndex = 0;
                  break;
                case 'n':
                  //Add in the indexing variables above
                  break;
                case 'I':
                  vertexIndex++;
                  vertexAxisIndex = 0;
                  break;
                case 's':
                  strokeIndex++;
                  break;
                case 'f':
                  fillIndex++;
                  break;
                case 'o':
                  backStrokeIndex++;
                  break;
                case 'm':
                  backFillIndex++;
                  break;
                //Just to make sure they don't skip a line early
                case 'v':
                  break;

                case 'A':
                  auxIndices[0] = 0;
                  for(byte i = 0; i < 3; i++)
                    angles[i] = 0;
                  break;
                case 'P':
                  auxIndices[1] = 0;
                  for(byte i = 0; i < 3; i++)
                    position[i] = 0;
                  break;
                case 'S':
                  auxIndices[2] = 0;
                  for(byte i = 0; i < 3; i++)
                    scale[i] = 0;
                  break;
                case 'T':
                  auxIndices[3] = 0;
                  auxIndices[4] = 0;
                  for(byte i = 0; i < 3; i++){
                     shear[i][0] = 0;
                     shear[i][1] = 0;
                  }
                  break;
                //Skipping any invalid characters
                default:
                  if(lineReader.hasNext())
                    lineReader.next();
              }
            }
          }
          else{
            if(lineRead.hasNextDouble()){
              //Fills out the data for the model
              switch(currArray){
                case 'I': //Vertex data
                  if(vertexIndex < model.length && vertexAxisIndex < 3){
                    model[vertexIndex][vertexAxisIndex] = (float)lineRead.nextDouble();
                    vertexAxisIndex++;
                    break;
                  } 
                  
                  if(model.length <= 0){
                    System.out.println("ERROR: TOO SMALL");
                    fileReader.close();
                    lineReader.close();
                    lineRead.close();
                    System.exit(1);
                  }
                  if(lineRead.hasNext()){
                    lineRead.next();
                    break;
                  }
                case 't':
                  //Triangle data
                  if(polygonIndex < poly.length && vertexAxisIndex < 3){
                    if(lineRead.hasNextInt()){
                      poly[polygonIndex][vertexAxisIndex] = lineRead.nextInt();
                      vertexAxisIndex++;
                      break;
                    }
                    else{
                      System.out.println("ERROR: NOT AN INT");
                      fileReader.close();
                      lineReader.close();
                      lineRead.close();
                      System.exit(1);
                    }
                  } 

                  if(model.length <= 0){
                    System.out.println("ERROR: TOO SMALL");
                    fileReader.close();
                    lineReader.close();
                    lineRead.close();
                    System.exit(1);
                  }
                  if(lineRead.hasNext()){
                    lineRead.next();
                    break;
                  }
                case 'n':
                  //Will handling filling out the normals list
                  break;
                case 's': //Stroke data
                  if(strokeIndex < colour.length){
                    if(lineRead.hasNextInt()){
                      colour[strokeIndex][0] = lineRead.nextInt();
                      break;
                    }
                    System.out.println("ERROR: NOT AN INT");
                    fileReader.close();
                    lineReader.close();
                    lineRead.close();
                    System.exit(1);
                  } 
                  if(colour.length <= 0){
                    System.out.println("ERROR: TOO SMALL");
                    fileReader.close();
                    lineReader.close();
                    lineRead.close();
                    System.exit(1);
                    }
                    if(lineRead.hasNext()){
                      lineRead.next();
                      break;
                    }
  
                case 'f': //Fill data
                  if(fillIndex < colour.length){
                    if(lineRead.hasNextInt()){
                      colour[fillIndex][1] = lineRead.nextInt();
                      break; 
                    }
                    System.out.println("ERROR: NOT AN INT");
                    fileReader.close();
                    lineReader.close();
                    lineRead.close();
                    System.exit(1);
                  } 
                  if(colour.length <= 0){
                    System.out.println("ERROR: TOO SMALL");
                    fileReader.close();
                    lineReader.close();
                    lineRead.close();
                    System.exit(1);
                  }
                  if(lineRead.hasNext()){
                    lineRead.next();
                    break;
                  }
                case 'v':
                  if(backVisible.length > 0){
                    if(lineRead.hasNextInt()){
                      int index = lineRead.nextInt();
                      if(backIndex < backVisible.length){
                        backVisible[backIndex] = index;
                        backIndex++;
                      }
                      else{
                        System.out.println("ERROR: LIST IS FULL");
                        if(lineRead.hasNext())
                          lineRead.next();
                      }
                      break;
                    }
                    System.out.println("ERROR: NOT AN INT");
                  }
                  else
                    System.out.println("ERROR: ARRAY SIZE TOO SMALL");
                  fileReader.close();
                  lineReader.close();
                  lineRead.close();
                  System.exit(1);
                case 'o':
                  if(backStrokeIndex < backColour.length){
                    if(lineRead.hasNextInt()){
                      backColour[backStrokeIndex][0] = lineRead.nextInt();
                      break;
                    }
                    System.out.println("ERROR: NOT AN INT");
                    fileReader.close();
                    lineReader.close();
                    lineRead.close();
                    System.exit(1);
                  } 
                  if(backColour.length <= 0){
                    System.out.println("ERROR: TOO SMALL");
                    fileReader.close();
                    lineReader.close();
                    lineRead.close();
                    System.exit(1);
                    }
                    if(lineRead.hasNext()){
                      lineRead.next();
                      break;
                    }
                case 'm':
                  if(backFillIndex < backColour.length){
                    if(lineRead.hasNextInt()){
                      backColour[backFillIndex][1] = lineRead.nextInt();
                      break;
                    }
                    System.out.println("ERROR: NOT AN INT");
                    fileReader.close();
                    lineReader.close();
                    lineRead.close();
                    System.exit(1);
                  } 
                  if(backColour.length <= 0){
                    System.out.println("ERROR: TOO SMALL");
                    fileReader.close();
                    lineReader.close();
                    lineRead.close();
                    System.exit(1);
                    }
                    if(lineRead.hasNext()){
                      lineRead.next();
                      break;
                    }
                case 'A': //Data for the defualt angle of the model
                  if(auxIndices[0] < angles.length){
                    angles[auxIndices[0]] = (float)lineRead.nextDouble();
                    auxIndices[0]++;
                    break;
                  } 
                  if(lineRead.hasNext())
                    lineRead.next();
                  break;
  
                case 'P': //Data for the default position of the model
                  if(auxIndices[1] < angles.length) {
                    position[auxIndices[1]] = (float)lineRead.nextDouble();
                    auxIndices[1]++;
                    break;
                  }
                  if(lineRead.hasNext())
                    lineRead.next();
                  break;
  
                case 'S': //Data for the default scale of the model
                  if(auxIndices[2] < angles.length){
                    scale[auxIndices[2]] = (float)lineRead.nextDouble();
                    auxIndices[2]++;
                    break;
                  } 
                  if(lineRead.hasNext())
                    lineRead.next();
                  break;
                case 'T':
                  if(auxIndices[3] < shear.length){
                    shear[auxIndices[3]][auxIndices[4]] = (float)lineRead.nextDouble();
                    if(auxIndices[4] < 1)
                      auxIndices[4]++;
                     else{
                       auxIndices[4] = 0;
                       auxIndices[3]++;
                     }
                     break;
                  }
                  if(lineRead.hasNext())
                    lineRead.next();
                  break;
              }
            }
            else{
              //Sets if the model or does not have a fill
              if(lineRead.hasNext()){
                line = lineRead.next();
                if(line.matches("hs"))
                  hasStroke = true;
                else if(line.matches("nf"))
                  hasFill = false;
                else if(line.matches("bb"))
                  isBillBoard = true;
                else if(line.matches("rd"))
                  noDepth = true;
                else if(line.matches("ac"))
                  attachedToCamera = true;
                else
                  if(lineRead.hasNext())
                    lineRead.next();
              }
            }
          }
          lineRead.close();
        }
        lineReader.close();
      }
      fileReader.close();
    }
    catch(FileNotFoundException e){
      System.out.println("ERROR: FILE NOT FOUND");
      System.exit(1);
    }
    //Copying the data to an object of the model class
    Model outputModel =  new Model(model, poly, colour, hasStroke, hasFill, isBillBoard);
    outputModel.initBackVisible(backVisible.length);
    outputModel.initBackColours(backColour.length);
    outputModel.setAngle(angles[0], angles[1], angles[2]);
    outputModel.setPosition(position[0], position[1], position[2]);
    outputModel.setShear(shear);
    outputModel.setScale(scale[0], scale[1], scale[2]);
    outputModel.setAttachedToCamera(attachedToCamera);
    outputModel.disableDepth(noDepth);
    for(int i = 0; i < backVisible.length; i++)
      if(backVisible[i] > -1)
        outputModel.setBackVisible(backVisible[i]);
    for(int i = 0; i < backColour.length; i++)
      outputModel.setBackColour(i, backColour[i][0], backColour[i][1]);
    return outputModel;
  }
  
  //Loads a model from a file and sets external angle and position data
  public static Model loadModel(String path, float[] pos, float[] angles) {
    Model newModel = loadModel(path);
    for (byte i = 0; i < 3; i++) {
      pos[i] = newModel.returnPosition()[i];
      angles[i] = newModel.returnAngle()[i];
    }
    return newModel;
  }
}
