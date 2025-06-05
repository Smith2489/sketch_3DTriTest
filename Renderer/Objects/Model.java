package Renderer.Objects;
import Renderer.ModelDataHandler.*;
import Renderer.Objects.Parents.*;
//Class for abstracting away triangle mesh object data
public class Model extends ShadedObject{
  private Geometry mesh;
  private ModelColours colours;
  //Flag bits:
  //bit 6: Inverted

  //Default constructor
  public Model(){
    super((byte)40);
    mesh = new Geometry();
    colours = new ModelColours();
  }
  
  //Constructor with 3D array of vertex positions, 2D array of colours, and 2 booleans for if the model has a stroke or a fill
  public Model(float[][] pointSet, int[][] polygonSet, int[][] colourSet, boolean hasStroke, boolean hasFill, boolean isBill){
    super((byte)(((hasFill) ? 8 : 0)|((hasStroke) ? 16 : 0) | 32));
    mesh = new Geometry(pointSet, polygonSet, isBill);
    colours = new ModelColours(colourSet, new float[0][3], polygonSet.length, mesh.returnPoints().length);
  }
  //Constructor with 3D array of vertex positions and 2D array of colours
  public Model(float[][] pointSet, int[][] polygonSet, int[][] colourSet){
    super((byte)40);
    mesh = new Geometry(pointSet, polygonSet);
    colours = new ModelColours(colourSet, new float[0][3], polygonSet.length, mesh.returnPoints().length);
  }

  public Model(Geometry newMesh, ModelColours newPallet){
    super((byte)40);
    mesh = newMesh;
    colours = newPallet;
  }
  public Model(Geometry newMesh, ModelColours newPallet, boolean hasStroke, boolean hasFill){
    super((byte)(((hasFill) ? 8 : 0)|((hasStroke) ? 16 : 0) | 32));
    mesh = newMesh;
    colours = newPallet;
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
  
  
  public void setIsBillBoard(boolean isBill){
    mesh.setIsBillBoard(isBill);
  }
  public void setInverted(boolean isInverted){
    if(isInverted)
      flags|=64;
    else
      flags&=-65;
  }

  public int returnPolygonCount(){
    return mesh.returnPolygonCount();
  }
  public int[][] returnColours(){
    return colours.returnColours();
  }

  public boolean returnIsBillBoard(){
    return mesh.returnIsBillBoard();
  }

  public boolean returnIsInverted(){
    return (flags & 64) == 64;
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

  public void copy(Object o){
    if(o instanceof Model){
      Model m = (Model)o;
      super.copy(m);
      mesh = m.mesh;
      colours = m.colours;
    }
  }
  public void copy(Model m){
    super.copy(m);
    mesh = m.mesh;
    colours = m.colours;
  }

  public boolean equals(Object o){
    if(o instanceof Model){
      Model m = (Model)o;
      boolean isEqual = super.equals(m);
      isEqual&=(mesh.equals(m.mesh));
      isEqual&=colours.equals(m.colours);
      return isEqual;
    }
    else
      return false;
  }
  public boolean equals(Model m){
    boolean isEqual = super.equals(m);
    isEqual&=(mesh.equals(m.mesh));
    isEqual&=colours.equals(m.colours);
    return isEqual;
  }

  

}