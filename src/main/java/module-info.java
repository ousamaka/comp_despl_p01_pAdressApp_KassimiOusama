module es.damdi.ousama.comp_despl_p01_padressapp_kassimiousama {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.prefs;

    opens es.damdi.ousama.comp_despl_p01_padressapp_kassimiousama.persistence;
    opens es.damdi.ousama.comp_despl_p01_padressapp_kassimiousama.view;
    opens es.damdi.ousama.comp_despl_p01_padressapp_kassimiousama.model;

    opens es.damdi.ousama.comp_despl_p01_padressapp_kassimiousama to javafx.fxml;
    exports es.damdi.ousama.comp_despl_p01_padressapp_kassimiousama;
}