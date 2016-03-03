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
                        System.out.println(selfAdr + ": groupsize " + replicationGroups.size());
                        leader = datastoreFactory.getReplicationGroupLeader(self);
                        System.out.println(self + " my leader is " + leader);
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
                        KVEntry kv = new KVEntry(5,100,0);
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
                        KVEntry kv = new KVEntry(5,100,0);
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



    public static SimulationScenario testAllOperationsAllLeaders() {
        SimulationScenario allOperationAllLeader = new SimulationScenario() {
            {
                //Start three nodes holding even values
                StochasticProcess nodeGroupProcess = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(9, allLeaderGroup, new BasicIntSequentialDistribution(1));
                    }
                };

                //Start client that gets a value
                StochasticProcess putClient = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(100));
                        raise(1, startPUTClient, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(10), new BasicIntSequentialDistribution(3));
                    }
                };

                StochasticProcess casClient = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startCASClient, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(11), new BasicIntSequentialDistribution(2));
                    }
                };

                StochasticProcess getClient = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(100));
                        raise(1, startGETClient, new BasicIntSequentialDistribution(12), new BasicIntSequentialDistribution(1));
                    }
                };

                StochasticProcess getClient2 = new StochasticProcess() {
                {
                    eventInterArrivalTime(constant(100));
                    raise(3, startGETClient, new BasicIntSequentialDistribution(15), new ConstantDistribution<Integer>(Integer.class, 3));
                }
            };

                StochasticProcess getClient3 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(100));
                        raise(3, startGETClient, new BasicIntSequentialDistribution(30), new ConstantDistribution<Integer>(Integer.class, 2));
                    }
                };

                StochasticProcess killreplicationNode1 = new StochasticProcess() {
                    {
                        raise(1, killNode, new BasicIntSequentialDistribution(1));
                    }
                };

                /*
                StochasticProcess killreplicationNode3 = new StochasticProcess() {
                    {
                        raise(1, killNode, new BasicIntSequentialDistribution(3));
                    }
                };
                */

                nodeGroupProcess.start();
                putClient.start();
                getClient.startAfterTerminationOf(5000, putClient);
                casClient.startAfterTerminationOf(5000, getClient);
                //killreplicationNode3.startAfterTerminationOf(5000, casClient);
                getClient2.startAfterTerminationOf(2000, casClient);
                killreplicationNode1.startAfterTerminationOf(2000, getClient2);
                getClient3.startAfterTerminationOf(2000, killreplicationNode1);

            }
        };

        return allOperationAllLeader;
    }

    public static SimulationScenario testAllOperationsOneLeader() {
        SimulationScenario allOperationOneLeader = new SimulationScenario() {
            {
                //Start three nodes holding even values
                StochasticProcess nodeGroupProcess = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(9, nodeGroup, new BasicIntSequentialDistribution(1));
                    }
                };

                //Start client that gets a value
                StochasticProcess putClient = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(100));
                        raise(1, startPUTClient, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(10), new BasicIntSequentialDistribution(5));
                    }
                };

                StochasticProcess casClient = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startCASClient, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(11), new BasicIntSequentialDistribution(2));
                    }
                };

                StochasticProcess getClient = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(100));
                        raise(1, startGETClient, new BasicIntSequentialDistribution(12), new BasicIntSequentialDistribution(1));
                    }
                };

                StochasticProcess getClient2 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(100));
                        raise(1, startGETClient, new BasicIntSequentialDistribution(15), new ConstantDistribution<Integer>(Integer.class, 8));
                    }
                };

                StochasticProcess getClient3 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(100));
                        raise(3, startGETClient, new BasicIntSequentialDistribution(30), new ConstantDistribution<Integer>(Integer.class, 3));
                    }
                };

                StochasticProcess killreplicationNode1 = new StochasticProcess() {
                    {
                        raise(1, killNode, new BasicIntSequentialDistribution(7));
                    }
                };

                /*
                StochasticProcess killreplicationNode3 = new StochasticProcess() {
                    {
                        raise(1, killNode, new BasicIntSequentialDistribution(3));
                    }
                };
                */

                nodeGroupProcess.start();
                putClient.start();
                getClient.startAfterTerminationOf(5000, putClient);
                casClient.startAfterTerminationOf(5000, getClient);
                //killreplicationNode3.startAfterTerminationOf(5000, casClient);
                getClient2.startAfterTerminationOf(2000, casClient);
                killreplicationNode1.startAfterTerminationOf(2000, getClient2);
                getClient3.startAfterTerminationOf(2000, killreplicationNode1);

            }
        };

        return allOperationOneLeader;
    }

    public static SimulationScenario testFailureDetectionAndLeaderElection() {
        SimulationScenario failureDetectionScenario = new SimulationScenario() {
            {

                //Start three nodes holding even values
                StochasticProcess nodeGroupProcess = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(6, nodeGroup, new BasicIntSequentialDistribution(1));
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
                        raise(1, killNode, new BasicIntSequentialDistribution(2));
                    }
                };


                //Restart node in even group with ip ending with 2
                StochasticProcess restartNode1 = new StochasticProcess() {
                    {
                        raise(1, nodeGroup, new BasicIntSequentialDistribution(2));
                    }
                };


                nodeGroupProcess.start();
                killNode1.startAfterTerminationOf(500, nodeGroupProcess);
                restartNode1.startAfterTerminationOf(500, killNode1);
                killNode2.startAfterTerminationOf(500, restartNode1);
                restartNode2.startAfterTerminationOf(500, killNode2);
                terminateAfterTerminationOf(10000, restartNode2);
            }
        };

        return failureDetectionScenario;
    }
}
