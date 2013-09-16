// Agent sample_agent in project lwjglenv

/* Initial beliefs and rules */



/* Initial goals */

!init.

/* Plans */

+!init : true <- 
	//iam debe ser la primer accion que ejecute el agente.
	iam;
	search_neighbors;
	!loop.

+!loop : true <- 
	//.print("looping");	
	.wait(100);
	choose;
	!loop.
