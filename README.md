# Java Ant-Colony-System-Framework (JACSF)
An implementation of Ant Colony System in Java

An *Ant Colony System* consists of a set of cooperating agents, called ants, that cooperate to find a good solution for optimization problems on graphs similar to the Travel Salesman Problem. (see *Dorigo, M., Gambardella, L.M., “Ant Colony System: A Cooperative Learning Approach to the Travel Salesman Problem”, TR/IRIDIA/1996-5, Université Libre de Bruxelle*)
Each single ant reflects a very trivial behavior: it simply goes from a node to another across an arc, but when all ants cooperate, like actual ants do in a real colony, the whole system reveals an intelligent behavior, as much as it is able to find a good solution for the TSP.

The framework is composed by three classes: 
-	**Ant** is an abstract class which implements the general behavior of an artificial Ant. It must be specialized, in the context of the specific problem, in order to behave as a concrete Ant. As can be seen in the Picture 1, the abstract methods which must be implemented in a concrete derived class are stateTransitionRule, localUpdatingRule, compare and end which correspond to the rules mentioned above. The method start launches a thread in which the ant works. This means that each ant works in its own thread in parallel with all others giving to ACS the parallelism shown by actual ant colonies. The other class methods and members, such as init, m_nCurNode, etc., are used internally to perform the activity of the ant. The algorithm which drives the artificial ant during its trip is shown in Picture 2.  Picture 10 in appendix A proposes a possible implementation in Java.

-	**AntColony** is an abstract class implementing the general behavior of an Ant Colony System. It must be specialized, in the context of the specific problem, in order to implement a concrete Ant Colony. The abstract methods which must be implemented in a concrete derived class are createAnts and globalUpdatingRule. The first one creates the instances the class Ant which walk on the edges of the graph. The second one matches with the corresponding rule mentioned above. The other class methods and members, such as start, iteration, m_ants, etc., are used internally to perform and track the activity of the whole ant colony. The algorithm which directs the colony is shown in Picture 3. Picture 9 in appendix A illustrates a possible implementation in Java.

-	**AntGraph**  is a class that implements the features of a graph specific for Ant Colony Systems. It supplies the values for the *cost measure δ(r, s)* and for the *desirability measure τ(r, s)* and gives a method to update the value *τ(r, s)*. Because the ants actually work in parallel they access asynchronously and unpredictably to the graph. To avoid that an ant tries to read from the graph while another are writing on, the accesses to AntGraph objects are synchronized by using the synchronization mechanism supplied by Java language.

## JACSF applied on Traveling Salesman Problem

To apply JACSF to the Traveling Salesman Problem I took the instance of ACS that Dorigo used in his work mentioned above and I implemented it using JACSF. 

I designed two concrete classes **Ant4TSP** and **AntColony4TSP**, derived respectively from the classes **Ant** and **AntColony**, which implement the transition and updating rules described above.

## JACSF applied to Multicasting Problem in a network (Stainer Problem on a Network)
Multicasting in a network is the targeting of a single data packet to a selected set of receivers in the network. It is in opposition to traditional communication modes unicast and broadcast that are, respectively, one-to-one and one-to-all communications. 
Considering the needs of efficiency, expressed in term of low delay during transmissions and limited bandwidth, solving the Multicasting Problem means generating a tree of nodes from the sender to the given set of clients which has the minimal cost in term of time and bandwidth.
Because the Multicasting Problem in networks can be seen as an instance of Steiner Problem on a network, I used JACSF to solve it.

I designed two classes **Ant4S**P and **AntColony4SP**, derived respectively from the **Ant4TSP** and **AntColony4TSP**. 
I opted to derive from the classes designed for TSP because there are very few changes to do respect to the base classes. 
The class **AntColony4SP** differs from **AntColony4TSP** in the method *globalUpdatingRule* and in the method *createAnts* in which it creates the ants. 
The class **Ant4TSP** differs from **Ant4TSP** only in the method *end* because the *End of Activity Rule* must return true when all nodes has been covered



