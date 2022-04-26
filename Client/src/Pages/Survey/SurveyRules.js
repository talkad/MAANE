import * as Space from 'react-spaces';
import {useEffect, useState} from "react";
import Connection from "../../Communication/Connection";


export default function SurveyRules(){

    const [id, setId] = useState('');

    useEffect(() => {
        var url = new URL(window.location.href);
        var surveyID = url.searchParams.get("surveyID");
        setId(surveyID)

        // TODO: send for the data

    }, []);

    return(
        <Space.Fill>
            <h1>rules {id}</h1>
        </Space.Fill>
    )
}