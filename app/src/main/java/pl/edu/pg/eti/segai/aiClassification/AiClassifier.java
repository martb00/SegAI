package pl.edu.pg.eti.segai.aiClassification;

import static pl.edu.pg.eti.segai.util.Constants.TRASH_TYPE_BIO;
import static pl.edu.pg.eti.segai.util.Constants.TRASH_TYPE_GLASS;
import static pl.edu.pg.eti.segai.util.Constants.TRASH_TYPE_MIXED;
import static pl.edu.pg.eti.segai.util.Constants.TRASH_TYPE_PAPER;
import static pl.edu.pg.eti.segai.util.Constants.TRASH_TYPE_PLASTIC_METAL;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import pl.edu.pg.eti.segai.ml.ModelBestNoReshapeOptimized;

public class AiClassifier {

    private static final String[] CLASSES = {
            TRASH_TYPE_BIO,
            TRASH_TYPE_GLASS,
            TRASH_TYPE_MIXED,
            TRASH_TYPE_PAPER,
            TRASH_TYPE_PLASTIC_METAL};

    public ClassificationResult classifyImage(Bitmap image, int IMAGE_SIZE, Context context) {
        try {
            ModelBestNoReshapeOptimized model = ModelBestNoReshapeOptimized.newInstance(context);
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);

            ByteBuffer byteBuffer = getByteBufferWithImageRGBPixels(image, IMAGE_SIZE);

            inputFeature0.loadBuffer(byteBuffer);
            ModelBestNoReshapeOptimized.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidenceArray = outputFeature0.getFloatArray();

            model.close();

            return getTypeAndProbability(confidenceArray);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @NonNull
    private ByteBuffer getByteBufferWithImageRGBPixels(Bitmap image, int IMAGE_SIZE) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * IMAGE_SIZE * IMAGE_SIZE * 3);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] intValues = new int[IMAGE_SIZE * IMAGE_SIZE];
        image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

        int pixel = 0;
        for (int i = 0; i < IMAGE_SIZE; i++) {
            for (int j = 0; j < IMAGE_SIZE; j++) {
                int val = intValues[pixel++]; //RGB
                byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255));
                byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255));
                byteBuffer.putFloat((val & 0xFF) * (1.f / 255));
            }
        }
        return byteBuffer;
    }

    @NonNull
    private ClassificationResult getTypeAndProbability(float[] confidenceArray) {
        int posOfMaxVal = 0;
        float maxConfidence = confidenceArray[0];
        for (int i = 1; i < confidenceArray.length; i++) {
            if (confidenceArray[i] > maxConfidence) {
                maxConfidence = confidenceArray[i];
                posOfMaxVal = i;
            }
        }

        String type = CLASSES[posOfMaxVal];
        double probability = getProbability(confidenceArray[posOfMaxVal]);

        return new ClassificationResult(type, probability);
    }

    private double getProbability(float v) {
        double val = v * 100;
        val = (int) (val * 100);
        return val / 100;
    }
}
