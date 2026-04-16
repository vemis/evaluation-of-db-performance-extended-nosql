package cz.cuni.mff.java.kurinna.microservice.controller;

import cz.cuni.mff.java.kurinna.common.controller.AbstractQueryController;
import cz.cuni.mff.java.kurinna.microservice.service.QueryService;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class QueryController extends AbstractQueryController<Map<String, Object>> {

    public QueryController(QueryService service) {
        super(service);
    }
}
