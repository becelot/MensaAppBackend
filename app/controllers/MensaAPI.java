package controllers;

import bt.MensaApp.lib.Model.IDataProvider;
import bt.MensaApp.lib.Model.JSON.JSONMensa;
import bt.MensaApp.lib.Model.JSON.JSONUniversity;
import bt.MensaApp.lib.Model.JSON.JsonParser;
import bt.MensaApp.lib.Model.Mensa;
import bt.MensaApp.lib.Model.NavigationHeader;
import bt.MensaApp.lib.Model.Rwth.Uncompressed.RwthUniversity;
import bt.MensaApp.lib.Model.University;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * API implementing the MensaApp functionality
 */
public class MensaAPI extends Controller {

    /**
     * Returns a list of supported universities on the server site
     * @return
     */
    private static List<IDataProvider> getSupportedUnivsersities() {
        ArrayList<IDataProvider> supportedUniList = new ArrayList<>();
        supportedUniList.add(new NavigationHeader("Universität"));
        supportedUniList.add(new RwthUniversity("RWTH Aachen"));
        return supportedUniList;
    }

    /**
     * API call for resource /getUni
     * @return A JSON object containing all universities supported
     */
    public static Result getUniversities() {
        //Get supported universities
        List<IDataProvider> uniList = getSupportedUnivsersities();

        //Convert universities to JSONUniversity to ensure correct behaviour in the client
        for (int i = 0; i < uniList.size(); i++) {
            if (uniList.get(i) instanceof University) {
                uniList.set(i, new JSONUniversity((University) uniList.get(i)));
            }
        }

        //Convert to JSON and send 200 OK
        return sendResponse(JsonParser.getParser().toJson(uniList));
    }

    /**
     * API call for resource /getMensa?uni=universityName
     * @return A JSON object containing all canteens supported by this university
     */
    public static Result getMensas(String universityName) {
        //Retrieve university by name
        Optional<University> uni = getUniversityFromString(universityName);

        //If uni does not exist, return bad request
        if (!uni.isPresent()) {
            return badRequest("University not found");
        }

        //Retireve list of all canteens for this university
        List<IDataProvider> menuList = new ArrayList<IDataProvider>(uni.get().getMensaList());

        //Convert canteens to JSONMensa to ensure correct behaviour in the client
        for (int i = 0; i < menuList.size(); i++) {
            if (menuList.get(i) instanceof Mensa) {
                menuList.set(i, new JSONMensa((Mensa)menuList.get(i)));
            }
        }

        //Convert to JSON and send 200 OK
        return sendResponse(JsonParser.getParser().toJson(menuList));
    }

    /**
     * Helper function that retrieves a university by name
     * @param universityName The name of the university
     * @return An optional containing a university or null
     */
    private static Optional<University> getUniversityFromString(String universityName) {
        return getSupportedUnivsersities().stream()
                .filter(m -> m instanceof University)
                .map(m -> (University) m)
                .filter(m -> m.getName().equals(universityName))
                .findFirst();
    }


    /**
     * Helper function that retrieves a mensa by university and mensa name
     * @param uni The university the mensa belongs to
     * @param mensaName The name of the mensa
     * @return An optional containing a mensa or null
     */
    private static Optional<Mensa> getMensaFromString(University uni, String mensaName) {
        return uni.getMensaList().stream()
                .filter(m -> m instanceof Mensa)
                .map(m -> (Mensa) m)
                .filter(m -> m.getName().equals(mensaName))
                .findFirst();
    }

    /**
     * API call for resource /getMenus?uni=universityName&mensa=mensaName
     * @param universityName The name of the university
     * @param mensaName The name of the mensa
     * @return A JSON object containing all menus found for the given university/mensa pair
     */
    public static Result getMenus(String universityName, String mensaName) {
        //Retrieve the university by name
        Optional<University> uni = getUniversityFromString(universityName);

        //If university not found, return bad request
        if (!uni.isPresent()) {
            return badRequest("University not found");
        }

        //Retrieve the mensa by name
        Optional<Mensa> mensa = getMensaFromString(uni.get(), mensaName);

        //If mensa not found, return bad request
        if (!mensa.isPresent()) {
            return badRequest("Mensa not found");
        }

        //Retrieve a list of menus
        List<IDataProvider> menuList = new ArrayList<IDataProvider>(mensa.get().getMenus());

        //Convert to JSON and send 200 OK
        return sendResponse(JsonParser.getParser().toJson(menuList));
    }

    public static Result sendResponse(String resp) {
        byte[] r = resp.getBytes();
        response().setHeader("Content-Length", Integer.toString(r.length));
        response().setContentType("application/json");
        return ok(r);
    }
}
