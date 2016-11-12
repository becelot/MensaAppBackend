package controllers;

import bt.MensaApp.Model.*;
import bt.MensaApp.Model.JSON.JSONMensa;
import bt.MensaApp.Model.JSON.JSONUniversity;
import bt.MensaApp.Model.JSON.JsonParser;
import bt.MensaApp.Model.Rwth.Uncompressed.RwthMensa;
import bt.MensaApp.Model.Rwth.Uncompressed.RwthUniversity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import play.mvc.*;

import views.html.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Application extends Controller {


    private static List<IDataProvider> getSupportedUnivsersities() {
        ArrayList<IDataProvider> supportedUniList = new ArrayList<>();
        supportedUniList.add(new NavigationHeader("Universität"));
        supportedUniList.add(new RwthUniversity("RWTH Aachen", "JSON"));
        return supportedUniList;
    }


    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }


    public static Result getUniversities() {

        List<IDataProvider> uniList = getSupportedUnivsersities();
        for (int i = 0; i < uniList.size(); i++) {
            if (uniList.get(i) instanceof University) {
                uniList.set(i, new JSONUniversity((University) uniList.get(i)));
            }
        }
        return ok(JsonParser.getParser().toJson(uniList));
    }

    public static Result getMensas(String universityName) {

        Optional<University> uni = getUniversityFromString(universityName);

        if (!uni.isPresent()) {
            return badRequest("University not found");
        }

        List<IDataProvider> menuList = new ArrayList<IDataProvider>(uni.get().getMensaList());
        for (int i = 0; i < menuList.size(); i++) {
            if (menuList.get(i) instanceof Mensa) {
                menuList.set(i, new JSONMensa((Mensa)menuList.get(i)));
            }
        }
        return ok(JsonParser.getParser().toJson(menuList));
    }

    private static Optional<University> getUniversityFromString(String universityName) {
        return getSupportedUnivsersities().stream()
                .filter(m -> m instanceof University)
                .map(m -> (University) m)
                .filter(m -> m.getName().equals(universityName))
                .findFirst();
    }


    private static Optional<Mensa> getMensaFromString(University uni, String mensaName) {
        return uni.getMensaList().stream()
                .filter(m -> m instanceof Mensa)
                .map(m -> (Mensa) m)
                .filter(m -> m.getName().equals(mensaName))
                .findFirst();
    }

    public static Result getMenus(String universityName, String mensaName) {

        Optional<University> uni = getUniversityFromString(universityName);

        if (!uni.isPresent()) {
            return badRequest("University not found");
        }


        Optional<Mensa> mensa = getMensaFromString(uni.get(), mensaName);

        if (!mensa.isPresent()) {
            return badRequest("Mensa not found");
        }


        List<IDataProvider> menuList = new ArrayList<IDataProvider>(mensa.get().getMenus());
        return ok(JsonParser.getParser().toJson(menuList));
    }

}
