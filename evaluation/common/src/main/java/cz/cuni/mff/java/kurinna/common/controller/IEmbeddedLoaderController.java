package cz.cuni.mff.java.kurinna.common.controller;

import org.springframework.http.ResponseEntity;

public interface IEmbeddedLoaderController {
    ResponseEntity<String> loadEmbeddedData();
}
