package Maths.Extensions;
import java.util.*;
public class ListFunctions{
  //Converts an array to a linked list
  public static LinkedList<Double> arrayToLinkedList(double[] arr){
    LinkedList<Double> list = new LinkedList<Double>();
    for(int i = 0; i < arr.length; i++)
      list.add(arr[i]);
    return list;
  }
  public static LinkedList<Float> arrayToLinkedList(float[] arr){
    LinkedList<Float> list = new LinkedList<Float>();
    for(int i = 0; i < arr.length; i++)
      list.add(arr[i]);
    return list;
  }
  public static LinkedList<Integer> arrayToLinkedList(int[] arr){
    LinkedList<Integer> list = new LinkedList<Integer>();
    for(int i = 0; i < arr.length; i++)
      list.add(arr[i]);
    return list;
  }
  public static LinkedList<Byte> arrayToLinkedList(byte[] arr){
    LinkedList<Byte> list = new LinkedList<Byte>();
    for(int i = 0; i < arr.length; i++)
      list.add(arr[i]);
    return list;
  }

  //Performs a binary search and returns true if the integer that the function is looking for is in the list
  public static boolean binarySearch(double[] numList, double num, int low, int high){
    if(numList.length <= 0 || high-low <= 0)
      return false;
    while(low <= high){
      int mid = low + ((high-low) >>> 1);
      if(numList[mid] == num)
        return true;
      if(numList[mid] < num)
        low = mid+1;
      else
        high = mid-1;
    }
    return false;
  }
  public static boolean binarySearch(float[] numList, double num, int low, int high){
    if(numList.length <= 0 || high-low <= 0)
      return false;
    while(low <= high){
      int mid = low + ((high-low) >>> 1);
      if(numList[mid] == num)
        return true;
      if(numList[mid] < num)
        low = mid+1;
      else
        high = mid-1;
    }
    return false;
  }
  public static boolean binarySearch(int[] numList, int num, int low, int high){
    if(numList.length <= 0 || high-low <= 0)
      return false;
    while(low <= high){
      int mid = low + ((high-low) >>> 1);
      if(numList[mid] == num)
        return true;
      if(numList[mid] < num)
        low = mid+1;
      else
        high = mid-1;
    }
    return false;
  }
  public static boolean binarySearch(byte[] numList, byte num, int low, int high){
    if(numList.length <= 0 || high-low <= 0)
      return false;
    while(low <= high){
      int mid = low + ((high-low) >>> 1);
      if(numList[mid] == num)
        return true;
      if(numList[mid] < num)
        low = mid+1;
      else
        high = mid-1;
    }
    return false;
  }
  
  //Performs a binary search on a list and returns the index at which the number of interest is located
  public static int binarySearchIndex(double[] numList, double num, int low, int high){
    if(numList.length <= 0 || high-low <= 0)
      return -1;
    while(low <= high){
      int mid = low + ((high-low) >>> 1);
      if(numList[mid] == num)
        return mid;
      if(numList[mid] < num)
        low = mid+1;
      else
        high = mid-1;
    }
    return -1;
  }
  public static int binarySearchIndex(float[] numList, float num, int low, int high){
    if(numList.length <= 0 || high-low <= 0)
      return -1;
    while(low <= high){
      int mid = low + ((high-low) >>> 1);
      if(numList[mid] == num)
        return mid;
      if(numList[mid] < num)
        low = mid+1;
      else
        high = mid-1;
    }
    return -1;
  }
  public static int binarySearchIndex(int[] numList, int num, int low, int high){
    if(numList.length <= 0 || high-low <= 0)
      return -1;
    while(low <= high){
      int mid = low + ((high-low) >>> 1);
      if(numList[mid] == num)
        return mid;
      if(numList[mid] < num)
        low = mid+1;
      else
        high = mid-1;
    }
    return -1;
  }
  public static int binarySearchIndex(byte[] numList, byte num, int low, int high){
    if(numList.length <= 0 || high-low <= 0)
      return -1;
    while(low <= high){
      int mid = low + ((high-low) >>> 1);
      if(numList[mid] == num)
        return mid;
      if(numList[mid] < num)
        low = mid+1;
      else
        high = mid-1;
    }
    return -1;
  }
  
  
  //Performs a merge sort
  public static void mergeSort(double[] arr, int l, int r){
    if(l < r){
      int mid = l +((r-l) >>> 1);
      mergeSort(arr, l, mid);
      mergeSort(arr, mid+1, r);
      merge(arr, l, r, mid);
    }
  }
  public static void mergeSort(float[] arr, int l, int r){
    if(l < r){
      int mid = l +((r-l) >>> 1);
      mergeSort(arr, l, mid);
      mergeSort(arr, mid+1, r);
      merge(arr, l, r, mid);
    }
  }
  public static void mergeSort(int[] arr, int l, int r){
    if(l < r){
      int mid = l +((r-l) >>> 1);
      mergeSort(arr, l, mid);
      mergeSort(arr, mid+1, r);
      merge(arr, l, r, mid);
    }
  }
  public static void mergeSort(byte[] arr, int l, int r){
    if(l < r){
      int mid = l +((r-l) >>> 1);
      mergeSort(arr, l, mid);
      mergeSort(arr, mid+1, r);
      merge(arr, l, r, mid);
    }
  }
  
  
  public static <E> int findInList(E[] list, E item){
    for(int i = 0; i < list.length; i++)
      if(item.equals(list[i]))
        return i;
    return -1;
  }
  
