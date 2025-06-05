package Renderer.Objects.Parents;
import java.util.LinkedList;
import Renderer.Objects.Physics.*;
import Actions.Action;
//Root superclass for all entities that define a scene (lights, cameras, etc.)
public class SceneEntity{
    protected final float EPSILON = 0.0001f;
    protected float[] pos = {0, 0, 0}; //Object's position
    protected float[] rot = {0, 0, 0}; //Object's rotation
    private Physics physics = new Physics(pos, rot); //Physics attached to object
    protected LinkedList<Action> actionList = new LinkedList<Action>(); //Actions associated with object
    private Action tempAction;
    public SceneEntity(){
        pos[0] = 0;
        pos[1] = 0;
        pos[2] = 0;
        rot[0] = 0;
        rot[1] = 0;
        rot[2] = 0;
        physics = new Physics(pos, rot);
        actionList = new LinkedList<Action>();
    }
    public SceneEntity(float[] newPosition){
        pos[0] = newPosition[0];
        pos[1] = newPosition[1];
        pos[2] = newPosition[2];
        rot[0] = 0;
        rot[1] = 0;
        rot[2] = 0;
        physics = new Physics(pos, rot);
        actionList = new LinkedList<Action>();
    }
    public SceneEntity(float x, float y, float z){
        pos[0] = x;
        pos[1] = y;
        pos[2] = z;
        rot[0] = 0;
        rot[1] = 0;
        rot[2] = 0;
        physics = new Physics(pos, rot);
        actionList = new LinkedList<Action>();
    }
    public SceneEntity(float[] newPosition, float[] newRotation){
        pos[0] = newPosition[0];
        pos[1] = newPosition[1];
        pos[2] = newPosition[2];
        rot[0] = newRotation[0];
        rot[1] = newRotation[1];
        rot[2] = newRotation[2];
        physics = new Physics(pos, rot);
        actionList = new LinkedList<Action>();
    }
    public SceneEntity(float x, float y, float z, float alpha, float beta, float gamma){
        pos[0] = x;
        pos[1] = y;
        pos[2] = z;
        rot[0] = alpha;
        rot[1] = beta;
        rot[2] = gamma;
        physics = new Physics(pos, rot);
        actionList = new LinkedList<Action>();
    }

    protected void addAction(Action newAction){
        newAction.setPos(pos);
        newAction.setRot(rot);
        newAction.setPhysics(physics);
        newAction.init();
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

    public Physics returnPhysicsPtr(){
        return physics;
    }

    //Sets an object's position
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

    public void setPositionX(float posX){
        pos[0] = posX;
    }
    public void setPositionY(float posY){
        pos[1] = posY;
    }
    public void setPositionZ(float posZ){
        pos[2] = posZ;
    }

    public float[] returnPosition(){
        return pos;
    }

    //Sets an object's rotation
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
    public void setRotationX(float rotX){
        rot[0] = rotX;
    }
    public void setRotationY(float rotY){
        rot[1] = rotY;
    }
    public void setRotationZ(float rotZ){
        rot[2] = rotZ;
    }

    public float[] returnRotation(){
        return rot;
    }

    public void copy(Object o){
        if(o instanceof SceneEntity){
            SceneEntity e = (SceneEntity)o;
            pos[0] = e.pos[0];
            pos[1] = e.pos[1];
            pos[2] = e.pos[2];
            rot[0] = e.rot[0];
            rot[1] = e.rot[1];
            rot[2] = e.rot[2];
        }
    }
    public void copy(SceneEntity e){
        pos[0] = e.pos[0];
        pos[1] = e.pos[1];
        pos[2] = e.pos[2];
        rot[0] = e.rot[0];
        rot[1] = e.rot[1];
        rot[2] = e.rot[2];
    }
    public boolean equals(Object o){
        if(o instanceof SceneEntity){
            SceneEntity e = (SceneEntity)o;
            boolean isEquals = false;
            for(byte i = 0; i < 3; i++){
                isEquals&=(Math.abs(pos[i] - e.pos[i]) <= EPSILON);
                isEquals&=(Math.abs(rot[i] - e.rot[i]) <= EPSILON);
            }
            return isEquals;
        }
        else
            return false;
    }
    public boolean equals(SceneEntity e){
        boolean isEquals = false;
        for(byte i = 0; i < 3; i++){
            isEquals&=(Math.abs(pos[i] - e.pos[i]) <= EPSILON);
            isEquals&=(Math.abs(rot[i] - e.rot[i]) <= EPSILON);
        }
        return isEquals;

    }
}