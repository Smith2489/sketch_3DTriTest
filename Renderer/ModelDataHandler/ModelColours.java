package Renderer.ModelDataHandler;
import Maths.Extensions.*;
public class ModelColours {
    private int polygonCount = 12;
    private int vertexCount = 8;
    private float[][] vertexColours = new float[8][3];
    private int[][] colours = new int[12][2]; //0 = stroke, 1 = fill (front face)
    private int numberOfVisibleBacks = 0;
    private int[] backVisible = new int[0]; //Triangles with visible back faces   <----+
    private int[][] backColour = new int[0][2]; //0 = stroke, 1 = fill (back face)     |--- the "list"
    private int[] ends = {-1, -1}; //0 = first index in list; 1 = last index in list --+
    private boolean completelyBlack = true;

    public ModelColours(){
        polygonCount = 12;
        vertexCount = 8;
        vertexColours = new float[8][3];
        for(byte i = 0; i < 8; i++){
            vertexColours[i][0] = 1;
            vertexColours[i][1] = 1;
            vertexColours[i][2] = 1;
        }
        colours = new int[12][2];
        numberOfVisibleBacks = 0;
        backVisible = new int[0];
        backColour = new int[0][2];
        ends[0] = -1;
        ends[1] = -1;
        for(int i = 0; i < 12; i++){
            colours[i][0] = 0xFF000000;
            colours[i][1] = 0xFF000000 | ((i&1)*-1);
            if((colours[i][1] & 0xFFFFFF) == 0)
                colours[i][1] = 0xFF0F0F0F;
        }
        completelyBlack = false;
    }
    public ModelColours(int[][] colourSet, float[][] vertexColourSet, int polygonNum, int vertexNum){
        polygonCount = polygonNum;
        colours = new int[polygonCount][2];
        vertexCount = vertexNum;
        vertexColours = new float[vertexCount][3];
        for(int i = 0; i < vertexCount; i++){
            if(i < vertexColourSet.length){
                vertexColours[i][0] = Math.max(0, Math.min(vertexColourSet[i][0], 1));
                vertexColours[i][1] = Math.max(0, Math.min(vertexColourSet[i][1], 1));
                vertexColours[i][2] = Math.max(0, Math.min(vertexColourSet[i][2], 1));
            }
            else{
                vertexColours[i][0] = 1;
                vertexColours[i][1] = 1;
                vertexColours[i][2] = 1;
            }

        }
        numberOfVisibleBacks = 0;
        backVisible = new int[0];
        backColour = new int[0][2];
        ends[0] = -1;
        ends[1] = -1;
        for(int i = 0; i < polygonCount; i++){
            colours[i][0] = colourSet[i%colourSet.length][0];
            if((colours[i][0] >>> 24) == 0){
                if(colours[i][0] <= 0xFF)
                    colours[i][0] = 0xFF000000 | (colours[i][0] << 16) | (colours[i][0] << 8) | colours[i][0];
                else if(colours[i][0] <= 0xFFFF)
                    colours[i][0] = ((colours[i][0] & 0xFF00) << 16) | ((colours[i][0] & 0xFF) << 16) | ((colours[i][0] & 0xFF) << 8) | (colours[i][0] & 0xFF);
                else
                    colours[i][0] = 0xFF000000 | colours[i][0];
            }
            //System.out.println(colourSet[i%colourSet.length]);
            colours[i][1] = colourSet[i%colourSet.length][1];
            if((colours[i][1] >>> 24) == 0){
                if(colours[i][1] <= 0xFF)
                    colours[i][1] = 0xFF000000 | (colours[i][1] << 16) | (colours[i][1] << 8) | colours[i][1];
                else if(colours[i][1] <= 0xFFFF)
                    colours[i][1] = ((colours[i][1] & 0xFF00) << 16) | ((colours[i][1] & 0xFF) << 16) | ((colours[i][1] & 0xFF) << 8) | (colours[i][1] & 0xFF);
                else
                    colours[i][1] = 0xFF000000 | colours[i][1];

            }
            if((colours[i][1] & 0xFFFFFF) != 0)
                completelyBlack = false;
        }
    }
    //Sets the colour pallete of the model
    //WARNING: THIS WILL RESET THE BACKFACE COLOURS
    public void setColours(int[][] colourSet, int polygonNum){
        polygonCount = polygonNum;
        colours = new int[polygonCount][2];
        numberOfVisibleBacks = 0;
        backVisible = new int[0];
        backColour = new int[0][2];
        ends[0] = -1;
        ends[1] = -1;
        for(int i = 0; i < polygonCount; i++){
            colours[i][0] = colourSet[i%colourSet.length][0];
            if((colours[i][0] >>> 24) == 0){
                if(colours[i][0] <= 0xFF)
                    colours[i][0] = 0xFF000000 | (colours[i][0] << 16) | (colours[i][0] << 8) | colours[i][0];
                else if(colours[i][0] <= 0xFFFF)
                    colours[i][0] = ((colours[i][0] & 0xFF00) << 16) | ((colours[i][0] & 0xFF) << 16) | ((colours[i][0] & 0xFF) << 8) | (colours[i][0] & 0xFF);
                else
                    colours[i][0] = 0xFF000000 | colours[i][0];
            }
            colours[i][1] = colourSet[i%colourSet.length][1];
            if((colours[i][1] >>> 24) == 0){
                if(colours[i][1] <= 0xFF)
                    colours[i][1] = 0xFF000000 | (colours[i][1] << 16) | (colours[i][1] << 8) | colours[i][1];
                else if(colours[i][1] <= 0xFFFF)
                    colours[i][1] = ((colours[i][1] & 0xFF00) << 16) | ((colours[i][1] & 0xFF) << 16) | ((colours[i][1] & 0xFF) << 8) | (colours[i][1] & 0xFF);
                else
                    colours[i][1] = 0xFF000000 | colours[i][1];

            }
            if((colours[i][1] & 0xFFFFFF) != 0)
                completelyBlack = false;
        }
    }
    public int[][] returnColours(){
        return colours;
    }
    public void setVertexColours(float[][] vertexColourSet, int vertexNum){
        vertexCount = vertexNum;
        vertexColours = new float[vertexCount][3];
        for(int i = 0; i < vertexCount; i++){
            if(i < vertexColourSet.length){
                vertexColours[i][0] = Math.max(0, Math.min(vertexColourSet[i][0], 1));
                vertexColours[i][1] = Math.max(0, Math.min(vertexColourSet[i][1], 1));
                vertexColours[i][2] = Math.max(0, Math.min(vertexColourSet[i][2], 1));
            }
            else{
                vertexColours[i][0] = 1;
                vertexColours[i][1] = 1;
                vertexColours[i][2] = 1;
            }

        }
    }

