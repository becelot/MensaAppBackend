package controllers;

import play.mvc.Controller;
import play.mvc.Result;

/**
 * Application controller indicating if the application was compiled successfully.
 */
public class Application extends Controller {
    public static Result index() {
        return ok("MensaAPI is currently active!");
    }

}
