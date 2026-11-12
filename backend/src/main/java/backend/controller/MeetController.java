package backend.controller;


// check back here if problem
import backend.model.MeetResponse;
import backend.model.MeetRequest;

import backend.service.MeetService;


import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api")
public class MeetController {

    private final MeetService meetService;

    public MeetController(MeetService meetService) {
        this.meetService = meetService;
    }

    @PostMapping("/meet")
    public MeetResponse meet(@RequestBody MeetRequest request) {
        return meetService.findTheaters(request);
    }
}