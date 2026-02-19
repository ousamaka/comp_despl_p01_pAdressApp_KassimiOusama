package es.damdi.ousama.comp_despl_p01_padressapp_kassimiousama;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import es.damdi.ousama.comp_despl_p01_padressapp_kassimiousama.model.Person;
import es.damdi.ousama.comp_despl_p01_padressapp_kassimiousama.persistence.JacksonPersonRepository;
import es.damdi.ousama.comp_despl_p01_padressapp_kassimiousama.persistence.PersonRepository;
import es.damdi.ousama.comp_despl_p01_padressapp_kassimiousama.settings.AppPreferences;
import es.damdi.ousama.comp_despl_p01_padressapp_kassimiousama.view.PersonOverviewController;
import es.damdi.ousama.comp_despl_p01_padressapp_kassimiousama.view.RootLayoutController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import javafx.scene.web.WebView;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.dansoftware.pdfdisplayer.PDFDisplayer;
import java.net.URL;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.net.URISyntaxException;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    // Declaramos la lista con un "Extractor" que vigila la propiedad de la fecha de nacimiento
    private ObservableList<Person> personData = FXCollections.observableArrayList(
            person -> new javafx.beans.Observable[] { person.birthdayProperty() }
    );

    /**
     * El repositorio (JSON con jacson)
     */
    private final PersonRepository repository = new JacksonPersonRepository();

    /**
     * El fichero actual asociado (si existe)
     */
    private File personFilePath;

    /**
     * El dirty
     */
    private boolean dirty;

    public MainApp() {
        // Creamos los datos de prueba con fechas de nacimiento VARIADAS
        Person p1 = new Person("Hans", "Muster");
        p1.setBirthday(java.time.LocalDate.of(1985, 2, 12)); // Millennial - Febrero
        personData.add(p1);

        Person p2 = new Person("Ruth", "Mueller");
        p2.setBirthday(java.time.LocalDate.of(1992, 5, 20)); // Millennial - Mayo
        personData.add(p2);

        Person p3 = new Person("Heinz", "Kurz");
        p3.setBirthday(java.time.LocalDate.of(1975, 8, 30)); // Gen X - Agosto
        personData.add(p3);

        Person p4 = new Person("Cornelia", "Meier");
        p4.setBirthday(java.time.LocalDate.of(2005, 1, 10)); // Gen Z - Enero
        personData.add(p4);

        Person p5 = new Person("Werner", "Meyer");
        p5.setBirthday(java.time.LocalDate.of(1960, 11, 5)); // Boomer - Noviembre
        personData.add(p5);

        Person p6 = new Person("Lydia", "Kunz");
        p6.setBirthday(java.time.LocalDate.of(1998, 7, 18)); // Gen Z - Julio
        personData.add(p6);

        Person p7 = new Person("Anna", "Best");
        p7.setBirthday(java.time.LocalDate.of(2010, 3, 25)); // Gen Z - Marzo
        personData.add(p7);

        Person p8 = new Person("Stefan", "Meier");
        p8.setBirthday(java.time.LocalDate.of(1988, 9, 14)); // Millennial - Septiembre
        personData.add(p8);

        Person p9 = new Person("Ousama", "Kassimi");
        p9.setBirthday(java.time.LocalDate.of(2002, 12, 1)); // Gen Z - Diciembre
        personData.add(p9);
    }

    /**
     * Returns the data as an observable list of Persons.
     * @return
     */
    public ObservableList<Person> getPersonData() {
        return personData;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("AddressApp - Ousama Kassimi");

        this.primaryStage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/images/address_book_32.png")));
        initRootLayout();

        showPersonOverview();

        // 7.7. Dirty flag cambios en la lista
        personData.addListener((javafx.collections.ListChangeListener<Person>) c -> setDirty(true));

        // 7.8. Cargar el último fichero al arrancar (con preferencias)
        loadOnStartup();
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);

            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());       //(3)

            // --- NUEVO: Dar acceso al RootLayoutController a MainApp ---
            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows the person overview inside the root layout.
     */
    public void showPersonOverview() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/PersonOverview.fxml"));
            AnchorPane personOverview = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(personOverview);

            // Give the controller access to the main app.
            PersonOverviewController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public PersonRepository getRepository() {
        return repository;
    }

    public File getPersonFilePath() {
        return personFilePath;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void setPersonData(ObservableList<Person> personData) {
        this.personData = personData;
    }

    /**
     * Abre un diálogo para editar los detalles de la persona especificada.
     * Si el usuario hace clic en OK, los cambios se guardan en el objeto persona proporcionado
     * y devuelve true.
     *
     * @param person el objeto persona a editar
     * @return true si el usuario hizo clic en OK, false en caso contrario.
     */
    public boolean showPersonEditDialog(Person person) {
        try {
            // Carga el archivo fxml y crea un nuevo escenario para el diálogo emergente.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/PersonEditDialog.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            // Crea el escenario del diálogo.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Person");
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);

            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());       //(3)

            dialogStage.setScene(scene);

            dialogStage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/images/address_book_32.png")));

            // Pone la persona en el controlador.
            es.damdi.ousama.comp_despl_p01_padressapp_kassimiousama.view.PersonEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setPerson(person);

            // Muestra el diálogo y espera hasta que el usuario lo cierre
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 7.4. Conecta el fichero actual con preferencias
    public void setPersonFilePath(File file) {
        this.personFilePath = file;
        AppPreferences.setPersonFile(file == null ? null : file.getAbsolutePath());
        // opcional: reflejar en el título
        if (primaryStage != null) {
            String name = (file == null) ? "AddressApp ICT" : "AddressApp ICT - " + file.getName();
            primaryStage.setTitle(name);
        }
    }

    // 7.5. Implementa loadPersonDataFromJson(File file)
    public void loadPersonDataFromJson(File file) throws IOException {
        // 1) Cargar desde repositorio
        List<Person> loaded = repository.load(file);
        // 2) IMPORTANTE: NO reasignar personData. Usar setAll.
        // Así la TableView sigue enlazada a la misma lista.
        personData.setAll(loaded);
        // 3) Guardar el fichero actual (y en preferencias)
        setPersonFilePath(file);
        // 4) Acabamos de cargar: no hay cambios sin guardar
        setDirty(false);
    }

    // 7.6. Implementa savePersonDataToJson(File file)
    public void savePersonDataToJson(File file) throws IOException {
        // 1) Guardar con el repositorio
        repository.save(file, new ArrayList<>(personData));
        // 2) Marcar fichero actual (y en preferencias)
        setPersonFilePath(file);
        // 3) Tras guardar, ya no hay cambios pendientes
        setDirty(false);
    }

    // 7.8. Cargar el último fichero al arrancar (con preferencias)
    private void loadOnStartup() {
        // 1) si hay ruta en Preferences -> carga
        AppPreferences.getPersonFile().ifPresentOrElse(
                path -> {
                    File f = new File(path);
                    if (f.exists()) {
                        try {
                            loadPersonDataFromJson(f);
                            setPersonFilePath(f);
                        } catch (IOException e) {
                            // si falla, cae al default
                            loadDefaultIfExists();
                        }
                    } else {
                        loadDefaultIfExists();
                    }
                },
                this::loadDefaultIfExists
        );
    }

    private void loadDefaultIfExists() {
        File f = defaultJsonPath.toFile();
        if (f.exists()) {
            try {
                loadPersonDataFromJson(f);
                setPersonFilePath(f);
            } catch (IOException ignored) {
                // si falla, te quedas con los datos en memoria (ej. sample data)
            }
        } else {
            // No existe aún: te quedas con los sample data (o lista vacía, como prefieras)
            setPersonFilePath(f); // así autosave crea el fichero al salir
        }
    }

    /**
     * Opens a dialog to show birthday statistics (AHORA NO MODAL).
     */
    public void showBirthdayStatistics() {
        try {
            // Load the fxml file and create a new stage for the popup.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/BirthdayStatistics.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Birthday Statistics");

            // BORRADOS INITMODALITY E INITOWNER PARA QUE SEA INDEPENDIENTE

            // Añadir icono
            dialogStage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/images/address_book_32.png")));

            Scene scene = new Scene(page);

            // APLICAR EL TEMA OSCURO A LA VENTANA DE ESTADÍSTICAS
            scene.getStylesheets().add(MainApp.class.getResource("view/DarkTheme.css").toExternalForm());

            // (Opcional) Le aplicamos también el color de fondo oscuro al panel base
            page.getStyleClass().add("background");

            dialogStage.setScene(scene);

            // Set the persons into the controller.
            es.damdi.ousama.comp_despl_p01_padressapp_kassimiousama.view.BirthdayStatisticsController controller = loader.getController();
            controller.setPersonData(personData);

            // show() en lugar de showAndWait()
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final Path defaultJsonPath =
            Paths.get(System.getProperty("user.home"), ".addressappv2", "persons.json");

    /**
     * Abre ventana de ayuda en HTML (No modal)
     */
    public void showHelpHTML() {
        Stage helpStage = new Stage();
        helpStage.setTitle("Ayuda - HTML");
        WebView webView = new WebView();

        URL url = MainApp.class.getResource("help/html/index.html");
        if (url != null) {
            webView.getEngine().load(url.toExternalForm());
        } else {
            webView.getEngine().loadContent("<html><body><h2>Error: Archivo HTML no encontrado.</h2></body></html>");
        }

        helpStage.setScene(new Scene(webView, 800, 600));
        helpStage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/images/address_book_32.png")));
        helpStage.show(); // show() lo hace NO MODAL, puedes seguir usando la app principal
    }

    /**
     * Abre ventana de ayuda en Markdown (No modal)
     */
    public void showHelpMarkdown() {
        Stage helpStage = new Stage();
        helpStage.setTitle("Ayuda - Markdown");
        WebView webView = new WebView();

        try (InputStream is = MainApp.class.getResourceAsStream("help/markdown/README.md")) {
            if (is != null) {
                String md = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Parser parser = Parser.builder().build();
                HtmlRenderer renderer = HtmlRenderer.builder().build();
                Node document = parser.parse(md);
                webView.getEngine().loadContent(renderer.render(document));
            } else {
                webView.getEngine().loadContent("<html><body><h2>Error: Archivo MD no encontrado.</h2></body></html>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        helpStage.setScene(new Scene(webView, 800, 600));
        helpStage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/images/address_book_32.png")));
        helpStage.show();
    }

    /**
     * Abre ventana de ayuda en PDF (No modal)
     */
    public void showHelpPDF() {
        Stage helpStage = new Stage();
        helpStage.setTitle("Ayuda - PDF");

        try {
            PDFDisplayer displayer = new PDFDisplayer();
            URL pdfUrl = MainApp.class.getResource("help/pdf/ayuda.pdf");

            if (pdfUrl != null) {
                File pdfFile = new File(pdfUrl.toURI());
                displayer.loadPDF(pdfFile);
                helpStage.setScene(new Scene(displayer.toNode(), 800, 600));
                helpStage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/images/address_book_32.png")));
                helpStage.show();
            } else {
                System.err.println("El archivo PDF no se encontró.");
            }
        } catch (URISyntaxException | IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}