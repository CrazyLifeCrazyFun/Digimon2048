package com.zhengxiaoyao0716.digimon2048;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.zhengxiaoyao0716.game2048.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GameActivity extends Activity {
	private TextView levelTextView;
	private TextView scoreTextView;
	private GridLayout boardGrid;

	private int boardH, boardW, aimNum;
	private Game2048 game2048;

	private int[] digimons;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		ImageView replayButton = (ImageView) findViewById(R.id.replayButton);
		replayButton.setOnClickListener(onClickListener);
		ImageView soundButton = (ImageView) findViewById(R.id.soundButton);
		soundButton.setOnClickListener(onClickListener);
		ImageView offButton = (ImageView) findViewById(R.id.offButton);
		offButton.setOnClickListener(onClickListener);
		
		levelTextView = (TextView) findViewById(R.id.levelTextView);
		scoreTextView = (TextView) findViewById(R.id.scoreTextView);
		boardGrid = (GridLayout) findViewById(R.id.boardGrid);
		boardGrid.setOnTouchListener(onBoardTouchListener);
		boardH = boardW = 4;
		aimNum = 4096;
		boardGrid.setRowCount(boardH);
		boardGrid.setColumnCount(boardW);
		for (int height = 0; height < boardH; height++)
			for (int width = 0; width < boardW; width++)
			{
				ImageView grid = new ImageView(this);
				grid.setBackgroundResource(R.mipmap.cover_grid);
				grid.setPadding(8, 9, 12, 11);
				boardGrid.addView(grid);
			}
		try {
			game2048 = new Game2048(gameCommunicate, boardH, boardW, aimNum);
		} catch (Game2048Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		game2048.startGame();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		game2048.quitGame();
	}

	private OnClickListener onClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId())
			{
				case R.id.replayButton:
				{
					new AlertDialog.Builder(getParent())
							.setNegativeButton(getString(R.string.cancel), null)
							.setNeutralButton(getString(R.string.restart),
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface d, int i) {
											game2048.replay(false);
										}})
							.setPositiveButton(getString(R.string.replay),
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface d, int i) {
											game2048.replay(true);
										}
									}).show();
				}break;
			}
			
		}
	};

	float touchX=0, touchY=0;
	private View.OnTouchListener onBoardTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO: Implement this method
			if (MotionEvent.ACTION_DOWN==event.getAction())
			{
				touchX = event.getX();
				touchY = event.getY();
			}
			else if (MotionEvent.ACTION_UP==event.getAction())
			{
				touchX -= event.getX();
				touchY -= event.getY();
				if (touchX == 0 && touchY == 0)
				{
					//show normal
				}
				else
				{
					try {
						game2048.action(((touchY + touchX > 0) ? 0 : 2)
								+ ((touchY - touchX > 0) ? 0 : 1));
					} catch (Game2048Exception e) {
						e.printStackTrace();
					}
				}
			}
			return true;
		}
	};

	private Game2048Communicate gameCommunicate = new Game2048Communicate()
	{
		@Override
		public Map<String, Object> loadData() {
			// TODO Auto-generated method stub
			//load game data
			HashMap<String, Object> dataMap = null;
			try {
				//read file
				FileInputStream inputStream = openFileInput("gameData");
				byte[] bytes = new byte[1024];
				ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
				while (inputStream.read(bytes) != -1) {
					arrayOutputStream.write(bytes, 0, bytes.length);
				}
				inputStream.close();
				arrayOutputStream.close();
				String dataStr = new String(arrayOutputStream.toByteArray());

				//read json, write map
				JSONObject dataJO = null;
				if (dataStr!=null) dataJO = new JSONObject(dataStr);
				dataMap = new HashMap<String, Object>();
				aimNum = dataJO.getInt("aimNum");
				dataMap.put("aimNum", aimNum);
				dataMap.put("level", dataJO.getInt("level"));
				dataMap.put("score", dataJO.getInt("score"));
				JSONArray boardJA = dataJO.getJSONArray("board");
				boardH = boardJA.length();
				boardW = boardJA.optJSONArray(0).length();
				int[][] board = new int[boardH][boardW];
				for (int height = 0; height < boardJA.length(); height++) {
					JSONArray rowJA = boardJA.getJSONArray(height);
					for (int width = 0; width < rowJA.length(); width++)
						board[height][width] = rowJA.getInt(width);
				}
				dataMap.put("board", board);

				//read json, write digimons
				JSONArray digimonJA = dataJO.getJSONArray("digimons");
				int digimonNums = digimonJA.length();
				digimons = new int[digimonNums];
				for (int index = 0; index < digimonNums; index++)
					digimons[index] = digimonJA.getInt(index);
			} catch (Exception e) {
				aimNum = 4096;
				boardH = boardW = 4;
				digimons = new int[1];
				digimons[0] = 1 + new Random().nextInt(14);
				return null;
			}
			return dataMap;
		}

		@Override
		public boolean saveData(Map<String, Object> dataMap) {
			// TODO Auto-generated method stub
			//save game data
			try {
				//read map, write json
				JSONObject dataJO = new JSONObject();
				dataJO.put("aimNum", dataMap.get("aimNum"));
				dataJO.put("level", dataMap.get("level"));
				dataJO.put("score", dataMap.get("score"));
				int[][] board = (int[][]) dataMap.get("board");
				JSONArray boardJA = new JSONArray();
				for (int[] row : board) {
					JSONArray rowJA = new JSONArray();
					for (int grid : row) rowJA.put(grid);
					boardJA.put(rowJA);
				}
				dataJO.put("board", boardJA);

				//read digimons, make json
				JSONArray digimonJA = new JSONArray();
				for (int digimon : digimons)
					digimonJA.put(digimon);
				dataJO.put("digimons", digimonJA);

				//write file
				FileOutputStream outputStream = openFileOutput("gameData",
						Activity.MODE_PRIVATE);
				outputStream.write(dataJO.toString().getBytes());
				outputStream.flush();
				outputStream.close();
			} catch (Exception e) {
				return false;
			}
			return true;
		}

		@Override
		public void showData(int level, int score, int[][] board) {
			// TODO Auto-generated method stub
			levelTextView.setText("Level:" + level);
			scoreTextView.setText("Score:" + score);

			String imageSort = new StringBuilder("grid")
					.append(digimons[level - 1]).append("_").toString();
			for (int height = 0; height < boardH; height++)
				for (int width = 0; width < boardW; width++)
				{
					ImageView grid = (ImageView) boardGrid.getChildAt(4 * height+ width);
					if (board[height][width]==0) grid.setImageResource(R.mipmap.grid00);
					else if (board[height][width]<=aimNum)
					{
						String imageName
								= new StringBuilder(imageSort)
								.append(board[height][width]).toString();
						grid.setImageResource(getResources().getIdentifier(imageName,
								"mipmap", "com.zhengxiaoyao0716.digimon2048"));
					}
					else
					{
						String imageName = new StringBuilder("grid")
								.append(digimons[board[height][width] - aimNum - 1])
								.append("_").append(aimNum).toString();
						grid.setImageResource(getResources().getIdentifier(imageName,
								"mipmap", "com.zhengxiaoyao0716.digimon2048"));
					}
				}
		}

		@Override
		public boolean levelUpIsEnterNextLevel(int level, int score) {
			// TODO Auto-generated method stub
			digimons = Arrays.copyOf(digimons, level + 1);
			digimons[level] = digimons[level - 1] + 1;
			if (digimons[level] > 14) digimons[level] = 1;
			return true;
		}

		@Override
		public boolean gameOverIsReplay(int level, int score) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean saveFailedIsStillQuit() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void noChangeRespond() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void movedRespond() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mergedRespond() {
			// TODO Auto-generated method stub
			
		}
	};
}