    public float[][] returnVertexColours(){
        return vertexColours;
    }

    //Sets the front-face colour of one of the polygons
    public void setSingleFrontColour(int[] newColours, int index){
        if(index < polygonCount){
            colours[index][0] = newColours[0];
            if((colours[index][0] >>> 24) == 0){
                if(colours[index][0] <= 0xFF)
                    colours[index][0] = 0xFF000000 | (colours[index][0] << 16) | (colours[index][0] << 8) | colours[index][0];
                else if(colours[index][0] <= 0xFFFF)
                    colours[index][0] = ((colours[index][0] & 0xFF00) << 16) | ((colours[index][0] & 0xFF) << 16) | ((colours[index][0] & 0xFF) << 8) | (colours[index][0] & 0xFF);
                else
                    colours[index][0] = 0xFF000000 | colours[index][0];
            }
            colours[index][1] = newColours[1];
            if((colours[index][1] >>> 24) == 0){
                if(colours[index][1] <= 0xFF)
                    colours[index][1] = 0xFF000000 | (colours[index][1] << 16) | (colours[index][1] << 8) | colours[index][1];
                else if(colours[index][1] <= 0xFFFF)
                    colours[index][1] = ((colours[index][1] & 0xFF00) << 16) | ((colours[index][1] & 0xFF) << 16) | ((colours[index][1] & 0xFF) << 8) | (colours[index][1] & 0xFF);
                else
                    colours[index][1] = 0xFF000000 | colours[index][1];
            }
            if((colours[index][1] & 0xFFFFFF) != 0)
                completelyBlack = false;
        }
        else{
            System.out.println("ERROR: INDEX "+index+" IS OUT OF BOUNDS");
            System.exit(1);
        }
    }
    //Initializes the list of triangles that are visible regardless of if they are facing the camera or not
    public void initBackVisible(int size){
        backVisible = new int[size];
        numberOfVisibleBacks = 0;
        for(int i = 0; i < size; i++)
            backVisible[i] = -1;
    }
  
    //Initializes the list of back face colours
    public void initBackColours(int size){
        backColour = new int[size][2];
        for(int i = 0; i < size; i++){
            backColour[i][0] = -1;
            backColour[i][1] = -1; 
        }
    }
  
