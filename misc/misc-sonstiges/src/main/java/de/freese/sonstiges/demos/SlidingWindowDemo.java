package de.freese.sonstiges.demos;

/**
 * @author Thomas Freese
 */
public final class SlidingWindowDemo {
    public static void main(final String[] args) {
        int[] arr = new int[]{2, 3};
        int windowSize = 3;

        // Output: Invalid There is no subarray of size 3 as the size of the whole array is 2.
        System.out.println(maxSum(arr, arr.length, windowSize));

        arr = new int[]{100, 200, 300, 400};
        windowSize = 2;
        // 700
        System.out.println(maxSum(arr, arr.length, windowSize));

        arr = new int[]{1, 4, 2, 10, 23, 3, 1, 0, 20};
        windowSize = 4;
        // 39: We get the maximum sum by adding subarray {4, 2, 10, 23} of size 4.
        System.out.println(maxSum(arr, arr.length, windowSize));

        arr = new int[]{1, 4, 2, 10, 2, 3, 1, 0, 20};
        windowSize = 4;
        // 24
        System.out.println(maxSum(arr, arr.length, windowSize));
    }

    /**
     * Returns maximum sum in a subarray of WindowSize.
     */
    static int maxSum(final int[] arr, final int arrayLength, final int windowSize) {
        // Initialize result.
        int maxSum = Integer.MIN_VALUE;

        // Consider all blocks starting with an i.
        for (int i = 0; i < arrayLength - windowSize + 1; i++) {
            int currentSum = 0;

            for (int j = 0; j < windowSize; j++) {
                currentSum = currentSum + arr[i + j];
            }

            // Update result if required.
            maxSum = Math.max(currentSum, maxSum);
        }

        return maxSum;
    }

    private SlidingWindowDemo() {
        super();
    }
}
