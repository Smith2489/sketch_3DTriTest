package Renderer.ModelDataHandler;
import java.util.*;
import java.io.*;
import Renderer.Objects.SceneEntities.Model;

//A class which loads and holds model data, which will be copied into the models
public class LoadModelFile {
  private static final String ERROR = "ERROR: ";
  private static final String TOO_SMALL = " ARRAY SIZE TOO SMALL";
  private static final String NOT_AN_INT = ERROR+"NOT AN INT";
  private static final String IS_FULL = ERROR+"LIST IS FULL";
  private static final String ALREADY_SET = " ARRAY SIZE ALREADY SET";
  private static final String NOT_FOUND = " NOT FOUND";
  private static final String SUCCESS = " SUCCESSFULLY LOADED\n";
  private static final String LOADING = "LOADING ";
  private static final String[] ARRAYS = {"VERTEX", "POLYGON", "COLOUR", "VISIBLE BACK", "BACK COLOUR", "VERTEX COLOUR"};
  private static final String[] TYPE = {"MODEL", "MESH", "PALLET"};

  private static boolean messageEnabled = true;

  public static void enableMessages(){
    messageEnabled = true;
  }

  public static void disableMessages(){
    messageEnabled = false;
  }


  public static Model loadModel(String dir){
    float[][] model = new float[0][3]; //Vertex list
    int[][] poly = new int[0][3]; //Polygon list
    int[][] colour = new int[1][2]; //i, 0 = stroke; i, 1 = fill
    float[][] vertexColours = new float[0][4]; //Vertex colour list
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
    int vertexColourIndex = -1; //Tracks the current vertex colour
    int vertexColourChannel = 0; //Tracks the current colour channel for the current vertex colour
    String currLine = ""; //Holds the current line
    char currArray = 0; //Holds the array currently being filled (taken from the start of the line)
    boolean[] sizeSet = {false, false, false, false, false}; //0 = vertex array, 1 = colour array
                                                             //2 = back visible array, 3 = back colour array, 
                                                             //4 = polygon array; If true, tells the program to skip setting the array's size
    boolean isBillBoard = false; //Stores whether or not the model is a billboard (essentially a 3D sprite)
    int[] backVisible = new int[0]; //Stores whether or not each polygon has backface culling (false = has; true = does not have)
    int[][] backColour = new int[0][2];
    file = new File(dir);
    try{
      if(messageEnabled)
        System.out.println(LOADING+TYPE[0]+": "+dir);
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
                    vertexColours = new float[model.length][3];
                    for(int i = 0; i < vertexColours.length; i++){
                      vertexColours[i][0] = 1;
                      vertexColours[i][1] = 1;
                      vertexColours[i][2] = 1;
                      vertexColours[i][3] = 1;
                    }
                    sizeSet[0] = true;
                    break;
                  }
                  System.out.println(NOT_AN_INT);
                  lineReader.close();
                  fileReader.close();
                  lineRead.close();
                  System.exit(1);
                }

                System.out.println(ARRAYS[0]+ALREADY_SET);
                if(lineReader.hasNext())
                  lineReader.next();
                break;
              case 'p':
                if(!sizeSet[4]){
                  if(lineReader.hasNextInt()){
                    int newSize = lineReader.nextInt();
                    poly = new int[newSize][3];
                    sizeSet[4] = true;
                    break;
                  }
                  System.out.println(NOT_AN_INT);
                  lineReader.close();
                  fileReader.close();
                  lineRead.close();
                  System.exit(1);
                }

