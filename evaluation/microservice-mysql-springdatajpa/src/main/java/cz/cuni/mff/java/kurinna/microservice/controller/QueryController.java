package cz.cuni.mff.java.kurinna.microservice.controller;

import cz.cuni.mff.java.kurinna.common.controller.AbstractQueryController;
import cz.cuni.mff.java.kurinna.microservice.service.QueryService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QueryController extends AbstractQueryController<Object[]> {

    public QueryController(QueryService service) {
        super(service);
    }
}
