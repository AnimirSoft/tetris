package com.animir.tetris.presenter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Create by Animir [2019.08.22]
 */
public class MainPresenterFunctions {

    // TODO : 완성후 Presenter 분리 예정

    public MainPresenterFunctions(){

    }


//    private HashMap<String, Integer> getGameHashMap(int positionX, int positionY, int color, int blockState, int blockArea){
//        HashMap<String, Integer> map = new HashMap<>();
//        map.put(MainConstants.Cons.PositionX, positionX);     // X 좌표
//        map.put(MainConstants.Cons.PositionY, positionY);     // Y 좌표
//        map.put(MainConstants.Cons.ColorCode, color);         // 컬러
//        map.put(MainConstants.Cons.BlockState, blockState);   // 블록 초기화
//        map.put(MainConstants.Cons.BlockArea, blockArea);     // 유져 영역
//        return map;
//    }
//
//    private int[][] rotate90Left(int[][] m) {
//        int columns = m.length;
//        int rows = m[0].length;
//        int[][] tmpMtx = new int[rows][columns];
//
//        for (int j = 0; j < columns; j++) {
//            for (int i = 0, k = rows - 1; i < rows; i++, k--) {
//                tmpMtx[i][j] = m[j][k];
//            }
//        }
//        return tmpMtx;
//    }
//
//    private Object[][] rotate90Left(Object[][] m) {
//        int columns = m.length;
//        int rows = m[0].length;
//        Object[][] tmpMtx = new Object[rows][columns];
//
//        for (int j = 0; j < columns; j++) {
//            for (int i = 0, k = rows - 1; i < rows; i++, k--) {
//                tmpMtx[i][j] = m[j][k];
//            }
//        }
//        return tmpMtx;
//    }
//    private ArrayList<ArrayList<HashMap<String, Integer>>> rotate90LeftArrayList(ArrayList<ArrayList<HashMap<String, Integer>>> list) {
//        int columns = positionY;
//        int rows = positionX;
//        ArrayList<ArrayList<HashMap<String, Integer>>> tmpMtx = getDefaultLists();
//        for (int j = 0; j < columns; j++) {
//            for (int i = 0, k = rows - 1; i < rows; i++, k--) {
//                tmpMtx.get(i).set(j, list.get(j).get(k));
//            }
//        }
//        return tmpMtx;
//    }
//
//    private static int[][] rotate90Right(int[][] m) {
//        int columns = m.length;
//        int rows = m[0].length;
//        int[][] tmpMtx = new int[rows][columns];
//
//        for (int j = 0; j < columns; j++) {
//            for (int i = 0; i < rows; i++) {
//                tmpMtx[i][columns - j - 1] = m[j][i];
//            }
//        }
//        return tmpMtx;
//    }
//    private static Object[][] rotate90Right(Object[][] m) {
//        int columns = m.length;
//        int rows = m[0].length;
//        Object[][] tmpMtx = new Object[rows][columns];
//
//        for (int j = 0; j < columns; j++) {
//            for (int i = 0; i < rows; i++) {
//                tmpMtx[i][columns - j - 1] = m[j][i];
//            }
//        }
//        return tmpMtx;
//    }
//    private ArrayList<ArrayList<HashMap<String, Integer>>> rotate90RightArrayList(ArrayList<ArrayList<HashMap<String, Integer>>> list) {
//        int columns = positionY;
//        int rows = positionX;
//        ArrayList<ArrayList<HashMap<String, Integer>>> tmpMtx = getDefaultLists();
//
//        for (int j = 0; j < columns; j++) {
//            for (int i = 0; i < rows; i++) {
//                tmpMtx.get(i).set((columns - j - 1), list.get(j).get(i));
//            }
//        }
//        return tmpMtx;
//    }
//    private ArrayList getDefaultLists(){
//        ArrayList<ArrayList<HashMap<String, Integer>>> tmpList = new ArrayList<>();
//        for(int index1 = 0; index1 < positionY; index1++){
//            ArrayList<HashMap<String, Integer>> childMapList = new ArrayList<>();
//            for(int index2 = 0; index2 < positionX; index2++){
//                childMapList.add(getGameHashMap(index1, index2, MainConstants.Cons.ColorBG, MainConstants.Cons.BlockStatus_Empty, MainConstants.Cons.BlockAearEmpty));
//            }
//            tmpList.add(childMapList);
//        }
//        return tmpList;
//    }
}
