package pl.edu.pg.eti.segai.aiClassification;

import java.io.Serializable;

public class ClassificationResult implements Serializable {
    private final String type;
    private final double probability;

    public ClassificationResult(String type, double probability) {
        this.type = type;
        this.probability = probability;
    }

    public String getType() {
        return type;
    }

    public double getProbability() {
        return probability;
    }
}
