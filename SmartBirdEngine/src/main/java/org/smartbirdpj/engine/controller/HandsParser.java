package org.smartbirdpj.engine.controller;

import java.util.ArrayList;
import java.util.List;

import org.smartbirdpj.mdl.MeldElement;
import org.smartbirdpj.mdl.enm.MeldEnum;
import org.smartbirdpj.mdl.enm.TileEnum;
import org.smartbirdpj.mdl.enm.WinningFormEnum;

public class HandsParser {

	public WinningHandsList parse(TileSet tileSet) {
		tileSet.sort();
		WinningHandsList winningHandsList= new WinningHandsList();
		// kokushi - musou
		WinningHands hands = parseKokushiMusou(tileSet);
		if(hands != null){
			winningHandsList.add(hands);
			return winningHandsList;
		}		
		// chi-toitsu
		hands = parse7Toitsu(tileSet);
		if(hands != null){
			winningHandsList.add(hands);
		}

		winningHandsList.addAll(parseBasic(tileSet).getList());
		
		for(WinningHands h : winningHandsList.getList()){
			if(tileSet.isRichi()) h.setRichi();
			if(tileSet.isTumo()) h.setTumo();
			if(tileSet.isStolen()) h.setStolen();
		}
		return winningHandsList;

	}
	private List<Integer> searchHeadPos(TileSet tileSet) {
		List<TileEnum> tiles = tileSet.getTiles();
		List<Integer> heads = new ArrayList<Integer>();
		for (int i = 0; i < tiles.size() - 1; i++) {
			TileEnum h1 = tiles.get(i);
			TileEnum h2 = tiles.get(i + 1);

			if (h1.equals(h2)) {
				heads.add(i);
			}
		}
		return heads;
	}

	private MeldNode makeMeldTree(TileSet tileSet,int headPos) {
		List<TileEnum> tiles = tileSet.getTiles();
		List<TileEnum> list = new ArrayList<TileEnum>(tiles);
		MeldNode mTree = new MeldNode();
		makeMeldHead(tileSet,list, mTree, headPos);

		boolean rv = makeMeldOtherThanHead(tileSet,list, mTree);

		return rv ? mTree : null;
	}

	private void makeMeldHead(TileSet tileSet,List<TileEnum> list, MeldNode mtree,
			int headPos) {
		TileEnum a1 = list.remove(headPos + 1);
		TileEnum a2 = list.remove(headPos);

		if (tileSet.isWinningTile(a1) || tileSet.isWinningTile(a1)) {
			mtree.setWinningFormEnum(WinningFormEnum.TANKI, tileSet.isTumo());

		}
		MeldElement yakuHead = new MeldElement(MeldEnum.EYES, a1,
				a2);
		mtree.setMeld(yakuHead);

	}

	private void makeMeldFromHuro(MeldNode mTree, List<MeldElement> huroList) {
		if (huroList.size() == 0) {
			return ;
		}
		MeldElement meld = huroList.remove(0);
		MeldNode child = new MeldNode();
		child.setMeld(meld);

		mTree.addChild(child);

		makeMeldFromHuro(child, huroList);
	}
	//TODO change return type to void and throw exception when returning false so far.
	private boolean makeMeldOtherThanHead(TileSet tileSet,List<TileEnum> list, MeldNode parent) {

		if (list.size() == 0) {
			List<MeldElement> huroList = new ArrayList<MeldElement>(tileSet.getHuros());
 			makeMeldFromHuro(parent, huroList);
 			return true;
		}
		boolean rv = false;
		TileEnum h1 = list.remove(0);

		// kotsu
		if (list.contains(h1)) {
			int indexOfH1 = list.indexOf(h1);
			TileEnum h2 = list.remove(indexOfH1);
			if (list.contains(h1)) {
				TileEnum h3 = list.remove(list.indexOf(h1));
				// Pong
				MeldElement r = null;
				r = new MeldElement(MeldEnum.WALL_PONG, h1, h2, h3);

				MeldNode child = new MeldNode();
				child.setMeld(r);

				if (tileSet.isWinningTile(h1) || tileSet.isWinningTile(h2) || tileSet.isWinningTile(h3)) {

					child.setWinningFormEnum(WinningFormEnum.SYANPON, tileSet.isTumo());
				}

				rv = makeMeldOtherThanHead(tileSet,list, child);
				if (rv) {
					parent.addChild(child);
				}

			} else {
				list.add(indexOfH1, h2);
			}

		}

		// CHOW
		TileEnum h2 = h1.next();
		TileEnum h3 = h2 == null ? null : h2.next();
		
		if (!h1.isWindAndDragon() && list.contains(h2) && list.contains(h3)) {
			// CHOW
			h2 = list.remove(list.indexOf(h2));
			h3 = list.remove(list.indexOf(h3));

			MeldElement r = null;
			r = new MeldElement(MeldEnum.WALL_CHOW, h1, h2, h3);

			MeldNode child = new MeldNode();
			child.setMeld(r);


			if (tileSet.isWinningTile(h1)) {
				if (h1.equals(TileEnum.CHARACTOR7)
						|| h1.equals(TileEnum.BAMBOO7)
						|| h1.equals(TileEnum.CIRCLE7)){
					child.setWinningFormEnum(WinningFormEnum.PENTYAN, tileSet.isTumo());
				}
				else {
					child.setWinningFormEnum(WinningFormEnum.RYANMEN, tileSet.isTumo());
				}
			}
			if (tileSet.isWinningTile(h2)) {

				child.setWinningFormEnum(WinningFormEnum.KANTYAN, tileSet.isTumo());
			}
			if (tileSet.isWinningTile(h3)) {

				if (h3.equals(TileEnum.CHARACTOR3) || h3.equals(TileEnum.BAMBOO3)
						|| h3.equals(TileEnum.CIRCLE3)) {
					child.setWinningFormEnum(WinningFormEnum.PENTYAN, tileSet.isTumo());
				} else {
					child.setWinningFormEnum(WinningFormEnum.RYANMEN, tileSet.isTumo());
				}
			}
			rv = makeMeldOtherThanHead(tileSet,list, child);
			if (rv) {
				parent.addChild(child);
			}

		}

		return rv;
	}

