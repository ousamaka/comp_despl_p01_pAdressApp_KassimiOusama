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

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    /**
     * The data as an observable list of Persons.
     */
    private ObservableList<Person> personData = FXCollections.observableArrayList();

    /**
     * El repositorio (JSON con jacson)
     *
     */


    private final PersonRepository repository = new JacksonPersonRepository();

    /**
     * El fichero actual asociado (si existe)
     *
     */

    private File personFilePath;

    /**
     * El dirty
     *
     */
    private boolean dirty;


    public MainApp() {
        // Add some sample data
        personData.add(new Person("Hans", "Muster"));
        personData.add(new Person("Ruth", "Mueller"));
        personData.add(new Person("Heinz", "Kurz"));
        personData.add(new Person("Cornelia", "Meier"));
        personData.add(new Person("Werner", "Meyer"));
        personData.add(new Person("Lydia", "Kunz"));
        personData.add(new Person("Anna", "Best"));
        personData.add(new Person("Stefan", "Meier"));
        personData.add(new Person("Ousama", "Kassimi"));
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

        //dentro del método start()
//7.7. Dirty flag cambios en la lista
        personData.addListener((javafx.collections.ListChangeListener<Person>) c -> setDirty(true));

        //dentro del método start()
//7.8. Cargar el último fichero al arrancar (con preferencias)
        loadOnStartup();

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
            dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
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

    //7.4. Conecta el fichero actual con preferencias
    public void setPersonFilePath(File file) {
        this.personFilePath = file;
        AppPreferences.setPersonFile(file == null ? null : file.getAbsolutePath());
        // opcional: reflejar en el título
        if (primaryStage != null) {
            String name = (file == null) ? "AddressApp ICT" : "AddressApp ICT - " + file.getName();
            primaryStage.setTitle(name);
        }
    }

    //7.5. Implementa loadPersonDataFromJson(File file)
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

    //7.6. Implementa savePersonDataToJson(File file)
    public void savePersonDataToJson(File file) throws IOException {
        // 1) Guardar con el repositorio
        repository.save(file, new ArrayList<>(personData));
        // 2) Marcar fichero actual (y en preferencias)
        setPersonFilePath(file);
        // 3) Tras guardar, ya no hay cambios pendientes
        setDirty(false);
    }


    //7.8. Cargar el último fichero al arrancar (con preferencias)
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
     * Opens a dialog to show birthday statistics.
     */
    public void showBirthdayStatistics() {
        try {
            // Load the fxml file and create a new stage for the popup.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/BirthdayStatistics.fxml"));
            AnchorPane page = (AnchorPane) loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Birthday Statistics");
            dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);

            // Añadir icono y estilos si quieres que se vea igual que el resto
            dialogStage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/images/address_book_32.png")));
            Scene scene = new Scene(page);
            // scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet()); // Opcional si quieres bootstrap aqui tambien
            dialogStage.setScene(scene);

            // Set the persons into the controller.
            es.damdi.ousama.comp_despl_p01_padressapp_kassimiousama.view.BirthdayStatisticsController controller = loader.getController();
            controller.setPersonData(personData);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final Path defaultJsonPath =
            Paths.get(System.getProperty("user.home"), ".addressappv2", "persons.json");



    public static void main(String[] args) {
        launch(args);
    }

}