package sim;

import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

public class ScenarioLauncher {
    public static void main(String[] args) {
        long seed = 123;
        SimulationScenario.setSeed(seed);


        //Tests of individual components
        SimulationScenario monarchicalEventualLeaderDetectorScenario = ScenarioGen.testMonarchicalEventualLeaderDetectorProperties();
        SimulationScenario bestEffortBroadcastScenario = ScenarioGen.testBestEffortBroadcastProperties();
        SimulationScenario eventualFailureDetectorScenario = ScenarioGen.testEventualPerfectFailureDetectorProperties();
        SimulationScenario perfectPointToPointLinkScenario = ScenarioGen.testPerfectPointToPointLinkProperties();
        SimulationScenario replicatedStateMachineScenario = ScenarioGen.testReplicatedStateMachineProperties();

        //Tests to ensure ASC properties, in different situations
        SimulationScenario ascOneLeader = ScenarioGen.testAbortableSequenceConsensusOneLeader();
        SimulationScenario ascAllLeader = ScenarioGen.testAbortableSequenceConsensusAllLeader();
        SimulationScenario ascLeaderElection = ScenarioGen.testAbortableSequenceConsensusLeaderElection();
        SimulationScenario ascNoDuplication = ScenarioGen.testAbortableSequenceConsensusNoDuplicates();
        SimulationScenario ascQuorumMajority = ScenarioGen.testAbortableSequenceConsensusQuorumMajority();

        //The current simulation scenario to be run
        ascLeaderElection.simulate(LauncherComp.class);
    }
}