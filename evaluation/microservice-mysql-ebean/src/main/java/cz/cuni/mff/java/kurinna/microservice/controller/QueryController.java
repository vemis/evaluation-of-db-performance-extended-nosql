package cz.cuni.mff.java.kurinna.microservice.controller;

import cz.cuni.mff.java.kurinna.common.controller.AbstractQueryController;
import cz.cuni.mff.java.kurinna.microservice.service.QueryService;
import io.ebean.SqlRow;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QueryController extends AbstractQueryController<SqlRow> {

    public QueryController(QueryService service) {
        super(service);
    }
}
