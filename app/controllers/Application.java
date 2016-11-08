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

    private static Gson getParser() {
        RuntimeTypeAdapterFactory<IDataProvider> runtimeTypeAdapterFactory = RuntimeTypeAdapterFactory
                .of(IDataProvider.class, "type")
                .registerSubtype(NavigationHeader.class, "header")
                .registerSubtype(University.class, "uni")
                .registerSubtype(RwthUniversity.class, "rwthUni")
                .registerSubtype(Mensa.class, "mensa")
                .registerSubtype(Menu.class, "menu");
        return new GsonBuilder().registerTypeAdapterFactory(runtimeTypeAdapterFactory).create();
    }


    private static List<IDataProvider> getSupportedUnivsersities() {
        ArrayList<IDataProvider> supportedUniList = new ArrayList<>();
        supportedUniList.add(new RwthUniversity("RWTH Aachen", "JSON"));
        return supportedUniList;
    }


    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public static Result getUniversities() {
        return ok(getParser().toJson(getSupportedUnivsersities()));
    }

    public static Result getMensas(String universityName) {
        Optional<University> uni = getSupportedUnivsersities().stream()
                .filter(m -> m instanceof University)
                .map(m -> (University) m)
                .filter(m -> m.getName().equals(universityName))
                .findFirst();

        if (!uni.isPresent()) {
            return badRequest("University not found");
        }

        List<IDataProvider> menuList = new ArrayList<IDataProvider>(uni.get().getMensaList());
        return ok(getParser().toJson(menuList));
    }

    public static Result getMenus(String universityName, String mensaName) {

        Optional<University> uni = getSupportedUnivsersities().stream()
                .filter(m -> m instanceof University)
                .map(m -> (University) m)
                .filter(m -> m.getName().equals(universityName))
                .findFirst();

        if (!uni.isPresent()) {
            return badRequest("University not found");
        }


        Optional<Mensa> mensa = uni.get().getMensaList().stream()
                .filter(m -> m instanceof Mensa)
                .map(m -> (Mensa) m)
                .filter(m -> m.getName().equals(mensaName))
                .findFirst();

        if (!mensa.isPresent()) {
            return badRequest("Mensa not found");
        }


        List<IDataProvider> menuList = new ArrayList<IDataProvider>(mensa.get().getMenus());
        return ok(getParser().toJson(menuList));
    }

}
