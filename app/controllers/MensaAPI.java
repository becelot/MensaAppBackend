package controllers;

import bt.MensaApp.Model.IDataProvider;
import bt.MensaApp.Model.JSON.JSONMensa;
import bt.MensaApp.Model.JSON.JSONUniversity;
import bt.MensaApp.Model.JSON.JsonParser;
import bt.MensaApp.Model.Mensa;
import bt.MensaApp.Model.NavigationHeader;
import bt.MensaApp.Model.Rwth.Uncompressed.RwthUniversity;
import bt.MensaApp.Model.University;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by bened on 11/12/2016.
 */
public class MensaAPI extends Controller {
    private static List<IDataProvider> getSupportedUnivsersities() {
        ArrayList<IDataProvider> supportedUniList = new ArrayList<>();
        supportedUniList.add(new NavigationHeader("Universität"));
        supportedUniList.add(new RwthUniversity("RWTH Aachen"));
        return supportedUniList;
    }


    public static Result index() {
        return ok(index.render("MensaAPI status: OK"));
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
