package sim;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.simulator.adaptor.Operation;
import se.sics.kompics.simulator.adaptor.Operation2;
import se.sics.kompics.simulator.adaptor.Operation3;
import se.sics.kompics.simulator.events.system.KillNodeEvent;
import sim.preload.DatastoreFactory;
import system.KVEntry;
import system.client.ClientParent;
import system.client.event.CASRequest;
import system.client.event.GETRequest;
import system.client.event.PUTRequest;
import system.data.Bound;
import system.network.TMessage;
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
import java.util.Random;

public class ScenarioGen {

    static DatastoreFactory datastoreFactory = new DatastoreFactory();

    static Operation1 nodeGroup = new Operation1<StartNodeEvent, Integer>() {
        @Override
        public StartNodeEvent generate(final Integer self) {
            return new StartNodeEvent() {
                TAddress selfAdr;
                ArrayList<TAddress> neighbours = new ArrayList<>();
                HashMap <Integer, KVEntry> store = new HashMap<>();
                ArrayList<TAddress> replicationGroup;
                TAddress leader;
                Bound bound;
                {
                    try {
                        selfAdr = new TAddress(InetAddress.getByName("192.193.0." + self), 10000);
                        store = datastoreFactory.getHashMapByIpSuffix(self);
                        replicationGroup = datastoreFactory.getReplicationGroupByIpSuffix(self);
                        neighbours = datastoreFactory.getNeighbours();
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
                    return new NodeParent.Init(selfAdr, neighbours, store, replicationGroup, leader, bound);
                }
            };
        }
    };

    static Operation2 startGETClient = new Operation2<StartNodeEvent, Integer, Integer>() {
        @Override
        public StartNodeEvent generate(final Integer ip, final Integer target) {
            return new StartNodeEvent() {
                TAddress selfAdr;
                ArrayList<TAddress> nodes;
                TMessage command;
                {
                    try {
                        //Client is started on ip ending with 100
                        selfAdr = new TAddress(InetAddress.getByName("192.193.0." + ip), 10000);
                        //Nodes to send GETRequest to
                        ArrayList<TAddress> nodes = new ArrayList<>();
                        nodes.add(new TAddress(InetAddress.getByName("192.193.0." + target), 10000));
                        this.nodes = nodes;
                        KVEntry kv = new KVEntry(5,-1,0);
                        this.command = new GETRequest(selfAdr, nodes.get(0), kv);
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
                    return new ClientParent.Init(selfAdr, nodes, command);
                }
            };
        }
    };

    static Operation3 startPUTClient = new Operation3<StartNodeEvent, Integer, Integer, Integer>() {
        @Override
        public StartNodeEvent generate(final Integer val, final Integer ip, final Integer target) {
            return new StartNodeEvent() {
                TAddress selfAdr;
                ArrayList<TAddress> nodes;
                TMessage command;
                {
                    try {
                        //Client is started on ip ending with 100
                        selfAdr = new TAddress(InetAddress.getByName("192.193.0." + ip), 10000);
                        //Nodes to send GETRequest to
                        ArrayList<TAddress> nodes = new ArrayList<>();
                        nodes.add(new TAddress(InetAddress.getByName("192.193.0." + target), 10000));
                        KVEntry kv = new KVEntry(5,100,0);
                        this.command = new PUTRequest(selfAdr, nodes.get(0), kv);
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
                    return new ClientParent.Init(selfAdr, nodes, command);
                }
            };
        }
    };

    static Operation3 startCASClient = new Operation3<StartNodeEvent, Integer, Integer, Integer>() {
        @Override
        public StartNodeEvent generate(final Integer val, final Integer ip, final Integer target) {
            return new StartNodeEvent() {
                TAddress selfAdr;
                ArrayList<TAddress> nodes;
                TMessage command;
                {
                    try {
                        //Client is started on ip ending with 100
                        selfAdr = new TAddress(InetAddress.getByName("192.193.0." + ip), 10000);
                        //Nodes to send GETRequest to
                        ArrayList<TAddress> nodes = new ArrayList<>();
                        nodes.add(new TAddress(InetAddress.getByName("192.193.0." + target), 10000));
                        KVEntry kv = new KVEntry(5,100,0);
                        this.command = new CASRequest(selfAdr, nodes.get(0), kv, 30);
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
                    return new ClientParent.Init(selfAdr, nodes, command);
                }
            };
        }
    };



    static Operation1 killNode = new Operation1<KillNodeEvent, Integer>() {
        @Override
        public KillNodeEvent generate(final Integer self) {
            return new KillNodeEvent() {
                TAddress selfAdr;

                {
                    try {
                        selfAdr = new TAddress(InetAddress.getByName("192.193.0." + self), 10000);
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


    public static SimulationScenario testGETOperation() {
        SimulationScenario getOperationScenario = new SimulationScenario() {
            {
                //Start three nodes holding even values
                StochasticProcess nodeGroupProcess = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(9, nodeGroup, new BasicIntSequentialDistribution(1));
                    }
                };

                //Start client that gets a value
                StochasticProcess getClient = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(1, startGETClient, new BasicIntSequentialDistribution(2), new BasicIntSequentialDistribution(3));
                    }
                };

                nodeGroupProcess.start();
                getClient.start();

                terminateAfterTerminationOf(1000, getClient);
            }
        };

        return getOperationScenario;
    }

    public static SimulationScenario testAllOperations() {
        SimulationScenario putOperationScenario = new SimulationScenario() {
            {
                //Start three nodes holding even values
                StochasticProcess nodeGroupProcess = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(3, nodeGroup, new BasicIntSequentialDistribution(1));
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
                        raise(1, startCASClient, new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(11), new BasicIntSequentialDistribution(3));
                    }
                };

                StochasticProcess getClient = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(100));
                        raise(1, startGETClient, new BasicIntSequentialDistribution(12), new BasicIntSequentialDistribution(3));
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
                //getClient.startAfterTerminationOf(5000, putClient);
                killreplicationNode1.startAfterTerminationOf(5000, putClient);
                casClient.startAfterTerminationOf(5000, killreplicationNode1);
                //killreplicationNode3.startAfterTerminationOf(5000, casClient);
                getClient.startAfterTerminationOf(2000, casClient);

            }
        };

        return putOperationScenario;
    }

    public static SimulationScenario testFailureDetection() {
        SimulationScenario failureDetectionScenario = new SimulationScenario() {
            {

                //Start three nodes holding even values
                StochasticProcess nodeGroupProcess = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(3, nodeGroup, new BasicIntSequentialDistribution(1));
                    }
                };

                //Kill node in even group with ip ending with 2
                StochasticProcess killNodeProcess = new StochasticProcess() {
                    {
                        raise(1, killNode, new BasicIntSequentialDistribution(2));
                    }
                };


                //Restart node in even group with ip ending with 2
                StochasticProcess restartNode = new StochasticProcess() {
                    {
                        raise(1, nodeGroup, new BasicIntSequentialDistribution(2));
                    }
                };


                nodeGroupProcess.start();
                killNodeProcess.startAfterStartOf(500, nodeGroupProcess);
                restartNode.startAfterStartOf(500, killNodeProcess);
                terminateAfterTerminationOf(10000, restartNode);
            }
        };

        return failureDetectionScenario;
    }
}