	private WinningHandsBasic parse7Toitsu(TileSet tileSet) {
		List<TileEnum> tiles = tileSet.getTiles();
		if(tiles.size() != 14){
			return null;
		}
		TileEnum previous = null;
		WinningHandsBasic winningHandsBasic = new WinningHandsBasic();
		for (int i = 0; i < tiles.size(); i = i + 2) {
			TileEnum h1 = tiles.get(i);
			TileEnum h2 = tiles.get(i + 1);
			if (h1 != h2) {
				return null;
			}
			// 7 toitsu doesn't allow duplicated pair.
			if(h1.equals(previous)){
				return null;
			}
			previous = h1;
			MeldElement m = new MeldElement(MeldEnum.EYES, h1, h2);
			winningHandsBasic.add(m);
		}
		winningHandsBasic.set7Toitsu();
		return winningHandsBasic;
	}

	private WinningHandsBasic parseKokushiMusou(TileSet tileSet) {

		WinningHandsBasic winningHands = new WinningHandsBasic();
		TileEnum[] kokushiTileCandidates = { TileEnum.CHARACTOR1, TileEnum.CHARACTOR9,
				TileEnum.BAMBOO1, TileEnum.BAMBOO9, TileEnum.CIRCLE1, TileEnum.CIRCLE9,
				TileEnum.EAST, TileEnum.SOUTH, TileEnum.WEST, TileEnum.NORTH,
				TileEnum.WHITE, TileEnum.GREEN, TileEnum.RED };

		for (TileEnum h : kokushiTileCandidates) {
			if (tileSet.getTiles().contains(h)) {
				MeldElement meld = new MeldElement(MeldEnum.KOKUSHI,h);
				winningHands.add(meld);
			}else{
				return null;
			}
		}
		TileEnum winningTile = tileSet.getWinningTile();
		List<TileEnum> tiles = tileSet.getTiles();
		tiles.remove(winningTile);
		if(tileSet.getTiles().contains(winningTile)){
			winningHands.setWinningForm(WinningFormEnum.KOKUSHI13);
		}else{
			winningHands.setWinningForm(WinningFormEnum.KOKUSHI_TANKI);
		}

		if(tileSet.isTumo()){
			winningHands.setTumo();
		}
		if(tileSet.isRichi()){
			winningHands.setRichi();
		}
		if(tileSet.isStolen()){
			return null; // illegal status. kokushi can't be composed of any stolen tile.
		}

		winningHands.setKokushi();
		return winningHands;
	}
	private WinningHandsList parseBasic(TileSet tileSet){
		List<Integer> headPoses = searchHeadPos(tileSet);
		WinningHandsList winningHandsList = new WinningHandsList();
		for (Integer headPos : headPoses) {
			MeldNode meldTree = makeMeldTree(tileSet,headPos);

			if (meldTree != null) {

				// to List<Yaku>
				winningHandsList.addAll(meldTree.transformGraphToList());
			}
		}

		return winningHandsList;
	}
}
