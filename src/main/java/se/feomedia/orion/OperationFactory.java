package se.feomedia.orion;

import com.artemis.Entity;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import se.feomedia.orion.operation.*;

import static com.badlogic.gdx.math.Interpolation.linear;
import static com.badlogic.gdx.utils.NumberUtils.floatToRawIntBits;
import static com.badlogic.gdx.utils.NumberUtils.intBitsToFloat;
import static java.lang.Math.max;

public final class OperationFactory {

	/**
	 * Create pools with this number of preallocated operations. Setting
	 * is global, affects all worlds.
	 */
	public static int initialPoolSize = 16;

	private static final Vector2 xy = new Vector2();

	private static final ObjectMap<Class<? extends Operation>, Pool<?>> pools
			= new ObjectMap<>();

	private static final WeightPool weightPool = new WeightPool();

	private OperationFactory() {}

	public static SequenceOperation sequence() {
		SequenceOperation action = operation(SequenceOperation.class);
		return action;
	}

	public static SequenceOperation sequence(Operation a) {
		SequenceOperation action = operation(SequenceOperation.class);
		action.addChild(a);
		return action;
	}

	public static SequenceOperation sequence(Operation a, Operation b) {
		SequenceOperation action = operation(SequenceOperation.class);
		action.addChild(a);
		action.addChild(b);
		return action;
	}

	public static SequenceOperation sequence(Operation a, Operation b, Operation c) {
		SequenceOperation action = operation(SequenceOperation.class);
		action.addChild(a);
		action.addChild(b);
		action.addChild(c);
		return action;
	}

	public static SequenceOperation sequence(Operation a, Operation b, Operation c, Operation d) {
		SequenceOperation action = operation(SequenceOperation.class);
		action.addChild(a);
		action.addChild(b);
		action.addChild(c);
		action.addChild(d);
		return action;
	}

	public static SequenceOperation sequence(Operation a, Operation b, Operation c, Operation d, Operation ...e) {
		SequenceOperation action = operation(SequenceOperation.class);
		action.addChild(a);
		action.addChild(b);
		action.addChild(c);
		action.addChild(d);
		for (int i = 0; i < e.length; i++) {
			action.addChild(e[i]);
		}
		return action;
	}

	public static ParallelOperation parallel() {
		ParallelOperation action = operation(ParallelOperation.class);
		return action;
	}

	public static ParallelOperation parallel(Operation a) {
		ParallelOperation action = operation(ParallelOperation.class);
		action.addChild(a);
		return action;
	}

	public static ParallelOperation parallel(Operation a, Operation b) {
		ParallelOperation action = operation(ParallelOperation.class);
		action.addChild(a);
		action.addChild(b);
		return action;
	}

	public static ParallelOperation parallel(Operation a, Operation b, Operation c) {
		ParallelOperation action = operation(ParallelOperation.class);
		action.addChild(a);
		action.addChild(b);
		action.addChild(c);
		return action;
	}

	public static ParallelOperation parallel(Operation a, Operation b, Operation c, Operation d) {
		ParallelOperation action = operation(ParallelOperation.class);
		action.addChild(a);
		action.addChild(b);
		action.addChild(c);
		action.addChild(d);
		return action;
	}

	public static ParallelOperation parallel(Operation a, Operation b, Operation c, Operation d, Operation ...e) {
		ParallelOperation action = operation(ParallelOperation.class);
		action.addChild(a);
		action.addChild(b);
		action.addChild(c);
		action.addChild(d);
		for (int i = 0; i < e.length; i++) {
			action.addChild(e[i]);
		}
		return action;
	}

	/**
	 * Runs until completion. This operation can't be
	 * serialized.
	 *
	 * @param runnable Runs on the main thread.
	 * @return The operation
	 */
	public static RunOperation run(Runnable runnable) {
		RunOperation op = operation(RunOperation.class);
		op.runnable = runnable;

		return op;
	}

	/**
	 * <p>
	 * Runs any operation on another entity. The forked operation
	 * is marked as compeleted when the operation finishes or
	 * when the target entity is deleted.
	 * </p>
	 * <p>
	 * Serialization compatible.
	 * </p>
	 * @param tag as registered with the {@link com.artemis.managers.TagManager}
	 * @param operation operation to run on the entity
	 * @return the fork operation
	 */
	public static ForkOperation fork(String tag, Operation operation) {
		ForkOperation op = operation(ForkOperation.class);
		op.configure(-1, tag, operation);

		return op;
	}

	/**
	 * <p>
	 * Runs any operation on another entity. The forked operation
	 * is marked as compeleted when the operation finishes or
	 * when the target entity is deleted.
	 * </p>
	 * <p>
	 * This operation is incompatible with serialization, as the
	 * entity id translation only works for operations directly
	 * attached to the entity.
	 * </p>
	 *
	 * @param entityId as registered with the {@link com.artemis.managers.TagManager}
	 * @param operation operation to run on the entity
	 * @return the fork operation
	 */
	public static ForkOperation fork(int entityId, Operation operation) {
		ForkOperation op = operation(ForkOperation.class);
		op.configure(entityId, null, operation);

		return op;
	}

