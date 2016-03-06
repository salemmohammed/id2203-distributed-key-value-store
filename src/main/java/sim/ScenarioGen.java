package sim;
import preload.DatastoreFactory;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.simulator.adaptor.Operation2;
import se.sics.kompics.simulator.adaptor.Operation3;
import se.sics.kompics.simulator.adaptor.distributions.ConstantDistribution;
import se.sics.kompics.simulator.events.system.KillNodeEvent;
import system.data.KVEntry;
import system.client.ClientParent;
import system.client.event.CASRequest;
import system.client.event.CommandMessage;
import system.client.event.GETRequest;
import system.client.event.PUTRequest;
import system.data.Bound;
import system.data.ReplicationGroup;
import system.node.NodeParent;
import system.network.TAddress;

import se.sics.kompics.Init;
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation1;
import se.sics.kompics.simulator.adaptor.distributions.extra.BasicIntSequentialDistribution;
import se.sics.kompics.simulator.events.system.StartNodeEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

public class ScenarioGen {

    static DatastoreFactory datastoreFactory = new DatastoreFactory();

    static Operation1 nodeGroup = new Operation1<StartNodeEvent, Integer>() {
        @Override
        public StartNodeEvent generate(final Integer self) {
            return new StartNodeEvent() {
                TAddress selfAdr;
                ArrayList<ReplicationGroup> replicationGroups = new ArrayList<>();
                HashMap <Integer, KVEntry> store = new HashMap<>();
                ReplicationGroup replicationGroup;
                TAddress leader;
                Bound bound;

                int port = self + 10000;
                {
                    try {
                        selfAdr = new TAddress(InetAddress.getByName("192.193.0." + self), port);
                        store = datastoreFactory.getHashMapByIpSuffix(self);
                        replicationGroup = datastoreFactory.getReplicationGroupByIpSuffix(self);
                        replicationGroups = datastoreFactory.getReplicationGroups();
                        leader = datastoreFactory.getReplicationGroupLeader(self);
                        bound = datastoreFactory.getBoundsByIpSuffix(self);

                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return NodeParent.class;
                }

                @Override
                public Init getComponentInit() {
                    return new NodeParent.Init(selfAdr, replicationGroups, store, replicationGroup, leader);
                }
            };
        }
    };

    static Operation1 allLeaderGroup = new Operation1<StartNodeEvent, Integer>() {
        @Override
        public StartNodeEvent generate(final Integer self) {
            return new StartNodeEvent() {
                TAddress selfAdr;
                ArrayList<ReplicationGroup> replicationGroups;
                HashMap <Integer, KVEntry> store = new HashMap<>();
                ReplicationGroup replicationGroup;
                TAddress leader;
                Bound bound;
                int port = self + 10000;
                {
                    try {
                        selfAdr = new TAddress(InetAddress.getByName("192.193.0." + self), port);
                        store = datastoreFactory.getHashMapByIpSuffix(self);
                        replicationGroup = datastoreFactory.getReplicationGroupByIpSuffix(self);
                        replicationGroups = datastoreFactory.getReplicationGroups();
                        leader = selfAdr;
                        bound = datastoreFactory.getBoundsByIpSuffix(self);

                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return NodeParent.class;
                }

                @Override
                public Init getComponentInit() {
                    return new NodeParent.Init(selfAdr, replicationGroups, store, replicationGroup, leader);
                }
            };
        }
    };

    static Operation2 startGETClient = new Operation2<StartNodeEvent, Integer, Integer>() {
        @Override
        public StartNodeEvent generate(final Integer self, final Integer target) {
            return new StartNodeEvent() {
                TAddress selfAdr;
                ArrayList<TAddress> nodes;
                CommandMessage command;

                int port = self + 10000;
                int targetPort = target + 10000;

                {
                    try {
                        //Client is started on ip ending with 100
                        selfAdr = new TAddress(InetAddress.getByName("192.193.0." + self), port);
                        //Nodes to send GETRequest to
                        ArrayList<TAddress> nodes = new ArrayList<>();
                        nodes.add(new TAddress(InetAddress.getByName("192.193.0." + target), targetPort));
                        this.nodes = nodes;
                        KVEntry kv = new KVEntry(5,-1,0);
                        this.command = new GETRequest(selfAdr, nodes.get(0), kv, selfAdr.getId(), 0);
                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class<? extends ComponentDefinition> getComponentDefinition() {
                    return ClientParent.class;
                }

                @Override
                public Init getComponentInit() {
                    return new ClientParent.Init(selfAdr, command);
                }
            };
        }
    };

    static Operation3 startPUTClient = new Operation3<StartNodeEvent, Integer, Integer, Integer>() {
        @Override
        public StartNodeEvent generate(final Integer val, final Integer self, final Integer target) {
            return new StartNodeEvent() {
                TAddress selfAdr;
                ArrayList<TAddress> nodes;
                CommandMessage command;


                int port = self + 10000;
                int targetPort = target + 10000;

                {
                    try {
                        //Client is started on ip ending with 100
                        selfAdr = new TAddress(InetAddress.getByName("192.193.0." + self), port);
                        //Nodes to send GETRequest to
                        ArrayList<TAddress> nodes = new ArrayList<>();
                        nodes.add(new TAddress(InetAddress.getByName("192.193.0." + target), targetPort));
                        KVEntry kv = new KVEntry(5,val,0);
                        this.command = new PUTRequest(selfAdr, nodes.get(0), kv, selfAdr.getId(), 0);
                        this.nodes = nodes;
                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class<? extends ComponentDefinition> getComponentDefinition() {
                    return ClientParent.class;
                }

                @Override
                public Init getComponentInit() {
                    return new ClientParent.Init(selfAdr, command);
                }
            };
        }
    };

    static Operation3 startCASClient = new Operation3<StartNodeEvent, Integer, Integer, Integer>() {
        @Override
        public StartNodeEvent generate(final Integer val, final Integer self, final Integer target) {
            return new StartNodeEvent() {
                TAddress selfAdr;
                ArrayList<TAddress> nodes;
                CommandMessage command;


                int port = self + 10000;
                int targetPort = target + 10000;

                {
                    try {
                        //Client is started on ip ending with 100
                        selfAdr = new TAddress(InetAddress.getByName("192.193.0." + self), port);
                        //Nodes to send GETRequest to
                        ArrayList<TAddress> nodes = new ArrayList<>();
                        nodes.add(new TAddress(InetAddress.getByName("192.193.0." + target), targetPort));
                        KVEntry kv = new KVEntry(5,val,0);
                        this.command = new CASRequest(selfAdr, nodes.get(0), kv, 30, selfAdr.getId(), 0);
                        this.nodes = nodes;
                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class<? extends ComponentDefinition> getComponentDefinition() {
                    return ClientParent.class;
                }

                @Override
                public Init getComponentInit() {
                    return new ClientParent.Init(selfAdr, command);
                }
            };
        }
    };



    static Operation1 killNode = new Operation1<KillNodeEvent, Integer>() {
        @Override
        public KillNodeEvent generate(final Integer self) {
            return new KillNodeEvent() {
                TAddress selfAdr;
                int port = self + 10000;

                {
                    try {
                        selfAdr = new TAddress(InetAddress.getByName("192.193.0." + self), port);
                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }
            };
        }
    };


    /*
    -- Simulation scenario to test Eventually-Perfect-Failure-Detector component --
    Properties:
    EPFD1: Strong completeness: Eventually, every process that crashes is permanently suspected by every correct process.
    EPFD2: Eventual strong accuracy: Eventually, no correct process is suspected by any correct process.

    SCENARIO:
    1. The scenario first creates the three replication groups with three nodes in each group.
    2. Kill the node with the last octet of 1.
    3. Kill the node with the last octet of 2.
    4. Printouts show that as soon as a process is killed, it is suspected by other correct nodes which satisfies
    EPFD1. Removing comment in class EventuallyPerfectFailureDetector makes all nodes to suspected all other nodes
    by default. By seeing that all correct nodes are removed from all correct nodes suspect sets, we can verify that
    EPFD2 is satisfied.
     */
    public static SimulationScenario testEventualPerfectFailureDetectorProperties() {
        SimulationScenario testFailureDetector = new SimulationScenario() {
            {

                //Start three nodes holding even values
                StochasticProcess nodeGroupProcess = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(3, nodeGroup, new BasicIntSequentialDistribution(1));
                    }
                };

                StochasticProcess killNode1 = new StochasticProcess() {
                    {
                        raise(1, killNode, new BasicIntSequentialDistribution(1));
                    }
                };

                StochasticProcess killNode2 = new StochasticProcess() {
                    {
                        raise(1, killNode, new BasicIntSequentialDistribution(2));
                    }
                };

                nodeGroupProcess.start();
                killNode1.startAfterTerminationOf(200, nodeGroupProcess);
                killNode2.startAfterTerminationOf(200, killNode1);
                terminateAfterTerminationOf(100, killNode2);
            }
        };

        return testFailureDetector;
    }


    /*
    -- Simulation scenario to test the Abortable-Sequence-Consensus component --
    Properties:
    Validity: If process p decides v then v is a sequence of proposed commands without duplicates
    Uniform Agreement: If process p decides u and process q decides v then one is a prefix of the other
    Integrity: If process p decides u and later decides v then u is a prefix of v
    Termination: If command C is proposed then eventually every correct process decides a sequence containing C


    SCENARIO:
    1. The scenario first creates the three replication groups with three nodes in each group.
    2. One client sends a PUT request, another sends a CAS request and a third sends a GET request.
    3. Outputs verifies that we have only one leader doing PROPOSE ,even if
       requests are sent to different nodes, in a replication group.
       Output satisfies the above mentioned ASC properties.
    */
    public static SimulationScenario testAbortableSequenceConsensusOneLeader() {

        SimulationScenario testAbortableSequenceConsensus = new SimulationScenario() {
            {
                StochasticProcess nodeGroupProcess = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(9, nodeGroup, new BasicIntSequentialDistribution(1));
                    }
                };

                StochasticProcess getClient = new StochasticProcess() {
                    {
                        raise(1, startGETClient, new BasicIntSequentialDistribution(30), new ConstantDistribution<Integer>(Integer.class, 3));
                    }
                };

                StochasticProcess putClient = new StochasticProcess() {
                    {
                        raise(1, startPUTClient, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(10), new BasicIntSequentialDistribution(1));
                    }
                };

                StochasticProcess casClient = new StochasticProcess() {
                    {
                        raise(1, startCASClient, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(11), new BasicIntSequentialDistribution(2));
                    }
                };

                nodeGroupProcess.start();
                putClient.start();
                casClient.startAtSameTimeWith(putClient);
                getClient.startAtSameTimeWith(putClient);
            }
        };

        return testAbortableSequenceConsensus;
    }


    /*
    -- Simulation scenario to test the Abortable-Sequence-Consensus component --
    Properties:
    Validity: If process p decides v then v is a sequence of proposed commands without duplicates
    Uniform Agreement: If process p decides u and process q decides v then one is a prefix of the other
    Integrity: If process p decides u and later decides v then u is a prefix of v
    Termination: If command C is proposed then eventually every correct process decides a sequence containing C


    SCENARIO:
    1. The scenario first creates the three replication groups with three nodes in each group.
    2. One client sends a PUT request, another sends a CAS request and a third sends a GET request.
    3. Outputs verifies that we have multiple leaders doing PROPOSE in a replication group.
       Two leaders act as conflicting proposers as expected and ASC still decides on a sequence
       of commands in total order. Output satisfies the above mentioned ASC properties.
    */
    public static SimulationScenario testAbortableSequenceConsensusAllLeader() {


        SimulationScenario allOperationAllLeader = new SimulationScenario() {
            {
                StochasticProcess nodeGroupProcess = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(9, allLeaderGroup, new BasicIntSequentialDistribution(1));
                    }
                };

                StochasticProcess getClient = new StochasticProcess() {
                    {
                        raise(1, startGETClient, new BasicIntSequentialDistribution(12), new ConstantDistribution<Integer>(Integer.class, 3));
                    }
                };

                StochasticProcess putClient = new StochasticProcess() {
                    {
                        raise(1, startPUTClient, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(10), new ConstantDistribution<Integer>(Integer.class, 1));

                    }
                };

                StochasticProcess casClient = new StochasticProcess() {
                    {
                        raise(1, startCASClient, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(11), new ConstantDistribution<Integer>(Integer.class, 2));
                    }
                };

                nodeGroupProcess.start();
                putClient.start();
                casClient.startAtSameTimeWith(putClient);
                getClient.startAtSameTimeWith(putClient);

            }
        };
        return allOperationAllLeader;
    }

    /*
    -- Simulation scenario to test the Abortable-Sequence-Consensus component --
    Properties:
    Validity: If process p decides v then v is a sequence of proposed commands without duplicates
    Uniform Agreement: If process p decides u and process q decides v then one is a prefix of the other
    Integrity: If process p decides u and later decides v then u is a prefix of v
    Termination: If command C is proposed then eventually every correct process decides a sequence containing C


    SCENARIO:
    1. The scenario first creates the three replication groups with three nodes in each group.
    2. One client send a GET request, another client send a PUT request and one client send one CAS requests,
       all processes are started at the same time. Requests from clients are sent to replication groups not responsible for the key-value pairs
    3. Output verifies that leader election works even if clients sends requests to nodes outside the responsible
       replication group. ASC properties still holds.
    */
    public static SimulationScenario testAbortableSequenceConsensusNoDuplicates() {
        SimulationScenario testAbortableSequenceConsensus = new SimulationScenario() {
            {
                StochasticProcess nodeGroupProcess = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(9, nodeGroup, new BasicIntSequentialDistribution(1));
                    }
                };

                StochasticProcess getClient = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(100));
                        raise(1, startGETClient, new BasicIntSequentialDistribution(30), new ConstantDistribution<Integer>(Integer.class, 7));
                    }
                };

                StochasticProcess putClient = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(100));
                        raise(1, startPUTClient, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(10), new BasicIntSequentialDistribution(5));
                    }
                };

                StochasticProcess casClient = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(100));
                        raise(1, startCASClient, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(15), new BasicIntSequentialDistribution(9));
                    }
                };

                nodeGroupProcess.start();
                putClient.start();
                casClient.startAtSameTimeWith(putClient);
                getClient.startAtSameTimeWith(putClient);
            }
        };

        return testAbortableSequenceConsensus;
    }




    /*
    -- Simulation scenario to test the Abortable-Sequence-Consensus component --
    Properties:
    Validity: If process p decides v then v is a sequence of proposed commands without duplicates
    Uniform Agreement: If process p decides u and process q decides v then one is a prefix of the other
    Integrity: If process p decides u and later decides v then u is a prefix of v
    Termination: If command C is proposed then eventually every correct process decides a sequence containing C


    SCENARIO:
    1. The scenario first creates the three replication groups with three nodes in each group.
    2. Two clients send two GET requests, two clients send two PUT requests and two clients send two CAS requests,
       all processes are started at the same time.
    3. Output verifies that leader election works when a leader crashes
       and that the ASC properties still holds.
    */
    public static SimulationScenario testAbortableSequenceConsensusLeaderElection() {
        SimulationScenario testAbortableSequenceConsensus = new SimulationScenario() {
            {
                StochasticProcess nodeGroupProcess = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(9, nodeGroup, new BasicIntSequentialDistribution(1));
                    }
                };

                StochasticProcess getClient = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(100));
                        raise(2, startGETClient, new BasicIntSequentialDistribution(30), new ConstantDistribution<Integer>(Integer.class, 3));
                    }
                };

                StochasticProcess putClient = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(100));
                        raise(2, startPUTClient, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(10), new BasicIntSequentialDistribution(1));
                    }
                };

                StochasticProcess casClient = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(100));
                        raise(2, startCASClient, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(15), new BasicIntSequentialDistribution(2));
                    }
                };


                StochasticProcess killNodeLeader = new StochasticProcess() {
                    {
                        raise(1, killNode, new BasicIntSequentialDistribution(1));
                    }
                };

                nodeGroupProcess.start();
                putClient.start();
                casClient.startAtSameTimeWith(putClient);
                getClient.startAtSameTimeWith(putClient);
                killNodeLeader.startAtSameTimeWith(putClient);
            }
        };

        return testAbortableSequenceConsensus;
    }


    /*
    -- Simulation scenario to test the Abortable-Sequence-Consensus component --
    Properties:
    Validity: If process p decides v then v is a sequence of proposed commands without duplicates
    Uniform Agreement: If process p decides u and process q decides v then one is a prefix of the other
    Integrity: If process p decides u and later decides v then u is a prefix of v
    Termination: If command C is proposed then eventually every correct process decides a sequence containing C


    SCENARIO:
    1. The scenario first creates the three replication groups with three nodes in each group.
    2. One client sends a PUT request, another sends a CAS request and a third sends a GET request.
    3. Two nodes are killed in the targeted replication group.
    4. Three clients send one request each, same as in step 2.
    5. Output indicates that requests from first round are decided whereas requests from the second round
       cannot be decided since there is no majority quorum in the responsible replication group. ASC properties still hold.
    */
    public static SimulationScenario testAbortableSequenceConsensusQuorumMajority() {

        SimulationScenario testAbortableSequenceConsensus = new SimulationScenario() {
            {
                StochasticProcess nodeGroupProcess = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(9, nodeGroup, new BasicIntSequentialDistribution(1));
                    }
                };

                StochasticProcess getClient = new StochasticProcess() {
                    {
                        raise(1, startGETClient, new BasicIntSequentialDistribution(30), new ConstantDistribution<Integer>(Integer.class, 3));
                    }
                };

                StochasticProcess putClient = new StochasticProcess() {
                    {
                        raise(1, startPUTClient, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(10), new BasicIntSequentialDistribution(1));
                    }
                };

                StochasticProcess casClient = new StochasticProcess() {
                    {
                        raise(1, startCASClient, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(11), new BasicIntSequentialDistribution(2));
                    }
                };

                 StochasticProcess getClient2 = new StochasticProcess() {
                    {
                        raise(1, startGETClient, new BasicIntSequentialDistribution(40), new ConstantDistribution<Integer>(Integer.class, 3));
                    }
                };

                StochasticProcess putClient2 = new StochasticProcess() {
                    {
                        raise(1, startPUTClient, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(15), new BasicIntSequentialDistribution(1));
                    }
                };

                StochasticProcess casClient2 = new StochasticProcess() {
                    {
                        raise(1, startCASClient, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(16), new BasicIntSequentialDistribution(2));
                    }
                };

                StochasticProcess kill1 = new StochasticProcess() {
                    {
                        raise(1, killNode, new BasicIntSequentialDistribution(2));
                    }
                };

                StochasticProcess kill2 = new StochasticProcess() {
                    {
                        raise(1, killNode, new BasicIntSequentialDistribution(3));
                    }
                };

                nodeGroupProcess.start();
                putClient.start();
                casClient.startAfterTerminationOf(100, putClient);
                getClient.startAfterTerminationOf(100,casClient);

                kill1.startAfterTerminationOf(100, getClient);
                kill2.startAfterTerminationOf(100, kill1);

                putClient2.startAfterStartOf(100, kill2);
                casClient2.startAfterTerminationOf(100, putClient2);
                getClient2.startAfterTerminationOf(100,casClient2);


            }
        };

        return testAbortableSequenceConsensus;
    }


    /*
    -- Simulation scenario to test Replicated-State-Machine component --
    Properties:
    RSM1: Agreement: All correct processes obtain the same sequence of outputs.
    RSM2: Termination: If a correct process executes a command, then the command eventually produces an output.

    SCENARIO:
    1. The scenario first creates the three replication groups with three nodes in each group.
    2. Two clients each sending a PUT request, two clients that sends one GET request each and two clients sends CAS requests,
       all processes are started at the same time.
    3. Printouts show that all processes obtain the same sequence of outputs, satisfying the property RSM1. All executed
       commands by correct processes produces an output, ensuring that property RSM2 is satisfied. Printouts also verify linearizability of
       the RSM as the commands are decided in total order which is provided by the ASC abstraction.
    */
    public static SimulationScenario testReplicatedStateMachineProperties() {
        SimulationScenario testReplicatedStateMachine = new SimulationScenario() {
            {

                //Start three nodes holding even values
                StochasticProcess nodeGroupProcess = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(9, nodeGroup, new BasicIntSequentialDistribution(1));
                    }
                };

                StochasticProcess getClient1 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(100));
                        raise(2, startGETClient, new BasicIntSequentialDistribution(30), new ConstantDistribution<Integer>(Integer.class, 1));
                    }
                };

                //Start client that gets a value
                StochasticProcess putClient1 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(100));
                        raise(2, startPUTClient, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(10),new ConstantDistribution<Integer>(Integer.class, 2));
                    }
                };

                StochasticProcess casClient = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(100));
                        raise(2, startCASClient, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(11), new ConstantDistribution<Integer>(Integer.class, 3));
                    }
                };

                nodeGroupProcess.start();
                putClient1.start();
                getClient1.startAtSameTimeWith(putClient1);
                casClient.startAtSameTimeWith(putClient1);
            }
        };

        return testReplicatedStateMachine;
    }



    /*
    -- Simulation scenario to test Perfect-Point-To-Point-Link --
    Properties:
    PL1: Reliable delivery: If a correct process p sends a message m to a correct process q, then q eventually delivers m.
    PL2: No duplication: No message is delivered by a process more than once.
    PL3: No creation: If some process q delivers a message m with sender p, then m was previously sent to q by process p.

    TCP/IP guarantees all properties of Perfect-Point-To-Point-Link.

    SCENARIO:
    1. The scenario first creates the three replication groups with three nodes in each group.
    2. A client sends a get request to leader in replication group.
    3. Printouts show that all correct processes deliver, satisfying PL1. The message is not duplicated, satisfying PL2.
    All delivered messages in the scenario was previously sent by a process.
 */
    public static SimulationScenario testPerfectPointToPointLinkProperties() {
        SimulationScenario testPerfectLink = new SimulationScenario() {
            {

                //Start three nodes holding even values
                StochasticProcess nodeGroupProcess = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(9, nodeGroup, new BasicIntSequentialDistribution(1));
                    }
                };

                StochasticProcess getClient1 = new StochasticProcess() {
                    {
                        raise(1, startGETClient, new BasicIntSequentialDistribution(30), new ConstantDistribution<Integer>(Integer.class, 1));
                    }
                };


                nodeGroupProcess.start();
                getClient1.startAfterTerminationOf(100, nodeGroupProcess);
                terminateAfterTerminationOf(100, getClient1);
            }
        };

        return testPerfectLink;
    }





    /*
    -- Simulation scenario to test Monarchical-Eventual-Leader-Detector component --
    Properties:
    ELD1: Eventual accuracy: There is a time after which every correct process trusts some correct process.
    ELD2: Eventual agreement: There is a time after which no two correct processes trust different correct processes.

    The monarchical rank is decided by the last octet X, of the ipv4 address (-.-.-.X). A lower number is considered
    higher rank.

    SCENARIO:
    1. The scenario first creates the three replication groups with three nodes in each group.
    2. Kill the node with the last octet of 1.
    3. Kill the node with the last octet of 2.
    4. Restart the node with last octet of 2.
    5. Restart the node with last octet of 1.
    6. Printouts show that ELD1 and ELD2 properties are satisfied since all correct nodes consider the node with lowest
    number on the last octet to be the leader even if they are restored. Which also satisfies the properties of monarchical leader election.
     */
    public static SimulationScenario testMonarchicalEventualLeaderDetectorProperties() {
        SimulationScenario testLeaderElection = new SimulationScenario() {
            {

                //Start three nodes holding even values
                StochasticProcess nodeGroupProcess = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(3, nodeGroup, new BasicIntSequentialDistribution(1));
                    }
                };

                //Kill node in even group with ip ending with 2
                StochasticProcess killNode2 = new StochasticProcess() {
                    {
                        raise(1, killNode, new BasicIntSequentialDistribution(2));
                    }
                };


                //Restart node in even group with ip ending with 2
                StochasticProcess restartNode2 = new StochasticProcess() {
                    {
                        raise(1, nodeGroup, new BasicIntSequentialDistribution(2));
                    }
                };

                StochasticProcess killNode1 = new StochasticProcess() {
                    {
                        raise(1, killNode, new BasicIntSequentialDistribution(1));
                    }
                };


                //Restart node in even group with ip ending with 2
                StochasticProcess restartNode1 = new StochasticProcess() {
                    {
                        raise(1, nodeGroup, new BasicIntSequentialDistribution(1));
                    }
                };

                nodeGroupProcess.start();
                killNode1.startAfterTerminationOf(200, nodeGroupProcess);
                killNode2.startAfterTerminationOf(200, killNode1);
                restartNode2.startAfterTerminationOf(200, killNode2);
                restartNode1.startAfterTerminationOf(200, restartNode2);
                terminateAfterTerminationOf(100, restartNode1);
            }
        };

        return testLeaderElection;
    }


    /*
    -- Simulation scenario to test Best-Effort Broadcast component --
    Properties:
    BEB1: Validity: If a correct process broadcasts a message m, then every correct process eventually delivers m.
    BEB2: No duplication: No message is delivered more than once.
    BEB3: No creation: If a process delivers a message m with sender s, then m was previously broadcast by process s.

    SCENARIO:
    1. The scenario first creates the three replication groups with three nodes in each group.
    2. After the nodes have been started a client sends a get request to a node in the wrong replication group.
    3. The receiving node forwards the get request to the responsible replication group using Best-Effort Broadcast.
    4. In the printouts we can see that each node in the group receives the broadcast according to BEB1, BEB2 and BEB3.
    5. The scenario then continues by killing a node in the responsible replication group.
    6. After having killed the node a second client sends a get request which is received by the two correct processes.
     */
    public static SimulationScenario testBestEffortBroadcastProperties() {
        SimulationScenario bestEffortBroadcastScenario = new SimulationScenario() {
            {

                //Start three nodes holding even values
                StochasticProcess nodeGroupProcess = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(9, nodeGroup, new BasicIntSequentialDistribution(1));
                    }
                };

                StochasticProcess killNode2 = new StochasticProcess() {
                    {
                        raise(1, killNode, new BasicIntSequentialDistribution(2));
                    }
                };

                StochasticProcess getClient1 = new StochasticProcess() {
                    {
                        raise(1, startGETClient, new BasicIntSequentialDistribution(30), new ConstantDistribution<Integer>(Integer.class, 8));
                    }
                };

                StochasticProcess getClient2 = new StochasticProcess() {
                    {
                        raise(1, startGETClient, new BasicIntSequentialDistribution(31), new ConstantDistribution<Integer>(Integer.class, 8));
                    }
                };

                nodeGroupProcess.start();
                getClient1.startAfterTerminationOf(100, nodeGroupProcess);
                killNode2.startAfterTerminationOf(100, getClient1);
                getClient2.startAfterTerminationOf(100, killNode2);
                terminateAfterTerminationOf(100, getClient2);
            }
        };

        return bestEffortBroadcastScenario;
    }
}
