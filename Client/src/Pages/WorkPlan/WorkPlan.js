import React, {useEffect} from "react";
import './WorkPlan.css'
import 'react-big-calendar/lib/css/react-big-calendar.css'

// COMPONENTS
import * as Space from 'react-spaces';
import { Calendar, momentLocalizer } from 'react-big-calendar';
import moment from 'moment'
import 'moment/locale/he'

const localizer = momentLocalizer(moment); // localizer to represent the data according to our location (israel)

// dummy data for offline testing
const myEventsList = [
    {
        title: "בטיחות במעבדה",
        start: "May 6th 2022, 13:00:00 pm",
        end: "May 6th 2022, 15:00:00 pm",
        allDay: false,
        resource: "https://momentjs.com/", // TODO: what the heck is the resource???
    },

    // {
    //     title: "קידום תלמידים",
    //     start: "7/5/2022",
    //     end: "7/5/2022",
    //     allDay: false,
    //     resource: "https://momentjs.com/", // TODO: what the heck is the resource???
    // },

    {
        title: "שעות פרטניות",
        start: "1/10/2022",
        end: "1/10/2022",
        allDay: false,
        resource: "https://momentjs.com/", // TODO: what the heck is the resource???
    }
]

export default function WorkPlan(){

    /**
     * sends a request to the server to get work plan of the current user
     */
    useEffect(() => {
        // TODO: get information from server
    }, );

    return (
        // all the spaces around are for placing the calendar
        <Space.Fill>
            <Space.Top size="5%"/>
            <Space.Fill>
                <Space.Left size="5%"/>
                <Space.Fill>
                    {/* the calendar */}
                    {/* TODO: for some reason when changing the calendar to weekly/daily/agenda it shows nothing*/}
                    {/* TODO: the toolbar of the calendar is in english. need to change it to hebrew */}
                    <Calendar
                        id={'work-plan-calendar'}
                        localizer={localizer}
                        events={myEventsList}
                        startAccessor="start"
                        endAccessor="end"
                    />
                </Space.Fill>
                <Space.Right size="5%"/>
            </Space.Fill>
            <Space.Bottom size="5%"/>
        </Space.Fill>
    )
}