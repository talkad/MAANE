import React, {useEffect, useState} from "react";
import './WorkPlan.css'
import 'react-big-calendar/lib/css/react-big-calendar.css'

// COMPONENTS
import * as Space from 'react-spaces';
import { Calendar, momentLocalizer } from 'react-big-calendar';
import moment from 'moment'
import Connection from "../../Communication/Connection";
import withDragAndDrop from 'react-big-calendar/lib/addons/dragAndDrop'
import 'react-big-calendar/lib/addons/dragAndDrop/styles.css'

import 'moment/locale/he'
import NotificationSnackbar from "../../CommonComponents/NotificationSnackbar";

const DnDCalendar = withDragAndDrop(Calendar)

const localizer = momentLocalizer(moment); // localizer to represent the data according to our location (israel)

// dummy data for offline testing
// const myEventsList = [
//     {
//         title: "בטיחות במעבדה",
//         start: "May 6th 2022, 13:00:00 pm",
//         end: "May 6th 2022, 15:00:00 pm",
//         allDay: false,
//         resource: "https://momentjs.com/",
//     },
//
//     // {
//     //     title: "קידום תלמידים",
//     //     start: "7/5/2022",
//     //     end: "7/5/2022",
//     //     allDay: false,
//     //     resource: "https://momentjs.com/",
//     // },
//
//     {
//         title: "שעות פרטניות",
//         start: "1/10/2022",
//         end: "1/10/2022",
//         allDay: false,
//         resource: "https://momentjs.com/",
//     }
// ]

// TODO: agree on date format

const myEventsList = [
    {
        title: "dunno",
        start: new Date(2022, 4, 13, 8, 0, 0), // that's how you do it
        end: new Date(2022, 4, 13, 10, 0, 0),
        allDay: false,
        resource: "https://momentjs.com/",
    },
    {
        title: "בטיחות במעבדה",
        start: new Date(2022, 4, 13, 13, 0, 0), // that's how you do it
        end: new Date(2022, 4, 13, 14, 0, 0),
    },
]

const messages = {
    week: 'שבוע',
    work_week: 'שבוע עבודה',
    day: 'יום',
    month: 'חודש',
    previous: 'הקודם',
    next: 'הבא',
    today: 'היום',
    agenda: "אג'נדה",
}

export default function WorkPlan(){
    const [dataDate, setDataDate] = useState(new Date())
    const [viewingDate, setViewingDate] = useState(new Date())
    const [eventList, setEventList] = useState(myEventsList)

    // snackbar data
    const [openSnackbar, setOpenSnackbar] = useState(false)
    const [snackbarSeverity, setSnackbarSeverity] = useState('')
    const [snackbarMessage, setSnackbarMessage] = useState('')

    const page_title = "לוח העבודה שלי";

    /**
     * sends a request to the server to get work plan of the current user
     */
    useEffect(() => {
        console.log(new Date())
        let currentYear = dataDate.getFullYear();
        let currentMonth = dataDate.getMonth();

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

    /**
     * onNavigate handler for when the user changes the viewed dates
     * @param newDate
     */
    const onNavigate = (newDate) => {
        if (newDate.getMonth() !== dataDate.getMonth()){
            setDataDate(newDate)

            // calling for new data to view
            new Connection().getWorkPlan(newDate.getFullYear(), newDate.getMonth(), arrangeActivities);
        }

        setViewingDate(newDate)
    }

    /**
     * call back function for the various event editing requests
     * @param data
     */
    const eventUpdateCallback = (data) => {
        if(data.failure){
            setOpenSnackbar(true)
            setSnackbarSeverity('error')
            snackbarMessage('אירעה שגיאה. הפעילות לא התעדכנה')
        }
        else{
            setOpenSnackbar(true)
            setSnackbarSeverity('success')
            snackbarMessage('הפעילות התעדכנה בהצלחה')

            // calling for new data to view
            new Connection().getWorkPlan(dataDate.getFullYear(), dataDate.getMonth(), arrangeActivities);
        }
    }

    /**
     * onMoveEvent handler which updates the times of the moved event
     * @param event
     * @param start
     * @param end
     */
    const moveEvent = ({event, start, end}) => {
        new Connection().editActivity(event.start, start, end, eventUpdateCallback)
    }

    /**
     * onResizeEvent handler which updates the times of the resized event
     * @param event
     * @param start
     * @param end
     */
    const resizeEvent = ({event, start, end}) => {
        new Connection().editActivity(event.start, start, end, eventUpdateCallback)
    }

    return (
        // all the spaces around are for placing the calendar
        <Space.Fill>
            <Space.Top size="5%"/>
            <Space.Fill>
                <Space.Left size="5%"/>
                <Space.Fill>
                    {/* the calendar */}
                    {/*todo: for some reason when dragging on month view it's inverted*/}
                    <h1>{page_title}</h1>
                    <DnDCalendar
                        date={viewingDate}
                        events={eventList}
                        messages={messages}
                        localizer={localizer}
                        onNavigate={onNavigate}
                        onEventDrop={moveEvent}
                        onEventResize={resizeEvent}
                        popup
                        resizable
                        rtl={true}
                    />

                    {/*notification snackbar*/}
                    <NotificationSnackbar
                        open={openSnackbar}
                        setOpen={setOpenSnackbar}
                        severity={snackbarSeverity}
                        message={snackbarMessage}/>
                </Space.Fill>
                <Space.Right size="5%"/>
            </Space.Fill>
            <Space.Bottom size="5%"/>
        </Space.Fill>
    )
}