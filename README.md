# Programming Beer Tracker
Project for Sub-symbolic AI methods on NTNU (Norwegian University of Science and Technology)
The project is an implementation of Evolving Neural Networks for a
Minimally-Cognitive Agent

Project information:
You will use your evolutionary algorithm (EA) to evolve the weights and and other parameters
for a small artificial neural network (ANN) whose fitness will depend upon the performance of
an agent controlled by the ANN. This agent will do a minimally cognitive task similar to those
used in the field of artificial life (ALife) to illustrate the emergence of rudimentary intelligence.
Specifically, the agent will sense falling objects of various sizes and must evolve the ability to a)
intercept objects smaller than itself, but b) avoid objects of its own size or larger.
The basic testing environment is a modified version of a simple game used by Randal Beer in his
research on ANNs. The game consists of a tracker agent and objects falling down from the sky
in a grid-world.
The world is 30 units wide and 15 units high. A game lasts 600 timesteps in this world.
Objects spawn from the top of the world, and fall straight down with a speed of 1 unit per
timestep. The horizontal spawn position of the object should be random, as well as the size.
The size can be from 1 to 6 units wide. There is only one falling object in the world at the same
time. When an object hits the tracker or the bottom of the world, a new one appears on the top
of the world the next timestep.
The tracker should be 5 units wide, and can only move horizontally at the bottom of the world.
The tracker consists of 5 shadow sensors (one per unit) that can see directly above whether there
is an object there or not. So the tracker only knows what's above it, and can't see the whole
world. It can move up to 4 steps in either direction per timestep. You will need to figure out how
to use the outputs of the two motor neurons (more about this later) to determine the direction
and magnitude of each move.
A capture is when the whole falling object lands on the tracker. Avoidance is when no part
of the falling object lands on the tracker. Note that avoidance is more than failing to capture,
1
for instance would a falling object where only half of it lands on the tracker neither count as a
capture nor an avoidance.
The goal of the game is for the tracker to capture small objects (of size 1, 2, 3 and 4) and avoid
big objects (of size 5 and 6).