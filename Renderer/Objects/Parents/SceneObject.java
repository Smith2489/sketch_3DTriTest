package Renderer.Objects.Parents;
import Wrapper.*;
import Actions.*;
//Superclass for objects that are drawn in a scene
public class SceneObject extends ScalableEntity{
    protected byte flags = 0; //0 = noDepth, 1 = isAttachedToCamera, 2 = always perform
    protected FloatWrapper uniTint = new FloatWrapper();

    public SceneObject(){
        super();
        flags = 0;
        uniTint.val = 1;
    }
    public SceneObject(byte defaultFlags){
        super();
        flags = defaultFlags;
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
    public SceneObject(float[] newPos, byte defaultFlags){
        super(newPos);
        flags = defaultFlags;
        uniTint.val = 1;
    }
    public SceneObject(float x, float y, float z, byte defaultFlags){
        super(x, y, z);
        flags = defaultFlags;
        uniTint.val = 1;
    }

    public void addAction(ModelAction newAction){
        if(newAction != null){
            super.addAction(newAction);
            newAction.setModelTint(uniTint);
            actionList.add(newAction);
          }
          else
            System.out.println("ERROR: ACTION CANNOT BE NULL");
    }

    public void alwaysPerform(boolean perform){
        if(perform)
            flags|=4;
        else
            flags&=-5;
    }
    public boolean alwaysPerform(){
        return (flags & 4) == 4;
    }

    public void setDepthWrite(boolean reversed){
        if(reversed)
            flags|=1;
        else
            flags&=-2;
    }
    public boolean returnDepthWrite(){
        return (flags & 1) == 1;
    }

    public void setAttachedToCamera(boolean attachedToCamera){
        if(attachedToCamera)
            flags|=2;
        else
            flags&=-3;
    }
    public boolean returnAttachedToCamera(){
        return (flags & 2) == 2;
    }

    public void setModelTint(float newTint){
        uniTint.val = Math.max(0, Math.min(newTint, 1));
    }
    public float returnModelTint(){
        return uniTint.val;
    }

    public void copy(Object o){
        if(o instanceof SceneObject){
            SceneObject s = (SceneObject)o;
            super.copy(s);
            flags = s.flags;
            uniTint.val = s.uniTint.val;
        }
    }
    public void copy(SceneObject s){
        super.copy(s);
        flags = s.flags;
        uniTint.val = s.uniTint.val;
    }

    public boolean equals(Object o){
        if(o instanceof SceneObject){
            SceneObject s = (SceneObject)o;
            boolean isEquals = super.equals(s);
            isEquals&=(flags == s.flags);
            isEquals&=(Math.abs(uniTint.val - s.uniTint.val) <= EPSILON);
            return isEquals;
        }
        else
            return false;
    }
    public boolean equals(SceneObject s){
        boolean isEquals = super.equals(s);
        isEquals&=(flags == s.flags);
        isEquals&=(Math.abs(uniTint.val - s.uniTint.val) <= EPSILON);
        return isEquals;
    }
}