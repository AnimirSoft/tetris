package com.animir.tetris.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.animir.tetris.R;
import com.animir.tetris.presenter.MainConstants;
import com.animir.tetris.presenter.MainPresenter;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Create by Animir [2019.08.22]
 */
public class MainActivity extends AppCompatActivity implements MainConstants.Views {

    private MainPresenter mPresenter = null;
    private int gameScore = 0;
    private int bestScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mPresenter = new MainPresenter();
        mPresenter.setGameLayout(this, this, findViewById(R.id.gameBoardMain));
        mPresenter.init();

        bestScore = mPresenter.getBestScore(this);
        ((TextView)findViewById(R.id.tv_best_score)).setText(String.valueOf(bestScore));
    }


    @OnClick({R.id.btnStart, R.id.btnBottmSlow, R.id.btnBottomFast, R.id.btnLeft, R.id.btnRight, R.id.btnLeftRotate, R.id.btnRightRotate})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btnStart:
                if(!mPresenter.isGameStartFlag()){
                    mPresenter.setGameStartFlag(true);
                    mPresenter.setGameOverFlag(false);
                    mPresenter.onClicker(view);
                    changeBtnEnable(false);
                    Toast.makeText(this, "GameStart!!!",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnLeft:
            case R.id.btnRight:
            case R.id.btnBottmSlow:
            case R.id.btnBottomFast:
            case R.id.btnLeftRotate:
            case R.id.btnRightRotate:
                if(mPresenter.isGameStartFlag())
                    mPresenter.onClicker(view);
                break;
        }
    }

    @Override
    public void showView(boolean gameOverFlag, int score) {
        if(gameOverFlag){
            mPresenter.setGameStartFlag(false);
            changeBtnEnable(true);
        }else{
            gameScore = score;
            ((TextView)findViewById(R.id.tv_score)).setText(String.valueOf(score));
        }
    }

    @Override
    public void showNextBlock(int[][] nextBlock, int colorCode) {

        int x = nextBlock.length;
        int y = nextBlock[0].length;

        LinearLayout nextLayout = findViewById(R.id.nextLayout);

        nextLayout.removeAllViews();

        for(int i = 0; i < y; i++){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            params.setMargins(1,1,1,1);
            LinearLayout childNextLayout = new LinearLayout(MainActivity.this);
            childNextLayout.setLayoutParams(params);

            for (int i2 = 0; i2 < x; i2++){
                TextView textView = new TextView(MainActivity.this);
                if(nextBlock[i][i2] == 0){
                    textView.setBackgroundColor(this.getResources().getColor(R.color.colorBg));
                }else{
                    textView.setBackgroundColor(mPresenter.getColorResource(MainActivity.this, nextBlock[i][i2]));
                }

                textView.setLayoutParams(params);
                childNextLayout.addView(textView);
            }
            nextLayout.addView(childNextLayout);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bestScore < gameScore)
            mPresenter.setBsetScore(this, gameScore);
    }

    private void changeBtnEnable(boolean enableFlag){
        findViewById(R.id.btnStart).setEnabled(enableFlag);
    }

}