    //Fills in the list of back face colours
    public void setBackColour(int index, int stroke, int fill){
        if(index >= 0 && index < backColour.length){
            if((stroke >>> 24) == 0){
                if(stroke <= 0xFF)
                    stroke = 0xFF000000 | (stroke << 16) | (stroke << 8) | stroke;
                else if(stroke <= 0xFFFF)
                    stroke = ((stroke & 0xFF00) << 16) | ((stroke & 0xFF) << 16) | ((stroke & 0xFF) << 8) | (stroke & 0xFF);
                else
                    stroke = 0xFF000000 | stroke;
            }
            backColour[index][0] = stroke;
            if((fill >>> 24) == 0){
                if(fill <= 0xFF)
                    fill = 0xFF000000 | (fill << 16) | (fill << 8) | fill;
                else if(fill <= 0xFFFF)
                    fill = ((fill & 0xFF00) << 16) | ((fill & 0xFF) << 16) | ((fill & 0xFF) << 8) | (fill & 0xFF);
                else
                    fill = 0xFF000000 | fill;

            }
            if((fill & 0xFFFFFF) != 0)
                completelyBlack = false;
            backColour[index][1] = fill;
            return;
        }
        System.out.println("ERROR: INDEX OUT OF BOUNDS");
        System.exit(1);
    }

    //Places a triangle into the list
    public void setBackVisible(int index){
        if(backVisible.length > 0){
            if(numberOfVisibleBacks < backVisible.length){
                //Places the first triangle in to the list
                if(numberOfVisibleBacks == 0){
                    ends[0] = index;
                    ends[1] = index;
                    backVisible[0] = index;
                    numberOfVisibleBacks++;
                }
                //Places in a new triangle at the end if it is greater than current maximum
                else if(index > ends[1]){
                    backVisible[numberOfVisibleBacks] = index;
                    numberOfVisibleBacks++;
                    ends[1] = index;
                }
                //Places a new triangle at the start if it is less than the minimum
                else if(index < ends[0]){
                    for(int i = numberOfVisibleBacks-1; i >= 0; i--)
                        backVisible[i+1] = backVisible[i];
                    backVisible[0] = index;
                    numberOfVisibleBacks++;
                    ends[0] = index;
                }
                //Places a new triangle into the list and sorts it if it is between the maximum and minimum
                else{
                    if(!ListFunctions.binarySearch(backVisible, index, 0, numberOfVisibleBacks)){
                        //Placing the triangle into the list
                        backVisible[numberOfVisibleBacks] = index;
                        numberOfVisibleBacks++;
                        //Constructing a temporary array for sorting
                        int[] indices = new int[numberOfVisibleBacks];
                        for(int i = 0; i < numberOfVisibleBacks; i++)
                            indices[i] = backVisible[i];
                        ListFunctions.mergeSort(indices, 0, indices.length-1);     
                        //Copying the temporary array's elements back into the main array
                        for(int i = 0; i < numberOfVisibleBacks; i++)
                            backVisible[i] = indices[i];
                        ends[0] = backVisible[0];
                        ends[1] = backVisible[numberOfVisibleBacks-1];
                    }
                }
                return;
            }
            System.out.println("ERROR: "+index+" OUT OF BOUNDS");
            System.exit(1);
        }
        System.out.println("ERROR: LIST OF POLYGONS WITH VISIBLE BACKS NOT INITIALIZED");
        System.exit(1);
    }

    //Returns if a triangle is visible when facing away from the camera or not
    public boolean returnBackVisible(int index){
        if(numberOfVisibleBacks == 0 || index <= -1 || index >= backVisible.length)
            return false;
        if(numberOfVisibleBacks == polygonCount)
            return true;
        return ListFunctions.binarySearch(backVisible, index, 0, numberOfVisibleBacks);
    }
    public int returnBackTri(int index){
        if(numberOfVisibleBacks == 0 || index <= -1 || index >= backVisible.length)
            return -1;
        if(numberOfVisibleBacks == polygonCount)
            return index;
        return ListFunctions.binarySearchIndex(backVisible, index, 0, numberOfVisibleBacks);
    }
  
    //Returns the length of the list of back face colours
    public int returnBackColourCount(){
        return backColour.length;
    }
  
