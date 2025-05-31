package Renderer.Objects;
import java.util.*;
import Wrapper.*;
import Actions.*;
import Maths.LinearAlgebra.*;
import Renderer.ScreenDraw.MVP;
import Renderer.ModelDataHandler.*;
//Class for abstracting away a model's data
public class Model{
  private Geometry mesh;
  private ModelColours colours;
  private float[] modelPosition = {0, 0, 0}; //Model position in space (x, y, z)
  private float[] modelAngles = {0, 0, 0}; //Model rotation
  private float[] modelScale = {1, 1, 1}; //Model scale
  private float[][] modelShear = {{0, 0}, {0, 0}, {0, 0}};
  //bit 0 = hasStroke, 
  //bit 1 = hasFill, 
  //bit 2 = attached to camera, 
  //bit 3 = noDepth, 
  //bit 4 = inverted, 
  //bit 5 = is Gauroud, 
  //bit 6 = always perform actions
  private byte flags = 32; 
  private Matrix modelMatrix = new Matrix();
  private LinkedList<Action> actionList = new LinkedList<Action>();
  private Action tempAction;
  private float brightness = 1;
  private float shininess = 4;
  private float maxFizzel = 1;
  private float fizzelThreshold = 1.1f;
  private FloatWrapper uniTint = new FloatWrapper();
  //Default constructor
  public Model(){
    mesh = new Geometry();
    colours = new ModelColours();
    modelMatrix = new Matrix();
    modelPosition[0] = 0;
    modelPosition[1] = 0;
    modelPosition[2] = 0;
    modelAngles[0] = 0;
    modelAngles[1] = 0;
    modelAngles[2] = 0;
    modelScale[0] = 1;
    modelScale[1] = 1;
    modelScale[2] = 1;
    brightness = 1;
    shininess = 4;
    maxFizzel = 1;
    fizzelThreshold = 1.1f;
    uniTint.val = 1;
    flags = 34;
    actionList = new LinkedList<Action>();
  }
  
  //Constructor with 3D array of vertex positions, 2D array of colours, and 2 booleans for if the model has a stroke or a fill
  public Model(float[][] pointSet, int[][] polygonSet, int[][] colourSet, boolean hasStroke, boolean hasFill, boolean isBill){
    mesh = new Geometry(pointSet, polygonSet, isBill);
    colours = new ModelColours(colourSet, new float[0][3], polygonSet.length, mesh.returnPoints().length);
    modelMatrix = new Matrix();
    //Initializing data
    modelPosition[0] = 0;
    modelPosition[1] = 0;
    modelPosition[2] = 0;
    modelAngles[0] = 0;
    modelAngles[1] = 0;
    modelAngles[2] = 0;
    modelScale[0] = 1;
    modelScale[1] = 1;
    modelScale[2] = 1;
    brightness = 1;
    shininess = 4;
    maxFizzel = 1;
    uniTint.val = 1;
    fizzelThreshold = 1.1f;
    flags = (byte)(((hasStroke) ? 1 : 0)|((hasFill) ? 2 : 0)|32);
    actionList = new LinkedList<Action>();
    
  }
  //Constructor with 3D array of vertex positions and 2D array of colours
  public Model(float[][] pointSet, int[][] polygonSet, int[][] colourSet){
    mesh = new Geometry(pointSet, polygonSet);
    colours = new ModelColours(colourSet, new float[0][3], polygonSet.length, mesh.returnPoints().length);
    modelMatrix = new Matrix();
    //Initializing data
    modelPosition[0] = 0;
    modelPosition[1] = 0;
    modelPosition[2] = 0;
    modelAngles[0] = 0;
    modelAngles[1] = 0;
    modelAngles[2] = 0;
    modelScale[0] = 1;
    modelScale[1] = 1;
    modelScale[2] = 1;
    shininess = 4;
    flags = 34;
    maxFizzel = 1;
    uniTint.val = 1;
    fizzelThreshold = 1.1f;
    actionList = new LinkedList<Action>();
  }

