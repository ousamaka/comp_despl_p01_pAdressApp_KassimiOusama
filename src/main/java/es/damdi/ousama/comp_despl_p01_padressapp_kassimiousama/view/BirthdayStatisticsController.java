package es.damdi.ousama.comp_despl_p01_padressapp_kassimiousama.view;

import es.damdi.ousama.comp_despl_p01_padressapp_kassimiousama.model.Person;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;

import java.text.DateFormatSymbols;
import java.util.*;

public class BirthdayStatisticsController {

    // --- GRÁFICO 1: BARRAS (Tutorial) ---
    @FXML
    private BarChart<String, Integer> barChart;
    @FXML
    private CategoryAxis xAxis;
    private ObservableList<String> monthNames = FXCollections.observableArrayList();

    // --- GRÁFICO 2: PIE (Generaciones) ---
    @FXML
    private PieChart pieChart;

    // --- GRÁFICO 3: LINEA (Años) ---
    @FXML
    private LineChart<Number, Number> lineChart;

    @FXML
    private void initialize() {
        // Inicializar meses para el gráfico de barras
        String[] months = DateFormatSymbols.getInstance(Locale.ENGLISH).getMonths();
        monthNames.addAll(Arrays.asList(months));
        xAxis.setCategories(monthNames);

        // --- ARREGLAR LA ESCALA DEL GRÁFICO DE LÍNEAS (AÑOS) ---
        NumberAxis xAxisLine = (NumberAxis) lineChart.getXAxis();

        // 1. Evitar que el eje X empiece obligatoriamente en 0 (hace que el rango sea realista)
        xAxisLine.setForceZeroInRange(false);

        // 2. Quitar el punto/coma de los miles (ej: "2,000" -> "2000")
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

        // (El pieChart se sobreescribe solo con setData, no hace falta clear)

        // Volvemos a calcular y pintar
        updateBarChart(persons);
        updatePieChart(persons);
        updateLineChart(persons);
    }

    private void updateBarChart(List<Person> persons) {
        int[] monthCounter = new int[12];
        for (Person p : persons) {
            // Protección por si la fecha está vacía
            if (p.getBirthday() != null) {
                int month = p.getBirthday().getMonthValue() - 1;
                monthCounter[month]++;
            }
        }

        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName("Birthdays");

        for (int i = 0; i < monthCounter.length; i++) {
            series.getData().add(new XYChart.Data<>(monthNames.get(i), monthCounter[i]));
        }
        barChart.getData().add(series);
    }

    private void updatePieChart(List<Person> persons) {
        int genZ = 0;       // 1997 - 2012
        int millennials = 0;// 1981 - 1996
        int genX = 0;       // 1965 - 1980
        int boomers = 0;    // 1946 - 1964
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
        // Contar nacimientos por año usando un Mapa
        Map<Integer, Integer> yearCounts = new TreeMap<>(); // TreeMap ordena por clave (año)

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
}