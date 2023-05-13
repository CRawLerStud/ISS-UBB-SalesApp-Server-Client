package app.client.gui;

import app.services.AppServices;
import javafx.stage.Stage;

public class Controller {
    protected AppServices services;
    protected Stage stage;

    public void set(AppServices services, Stage stage){
        this.stage = stage;
        this.services = services;
    }
}
