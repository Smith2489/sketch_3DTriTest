import java.util.*;
//A class for defining specific instances of line-based models
public class LineObj {
    private LineModel obj; //A reference to a mesh
    private float[] pos = {0, 0, 0}; //The translation for the model
    private float[] rot = {0, 0, 0}; //The rotation for the model
    private float[] scl = {1, 1, 1}; //The scale for the model
    private float[][] shr = {{0, 0}, {0, 0}, {0, 0}};//The shear for the model
    private byte flags = 0; //0 = depth disable, 1 = is attached to camera, 2 = always perform action
    private FloatWrapper uniTint = new FloatWrapper();
    private Matrix modelMatrix = new Matrix(); //The model matrix for this object
    private LinkedList<Action> actionList = new LinkedList<Action>();
    private Action tempAction;
    public LineObj(){
        obj = new LineModel();
        modelMatrix = new Matrix();
        uniTint.val = 1;
        flags = 0;
        for(byte i = 0; i < 3; i++){
            pos[i] = 0;
            rot[i] = 0;
            scl[i] = 1;
            shr[i][0] = 0;
            shr[i][1] = 0;
        }
        actionList = new LinkedList<Action>();
    }
    public LineObj(float[][] endPoints, int[][] lines, int[] stroke){
        obj = new LineModel(endPoints, lines, stroke);
        modelMatrix = new Matrix();
        uniTint.val = 1;
        flags = 0;
        for(byte i = 0; i < 3; i++){
            pos[i] = 0;
            rot[i] = 0;
            scl[i] = 1;
            shr[i][0] = 0;
            shr[i][1] = 0;
        }
        actionList = new LinkedList<Action>();
    }
    public LineObj(LineModel line){
        obj = line;
        modelMatrix = new Matrix();
        uniTint.val = 1;
        flags = 0;
        for(byte i = 0; i < 3; i++){
            pos[i] = 0;
            rot[i] = 0;
            scl[i] = 1;
            shr[i][0] = 0;
            shr[i][1] = 0;
        }
        actionList = new LinkedList<Action>();
    }

    public void addAction(ModelAction newAction){
      if(newAction != null){
        newAction.setPos(pos);
        newAction.setRot(rot);
        newAction.setScale(scl);
        newAction.setShear(shr);
        newAction.setMatrix(modelMatrix);
        newAction.setModelTint(uniTint);
        actionList.add(newAction);
      }
      else
        System.out.println("ERRROR: ACTION CANNOT BE NULL");
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
        flags|=4;
    else
        flags&=-5;
  }
  public boolean alwaysPerform(){
    return ((flags & 4) == 4);
  }
  
  public void setModelTint(float newTint){
    uniTint.val = Math.max(0, Math.min(newTint, 1));
  }
  public float returnModelTint(){
    return uniTint.val;
  }

    //Sets the reference to the mesh
    public void setLineModelPtr(LineModel newPtr){
        obj = newPtr;
    }
    //Sets the translation to be applied to the model
    public void setPosition(float[] newPos){
        pos[0] = newPos[0];
        pos[1] = newPos[1];
        pos[2] = newPos[2];
    }
    public void setPosition(float x, float y, float z){
        pos[0] = x;
        pos[1] = y;
        pos[2] = z;
    }

    //Sets the rotation to be applied to the model
    public void setRotation(float[] newRot){
        rot[0] = newRot[0];
        rot[1] = newRot[1];
        rot[2] = newRot[2];
    }
    public void setRotation(float alpha, float beta, float gamma){
        rot[0] = alpha;
        rot[1] = beta;
        rot[2] = gamma;
    }

    //Sets the scale to be applied to the model
    public void setScale(float[] newScale){
        scl[0] = newScale[0];
        scl[1] = newScale[1];
        scl[2] = newScale[2];
    }
    public void setScale(float xScl, float yScl, float zScl){
        scl[0] = xScl;
        scl[1] = yScl;
        scl[2] = zScl;
    }

    //Sets the shear to be applied to the model
    public void setShear(float[][] newShear){
        shr[0][0] = newShear[0][0];
        shr[0][1] = newShear[0][1];
        shr[1][0] = newShear[1][0];
        shr[1][1] = newShear[1][1];
        shr[2][0] = newShear[2][0];
        shr[2][1] = newShear[2][1];
    }
    public void setShear(float[] shearX, float[] shearY, float[] shearZ){
        shr[0][0] = shearX[0];
        shr[0][1] = shearX[1];
        shr[1][0] = shearY[0];
        shr[1][1] = shearY[1];
        shr[2][0] = shearZ[0];
        shr[2][1] = shearZ[1];
    }
    public void setShear(float x1, float x2, float y1, float y2, float z1, float z2){
        shr[0][0] = x1;
        shr[0][1] = x2;
        shr[1][0] = y1;
        shr[1][1] = y2;
        shr[2][0] = z1;
        shr[2][1] = z2;
    }

