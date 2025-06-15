package Renderer.Objects.Parents;
import java.util.LinkedList;
import java.util.Objects;
import Renderer.Objects.Physics.*;
import Actions.ObjectActions.Action;
import Maths.LinearAlgebra.*;
import Renderer.ScreenDraw.MVP;
//Root superclass for all entities that define a scene (lights, cameras, etc.)
public class SceneEntity{
    //A list of parent objects that have already been transformed
    //The idea is to prevent infinite recursion in the event that one parent object links to another
    //in such a way that they form a loop
    private static LinkedList <SceneEntity> alreadyVisited = new LinkedList<SceneEntity>();
    protected final float EPSILON = 0.0001f; //For equality checking with floats
    protected byte flags = 0; //0 = always peform
    protected float[] pos = {0, 0, 0}; //Object's position
    protected float[] rot = {0, 0, 0}; //Object's rotation
    private Physics physics = new Physics(pos, rot); //Physics attached to object
    protected LinkedList<Action> actionList = new LinkedList<Action>(); //Actions associated with object
    private static Action tempAction; //A temporary action used when iterating over multiple actions
    private SceneEntity parent = null; //A parent transformation for this object
    protected Matrix modelMatrix = new Matrix();
    public SceneEntity(){
        pos[0] = 0;
        pos[1] = 0;
        pos[2] = 0;
        rot[0] = 0;
        rot[1] = 0;
        rot[2] = 0;
        physics = new Physics(pos, rot);
        actionList = new LinkedList<Action>();
        parent = null;
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
        parent = null;
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
        parent = null;
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
        parent = null;
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
        parent = null;
    }

    //Sets the reference to the parent transformation for this object
    public void setParentTransform(SceneEntity newParent){
        parent = newParent;
    }

    //Returns the reference to the parent transformation for this object
    public SceneEntity getParentTransformation(){
        return parent;
    }

    //Goes through each object's parents and multiplies their transformations together using
    private Matrix transformRecursive(SceneEntity newParent, boolean isBillboard, boolean beforeBillboard){
        //Create this object's transformation matrix
        Matrix transformMatrix;
        if(!isBillboard)
            transformMatrix = MatrixOperations.matrixMultiply(MVP.returnTranslation(newParent.returnPosition()), MVP.returnRotation(newParent.returnRotation()));
        else{
            if(beforeBillboard){
                transformMatrix = MVP.returnTranslation(newParent.returnPosition());
            }
            else{
                transformMatrix = MVP.returnRotation(0, 0, newParent.returnRotation()[2]);
            }
        }
        //Check if we've already seen this this object before. If we have, add it to a list, otherwise skip
        if(!alreadyVisited.contains(newParent)){
            alreadyVisited.add(newParent);
            //Multiply the current object's parent's transformations by its own if it has a parent. Otherwise, skip and
            //return this object's transformation matrix
            if(newParent.getParentTransformation() != null){
                return MatrixOperations.matrixMultiply(transformRecursive(newParent.getParentTransformation(), isBillboard, beforeBillboard), transformMatrix);
            }
            else
                return transformMatrix;
        }
        else
            return transformMatrix;
    }

    //Goes through each object's parents and multiplies their transformations together using
    private Matrix transformRecursive(SceneEntity newParent){
        //Create this object's transformation matrix
        Matrix transformMatrix = MatrixOperations.matrixMultiply(MVP.returnTranslation(newParent.returnPosition()), MVP.returnRotation(newParent.returnRotation()));

        //Check if we've already seen this this object before. If we have, add it to a list, otherwise skip
        if(!alreadyVisited.contains(newParent)){
            alreadyVisited.add(newParent);
            //Multiply the current object's parent's transformations by its own if it has a parent. Otherwise, skip and
            //return this object's transformation matrix
            if(newParent.getParentTransformation() != null){
                return MatrixOperations.matrixMultiply(transformRecursive(newParent.getParentTransformation()), transformMatrix);
            }
            else
                return transformMatrix;
        }
        else
            return transformMatrix;
    }
    
