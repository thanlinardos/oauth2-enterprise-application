package com.thanlinardos.resource_server.controller.mvc;

import com.thanlinardos.resource_server.model.info.Holiday;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Controller
public class HolidaysController {

    public static final String FESTIVAL = "festival";

    @GetMapping("/holidays/{display}")
    public String displayHolidays(@PathVariable String display, Model model) {
        if (null != display && display.equals("all")) {
            model.addAttribute(FESTIVAL, true);
            model.addAttribute(FESTIVAL, true);
        } else if (null != display && display.equals("federal")) {
            model.addAttribute("federal", true);
        } else if (null != display && display.equals(FESTIVAL)) {
            model.addAttribute(FESTIVAL, true);
        }
        List<Holiday> holidays = Arrays.asList(
                new Holiday(" Jan 1 ", "New Year's Day", Holiday.Type.FESTIVAL),
                new Holiday(" Oct 31 ", "Halloween", Holiday.Type.FESTIVAL),
                new Holiday(" Nov 24 ", "Thanksgiving Day", Holiday.Type.FESTIVAL),
                new Holiday(" Dec 25 ", "Christmas", Holiday.Type.FESTIVAL),
                new Holiday(" Jan 17 ", "Martin Luther King Jr. Day", Holiday.Type.FEDERAL),
                new Holiday(" July 4 ", "Independence Day", Holiday.Type.FEDERAL),
                new Holiday(" Sep 5 ", "Labor Day", Holiday.Type.FEDERAL),
                new Holiday(" Nov 11 ", "Veterans Day", Holiday.Type.FEDERAL)
        );
        for (Holiday.Type type : Holiday.Type.values()) {
            model.addAttribute(type.toString(),
                    (holidays.stream().filter(holiday -> holiday.type().equals(type)).toList()));
        }
        return "holidays";
    }

}
