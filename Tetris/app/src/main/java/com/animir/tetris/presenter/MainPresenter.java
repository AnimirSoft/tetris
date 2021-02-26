package com.animir.tetris.presenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.animir.tetris.R;
import com.animir.tetris.model.MainModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Create by Animir [2019.08.22]
 */
public class MainPresenter implements MainConstants.Presenter {
    private boolean gameOverFlag = false;
    private boolean mGameStartFlag = false;

    private boolean btnDownFlag = true;
    private boolean btnFlag = true;

    private MainConstants.Views mMainActivityViews = null;
    private MainModel mainModel = null;

    private Context mContext = null;
    private LinearLayout gameMainLayout = null;

    private int[][] gameUserBlock = null;
    private int[][] gameNextUserBlock = null;

    //private long handlerStartTime = 0;

    private int positionX = 15;
    private int positionY = 30;

    private int score = 0;
    private int mGameSpeed = 300;

    private ArrayList<ArrayList<HashMap<String, Integer>>> gameBoard = null;
    private ArrayList<ArrayList<HashMap<String, Integer>>> stopBlockTmp = null;
    private ArrayList<ArrayList<HashMap<String, Integer>>> userBlockTmp = null;

    // TODO : 게임이 진행하지 않는 버그 발생됨됨 (블록 삭제시, 내리기 버튼 연타시)
    // TODO : Block 여러개 삭제시 진행 불가 버그
    // TODO : 중간 Block 삭제 되지 않는 현상

    public void setGameLayout(Context context, MainConstants.Views views, LinearLayout mainLayout){
        mContext = context;
        mMainActivityViews = views;
        gameMainLayout = mainLayout;
        mainModel = MainModel.getInstance();
    }

    public void init(){
        mGameStartFlag = false;

        score = 0;

        mMainActivityViews.showView(false, score);

        if(gameBoard != null){
            gameBoard.clear();
            gameBoard = null;
        }
        gameBoard = new ArrayList<>();

        gameUserBlock = new int[4][4];

        gameMainLayout.removeAllViews();
        for(int index1 = 0; index1 < positionY; index1++){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            params.setMargins(1,1,1,1);
            LinearLayout childLayout = new LinearLayout(mContext);
            childLayout.setLayoutParams(params);

            ArrayList<HashMap<String, Integer>> childMapList = new ArrayList<>();
            for(int index2 = 0; index2 < positionX; index2++){
                TextView textView = new TextView(mContext);
                textView.setBackgroundColor(mContext.getResources().getColor(R.color.colorBg));
                textView.setLayoutParams(params);
                textView.setTag(index2+","+index1);
                childLayout.addView(textView);

                childMapList.add(getGameHashMap(index1, index2, MainConstants.Cons.ColorBG, MainConstants.Cons.BlockStatus_Empty, MainConstants.Cons.BlockAearEmpty));
            }
            gameBoard.add(childMapList);
            gameMainLayout.addView(childLayout);
        }
    }

    private void gameReSet(){
        for(int i = 0; i < positionY; i++){
            for(int i2 = 0; i2 < positionX; i2++) {
                gameBoard.get(i).get(i2).put(MainConstants.Cons.BlockState, MainConstants.Cons.BlockStatus_Empty);
                gameBoard.get(i).get(i2).put(MainConstants.Cons.ColorCode, MainConstants.Cons.ColorBG);
                gameBoard.get(i).get(i2).put(MainConstants.Cons.BlockArea, MainConstants.Cons.BlockAearEmpty);
            }
        }
        setGameDisplay();
    }

    private void gameStart(){

        btnFlag = false;

        gameReSet();

        // 랜덤 블럭 가져오기

        gameUserBlock = getRandomBlock();

        // 유저 블록 시작 생성 위치
        startUserBlock();

        // 게임화면 그리기
        setGameDisplay();

        // 게임 시작
        startTimer(MainConstants.Cons.MoveDownFlag_Movement, mGameSpeed);
    }