  public Model(Geometry newMesh, ModelColours newPallet){
    mesh = newMesh;
    colours = newPallet;
    modelMatrix = new Matrix();
    modelPosition[0] = 0;
    modelPosition[1] = 0;
    modelPosition[2] = 0;
    modelAngles[0] = 0;
    modelAngles[1] = 0;
    modelAngles[2] = 0;
    modelScale[0] = 1;
    modelScale[1] = 1;
    modelScale[2] = 1;
    shininess = 4;
    flags = 34;
    brightness = 1;
    maxFizzel = 1;
    uniTint.val = 1;
    fizzelThreshold = 1.1f;
    actionList = new LinkedList<Action>();
  }
  public Model(Geometry newMesh, ModelColours newPallet, boolean hasStroke, boolean hasFill){
    mesh = newMesh;
    colours = newPallet;
    modelMatrix = new Matrix();
    modelPosition[0] = 0;
    modelPosition[1] = 0;
    modelPosition[2] = 0;
    modelAngles[0] = 0;
    modelAngles[1] = 0;
    modelAngles[2] = 0;
    modelScale[0] = 1;
    modelScale[1] = 1;
    modelScale[2] = 1;
    shininess = 4;
    maxFizzel = 1;
    uniTint.val = 1;
    fizzelThreshold = 1.1f;
    flags = (byte)(((hasStroke) ? 1 : 0)|((hasFill) ? 2 : 0)|32);
    actionList = new LinkedList<Action>();
    brightness = 1;
  }
  public void setFizzelParameters(float newMax, float newThreshold){
    maxFizzel = newMax;
    fizzelThreshold = newThreshold;
  }
  public float returnMaxFizzel(){
    return maxFizzel;
  }
  public float returnFizzelThreshold(){
    return fizzelThreshold;
  }
  public void addAction(ModelAction newAction){
    if(newAction != null){
      newAction.setPos(modelPosition);
      newAction.setRot(modelAngles);
      newAction.setScale(modelScale);
      newAction.setShear(modelShear);
      newAction.setMatrix(modelMatrix);
      newAction.setModelTint(uniTint);
      actionList.add(newAction);
    }
    else
      System.out.println("ERROR: ACTION CANNOT BE NULL");
  }
  public Action removeFirstAction(){
    return actionList.removeFirst();
  }
  public Action removeLastAction(){
    return actionList.removeLast();
  }
  public Action removeAction(int i){
    return actionList.remove(i);
  }
  public void clearActionList(){
    actionList.clear();
  }
  public boolean hasActions(){
    return !actionList.isEmpty();
  }
  public int numOfActions(){
    return actionList.size();
  }
  public void executeActions(){
    int length = actionList.size();
    for(int i = 0; i < length; i++){
      tempAction = actionList.removeFirst();
      actionList.add(tempAction);
      tempAction.perform();
    }
  }
  public void alwaysPerform(boolean perform){
    if(perform)
      flags|=64;
    else
      flags&=-65;
  }
  public boolean alwaysPerform(){
    return ((flags & 64) == 64);
  }
  public void setModelTint(float newTint){
    uniTint.val = Math.max(0, Math.min(newTint, 1));
  }
  public float returnModelTint(){
    return uniTint.val;
  }
  public float returnBrightness(){
    return brightness;
  }
  public void setBrightness(float newBrightness){
    brightness = Math.max(0, Math.min(newBrightness, 1));
  }
  public float returnShininess(){
    return shininess;
  }
  public void setShininess(float newShininess){
    shininess = Math.max(0, newShininess);
  }
  public boolean equals(Object o){
    if(o instanceof Model){
      Model m = (Model)o;
      boolean isEqual = true;
      isEqual&=(mesh.equals(m.mesh));
      isEqual&=colours.equals(m.colours);
      isEqual&=modelMatrix.equals(m.modelMatrix);
      isEqual&=(Math.abs(brightness - m.brightness) <= 0.0001);
      isEqual&=(Math.abs(shininess - m.shininess) <= 0.0001);
      isEqual&=(Math.abs(maxFizzel - m.maxFizzel) <= 0.0001);
      isEqual&=(Math.abs(fizzelThreshold - m.fizzelThreshold) <= 0.0001f);
      isEqual&=(Math.abs(uniTint.val - m.uniTint.val) <= 0.0001);
      for(byte i = 0; i < 3; i++){
        isEqual&=(Math.abs(modelPosition[i] - m.modelPosition[i]) <= 0.0001);
        isEqual&=(Math.abs(modelScale[i] - m.modelScale[i]) <= 0.0001);
        isEqual&=(Math.abs(modelAngles[i] - m.modelAngles[i]) <= 0.0001);
        isEqual&=(Math.abs(modelShear[i][0] - m.modelShear[i][0]) <= 0.0001);
        isEqual&=(Math.abs(modelShear[i][1] - m.modelShear[i][1]) <= 0.0001);
      }
      return isEqual;
    }
    else
    return false;
  }
  public boolean equals(Model m){
    boolean isEqual = true;
    isEqual&=(mesh.equals(m.mesh));
    isEqual&=colours.equals(m.colours);
    isEqual&=modelMatrix.equals(m.modelMatrix);
    isEqual&=(Math.abs(brightness - m.brightness) <= 0.0001);
    isEqual&=(Math.abs(shininess - m.shininess) <= 0.0001);
    isEqual&=(Math.abs(maxFizzel - m.maxFizzel) <= 0.0001);
    isEqual&=(Math.abs(fizzelThreshold - m.fizzelThreshold) <= 0.0001f);
    isEqual&=(Math.abs(uniTint.val - m.uniTint.val) <= 0.0001);
    for(byte i = 0; i < 3; i++){
      isEqual&=(Math.abs(modelPosition[i] - m.modelPosition[i]) <= 0.0001);
      isEqual&=(Math.abs(modelScale[i] - m.modelScale[i]) <= 0.0001);
      isEqual&=(Math.abs(modelAngles[i] - m.modelAngles[i]) <= 0.0001);
      isEqual&=(Math.abs(modelShear[i][0] - m.modelShear[i][0]) <= 0.0001);
      isEqual&=(Math.abs(modelShear[i][1] - m.modelShear[i][1]) <= 0.0001);
    }
    return isEqual;
  }
  public void copy(Object o){
    if(o instanceof Model){
      Model m = (Model)o;
      mesh = m.mesh;
      colours = m.colours;
      modelMatrix.copy(m.modelMatrix);
      brightness = m.brightness;
      shininess = m.shininess;
      maxFizzel = m.maxFizzel;
      fizzelThreshold = m.fizzelThreshold;
      uniTint.val = m.uniTint.val;
      for(byte i = 0; i < 3; i++){
        modelPosition[i] = m.modelPosition[i];
        modelScale[i] = m.modelScale[i];
        modelAngles[i] = m.modelAngles[i];
        modelShear[i][0] = m.modelShear[i][0];
        modelShear[i][1] = m.modelShear[i][1];
      }
    }
  }
  public void copy(Model m){
    mesh = m.mesh;
    colours = m.colours;
    modelMatrix.copy(m.modelMatrix);
    brightness = m.brightness;
    shininess = m.shininess;
    maxFizzel = m.maxFizzel;
    fizzelThreshold = m.fizzelThreshold;
    uniTint.val = m.uniTint.val;
    for(byte i = 0; i < 3; i++){
      modelPosition[i] = m.modelPosition[i];
      modelScale[i] = m.modelScale[i];
      modelAngles[i] = m.modelAngles[i];
      modelShear[i][0] = m.modelShear[i][0];
      modelShear[i][1] = m.modelShear[i][1];
    }
  }
  
