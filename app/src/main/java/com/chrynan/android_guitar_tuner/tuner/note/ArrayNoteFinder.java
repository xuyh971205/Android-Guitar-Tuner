package com.chrynan.android_guitar_tuner.tuner.note;

/**
 * An Array implementation of the {@link NoteFinder} interface. Note that this implementation may
 * produce incorrect results if the provided frequency is out of range (5587.65 to 16.3516).
 * However, this should be more than sufficient for a Guitar Tuner Application. Results are rounded
 * to the second decimal place (3520.00). Internally this implementation uses a Quicksort algorithm
 * variant on a predefined array of supported frequencies. This leads to an average O(log(n)) time
 * complexity. TODO Add tests.
 */
public class ArrayNoteFinder implements NoteFinder {

    private static final double F0_440_HZ = 440.00;
    private static final int F0_INDEX = 44;
    private static final double TWELTH_ROOT_OF_2 = Math.pow(2, 1 / 12);

    private final double[] noteFrequencies = new double[]{5587.65, 5274.04, 4978.03, 4698.64, 4434.92,
            4186.01, 3951.07, 3729.31, 3520.00, 3322.44, 3135.96, 2959.96, 2793.83, 2637.02, 2489.02,
            2349.32, 2217.46, 2093.00, 1975.53, 1864.66, 1760.00, 1661.22, 1567.98, 1479.98, 1396.91,
            1318.51, 1244.51, 1174.66, 1108.73, 1046.50, 987.767, 932.328, 880.000, 830.609, 783.991,
            739.989, 698.456, 659.255, 622.254, 587.330, 554.365, 523.251, 493.883, 466.164, 440.000,
            415.305, 391.995, 369.994, 349.228, 329.628, 311.127, 293.665, 277.183, 261.626, 246.942,
            233.082, 220.000, 207.652, 195.998, 184.997, 174.614, 164.814, 155.563, 146.832, 138.591,
            130.813, 123.471, 116.541, 110.000, 103.826, 97.9989, 92.4986, 87.3071, 82.4069, 77.7817,
            73.4162, 69.2957, 65.4064, 61.7354, 58.2705, 55.0000, 51.9131, 48.9994, 46.2493, 43.6535,
            41.2034, 38.8909, 36.7081, 34.6478, 32.7032, 30.8677, 29.1352, 27.5000, 25.9565, 24.4997,
            23.1247, 21.8268, 20.6017, 19.4454, 18.3540, 17.3239, 16.3516};

    private final String[] noteNames = new String[]{"F", "E", "D♯", "D", "C♯", "C", "B", "A♯", "A", "G♯", "G", "F♯"};

    private String noteName;
    private float percentDiff;

    public ArrayNoteFinder() {
        // Default public constructor
    }

    @Override
    public void setFrequency(final double frequency) {
        int frequencyIndex = getFrequencyIndex(frequency);

        // Get the name of the note
        noteName = noteNames[frequencyIndex % noteNames.length];

        // Get the percentage difference
        double difference = frequency - noteFrequencies[frequencyIndex];

        // Equation: fn = f0 * (a)^n, where a = (2)^1/12
        int n = (difference < 0 ? frequencyIndex - 1 : frequencyIndex + 1) - F0_INDEX;

        double nextClosestFrequency = F0_440_HZ * Math.pow(TWELTH_ROOT_OF_2, n);

        if (difference < 0) {
            percentDiff = (float) (((frequency - nextClosestFrequency) * 100) / (noteFrequencies[frequencyIndex] - nextClosestFrequency));
        } else {
            percentDiff = (float) (((frequency - noteFrequencies[frequencyIndex]) * 100) / (nextClosestFrequency - noteFrequencies[frequencyIndex]));
        }
    }

    @Override
    public String getNoteName() {
        return noteName;
    }

    @Override
    public float getPercentageDifference() {
        return percentDiff;
    }

    private int getFrequencyIndex(final double frequency) {
        return getClosestFrequencyIndex(frequency, 0, noteFrequencies.length - 1);
    }

    private int getClosestFrequencyIndex(final double frequency, int startIndex, int endIndex) {
        if (startIndex == endIndex - 1) {
            double startDiff = frequency - noteFrequencies[startIndex];
            double endDiff = frequency - noteFrequencies[endIndex];

            return startDiff < endDiff ? startIndex : endIndex;
        }

        int pivot = (startIndex + endIndex) / 2;
        double pivotValue = noteFrequencies[pivot];

        if (frequency < pivotValue) {
            return getClosestFrequencyIndex(frequency, startIndex, pivot);
        } else {
            return getClosestFrequencyIndex(frequency, pivot, endIndex);
        }
    }
}