    public boolean isCompletelyBlack(){
        return completelyBlack;
    }
    //Returns the stroke (sub=0) or fill (sub=1) of the triangle of interest
    public int returnBackColour(int index, byte sub){
        if(sub >= 0 && sub <= 1 && index >= 0 && index < backColour.length)
            return backColour[index][sub];
        System.out.println("ERROR: INDEXING OUT OF RANGE");
        return -1;
    }
    public int returnPolygonCount(){
        return polygonCount;
    }

    public boolean equals(Object o){
        if(o instanceof ModelColours){
            ModelColours mC = (ModelColours)o;
            if(colours.length != mC.colours.length || backColour.length != mC.backColour.length || backVisible.length != backVisible.length)
                return false;
            boolean isEquals = true;
            isEquals&=(polygonCount == mC.polygonCount);
            isEquals&=(numberOfVisibleBacks == mC.numberOfVisibleBacks);
            isEquals&=(ends[0] == mC.ends[0]);
            isEquals&=(ends[1] == mC.ends[1]);
            for(int i = 0; i < colours.length; i++){
                isEquals&=(colours[i][0] == mC.colours[i][0]);
                isEquals&=(colours[i][1] == mC.colours[i][1]);
            }
            for(int i = 0; i < backColour.length; i++){
                isEquals&=(backColour[i][0] == mC.backColour[i][0]);
                isEquals&=(backColour[i][1] == mC.backColour[i][1]);
            }
            for(int i = 0; i < backVisible.length; i++)
                isEquals&=(backVisible[i] == mC.backVisible[i]);
            isEquals&=(completelyBlack == mC.completelyBlack);
            return isEquals;
        }
        else
            return false;
    }

    public boolean equals(ModelColours mC){
        if(colours.length != mC.colours.length || backColour.length != mC.backColour.length || backVisible.length != backVisible.length)
            return false;
        boolean isEquals = true;
        isEquals&=(polygonCount == mC.polygonCount);
        isEquals&=(numberOfVisibleBacks == mC.numberOfVisibleBacks);
        isEquals&=(ends[0] == mC.ends[0]);
        isEquals&=(ends[1] == mC.ends[1]);
        for(int i = 0; i < colours.length; i++){
            isEquals&=(colours[i][0] == mC.colours[i][0]);
            isEquals&=(colours[i][1] == mC.colours[i][1]);
        }
        for(int i = 0; i < backColour.length; i++){
            isEquals&=(backColour[i][0] == mC.backColour[i][0]);
            isEquals&=(backColour[i][1] == mC.backColour[i][1]);
        }
        for(int i = 0; i < backVisible.length; i++)
            isEquals&=(backVisible[i] == mC.backVisible[i]);
        isEquals&=(completelyBlack == mC.completelyBlack);
        return isEquals;
    }
    public void copy(Object o){
        if(o instanceof ModelColours){
            ModelColours mC = (ModelColours)o;
            polygonCount = mC.polygonCount;
            numberOfVisibleBacks = mC.numberOfVisibleBacks;
            ends[0] = mC.ends[0];
            ends[1] = mC.ends[1];
            colours = new int[mC.colours.length][2];
            backColour = new int[mC.backColour.length][2];
            backVisible = new int[mC.backVisible.length];
            for(int i = 0; i < colours.length; i++){
                colours[i][0] = mC.colours[i][0];
                colours[i][1] = mC.colours[i][1];
            }
            for(int i = 0; i < backColour.length; i++){
                backColour[i][0] = mC.backColour[i][0];
                backColour[i][1] = mC.backColour[i][1];
            }
            for(int i = 0; i < backVisible.length; i++)
                backVisible[i] = mC.backVisible[i];
            completelyBlack = mC.completelyBlack;
        }
    }

    public void copy(ModelColours mC){
        polygonCount = mC.polygonCount;
        numberOfVisibleBacks = mC.numberOfVisibleBacks;
        ends[0] = mC.ends[0];
        ends[1] = mC.ends[1];
        colours = new int[mC.colours.length][2];
        backColour = new int[mC.backColour.length][2];
        backVisible = new int[mC.backVisible.length];
        for(int i = 0; i < colours.length; i++){
            colours[i][0] = mC.colours[i][0];
            colours[i][1] = mC.colours[i][1];
        }
        for(int i = 0; i < backColour.length; i++){
            backColour[i][0] = mC.backColour[i][0];
            backColour[i][1] = mC.backColour[i][1];
        }
        for(int i = 0; i < backVisible.length; i++)
            backVisible[i] = mC.backVisible[i];
        completelyBlack = mC.completelyBlack;
    }
}
