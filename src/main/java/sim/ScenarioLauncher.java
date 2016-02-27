package sim;

import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

public class ScenarioLauncher {
    public static void main(String[] args) {
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario simpleBootScenario = ScenarioGen.testAllOperations();
        simpleBootScenario.simulate(LauncherComp.class);
    }
}
