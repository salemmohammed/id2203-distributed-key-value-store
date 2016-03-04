package sim;

import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

public class ScenarioLauncher {
    public static void main(String[] args) {
        long seed = 123;
        SimulationScenario.setSeed(seed);


        //Shippable
        SimulationScenario monarchicalEventualLeaderDetectorScenario = ScenarioGen.testMonarchicalEventualLeaderDetectorProperties();
        SimulationScenario bestEffortBroadcastScenario = ScenarioGen.testBestEffortBroadcastProperties();
        SimulationScenario eventualFailureDetectorScenario = ScenarioGen.testEventualPerfectFailureDetectorProperties();
        SimulationScenario perfectPointToPointLinkScenario = ScenarioGen.testPerfectPointToPointLinkProperties();
        SimulationScenario replicatedStateMachineScenario = ScenarioGen.testReplicatedStateMachineProperties();

        //Asc tests
        SimulationScenario ascOneLeader = ScenarioGen.testAbortableSequenceConsensusOneLeader();
        SimulationScenario ascAllLeader = ScenarioGen.testAbortableSequenceConsensusAllLeader();
        SimulationScenario ascLeaderElection = ScenarioGen.testAbortableSequenceConsensusLeaderElection();
        SimulationScenario ascNoDuplication = ScenarioGen.testAbortableSequenceConsensusNoDuplicates();
        SimulationScenario ascQuorumMajority = ScenarioGen.testAbortableSequenceConsensusQuorumMajority();

        ascQuorumMajority.simulate(LauncherComp.class);
    }
}
