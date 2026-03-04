# Hi :3

## this simple Paper plugin features some simple yet neat features:  

- six simple yet, again, neat challenges
- controlling the challenges via Paper's Brigadier System
- a somewhat modular code structure, allowing for pretty easy adding (and removing) of challenges

## the challenges are the following:  

- 'nocraftingtable' - disables all right click ations with crafting tables
- 'nofalldamage' - sets you to spectator mode when fall damage would be taken
- 'noarmor' - deletes all armor that is equipped
- 'threehearts' - changes hearts to 3 (6hp)
- 'wolfi' - adds a tamed wolf to the invoker of the challenge, that, if dead, sets the player into spectator mode
- 'blockdroprandomizer' - randomizes block drops, but stay consistent as long the challenge isn't disabled

## enabling/disabling challenges:  

you can enable any challenge by typing the following into the text prompt or into the server command prompt:  
`challenges enable <challenge name>`  
  
the same can be done for disabling, just switch 'enable' for 'disable':  
`challenges disable <challenge name>`  
  
replace \<challenge name\> with the name of the challenge you wish to either enable or disable:  
`challenges enable nocraftingtable`  
  
some challenges can only be enabled from within the minecraft text prompt, since those challenges need a player to revole around, and I have not yet come around to adding functionality for an optional argument to provide a players name/uuid.  
one example of this (which should also be the only occurrence) is the 'wolfi' challenge. you enable it just like you do any of other the challenges.