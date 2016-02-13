package sim;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.simulator.adaptor.Operation;
import se.sics.kompics.simulator.events.system.KillNodeEvent;
import sim.preload.DatastoreFactory;
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
import java.util.Random;

public class ScenarioGen {

    static DatastoreFactory datastoreFactory = new DatastoreFactory();

    static Operation1 evenNumberGroup = new Operation1<StartNodeEvent, Integer>() {
        @Override
        public StartNodeEvent generate(final Integer self) {
            return new StartNodeEvent() {
                TAddress selfAdr;
                ArrayList<TAddress> neighbours = new ArrayList<>();
                TAddress otherGroupLeader;
                HashMap <Integer, Integer> store = new HashMap<>();
                boolean isLeader;
                {
                    try {
                        selfAdr = new TAddress(InetAddress.getByName("192.193.0." + self), 10000);
                        otherGroupLeader = new TAddress(InetAddress.getByName("192.193.0.4"), 10000);
                        store = datastoreFactory.getEvenHashMapByIpSuffix(self);
                        isLeader = false;
                        if(self == 1) {
                            isLeader = true;
                        }
                        for (int i = 1; i < 4; i++) {
                            if (i != self) {
                                neighbours.add(new TAddress(InetAddress.getByName("192.193.0." + i), 10000));
                            }
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
                    return new NodeParent.Init(selfAdr, neighbours, otherGroupLeader, store, isLeader);
                }
            };
        }
    };

    static Operation1 oddNumberGroup = new Operation1<StartNodeEvent, Integer>() {

        @Override
        public StartNodeEvent generate(final Integer self) {
            return new StartNodeEvent() {
                TAddress selfAdr;
                ArrayList<TAddress> neighbours = new ArrayList<>();
                TAddress otherGroupLeader;
                HashMap <Integer, Integer> store = new HashMap<>();
                boolean isLeader;
                {
                    try {
                        selfAdr = new TAddress(InetAddress.getByName("192.193.0." + self), 10000);
                        otherGroupLeader = new TAddress(InetAddress.getByName("192.193.0.1"), 10000);
                        store = datastoreFactory.getUnevenHashMapByIpSuffix(self);
                        isLeader = false;
                        if(self == 4) {
                            isLeader = true;
                        }

                        for (int i = 4; i < 7; i++) {
                            if (i != self) {
                                neighbours.add(new TAddress(InetAddress.getByName("192.193.0." + i), 10000));
                            }
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
                    return new NodeParent.Init(selfAdr, neighbours, otherGroupLeader, store, isLeader);
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
                StochasticProcess evenNumberNodes = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(3, evenNumberGroup, new BasicIntSequentialDistribution(1));
                    }
                };

                //Start three nodes holding uneven values
                StochasticProcess oddNumberNodes = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(3, oddNumberGroup, new BasicIntSequentialDistribution(4));
                    }
                };

                //Start client that gets a value
                StochasticProcess getClient = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(1, startGETClient);
                    }
                };


                evenNumberNodes.start();
                oddNumberNodes.start();
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
                StochasticProcess evenNumberNodes = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(3, evenNumberGroup, new BasicIntSequentialDistribution(1));
                    }
                };

                //Start three nodes holding uneven values
                StochasticProcess oddNumberNodes = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(3, oddNumberGroup, new BasicIntSequentialDistribution(4));
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
                        raise(1, evenNumberGroup, new BasicIntSequentialDistribution(2));
                    }
                };
                evenNumberNodes.start();
                oddNumberNodes.start();
                killNodeProcess.startAfterStartOf(500, oddNumberNodes);
                restartNode.startAfterStartOf(500, killNodeProcess);
                terminateAfterTerminationOf(10000, restartNode);
            }
        };

        return failureDetectionScenario;
    }
}
