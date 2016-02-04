package sim;
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
import java.util.HashMap;

public class ScenarioGen {


    static Operation1 startOp = new Operation1<StartNodeEvent, Integer>() {

        @Override
        public StartNodeEvent generate(final Integer self) {
            return new StartNodeEvent() {
                TAddress selfAdr;
                HashMap<String, TAddress> neighbours = new HashMap<>();

                {
                    try {
                        selfAdr = new TAddress(InetAddress.getByName("192.193.0." + self), 10000);

                        for (int i = 0; i < 3; i++) {
                            if (i != self) {
                                neighbours.put("node" + 1, new TAddress(InetAddress.getByName("192.193.0." + i), 10000));
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
                    return new Parent.Init(selfAdr, neighbours);
                }

                @Override
                public String toString() {
                    return "StartNode<" + selfAdr.toString() + ">";
                }
            };
        }
    };


    public static SimulationScenario simpleNodePing() {
        SimulationScenario scen = new SimulationScenario() {
            {
                StochasticProcess nodes = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(3, startOp, new BasicIntSequentialDistribution(1));
                    }
                };

                nodes.start();
                terminateAfterTerminationOf(10000, nodes);
            }
        };

        return scen;
    }



}
