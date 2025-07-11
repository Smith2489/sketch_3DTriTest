package Renderer.Objects.Parents;
import Wrapper.*;
import Actions.ObjectActions.*;
import Renderer.ScreenDraw.MVP;
//Superclass for objects that are drawn in a scene
public class SceneObject extends ScalableEntity{
    //1 = isAttachedToCamera, 2 = noDepth
    private FloatWrapper uniTint = new FloatWrapper(); //The overall transparency of the object

    public SceneObject(){
        super();
        flags = 0;
        uniTint.val = 1;
    }
    protected SceneObject(byte defaultFlags){
        super(defaultFlags);
        uniTint.val = 1;
    }
    public SceneObject(float[] newPos){
        super(newPos);
        flags = 0;
        uniTint.val = 1;
    }
    public SceneObject(float x, float y, float z){
        super(x, y, z);
        flags = 0;
        uniTint.val = 1;
    }
    protected SceneObject(float[] newPos, byte defaultFlags){
        super(newPos, defaultFlags);
        uniTint.val = 1;
    }
    protected SceneObject(float x, float y, float z, byte defaultFlags){
        super(x, y, z, defaultFlags);
        uniTint.val = 1;
    }

    //Adds an action and initializes it
    public void addAction(ModelAction newAction){
        if(newAction != null){
            newAction.setModelTint(uniTint);
            super.appendAction(newAction);
            actionList.add(newAction);
          }
          else
            System.out.println(NULL_ACTION);
    }

    //Sets the object to be drawn on the same layer or the layer above
    public void setDepthWrite(boolean reversed){
        if(reversed)
            flags|=4;
        else
            flags&=-5;
    }
    public boolean returnDepthWrite(){
        return (flags & 4) == 4;
    }

    //Attaches the object to the camera
    public void setAttachedToCamera(boolean attachedToCamera){
        if(attachedToCamera)
            flags|=2;
        else
            flags&=-3;
    }
    public boolean returnAttachedToCamera(){
        return (flags & 2) == 2;
    }

    //Sets the overall transparency of the object
    public void setModelTint(float newTint){
        uniTint.val = Math.max(0, Math.min(newTint, 1));
    }
    public float returnModelTint(){
        return uniTint.val;
    }

    public void setModelMatrix(){
        modelMatrix.copy(MVP.inverseViewMatrix(pos, rot, scale, shear));
    }

    public void copy(Object o){
        if(o instanceof SceneObject){
            SceneObject s = (SceneObject)o;
            super.copy(s);
            uniTint.val = s.uniTint.val;
        }
    }
    public void copy(SceneObject s){
        super.copy(s);
        uniTint.val = s.uniTint.val;
    }

    public boolean equals(Object o){
        if(o instanceof SceneObject){
            SceneObject s = (SceneObject)o;
            boolean isEquals = super.equals(s);
            isEquals&=(Math.abs(uniTint.val - s.uniTint.val) <= EPSILON);
            return isEquals;
        }
        else
            return false;
    }
    public boolean equals(SceneObject s){
        boolean isEquals = super.equals(s);
        isEquals&=(Math.abs(uniTint.val - s.uniTint.val) <= EPSILON);
        return isEquals;
    }
}