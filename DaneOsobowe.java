package sample;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

/**
 * Created by pwilkin on 30-Nov-17.
 */
public class DaneOsobowe implements HierarchicalController<MainController> {

    public TextField imie;
    public TextField nazwisko;
    public TextField pesel;
    public TextField indeks;
    public TableView<Student> tabelka;
    private MainController parentController;

    public void dodaj(ActionEvent actionEvent) {
        Student st = new Student();
        st.setName(imie.getText());
        st.setSurname(nazwisko.getText());
        st.setPesel(pesel.getText());
        st.setIdx(indeks.getText());
        tabelka.getItems().add(st);
    }

    public void setParentController(MainController parentController) {
        this.parentController = parentController;
        tabelka.setEditable(true);
        //tabelka.getItems().addAll(parentController.getDataContainer().getStudents());
        tabelka.setItems(parentController.getDataContainer().getStudents());
    }

    public void usunZmiany() {
        tabelka.getItems().clear();
        tabelka.getItems().addAll(parentController.getDataContainer().getStudents());
    }

    public MainController getParentController() {
        return parentController;
    }

    public void initialize() {
        for (TableColumn<Student, ?> studentTableColumn : tabelka.getColumns()) {
            if ("imie".equals(studentTableColumn.getId())) {
                TableColumn<Student, String> imieColumn = (TableColumn<Student, String>) studentTableColumn;
                studentTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
                imieColumn.setCellFactory(TextFieldTableCell.forTableColumn());
                imieColumn.setOnEditCommit((val) -> {
                    val.getTableView().getItems().get(val.getTablePosition().getRow()).setName(val.getNewValue());
                });


            } else if ("nazwisko".equals(studentTableColumn.getId())) {
                studentTableColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));



            } else if ("pesel".equals(studentTableColumn.getId())) {
                studentTableColumn.setCellValueFactory(new PropertyValueFactory<>("pesel"));
                TableColumn<Student, String> peselColumn = (TableColumn<Student, String>) studentTableColumn;
                peselColumn.setCellFactory(TextFieldTableCell.forTableColumn());


            } else if ("indeks".equals(studentTableColumn.getId())) {
                studentTableColumn.setCellValueFactory(new PropertyValueFactory<>("idx"));
                TableColumn<Student, String> indColumn = (TableColumn<Student, String>) studentTableColumn;
                indColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            }
        }

    }

    public void zapisz(ActionEvent actionEvent) {
        ArrayList<Student> studentsList = new ArrayList<>(tabelka.getItems());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data.obj"))) {
            oos.writeObject(studentsList);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Uwaga na serializację: https://sekurak.pl/java-vs-deserializacja-niezaufanych-danych-i-zdalne-wykonanie-kodu-czesc-i/ */
    public void wczytaj(ActionEvent actionEvent) {
        ArrayList<Student> studentsList;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data.obj"))) {
            studentsList = (ArrayList<Student>) ois.readObject();
            tabelka.getItems().clear();
            tabelka.getItems().addAll(studentsList);
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void synchronizuj(ActionEvent actionEvent) {
        parentController.getDataContainer().setStudents(tabelka.getItems());
    }

    public void dodajJesliEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            dodaj(new ActionEvent(keyEvent.getSource(), keyEvent.getTarget()));
        }
    }
}