  public Geometry returnMeshPtr(){
    return mesh;
  }
  public ModelColours returnPalletPtr(){
    return colours;
  }
  
  public void setGeometryPtr(Geometry newGeometry){
    mesh = newGeometry;
  }
  public void setPalletPtr(ModelColours newPallet){
    colours = newPallet;
  }
  //Setting the model's position
  public void setPosition(float x, float y, float z){
    modelPosition[0] = x;
    modelPosition[1] = y;
    modelPosition[2] = z;
  }
  //Setting the model's rotation
  public void setAngle(float alpha, float beta, float gamma){
    modelAngles[0] = alpha;
    modelAngles[1] = beta;
    modelAngles[2] = gamma;
  }
  //Setting the model's scale
  public void setScale(float sX, float sY, float sZ){
    modelScale[0] = sX;
    modelScale[1] = sY;
    modelScale[2] = sZ;
  }
  
  public void setShear(float[][] shear){
    for(byte i = 0; i < 3; i++){
      modelShear[i][0] = shear[i][0];
      modelShear[i][1] = shear[i][1];
    }
  }
  
  public void setShear(float[] shearX, float[] shearY, float[] shearZ){
    modelShear[0][0] = shearX[0];
    modelShear[0][1] = shearX[1];
    modelShear[1][0] = shearY[0];
    modelShear[1][1] = shearY[1];
    modelShear[2][0] = shearZ[0];
    modelShear[2][1] = shearZ[1];
  }
  public void setShearX(float x1, float x2){
    modelShear[0][0] = x1;
    modelShear[0][1] = x2;
  }
  public void setShearX(float[] shearX){
    modelShear[0][0] = shearX[0];
    modelShear[0][1] = shearX[1];
  }
  public void setShearY(float y1, float y2){
    modelShear[1][0] = y1;
    modelShear[1][1] = y2;
  }
  public void setShearY(float[] shearY){
    modelShear[1][0] = shearY[0];
    modelShear[1][1] = shearY[1];
  }
    public void setShearZ(float z1, float z2){
    modelShear[2][0] = z1;
    modelShear[2][1] = z2;
  }
  public void setShearZ(float[] shearZ){
    modelShear[2][0] = shearZ[0];
    modelShear[2][1] = shearZ[1];
  }
  public void setModelMatrix(Matrix newModel){
    if(Objects.nonNull(newModel) && newModel.returnWidth() == 4 && newModel.returnHeight() == 4)
      modelMatrix.copy(newModel);
    else
      System.out.println("ERROR: MATRIX MUST BE A VALID 4x4 MATRIX");
  }
  public void setModelMatrix(){
    modelMatrix.copy(MVP.inverseViewMatrix(modelPosition, modelAngles, modelScale, modelShear));
  }


