package com.wcecil.rules

import com.wcecil.beans.GameState;

abstract class Rule {
	abstract def doRule(GameState g);
}