                System.out.println(ARRAYS[1]+ALREADY_SET);
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
                  System.out.println(NOT_AN_INT);
                  lineReader.close();
                  fileReader.close();
                  System.exit(1);

                } 
                System.out.println(ARRAYS[2]+ALREADY_SET);
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
                System.out.println(NOT_AN_INT);
                lineReader.close();
                fileReader.close();
                lineRead.close();
                System.exit(1);
              }

              System.out.println(ARRAYS[3]+ALREADY_SET);
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
                System.out.println(NOT_AN_INT);
                lineReader.close();
                fileReader.close();
                lineRead.close();
                System.exit(1);
              }

              System.out.println(ARRAYS[4]+ALREADY_SET);
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
                case 'a':
                  vertexColourIndex++;
                  vertexColourChannel = 0;
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
                    System.out.println(ERROR+ARRAYS[0]+TOO_SMALL);
                    fileReader.close();
                    lineReader.close();
                    lineRead.close();
                    System.exit(1);
                  }
                  if(lineRead.hasNext()){
                    lineRead.next();
                    break;
                  }
                case 'a':
                  if(vertexColourIndex < vertexColours.length && vertexColourChannel < 4){
                    vertexColours[vertexColourIndex][vertexColourChannel] = (float)lineRead.nextDouble();
                    vertexColourChannel++;
                    break;
                  }
                  if(vertexColours.length <= 0){
                    System.out.println(ERROR+ARRAYS[5]+TOO_SMALL);
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
                      System.out.println(NOT_AN_INT);
                      fileReader.close();
                      lineReader.close();
                      lineRead.close();
                      System.exit(1);
                    }
                  } 

                  if(model.length <= 0){
                    System.out.println(ERROR+ARRAYS[1]+TOO_SMALL);
                    fileReader.close();
                    lineReader.close();
                    lineRead.close();
                    System.exit(1);
                  }
                  if(lineRead.hasNext()){
                    lineRead.next();
                    break;
                  }
                case 's': //Stroke data
                  if(strokeIndex < colour.length){
                    if(lineRead.hasNextInt()){
                      colour[strokeIndex][0] = lineRead.nextInt();
                      break;
                    }
                    System.out.println(NOT_AN_INT);
                    fileReader.close();
                    lineReader.close();
                    lineRead.close();
                    System.exit(1);
                  } 
                  if(colour.length <= 0){
                    System.out.println(ERROR+ARRAYS[2]+TOO_SMALL);
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
                    System.out.println(NOT_AN_INT);
                    fileReader.close();
                    lineReader.close();
                    lineRead.close();
                    System.exit(1);
                  } 
                  if(colour.length <= 0){
                    System.out.println(ERROR+ARRAYS[2]+TOO_SMALL);
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
                        System.out.println(IS_FULL);
                        if(lineRead.hasNext())
                          lineRead.next();
                      }
                      break;
                    }
                    System.out.println(NOT_AN_INT);
                  }
                  else
                    System.out.println(ERROR+ARRAYS[3]+TOO_SMALL);
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
                    System.out.println(NOT_AN_INT);
                    fileReader.close();
                    lineReader.close();
                    lineRead.close();
                    System.exit(1);
                  } 
                  if(backColour.length <= 0){
                    System.out.println(ERROR+ARRAYS[4]+TOO_SMALL);
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
                    System.out.println(NOT_AN_INT);
                    fileReader.close();
                    lineReader.close();
                    lineRead.close();
                    System.exit(1);
                  } 
                  if(backColour.length <= 0){
                    System.out.println(ERROR+ARRAYS[4]+TOO_SMALL);
                    fileReader.close();
                    lineReader.close();
                    lineRead.close();
                    System.exit(1);
                    }
                    if(lineRead.hasNext()){
                      lineRead.next();
                      break;
                    }
  
              }
            }
            else{
              //Sets if the model or does not have a fill
              if(lineRead.hasNext()){
                line = lineRead.next();
                if(line.matches("bb"))
                  isBillBoard = true;
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
      System.out.println(ERROR+"FILE "+dir+NOT_FOUND);
      System.exit(1);
    }
    //Copying the data to an object of the model class
    Geometry outputModel =  new Geometry(model, poly, isBillBoard);
    ModelColours outputColours = new ModelColours(colour, vertexColours, poly.length, vertexColours.length);
    outputColours.initBackVisible(backVisible.length);
    outputColours.initBackColours(backColour.length);
    for(int i = 0; i < backVisible.length; i++){
      if(backVisible[i] > -1)
        outputColours.setBackVisible(backVisible[i]);
    }
    for(int i = 0; i < backColour.length; i++)
      outputColours.setBackColour(i, backColour[i][0], backColour[i][1]);
    if(messageEnabled)
      System.out.println(TYPE[0]+" "+dir+SUCCESS);
    return new Model(outputModel, outputColours);
  }
  public static Geometry loadGeometry(String dir){
    float[][] model = new float[0][3]; //Vertex list
    int[][] poly = new int[0][3]; //Polygon list
    File file; //Stores the current file
    Scanner fileReader; //Reads each line of the file
    int polygonIndex = -1; //Tracks the current polygon's vertices
    int vertexIndex = -1; //Tracks the current vertex
    int vertexAxisIndex = 0;//Stores the current axis for the current vertex for the current polygon
    String currLine = ""; //Holds the current line
    char currArray = 0; //Holds the array currently being filled (taken from the start of the line)
    boolean[] sizeSet = {false, false}; //0 = vertex array, 
                                        //1 = polygon array; If true, tells the program to skip setting the array's size
    boolean isBillBoard = false; //Stores whether or not the model is a billboard (essentially a 3D sprite)
    file = new File(dir);
    try{
      if(messageEnabled)
        System.out.println(LOADING+TYPE[1]+": "+dir);
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
                  System.out.println(NOT_AN_INT);
                  lineReader.close();
                  fileReader.close();
                  lineRead.close();
                  System.exit(1);
                }

                System.out.println(ARRAYS[0]+ALREADY_SET);
                if(lineReader.hasNext())
                  lineReader.next();
                break;
              case 'p':
                if(!sizeSet[1]){
                  if(lineReader.hasNextInt()){
                    int newSize = lineReader.nextInt();
                    poly = new int[newSize][3];
                    sizeSet[1] = true;
                    break;
                  }
                  System.out.println(NOT_AN_INT);
                  lineReader.close();
                  fileReader.close();
                  lineRead.close();
                  System.exit(1);
                }

                System.out.println(ARRAYS[1]+ALREADY_SET);
                if(lineReader.hasNext())
                  lineReader.next();
                break;
            default:
              //Cases for setting the current array to be modified
              currArray = line.charAt(0);
              switch(currArray){
                //Iterating through the arrays
                case 't':
                  polygonIndex++;
                  vertexAxisIndex = 0;
                  break;
                case 'I':
                  vertexIndex++;
                  vertexAxisIndex = 0;
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
                    System.out.println(ERROR+ARRAYS[0]+TOO_SMALL);
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
                      System.out.println(NOT_AN_INT);
                      fileReader.close();
                      lineReader.close();
                      lineRead.close();
                      System.exit(1);
                    }
                  } 

                  if(model.length <= 0){
                    System.out.println(ERROR+ARRAYS[1]+TOO_SMALL);
                    fileReader.close();
                    lineReader.close();
                    lineRead.close();
                    System.exit(1);
                  }
                  if(lineRead.hasNext()){
                    lineRead.next();
                    break;
                  }
              }
            }
            else{
              //Sets if the model or does not have a fill
              if(lineRead.hasNext()){
                line = lineRead.next();
                if(line.matches("bb"))
                  isBillBoard = true;
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
      System.out.println(ERROR+"FILE "+dir+NOT_FOUND);
      System.exit(1);
    }
    if(messageEnabled)
      System.out.println(TYPE[1]+" "+dir+SUCCESS);
    return new Geometry(model, poly, isBillBoard);
  }
  public static ModelColours loadPallet(String dir){
    int vertexCount = 0;
    int[][] colour = new int[1][2]; //i, 0 = stroke; i, 1 = fill
    float[][] vertexColours = new float[0][4]; //Vertex colour list
    File file; //Stores the current file
    Scanner fileReader; //Reads each line of the file
    int strokeIndex = -1; //Tracks the current stroke
    int fillIndex = -1; //Tracks the current fill
    int polygonCount = 0;
    int backIndex = 0; //Tracks the current index of the list of polygons that are exempt from backface culling
    int backFillIndex = -1;//Tracks the current back face fill index
    int backStrokeIndex = -1; //Tracks the current back face stroke index
    int vertexColourIndex = -1; //Tracks the current vertex colour
    int vertexColourChannel = 0; //Tracks the current colour channel for the current vertex colour
    String currLine = ""; //Holds the current line
    char currArray = 0; //Holds the array currently being filled (taken from the start of the line)
    boolean[] sizeSet = {false, false, false, false}; //0 = colour array
                                               //1 = back visible array
                                               //2 = back colour array, 
                                               //3 = vertex colour array
                                               //;If true, tells the program to skip setting the array's size
    int[] backVisible = new int[0]; //Stores whether or not each polygon has backface culling (false = has; true = does not have)
    int[][] backColour = new int[0][2];
    file = new File(dir);
    try{
      if(messageEnabled)
        System.out.println(LOADING+TYPE[2]+": "+dir);
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
              case 'V':
                if(!sizeSet[3]){
                  if(lineReader.hasNextInt()){
                    vertexCount = lineReader.nextInt();
                    vertexColours = new float[vertexCount][4];
                    for(int i = 0; i < vertexCount; i++){
                      vertexColours[i][0] = 1;
                      vertexColours[i][1] = 1;
                      vertexColours[i][2] = 1;
                      vertexColours[i][3] = 1;
                    }
                    sizeSet[3] = true;
                    break;
                  }
                  System.out.println(NOT_AN_INT);
                  lineReader.close();
                  fileReader.close();
                  lineRead.close();
                  System.exit(1);
                }
                System.out.println(ARRAYS[5]+ALREADY_SET);
                if(lineRead.hasNext())
                  lineRead.next();
                break;
              case 'p':
                if(lineReader.hasNextInt()){
                  polygonCount = lineReader.nextInt();
                  break;
                }
                System.out.println(NOT_AN_INT);
                lineReader.close();
                fileReader.close();
                lineRead.close();
                System.exit(1);
                break;
              case 'c':
                if(!sizeSet[0]){
                  if(lineReader.hasNextInt()){
                    colour = new int[lineReader.nextInt()][2];
                    sizeSet[0] = true;
                    break;
                  }
                  System.out.println(NOT_AN_INT);
                  lineReader.close();
                  fileReader.close();
                  System.exit(1);

                } 
                System.out.println(ARRAYS[2]+ALREADY_SET);
                if(lineRead.hasNext())
                  lineRead.next();
                break;
            case 'i':
              if(!sizeSet[1]){
                if(lineReader.hasNextInt()){
                  backVisible = new int[lineReader.nextInt()];
                  for(int i = 0; i < backVisible.length; i++)
                    backVisible[i] = -1;
                  sizeSet[1] = true;
                  break;
                }
                System.out.println(NOT_AN_INT);
                lineReader.close();
                fileReader.close();
                lineRead.close();
                System.exit(1);
              }

              System.out.println(ARRAYS[3]+ALREADY_SET);
              if(lineReader.hasNext())
                lineReader.next();
              break;
            case 'b':
              if(!sizeSet[2]){
                if(lineReader.hasNextInt()){
                  backColour = new int[lineReader.nextInt()][2];
                  for(int i = 0; i < backColour.length; i++){
                    backColour[i][0] = -1;
                    backColour[i][1] = -1;
                  }
                  sizeSet[2] = true;
                  break;
                }
                System.out.println(NOT_AN_INT);
                lineReader.close();
                fileReader.close();
                lineRead.close();
                System.exit(1);
              }

              System.out.println(ARRAYS[4]+ALREADY_SET);
              if(lineReader.hasNext())
                lineReader.next();
            default:
              //Cases for setting the current array to be modified
              currArray = line.charAt(0);
              switch(currArray){
                case 'a':
                  vertexColourIndex++;
                  vertexColourChannel = 0;
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
                case 's': //Stroke data
                  if(strokeIndex < colour.length){
                    if(lineRead.hasNextInt()){
                      colour[strokeIndex][0] = lineRead.nextInt();
                      break;
                    }
                    System.out.println(NOT_AN_INT);
                    fileReader.close();
                    lineReader.close();
                    lineRead.close();
                    System.exit(1);
                  } 
                  if(colour.length <= 0){
                    System.out.println(ERROR+ARRAYS[2]+TOO_SMALL);
                    fileReader.close();
                    lineReader.close();
                    lineRead.close();
                    System.exit(1);
                    }
                    if(lineRead.hasNext()){
                      lineRead.next();
                      break;
                    }
                case 'a':
                  if(vertexColourIndex < vertexColours.length && vertexColourChannel < 4){
                    vertexColours[vertexColourIndex][vertexColourChannel] = (float)lineRead.nextDouble();
                    vertexColourChannel++;
                    break;
                  }
                  if(vertexColours.length <= 0){
                    System.out.println(ERROR+ARRAYS[5]+TOO_SMALL);
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
                    System.out.println(NOT_AN_INT);
                    fileReader.close();
                    lineReader.close();
                    lineRead.close();
                    System.exit(1);
                  } 
                  if(colour.length <= 0){
                    System.out.println(ERROR+ARRAYS[2]+TOO_SMALL);
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
                        System.out.println(IS_FULL);
                        if(lineRead.hasNext())
                          lineRead.next();
                      }
                      break;
                    }
                    System.out.println(NOT_AN_INT);
                  }
                  else
                    System.out.println(ERROR+ARRAYS[3]+TOO_SMALL);
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
                    System.out.println(NOT_AN_INT);
                    fileReader.close();
                    lineReader.close();
                    lineRead.close();
                    System.exit(1);
                  } 
                  if(backColour.length <= 0){
                    System.out.println(ERROR+ARRAYS[4]+TOO_SMALL);
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
                    System.out.println(NOT_AN_INT);
                    fileReader.close();
                    lineReader.close();
                    lineRead.close();
                    System.exit(1);
                  } 
                  if(backColour.length <= 0){
                    System.out.println(ERROR+ARRAYS[4]+TOO_SMALL);
                    fileReader.close();
                    lineReader.close();
                    lineRead.close();
                    System.exit(1);
                    }
                    if(lineRead.hasNext()){
                      lineRead.next();
                      break;
                    }
  
              }
            }
            else{
              //Sets if the model or does not have a fill
              if(lineRead.hasNext())
                line = lineRead.next();
            }
          }
          lineRead.close();
        }
        lineReader.close();
      }
      fileReader.close();
    }
    catch(FileNotFoundException e){
      System.out.println(ERROR+"FILE "+dir+NOT_FOUND);
      System.exit(1);
    }
    ModelColours outputColours = new ModelColours(colour, vertexColours, polygonCount, vertexCount);
    outputColours.initBackVisible(backVisible.length);
    outputColours.initBackColours(backColour.length);
    for(int i = 0; i < backVisible.length; i++){
      if(backVisible[i] > -1)
        outputColours.setBackVisible(backVisible[i]);
    }
    for(int i = 0; i < backColour.length; i++)
      outputColours.setBackColour(i, backColour[i][0], backColour[i][1]);
    if(messageEnabled)
      System.out.println(TYPE[2]+" "+dir+SUCCESS);
    return outputColours;
  }
}
