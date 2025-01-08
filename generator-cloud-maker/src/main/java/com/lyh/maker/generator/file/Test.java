package com.lyh.maker.generator.file;

import cn.hutool.core.io.FileUtil;

import java.nio.file.Paths;

public class Test {

    public static void main(String[] args) {

        int[] arr = new int[]{2,54,6,244,10,1,5};
//        insertSort(arr);
        quickSort(arr,0,arr[arr.length-1]);
        for (int i : arr) {
            System.out.println(i);
        }

    }

    private static void quickSort(int[] arr, int left, int right){

        if(left <right){
            int pivot = oneDivide(arr,left,right);
            quickSort(arr,left,pivot-1);
            quickSort(arr,pivot+1,right);
        }
//        if(left == right){
//            for (int i : arr) {
//                System.out.println(i);
//            }
//        }

    }

    private static int oneDivide(int[] arr, int left, int right) {
        int pivot = arr[left];
        while(left < right){
            while(pivot<arr[right] && right>left){
                right--;
            }
            if(right >left){
                arr[left] = arr[right];
                left++;
            }
            while(pivot > arr[left] & right >left){
                left++;
            }
            if(right > left){
                arr[right] = arr[left];
                right--;
            }
        }

        arr[left] = pivot;
        return left==right?left:-1;
    }

    private static void insertSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int temp;
            for(int j = i-1; j>=0; j--){
                if(arr[j] > arr[j+1]){
                    temp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = temp;
                }
            }
        }
        for (int i : arr) {
            System.out.println(i);
        }
    }
}
