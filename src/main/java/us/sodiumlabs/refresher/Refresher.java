package us.sodiumlabs.refresher;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * A container that ensures that there will only ever be one instance of a given type.
 *
 * @param <T> the contained type.
 */
@SuppressWarnings("unused")
public final class Refresher<T>
    implements AutoCloseable
{
    private final Supplier<T> refresher;

    private final Consumer<T> closer;

    private T t;

    private final Supplier<T> presentSupplier = () -> this.t;

    private final Supplier<T> absentSupplier = this::refresh;

    private Supplier<T> currentSupplier = absentSupplier;

    private final Consumer<T> absentCloser = t -> {};

    private Consumer<T> currentCloser = absentCloser;

    /**
     * @param refresher - a factory function used to create the resource.
     */
    public Refresher(final Supplier<T> refresher) {
        this.refresher = requireNonNull(refresher);
        this.closer = absentCloser;
    }

    /**
     * @param refresher - a factory function used to create the resource.
     * @param closer - a function that will close the resource when the "close" or "refresh" methods are called.
     */
    public Refresher(final Supplier<T> refresher, final Consumer<T> closer) {
        this.refresher = requireNonNull(refresher);
        this.closer = requireNonNull(closer);
    }

    public synchronized T get() {
        return currentSupplier.get();
    }


    public synchronized T refresh() {
        close();
        this.t = refresher.get();
        currentSupplier = presentSupplier;
        currentCloser = closer;
        return this.t;
    }

    public synchronized void close() {
        currentCloser.accept(t);
        currentSupplier = absentSupplier;
        currentCloser = absentCloser;
    }
}