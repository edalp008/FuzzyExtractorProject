package recognition;

/**
 * Created by trill on 2016-03-29.
 */
public class Result {
    private int predictedLabel;
    private double metric;

    public Result(int predictedLabel, double metric) {
        this.predictedLabel = predictedLabel;
        this.metric = metric;
    }

    public int getPredictedLabel() {
        return predictedLabel;
    }

    public void setPredictedLabel(int predictedLabel) {
        this.predictedLabel = predictedLabel;
    }

    public double getMetric() {
        return metric;
    }

    public void setMetric(double metric) {
        this.metric = metric;
    }
}
