package org.sb.server;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.sb.mdl.cnst.GameConstants;

public class GamePointHolder {
	PrintStream ps = System.out;
	private Map<Integer,Integer> scoreMap = new HashMap<Integer, Integer>();
	
	private List<Map<Integer,Integer>> pointDetailList = new ArrayList<Map<Integer,Integer>>();
	
	public GamePointHolder(){
		for(int playerId = 0; playerId < GameConstants.PLAYER_NUM; playerId++){
			scoreMap.put(playerId, GameConstants.INIT_POINT);
		}
	}
	
	public void payPoint(int round,int point,int playerIdFrom,int playerIdTo){
		int currentPayerPoint = scoreMap.get(playerIdFrom) == null ? 0 : scoreMap.get(playerIdFrom);
		int currentReceiverPoint= scoreMap.get(playerIdTo) == null ? 0 : scoreMap.get(playerIdTo);
				
		
		int updatedPayerPoint = currentPayerPoint - point;
		int updatedReciverPoint = currentReceiverPoint + point;
		
		scoreMap.put(playerIdFrom,updatedPayerPoint);
		scoreMap.put(playerIdTo,updatedReciverPoint);
		
//		recodePointDetail(round, point, playerIdFrom, playerIdTo);
	}
//	private void recodePointDetail(int round,int point,int playerIdFrom,int playerIdTo){
//		Map<Integer,Integer> m = null;
//		if(pointDetailList.size() >= (round + 1)){
//			m = pointDetailList.get(round);
//
//		}else{
//			m = new HashMap<Integer,Integer>();
//			pointDetailList.add(round, m);
//
//		}
//	
//		
//		m.put(playerIdFrom, -1 * point);
//		m.put(playerIdTo, point);
//
//	}
	public void showScore(){
		int sum = 0;
		ps.println("=======Score Status at this round=======");
		for(int i = 0; i < GameConstants.PLAYER_NUM; i++){
			ps.println(scoreMap.get(i));
			sum += scoreMap.get(i);
		}
		ps.println("sum:" + sum);
		ps.println("=======Score Status at this round=======");
	}
	@Test
	public void test(){

		for(int round = 0; round < 16; round++){
			payPoint(round, 2000, 0, 1);
			payPoint(round, 2000, 0, 2);
			payPoint(round, 2000, 0, 3);

		}
		payPoint(16, 48000, 3, 0);
		for(int i = 0; i < GameConstants.PLAYER_NUM; i++){
			ps.println(scoreMap.get(i));
		}
		for(int i = 0 ; i < pointDetailList.size(); i++){
			ps.println("round :" + i);
			Map<Integer,Integer> map = pointDetailList.get(i);
			for(int key : map.keySet()){
				ps.println(key + ":" + map.get(key));
			}
		}
	}
}
