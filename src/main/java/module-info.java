module ir.map.gr222.sem7 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens ir.map.gr222.sem7 to javafx.fxml;
    exports ir.map.gr222.sem7;

    exports ir.map.gr222.sem7.gui;
    opens ir.map.gr222.sem7.gui to javafx.fxml;

    opens ir.map.gr222.sem7.domain to javafx.base;
    exports ir.map.gr222.sem7.domain;

    opens ir.map.gr222.sem7.controller to javafx.fxml;
    exports ir.map.gr222.sem7.controller;

    opens ir.map.gr222.sem7.service to javafx.base;
    exports ir.map.gr222.sem7.service;
}