package com.wcecil.game.core

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service

import com.wcecil.beans.GameState
import com.wcecil.common.settings.Settings
import com.wcecil.data.objects.GameAudit
import com.wcecil.data.repositiories.AuditRepository;
import com.wcecil.game.actions.Action
import com.wcecil.game.rules.Rule
import com.wcecil.game.triggers.Trigger

class GameController {
	
	static def doAction(GameState g, Action a){
		def result = null
		if(a.isValid(g)){

			doTriggers( g,  a )

			result = a.doAction(g);

			def tic = g.ticCount.getAndIncrement()
			saveAudit(g, a)

			g.getRules().each{ Rule r ->
				r.doRule(g)
			}
		}else{
			throw new IllegalStateException("Game in illegal state for attempting $a on $g");
		}

		result
	}

	static void doTriggers(GameState g, Action a){
		g.triggers.each { Trigger t ->
			if(t.isTriggered(a)){
				t.doTrigger(g, a)
			}
		}
	}

	static void saveAudit(GameState g, Action a) {
		def tic = g.ticCount.get()
		if(a.audit){
			if(Settings.debug) println "$tic:${a.audit}"
			
			GameAudit ga = new GameAudit(gameId: g.getId(), playerId : a.getSourcePlayer()?.getUserId(), order: tic, message:a.audit )
			g.audit << ga
		}
	}

	public static Action buildAction(GameState g, String action) {
		Action a = null;

		a = new GameController().getClass().getClassLoader().loadClass("com.wcecil.game.actions.core.$action", true)?.newInstance()

		if(a==null){
			throw new IllegalStateException("Cannot Parse Action '$action'")
		}

		a.setSourcePlayer(g.getCurrentPlayer())

		return a;
	}
}