  public void setTransformations(float posX, float posY, float posZ, float alpha, float beta, float gamma, float scX, float scY, float scZ, float shrX1, float shrX2, float shrY1, float shrY2, float shrZ1, float shrZ2){
    modelPosition[0] = posX;
    modelPosition[1] = posY;
    modelPosition[2] = posZ;
    modelAngles[0] = alpha;
    modelAngles[1] = beta;
    modelAngles[2] = gamma;
    modelScale[0] = scX;
    modelScale[1] = scY;
    modelScale[2] = scZ;
    modelShear[0][0] = shrX1;
    modelShear[0][1] = shrX2;
    modelShear[1][0] = shrY1;
    modelShear[1][1] = shrY2;
    modelShear[2][0] = shrZ1;
    modelShear[2][1] = shrZ2;
  }
  public void setTransformations(float[] pos, float[] rotation, float[] scale, float[] shearX, float[] shearY, float[] shearZ){
    for(byte i = 0; i < 3; i++){
      modelPosition[i] = pos[i];
      modelAngles[i] = rotation[i];
      modelScale[i] = scale[i];
    }
    modelShear[0][0] = shearX[0];
    modelShear[0][1] = shearX[1];
    modelShear[1][0] = shearY[0];
    modelShear[1][1] = shearY[1];
    modelShear[2][0] = shearX[0];
    modelShear[2][1] = shearX[1];
  }
  public void setTransformations(float[] pos, float[] rotation, float[] scale, float shrX1, float shrX2, float shrY1, float shrY2, float shrZ1, float shrZ2){
    for(byte i = 0; i < 3; i++){
      modelPosition[i] = pos[i];
      modelAngles[i] = rotation[i];
      modelScale[i] = scale[i];
    }
    modelShear[0][0] = shrX1;
    modelShear[0][1] = shrX2;
    modelShear[1][0] = shrY1;
    modelShear[1][1] = shrY2;
    modelShear[2][0] = shrZ1;
    modelShear[2][1] = shrZ2;
  }
  public void setTransformations(float[] pos, float[] rotation, float[] scale, float[][] shear){
    for(byte i = 0; i < 3; i++){
      modelPosition[i] = pos[i];
      modelAngles[i] = rotation[i];
      modelScale[i] = scale[i];
      modelShear[i][0] = shear[i][0];
      modelShear[i][1] = shear[i][1];
    }
  }

