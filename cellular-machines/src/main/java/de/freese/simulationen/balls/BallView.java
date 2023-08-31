// Created: 18.09.2009
package de.freese.simulationen.balls;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import de.freese.simulationen.ScheduledFutureAwareRunnable;
import de.freese.simulationen.SimulationView;

/**
 * View für die Ball Simulation.
 *
 * @author Thomas Freese
 */
public class BallView extends SimulationView<BallSimulation> {
    @Override
    protected void start() {
        super.start();

        // Die Simulation würde ewig weitergehen, auch wenn die Bälle schon am Boden liegen.
        BooleanSupplier exitCondition = getSimulation()::isFinished;
        Runnable task = this::stop;

        ScheduledFutureAwareRunnable futureAwareRunnable = new ScheduledFutureAwareRunnable(exitCondition, task, "Ball-Simulation");

        ScheduledFuture<?> scheduledFuture = getScheduledExecutorService().scheduleWithFixedDelay(futureAwareRunnable, 3, 3, TimeUnit.SECONDS);
        futureAwareRunnable.setScheduledFuture(scheduledFuture);
    }
}
