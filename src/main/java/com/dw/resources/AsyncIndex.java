package com.dw.resources;

import com.dw.services.SomeTimedService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

@Path("/async")
public class AsyncIndex {

    private final SomeTimedService someTimedService;

    @Inject
    public AsyncIndex(SomeTimedService someTimedService) {
        this.someTimedService = someTimedService;
    }
    @GET
    public void asyncHelloWorld(@Suspended final AsyncResponse asyncResponse) {
       this.someTimedService.returnStringAsync().thenAccept(result->{
           asyncResponse.resume(result);
       });
    }
}
