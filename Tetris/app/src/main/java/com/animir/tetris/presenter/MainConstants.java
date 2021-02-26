package com.animir.tetris.presenter;

import android.view.View;

import com.animir.tetris.R;

/**
 * Create by Animir [2019.08.22]
 */
public interface MainConstants {

    interface Views{
        void showView(boolean gameOverFlag, int score);
        void showNextBlock(int[][] nextBlock, int colorCode);
    }

    interface Presenter{
        void onClicker(View view);

    }

    interface Cons{
        String PositionX = "X";
        String PositionY = "Y";
        String ColorCode = "ColorCode";
        String BlockState = "BlockState";
        String BlockArea = "BlockArea";


        // ColorCodes
        int ColorBG = 0;        // 검정색 반투명
        int ColorRED = 1;       // 빨간색
        int ColorBLUE = 2;      // 파란색
        int ColorPURPLE = 3;    // 보라색
        int ColorSKY = 4;       // 하늘색
        int ColorGREEN = 5;     // 녹색
        int ColorYELLOW = 6;    // 노란색
        int ColorOrange = 7;    // 주황색

        // ColorResources
        int ColorBG_Resource = R.color.colorBg;         // 검정색 반투명
        int ColorRED_Resource = R.color.colorRed;       // 빨간색
        int ColorBLUE_Resource = R.color.colorBlue;     // 파란색
        int ColorPURPLE_Resource = R.color.colorPurple; // 보라색
        int ColorSKY_Resource = R.color.colorSky;       // 하늘색
        int ColorGREEN_Resource = R.color.colorGreen;   // 녹색
        int ColorYELLOW_Resource = R.color.colorYellow; // 노란색
        int ColorOrange_Resource = R.color.colorOrange; // 주황색


        // BlockStates
        int BlockStatus_User = 0;   // 유져 블록
        int BlockStatus_Empty = 1;  // 빈 공간
        int BlockStatus_Block = 2;  // 블럭 공간


        int BlockAreaUser = 0;      // 유져 영역
        int BlockAearEmpty = 1;     // 빈 영역


        int RotateTypeLeft = 0;     // 왼쪽으로 회전
        int RotateTypeRight = 1;    // 오른쪽으로 회전

        int MoveDownFlag_Stay = 0;              // 대기
        int MoveDownFlag_Movement = 1;          // 이동 가능
        int MoveDownFlag_Impossibility = 2;     // 이동 불가능
        int MoveDownFlag_Exit = 3;              // 종료


        int ScoreTypeDown = 0;      // 점수 타입 : 다운할때
        int ScoreTypeBlock = 1;     // 점수 타입 : 블록 삭제할때
        int ScoreTypeNone = 2;      // 점수 타입 : 없음

        int ScoreDown = 1;          // 다운시 점수
        int ScoreBlock = 10;        // 블록 삭제 할대 점수



        int PositionXValue = 0;
        int PositionYValue = 0;



        // DB Key
        String PREF_BEST_SCORE = "best_score";


    }

    interface Blocks{

        /**
         * 하늘색 I 자 블록 <br>
         * Code 4
         */
        int[][] Block_I =   {
                            {4, 0, 0, 0},
                            {4, 0, 0, 0},
                            {4, 0, 0, 0},
                            {4, 0, 0, 0}
                            };

        /**
         * 파란색 J 자 블록 <br>
         * Code 2
         */
        int[][] Block_J =   {
                            {0, 2, 0, 0},
                            {0, 2, 0, 0},
                            {2, 2, 0, 0},
                            {0, 0, 0, 0}
                            };

        /**
         * 주황색 L 자 블록 <br>
         * Code 7
         */
        int[][] Block_L =   {
                            {7, 0, 0, 0},
                            {7, 0, 0, 0},
                            {7, 7, 0, 0},
                            {0, 0, 0, 0}
                            };

        /**
         * 노란색 O 자 블록 <br>
         * Code 6
         */
        int[][] Block_O =   {
                            {6, 6, 0, 0},
                            {6, 6, 0, 0},
                            {0, 0, 0, 0},
                            {0, 0, 0, 0}
                            };

        /**
         * 녹색 S 자 블록 <br>
         * Code 5
         */
        int[][] Block_S =   {
                            {0, 5, 5, 0},
                            {5, 5, 0, 0},
                            {0, 0, 0, 0},
                            {0, 0, 0, 0}
                            };

        /**
         * 보라색 T 자 블록 <br>
         * Code 3
         */
        int[][] Block_T =   {
                            {0, 3, 0, 0},
                            {3, 3, 3, 0},
                            {0, 0, 0, 0},
                            {0, 0, 0, 0}
                            };

        /**
         * 빨간색 Z 자 블록 <br>
         * Code 1
         */
        int[][] Block_Z =   {
                            {1, 1, 0, 0},
                            {0, 1, 1, 0},
                            {0, 0, 0, 0},
                            {0, 0, 0, 0}
                            };



    }


}