    //Returns a transformation matrix for the parent objects
    public Matrix transform(boolean isBillboard, boolean position){
        //Checks if the parent isn't null and returns an I4 matrix if it is
        if(parent != null && parent != this){
            //Clear the list of already visited parents and go through each parent's transformations
            alreadyVisited.clear();
            return transformRecursive(parent, isBillboard, position);
        }
        else
            return new Matrix(4, 4);
    }

    //Returns a transformation matrix for the parent objects
    public Matrix transform(){
        //Checks if the parent isn't null and returns an I4 matrix if it is
        if(parent != null && parent != this){
            //Clear the list of already visited parents and go through each parent's transformations
            alreadyVisited.clear();
            return transformRecursive(parent);
        }
        else
            return new Matrix(4, 4);
    }

    //Initializes a new action without adding it to the list
    protected void appendAction(Action newAction){
        newAction.setPos(pos);
        newAction.setRot(rot);
        newAction.setPhysics(physics);
        newAction.setMatrix(modelMatrix);
        newAction.init();
    }

    //Initializes a new action and adds it to the list
    protected void addAction(Action newAction){
        newAction.setPos(pos);
        newAction.setRot(rot);
        newAction.setPhysics(physics);
        newAction.setMatrix(modelMatrix);
        newAction.init();
        actionList.add(newAction);
    }



    //Deletes the action that is at the head of the list and returns it
    public Action removeFirstAction(){
        return actionList.removeFirst();
    }

    //Deletes the action that is at the tail of the list and returns it
    public Action removeLastAction(){
        return actionList.removeLast();
    }

    //Deletes an arbitary action from the list and returns it
    public Action removeAction(int i){
        return actionList.remove(i);
    }

    //Wipes the list of all actions
    public void clearActionList(){
        actionList.clear();
    }

    //Returns if the list has any actions
    public boolean hasActions(){
        return !actionList.isEmpty();
    }

    //Returns how many actions there are
    public int numOfActions(){
        return actionList.size();
    }

    //Iterates through the list of actions and runs them
    public void executeActions(){
        int length = actionList.size();
        for(int i = 0; i < length; i++){
          tempAction = actionList.removeFirst();
          actionList.add(tempAction);
          tempAction.perform();
        }
    }

    //Forces actions to always be performed
    public void alwaysPerform(boolean perform){
        if(perform)
            flags|=1;
        else
            flags&=-2;
    }
    public boolean alwaysPerform(){
        return (flags & 1) == 1;
    }

    //Returns the reference to this object's physics
    public Physics returnPhysicsPtr(){
        return physics;
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
        modelMatrix.copy(MatrixOperations.matrixMultiply(MVP.returnTranslation(pos), MVP.returnRotation(rot)));
    }

    //Returns the reference to the current model matrix
    public Matrix returnModelMatrix(){
        return modelMatrix;
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
            modelMatrix.copy(e.modelMatrix);
            pos[0] = e.pos[0];
            pos[1] = e.pos[1];
            pos[2] = e.pos[2];
            rot[0] = e.rot[0];
            rot[1] = e.rot[1];
            rot[2] = e.rot[2];
            flags = e.flags;
        }
    }
    public void copy(SceneEntity e){
        modelMatrix.copy(e.modelMatrix);
        pos[0] = e.pos[0];
        pos[1] = e.pos[1];
        pos[2] = e.pos[2];
        rot[0] = e.rot[0];
        rot[1] = e.rot[1];
        rot[2] = e.rot[2];
        flags = e.flags;
    }
    public boolean equals(Object o){
        if(o instanceof SceneEntity){
            SceneEntity e = (SceneEntity)o;
            boolean isEquals = modelMatrix.equals(e.modelMatrix);
            for(byte i = 0; i < 3; i++){
                isEquals&=(Math.abs(pos[i] - e.pos[i]) <= EPSILON);
                isEquals&=(Math.abs(rot[i] - e.rot[i]) <= EPSILON);
            }
            return isEquals && (flags == e.flags);
        }
        else
            return false;
    }
    public boolean equals(SceneEntity e){
        boolean isEquals = modelMatrix.equals(e.modelMatrix);
        for(byte i = 0; i < 3; i++){
            isEquals&=(Math.abs(pos[i] - e.pos[i]) <= EPSILON);
            isEquals&=(Math.abs(rot[i] - e.rot[i]) <= EPSILON);
        }
        return isEquals && (flags == e.flags);

    }
}