	/**
	 * <p>
	 * Runs any operation on another entity. The forked operation
	 * is marked as compeleted when the operation finishes or
	 * when the target entity is deleted.
	 * </p>
	 * <p>
	 * This operation is incompatible with serialization, as the
	 * entity id translation only works for operations directly
	 * attached to the entity.
	 * </p>
	 *
	 * @param e as registered with the {@link com.artemis.managers.TagManager}
	 * @param operation operation to run on the entity
	 * @return the fork operation
	 */
	public static ForkOperation fork(Entity e, Operation operation) {
		return fork(e.getId(), operation);
	}


	public static DelayOperation delay(float duration) {
		DelayOperation action = operation(DelayOperation.class);
		action.duration = duration;

		return action;
	}

	public static RepeatOperation repeat(int times, Operation operation) {
		RepeatOperation op = operation(RepeatOperation.class);
		op.configure(times, operation);

		return op;
	}

	public static Operation forever(Operation operation) {
		return repeat(Integer.MAX_VALUE, operation);
	}

	public static DelayTickOperation delayTick(int ticksToWait) {
		DelayTickOperation action = operation(DelayTickOperation.class);
		action.ticksToWait = ticksToWait;

		return action;
	}

	public static KillOperation killEntity() {
		KillOperation op = operation(KillOperation.class);
		return op;
	}

	private static IfElseOperation ifElse(boolean b, Operation ifTrue) {
		IfElseOperation op = operation(IfElseOperation.class);
		op.configure(b, ifTrue);
		return op;
	}

	public static IfElseOperation ifTrue(boolean b, Operation ifTrue) {
		return ifElse(b, ifTrue);
	}

	public static IfElseOperation ifFalse(boolean b, Operation ifFalse) {
		return ifElse(!b, ifFalse);
	}

	/**
	 * <p>An entity may only have a single non-completed <code>unique</code>
	 * operation at any time. It is primarily used for cancelling conflicting
	 * or invalid operations.</p>
	 *
	 * <p>Example: <code>unique</code> controlling UI widget interactions, with
	 * different durations for touch/click:</p>
	 *
	 * <pre>
	 * private void touch(Entity e) {
	 *     unique("interact", // marks any other unique("interact") as complete
	 *         scaleTo(.75f, seconds(.1f), pow2)
	 *     ).register(e);
	 * }
	 *
	 * private void click(Entity e) {
	 *     unique("interact", // marks any other unique("interact") as complete
	 *         sequence(
	 *             removeComponent(Touchable.class),
	 *             scaleTo(1f, seconds(.5f), bounceOut),
	 *             sendEvent(e),
	 *             createComponent(Touchable.class)
	 *         )
	 *     ).register(e);
	 * }
	 * </pre>
	 *
	 * @param tag Unique per-entity id
	 * @param operation Operation to run.
	 * @return requested operation.
	 */
	public static UniqueOperation unique(String tag, Operation operation) {
		UniqueOperation op = operation(UniqueOperation.class);
		op.configure(tag, operation);

		return op;
	}

	public static NullOperation noop() {
		return operation(NullOperation.class);
	}

	public static <T extends Operation> T operation(final Class<T> operationType) {
		T op = pool(operationType).obtain();
		op.started = false;
		return op;
	}

	public static Weight weight(float weight, Operation operation) {
		Weight w = weightPool.obtain();
		w.weight = weight;
		w.op = operation;

		return w;
	}

	public static RandomOperation random(Operation a) {
		return random(
			weight(1f, a));
	}

	public static RandomOperation random(Operation a,
	                                     Operation b) {
		return random(
			weight(1f, a),
			weight(1f, b));
	}

	public static RandomOperation random(Operation a,
	                                     Operation b,
	                                     Operation c) {
		return random(
			weight(1f, a),
			weight(1f, b),
			weight(1f, c));
	}

	public static RandomOperation random(Operation a,
	                                     Operation b,
	                                     Operation c,
	                                     Operation d) {
		return random(
			weight(1f, a),
			weight(1f, b),
			weight(1f, c),
			weight(1f, d));
	}

	public static RandomOperation random(Operation a,
	                                     Operation b,
	                                     Operation c,
	                                     Operation d,
	                                     Operation e) {

		return random(
			weight(1f, a),
			weight(1f, b),
			weight(1f, c),
			weight(1f, d),
			weight(1f, e));
	}

	public static RandomOperation random(Weight a) {
		RandomOperation op = operation(RandomOperation.class);
		op.configure(a.weight, a.op);
		return op;
	}

	public static RandomOperation random(Weight a,
	                                     Weight b) {

		RandomOperation op = operation(RandomOperation.class);
		op.configure(a.weight, a.op);
		op.configure(b.weight, b.op);
		return op;
	}

	public static RandomOperation random(Weight a,
	                                     Weight b,
	                                     Weight c) {

		RandomOperation op = operation(RandomOperation.class);
		op.configure(a.weight, a.op);
		op.configure(b.weight, b.op);
		op.configure(c.weight, c.op);
		return op;
	}

