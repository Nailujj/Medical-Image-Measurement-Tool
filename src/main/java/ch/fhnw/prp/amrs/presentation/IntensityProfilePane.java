package ch.fhnw.prp.amrs.presentation;

import ch.fhnw.prp.amrs.logic.Point;
import ch.fhnw.prp.amrs.logic.PointIntensityCalculator;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Objects;

public class IntensityProfilePane extends StackPane implements StateObserver {
    private final StateModel stateModel;

    private static boolean calculate = false;
    private final XYChart.Series<Number, Number> series;

    public IntensityProfilePane(StateModel stateModel) {
        this.stateModel = stateModel;
        stateModel.addObserver(this);

        Text text = new Text("Please mark a distance to display the intensity profile.");

        getChildren().add(text);

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = getYAxis(stateModel);

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Intensity Profile");

        series = new XYChart.Series<>();
        series.setName("Data Points");

        lineChart.getData().add(series);

        VBox vbox = new VBox(lineChart);
        vbox.setAlignment(Pos.CENTER);

        getChildren().add(vbox);
    }

    private static NumberAxis getYAxis(StateModel stateModel) {
        NumberAxis yAxis;

        if (stateModel.getImageData().getImageFile().equals("dem-switzerland.png")) {
            yAxis = new NumberAxis(0, 4556, 1000);
        } else {
            yAxis = new NumberAxis(0, 100, 10);
        }

        //This method is used to set a custom formatter for the tick labels on the axis.
        //It allows to customize how the tick labels are displayed.
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis) {
            @Override
            public String toString(Number object) {
                return super.toString(object);
            }
        });
        yAxis.setTickLabelGap(10);
        yAxis.setMinorTickVisible(false);
        return yAxis;
    }

    @Override
    public void stateChanged() {
        if (calculate)
            // Trigger the update in a separate thread to avoid blocking the UI
            new Thread(this::updateChartData).start();

        String styleSheet = stateModel.getStyleSheetOfColorMode();
        if (getScene().getStylesheets().isEmpty() || !getScene().getStylesheets().get(0).endsWith(styleSheet)) {
            getScene().getStylesheets().clear();
            getScene().getStylesheets().add(Objects.requireNonNull(
                    getClass().getClassLoader().getResource(styleSheet)).toExternalForm());
        }
    }

    private int lastCalculatedSetIndex = 0;

    private void updateChartData() {
        Platform.runLater(() -> {
            series.getData().clear(); // Clear existing data
            int pointCounter = 0;

            lastCalculatedSetIndex = stateModel.getPointSet().getPoints().size()
                    - (stateModel.getPointSet().getPoints().size() - lastCalculatedSetIndex);



            // Only calculate intensity of newly added points
            for (int i = lastCalculatedSetIndex; i < stateModel.getPointSet().getPoints().size() - 1; i++) {

                try {
                    series.getData().add(new XYChart.Data<>(pointCounter, PointIntensityCalculator.getIntensity(
                            stateModel.getPointSet().getPoints().get(lastCalculatedSetIndex), stateModel)));
                } catch (Exception e) {
                    System.out.println("no points selected yet");
                }

                for (Point point : PointIntensityCalculator
                        .getAllPointsBetween(
                                stateModel.getPointSet().getPoints().get(i),
                                stateModel.getPointSet().getPoints().get(i + 1))) {
                    series.getData().add(new XYChart.Data<>(pointCounter,
                            PointIntensityCalculator.getIntensity(point, stateModel)));

                    pointCounter++;
                }
                pointCounter++;
            }
        });
    }

    public static void setCalculateAndDisplay(boolean _calculate) {
        calculate = _calculate;
    }
}
