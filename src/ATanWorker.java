import java.math.BigDecimal;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Connor on 3/10/16.
 */
public class ATanWorker {

    public static ExecutorService executor = Executors.newCachedThreadPool();
    private static final int rounding = BigDecimal.ROUND_HALF_EVEN;

    private int decimalPlaces;

    private volatile BigDecimal initial, initialSquared;
    private volatile BigDecimal result, numerator, term;

    private volatile int spot = 1;

    public ATanWorker(int number, int decimalPlaces, Callback<BigDecimal> onFinish) {
        this.initial = BigDecimal.valueOf(number);
        this.initialSquared = this.initial.pow(2);
        this.decimalPlaces = decimalPlaces + 5;

        this.numerator = BigDecimal.ONE.divide(initial, this.decimalPlaces, rounding);
        this.result = this.numerator;

        Callable<Void> worker = () -> {
            do {
                synchronized (this.numerator) {
                    this.numerator = numerator.divide(initialSquared, this.decimalPlaces, rounding);
                    int denominator = 2 * spot + 1;

                    this.term = this.numerator.divide(BigDecimal.valueOf(denominator), this.decimalPlaces, rounding);

                    if (spot++ % 2 != 0) {
                        this.result = result.subtract(this.term);
                    } else {
                        this.result = result.add(this.term);
                    }
                }
            } while (term.compareTo(BigDecimal.ZERO) != 0);

            if (onFinish != null) {
                onFinish.call(this.result);
            }

            return null;
        };

        executor.submit(worker);
    }

    public int getCurrentSpot() {
        return spot;
    }

    private BigDecimal getCurrentTerm() {
        return term;
    }
}
