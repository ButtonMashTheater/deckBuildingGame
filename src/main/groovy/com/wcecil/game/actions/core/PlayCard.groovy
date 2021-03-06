package com.wcecil.game.actions.core

import com.wcecil.beans.dto.GameState
import com.wcecil.beans.gameobjects.Card
import com.wcecil.beans.gameobjects.Player
import com.wcecil.common.annotations.UserAction
import com.wcecil.game.actions.Action
import com.wcecil.game.core.GameController
import com.wcecil.game.util.CardMovementHelper
import com.wcecil.websocket.messanger.MessangerService

@UserAction
class PlayCard extends Action {

	String audit = 'Unable to play card'

	def doAction(GameState g, GameController gc) {
		if(sourceCard.money){
			addMoney(sourcePlayer, sourceCard.money)
		}

		if(sourceCard.draw){
			drawCards(g, sourcePlayer, sourceCard.draw, gc)
		}
		
		CardMovementHelper.playCard(sourcePlayer, sourceCard)

		if(sourceCard.specialActionScript){
			sourceCard.specialAction(g, sourcePlayer, targetPlayer, gc)
		}

		audit = "Player ${sourcePlayer.id} played the card '${sourceCard.name}'"
	}



	boolean isValid(GameState g) {
		sourceCard!=null
	}

	def addMoney(Player player, Integer money){
		player.money+=money;
	}

	def drawCards(GameState g, Player player, Integer cards, GameController gc){
		(1..cards).each{
			def drawCard = new DrawCard(targetPlayer:sourcePlayer, cause:this)
			gc.doAction(g, drawCard)
		}
	}
	
	@Override
	public void sendNotification(GameState g, MessangerService messangerService) {
		messangerService.updateGame(g?.id, sourcePlayer?.userId, Action.getActionMessage(this));
	}
}
