import React, {useEffect, useState} from "react";
import './WorkPlan.css'
import 'react-big-calendar/lib/css/react-big-calendar.css'

// COMPONENTS
import * as Space from 'react-spaces';
import { Calendar, momentLocalizer } from 'react-big-calendar';
import moment from 'moment'
import Connection from "../../Communication/Connection";

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
    const [eventList, setEventList] = useState([])

    /**
     * sends a request to the server to get work plan of the current user
     */
    useEffect(() => {
        let currentYear = new Date().getFullYear();
        let currentMonth = new Date().getMonth();

        new Connection().getWorkPlan(currentYear, currentMonth, arrangeActivities);
    }, []);

    /**
     * 
     */
    const arrangeActivities = (data) => {
        if(!data.failure){
            console.log('parsing dates')
            let dates = data.result.calendar
            for(const date of dates){
                let parsedDate = new Date(date.first);
                console.log(parsedDate)
                
                let endDate = parsedDate;
                endDate.setHours(endDate.getHours() + 2)

                setEventList(eventList => [...eventList, {title: date.second.title, start: parsedDate, end: endDate, allDay: false, resource: "https://momentjs.com/"}])
            }
        }
    }

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
                        events={eventList}
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