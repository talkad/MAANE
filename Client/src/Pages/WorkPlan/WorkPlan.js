import React, {useEffect, useState} from "react";
import './WorkPlan.css'
import 'react-big-calendar/lib/css/react-big-calendar.css'

// COMPONENTS
import * as Space from 'react-spaces';
import { Calendar, momentLocalizer } from 'react-big-calendar';
import moment from 'moment'
import 'moment/locale/he'

const localizer = momentLocalizer(moment);

const myEventsList = [
    {
        title: "שלום שם",
        start: "1/2/2022",
        end: "1/2/2022",
        allDay: false,
        resource: "https://momentjs.com/", // TODO: what the heck is the resource???
    }
]

export default function WorkPlan(){

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