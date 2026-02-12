package es.damdi.ousama.comp_despl_p01_padressapp_kassimiousama.persistence;

import es.damdi.ousama.comp_despl_p01_padressapp_kassimiousama.model.Person;

import java.io.File;
import java.io.IOException;
import java.util.List;
public interface PersonRepository {
    List<Person> load(File file) throws IOException;
    void save(File file, List<Person> persons) throws IOException;
}