  public void setFlags(boolean hasStroke, boolean hasFill, boolean isAttached, boolean noDepth, boolean isInverted){
    flags = (byte)(((hasStroke) ? 1 : 0) | ((hasFill) ? 2 : 0) | ((isAttached) ? 4 : 0) | ((noDepth) ? 8 : 0) | ((isInverted) ? 16 : 0));
  }

  //Setting if a model has stroke
  public void setHasStrokes(boolean hasStroke){
    if(hasStroke)
      flags|=1;
    else
      flags&=-2;
  }
  //Setting if a model has fill
  public void setHasFill(boolean hasFill){
    if(hasFill)
      flags|=2;
    else
      flags&=-3;
  }
  public void setIsBillBoard(boolean isBill){
    mesh.setIsBillBoard(isBill);
  }
  public void setAttachedToCamera(boolean isAttached){
    if(isAttached)
      flags|=4;
    else
      flags&=-5;
  }
  public void setInverted(boolean isInverted){
    if(isInverted)
      flags|=16;
    else
      flags&=-17;
  }
  public void disableDepth(boolean noDepth){
    if(noDepth)
      flags|=8;
    else
      flags&=-9;
  }
  public void setGauroud(boolean isGauroud){
    if(isGauroud)
      flags|=32;
    else
      flags&=-33;
  }

  public int returnPolygonCount(){
    return mesh.returnPolygonCount();
  }
  public int[][] returnColours(){
    return colours.returnColours();
  }
  public boolean returnHasStroke(){
    return (flags & 1) == 1;
  }
  public boolean returnHasFill(){
    return (flags & 2) == 2;
  }
  public boolean returnIsBillBoard(){
    return mesh.returnIsBillBoard();
  }
  public boolean returnAttachedToCamera(){
    return (flags & 4) == 4;
  }
  public boolean returnNoDepth(){
    return (flags & 8) == 8;
  }
  public boolean returnIsInverted(){
    return (flags & 16) == 16;
  }
  public boolean isGauroud(){
    return (flags & 32) == 32;
  }
  public float[] returnNormals(int index){
    return mesh.returnNormals(index);
  }
  public float[][] returnPoints(){
    return mesh.returnPoints();
  }
  public int[][] returnPolygons(){
    return mesh.returnPolygons();
  }
  public float[] returnModelCentre(){
    return mesh.returnModelCentre();
  }
  public float[] returnPosition(){
    return modelPosition;
  }
  public float[] returnAngle(){
    return modelAngles;
  }
  public float[] returnScale(){
    return modelScale;
  }
  public float[][] returnShear(){
    return modelShear;
  }
  public float[] returnShearX(){
    return modelShear[0];
  }
  public float[] returnShearY(){
    return modelShear[1];
  }
  public float[] returnShearZ(){
    return modelShear[2];
  }
  public float[] returnMinVertices(){
    return mesh.returnMinVertices();
  }
  public float[] returnMaxVertices(){
    return mesh.returnMaxVertices();
  }
  public float[][] returnVertexNormals(){
    return mesh.returnVertexNormals();
  }

  public float[][] returnVertexColours(){
    return colours.returnVertexColours();
  }

  //Returns if a triangle is visible when facing away from the camera or not
  public boolean returnBackVisible(int index){
    return colours.returnBackVisible(index);
  }
  
  //Returns the length of the list of back face colours
  public int returnBackColourCount(){
    return colours.returnBackColourCount();
  }
  
  //Returns the stroke (sub=0) or fill (sub=1) of the triangle of interest
  public int returnBackColour(int index, byte sub){
    return colours.returnBackColour(index, sub);
  }
  
  public boolean isCompletelyBlack(){
    return colours.isCompletelyBlack();
  }

  //Returns the bounding box
  public float[][] returnBoundingBox(){
    return mesh.returnBoundingBox();
  }
  public Matrix returnModelMatrix(){
    return modelMatrix;
  }
}