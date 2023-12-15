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
        final BooleanSupplier exitCondition = getSimulation()::isFinished;
        final Runnable task = this::stop;

        final ScheduledFutureAwareRunnable futureAwareRunnable = new ScheduledFutureAwareRunnable(exitCondition, task, "Ball-Simulation");

        final ScheduledFuture<?> scheduledFuture = getScheduledExecutorService().scheduleWithFixedDelay(futureAwareRunnable, 3, 3, TimeUnit.SECONDS);
        futureAwareRunnable.setScheduledFuture(scheduledFuture);
    }
}