  //Takes in an array, splits it into two pieces, and them merges them back together with the elements in ascending order
  private static void merge(double[] arr, int l, int r, int mid){
    //Finding the sizes of the left and right halfs and filling out new lists
    int leftSize = mid-l+1;
    int rightSize = r-mid;
    double[] leftSide = new double[leftSize];
    double[] rightSide = new double[rightSize];
    for(int i = 0; i < leftSize; i++)
      leftSide[i] = arr[i+l];
    for(int i = 0; i < rightSize; i++)
      rightSide[i] = arr[i+mid+1];
     
    //Merging the two arrays together
    int i = 0;//leftSide index
    int j = 0;//rightSide index
    int k = l;//arr index
    //Filling out the list whilst there are still unconsidered elements in both leftSide and rightSide
    while(i < leftSize && j < rightSize){
      if(leftSide[i] <= rightSide[j]){
        arr[k] = leftSide[i];
        i++;
      }
      else{
        arr[k] = rightSide[j];
        j++;
      }
      k++;
    }
    //Filling out the list with left over elements from leftSide
    while(i < leftSize){
      arr[k] = leftSide[i];
      i++;
      k++;
    }
    //Filling out the list with left over elements from rightSide
    while(j < rightSize){
      arr[k] = rightSide[j];
      j++;
      k++;
    }
  }
  
  private static void merge(float[] arr, int l, int r, int mid){
    //Finding the sizes of the left and right halfs and filling out new lists
    int leftSize = mid-l+1;
    int rightSize = r-mid;
    float[] leftSide = new float[leftSize];
    float[] rightSide = new float[rightSize];
    for(int i = 0; i < leftSize; i++)
      leftSide[i] = arr[i+l];
    for(int i = 0; i < rightSize; i++)
      rightSide[i] = arr[i+mid+1];
     
    //Merging the two arrays together
    int i = 0;//leftSide index
    int j = 0;//rightSide index
    int k = l;//arr index
    //Filling out the list whilst there are still unconsidered elements in both leftSide and rightSide
    while(i < leftSize && j < rightSize){
      if(leftSide[i] <= rightSide[j]){
        arr[k] = leftSide[i];
        i++;
      }
      else{
        arr[k] = rightSide[j];
        j++;
      }
      k++;
    }
    //Filling out the list with left over elements from leftSide
    while(i < leftSize){
      arr[k] = leftSide[i];
      i++;
      k++;
    }
    //Filling out the list with left over elements from rightSide
    while(j < rightSize){
      arr[k] = rightSide[j];
      j++;
      k++;
    }
  }
  private static void merge(int[] arr, int l, int r, int mid){
    //Finding the sizes of the left and right halfs and filling out new lists
    int leftSize = mid-l+1;
    int rightSize = r-mid;
    int[] leftSide = new int[leftSize];
    int[] rightSide = new int[rightSize];
    for(int i = 0; i < leftSize; i++)
      leftSide[i] = arr[i+l];
    for(int i = 0; i < rightSize; i++)
      rightSide[i] = arr[i+mid+1];
     
    //Merging the two arrays together
    int i = 0;//leftSide index
    int j = 0;//rightSide index
    int k = l;//arr index
    //Filling out the list whilst there are still unconsidered elements in both leftSide and rightSide
    while(i < leftSize && j < rightSize){
      if(leftSide[i] <= rightSide[j]){
        arr[k] = leftSide[i];
        i++;
      }
      else{
        arr[k] = rightSide[j];
        j++;
      }
      k++;
    }
    //Filling out the list with left over elements from leftSide
    while(i < leftSize){
      arr[k] = leftSide[i];
      i++;
      k++;
    }
    //Filling out the list with left over elements from rightSide
    while(j < rightSize){
      arr[k] = rightSide[j];
      j++;
      k++;
    }
  }
  private static void merge(byte[] arr, int l, int r, int mid){
    //Finding the sizes of the left and right halfs and filling out new lists
    int leftSize = mid-l+1;
    int rightSize = r-mid;
    byte[] leftSide = new byte[leftSize];
    byte[] rightSide = new byte[rightSize];
    for(int i = 0; i < leftSize; i++)
      leftSide[i] = arr[i+l];
    for(int i = 0; i < rightSize; i++)
      rightSide[i] = arr[i+mid+1];
     
    //Merging the two arrays together
    int i = 0;//leftSide index
    int j = 0;//rightSide index
    int k = l;//arr index
    //Filling out the list whilst there are still unconsidered elements in both leftSide and rightSide
    while(i < leftSize && j < rightSize){
      if(leftSide[i] <= rightSide[j]){
        arr[k] = leftSide[i];
        i++;
      }
      else{
        arr[k] = rightSide[j];
        j++;
      }
      k++;
    }
    //Filling out the list with left over elements from leftSide
    while(i < leftSize){
      arr[k] = leftSide[i];
      i++;
      k++;
    }
    //Filling out the list with left over elements from rightSide
    while(j < rightSize){
      arr[k] = rightSide[j];
      j++;
      k++;
    }
  }
  
