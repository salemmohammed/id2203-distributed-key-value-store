package sim;
import se.sics.kompics.simulator.events.system.KillNodeEvent;
import system.Parent;
import system.TAddress;

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


    static Operation1 evenNumberGroup = new Operation1<StartNodeEvent, Integer>() {

        @Override
        public StartNodeEvent generate(final Integer self) {
            return new StartNodeEvent() {
                TAddress selfAdr;
                ArrayList<TAddress> neighbours = new ArrayList<>();
                TAddress otherGroupLeader;
                boolean isLeader;

                {
                    try {
                        selfAdr = new TAddress(InetAddress.getByName("192.193.0." + self), 10000);
                        otherGroupLeader = new TAddress(InetAddress.getByName("192.193.0.4"), 10000);
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
                    return Parent.class;
                }

                @Override
                public Init getComponentInit() {
                    return new Parent.Init(selfAdr, neighbours, otherGroupLeader, isLeader );
                }

                @Override
                public String toString() {
                    return "StartNode<" + selfAdr.toString() + ">";
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
                boolean isLeader;
                {
                    try {
                        selfAdr = new TAddress(InetAddress.getByName("192.193.0." + self), 10000);
                        otherGroupLeader = new TAddress(InetAddress.getByName("192.193.0.1"), 10000);
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
                    return Parent.class;
                }

                @Override
                public Init getComponentInit() {
                    return new Parent.Init(selfAdr, neighbours, otherGroupLeader, isLeader);
                }

                @Override
                public String toString() {
                    return "StartNode<" + selfAdr.toString() + ">";
                }
            };
        }
    };

    static Operation1 killNodeOperation = new Operation1<KillNodeEvent, Integer>() {
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


    public static SimulationScenario simpleNodePing() {
        SimulationScenario scen = new SimulationScenario() {
            {
                StochasticProcess evenNumberNodes = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(3, evenNumberGroup, new BasicIntSequentialDistribution(1));
                    }
                };

                StochasticProcess oddNumberNodes = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(0));
                        raise(3, oddNumberGroup, new BasicIntSequentialDistribution(4));
                    }
                };

                //Kill node on ip ending with 2
                StochasticProcess killNode = new StochasticProcess() {
                    {
                        raise(1, killNodeOperation, new BasicIntSequentialDistribution(2));
                    }
                };

                //Restartnode on ip ending with 2
                StochasticProcess restartNode = new StochasticProcess() {
                    {
                        raise(1, evenNumberGroup, new BasicIntSequentialDistribution(2));
                    }
                };


                evenNumberNodes.start();
                oddNumberNodes.start();
                killNode.startAfterStartOf(500, oddNumberNodes);
                restartNode.startAfterStartOf(500, killNode);
                terminateAfterTerminationOf(10000, restartNode);
            }
        };

        return scen;
    }



}
