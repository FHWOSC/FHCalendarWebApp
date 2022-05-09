const axios = require('axios');
const { JSDOM } = require('jsdom');

function parse(url) {
    console.log(url)
    
    axios.get(url).then(html => {

        const dom = new JSDOM(html.data);
        const document = dom.window.document;

        const tablePlan = document.querySelector("table");
        tablePlan.setAttribute("id", "splan-table");
        const rows = document.querySelectorAll("#splan-table > tbody > tr");
        
        let planTitle = "benutzerdefiniert"; 
        const timeSlots = [];
        const rowHeader = rows[0];
        rowHeader.querySelectorAll("td").forEach((header, idx) => {
            if (idx == 0) {
                planTitle = header.textContent;
            } else {
                const bounds = header.innerHTML.split(" - ");
                if (bounds.length != 2) return;
    
                timeSlots[idx] = {
                    "start":  bounds[0],
                    "end":    bounds[1]
                };
            }
        });
        console.log(timeSlots);

        const courses = [];
        const tdCourses = [...rows].slice(1);
        let day = 0;


        for (let row = 0; row < tdCourses.length; row++) {
            const tr = tdCourses[row];
            for (let col = 0; col < tdCourses[row].childElementCount; col++) {
                let elem = [...tr.children][col];

                if (col == 0) {
                    // found field for name of weekday, start new day
                    day++;
                } else {
                    const length = elem.colspan ?? 1;
                    if (elem.childElementCount > 0) {
                        console.log(elem.innerHTML)
                        const courseName = elem.querySelector(".splan_veranstaltung").innerHTML.replaceAll("&nbsp;", " ").trim();

                        const employeeAttrs = [...elem.querySelectorAll(".splan_mitarbeiter a")];
                        const employeeObjs = employeeAttrs.map(empl => {
                            const emplNameParts = empl.title.split(", ");
                            if (emplNameParts.length != 2) return undefined;

                            return {
                                "first_name": emplNameParts[1],
                                "last_name": emplNameParts[0],
                                "abbr": empl.textContent
                            };
                        });

                        const roomAttrs = [...elem.querySelectorAll(".splan_hoerer_raum a")];
                        const roomObjs = roomAttrs.map(room => {
                            return {
                                "name": room.title,
                                "abbr": room.textContent
                            };
                        });

                        courses.push({
                            "name": courseName,
                            "time": {
                                "day": day,
                                "start": timeSlots[col].start,
                                "end": timeSlots[col + length - 1].end,
                                "length": length
                            },
                            "employees": employeeObjs,
                            "rooms": roomObjs
                        });
                    }
                }
            }
        }

        return {
            "planTitle": planTitle,
            "courses": courses
        };

    });
}

module.exports = {
    "parse": parse
}

