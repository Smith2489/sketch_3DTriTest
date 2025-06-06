package Renderer.Objects.Physics;
import Maths.LinearAlgebra.*;
public class Physics {
    private static byte flags = 0;//bit 0 = use realistic drag calculations
    //General object information
    private float[] pos = {0, 0, 0};
    private float[] rot = {0, 0, 0};
    private float crossSectionArea = 1; //The object's cross-sectional area
    private float mass = 1;

    //Drag
    public static float dragCoefficient = 1;
    public static float fluidDensity = 1; //Density of the fluid the object is currently in
    public static float fluidVelocity = 0; //Velocity of the fluid the object is currently in
    private float dragAcceleration = 0;

    //Gravity
    public static float gravityAcceleration = 0.2f;
    private float gravityVelocity = 0;
    private float[] gravityDirection = {0, 1, 0};
    public float terminalVelocity = Float.MAX_VALUE;

    public void useDragCalculations(){
        flags|=1;
    } 
    public void useSpeedCap(){
        flags&=-2;
        dragAcceleration = 0;
    }

    public Physics(float[] newPos, float[] newRot){
        pos = newPos;
        rot = newRot;
        mass = 1;
        crossSectionArea = 1;
        dragAcceleration = 0;
        gravityVelocity = 0;
        gravityDirection[0] = 0;
        gravityDirection[1] = 1;
        gravityDirection[2] = 0;
    }

    public void setMass(float newMass){
        if(newMass > 0.000001f)
            mass = newMass;
        else
            mass = 0.000001f;
    }

    public void setCrossSectionArea(float newArea){
        crossSectionArea = Math.max(0, newArea);
    }

    public void setGravityDirection(float[] newDirection){
        gravityDirection = VectorOperations.vectorNormalization3D(newDirection);
    }

    public void setGravityDirection(float xDir, float yDir, float zDir){
        gravityDirection[0] = xDir;
        gravityDirection[1] = yDir;
        gravityDirection[2] = zDir;
        gravityDirection = VectorOperations.vectorNormalization3D(gravityDirection);
    }
    public void applyGravity(){
        if((flags & 1) == 1)
            dragAcceleration = (0.5f*fluidDensity*(gravityVelocity-fluidVelocity)*(gravityVelocity-fluidVelocity)*dragCoefficient*crossSectionArea)/mass;
        else if(Math.abs(gravityVelocity) > Math.abs(terminalVelocity))
            gravityVelocity = terminalVelocity;
        //System.out.println(dragAcceleration);
        pos[0]+=(gravityDirection[0]*gravityVelocity);
        pos[1]+=(gravityDirection[1]*gravityVelocity);
        pos[2]+=(gravityDirection[2]*gravityVelocity);
        gravityVelocity+=(gravityAcceleration-dragAcceleration);

    }
    public void setGravityVelocity(){
        gravityVelocity = 0;
    }
    public void setGravityVelocity(float newVelocity){
        gravityVelocity = newVelocity;
    }
}