    //Sets if the object should be drawn on top of everything else
    public void setDepthWrite(boolean depthDisable){
        if(depthDisable)
            flags|=1;
        else
            flags&=-2;
    }

    //Sets whether or not the object is attached to the camera
    public void setAttachedToCamera(boolean attachedToCamera){
        if(attachedToCamera)
            flags|=2;
        else
            flags&=-3;
    }

    //Sets the the model matrix for the object to an existing matrix
    public void setModelMatrix(Matrix newModel){
        if(Objects.nonNull(newModel) && newModel.returnWidth() == 4 && newModel.returnHeight() == 4)
            modelMatrix.copy(newModel);
        else
            System.out.println("ERROR: MATRIX MUST BE A VALID 4x4 MATRIX");
    }

    //Sets the model matrix for the object to a new matrix
    public void setModelMatrix(){
        modelMatrix.copy(MVP.inverseViewMatrix(pos, rot, scl, shr));
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
    //Returns the translation to be applied to the model
    public float[] returnPosition(){
        return pos;
    }

    //Returns the rotation to be applied to the model
    public float[] returnRotation(){
        return rot;
    }

    //Returns the scale to be applied to the model
    public float[] returnScale(){
        return scl;
    }

    //Returns the shear to be applied to the model
    public float[][] returnShear(){
        return shr;
    }
    public float[] returnShearX(){
        return shr[0];
    }
    public float[] returnShearY(){
        return shr[1];
    }
    public float[] returnShearZ(){
        return shr[2];
    }

    //Returns the line model's model matrix
    public Matrix returnModelMatrix(){
        return modelMatrix;
    }
    //Returns if the model should be drawn on top of everything else
    public boolean returnDepthDisable(){
        return (flags & 1) == 1;
    }
    
    //Returns if the model should be attached to the camera
    public boolean returnAttachedToCamera(){
        return (flags & 2) == 2;
    }
    
    //Returns if two specific line model objects are equal
    public boolean equals(Object o){
        if(o instanceof LineObj){
            LineObj l = (LineObj)o;
            boolean isEqual = obj.equals(l.obj);
            isEqual&=modelMatrix.equals(l.modelMatrix);
            isEqual&=(Math.abs(uniTint.val - l.uniTint.val) <= 0.0001);
            isEqual&=(flags == l.flags);
            for(byte i = 0; i < 3; i++){
                isEqual&=(Math.abs(pos[i]-l.pos[i]) <= 0.0001f);
                isEqual&=(Math.abs(rot[i]-l.rot[i]) <= 0.0001f);
                isEqual&=(Math.abs(scl[i]-l.scl[i]) <= 0.0001f);
                isEqual&=(Math.abs(shr[i][0]-l.shr[i][0]) <= 0.0001f);
                isEqual&=(Math.abs(shr[i][1]-l.shr[i][1]) <= 0.0001f);
            }
            return isEqual;
        }
        else
            return false;
    }
    public boolean equals(LineObj l){
        boolean isEqual = obj.equals(l.obj);
        isEqual&=modelMatrix.equals(l.modelMatrix);
        isEqual&=(Math.abs(uniTint.val - l.uniTint.val) <= 0.0001);
        isEqual&=(flags == l.flags);
        for(byte i = 0; i < 3; i++){
            isEqual&=(Math.abs(pos[i]-l.pos[i]) <= 0.0001f);
            isEqual&=(Math.abs(rot[i]-l.rot[i]) <= 0.0001f);
            isEqual&=(Math.abs(scl[i]-l.scl[i]) <= 0.0001f);
            isEqual&=(Math.abs(shr[i][0]-l.shr[i][0]) <= 0.0001f);
            isEqual&=(Math.abs(shr[i][1]-l.shr[i][1]) <= 0.0001f);
        }
        return isEqual;
    }

    //Copies one line model object to the current one
    public void copy(Object o){
        if(o instanceof LineObj){
            LineObj l = (LineObj)o;
            obj = l.obj;
            uniTint.val = l.uniTint.val;
            flags = l.flags;
            for(byte i = 0; i < 3; i++){
                pos[i] = l.pos[i];
                rot[i] = l.rot[i];
                scl[i] = l.scl[i];
                shr[i][0] = l.shr[i][0];
                shr[i][1] = l.shr[i][1];
            }
            modelMatrix.copy(l.modelMatrix);
        }
    }
    public void copy(LineObj l){
        obj = l.obj;
        flags = l.flags;
        uniTint.val = l.uniTint.val;
        for(byte i = 0; i < 3; i++){
            pos[i] = l.pos[i];
            rot[i] = l.rot[i];
            scl[i] = l.scl[i];
            shr[i][0] = l.shr[i][0];
            shr[i][1] = l.shr[i][1];
        }
        modelMatrix.copy(l.modelMatrix);
    }

}
