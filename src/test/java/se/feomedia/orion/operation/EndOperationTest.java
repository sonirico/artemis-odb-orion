package se.feomedia.orion.operation;


import com.artemis.World;
import com.artemis.WorldConfiguration;
import com.artemis.annotations.Wire;
import org.junit.Test;
import se.feomedia.orion.Executor;
import se.feomedia.orion.Operation;
import se.feomedia.orion.OperationTree;
import se.feomedia.orion.system.OperationSystem;

import static junit.framework.TestCase.assertEquals;
import static se.feomedia.orion.OperationFactory.operation;

public class EndOperationTest {

    @Test
    public void end_test () {
        World world = new World(
            new WorldConfiguration().setSystem(OperationSystem.class)
        );

        FiniteOperation finiteOperation = operation(FiniteOperation.class);

        finiteOperation.register(world, world.create());

        world.process();

        assertEquals(1, finiteOperation.n);

        world.process();

        assertEquals(2, finiteOperation.n);

        world.process();

        assertEquals(-1, finiteOperation.n);
    }

    public static class FiniteOperation extends Operation {

        public int n = 0;

        @Override
        public Class<? extends Executor> executorType() {
            return FiniteExecutor.class;
        }

        @Override
        protected boolean isComplete() {
            return n > 2;
        }

        @Override
        public void reset() {
            n = 0;
        }

        @Wire
        public static class FiniteExecutor extends Executor<FiniteOperation> {

            @Override
            protected void begin(FiniteOperation operation, OperationTree node) {
                operation.n = 0;
            }

            @Override
            protected float act(float delta, FiniteOperation operation, OperationTree node) {
                operation.n++;
                return 0;
            }

            @Override
            protected void end(FiniteOperation operation, OperationTree node) {
                operation.n = -1;
            }
        }
    }
}
