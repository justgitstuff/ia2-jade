# Introduction #

Used to send action orders to a higher class Agent


# Ussage #

First, create an instance, passing the sender Agent and the type of agent to send it to:

  * MovementSender m = new MovementSender(this, UtilsAgents.HARVESTER\_MANAGER\_AGENT);

Once you have decided what to do, send the order:

  * m.go(Direction.UP);
  * m.get(Direction.UP, Type.METAL);
  * m.put(Direction.DOWN,Type.METAL);

## TODO ##

Implement result check if necessary, for example

  * m.wasSuccesful();