  //Reverses the order of a 1D array
  public static void reverseArrayOrder(double[] array){
    for(int i = 0; i < (array.length >>> 1); i++){
      double temp = array[array.length-i-1];
      array[array.length-i-1] = array[i];
      array[i] = temp;
    }
  }
  public static void reverseArrayOrder(float[] array){
    for(int i = 0; i < (array.length >>> 1); i++){
      float temp = array[array.length-i-1];
      array[array.length-i-1] = array[i];
      array[i] = temp;
    }
  }
  public static void reverseArrayOrder(int[] array){
    for(int i = 0; i < (array.length >>> 1); i++){
      int temp = array[array.length-i-1];
      array[array.length-i-1] = array[i];
      array[i] = temp;
    }
  }
  public static void reverseArrayOrder(byte[] array){
    for(int i = 0; i < (array.length >>> 1); i++){
      byte temp = array[array.length-i-1];
      array[array.length-i-1] = array[i];
      array[i] = temp;
    }
  }
  
  //Reverses the order of the rows in a 2D array
  public static void reverseRowOrder(double[][] array){
    for(int i = 0; i < (array.length >>> 1); i++){
      for(int j = 0; j < array[i].length; j++){
        double temp = array[array.length-i-1][j];
        array[array.length-i-1][j] = array[i][j];
        array[i][j] = temp;
      }
    }
  }
  public static void reverseRowOrder(float[][] array){
    for(int i = 0; i < (array.length >>> 1); i++){
      for(int j = 0; j < array[i].length; j++){
        float temp = array[array.length-i-1][j];
        array[array.length-i-1][j] = array[i][j];
        array[i][j] = temp;
      }
    }
  }
  public static void reverseRowOrder(int[][] array){
    for(int i = 0; i < (array.length >>> 1); i++){
      for(int j = 0; j < array[i].length; j++){
        int temp = array[array.length-i-1][j];
        array[array.length-i-1][j] = array[i][j];
        array[i][j] = temp;
      }
    }
  }
  public static void reverseRowOrder(byte[][] array){
    for(int i = 0; i < (array.length >>> 1); i++){
      for(int j = 0; j < array[i].length; j++){
        byte temp = array[array.length-i-1][j];
        array[array.length-i-1][j] = array[i][j];
        array[i][j] = temp;
      }
    }
  }
  
  //Reverses order of the columns in a 2D array
  public static void reverseColumnOrder(double[][] array){
    for(int i = 0; i < array.length; i++){
      for(int j = 0; j < (array[i].length >>> 1); j++){
        double temp = array[i][array.length-j-1];
        array[i][array.length-j-1] = array[i][j];
        array[i][j] = temp;
      }
    }
  } 
  public static void reverseColumnOrder(float[][] array){
    for(int i = 0; i < array.length; i++){
      for(int j = 0; j < (array[i].length >>> 1); j++){
        float temp = array[i][array.length-j-1];
        array[i][array.length-j-1] = array[i][j];
        array[i][j] = temp;
      }
    }
  } 
  public static void reverseColumnOrder(int[][] array){
    for(int i = 0; i < array.length; i++){
      for(int j = 0; j < (array[i].length >>> 1); j++){
        int temp = array[i][array.length-j-1];
        array[i][array.length-j-1] = array[i][j];
        array[i][j] = temp;
      }
    }
  } 
  public static void reverseColumnOrder(byte[][] array){
    for(int i = 0; i < array.length; i++){
      for(int j = 0; j < (array[i].length >>> 1); j++){
        byte temp = array[i][array.length-j-1];
        array[i][array.length-j-1] = array[i][j];
        array[i][j] = temp;
      }
    }
  } 
}
