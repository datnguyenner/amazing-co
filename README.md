# amazing-co

Problem: 
We in Amazing Co need to model how our company is structured so we can do awesome stuff.
We have a root node (only one) and several children nodes, each one with its own children as well. It's a tree-based structure. 

Something like

         root

        /    \

       a      b

       |

       c

We need two HTTP APIs that will serve the two basic operations:
1) Get all descendant nodes of a given node (the given node can be anyone in the tree structure).
2) Change the parent node of a given node (the given node can be anyone in the tree structure).
They need to answer quickly, even with tons of nodes. Also,we can't afford to lose this information, so some sort of persistence is required.
Each node should have the following info:
1) node identification
2) who is the parent node
3) who is the root node
4) the height of the node. In the above example, height(root) = 0 and height(a) == 1.


Solution:
amazing-co was implemented using Spring boot for HTTP APIS, Redis for persisting data, and docker to put it all together.

-clone repo
-cd amazing-co/
-docker-compose up -d

Get all descendant nodes of a given node: 
- http://localhost:8080/nodes/{node}/descendants where {node} is the name of the node.
- Will return an array of JSON objects of nodes which includes node information 1-4.
- Make sure the name of the node is correct or you will get a 404 
- Only the root node is available when Redis is first initialized 

Change the parent node of a given node: 
- http://localhost:8080/nodes/{node}/change-parent where {node} is the name of the child node 
- Body: {node} is type text where {node} is name of the new parent node
- A node can not change its parent to be one of its descendants. From the above ex. node "a" can't change its parent to "c" but can change to b
- If a child node does not exist then a new node will be created and added to the existing parent
- The new parent must exist in DB or you will get a 304 Not Modified.

Additional Info:
- All data are stored using key/value pair
- Unit test are added but will only work if redis server is running. For improvement I can mock redis in the test
- jars are added to this repo so no need to build
- I also provided a sample tree to quickly test persisted nodes http://localhost:8080/useTestData
 
