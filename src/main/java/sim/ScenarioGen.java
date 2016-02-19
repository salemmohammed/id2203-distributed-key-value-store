package sim;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.simulator.adaptor.Operation;
import se.sics.kompics.simulator.events.system.KillNodeEvent;
import sim.preload.DatastoreFactory;
import system.KVEntry;
import system.client.ClientParent;
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
                ArrayList<TAddress> neighbours = new ArrayList<>();
                HashMap <Integer, KVEntry> store = new HashMap<>();
                ArrayList<TAddress> replicationGroup;
                boolean isLeader;
                {
                    try {
                        selfAdr = new TAddress(InetAddress.getByName("192.193.0." + self), 10000);
                        store = datastoreFactory.getHashMapByIpSuffix(self);
                        replicationGroup = datastoreFactory.getReplicationGroupByIpSuffix(self);
                        neighbours = datastoreFactory.getNeighbours();
                        isLeader = false;
                        if(self == 4) {
                            isLeader = true;
                        }

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
                    return new NodeParent.Init(selfAdr, neighbours, store, replicationGroup, isLeader);
                }
            };
        }
    };

    static Operation startGETClient = new Operation<StartNodeEvent>() {
        @Override
        public StartNodeEvent generate() {
            return new StartNodeEvent() {
                TAddress selfAdr;
                ArrayList<TAddress> nodes;

                {
                    try {
                        //Client is started on ip ending with 100
                        selfAdr = new TAddress(InetAddress.getByName("192.193.0." + 100), 10000);
                        //Nodes to send GETRequest to
                        ArrayList<TAddress> nodes = new ArrayList<>();
                        nodes.add(new TAddress(InetAddress.getByName("192.193.0." + 1), 10000));
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
                    return new ClientParent.Init(selfAdr, nodes);
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
        SimulationScenario failureDetectionScenario = new SimulationScenario() {
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
                        raise(1, startGETClient);
                    }
                };

                nodeGroupProcess.start();
                getClient.start();

                terminateAfterTerminationOf(1000, getClient);
            }
        };

        return failureDetectionScenario;
    }

    public static SimulationScenario testFailureDetection() {
        SimulationScenario failureDetectionScenario = new SimulationScenario() {
            {

                //Start three nodes holding even values
                StochasticProcess nodeGroupProcess = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(9, nodeGroup, new BasicIntSequentialDistribution(1));
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
