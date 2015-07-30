package com.wcecil.common

import com.wcecil.actions.AbstractAction;
import com.wcecil.actions.core.ShuffleDiscardIntoDeck
import com.wcecil.beans.GameState;
import com.wcecil.beans.gameobjects.Card
import com.wcecil.beans.gameobjects.Player
import com.wcecil.core.GameController

class CardMovementHelper {
	static Card drawCard(Player p, GameState g, AbstractAction cause){
		Card result = null
		if(!p.deck){
			def shuffle = new ShuffleDiscardIntoDeck(targetPlayer:p, cause:cause);
			GameController.doAction(g, shuffle)
		}

		result
	}



	static boolean moveDiscardToDeckAndShuffle(Player p, GameState g, AbstractAction cause) {
		if(p.discard){
			p.deck.addAll(p.discard)
			p.discard.clear()
			Collections.shuffle(p.deck)
			return true
		}
		false
	}
}
