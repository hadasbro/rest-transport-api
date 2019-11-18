package github.hadasbro.transport.controllers;

import github.hadasbro.transport.components.DataFakerComponent;
import github.hadasbro.transport.requests.responses.ResultResponseSimple;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@SuppressWarnings({"unused"})
@Log
@RestController
@Profile({"dev","docker","prod"})
@RequestMapping("/utils/")
/* only for internal usage on dev */
class UtilsController extends BaseController {

    @Autowired
    private DataFakerComponent dataFaker;


    /*
    ################################################################################
    ########################## controller methods ##################################
    ################################################################################
     */

    @RequestMapping(
            value = "load-fake-data",
            method = RequestMethod.GET
    )
    public ResponseEntity<ResultResponseSimple> dataFaker() {
        dataFaker.loadFakeData();
        return ResponseEntity.ok(new ResultResponseSimple());
    }

}