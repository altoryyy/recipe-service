package recipeservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import recipeservice.log.VisitCounter;

@RestController
@RequestMapping("/api/visits")
public class VisitController {

    private final VisitCounter visitCounter;

    public VisitController(VisitCounter visitCounter) {
        this.visitCounter = visitCounter;
    }

    @GetMapping("/visit")
    public String visit() {
        visitCounter.incrementVisit("/api/visits/visit");
        return "Visit counted!";
    }

    @GetMapping("/count")
    public long getVisitCount(@RequestParam String url) {
        return visitCounter.getVisitCount(url);
    }
}