    @SuppressLint("HandlerLeak")
    private Handler gameTimeHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {

            gameTimeHandler.removeMessages(0);

            switch (msg.what){
                case MainConstants.Cons.MoveDownFlag_Movement:

                    if(gameOverFlag){
                        Toast.makeText(mContext, "GameOver!!!!!",Toast.LENGTH_LONG).show();
                        mMainActivityViews.showView(true, score);
                        startTimer(MainConstants.Cons.MoveDownFlag_Exit, mGameSpeed);
                        break;
                    }

                    int moveDownFlag = moveDown(gameBoard);
                    Log.d("#@#", "moveDownFlag : " + moveDownFlag + " btnFlag : " + btnFlag);
                    if(moveDownFlag == MainConstants.Cons.MoveDownFlag_Movement){
                        setGameDisplay();
                        btnFlag = true;
                        if(!gameOverFlag)
                            startTimer(MainConstants.Cons.MoveDownFlag_Movement, mGameSpeed);
                    }else if(moveDownFlag == MainConstants.Cons.MoveDownFlag_Impossibility){
                        startTimer(MainConstants.Cons.MoveDownFlag_Impossibility, mGameSpeed);
                    }else{
                        startTimer(MainConstants.Cons.MoveDownFlag_Movement, mGameSpeed);
                    }
                    break;

                case MainConstants.Cons.MoveDownFlag_Impossibility:
                    if(gameOverFlag){
                        Toast.makeText(mContext, "GameOver!!!!!",Toast.LENGTH_LONG).show();
                        mMainActivityViews.showView(true, score);
                        startTimer(MainConstants.Cons.MoveDownFlag_Exit, mGameSpeed);
                        break;
                    }

                    btnFlag = false;

                    // 랜덤 블럭 가져오기
                    gameUserBlock = getRandomBlock();

                    // 유저 블록 시작 생성 위치
                    startUserBlock();

                    // 게임화면 그리기
                    setGameDisplay();


                    btnDownFlag = true;

                    startTimer(MainConstants.Cons.MoveDownFlag_Movement, mGameSpeed);
                    break;

                case MainConstants.Cons.MoveDownFlag_Exit:
                    gameTimeHandler.removeMessages(0);
                    break;
            }
        }
    };

    @Override
    public void onClicker(View view) {
        switch (view.getId()){
            case R.id.btnStart:
                gameStart();
                break;
            case R.id.btnLeft:
                if(!btnFlag)
                    break;

                if(moveLeft(gameBoard))
                    setGameDisplay();

//                if(moveDown(gameBoard))
//                    setGameDisplay();

                break;
            case R.id.btnRight:
                if(!btnFlag)
                    break;

                if(moveRight(gameBoard))
                    setGameDisplay();

//                if(moveDown(gameBoard))
//                    setGameDisplay();

                break;
            case R.id.btnBottmSlow:

                if(!btnDownFlag)
                    break;

                if(!btnFlag)
                    break;

                if(moveDown(gameBoard) == MainConstants.Cons.MoveDownFlag_Movement)
                    setGameDisplay();

                break;
            case R.id.btnBottomFast:
                if(!btnFlag)
                    break;

                break;
            case R.id.btnLeftRotate:
                if(!btnFlag)
                    break;

                if(rotateLeftRight(gameBoard, MainConstants.Cons.RotateTypeLeft))
                    setGameDisplay();

//                if(moveDown(gameBoard))
//                    setGameDisplay();

                break;
            case R.id.btnRightRotate:
                if(!btnFlag)
                    break;

                if(rotateLeftRight(gameBoard, MainConstants.Cons.RotateTypeRight))
                    setGameDisplay();

//                if(moveDown(gameBoard))
//                    setGameDisplay();

                break;
        }
    }

    // ===========================
    //
    // ===========================

    private int getBlockColorResource(int colorCode){
        int result = 0;
        switch (colorCode){
            case MainConstants.Cons.ColorBG: result = MainConstants.Cons.ColorBG_Resource; break;
            case MainConstants.Cons.ColorRED: result = MainConstants.Cons.ColorRED_Resource; break;
            case MainConstants.Cons.ColorBLUE: result = MainConstants.Cons.ColorBLUE_Resource; break;
            case MainConstants.Cons.ColorPURPLE: result = MainConstants.Cons.ColorPURPLE_Resource; break;
            case MainConstants.Cons.ColorSKY: result = MainConstants.Cons.ColorSKY_Resource; break;
            case MainConstants.Cons.ColorGREEN: result = MainConstants.Cons.ColorGREEN_Resource; break;
            case MainConstants.Cons.ColorYELLOW: result = MainConstants.Cons.ColorYELLOW_Resource; break;
            case MainConstants.Cons.ColorOrange: result = MainConstants.Cons.ColorOrange_Resource; break;
        }
        return result;
    }

    private int[][] getRandomBlock(){
        int random = 0;

        if(gameNextUserBlock == null){
            random = (int)(Math.random() * 7) + 1;
            gameNextUserBlock = getGameBlock(random);
            mMainActivityViews.showNextBlock(gameNextUserBlock, random);
            random = (int)(Math.random() * 7) + 1;
            return getGameBlock(random);
        }else{
            int[][] nowBlock = gameNextUserBlock;
            random = (int)(Math.random() * 7) + 1;
            gameNextUserBlock = getGameBlock(random);
            mMainActivityViews.showNextBlock(gameNextUserBlock, random);
            return nowBlock;
        }
    }

    private int[][] getGameBlock(int blockCode){
        int[][] result = null;
        switch(blockCode){
            case MainConstants.Cons.ColorRED:
                result = MainConstants.Blocks.Block_Z;
                break;
            case MainConstants.Cons.ColorBLUE:
                result = MainConstants.Blocks.Block_J;
                break;
            case MainConstants.Cons.ColorPURPLE:
                result = MainConstants.Blocks.Block_T;
                break;
            case MainConstants.Cons.ColorSKY:
                result = MainConstants.Blocks.Block_I;
                break;
            case MainConstants.Cons.ColorGREEN:
                result = MainConstants.Blocks.Block_S;
                break;
            case MainConstants.Cons.ColorYELLOW:
                result = MainConstants.Blocks.Block_O;
                break;
            case MainConstants.Cons.ColorOrange:
                result = MainConstants.Blocks.Block_L;
                break;
        }
        return result;
    }

    private void startUserBlock(){
        ArrayList<ArrayList<HashMap<String, Integer>>> gameBoardTmp = gameBoard;
        for(int i = 0; i < positionY; i++){
            for(int i2 = 0; i2 < positionX; i2++){
                if((i2 > 4 && i2 < 9) && (i < 4)){
                    gameBoardTmp.get(i).get(i2).put(MainConstants.Cons.ColorCode, gameUserBlock[i][i2-5]);
                    gameBoardTmp.get(i).get(i2).put(MainConstants.Cons.BlockArea, MainConstants.Cons.BlockAreaUser);
                    if(gameBoard.get(i).get(i2).get(MainConstants.Cons.ColorCode) != 0){
                        gameBoardTmp.get(i).get(i2).put(MainConstants.Cons.BlockState, MainConstants.Cons.BlockStatus_User);
                    }
                }
            }
        }

        gameBoard = gameBoardTmp;

        Log.d("#@#", "유져 블럭 생성됨! GameOverFalg : " + String.valueOf(gameOverFlag));
    }

    private void setGameDisplay(){
        for(int i = 0; i < positionY; i++){
            for(int i2 = 0; i2 < positionX; i2++){
                HashMap<String, Integer> map = gameBoard.get(i).get(i2);
                int colorCode = map.get(MainConstants.Cons.ColorCode);
                TextView textView = (TextView) ((LinearLayout)gameMainLayout.getChildAt(i)).getChildAt(i2);
                textView.setBackgroundColor(mContext.getResources().getColor(getBlockColorResource(colorCode)));
            }
        }
        btnFlag = true;
    }

    private int moveDown(ArrayList<ArrayList<HashMap<String, Integer>>> gameBoard){
        if(btnFlag){
            btnFlag = false;

            //아래로 이동 가능한지 여부 판단.
            int blockState = MainConstants.Cons.BlockStatus_Empty;
            for(int i = 0; i < positionY; i++){
                for(int i2 = 0; i2 < positionX; i2++) {
                    if((gameBoard.get(i).get(i2).get(MainConstants.Cons.BlockState) == MainConstants.Cons.BlockStatus_User) && (gameBoard.get(i).get(i2).get(MainConstants.Cons.ColorCode) != MainConstants.Cons.ColorBG)){
                        if(i+1 <= (positionY-1)){
                            // 다운 가능

                            if((gameBoard.get(i+1).get(i2).get(MainConstants.Cons.BlockState)) == MainConstants.Cons.BlockStatus_Block){
                                blockState = MainConstants.Cons.BlockStatus_Block;
                                break;
                            }
                        }else{
                            // 다운 불가능
                            btnDownFlag = false;
                            blockState = MainConstants.Cons.BlockStatus_Block;
                            break;
                        }
                    }
                }
            }

            if(blockState == MainConstants.Cons.BlockStatus_Block){

                // 더 이상 이동할게 없음 유져 블록을 변경시켜야함
                for(int i = 0; i < positionY; i++){
                    for(int i2 = 0; i2 < positionX; i2++) {
                        if((gameBoard.get(i).get(i2).get(MainConstants.Cons.BlockState) == MainConstants.Cons.BlockStatus_User)){
                            gameBoard.get(i).get(i2).put(MainConstants.Cons.BlockState, MainConstants.Cons.BlockStatus_Block);
                        }
                        gameBoard.get(i).get(i2).put(MainConstants.Cons.BlockArea, MainConstants.Cons.BlockAearEmpty);
                    }
                }

                //게임 오버 판단.
                for(int i = 0; i < positionX; i++){
                    if((gameBoard.get(0).get(i).get(MainConstants.Cons.BlockState) == MainConstants.Cons.BlockStatus_Block)){
                        gameOverFlag = true;
                        break;
                    }
                }

                // 블록 확인
                log("BlockArea", gameBoard, MainConstants.Cons.BlockArea);

                log("BlockState", gameBoard, MainConstants.Cons.BlockState);

                log("ColorCode", gameBoard, MainConstants.Cons.ColorCode);


                return MainConstants.Cons.MoveDownFlag_Impossibility;
            }

            // 적제된 블럭과 유져 블럭 나눠서 옴겨담기
            blockDivision(gameBoard);

            //유저 블럭 한칸 내리기
            //log("Down 111", userBlockTmp, MainConstants.Cons.BlockArea);
            Collections.reverse(userBlockTmp);
           // log("Down 222", userBlockTmp, MainConstants.Cons.BlockArea);

            boolean areaInMove = false;
            boolean wallFlag = false;
            ArrayList<ArrayList<HashMap<String, Integer>>> moveDownTmp = new ArrayList<ArrayList<HashMap<String, Integer>>>();
            for(int i = 0; i < positionY; i++){
                ArrayList<HashMap<String, Integer>> moveChildTmp = new ArrayList<HashMap<String, Integer>>();
                for(int i2 = 0; i2 < positionX; i2++) {

                    for(int i3 = 0; i3 < positionX; i3++) {
                        if((userBlockTmp.get(0).get(i3).get(MainConstants.Cons.BlockArea) == MainConstants.Cons.BlockAreaUser)){
                            wallFlag = true;
                            areaInMove = true;
                            break;
                        }
                    }

                    if(!wallFlag){
                        if((i+1) <= (positionY-1)){
                            moveChildTmp.add(userBlockTmp.get(i+1).get(i2));
                        }else{
                            moveChildTmp.add(getGameHashMap(i, i2, MainConstants.Cons.ColorBG, MainConstants.Cons.BlockStatus_Empty, MainConstants.Cons.BlockAearEmpty));
                        }
                    }else{
                        moveChildTmp.add(userBlockTmp.get(i).get(i2));
                    }
                }
                wallFlag = false;
                moveDownTmp.add(moveChildTmp);
            }

            //log("Down 333", moveDownTmp, MainConstants.Cons.BlockArea);
            Collections.reverse(moveDownTmp);

            //log("Down 444", moveDownTmp, MainConstants.Cons.BlockArea);

            // 유져 에어리어 안에서 이동해야하는 경우
            if(areaInMove){
                Object[][] areaInMoveTmp = new Object[4][4];
                int tmpX = 0, tmpY = 0;
                Log.d("#@#", "============== areaInMove ===============");
                for(int i = 0; i < positionY; i++){
                    String log = "";
                    for(int i2 = 0; i2 < positionX; i2++) {
                        if(moveDownTmp.get(i).get(i2).get(MainConstants.Cons.BlockArea) == MainConstants.Cons.BlockAreaUser){
                            areaInMoveTmp[tmpX][tmpY] = moveDownTmp.get(i).get(i2);
                            tmpY++;
                            log += "0 ";
                        }else{
                            log += "1 ";
                        }
                    }
                    if(tmpY > 0){
                        tmpX++;
                        tmpY = 0;
                    }
                    Log.d("#@#", log);
                }
                tmpX = 0; tmpY = 0;
                Object[][] areaInMoveTmpLR = new Object[4][4];

                // 이동 가능여부 확인
                boolean moveDownFlag = true;
                for(int i = 0; i < 4; i++){
                    for(int i2 = 0; i2 < 4; i2++){
                        HashMap<String, Integer> map = (HashMap<String, Integer>)areaInMoveTmp[i][i2];
                        if(map == null){
                            moveDownFlag = false;
                            break;
                        }
                        if(map.get(MainConstants.Cons.BlockState) == MainConstants.Cons.BlockStatus_User){
                            if(i == 3){
                                moveDownFlag = false;
                                break;
                            }
                        }
                    }
                    if(!moveDownFlag)
                        break;
                }

                // 이동함
                if(moveDownFlag){
                    for(int i = 0; i < 4; i++){
                        for(int i2 = 3; i2 >= 0; i2--){
                            areaInMoveTmpLR[i][i2] = areaInMoveTmp[i == 0 ? 3 : i-1][i2];
                        }
                    }
                }

                ArrayList<ArrayList<HashMap<String, Integer>>> userTmp = new ArrayList<ArrayList<HashMap<String, Integer>>>();
                for(int i = 0; i < positionY; i++){
                    ArrayList<HashMap<String, Integer>> tmp = new ArrayList<HashMap<String, Integer>>();
                    for(int i2 = 0; i2 < positionX; i2++) {
                        if(moveDownTmp.get(i).get(i2).get(MainConstants.Cons.BlockArea) == MainConstants.Cons.BlockAreaUser){
                            tmp.add(((HashMap<String, Integer>)areaInMoveTmpLR[tmpX][tmpY]));
                            tmpY++;
                        }else{
                            tmp.add(moveDownTmp.get(i).get(i2));
                        }
                    }
                    userTmp.add(tmp);
                    if(tmpY > 0){
                        tmpX++;
                        tmpY = 0;
                    }
                }
                moveDownTmp = userTmp;
            }

            //log("Down 444", moveDownTmp, MainConstants.Cons.BlockArea);

            // 유져블럭과 적제 블럭 합치기
            ArrayList<ArrayList<HashMap<String, Integer>>> blockTmp = blockSum(moveDownTmp, MainConstants.Cons.ScoreTypeDown);

            stopBlockTmp = new ArrayList<ArrayList<HashMap<String, Integer>>>();
            userBlockTmp = new ArrayList<ArrayList<HashMap<String, Integer>>>();

            if(blockTmp != null)
                this.gameBoard = blockTmp;

        }
        return MainConstants.Cons.MoveDownFlag_Movement;
    }

    private boolean moveLeft(ArrayList<ArrayList<HashMap<String, Integer>>> gameBoard){
        if(btnFlag){
            btnFlag = false;
            //왼쪽으로 이동 가능한지 판단
            int blockState = MainConstants.Cons.BlockStatus_Empty;
            boolean moveFlag = true;
            for(int i = 0; i < positionY; i++){
                for(int i2 = 0; i2 < positionX; i2++) {
                    //&& (gameBoard.get(i).get(i2).get(MainConstants.Cons.ColorCode) != MainConstants.Cons.ColorBG)
                    if((gameBoard.get(i).get(i2).get(MainConstants.Cons.BlockState) == MainConstants.Cons.BlockStatus_User) ){
                        if(i2-1 < 0){
                            // 이동 불가
                            moveFlag = false;
                            blockState = MainConstants.Cons.BlockStatus_Block;
                            break;
                        }else{
                            // 이동가능
                            blockState = MainConstants.Cons.BlockStatus_Empty;
                        }
                    }
                }
                if(!moveFlag)
                    break;
            }
            if(blockState == MainConstants.Cons.BlockStatus_Block){
                btnFlag = true;
                return false;
            }

            // 적제된 블럭과 유져 블럭 나눠서 옴겨담기
            blockDivision(gameBoard);

            //유저 블럭 한칸 왼쪽으로 이동하기
            ArrayList<ArrayList<HashMap<String, Integer>>> moveLeftTmp = new ArrayList<ArrayList<HashMap<String, Integer>>>();

            boolean areaInMove = false;
            boolean wallFlag = false;
            for(int i = 0; i < positionY; i++){
                ArrayList<HashMap<String, Integer>> moveLeftChildTmp = new ArrayList<HashMap<String, Integer>>();
                for(int i2 = 0; i2 < positionX; i2++) {
                    HashMap<String, Integer> map = userBlockTmp.get(i).get(i2);

                    if((gameBoard.get(i).get(i2).get(MainConstants.Cons.BlockArea) == MainConstants.Cons.BlockAreaUser)) {
                        if((i2-1) == -1)
                            wallFlag = true;

                        if(!wallFlag){
                            moveLeftChildTmp.set((i2-1), map);
                            moveLeftChildTmp.add(getGameHashMap(i, i2, MainConstants.Cons.ColorBG, MainConstants.Cons.BlockStatus_Empty, MainConstants.Cons.BlockAearEmpty));
                        }else{
                            areaInMove = true;
                            moveLeftChildTmp.add(map);
                        }
                    }else{
                        moveLeftChildTmp.add(getGameHashMap(i, i2, MainConstants.Cons.ColorBG, MainConstants.Cons.BlockStatus_Empty, MainConstants.Cons.BlockAearEmpty));
                    }
                }
                wallFlag = false;
                moveLeftTmp.add(moveLeftChildTmp);
            }

            // 유져 에어리어 안에서 이동해야하는 경우
            if(areaInMove){
                Object[][] areaInMoveTmp = new Object[4][4];
                int tmpX = 0, tmpY = 0;
                for(int i = 0; i < positionY; i++){
                    for(int i2 = 0; i2 < positionX; i2++) {
                        if(moveLeftTmp.get(i).get(i2).get(MainConstants.Cons.BlockArea) == MainConstants.Cons.BlockAreaUser){
                            areaInMoveTmp[tmpX][tmpY] = moveLeftTmp.get(i).get(i2);
                            tmpY++;
                        }
                    }
                    if(tmpY > 0){
                        tmpX++;
                        tmpY = 0;
                    }
                }
                tmpX = 0; tmpY = 0;
                Object[][] areaInMoveTmpLR = new Object[4][4];

                // 이동 가능여부 확인
                boolean moveLeftFlag = true;
                for(int i = 0; i < 4; i++){
                    for(int i2 = 0; i2 < 4; i2++){
                        HashMap<String, Integer> map = (HashMap<String, Integer>)areaInMoveTmp[i][i2];
                        if(map.get(MainConstants.Cons.BlockState) == MainConstants.Cons.BlockStatus_User){
                            if(i2 == 0){
                                moveLeftFlag = false;
                                break;
                            }
                        }
                    }
                    if(!moveLeftFlag)
                        break;
                }

                // 이동함
                if(moveLeftFlag){
                    for(int i = 0; i < 4; i++){
                        for(int i2 = 0; i2 < 4; i2++){
                            areaInMoveTmpLR[i][i2] = areaInMoveTmp[i][i2 == 3 ? 0 : i2+1];
                        }
                    }
                }

                ArrayList<ArrayList<HashMap<String, Integer>>> userTmp = new ArrayList<ArrayList<HashMap<String, Integer>>>();
                for(int i = 0; i < positionY; i++){
                    ArrayList<HashMap<String, Integer>> tmp = new ArrayList<HashMap<String, Integer>>();
                    for(int i2 = 0; i2 < positionX; i2++) {
                        if(moveLeftTmp.get(i).get(i2).get(MainConstants.Cons.BlockArea) == MainConstants.Cons.BlockAreaUser){
                            tmp.add(((HashMap<String, Integer>)areaInMoveTmpLR[tmpX][tmpY]));
                            tmpY++;
                        }else{
                            tmp.add(moveLeftTmp.get(i).get(i2));
                        }
                    }
                    userTmp.add(tmp);
                    if(tmpY > 0){
                        tmpX++;
                        tmpY = 0;
                    }
                }
                moveLeftTmp = userTmp;
            }

            // 유져블럭과 적제 블럭 합치기
            ArrayList<ArrayList<HashMap<String, Integer>>> blockTmp = blockSum(moveLeftTmp, MainConstants.Cons.ScoreTypeNone);

            stopBlockTmp = new ArrayList<ArrayList<HashMap<String, Integer>>>();
            userBlockTmp = new ArrayList<ArrayList<HashMap<String, Integer>>>();

            if(blockTmp != null)
                this.gameBoard = blockTmp;

            btnFlag = true;
            return true;
        }
        return false;
    }

    private boolean moveRight(ArrayList<ArrayList<HashMap<String, Integer>>> gameBoard){
        if(btnFlag){
            btnFlag = false;
            //오른쪽으로 이동 가능한지 판단
            boolean moveFlag = true;
            int blockState = MainConstants.Cons.BlockStatus_Empty;

            //log("Right", gameBoard, MainConstants.Cons.BlockArea);

            for(int i = 0; i < positionY; i++){
                for(int i2 = 0; i2 < positionX; i2++) {
                    if((gameBoard.get(i).get(i2).get(MainConstants.Cons.BlockState) == MainConstants.Cons.BlockStatus_User) && (gameBoard.get(i).get(i2).get(MainConstants.Cons.ColorCode) != MainConstants.Cons.ColorBG)){
                        if(i2+1 <= positionX-1){
                            if(gameBoard.get(i).get(i2+1).get(MainConstants.Cons.BlockState) == MainConstants.Cons.BlockStatus_Block){
                                // 이동 불가
                                moveFlag = false;
                                blockState = MainConstants.Cons.BlockStatus_Block;
                                break;
                            }else{
                                // 이동가능
                                blockState = MainConstants.Cons.BlockStatus_Empty;
                            }
                        }else{
                            // 이동 불가
                            moveFlag = false;
                            blockState = MainConstants.Cons.BlockStatus_Block;
                            break;
                        }
                    }
                }
                if(!moveFlag)
                    break;
            }

            Log.d("#@#", "Right blockState : " + blockState);
            if(blockState == MainConstants.Cons.BlockStatus_Block){
                btnFlag = true;
                return false;
            }

            // 적제된 블럭과 유져 블럭 나눠서 옴겨담기
            blockDivision(gameBoard);

            //유저 블럭 한칸 오른쪽으로 이동하기
            for(int i = 0; i < positionY; i++){
                Collections.reverse(userBlockTmp.get(i));
            }

            boolean areaInMove = false;
            boolean wallFlag = false;
            ArrayList<ArrayList<HashMap<String, Integer>>> moveRightTmp = new ArrayList<ArrayList<HashMap<String, Integer>>>();
            for(int i = 0; i < positionY; i++){
                ArrayList<HashMap<String, Integer>> moveRightChildTmp = new ArrayList<HashMap<String, Integer>>();
                for(int i2 = 0; i2 < positionX; i2++) {
                    HashMap<String, Integer> map = userBlockTmp.get(i).get(i2);

                    if((userBlockTmp.get(i).get(i2).get(MainConstants.Cons.BlockArea) == MainConstants.Cons.BlockAreaUser)) {
                        if((i2-1) == -1)
                            wallFlag = true;

                        if(!wallFlag){
                            moveRightChildTmp.set((i2-1), map);
                            moveRightChildTmp.add(getGameHashMap(i, i2, MainConstants.Cons.ColorBG, MainConstants.Cons.BlockStatus_Empty, MainConstants.Cons.BlockAearEmpty));
                        }else{
                            areaInMove = true;
                            moveRightChildTmp.add(map);
                        }
                    }else{
                        moveRightChildTmp.add(getGameHashMap(i, i2, MainConstants.Cons.ColorBG, MainConstants.Cons.BlockStatus_Empty, MainConstants.Cons.BlockAearEmpty));
                    }
                }
                wallFlag = false;
                moveRightTmp.add(moveRightChildTmp);
            }
            //*/
            for(int i = 0; i < positionY; i++){
                Collections.reverse(moveRightTmp.get(i));
            }

            // 유져 에어리어 안에서 이동해야하는 경우
            if(areaInMove){
                Object[][] areaInMoveTmp = new Object[4][4];
                int tmpX = 0, tmpY = 0;
                for(int i = 0; i < positionY; i++){
                    for(int i2 = 0; i2 < positionX; i2++) {
                        if(moveRightTmp.get(i).get(i2).get(MainConstants.Cons.BlockArea) == MainConstants.Cons.BlockAreaUser){
                            areaInMoveTmp[tmpX][tmpY] = moveRightTmp.get(i).get(i2);
                            tmpY++;
                        }
                    }
                    if(tmpY > 0){
                        tmpX++;
                        tmpY = 0;
                    }
                }
                tmpX = 0; tmpY = 0;
                Object[][] areaInMoveTmpLR = new Object[4][4];

                // 이동 가능여부 확인
                boolean moveLeftFlag = true;
                for(int i = 0; i < 4; i++){
                    for(int i2 = 0; i2 < 4; i2++){
                        HashMap<String, Integer> map = (HashMap<String, Integer>)areaInMoveTmp[i][i2];
                        if(map.get(MainConstants.Cons.BlockState) == MainConstants.Cons.BlockStatus_User){
                            if(i2 == 3){
                                moveLeftFlag = false;
                                break;
                            }
                        }
                    }
                    if(!moveLeftFlag)
                        break;
                }

                // 이동함
                if(moveLeftFlag){
                    for(int i = 0; i < 4; i++){
                        for(int i2 = 3; i2 >= 0; i2--){
                            areaInMoveTmpLR[i][i2] = areaInMoveTmp[i][i2 == 0 ? 3 : i2-1];
                        }
                    }
                }

                ArrayList<ArrayList<HashMap<String, Integer>>> userTmp = new ArrayList<ArrayList<HashMap<String, Integer>>>();
                for(int i = 0; i < positionY; i++){
                    ArrayList<HashMap<String, Integer>> tmp = new ArrayList<HashMap<String, Integer>>();
                    for(int i2 = 0; i2 < positionX; i2++) {
                        if(moveRightTmp.get(i).get(i2).get(MainConstants.Cons.BlockArea) == MainConstants.Cons.BlockAreaUser){
                            tmp.add(((HashMap<String, Integer>)areaInMoveTmpLR[tmpX][tmpY]));
                            tmpY++;
                        }else{
                            tmp.add(moveRightTmp.get(i).get(i2));
                        }
                    }
                    userTmp.add(tmp);
                    if(tmpY > 0){
                        tmpX++;
                        tmpY = 0;
                    }
                }
                moveRightTmp = userTmp;
            }

           //log("Right", moveRightTmp, MainConstants.Cons.BlockArea);

            // 유져블럭과 적제 블럭 합치기
            ArrayList<ArrayList<HashMap<String, Integer>>> blockTmp = blockSum(moveRightTmp, MainConstants.Cons.ScoreTypeNone);

            stopBlockTmp = new ArrayList<ArrayList<HashMap<String, Integer>>>();
            userBlockTmp = new ArrayList<ArrayList<HashMap<String, Integer>>>();

            if(blockTmp != null)
                this.gameBoard = blockTmp;

            btnFlag = true;
            return true;
        }
        return true;
    }

    private boolean rotateLeftRight(ArrayList<ArrayList<HashMap<String, Integer>>> gameBoard, int rotateType){
        if(btnFlag){
            btnFlag = false;

            //적제된 블럭과 유져 블럭 나눠서 옴겨담기
            blockDivision(gameBoard);

           // log("rotateLeftRight", userBlockTmp, MainConstants.Cons.BlockArea);

            //회전시키기
            Object[][] rotateTmp = new Object[4][4];
            int tmpX = 0, tmpY = 0;
            for(int i = 0; i < positionY; i++){
                for(int i2 = 0; i2 < positionX; i2++) {
                    if(userBlockTmp.get(i).get(i2).get(MainConstants.Cons.BlockArea) == MainConstants.Cons.BlockAreaUser){
                        rotateTmp[tmpX][tmpY] = userBlockTmp.get(i).get(i2);
                        tmpY++;
                    }
                }
                if(tmpY > 0){
                    tmpX++;
                    tmpY = 0;
                }
            }
            tmpX = 0; tmpY = 0;

            Object[][] rotateLR = null;

            switch (rotateType){
                case MainConstants.Cons.RotateTypeLeft:
                    rotateLR = rotate90Left(rotateTmp);
                    break;
                case MainConstants.Cons.RotateTypeRight:
                    rotateLR = rotate90Right(rotateTmp);
                    break;
            }

            ArrayList<ArrayList<HashMap<String, Integer>>> userTmp = new ArrayList<ArrayList<HashMap<String, Integer>>>();

            for(int i = 0; i < positionY; i++){
                ArrayList<HashMap<String, Integer>> tmp = new ArrayList<HashMap<String, Integer>>();
                for(int i2 = 0; i2 < positionX; i2++) {
                    if(userBlockTmp.get(i).get(i2).get(MainConstants.Cons.BlockArea) == MainConstants.Cons.BlockAreaUser){
                        tmp.add(((HashMap<String, Integer>)rotateLR[tmpX][tmpY]));
                        tmpY++;
                    }else{
                        tmp.add(userBlockTmp.get(i).get(i2));
                    }
                }
                userTmp.add(tmp);
                if(tmpY > 0){
                    tmpX++;
                    tmpY = 0;
                }
            }

            userBlockTmp = userTmp;

            // 유져블럭과 적제 블럭 합치기
            ArrayList<ArrayList<HashMap<String, Integer>>> blockTmp = blockSum(userBlockTmp, MainConstants.Cons.ScoreTypeNone);

            stopBlockTmp = new ArrayList<ArrayList<HashMap<String, Integer>>>();
            userBlockTmp = new ArrayList<ArrayList<HashMap<String, Integer>>>();

            if(blockTmp != null)
                this.gameBoard = blockTmp;

            btnFlag = true;
            return true;
        }
        return false;
    }

    private void log(String name, ArrayList<ArrayList<HashMap<String, Integer>>> list, String key){
        Log.d("#@#", "===============" + name +"=====================");
        for(int i = 0; i < positionY; i++){
            String log = "";
            for(int i2 = 0; i2 < positionX; i2++){
                /*
                if(list.get(i).get(i2).get(key) == MainConstants.Cons.BlockAreaUser){
                    log += String.valueOf(list.get(i).get(i2).get(key) + " ");
                }
                /*/
                log += String.valueOf(list.get(i).get(i2).get(key) + " ");
                //*/
            }
            Log.d("#@#", log);
        }
    }

    /**
     * 유져블럭과 적제 블럭 합치기
     * @param moveTmp
     */
    private ArrayList<ArrayList<HashMap<String, Integer>>> blockSum(ArrayList<ArrayList<HashMap<String, Integer>>> moveTmp, int scoreType){
        boolean rotateFlag = true;
        ArrayList<ArrayList<HashMap<String, Integer>>> blockTmp = new ArrayList<ArrayList<HashMap<String, Integer>>>();
        for(int i = 0; i < positionY; i++){
            ArrayList<HashMap<String, Integer>> gameBoardChildTmp = new ArrayList<HashMap<String, Integer>>();
            for(int i2 = 0; i2 < positionX; i2++) {

                if(moveTmp.get(i).get(i2) == null){
                    rotateFlag = false;
                    break;
                }

                if((moveTmp.get(i).get(i2).get(MainConstants.Cons.BlockState) == MainConstants.Cons.BlockStatus_User )){
                    gameBoardChildTmp.add(moveTmp.get(i).get(i2));
                }else{
                    HashMap<String, Integer> mapTmp = new HashMap<>();
                    mapTmp.put(MainConstants.Cons.PositionX, stopBlockTmp.get(i).get(i2).get(MainConstants.Cons.PositionX));
                    mapTmp.put(MainConstants.Cons.PositionY, stopBlockTmp.get(i).get(i2).get(MainConstants.Cons.PositionY));
                    mapTmp.put(MainConstants.Cons.ColorCode, stopBlockTmp.get(i).get(i2).get(MainConstants.Cons.ColorCode));
                    mapTmp.put(MainConstants.Cons.BlockState, stopBlockTmp.get(i).get(i2).get(MainConstants.Cons.BlockState));
                    mapTmp.put(MainConstants.Cons.BlockArea, moveTmp.get(i).get(i2).get(MainConstants.Cons.BlockArea));
                    gameBoardChildTmp.add(mapTmp);
                }
            }
            if(!rotateFlag)
                break;

            blockTmp.add(gameBoardChildTmp);
        }

        if(rotateFlag){
            // 점수 계산

            switch (scoreType){
                case MainConstants.Cons.ScoreTypeDown:
                    // 다운시 점수
                    score += MainConstants.Cons.ScoreDown;

                    // 블록 삭제 점수
                    boolean lineScoreCheckFalg = false;
                    int deleteCount = 0;

                    Collections.reverse(blockTmp);

                    // 삭제할 ListObject Count
                    ArrayList<ArrayList<HashMap<String, Integer>>> deleteObjects = new ArrayList<ArrayList<HashMap<String, Integer>>>();
                    for(int i = 0; i < blockTmp.size(); i++){
                        boolean deleteFlag = true;
                        ArrayList<HashMap<String, Integer>> deleteObject = blockTmp.get(i);
                        for(int i2 = 0; i2 < positionX; i2++){
                            if(deleteObject.get(i2).get(MainConstants.Cons.BlockState) == MainConstants.Cons.BlockStatus_Empty){
                                deleteFlag = false;
                                break;
                            }
                        }
                        if(deleteFlag){
                            deleteCount += 1;
                            lineScoreCheckFalg = true;
                            deleteObjects.add(deleteObject);
                        }
                    }

                    if(lineScoreCheckFalg){
                        gameTimeHandler.removeMessages(0);
                        //long nowTime = System.currentTimeMillis();
                        //long nextTime = (nowTime - handlerStartTime);

                        // 삭제한다.
                        for(int i = 0; i < deleteObjects.size(); i++){
                            ArrayList<HashMap<String, Integer>> object = deleteObjects.get(i);
                            for(int i2 = 0; i2 < blockTmp.size(); i2++){
                                if(objectEquals(object, blockTmp.get(i2))){
                                    blockTmp.remove(object);
                                }
                            }
                        }

                        // 삭제한 만큼의 줄을 추가해준다.
                        for (int i = 0; i < deleteCount; i++){
                            blockTmp.add(getNewLineArrayList(positionY - blockTmp.size()));
                        }

                        score += (MainConstants.Cons.ScoreBlock * deleteCount);

                        //startTimer(MainConstants.Cons.MoveDownFlag_Movement, nextTime);
                    }

                    Collections.reverse(blockTmp);
                    break;
            }
            mMainActivityViews.showView(gameOverFlag, score);
        }

       return blockTmp;
    }

    private boolean objectEquals(Object objA, Object objB){
        try{
            if(objA.equals(objB)){
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }

    /**
     * 삭제한 리스트 추가용
     * @param x
     * @return
     */
    private ArrayList<HashMap<String, Integer>> getNewLineArrayList(int x){
        ArrayList<HashMap<String, Integer>> newList = new ArrayList<HashMap<String, Integer>>();
        for(int i = 0; i < positionX; i++){
            newList.add(getGameHashMap(x, i, MainConstants.Cons.ColorBG, MainConstants.Cons.BlockStatus_Empty, MainConstants.Cons.BlockAearEmpty));
        }
        return newList;
    }

    /**
     * stopBlockTmp, userBlockTmp <br>
     * 시작시 변수를 초기화 시키고 전역변수에 담아둔다.
     */
    private void blockDivision(ArrayList<ArrayList<HashMap<String, Integer>>> gameBoard){
        stopBlockTmp = new ArrayList<ArrayList<HashMap<String, Integer>>>();
        userBlockTmp = new ArrayList<ArrayList<HashMap<String, Integer>>>();

        for(int i = 0; i < positionY; i++){
            ArrayList<HashMap<String, Integer>> stopBlockChildTmp = new ArrayList<HashMap<String, Integer>>();
            ArrayList<HashMap<String, Integer>> userBlockChildTmp = new ArrayList<HashMap<String, Integer>>();
            for(int i2 = 0; i2 < positionX; i2++){
                HashMap<String, Integer> map = new HashMap<>();
                map.put(MainConstants.Cons.PositionX, gameBoard.get(i).get(i2).get(MainConstants.Cons.PositionX));
                map.put(MainConstants.Cons.PositionY, gameBoard.get(i).get(i2).get(MainConstants.Cons.PositionY));
                map.put(MainConstants.Cons.ColorCode, gameBoard.get(i).get(i2).get(MainConstants.Cons.ColorCode));
                map.put(MainConstants.Cons.BlockState, gameBoard.get(i).get(i2).get(MainConstants.Cons.BlockState));
                map.put(MainConstants.Cons.BlockArea, gameBoard.get(i).get(i2).get(MainConstants.Cons.BlockArea));

                HashMap<String, Integer> mapEmpty = new HashMap<>();
                mapEmpty.put(MainConstants.Cons.PositionX, gameBoard.get(i).get(i2).get(MainConstants.Cons.PositionX));
                mapEmpty.put(MainConstants.Cons.PositionY, gameBoard.get(i).get(i2).get(MainConstants.Cons.PositionY));
                mapEmpty.put(MainConstants.Cons.ColorCode, MainConstants.Cons.ColorBG);
                mapEmpty.put(MainConstants.Cons.BlockState, MainConstants.Cons.BlockStatus_Empty);
                mapEmpty.put(MainConstants.Cons.BlockArea, gameBoard.get(i).get(i2).get(MainConstants.Cons.BlockArea));

                if(map.get(MainConstants.Cons.BlockState) == MainConstants.Cons.BlockStatus_User){
                    // 유저블럭
                    userBlockChildTmp.add(map);
                    stopBlockChildTmp.add(mapEmpty);
                } else if(map.get(MainConstants.Cons.BlockState) == MainConstants.Cons.BlockStatus_Block){
                    // 적제블럭
                    userBlockChildTmp.add(mapEmpty);
                    stopBlockChildTmp.add(map);
                }else{
                    stopBlockChildTmp.add(mapEmpty);
                    userBlockChildTmp.add(map);
                }
            }

            stopBlockTmp.add(stopBlockChildTmp);
            userBlockTmp.add(userBlockChildTmp);
        }
    }

    public int getColorResource(Context context, int colorCode){
        int color = 0;
        switch(colorCode){
            case MainConstants.Cons.ColorRED:
                color = context.getResources().getColor(R.color.colorRed);
                break;
            case MainConstants.Cons.ColorBLUE:
                color = context.getResources().getColor(R.color.colorBlue);
                break;
            case MainConstants.Cons.ColorPURPLE:
                color = context.getResources().getColor(R.color.colorPurple);
                break;
            case MainConstants.Cons.ColorSKY:
                color = context.getResources().getColor(R.color.colorSky);
                break;
            case MainConstants.Cons.ColorGREEN:
                color = context.getResources().getColor(R.color.colorGreen);
                break;
            case MainConstants.Cons.ColorYELLOW:
                color = context.getResources().getColor(R.color.colorYellow);
                break;
            case MainConstants.Cons.ColorOrange:
                color = context.getResources().getColor(R.color.colorOrange);
                break;
        }
        return color;
    }


    public int getBestScore(Context context){
        return mainModel.getIntegerPref(context, MainConstants.Cons.PREF_BEST_SCORE);
    }

    public void setBsetScore(Context context, int bestScore){
        mainModel.setIntegerPref(context, MainConstants.Cons.PREF_BEST_SCORE, bestScore);
    }

    private HashMap<String, Integer> getGameHashMap(int positionX, int positionY, int color, int blockState, int blockArea){
        HashMap<String, Integer> map = new HashMap<>();
        map.put(MainConstants.Cons.PositionX, positionX);     // X 좌표
        map.put(MainConstants.Cons.PositionY, positionY);     // Y 좌표
        map.put(MainConstants.Cons.ColorCode, color);         // 컬러
        map.put(MainConstants.Cons.BlockState, blockState);   // 블록 초기화
        map.put(MainConstants.Cons.BlockArea, blockArea);     // 유져 영역
        return map;
    }

    private int[][] rotate90Left(int[][] m) {
        int columns = m.length;
        int rows = m[0].length;
        int[][] tmpMtx = new int[rows][columns];

        for (int j = 0; j < columns; j++) {
            for (int i = 0, k = rows - 1; i < rows; i++, k--) {
                tmpMtx[i][j] = m[j][k];
            }
        }
        return tmpMtx;
    }

    private Object[][] rotate90Left(Object[][] m) {
        int columns = m.length;
        int rows = m[0].length;
        Object[][] tmpMtx = new Object[rows][columns];

        for (int j = 0; j < columns; j++) {
            for (int i = 0, k = rows - 1; i < rows; i++, k--) {
                tmpMtx[i][j] = m[j][k];
            }
        }
        return tmpMtx;
    }
    private ArrayList<ArrayList<HashMap<String, Integer>>> rotate90LeftArrayList(ArrayList<ArrayList<HashMap<String, Integer>>> list) {
        int columns = positionY;
        int rows = positionX;
        ArrayList<ArrayList<HashMap<String, Integer>>> tmpMtx = getDefaultLists();
        for (int j = 0; j < columns; j++) {
            for (int i = 0, k = rows - 1; i < rows; i++, k--) {
                tmpMtx.get(i).set(j, list.get(j).get(k));
            }
        }
        return tmpMtx;
    }

    private static int[][] rotate90Right(int[][] m) {
        int columns = m.length;
        int rows = m[0].length;
        int[][] tmpMtx = new int[rows][columns];

        for (int j = 0; j < columns; j++) {
            for (int i = 0; i < rows; i++) {
                tmpMtx[i][columns - j - 1] = m[j][i];
            }
        }
        return tmpMtx;
    }
    private static Object[][] rotate90Right(Object[][] m) {
        int columns = m.length;
        int rows = m[0].length;
        Object[][] tmpMtx = new Object[rows][columns];

        for (int j = 0; j < columns; j++) {
            for (int i = 0; i < rows; i++) {
                tmpMtx[i][columns - j - 1] = m[j][i];
            }
        }
        return tmpMtx;
    }
    private ArrayList<ArrayList<HashMap<String, Integer>>> rotate90RightArrayList(ArrayList<ArrayList<HashMap<String, Integer>>> list) {
        int columns = positionY;
        int rows = positionX;
        ArrayList<ArrayList<HashMap<String, Integer>>> tmpMtx = getDefaultLists();

        for (int j = 0; j < columns; j++) {
            for (int i = 0; i < rows; i++) {
                tmpMtx.get(i).set((columns - j - 1), list.get(j).get(i));
            }
        }
        return tmpMtx;
    }
    private ArrayList getDefaultLists(){
        ArrayList<ArrayList<HashMap<String, Integer>>> tmpList = new ArrayList<>();
        for(int index1 = 0; index1 < positionY; index1++){
            ArrayList<HashMap<String, Integer>> childMapList = new ArrayList<>();
            for(int index2 = 0; index2 < positionX; index2++){
                childMapList.add(getGameHashMap(index1, index2, MainConstants.Cons.ColorBG, MainConstants.Cons.BlockStatus_Empty, MainConstants.Cons.BlockAearEmpty));
            }
            tmpList.add(childMapList);
        }
        return tmpList;
    }

    private void startTimer(int sendType, long gameSpeed){
        gameTimeHandler.sendEmptyMessageDelayed(sendType, gameSpeed);
        //handlerStartTime = System.currentTimeMillis();
    }

    // ===========================
    // Getter, Setter
    // ===========================

    public boolean isGameStartFlag(){
        return mGameStartFlag;
    }
    public void setGameStartFlag(boolean gameStartFlag){
        this.mGameStartFlag = gameStartFlag;
    }
    public void setGameOverFlag(boolean gameOverFlag){
        this.gameOverFlag = gameOverFlag;
    }

    public int getPositionX() { return positionX; }
    public int getPositionY() { return positionY; }
}
