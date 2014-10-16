package com.mygdx.game;

public class FSM {

		public String state;
		
		public FSM(){
			state = "Attack";
		}
		
		public void transitionToAttack(){
			state = "Attack";
		}
		
		public void transitionToWander(){
			state = "Wander";
		}
		
		public String getCurrentState(){
			return state;
		}
}
