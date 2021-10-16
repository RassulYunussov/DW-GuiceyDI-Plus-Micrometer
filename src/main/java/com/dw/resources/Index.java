package com.dw.resources;

import com.dw.models.SampleModel;
import com.dw.services.SomeTimedService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class Index {

    private final SomeTimedService someTimedService;

    @Inject
    public Index(SomeTimedService someTimedService) {
        this.someTimedService = someTimedService;
    }

    @GET
    public SampleModel GetSampleModel() throws InterruptedException {
        return new SampleModel(someTimedService.returnString());
    }
}
