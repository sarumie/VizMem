module com.imura.VizMem {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires static lombok;
    requires java.sql;

    opens com.imura.VizMem to javafx.fxml;
    exports com.imura.VizMem;
}