package sim;

import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

public class ScenarioLauncher {
    public static void main(String[] args) {
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario monarchicalEventualLeaderDetectorScenario = ScenarioGen.testMonarchicalEventualLeaderDetectorProperties();
        SimulationScenario allLeaderAllOperationsASC = ScenarioGen.testAllOperationsAllLeaders();
        SimulationScenario oneLeaderAllOperationsASC = ScenarioGen.testAllOperationsOneLeader();
        SimulationScenario bestEffortBroadcastScenario = ScenarioGen.testBestEffortBroadcastProperties();
        SimulationScenario eventualFailureDetectorScenario = ScenarioGen.testEventualPerfectFailureDetectorProperties();
        SimulationScenario perfectPointToPointLinkScenario = ScenarioGen.testPerfectPointToPointLinkProperties();
        SimulationScenario replicatedStateMachineScenario = ScenarioGen.testReplicatedStateMachineProperties();
        replicatedStateMachineScenario.simulate(LauncherComp.class);
        //monarchicalEventualLeaderDetectorScenario.simulate(LauncherComp.class);
    }
}
