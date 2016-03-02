package sim;

import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

public class ScenarioLauncher {
    public static void main(String[] args) {
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario failureDetectionAndLeaderElection = ScenarioGen.testFailureDetectionAndLeaderElection();
        SimulationScenario allLeaderAllOperationsASC = ScenarioGen.testAllOperationsAllLeaders();
        SimulationScenario oneLeaderAllOperationsASC = ScenarioGen.testAllOperationsOneLeader();
        oneLeaderAllOperationsASC.simulate(LauncherComp.class);
    }
}
