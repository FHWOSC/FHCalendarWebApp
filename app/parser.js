const axios = require('axios');
const { JSDOM } = require('jsdom');


/**
 * An object representing a time slot.
 * @typedef {{ "start": string, 
 *            "end": string
 *          }} TimeSlot
 */

/**
 * An object representing a course's time info.
 * @typedef {{ "day": number,
 *            "start": number,
 *            "end": number,
 *            "length": number
 *          }} TimeInfo
 */

/**
 * An object representing a course.
 * @typedef {{ "name": string, 
 *            "time": TimeInfo,
 *            "employees": Employee[],
 *            "rooms": Room[]
 *          }} Course
 */

/**
 * An object representing an employee.
 * @typedef {{ "first_name": string,
 *             "last_name": string,
 *             "abbr": string
 *          }} Employee
 */

/**
 * An object containing a parsed plan.
 * @typedef {{ "title": string,
 *             "courses": Course[],
 *          }} Plan
 */

/**
 * An object representing a room.
 * @typedef {{ "name": string
 *            "abbr": string
 *          }} Room
 */

/**
 * Array of time slot objects, each containing the slot's start and end time.
 * @type TimeSlot
 */
const timeSlots = [];

/**
 * Array of course objects, each containing the course's name, time slot,
 * employees and used rooms.
 * 
 * The time slot is represented as an object containing the day the slot is on
 * (0=Monday .. 6=Sunday), and the slot's start time, end time, and length.
 * 
 * Employees are represented as an array of objects, each containing
 * the employee's first name, last name and abbreviation.
 * 
 * Rooms are represented as an array of objects, each containing
 * the room's name and abbreviation.
 * 
 * @type Course[]
 */
const courses = [];

/**
 * Parses the calendar at the given url asynchronously.
 * 
 * @param {string} url url to the calendar
 * @returns {Promise<Plan>} the parsed calendar
 */
function parseAsync(url) {
    console.log(url)
    return new Promise(function(resolve, reject) {
    
        axios.get(url).then(html => {

            const dom = new JSDOM(html.data);
            const document = dom.window.document;

            const tablePlan = document.querySelector("table");
            const rows = [...tablePlan.querySelector("tbody").children];
            
            let planTitle = "benutzerdefiniert"; 
            const rowHeader = rows[0];
            rowHeader.querySelectorAll("td").forEach((header, idx) => {
                if (idx == 0 && header.textContent.replaceAll("&nbsp;", " ").trim()) {
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
            
            const rowsCourses = rows.slice(1);
            let day = -1;
            let rowspan = 0;
            
            for (let row = 0; row < rowsCourses.length; row++) {
                const tr = rowsCourses[row];
                
                for (let col = 0; col < tr.childElementCount; col++) {
                    let elem = [...tr.children][col];

                    if (col == 0) {
                        if (rowspan > 0) {
                            parseCourse(elem, day, col + 1)
                            rowspan--;
                        } else {
                            rowspan = elem.hasAttribute("rowspan") ? +elem.getAttribute("rowspan") - 1 : 0;
                            day++;
                        }
                    } else {
                       parseCourse(elem, day, col)
                    }
                }
            }

            resolve({
                "title": planTitle,
                "courses": courses
            });

        })
        .catch(reject);
    });
}

/**
 * Parses a course from the given HTML element (if possible),
 * using the given day and column, and adds it tnto {@link courses}.
 * 
 * @param {Element} elem HTML element to parse from
 * @param {number} day day of week (0=Monday .. 6=Sunday)
 * @param {number} col column index (= time slot index)
 */
function parseCourse(elem, day, col) {
    const length = elem.hasAttribute("colspan") ? +elem.getAttribute("colspan") : 1;

    if (elem.childElementCount > 0) {
        const courseName = elem.querySelector("td.splan_veranstaltung").innerHTML.replaceAll("&nbsp;", " ").trim();

        const employeeAttrs = [...elem.querySelectorAll(".splan_mitarbeiter a")];
        const employeeObjs = employeeAttrs.map(empl => {
            const emplNameParts = empl.title.split(", ");
            if (emplNameParts.length != 2) return undefined;

            return {
                "first_name": emplNameParts.splice(1).join(" "),
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

module.exports = {
    "parseAsync": parseAsync
}