	public static RandomOperation random(Weight a,
	                                     Weight b,
	                                     Weight c,
	                                     Weight d) {

		RandomOperation op = operation(RandomOperation.class);
		op.configure(a.weight, a.op);
		op.configure(b.weight, b.op);
		op.configure(c.weight, c.op);
		op.configure(d.weight, d.op);
		return op;
	}

	public static RandomOperation random(Weight a,
	                                     Weight b,
	                                     Weight c,
	                                     Weight d,
	                                     Weight e) {

		RandomOperation op = operation(RandomOperation.class);
		op.configure(a.weight, a.op);
		op.configure(b.weight, b.op);
		op.configure(c.weight, c.op);
		op.configure(d.weight, d.op);
		op.configure(e.weight, e.op);
		return op;
	}

	public static RandomOperation random(Operation operation, Operation... ops) {
		RandomOperation op = operation(RandomOperation.class);
		op.configure(1, operation);
		for (int i = 0; i < ops.length; i++) {
			op.configure(1, ops[i]);
		}

		return op;
	}

	public static RandomOperation random(Weight weight, Weight... weights) {
		RandomOperation op = operation(RandomOperation.class);
		op.configure(weight.weight, weight.op);
		for (int i = 0; i < weights.length; i++) {
			Weight w = weights[i];
			op.configure(w.weight, w.op);
		}

		return op;
	}

	public static TranslateVector2Operation translate(Vector2 v, Vector2 destination) {
		return translate(v, destination, 0, linear);
	}

	public static TranslateVector2Operation translate(Vector2 v,
	                                                  Vector2 destination,
	                                                  float duration,
	                                                  Interpolation interpolation) {

		TranslateVector2Operation op = operation(TranslateVector2Operation.class);
		configure(op, duration, interpolation);

		op.value = v;
		op.end.set(destination);

		return op;
	}

	public static TranslateVector3Operation translate(Vector3 v, Vector3 destination) {
		return translate(v, destination, 0, linear);
	}

	public static TranslateVector3Operation translate(Vector3 v,
	                                                  Vector3 destination,
	                                                  float duration,
	                                                  Interpolation interpolation) {

		TranslateVector3Operation op = operation(TranslateVector3Operation.class);
		configure(op, duration, interpolation);

		op.value = v;
		op.end.set(destination);

		return op;
	}

	static <T extends Operation> void free(Operation op) {
		((Pool) pool(op.getClass())).free(op);
	}

	private static <T extends Operation> Pool<T> pool(Class<T> operationType) {
		Pool<T> pool = (Pool<T>) pools.get(operationType);
		if (pool == null) {
			pool = new OperationPool<>(operationType, max(1, initialPoolSize));
			pools.put(operationType, pool);
		}

		return pool;
	}

	/**
	 * If value is 0, returns the next highest float value.
	 *
	 * @param value seconds
	 * @return time in seconds.
	 */
	public static float seconds(float value) {
		if (value == 0) {
			int raw = floatToRawIntBits(value);
			value = intBitsToFloat(raw + 1);
		}

		return value;
	}

	/**
	 * If value is 0, returns the next highest float value.
	 *
	 * @param value milliseconds
	 * @return time in milliseconds.
	 */
	public static float milliseconds(float value) {
		return seconds(value) / 1000f;
	}

	public static float secondsRng(float max) {
		return secondsRng(0, seconds(max));
	}

	public static float secondsRng(float min, float max) {
		return MathUtils.random(seconds(min), max);
	}


	/**
	 * Syntactic convenience returning a static {@link Vector2} - be
	 * sure to never store the reference.
	 *
	 * @return static Vector2 instance.
	 */
	public static Vector2 xy(float x, float y) {
		return xy.set(x, y);
	}

	/**
	 * Base configuraiton for operations acting over time.
	 *
	 * @param op operation to configure
	 * @param duration in seconds
	 * @param interpolation any libgdx interpolation
	 * @param <T> Temporal operation type
	 * @return
	 */
	public static <T extends TemporalOperation> T configure(T op,
	                                                        float duration,
	                                                        Interpolation interpolation) {
		op.duration = duration;
		op.interpolation = interpolation;
		return op;
	}

	public static class Weight {
		Weight() {}

		float weight;
		Operation op;
	}

	static class WeightPool extends Pool<Weight> {
		@Override
		protected Weight newObject() {
			return new Weight();
		}
	}

	static class OperationPool<T extends Operation> extends Pool<T> {
		private final Class<T> operationType;

		public OperationPool(Class<T> operationType, int initialSize) {
			super(initialSize);
			this.operationType = operationType;

			Array<T> initial = new Array<>(initialSize);
			for (int i = 0; initialSize > i; i++) {
				initial.add(newObject());
			}

			freeAll(initial);
		}

		@Override
		protected T newObject() {
			try {
				return operationType.newInstance();
			} catch (InstantiationException	| IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
