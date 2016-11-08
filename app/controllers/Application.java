package controllers;

import bt.MensaApp.Model.*;
import bt.MensaApp.Model.Rwth.Uncompressed.RwthUniversity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import play.mvc.*;

import views.html.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }



    public static Result getMenus(String mensaName) {
        RuntimeTypeAdapterFactory<IDataProvider> runtimeTypeAdapterFactory = RuntimeTypeAdapterFactory
                .of(IDataProvider.class, "type")
                .registerSubtype(NavigationHeader.class, "header")
                .registerSubtype(Menu.class, "menu");
        Gson gson = new GsonBuilder().registerTypeAdapterFactory(runtimeTypeAdapterFactory).create();

        University university = new RwthUniversity("RWTH Aachen", "Uncompressed");
        Optional<Mensa> mensa = university.getMensaList().stream()
                .filter(m -> m instanceof Mensa)
                .map(m -> (Mensa) m)
                .filter(m -> m.getName().equals(mensaName))
                .findFirst();

        if (!mensa.isPresent()) {
            return badRequest("Mensa not found");
        }


        List<IDataProvider> menuList = new ArrayList<IDataProvider>(mensa.get().getMenus());
        return ok(gson.toJson(menuList));
    }

}
