package es.damdi.ousama.comp_despl_p01_padressapp_kassimiousama.view;

import es.damdi.ousama.comp_despl_p01_padressapp_kassimiousama.model.Person;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.layout.StackPane;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.chart.ChartData;
import javafx.scene.paint.Color;

import java.text.DateFormatSymbols;
import java.util.*;

public class BirthdayStatisticsController {

    // --- GRÁFICO 1: BARRAS (Tutorial) ---
    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private CategoryAxis xAxis;
    private ObservableList<String> monthNames = FXCollections.observableArrayList();

    // --- GRÁFICO 2: PIE (Generaciones) ---
    @FXML
    private PieChart pieChart;

    // --- GRÁFICO 3: LÍNEA (Años) ---
    @FXML
    private LineChart<Number, Number> lineChart;

    // --- GRÁFICO 4: DONUT (TilesFX) ---
    @FXML
    private StackPane donutChartContainer;

    private Tile donutTile;
    private ChartData dataGenZ;
    private ChartData dataMillennials;
    private ChartData dataGenX;
    private ChartData dataBoomers;
    private ChartData dataOthers;

    @FXML
    private void initialize() {
        // Inicializar meses para el gráfico de barras
        String[] months = DateFormatSymbols.getInstance(Locale.ENGLISH).getMonths();
        monthNames.addAll(Arrays.asList(months));
        xAxis.setCategories(monthNames);

        // --- ARREGLAR LA ESCALA DEL GRÁFICO DE LÍNEAS (AÑOS) ---
        NumberAxis xAxisLine = (NumberAxis) lineChart.getXAxis();
        xAxisLine.setForceZeroInRange(false);
        xAxisLine.setTickLabelFormatter(new javafx.util.StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                return String.valueOf(object.intValue()); // Muestra solo el número entero sin formato
            }

            @Override
            public Number fromString(String string) {
                return Integer.parseInt(string);
            }
        });

        // --- ARREGLAR LA ESCALA DEL GRÁFICO DE BARRAS ---
        NumberAxis yAxisBar = (NumberAxis) barChart.getYAxis();
        yAxisBar.setTickUnit(1);
        yAxisBar.setMinorTickVisible(false);
        yAxisBar.setTickLabelFormatter(new javafx.util.StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                if (object.doubleValue() == object.intValue()) {
                    return String.valueOf(object.intValue());
                }
                return "";
            }
            @Override
            public Number fromString(String string) {
                return Integer.parseInt(string);
            }
        });

        // --- INICIALIZAR GRÁFICO DONUT DE TILESFX ---
        // Nota: Cambiamos PURPLE por MAGENTA que es el color oficial de la librería TilesFX
        dataGenZ = new ChartData("Gen Z", 0, Tile.ORANGE);
        dataMillennials = new ChartData("Millennials", 0, Tile.BLUE);
        dataGenX = new ChartData("Gen X", 0, Tile.MAGENTA);
        dataBoomers = new ChartData("Baby Boomers", 0, Tile.RED);
        dataOthers = new ChartData("Others", 0, Tile.GREEN);

        donutTile = TileBuilder.create()
                .skinType(Tile.SkinType.DONUT_CHART)
                .title("Generaciones")
                .textVisible(false)
                .chartData(dataGenZ, dataMillennials, dataGenX, dataBoomers, dataOthers)
                .backgroundColor(Color.TRANSPARENT)
                .build();

        // Metemos el Tile dentro del StackPane
        if (donutChartContainer != null) {
            donutChartContainer.getChildren().add(donutTile);
        }
    }

    /**
     * Setea los datos, dibuja por primera vez y añade el escuchador en tiempo real
     */
    public void setPersonData(ObservableList<Person> persons) {
        // 1. Pintar los gráficos por primera vez al abrir la ventana
        refreshAllCharts(persons);

        // 2. MAGIA: Añadir un "Listener" (Escuchador).
        // Si añades, borras o editas a una persona, esto se ejecuta solo.
        persons.addListener((ListChangeListener<Person>) change -> {
            refreshAllCharts(persons);
        });
    }

    /**
     * Limpia los gráficos antiguos y dibuja los nuevos.
     */
    private void refreshAllCharts(List<Person> persons) {
        // Limpiamos los datos anteriores para que no se amontonen las barras y líneas
        barChart.getData().clear();
        lineChart.getData().clear();

        // Volvemos a calcular y pintar
        updateBarChart(persons);
        updatePieChart(persons);
        updateLineChart(persons);

        // Llamada a nuestro nuevo gráfico TilesFX
        updateDonutChart(persons);
    }

    private void updateBarChart(List<Person> persons) {
        int[] monthCounter = new int[12];
        for (Person p : persons) {
            if (p.getBirthday() != null) {
                int month = p.getBirthday().getMonthValue() - 1;
                monthCounter[month]++;
            }
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Birthdays");

        for (int i = 0; i < monthCounter.length; i++) {
            series.getData().add(new XYChart.Data<>(monthNames.get(i), monthCounter[i]));
        }
        barChart.getData().add(series);
    }

    private void updatePieChart(List<Person> persons) {
        int genZ = 0;
        int millennials = 0;
        int genX = 0;
        int boomers = 0;
        int others = 0;

        for (Person p : persons) {
            if (p.getBirthday() != null) {
                int year = p.getBirthday().getYear();
                if (year >= 1997 && year <= 2012) genZ++;
                else if (year >= 1981 && year <= 1996) millennials++;
                else if (year >= 1965 && year <= 1980) genX++;
                else if (year >= 1946 && year <= 1964) boomers++;
                else others++;
            }
        }

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Gen Z", genZ),
                new PieChart.Data("Millennials", millennials),
                new PieChart.Data("Gen X", genX),
                new PieChart.Data("Baby Boomers", boomers),
                new PieChart.Data("Others", others)
        );

        pieChart.setData(pieData);
    }

    private void updateLineChart(List<Person> persons) {
        Map<Integer, Integer> yearCounts = new TreeMap<>();

        for (Person p : persons) {
            if (p.getBirthday() != null) {
                int year = p.getBirthday().getYear();
                yearCounts.put(year, yearCounts.getOrDefault(year, 0) + 1);
            }
        }

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Births Trend");

        for (Map.Entry<Integer, Integer> entry : yearCounts.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        lineChart.getData().add(series);
    }

    private void updateDonutChart(List<Person> persons) {
        int genZ = 0, millennials = 0, genX = 0, boomers = 0, others = 0;

        for (Person p : persons) {
            if (p.getBirthday() != null) {
                int year = p.getBirthday().getYear();
                if (year >= 1997 && year <= 2012) genZ++;
                else if (year >= 1981 && year <= 1996) millennials++;
                else if (year >= 1965 && year <= 1980) genX++;
                else if (year >= 1946 && year <= 1964) boomers++;
                else others++;
            }
        }

        // Lo espectacular de TilesFX es que al usar .setValue(), el gráfico se anima solo
        dataGenZ.setValue(genZ);
        dataMillennials.setValue(millennials);
        dataGenX.setValue(genX);
        dataBoomers.setValue(boomers);
        dataOthers.setValue(others);
    }
}