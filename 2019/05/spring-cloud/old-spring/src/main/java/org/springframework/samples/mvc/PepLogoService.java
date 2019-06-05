package org.springframework.samples.mvc;

import feign.RequestLine;

public interface PepLogoService {

    @RequestLine("GET /banner")
    String banner();

}
