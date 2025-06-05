package Renderer.Objects;
import Renderer.ModelDataHandler.LineModel;
import Renderer.Objects.Parents.*;
//Class for abstracting away line mesh object data
public class LineObj extends SceneObject{
    private LineModel obj; //A reference to a mesh
    public LineObj(){
        super();
        obj = new LineModel();
    }
    public LineObj(float[][] endPoints, int[][] lines, int[] stroke){
        super();
        obj = new LineModel(endPoints, lines, stroke);
    }
    public LineObj(LineModel line){
        super();
        obj = line;
    }

    //Sets the reference to the mesh
    public void setLineModelPtr(LineModel newPtr){
        obj = newPtr;
    }

    //Returns a reference to the mesh
    public LineModel returnLineModelPtr(){
        return obj;
    }
    //Returns data describing the mesh
    public int returnStroke(int index){
        return obj.returnStroke(index);
    }
    public float[][] returnBoundingBox(){
        return obj.returnBoundingBox();
    }
    public float[] returnMinVertices(){
        return obj.returnMinVertices();
    }
    public float[] returnMaxVertices(){
        return obj.returnMaxVertices();
    }
    public int returnLineCount(){
        return obj.returnLineCount();
    }
    public float[] returnModelCentre(){
        return obj.returnModelCentre();
    }
    
    //Copies one line model object to the current one
    public void copy(Object o){
        if(o instanceof LineObj){
            LineObj l = (LineObj)o;
            super.copy(l);
            obj = l.obj;
        }
    }
    public void copy(LineObj l){
        super.copy(l);
        obj = l.obj;
    }

    //Returns if two specific line model objects are equal
    public boolean equals(Object o){
        if(o instanceof LineObj){
            LineObj l = (LineObj)o;
            boolean isEqual = super.equals(l);
            isEqual&=obj.equals(l.obj);
            return isEqual;
        }
        else
            return false;
    }
    public boolean equals(LineObj l){
        boolean isEqual = super.equals(l);
        isEqual&=obj.equals(l.obj);
        return isEqual;
    }
}