// Agent sample_agent in project lwjglenv

/* Initial beliefs and rules */



/* Initial goals */

!init.

/* Plans */

+!init : true <- 
	//iam debe ser la primer accion que ejecute el agente.
	iam;
	!init_alt(8);
	!init_personal;
	!loop.

// alternativas disponibles
+!init_alt(Num) : Num = 0 <-
	nothing. 

+!init_alt(Num) : true <-
	+alt(Num-1);
	!init_alt(Num-1).

// Alternativa personal
+!init_personal: true <-
	.count(alt(_),N);
	.random(Alt);
	+personal_alt(Alt*8);
	+chosen_alt(Alt*8).

+!loop : true <- 
	//.print("looping");	
	.wait(1000);
	choose;
	!loop.
	
+social_alt(N): true <-
	-+chosen_alt(N).
