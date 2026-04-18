package cz.cuni.mff.java.microservice.controller;

import cz.cuni.mff.java.kurinna.common.controller.AbstractEmbeddedQueryController;
import cz.cuni.mff.java.microservice.service.EmbeddedQueryService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmbeddedQueryController extends AbstractEmbeddedQueryController<Object> {

    public EmbeddedQueryController(EmbeddedQueryService service) {
        super(service);
    }
}
