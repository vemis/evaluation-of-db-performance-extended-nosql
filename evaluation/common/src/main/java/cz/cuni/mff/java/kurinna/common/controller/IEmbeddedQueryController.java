package cz.cuni.mff.java.kurinna.common.controller;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface IEmbeddedQueryController {

    ResponseEntity<Map<String, Object>> r1(int repetitions);

    ResponseEntity<Map<String, Object>> r2(int repetitions);

    ResponseEntity<Map<String, Object>> r3(int repetitions);

    ResponseEntity<Map<String, Object>> r4(int repetitions);

    ResponseEntity<Map<String, Object>> r5(int repetitions);

    ResponseEntity<Map<String, Object>> r6(int repetitions);

    ResponseEntity<Map<String, Object>> r7(int repetitions);

    ResponseEntity<Map<String, Object>> r8(int repetitions);

    ResponseEntity<Map<String, Object>> r9(int repetitions);
}
